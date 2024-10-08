package com.puutaro.commandclick.proccess.filer

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object StartFileMaker {

    fun makeCmdTerminalListFiles(
        fragment: Fragment,
    ){
        val context = fragment.context
        val fannelDirName = CcPathTool.makeFannelDirName(
            SystemFannel.tapTerminal
        )
        makeCmdTerminalListFile(
            context,
            fannelDirName,
            AssetsFileManager.cmdListTxt,
        )
        makeCmdTerminalListFile(
            context,
            fannelDirName,
            AssetsFileManager.extraKeyListTxt,
        )
    }

//    fun makeForConfig(
////        fragment: Fragment,
//    ){
//        CoroutineScope(Dispatchers.IO).launch {
//            InitSettingListFile.makeFile(
//                UsePath.homeFannelsFilePath,
////                UsePath.cmdclickDefaultAppDirPath,
//                UsePath.cmdclickConfigFileName,
//                listOf(
//                    "${UsePath.cmdclickDefaultAppDirName}__.js",
//                )
//            )
//        }
//    }

//    private fun makeFannelScript(
//        fragment: Fragment,
//        currentAppDirPath: String,
//        fanneName: String,
//        assetsRelativePath: String
//    ){
//        val fannelPathObj = File(
//            currentAppDirPath,
//            fanneName
//        )
//        if(
//            fannelPathObj.isFile
//        ) return
//        val context = fragment.context
//        val content = AssetsFileManager.readFromAssets(
//            context,
//            assetsRelativePath
//        )
//        FileSystems.writeFile(
//            fannelPathObj.absolutePath,
//            content
//        )
//    }
}

//private object InitSettingListFile {
//
//
//    fun makeFile(
//        settingListFilePathSrc: String,
////        currentAppDirPath: String,
//        scriptName: String,
//        settingConList: List<String>,
//    ){
//        val settingListFilePath = ScriptPreWordReplacer.pathReplace(
//            settingListFilePathSrc,
////            currentAppDirPath,
//            scriptName
//        )
//        val settingListFilePathObj =
//            File(settingListFilePath)
//        if(
//            settingListFilePathObj.isFile
//        ) return
//        val contents = settingConList.joinToString("\n")
//        FileSystems.writeFile(
//            settingListFilePathObj.absolutePath,
//            contents
//        )
//    }
//}

private fun makeCmdTerminalListFile(
    context: Context?,
    fannelDirName: String,
    targetAssetsFilePath: String,
) {
    val fannelDirPath = "${UsePath.cmdclickDefaultAppDirPath}/${fannelDirName}"
    val listFilePath = targetAssetsFilePath.replace(
        AssetsFileManager.cmdTerminalDirPath,
        fannelDirPath
    )
    val listFilePathObj = File(listFilePath)
    if(
        listFilePathObj.isFile
    ) return
    FileSystems.writeFile(
        listFilePath,
        AssetsFileManager.readFromAssets(
            context,
            targetAssetsFilePath
        )
    )
}