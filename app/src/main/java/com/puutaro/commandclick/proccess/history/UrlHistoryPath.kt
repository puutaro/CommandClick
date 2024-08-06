package com.puutaro.commandclick.proccess.history

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File


object UrlHistoryPath {

    const val lastModifyExtend = ".lastModified"

    fun makePathNameFromUrl(
        url: String
    ): String {
        return url.replace(
            Regex("[^a-zA-Z0-9_%-]+"),
            ""
        ).takeLast(20)
    }

    fun getCapturePngPathsByUrl(
        currentAppDirPath: String,
        url: String,
    ): List<String>? {
        val capturePartsPngDirPath = getCapturePartsPngDirPath(
            currentAppDirPath,
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
        currentAppDirPath: String
    ): String {
        return File(
            File(currentAppDirPath, UsePath.cmdclickUrlSystemDirRelativePath).absolutePath,
            "capture"
        ).absolutePath
    }

    fun makeCaptureHistoryLastModifiedFilePath(
        currentAppDirPath: String,
        url: String,
    ): String {
        return File(
            makeCaptureHistoryDirPath(currentAppDirPath),
            makePathNameFromUrl(url),
        ).absolutePath + lastModifyExtend
    }

    fun getCaptureUniqueDirPath(
        currentAppDirPath: String,
        url: String
    ): String {
        return File(
            makeCaptureHistoryDirPath(currentAppDirPath),
            makePathNameFromUrl(url),
        ).absolutePath
    }


    fun getCapturePartsPngDirPath(
        currentAppDirPath: String,
        currentUrl: String,
    ): String {
        return listOf(
            makeCaptureHistoryDirPath(currentAppDirPath),
            makePathNameFromUrl(currentUrl),
            "partPng"
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }

    fun getCaptureGifTextPath(
        currentAppDirPath: String,
        currentUrl: String,
    ): String {
        return listOf(
            makeCaptureHistoryDirPath(currentAppDirPath),
            makePathNameFromUrl(currentUrl),
            "gif.txt"
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }

    fun getCaptureGifPath(
        currentAppDirPath: String,
        currentUrl: String,
    ): String {
        return listOf(
            makeCaptureHistoryDirPath(currentAppDirPath),
            makePathNameFromUrl(currentUrl),
            "gif.gif"
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }

}