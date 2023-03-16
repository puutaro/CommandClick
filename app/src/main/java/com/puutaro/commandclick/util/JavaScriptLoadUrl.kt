package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import java.io.File

class JavaScriptLoadUrl {
    companion object {
        fun make (
            execJsPath: String,
            jsListSource: List<String>? = null
        ):String? {
            val jsFileObj = File(execJsPath)
            if(!jsFileObj.isFile) return null
            val recentAppDirPath = jsFileObj.parent
            if(recentAppDirPath.isNullOrEmpty()) return null
            val languageTypeToSectionHolderMap =
                CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(LanguageTypeSelects.JAVA_SCRIPT)
            val settingSectionStart = languageTypeToSectionHolderMap?.get(
                CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
            ) as String
            val settingSectionEnd = languageTypeToSectionHolderMap.get(
                CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
            ) as String

            val commandSectionStart = languageTypeToSectionHolderMap.get(
                CommandClickShellScript.Companion.HolderTypeName.CMD_SEC_START
            ) as String
            val commandSectionEnd = languageTypeToSectionHolderMap.get(
                CommandClickShellScript.Companion.HolderTypeName.CMD_SEC_END
            ) as String
            var countSettingSectionStart = 0
            var countSettingSectionEnd = 0
            var countCmdSectionStart = 0
            var countCmdSectionEnd = 0
            val jsList = if(
                jsListSource.isNullOrEmpty()
            ) {
                ReadText(
                    recentAppDirPath,
                    jsFileObj.name
                ).textToList()
            } else jsListSource
            val loadJsUrl = jsList.map {
                if(
                    it.startsWith(settingSectionStart)
                    && it.endsWith(settingSectionStart)
                ) countSettingSectionStart++
                if(
                    it.startsWith(settingSectionEnd)
                    && it.endsWith(settingSectionEnd)
                ) countSettingSectionEnd++
                if(
                    it.startsWith(commandSectionStart)
                    && it.endsWith(commandSectionStart)
                ) countCmdSectionStart++
                if(
                    it.startsWith(commandSectionEnd)
                    && it.endsWith(commandSectionEnd)
                ) countCmdSectionEnd++
                if(
                    countSettingSectionStart > 0
                    && countSettingSectionEnd == 0
                ) "$it;"
                else if(
                    countCmdSectionStart > 0
                    && countCmdSectionEnd == 0
                ) "$it;"
                else it
            }.map {
                val trimJsRow = it
                    .trim(' ')
                    .trim('\t')
                    .trim(' ')
                    .trim('\t')
                if(
                    trimJsRow.startsWith("//")
                ) return@map String()
                trimJsRow
            }.joinToString(" ")
                .replace("\${0}", "${execJsPath}")
                .replace("\${01}", "${recentAppDirPath}")
            if(
                loadJsUrl.isEmpty()
                || loadJsUrl.isBlank()
            ) return null
            return "javascript:(function() { ${loadJsUrl} })();"
        }
    }
}