package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.PromptListAdapter
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class PromptWithListDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var returnValue = String()
    private var promptDialogObj: Dialog? = null
    private val listPrefix = "list"
    private val listDirName = "${listPrefix}Text"
    private val statisticsName = "statistics"
    private val listTxtSuffix = ".txt"
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
    private val mapSeparator = ','
    private val firstSeparator = '|'
    private val secondSeparator = '?'
    private var onDialog = false
    private val switchOn = "ON"
    private val switchOff = "OFF"

    fun create(
        fannelPath: String,
        listOrDefoTxtVars: String,
    ): String {
        val fannelFile = File(fannelPath)
        if(
            !fannelFile.isFile
        ) return String()
        val promptListTotalMap = CmdClickMap.createMap(
            listOrDefoTxtVars,
            mapSeparator,
        ).toMap()
        val editTextMap = CmdClickMap.createMap(
            promptListTotalMap.get(PromptWithTextMapKey.editText.name),
            firstSeparator
        ).toMap()
        val promptListMap = CmdClickMap.createMap(
            promptListTotalMap.get(PromptWithTextMapKey.list.name),
            firstSeparator
        ).toMap()
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val fannelDirPath = CcPathTool.getMainFannelDirPath(fannelPath)
        val listDirPath = "${fannelDirPath}/${listDirName}"

        val variableName = promptListMap.get(PromptListVars.variableName.name)
        val listTxtName = makeListTextFileName(
            variableName,
        )
        val promptListFile =
            File(listDirPath, listTxtName)
        onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        terminalFragment,
                        fannelDirPath,
                        promptListFile,
                        editTextMap,
                        promptListMap,
                    )
                } catch (e: Exception){
                    Log.e(this.javaClass.name, e.toString())
                }
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (
                        !onDialog
                    ) break
                }
            }
        }
        return returnValue
    }


    private fun execCreate(
        terminalFragment: TerminalFragment,
        fannelDirPath: String,
        promptListFile: File,
        editTextMap: Map<String, String>,
        promptListMap: Map<String, String>,
    ) {
        val context = terminalFragment.context
            ?: return

        promptDialogObj = Dialog(
            context,
//            R.style.BottomSheetDialogTheme
        )

        promptDialogObj?.setContentView(
            R.layout.prompt_list_dialog_layout
        )
        val setTextSrc = editTextMap.get(
            PromptEditTextKey.default.name
        ) ?: String()
        val setText = when(setTextSrc.isEmpty()){
            true -> EditTextMakerForPromptList.makeTextByShell(
                terminalFragment,
                editTextMap
            )
            else -> setTextSrc
        }
        val disableListBind = editTextMap.get(
            PromptEditTextKey.disableListBind.name
        ) == switchOn
        val editTextVisible = editTextMap.get(
            PromptEditTextKey.visible.name
        ) != switchOff
        val listLimit =
            promptListMap.get(PromptListVars.limit.name)?.let {
                try{
                    it.toInt()
                } catch (e: Exception){
                    null
                }
            }
        val filterText = when(disableListBind || !editTextVisible){
            true -> String()
            else -> setText ?: String()
        }
        CoroutineScope(Dispatchers.Main).launch {
            val promptList = withContext(Dispatchers.IO) {
                makePromptList(
                    promptListFile,
                    promptListMap,
                    filterText,
                    listLimit,
                )
            }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lPrompt.txt").absolutePath,
//            listOf(
//                "editTextMap: ${editTextMap}",
//                "promptListMap: ${promptListMap}",
//                "promptList: ${promptList}"
//            ).joinToString("\n")
//        )
            val promptListAdapter = withContext(Dispatchers.Main) {
                PromptListAdapter(
                    context,
                    promptList,
                )
            }
            val listVisible =
                withContext(Dispatchers.IO) {
                    promptListMap.get(PromptListVars.visible.name) != switchOff
                }
            val promptListView =
                withContext(Dispatchers.Main) {
                    promptDialogObj?.findViewById<RecyclerView>(
                        R.id.prompt_list_dialog_list_view
                    )
                }
            withContext(Dispatchers.Main) {
                promptDialogObj?.setOnCancelListener {
                    exitDialog(
                        fannelDirPath,
                        promptListView,
                        String(),
                        promptListFile,
                        listLimit,
                    )
                    returnValue = String()
                    promptDialogObj?.dismiss()
                    promptDialogObj = null
                    onDialog = false
                }
            }
            withContext(Dispatchers.Main) {
                promptListView?.apply {
                    isVisible = listVisible
                    adapter = promptListAdapter
                    layoutManager = PreLoadLayoutManager(
                        context,
                        true,
                    )
                }
            }
            val promptEditText = withContext(Dispatchers.Main) {
                EditTextMakerForPromptList.make(
                    promptDialogObj,
                    editTextMap,
                    setText,
                    editTextVisible,
                )
            }
            withContext(Dispatchers.Main) {
                val promptCancelButton =promptDialogObj?.findViewById<AppCompatImageButton>(
                    R.id.prompt_list_dialog_cancel
                )
                promptCancelButton?.setOnClickListener {
                    returnValue = String()
                    promptDialogObj?.dismiss()
                    promptDialogObj = null
                    onDialog = false
                }
            }
            withContext(Dispatchers.Main) {
                val promptOkButtonView =
                    promptDialogObj?.findViewById<AppCompatImageButton>(
                        R.id.prompt_list_dialog_ok
                    )
                promptOkButtonView?.isVisible = editTextVisible
                promptOkButtonView?.setOnClickListener {
                    val inputEditable = promptEditText?.text
                    if (
                        inputEditable.isNullOrEmpty()
                    ) {
                        exitDialog(
                            fannelDirPath,
                            promptListView,
                            String(),
                            promptListFile,
                            listLimit,
                        )
                        return@setOnClickListener
                    } else returnValue = inputEditable.toString()
                    exitDialog(
                        fannelDirPath,
                        promptListView,
                        inputEditable.toString(),
                        promptListFile,
                        listLimit,
                    )
                }
            }
            withContext(Dispatchers.Main) {
                editTextKeyListener(
                    fannelDirPath,
                    promptListView,
                    promptEditText,
                    promptListFile,
                    listLimit,
                )
            }
            withContext(Dispatchers.Main) {
                setPromptEditText(
                    promptEditText,
                    promptListFile,
                    promptListMap,
                    promptListAdapter,
                    disableListBind,
                    listLimit,
                )
            }
            withContext(Dispatchers.Main) {
                setItemClickListener(
                    fannelDirPath,
                    promptListView,
                    promptListAdapter,
                    promptEditText,
                    promptListMap,
                    promptListFile,
                    listLimit,
                )
            }
        }
        promptDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        promptDialogObj?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialogObj?.show()
    }

    private fun makePromptList(
        promptListFile: File,
        promptListMap: Map<String, String>,
        filterString: String,
        listLimit: Int?,
    ): MutableList<String> {
        val mainList = ReadText(
            promptListFile.absolutePath
        ).textToList()
        val comcatFilePathList = makeExtraList(
            promptListMap.get(PromptListVars.concatFilePathList.name)?.let {
                QuoteTool.splitBySurroundedIgnore(
                    it,
                    secondSeparator
                )
            }
        )
        val promptListByComcatFilePathList = mainList + comcatFilePathList.filter {
            !mainList.contains(it)
        }
        val concatList = makeExtraListFromCon(
            promptListMap.get(PromptListVars.concatList.name)?.let {
                QuoteTool.trimBothEdgeQuote(it)
            }
        )
        val promptList = promptListByComcatFilePathList + concatList.filter {
            !promptListByComcatFilePathList.contains(it)
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lPrompt_make.txt").absolutePath,
//            listOf(
//                "mainList: ${mainList}",
//                "srcListEntry: ${comcatFilePathList}",
//                "srcListEntryFromCon: ${concatList}",
//                "promptListSrc: ${promptListByComcatFilePathList}",
//                "promptList: ${promptList}",
//            ).joinToString("\n")
//        )
        return when(filterString.isEmpty()){
            true -> promptList
            else -> promptList.distinct().filter {
                    line ->
                Regex(
                    filterString
                        .lowercase()
                        .replace("\n", "")
                ).containsMatchIn(
                    line.lowercase()
                )
            }
        }.let {
            listSrc ->
            when(listLimit == null){
                true -> listSrc
                else -> listSrc.take(listLimit)
            } .toMutableList()
        }
    }


    private fun setPromptEditText(
        promptEditText: AppCompatEditText?,
        promptListFile: File,
        editTextMap: Map<String, String>,
        promptListAdapter: PromptListAdapter,
        disableListBind: Boolean,
        listLimit: Int?,
    ){
        if(promptEditText == null) return
        promptEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(
                    !promptEditText.hasFocus()
                    || disableListBind
                ) return
                val updatePromptList = makePromptList(
                    promptListFile,
                    editTextMap,
                    promptEditText.text.toString(),
                    listLimit,
                )
                promptListAdapter.promptList.clear()
                promptListAdapter.promptList.addAll(updatePromptList)
                promptListAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun editTextKeyListener(
        fannelDirPath: String,
        promptListView: RecyclerView?,
        promptEditText: AppCompatEditText?,
        promptListFile: File,
        listLimit: Int?,
    ){
        promptEditText?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action != KeyEvent.ACTION_DOWN ||
                    keyCode != KeyEvent.KEYCODE_ENTER
                ) return false
                val currentInputEditable = promptEditText.text
                if(promptEditText.text.isNullOrEmpty()){
                    exitDialog(
                        fannelDirPath,
                        promptListView,
                        String(),
                        promptListFile,
                        listLimit,
                    )
//                    returnValue = String()
//                    promptDialogObj?.dismiss()
//                    promptDialogObj = null
//                    onDialog = false
                    return false
                }
//                returnValue = currentInputEditable.toString()
                exitDialog(
                    fannelDirPath,
                    promptListView,
                    currentInputEditable.toString(),
                    promptListFile,
                    listLimit,
                )
//                promptDialogObj?.dismiss()
//                promptDialogObj = null
//                onDialog = false
                return false
            }
        })
    }

    private fun setItemClickListener(
        fannelDirPath: String,
        promptListView: RecyclerView?,
        promptListAdapter: PromptListAdapter,
        promptEditText: AppCompatEditText?,
        promptListMap: Map<String, String>,
        promptListFile: File,
        listLimit: Int?,
    ){
        val onInsertByClick = promptListMap.get(
            PromptListVars.onInsertByClick.name
        ) == switchOn
        val onDismissByClick = promptListMap.get(
            PromptListVars.onDismissByClick.name
        ) == switchOn
        promptListAdapter.itemClickListener = object: PromptListAdapter.OnItemClickListener{
            override fun onItemClick(holder: PromptListAdapter.PromptListViewHolder) {
                val itemStr = holder.itemStr
                if(onInsertByClick) {
                    promptEditText?.setText(itemStr)
                }
                if(
                    !onDismissByClick
                ) return
                exitDialog(
                    fannelDirPath,
                    promptListView,
                    itemStr,
                    promptListFile,
                    listLimit,
                )
            }
        }
    }

    private fun makeExtraList(
        listConcatFilePathList: List<String>?,
    ): List<String> {
        if(
            listConcatFilePathList.isNullOrEmpty()
        ) return emptyList()
        return listConcatFilePathList.map {
            ReadText(it).textToList()
        }.flatten().filter { it.trim().isNotEmpty()}

    }

    private fun makeExtraListFromCon(
        concatList: String?,
    ): List<String> {
        if(
            concatList.isNullOrEmpty()
        ) return emptyList()
        return concatList.split(secondSeparator)

    }

    private fun registerToColdList(
        fannelDirPath: String,
        promptListFile: File,
        listLimit: Int?,
    ){
        val trimedReturnValue =
            returnValue.trim()
        if(
            trimedReturnValue.isEmpty()
        ) return

        val promptListDirPath = promptListFile.parent
            ?: return
        saveStatistics(
            fannelDirPath,
            trimedReturnValue,
        )
        FileSystems.createDirs(
            promptListDirPath
        )
        val updatePromptList =
            listOf(trimedReturnValue) +
                    makeNoEmptyList(
                        trimedReturnValue,
                        promptListFile.name,
                        promptListDirPath,
                    ).filter {
                        trimedReturnValue != it
                    }.distinct().let {
                        listSrc ->
                        when(listLimit == null){
                            true -> listSrc
                            else -> listSrc.take(listLimit)
                        }
                    }
        FileSystems.writeFile(
            promptListFile.absolutePath,
            updatePromptList.joinToString("\n")
        )
    }

    private fun saveStatistics(
        fannelDirPath: String,
        trimedReturnValue: String,
    ){
        val statisticsFile = File("${fannelDirPath}/${statisticsName}/promptWithList.txt")
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lStatistics.txt").absolutePath,
//            listOf(
//                "statisticsFile: ${statisticsFile.absolutePath}"
//            ).joinToString("\n")
//        )
        val updateStatisticsCon = ReadText(
            statisticsFile.absolutePath
        ).textToList() + listOf(trimedReturnValue)
        FileSystems.writeFile(
            statisticsFile.absolutePath,
            updateStatisticsCon.joinToString("\n")
        )
    }

    private fun makeNoEmptyList(
        trimedReturnValue: String,
        listTxtName: String,
        listDirPath: String,
    ): List<String> {
        val curList = ReadText(
            File(
                listDirPath,
                listTxtName
            ).absolutePath
        ).textToList()
        if(
            trimedReturnValue.isNotEmpty()
        ) return listOf(trimedReturnValue) + curList
        return curList
    }

    private fun makeListTextFileName(
        variableName: String?,
    ): String {
        val prefixUpperVariableName = variableName?.replaceFirstChar { it.uppercase() }
        return "${listPrefix}${prefixUpperVariableName}${listTxtSuffix}"
    }

    private fun exitDialog(
        fannelDirPath: String,
        promptListView: RecyclerView?,
        returnStr: String,
        promptListFile: File,
        listLimit: Int?,
    ){
        promptListView?.layoutManager = null
        promptListView?.adapter = null
        promptListView?.recycledViewPool?.clear()
        promptListView?.removeAllViews()
        returnValue = returnStr
        registerToColdList(
            fannelDirPath,
            promptListFile,
            listLimit,
        )
        promptDialogObj?.dismiss()
        promptDialogObj = null
        onDialog = false
    }
}

