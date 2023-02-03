package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.RecordNumToMapNameValueInHolderColumn
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder


class ValidateShell {
    companion object {
        fun correct (
            fragment: Fragment,
            shellContentsList: List<String>,
        ): String {
            val context = fragment.context


            val holderList = shellContentsList.filter {
                it == CommandClickShellScript.LABELING_SECTION_START
                        || it == CommandClickShellScript.LABELING_SECTION_END
                        || it == CommandClickShellScript.SETTING_SECTION_START
                        || it == CommandClickShellScript.SETTING_SECTION_END
                        || it == CommandClickShellScript.CMD_VARIABLE_SECTION_START
                        || it == CommandClickShellScript.CMD_VARIABLE_SECTION_END
            }
            val holderCheck = HolderCheck(
                context,
                holderList
            )
            val checkHolderNumErrMesseage =
                holderCheck.aboutHolderNum()
            if(checkHolderNumErrMesseage.isNotEmpty()) {
                return checkHolderNumErrMesseage
            }
            val checkHoderSetEqualErrMesseage =
                holderCheck.aboutHoderSetMatch()
            if(checkHoderSetEqualErrMesseage.isNotEmpty()) {
                return checkHoderSetEqualErrMesseage
            }
            val checkHoderOrderErrMesseage =
                holderCheck.aboutHolderOrder()
            if(checkHoderOrderErrMesseage.isNotEmpty()) {
                return checkHoderOrderErrMesseage
            }

            val checkVriableValue = CheckVriableValue(
                context,
                shellContentsList
            )
            val checkBlankSurroundedByQuote = checkVriableValue.aboutBlankSurroundedByQuote()
            if(checkBlankSurroundedByQuote.isNotEmpty()){
                return checkBlankSurroundedByQuote
            }
            val checkQuoteSetOrIsBackslash = checkVriableValue.aboutQuoteSetOrIsBackSlash()
            if(checkQuoteSetOrIsBackslash.isNotEmpty()){
                return checkQuoteSetOrIsBackslash
            }
            return String()
        }
    }
}

private class CheckVriableValue(
    private val context: Context?,
    shellContentsList: List<String>
){
    private val recordNumToMapNameValueInHolder =
        RecordNumToMapNameValueInHolder.parse(
            shellContentsList,
            CommandClickShellScript.CMD_VARIABLE_SECTION_START,
            CommandClickShellScript.CMD_VARIABLE_SECTION_END
        )

    fun aboutBlankSurroundedByQuote(): String {
        recordNumToMapNameValueInHolder?.forEach{
            numToNameToValueMap ->
            val currentVariableName = numToNameToValueMap.value?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            ) ?: String()
            val currentVariableValue = numToNameToValueMap.value?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
            ) ?: String()
            val currentValueLength = currentVariableValue.length - 1
            if(currentVariableValue.isEmpty()) return@forEach
            if(currentVariableValue.indexOf(' ') == -1) return@forEach
            if(
                currentVariableValue.indexOf('\'') == 0
                && currentVariableValue.lastIndexOf('\'') == currentValueLength
            ) return@forEach
            if(
                currentVariableValue.indexOf('"') == 0
                && currentVariableValue.lastIndexOf('"') == currentValueLength
                && currentVariableValue.filter { it == '"' }.count() == 2
            ) return@forEach
            return context?.getString(
                R.string.variable_blank_no_surrounded_by_quote_err,
                "${currentVariableName}=${currentVariableValue}"
            ) ?: String()
        }
        return String()
    }

    fun aboutQuoteSetOrIsBackSlash(): String {
        recordNumToMapNameValueInHolder?.forEach{
                numToNameToValueMap ->
            val currentMapNameValue = numToNameToValueMap.value
            val currentVariableName = currentMapNameValue?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
            ) ?: String()
            val currentVariableValueString = currentMapNameValue?.get(
                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
            ) ?: String()
            val currentValueLength = currentVariableValueString.length - 1
            if(currentVariableValueString.indexOf('\\') != -1) return context?.getString(
                R.string.variable_exist_backslash_err,
                "${currentVariableName}=${currentVariableValueString}"
            ) ?: String()
            if(currentVariableValueString.indexOf('\'') == -1) return@forEach
            if(currentVariableValueString.indexOf('"') == -1) return@forEach
            val middleCurrentVariableValueString = currentVariableValueString.substring(
                1, currentValueLength
            )
            if(
                middleCurrentVariableValueString.indexOf('"') != -1
                && middleCurrentVariableValueString.indexOf('\'') != -1
            ) return context?.getString(
                R.string.variable_quote_set_err,
                "${currentVariableName}=${currentVariableValueString}"
            ) ?: String()
            if(
                currentVariableValueString.lastIndexOf('\'') == 0
                && currentVariableValueString.lastIndexOf('\'') == currentValueLength
                && currentVariableValueString.filter { it == '\'' }.count() == 2
            ) return@forEach
            if(
                currentVariableValueString.lastIndexOf('"') == 0
                && currentVariableValueString.lastIndexOf('"') == currentValueLength
                && currentVariableValueString.filter { it == '"' }.count() == 2
            ) return@forEach
            if(currentVariableValueString.indexOf('\'') != -1) return@forEach
            return context?.getString(
                R.string.variable_quote_set_err,
                "${currentVariableName}=${currentVariableValueString}"
            ) ?: String()
        }
        return String()
    }
}


