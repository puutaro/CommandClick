package com.puutaro.commandclick.proccess.history

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import java.io.File
import java.time.LocalDateTime


object UrlCaptureHistoryTool {

    const val takeHistoryNum = 200
    var beforeByteCount = 0

    fun insertToHistory(
        currentAppDirPath: String,
        currentUrl: String?,
        capture: Bitmap?,
    ){
        val start = LocalDateTime.now()
        if(
            currentUrl.isNullOrEmpty()
            ||  currentUrl.contains("/maps/")
            || capture == null
        ) return
        val isNotHttp = !currentUrl.startsWith(WebUrlVariables.httpsPrefix)
                && !currentUrl.startsWith(WebUrlVariables.httpsPrefix)
        if(
            isNotHttp
        ) return

//        val base64Str = BitmapTool.Base64Tool.encode(
//            capture,
//            10
//        ) ?: return
        val curByteCount = capture.byteCount
        if(
            curByteCount == beforeByteCount
        ) return
        beforeByteCount = curByteCount
        val convertBitmapToByteArrayStart = LocalDateTime.now()
        val byteArray = BitmapTool.convertBitmapToByteArray(capture, 10)
        val convertBitmapToByteArrayEnd = LocalDateTime.now()
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "capture_url.txt").absolutePath,
//            currentUrl
//        )
//        FileSystems.savePngFromBitMap(
//            File(UsePath.cmdclickDefaultAppDirPath, "capture.png").absolutePath,
//            capture
//        )
        val partsPngDirPath = UrlHistoryPath.getCapturePartsPngDirPath(
            currentAppDirPath,
            currentUrl,
        )
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "g_url_dir.txt").absolutePath,
            listOf(
                "partsPngDirPath: ${partsPngDirPath}",
                "currentAppDirPath: ${currentAppDirPath}",
                "currentUrl: ${currentUrl}"
            ).joinToString("\n")
        )
        execTrimFiles(partsPngDirPath)
        val partPngName = CommandClickScriptVariable.makeRndPrefix() + ".png"
        FileSystems.writeFromByteArray(
            File(partsPngDirPath, partPngName).absolutePath,
            byteArray,
        )
//        val gifTxtPath = UrlHistoryPath.getCaptureGifTextPath(
//            currentAppDirPath,
//            currentUrl,
//        )
        val gifPath = UrlHistoryPath.getCaptureGifPath(
            currentAppDirPath,
            currentUrl,
        )
        FileSystems.writeFile(
            UrlHistoryPath.makeCaptureHistoryLastModifiedFilePath(
                currentAppDirPath,
                currentUrl,
            ),
            String()
        )
        if(!File(gifPath).isFile){
            FileSystems.writeFromByteArray(
                gifPath,
                BitmapTool.convertBitmapToByteArray(capture)
            )
//            BitmapTool.Base64Tool.encode(capture)?.let {
//                FileSystems.writeFile(
//                    gifPath,
//                    it,
//                )
//            }
            return
        }
//        val createGifStart = LocalDateTime.now()
//        UrlHistoryPath.getCapturePngPathsByUrl(
//            currentAppDirPath,
//            currentUrl,
//        )?.map {
//            BitmapTool.convertFileToBitmap(it)
////            BitmapTool.Base64Tool.decode(
////                ReadText(it).readText()
////            )
//        }.let {
//            if(it.isNullOrEmpty()) {
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "gif_failue.txt").absolutePath,
//                        listOf(
//                            "dirPath: ${File(
//                            UrlHistoryPath.makeCaptureHistoryDirPath(currentAppDirPath),
//                            UrlHistoryPath.makePathNameFromUrl(currentUrl),
//                        ).absolutePath}",
//                            "getCaptureBase64TxtPathsByUrl: ${UrlHistoryPath.getCapturePngPathsByUrl(
//                                currentAppDirPath,
//                                currentUrl,
//                            )}"
//                        ).joinToString("\n")
//                )
//                return@let
//            }
//            val execCreateGifStart = LocalDateTime.now()
//            val bo = BitmapTool.generateGIF(it)
//            val execCreateGifEnd = LocalDateTime.now()
//////            BitmapTool.saveGif(
//////                File(UsePath.cmdclickDefaultAppDirPath, "gif.gif").absolutePath,
//////                bo
//////            )
//           BitmapTool.saveGifTxt(
//               gifTxtPath,
//               bo,
//           )
//            val createGifEnd = LocalDateTime.now()
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "gif_create_log.txt").absolutePath,
//                listOf(
//                    "start: ${start}",
//                    "convertBitmapToByteArrayStart: ${convertBitmapToByteArrayStart}",
//                    "convertBitmapToByteArrayEnd: ${convertBitmapToByteArrayEnd}",
//                    "createGifStart: ${createGifStart}",
//                    "execCreateGifStart: ${execCreateGifStart}",
//                    "execCreateGifEnd: ${execCreateGifEnd}",
//                    "createGifEnd: ${createGifEnd}",
//                ).joinToString("\n")
//            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "gif_failue2.txt").absolutePath,
//                listOf(
//                    "dirPath: ${File(
//                        UrlHistoryPath.makeCaptureHistoryDirPath(currentAppDirPath),
//                        UrlHistoryPath.makePathNameFromUrl(currentUrl),
//                    ).absolutePath}",
//                    "getCaptureBase64TxtPathsByUrl: ${UrlHistoryPath.getCapturePngPathsByUrl(
//                        currentAppDirPath,
//                        currentUrl,
//                    )}",
//                    "gifTxtPath: ${gifTxtPath}"
//                ).joinToString("\n")
//            )
//
//        }
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

    private fun takeFirst200Str(base64Str: String): String {
        return base64Str.take(200)
    }

}