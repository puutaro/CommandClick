package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.R
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.*
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ButtonViewHowActive
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.edit_text_support_view.*
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.*
import com.puutaro.commandclick.proccess.ShellFileDescription


class EditTextProducerForEdit(
    editFragment: EditFragment,
    readSharePreffernceMap: Map<String, String>,
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

    private val setVariableTypeList = makeSetVariableTypeList(
        recordNumToMapNameValueInSettingHolder
    )

    private val recordNumToSetVariableMaps = makeRecordNumToSetVariableMaps(
        setVariableTypeList,
        recordNumToMapNameValueInCommandHolder
    )

    private val withEditTextWithButton = WithEditTextWithButton(
        editFragment,
        readSharePreffernceMap,
        currentShellContentsList,
        recordNumToMapNameValueInCommandHolder
    )


    private val withSpinnerView = WithSpinnerView(
        editFragment.context
    )

    private val withSpinnerWithButtonView = WithSpinnerWithButtonView(
        editFragment,
        readSharePreffernceMap,
        currentShellContentsList,
        recordNumToMapNameValueInCommandHolder
    )

    private val withEditableSpinnerView = WithEditableSpinnerView(
        editFragment.context
    )

    private val withEditableSpinnerWithButtonView = WithEditableSpinnerWithButtonView(
        editFragment,
        readSharePreffernceMap,
        currentShellContentsList,
        recordNumToMapNameValueInCommandHolder
    )


    private val withDirOrFileChooseView = WithDirOrFileChooseView(
        editFragment
    )

    private val withDirOrFileChooseViewWithButtonView = WithDirOrFileChooseViewWithButtonView(
        editFragment,
        readSharePreffernceMap,
        currentShellContentsList,
        recordNumToMapNameValueInCommandHolder
    )

    private val withInDeCremenView = WithInDeCremenView(
        editFragment
    )

    private val withInDeCremenViewWithButtonView = WithInDeCremenViewWithButtonView(
        editFragment,
        readSharePreffernceMap,
        currentShellContentsList,
        recordNumToMapNameValueInCommandHolder
    )

    private val withColorPickerView = WithColorPickerView(
        editFragment
    )

    private val withButtonView = WithButtonView(
        editFragment,
        readSharePreffernceMap,
        currentShellContentsList,
        recordNumToMapNameValueInCommandHolder
    )

    private val withDatePickerView = WithDatePickerView(
        editFragment,
    )

    private val withColorPickerWithButtonView = WithColorPickerWithButtonView(
        editFragment,
        readSharePreffernceMap,
        currentShellContentsList,
        recordNumToMapNameValueInCommandHolder
    )


    fun adds(
        onSettingEdit: Boolean = false
    ) {
        if(onSettingEdit){
            val setVariableListForSettingHolder =
                CommandClickShellScript.setVariableForSettingHolder
            val recordNumToSetVariableMapsForSettingHolder = makeRecordNumToSetVariableMaps(
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
                    currentShellContentsList
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
                currentShellContentsList
            )
        )
    }

    internal fun execAdd(
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
            val setVariableMap = recordNumToSetVariableMaps?.get(
                currentRecordNum
            )
            when(
                setVariableMap?.get(
                    SetVariableTypeColumn.VARIABLE_TYPE.name
                )
            ) {
                EditTextSupportViewName.EDITABLE_BUTTON.str -> {
                    val innerLinearLayout = withEditTextWithButton.create(
                        currentId,
                        currentVariableValue,
                        insertTextView,
                        insertEditText,
                        setVariableMap,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.CHECK_BOX.str -> {
                    val innerLinearLayout = withSpinnerView.create(
                        currentId,
                        currentVariableValue,
                        insertEditText,
                        setVariableMap
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.CHECK_BOX_BUTTON.str -> {
                    val innerLinearLayout = withSpinnerWithButtonView.create(
                        currentId,
                        currentVariableValue,
                        insertTextView,
                        insertEditText,
                        setVariableMap
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_CHECK_BOX.str -> {
                    val innerLinearLayout = withEditableSpinnerView.create(
                        currentId,
                        currentVariableValue,
                        insertEditText,
                        setVariableMap
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.EDITABLE_CHECK_BOX_BUTTON.str -> {
                    val innerLinearLayout = withEditableSpinnerWithButtonView.create(
                        currentId,
                        currentVariableValue,
                        insertTextView,
                        insertEditText,
                        setVariableMap
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.MDIRECTORY_PICKER.str,
                EditTextSupportViewName.DIRECTORY_PICKER.str -> {
                    val innerLinearLayout = withDirOrFileChooseView.create(
                        insertEditText,
                        currentVariableValue,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.DIRECTORY_PICKER_BUTTON.str -> {
                    val innerLinearLayout = withDirOrFileChooseViewWithButtonView.create(
                        currentId,
                        currentVariableValue,
                        insertTextView,
                        insertEditText,
                        setVariableMap,
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.FILE_PICKER.str,
                EditTextSupportViewName.MFILE_PICKER.str -> {
                    val innerLinearLayout = withDirOrFileChooseView.create(
                        insertEditText,
                        currentVariableValue,
                        false
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.FILE_PICKER_BUTTON.str -> {
                    val innerLinearLayout = withDirOrFileChooseViewWithButtonView.create(
                        currentId,
                        currentVariableValue,
                        insertTextView,
                        insertEditText,
                        setVariableMap,
                        false
                    )
                    binding.editLinearLayout.addView(innerLinearLayout)
                }
                EditTextSupportViewName.NUM_INDE_CREMENTER.str -> {
                    val innerLayout = withInDeCremenView.createNumInDeCrementer(
                        currentVariableValue,
                        insertEditText,
                        setVariableMap
                    )
                    binding.editLinearLayout.addView(innerLayout)
                }
                EditTextSupportViewName.NUM_INDE_CREMENTER_BUTTON.str -> {
                    val innerLayout = withInDeCremenViewWithButtonView.create(
                        currentId,
                        insertTextView,
                        currentVariableValue,
                        insertEditText,
                        setVariableMap
                    )
                    binding.editLinearLayout.addView(innerLayout)
                }
                EditTextSupportViewName.BUTTON.str,
                EditTextSupportViewName.FBUTTON.str-> {
                    val innerLayout = withButtonView.create(
                        currentId,
                        currentVariableValue,
                        insertTextView,
                        insertEditText,
                        setVariableMap
                    )
                    binding.editLinearLayout.addView(innerLayout)
                }
                EditTextSupportViewName.DATE.str -> {
                    val insertingEditText = withDatePickerView.create(
                        insertEditText,
                        currentVariableValue,
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                EditTextSupportViewName.COLOR.str -> {
                    val insertingEditText = withColorPickerView.create(
                        insertEditText,
                        currentVariableValue,
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                EditTextSupportViewName.COLOR_BUTTON.str -> {
                    val insertingEditText = withColorPickerWithButtonView.create(
                        currentId,
                        currentVariableValue,
                        insertTextView,
                        insertEditText,
                        setVariableMap
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                EditTextSupportViewName.READ_ONLY_EDIT_TEXT.str -> {
                    val insertingEditText = execInsertEditText(
                        insertEditText,
                        currentId,
                        currentVariableValue,
                        EditTextType.READ_ONLY
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                EditTextSupportViewName.PASSWORD.str -> {
                    val insertingEditText = execInsertEditText(
                        insertEditText,
                        currentId,
                        currentVariableValue,
                        EditTextType.PASSWORD
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
                else -> {
                    val insertingEditText = execInsertEditText(
                        insertEditText,
                        currentId,
                        currentVariableValue,
                    )
                    binding.editLinearLayout.addView(insertingEditText)
                }
            }
        }
    }

    private fun execInsertEditText(
        insertEditText: EditText,
        currentId: Int,
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


internal enum class EditTextType {
    PASSWORD,
    PLAIN,
    READ_ONLY,
}

internal fun makeDescriptionButton(
    context: Context?,
    currentShellContentsList: List<String>
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
        ShellFileDescription.show(
            innerButtonView.context,
            currentShellContentsList
        )
    }
    return descriptionButton
}

internal fun makeRecordNumToSetVariableMaps(
    setVariableTypeList: List<String>?,
    recordNumToMapNameValueInCommandHolder: Map<Int, Map<String,String>?>?
): Map<Int, Map<String, String>>? {
    if(setVariableTypeList == null) return null
    val usedRecordNumSet = mutableSetOf<Int>()
    val setVariableTypeListLength = setVariableTypeList.size -1
    if(setVariableTypeListLength < 0) return null
    return (0..setVariableTypeListLength).map {
        val currentFetchSetVariableType = setVariableTypeList[it]
        val currentFetchSetVariableTypeLength = currentFetchSetVariableType.length
        val equalIndex = currentFetchSetVariableType.indexOf('=')
        if(equalIndex == -1) {
            return null
        }
        val variableNameAddType = currentFetchSetVariableType.substring(
            0, equalIndex
        )
        val variableNameAddTypeLength = variableNameAddType.length;
        val colonIndex = variableNameAddType.indexOf(':')
        if(colonIndex == -1) {
            return null
        }
        val variableName = variableNameAddType.substring(
            0, colonIndex
        )
        val variableType = variableNameAddType.substring(
            colonIndex+1, variableNameAddTypeLength
        )
        val variableTypeValue = currentFetchSetVariableType.substring(
            equalIndex + 1, currentFetchSetVariableTypeLength
        )
        val hitRecordNumList = recordNumToMapNameValueInCommandHolder?.filterValues {
                keyValueMap ->
            keyValueMap?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            ) == variableName
        }?.keys?.toList()
        val aliveHitRecordNumList = hitRecordNumList?.filter {
            !usedRecordNumSet.contains(it)
        }
        val aliveHitRecordNumFirst = aliveHitRecordNumList?.firstOrNull() ?: -1
        usedRecordNumSet.add(aliveHitRecordNumFirst)
        aliveHitRecordNumFirst to mapOf(
                    SetVariableTypeColumn.VARIABLE_NAME.name
                            to variableName,
                    SetVariableTypeColumn.VARIABLE_TYPE.name
                            to variableType,
                    SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
                            to variableTypeValue,
                )
    }.toMap()
}


internal fun makeSetVariableTypeList(
    recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?
): List<String>? {
    return recordNumToMapNameValueInSettingHolder?.filter {
            entry ->
        entry.value?.get(
            RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
        ) == CommandClickShellScript.SET_VARIABLE_TYPE
    }?.map {
            entry ->
        val entryValue = entry.value
        val setTargetVariableValueBeforeTrim = entryValue?.get(
            RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
        )
        if(setTargetVariableValueBeforeTrim?.indexOf('"') == 0){
            setTargetVariableValueBeforeTrim.trim('"')
        } else if(setTargetVariableValueBeforeTrim?.indexOf('\'') == 0){
            setTargetVariableValueBeforeTrim.trim('\'')
        } else {
            setTargetVariableValueBeforeTrim
        } ?: String()
    }?.joinToString(",")
        ?.split(',')
        ?.filter { it.isNotEmpty() }
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
