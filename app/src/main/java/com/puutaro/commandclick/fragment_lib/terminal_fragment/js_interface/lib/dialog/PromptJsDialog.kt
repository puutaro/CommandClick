package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.AutoCompleteAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File


class PromptJsDialog(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    private var returnValue = String()
    private var promptDialogObj: Dialog? = null
    private val suggestPrefix = "suggest"
    private val suggestDirName = "${suggestPrefix}Text"
    private val suggestTxtSuffix = ".txt"
    private val currentAppDirPath = terminalFragment.currentAppDirPath
    private val currentScriptName = terminalFragment.currentFannelName
    private val fannelDirName = CcPathTool.makeFannelDirName(
        currentScriptName
    )
    private val fannelDirPath = "${currentAppDirPath}/${fannelDirName}"
    private val suggestDirPath = "${fannelDirPath}/${suggestDirName}"
    private val mapSeparator = ','
    private val firstSeparator = '|'
    private val secondSeparator = '?'

    fun create(
        title: String,
        message: String,
        suggestOrDefoTxtVars: String,
    ): String {
        val promptMap = CmdClickMap.createMap(
            suggestOrDefoTxtVars,
            mapSeparator,
        ).toMap()
        val suggestMap = CmdClickMap.createMap(
            promptMap.get(PromptMapKey.suggest.name),
            firstSeparator
        ).toMap()

//            makeSuggestAndDefoTxtMap(suggestOrDefoTxtVars)
        val variableName = suggestMap.get(SuggestVars.variableName.name)
//        val prefixUpperVariableName = variableName?.replaceFirstChar { it.uppercase() }
        val suggestTxtName = makeSuggestTextFileName(
            variableName,
        )
//            "${suggestPrefix}${prefixUpperVariableName}${suggestTxtSuffix}"
        val mainSuggestList = ReadText(
            File(suggestDirPath, suggestTxtName).absolutePath
        ).textToList()
        val suggestSrcListEntry = makeExtraSuggestList(
            suggestMap.get(SuggestVars.concatFilePathList.name)?.let {
                QuoteTool.splitBySurroundedIgnore(
                    it,
                    secondSeparator
                )
            }
//            .split(secondSeparator)
        ) + mainSuggestList
        val suggestSrcList = suggestSrcListEntry.distinct()
        terminalViewModel.onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        title,
                        message,
                        promptMap,
                        variableName,
                        suggestSrcList,
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
        promptMap: Map<String, String>,
        variableName: String?,
        suggestSrcList: List<String>,
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

        val editTextMap = CmdClickMap.createMap(
            promptMap.get(PromptMapKey.editText.name),
            firstSeparator
        ).toMap()
        val promptEditText = EditTextMakerForPrompt.make(
            terminalFragment,
            promptDialogObj,
            editTextMap,
        )
//        val promptEditText =
//            promptDialogObj?.findViewById<AutoCompleteTextView>(
//                R.id.prompt_dialog_input
//            )
//
//        promptMap.get(
//            EditTextKey.default.name
//        )
//        promptEditText?.requestFocus()
        setSuggestEditText(
            promptEditText,
            suggestSrcList,
        )
        editTextKeyListener(
            promptEditText,
            variableName
        )
        val promptCancelButton =
            promptDialogObj?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            returnValue = String()
            promptDialogObj?.dismiss()
            promptDialogObj = null
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
                promptDialogObj = null
                terminalViewModel.onDialog = false
                return@setOnClickListener
            }
            else returnValue = inputEditable.toString()
            registerToSuggest(variableName)
            promptDialogObj?.dismiss()
            promptDialogObj = null
            terminalViewModel.onDialog = false
        }
        promptDialogObj?.setOnCancelListener {
            returnValue = String()
            promptDialogObj?.dismiss()
            promptDialogObj = null
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
        suggestSrcList: List<String>
    ){
        if(promptEditText == null) return
        if(suggestSrcList.isEmpty()) return
        promptEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!promptEditText.hasFocus()) return
                makeSuggest(
                    promptEditText,
                    suggestSrcList,
                )
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun editTextKeyListener(
        promptEditText: AutoCompleteTextView?,
        variableName: String?,
    ){
        promptEditText?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action != KeyEvent.ACTION_DOWN ||
                    keyCode != KeyEvent.KEYCODE_ENTER
                ) return false
                val currentInputEditable = promptEditText.text
                if(promptEditText.text.isNullOrEmpty()){
                    returnValue = String()
                    promptDialogObj?.dismiss()
                    promptDialogObj = null
                    terminalViewModel.onDialog = false
                    return false
                }
                returnValue = currentInputEditable.toString()
                registerToSuggest(
                    variableName,
                )
                promptDialogObj?.dismiss()
                promptDialogObj = null
                terminalViewModel.onDialog = false
                return false
            }
        })
    }


    private fun makeSuggest(
        promptEditText: AutoCompleteTextView?,
        suggestSrcList: List<String>
    ){
        if(promptEditText == null) return
        if(context == null) return
        if(
            currentScriptName.isEmpty()
            || currentScriptName == CommandClickScriptVariable.EMPTY_STRING
        ) return
        FileSystems.createDirs(
            suggestDirPath
        )
        val currentText = promptEditText.text
            ?: return
        val suggestList = suggestSrcList.filter {
            it.contains(currentText)
        }
        if(
            suggestList.isEmpty()
        ) return
        val suggestAdapter = AutoCompleteAdapter(
            context,
            android.R.layout.simple_list_item_1,
            suggestList
        )
        promptEditText.threshold = 0
        promptEditText.setAdapter(
            suggestAdapter
        )
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
        variableName: String?,
    ){
        val trimedReturnValue =
            returnValue.trim()
        if(
            variableName.isNullOrEmpty()
        ) return
//        val prefixUpperVariableName = variableName.replaceFirstChar { it.uppercase() }
        val suggestTxtName = makeSuggestTextFileName(
            variableName,
        )
//            "${suggestPrefix}${prefixUpperVariableName}${suggestTxtSuffix}"
        FileSystems.createDirs(
            suggestDirPath
        )
        val updateSuggestList =
            listOf(trimedReturnValue) +
                    makeNoEmptyList(
                        trimedReturnValue,
                        suggestTxtName,
                    ).filter {
                        trimedReturnValue != it
                    }.distinct().take(200)
        FileSystems.writeFile(
            File(
                suggestDirPath,
                suggestTxtName
            ).absolutePath,
            updateSuggestList.joinToString("\n")
        )
    }

