package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Intent
import android.view.MotionEvent
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingCmdArgs
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.intent.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.ExecJsScriptInEdit
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.GridDialogForButton
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SetVariableTypeValue
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.button.JsPathForEditButton
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.proccess.edit.lib.EditVariableName
import com.puutaro.commandclick.proccess.edit.lib.ListContentsSelectBoxTool
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePreferenceMethod
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
    private const val basht = "basht"
    private const val bashf = "bashf"
    private const val bashb = "bashb"
    private const val blankString = "cmdclickBlank"
    private const val buttoLabelThis = "this"
    private var consecutiveJob: Job? = null
    const val doubleColon = "::"
    private val backStackMacro = doubleColon +
            SettingVariableSelects.ButtonEditExecVariantSelects.BackStack.name +
            doubleColon
    private val termOutMacro = doubleColon +
            SettingVariableSelects.ButtonEditExecVariantSelects.TermOut.name +
            doubleColon
    private val noJsTermOut = doubleColon +
            SettingVariableSelects.ButtonEditExecVariantSelects.NoJsTermOut.name +
            doubleColon
    private val termLong = doubleColon +
            SettingVariableSelects.ButtonEditExecVariantSelects.TermLong.name +
            doubleColon
    private val cmdclickTempButtonExecShellName = "cmdclickTempButtonExec.sh"
    fun make (
        editFragment: EditFragment,
        insertTextView: TextView,
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float,
        currentComponentIndex: Int,
    ): Button {
        val context = editParameters.context
        val currentId = editParameters.currentId

        val scriptFileSaver = ScriptFileSaver(
            editFragment,
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
            insertButton,
            buttonMap,
        )
//        insertTextView.isVisible = isInsertTextViewVisible
        val currentVariableName = editParameters.currentVariableName
        val buttonEventArgs = ButtonEventArgs(
            insertButton,
            editFragment,
            insertEditText,
            currentVariableName,
            scriptFileSaver,
            editParameters,
            buttonMap,
            currentSetVariableValue
        )
        when(getIsConsec(buttonMap)) {
            true -> buttonTouchListener(buttonEventArgs)
            else -> buttonClickListner(buttonEventArgs
            )
        }
        return insertButton
    }

    private fun buttonClickListner(
        buttonEventArgs: ButtonEventArgs,
    ){
        val insertButton = buttonEventArgs.insertButton
        insertButton.setOnClickListener {
            execButtonClickEvent(
                buttonEventArgs
            )
        }
    }

    private fun buttonTouchListener(
        buttonEventArgs: ButtonEventArgs,
    ){
        val insertButton = buttonEventArgs.insertButton
        val buttonMap = buttonEventArgs.buttonMap
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
                                        buttonEventArgs
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
        buttonEventArgs: ButtonEventArgs,
    ){
        val editFragment = buttonEventArgs.editFragment
        val context = editFragment.context
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val editParameters = buttonEventArgs.editParameters
        val recordNumToMapNameValueInCommandHolder = editParameters.recordNumToMapNameValueInCommandHolder
        val recordNumToMapNameValueInSettingHolder = editParameters.recordNumToMapNameValueInCommandHolder
        val setReplaceVariableMap = editParameters.setReplaceVariableMap
        val readSharePreffernceMap = editParameters.readSharePreffernceMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val buttonMap = buttonEventArgs.buttonMap
        val currentButtonTag = buttonMap?.get(
            ButtonEditKey.tag.name
        )

        buttonEventArgs.scriptFileSaver.save(
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
        saveListContents(editFragment, currentButtonTag)
        simpleJsExecutor(
            buttonEventArgs
        )
        val execCmdEditable = buttonEventArgs.insertEditText.text
        val isExecCmd = !buttonMap?.get(
            ButtonEditKey.cmd.name
        ).isNullOrEmpty()
        if(!isExecCmd) return
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
        if(
            innerExecCmd.isEmpty()
        ) return

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
            listener?.onTermSizeLongForEdit(editFragment)
        }
        val execCmdAfterTrimButtonEditExecVariant =
            innerExecCmd
                .replace(backStackMacro, "")
                .replace(termOutMacro, "")
                .replace(noJsTermOut, "")
                .replace(termLong, "")
                .trim()
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
        when(firstCmdStr){
            jsFrag
            -> execJsFileForButton(
                editFragment,
                terminalViewModel,
                execCmdReplaceBlankList,
                buttonMap
            )
            settingFrag
            -> execSettingCmd(
                buttonEventArgs,
                execCmdReplaceBlankList,
            )
            basht
            -> execShellScriptByTermux(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
                innerExecCmd,
            )
            bashb
            -> execShellScriptByBackground(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
            )
            bashf
            -> execShellScriptByForeground(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
            )
            else
            -> execShellHandler(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
                innerExecCmd,
            )
        }
    }

    private fun execShellScriptByTermux(
        editFragment: EditFragment,
        execCmdAfterTrimButtonEditExecVariant: String,
        innerExecCmd: String,
    ){
        val context = editFragment.context
            ?: return
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val outputPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
        val execCmd = if(
            execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null")
            || execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null 2>&1")
        ) "${execCmdAfterTrimButtonEditExecVariant};"
        else "$execCmdAfterTrimButtonEditExecVariant >> \"${outputPath}\""
        ExecBashScriptIntent.ToTermux(
            context,
            execCmd.replace(
                Regex("^${basht}"),
                "bash"
            ),
            true
        )
        val onEditExecuteOnce = innerExecCmd.contains(backStackMacro)
        if(onEditExecuteOnce) {
            val listener =
                context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                ToolbarButtonBariantForEdit.OK,
                editFragment.readSharePreferenceMap,
                true,
            )
        }
    }


    private fun execShellHandler(
        editFragment: EditFragment,
        execCmdAfterTrimButtonEditExecVariant: String,
        innerExecCmd: String,
    ){
        if(
            innerExecCmd.isEmpty()
        ) return
        val substituteSettingVariableList =
            CommandClickVariables.substituteVariableListFromHolder(
                editFragment.currentScriptContentsList,
                editFragment.settingSectionStart,
                editFragment.settingSectionEnd,
            )
        val shellExecEnv = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.SHELL_EXEC_ENV
        ) ?: CommandClickScriptVariable.SHELL_EXEC_ENV_DEFAULT_VALUE
        when(shellExecEnv){
            SettingVariableSelects.ShellExecEnvSelects.UBUNTU.name
            -> execUbuntuShellHandler(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
                substituteSettingVariableList
            )
            SettingVariableSelects.ShellExecEnvSelects.TERMUX.name
            -> execShellScriptByTermux(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
                innerExecCmd,
            )
        }
    }

    private fun execUbuntuShellHandler(
        editFragment: EditFragment,
        execCmdAfterTrimButtonEditExecVariant: String,
        substituteSettingVariableList: List<String>?
    ){

        val ubuntuExecMode = CommandClickVariables.substituteCmdClickVariable(
            substituteSettingVariableList,
            CommandClickScriptVariable.UBUNTU_EXEC_MODE
        ) ?: CommandClickScriptVariable.UBUNTU_EXEC_MODE_DEFAULT_VALUE
        when(ubuntuExecMode){
            SettingVariableSelects.UbuntuExecModeSelects.background.name
            -> execShellScriptByBackground(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
            )
            SettingVariableSelects.UbuntuExecModeSelects.foreground.name
            -> execShellScriptByForeground(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
            )
        }
    }

    private fun execShellScriptByForeground(
        editFragment: EditFragment,
        execCmdAfterTrimButtonEditExecVariant: String,
    ){
        val cmdclickTempButtonExecShellPath =
            makeUbuntuButtonExecTempShellPath(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
            )
        val foregroundCmdIntent = Intent()
        foregroundCmdIntent.action = BroadCastIntentSchemeUbuntu.FOREGROUND_CMD_START.action
        foregroundCmdIntent.putExtra(
            UbuntuServerIntentExtra.foregroundShellPath.schema,
            cmdclickTempButtonExecShellPath
        )
        foregroundCmdIntent.putExtra(
            UbuntuServerIntentExtra.foregroundArgsTabSepaStr.schema,
            String()
        )
        foregroundCmdIntent.putExtra(
            UbuntuServerIntentExtra.foregroundTimeout.schema,
            "2000"
        )
        editFragment.activity?.sendBroadcast(foregroundCmdIntent)
    }
    private fun execShellScriptByBackground(
        editFragment: EditFragment,
        execCmdAfterTrimButtonEditExecVariant: String,
    ){
        val cmdclickTempButtonExecShellPath =
            makeUbuntuButtonExecTempShellPath(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
            )
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val backgroundCmdIntent = Intent()
        backgroundCmdIntent.action = BroadCastIntentSchemeUbuntu.BACKGROUND_CMD_START.action
        backgroundCmdIntent.putExtra(
            UbuntuServerIntentExtra.backgroundShellPath.schema,
            cmdclickTempButtonExecShellPath
        )
        backgroundCmdIntent.putExtra(
            UbuntuServerIntentExtra.backgroundArgsTabSepaStr.schema,
            String()
        )
        backgroundCmdIntent.putExtra(
            UbuntuServerIntentExtra.backgroundMonitorFileName.schema,
            terminalViewModel.currentMonitorFileName
        )
        editFragment.activity?.sendBroadcast(backgroundCmdIntent)
    }

    private fun makeUbuntuButtonExecTempShellPath(
        editFragment: EditFragment,
        execCmdAfterTrimButtonEditExecVariant: String,
    ): String {
        val readSharePreffernceMap = editFragment.readSharePreferenceMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val fannelDirName = CcPathTool.makeFannelDirName(
            currentScriptName
        )
        val fannelDirPath = "${currentAppDirPath}/$fannelDirName"
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val outputMonitorPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
        val execCmd = if(
            execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null")
            || execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null 2>&1")
        ) "${execCmdAfterTrimButtonEditExecVariant};"
        else "$execCmdAfterTrimButtonEditExecVariant >> \"${outputMonitorPath}\""
        val tempBtnShellDirPath = "$fannelDirPath/temp_btn_shell"
        FileSystems.writeFile(
            tempBtnShellDirPath,
            cmdclickTempButtonExecShellName,
            execCmd
                .replace(Regex("^${bashb}"), "bash")
                .replace(Regex("^${bashf}"), "bash")
        )
        return "${tempBtnShellDirPath}/$cmdclickTempButtonExecShellName"
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
                    currentScriptName
                )
            }.let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableMap,
                    currentAppDirPath,
                    currentScriptName
                )
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
        buttonEventArgs: ButtonEventArgs,
        execCmdReplaceBlankList: List<String>,
    ){
        val setFOptionMap = getSetFOptionMap(
            execCmdReplaceBlankList
        ) ?: return
        val settingArg = setFOptionMap.get(SET_F_OPTION_MAP_KEY.type)
            ?: return
        when(settingArg){
            SettingCmdArgs.ListAdd.name -> {
                val listConSlSpiOptionsStr = getFromSetVariableValueByIndex(
                    buttonEventArgs.currentSetVariableValue,
                    0
                )
                execListAddForSetting(
                    buttonEventArgs.editFragment,
                    buttonEventArgs.insertEditText,
                    buttonEventArgs.editFragment.readSharePreferenceMap,
                    listConSlSpiOptionsStr,
                    setFOptionMap
                )
            }
            else -> {}
        }
    }

    private fun saveListContents(
        editFragment: EditFragment,
        currentButtonTag: String?
    ){
        if(
            currentButtonTag.isNullOrEmpty()
        ) return
        val saveTagsKey = ListContentsSelectSpinnerViewProducer.ListContentsEditKey.saveTags.name
        val listContentsMap = editFragment.listConSelectBoxMapList.firstOrNull {
            it?.get(saveTagsKey) == currentButtonTag
        }
        if(
            listContentsMap.isNullOrEmpty()
        ) return
        val saveTargetListFilePath =
            listContentsMap.get(
                ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
            )
        if(
            saveTargetListFilePath.isNullOrEmpty()
        ) return
        val saveValName =
            listContentsMap.get(
                ListContentsSelectSpinnerViewProducer.ListContentsEditKey.saveValName.name
            )
        if(
            saveValName.isNullOrEmpty()
        ) return
        val saveFilterShellPath =
            listContentsMap.get(
                ListContentsSelectSpinnerViewProducer.ListContentsEditKey.saveFilterShellPath.name
            )
        val filterSaveValue = when(saveFilterShellPath.isNullOrEmpty()) {
            true -> return
            else -> makeShellConForListConSBFilter(
                    editFragment,
                    saveFilterShellPath,
                    saveValName,
                )
        }
        if(
            filterSaveValue.isNullOrEmpty()
        ) return
        ListContentsSelectBoxTool.updateListFileCon(
            saveTargetListFilePath,
            filterSaveValue
        )
    }

    private fun makeShellConForListConSBFilter(
        editFragment: EditFragment,
        saveFilterShellPath: String,
        saveValName: String,
    ): String? {
        val saveTextCon = "\${CMDCLICK_TEXT_CONTENTS}"
        val saveFilterShellPathObj = File(saveFilterShellPath)
        val shellParentDirPath = saveFilterShellPathObj.parent
            ?: return null
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            editFragment.readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentAppFannelPath = SharePreferenceMethod.getReadSharePreffernceMap(
            editFragment.readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_name,
        )
        val saveValue = EditVariableName.getText(
            editFragment,
            saveValName
        )
        val shellCon = ReadText(
            shellParentDirPath,
            saveFilterShellPathObj.name
        ).readText().replace(
            saveTextCon,
            saveValue
        ).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                editFragment.setReplaceVariableMap,
                currentAppDirPath,
                currentAppFannelPath
            )
        }
        return editFragment.busyboxExecutor?.getCmdOutput(shellCon)
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
        val currentScriptName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
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
        val isJsMacro = JsPathForEditButton.JsPathMacroForEditButton.values().map{
            it.name
        }.contains(jsFilePath)
        if(
            !File(jsFilePath).isFile
            && !isJsMacro
        ) return
        keyboardHide(
            editFragment,
            buttonMap
        )

        terminalViewModel.jsArguments = makeArgs(execCmdReplaceBlankList)
        when(isJsMacro){
            true -> JsPathForEditButton.jsPathMacroHandler(
                editFragment,
                jsFilePath,
            )
            else -> ExecJsScriptInEdit.exec(
                editFragment,
                jsFilePath,
            )
        }
    }

    private fun keyboardHide(
        editFragment: EditFragment,
        buttonMap: Map<String, String>?,
    ){
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
    }

    private fun simpleJsExecutor(
        buttonEventArgs: ButtonEventArgs,
    ){
        val currentEditTextConMark = "\${CURRENT_VAL_VALUE}"
        val editFragment = buttonEventArgs.editParameters.currentFragment
        if(editFragment !is EditFragment) return
        val currentVariableName = buttonEventArgs.currentVariableName
        val currentEditTextCon = currentVariableName?.let {
            EditVariableName.getText(editFragment, it)
        } ?: String()
        val buttonMap = buttonEventArgs.buttonMap
        val oneLineJsCon = buttonMap?.get(
            ButtonEditKey.oneLineJs.name
        )
        if(
            oneLineJsCon.isNullOrEmpty()
        ) return
        keyboardHide(
            editFragment,
            buttonMap
        )
        ExecJsScriptInEdit.execJsConForEdit(
            editFragment,
            oneLineJsCon.replace(
                currentEditTextConMark,
                currentEditTextCon
            )
        )
    }

    private fun makeArgs(
        execCmdReplaceBlankList: List<String>,
    ): String {
        return if(execCmdReplaceBlankList.size > 1){
            execCmdReplaceBlankList
                .slice(2..execCmdReplaceBlankList.size-1)
                .map{
                    QuoteTool.trimBothEdgeQuote(
                        it.replace(blankString, " ")
                    )
                }.joinToString("\t")
        } else String()
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
        ) == true.toString()
    }


    private fun getDisableKeyboardHidden(
        buttonMap: Map<String, String>?
    ): Boolean {
        return buttonMap?.get(
            ButtonEditKey.disableKeyboardHidden.name
        ) == true.toString()
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
        textSize,
        onBorder,
        disableKeyboardHidden,
        tag,
        oneLineJs,
    }

    enum class OnBoarderKeyForButton {
        OFF
    }

    object SET_F_OPTION_MAP_KEY {
        val type = "type"
        enum class ListAdd {
            dirPath,
            suffix,
            howFull,
        }
    }

    private data class ButtonEventArgs(
        val insertButton: Button,
        val editFragment: EditFragment,
        val insertEditText: EditText,
        val currentVariableName: String?,
        val scriptFileSaver: ScriptFileSaver,
        val editParameters: EditParameters,
        val buttonMap: Map<String, String>?,
        val currentSetVariableValue: String?
        )
}