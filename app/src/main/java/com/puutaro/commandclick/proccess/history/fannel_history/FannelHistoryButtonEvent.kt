package com.puutaro.commandclick.proccess.history.fannel_history

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.FannelManageAdapter
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.PreInstallFannel
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.ScriptFileEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press.LongPressMenuTool
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.EditExecuteOrElse
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.proccess.pin.PinFannelManager
import com.puutaro.commandclick.proccess.qr.QrDialogMethod
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.FactFannel
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.map.FannelSettingMap
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


//private val mainMenuGroupId = 30000

//private enum class HistoryMenuEnums(
//    val groupId: Int,
//    val itemId: Int,
//    val order: Int,
//    val itemName: String
//) {
//    DELETE(mainMenuGroupId, 30100, 1, "delete"),
//}
class FannelHistoryButtonEvent (
    private val fragment: Fragment,
//    private val sharedPref: FannelInfoTool.FannelInfoSharePref?,
    )
{
    private val cmdclickAppHistoryDirAdminPath = UsePath.cmdclickAppHistoryDirAdminPath
    private val context = fragment.context
    private val searchTextLinearWeight = SearchTextLinearWeight.calculate(fragment)
    private val listLinearWeight = 1F - searchTextLinearWeight
    private var fannelHistoryDialog: Dialog? = null
    private var updateRecyclerJob: Job? = null
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

//    private val homeFannelList = when(
//        fragment
//    ) {
//        is CommandIndexFragment -> {
//            fragment.homeFannelHistoryNameList
//        }
//        is EditFragment -> {
//            fragment.homeFannelHistoryNameList
//        }
//        else -> emptyList()
//    } ?: emptyList()

    companion object {
//        private val languageType = LanguageTypeSelects.JAVA_SCRIPT
//        private val languageTypeToSectionHolderMap =
//            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
//                languageType
//            )
//        private val settingSectionStart = languageTypeToSectionHolderMap?.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//        ) as String
//
//        private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//        ) as String
        val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
        val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END
    }
    fun invoke(
    ) {
        if(
            context == null
        ) return
//        deleteOverHistory(
//            cmdclickAppHistoryDirAdminPath
//        )
        FileSystems.createFiles(
            File(
                cmdclickAppHistoryDirAdminPath,
                FannelHistoryManager.makeAppHistoryFileNameForInit(
                    UsePath.cmdclickDefaultAppDirName,
                )
            ).absolutePath
        )
        val fannelNameList = makeUpdateFannelNameList()
        val fannelInfoMap = when(fragment){
            is CommandIndexFragment -> {
                fragment.fannelInfoMap
            }
            is EditFragment -> {
                fragment.fannelInfoMap
            }
            is TerminalFragment -> fragment.fannelInfoMap
            else -> emptyMap()
        }
        val fannelManageListAdapter = FannelManageAdapter(
            context,
            fannelInfoMap,
            fannelNameList.toMutableList()
        )
        fannelHistoryDialog = Dialog(
            context
        )
        fannelHistoryDialog?.setContentView(
                R.layout.fannel_history_recycler_view
            )
        val searchText =
            fannelHistoryDialog?.findViewById<AppCompatEditText>(
                R.id.fannel_history_search_edit_text
            )
        val searchTextLinearParams =
            searchText?.layoutParams as LinearLayout.LayoutParams
        searchTextLinearParams.weight = searchTextLinearWeight
        val fannelManageListView =
            fannelHistoryDialog?.findViewById<RecyclerView>(
                R.id.fannel_history_recycler_view
            )
        val historyListViewLinearParams =
            fannelManageListView?.layoutParams as LinearLayout.LayoutParams
        historyListViewLinearParams.weight = listLinearWeight
        fannelManageListView.layoutManager = GridLayoutManager(
            context,
            2,
            LinearLayoutManager.VERTICAL,
            false
        )
        fannelManageListView.adapter = fannelManageListAdapter
        fannelManageListView.layoutManager?.scrollToPosition(
            fannelManageListAdapter.itemCount - 1
        )
        makeSearchEditText(
            fannelManageListAdapter,
            fannelManageListView,
            searchText
        )
        setItemTouchHelper(
            fannelManageListView,
            fannelManageListAdapter,
            searchText,
        )
        fannelHistoryDialog?.window
            ?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        fannelHistoryDialog?.window
            ?.setGravity(Gravity.BOTTOM)
        fannelHistoryDialog?.show()
        fannelHistoryDialog?.setOnCancelListener(
            object : DialogInterface.OnCancelListener {
                override fun onCancel(dialog: DialogInterface?) {
//                    terminalViewModel.onDialog = false
                    exitDialog(
                        fannelManageListView
                    )
                }
            })
//        terminalViewModel.onDialog = true

//        invokeItemSetLongTimeClickListenerForHistory(
//            historyListAdapter,
//            searchText,
//            cmdclickAppHistoryDirAdminPath
//        )
        invokeItemSetClickListenerForFannelManage(
            fannelManageListView,
            fannelManageListAdapter
        )
        setFannelManageListViewOnPinItemClickListener(
            fannelManageListAdapter,
        )
//        setFannelManageListViewOnDeleteItemClickListener (
//            fannelManageListAdapter,
//            searchText,
//        )
        setFannelManageListViewOnEditItemClickListener(
            fannelManageListView,
            fannelManageListAdapter,
        )
        setFannelManageListViewOnLogoItemClickListener (
            fannelManageListAdapter,
        )
        setFannelManageListViewOnLongPressItemClickListener(
            fannelManageListAdapter,
        )
    }

    private fun makeSearchEditText(
        fannelManageListAdapter: FannelManageAdapter,
        fannelManageListView: RecyclerView?,
        searchText: AppCompatEditText,
    ){
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return

                val updateFannelNameList = makeUpdateFannelNameList()
                val filteredCmdStrList = updateFannelNameList.filter {
                    Regex(
                        s.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }
                fannelManageListAdapter.fannelNameList.clear()
                fannelManageListAdapter.fannelNameList.addAll(filteredCmdStrList)
                fannelManageListAdapter.notifyDataSetChanged()
                updateRecyclerJob?.cancel()
                updateRecyclerJob = CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO){
                        delay(200)
                    }
                    fannelManageListView?.layoutManager?.scrollToPosition(
                        fannelManageListAdapter.itemCount - 1
                    )
                }

            }
        })
    }

    private fun makeUpdateFannelNameList(): List<String> {
        val maskListForFannelManageList =
            SystemFannel.maskListForFannelManageList
        val jsFileSuffix = UsePath.JS_FILE_SUFFIX
        val fannelList =
            FileSystems.sortedFiles(
                cmdclickDefaultAppDirPath
            ).filter {
                val isNotMask = !maskListForFannelManageList.contains(it)
                isNotMask
                        && it.endsWith(jsFileSuffix)
            }
//            FileSystems.filterSuffixShellOrJsFiles(
//            cmdclickAppHistoryDirAdminPath
//        ).filter {
//            !homeFannelList.contains(it)
//        }
        val homeFannelName = SystemFannel.home
        val fannelNameListSource =
            fannelList.filter {
                it != homeFannelName
            } + listOf(homeFannelName)
//        + homeFannelList.reversed()
        val fannelNameListSourceSize = fannelNameListSource.size
        return when(
            fannelNameListSourceSize % 2 == 1
                    &&  fannelNameListSourceSize > 3
        ){
            true -> listOf(homeFannelName) + fannelNameListSource
//            + homeFannelList.last()
            else -> fannelNameListSource
        }
    }

    private fun invokeItemSetClickListenerForFannelManage(
        fannelManageListView: RecyclerView,
        fannelManageListAdapter: FannelManageAdapter,
    ) {
        fannelManageListAdapter.itemClickListener = object: FannelManageAdapter.OnItemClickListener {
            override fun onItemClick(holder: FannelManageAdapter.FannelManageViewHolder) {
                val position = holder.bindingAdapterPosition
                val fannelName = fannelManageListAdapter.fannelNameList[position]
                if(
                    !FactFannel.isFactFannel(fannelName)
                    && fannelName != SystemFannel.home
                ){
                    FactFannel.creatingToast()
                    return
                }
                FileSystems.updateLastModified(
                    File(cmdclickDefaultAppDirPath, fannelName).absolutePath
                )
                EditExecuteOrElse.handle(
                    fragment,
                    fannelName
                )
                exitDialog(
                    fannelManageListView
                )
            }
        }
    }

    private fun setFannelManageListViewOnPinItemClickListener(
        fannelManageListAdapter: FannelManageAdapter,
    ) {
//        val pinLimit = FannelManageAdapter.pinLimit
        fannelManageListAdapter.pinItemClickListener = object: FannelManageAdapter.OnPinItemClickListener {
            override fun onItemClick(holder: FannelManageAdapter.FannelManageViewHolder) {
//                terminalViewModel.onDialog = false
                val position = holder.bindingAdapterPosition
                val fannelName = fannelManageListAdapter.fannelNameList[position]
                if(
                    !FactFannel.isFactFannel(fannelName)
                ){
                    FactFannel.creatingToast()
                    return
                }
                val pinFannelList = PinFannelManager.get()
                when(
                    pinFannelList.contains(fannelName)
                ){
                    true -> {
                        PinFannelManager.remove(fannelName)
                        ToastUtils.showShort("Remove ok: ${fannelName}")
                        holder.pinImageButtonView.imageTintList =
                            context?.getColorStateList(FannelManageAdapter.buttonOrdinalyColor)
                    }
                    else -> {
                        PinFannelManager.add(fannelName)
                        ToastUtils.showShort("Add ok: ${fannelName}")
                        holder.pinImageButtonView.imageTintList =
                            context?.getColorStateList(FannelManageAdapter.pinExistColor)
                    }
                }
                PinFannelManager.updateBroadcast(context)
            }
        }
    }

    private fun setFannelManageListViewOnEditItemClickListener(
        fannelManageListView: RecyclerView,
        fannelManageListAdapter: FannelManageAdapter,
    ) {
        fannelManageListAdapter.editItemClickListener = object: FannelManageAdapter.OnEditItemClickListener {
            override fun onItemClick(holder: FannelManageAdapter.FannelManageViewHolder) {
//                terminalViewModel.onDialog = false
                val position = holder.bindingAdapterPosition
                val fannelName =
                    fannelManageListAdapter.fannelNameList.getOrNull(position)
                        ?: return
                exitDialog(fannelManageListView)
                if(
                    !FactFannel.isFactFannel(fannelName)
                ){
                    FactFannel.creatingToast()
                    return
                }
                when(fannelName == SystemFannel.home) {
                    true -> {
                        preferenceEdit()
                    }
                    false -> ScriptFileEdit.edit(
                        fragment,
                        fannelName
                    )
                }
            }
        }
    }

    private fun preferenceEdit(
    ){
        val activity = fragment.activity
        val targetFragmentInstance = TargetFragmentInstance()
        val bottomFragment = targetFragmentInstance.getCmdIndexFragmentFromFrag(
            activity,
        )
        if(
            bottomFragment !is CommandIndexFragment
        ) return
        val preference = SystemFannel.preference
        if(
            !File(UsePath.cmdclickDefaultAppDirPath, preference).isFile
        ) {
            ToastUtils.showShort("wait for creating..")
            return
        }
        SystemFannelLauncher.launch(
            fragment,
            preference,
        )
    }

    private fun setFannelManageListViewOnLongPressItemClickListener(
        fannelManageListAdapter: FannelManageAdapter,
    ) {
        fannelManageListAdapter.longPressItemClickListener = object: FannelManageAdapter.OnLongPressItemClickListener {
            override fun onItemClick(holder: FannelManageAdapter.FannelManageViewHolder) {
                val position = holder.bindingAdapterPosition
                val fannelName =
                    fannelManageListAdapter.fannelNameList.getOrNull(position)
                        ?: return
                if(
                    !FactFannel.isFactFannel(fannelName)
                ){
                    FactFannel.creatingToast()
                    return
                }
                val repValsMap =
                    SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                        context,
                        File(cmdclickDefaultAppDirPath, fannelName).absolutePath
                    )
                val settingVariableList =
                    CommandClickVariables.extractValListFromHolder(
                        CommandClickVariables.makeMainFannelConList(
                            fannelName,
                            repValsMap
                        ),
                        settingSectionStart,
                        settingSectionEnd
                    )
                val switchOn = FannelSettingMap.switchOn
                val menuNameToEnableList = LongPressMenuName.values().map {
                    val menuSettingPathSrc = menuNameToMenuPathMap.get(it)
                        ?: String()
                    val menuSettingPath = ScriptPreWordReplacer.replace(
                        menuSettingPathSrc,
                        SystemFannel.preference,
                    )
                    val jsPathSettingValName = menuNameToJsPathSettingValsMap.get(it)
                        ?: String()
                    val jsPath = SettingVariableReader.getStrValue(
                        settingVariableList,
                        jsPathSettingValName,
                        String(),
                    )
                    val isJsPath = File(jsPath).isFile
                            || jsPath == switchOn
                    if(
                        !isJsPath
                    ) return@map String() to null
                    val isExist = ReadText(
                        File(menuSettingPath).absolutePath
                    ).textToList().contains(fannelName)
                    val prefix = when(isExist){
                        true -> LongPressMenuPrefix.REMOVE.prefix
                        else -> LongPressMenuPrefix.ADD.prefix
                    }
                    prefix + it.menu to isExist
                }.filter {
                    it.second != null
                }
                LongPressManageListDialog.handle(
                    context,
                    fannelName,
                    menuNameToEnableList,

                )
            }
        }
    }

