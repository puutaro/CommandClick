package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.content.Intent
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingCmdArgs
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.ExecJsScriptInEdit
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.GridDialogForButton
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SetVariableTypeValue
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.button.JsPathForEditButton
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.proccess.edit.lib.EditVariableName
import com.puutaro.commandclick.proccess.edit.lib.ListContentsSaverByTag
import com.puutaro.commandclick.proccess.edit.lib.ListPathGetterForDragSort
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.tool_bar_button.JsActionHandler
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


object ButtonViewProducer {
    private const val setVariableSetSeparator = '|'
    private const val blankString = "cmdclickBlank"
    private const val buttoLabelThis = "this"
    private var consecutiveJob: Job? = null
    const val doubleColon = "::"
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
        val context = editFragment.context
        val currentId = editParameters.currentId

        val currentSetVariableValue = SetVariableTypeValue.makeByReplace(
            editParameters
        )

        val buttonMap = getButtonMap(
            editFragment,
            currentSetVariableValue,
            currentComponentIndex
        )
        val linearParamsForButton = LinearLayoutCompat.LayoutParams(
            0,
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
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
        val currentVariableName = editParameters.currentVariableName
        val buttonEventArgs = ButtonEventArgs(
            insertButton,
            editFragment,
            insertEditText,
            currentVariableName,
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
        val buttonMap = buttonEventArgs.buttonMap
        val currentButtonTag = buttonMap?.get(
            ButtonEditKey.tag.name
        )

        ScriptFileSaver.save(editFragment)
        ListContentsSaverByTag.save(
            editFragment,
            listOf(currentButtonTag)
        )
        val execCmdEditable = buttonEventArgs.insertEditText.text
        val isExecCmd = !buttonMap?.get(
            ButtonEditKey.cmd.name
        ).isNullOrEmpty()
        if(!isExecCmd) return
        val cmdPrefix = getCmdPrefix(
            buttonMap
        ).let{
            QuoteTool.splitBySurroundedIgnore(
                it,
                '?'
            )
        }.firstOrNull()
            ?: String()
        if(
            execCmdEditable.isNullOrEmpty()
            && cmdPrefix.isEmpty()
        ) return
        val innerExecCmd = makeInnerExecCmd(
            cmdPrefix,
            execCmdEditable.toString(),
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
//                .replace(backStackMacro, "")
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
        val buttonCmdType = ButtonCmdType.values().firstOrNull {
            it.prefix == firstCmdStr
        } ?: ButtonCmdType.jsCode
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsButton.txt").absolutePath,
//            listOf(
//                "execCmdAfterTrimButtonEditExecVariant: ${execCmdAfterTrimButtonEditExecVariant}",
//                "execCmdReplaceBlankList: ${execCmdReplaceBlankList}",
//                "buttonMap: ${buttonMap}",
//                "buttonCmdType: ${buttonCmdType.prefix}",
//            ).joinToString("\n\n")
//        )
        when(buttonCmdType){
            ButtonCmdType.jsActionFrag
            -> execJsActionForButton(
                editFragment,
                execCmdReplaceBlankList,
                buttonMap,
            )
            ButtonCmdType.jsActionConFrag
            -> execJsActionConForButton(
                editFragment,
                execCmdReplaceBlankList,
                buttonMap
            )
            ButtonCmdType.jsFrag
            -> execJsFileForButton(
                editFragment,
                terminalViewModel,
                execCmdReplaceBlankList,
                buttonMap
            )
            ButtonCmdType.settingFrag
            -> execSettingCmd(
                buttonEventArgs,
                execCmdReplaceBlankList,
            )
            ButtonCmdType.basht
            -> execShellScriptByTermux(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
//                innerExecCmd,
            )
            ButtonCmdType.bashb
            -> execShellScriptByBackground(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
            )
            ButtonCmdType.bashf
            -> execShellScriptByForeground(
                editFragment,
                execCmdAfterTrimButtonEditExecVariant,
            )
            ButtonCmdType.jsCode
            -> jsConExecutor(
                editFragment,
                buttonEventArgs,
                innerExecCmd,
            )
//                execShellHandler(
//                editFragment,
//                execCmdAfterTrimButtonEditExecVariant,
//                innerExecCmd,
//            )
        }
    }

    private fun execShellScriptByTermux(
        editFragment: EditFragment,
        execCmdAfterTrimButtonEditExecVariant: String,
//        innerExecCmd: String,
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
                Regex("^${ButtonCmdType.basht}"),
                "bash"
            ),
            true
        )
//        val onEditExecuteOnce = innerExecCmd.contains(backStackMacro)
//        if(onEditExecuteOnce) {
//            val listener =
//                context as? EditFragment.onToolBarButtonClickListenerForEditFragment
//            listener?.onToolBarButtonClickForEditFragment(
//                editFragment.tag,
//                ToolbarButtonBariantForEdit.OK,
//                editFragment.readSharePreferenceMap,
//                true,
//            )
//        }
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
            editFragment.settingFannelConList
//            CommandClickVariables.substituteVariableListFromHolder(
//                editFragment.currentFannelConList,
//                editFragment.settingSectionStart,
//                editFragment.settingSectionEnd,
//            )
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
//                innerExecCmd,
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
        val fannelInfoMap = editFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentScriptName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val fannelDirName = CcPathTool.makeFannelDirName(
            currentScriptName
        )
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val fannelDirPath = "${cmdclickDefaultAppDirPath}/$fannelDirName"
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val outputMonitorPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
        val execCmd = if(
            execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null")
            || execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null 2>&1")
        ) "${execCmdAfterTrimButtonEditExecVariant};"
        else "$execCmdAfterTrimButtonEditExecVariant >> \"${outputMonitorPath}\""
        val tempBtnShellDirPath = "$fannelDirPath/temp_btn_shell"
        FileSystems.writeFile(
            File(
                tempBtnShellDirPath,
                cmdclickTempButtonExecShellName
            ).absolutePath,
            execCmd
                .replace(Regex("^${ButtonCmdType.bashb}"), "bash")
                .replace(Regex("^${ButtonCmdType.bashf}"), "bash")
        )
        return "${tempBtnShellDirPath}/$cmdclickTempButtonExecShellName"
    }

