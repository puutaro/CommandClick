package com.puutaro.commandclick.proccess.history.url_history

import android.graphics.Bitmap
import com.puutaro.commandclick.util.url.EnableUrlPrefix
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import java.io.File


object UrlCaptureHistoryTool {

    const val takeHistoryNum = 200
    var prevSize = 1000
    private const val baseThresholdDiff = 50_000
    private const val baseResolutionWidth = 1080

    fun insertToHistory(
        currentUrl: String?,
        capture: Bitmap?,
        pxWidth: Int
    ){
        if(
            currentUrl.isNullOrEmpty()
            ||  currentUrl.contains("/maps/")
            || capture == null
        ) {
            return
        }
        val isNotHttp = !EnableUrlPrefix.isHttpPrefix(currentUrl)
        if(
            isNotHttp
        ) {
            return
        }
        val curHash = BitmapTool.hash(capture)
        val curRawSize = BitmapTool.convertBitmapToByteArray(capture).size
        val saveDiffThreshold = (baseThresholdDiff * pxWidth) / baseResolutionWidth
        if(
            Math.abs(curRawSize - prevSize) < saveDiffThreshold
        ) return
        prevSize = curRawSize
        val smallBitmap = BitmapTool.resizeByMaxHeight(capture, 700.0)
        val byteArray = BitmapTool.convertBitmapToByteArray(smallBitmap, 100)
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "g_url_dir.txt").absolutePath,
//            listOf(
//                "partsPngDirPath: ${partsPngDirPath}",
//                "currentAppDirPath: ${currentAppDirPath}",
//                "currentUrl: ${currentUrl}"
//            ).joinToString("\n")
//        )
        val captureUniqueDirPath = UrlHistoryPath.getCaptureUniqueDirPath(
            currentUrl
        )
        FileSystems.createDirs(captureUniqueDirPath)
        val partsPngDirPath = UrlHistoryPath.getCapturePartsPngDirPath(
            currentUrl,
        )
        execTrimFiles(partsPngDirPath)
        val partPngName = "${curHash}.png"
        FileSystems.writeFromByteArray(
            File(partsPngDirPath, partPngName).absolutePath,
            byteArray,
        )
        FileSystems.writeFile(
            UrlHistoryPath.makeCaptureHistoryLastModifiedFilePath(
                currentUrl,
            ),
            String()
        )
        val gifPath = UrlHistoryPath.getCaptureGifPath(
            currentUrl,
        )
        if(!File(gifPath).isFile){
                FileSystems.writeFromByteArray(
                    gifPath,
                    BitmapTool.convertBitmapToByteArray(smallBitmap),
                )
            return
        }
    }

    private fun execTrimFiles(
        captureSaveDir: String
    ){
        val lastModifiedCapturePngPathList =
            FileSystems.sortedFiles(
                captureSaveDir,
                "on"
            )
        val totalNum = lastModifiedCapturePngPathList.size
        val limitNum = 2
        val removeNum = totalNum - limitNum
        if(removeNum <= 0) return
        lastModifiedCapturePngPathList.takeLast(removeNum).forEach {
            FileSystems.removeFiles(
                File(captureSaveDir, it).absolutePath
            )
        }
    }
}
