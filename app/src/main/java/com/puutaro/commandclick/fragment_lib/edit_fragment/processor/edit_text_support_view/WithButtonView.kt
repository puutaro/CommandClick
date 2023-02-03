package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view


import android.text.InputType
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.ExecTerminalDo
import com.puutaro.commandclick.util.Intent.ExecBashScriptIntent
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class WithButtonView(
    private val editFragment: EditFragment,
    readSharePreffernceMap: Map<String, String>,
) {
    private val context = editFragment.context
    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )
    val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
    private val outputPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"

    private val currentShellName = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_shell_file_name
    )
    fun create(
        currentId: Int,
        currentVariableValue: String?,
        insertTextView: TextView,
        insertEditText: EditText,
    ): LinearLayout {
        val horozontalLinearLayout = LinearLayout(context)
        horozontalLinearLayout.orientation = LinearLayout.HORIZONTAL
        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(currentVariableValue)
        linearParamsForEditTextTest.weight = 0.001F
        insertEditText.layoutParams = linearParamsForEditTextTest
        horozontalLinearLayout.addView(insertEditText)
        val linearParamsForButton = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForButton.weight = 2F
        val insertButton = Button(context)
        insertButton.id = currentId + EditTextSupportViewId.BUTTON.id
        insertButton.tag = "button${currentId + EditTextSupportViewId.BUTTON.id}"
        insertButton.setText(insertTextView.text)
        insertTextView.isVisible = false


        insertButton.setOnClickListener {
            innerButtonView ->
            val execCmdEditable = insertEditText.text
            if(execCmdEditable.isNullOrEmpty()) return@setOnClickListener
            ExecTerminalDo.execTerminalDo(
                editFragment,
                currentAppDirPath,
                currentShellName,
            )
            val innerExecCmd = execCmdEditable
                .trim('\'')
                .trim('"')
            val execCmd = "${innerExecCmd} >> \"${outputPath}\";"
            ExecBashScriptIntent.ToTermux(
                editFragment.runShell,
                context,
                execCmd,
                true
            )
        }

        insertButton.layoutParams = linearParamsForButton
        horozontalLinearLayout.addView(insertButton)
        return horozontalLinearLayout
    }
}