package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object CmdClickSystemAppDir {

    private val appSystemDirName = "appSystemDir"
    fun create(
        context: Context?,
    ){
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val assetsPrefix = "${appSystemDirName}/system"
                val systemZipPath = assetsPrefix
                AssetsFileManager.copyFileOrDirFromAssets(
                    context,
                    systemZipPath,
                    assetsPrefix,
                    UsePath.cmdclickSystemAppDirPath,
                    emptyList()
                )
            }
            withContext(Dispatchers.IO){
                createConfigFannel(context)
            }
        }
    }

    fun createConfigFannel(
        context: Context?,
    ){
        FannelVersion.create(
            context,
            UsePath.cmdclickSystemAppDirPath,
            "cmdclickConfig",
        )
    }

    fun createPreferenceFannel(
        context: Context?,
        fannelInfoMap: Map<String, String>,
    ){
        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
            fannelInfoMap
        )
        FannelVersion.create(
            context,
            currentAppDirPath,
            "preference",
        )
    }

    object FannelVersion {
        fun create(
            context: Context?,
            targetAppDirPath: String,
            fannelRawName: String,
        ) {
            val assetsPrefix = "${appSystemDirName}/version"
            val fannelRawDirAssetsPath =
                AssetsFileManager.concatAssetsPath(
                    listOf(assetsPrefix, fannelRawName)
                )
            val fannelDirName = fannelRawName + "Dir"
            val infoDirName = "info"
            val fannelInfoDirAssetsPath = let {
                val fannelDirAssetsPath =
                    AssetsFileManager.concatAssetsPath(
                        listOf(fannelRawDirAssetsPath, fannelDirName)
                    )
                AssetsFileManager.concatAssetsPath(
                    listOf(fannelDirAssetsPath, infoDirName)
                )
            }
            val versionTextName = "version.txt"
            val infoDirAssetsPath = AssetsFileManager.concatAssetsPath(
                listOf(fannelDirName, infoDirName),
            )
            val curVersionFilePath =
                File(
                    targetAppDirPath,
                    "${infoDirAssetsPath}/${versionTextName}"
                ).absolutePath
            val assetsVersionFilePath =
                AssetsFileManager.concatAssetsPath(
                    listOf(
                        assetsPrefix,
                        infoDirAssetsPath,
                        versionTextName
                    )
                )
            val assetsVersion = AssetsFileManager.readFromAssets(
                context,
                assetsVersionFilePath
            ).split("\n")
                .firstOrNull()
                ?.trim()
            val curVersion = ReadText(curVersionFilePath)
                .textToList()
                .firstOrNull()
                ?.trim()
            val isUpdate =
                curVersion.isNullOrEmpty()
                        || curVersion != assetsVersion
            val isNotUpdate = !isUpdate
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "cmdclickconfigVer.txt").absolutePath,
//                listOf(
//                    "curVersionFilePath: ${curVersionFilePath}",
//                    "curVersion : ${curVersion}",
//                    "assetsVersionFilePath: ${assetsVersionFilePath}",
//                    "assetsVersion: ${assetsVersion}",
//                    "isNotUpdate: ${isNotUpdate}"
//                ).joinToString("\n\n")
//            )
            if (isNotUpdate) return
            assetsVersion?.let {
                FileSystems.writeFile(
                    curVersionFilePath,
                    assetsVersion
                )
            }
//            CoroutineScope(Dispatchers.IO).launch {
//                    withContext(Dispatchers.IO) {
            val escapeTsvName = "escape.tsv"
            val assetsEscapeTsvPath =
                AssetsFileManager.concatAssetsPath(
                    listOf(fannelInfoDirAssetsPath, escapeTsvName)
                )
            val curEscapeTsvPath = File(
                targetAppDirPath,
                infoDirAssetsPath
            ).absolutePath.let { curInfoDirPath ->
                File(
                    curInfoDirPath,
                    escapeTsvName,
                ).absolutePath
            }
            val escapeMapList = makeEscapeRelativeAssetsPathList(
                        context,
                        assetsEscapeTsvPath,
                        curEscapeTsvPath,
                    )
//                }
//            val escapeFilePathList =
//                AssetsFileManager.readFromAssets(
//                    context,
//                    escapeFilePath
//                ).split("\n").filter { it.isNotEmpty() }
//                withContext(Dispatchers.IO) {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "escape.txt").absolutePath,
//                    listOf(
//                        "${escapeMapList}"
//                    ).joinToString("\n\b") + "\n-------------\n\n"
//                )
            AssetsFileManager.copyFileOrDirFromAssets(
                context,
                fannelRawDirAssetsPath,
                fannelRawDirAssetsPath,
                targetAppDirPath,
                escapeMapList
            )
//                }
        }
    }

    private fun makeEscapeRelativeAssetsPathList(
        context: Context?,
        assetsEscapeTsvPath: String,
        curEscapeTsvPath: String,
    ): List<String> {
        return AssetsFileManager.readFromAssets(
            context,
            assetsEscapeTsvPath
        ).split("\n").map { line ->
            val trimLine = line.trim()
            if (
                trimLine.isEmpty()
            ) return@map String()
            val relativePathAndVersion =
                trimLine.split("\t")
            val relativePath = relativePathAndVersion
                .firstOrNull()
                ?.trim()
                ?: String()
            val assetsVersion = relativePathAndVersion.getOrNull(1)
            val curVersion = TsvTool.getKeyValue(
                curEscapeTsvPath,
                relativePath
            ).let { QuoteTool.trimBothEdgeQuote(it) }
            val isEscape = assetsVersion.isNullOrEmpty()
                    || assetsVersion == curVersion
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "escape_inner.txt").absolutePath,
//                listOf(
//                    "relativePathAndVersion: ${relativePathAndVersion}",
//                    "curEscapeTsvPath: ${curEscapeTsvPath}",
//                    "assetsVersion: ${assetsVersion}",
//                    "curVersion: ${curVersion}",
//                    "isEscape: ${isEscape}",
//                ).joinToString("\n\n") + "\n----\n"
//            )
            when (isEscape) {
                false -> String()
                else -> relativePath
            }
        }.filter {
            it.isNotEmpty()
        }
    }
}