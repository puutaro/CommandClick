package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder


//object ValidateShell {
//    fun correct (
//        fragment: Fragment,
//        shellContentsList: List<String>,
//        shellFileName: String,
//    ): String {
//        val context = fragment.context
////            val languageType =
////                CommandClickVariables.judgeJsOrShellFromSuffix(shellFileName)
////
////            val languageTypeToSectionHolderMap =
////                CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
//        val labelingSectionStart =  CommandClickScriptVariable.LABELING_SEC_START
////           languageTypeToSectionHolderMap?.get(
////                CommandClickScriptVariable.HolderTypeName.LABELING_SEC_START
////            ) as String
//        val labelingSectionEnd =  CommandClickScriptVariable.LABELING_SEC_END
////            CommandClickScriptVariable.SETTING_SEC_END,languageTypeToSectionHolderMap.get(
////                CommandClickScriptVariable.HolderTypeName.LABELING_SEC_END
////            ) as String
//        val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
////            CommandClickScriptVariable.SETTING_SEC_END,languageTypeToSectionHolderMap.get(
////                CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
////            ) as String
//        val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END
////            languageTypeToSectionHolderMap.get(
////                CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
////            ) as String
//
//        val commandSectionStart =  CommandClickScriptVariable.CMD_SEC_START
////            CommandClickScriptVariable.SETTING_SEC_END,languageTypeToSectionHolderMap.get(
////                CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
////            ) as String
//        val commandSectionEnd =  CommandClickScriptVariable.CMD_SEC_END
////            languageTypeToSectionHolderMap.get(
////                CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
////            ) as String
//
//
//        val holderList = shellContentsList.filter {
//            it == labelingSectionStart
//                    || it == labelingSectionEnd
//                    || it == settingSectionStart
//                    || it == settingSectionEnd
//                    || it == commandSectionStart
//                    || it == commandSectionEnd
//        }
//        val holderCheck = HolderCheck(
//            context,
//            holderList,
//            shellFileName
//        )
//        val checkHolderNumErrMessage =
//            holderCheck.aboutHolderNum()
//        if(checkHolderNumErrMessage.isNotEmpty()) {
//            return checkHolderNumErrMessage
//        }
//        val checkHolderSetEqualErrMessage =
//            holderCheck.aboutHoderSetMatch()
//        if(checkHolderSetEqualErrMessage.isNotEmpty()) {
//            return checkHolderSetEqualErrMessage
//        }
//        val checkHolderOrderErrMessage =
//            holderCheck.aboutHolderOrder()
//        if(checkHolderOrderErrMessage.isNotEmpty()) {
//            return checkHolderOrderErrMessage
//        }
//
////        val checkVariableValue = CheckVariableValue(
////            context,
////            shellContentsList,
//////                shellFileName
////        )
////        val checkQuoteSetOrIsBackslash = checkVariableValue.aboutQuoteSetOrIsBackSlash()
////        if(checkQuoteSetOrIsBackslash.isNotEmpty()){
////            return checkQuoteSetOrIsBackslash
////        }
//        return String()
//    }
//}

private class CheckVariableValue(
    private val context: Context?,
    shellContentsList: List<String>,
//    shellFileName: String
){
//    val languageType =
//        CommandClickVariables.judgeJsOrShellFromSuffix(shellFileName)
//
//    val languageTypeToSectionHolderMap =
//        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)

    val commandSectionStart =  CommandClickScriptVariable.CMD_SEC_START
    val commandSectionEnd =  CommandClickScriptVariable.CMD_SEC_END
//    val labelingSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.LABELING_SEC_START
//    ) as String
//    val labelingSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.LABELING_SEC_END
//    ) as String
//    val settingSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//    ) as String
//    val settingSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//    ) as String

//    val commandSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
//    ) as String
//    val commandSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
//    ) as String


    private val recordNumToMapNameValueInHolder =
        RecordNumToMapNameValueInHolder.parse(
            shellContentsList,
            commandSectionStart,
            commandSectionEnd
        )

//    fun aboutQuoteSetOrIsBackSlash(): String {
//        recordNumToMapNameValueInHolder?.forEach{
//                numToNameToValueMap ->
//            val currentMapNameValue = numToNameToValueMap.value
//            val currentVariableName = currentMapNameValue?.get(
//                RecordNumToMapNameValueInHolderColumn.VARIABLE_NAME.name
//            ) ?: String()
//            val currentVariableValueString = currentMapNameValue?.get(
//                RecordNumToMapNameValueInHolderColumn.VARIABLE_VALUE.name
//            ) ?: String()
//            val currentValueLength = currentVariableValueString.length - 1
//            if(currentVariableValueString.indexOf('\\') != -1) {
//                return context?.getString(
//                    R.string.variable_exist_backslash_err,
//                    "${currentVariableName}=${currentVariableValueString}"
//                ) ?: String()
//            }
//            if(currentVariableValueString.indexOf('\'') == -1) return@forEach
//            if(currentVariableValueString.indexOf('"') == -1) return@forEach
//            val middleCurrentVariableValueString = currentVariableValueString.substring(
//                1, currentValueLength
//            )
//            if(
//                middleCurrentVariableValueString.indexOf('"') != -1
//                && middleCurrentVariableValueString.indexOf('\'') != -1
//            ) return context?.getString(
//                R.string.variable_quote_set_err,
//                "${currentVariableName}=${currentVariableValueString}"
//            ) ?: String()
//            if(
//                currentVariableValueString.lastIndexOf('\'') == 0
//                && currentVariableValueString.lastIndexOf('\'') == currentValueLength
//                && currentVariableValueString.filter { it == '\'' }.count() == 2
//            ) return@forEach
//            if(
//                currentVariableValueString.lastIndexOf('"') == 0
//                && currentVariableValueString.lastIndexOf('"') == currentValueLength
//                && currentVariableValueString.filter { it == '"' }.count() == 2
//            ) return@forEach
//            if(currentVariableValueString.indexOf('\'') != -1) return@forEach
//            return context?.getString(
//                R.string.variable_quote_set_err,
//                "${currentVariableName}=${currentVariableValueString}"
//            ) ?: String()
//        }
//        return String()
//    }
}


private class HolderCheck(
    private val context: Context?,
    private val holderList: List<String>,
    shellFileName: String
) {

//    val languageType =
//        CommandClickVariables.judgeJsOrShellFromSuffix(shellFileName)
//
//    val languageTypeToSectionHolderMap =
//        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
//    val labelingSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.LABELING_SEC_START
//    ) as String
//    val labelingSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.LABELING_SEC_END
//    ) as String
//    val settingSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//    ) as String
//    val settingSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//    ) as String
//
//    val commandSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
//    ) as String
//    val commandSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
//    ) as String
    val labelingSectionStart =  CommandClickScriptVariable.LABELING_SEC_START
    val labelingSectionEnd =  CommandClickScriptVariable.LABELING_SEC_END
    val settingSectionStart =  CommandClickScriptVariable.SETTING_SEC_START
    val settingSectionEnd =  CommandClickScriptVariable.SETTING_SEC_END

    val commandSectionStart =  CommandClickScriptVariable.CMD_SEC_START
    val commandSectionEnd =  CommandClickScriptVariable.CMD_SEC_END


    private val holderListForValidateList = listOf(
        labelingSectionStart,
        labelingSectionEnd,
        settingSectionStart,
        settingSectionEnd,
        commandSectionStart,
        commandSectionEnd,
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
            labelingSectionStart to
            labelingSectionEnd,
            settingSectionStart to
            settingSectionEnd,
            commandSectionStart to
            commandSectionEnd,
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