//    private fun setFannelManageListViewOnDeleteItemClickListener (
//        fannelManageAdapter: FannelManageAdapter,
//        searchText: AppCompatEditText,
//    ){
//        fannelManageAdapter.deleteItemClickListener = object: FannelManageAdapter.OnDeleteItemClickListener {
//            override fun onItemClick(holder: FannelManageAdapter.FannelManageViewHolder) {
//                val bindingAdapterPosition = holder.bindingAdapterPosition
//                val historyLine = fannelManageAdapter.fannelNameList[bindingAdapterPosition]
////                val appDirName =
////                    FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
////                        historyLine
////                    )
//                val fannelName =
//                    FannelHistoryManager.getFannelNameFromAppHistoryFileName(
//                        historyLine
//                    )
//                val selectedHistoryFile = FannelHistoryManager.makeAppHistoryFileNameForInit(
////                    appDirName,
//                    fannelName
//                )
//                execDeleteHistoryFile(
//                    selectedHistoryFile,
//                    cmdclickAppHistoryDirAdminPath,
//                    fannelManageAdapter,
//                    bindingAdapterPosition,
//                    searchText,
//                )
////                fannelHistoryAdapter.notifyDataSetChanged()
////                updateRecyclerJob?.cancel()
////                updateRecyclerJob = CoroutineScope(Dispatchers.Main).launch {
////                    withContext(Dispatchers.IO){
////                        delay(200)
////                    }
////                    urlHistoryListView?.layoutManager?.scrollToPosition(
////                        fannelHistoryAdapter.itemCount - 1
////                    )
////                }
//            }
//        }
//    }

    private fun setFannelManageListViewOnLogoItemClickListener (
        fannelManageAdapter: FannelManageAdapter,
    ){
        fannelManageAdapter.shareItemClickListener = object: FannelManageAdapter.OnShareItemClickListener {
            override fun onItemClick(holder: FannelManageAdapter.FannelManageViewHolder) {
                val position = holder.bindingAdapterPosition
                val fannelName =
                    fannelManageAdapter.fannelNameList.getOrNull(position)
                        ?: return
                if(
                    !FactFannel.isFactFannel(fannelName)
                    && fannelName != SystemFannel.home
                ){
                    FactFannel.creatingToast()
                    return
                }
//                val urlHistoryAdapterRelativeLayout = holder.fannelHistoryAdapterRelativeLayout
                CoroutineScope(Dispatchers.Main).launch {
                    QrDialogMethod.launchPassDialog(
                        fragment,
//                currentAppDirPath,
                        fannelName,
                    )
//                    withContext(Dispatchers.Main){
//                        ToastUtils.showShort("share")
//                    }
//                    val pngImagePathObj = HistoryShareImage.makePngImageFromView(
//                        context,
//                        urlHistoryAdapterRelativeLayout
//                    ) ?: return@launch
//                    withContext(Dispatchers.Main) {
//                        IntentVariant.sharePngImage(
//                            pngImagePathObj,
//                            context,
//                        )
//                    }
                }
            }
        }
    }

    private fun setItemTouchHelper(
        recyclerView: RecyclerView,
        fannelManageAdapter: FannelManageAdapter,
        searchText: AppCompatEditText?,
    ){
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.ACTION_STATE_IDLE,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    if(
                        direction != ItemTouchHelper.LEFT
                        && direction != ItemTouchHelper.RIGHT
                    ) return
                    val position = viewHolder.layoutPosition
                    val fannelName = fannelManageAdapter.fannelNameList.getOrNull(position)
                        ?: return
//                    val appDirName =
//                        FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
//                            historyLine
//                        )
                    DeleteConfirmDialog.launch(
                        fragment,
                        fannelName,
                        recyclerView,
                        fannelManageAdapter,
                        position,
                        searchText,
                    )
                }

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)
                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.alpha = 1.0f
                }
            })
        mIth.attachToRecyclerView(recyclerView)
    }

