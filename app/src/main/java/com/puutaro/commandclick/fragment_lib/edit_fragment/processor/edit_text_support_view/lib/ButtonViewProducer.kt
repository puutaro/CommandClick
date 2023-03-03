package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.lib

import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ShellScriptSaver
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.SetVariableTypeColumn
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ButtonViewProducer {
    companion object {
        private val setVariableSetSeparator = "|"

        fun make (
            editFragment: EditFragment,
            readSharePreffernceMap: Map<String, String>,
            currentId: Int,
            insertTextView: TextView,
            insertEditText: EditText,
            currentRecordNumToSetVariableMap: Map<String,String>,
            weight: Float,
            currentShellContentsList: List<String>,
            recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
            isInsertTextViewVisible: Boolean = false
        ): Button {

            val context = editFragment.context
            val binding = editFragment.binding
            val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            )
            val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
            val outputPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
            val currentShellName = SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_shell_file_name
            )

            val shellScriptSaver = ShellScriptSaver(
                binding,
                editFragment,
                readSharePreffernceMap,
                true ,
            )

            val linearParamsForButton = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
            linearParamsForButton.weight = weight
            val insertButton = Button(context)
            insertButton.id = currentId + EditTextSupportViewId.BUTTON.id
            insertButton.tag = "button${currentId + EditTextSupportViewId.BUTTON.id}"
            val buttonLabel = if(isInsertTextViewVisible){
               "EXEC"
            } else insertTextView.text
            insertButton.setText(buttonLabel)
            insertTextView.isVisible = isInsertTextViewVisible

            insertButton.setOnClickListener {
                    innerButtonView ->
                val execCmdEditable = insertEditText.text

                val cmdPrefixEntrySource = currentRecordNumToSetVariableMap.get(
                    SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
                )
                val cmdPrefix = makeCmdPrefix(cmdPrefixEntrySource)
                if(
                    execCmdEditable.isNullOrEmpty()
                    && cmdPrefix.isEmpty()
                ) return@setOnClickListener

                val innerExecCmd = (
                        "$cmdPrefix " +
                                BothEdgeQuote.trim(
                                    execCmdEditable.toString()
                                )
                        )
                    .trim(';')
                    .replace(Regex("  *"), " ")
                    .replace("$0", "${currentAppDirPath}/${currentShellName}")
                val doubleColon = "::"
                val backStackMacro = doubleColon + SettingVariableSelects.Companion.ButtonEditExecVarantSelects.BackStack.name + doubleColon
                val termOutMacro = doubleColon + SettingVariableSelects.Companion.ButtonEditExecVarantSelects.TermOut.name + doubleColon

                val onEditExecuteOnce = innerExecCmd.contains(backStackMacro)
                val onTermOutMacro = innerExecCmd.contains(termOutMacro)
                val execCmdAfterTrimButtonEditExecVariant =
                    innerExecCmd
                        .replace(backStackMacro, "")
                        .replace(termOutMacro, "")
                        .replace(Regex("  *"), " ")
                        .replace(";;*", ";")
                val execCmd = if(
                    execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null")
                    || execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null 2>&1")
                ) "${execCmdAfterTrimButtonEditExecVariant};"
                else "$execCmdAfterTrimButtonEditExecVariant >> \"${outputPath}\""
                shellScriptSaver.save(
                    currentShellContentsList,
                    recordNumToMapNameValueInCommandHolder,
                )
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
                if(onTermOutMacro) {
                    terminalViewModel.onDisplayUpdate = true
                }
            }

            insertButton.layoutParams = linearParamsForButton
            return insertButton
        }

        private fun makeCmdPrefix(
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
    }
}