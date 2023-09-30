package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.AutoCompleteAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class PromptJsDialog(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var returnValue = String()
    private var promptDialogObj: Dialog? = null
    private val suggestPrefix = "suggest"
    private val suggestDirName = "${suggestPrefix}Text"
    private val suggestTxtSuffix = ".txt"
    private val currentAppDirPath = terminalFragment.currentAppDirPath
    private val currentScriptName = terminalFragment.currentScriptName
    private val fannelDirName = currentScriptName
        .removeSuffix(UsePath.JS_FILE_SUFFIX)
        .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
            "Dir"
    private val fannelDirPath = "${currentAppDirPath}/${fannelDirName}"
    private val suggestDirPath = "${fannelDirPath}/${suggestDirName}"

    fun create(
        title: String,
        message: String,
        variableName: String,
    ): String {
        terminalViewModel.onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        title,
                        message,
                        variableName
                    )
                } catch (e: Exception){
                    Log.e(this.javaClass.name, e.toString())
                }
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (
                        !terminalViewModel.onDialog
                    ) break
                }
            }
        }
        return returnValue
    }


    private fun execCreate(
        title: String,
        message: String,
        variableName: String,
    ) {
        val context = context
            ?: return

        promptDialogObj = Dialog(
            context,
            R.style.BottomSheetDialogTheme
        )

        promptDialogObj?.setContentView(
            R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialogObj?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_title
            )
        if(
            title.isNotEmpty()
        ) promptTitleTextView?.text = title
        else promptTitleTextView?.isVisible = false

        val promptMessageTextView =
            promptDialogObj?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        if(
            message.isNotEmpty()
        ) promptMessageTextView?.text = message
        else promptMessageTextView?.isVisible = false
        val promptEditText =
            promptDialogObj?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            )
        promptEditText?.requestFocus()
        setSuggestEditText(
            promptEditText,
            variableName,
        )
        val promptCancelButton =
            promptDialogObj?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            returnValue = String()
            promptDialogObj?.dismiss()
            terminalViewModel.onDialog = false
        }
        val promptOkButtonView =
            promptDialogObj?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            val inputEditable = promptEditText?.text
            if(
                inputEditable.isNullOrEmpty()
            ) {
                returnValue = String()
                promptDialogObj?.dismiss()
                terminalViewModel.onDialog = false
                return@setOnClickListener
            }
            else returnValue = inputEditable.toString()
            registerToSuggest(variableName)
            promptDialogObj?.dismiss()
            terminalViewModel.onDialog = false
        }
        promptDialogObj?.setOnCancelListener {
            returnValue = String()
            promptDialogObj?.dismiss()
            terminalViewModel.onDialog = false
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

    private fun setSuggestEditText(
        promptEditText: AutoCompleteTextView?,
        variableName: String,
    ){
        if(promptEditText == null) return
        if(variableName.isEmpty()) return
        promptEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!promptEditText.hasFocus()) return
                makeSuggest(
                    promptEditText,
                    variableName,
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun makeSuggest(
        promptEditText: AutoCompleteTextView?,
        variableName: String,
    ){
        if(promptEditText == null) return
        if(context == null) return
        if(
            currentScriptName.isEmpty()
            || currentScriptName == CommandClickScriptVariable.EMPTY_STRING
        ) return
        val prefixUpperVariableName = variableName.replaceFirstChar { it.uppercase() }
        val suggestTxtName = "${suggestPrefix}${prefixUpperVariableName}${suggestTxtSuffix}"
        FileSystems.createDirs(
            suggestDirPath
        )
        val currentText = promptEditText.text
            ?: return
        val suggestList = ReadText(
            suggestDirPath,
            suggestTxtName
        ).textToList().filter {
            it.contains(currentText)
        }
        if(
            suggestList.isEmpty()
        ) return
        val suggestAdapter = AutoCompleteAdapter(
            context,
            android.R.layout.simple_list_item_1,
            ReadText(
                suggestDirPath,
                suggestTxtName
            ).textToList()
        )
        promptEditText.threshold = 0
        promptEditText.setAdapter(
            suggestAdapter
        )
    }
    private fun registerToSuggest(
        variableName: String,
    ){
        val prefixUpperVariableName = variableName.replaceFirstChar { it.uppercase() }
        val suggestTxtName = "${suggestPrefix}${prefixUpperVariableName}${suggestTxtSuffix}"
        FileSystems.createDirs(
            suggestDirPath
        )
        val updateSuggestList = listOf(returnValue) + ReadText(
            suggestDirPath,
            suggestTxtName
        ).textToList().filter {
            returnValue != it
        }.distinct().take(200)
        FileSystems.writeFile(
            suggestDirPath,
            suggestTxtName,
            updateSuggestList.joinToString("\n")
        )
    }
}
