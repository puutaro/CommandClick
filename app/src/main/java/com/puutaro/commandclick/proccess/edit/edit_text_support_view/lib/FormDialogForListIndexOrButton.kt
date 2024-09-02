package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
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
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


object FormDialogForListIndexOrButton {

    private var formDialog: Dialog? = null
    private var returnValue = String()
    private val exitTextStartId = 110000

    private val variableTypeDefineListForMiniEdit = TypeVariable.variableTypeDefineListForMiniEdit

//    private val languageType =
//        LanguageTypeSelects.JAVA_SCRIPT

//    private val languageTypeToSectionHolderMap =
//        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
//    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//    ) as String
//    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//    ) as String
//
//    private val commandSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
//    ) as String
//    private val commandSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
//    ) as String
    val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
    val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END

    private val commandSectionStart =  CommandClickScriptVariable.CMD_SEC_START
    private val commandSectionEnd =  CommandClickScriptVariable.CMD_SEC_END


    fun create(
        editFragment: EditFragment,
        title: String,
        parentDirPath: String,
        selectedScriptName: String,
        onSetting: String,
    ) {
        val context = editFragment.context
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()

        val scriptContents = ReadText(
            File(
                parentDirPath,
                selectedScriptName
            ).absolutePath,
        ).textToList()
        terminalViewModel.onDialog = true
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main) {
                execCreate(
                    editFragment,
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
        editFragment: EditFragment,
        title: String,
        parentDirPath: String,
        selectedScriptName: String,
        scriptContentsList: List<String>,
        onSetting: String
    ) {
        val context = editFragment.context
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        if(
            context == null
        ) {
            terminalViewModel.onDialog = false
            returnValue = String()
            return
        }

        formDialog = Dialog(
            context
        )
        formDialog?.setContentView(
            com.puutaro.commandclick.R.layout.form_dialog_laytout
        )
        val confirmTitleTextView =
            formDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.form_dialog_title
            )
        if(
            title.isNotEmpty()
        ) confirmTitleTextView?.text = title
        else confirmTitleTextView?.isVisible = false
        val linearLayout =
            formDialog?.findViewById<LinearLayoutCompat>(
                com.puutaro.commandclick.R.id.form_dialog_contents_linear
            ) ?: return


        val virtualReadPreffrenceMap = mapOf(
//            FannelInfoSetting.current_app_dir.name
//                    to parentDirPath,
            FannelInfoSetting.current_fannel_name.name
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
        val recordNumToSetVariableMaps = if(
            onSetting.isEmpty()
        ) {
            val setVariableForCmdHolder = SetVariableTyper.makeSetVariableTypeList(
                recordNumToMapNameValueInSettingHolder,
//                parentDirPath,
                selectedScriptName,
                editFragment.setReplaceVariableMap
            )
            SetVariableTyper.makeRecordNumToSetVariableMaps(
                editFragment,
                setVariableForCmdHolder,
                recordNumToMapNameValueInCommandHolder,
                null,
                editFragment.busyboxExecutor,
            )
        } else {
            SetVariableTyper.makeRecordNumToSetVariableMaps(
                editFragment,
                CommandClickScriptVariable.setVariableForSettingHolder,
                recordNumToMapNameValueInSettingHolder,
                null,
                editFragment.busyboxExecutor,
            )
        }

        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            scriptContentsList,
            settingSectionStart,
            settingSectionEnd
        )
        val setReplaceVariableMap = if(
            onSetting.isEmpty()
        ) {
            SetReplaceVariabler.makeSetReplaceVariableMap(
                context,
                settingVariableList,
//                parentDirPath,
                selectedScriptName,
            )
        } else {
            mapOf()
        }

        val editParameters = if(
            onSetting.isEmpty()
        ) {
            EditParameters(
//                editFragment,
                scriptContentsList,
                recordNumToMapNameValueInCommandHolder,
                recordNumToMapNameValueInSettingHolder,
                virtualReadPreffrenceMap,
                setReplaceVariableMap,
                true,
                emptyList()
            )
        } else {
            val hideSettingVariableList = ListSettingVariableListMaker.makeFromSettingVariableList(
                CommandClickScriptVariable.HIDE_SETTING_VARIABLES,
                virtualReadPreffrenceMap,
                editFragment.setReplaceVariableMap,
                settingVariableList,
            )
            EditParameters(
//                editFragment,
                scriptContentsList,
                recordNumToMapNameValueInSettingHolder,
                null,
                virtualReadPreffrenceMap,
                setReplaceVariableMap,
                true,
                hideSettingVariableList
            )
        }

        execFormPartsAdd(
            editFragment,
            editParameters,
            recordNumToSetVariableMaps,
            exitTextStartId,
            linearLayout
        )
        terminalViewModel.onDialog = true
        returnValue = String()

        val confirmCancelButton =
            formDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.form_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            terminalViewModel.onDialog = false
            returnValue = String()
            formDialog?.dismiss()
            formDialog = null
        }
        val confirmOkButton =
            formDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.form_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            formDialog?.dismiss()
            formDialog = null
            val recordNumToMapNameValueInHolder = if(
                onSetting.isEmpty()
            ) recordNumToMapNameValueInCommandHolder
            else recordNumToMapNameValueInSettingHolder
            val updateVirtualJsContentsList = if(
                recordNumToMapNameValueInHolder.isNullOrEmpty()
            ) scriptContentsList
            else {
                ScriptContentsLister.update(
                    listOf(linearLayout),
                    recordNumToMapNameValueInHolder,
                    scriptContentsList,
                    exitTextStartId
                )
            }
            FileSystems.writeFile(
                File(
                    parentDirPath,
                    selectedScriptName
                ).absolutePath,
                updateVirtualJsContentsList.joinToString("\n")
            )
            terminalViewModel.onDialog = false
        }

        formDialog?.setOnCancelListener {
            terminalViewModel.onDialog = false
            returnValue = String()
            formDialog?.dismiss()
            formDialog = null
        }
        formDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        formDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        formDialog?.show()
    }


    private fun execFormPartsAdd(
        editFragment: EditFragment,
        editParameters: EditParameters,
        recordNumToSetVariableMaps: Map<Int, Map<String,String>?>?,
        editTextStartId: Int,
        linearLayout: LinearLayoutCompat
    ){
        val recordNumToNameToValueInHolderSize =
            editParameters.recordNumToMapNameValueInCommandHolder?.size ?: return
        (1..recordNumToNameToValueInHolderSize).forEach { seedNum ->
            val linearParams = LinearLayoutCompat.LayoutParams(
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
            val context = editFragment.context
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

            val horizontalLinearLayout = WithEditComponentForListIndex.insert(
                editFragment,
                insertTextView,
                editParameters,
            )
            linearLayout.addView(horizontalLinearLayout)
        }
    }
}
