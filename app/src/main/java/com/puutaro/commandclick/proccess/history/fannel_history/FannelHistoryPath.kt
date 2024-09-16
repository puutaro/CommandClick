package com.puutaro.commandclick.proccess.history.fannel_history

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import java.io.File

object FannelHistoryPath {

    val captureDirName = "capture"
    val faceDirName = "face"
    val partPngDirName = "partPng"
    val gifFileName = "gif.gif"
    val gifDesignName = "gifDesign.txt"

    fun makePartPngDirCut(): String {
        return listOf(
            UsePath.settingImagesDirName,
            captureDirName,
            partPngDirName,
        ).joinToString("/")
    }


    fun getCapturePartsPngDirPath(
//        currentAppDirPath: String,
        currentFannelName: String?,
    ): String {
        return listOf(
            makeFannelSettingImagesCaptureDirPath(
//                currentAppDirPath,
                currentFannelName,
            ),
            partPngDirName
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }

    fun getCaptureFacePngDirPath(
//        currentAppDirPath: String,
        currentFannelName: String?,
    ): String {
        return listOf(
            makeFannelSettingImagesCaptureDirPath(
//                currentAppDirPath,
                currentFannelName,
            ),
            faceDirName
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }


    fun getCaptureGifPath(
//        currentAppDirPath: String,
        currentFannelName: String?,
    ): String {
        return listOf(
            makeFannelSettingImagesCaptureDirPath(
//                currentAppDirPath,
                currentFannelName,
            ),
            gifFileName
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }

    fun getCaptureGifDesignPath(
//        currentAppDirPath: String,
        currentFannelName: String?,
    ): String {
        return listOf(
            makeFannelSettingImagesCaptureDirPath(
//                currentAppDirPath,
                currentFannelName,
            ),
            gifDesignName
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }

    private fun makeFannelSettingImagesCaptureDirPath(
//        currentAppDirPath: String,
        currentFannelName: String?,
    ): String {
        return File(
            makeFannelSettingImagesDirPath(
//                currentAppDirPath,
                currentFannelName,
            ),
            captureDirName
        ).absolutePath
    }

    private fun makeFannelSettingImagesDirPath(
//        currentAppDirPath: String,
        currentFannelName: String?,
    ): String {
        val replaceFannelName = when(
            currentFannelName.isNullOrEmpty()
                    || currentFannelName == FannelInfoSetting.current_fannel_name.defalutStr
        ) {
            true -> UsePath.cmdclickAppDirSystemFannelName
            else -> currentFannelName
        }
        return ScriptPreWordReplacer.replace(
            UsePath.fannelSettingImagesDirPath,
//            currentAppDirPath,
            replaceFannelName
        )
    }
}