//    private fun invokeItemSetLongTimeClickListenerForHistory(
//        historyListAdapter: FannelHistoryAdapter,
//        searchText: EditText,
//        cmdclickAppHistoryDirAdminPath: String,
//    ){
//        historyListAdapter.itemLongClickListener = object :
//            FannelHistoryAdapter.OnItemLongClickListener {
//            override fun onItemLongClick(
//                itemView: View,
//                holder: FannelHistoryAdapter.FannelHistoryViewHolder
//            ) {
//                val appDirName = holder.appDirNameTextView.text.toString()
//                val fannelName = holder.fannelNameTextView.text.toString()
//                val selectedHistoryFile = AppHistoryManager.makeAppHistoryFileNameForInit(
//                    appDirName,
//                    fannelName
//                )
//                val popup = PopupMenu(
//                    context,
//                    itemView
//                )
//                val inflater = popup.menuInflater
//                inflater.inflate(
//                    com.puutaro.commandclick.R.menu.history_admin_menu,
//                    popup.menu
//                )
//                popup.menu.add(
//                    HistoryMenuEnums.DELETE.groupId,
//                    HistoryMenuEnums.DELETE.itemId,
//                    HistoryMenuEnums.DELETE.order,
//                    HistoryMenuEnums.DELETE.itemName,
//                )
//                popup.setOnMenuItemClickListener {
//                        menuItem ->
//                    execDeleteHistoryFile(
//                        selectedHistoryFile,
//                        cmdclickAppHistoryDirAdminPath,
//                        historyListAdapter,
//                    )
//                    searchText.text.clear()
//                    true
//                }
//                popup.show()
//            }
//        }
//    }


    private object DeleteConfirmDialog {

        private var deleteConfirmDialog: Dialog? = null

        fun launch(
            fragment: Fragment,
            fannelName: String,
            recyclerView: RecyclerView,
            fannelManageAdapter: FannelManageAdapter,
            position: Int,
            searchText: AppCompatEditText?,
        ){
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    execLaunch(
                        fragment,
                        fannelName,
                        recyclerView,
                        fannelManageAdapter,
                        position,
                        searchText,
                    )
                }
            }
        }
        private fun execLaunch(
            fragment: Fragment,
            fannelName: String,
            recyclerView: RecyclerView,
            fannelManageAdapter: FannelManageAdapter,
            position: Int,
            searchText: AppCompatEditText?,
        ){
            val context = fragment.context
                ?: return
            deleteConfirmDialog = Dialog(
                context
            )
            deleteConfirmDialog?.setContentView(
                R.layout.confirm_text_dialog
            )
            val confirmTitleTextView =
                deleteConfirmDialog?.findViewById<AppCompatTextView>(
                    R.id.confirm_text_dialog_title
                )
            val confirmTitle = when(
                PreInstallFannel.isPreinstallFannel(fannelName)
            ){
                false -> "Delete ok?"
                else -> "Init ok?"
            }
            confirmTitleTextView?.text = confirmTitle
            val confirmContentTextView =
                deleteConfirmDialog?.findViewById<AppCompatTextView>(
                    R.id.confirm_text_dialog_text_view
                )
            confirmContentTextView?.text =
                SystemFannel.convertDisplayNameToFannelName(fannelName)
            val confirmCancelButton =
                deleteConfirmDialog?.findViewById<AppCompatImageButton>(
                    R.id.confirm_text_dialog_cancel
                )
            confirmCancelButton?.setOnClickListener {
                deleteConfirmDialog?.dismiss()
                deleteConfirmDialog = null
                cancelProcess(
                    recyclerView,
                    position,
                )
            }
            deleteConfirmDialog?.setOnCancelListener {
                deleteConfirmDialog?.dismiss()
                deleteConfirmDialog = null
                cancelProcess(
                    recyclerView,
                    position,
                )
            }
            val confirmOkButton =
                deleteConfirmDialog?.findViewById<AppCompatImageButton>(
                    R.id.confirm_text_dialog_ok
                )
            confirmOkButton?.setOnClickListener {
                deleteConfirmDialog?.dismiss()
                deleteConfirmDialog = null
                execDeleteFannel(
                    context,
                    fannelName,
                    fannelManageAdapter,
                    position,
                    searchText,
                )
            }
            deleteConfirmDialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            deleteConfirmDialog?.window?.setGravity(
                Gravity.CENTER
            )
            deleteConfirmDialog?.show()
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
            context: Context?,
            fannelName: String,
            fannelManageAdapter: FannelManageAdapter,
            position: Int,
            searchText: AppCompatEditText?,
        ) {
            PinFannelManager.remove(fannelName)
            LongPressMenuTool.removeAll(fannelName)
            deleteFannel(fannelName)
            val isPreInstallFannel =
                PreInstallFannel.isPreinstallFannel(fannelName)
            when(isPreInstallFannel) {
                false -> {
                    fannelManageAdapter.fannelNameList.removeAt(position)
                    fannelManageAdapter.notifyItemRemoved(position)
                }
                else -> fannelManageAdapter.notifyDataSetChanged()
            }
            searchText?.text?.clear()
            if(isPreInstallFannel) {
                CoroutineScope(Dispatchers.IO).launch {
                    val createFannelName = withContext(Dispatchers.IO) {
                        FactFannel.convertToFactFannelName(fannelName)
                    }
                    val fannelList = withContext(Dispatchers.IO){
                        UrlFileSystems.getFannelList(context).split("\n")
                    }
                    withContext(Dispatchers.IO) {
                        UrlFileSystems.createFileByOverride(
                            context,
                            createFannelName,
                            fannelList
                        )
                    }
                }
            }
        }

        private fun deleteFannel(
            fannelName: String
        ){
            if(
                !PreInstallFannel.isPreinstallFannel(fannelName)
            ){
                FileSystems.removeFileWithDir(
                    File(UsePath.cmdclickDefaultAppDirPath, fannelName)
                )
                return
            }
            val deleteFannelName =
                FactFannel.convertToFactFannelName(fannelName)
            deleteAndSaveSettingImage(deleteFannelName)

        }

        fun deleteAndSaveSettingImage(
            fannelName: String
        ){
            val settingImageDirPath = ScriptPreWordReplacer.replace(
                UsePath.fannelSettingImagesDirPath,
                fannelName
            )
            val cmdclickTempDownloadDirPath = UsePath.cmdclickTempDownloadDirPath
            FileSystems.removeAndCreateDir(cmdclickTempDownloadDirPath)
            FileSystems.copyDirectory(
                settingImageDirPath,
                cmdclickTempDownloadDirPath,
            )
            FileSystems.removeFileWithDir(
                File(
                    UsePath.cmdclickDefaultAppDirPath,
                    fannelName,
                )
            )
            FileSystems.copyDirectory(
                cmdclickTempDownloadDirPath,
                settingImageDirPath,
            )
        }

    }



