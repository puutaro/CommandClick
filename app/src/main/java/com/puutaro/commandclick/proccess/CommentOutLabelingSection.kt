package com.puutaro.commandclick.proccess

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable

object CommentOutLabelingSection {
    fun commentOut(
        shellContentsList: List<String>,
//        shellScriptName: String,
    ): List<String> {
//        val languageType =
//            CommandClickVariables.judgeJsOrShellFromSuffix(shellScriptName)
//
//        val languageTypeToSectionHolderMap =
//            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val labelingSectionStart =  CommandClickScriptVariable.LABELING_SEC_START
//        languageTypeToSectionHolderMap?.get(
//            CommandClickScriptVariable.HolderTypeName.LABELING_SEC_START
//        ) as String
        val labelingSectionEnd =  CommandClickScriptVariable.LABELING_SEC_END
//        CommandClickScriptVariable.SETTING_SEC_END,languageTypeToSectionHolderMap.get(
//            CommandClickScriptVariable.HolderTypeName.LABELING_SEC_END
//        ) as String
        val commentOutMark = "//"

//        when(languageType) {
//            LanguageTypeSelects.SHELL_SCRIPT -> "#"
//            else -> "//"
//        }

        var countLabelingSectionStart = 0
        var countLabelingSectionEnd = 0
        return shellContentsList.map {
            if(
                it.startsWith(labelingSectionStart)
                && it.endsWith(labelingSectionEnd)
            ) countLabelingSectionStart++
            if(
                it.startsWith(labelingSectionStart)
                && it.endsWith(labelingSectionEnd)
            ) countLabelingSectionEnd++
            execCommentOut(
                it,
                countLabelingSectionStart,
                countLabelingSectionEnd,
                commentOutMark
            )
        }
    }

    private fun execCommentOut(
        rowStr: String,
        countLabelingSectionStart: Int,
        countLabelingSectionEnd: Int,
        commentOutMark: String,
    ): String {
        if(
            countLabelingSectionStart == 0
            || countLabelingSectionEnd > 0
        ) return rowStr
        if(
            rowStr.startsWith(commentOutMark)
        ) return rowStr
        return "${commentOutMark} ${rowStr}"

    }
}
