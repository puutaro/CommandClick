package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.text.Editable
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


object ButtonViewProducer {
    private val setVariableSetSeparator = "|"
    private val jsFrag = "jsf"
    private val blankString = "cmdclickBlank"
    fun make (
        editFragment: EditFragment,
        insertTextView: TextView,
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float,
        isInsertTextViewVisible: Boolean = false
    ): Button {
        val context = editParameters.context
        val readSharePreffernceMap = editParameters.readSharePreffernceMap
        val currentId = editParameters.currentId
        val currentSetVariableMap = editParameters.setVariableMap
        val currentShellContentsList = editParameters.currentShellContentsList
        val recordNumToMapNameValueInCommandHolder = editParameters.recordNumToMapNameValueInCommandHolder
        val setReplaceVariableMap = editParameters.setReplaceVariableMap

        val binding = editFragment.binding
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val outputPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
        val currentScriptName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )

        val scriptFileSaver = ScriptFileSaver(
            binding,
            editFragment,
            readSharePreffernceMap,
            true ,
        )

        val cmdPrefixEntrySource = currentSetVariableMap?.get(
            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
        )
        val buttonVariables = makeButtonVariables(cmdPrefixEntrySource)

        val linearParamsForButton = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForButton.weight = weight
        val insertButton = Button(context)
        insertButton.id = currentId + EditTextSupportViewId.BUTTON.id
        insertButton.tag = "button${currentId + EditTextSupportViewId.BUTTON.id}"

        insertButton.text = makeButtonLabel(
            buttonVariables,
            isInsertTextViewVisible,
            insertTextView.text.toString(),
        )
        insertTextView.isVisible = isInsertTextViewVisible

        insertButton.setOnClickListener {
                innerButtonView ->
            scriptFileSaver.save(
                currentShellContentsList,
                recordNumToMapNameValueInCommandHolder,
            )
            val execCmdEditable = insertEditText.text

            val cmdPrefix = buttonVariables
                .split("!")
                .firstOrNull()
                ?: String()
            if(
                execCmdEditable.isNullOrEmpty()
                && cmdPrefix.isEmpty()
            ) return@setOnClickListener
            val currentScriptPath = "${currentAppDirPath}/${currentScriptName}"
            val innerExecCmd =  makeInnerExecCmd(
                cmdPrefix,
                execCmdEditable.toString(),
                currentScriptPath,
                setReplaceVariableMap,
            )
            val doubleColon = "::"
            val backStackMacro = doubleColon + SettingVariableSelects.Companion.ButtonEditExecVariantSelects.BackStack.name + doubleColon
            val termOutMacro = doubleColon + SettingVariableSelects.Companion.ButtonEditExecVariantSelects.TermOut.name + doubleColon
            val noJsTermOut = doubleColon + SettingVariableSelects.Companion.ButtonEditExecVariantSelects.NoJsTermOut.name + doubleColon
            val termLong = doubleColon + SettingVariableSelects.Companion.ButtonEditExecVariantSelects.TermLong.name + doubleColon

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


            if(
                BothEdgeQuote.trim(
                    execCmdReplaceBlankList.firstOrNull()
                ) == jsFrag
            ){
                execJsFileForButton(
                    editFragment,
                    terminalViewModel,
                    execCmdReplaceBlankList
                )
                return@setOnClickListener
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

        insertButton.layoutParams = linearParamsForButton
        return insertButton
    }

    private fun makeButtonVariables(
        cmdPrefixEntrySource: String?
    ): String {
        if(
            cmdPrefixEntrySource.isNullOrEmpty()
        ) return String()
        if(
            cmdPrefixEntrySource.contains(
                setVariableSetSeparator
            )
        ) return cmdPrefixEntrySource.split(setVariableSetSeparator).let {
                val cmdPrefixEntry = it
                    .slice(1 until it.size)
                    .joinToString(setVariableSetSeparator)
                BothEdgeQuote.trim(cmdPrefixEntry)
            }
        return BothEdgeQuote.trim(cmdPrefixEntrySource)
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
            .removeSuffix(CommandClickScriptVariable.JS_FILE_SUFFIX)
            .removeSuffix(CommandClickScriptVariable.SHELL_FILE_SUFFIX) +
                "Dir"
        val innerExecCmdSourceBeforeReplace =
            "$cmdPrefix " +
                BothEdgeQuote.trim(
                    execCmdEditableString
                )
        return innerExecCmdSourceBeforeReplace.trim(';')
            .replace(Regex("  *"), " ")
            .let {
                ScriptPreWordReplacer.replace(
                    it,
                    currentScriptPath,
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
                            currentScriptPath,
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

    private fun execJsFileForButton(
        editFragment: EditFragment,
        terminalViewModel: TerminalViewModel,
        execCmdReplaceBlankList: List<String>,
    ){
        terminalViewModel.jsArguments = String()
        val context = editFragment.context
        val jsFilePathIndex = 1
        val jsFilePath = BothEdgeQuote.trim(
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
        Keyboard.hiddenKeyboardForFragment(editFragment)
        terminalViewModel.jsArguments = if(execCmdReplaceBlankList.size > 1){
            execCmdReplaceBlankList
                .slice(2..execCmdReplaceBlankList.size-1)
                .map{
                    BothEdgeQuote.trim(
                        it.replace(blankString, " ")
                    )
                }.joinToString("\t")
        } else String()
        editFragment.jsExecuteJob?.cancel()
        editFragment.jsExecuteJob = CoroutineScope(Dispatchers.IO).launch {
            val onLaunchUrl = EnableTerminalWebView.check(
                editFragment,
                editFragment.context?.getString(
                    R.string.edit_execute_terminal_fragment
                )
            )
            if(!onLaunchUrl) return@launch
            withContext(Dispatchers.Main) {
                val listenerForWebLaunch = editFragment.context as? EditFragment.OnLaunchUrlByWebViewForEditListener
                listenerForWebLaunch?.onLaunchUrlByWebViewForEdit(
                    JavaScriptLoadUrl.make(
                        context,
                        jsFilePath,
                    ).toString()
                )
            }
        }
    }

    private fun makeButtonLabel(
        buttonVariables: String,
        isInsertTextViewVisible: Boolean,
        textViewLabel: String,
    ): String {
        val buttonLabelSource = buttonVariables.split("!")
            .getOrNull(1)
        return if(
            !buttonLabelSource.isNullOrEmpty()
        ) buttonLabelSource
        else if(isInsertTextViewVisible){
            "EXEC"
        } else textViewLabel
    }
}