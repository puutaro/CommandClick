package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.*
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.*
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.proccess.edit.lib.ScriptContentsLister
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*

class FormJsDialog(
    private val terminalFragment: TerminalFragment
) {

    private var returnValue = String()

    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

    private val exitTextStartId = 90000

    private val withSpinnerView = WithSpinnerView()
    private val withEditableSpinnerView = WithEditableSpinnerView()

    private val withFileSelectEditableSpinnerView = WithFileSelectEditableSpinnerView()

    private val withEditableListContentsSelectSpinnerView = WithEditableListContentsSelectSpinnerView()

    private val withInDeCremenView = WithInDeCremenView()


    private val languageType =
        LanguageTypeSelects.JAVA_SCRIPT

    private val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_START
    ) as String
    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.Companion.HolderTypeName.SETTING_SEC_END
    ) as String

    private val commandSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.Companion.HolderTypeName.CMD_SEC_START
    ) as String
    private val commandSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.Companion.HolderTypeName.CMD_SEC_END
    ) as String


    fun create(
        title: String,
        formSettingVariables: String,
        formCommandVariables: String
    ): String {
        terminalViewModel.onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    formSettingVariables,
                    formCommandVariables
                )
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (!terminalViewModel.onDialog) break
                }
            }
        }
        return returnValue
    }


    private fun execCreate(
        title: String,
        formSettingVariables: String,
        formCommandVariables: String
    ) {
        val virtualJsContentsList =  makeVirtualJsContentsList(
            formSettingVariables,
            formCommandVariables
        ) ?: return

        val recordNumToMapNameValueInCommandHolder =
            RecordNumToMapNameValueInHolder.parse(
                virtualJsContentsList,
                commandSectionStart,
                commandSectionEnd,
            )
        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                virtualJsContentsList,
                settingSectionStart,
                settingSectionEnd,
            )

        val setVariableTypeList = SetVariableTyper.makeSetVariableTypeList(
            recordNumToMapNameValueInSettingHolder
        )

        val recordNumToSetVariableMaps = SetVariableTyper.makeRecordNumToSetVariableMaps(
            setVariableTypeList,
            recordNumToMapNameValueInCommandHolder
        )

        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMap(
            recordNumToMapNameValueInSettingHolder
        )

        val scrollView = ScrollView(context)
        val linearLayoutForScrollViewParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        scrollView.layoutParams = linearLayoutForScrollViewParam

        val linearLayout = LinearLayout(context)
        linearLayout.orientation =  LinearLayout.VERTICAL
        linearLayout.weightSum = 1F
        val linearLayoutParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutParam.marginStart = 20
        linearLayoutParam.marginEnd = 20
        linearLayout.layoutParams = linearLayoutParam

        val virtualReadPreffrenceMap = mapOf(
            SharePrefferenceSetting.current_app_dir.name
                to terminalFragment.currentAppDirPath
        )
        val editParameters = EditParameters(
            context,
            virtualJsContentsList,
            recordNumToMapNameValueInCommandHolder,
            virtualReadPreffrenceMap,
            setReplaceVariableMap,
            true
        )

        execFormPartsAdd(
            editParameters,
            recordNumToSetVariableMaps,
            exitTextStartId,
            linearLayout
        )

        scrollView.addView(linearLayout)

        terminalViewModel.onDialog = true
        returnValue = String()

        val alertDialog = AlertDialog.Builder(
            context
        )
            .setTitle(title)
            .setView(scrollView)
            .setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                val updateVirtualJsContentsList = if(
                    recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
                ) virtualJsContentsList
                else {
                    ScriptContentsLister(
                        linearLayout
                    ).update(
                        recordNumToMapNameValueInCommandHolder,
                        virtualJsContentsList,
                        exitTextStartId
                    )
                }
                terminalViewModel.onDialog = false
                val dialogReturnStringListSource = CommandClickVariables.substituteVariableListFromHolder(
                    updateVirtualJsContentsList,
                    commandSectionStart,
                    commandSectionEnd
                )
                returnValue =
                    dialogReturnStringListSource?.slice(
                        1 until dialogReturnStringListSource.size-1
                    )?.joinToString("\n") ?: String()
            })
            .setNegativeButton("NO", DialogInterface.OnClickListener{ dialog, which ->
                terminalViewModel.onDialog = false
                returnValue = String()
            })
            .show()

        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })
        alertDialog.window?.setGravity(Gravity.BOTTOM)
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            context?.getColor(android.R.color.black) as Int
        )
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            context.getColor(android.R.color.black)
        )

        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })

    }


    private fun makeVirtualJsContentsList(
        formSettingVariables: String,
        formCommandVariables: String
    ): List<String>? {
        val setVariableTypeSource =
            formSettingVariables
                .split("\t")
                .map{
                    "${CommandClickScriptVariable.SET_VARIABLE_TYPE}=\"${it}\""
                }.joinToString("\n")
        val settingSectionContents =
            "${settingSectionStart}\n${setVariableTypeSource}\n${settingSectionEnd}"
        val commandSectionContents =
            "${commandSectionStart}\t${formCommandVariables}\t${commandSectionEnd}"
                .replace("\t", "\n")
        return "\n\n${settingSectionContents}\n\n\n${commandSectionContents}\n".split("\n")
    }

    private fun execFormPartsAdd(
        editParameters: EditParameters,
        recordNumToSetVariableMaps: Map<Int, Map<String,String>?>?,
        editTextStartId: Int,
        linearLayout: LinearLayout
    ){
        val recordNumToNameToValueInHolderSize = editParameters.recordNumToMapNameValueInCommandHolder?.size ?: return
        (1..recordNumToNameToValueInHolderSize).forEach { seedNum ->
            val linearParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            val currentOrder = seedNum - 1
            val currentRecordNumToMapNameValueInHolder = editParameters.recordNumToMapNameValueInCommandHolder.entries.elementAt(
                currentOrder
            )
            val currentRecordNumToNameToValueInHolder =
                currentRecordNumToMapNameValueInHolder.value
            val insertTextView = TextView(context)
            val currentVariableName = currentRecordNumToNameToValueInHolder?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            )
            val currentVariableValue = currentRecordNumToNameToValueInHolder?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
            )
            val currentId = editTextStartId + currentOrder
            insertTextView.text = currentVariableName
            linearParams.weight = 1F
            insertTextView.layoutParams = linearParams
            linearLayout.addView(insertTextView)
            val insertEditText = EditText(context)
            insertEditText.tag = currentVariableName
            insertEditText.id = currentId
            insertEditText.setSelectAllOnFocus(true)
            val currentRecordNum =
                currentRecordNumToMapNameValueInHolder.key
            editParameters.setVariableMap = recordNumToSetVariableMaps?.get(
                currentRecordNum
            )
            editParameters.currentId = currentId
            editParameters.currentVariableValue = currentVariableValue
            when(
                editParameters.setVariableMap?.get(
                    SetVariableTypeColumn.VARIABLE_TYPE.name
                )
            ) {
                EditTextSupportViewName.CHECK_BOX.str -> {
                    val innerLinearLayout = withSpinnerView.create(
                        insertEditText,
                        editParameters
                    )
                    linearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_CHECK_BOX.str -> {
                    val innerLinearLayout = withEditableSpinnerView.create(
                        insertEditText,
                        editParameters
                    )
                    linearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_FILE_CHECK_BOX.str -> {
                    val innerLinearLayout = withFileSelectEditableSpinnerView.create(
                        insertEditText,
                        editParameters
                    )
                    linearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_LIST_CONTENTS_CHECK_BOX.str -> {
                    val innerLinearLayout = withEditableListContentsSelectSpinnerView.create(
                        insertEditText,
                        editParameters
                    )
                    linearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.NUM_INDE_CREMENTER.str -> {
                    val innerLinearLayout = withInDeCremenView.create(
                        insertEditText,
                        editParameters
                    )
                    linearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.PASSWORD.str -> {
                    val insertingEditText = execInsertEditText(
                        insertEditText,
                        linearParams,
                        currentVariableValue,
                        EditTextType.PASSWORD
                    )
                    linearLayout.addView(insertingEditText)
                }
                else -> {
                    val insertingEditText = execInsertEditText(
                        insertEditText,
                        linearParams,
                        currentVariableValue,
                    )
                    linearLayout.addView(insertingEditText)
                }
            }
        }
    }

    private fun execInsertEditText(
        insertEditText: EditText,
        linearParams: LinearLayout.LayoutParams,
        currentVariableValue: String?,
        textType: EditTextType = EditTextType.PLAIN
    ): EditText {
        insertEditText.setText(currentVariableValue)
        insertEditText.layoutParams = linearParams
        when(textType){
            EditTextType.READ_ONLY -> insertEditText.setEnabled(false)
            EditTextType.PASSWORD -> insertEditText.inputType = (
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
                    )
            else ->  insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        }
        return insertEditText
    }
}