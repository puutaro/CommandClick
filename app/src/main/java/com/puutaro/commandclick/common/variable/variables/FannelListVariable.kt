package com.puutaro.commandclick.common.variable.variables

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.file.UrlFileSystems
import java.io.File

object FannelListVariable {

    val cmdclickFannelListSeparator = "CMDCLICK_FANNEL_LIST_SEPARATOR"
    val descriptionFirstLineLimit = 100

    val fannelNameIndex = 0
    val editExecuteValueIndex = 1
    val descIndex = 2

    fun getFannelName(conLine: String): String {
        return conLine.split("\n")
            .getOrNull(fannelNameIndex) ?: String()
    }

    fun getEditExecute(conLine: String): String {
        return conLine.split("\n")
            .getOrNull(editExecuteValueIndex)
            ?.trim()
            ?: String()
    }

    fun getDesc(conLine: String): String {
        return conLine
            .split("\n")
            .getOrNull(descIndex)
            ?.trim()
            ?.removePrefix("-")
            ?: String()
    }

    fun makeFannelListMemoryContents(
        context: Context?
    ): List<String> {
        val cmdclickFannelItselfDirPath = UsePath.cmdclickFannelItselfDirPath
        if(
            !File(cmdclickFannelItselfDirPath).isDirectory
        ) return emptyList()
        val fannelsListSource = FileSystems.filterSuffixShellOrJsOrHtmlFiles(
            cmdclickFannelItselfDirPath,
        )
        val firstDescriptionLineRange = 50
        return fannelsListSource.map {
            val fannelConList = ReadText(
                File(cmdclickFannelItselfDirPath, it).absolutePath,
            ).textToList()
            val editExecuteValue = CommandClickVariables.returnEditExecuteValueStr(
                fannelConList,
                LanguageTypeSelects.JAVA_SCRIPT
            )
            val descConSrc = ScriptFileDescription.makeDescriptionContents(
                fannelConList,
//                cmdclickFannelItselfDirPath,
                it
            )
            val readmeUrl = ScriptFileDescription.getReadmeUrl(descConSrc)
            val descCon = when(readmeUrl.isNullOrEmpty()){
                true
                -> descConSrc
                else
                -> CurlManager.get(
                    context,
                    makeReadmeRawUrl(readmeUrl),
                    String(),
                    String(),
                    2000
                ).let {
                    val isConnOk = CurlManager.isConnOk(it)
                    if(!isConnOk) return@let String()
                    String(it)
                }
            }
            val descFirstLineSource = descCon.split('\n').take(firstDescriptionLineRange).filter {
                val trimLine = it.trim()
                val isLetter =
                    trimLine.firstOrNull()?.isLetter()
                        ?: false
                isLetter && trimLine.isNotEmpty()
            }.firstOrNull()
            val descFirstLine = if(
                !descFirstLineSource.isNullOrEmpty()
                && descFirstLineSource.length > descriptionFirstLineLimit
            ) descFirstLineSource.substring(0, descriptionFirstLineLimit)
            else descFirstLineSource
            return@map if(descFirstLine.isNullOrEmpty()) it
            else {
                "$it\n${editExecuteValue}\n\t\t$descFirstLine"
            }
        }
    }

    private fun makeReadmeRawUrl(
        readmeUrl: String,
    ): String {
        val gitComPrefix = UrlFileSystems.gitComPrefix
        val gitUserContentPrefix = UrlFileSystems.gitUserContentPrefix
        val readmeSuffix = UrlFileSystems.readmeSuffix
        val gitSuffix = ".git"
        return listOf(
            readmeUrl
                .replace(Regex("${gitSuffix}#.*$"), "")
                .replace(Regex("#.*$"), "")
                .removeSuffix(gitSuffix)
                .replace(
                    gitComPrefix,
                    gitUserContentPrefix
                ),
            readmeSuffix
        ).joinToString("/")
    }
}