//    fun updateLastModifyForHistoryAndAppDir(
//        selectedHistoryFilePath: String,
//        selectedAppDirName: String,
//    ){
//        FileSystems.updateLastModified(
//            selectedHistoryFilePath
//        )
//        FileSystems.updateLastModified(
//            File(
//                UsePath.cmdclickAppDirAdminPath,
//                selectedAppDirName + UsePath.JS_FILE_SUFFIX
//            ).absolutePath
//        )
//    }

    private fun exitDialog(
        historyListView: RecyclerView
    ){
        historyListView.layoutManager = null
        historyListView.adapter = null
        historyListView.recycledViewPool.clear()
        historyListView.removeAllViews()
        fannelHistoryDialog?.dismiss()
        fannelHistoryDialog = null
    }
}

private object LongPressManageListDialog {

    private var longPressMenuManageDialog: Dialog? = null
    fun handle(
        context: Context?,
        fannelName: String,
        longPressMenuToIsExistList: List<Pair<String, Boolean?>>
    ){
        if(context == null)  return

        longPressMenuManageDialog = Dialog(
            context,
        )
        longPressMenuManageDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        val listDialogTitle = longPressMenuManageDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )
        listDialogTitle?.text = "Select"
        val listDialogMessage = longPressMenuManageDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = longPressMenuManageDialog?.findViewById<AppCompatEditText>(
            R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = longPressMenuManageDialog?.findViewById<AppCompatImageButton>(
            R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            longPressMenuManageDialog?.dismiss()
            longPressMenuManageDialog = null
        }
        setListView(
            context,
            fannelName,
            longPressMenuToIsExistList
        )

        longPressMenuManageDialog?.setOnCancelListener {
            longPressMenuManageDialog?.dismiss()
            longPressMenuManageDialog = null
        }
        longPressMenuManageDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        longPressMenuManageDialog?.window?.setGravity(Gravity.BOTTOM)
        longPressMenuManageDialog?.show()
    }

