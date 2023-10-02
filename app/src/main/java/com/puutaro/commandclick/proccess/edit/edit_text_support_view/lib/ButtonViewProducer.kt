package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.view.MotionEvent
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.SettingCmdArgs
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.ExecJsScriptInEdit
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.GridDialogForButton
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.ListDialogForButton
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SetVariableTypeValue
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


object ButtonViewProducer {
    private const val setVariableSetSeparator = "|"
    private const val jsFrag = "jsf"
    private const val settingFrag = "setf"
    private const val blankString = "cmdclickBlank"
    private const val buttoLabelThis = "this"
    private var consecutiveJob: Job? = null
    fun make (
        editFragment: EditFragment,
        insertTextView: TextView,
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float,
        currentComponentIndex: Int,
        isInsertTextViewVisible: Boolean = false
    ): Button {
        val context = editParameters.context
        val readSharePreffernceMap = editParameters.readSharePreffernceMap
        val currentId = editParameters.currentId

        val binding = editFragment.binding
        val scriptFileSaver = ScriptFileSaver(
            binding,
            editFragment,
            readSharePreffernceMap,
            true ,
        )

        val currentSetVariableValue = SetVariableTypeValue.makeByReplace(
            editParameters
        )

        val buttonMap = getButtonMap(
            currentSetVariableValue,
            currentComponentIndex
        )
        val linearParamsForButton = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForButton.weight = weight
        val insertButton = Button(context)
        insertButton.id = currentId + EditTextSupportViewId.BUTTON.id
        insertButton.tag = "button${currentId + EditTextSupportViewId.BUTTON.id}"
        insertButton.layoutParams = linearParamsForButton
        insertButton.text = makeButtonLabel(
            getButtonLabel(buttonMap),
            insertTextView.text.toString(),
        )
        ButtonSetter.set(
            context,
            insertButton
        )
        insertTextView.isVisible = isInsertTextViewVisible

//        insertButton.setOnClickListener {
//                innerButtonView ->
//            execButtonClickEvent(
//                editFragment,
//                insertEditText,
//                scriptFileSaver,
//                editParameters,
//                buttonMap,
//                currentSetVariableValue
//            )
//        }

        buttonTouchListener(
            insertButton,
            editFragment,
            insertEditText,
            scriptFileSaver,
            editParameters,
            buttonMap,
            currentSetVariableValue
        )
        return insertButton
    }

