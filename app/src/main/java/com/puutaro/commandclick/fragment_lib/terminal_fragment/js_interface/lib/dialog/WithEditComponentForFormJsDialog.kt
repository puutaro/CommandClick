package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.content.Context
import android.text.InputType
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.common.variable.edit.TypeVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.checkIndexNum
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.culcSetVariableTypeMarkIndex
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditTextSetter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.EditableSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.InDeCrementerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.SpinnerViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.VariableLabelAdder
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SetVariableTypeValue
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.updateSetVariableMapByEditSupportViewNameIndex
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.updateVariableTypeListByExcludeSupportView
import com.puutaro.commandclick.proccess.edit.lib.SetVariableTyper

object WithEditComponentForFormJsDialog {

    private val textAndLabelList = TypeVariable.textAndLabelList
    private val noIndexTypeList = TypeVariable.noIndexTypeList

    fun insert(
        fragment: Fragment,
        insertTextView: TextView,
        editParameters: EditParameters,
    ): LinearLayout {
        val context = fragment.context

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
            fragment,
            editParameters,
            editTextPropertyMap,
            editTextWeight
        )
        val isInsertTextViewVisible = !editParameters.variableTypeList.contains(
            EditTextSupportViewName.HIDDEN_LABEL.str
        )

        checkIndexNum(
            fragment,
            editParameters,
            noIndexTypeList
        )

        insertTextView.isVisible = isInsertTextViewVisible
        val horizontalLinearLayout = makeHorizontalLayout(context)
        horizontalLinearLayout.addView(insertEditText)
        (variableTypeList.indices).forEach {
            val variableTypeListUntilCurrent =  variableTypeList.take(it + 1)
            val currentComponentIndex = variableTypeListUntilCurrent.filter {
                !noIndexTypeList.contains(it)
            }.size - 1
            when(variableTypeList[it]){
                EditTextSupportViewName.CHECK_BOX.str -> {
                    val insertSpinner = SpinnerViewProducer.make(
                        fragment.context,
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertSpinner)
                }
                EditTextSupportViewName.EDITABLE_CHECK_BOX.str -> {
                    val insertSpinner = EditableSpinnerViewProducer.make(
                        fragment,
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertSpinner)
                }
                EditTextSupportViewName.EDITABLE_FILE_SELECT_BOX.str -> {
                    val editableFileSelectSpinner = FileSelectSpinnerViewProducer.make(
                        fragment,
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(editableFileSelectSpinner)
                }
                EditTextSupportViewName.LIST_CONTENTS_SELECT_BOX.str -> {
                    val insertListConSpinner = ListContentsSelectSpinnerViewProducer.make(
                        fragment,
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertListConSpinner)
                }
                EditTextSupportViewName.EDITABLE_LIST_CONTENTS_SELECT_BOX.str -> {
                    val insertListConSpinner = EditableListContentsSelectSpinnerViewProducer.make(
                        fragment,
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                    )
                    horizontalLinearLayout.addView(insertListConSpinner)
                }
                EditTextSupportViewName.NUM_INDE_CREMENTER.str -> {
                    val incButton = InDeCrementerViewProducer.make(
                        fragment,
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                        true,
                    )
                    horizontalLinearLayout.addView(incButton)
                    val decButton = InDeCrementerViewProducer.make(
                        fragment,
                        insertEditText,
                        editParameters,
                        currentComponentIndex,
                        otherComponentWeight,
                        false
                    )
                    horizontalLinearLayout.addView(decButton)
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
}