//    private fun makeSuggestAndDefoTxtMap(
//        suggestVars: String,
//    ): Map<String, String> {
//        return CmdClickMap.createMap(
//            suggestVars,
//            firstSeparator
//        ).toMap()
//    }

    private fun makeNoEmptyList(
        trimedReturnValue: String,
        suggestTxtName: String,
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
}

private object EditTextMakerForPrompt {
    fun make(
        terminalFragment: TerminalFragment,
        promptDialogObj: Dialog?,
        editTextMap: Map<String, String>,
    ): AutoCompleteTextView? {
        val promptEditText =
            promptDialogObj?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            ) ?: return null
        val setTextSrc = editTextMap.get(
            EditTextKey.default.name
        )
        val setText = when(setTextSrc.isNullOrEmpty()){
            true -> makeTextByShell(
                terminalFragment,
                editTextMap
            )
            else -> setTextSrc
        }
        if(
            !setText.isNullOrEmpty()
        ){
            promptEditText.setText(setText)
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
            EditTextKey.fannelPath.name
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
        val readSharePreferenceMap = SharePrefTool.getReadSharePrefMap(
            terminalFragment,
            mainOrSubFannelPath
        )
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )

        val shellCon = editTextMap.get(
            EditTextKey.shellPath.name
        )?.let {
            EditSettingExtraArgsTool.makeShellCon(editTextMap)
        }?.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
            )
        } ?: return null
        val busyboxExecutor = BusyboxExecutor(
            context,
            UbuntuFiles(context),
        )
        val repValMap = editTextMap.get(
            EditTextKey.repValCon.name
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


private enum class PromptMapKey {
    suggest,
    editText,
}

private enum class EditTextKey {
    default,
    shellPath,
    fannelPath,
    repValCon,
}
private enum class SuggestVars {
    variableName,
    concatFilePathList
}