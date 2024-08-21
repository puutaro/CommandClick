package com.puutaro.commandclick.proccess

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.dialog.DialogObject
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.file.UrlFileSystems
import java.io.File


object ScriptFileDescription {

    private val filePrefix = EditSettings.filePrefix
    private val httpsPrefix = WebUrlVariables.httpsPrefix

    fun show(
        fragment: Fragment,
        currentScriptContentsList: List<String>,
//        currentAppDirPath: String,
        fannelName: String,
    ) {
        val labelingSecConList = subLabelingSecConList(
            currentScriptContentsList,
            fannelName,
        ) ?: return

        val descCon = makeDescriptionContents(
            labelingSecConList,
//            currentAppDirPath,
            fannelName,
        )
        val targetFragmentInstance = TargetFragmentInstance()
        val isTerminalFragment =
            targetFragmentInstance.getCurrentTerminalFragmentFromFrag(
                fragment.activity
            ) != null
        val readmeUrl = getReadmeUrl(descCon)
        val isReadmeUrl = !readmeUrl.isNullOrEmpty()
        val isLaunchWebReadme = isTerminalFragment && isReadmeUrl
        when(isLaunchWebReadme){
            true -> {
                val webSearcherName = UrlFileSystems.Companion.FirstCreateFannels.WebSearcher.str +
                        UsePath.JS_FILE_SUFFIX
                val systemExecRepTextList =
                    when(readmeUrl == null){
                        false -> listOf(readmeUrl)
                        else -> emptyList()
                    }
                ExecJsLoad.execExternalJs(
                    fragment,
//                    currentAppDirPath,
                    webSearcherName,
                    systemExecRepTextList
                )
            }
            else -> {
                if(
                    isMdConAsHttp(
                        labelingSecConList,
                        fannelName,
                    )
                ){
                    DialogObject.descDialog(
                        fragment,
                        fannelName,
                        extractMdContents(
                            String(),
//                            currentAppDirPath,
                            fannelName,
                        )
                    )
                    return
                }
                DialogObject.descDialog(
                    fragment,
                    fannelName,
                    makeDescriptionContents(
                        labelingSecConList,
//                        currentAppDirPath,
                        fannelName,
                    )
                )
            }
        }

    }

    private fun isMdConAsHttp(
        labelingSecConList: List<String>,
        fannelName: String,
    ): Boolean {
        val labelingSecListSize = labelingSecConList.size
        if(
            labelingSecListSize > 4
        ) return false
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(fannelName)
        val removePrefix = when(
            languageType == LanguageTypeSelects.SHELL_SCRIPT
        ) {
            true -> "#"
            else -> "//"
        }
        return labelingSecConList.any {
            val line = it.removePrefix(removePrefix).trim()
            line.startsWith(httpsPrefix)
        }
    }
    fun getReadmeUrl(
        descCon: String,
    ): String? {
        val httpsPrefix = UrlFileSystems.gitComPrefix
        val lineNumForGirExtractGitComUrl = 50
        return descCon.split("\n").take(lineNumForGirExtractGitComUrl).filter{
            val line = it.trim()
            line.startsWith(httpsPrefix)
        }.firstOrNull()?.trim()
    }

    fun makeDescriptionContents(
        labelingSecConList: List<String>,
//        currentAppDirPath: String,
        fannelName: String,
    ): String {
        return makeDescConFromLabelingSec(
            labelingSecConList,
//            currentAppDirPath,
            fannelName,
        ).joinToString("\n")
    }

    private fun subLabelingSecConList(
        currentScriptContentsList: List<String>,
        fannelName: String,
    ): List<String>? {
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(fannelName)
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val labelingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.LABELING_SEC_START
        ) as String
        val labelingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.LABELING_SEC_END
        ) as String
        return CommandClickVariables.extractValListFromHolder(
            currentScriptContentsList,
            labelingSectionStart,
            labelingSectionEnd,
        )
    }

    private fun makeDescConFromLabelingSec(
        labelingSecConList: List<String>,
//        currentAppDirPath: String,
        fannelName: String,
    ): List<String> {
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(fannelName)
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
        return (listOf("\n") + labelingSecConList).filter {
            (
                    !it.startsWith(labelingSectionStart)
                            && !it.endsWith(labelingSectionStart)
                    )
                    && (
                    !it.startsWith(labelingSectionEnd)
                            && !it.endsWith(labelingSectionEnd)
                    )
        }.map {
                line ->
            val inputDescLine = line
                .trim()
                .removePrefix(removePrefix)
                .removePrefix(" ")
                .let {
                    it + suffixBlank
                }
            if(
                inputDescLine.trim() != filePrefix
            ) return@map inputDescLine
            extractMdContents(
                inputDescLine,
//                currentAppDirPath,
                fannelName,
            )
        }
    }

    private fun extractMdContents(
        inputDescLine: String,
//        currentAppDirPath: String,
        fannelName: String,
    ): String {
        val fannelReadmePath = ScriptPreWordReplacer.replace(
            UsePath.fannelReadmePath,
//            currentAppDirPath,
            fannelName,
        )
        val mdPathObj = File(fannelReadmePath)
        if(
            !mdPathObj.isFile
        ) return inputDescLine
        return ReadText(
            fannelReadmePath
        ).readText()
    }
}