    private fun setListView(
        context: Context,
        fannelName: String,
        longPressMenuToIsExistList: List<Pair<String, Boolean?>>,
    ) {

        val subMenuListView =
            longPressMenuManageDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            ) ?: return
        val zeroIcon = R.drawable.icons8_wheel
        val longPressMenuList = longPressMenuToIsExistList.map {
            val menuName = it.first
            val isExist = it.second
            val icon = when(isExist){
                null -> LongPressMenuPrefix.ZERO.simbol
                true -> LongPressMenuPrefix.REMOVE.simbol
                false -> LongPressMenuPrefix.ADD.simbol
            }
            menuName to icon
        }.filter {
            it.second != zeroIcon
        }
        val subMenuAdapter = SubMenuAdapter(
            context,
            longPressMenuList.toMutableList()
        )
        subMenuListView.adapter = subMenuAdapter
        invokeItemSetClickListenerForLanguageType(
            context,
            subMenuListView,
            fannelName,
            longPressMenuToIsExistList
        )
    }

    private fun invokeItemSetClickListenerForLanguageType(
        context: Context,
        subMenuListView: ListView,
        fannelName: String,
        longPressMenuToIsExistList: List<Pair<String, Boolean?>>,
    ){
        subMenuListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val selectedMenuNameStrWithPrefix = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            val menuNameToExist = longPressMenuToIsExistList.firstOrNull {
                it.first == selectedMenuNameStrWithPrefix
            } ?: return@setOnItemClickListener
            val menuNameStr = menuNameToExist.first
            val longPressMenuName = LongPressMenuName.values().firstOrNull {
                menuNameStr.split(" ").filterIndexed {
                        index, _ -> index > 0
                }.joinToString(
                    " "
                ) == it.menu
            }
            val menuPathSrc = menuNameToMenuPathMap.get(longPressMenuName)
                ?: return@setOnItemClickListener
            val menuPath = ScriptPreWordReplacer.replace(
                menuPathSrc,
                SystemFannel.preference,
            )
            val menuConList = ReadText(menuPath).textToList()
            val updateLongPressMenuToIsExistList = when(menuNameToExist.second){
                null -> longPressMenuToIsExistList
                true -> {
                    val removedLongPressMenuList = menuConList.filter {
                        it != fannelName
                    }
                    FileSystems.writeFile(
                        menuPath,
                        removedLongPressMenuList.joinToString("\n")
                    )
                    longPressMenuToIsExistList.map {
                        val curMenuNameStr = it.first
                        if(
                            curMenuNameStr != menuNameStr
                        ) return@map it
                        val add = LongPressMenuPrefix.ADD
                        add.prefix + longPressMenuName?.menu to false
                    }
                }
                else -> {
                    val addedLongPressMenuList = listOf(fannelName) + menuConList.filter {
                        it != fannelName
                    }
                    FileSystems.writeFile(
                        menuPath,
                        addedLongPressMenuList.joinToString("\n")
                    )
                    longPressMenuToIsExistList.map {
                        val curMenuNameStr = it.first
                        if(
                            curMenuNameStr != menuNameStr
                        ) return@map it
                        val remove = LongPressMenuPrefix.REMOVE
                        remove.prefix + longPressMenuName?.menu to true
                    }
                }
            }
            longPressMenuManageDialog?.dismiss()
            longPressMenuManageDialog = null
            handle(
                context,
                fannelName,
                updateLongPressMenuToIsExistList,
            )
        }
    }
}

