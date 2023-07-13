package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.RecordNumToMapNameValueInHolderColumn.*
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.common.variable.edit.TypeVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithEditComponentForListIndex
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
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
import kotlinx.coroutines.withContext


class FormDialogForListIndexOrButton(
    private val editFragment: EditFragment
) {

    private var returnValue = String()
    private val variableTypeDefineListForMiniEdit
            = TypeVariable.variableTypeDefineListForMiniEdit
    private val context = editFragment.context
    private val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()

    private val exitTextStartId = 110000

    private val withEditComponentForListIndex = WithEditComponentForListIndex()

    private val languageType =
        LanguageTypeSelects.JAVA_SCRIPT

    private val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String
    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String

    private val commandSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
    ) as String
    private val commandSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
    ) as String


    fun create(
        title: String,
        parentDirPath: String,
        selectedScriptName: String,
        onSetting: String,
    ) {
        val scriptContents = ReadText(
            parentDirPath,
            selectedScriptName,
        ).textToList()
        terminalViewModel.onDialog = true
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    parentDirPath,
                    selectedScriptName,
                    scriptContents,
                    onSetting
                )
            }
            withContext(Dispatchers.IO) {
                for(i in 1..6000) {
                    delay(100)
                    if (!terminalViewModel.onDialog) break
                }
            }
        }
    }


    private fun execCreate(
        title: String,
        parentDirPath: String,
        selectedScriptName: String,
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
                    to parentDirPath,
            SharePrefferenceSetting.current_script_file_name.name
                    to selectedScriptName
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
                true,
            )
        val fannelDirName = selectedScriptName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
        val recordNumToSetVariableMaps = if(
            onSetting.isEmpty()
        ) {
            val setVariableForCmdHolder = SetVariableTyper.makeSetVariableTypeList(
                recordNumToMapNameValueInSettingHolder,
                parentDirPath,
                fannelDirName,
                selectedScriptName,
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
                recordNumToMapNameValueInSettingHolder,
                parentDirPath,
                fannelDirName,
                selectedScriptName,
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
                true,
                emptyList()
            )
        } else {
            val hideSettingVariableList = ListSettingVariableListMaker.make(
                CommandClickScriptVariable.HIDE_SETTING_VARIABLES,
                parentDirPath,
                selectedScriptName,
                fannelDirName,
                scriptContentsList,
                settingSectionStart,
                settingSectionEnd,
            )
            EditParameters(
                editFragment,
                scriptContentsList,
                recordNumToMapNameValueInSettingHolder,
                virtualReadPreffrenceMap,
                setReplaceVariableMap,
                true,
                hideSettingVariableList
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
                    selectedScriptName,
                    updateVirtualJsContentsList.joinToString("\n")
                )
                val updateScriptFileName = CommandClickVariables.substituteCmdClickVariable(
                    updateVirtualJsContentsList,
                    CommandClickScriptVariable.SCRIPT_FILE_NAME
                )
                if(
                    updateScriptFileName == selectedScriptName
                ) {
                    terminalViewModel.onDialog = false
                    return@OnClickListener
                }
                FileSystems.copyFile(
                    "${parentDirPath}/${selectedScriptName}",
                    "${parentDirPath}/${updateScriptFileName}",
                )
                FileSystems.removeFiles(
                    parentDirPath,
                    selectedScriptName,
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
            val currentRecordNum =
                currentRecordNumToMapNameValueInHolder.key

            editParameters.currentId = currentId
            editParameters.currentVariableName = currentVariableName
            editParameters.currentVariableValue = currentVariableValue
            editParameters.setVariableMap = recordNumToSetVariableMaps?.get(
                currentRecordNum
            )
            val variableTypeList = editParameters.setVariableMap?.get(
                SetVariableTypeColumn.VARIABLE_TYPE.name
            )?.split(":")?.filter {
                variableTypeDefineListForMiniEdit.contains(it)
            } ?: emptyList()
            editParameters.variableTypeList = variableTypeList

            val horizontalLinearLayout = withEditComponentForListIndex.insert(
                insertTextView,
                editParameters,
            )
            linearLayout.addView(horizontalLinearLayout)
        }
    }
}
