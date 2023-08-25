package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditFragmentTitle
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.CopyAppDirEventForEdit
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FormDialogForListIndexOrButton
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.ExecJsScriptInEdit
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.DialogObject
import com.puutaro.commandclick.util.Editor
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder


class WithIndexListView(
    private val editFragment: EditFragment
) {
    private val context = editFragment.context
    private val binding = editFragment.binding
    private val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    private var currentSetVariableMap: Map<String, String>? = mapOf()
    private var currentAppDirPath = String()
    private var currentScriptName = String()
    private val editListRecyclerView = binding.editListRecyclerView
    private val editListSearchEditText = binding.editListSearchEditText
    private var promptDialog: Dialog? = null
    private var confirmDialog: Dialog? = null
    private var confirmDialog2: Dialog? = null
    private var clickDirPath = String()
    private val clickDirName = "click"
    private val itemClickJsName = "itemClick.js"
    private val menuClickJsName = "menuClick.js"
    private val subMenuClickJsName = "subMenuClick.js"
    private val formDialogForListIndexOrButton = FormDialogForListIndexOrButton(
        editFragment
    )
    private var selectedItemForCopy = String()
    private val prefixRegex = Regex("^content.*fileprovider/root/storage")
    private val getDirectory = editFragment.registerForActivityResult(
        ActivityResultContracts.OpenDocument()) { uri ->
        if (
            uri == null
            || uri.toString() == String()
        ) return@registerForActivityResult
        val pathSource = runBlocking {
            File(
                withContext(Dispatchers.IO) {
                    URLDecoder.decode(
                        uri.toString(), Charsets.UTF_8.name()
                    )
                }.replace(prefixRegex, "/storage")
            )
        }
        val targetDirectoryPath =
            pathSource.parent ?: String()
        val sourceScriptFilePath = "${filterDir}/${selectedItemForCopy}"
        val targetScriptFilePathSource = "${targetDirectoryPath}/${selectedItemForCopy}"
        val targetScriptFilePath = if(
            targetScriptFilePathSource == sourceScriptFilePath
        ) "${targetDirectoryPath}/" +
                "${CommandClickScriptVariable.makeCopyPrefix()}_${selectedItemForCopy}"
        else targetScriptFilePathSource
        FileSystems.copyFile(
            sourceScriptFilePath,
            targetScriptFilePath
        )
        val sourceFannelName =
            selectedItemForCopy
                .removeSuffix(UsePath.JS_FILE_SUFFIX)
                .removeSuffix(UsePath.SHELL_FILE_SUFFIX)
        val targetFannelName =
            File(targetScriptFilePath).name
                .removeSuffix(UsePath.JS_FILE_SUFFIX)
                .removeSuffix(UsePath.SHELL_FILE_SUFFIX)
        val sourceFannelDir = sourceFannelName + UsePath.fannelDirSuffix
        val targetFannelDir = targetFannelName + UsePath.fannelDirSuffix
        FileSystems.copyDirectory(
            "${filterDir}/${sourceFannelDir}",
            "${targetDirectoryPath}/${targetFannelDir}"
        )
        listIndexListUpdateFileList(
            editFragment,
            makeFileList()
        )
        Toast.makeText(
            context,
            "copy file ok",
            Toast.LENGTH_LONG
        ).show()
    }

    private val getFile = editFragment.registerForActivityResult(
        ActivityResultContracts.OpenDocument()) { uri ->
        if (
            uri == null
            || uri.toString() == String()
        ) return@registerForActivityResult
        val pathSource = runBlocking {
            File(
                withContext(Dispatchers.IO) {
                    URLDecoder.decode(
                        uri.toString(), Charsets.UTF_8.name()
                    )
                }.replace(prefixRegex, "/storage")
            )
        }
        val sourceFilePath =
            pathSource.absolutePath ?: String()
        val sourceDirPath =
            pathSource.parent ?: String()
        val getFileName = pathSource.name
        val targetScriptFilePathSource = "${filterDir}/${getFileName}"
        FileSystems.copyFile(
            sourceFilePath,
            targetScriptFilePathSource
        )
        val sourceFannelName =
            getFileName
                .removeSuffix(UsePath.JS_FILE_SUFFIX)
                .removeSuffix(UsePath.SHELL_FILE_SUFFIX)
        val sourceFannelDir = sourceFannelName + UsePath.fannelDirSuffix
        FileSystems.copyDirectory(
            "${sourceDirPath}/${sourceFannelDir}",
            "${filterDir}/${sourceFannelDir}"
        )
        listIndexListUpdateFileList(
            editFragment,
            makeFileList()
        )
        Toast.makeText(
            context,
            "get file ok",
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        private const val throughMark = "-"
        private const val noExtend = "NoExtend"

        private var filterDir = String()
        private var filterPrefix = String()
        private var filterSuffix = String()
        private var fannelDirName = String()
        private var fannelDirPath = String()

        fun makeFileList(): MutableList<String> {
            val fileListSource = FileSystems.sortedFiles(
                filterDir,
            ).filter {
                it.startsWith(filterPrefix)
                        && judgeBySuffixForIndex(it, filterSuffix)
                        && File("$filterDir/$it").isFile
            }
            if(
                fileListSource.isEmpty()
            ) return mutableListOf(throughMark)
            return fileListSource.toMutableList()
        }

        fun listIndexListUpdateFileList(
            editFragment: EditFragment,
            updateList: List<String>,
        ){
            val editListRecyclerView = editFragment.binding.editListRecyclerView
            val listIndexForEditAdapter =
                editListRecyclerView.adapter as ListIndexForEditAdapter
            listIndexForEditAdapter.listIndexList.clear()
            listIndexForEditAdapter.listIndexList.addAll(updateList)
            listIndexForEditAdapter.notifyDataSetChanged()
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                editListRecyclerView.layoutManager?.scrollToPosition(
                    listIndexForEditAdapter.itemCount - 1
                )
            }
        }

        private fun judgeBySuffixForIndex(
            targetStr: String,
            filterSuffix: String,
        ): Boolean {
            if(filterSuffix != noExtend) {
                return filterSuffix.split("&").any {
                    targetStr.endsWith(it)
                }
            }
            return !Regex("\\..*$").containsMatchIn(targetStr)
        }
    }

    var languageType = LanguageTypeSelects.JAVA_SCRIPT
    var languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
            languageType
        )
    var settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String

    var settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String

    fun create(
        editParameters: EditParameters,
    ) {
        binding.editListLinearLayout.isVisible = true
        binding.editTextScroll.isVisible = false
        val context = editParameters.context
        currentSetVariableMap = editParameters.setVariableMap
        currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        currentScriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        fannelDirName = currentScriptName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
        fannelDirPath = "${currentAppDirPath}/${fannelDirName}"

        val indexListMap = getIndexListMap(
            editParameters
        )
        filterDir = getFilterListDir(
            indexListMap,
            currentAppDirPath,
            fannelDirName,
            currentScriptName
        )
        filterPrefix = getFilterPrefix(
            indexListMap,
        )
        filterSuffix = getFilterSuffix(
            indexListMap,
        )

        FileSystems.createDirs(filterDir)
        clickDirPath = "${fannelDirPath}/${clickDirName}"
        FileSystems.createDirs(clickDirPath)

        val menuList = getMenuMap(
            editParameters
        )?.get(IndexListEditMenu.menu.name)
            ?: emptyList()

        val fileList = makeFileList()


        val editListRecyclerView =
            binding.editListRecyclerView
        val listIndexForEditAdapter = ListIndexForEditAdapter(
            editFragment,
            filterDir,
            fileList
        )
        editListRecyclerView.adapter = listIndexForEditAdapter
        val preLoadLayoutManager = PreLoadLayoutManager(
            context,
        )
        preLoadLayoutManager.stackFromEnd = true
        editListRecyclerView.layoutManager = preLoadLayoutManager
        invokeItemSetClickListenerForFileList()
        invokeContentsSetClickListenerForFileList()
        invokeItemSetLongTimeClickListenerForHistory(
            menuList
        )
        makeSearchEditText(
            editListSearchEditText,
            readSharePreffernceMap
        )
//        TODO remove to confirm normal scroll to bottom
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(100)
//            editListRecyclerView.layoutManager?.scrollToPosition(
//                listIndexForEditAdapter.itemCount - 1
//            )
//        }
    }

    private fun invokeItemSetClickListenerForFileList() {
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexForEditAdapter.fannelNameClickListener =
            object: ListIndexForEditAdapter.OnFannelNameItemClickListener {
                override fun onFannelNameClick(
                    itemView: View,
                    holder: ListIndexForEditAdapter.ListIndexListViewHolder
                ) {
                    val selectedItem =
                        holder.fannelNameTextView.text.toString()
                    execItemClickJs(
                        clickDirPath,
                        selectedItem,
                    )
                    editListSearchEditText.setText(String())
                }
        }
    }

    private fun invokeContentsSetClickListenerForFileList() {
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexForEditAdapter.fannelContentsClickListener = object: ListIndexForEditAdapter.OnFannelContentsItemClickListener {
            override fun onFannelContentsClick(
                itemView: View,
                holder: ListIndexForEditAdapter.ListIndexListViewHolder
            ) {
                val selectedItem =
                    holder.fannelNameTextView.text.toString()
                val contents = if(
                    File("${filterDir}/${selectedItem}").isFile
                ) ReadText(
                    filterDir,
                    selectedItem
                ).readText()
                else "no file"
                DialogObject.simpleTextShow(
                    itemView.context,
                    "file contents: $selectedItem",
                    contents
                )
                editListSearchEditText.setText(String())
            }
        }
    }

    private fun makeSearchEditText(
        searchText: AppCompatEditText,
        readSharePreffernceMap: Map<String, String>
    ) {
        searchText.hint = EditFragmentTitle.make(
            editFragment,
            SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir,
            ),
            SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_script_file_name,
            )
        )
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return
                val filteredUrlHistoryList = makeFileList().filter {
                    Regex(
                        searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }
                listIndexListUpdateFileList(
                    editFragment,
                    filteredUrlHistoryList
                )
            }
        })
    }

    private fun invokeItemSetLongTimeClickListenerForHistory(
        menuList: List<String>,
    ){
        val indexForEditAdapter = editListRecyclerView.adapter as ListIndexForEditAdapter
        indexForEditAdapter.itemLongClickListener = object : ListIndexForEditAdapter.OnItemLongClickListener {
            override fun onItemLongClick(
                itemView: View,
                holder: ListIndexForEditAdapter.ListIndexListViewHolder,
                position: Int
            ) {
                val selectedItem =
                    indexForEditAdapter.listIndexList[position]
                val popup = PopupMenu(
                    context,
                    itemView,
                    Gravity.BOTTOM
                )
                popup.menu.clear()
                val inflater = popup.menuInflater
                inflater.inflate(
                    com.puutaro.commandclick.R.menu.history_admin_menu,
                    popup.menu
                )
                (menuList.indices).forEach {
                    val menuSubMenuList = menuList[it].split("&")
                    val menuName = menuSubMenuList[0]
                    val itemId = mainMenuGroupId + it * 100
                    if(
                        menuSubMenuList.size < 2
                    ) {
                        popup.menu.add(
                            mainMenuGroupId,
                            itemId,
                            it,
                            menuList[it],
                        )
                        return@forEach
                    }
                    val subMenuList = menuSubMenuList.slice(
                        1 until menuSubMenuList.size
                    )
                    execAddSettingSubMenu(
                        popup,
                        itemId,
                        it,
                        menuName,
                        subMenuList
                    )
                }
                popupMenuItemSelected(
                    popup,
                    menuList,
                    selectedItem,
                )
                Toast.makeText(
                    context,
                    selectedItem,
                    Toast.LENGTH_SHORT
                ).show()
                popup.show()
            }
        }
    }

    private fun popupMenuItemSelected(
        popup: PopupMenu,
        menuList: List<String>,
        selectedItem: String,
    ){
        popup.setOnMenuItemClickListener { menuItem ->
            val itemId = menuItem.itemId
            val indexSource = itemId - mainMenuGroupId
            val menuIndex = indexSource / 100
            val subMenuIndex = indexSource % 100
            val menuSubMenuList = menuList[menuIndex].split("&")
            val menuSubMenuListSize = menuSubMenuList.size
            if(
                menuSubMenuListSize < 2
            ) {
                val menuName = menuSubMenuList[0]
                execMenuOrSubMenuClickJs(
                    clickDirPath,
                    menuClickJsName,
                    selectedItem,
                    menuName,
                )
                return@setOnMenuItemClickListener true
            }
            if(
                subMenuIndex == 0
            ) return@setOnMenuItemClickListener true
            val subMenuList = menuSubMenuList.slice(
                1 until menuSubMenuList.size
            )
            val subMenuName = subMenuList[subMenuIndex - 1]
            execMenuOrSubMenuClickJs(
                clickDirPath,
                subMenuClickJsName,
                selectedItem,
                subMenuName,
            )
            true
        }
    }

    private fun execItemClickJs(
        parentDirPath: String,
        selectedItem: String,
    ){
        if(
            selectedItem == throughMark
        ) return
        val onOverrideItemClickExec =
            editFragment.overrideItemClickExec !=
                    SettingVariableSelects.OnUrlHistoryRegisterSelects.OFF.name
        if(
            !onOverrideItemClickExec
            && selectedItem.endsWith(
                        UsePath.HTML_FILE_SUFFIX
                    ) || selectedItem.endsWith(
                        UsePath.HTM_FILE_SUFFIX
                    )
        ){
            BroadCastIntent.sendUrlCon(
                editFragment,
                "${currentAppDirPath}/$selectedItem"
            )
            return
        }
        if(
            !onOverrideItemClickExec
            && (
                    selectedItem.endsWith(
                        UsePath.JS_FILE_SUFFIX
                ) || selectedItem.endsWith(
                        UsePath.JSX_FILE_SUFFIX
                    )
                )
        ) {
            val execJsFilePath =
                "${filterDir}/${selectedItem}"
            ExecJsScriptInEdit.exec(
                editFragment,
                execJsFilePath,
            )
            clickUpdateFileList(
                selectedItem
            )
            return
        }
        if(
            !onOverrideItemClickExec
            && selectedItem.endsWith(
                UsePath.SHELL_FILE_SUFFIX
            )
        ) {
            val outputPath =
                "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
            val execShellFilePath =
                "${filterDir}/${selectedItem}"
            val execCmd =
                "${editFragment.runShell} \"${execShellFilePath}\" >> ${outputPath}"
            ExecBashScriptIntent.ToTermux(
                editFragment.runShell,
                context,
                execCmd,
                true
            )
            clickUpdateFileList(
                selectedItem
            )
            return
        }
        val execJsFilePath =
            "${parentDirPath}/${itemClickJsName}"
        terminalViewModel.jsArguments = listOf(
            parentDirPath,
            filterDir,
            selectedItem,
        ).joinToString("\t")
        ExecJsScriptInEdit.exec(
            editFragment,
            execJsFilePath,
        )
        clickUpdateFileList(
            selectedItem
        )
    }

    private fun execMenuOrSubMenuClickJs(
        parentDirPath: String,
        clickJsName: String,
        selectedItem: String,
        menuName: String,
    ){
        when(menuName){
            preMenuType.sync.name -> {
                listIndexListUpdateFileList(
                    editFragment,
                    makeFileList()
                )
                return
            }
            preMenuType.delete.name -> {
                execItemDelete(selectedItem)
                return
            }
            preMenuType.cat.name -> {
                execItemCat(
                    selectedItem
                )
                return
            }
            preMenuType.write.name -> {
                execWriteItem(
                    selectedItem
                )
                return
            }
            preMenuType.add.name -> {
                execAddItem()
                return
            }
            preMenuType.add_app_dir.name -> {
                if(
                    selectedItem == throughMark
                ) return
                execAddAppDir()
                return
            }
            preMenuType.copy_path.name -> {
                execCopyPath(
                    selectedItem
                )
                return
            }
            preMenuType.copy_file.name -> {
                execCopyFile(
                    selectedItem
                )
                return
            }
            preMenuType.copy_app_dir.name -> {
                execCopyAppDir(
                    selectedItem
                )
                return
            }
            preMenuType.rename_app_dir.name -> {
                if(
                    selectedItem == throughMark
                ) return
                execRenameForAppDirAdmin(
                    selectedItem
                )
                listIndexListUpdateFileList(
                    editFragment,
                    makeFileList()
                )
                return
            }
            preMenuType.get.name -> {
                execGetFile()
                return
            }
            preMenuType.desc.name -> {
                execShowDescription(
                    selectedItem
                )
                return
            }
            preMenuType.editC.name -> {
                if(
                    selectedItem == throughMark
                ) return
                formDialogForListIndexOrButton.create(
                    "edit command variable",
                    filterDir,
                    selectedItem,
                    String()
                )
//                TODO delete scroll because of recycler view update speed depending on edge
//                CoroutineScope(Dispatchers.IO).launch {
//                    withContext(Dispatchers.IO) {
//                        for (i in 0..3000) {
//                            delay(200)
//                            if(
//                                !terminalViewModel.onDialog
//                            ) break
//                        }
//                    }
//                    withContext(Dispatchers.Main){
//                        listIndexListUpdateFileList(
//                            editFragment,
//                            makeFileList(),
//                            false
//                        )
//                        delay(100)
//                        val listIndexForEditAdapter = editListRecyclerView.adapter as ListIndexForEditAdapter
//                        editListRecyclerView.layoutManager?.scrollToPosition(
//                            listIndexForEditAdapter.itemCount - 1
//                        )
//                    }
//                }
                return
            }
            preMenuType.editS.name -> {
                if(
                    selectedItem == throughMark
                ) return
                formDialogForListIndexOrButton.create(
                    "edit setting variable",
                    filterDir,
                    selectedItem,
                    "setting"
                )
//                TODO delete scroll because of recycler view update speed depending on edge
//                CoroutineScope(Dispatchers.IO).launch {
//                    withContext(Dispatchers.IO) {
//                        for (i in 0..3000) {
//                            delay(200)
//                            if(
//                                !terminalViewModel.onDialog
//                            ) break
//                        }
//                    }
//                    withContext(Dispatchers.Main){
//                        listIndexListUpdateFileList(
//                            editFragment,
//                            makeFileList()
//                        )
//                    }
//                }
            }
        }
        val execJsFilePath = "${parentDirPath}/${clickJsName}"
        terminalViewModel.jsArguments = listOf(
            parentDirPath,
            filterDir,
            selectedItem,
            menuName
        ).joinToString("\t")
        ExecJsScriptInEdit.exec(
            editFragment,
            execJsFilePath,
        )
    }

    private fun execAddAppDir(){
        if(
            context == null
        ) return

        promptDialog = Dialog(
            context
        )
        promptDialog?.setContentView(
            com.puutaro.commandclick.R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Input create app directory name"
        val promptMessageTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            promptDialog?.findViewById<AppCompatEditText>(
                com.puutaro.commandclick.R.id.prompt_dialog_input
            )
        val promptCancelButton =
            promptDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            promptDialog?.dismiss()
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            promptDialog?.dismiss()
            val inputScriptFileName = promptEditText?.text.toString()
            val jsFileSuffix = UsePath.JS_FILE_SUFFIX
            val isJsSuffix = inputScriptFileName.endsWith(jsFileSuffix)
            val scriptFileName = if (
                isJsSuffix
            ) inputScriptFileName
            else inputScriptFileName + jsFileSuffix

            CommandClickScriptVariable.makeAppDirAdminFile(
                UsePath.cmdclickAppDirAdminPath,
                scriptFileName
            )
            listIndexListUpdateFileList(
                editFragment,
                makeFileList()
            )
            val createAppDirName = if (
                isJsSuffix
            ) {
                inputScriptFileName.removeSuffix(jsFileSuffix)
            } else {
                inputScriptFileName
            }
            val createAppDirPath = "${UsePath.cmdclickAppDirPath}/${createAppDirName}"
            FileSystems.createDirs(
                createAppDirPath
            )
            FileSystems.createDirs(
                "${createAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
            )
        }
        promptDialog?.setOnCancelListener {
            promptDialog?.dismiss()
        }
        promptDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        promptDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialog?.show()
    }

    private fun execCopyAppDir(
        selectedItem: String
    ){
        if(
            context == null
        ) return
        promptDialog = Dialog(
            context
        )
        promptDialog?.setContentView(
            com.puutaro.commandclick.R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Input, destination App dir name"
        val promptMessageTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.prompt_dialog_message
            )
        promptMessageTextView?.text = "current app dir name: ${selectedItem}"
        val promptEditText =
            promptDialog?.findViewById<AppCompatEditText>(
                com.puutaro.commandclick.R.id.prompt_dialog_input
            ) ?: return
        val promptCancelButton =
            promptDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            promptDialog?.dismiss()
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            promptDialog?.dismiss()
            CopyAppDirEventForEdit.execCopyAppDir(
                editFragment,
                UsePath.cmdclickAppDirAdminPath,
                selectedItem,
                promptEditText
            )
            listIndexListUpdateFileList(
                editFragment,
                makeFileList()
            )
        }
        promptDialog?.setOnCancelListener {
            promptDialog?.dismiss()
        }
        promptDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        promptDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialog?.show()
    }

    private fun execRenameForAppDirAdmin(
        selectedItem: String,
    ){
        if(
            context == null
        ) return
        val jsSuffix = UsePath.JS_FILE_SUFFIX
        promptDialog = Dialog(
            context
        )
        promptDialog?.setContentView(
            com.puutaro.commandclick.R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Rename app dir"
        val promptMessageTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            promptDialog?.findViewById<AppCompatEditText>(
                com.puutaro.commandclick.R.id.prompt_dialog_input
            )
        promptEditText?.setText(
            selectedItem.removeSuffix(jsSuffix)
        )
        val promptCancelButton =
            promptDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            promptDialog?.dismiss()
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            promptDialog?.dismiss()
            val inputEditable = promptEditText?.text
            if(
                inputEditable.isNullOrEmpty()
            ) {
                Toast.makeText(
                    context,
                    "No type item name",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val renamedAppDirNameSource = inputEditable.toString()
            val renamedAppDirName = if(
                renamedAppDirNameSource.endsWith(jsSuffix)
            ) renamedAppDirNameSource
            else "${renamedAppDirNameSource}${jsSuffix}"
            if(
                selectedItem == renamedAppDirName
            ) return@setOnClickListener
            CommandClickScriptVariable.makeAppDirAdminFile(
                UsePath.cmdclickAppDirAdminPath,
                renamedAppDirName
            )
            FileSystems.removeFiles(
                UsePath.cmdclickAppDirAdminPath,
                selectedItem
            )
            val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
            val beforeMoveDirPath = cmdclickAppDirPath + '/' +
                    selectedItem.removeSuffix(
                        UsePath.JS_FILE_SUFFIX
                    )
            val afterMoveDirPath = cmdclickAppDirPath + '/' +
                    renamedAppDirName.removeSuffix(
                        UsePath.JS_FILE_SUFFIX
                    )
            FileSystems.moveDirectory(
                beforeMoveDirPath,
                afterMoveDirPath,
            )
            listIndexListUpdateFileList(
                editFragment,
                makeFileList()
            )
        }
        promptDialog?.setOnCancelListener {
            promptDialog?.dismiss()
        }
        promptDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        promptDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialog?.show()
    }

    private fun execShowDescription(
        selectedItem: String,
    ){
        if(
            selectedItem == throughMark
        ) return
        ScriptFileDescription.show(
            editFragment,
            ReadText(
                currentAppDirPath,
                selectedItem
            ).textToList(),
            currentAppDirPath,
            selectedItem
        )
    }
    private fun execGetFile(){
        getFile.launch(
            arrayOf(Intent.CATEGORY_OPENABLE)
        )
    }

    private fun execCopyFile(
        selectedItem: String
    ){
        if(
            selectedItem == throughMark
        ) return
        selectedItemForCopy = selectedItem
        getDirectory.launch(
            arrayOf(Intent.CATEGORY_OPENABLE)
        )
    }

    private fun execCopyPath(
        selectedItem: String
    ){
        if(
            selectedItem == throughMark
        ) return
        val selectedItemPath = "${filterDir}/${selectedItem}"
        val clipboard = context?.getSystemService(
            Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(
            "cmdclick path",
            selectedItemPath
        )
        clipboard.setPrimaryClip(clip)
        Toast.makeText(
            context,
            "copy ok",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun execWriteItem(
        selectedItem: String
    ){
        if(
            selectedItem == throughMark
        ) return
        val editor = Editor(
            filterDir,
            selectedItem,
            context
        )
        editor.open()
    }

    private fun execAddItem(){
        if(
            context == null
        ) return
        promptDialog = Dialog(
            context
        )
        promptDialog?.setContentView(
            com.puutaro.commandclick.R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Type item name"
        val promptMessageTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            promptDialog?.findViewById<AppCompatEditText>(
                com.puutaro.commandclick.R.id.prompt_dialog_input
            )
        val promptCancelButton =
            promptDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            promptDialog?.dismiss()
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            promptDialog?.dismiss()
            val inputEditable = promptEditText?.text
            if(
                inputEditable.isNullOrEmpty()
            ) {
                Toast.makeText(
                    context,
                    "No type item name",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            FileSystems.writeFile(
                filterDir,
                inputEditable.toString(),
                String()
            )
            listIndexListUpdateFileList(
                editFragment,
                makeFileList()
            )
        }
        promptDialog?.setOnCancelListener {
            promptDialog?.dismiss()
        }
        promptDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        promptDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialog?.show()
    }

    private fun execItemDelete(
        selectedItem: String,
    ){
        if(
            context == null
        ) return
        if(
            selectedItem == throughMark
        ) return
        val scriptContents = ReadText(
            filterDir,
            selectedItem
        ).readText()
        val displayContents = "\tpath: ${filterDir}/${selectedItem}" +
                "\n---\n${scriptContents}"



        confirmDialog = Dialog(
            context
        )
        confirmDialog?.setContentView(
            com.puutaro.commandclick.R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            confirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_title
            )
        confirmTitleTextView?.text = "Delete bellow contents, ok?"
        val confirmContentTextView =
            confirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
            )
        confirmContentTextView?.text = displayContents
        val confirmCancelButton =
            confirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            confirmDialog?.dismiss()
        }
        val confirmOkButton =
            confirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            confirmDialog?.dismiss()
            FileSystems.removeFiles(
                filterDir,
                selectedItem
            )
            val deleteFannelName =
                selectedItem
                    .removeSuffix(UsePath.JS_FILE_SUFFIX)
                    .removeSuffix(UsePath.SHELL_FILE_SUFFIX)
            val deleteFannelDir = deleteFannelName + UsePath.fannelDirSuffix
            FileSystems.removeDir(
                "${filterDir}/${deleteFannelDir}"
            )
            listIndexListUpdateFileList(
                editFragment,
                makeFileList()
            )
            if (
                filterDir.removeSuffix("/")
                == UsePath.cmdclickAppDirAdminPath
            ) {
                val deleteAppDirName = selectedItem.removeSuffix(
                    UsePath.JS_FILE_SUFFIX
                )
                val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
                val displayDeleteAppDirPath =
                    "${
                        UsePath.makeTermuxPathByReplace(
                            cmdclickAppDirPath
                        )
                    }/${deleteAppDirName}"


                confirmDialog2 = Dialog(
                    context
                )
                confirmDialog2?.setContentView(
                    com.puutaro.commandclick.R.layout.confirm_text_dialog
                )
                val confirmTitleForDeleteAppDirTextView =
                    confirmDialog2?.findViewById<AppCompatTextView>(
                        com.puutaro.commandclick.R.id.confirm_text_dialog_title
                    )
                confirmTitleForDeleteAppDirTextView?.text =
                    "Delete bellow App dir, ok?"
                val confirmContentTextViewForDeleteAppDir =
                    confirmDialog2?.findViewById<AppCompatTextView>(
                        com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
                    )
                confirmContentTextViewForDeleteAppDir?.text =
                    "\tpath: ${displayDeleteAppDirPath}"
                val confirmCancelButtonForDeleteAppDir =
                    confirmDialog2?.findViewById<AppCompatImageButton>(
                        com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
                    )
                confirmCancelButtonForDeleteAppDir?.setOnClickListener {
                    confirmDialog2?.dismiss()
                }
                val confirmOkButtonForDeleteAppDir =
                    confirmDialog2?.findViewById<AppCompatImageButton>(
                        com.puutaro.commandclick.R.id.confirm_text_dialog_ok
                    )
                confirmOkButtonForDeleteAppDir?.setOnClickListener {
                    confirmDialog2?.dismiss()
                    val deleteAppDirPath =
                        "${cmdclickAppDirPath}/${deleteAppDirName}"
                    FileSystems.removeDir(
                        deleteAppDirPath
                    )
                    listIndexListUpdateFileList(
                        editFragment,
                        makeFileList()
                    )
                }
                confirmDialog2?.setOnCancelListener {
                    confirmDialog2?.dismiss()
                }
                confirmDialog2?.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                confirmDialog2?.window?.setGravity(
                    Gravity.BOTTOM
                )
                confirmDialog2?.show()
            }
        }
        confirmDialog?.setOnCancelListener {
            confirmDialog?.dismiss()
        }
        confirmDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        confirmDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        confirmDialog?.show()
    }

    private fun execItemCat(
        selectedItem: String,
    ){
        if(
            selectedItem == throughMark
        ) return
        val scriptContents = ReadText(
            filterDir,
            selectedItem
        ).readText()
        val displayContents = "\tpath: ${filterDir}/${selectedItem}" +
                "\n---\n${scriptContents}"
        DialogObject.simpleTextShow(
            context,
            "Show contents",
            displayContents
        )
    }

    private fun getIndexListMap(
        editParameters: EditParameters
    ): Map<String, String>? {
        return currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.split('|')
            ?.firstOrNull()
            ?.let {
                ScriptPreWordReplacer.replace(
                    it,
                    currentAppDirPath,
                    fannelDirName,
                    currentScriptName
                )
            }.let {
                ReplaceVariableMapReflecter.reflect(
                    QuoteTool.trimBothEdgeQuote(it),
                    editParameters
                )
            }?.split('!')?.map {
                CcScript.makeKeyValuePairFromSeparatedString(
                    it,
                    "="
                )
            }?.toMap()
    }

    private fun getMenuMap(
        editParameters: EditParameters
    ): Map<String, List<String>>? {
        val menuListSource = currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.split('|')
            ?: return null
        if(
            menuListSource.size != 2
        ) return null
        return menuListSource
            .lastOrNull()
            ?.let {
                ScriptPreWordReplacer.replace(
                    it,
                    currentAppDirPath,
                    fannelDirName,
                    currentScriptName
                )
            }.let {
                ReplaceVariableMapReflecter.reflect(
                    QuoteTool.trimBothEdgeQuote(it),
                    editParameters
                )
            }?.let {
                val keyValue = CcScript.makeKeyValuePairFromSeparatedString(
                    it,
                    "="
                )
                listOf(keyValue.first to
                        keyValue.second.split("!"))
            }?.toMap()
    }

    private fun clickUpdateFileList(
        selectedItem: String
    ){
        FileSystems.updateLastModified(
            filterDir,
            selectedItem
        )
        listIndexListUpdateFileList(
            editFragment,
           makeFileList()
        )
    }
}

private enum class IndexListEditMenu {
    menu,
}
private enum class IndexListEditKey {
    listDir,
    prefix,
    suffix
}

private fun getFilterListDir(
    fcbMap: Map<String, String>?,
    currentAppDirPath: String,
    fannelDirName: String,
    currentScriptName: String
): String {
    return fcbMap?.get(IndexListEditKey.listDir.name)?.let{
        ScriptPreWordReplacer.replace(
            it,
            currentAppDirPath,
            fannelDirName,
            currentScriptName
        )
    }?.let {
        QuoteTool.trimBothEdgeQuote(it)
    } ?: String()
}
private fun getFilterPrefix(
    fcbMap: Map<String, String>?,
): String {
    return fcbMap?.get(IndexListEditKey.prefix.name)?.let {
        QuoteTool.trimBothEdgeQuote(it)
    } ?: String()
}

private fun getFilterSuffix(
    fcbMap: Map<String, String>?,
): String {
    return fcbMap?.get(IndexListEditKey.suffix.name)?.let {
        QuoteTool.trimBothEdgeQuote(it)
    } ?: String()
}


private val mainMenuGroupId = 80000

private fun execAddSettingSubMenu(
    popup: PopupMenu,
    itemId: Int,
    order: Int,
    itemName: String,
    subMenuList: List<String>,
){
    val sub = popup.menu.addSubMenu(
        mainMenuGroupId,
        itemId,
        order,
        itemName
    )
    val subMenuGid = order
    (subMenuList.indices).forEach{
        sub.add(
            subMenuGid,
            itemId + it + 1,
            it,
            subMenuList[it]
        )
    }
}

enum class preMenuType {
    sync,
    delete,
    write,
    add,
    add_app_dir,
    rename_app_dir,
    cat,
    copy_path,
    copy_file,
    copy_app_dir,
    get,
    bookmark,
    editC,
    editS,
    desc
}