//private fun deleteOverHistory(
//    cmdclickAppHistoryDirAdminPath: String
//){
//    val leavesHistoryNum = 50
//    val dirFiles = FileSystems.sortedFiles(
//        cmdclickAppHistoryDirAdminPath,
//    )
//    if(dirFiles.isEmpty()) return
//    val deleteFileNum = dirFiles.size - leavesHistoryNum
//    if(deleteFileNum <= 0) return
//    val deletingFiles = dirFiles.take(deleteFileNum)
//    deletingFiles.forEach {
//        try {
//            val fileEntry = File(
//                cmdclickAppHistoryDirAdminPath,
//                it
//            )
//            if(!fileEntry.isFile) return@forEach
//            fileEntry.delete()
//        } catch (e: Exception) {
//            println("pass")
//        }
//    }
//}

private enum class LongPressMenuName(val menu: String){
    SRC_ANCHOR("src anchor"),
    SRC_IMAGE_ANCHOR("src image anchor"),
    IMAGE_ANCHOR("image"),
}

private enum class LongPressMenuPrefix(
    val prefix: String,
    val simbol: Int,
){
    ZERO(String(), com.skydoves.colorpickerview.R.drawable.wheel),
    REMOVE("Remove ", R.drawable.minus),
    ADD("Add ", R.drawable.icons8_plus),
}

private val menuNameToMenuPathMap = mapOf(
    LongPressMenuName.IMAGE_ANCHOR to UsePath.imageLongPressMenuFilePath,
    LongPressMenuName.SRC_ANCHOR to UsePath.srcAnchorLongPressMenuFilePath,
    LongPressMenuName.SRC_IMAGE_ANCHOR to UsePath.srcImageAnchorLongPressMenuFilePath,
)

private val menuNameToJsPathSettingValsMap = mapOf(
    LongPressMenuName.IMAGE_ANCHOR to CommandClickScriptVariable.IMAGE_LONG_PRESS_JS_PATH,
    LongPressMenuName.SRC_ANCHOR to CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_JS_PATH,
    LongPressMenuName.SRC_IMAGE_ANCHOR to CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_JS_PATH,
)

