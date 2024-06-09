package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import android.content.Context
import androidx.fragment.app.Fragment
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
            "cmdclickConfig",
            "1",
        )
    }


    private fun createFannelByVer(
        context: Context?,
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