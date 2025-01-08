package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.app.Dialog
import android.content.Context
import android.webkit.ValueCallback
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.PinFannelAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsFannelInfo
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.intent.EditExecuteOrElse
import com.puutaro.commandclick.proccess.pin.PinFannelManager
import com.puutaro.commandclick.util.FactFannel
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.list.ListTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FannelSettingMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object PinFannelBarManager {

    val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

    fun set(
        terminalFragment: TerminalFragment,
        tag: String?,
        pinFannelRecyclerView: RecyclerView,
    ){
        val context = terminalFragment.context
        when(
            !tag.isNullOrEmpty()
                    && tag == context?.getString(R.string.index_terminal_fragment)
        ) {
            false -> {
                pinFannelRecyclerView.isVisible = false
            }
            else -> {
                val fannelSettingInfoMap = ReadText(
                    FannelSettingMap.fannelSettingMapTsvPath
                ).textToList().map {
                        fannelAdapterInfoLine ->
                    val fanneNameAndMapCon = fannelAdapterInfoLine.split("\t")
                    val fannelName = fanneNameAndMapCon.firstOrNull() ?: String()
                    val adapterInfoMapCon = fanneNameAndMapCon.getOrNull(1)
                    fannelName to CmdClickMap.createMap(
                        adapterInfoMapCon,
                        FannelSettingMap.keySeparator
                    ).toMap()
                }.toMap()
                val cmdindexSelectionSearchButton =
                    TargetFragmentInstance.getCmdIndexFragmentFromFrag(
                        terminalFragment.activity
                    )?.binding?.cmdindexSelectionSearchButton
                val pinFannelList =
                    PinFannelManager.extractPinFannelMapList(cmdindexSelectionSearchButton)
                val pinFannelAdapter = PinFannelAdapter(
                    context,
                    pinFannelList.toMutableList(),
                    fannelSettingInfoMap
                )
                pinFannelRecyclerView.adapter = pinFannelAdapter
                pinFannelRecyclerView.layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                invokeItemSetClickListener(
                    terminalFragment,
                    pinFannelAdapter,
                )
                pinTouchHelper(
                    terminalFragment,
                    pinFannelRecyclerView,
                    pinFannelAdapter,
                )
            }
        }
    }

    fun clear(
        fannelPinRecyclerView: RecyclerView,
    ){
        fannelPinRecyclerView.layoutManager = null
        fannelPinRecyclerView.adapter = null
        fannelPinRecyclerView.recycledViewPool.clear()
        fannelPinRecyclerView.removeAllViews()
    }

    fun update(
        context: Context?,
        tag: String?,
        fannelPinRecyclerView: RecyclerView,
        cmdindexSelectionSearchButton: CardView?,
    ){
        if(
            tag == context?.getString(R.string.edit_terminal_fragment)
        ) return
        val pinFannelAdapter =
            fannelPinRecyclerView.adapter as? PinFannelAdapter
                ?: return
        val pinList = PinFannelManager.extractPinFannelMapList(cmdindexSelectionSearchButton)
        val fannelSettingMap = FannelSettingMap.create()
        if(
            pinFannelAdapter.pinFannelInfoMapList == pinList
            && pinFannelAdapter.fannelSettingInfoMap == fannelSettingMap
        ) return
        pinFannelAdapter.pinFannelInfoMapList.clear()
        pinFannelAdapter.pinFannelInfoMapList.addAll(pinList)
        pinFannelAdapter.fannelSettingInfoMap = fannelSettingMap
        pinFannelAdapter.notifyDataSetChanged()
    }

    private fun invokeItemSetClickListener(
        terminalFragment: TerminalFragment,
        pinFannelListAdapter: PinFannelAdapter,
    ) {
        pinFannelListAdapter.itemClickListener = object: PinFannelAdapter.OnItemClickListener {
            override fun onItemClick(holder: PinFannelAdapter.PinFannelViewHolder) {
                val position = holder.bindingAdapterPosition
                val fannelName = pinFannelListAdapter.pinFannelInfoMapList.getOrNull(position)?.get(
                    PinFannelManager.PinFannelKey.FANNEL_NAME.key
                ) ?: return
                if(
                    !FactFannel.isFactFannel(fannelName)
                ){
                    FactFannel.creatingToast()
                    return
                }
                CoroutineScope(Dispatchers.IO).launch {
                    FileSystems.updateLastModified(
                        File(cmdclickDefaultAppDirPath, fannelName).absolutePath
                    )
                }
                terminalFragment.editListDialogForOrdinaryRevolver?.show(
                    FannelInfoTool.makeFannelInfoMapByString(
                        fannelName,
                        String()
                    ).map {
                        "${it.key}=${it.value}"
                    }.joinToString(JsFannelInfo.fannelInfoMapSeparator.toString()),
                    "/storage/emulated/0/Documents/cmdclick/AppDir/default/settingAcTestDir/settings/editListConfig.js"
//                    textToSpeech2Dir
//                    settingAcTestDir
                )
                return
                EditExecuteOrElse.handle(
                    terminalFragment,
                    fannelName,
                )
            }
        }
    }

    private fun pinTouchHelper(
        terminalFragment: TerminalFragment,
        pinListRecyclerView: RecyclerView,
        pinFannelAdapter: PinFannelAdapter,
    ){
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                ItemTouchHelper.UP,
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val adapter = recyclerView.adapter as PinFannelAdapter
                    val fromViewHolder = viewHolder as
                            PinFannelAdapter.PinFannelViewHolder
                    val toViewHolder = target as
                            PinFannelAdapter.PinFannelViewHolder
                    val from = fromViewHolder.bindingAdapterPosition
                    val to = toViewHolder.bindingAdapterPosition
                    adapter.notifyItemMoved(from, to)
                    ListTool.switchMapList(
                        pinFannelAdapter.pinFannelInfoMapList,
                        from,
                        to,
                    )
                    PinFannelManager.save(
                        pinFannelAdapter.pinFannelInfoMapList
                    )
                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    if(
                        direction != ItemTouchHelper.UP
                    ) return
                    val listIndexViewHolder =
                        viewHolder as PinFannelAdapter.PinFannelViewHolder
                    val fannelName =
                        pinFannelAdapter.pinFannelInfoMapList.getOrNull(
                            listIndexViewHolder.bindingAdapterPosition
                        )?.get(PinFannelManager.PinFannelKey.FANNEL_NAME.key) ?: return
                    CoroutineScope(Dispatchers.Main).launch {
                        DeleteConfirmDialog.launch(
                            terminalFragment,
                            fannelName,
                            pinListRecyclerView,
                            pinFannelAdapter,
                            listIndexViewHolder.bindingAdapterPosition
                        )
                    }
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?, actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)

                    viewHolder.itemView.alpha = 1.0f
                }
            })
        mIth.attachToRecyclerView(pinListRecyclerView)
    }

    private object DeleteConfirmDialog {

        private var deleteConfirmDialog: Dialog? = null

        fun launch(
            fragment: Fragment,
            fannelName: String,
            recyclerView: RecyclerView,
            pinFannelAdapter: PinFannelAdapter,
            position: Int,
        ){
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    execLaunch(
                        fragment,
                        fannelName,
                        recyclerView,
                        pinFannelAdapter,
                        position,
                    )
                }
            }
        }
        private fun execLaunch(
            fragment: Fragment,
            fannelName: String,
            recyclerView: RecyclerView,
            pinFannelAdapter: PinFannelAdapter,
            position: Int,
        ){
            val context = fragment.context ?: return

            val terminalFragment = when(fragment){
                is TerminalFragment -> fragment
                else -> TargetFragmentInstance.getCurrentTerminalFragmentFromFrag(
                    fragment.activity,
                )
            } ?: return
            val message = SystemFannel.convertDisplayNameToFannelName(fannelName)
            val jsDialogStr = ExecJsInterfaceAdder.convertUseJsInterfaceName(
                JsDialog::class.java.simpleName
            )
            val confirmScript = """
                ${jsDialogStr}.confirm(
                    "Delete pin ok?",
                    "${message}",
                );
            """.trimIndent()
            terminalFragment.binding.terminalWebView.evaluateJavascript(
                confirmScript,
                ValueCallback<String> { isDelete ->
                    when(isDelete){
                        true.toString() -> execDeleteFannel(
                            pinFannelAdapter,
                            position,
                        )
                        else -> cancelProcess(
                            recyclerView,
                            position,
                        )
                    }
                })


//            deleteConfirmDialog = Dialog(
//                context
//            )
//            deleteConfirmDialog?.setContentView(
//                R.layout.confirm_text_dialog
//            )
//            val confirmTitleTextView =
//                deleteConfirmDialog?.findViewById<AppCompatTextView>(
//                    R.id.confirm_text_dialog_title
//                )
//            val confirmTitle = "Delete pin ok?"
//            confirmTitleTextView?.text = confirmTitle
//            val confirmContentTextView =
//                deleteConfirmDialog?.findViewById<AppCompatTextView>(
//                    R.id.confirm_text_dialog_text_view
//                )
//            confirmContentTextView?.text =
//                SystemFannel.convertDisplayNameToFannelName(fannelName)
//            val confirmCancelButton =
//                deleteConfirmDialog?.findViewById<AppCompatImageButton>(
//                    R.id.confirm_text_dialog_cancel
//                )
//            confirmCancelButton?.setOnClickListener {
//                deleteConfirmDialog?.dismiss()
//                deleteConfirmDialog = null
//                cancelProcess(
//                    recyclerView,
//                    position,
//                )
//            }
//            deleteConfirmDialog?.setOnCancelListener {
//                deleteConfirmDialog?.dismiss()
//                deleteConfirmDialog = null
//                cancelProcess(
//                    recyclerView,
//                    position,
//                )
//            }
//            val confirmOkButton =
//                deleteConfirmDialog?.findViewById<AppCompatImageButton>(
//                    R.id.confirm_text_dialog_ok
//                )
//            confirmOkButton?.setOnClickListener {
//                deleteConfirmDialog?.dismiss()
//                deleteConfirmDialog = null
//                execDeleteFannel(
//                    pinFannelAdapter,
//                    position,
//                )
//            }
//            deleteConfirmDialog?.window?.setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            deleteConfirmDialog?.window?.setGravity(
//                Gravity.CENTER
//            )
//            deleteConfirmDialog?.show()
        }

        private fun cancelProcess(
            recyclerView: RecyclerView,
            listIndexPosition: Int,
        ){
            recyclerView.adapter?.notifyItemChanged(
                listIndexPosition
            )
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    delay(300)
                    recyclerView.layoutManager?.scrollToPosition(
                        listIndexPosition
                    )
                }
            }
        }

        private fun execDeleteFannel(
            pinFannelAdapter: PinFannelAdapter,
            position: Int,
        ) {
            pinFannelAdapter.pinFannelInfoMapList.removeAt(position)
            pinFannelAdapter.notifyItemRemoved(position)
            PinFannelManager.save(
                pinFannelAdapter.pinFannelInfoMapList

            )
        }
    }
}