private class HolderCheck(
    private val context: Context?,
    private val holderList: List<String>,
) {

    private val holderListForValidateList = listOf(
        CommandClickShellScript.LABELING_SECTION_START,
        CommandClickShellScript.LABELING_SECTION_END,
        CommandClickShellScript.SETTING_SECTION_START,
        CommandClickShellScript.SETTING_SECTION_END,
        CommandClickShellScript.CMD_VARIABLE_SECTION_START,
        CommandClickShellScript.CMD_VARIABLE_SECTION_END,
    )

    fun aboutHolderNum(): String {
        holderListForValidateList.forEach {
                checkHolderName ->
            val checkHolderNum = holderList.filter {
                it == checkHolderName
            }.size
            if(
                checkHolderNum <= 1
            ) return@forEach
            return context?.getString(
                R.string.holder_num_err,
                checkHolderName,
                checkHolderNum
            ) ?: String()
        }
        return String()
    }

    fun aboutHoderSetMatch(): String {
        return mapOf(
            CommandClickShellScript.LABELING_SECTION_START to
            CommandClickShellScript.LABELING_SECTION_END,
            CommandClickShellScript.SETTING_SECTION_START to
            CommandClickShellScript.SETTING_SECTION_END,
            CommandClickShellScript.CMD_VARIABLE_SECTION_START to
            CommandClickShellScript.CMD_VARIABLE_SECTION_END,
        ).map {
            checkHolderMap ->
            val startHolderNum = holderList.filter {
                it == checkHolderMap.key
            }.size
            val endHolderNum = holderList.filter {
                it == checkHolderMap.value
            }.size
            context?.getString(
                R.string.holder_set_no_match_err,
                checkHolderMap.key,
                checkHolderMap.value
            ) to (startHolderNum == endHolderNum)
        }.toMap().filterValues { it == false }.keys.joinToString(", ")
    }

    fun aboutHolderOrder(): String {
        val holderOrderMap = holderListForValidateList.map {
            holderName ->
            holderName to holderList.indexOf(holderName)
        }.toMap().filterValues { it > -1 }
        if(
            holderOrderMap.values.joinToString()
            == holderOrderMap.values.sorted().joinToString()
        ){
            return String()
        }
        return context?.getString(
            R.string.holder_order_err,
            holderList.joinToString(", ")
        ) ?: String()

    }
}