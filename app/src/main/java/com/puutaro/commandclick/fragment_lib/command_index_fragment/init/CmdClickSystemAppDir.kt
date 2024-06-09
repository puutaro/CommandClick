package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object CmdClickSystemAppDir {

    private val appSystemDirName = "appSystemDir"
    fun create(
        cmdIndexFragment: CommandIndexFragment
    ){
        CoroutineScope(Dispatchers.IO).launch {
            val context = cmdIndexFragment.context
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
                createFannelByVer(
                    cmdIndexFragment,
                    "cmdclickConfig",
                    "1",
                )
            }


        }
    }


    private fun createFannelByVer(
        cmdIndexFragment: CommandIndexFragment,
        fannelRawName: String,
        version: String,
    ){
        val context = cmdIndexFragment.context
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
        val versionFilePath =
            File(
                UsePath.cmdclickSystemAppDirPath,
                "${fannelDirName}/${infoDirName}/${versionTextName}"
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
            val escapeFilePath =
                "${fannelInfoDirAssetsPath}/escape.txt"
            val escapeFilePathList =
                AssetsFileManager.readFromAssets(
                    context,
                    escapeFilePath
                ).split("\n").filter { it.isNotEmpty() }
            AssetsFileManager.copyFileOrDirFromAssets(
                context,
                fannelRawDirAssetsPath,
                fannelRawDirAssetsPath,
                UsePath.cmdclickSystemAppDirPath,
                escapeFilePathList
            )
        }
    }
}