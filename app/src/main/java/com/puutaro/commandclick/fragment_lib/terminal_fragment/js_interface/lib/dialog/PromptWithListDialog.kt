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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class PromptWithListDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var returnValue = String()
    private var promptDialogObj: Dialog? = null
    private val suggestPrefix = "suggest"
    private val suggestDirName = "${suggestPrefix}Text"
    private val statisticsName = "statistics"
    private val suggestTxtSuffix = ".txt"
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
    private val mapSeparator = ','
    private val firstSeparator = '|'
    private val secondSeparator = '?'
    private var onDialog = false
    private val switchOn = "ON"
    private val switchOff = "OFF"

    fun create(
        suggestOrDefoTxtVars: String,
    ): String {
        val promptListTotalMap = CmdClickMap.createMap(
            suggestOrDefoTxtVars,
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
        val currentScriptName = terminalFragment.currentFannelName
        val fannelDirName = CcPathTool.makeFannelDirName(
            currentScriptName
        )
        val fannelDirPath = "${cmdclickDefaultAppDirPath}/${fannelDirName}"
        val suggestDirPath = "${fannelDirPath}/${suggestDirName}"

//            makeSuggestAndDefoTxtMap(suggestOrDefoTxtVars)
        val variableName = promptListMap.get(PromptListVars.variableName.name)
//        val prefixUpperVariableName = variableName?.replaceFirstChar { it.uppercase() }
        val suggestTxtName = makeSuggestTextFileName(
            variableName,
        )
//            "${suggestPrefix}${prefixUpperVariableName}${suggestTxtSuffix}"
        val promptListFile =
            File(suggestDirPath, suggestTxtName)
//        val mainSuggestList = ReadText(
//            File(suggestDirPath, suggestTxtName).absolutePath
//        ).textToList()
//        val suggestSrcListEntry = makeExtraSuggestList(
//            promptListMap.get(PromptListVars.concatFilePathList.name)?.let {
//                QuoteTool.splitBySurroundedIgnore(
//                    it,
//                    secondSeparator
//                )
//            }
//        ) + mainSuggestList
//        val suggestSrcList = suggestSrcListEntry.distinct()
        onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        terminalFragment,
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
        promptListFile: File,
        editTextMap: Map<String, String>,
        promptListMap: Map<String, String>,
//        suggestDirPath: String,
//        suggestSrcList: List<String>,
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

//        val editTextMap = CmdClickMap.createMap(
//            promptListMap.get(PromptWithTextMapKey.editText.name),
//            firstSeparator
//        ).toMap()
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
        val disableSuggestBind = editTextMap.get(
            PromptEditTextKey.disableSuggestBind.name
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
        val filterText = when(disableSuggestBind || !editTextVisible){
            true -> String()
            else -> setText ?: String()
        }
        val promptList = makePromptList(
            promptListFile,
            promptListMap,
            filterText,
            listLimit,
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lPrompt.txt").absolutePath,
//            listOf(
//                "editTextMap: ${editTextMap}",
//                "promptListMap: ${promptListMap}",
//                "promptList: ${promptList}"
//            ).joinToString("\n")
//        )
        val promptListAdapter = PromptListAdapter(
            context,
            promptList,
        )
        val listVisible =
            promptListMap.get(PromptListVars.visible.name) != switchOff
        val promptListView =
            promptDialogObj?.findViewById<RecyclerView>(
                R.id.prompt_list_dialog_list_view
            )
        promptListView?.isVisible = listVisible
        promptListView?.adapter = promptListAdapter
        promptListView?.layoutManager = PreLoadLayoutManager(
            context,
            true,
        )
        val promptEditText = EditTextMakerForPromptList.make(
//            terminalFragment,
            promptDialogObj,
            editTextMap,
            setText,
            editTextVisible,
        )
        val promptCancelButton =
            promptDialogObj?.findViewById<AppCompatImageButton>(
                R.id.prompt_list_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            returnValue = String()
            promptDialogObj?.dismiss()
            promptDialogObj = null
            onDialog = false
        }
        val promptOkButtonView =
            promptDialogObj?.findViewById<AppCompatImageButton>(
                R.id.prompt_list_dialog_ok
            )
        promptOkButtonView?.isVisible = editTextVisible
        promptOkButtonView?.setOnClickListener {
            val inputEditable = promptEditText?.text
            if(
                inputEditable.isNullOrEmpty()
            ) {
                exitDialog(
                    promptListView,
                    String(),
                    promptListFile,
                    listLimit,
                )
                return@setOnClickListener
            }
            else returnValue = inputEditable.toString()
            exitDialog(
                promptListView,
                inputEditable.toString(),
                promptListFile,
                listLimit,
            )
        }
//        val promptEditText =
//            promptDialogObj?.findViewById<AutoCompleteTextView>(
//                R.id.prompt_dialog_input
//            )
//
//        promptMap.get(
//            EditTextKey.default.name
//        )
//        promptEditText?.requestFocus()
//        setSuggestEditText(
//            terminalFragment,
//            promptEditText,
//            suggestSrcList,
//        )
        editTextKeyListener(
            promptListView,
            promptEditText,
            promptListFile,
            listLimit,
        )
        setSuggestEditText(
            promptEditText,
            promptListFile,
            promptListMap,
            promptListAdapter,
            disableSuggestBind,
            listLimit,
        )
        setItemClickListener(
            promptListView,
            promptListAdapter,
            promptEditText,
            promptListMap,
            promptListFile,
            listLimit,
        )
        promptDialogObj?.setOnCancelListener {
            exitDialog(
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
        promptDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        promptDialogObj?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialogObj?.show()
    }

    private fun makePromptList(
        promptListFile: File,
//        suggestDirPath: String,
//        suggestTxtName: String,
        suggestMap: Map<String, String>,
        filterString: String,
        listLimit: Int?,
    ): MutableList<String> {
        val mainSuggestList = ReadText(
            promptListFile.absolutePath
        ).textToList()
        val suggestSrcListEntry = makeExtraSuggestList(
            suggestMap.get(PromptListVars.concatFilePathList.name)?.let {
                QuoteTool.splitBySurroundedIgnore(
                    it,
                    secondSeparator
                )
            }
        )
        val promptList = mainSuggestList + suggestSrcListEntry.filter {
            !mainSuggestList.contains(it)
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lPrompt_make.txt").absolutePath,
//            listOf(
//                "mainSuggestList: ${mainSuggestList}",
//                "suggestSrcListEntry: ${suggestSrcListEntry}",
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


    private fun setSuggestEditText(
        promptEditText: AppCompatEditText?,
        promptListFile: File,
        suggestMap: Map<String, String>,
        promptListAdapter: PromptListAdapter,
        disableSuggestBind: Boolean,
        listLimit: Int?,
    ){
        if(promptEditText == null) return
        promptEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(
                    !promptEditText.hasFocus()
                    || disableSuggestBind
                ) return
                val updatePromptList = makePromptList(
                    promptListFile,
                    suggestMap,
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
                    promptListView,
                    itemStr,
                    promptListFile,
                    listLimit,
                )
            }
        }
    }

    private fun makeExtraSuggestList(
        suggestConcatFilePathList: List<String>?,
    ): List<String> {
        if(
            suggestConcatFilePathList.isNullOrEmpty()
        ) return emptyList()
        return suggestConcatFilePathList.map {
            ReadText(it).textToList()
        }.flatten().filter { it.trim().isNotEmpty()}

    }

    private fun registerToSuggest(
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
            promptListDirPath,
            trimedReturnValue,
        )
        FileSystems.createDirs(
            promptListDirPath
        )
        val updateSuggestList =
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
            updateSuggestList.joinToString("\n")
        )
    }

    private fun saveStatistics(
        promptListDirPath: String,
        trimedReturnValue: String,
    ){
        val fannelDirPath = File(promptListDirPath).parent
            ?: return
        val statisticsFile = File("${fannelDirPath}/${statisticsName}/${statisticsName}.txt")
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
        suggestTxtName: String,
        suggestDirPath: String,
    ): List<String> {
        val curSuggestList = ReadText(
            File(
                suggestDirPath,
                suggestTxtName
            ).absolutePath
        ).textToList()
        if(
            trimedReturnValue.isNotEmpty()
        ) return listOf(trimedReturnValue) + curSuggestList
        return curSuggestList
    }

    private fun makeSuggestTextFileName(
        variableName: String?,
    ): String {
        val prefixUpperVariableName = variableName?.replaceFirstChar { it.uppercase() }
        return "${suggestPrefix}${prefixUpperVariableName}${suggestTxtSuffix}"
    }

    private fun exitDialog(
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
        registerToSuggest(
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
    disableSuggestBind,
    visible,
}
private enum class PromptListVars {
    variableName,
    concatFilePathList,
    onInsertByClick,
    onDismissByClick,
    visible,
    limit,
}