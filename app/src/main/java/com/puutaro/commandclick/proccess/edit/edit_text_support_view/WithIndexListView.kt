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
import android.widget.AutoCompleteTextView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListIndexEditConfig
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.InstallFannelList
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.CopyAppDirEventForEdit
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FormDialogForListIndexOrButton
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.ExecJsScriptInEdit
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.list_index.FannelLogoLongClickDoForListIndex
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.proccess.qr.QrLogo
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.QrDialogClickHandler
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.dialog.DialogObject
import com.puutaro.commandclick.util.editor.EditorByIntent
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.Map.ConfigMapTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.util.editor.EditorByEditText
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
    private var mainMenuListIndexDialog: Dialog? = null
    private var listIndexSubMenuDialog: Dialog? = null
    private var clickDirPath = String()
    private val clickDirName = "click"
    private val itemClickJsName = "itemClick.js"
    private val menuClickJsName = "menuClick.js"
    private val subMenuClickJsName = "subMenuClick.js"
    private val formDialogForListIndexOrButton = FormDialogForListIndexOrButton(
        editFragment
    )
    private val listIndexConfigMap = ConfigMapTool.create(
        UsePath.listIndexForEditConfigPath,
        String(),
        readSharePreffernceMap,
        mapOf(),
    )
    private val isInstallFannel = ListIndexEditConfig.howInstallFannel(
        listIndexConfigMap
    )
    private var selectedItemForCopy = String()
    private val prefixRegex = Regex("^content.*fileprovider/root/storage")
    private val getDirectoryAndCopy = editFragment.registerForActivityResult(
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
        FileSystems.execCopyFileWithDir(
            File(sourceScriptFilePath),
            File(targetScriptFilePath),
        )
        listIndexListUpdateFileList(
            editFragment,
            makeFileListHandler(isInstallFannel)
        )
        Toast.makeText(
            context,
            "copy file ok",
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        private const val throughMark = "-"
        private const val noExtend = "NoExtend"
        private const val subMenuSeparator = "&"

        var filterDir = String()
        private var filterPrefix = String()
        private var filterSuffix = String()
        private var fannelDirName = String()
        private var fannelDirPath = String()
        private var fannelMenuDirPath = String()

        fun makeFileListHandler(isInstallFannel: Boolean): MutableList<String> {
            return when(isInstallFannel) {
                true -> makeFannelListForListView().toMutableList()
                else -> makeFileList()
            }
        }

        private fun makeFileList(): MutableList<String> {

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

        private fun makeFannelListForListView(): List<String> {
            val fannelListSource = ReadText(
                UsePath.cmdclickFannelListDirPath,
                UsePath.fannelListMemoryName,
            ).readText()
                .replace(Regex("\\*\\*([a-zA-Z0-9]*)\\*\\*"), "*$1")
                .split(FannelListVariable.cmdclickFannelListSeparator)
            return if (
                fannelListSource.isNotEmpty()
                && !fannelListSource
                    .firstOrNull()
                    ?.trim()
                    .isNullOrEmpty()
            ) {
                fannelListSource
            } else mutableListOf(InstallFannelList.blankListMark)
        }

        fun listIndexListUpdateFileList(
            editFragment: EditFragment,
            updateList: List<String>,
        ){
            val editListRecyclerView = editFragment.binding.editListRecyclerView
            if(
                !editListRecyclerView.isVisible
            ) return
            val listIndexForEditAdapter =
                editListRecyclerView.adapter as? ListIndexForEditAdapter
                    ?: return
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
                return filterSuffix.split(subMenuSeparator).any {
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
        editFragment.isInstallFannelForListIndex = isInstallFannel
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
        fannelDirName = CcPathTool.makeFannelDirName(
            currentScriptName
        )
        fannelDirPath = "${currentAppDirPath}/${fannelDirName}"
        fannelMenuDirPath = "${fannelDirPath}/menu"

        val replacedSetVariableMap = makeReplacedSetVariableMap(
            editParameters
        )
        val indexListMap = getIndexListMap(
            replacedSetVariableMap
        )

        filterDir = getFilterListDir(
            indexListMap,
            currentAppDirPath,
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

        val menuMapList = createMenuMapList(
            replacedSetVariableMap,
        )

        val fileList = makeFileListHandler(isInstallFannel)

        val editListRecyclerView =
            binding.editListRecyclerView
        val listIndexForEditAdapter = ListIndexForEditAdapter(
            editFragment,
            filterDir,
            readSharePreffernceMap,
            editParameters.setReplaceVariableMap,
            fileList,
        )
        editListRecyclerView.adapter = listIndexForEditAdapter
        val preLoadLayoutManager = PreLoadLayoutManager(
            context,
        )
        preLoadLayoutManager.stackFromEnd = true
        editListRecyclerView.layoutManager = preLoadLayoutManager
        invokeItemSetClickListenerForFileList()
        invokeQrLogoSetClickListenerForFileList()
        invokeQrLogoSetLongClickListenerForFileList()
        invokeItemSetLongTimeClickListenerForIndexList(
            menuMapList
        )
        makeSearchEditText(
            editListSearchEditText,
            readSharePreffernceMap
        )
    }

        private fun invokeItemSetClickListenerForFileList() {
        val listIndexForEditAdapter =
            editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexForEditAdapter.fileNameClickListener =
            object: ListIndexForEditAdapter.OnFileNameItemClickListener {
                override fun onFileNameClick(
                    itemView: View,
                    holder: ListIndexForEditAdapter.ListIndexListViewHolder
                ) {
                    val selectedItem = holder.fileName
                    execItemClickJs(
                        clickDirPath,
                        selectedItem,
                    )
                    editListSearchEditText.setText(String())
                }
        }
    }

    private fun invokeQrLogoSetClickListenerForFileList() {

        val listIndexForEditAdapter =
            editListRecyclerView.adapter as ListIndexForEditAdapter
        listIndexForEditAdapter.fileQrLogoClickListener = object: ListIndexForEditAdapter.OnFileQrLogoItemClickListener {
            override fun onFileQrLogoClick(
                itemView: View,
                holder: ListIndexForEditAdapter.ListIndexListViewHolder
            ) {
                QrDialogClickHandler.handle(
                    false,
                    editFragment,
                    currentAppDirPath,
                    filterDir,
                    holder.fileName,
                    listIndexForEditAdapter.qrDialogConfigMap
                )
            }
        }
    }

    private fun invokeQrLogoSetLongClickListenerForFileList(
    ) {
        FannelLogoLongClickDoForListIndex.invoke(
            editFragment,
            currentAppDirPath,
            filterDir,
        )
    }

    private fun makeSearchEditText(
        searchText: AppCompatEditText,
        readSharePreffernceMap: Map<String, String>
    ) {
        searchText.hint = TitleImageAndViewSetter.makeTitle(
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
                val filteredUrlHistoryList = makeFileListHandler(isInstallFannel).filter {
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

    private fun invokeItemSetLongTimeClickListenerForIndexList(
        menuMapList: List<Map<String, String?>>?
    ){
        if(
            context == null
        ) return
        val indexForEditAdapter = editListRecyclerView.adapter as ListIndexForEditAdapter
        indexForEditAdapter.itemLongClickListener = object : ListIndexForEditAdapter.OnItemLongClickListener {
            override fun onItemLongClick(
                itemView: View,
                holder: ListIndexForEditAdapter.ListIndexListViewHolder,
                position: Int
            ) {
                if(
                    menuMapList.isNullOrEmpty()
                ) return
                val selectedItem = holder.fileName
//                    indexForEditAdapter.listIndexList[position]
                mainMenuListIndexDialog = Dialog(
                    context
                )
                mainMenuListIndexDialog?.setContentView(
                    R.layout.list_dialog_layout
                )
                QrLogo(editFragment).setTitleQrLogo(
                    mainMenuListIndexDialog?.findViewById<AppCompatImageView>(
                        R.id.list_dialog_title_image
                    ),
                    filterDir,
                    selectedItem
                )

                val listDialogTitle =
                    mainMenuListIndexDialog?.findViewById<AppCompatTextView>(
                        R.id.list_dialog_title
                    )
                listDialogTitle?.text = selectedItem
                val listDialogMessage =
                    mainMenuListIndexDialog?.findViewById<AppCompatTextView>(
                        R.id.list_dialog_message
                    )
                listDialogMessage?.isVisible = false
                val listDialogSearchEditText =
                    mainMenuListIndexDialog?.findViewById<AppCompatEditText>(
                        R.id.list_dialog_search_edit_text
                    )
                listDialogSearchEditText?.isVisible = false
                val cancelButton =
                    mainMenuListIndexDialog?.findViewById<AppCompatImageButton>(
                        R.id.list_dialog_cancel
                    )
                cancelButton?.setOnClickListener {
                    mainMenuListIndexDialog?.dismiss()
                }

                setContextMenuListView(
                    editFragment,
                    selectedItem,
                    menuMapList
                )
                mainMenuListIndexDialog?.setOnCancelListener {
                    mainMenuListIndexDialog?.dismiss()
                }
                mainMenuListIndexDialog?.window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                mainMenuListIndexDialog?.window?.setGravity(Gravity.BOTTOM)
                mainMenuListIndexDialog?.show()
            }
        }
    }

    private fun setContextMenuListView(
        editFragment: EditFragment,
        selectedItem: String,
        menuMapList:  List<Map<String, String?>>?,
    ){
        val context = editFragment.context
            ?: return
        if(
            menuMapList.isNullOrEmpty()
        ) return
        val menuPairList = menuMapList.map{
            val menuName = it.get(MenuMapKey.MAIN_MENU_NAME.str)
                ?: String()
            val iconId = PreMenuType.values().filter {
                it.menuName == menuName
            }.firstOrNull()?.iconId ?: R.drawable.icons8_wheel
            menuName to iconId
        }.filter {
            it.first.isNotEmpty()
        }
        val contextMenuListView =
            mainMenuListIndexDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            ) ?: return
        val mainMenuAdapter = SubMenuAdapter(
            context,
            menuPairList.toMutableList()
        )
        contextMenuListView.adapter = mainMenuAdapter
        invokeItemSetClickListnerForListIndexContextMenuList(
            menuMapList,
            contextMenuListView,
            selectedItem
        )
    }

    private fun invokeItemSetClickListnerForListIndexContextMenuList(
        menuMapList:  List<Map<String, String?>>?,
        contextMenuListView: ListView,
        selectedItemName: String,
    ) {
        contextMenuListView.setOnItemClickListener {
                parent, View, pos, id ->
            mainMenuListIndexDialog?.dismiss()
            val menuListAdapter = contextMenuListView.adapter as SubMenuAdapter
            val selectedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            if(
                menuMapList.isNullOrEmpty()
            ) return@setOnItemClickListener
            val isSubMenuList = menuMapList.filter {
                val isMainMenuName =
                    it.get(MenuMapKey.MAIN_MENU_NAME.str) == selectedMenuName
                if (
                    !isMainMenuName
                ) return@filter false
                val isSubMenuList =
                    !it.get(MenuMapKey.SUB_MENU_NAME_LIST_STR.str).isNullOrEmpty()
                isSubMenuList
            }.isNotEmpty()
            when(isSubMenuList) {
                false -> execMenuOrSubMenuClickJs(
                    clickDirPath,
                    menuClickJsName,
                    selectedItemName,
                    selectedMenuName,
                )
                else -> {
                    launchSubMenuDialogForListIndex(
                        menuMapList,
                        selectedMenuName,
                        selectedItemName,
                    )
                }
            }
            return@setOnItemClickListener
        }
    }

    private fun execItemClickJs(
        parentDirPath: String,
        selectedItem: String,
    ){
        if(
            selectedItem == throughMark
            || selectedItem.trim() == InstallFannelList.blankListMark
        ) {
            noFileToast()
            return
        }
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
            PreMenuType.DELETE.menuName -> {
                execItemDelete(selectedItem)
                return
            }
            PreMenuType.CAT.menuName -> {
                execItemCat(
                    selectedItem
                )
                return
            }
            PreMenuType.WRITE.menuName -> {
                execWriteItem(
                    selectedItem
                )
                return
            }
            PreMenuType.COPY_PATH.menuName -> {
                execCopyPath(
                    selectedItem
                )
                return
            }
            PreMenuType.COPY_FILE.menuName -> {
                execCopyFile(
                    selectedItem
                )
                return
            }
            PreMenuType.COPY_FILE_HERE.menuName -> {
                execCopyFileHere(
                    "${filterDir}/${selectedItem}",
                    "${filterDir}/${selectedItem}",
                )
                return
            }
            PreMenuType.COPY_APP_DIR.menuName -> {
                execCopyAppDir(
                    selectedItem
                )
                return
            }
            PreMenuType.RENAME_APP_DIR.menuName -> {
                if(
                    selectedItem == throughMark
                ) {
                    noFileToast()
                    return
                }
                execRenameForAppDirAdmin(
                    selectedItem
                )
                listIndexListUpdateFileList(
                    editFragment,
                    makeFileListHandler(isInstallFannel)
                )
                return
            }
            PreMenuType.DESC.menuName -> {
                execShowDescription(
                    selectedItem
                )
                return
            }
            PreMenuType.EDIT_C.menuName -> {
                if(
                    selectedItem == throughMark
                ) {
                    noFileToast()
                    return
                }
                formDialogForListIndexOrButton.create(
                    "edit command variable",
                    filterDir,
                    selectedItem,
                    String()
                )
                return
            }
            PreMenuType.EDIT_S.menuName -> {
                if(
                    selectedItem == throughMark
                ) {
                    noFileToast()
                    return
                }
                formDialogForListIndexOrButton.create(
                    "edit setting variable",
                    filterDir,
                    selectedItem,
                    "setting"
                )
            }
            PreMenuType.SIMPLE_EDIT.menuName -> {
                EditorByEditText.byEditText(
                    editFragment,
                    filterDir,
                    selectedItem,
                    ReadText(
                        filterDir,
                        selectedItem
                    ).readText()
                )
            }
            PreMenuType.SCAN_QR.menuName -> QrScanner(
                editFragment,
                filterDir
            ).scanFromCamera()
        }
        val menuScriptPathObj = File("${fannelMenuDirPath}/${menuName}${UsePath.JS_FILE_SUFFIX}")
        val execJsFilePath = when(menuScriptPathObj.isFile) {
            true -> menuScriptPathObj.absolutePath
            else -> "${parentDirPath}/${clickJsName}"
        }
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

    private fun execCopyFileHere(
        srcFilePath: String,
        destiFilePath: String,
    ){
        FileSystems.execCopyFileWithDir(
            File(srcFilePath),
            File(destiFilePath),
        )
        Toast.makeText(
            context,
            "Copy ok",
            Toast.LENGTH_SHORT
        ).show()
        listIndexListUpdateFileList(
            editFragment,
            makeFileListHandler(isInstallFannel)
        )
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
            R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Input, destination App dir name"
        val promptMessageTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        promptMessageTextView?.text = "current app dir name: ${selectedItem}"
        val promptEditText =
            promptDialog?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            ) ?: return
        val promptCancelButton =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            promptDialog?.dismiss()
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
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
                makeFileListHandler(isInstallFannel)
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
            R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Rename app dir"
        val promptMessageTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            promptDialog?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            )
        promptEditText?.setText(
            selectedItem.removeSuffix(jsSuffix)
        )
        val promptCancelButton =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            promptDialog?.dismiss()
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
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
            val selectedItemFannelDirName = CcPathTool.makeFannelDirName(selectedItem)
            val selectedItemFannelDirPath = "${UsePath.cmdclickAppDirAdminPath}/$selectedItemFannelDirName"
            val renamedFannelDirName = CcPathTool.makeFannelDirName(renamedAppDirName)
            val renamedFannelDirPath = "${UsePath.cmdclickAppDirAdminPath}/$renamedFannelDirName"
            FileSystems.moveDirectory(
                selectedItemFannelDirPath,
                renamedFannelDirPath
            )
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
                makeFileListHandler(isInstallFannel)
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
        ) {
            noFileToast()
            return
        }
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

    private fun execCopyFile(
        selectedItem: String
    ){
        if(
            selectedItem == throughMark
        ) {
            noFileToast()
            return
        }
        selectedItemForCopy = selectedItem
        getDirectoryAndCopy.launch(
            arrayOf(Intent.CATEGORY_OPENABLE)
        )
    }

    private fun execCopyPath(
        selectedItem: String
    ){
        if(
            selectedItem == throughMark
        ) {
            noFileToast()
            return
        }
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
        ) {
            noFileToast()
            return
        }
        val editorByIntent = EditorByIntent(
            filterDir,
            selectedItem,
            context
        )
        editorByIntent.byIntent()
    }

    private fun execItemDelete(
        selectedItem: String,
    ){
        if(
            context == null
        ) return
        if(
            selectedItem == throughMark
        ) {
            noFileToast()
            return
        }
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
            R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            confirmDialog?.findViewById<AppCompatTextView>(
                R.id.confirm_text_dialog_title
            )
        confirmTitleTextView?.text = "Delete bellow contents, ok?"
        val confirmContentTextView =
            confirmDialog?.findViewById<AppCompatTextView>(
                R.id.confirm_text_dialog_text_view
            )
        confirmContentTextView?.text = displayContents
        val confirmCancelButton =
            confirmDialog?.findViewById<AppCompatImageButton>(
                R.id.confirm_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            confirmDialog?.dismiss()
        }
        val confirmOkButton =
            confirmDialog?.findViewById<AppCompatImageButton>(
                R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            confirmDialog?.dismiss()
            FileSystems.removeFiles(
                filterDir,
                selectedItem
            )
            val deleteFannelDir =
                CcPathTool.makeFannelDirName(
                    selectedItem
                )
            FileSystems.removeDir(
                "${filterDir}/${deleteFannelDir}"
            )
            listIndexListUpdateFileList(
                editFragment,
                makeFileListHandler(isInstallFannel)
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
                    R.layout.confirm_text_dialog
                )
                val confirmTitleForDeleteAppDirTextView =
                    confirmDialog2?.findViewById<AppCompatTextView>(
                        R.id.confirm_text_dialog_title
                    )
                confirmTitleForDeleteAppDirTextView?.text =
                    "Delete bellow App dir, ok?"
                val confirmContentTextViewForDeleteAppDir =
                    confirmDialog2?.findViewById<AppCompatTextView>(
                        R.id.confirm_text_dialog_text_view
                    )
                confirmContentTextViewForDeleteAppDir?.text =
                    "\tpath: ${displayDeleteAppDirPath}"
                val confirmCancelButtonForDeleteAppDir =
                    confirmDialog2?.findViewById<AppCompatImageButton>(
                        R.id.confirm_text_dialog_cancel
                    )
                confirmCancelButtonForDeleteAppDir?.setOnClickListener {
                    confirmDialog2?.dismiss()
                }
                val confirmOkButtonForDeleteAppDir =
                    confirmDialog2?.findViewById<AppCompatImageButton>(
                        R.id.confirm_text_dialog_ok
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
                        makeFileListHandler(isInstallFannel)
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
        ) {
            noFileToast()
            return
        }
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
        replacedSetVariableMap: Map<String, String>?
    ): Map<String, String>? {
        val listDirKeyName = IndexListEditKey.listDir.name
        return replacedSetVariableMap?.get(
            listDirKeyName
        )?.split("!")?.map {
            val isListDirKeyEl = !it.contains("=") && it.isNotEmpty()
            val line = when(isListDirKeyEl){
                false -> it
                else ->  "${listDirKeyName}=${it}"
            }
            CcScript.makeKeyValuePairFromSeparatedString(
                line,
                    "="
                )
        }?.toMap() ?: return null
    }

    private fun makeReplacedSetVariableMap(
        editParameters: EditParameters
    ): Map<String, String>? {
        return currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                currentScriptName
            )
        }.let {
            ReplaceVariableMapReflecter.reflect(
                QuoteTool.trimBothEdgeQuote(it),
                editParameters
            )
        }?.split('|')?.map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }?.toMap()
    }

    private fun getMenuMap(
        replacedSetVariableMap: Map<String, String>?,
    ): Map<String, List<String>>? {
        val menuKeyName = IndexListEditMenu.menu.name
        val menuValueList = replacedSetVariableMap
            ?.get(menuKeyName)
            ?.split("!")
            ?: return null
        return mapOf(menuKeyName to menuValueList)
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
           makeFileListHandler(isInstallFannel)
        )
    }

    private fun createMenuMapList(
        replacedSetVariableMap: Map<String, String>?
    ): List<Map<String, String?>>? {
        return getMenuMap(
            replacedSetVariableMap
        )?.get(IndexListEditMenu.menu.name)?.map {
                menuLine ->
            val menuSubMenuList = menuLine.split(subMenuSeparator)
            val menuName = menuSubMenuList[0]
            val subMenuListStr = if(
                menuSubMenuList.size < 2
            ) null
            else menuSubMenuList.slice(
                1 until menuSubMenuList.size
            ).joinToString(subMenuSeparator)
            mapOf(
                MenuMapKey.MAIN_MENU_NAME.str to menuName,
                MenuMapKey.SUB_MENU_NAME_LIST_STR.str to subMenuListStr
            )
        }?.filter { it.isNotEmpty() }
    }



    private fun launchSubMenuDialogForListIndex(
        menuMapList:  List<Map<String, String?>>?,
        selectedMainMenuName: String,
        selectedItemName: String,
    ){
        val context = editFragment.context
            ?: return
        listIndexSubMenuDialog = Dialog(
            context
        )
        listIndexSubMenuDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        QrLogo(editFragment).setTitleQrLogo(
            listIndexSubMenuDialog?.findViewById<AppCompatImageView>(
                R.id.list_dialog_title_image
            ),
            filterDir,
            selectedItemName
        )
        val listDialogTitle = listIndexSubMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )
        listDialogTitle?.text = "$selectedMainMenuName: $selectedItemName"
        val listDialogMessage = listIndexSubMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = listIndexSubMenuDialog?.findViewById<AppCompatEditText>(
            R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = listIndexSubMenuDialog?.findViewById<AppCompatImageButton>(
            R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            listIndexSubMenuDialog?.dismiss()
        }

        setListIndexSubMenuListView(
            menuMapList,
            selectedMainMenuName,
            selectedItemName,
        )
        listIndexSubMenuDialog?.setOnCancelListener {
            listIndexSubMenuDialog?.dismiss()
        }
        listIndexSubMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        listIndexSubMenuDialog?.window?.setGravity(Gravity.BOTTOM)
        listIndexSubMenuDialog?.show()
    }

    private fun setListIndexSubMenuListView(
        menuMapList:  List<Map<String, String?>>?,
        selectedMainMenuName: String,
        selectedScriptName: String,
    ) {
        val context = editFragment.context
            ?: return
        if(menuMapList.isNullOrEmpty()) return
        val listIndexSubMenuList = menuMapList.map {
            val mainMenuName = it.get(MenuMapKey.MAIN_MENU_NAME.str)
            val isMainMenu =
                mainMenuName == selectedMainMenuName
            if(!isMainMenu) return@map emptyList()
            val subMenuListStr = it.get(MenuMapKey.SUB_MENU_NAME_LIST_STR.str)
                ?: String()
            subMenuListStr.split(subMenuSeparator)
        }.filter {
            it.isNotEmpty()
        }.firstOrNull()?.map {
            subMenuName ->
            val icon = PreMenuType.values().filter {
                it.menuName == subMenuName
            }.firstOrNull()?.iconId ?: R.drawable.icons8_wheel
            subMenuName to icon
        } ?: return
        val copyMenuListView =
            listIndexSubMenuDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            listIndexSubMenuList.toMutableList()
        )
        copyMenuListView.adapter = subMenuAdapter
        invokeItemSetClickListnerForListIndexSubMenu(
            copyMenuListView,
            selectedScriptName
        )
    }

    private fun invokeItemSetClickListnerForListIndexSubMenu(
        listIndexSubMenuMenuListView: ListView,
        selectedItem: String,
    ){
        listIndexSubMenuMenuListView.setOnItemClickListener {
                parent, View, pos, id ->
            listIndexSubMenuDialog?.dismiss()
            val menuListAdapter = listIndexSubMenuMenuListView.adapter as SubMenuAdapter
            val selectedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            execMenuOrSubMenuClickJs(
                clickDirPath,
                subMenuClickJsName,
                selectedItem,
                selectedMenuName
            )
            return@setOnItemClickListener
        }
    }

    private fun noFileToast(
        message: String = "No file"
    ){
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}

