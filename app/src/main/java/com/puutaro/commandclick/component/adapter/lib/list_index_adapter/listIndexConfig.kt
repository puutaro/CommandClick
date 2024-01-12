package com.puutaro.commandclick.component.adapter.lib.list_index_adapter

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.LinuxCmd
import com.puutaro.commandclick.util.Map.CmdClickMap
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.UrlFileSystems
import java.io.File

object ListIndexEditConfig {
    enum class ListIndexConfigKey(
        val key: String,
    ) {
        NAME("name"),
        DESC("desc"),
    }

    enum class ListIndexFileNameKey(
        val key: String,
    ) {
        ON_HIDE("onHide"),
        REMOVE_EXTEND("removeExtend"),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
    }

    enum class ListIndexDescKey(
        val key: String,
    ) {
        LENGTH("length"),
        FANNEL_DESC("fannelDesc"),
        SHELL_PATH("shellPath"),
    }

    fun setFileNameTextView(
        fileNameTextView: AppCompatTextView?,
        fileNameSrc: String,
        listIndexConfigMap: Map<String, String>?
    ) {
        val fileNameConfigMap = listIndexConfigMap?.get(
            ListIndexConfigKey.NAME.key
        ).let {
            if (
                it.isNullOrEmpty()
            ) return@let emptyMap()
            CmdClickMap.createMap(
                it,
                "|"
            ).toMap()
        }
        val isHide = fileNameConfigMap.containsKey(ListIndexFileNameKey.ON_HIDE.key)
        if (isHide) {
            fileNameTextView?.isVisible = false
            return
        }
        fileNameTextView?.text = makeFileName(
            fileNameSrc,
            listIndexConfigMap,
        )
    }

    private fun makeFileName(
        fileNameSrc: String,
        fileNameConfigMap: Map<String, String>?
    ): String {
        if (
            fileNameConfigMap.isNullOrEmpty()
        ) return fileNameSrc
        val removedExtendFileName =
            fileNameConfigMap.containsKey(ListIndexFileNameKey.REMOVE_EXTEND.key).let {
                if (!it) return@let fileNameSrc
                CcPathTool.trimAllExtend(fileNameSrc)
            }
        val compPrefixedFileName =
            fileNameConfigMap.get(ListIndexFileNameKey.COMP_PREFIX.key).let {
                if (
                    it.isNullOrEmpty()
                ) return@let removedExtendFileName
                UsePath.compPrefix(fileNameSrc, it)
            }
        return fileNameConfigMap.get(ListIndexFileNameKey.COMP_SUFFIX.key).let {
            if (
                it.isNullOrEmpty()
            ) return@let compPrefixedFileName
            UsePath.compExtend(fileNameSrc, it)
        }
    }

    fun makeFileDesc(
        parentDirPath: String,
        fileName: String,
        fileCon: String,
        listIndexConfigMap: Map<String, String>?,
    ): String? {
        val defaultMaxTakeLength = 50
        val defaultTakeFileCon = fileCon.take(defaultMaxTakeLength)
        if (
            listIndexConfigMap.isNullOrEmpty()
        ) return null
        val descValue = listIndexConfigMap.get(ListIndexConfigKey.DESC.key)
        if (
            descValue.isNullOrEmpty()
        ) return null
        val descConfigMap = descValue.let {
            CmdClickMap.createMap(
                it,
                "|"
            )
        }.toMap()
        descConfigMap.get(ListIndexDescKey.LENGTH.key).let {
            if (
                it.isNullOrEmpty()
            ) return@let defaultTakeFileCon
            val maxTakeLength = try {
                it.toInt()
            } catch (e: Exception) {
                defaultMaxTakeLength
            }
            return fileCon.take(maxTakeLength)
        }
        descConfigMap.containsKey(ListIndexDescKey.FANNEL_DESC.key).let {
            if (
                !it
            ) return@let defaultTakeFileCon
            return getFannelDescFirstLine(
                parentDirPath,
                fileName
            )
        }
        val descConMark = "DESC_CON_MARK"
        return descConfigMap.get(ListIndexDescKey.SHELL_PATH.key).let {
            if (
                it.isNullOrEmpty()
            ) return@let defaultTakeFileCon
            val shellPathObj = File(it)
            if (
                !shellPathObj.isFile
            ) return@let defaultTakeFileCon
            val shellDirPath = shellPathObj.parent
                ?: return@let defaultTakeFileCon
            val shellCon = ReadText(
                shellDirPath,
                shellPathObj.name
            ).readText().replace(
                descConMark,
                fileCon,
            )
            return@let LinuxCmd.execCommand(
                listOf("sh", "-c", shellCon).joinToString("\t")
            )
        }
    }

    private fun getFannelDescFirstLine(
        parentDirPath: String,
        fannelName: String
    ): String {
        val descConSrc = ScriptFileDescription.makeDescriptionContents(
            ReadText(
                parentDirPath,
                fannelName
            ).textToList(),
            parentDirPath,
            fannelName
        )
        val readmeUrl = ScriptFileDescription.getReadmeUrl(descConSrc)
        val descCon = when (readmeUrl.isNullOrEmpty()) {
            true
            -> descConSrc

            else
            -> CurlManager.get(
                makeReadmeRawUrl(readmeUrl),
                String(),
                String(),
                2000
            ).let {
                val isConnOk = CurlManager.isConnOk(it)
                if (!isConnOk) return@let String()
                String(it)
            }
        }
        val firstDescriptionLineRange = 50
        val descriptionFirstLineLimit = FannelListVariable.descriptionFirstLineLimit
        val descFirstLineSource = descCon.split('\n').take(firstDescriptionLineRange).filter {
            val trimLine = it.trim()
            val isLetter =
                trimLine.firstOrNull()?.isLetter()
                    ?: false
            isLetter && trimLine.isNotEmpty()
        }.firstOrNull()
        return if (
            !descFirstLineSource.isNullOrEmpty()
            && descFirstLineSource.length > descriptionFirstLineLimit
        ) descFirstLineSource.substring(0, descriptionFirstLineLimit)
        else descFirstLineSource ?: String()
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