private object EditTextMakerForPromptList {
    fun make(
        promptDialogObj: Dialog?,
        editTextMap: Map<String, String>,
        setText: String?,
        visible: Boolean,
    ): AppCompatEditText? {
        val promptEditText =
            promptDialogObj?.findViewById<AppCompatEditText>(
                R.id.prompt_list_dialog_search_edit_text
            ) ?: return null
        if(
            !setText.isNullOrEmpty()
        ){
            promptEditText.setText(setText)
        }
        editTextMap.get(
            PromptEditTextKey.hint.name
        )?.let {
            promptEditText.hint = it
        }
        if(!visible) {
            promptEditText.isVisible = false
            return promptEditText
        }
        promptEditText.requestFocus()
        return promptEditText
    }

    fun makeTextByShell(
        terminalFragment: TerminalFragment,
        editTextMap: Map<String, String>,
    ): String? {
        val context = terminalFragment.context
            ?: return null
        val mainOrSubFannelPath = editTextMap.get(
            PromptEditTextKey.fannelPath.name
        )
        val setReplaceVariableMap = when(
            mainOrSubFannelPath.isNullOrEmpty()
        ){
            true -> emptyMap()
            else -> SetReplaceVariabler
                .makeSetReplaceVariableMapFromSubFannel(
                    context,
                    mainOrSubFannelPath
                )
        }
        val fannelInfoMap = FannelInfoTool.getFannelInfoMap(
            terminalFragment,
            mainOrSubFannelPath
        )
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )

        val shellCon = editTextMap.get(
            PromptEditTextKey.shellPath.name
        )?.let {
            EditSettingExtraArgsTool.makeShellCon(editTextMap)
        }?.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName
            )
        } ?: return null
        val busyboxExecutor = BusyboxExecutor(
            context,
            UbuntuFiles(context),
        )
        val repValMap = editTextMap.get(
            PromptEditTextKey.repValCon.name
        ).let {
            CmdClickMap.createMap(
                it,
                '&'
            )
        }.toMap()
        return busyboxExecutor.getCmdOutput(
            shellCon,
            repValMap
        )
    }
}


private enum class PromptWithTextMapKey {
    list,
    editText,
}

private enum class PromptEditTextKey {
    default,
    hint,
    shellPath,
    fannelPath,
    repValCon,
    disableListBind,
    visible,
}
private enum class PromptListVars {
    variableName,
    concatFilePathList,
    concatList,
    onInsertByClick,
    onDismissByClick,
    visible,
    limit,
}