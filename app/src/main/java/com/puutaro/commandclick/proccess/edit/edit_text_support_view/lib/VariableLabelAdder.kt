package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.widget.TextView
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SetVariableTypeValue
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.CcScript

object VariableLabelAdder {

    private const val setVariableSetSeparator = "|"

    fun add(
        insertTextView: TextView,
        editParameters: EditParameters,
        textLabelIndex: Int,
    ){
        if(
            textLabelIndex < 0
        ) return
        val currentSetVariableValue = SetVariableTypeValue.makeByReplace(
            editParameters
        )

        val textViewMap = getTextViewMap(
            currentSetVariableValue,
            textLabelIndex
        )
        val addLabel = textViewMap?.get(
            TextVeiwLabelType.label.name
        ) ?: return
        if(
            addLabel.isEmpty()
        ) return
        insertTextView.text =
            when (addLabel) {
            "this" -> editParameters.currentVariableName?.camelToLowerBlankNoRegex()
            "THIS" -> editParameters.currentVariableName?.upperToLower()
            else -> addLabel
        }
    }

    private fun getTextViewMap(
        currentSetVariableValue: String?,
        currentComponentIndex: Int
    ): Map<String, String>? {
        return currentSetVariableValue?.let {
            if(
                it.contains(
                    setVariableSetSeparator
                )
            ) return@let it.split(
                setVariableSetSeparator
            ).getOrNull(currentComponentIndex).let {
                QuoteTool.trimBothEdgeQuote(it)
            }
            QuoteTool.trimBothEdgeQuote(it)
        }?.split('!')
            ?.map {
                CcScript.makeKeyValuePairFromSeparatedString(
                    it,
                    "="
                )
            }?.toMap()
    }

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