private enum class MenuMapKey(
    val str: String
) {
    MAIN_MENU_NAME("meinMenuName"),
    SUB_MENU_NAME_LIST_STR("subMenuNameList"),
}

private enum class IndexListEditMenu {
    menu,
}

private enum class IndexListEditKey {
    listDir,
    prefix,
    suffix,
}

private fun getFilterListDir(
    indexListMap: Map<String, String>?,
    currentAppDirPath: String,
    currentScriptName: String
): String {
    return indexListMap?.get(IndexListEditKey.listDir.name)?.let{
        ScriptPreWordReplacer.replace(
            it,
            currentAppDirPath,
            currentScriptName
        )
    }?.let {
        QuoteTool.trimBothEdgeQuote(it)
    } ?: String()
}
private fun getFilterPrefix(
    indexListMap: Map<String, String>?,
): String {
    return indexListMap?.get(IndexListEditKey.prefix.name)?.let {
        QuoteTool.trimBothEdgeQuote(it)
    } ?: String()
}

private fun getFilterSuffix(
    indexListMap: Map<String, String>?,
): String {
    return indexListMap?.get(IndexListEditKey.suffix.name)?.let {
        QuoteTool.trimBothEdgeQuote(it)
    } ?: String()
}


enum class PreMenuType(
    val menuName: String,
    val iconId: Int,
) {
    DELETE("delete", R.drawable.icons8_refresh),
    WRITE("write", R.drawable.icons8_edit),
    SIMPLE_EDIT("sEdit", R.drawable.icons8_edit_frame),
    RENAME_APP_DIR("rename_app_dir", R.drawable.icons8_edit_frame),
    CAT("cat", R.drawable.icons8_file),
    COPY_PATH("copy_path", com.termux.shared.R.drawable.ic_copy),
    COPY_FILE("copy_file", androidx.appcompat.R.drawable.abc_ic_menu_copy_mtrl_am_alpha),
    COPY_FILE_HERE("copy_file_here", androidx.appcompat.R.drawable.abc_ic_menu_copy_mtrl_am_alpha),
    COPY_APP_DIR("copy_app_dir", com.google.android.material.R.drawable.abc_ic_menu_copy_mtrl_am_alpha),
    EDIT_C("editC", R.drawable.icons8_edit),
    EDIT_S("editS", R.drawable.icons8_edit),
    DESC("desc", R.drawable.icons8_info),
    SCAN_QR("scanQR", R.drawable.icons_qr_code),
}
