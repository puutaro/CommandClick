package com.puutaro.commandclick.proccess

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import java.io.File

object StartUpScriptMaker {

    private val cmdclickStartupJsName = UsePath.cmdclickStartupJsName
    private val startupFannelDirName = cmdclickStartupJsName
        .removeSuffix(UsePath.JS_FILE_SUFFIX)
        .removeSuffix(UsePath.SHELL_FILE_SUFFIX) +
            "Dir"

    fun make(
        fragment: Fragment,
        currentAppDirPath: String
    ){
        makeFannelScript(
            fragment,
            currentAppDirPath,
            AssetsFileManager.siteSumamryFannelName,
            AssetsFileManager.assetsLongPressSiteSummaryPath
        )
        makeFannelScript(
            fragment,
            currentAppDirPath,
            AssetsFileManager.urlIntenderFannelName,
            AssetsFileManager.assetsLongPressUrlIntenderPath
        )
        CommandClickScriptVariable.makeAutoJsFile(
            currentAppDirPath,
            cmdclickStartupJsName
        )
        val longPressMenuDirPath = longPressFilePathReplace(
            UsePath.longPressMenuDirPath,
            currentAppDirPath,
        )
        FileSystems.createDirs(
            longPressMenuDirPath
        )
        makeSrcImageAnchorLongPressMenuFilePath(
            currentAppDirPath,
        )
        makeImageLongPressMenuFilePath(
            currentAppDirPath,
        )

        makeSrcAnchorLongPressMenuFilePath(
            currentAppDirPath,
        )
    }

    private fun longPressFilePathReplace(
        longPressFilePath: String,
        currentAppDirPath: String,
    ): String {
        return longPressFilePath.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                startupFannelDirName,
                cmdclickStartupJsName,
            )
        }
    }

    private fun makeSrcImageAnchorLongPressMenuFilePath(
        currentAppDirPath: String,
    ){
        val srcImageAnchorLongPressMenuFilePath = longPressFilePathReplace(
            UsePath.srcImageAnchorLongPressMenuFilePath,
            currentAppDirPath,
        )
        val srcImageAnchorLongPressMenuFilePathObj =
            File(srcImageAnchorLongPressMenuFilePath)
        if(
            srcImageAnchorLongPressMenuFilePathObj.isFile
        ) return
        val srcImageAnchorLongPressMenuDirPath = srcImageAnchorLongPressMenuFilePathObj.parent
            ?: return
        val srcImageAnchorLongPressMenuFileName = srcImageAnchorLongPressMenuFilePathObj.name
        val contents = listOf(
            AssetsFileManager.urlIntenderFannelName,
            AssetsFileManager.siteSumamryFannelName
        ).joinToString("\n")
        FileSystems.writeFile(
            srcImageAnchorLongPressMenuDirPath,
            srcImageAnchorLongPressMenuFileName,
            contents
        )
    }

    private fun makeSrcAnchorLongPressMenuFilePath(
        currentAppDirPath: String,
    ){
        val srcAnchorLongPressMenuFilePath = longPressFilePathReplace(
            UsePath.srcAnchorLongPressMenuFilePath,
            currentAppDirPath,
        )
        val srcAnchorLongPressMenuFilePathObj =
            File(srcAnchorLongPressMenuFilePath)
        if(
            srcAnchorLongPressMenuFilePathObj.isFile
        ) return
        val srcAnchorLongPressMenuDirPath = srcAnchorLongPressMenuFilePathObj.parent
            ?: return
        val srcAnchorLongPressMenuFileName = srcAnchorLongPressMenuFilePathObj.name
        val contents = listOf(
            AssetsFileManager.urlIntenderFannelName,
            AssetsFileManager.siteSumamryFannelName
        ).joinToString("\n")
        FileSystems.writeFile(
            srcAnchorLongPressMenuDirPath,
            srcAnchorLongPressMenuFileName,
            contents
        )
    }

    private fun makeImageLongPressMenuFilePath(
        currentAppDirPath: String,
    ){
        val imageLongPressMenuFilePath = longPressFilePathReplace(
            UsePath.imageLongPressMenuFilePath,
            currentAppDirPath,
        )
        val imageLongPressMenuFilePathObj =
            File(imageLongPressMenuFilePath)
        if(
            imageLongPressMenuFilePathObj.isFile
        ) return
        val imageLongPressMenuDirPath = imageLongPressMenuFilePathObj.parent
            ?: return
        val imageLongPressMenuFileName = imageLongPressMenuFilePathObj.name
        val contents = listOf(
            AssetsFileManager.urlIntenderFannelName,
        ).joinToString("\n")
        FileSystems.writeFile(
            imageLongPressMenuDirPath,
            imageLongPressMenuFileName,
            contents
        )
    }

    private fun makeFannelScript(
        fragment: Fragment,
        currentAppDirPath: String,
        fanneName: String,
        assetsRelativePath: String
    ){
        if(
            File(
                currentAppDirPath,
                fanneName
            ).isFile
        ) return
        val context = fragment.context
        val content = AssetsFileManager.readFromAssets(
            context,
            assetsRelativePath
        )
        FileSystems.writeFile(
            currentAppDirPath,
            fanneName,
            content
        )
    }
}