package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.widget.TextView
import com.puutaro.commandclick.common.variable.edit.EditParameters

object VariableLabelAdder {

//    fun add(
//        insertTextView: TextView,
//        editParameters: EditParameters,
//        textLabelIndex: Int,
//    ){
//        if(
//            textLabelIndex < 0
//        ) return
//        val currentSetVariableValue = SetVariableTypeValue.makeByReplace(
//            editParameters
//        )
//
//        val textViewMap = SetVariableTyper.getCertainSetValIndexMap(
//            currentSetVariableValue,
//            textLabelIndex
//        )
//        val addLabel = textViewMap?.get(
//            TextVeiwLabelType.label.name
//        ) ?: return
//        if(
//            addLabel.isEmpty()
//        ) return
//        insertTextView.text =
//            when (addLabel) {
//            "this" -> editParameters.currentVariableName?.camelToLowerBlankNoRegex()
//            "THIS" -> editParameters.currentVariableName?.upperToLower()
//            else -> addLabel
//        }
//    }

    enum class TextVeiwLabelType {
        label
    }


    private fun String.camelToLowerBlankNoRegex(): String {
        return this.fold(StringBuilder()) { acc, c ->
            acc.let {
                val lowerC = c.lowercase()
                acc.append(
                    if (
                        acc.isNotEmpty() && c.isUpperCase()
                    ) " $lowerC" else lowerC
                )
            }
        }.toString().trim().replaceFirstChar {
            it.uppercase()
        }

    }

    private fun String.upperToLower(): String {
        return this.fold(StringBuilder()) { acc, c ->
            acc.let {
                val lowerC = c.lowercase()
                acc.append(
                    if (
                        acc.isNotEmpty() && c.isUpperCase()
                    ) lowerC
                    else if(c == '_' ) " "
                    else lowerC
                )
            }
        }.toString().trim().replaceFirstChar {
            it.uppercase()
        }

    }
}