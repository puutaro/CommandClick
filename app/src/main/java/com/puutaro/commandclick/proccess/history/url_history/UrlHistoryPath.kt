package com.puutaro.commandclick.proccess.history.url_history

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.url.WebUrlVariables
import java.io.File


object UrlHistoryPath {

    const val lastModifyExtend = ".lastModified"
    const val partPngDirName = "partPng"
    val toolbarUrlImageDirPath = File(UsePath.cmdclickFannelSystemDirPath, "toolbarButtonImage").absolutePath

    fun makePathNameFromUrl(
        url: String
    ): String {
        val originalUrl = url.trim().let {
            CcPathTool.toValidPathWord(it.hashCode().toString())
        }
        return originalUrl.take(20)
    }

    fun getCapturePngPathsByUrl(
//        currentAppDirPath: String,
        url: String,
    ): List<String>? {
        val capturePartsPngDirPath = getCapturePartsPngDirPath(
//            currentAppDirPath,
            url
        )
        if(
            !File(capturePartsPngDirPath).isDirectory
        ) return null
        return FileSystems.sortedFiles(
            capturePartsPngDirPath
        ).map {
            File(capturePartsPngDirPath, it).absolutePath
        }
    }

    fun makeCaptureHistoryDirPath(
//        currentAppDirPath: String
    ): String {
        return File(
            File(UsePath.cmdclickDefaultAppDirPath, UsePath.cmdclickUrlSystemDirRelativePath).absolutePath,
            "capture"
        ).absolutePath
    }

    fun makeCaptureHistoryLastModifiedFilePath(
//        currentAppDirPath: String,
        url: String,
    ): String {
        return File(
            makeCaptureHistoryDirPath(),
            makePathNameFromUrl(url),
        ).absolutePath + lastModifyExtend
    }

    fun getCaptureUniqueDirPath(
//        currentAppDirPath: String,
        url: String
    ): String {
        return File(
            makeCaptureHistoryDirPath(),
            makePathNameFromUrl(url),
        ).absolutePath
    }


    fun getCapturePartsPngDirPath(
//        currentAppDirPath: String,
        currentUrl: String,
    ): String {
        return listOf(
            makeCaptureHistoryDirPath(),
            makePathNameFromUrl(currentUrl),
            partPngDirName
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }

//    fun getCaptureGifTextPath(
////        currentAppDirPath: String,
//        currentUrl: String,
//    ): String {
//        return listOf(
//            makeCaptureHistoryDirPath(),
//            makePathNameFromUrl(currentUrl),
//            "gif.txt"
//        ).joinToString("/").replace(
//            Regex("[/]+"), "/"
//        )
//    }

    fun getCaptureGifPath(
//        currentAppDirPath: String,
        currentUrl: String,
    ): String {
        return listOf(
            makeCaptureHistoryDirPath(),
            makePathNameFromUrl(currentUrl),
            "gif.gif"
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }

}