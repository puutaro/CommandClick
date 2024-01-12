package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.app.Dialog
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.InstallFannelListAdapter
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.service.GitCloneService
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class InstallFromFannelRepo(
    private val cmdIndexFragment: CommandIndexFragment,
    private val currentAppDirPath: String,
) {
    private val fannelListUpdateDelayMiliSec = InstallFannelList.fannelListUpdateDelayMiliSec
    private val cmdclickFannelDirPath = UsePath.cmdclickFannelDirPath
    private val context = cmdIndexFragment.context
    private val binding = cmdIndexFragment.binding
    private val cmdListView = binding.cmdList
    private val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
    private val blankListMark = InstallFannelList.blankListMark
    val languageType = LanguageTypeSelects.JAVA_SCRIPT
    val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
            languageType
        )

    fun install(){
        if(context == null) return
        FileSystems.createDirs(UsePath.cmdclickFannelListDirPath)
        cmdIndexFragment.installFannelDialog = Dialog(context)
        cmdIndexFragment.installFannelDialog?.setContentView(
            R.layout.install_fannel_dialog_layout
        )
        val installFannelRecyclerView = cmdIndexFragment.installFannelDialog?.findViewById<RecyclerView>(
            R.id.install_fannel_recycler
        )
        val installFannelListAdapter = InstallFannelListAdapter(
            cmdIndexFragment,
            currentAppDirPath,
            InstallFannelList.makeFannelListForListView().toMutableList(),
        )
        cancelButtonSetOnClickListener()
        installFannelRecyclerView?.adapter = installFannelListAdapter
        installFannelRecyclerView?.layoutManager = PreLoadLayoutManager(
            context,
        )
        setFannelContentsClickListener(
            installFannelRecyclerView
        )
        setFannelItemClickListener(
            installFannelRecyclerView
        )
        syncButtonSetOnClickListener()
        searchByEditText(
            installFannelRecyclerView
        )
        cmdIndexFragment.installFannelDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        cmdIndexFragment.installFannelDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        cmdIndexFragment.installFannelDialog?.setOnCancelListener {
            cmdIndexFragment.installFannelDialog?.dismiss()
            terminalViewModel.onDialog = false
        }
        cmdIndexFragment.installFannelDialog?.show()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                delay(fannelListUpdateDelayMiliSec)
            }
            withContext(Dispatchers.Main){
                installFannelRecyclerView?.scrollToPosition(
                    installFannelListAdapter.itemCount - 1
                )
            }
        }
    }

    private fun cancelButtonSetOnClickListener(
    ){
        val cancelImageButton = cmdIndexFragment.installFannelDialog?.findViewById<ImageButton>(
            R.id.install_fannel_cancel_button
        )
        cancelImageButton?.setOnClickListener {
            cmdIndexFragment.installFannelDialog?.dismiss()
            terminalViewModel.onDialog = false
        }
    }


    private fun searchByEditText(
        fannelRecyclerView: RecyclerView?,
    ){
        val searchEditText = cmdIndexFragment.installFannelDialog?.findViewById<EditText>(
            R.id.install_fannel_search_edit_text
        ) ?: return
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchEditText.hasFocus()) return
                val updateFannelList = InstallFannelList.makeFannelListForListView()

                val filteredFannelList = updateFannelList.filter {
                    Regex(
                        s.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }
                InstallFannelList.updateInstallFannelList(
                    fannelRecyclerView,
                    filteredFannelList
                )
            }
        })
    }

    private fun syncButtonSetOnClickListener(
    ){
        val syncImageButton = cmdIndexFragment.installFannelDialog?.findViewById<ImageButton>(
            R.id.install_fannel_sync_button
        )
        syncImageButton?.setOnClickListener {
            gitCloneAndMakeFannelList()
        }
    }

    private fun setFannelContentsClickListener(
        fannelRecyclerView: RecyclerView?,
    ){
        val installFannelListAdapter =
            fannelRecyclerView?.adapter as? InstallFannelListAdapter
                ?: return
        installFannelListAdapter.fannelContentsClickListener =
            object : InstallFannelListAdapter.OnFannelContentsItemClickListener {
                override fun onFannelContentsClick(
                    itemView: View,
                    holder: InstallFannelListAdapter.FannelInstallerListViewHolder
                ) {
                    val fannelNameTextView = holder.fannelNameTextView
                    val fannelName = fannelNameTextView.text.toString()
                    ScriptFileDescription.show(
                        cmdIndexFragment,
                        ReadText(
                            cmdclickFannelDirPath,
                            fannelName,
                        ).textToList(),
                        cmdclickFannelDirPath,
                        fannelName
                    )
                }
            }
    }

    private fun setFannelItemClickListener(
        fannelRecyclerView: RecyclerView?,
    ){
        val installFannelListAdapter =
            fannelRecyclerView?.adapter as? InstallFannelListAdapter
                ?: return
        installFannelListAdapter.fannelItemClickListener =
            object : InstallFannelListAdapter.OnFannelItemClickListener {
                override fun onFannelItemClick(
                    itemView: View,
                    holder: InstallFannelListAdapter.FannelInstallerListViewHolder
                ) {
                    itemView.context.stopService(
                        Intent(
                            cmdIndexFragment.activity,
                            GitCloneService::class.java
                        )
                    )
                    val fannelNameTextView = holder.fannelNameTextView
                    val selectedFannel = fannelNameTextView.text.toString()
                    if(
                        selectedFannel == blankListMark
                    ) return
                    val selectedFannelPath =
                        "${UsePath.cmdclickFannelItselfDirPath}/${selectedFannel}"
                    val selectedFannelPathObj = File(selectedFannelPath)
                    if(
                        !selectedFannelPathObj.isFile
                    ) return
                    val installFannelPathObj =  File("${currentAppDirPath}/${selectedFannel}")
                    val compMessage = when(installFannelPathObj.isFile) {
                        false -> "install ok: ${selectedFannel}"
                        else -> "update ok: ${selectedFannel}"
                    }
                    FileSystems.execCopyFileWithDir(
                        selectedFannelPathObj,
                        installFannelPathObj,
                        true,
                    )
                    val searchEditText = cmdIndexFragment.installFannelDialog?.findViewById<EditText>(
                        R.id.install_fannel_search_edit_text
                    ) ?: return
                    searchEditText.setText(String())
                    CommandListManager.execListUpdateForCmdIndex(
                        currentAppDirPath,
                        cmdListView,
                    )
                    Toast.makeText(
                        cmdIndexFragment.context,
                        compMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun gitCloneAndMakeFannelList(){
        if(context == null) return
        val intent = Intent(
            cmdIndexFragment.activity,
            GitCloneService::class.java
        )
        context.startForegroundService(intent)
    }
}

object InstallFannelList {

    private val cmdclickFannelListSeparator = FannelListVariable.cmdclickFannelListSeparator
    val blankListMark = "Let's press sync button at right bellow"
    val fannelListUpdateDelayMiliSec = 400L
    fun updateInstallFannelList(
        installFannelRecyclerView: RecyclerView?,
        updatedFannelList: List<String>
    ) {
        val installFannelListAdapter = installFannelRecyclerView?.adapter as? InstallFannelListAdapter
            ?: return
        installFannelListAdapter.fannelInstallerList.clear()
        installFannelListAdapter.fannelInstallerList.addAll(updatedFannelList)
        installFannelListAdapter.notifyDataSetChanged()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                delay(fannelListUpdateDelayMiliSec)
            }
            withContext(Dispatchers.Main){
                installFannelRecyclerView.scrollToPosition(
                    installFannelListAdapter.itemCount - 1
                )
            }
        }
    }

    fun makeFannelListForListView(): List<String> {
        val fannelListSource = ReadText(
            UsePath.cmdclickFannelListDirPath,
            UsePath.fannelListMemoryName,
        ).readText()
            .replace(Regex("\\*\\*([a-zA-Z0-9]*)\\*\\*"), "*$1")
            .split(cmdclickFannelListSeparator)
        return if (
            fannelListSource.isNotEmpty()
            && !fannelListSource
                .firstOrNull()
                ?.trim()
                .isNullOrEmpty()
        ) {
            fannelListSource
        } else mutableListOf(blankListMark)
    }
}

