package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.lib

import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
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
            isInsertTextViewVisible: Boolean = false
        ): Button {

            val context = editFragment.context
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
                if(execCmdEditable.isNullOrEmpty()) return@setOnClickListener

                val cmdPrefixEntrySource = currentRecordNumToSetVariableMap.get(
                    SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
                )
                val cmdPrefix = makeCmdPrefix(cmdPrefixEntrySource)

                val innerExecCmd = (
                        "$cmdPrefix " +
                                BothEdgeQuote.trim(
                                    execCmdEditable.toString()
                                )
                        )
                    .trim(';')
                    .replace(Regex("  *"), " ")
                    .replace("$0", "${currentAppDirPath}/${currentShellName}")
                val backStackPrefix = SettingVariableSelects.Companion.ButtonEditExecVarantSelects.BackStack.name
                val onEditExecuteOnce = innerExecCmd.startsWith(backStackPrefix)
                val execCmdAfterTrimButtonEditExecVariant = if(
                    onEditExecuteOnce
                ) innerExecCmd.removePrefix(backStackPrefix)
                else innerExecCmd
                val execCmd = if(
                    execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null")
                    || execCmdAfterTrimButtonEditExecVariant.endsWith("> /dev/null 2>&1")
                ) "${execCmdAfterTrimButtonEditExecVariant};"
                else "${execCmdAfterTrimButtonEditExecVariant} >> \"${outputPath}\""
                ExecBashScriptIntent.ToTermux(
                    editFragment.runShell,
                    context,
                    execCmd,
                    true
                )
                if(
                    !onEditExecuteOnce
                ) return@setOnClickListener
                val listener = context as? EditFragment.onToolBarButtonClickListenerForEditFragment
                listener?.onToolBarButtonClickForEditFragment(
                    editFragment.tag,
                    ToolbarButtonBariantForEdit.OK,
                    readSharePreffernceMap,
                    true,
                )
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
            return if(
                cmdPrefixEntrySource.contains(setVariableSetSeparator) == true
            ) {
                cmdPrefixEntrySource.split(setVariableSetSeparator).let {
                    val cmdPrefixEntry = it
                        .slice(1..it.size - 1)
                        .joinToString(setVariableSetSeparator)
                    BothEdgeQuote.trim(cmdPrefixEntry)
                }
            } else BothEdgeQuote.trim(cmdPrefixEntrySource)
        }
    }
}