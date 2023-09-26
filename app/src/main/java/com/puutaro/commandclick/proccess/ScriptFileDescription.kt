package com.puutaro.commandclick.proccess

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.DialogObject
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.JsOrShellFromSuffix
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import java.io.File


object ScriptFileDescription {

    private val filePrefix = "file://"
    private val mdSuffix = ".md"

    fun show(
        fragment: Fragment,
        currentScriptContentsList: List<String>,
        currentAppDirName: String,
        fannelName: String
    ) {
        DialogObject.descDialog(
            fragment,
            fannelName,
            makeDescriptionContents(
                currentScriptContentsList,
                currentAppDirName,
                fannelName,
            )
        )
    }

    fun makeDescriptionContents(
        currentScriptContentsList: List<String>,
        currentAppDirName: String,
        fannelName: String,
    ): String {
        val languageType =
            JsOrShellFromSuffix.judge(fannelName)

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
        val suffixBlank = "  "
        val descriptionContentsList =
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
                line ->
                val inputDescLine = line
                    .trim(' ')
                    .removePrefix(removePrefix)
                    .removePrefix(" ")
                    .let {
                        it + suffixBlank
                    }
                if(
                    !inputDescLine.startsWith(
                        filePrefix
                    )
                    || !inputDescLine.endsWith(
                        "${mdSuffix}${suffixBlank}"
                    )
                ) return@map inputDescLine
                extractMdContents(
                    inputDescLine,
                    currentAppDirName,
                    fannelName,
                )
            } ?: return String()
        return descriptionContentsList.joinToString("\n")
    }

    private fun extractMdContents(
        inputDescLine: String,
        currentAppDirName: String,
        fannelName: String,
    ): String {
        val fannelDirName = fannelName
            .removeSuffix(UsePath.JS_FILE_SUFFIX)
            .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
                "Dir"
        val mdPath = inputDescLine.trim().let{
            QuoteTool.trimBothEdgeQuote(it)
        }.removePrefix(
            filePrefix
        ).let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirName,
                fannelDirName,
                fannelName
            )
        }
        FileSystems.writeFile(
            currentAppDirName,
            "debug.txt",
            mdPath
        )
        val mdPathObj = File(mdPath)
        if(
            !mdPathObj.isFile
        ) return inputDescLine
        val mdParent = mdPathObj.parent
            ?: return inputDescLine
        val mdName = mdPathObj.name
            ?: return inputDescLine
        return ReadText(
            mdParent,
            mdName
        ).readText()
    }
}
