package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.R
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditFragmentTitle
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditTextForListIndex
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.ExecJsScriptInEdit
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.Editor
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.util.UrlTitleTrimmer
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
    companion object {
        val pxHeightNoTerminal = 85
        val pxHeightOnTerminal = 49
        val pxHeightOnKeyboard = 48
    }
    private val context = editFragment.context
    private val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
    private val noExtend = "NoExtend"
    private val throughMark = "-"
    private var currentSetVariableMap: Map<String, String>? = mapOf()
    private var currentAppDirPath = String()
    private var currentScriptName = String()
    private val fileListView = ListView(context)
    private var fileDisplayListAdapter: ArrayAdapter<String>? = null
    private var filterDir = String()
    private var filterPrefix = String()
    private var filterSuffix = String()
    private var fannelDirName = String()
    private var fannelDirPath = String()
    private var clickDirPath = String()
    private val clickDirName = "click"
    private val itemClickJsName = "itemClick.js"
    private val menuClickJsName = "menuClick.js"
    private val subMenuClickJsName = "subMenuClick.js"
    private val editTextForListIndex = EditTextForListIndex(
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
                .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
                .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX)
        val targetFannelName =
            File(targetScriptFilePath).name
                .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
                .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX)
        val sourceFannelDir = sourceFannelName + UsePath.fannelDirSuffix
        val targetFannelDir = targetFannelName + UsePath.fannelDirSuffix
        FileSystems.copyDirectory(
            "${filterDir}/${sourceFannelDir}",
            "${targetDirectoryPath}/${targetFannelDir}"
        )
        updateFileList()
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
                .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
                .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX)
        val sourceFannelDir = sourceFannelName + UsePath.fannelDirSuffix
        FileSystems.copyDirectory(
            "${sourceDirPath}/${sourceFannelDir}",
            "${filterDir}/${sourceFannelDir}"
        )
        updateFileList()
        Toast.makeText(
            context,
            "get file ok",
            Toast.LENGTH_LONG
        ).show()
    }

    fun create(
        editParameters: EditParameters,
    ): LinearLayout {
        val context = editParameters.context
        currentSetVariableMap = editParameters.setVariableMap
        val readSharePreffernceMap = editParameters.readSharePreffernceMap
        currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        currentScriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        fannelDirName = currentScriptName
            .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
            .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX) +
                "Dir"
        fannelDirPath = "${currentAppDirPath}/${fannelDirName}"

        fileListView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        fileListView.isStackFromBottom = true

        val indexListMap = getIndexListMap(
            editParameters
        )
        filterDir = getFilterListDir(
            indexListMap,
            "${currentAppDirPath}/${currentScriptName}",
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
        val linearLayoutForTotal = LinearLayout(context)
        linearLayoutForTotal.tag = editFragment.indexListLinearLayoutTagName
        val linearLayoutParamForTotal = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            PxHeightCalculateForIndexList.culc(
                editFragment,
                editFragment.terminalOn
            )
        )

        linearLayoutForTotal.layoutParams = linearLayoutParamForTotal
        linearLayoutForTotal.orientation =  LinearLayout.VERTICAL

        val searchTextLinearWeight = SearchTextLinearWeight.calculate(
            editFragment
        )
        val listLinearWeight = 1F - searchTextLinearWeight
        val linearLayoutForListView = NestLinearLayout.make(
            context,
            listLinearWeight
        )
        linearLayoutForListView.addView(fileListView)

        val linearLayoutForSearch = NestLinearLayout.make(
            context,
            searchTextLinearWeight
        )

        val searchText = EditText(context)
        linearLayoutForSearch.addView(searchText)
        linearLayoutForTotal.addView(linearLayoutForListView)
        linearLayoutForTotal.addView(linearLayoutForSearch)

        fileDisplayListAdapter?.clear()
        fileDisplayListAdapter = ArrayAdapter(
            context as Context,
            R.layout.simple_list_item_1,
            fileList
        )
        fileListView.adapter = fileDisplayListAdapter
        fileListView.setSelection(
            fileDisplayListAdapter?.count ?: 0
        )

        invokeItemSetClickListenerForFileList(
            fileListView,
        )

        invokeItemSetLongTimeClickListenerForHistory(
            fileListView,
            menuList
        )

        makeSearchEditText(
            fileListView,
            fileDisplayListAdapter,
            searchText,
            readSharePreffernceMap
        )

        return linearLayoutForTotal
    }

    private fun invokeItemSetClickListenerForFileList(
        fileListView: ListView,
    ) {
        fileListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            val selectedItem =
                fileListView
                    .adapter
                    .getItem(pos)
                        as String
            execItemClickJs(
                clickDirPath,
                selectedItem,
            )
            return@setOnItemClickListener
        }
    }

    private fun makeSearchEditText(
        urlHistoryListView: ListView,
        urlHistoryListAdapter: ArrayAdapter<String>?,
        searchText: EditText,
        readSharePreffernceMap: Map<String, String>
    ) {
        val linearLayoutParamForSearchText = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutParamForSearchText.topMargin = 20
        linearLayoutParamForSearchText.bottomMargin = 20
        searchText.layoutParams = linearLayoutParamForSearchText
        searchText.inputType = InputType.TYPE_CLASS_TEXT
        searchText.background = null
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
        searchText.setPadding(30, 10, 20, 10)
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return
                val filteredUrlHistoryList = makeFileList().map {
                    val urlTitleSource =
                        it.split("\t")
                            .firstOrNull() ?:String()
                    UrlTitleTrimmer.trim(
                        urlTitleSource
                    )
                }.filter {
                    Regex(
                        searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }
                CommandListManager.execListUpdateByEditText(
                    filteredUrlHistoryList,
                    urlHistoryListAdapter as ArrayAdapter<String>,
                    urlHistoryListView
                )
                urlHistoryListView.setSelection(
                    urlHistoryListAdapter.count
                )
            }
        })
    }

    private fun invokeItemSetLongTimeClickListenerForHistory(
        fileListView: ListView,
        menuList: List<String>,
    ){
        fileListView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener {
                    parent, listSelectedView, pos, id ->
                val selectedItem =
                    fileListView.adapter.getItem(pos) as String
                val popup = PopupMenu(
                    context,
                    listSelectedView,
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
                true
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
                    SettingVariableSelects.Companion.OnUrlHistoryRegisterSelects.OFF.name
        if(
            !onOverrideItemClickExec
            && selectedItem.endsWith(
                        CommandClickScriptVariable.HTML_FILE_SUFFIX
                    ) || selectedItem.endsWith(
                        CommandClickScriptVariable.HTM_FILE_SUFFIX
                    )
        ){
            BroadCastIntent.send(
                editFragment,
                "${currentAppDirPath}/$selectedItem"
            )
            return
        }
        if(
            !onOverrideItemClickExec
            && (
                    selectedItem.endsWith(
                    CommandClickScriptVariable.JS_FILE_SUFFIX
                ) || selectedItem.endsWith(
                        CommandClickScriptVariable.JSX_FILE_SUFFIX
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
                CommandClickScriptVariable.SHELL_FILE_SUFFIX
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
                updateFileList()
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
                editTextForListIndex.create(
                    "edit command variable",
                    filterDir,
                    selectedItem,
                    String()
                )
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        for (i in 0..3000) {
                            delay(200)
                            if(
                                !terminalViewModel.onDialog
                            ) break
                        }
                    }
                    withContext(Dispatchers.Main){
                        updateFileList()
                    }
                }
            }
            preMenuType.editS.name -> {
                if(
                    selectedItem == throughMark
                ) return
                editTextForListIndex.create(
                    "edit setting variable",
                    filterDir,
                    selectedItem,
                    "setting"
                )
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        for (i in 0..3000) {
                            delay(200)
                            if(
                                !terminalViewModel.onDialog
                            ) break
                        }
                    }
                    withContext(Dispatchers.Main){
                        updateFileList()
                    }
                }
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

    private fun execShowDescription(
        selectedItem: String,
    ){
        if(
            selectedItem == throughMark
        ) return
        ScriptFileDescription.show(
            editFragment.context,
            ReadText(
                currentAppDirPath,
                selectedItem
            ).textToList(),
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
        val editText = EditText(context)
        editText.inputType = InputType.TYPE_CLASS_TEXT
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(
                "Type item name"
            )
            .setView(editText)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                if(
                    editText.text.isNullOrEmpty()
                ) {
                    Toast.makeText(
                        context,
                        "No type item name",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                }
                FileSystems.writeFile(
                    filterDir,
                    editText.text.toString(),
                    String()
                )
                updateFileList()
            })
            .setNegativeButton("NO", null)
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            context?.getColor(R.color.black) as Int
        )
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            context.getColor(R.color.black)
        )
        alertDialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun execItemDelete(
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
        val linearLayoutForDialog = LinearLayoutAdderForDialog.add(
            context,
            displayContents
        )
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(
                "Delete bellow contents, ok?"
            )
            .setView(linearLayoutForDialog)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                FileSystems.removeFiles(
                    filterDir,
                    selectedItem
                )
                val deleteFannelName =
                    selectedItem
                        .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
                        .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX)
                val deleteFannelDir = deleteFannelName + UsePath.fannelDirSuffix
                FileSystems.removeDir(
                    "${filterDir}/${deleteFannelDir}"
                )
                updateFileList()
            })
            .setNegativeButton("NO", null)
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            context?.getColor(R.color.black) as Int
        )
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            context.getColor(R.color.black)
        )
        alertDialog.window?.setGravity(Gravity.BOTTOM)
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
        val linearLayoutForDialog = LinearLayoutAdderForDialog.add(
            context,
            displayContents
        )
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(
                "Show contents"
            )
            .setView(linearLayoutForDialog)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            })
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            context?.getColor(R.color.black) as Int
        )
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            context.getColor(R.color.black)
        )
        alertDialog.window?.setGravity(Gravity.BOTTOM)
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

    fun getIndexListMap(
        editParameters: EditParameters
    ): Map<String, String>? {
        return currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )?.split('|')
            ?.firstOrNull()
            ?.let {
                ScriptPreWordReplacer.replace(
                    it,
                    "${currentAppDirPath}/${currentScriptName}",
                    currentAppDirPath,
                    fannelDirName,
                    currentScriptName
                )
            }.let {
                ReplaceVariableMapReflecter.reflect(
                    BothEdgeQuote.trim(it),
                    editParameters
                )
            }?.split('!')?.map {
                CcScript.makeKeyValuePairFromSeparatedString(
                    it,
                    "="
                )
            }?.toMap()
    }

    fun getMenuMap(
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
                    "${currentAppDirPath}/${currentScriptName}",
                    currentAppDirPath,
                    fannelDirName,
                    currentScriptName
                )
            }.let {
                ReplaceVariableMapReflecter.reflect(
                    BothEdgeQuote.trim(it),
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
        updateFileList()
    }
    private fun updateFileList(){
        fileDisplayListAdapter?.clear()
        val updateList = makeFileList()
        fileDisplayListAdapter?.addAll(updateList)
        fileListView.adapter = fileDisplayListAdapter
        fileDisplayListAdapter?.notifyDataSetChanged();
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
    currentScriptPath: String,
    currentAppDirPath: String,
    fannelDirName: String,
    currentScriptName: String
): String {
    return fcbMap?.get(IndexListEditKey.listDir.name)?.let{
        ScriptPreWordReplacer.replace(
            it,
            currentScriptPath,
            currentAppDirPath,
            fannelDirName,
            currentScriptName
        )
    }?.let {
        BothEdgeQuote.trim(it)
    } ?: String()
}
private fun getFilterPrefix(
    fcbMap: Map<String, String>?,
): String {
    return fcbMap?.get(IndexListEditKey.prefix.name)?.let {
        BothEdgeQuote.trim(it)
    } ?: String()
}

private fun getFilterSuffix(
    fcbMap: Map<String, String>?,
): String {
    return fcbMap?.get(IndexListEditKey.suffix.name)?.let {
        BothEdgeQuote.trim(it)
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

private object PxHeightCalculateForIndexList {
    fun culc(
        editFragment: EditFragment,
        terminalOn: String
    ): Int
    {
        val defaultPxHeight = 300
        val pxHeight = if (
            Build.VERSION.SDK_INT > 30
        ) {
            val windowMetrics =
                editFragment.activity?.windowManager?.currentWindowMetrics
                    ?: return defaultPxHeight
            windowMetrics.bounds.height()
        } else {
            val display = editFragment.activity?.windowManager?.getDefaultDisplay()
            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)
            outMetrics.heightPixels
        }
        val heightRate = if (
            terminalOn
            != SettingVariableSelects.Companion.TerminalDoSelects.OFF.name
        ) WithIndexListView.pxHeightOnTerminal
        else WithIndexListView.pxHeightNoTerminal
        return (pxHeight * heightRate) / 100
    }
}
enum class preMenuType {
    sync,
    delete,
    write,
    add,
    cat,
    copy_path,
    copy_file,
    get,
    bookmark,
    editC,
    editS,
    desc
}
