package com.puutaro.commandclick.proccess

import com.puutaro.commandclick.common.variable.CommandClickShellScript

class CommentOutLabelingSection {
    companion object {
        fun commentOut(
            shellContentsList: List<String>
        ): List<String> {
            var countLabelingSectionStart = 0
            var countLabelingSectionEnd = 0
            return shellContentsList.map {
                if(
                    it.startsWith(CommandClickShellScript.LABELING_SECTION_START)
                    && it.endsWith(CommandClickShellScript.LABELING_SECTION_START)
                ) countLabelingSectionStart++
                if(
                    it.startsWith(CommandClickShellScript.LABELING_SECTION_END)
                    && it.endsWith(CommandClickShellScript.LABELING_SECTION_END)
                ) countLabelingSectionEnd++
                execCommentOut(
                    it,
                    countLabelingSectionStart,
                    countLabelingSectionEnd
                )
            }
        }

        private fun execCommentOut(
            rowStr: String,
            countLabelingSectionStart: Int,
            countLabelingSectionEnd: Int
        ): String {
            if(
                countLabelingSectionStart == 0
                || countLabelingSectionEnd > 0
            ) return rowStr
            if(
                rowStr.startsWith("#")
            ) return rowStr
            return "# ${rowStr}"

        }
    }
}