    private fun buttonTouchListener(
        insertButton: Button,
        editFragment: EditFragment,
        insertEditText: EditText,
        scriptFileSaver: ScriptFileSaver,
        editParameters: EditParameters,
        buttonMap: Map<String, String>?,
        currentSetVariableValue: String?
    ){
        with(insertButton) {
            setOnTouchListener(android.view.View.OnTouchListener { v, event ->
                var execTouchJob: Job? = null
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        consecutiveJob?.cancel()
                        consecutiveJob = CoroutineScope(Dispatchers.IO).launch {
                            var roopTimes = 0
                            while (true) {
                                execTouchJob = CoroutineScope(Dispatchers.Main).launch {
                                    execButtonClickEvent(
                                        editFragment,
                                        insertEditText,
                                        scriptFileSaver,
                                        editParameters,
                                        buttonMap,
                                        currentSetVariableValue
                                    )
                                }
                                if(
                                    !getIsConsec(buttonMap)
                                ) break
                                withContext(Dispatchers.IO){
                                    if(
                                        roopTimes == 0
                                    ) delay(300)
                                    else delay(60)
                                }
                                roopTimes++
                            }
                        }
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL, -> {
                        v.performClick()
                        execTouchJob?.cancel()
                        consecutiveJob?.cancel()
                    }
                }
                true
            })
        }
    }

    private fun execButtonClickEvent(
        editFragment: EditFragment,
        insertEditText: EditText,
        scriptFileSaver: ScriptFileSaver,
        editParameters: EditParameters,
        buttonMap: Map<String, String>?,
        currentSetVariableValue: String?
    ){
        val context = editFragment.context
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val currentShellContentsList = editParameters.currentShellContentsList
        val recordNumToMapNameValueInCommandHolder = editParameters.recordNumToMapNameValueInCommandHolder
        val setReplaceVariableMap = editParameters.setReplaceVariableMap
        val readSharePreffernceMap = editParameters.readSharePreffernceMap
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val outputPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
        val currentScriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        scriptFileSaver.save(
            currentShellContentsList,
            recordNumToMapNameValueInCommandHolder,
        )
        val execCmdEditable = insertEditText.text

        val cmdPrefix = getCmdPrefix(
            buttonMap
        )
            .split("!")
            .firstOrNull()
            ?: String()
        if(
            execCmdEditable.isNullOrEmpty()
            && cmdPrefix.isEmpty()
        ) return
        val currentScriptPath = "${currentAppDirPath}/${currentScriptName}"
        val innerExecCmd =  makeInnerExecCmd(
            cmdPrefix,
            execCmdEditable.toString(),
            currentScriptPath,
            setReplaceVariableMap,
        )

        val doubleColon = "::"
        val backStackMacro = doubleColon +
                SettingVariableSelects.ButtonEditExecVariantSelects.BackStack.name +
                doubleColon
        val termOutMacro = doubleColon +
                SettingVariableSelects.ButtonEditExecVariantSelects.TermOut.name +
                doubleColon
        val noJsTermOut = doubleColon +
                SettingVariableSelects.ButtonEditExecVariantSelects.NoJsTermOut.name +
                doubleColon
        val termLong = doubleColon +
                SettingVariableSelects.ButtonEditExecVariantSelects.TermLong.name +
                doubleColon

        val onEditExecuteOnce = innerExecCmd.contains(backStackMacro)
        val onTermOutMacro = innerExecCmd.contains(termOutMacro)
        if(onTermOutMacro) {
            terminalViewModel.onDisplayUpdate = true
        }
        terminalViewModel.onDisplayUpdate = !innerExecCmd.contains(noJsTermOut)
        if(
            innerExecCmd.contains(termLong)
        ){
            val listener =
                context as? EditFragment.OnTermSizeLongListenerForEdit
            listener?.onTermSizeLongForEdit()
        }
        val execCmdAfterTrimButtonEditExecVariant =
            innerExecCmd
                .replace(backStackMacro, "")
                .replace(termOutMacro, "")
                .replace(noJsTermOut, "")
                .replace(termLong, "")
                .trim(' ')
                .replace(Regex("\t\t*"), " ")
                .replace("\t", " ")
                .replace(Regex(";;*"), ";")
                .replace(Regex("  *"), " ")
        val execCmdReplaceBlankList = surroundBlankReplace(
            execCmdAfterTrimButtonEditExecVariant
        ).split(" ")

        val firstCmdStr = QuoteTool.trimBothEdgeQuote(
            execCmdReplaceBlankList.firstOrNull()
        )
        if(
            firstCmdStr == jsFrag
        ){
            execJsFileForButton(
                editFragment,
                terminalViewModel,
                execCmdReplaceBlankList,
                buttonMap
            )
            return
        }
        if(
            firstCmdStr == settingFrag
        ){
            execSettingCmd(
                editFragment,
                insertEditText,
                readSharePreffernceMap,
                execCmdReplaceBlankList,
                currentSetVariableValue,
            )
            return
        }

        val execCmd = if(
            execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null")
            || execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null 2>&1")
        ) "${execCmdAfterTrimButtonEditExecVariant};"
        else "$execCmdAfterTrimButtonEditExecVariant >> \"${outputPath}\""
        ExecBashScriptIntent.ToTermux(
            editFragment.runShell,
            context,
            execCmd,
            true
        )
        if(onEditExecuteOnce) {
            val listener =
                context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                ToolbarButtonBariantForEdit.OK,
                readSharePreffernceMap,
                true,
            )
        }
    }

    private fun makeInnerExecCmd(
        cmdPrefix: String,
        execCmdEditableString: String,
        currentScriptPath: String,
        setReplaceVariableMap: Map<String, String>?
    ): String {
        val scriptFileObj = File(currentScriptPath)
        val currentAppDirPath = scriptFileObj.parent
            ?: return String()
        val currentScriptName = scriptFileObj.name
            ?: return String()
        val fannelDirName = currentScriptName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
        val innerExecCmdSourceBeforeReplace =
            "$cmdPrefix " +
                QuoteTool.trimBothEdgeQuote(
                    execCmdEditableString
                )
        return innerExecCmdSourceBeforeReplace.trim(';')
            .replace(Regex("  *"), " ")
            .let {
                ScriptPreWordReplacer.replace(
                    it,
                    currentAppDirPath,
                    fannelDirName,
                    currentScriptName
                )
            }.let {
            var innerExecCmd = it
            setReplaceVariableMap?.forEach {
                val replaceVariable = "\${${it.key}}"
                val replaceString = it.value
                    .let {
                        ScriptPreWordReplacer.replace(
                            it,
                            currentAppDirPath,
                            fannelDirName,
                            currentScriptName
                        )
                    }
                innerExecCmd = innerExecCmd.replace(
                    replaceVariable,
                    replaceString
                )
            }
            innerExecCmd
        }
    }

    private fun surroundBlankReplace(
        targetString: String,
    ): String {
        var quoteType: Char? = null
        return targetString.toList().map{
            if(
                quoteType == null
                &&
                (
                        it.equals('\'')
                                || it.equals('\"')
                        )
            ) {
                quoteType = it
                return@map it
            }

            if(
                quoteType != null
                && it == quoteType
            ) {
                quoteType = null
                return@map it
            }
            if(
                quoteType != null
                    && it == ' '
            ){
                return@map blankString
            }
            it
        }.joinToString("")
    }

    private fun execSettingCmd(
        editFragment: EditFragment,
        insertEditText: EditText,
        readSharePreffernceMap: Map<String, String>,
        execCmdReplaceBlankList: List<String>,
        currentSetVariableValue: String?
    ){
        val setFOptionMap = getSetFOptionMap(
            execCmdReplaceBlankList
        ) ?: return
        val settingArg = setFOptionMap.get(SET_F_OPTION_MAP_KEY.type)
            ?: return
        when(settingArg){
            SettingCmdArgs.ListAdd.name -> {
                val listConSlSpiOptionsStr = getFromSetVariableValueByIndex(
                    currentSetVariableValue,
                    0
                )
                execListAddForSetting(
                    editFragment,
                    insertEditText,
                    readSharePreffernceMap,
                    listConSlSpiOptionsStr,
                    setFOptionMap
                )
            }
//            SettingCmdArgs.ListAdd_BY_SB.name -> {
//                val listConSlSpiOptionsStr = getFromSetVariableValueByIndex(
//                    currentSetVariableValue,
//                    0
//                )
//                execListAddBySBForSetting(
//                    editFragment,
//                    insertEditText,
//                    listConSlSpiOptionsStr,
//                    setFOptionMap
//                )
//            }
            else -> {}
        }
    }

    private fun execListAddForSetting(
        editFragment: EditFragment,
        insertEditText: EditText,
        readSharePreffernceMap: Map<String, String>,
        listConSlSpiOptionsStr: String?,
        setFOptionMap: Map<String, String>
    ){
        val context = editFragment.context
        val suffixList = setFOptionMap.get(
            SET_F_OPTION_MAP_KEY.ListAdd.suffix.name
        )?.split("&") ?: emptyList()
        val addSourceDirPath = setFOptionMap.get(
            SET_F_OPTION_MAP_KEY.ListAdd.dirPath.name
        ) ?: return
        val isFull = !setFOptionMap.get(
            SET_F_OPTION_MAP_KEY.ListAdd.howFull.name
        ).isNullOrEmpty()
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val currentScriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        val listCon = FileSystems.sortedFiles(
            addSourceDirPath,
            "on"
        ).filter {
            fileName ->
            fileName != currentScriptName
                    && suffixList.any {
                    suffix ->
                fileName.endsWith(suffix)
                    }
        }.map {
            "$addSourceDirPath/$it"
        }.joinToString("\n")
        if(
            listCon.isEmpty()
        ) return
        GridDialogForButton.create(
            editFragment,
            listCon,
        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO){
                for (i in 1..6000) {
                    delay(100)
                    if (!terminalViewModel.onDialog) break
                }

            }
            val selectedScript = withContext(Dispatchers.IO) {
                terminalViewModel.dialogReturnValue.replace(
                    "${addSourceDirPath}/",
                    ""
                )
            }
            terminalViewModel.dialogReturnValue = String()
            if(
                selectedScript.isEmpty()
            ) return@launch
            if(
                selectedScript == currentScriptName
            ) {
                Toast.makeText(
                    context,
                    "this script cannot register\n ${selectedScript}",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            val listPathKey = ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
            val listFilePath = listConSlSpiOptionsStr
                ?.split("!")
                ?.filter {
                    it.contains(listPathKey)
                }?.firstOrNull()
                ?.replace("${listPathKey}=", "")
                ?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: return@launch
            val listFilePathOjb = File(listFilePath)
            val listDirPath = listFilePathOjb.parent
                ?: return@launch
            FileSystems.createDirs(listDirPath)
            val listFileName = listFilePathOjb.name
            if(
                ReadText(
                    listDirPath,
                    listFileName
                ).textToList().filter {
                    it == selectedScript
                }.isNotEmpty()
            ) {
                Toast.makeText(
                    context,
                    "this script already register\n ${selectedScript}",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            withContext(Dispatchers.IO){
                val insertSelectedScript = if(
                    isFull
                ) "$addSourceDirPath/$selectedScript"
                else selectedScript
                val updateListCon = insertSelectedScript + "\n" + ReadText(
                    listDirPath,
                    listFileName
                ).readText()
                FileSystems.writeFile(
                    listDirPath,
                    listFileName,
                    updateListCon
                )
            }
            withContext(Dispatchers.Main){
                insertEditText.setText(listFilePath)
            }
        }
    }

    private fun execListAddBySBForSetting(
        editFragment: EditFragment,
        insertEditText: EditText,
        listConSlSpiOptionsStr: String?,
        setFOptionMap: Map<String, String>
    ){
        val context = editFragment.context
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val listCon = setFOptionMap.get(
            SET_F_OPTION_MAP_KEY.ListAddBySB.selectsValues.name
        )?.replace("&", "\n") ?: return
        ListDialogForButton.create(
            editFragment,
            listCon,
        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO){
                for (i in 1..6000) {
                    delay(100)
                    if (!terminalViewModel.onDialog) break
                }
            }
            val selectedItem = terminalViewModel.dialogReturnValue
            terminalViewModel.dialogReturnValue = String()
            if(
                selectedItem.isEmpty()
            ) return@launch
            val listPathKey = ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
            val listFilePath = listConSlSpiOptionsStr
                ?.split("!")
                ?.filter {
                    it.contains(listPathKey)
                }?.firstOrNull()
                ?.replace("${listPathKey}=", "")
                ?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: return@launch
            val listFilePathOjb = File(listFilePath)
            val listDirPath = listFilePathOjb.parent
                ?: return@launch
            FileSystems.createDirs(listDirPath)
            val listFileName = listFilePathOjb.name
            if(
                ReadText(
                    listDirPath,
                    listFileName
                ).textToList().filter {
                    it == selectedItem
                }.isNotEmpty()
            ) {
                Toast.makeText(
                    context,
                    "this script already register\n ${selectedItem}",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            withContext(Dispatchers.IO){
                val updateListCon = selectedItem + "\n" + ReadText(
                    listDirPath,
                    listFileName
                ).readText()
                FileSystems.writeFile(
                    listDirPath,
                    listFileName,
                    updateListCon
                )
            }
            withContext(Dispatchers.Main){
                insertEditText.setText(listFilePath)
            }
        }
    }

    private fun execJsFileForButton(
        editFragment: EditFragment,
        terminalViewModel: TerminalViewModel,
        execCmdReplaceBlankList: List<String>,
        buttonMap: Map<String, String>?
    ){
        terminalViewModel.jsArguments = String()
        val jsFilePathIndex = 1
        val jsFilePath = QuoteTool.trimBothEdgeQuote(
            execCmdReplaceBlankList.get(
                jsFilePathIndex
            )
        )
        if(
            !File(jsFilePath).isFile
        ) return
        val listener = editFragment.context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
        listener?.onKeyBoardVisibleChangeForEditFragment(
            false,
            true,
        )
        if(
            !getDisableKeyboardHidden(
                buttonMap
            )
        ) Keyboard.hiddenKeyboardForFragment(editFragment)
        terminalViewModel.jsArguments = if(execCmdReplaceBlankList.size > 1){
            execCmdReplaceBlankList
                .slice(2..execCmdReplaceBlankList.size-1)
                .map{
                    QuoteTool.trimBothEdgeQuote(
                        it.replace(blankString, " ")
                    )
                }.joinToString("\t")
        } else String()
        ExecJsScriptInEdit.exec(
            editFragment,
            jsFilePath,
        )
    }

    private fun makeButtonLabel(
        buttonLabelSource: String?,
        textViewLabel: String,
    ): String {
        return if(
            buttonLabelSource == buttoLabelThis
        ) textViewLabel
        else if(
            !buttonLabelSource.isNullOrEmpty()
        ) buttonLabelSource
        else "EXEC"
    }

    private fun getCmdPrefix(
        buttonMap: Map<String, String>?
    ): String {
        return buttonMap?.get(
            ButtonEditKey.cmd.name
        ) ?: String()
    }

    private fun getButtonLabel(
        buttonMap: Map<String, String>?
    ): String {
        return buttonMap?.get(
            ButtonEditKey.label.name
        ) ?: String()
    }

    private fun getIsConsec(
        buttonMap: Map<String, String>?
    ): Boolean {
        return buttonMap?.get(
            ButtonEditKey.isConsec.name
        ) == "true"
    }


    private fun getDisableKeyboardHidden(
        buttonMap: Map<String, String>?
    ): Boolean {
        return buttonMap?.get(
            ButtonEditKey.disableKeyboardHidden.name
        ) == "true"
    }

    private fun getButtonMap(
        currentSetVariableValue: String?,
        currentComponentIndex: Int
    ): Map<String, String>? {
        return currentSetVariableValue?.let {
            if(
                it.contains(
                    setVariableSetSeparator
                )
            ) return@let it.split(
                setVariableSetSeparator
            ).getOrNull(currentComponentIndex).let {
                QuoteTool.trimBothEdgeQuote(it)
            }
            QuoteTool.trimBothEdgeQuote(it)
        }?.split('!')
            ?.map {
                CcScript.makeKeyValuePairFromSeparatedString(
                    it,
                    "="
                )
            }?.toMap()
    }

    private fun getSetFOptionMap(
        execCmdReplaceBlankList: List<String>,
    ): Map<String, String>? {
        val execCmdReplaceBlankListSize = execCmdReplaceBlankList.size
        val optionList = if(
            execCmdReplaceBlankList.size > 1
        ) execCmdReplaceBlankList.slice(
            1 until execCmdReplaceBlankListSize
        )
        else return null
        return optionList.map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }.toMap()
    }

    private fun getFromSetVariableValueByIndex(
        currentSetVariableValue: String?,
        targetIndex: Int
    ): String? {
        return currentSetVariableValue?.let {
            if(
                it.contains(
                    setVariableSetSeparator
                )
            ) return@let it.split(
                setVariableSetSeparator
            ).getOrNull(targetIndex).let {
                QuoteTool.trimBothEdgeQuote(it)
            }
            QuoteTool.trimBothEdgeQuote(it)
        }
    }


    enum class ButtonEditKey {
        cmd,
        label,
        isConsec,
        disableKeyboardHidden
    }

    object SET_F_OPTION_MAP_KEY {
        val type = "type"
        enum class ListAdd {
            dirPath,
            suffix,
            howFull,
        }
        enum class ListAddBySB {
            selectsValues,
        }
    }
}