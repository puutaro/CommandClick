package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.R
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.*
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.*
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ButtonViewHowActive
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.*
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.*
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.util.SharePreffrenceMethod


class EditTextProducerForEdit(
    editFragment: EditFragment,
    private val currentShellContentsList: List<String>,
    private val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String,String>?>?,
    private val recordNumToMapNameValueInSettingHolder: Map<Int, Map<String,String>?>?,
    private val enableCmdEdit: Boolean
) {
    private val binding = editFragment.binding
    private val context = editFragment.context
    private val linearParams = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    )


    private val buttonViewHowActive = ButtonViewHowActive(
        binding,
        editFragment
    )
    private val validateErrEditTextNumberSet = mutableSetOf<Int>()

    private val setVariableTypeList = SetVariableTyper.makeSetVariableTypeList(
        recordNumToMapNameValueInSettingHolder
    )

    private val recordNumToSetVariableMaps = SetVariableTyper.makeRecordNumToSetVariableMaps(
        setVariableTypeList,
        recordNumToMapNameValueInCommandHolder
    )
    private val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMap(
        recordNumToMapNameValueInSettingHolder
    )

    private val readSharePreffernceMap = editFragment.readSharePreffernceMap

    private val editParameters = EditParameters(
        editFragment.context,
        currentShellContentsList,
        recordNumToMapNameValueInCommandHolder,
        readSharePreffernceMap,
        setReplaceVariableMap,
        false,
    )

    private val withEditTextWithButton = WithEditTextWithButton(
        editFragment,
    )


    private val withSpinnerView = WithSpinnerView()

    private val withSpinnerWithButtonView = WithSpinnerWithButtonView(
        editFragment,
    )

    private val withEditableSpinnerView = WithEditableSpinnerView()

    private val withEditableSpinnerWithButtonView = WithEditableSpinnerWithButtonView(
        editFragment,
    )


    private val withEditableFileSelectSpinnerWithButtonView = WithEditableFileSelectSpinnerWithButtonView(
        editFragment,
    )

    private val withEditableListContentsSelectSpinnerWithButton = WithEditableListContentsSelectSpinnerWithButton(
        editFragment,
    )

    private val withListContentsSelectSpinnerWithButton = WithListContentsSelectSpinnerWithButton(
        editFragment,
    )


    private val withDirOrFileChooseView = WithDirOrFileChooseView(
        editFragment
    )

    private val withFileSelectEditableSpinnerView = WithFileSelectEditableSpinnerView()

    private val withEditableListContentsSelectSpinnerView = WithEditableListContentsSelectSpinnerView()

    private val withListContentsSelectSpinnerView = WithListContentsSelectSpinnerView()

    private val withDirOrFileChooseViewWithButtonView = WithDirOrFileChooseViewWithButtonView(
        editFragment,
    )

    private val withInDeCremenView = WithInDeCremenView()

    private val withInDeCremenViewWithButtonView = WithInDeCremenViewWithButtonView(
        editFragment,
    )

    private val withColorPickerView = WithColorPickerView(
        editFragment
    )

    private val withButtonView = WithButtonView(
        editFragment,
    )

    private val withDatePickerView = WithDatePickerView()

    private val withTimePickerView = WithTimePickerView()

    private val withColorPickerWithButtonView = WithColorPickerWithButtonView(
        editFragment,
    )


    fun adds(
        onSettingEdit: Boolean = false
    ) {
        if(onSettingEdit){
            val setVariableListForSettingHolder =
                CommandClickShellScript.setVariableForSettingHolder
            val recordNumToSetVariableMapsForSettingHolder = SetVariableTyper.makeRecordNumToSetVariableMaps(
                setVariableListForSettingHolder,
                recordNumToMapNameValueInSettingHolder
            )

            execAdd(
                recordNumToMapNameValueInSettingHolder,
                recordNumToSetVariableMapsForSettingHolder,
                EditTextIdForEdit.SETTING_VARIABLE.id
            )
            binding.editLinearLayout.addView(
                makeDescriptionButton(
                    context,
                    currentShellContentsList,
                    readSharePreffernceMap
                )
            )
            return
        }
        execAdd(
            recordNumToMapNameValueInCommandHolder,
            recordNumToSetVariableMaps,
            EditTextIdForEdit.COMMAND_VARIABLE.id
        )
        binding.editLinearLayout.addView(
            makeDescriptionButton(
                context,
                currentShellContentsList,
                readSharePreffernceMap
            )
        )
    }

    private fun execAdd(
        recordNumToMapNameValueInHolder: Map<Int, Map<String,String>?>?,
        recordNumToSetVariableMaps: Map<Int, Map<String,String>?>?,
        editTextStartId: Int,
    ){
        val recordNumToNameToValueInHolderSize = recordNumToMapNameValueInHolder?.size ?: return
        (1..recordNumToNameToValueInHolderSize).forEach { seedNum ->
            val currentOrder = seedNum - 1
            val currentRecordNumToMapNameValueInHolder = recordNumToMapNameValueInHolder.entries.elementAt(
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
            binding.editLinearLayout.addView(insertTextView)
            val insertEditText = EditText(context)
            insertEditText.tag = currentVariableName
            insertEditText.id = currentId
            insertEditText.setSelectAllOnFocus(true)
            val currentRecordNum =
                currentRecordNumToMapNameValueInHolder.key
            editParameters.currentId = currentId
            editParameters.currentVariableValue = currentVariableValue
            editParameters.setVariableMap = recordNumToSetVariableMaps?.get(
                currentRecordNum
            )
            when(
                editParameters.setVariableMap?.get(
                    SetVariableTypeColumn.VARIABLE_TYPE.name
                )
            ) {
                EditTextSupportViewName.EDITABLE_BUTTON.str -> {
                    val innerLinearLayout = withEditTextWithButton.create(
                        insertTextView,
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.CHECK_BOX.str -> {
                    val innerLinearLayout = withSpinnerView.create(
                        insertEditText,
                        editParameters
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.CHECK_BOX_BUTTON.str -> {
                    val innerLinearLayout = withSpinnerWithButtonView.create(
                        insertTextView,
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_CHECK_BOX.str -> {
                    val innerLinearLayout = withEditableSpinnerView.create(
                        insertEditText,
                        editParameters
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_CHECK_BOX_BUTTON.str -> {
                    val innerLinearLayout = withEditableSpinnerWithButtonView.create(
                        insertTextView,
                        insertEditText,
                        editParameters
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_FILE_CHECK_BOX.str -> {
                    val innerLinearLayout = withFileSelectEditableSpinnerView.create(
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_FILE_CHECK_BOX_BUTTON.str -> {
                    val innerLinearLayout = withEditableFileSelectSpinnerWithButtonView.create(
                        insertTextView,
                        insertEditText,
                        editParameters
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.LIST_CONTENTS_CHECK_BOX_BUTTON.str -> {
                    val innerLinearLayout = withListContentsSelectSpinnerWithButton.create(
                        insertTextView,
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.LIST_CONTENTS_CHECK_BOX.str -> {
                    val innerLinearLayout = withListContentsSelectSpinnerView.create(
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_LIST_CONTENTS_CHECK_BOX_BUTTON.str -> {
                    val innerLinearLayout = withEditableListContentsSelectSpinnerWithButton.create(
                        insertTextView,
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_LIST_CONTENTS_CHECK_BOX.str -> {
                    val innerLinearLayout = withEditableListContentsSelectSpinnerView.create(
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.MDIRECTORY_PICKER.str,
                EditTextSupportViewName.DIRECTORY_PICKER.str -> {
                    val innerLinearLayout = withDirOrFileChooseView.create(
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.DIRECTORY_PICKER_BUTTON.str -> {
                    val innerLinearLayout = withDirOrFileChooseViewWithButtonView.create(
                        insertTextView,
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.FILE_PICKER.str,
                EditTextSupportViewName.MFILE_PICKER.str -> {
                    val innerLinearLayout = withDirOrFileChooseView.create(
                        insertEditText,
                        editParameters,
                        false
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.FILE_PICKER_BUTTON.str -> {
                    val innerLinearLayout = withDirOrFileChooseViewWithButtonView.create(
                        insertTextView,
                        insertEditText,
                        editParameters,
                        false
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.NUM_INDE_CREMENTER.str -> {
                    val innerLayout = withInDeCremenView.create(
                        insertEditText,
                        editParameters
                    )
                    binding.editLinearLayout.addView(innerLayout)
                }
                EditTextSupportViewName.NUM_INDE_CREMENTER_BUTTON.str -> {
                    val innerLayout = withInDeCremenViewWithButtonView.create(
                        insertTextView,
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(innerLayout)
                }
                EditTextSupportViewName.BUTTON.str,
                EditTextSupportViewName.FBUTTON.str-> {
                    val innerLayout = withButtonView.create(
                        insertTextView,
                        insertEditText,
                        editParameters
                    )
                    binding.editLinearLayout.addView(innerLayout)
                }
                EditTextSupportViewName.DATE.str -> {
                    val insertingEditText = withDatePickerView.create(
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                EditTextSupportViewName.TIME.str -> {
                    val insertingEditText = withTimePickerView.create(
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                EditTextSupportViewName.COLOR.str -> {
                    val insertingEditText = withColorPickerView.create(
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                EditTextSupportViewName.COLOR_BUTTON.str -> {
                    val insertingEditText = withColorPickerWithButtonView.create(
                        insertTextView,
                        insertEditText,
                        editParameters
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                EditTextSupportViewName.READ_ONLY_EDIT_TEXT.str -> {
                    val insertingEditText = execInsertEditText(
                        insertEditText,
                        editParameters,
                        EditTextType.READ_ONLY
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                EditTextSupportViewName.PASSWORD.str -> {
                    val insertingEditText = execInsertEditText(
                        insertEditText,
                        editParameters,
                        EditTextType.PASSWORD
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                else -> {
                    val insertingEditText = execInsertEditText(
                        insertEditText,
                        editParameters,
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
            }
        }
    }

    fun execInsertEditText(
        insertEditText: EditText,
        editParameters: EditParameters,
        textType: EditTextType = EditTextType.PLAIN
    ): EditText {
        val currentId = editParameters.currentId
        val currentVariableValue = editParameters.currentVariableValue
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
        addTextChangeListenerForEditText(
            insertEditText,
            currentId
        )
        return insertEditText
    }

    private fun addTextChangeListenerForEditText(
        insertEditText: EditText,
        currentOrder: Int,
    ){
        insertEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(
                s: Editable?
            ) {}

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val currentText = insertEditText.text
                val includeBothQuoteInMiddle = checkMiddleText(currentText)
                val includeForbbidenStr = "\\" in currentText
                if (includeForbbidenStr ||
                    includeBothQuoteInMiddle
                ) {
                    insertEditText.setError("\\ or both quote is used")

                    buttonViewHowActive.buttonViewHowActive(
                        ToolbarButtonBariantForEdit.OK.str,
                        false
                    )
                    buttonViewHowActive.buttonViewHowActive(
                        ToolbarButtonBariantForEdit.EDIT.str,
                        false
                    )
                    validateErrEditTextNumberSet.add(currentOrder)
                    return
                }
                validateErrEditTextNumberSet.remove(currentOrder)
                if(validateErrEditTextNumberSet.size > 0) return
                buttonViewHowActive.buttonViewHowActive(
                    ToolbarButtonBariantForEdit.OK.str,
                    true
                )
                buttonViewHowActive.buttonViewHowActive(
                    ToolbarButtonBariantForEdit.EDIT.str,
                    enableCmdEdit
                )
            }
        })
    }
}

internal fun makeDescriptionButton(
    context: Context?,
    currentShellContentsList: List<String>,
    readSharePreffernceMap: Map<String, String>
): Button {
    val descriptionButton = Button(context)
    val buttonLabel = "Desctiption"
    descriptionButton.setText(buttonLabel)
    context?.let {
        descriptionButton.setTextColor(
            it.getColor(R.color.white)
        )
        descriptionButton.setBackgroundTintList(
            it.getColorStateList(com.puutaro.commandclick.R.color.black)
        )
    }
    descriptionButton.setOnClickListener {
            innerButtonView ->
        ScriptFileDescription.show(
            innerButtonView.context,
            currentShellContentsList,
            SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_script_file_name
            )
        )
    }
    return descriptionButton
}


internal fun checkMiddleText(
    currentText: Editable
): Boolean{
    val currentTextLength = currentText.length
    if(currentTextLength <= 2) return false
    val currentTextLengthMinus = currentText.length - 1
    val middleCurrentText = currentText.substring(1, currentTextLengthMinus)
    return (middleCurrentText.indexOf('\'') != -1
                && middleCurrentText.indexOf('"') != -1 )
}
