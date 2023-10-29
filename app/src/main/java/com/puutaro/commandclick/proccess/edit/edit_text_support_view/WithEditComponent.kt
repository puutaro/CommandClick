package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.common.variable.edit.TypeVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ButtonViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ColorPickerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.DatePickerProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.DirOrFileChooseProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.DragSortListViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableListContentsMultiSelectGridViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableListContentsMultiSeletctSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableListContentsSelectGridViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableListContentsSelectOnlyImageGridViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectGridViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectOnlyImageGridViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.InDeCrementerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.MultiFileSelectGridViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.MultiSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.SpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.TimePickerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.VariableLabelAdder
import com.puutaro.commandclick.view_model.activity.EditViewModel

class WithEditComponent(
    private val editFragment: EditFragment,
    private val enableCmdEdit: Boolean
) {
    private val textAndLabelList = TypeVariable.textAndLabelList
    private val noIndexTypeList = TypeVariable.noIndexTypeList
    private val editViewModel: EditViewModel by editFragment.activityViewModels()

    fun insert(
        insertTextView: TextView,
        editParameters: EditParameters,
    ): LinearLayout {
        val context = editParameters.context
        val textLabelIndex = culcTextLabelMarkIndex(
            editParameters,
            textAndLabelList
        )
        VariableLabelAdder.add(
            insertTextView,
            editParameters,
            textLabelIndex
        )
        val variableTypeList = updateVariableTypeListByLabel(
            editParameters,
            textLabelIndex
        )
        editParameters.variableTypeList = variableTypeList
        editParameters.setVariableMap = updateSetVariableMapByLabelIndex(
                editParameters,
                textLabelIndex,
            )

        val editTextWeight = decideTextEditWeight(
            variableTypeList,
        )
        val otherComponentWeight = decideOtherComponentWeight(
            editTextWeight,
            variableTypeList,
        )
        val insertEditText = initEditText(
            editParameters,
            editTextWeight
        )
        val horizontalLinearLayout = makeHorizontalLayout(context)
        horizontalLinearLayout.addView(insertEditText)
        hideVariables(
            editFragment,
            editParameters,
            insertTextView,
            horizontalLinearLayout,
        )
        (variableTypeList.indices).forEach {
            val variableTypeListUntilCurrent =  variableTypeList.take(it + 1)
            val currentComponentIndex = variableTypeListUntilCurrent.filter {
                !noIndexTypeList.contains(it)
            }.size - 1
            when(variableTypeList[it]){
                EditTextSupportViewName.BUTTON.str -> {
                    val insertButton = ButtonViewProducer.make(
                        editFragment,
                        insertTextView,
                        insertEditText,
                        editParameters,
                        otherComponentWeight,
                        currentComponentIndex,
//                        isInsertTextViewVisible
                    )
                    horizontalLinearLayout.addView(insertButton)
                }
                EditTextSupportViewName.CHECK_BOX.str -> {
                    val insertSpinner = SpinnerViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertSpinner)
                }
                EditTextSupportViewName.EDITABLE_CHECK_BOX.str -> {
                    val insertSpinner = EditableSpinnerViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertSpinner)
                }
                EditTextSupportViewName.LIST_CONTENTS_SELECT_BOX.str -> {
                    val insertListConSpinner = ListContentsSelectSpinnerViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertListConSpinner)
                }
                EditTextSupportViewName.EDITABLE_LIST_CONTENTS_SELECT_BOX.str -> {
                    val insertListConSpinner = EditableListContentsSelectSpinnerViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertListConSpinner)
                }
                EditTextSupportViewName.GRID_BOX.str -> {
                    val insertGridSelectBox = EditableListContentsSelectGridViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertGridSelectBox)
                }
                EditTextSupportViewName.MULTI_GRID_BOX.str -> {
                    val insertGridSelectBox = EditableListContentsMultiSelectGridViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertGridSelectBox)
                }
                EditTextSupportViewName.ONlY_IMAGE_GRID_BOX.str -> {
                    val insertGridSelectBox = EditableListContentsSelectOnlyImageGridViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertGridSelectBox)
                }
                EditTextSupportViewName.MULTI_SELECT_BOX.str -> {
                    val multiSelectSpinner = MultiSelectSpinnerViewProducer.make(
                        insertTextView,
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(multiSelectSpinner)
                }
                EditTextSupportViewName.LIST_CONTENTS_MULTI_SELECT_BOX.str -> {
                    val listConMultiSelectSpinner = EditableListContentsMultiSeletctSpinnerViewProducer.make(
                        insertTextView,
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(listConMultiSelectSpinner)
                }
                EditTextSupportViewName.DRAG_SORT_SELECT_BOX.str -> {
                    val dragSortListView = DragSortListViewProducer.make(
                        editFragment,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight
                    )
                    horizontalLinearLayout.addView(dragSortListView)
                }
                EditTextSupportViewName.EDITABLE_FILE_SELECT_BOX.str -> {
                    val editableFileSelectSpinner = FileSelectSpinnerViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(editableFileSelectSpinner)
                }
                EditTextSupportViewName.FILE_SELECT_GRID_BOX.str -> {
                    val fileSelectGridSelectBox = FileSelectGridViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(fileSelectGridSelectBox)
                }
                EditTextSupportViewName.MULTI_FILE_SELECT_GRID_BOX.str -> {
                    val fileSelectGridSelectBox = MultiFileSelectGridViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(fileSelectGridSelectBox)
                }
                EditTextSupportViewName.FILE_SELECT_ONLY_IMAGE_GRID_BOX.str -> {
                    val fileSelectGridSelectBox = FileSelectOnlyImageGridViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(fileSelectGridSelectBox)
                }
                EditTextSupportViewName.NUM_INDE_CREMENTER.str -> {
                    val incButton = InDeCrementerViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                        true,
                    )
                    horizontalLinearLayout.addView(incButton)
                    val decButton = InDeCrementerViewProducer.make(
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                        false
                    )
                    horizontalLinearLayout.addView(decButton)
                }
                EditTextSupportViewName.FILE_PICKER.str -> {
                    val fileChooser = DirOrFileChooseProducer.make(
                        editFragment,
                        false,
                        insertEditText,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(fileChooser)
                }
                EditTextSupportViewName.DIRECTORY_PICKER.str -> {
                    val dirChooser = DirOrFileChooseProducer.make(
                        editFragment,
                        true,
                        insertEditText,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(dirChooser)
                }
                EditTextSupportViewName.COLOR.str -> {
                    val colorPicker = ColorPickerViewProducer.make(
                        editFragment,
                        insertEditText,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(colorPicker)
                }
                EditTextSupportViewName.DATE.str -> {
                    val datePicker = DatePickerProducer.make(
                        insertEditText,
                        editParameters,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(datePicker)
                }
                EditTextSupportViewName.TIME.str -> {
                    val timePicker = TimePickerViewProducer.make(
                        insertEditText,
                        editParameters,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(timePicker)
                }
                EditTextSupportViewName.READ_ONLY_EDIT_TEXT.str -> {
                    insertEditText.isEnabled = false
                }
                EditTextSupportViewName.PASSWORD.str -> {
                    insertEditText.inputType = (
                            InputType.TYPE_CLASS_TEXT or
                                InputType.TYPE_TEXT_VARIATION_PASSWORD
                        )
                }
                else -> {}
            }
        }
        return horizontalLinearLayout
    }


    private fun initEditText(
        editParameters: EditParameters,
        editTextWeight: Float
    ): EditText {
        val context = editParameters.context
        val currentId = editParameters.currentId
        val currentVariableValue = editParameters.currentVariableValue
        val currentVariableName = editParameters.currentVariableName

        val linearParamsForEditTextTest = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        val insertEditText = EditText(context)
        insertEditText.clearFocus()
        insertEditText.tag = currentVariableName
        insertEditText.id = currentId
//        insertEditText.setTextColor(context?.getColor(R.color.terminal_color) as Int)
        insertEditText.backgroundTintList = context?.getColorStateList(R.color.gray_out)
        editViewModel.variableNameToEditTextIdMap.put(
            currentVariableName as String,
            currentId
        )

        insertEditText.inputType = InputType.TYPE_CLASS_TEXT
        insertEditText.setText(currentVariableValue)
        insertEditText.setSelectAllOnFocus(true)
//        insertEditText.setTextColor(Color.parseColor("#FFFFFF"))
        linearParamsForEditTextTest.weight = editTextWeight
        insertEditText.layoutParams = linearParamsForEditTextTest
//        addTextChangeListenerForEditText(
//            insertEditText,
//            currentId
//        )
        return insertEditText
    }

    private  fun makeHorizontalLayout(
        context: Context?
    ):LinearLayout {
        val horizontalLinearLayout = LinearLayout(context)
        horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL
        horizontalLinearLayout.weightSum = 1F
        val linearParamsForHorizontal = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        horizontalLinearLayout.layoutParams = linearParamsForHorizontal
        return horizontalLinearLayout
    }

    private fun decideTextEditWeight(
        variableTypeList: List<String>,
    ): Float {
        val isTextEmphasis = variableTypeList.contains(
            EditTextSupportViewName.EDIT_TEXT_EMPHASIS.str
        )
        val textAndLabelOtherCompLength = makeTextAndLabelOtherCompLength(variableTypeList)
        if(
            textAndLabelOtherCompLength == 0
        ) return 1F
        if(
            !isTextEmphasis
        ) return 0F
        return when(true){
            (textAndLabelOtherCompLength >= 3) -> 0.45F
            (textAndLabelOtherCompLength == 2) -> 0.6F
            (textAndLabelOtherCompLength == 1) -> 0.7F
            else -> 1F
        }
    }

    private fun decideOtherComponentWeight(
        editTextWeight: Float,
        variableTypeList: List<String>,
    ): Float {
        val variableTypeIndexLength = makeVariableTypeIndexLength(
            variableTypeList
        )
        if(
            variableTypeIndexLength > 0
        ) return (1F - editTextWeight) / variableTypeIndexLength
        return  0F
    }

    private fun makeVariableTypeIndexLength(
        variableTypeList: List<String>
    ): Int {
        val otherCompLengthSource = variableTypeList.filter {
            !textAndLabelList.contains(it)
        }.size
        val isNumComponent = variableTypeList.contains(
            EditTextSupportViewName.NUM_INDE_CREMENTER.str
        )
        if (
            isNumComponent
        ) return otherCompLengthSource + 1
        return otherCompLengthSource
    }

    private fun makeTextAndLabelOtherCompLength(
        variableTypeList: List<String>
    ): Int {
        val textAndLabelOtherCompLength = variableTypeList.filter {
            !textAndLabelList.contains(it)
        }.size
        val isNumComponent = variableTypeList.contains(
            EditTextSupportViewName.NUM_INDE_CREMENTER.str
        )
        if (
            isNumComponent
        ) return textAndLabelOtherCompLength + 1
        return textAndLabelOtherCompLength
    }

//    private fun addTextChangeListenerForEditText(
//        insertEditText: EditText,
//        currentOrder: Int,
//    ){
//        insertEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(
//                s: Editable?
//            ) {}
//
//            override fun beforeTextChanged(
//                s: CharSequence?, start: Int, count: Int, after: Int
//            ) {}
//
//            override fun onTextChanged(
//                s: CharSequence?,
//                start: Int,
//                before: Int,
//                count: Int
//            ) {
//                val currentText = insertEditText.text
//                val includeBothQuoteInMiddle = checkMiddleText(currentText)
//                val includeForbbidenStr = "\\" in currentText
//                if (includeForbbidenStr ||
//                    includeBothQuoteInMiddle
//                ) {
//                    insertEditText.setError("\\ or both quote is used")
//
//                    buttonViewHowActive.buttonViewHowActive(
//                        ToolbarButtonBariantForEdit.OK.str,
//                        false
//                    )
//                    buttonViewHowActive.buttonViewHowActive(
//                        ToolbarButtonBariantForEdit.EDIT.str,
//                        false
//                    )
//                    validateErrEditTextNumberSet.add(currentOrder)
//                    return
//                }
//                validateErrEditTextNumberSet.remove(currentOrder)
//                if(validateErrEditTextNumberSet.size > 0) return
//                buttonViewHowActive.buttonViewHowActive(
//                    ToolbarButtonBariantForEdit.OK.str,
//                    true
//                )
//                buttonViewHowActive.buttonViewHowActive(
//                    ToolbarButtonBariantForEdit.EDIT.str,
//                    enableCmdEdit
//                )
//            }
//        })
//    }

    private fun hideVariables(
        editFragment: EditFragment,
        editParameters: EditParameters,
        insertTextView: TextView,
        horizontalLinearLayout: LinearLayout,
    ){
        val isVisible = !editParameters.hideSettingVariableList.contains(
            editParameters.currentVariableName
        )
        if(!isVisible) {
            horizontalLinearLayout.isVisible = isVisible
            insertTextView.isVisible = isVisible
            return
        }
        val variableTypeList = editParameters.variableTypeList
        val isInsertTextViewVisible = !variableTypeList.contains(
            EditTextSupportViewName.HIDDEN_LABEL.str
        )
        insertTextView.isVisible = isInsertTextViewVisible
    }
}

private fun checkMiddleText(
    currentText: Editable,
): Boolean{
    val currentTextLength = currentText.length
    if(currentTextLength <= 2) return false
    val currentTextLengthMinus = currentText.length - 1
    val middleCurrentText = currentText.substring(1, currentTextLengthMinus)
    return (middleCurrentText.indexOf('\'') != -1
            && middleCurrentText.indexOf('"') != -1 )
}

fun culcTextLabelMarkIndex(
    editParameters: EditParameters,
    textAndLabelList: List<String>,
): Int {
    return editParameters.variableTypeList.filter {
        !textAndLabelList.contains(it)
    }.indexOf (
        EditTextSupportViewName.VARIABLE_LABEL.str
    )
}

fun updateVariableTypeListByLabel(
    editParameters: EditParameters,
    textLabelIndex: Int,
): List<String> {
    val variableTypeListSource = editParameters.variableTypeList
    if(
        textLabelIndex < 0
    ) return variableTypeListSource
    return variableTypeListSource.filter {
        it != EditTextSupportViewName.VARIABLE_LABEL.str
    }
}

fun updateSetVariableMapByLabelIndex(
    editParameters: EditParameters,
    textLabelIndex: Int,
): Map<String, String>? {
    val setVariableSetSeparator = "|"
    val setVariableMapSource = editParameters.setVariableMap
    if(
        textLabelIndex < 0
    ) return setVariableMapSource
    val setVariableMapValueList = editParameters.setVariableMap?.get(
        SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
    )?.split(setVariableSetSeparator)
        ?.toMutableList()
    val setVariableMapValueListLimitIndex =
        setVariableMapValueList?.size ?: 0
    if(
        textLabelIndex >= setVariableMapValueListLimitIndex
    ) return setVariableMapSource
    setVariableMapValueList?.removeAt(textLabelIndex)
    val setVariableMapValue =
        setVariableMapValueList?.joinToString(setVariableSetSeparator) ?: String()
    if(
        setVariableMapSource.isNullOrEmpty()
    ) return null
    return setVariableMapSource + mapOf(
        SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name to
                setVariableMapValue
    )
}
