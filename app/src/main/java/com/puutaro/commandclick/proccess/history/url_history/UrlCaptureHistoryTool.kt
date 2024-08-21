package com.puutaro.commandclick.proccess.history.url_history

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.url.EnableUrlPrefix
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import java.io.File


object UrlCaptureHistoryTool {

    const val takeHistoryNum = 200
    private var beforeHash = String()

    fun insertToHistory(
//        currentAppDirPath: String,
        currentUrl: String?,
        capture: Bitmap?,
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
        if(
            curHash == beforeHash
        ) {
//            CoroutineScope(Dispatchers.Main).launch {
//                ToastUtils.showShort("cannot save curHash")
//            }
            return
        }
        beforeHash = curHash
        val smallBitmap = BitmapTool.resizeByMaxHeight(capture, 700.0)
        val byteArray = BitmapTool.convertBitmapToByteArray(smallBitmap, 100)
        val captureUniqueDirPath = UrlHistoryPath.getCaptureUniqueDirPath(
//            currentAppDirPath,
            currentUrl
        )
        FileSystems.createDirs(captureUniqueDirPath)
        val partsPngDirPath = UrlHistoryPath.getCapturePartsPngDirPath(
//            currentAppDirPath,
            currentUrl,
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "g_url_dir.txt").absolutePath,
//            listOf(
//                "partsPngDirPath: ${partsPngDirPath}",
//                "currentAppDirPath: ${currentAppDirPath}",
//                "currentUrl: ${currentUrl}"
//            ).joinToString("\n")
//        )
        execTrimFiles(partsPngDirPath)
        val partPngName = "${curHash}.png"
//        CoroutineScope(Dispatchers.Main).launch {
//            ToastUtils.showShort("save ${File(partsPngDirPath, partPngName).absolutePath}")
//        }
        FileSystems.writeFromByteArray(
            File(partsPngDirPath, partPngName).absolutePath,
            byteArray,
        )
        FileSystems.writeFromByteArray(
            File(UsePath.cmdclickDefaultAppDirPath, "gif.png").absolutePath,
            byteArray,
        )
        val gifTxtPath = UrlHistoryPath.getCaptureGifTextPath(
//            currentAppDirPath,
            currentUrl,
        )
//        val gifPath = UrlHistoryPath.getCaptureGifPath(
//            currentAppDirPath,
//            currentUrl,
//        )
        FileSystems.writeFile(
            UrlHistoryPath.makeCaptureHistoryLastModifiedFilePath(
//                currentAppDirPath,
                currentUrl,
            ),
            String()
        )
        if(!File(gifTxtPath).isFile){
//            FileSystems.writeFromByteArray(
//                gifPath,
//                BitmapTool.convertBitmapToByteArray(smallBitmap)
//            )
            BitmapTool.Base64Tool.encode(smallBitmap)?.let {
                FileSystems.writeFile(
                    gifTxtPath,
                    it,
                )
            }
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