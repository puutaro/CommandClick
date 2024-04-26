package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.common.variable.edit.TypeVariable
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ButtonViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ColorPickerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.DatePickerProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.DirOrFileChooseProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.DragSortListViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditTextSetter
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
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SetVariableTypeValue
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.QuoteTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WithEditComponent(
    private val editFragment: EditFragment,
) {
    private val textAndLabelList = TypeVariable.textAndLabelList
    private val noIndexTypeList = TypeVariable.noIndexTypeList

    fun insert(
        insertTextView: TextView,
        editParameters: EditParameters,
    ): LinearLayout {
        val context = editParameters.context
        val textLabelIndex = culcSetVariableTypeMarkIndex(
            editParameters,
            textAndLabelList,
            EditTextSupportViewName.VARIABLE_LABEL.str
        )
        VariableLabelAdder.add(
            insertTextView,
            editParameters,
            textLabelIndex
        )
        editParameters.variableTypeList = updateVariableTypeListByExcludeSupportView(
            editParameters,
            textLabelIndex,
            EditTextSupportViewName.VARIABLE_LABEL.str
        )
        editParameters.setVariableMap = updateSetVariableMapByEditSupportViewNameIndex(
            editParameters,
            textLabelIndex,
        )

        val editTextPropertyIndex = culcSetVariableTypeMarkIndex(
            editParameters,
            textAndLabelList,
            EditTextSupportViewName.EDIT_TEXT_PROPERTY.str
        )
        val setVariableValueForEditText = SetVariableTypeValue.makeByReplace(
            editParameters
        )
        val editTextPropertyMap = SetVariableTyper.getCertainSetValIndexMap(
            setVariableValueForEditText,
            editTextPropertyIndex
        )
        val variableTypeList = updateVariableTypeListByExcludeSupportView(
            editParameters,
            editTextPropertyIndex,
            EditTextSupportViewName.EDIT_TEXT_PROPERTY.str
        )
        editParameters.variableTypeList = variableTypeList
        editParameters.setVariableMap = updateSetVariableMapByEditSupportViewNameIndex(
            editParameters,
            editTextPropertyIndex,
        )
        val editTextWeight = decideTextEditWeight(
            variableTypeList,
        )
        val otherComponentWeight = decideOtherComponentWeight(
            editTextWeight,
            variableTypeList,
        )
        val insertEditText = EditTextSetter.set(
            editParameters,
            editTextPropertyMap,
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
        checkIndexNum(
            editParameters,
            noIndexTypeList
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
                        editParameters,
                        false,
                        insertEditText,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(fileChooser)
                }
                EditTextSupportViewName.DIRECTORY_PICKER.str -> {
                    val dirChooser = DirOrFileChooseProducer.make(
                        editFragment,
                        editParameters,
                        true,
                        insertEditText,
                        currentComponentIndex,
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

    private fun hideVariables(
        editFragment: EditFragment,
        editParameters: EditParameters,
        insertTextView: TextView,
        horizontalLinearLayout: LinearLayout,
    ){
//        val isVisible = !editParameters.hideSettingVariableList.contains(
//            editParameters.currentVariableName
//        )
//        if(!isVisible) {
//            horizontalLinearLayout.isVisible = isVisible
//            insertTextView.isVisible = isVisible
//            return
//        }
        val variableTypeList = editParameters.variableTypeList
        val isInsertTextViewVisible = !variableTypeList.contains(
            EditTextSupportViewName.HIDDEN_LABEL.str
        )
        insertTextView.isVisible = isInsertTextViewVisible
    }
}

fun checkIndexNum(
    editParameters: EditParameters,
    noIndexTypeList: List<String>,
){
    val currentVariableName = editParameters.currentVariableName
    CoroutineScope(Dispatchers.IO).launch {
        val variableTypeList =
            withContext(Dispatchers.IO) {
                editParameters.variableTypeList
            }
        val currentSetVariableValue =
            withContext(Dispatchers.IO) {
                editParameters.setVariableMap?.get(
                    SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
                )
            }
        val variableTypeIsIndexList =
            withContext(Dispatchers.IO) {
                variableTypeList.filter {
                    !noIndexTypeList.contains(it)
                }
            }
        val variableTypeIsIndexListSize =
            withContext(Dispatchers.IO) {
                variableTypeIsIndexList.size
            }
        val currentSetVariableValueIndexSize =
            withContext(Dispatchers.IO) {
                currentSetVariableValue?.let {
                    QuoteTool.splitBySurroundedIgnore(
                        currentSetVariableValue,
                        '|'
                    ).size
                } ?: 0
//                currentSetVariableValue?.split("|")
//                    ?.size ?: 0
            }
        val existVariableTypeIsIndexList =
            variableTypeIsIndexList.joinToString(":").trim().isNotEmpty()
        val existCurrentVariableName =
            !currentVariableName.isNullOrEmpty()
        if (
            variableTypeIsIndexListSize == currentSetVariableValueIndexSize
            || existVariableTypeIsIndexList
            || existCurrentVariableName
        ) return@launch
        withContext(Dispatchers.IO) {
            LogSystems.stdWarn(
                "not match ${currentVariableName} ${CommandClickScriptVariable.SET_VARIABLE_TYPE}; " +
                        "options / values -> " +
                        "$variableTypeIsIndexListSize / $currentSetVariableValueIndexSize -> " +
                        "( ${variableTypeIsIndexList.joinToString(":")} ) / " +
                        "( ${currentSetVariableValue} )"
            )
        }
    }
}



fun culcSetVariableTypeMarkIndex(
    editParameters: EditParameters,
    textAndLabelList: List<String>,
    setValTypeMarkStr: String,
): Int {
    return editParameters.variableTypeList.filter {
        !textAndLabelList.contains(it)
    }.indexOf (
        setValTypeMarkStr
    )
}


fun updateVariableTypeListByExcludeSupportView(
    editParameters: EditParameters,
    textLabelIndex: Int,
    excludeSupportViewName: String
): List<String> {
    val variableTypeListSource = editParameters.variableTypeList
    if(
        textLabelIndex < 0
    ) return variableTypeListSource
    return variableTypeListSource.filter {
        it != excludeSupportViewName
    }
}

fun updateSetVariableMapByEditSupportViewNameIndex(
    editParameters: EditParameters,
    textLabelIndex: Int,
): Map<String, String>? {
    val setVariableSetSeparator = '|'
    val setVariableMapSource = editParameters.setVariableMap
    if(
        textLabelIndex < 0
    ) return setVariableMapSource
    val setVariableMapValueList = editParameters.setVariableMap?.get(
        SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
    )?.let {
        QuoteTool.splitBySurroundedIgnore(
            it,
            setVariableSetSeparator
        )
    }
//        ?.split(setVariableSetSeparator)
        ?.toMutableList()
    val setVariableMapValueListLimitIndex =
        setVariableMapValueList?.size ?: 0
    if(
        textLabelIndex >= setVariableMapValueListLimitIndex
    ) return setVariableMapSource
    setVariableMapValueList?.removeAt(textLabelIndex)
    val setVariableMapValue =
        setVariableMapValueList?.joinToString(setVariableSetSeparator.toString()) ?: String()
    if(
        setVariableMapSource.isNullOrEmpty()
    ) return null
    return setVariableMapSource + mapOf(
        SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name to
                setVariableMapValue
    )
}
