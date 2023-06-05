package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.common.variable.edit.EditTextType
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn.*
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithEditableFileSelectSpinnerView
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithEditableListContentsSelectSpinnerView
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithEditableSpinnerView
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithInDeCremenView
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithSpinnerView
import com.puutaro.commandclick.proccess.edit.lib.ScriptContentsLister
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class EditTextForListIndex(
    private val editFragment: EditFragment
) {

    private var returnValue = String()

    private val context = editFragment.context
    private val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()

    private val exitTextStartId = 110000

    private val withSpinnerView = WithSpinnerView()
    private val withEditableSpinnerView = WithEditableSpinnerView()

    private val withEditableFileSelectSpinnerView = WithEditableFileSelectSpinnerView()

    private val withEditableListContentsSelectSpinnerView =
        WithEditableListContentsSelectSpinnerView()

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
        parentDirPath: String,
        selectedItem: String,
        onSetting: String,
    ) {
        val scriptContents = ReadText(
            parentDirPath,
            selectedItem,
        ).textToList()
        terminalViewModel.onDialog = true
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    parentDirPath,
                    selectedItem,
                    scriptContents,
                    onSetting
                )
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (!terminalViewModel.onDialog) break
                }
            }
        }
//        return returnValue
    }


    private fun execCreate(
        title: String,
        parentDirPath: String,
        selectedItem: String,
        scriptContentsList: List<String>,
        onSetting: String
    ) {

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
                    to parentDirPath
        )

        val recordNumToMapNameValueInCommandHolder =
            RecordNumToMapNameValueInHolder.parse(
                scriptContentsList,
                commandSectionStart,
                commandSectionEnd,
            )
        val recordNumToMapNameValueInSettingHolder =
            RecordNumToMapNameValueInHolder.parse(
                scriptContentsList,
                settingSectionStart,
                settingSectionEnd,
            )

        val recordNumToSetVariableMaps = if(
            onSetting.isEmpty()
        ) {
            val setVariableForCmdHolder = SetVariableTyper.makeSetVariableTypeList(
                recordNumToMapNameValueInSettingHolder
            )
            SetVariableTyper.makeRecordNumToSetVariableMaps(
                setVariableForCmdHolder,
                recordNumToMapNameValueInCommandHolder
            )
        } else {
            SetVariableTyper.makeRecordNumToSetVariableMaps(
                CommandClickScriptVariable.setVariableForSettingHolder,
                recordNumToMapNameValueInSettingHolder
            )
        }

        val setReplaceVariableMap = if(
            onSetting.isEmpty()
        ) {
            SetReplaceVariabler.makeSetReplaceVariableMap(
                recordNumToMapNameValueInSettingHolder
            )
        } else {
            mapOf()
        }


        val editParameters = if(
            onSetting.isEmpty()
        ) {
            EditParameters(
                editFragment,
                scriptContentsList,
                recordNumToMapNameValueInCommandHolder,
                virtualReadPreffrenceMap,
                setReplaceVariableMap,
                true
            )
        } else {
            EditParameters(
                editFragment,
                scriptContentsList,
                recordNumToMapNameValueInSettingHolder,
                virtualReadPreffrenceMap,
                setReplaceVariableMap,
                true
            )
        }

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
                val recordNumToMapNameValueInHolder = if(
                    onSetting.isEmpty()
                ) recordNumToMapNameValueInCommandHolder
                else recordNumToMapNameValueInSettingHolder
                val updateVirtualJsContentsList = if(
                    recordNumToMapNameValueInHolder.isNullOrEmpty()
                ) scriptContentsList
                else {
                    ScriptContentsLister(
                        linearLayout
                    ).update(
                        recordNumToMapNameValueInHolder,
                        scriptContentsList,
                        exitTextStartId
                    )
                }
                FileSystems.writeFile(
                    parentDirPath,
                    selectedItem,
                    updateVirtualJsContentsList.joinToString("\n")
                )
                val updateScriptFileName = CommandClickVariables.substituteCmdClickVariable(
                    updateVirtualJsContentsList,
                    CommandClickScriptVariable.SCRIPT_FILE_NAME
                )
                if(
                    updateScriptFileName == selectedItem
                ) {
                    terminalViewModel.onDialog = false
                    return@OnClickListener
                }
                FileSystems.copyFile(
                    "${parentDirPath}/${selectedItem}",
                    "${parentDirPath}/${updateScriptFileName}",
                )
                FileSystems.removeFiles(
                    parentDirPath,
                    selectedItem,
                )
                terminalViewModel.onDialog = false
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


    private fun execFormPartsAdd(
        editParameters: EditParameters,
        recordNumToSetVariableMaps: Map<Int, Map<String,String>?>?,
        editTextStartId: Int,
        linearLayout: LinearLayout
    ){
        val recordNumToNameToValueInHolderSize =
            editParameters.recordNumToMapNameValueInCommandHolder?.size ?: return
        (1..recordNumToNameToValueInHolderSize).forEach { seedNum ->
            val linearParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            val currentOrder = seedNum - 1
            val currentRecordNumToMapNameValueInHolder =
                editParameters.recordNumToMapNameValueInCommandHolder.entries.elementAt(
                    currentOrder
                )
            val currentRecordNumToNameToValueInHolder =
                currentRecordNumToMapNameValueInHolder.value
            val insertTextView = TextView(context)
            val currentVariableName = currentRecordNumToNameToValueInHolder?.get(
                VARIABLE_NAME.name
            )
            val currentVariableValue = currentRecordNumToNameToValueInHolder?.get(
                VARIABLE_VALUE.name
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
                EditTextSupportViewName.EDITABLE_FILE_SELECT_CHECK_BOX.str -> {
                    val innerLinearLayout = withEditableFileSelectSpinnerView.create(
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
                EditTextSupportViewName.READ_ONLY_EDIT_TEXT.str -> {
                    val insertingEditText = execInsertEditText(
                        insertEditText,
                        linearParams,
                        currentVariableValue,
                        EditTextType.READ_ONLY
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
            EditTextType.READ_ONLY -> {
                insertEditText.setEnabled(false)
                insertEditText.focusable = View.NOT_FOCUSABLE
            }
            EditTextType.PASSWORD -> insertEditText.inputType = (
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
                    )
            else ->  insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        }
        return insertEditText
    }
}
