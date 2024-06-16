package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePrefTool
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
        createFannelByVer(
            context,
            UsePath.cmdclickSystemAppDirPath,
            "cmdclickConfig",
            "1",
        )
    }

    fun createPreferenceFannel(
        context: Context?,
        readSharePrefMap: Map<String, String>,
    ){
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePrefMap
        )
        createFannelByVer(
            context,
            currentAppDirPath,
            "preference",
            "1",
        )
    }


    private fun createFannelByVer(
        context: Context?,
        targetAppDirPath: String,
        fannelRawName: String,
        version: String,
    ){
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
                listOf(fannelDirAssetsPath,infoDirName)
            )
        }
        val versionTextName = "version.txt"
        val infoDirAssetsPath = AssetsFileManager.concatAssetsPath(
            listOf(fannelDirName, infoDirName),
        )
        val versionFilePath =
            File(
                targetAppDirPath,
                "${infoDirAssetsPath}/${versionTextName}"
            ).absolutePath
        val isNotUpdate =
            ReadText(versionFilePath)
                .textToList()
                .firstOrNull()
                ?.trim() == version
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "cmdclickconfigVer.txt").absolutePath,
//            listOf(
//                "con : ${ReadText(versionFilePath)
//                    .textToList()
//                    .firstOrNull()
//                    ?.trim()}",
//                "isNotUpdate: ${isNotUpdate}"
//            ).joinToString("\n\n")
//        )
        if(isNotUpdate) return
        FileSystems.writeFile(
            versionFilePath,
            version
        )
        CoroutineScope(Dispatchers.IO).launch {
            val escapeMapList = withContext(Dispatchers.IO) {
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
                makeEscapeRelativeAssetsPathList(
                    context,
                    assetsEscapeTsvPath,
                    curEscapeTsvPath,
                )
            }
//            val escapeFilePathList =
//                AssetsFileManager.readFromAssets(
//                    context,
//                    escapeFilePath
//                ).split("\n").filter { it.isNotEmpty() }
            withContext(Dispatchers.IO) {
                FileSystems.updateFile(
                    File(UsePath.cmdclickDefaultAppDirPath, "escape.txt").absolutePath,
                    listOf(
                        "${escapeMapList}"
                    ).joinToString("\n\b") + "\n-------------\n\n"
                )
                AssetsFileManager.copyFileOrDirFromAssets(
                    context,
                    fannelRawDirAssetsPath,
                    fannelRawDirAssetsPath,
                    targetAppDirPath,
                    escapeMapList
                )
            }
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
        ).split("\n").map {
            line ->
            val trimLine = line.trim()
            if(
                trimLine.isEmpty()
            ) return@map String()
            val relativePathAndVersion =
                trimLine.split("\t")
            val relativePath = relativePathAndVersion
                .firstOrNull()
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
            when(isEscape){
                false -> String()
                else -> relativePath
            }
        }.filter {
            it.isNotEmpty()
        }
    }
}