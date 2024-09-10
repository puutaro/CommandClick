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
//        val start = LocalDateTime.now()
        if(
            currentUrl.isNullOrEmpty()
            ||  currentUrl.contains("/maps/")
            || capture == null
        ) {
//            CoroutineScope(Dispatchers.Main).launch{
//                ToastUtils.showShort("exit ${currentUrl}")
//            }
            return
        }
        val isNotHttp = !EnableUrlPrefix.isHttpPrefix(currentUrl)
        if(
            isNotHttp
        ) {
//            CoroutineScope(Dispatchers.Main).launch{
//                ToastUtils.showShort("exit isNotHttp: ${currentUrl}")
//            }
            return
        }
        val curHash = BitmapTool.hash(capture)
        val curRawSize = BitmapTool.convertBitmapToByteArray(capture).size
//            val curSize = curRawSize - curRawSize % 10000
        val saveDiffThreshold = (baseThresholdDiff * pxWidth) / baseResolutionWidth
        if(
            Math.abs(curRawSize - prevSize) < saveDiffThreshold
        ) return
        prevSize = curRawSize
//        val capDirPath = File(UsePath.cmdclickDefaultAppDirPath, "cap").absolutePath
//        FileSystems.createDirs(
//            capDirPath
//        )
//        BitmapTool.convertBitmapToByteArray(capture).let {
//            FileSystems.writeFromByteArray(
//                File(capDirPath, "${LocalDateTime.now().toString().replace(Regex("[^a-zA-Z0-9_-]"), "")}_${prevSize}_${title?.take(20)}.png").absolutePath,
//                it,
//            )
//        }
//        if(
//            curHash == beforeHash
//        ) {
////            CoroutineScope(Dispatchers.Main).launch {
////                ToastUtils.showShort("cannot save curHash")
////            }
//            return
//        }
//        beforeHash = curHash
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
//            currentAppDirPath,
            currentUrl
        )
        FileSystems.createDirs(captureUniqueDirPath)
        val partsPngDirPath = UrlHistoryPath.getCapturePartsPngDirPath(
//            currentAppDirPath,
            currentUrl,
        )
//        val isPartsPng = FileSystems.sortedFiles(
//            partsPngDirPath
//        ).isNotEmpty()
        execTrimFiles(partsPngDirPath)
        val partPngName = "${curHash}.png"
//        CoroutineScope(Dispatchers.Main).launch {
//            ToastUtils.showShort("save ${File(partsPngDirPath, partPngName).absolutePath}")
//        }
        FileSystems.writeFromByteArray(
            File(partsPngDirPath, partPngName).absolutePath,
            byteArray,
        )
//        val gifTxtPath = UrlHistoryPath.getCaptureGifTextPath(
////            currentAppDirPath,
//            currentUrl,
//        )
        val gifPath = UrlHistoryPath.getCaptureGifPath(
            currentUrl,
        )
        FileSystems.writeFile(
            UrlHistoryPath.makeCaptureHistoryLastModifiedFilePath(
                currentUrl,
            ),
            String()
        )
        if(!File(gifPath).isFile){
//            FileSystems.writeFromByteArray(
//                gifPath,
//                BitmapTool.convertBitmapToByteArray(smallBitmap)
//            )
//            BitmapTool.Base64Tool.encode(smallBitmap)?.let {
                FileSystems.writeFromByteArray(
                    gifPath,
                    BitmapTool.convertBitmapToByteArray(smallBitmap),
                )
//            }
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