    private fun makeInnerExecCmd(
        cmdPrefix: String,
        execCmdEditableString: String,
    ): String {
        val innerExecCmdSourceBeforeReplace =
            "$cmdPrefix " +
                QuoteTool.trimBothEdgeQuote(
                    execCmdEditableString
                )
        return innerExecCmdSourceBeforeReplace.trim(';')
            .replace(Regex("  *"), " ")
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
                    buttonEventArgs.currentVariableName,
                    listConSlSpiOptionsStr,
                    setFOptionMap
                )
            }
            else -> {}
        }
    }

    private fun execListAddForSetting(
        editFragment: EditFragment,
        currentVariableName: String?,
        listConSlSpiOptionsStr: String?,
        setFOptionMap: Map<String, String>
    ){
        val suffixList = setFOptionMap.get(
            SET_F_OPTION_MAP_KEY.ListAdd.suffix.name
        )?.let {
            QuoteTool.splitBySurroundedIgnore(
                it,
                '&'
            )
        }
            ?: emptyList()
        val addSourceDirPath = setFOptionMap.get(
            SET_F_OPTION_MAP_KEY.ListAdd.dirPath.name
        ) ?: return
        val isFull = !setFOptionMap.get(
            SET_F_OPTION_MAP_KEY.ListAdd.howFull.name
        ).isNullOrEmpty()
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val currentScriptName = FannelInfoTool.getCurrentFannelName(
            editFragment.fannelInfoMap
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
                ToastUtils.showShort("this script cannot register\n ${selectedScript}")
                return@launch
            }
            val listFilePath = getListPathForDragSort(
                editFragment,
                listConSlSpiOptionsStr,
                currentVariableName
            ) ?: return@launch
            val listFilePathOjb = File(listFilePath)
            val listDirPath = listFilePathOjb.parent
                ?: return@launch
            FileSystems.createDirs(listDirPath)
            if(
                ReadText(
                    listFilePath
                ).textToList().filter {
                    it == selectedScript
                }.isNotEmpty()
            ) {
                ToastUtils.showShort("this script already register\n ${selectedScript}")
                return@launch
            }
            withContext(Dispatchers.IO){
                val insertSelectedScript = if(
                    isFull
                ) "$addSourceDirPath/$selectedScript"
                else selectedScript
                val updateListCon = insertSelectedScript + "\n" + ReadText(
                    listFilePath
                ).readText()
                FileSystems.writeFile(
                    listFilePath,
                    updateListCon
                )
            }
        }
    }

    private fun getListPathForDragSort(
        editFragment: EditFragment,
        listConSlSpiOptionsStr: String?,
        currentVariableName: String?
    ): String? {
        val listPathKey = ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
        return listConSlSpiOptionsStr
            ?.let {
                QuoteTool.splitBySurroundedIgnore(
                    it,
                    '?'
                )
            }
            ?.filter {
                it.contains(listPathKey)
            }?.firstOrNull()
            ?.replace("${listPathKey}=", "")
            ?.let {
                QuoteTool.trimBothEdgeQuote(it)
            }?.let {
                ListPathGetterForDragSort.getByListPathSrc(
                    it,
                    editFragment,
                    currentVariableName ?: String(),
                )
            }
    }

    private fun execJsActionForButton(
        editFragment: EditFragment,
        execCmdReplaceBlankList: List<String>,
        buttonMap: Map<String, String>?
    ){
        val jsFilePathIndex = 1
        val jsActionFilePath = QuoteTool.trimBothEdgeQuote(
            execCmdReplaceBlankList.get(
                jsFilePathIndex
            ).replace(blankString, " ")
        )
        if(
            !File(jsActionFilePath).isFile
        ) return
        keyboardHide(
            editFragment,
            buttonMap
        )
        val setReplaceVariableMap =
            SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                editFragment.context,
                jsActionFilePath
            )
        JsActionHandler.handle(
            editFragment,
            editFragment.fannelInfoMap,
            String(),
            setReplaceVariableMap,
            ReadText(jsActionFilePath).readText()
        )
    }

    private fun execJsActionConForButton(
        editFragment: EditFragment,
        execCmdReplaceBlankList: List<String>,
        buttonMap: Map<String, String>?
    ){
        val jsFilePathIndex = 1
        val jsActionCon = QuoteTool.trimBothEdgeQuote(
            execCmdReplaceBlankList.get(
                jsFilePathIndex
            ).replace(blankString, " ")
        )
//        val isJsMacro = JsPathForEditButton.JsPathMacroForEditButton.values().map{
//            it.name
//        }.contains(jsFilePath)
        keyboardHide(
            editFragment,
            buttonMap
        )
//        terminalViewModel.jsArguments = makeArgs(execCmdReplaceBlankList)

        JsActionHandler.handle(
            editFragment,
            editFragment.fannelInfoMap,
            String(),
            editFragment.setReplaceVariableMap,
            jsActionCon
        )
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

    private fun jsConExecutor(
        editFragment: EditFragment,
        buttonEventArgs: ButtonEventArgs,
        jsConSrc: String,
    ){
        val currentEditTextConMark = "\${CURRENT_VAL_VALUE}"
        val currentVariableName = buttonEventArgs.currentVariableName
        val currentEditTextCon = currentVariableName?.let {
            EditVariableName.getText(editFragment, it)
        } ?: String()
        ExecJsScriptInEdit.execJsConForEdit(
            editFragment,
            jsConSrc.replace(
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
        editFragment: EditFragment,
        currentSetVariableValue: String?,
        currentComponentIndex: Int
    ): Map<String, String> {
        val fannelInfoMap = editFragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        return currentSetVariableValue?.let {
            if(
                it.contains(
                    setVariableSetSeparator
                )
            ) return@let QuoteTool.splitBySurroundedIgnore(
                it,
                setVariableSetSeparator
            )
                .getOrNull(currentComponentIndex).let {
                QuoteTool.trimBothEdgeQuote(it)
            }
            QuoteTool.trimBothEdgeQuote(it)
        }?.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                editFragment.setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName
            )
        }.let {
            CmdClickMap.createMap(
                it,
                '?'
            ).toMap()
        }
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
            ) return@let QuoteTool.splitBySurroundedIgnore(
                it,
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
        val editParameters: EditParameters,
        val buttonMap: Map<String, String>?,
        val currentSetVariableValue: String?
        )

    private enum class ButtonCmdType(
        val prefix: String,
    ){
        jsFrag("jsf"),
        jsActionFrag("jsa"),
        jsActionConFrag("jsac"),
        jsCode("jsCode"),
        settingFrag("setf"),
        basht("basht"),
        bashf("bashf"),
        bashb("bashb"),
    }
}
