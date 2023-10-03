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
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.FannelListVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
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
import java.io.File


class InstallFromFannelRepo(
    private val cmdIndexFragment: CommandIndexFragment,
    private val currentAppDirPath: String,
) {
    private val cmdclickFannelDirPath = UsePath.cmdclickFannelDirPath
    private val context = cmdIndexFragment.context
    private val binding = cmdIndexFragment.binding
    private val cmdListView = binding.cmdList
    private val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
    private val blankListMark = InstallFannelList.blankListMark
    private val fannelDirSuffix = UsePath.fannelDirSuffix
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
            InstallFannelList.makeFannelListForListView().toMutableList()
        )
        cancelButtonSetOnClickListener()
        installFannelRecyclerView?.adapter = installFannelListAdapter
        installFannelRecyclerView?.layoutManager = PreLoadLayoutManager(
            context,
        )
        installFannelRecyclerView?.scrollToPosition(
        installFannelListAdapter.itemCount - 1
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
                    val itemContext = itemView.context
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
                    if(
                        !File(selectedFannelPath).isFile
                    ) return
                    FileSystems.copyFile(
                        selectedFannelPath,
                        "${currentAppDirPath}/${selectedFannel}"
                    )
                    val selectedFannelName = 
                        selectedFannel
                            .removeSuffix(UsePath.JS_FILE_SUFFIX)
                            .removeSuffix(UsePath.SHELL_FILE_SUFFIX)
                    val fannelDir = selectedFannelName + fannelDirSuffix
                    val selectedFannelDirPath =
                        "${UsePath.cmdclickFannelItselfDirPath}/${fannelDir}"
                    if(
                        !File(selectedFannelDirPath).isDirectory
                    ) {
                        Toast.makeText(
                            cmdIndexFragment.context,
                            "install ok: ${selectedFannelName}",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }
                    FileSystems.copyDirectory(
                        selectedFannelDirPath,
                        "${currentAppDirPath}/${fannelDir}"
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
                        "install ok: ${selectedFannelName}",
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
    val blankListMark = "Let's press sync button at left bellow"
    fun updateInstallFannelList(
        fannelRecyclerView: RecyclerView?,
        updatedFannelList: List<String>
    ) {
        val installFannelListAdapter = fannelRecyclerView?.adapter as? InstallFannelListAdapter
            ?: return
        installFannelListAdapter.fannelInstallerList.clear()
        installFannelListAdapter.fannelInstallerList.addAll(updatedFannelList)
        installFannelListAdapter.notifyDataSetChanged()
        fannelRecyclerView.scrollToPosition(installFannelListAdapter.itemCount - 1)

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

