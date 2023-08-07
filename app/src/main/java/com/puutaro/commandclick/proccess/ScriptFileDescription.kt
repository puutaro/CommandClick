package com.puutaro.commandclick.proccess

import android.content.Context
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.DialogObject
import com.puutaro.commandclick.util.JsOrShellFromSuffix


object ScriptFileDescription {

    fun show(
        contextSrc: Context?,
        currentScriptContentsList: List<String>,
        scriptFileName: String
    ) {
        val context = contextSrc
            ?: return
        DialogObject.simpleTextShow(
            context,
            "Description",
            makeDescriptionContents(
                currentScriptContentsList,
                scriptFileName
            )
        )
    }

    fun makeDescriptionContents(
        currentScriptContentsList: List<String>,
        scriptFileName: String
    ): String {
        val languageType =
            JsOrShellFromSuffix.judge(scriptFileName)

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val labelingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.LABELING_SEC_START
        ) as String
        val labelingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.LABELING_SEC_END
        ) as String
        val removePrefix = if(languageType == LanguageTypeSelects.SHELL_SCRIPT){
            "#"
        } else "//"

        val descripitionContentsList =
            CommandClickVariables.substituteVariableListFromHolder(
                currentScriptContentsList,
                labelingSectionStart,
                labelingSectionEnd,
            )?.filter {
                (
                        !it.startsWith(labelingSectionStart)
                                && !it.endsWith(labelingSectionStart)
                        )
                        && (
                        !it.startsWith(labelingSectionEnd)
                                && !it.endsWith(labelingSectionEnd)
                        )
            }?.map {
                it
                    .trim(' ')
                    .removePrefix(removePrefix)
            } ?: return String()
        return descripitionContentsList.joinToString("\n")
    }
}