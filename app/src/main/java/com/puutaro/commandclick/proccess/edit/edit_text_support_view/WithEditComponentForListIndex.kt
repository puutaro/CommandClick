package com.puutaro.commandclick.proccess.edit.edit_text_support_view

import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.common.variable.edit.TypeVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditTextSetter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.InDeCrementerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.SpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.VariableLabelAdder

object WithEditComponentForListIndex {
    private val textAndLabelList = TypeVariable.textAndLabelList
    private val noIndexTypeList = TypeVariable.noIndexTypeList

//    fun insert(
//        editFragment: EditFragment,
//        insertTextView: TextView,
//        editParameters: EditParameters,
//    ): LinearLayout {
//        val textLabelIndex = culcSetVariableTypeMarkIndex(
//            editParameters,
//            textAndLabelList,
//            EditTextSupportViewName.VARIABLE_LABEL.str
//        )
//        VariableLabelAdder.add(
//            insertTextView,
//            editParameters,
//            textLabelIndex
//        )
//        editParameters.variableTypeList = updateVariableTypeListByExcludeSupportView(
//            editParameters,
//            textLabelIndex,
//            EditTextSupportViewName.VARIABLE_LABEL.str
//        )
//        editParameters.setVariableMap = updateSetVariableMapByEditSupportViewNameIndex(
//            editParameters,
//            textLabelIndex,
//        )
//
//
//        val editTextPropertyIndex = culcSetVariableTypeMarkIndex(
//            editParameters,
//            textAndLabelList,
//            EditTextSupportViewName.EDIT_TEXT_PROPERTY.str
//        )
//        val setVariableValueForEditText = SetVariableTypeValue.makeByReplace(
//            editParameters
//        )
//        val editTextPropertyMap = SetVariableTyper.getCertainSetValIndexMap(
//            setVariableValueForEditText,
//            editTextPropertyIndex
//        )
//        val variableTypeList = updateVariableTypeListByExcludeSupportView(
//            editParameters,
//            editTextPropertyIndex,
//            EditTextSupportViewName.EDIT_TEXT_PROPERTY.str
//        )
//        editParameters.setVariableMap = updateSetVariableMapByEditSupportViewNameIndex(
//            editParameters,
//            editTextPropertyIndex,
//        )
//
//
//        val editTextWeight = decideTextEditWeight(
//            variableTypeList,
//        )
//        val otherComponentWeight = decideOtherComponentWeight(
//            editTextWeight,
//            variableTypeList,
//        )
//        val insertEditText = EditTextSetter.set(
//            editFragment,
//            editParameters,
//            editTextPropertyMap,
//            editTextWeight
//        )
//        val context = editFragment.context
//        val horizontalLinearLayout = makeHorizontalLayout(context)
//        horizontalLinearLayout.addView(insertEditText)
////        hideSettingVariableWhenSettingEdit(
////            editParameters,
////            insertTextView,
////            horizontalLinearLayout,
////        )
//        (variableTypeList.indices).forEach {
//            val variableTypeListUntilCurrent =  variableTypeList.take(it + 1)
//            val currentComponentIndex = variableTypeListUntilCurrent.filter {
//                !noIndexTypeList.contains(it)
//            }.size - 1
//            when(variableTypeList[it]){
//                EditTextSupportViewName.CHECK_BOX.str -> {
//                    val insertSpinner = SpinnerViewProducer.make(
//                        context,
//                        insertEditText,
//                        editParameters,
//                        currentComponentIndex,
//                        otherComponentWeight,
//                    )
//                    horizontalLinearLayout.addView(insertSpinner)
//                }
//                EditTextSupportViewName.EDITABLE_CHECK_BOX.str -> {
//                    val insertSpinner = EditableSpinnerViewProducer.make(
//                        editFragment,
//                        insertEditText,
//                        editParameters,
//                        currentComponentIndex,
//                        otherComponentWeight,
//                    )
//                    horizontalLinearLayout.addView(insertSpinner)
//                }
//                EditTextSupportViewName.EDITABLE_FILE_SELECT_BOX.str -> {
//                    val editableFileSelectSpinner = FileSelectSpinnerViewProducer.make(
//                        editFragment,
//                        insertEditText,
//                        editParameters,
//                        currentComponentIndex,
//                        otherComponentWeight,
//                    )
//                    horizontalLinearLayout.addView(editableFileSelectSpinner)
//                }
//                EditTextSupportViewName.LIST_CONTENTS_SELECT_BOX.str -> {
//                    val insertListConSpinner = ListContentsSelectSpinnerViewProducer.make(
//                        editFragment,
//                        insertEditText,
//                        editParameters,
//                        currentComponentIndex,
//                        otherComponentWeight,
//                    )
//                    horizontalLinearLayout.addView(insertListConSpinner)
//                }
//                EditTextSupportViewName.EDITABLE_LIST_CONTENTS_SELECT_BOX.str -> {
//                    val insertListConSpinner = EditableListContentsSelectSpinnerViewProducer.make(
//                        editFragment,
//                        insertEditText,
//                        editParameters,
//                        currentComponentIndex,
//                        otherComponentWeight,
//                    )
//                    horizontalLinearLayout.addView(insertListConSpinner)
//                }
//                EditTextSupportViewName.NUM_INDE_CREMENTER.str -> {
//                    val incButton = InDeCrementerViewProducer.make(
//                        editFragment,
//                        insertEditText,
//                        editParameters,
//                        currentComponentIndex,
//                        otherComponentWeight,
//                        true,
//                    )
//                    horizontalLinearLayout.addView(incButton)
//                    val decButton = InDeCrementerViewProducer.make(
//                        editFragment,
//                        insertEditText,
//                        editParameters,
//                        currentComponentIndex,
//                        otherComponentWeight,
//                        false
//                    )
//                    horizontalLinearLayout.addView(decButton)
//                }
//                EditTextSupportViewName.READ_ONLY_EDIT_TEXT.str -> {
//                    insertEditText.isEnabled = false
//                }
//                EditTextSupportViewName.PASSWORD.str -> {
//                    insertEditText.inputType = (
//                            InputType.TYPE_CLASS_TEXT or
//                                    InputType.TYPE_TEXT_VARIATION_PASSWORD
//                            )
//                }
//                else -> {}
//            }
//        }
//        return horizontalLinearLayout
//    }

    private  fun makeHorizontalLayout(
        context: Context?
    ): LinearLayout {
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
        val TextAndLabelOtherCompLength = makeTextAndLabelOtherCompLength(variableTypeList)
        if(
            TextAndLabelOtherCompLength == 0
        ) return 1F
        if(
            !isTextEmphasis
        ) return 0F
        return when(true){
            (TextAndLabelOtherCompLength >= 3) -> 0.45F
            (TextAndLabelOtherCompLength == 2) -> 0.6F
            (TextAndLabelOtherCompLength == 1) -> 0.7F
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

//    private fun hideSettingVariableWhenSettingEdit(
//        editParameters: EditParameters,
//        insertTextView: TextView,
//        horizontalLinearLayout: LinearLayout,
//    ){
//        val hideSettingVariableList = editParameters.hideSettingVariableList
//        if(
//            hideSettingVariableList.isEmpty()
//        ) return
//        val isHidden = !editParameters.hideSettingVariableList.contains(
//            editParameters.currentVariableName
//        )
//        horizontalLinearLayout.isVisible = isHidden
//        insertTextView.isVisible = isHidden
//    }
}
