package com.puutaro.commandclick.fragment_lib.terminal_fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.UrlHistoryPath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

object GifCreateMonitor {

    fun watch(
        terminalFragment: TerminalFragment,
    ){
        val lastModifyExtend = UrlHistoryPath.lastModifyExtend
        val currentAppDirPath = terminalFragment.currentAppDirPath
        val captureHistoryDirPath =
            UrlHistoryPath.makeCaptureHistoryDirPath(
                currentAppDirPath
            )
        terminalFragment.lifecycleScope.launch {
            terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED){
                withContext(Dispatchers.IO) {
                    var previousPngList: List<String> = emptyList()
                    while(true) {
                        delay(2000)
                        val firstUpdateDirName = FileSystems.sortedFiles(
                            captureHistoryDirPath,
                            "on"
                        ).firstOrNull()
                            ?.removeSuffix(lastModifyExtend)
                            ?: continue
                        FileSystems.writeFile(
                            File(UsePath.cmdclickDefaultAppDirPath,"gfirstUpdateDirName.txt").absolutePath,
                            listOf(
                                "firstUpdateDirName: ${firstUpdateDirName}"
                            ).joinToString("\n")
                        )
                        val capturePartsPngDirPath = getCapturePartsPngDirPath(
                            currentAppDirPath,
                            firstUpdateDirName
                        )
                        val curPngPathList =
                            FileSystems.sortedFiles(capturePartsPngDirPath).map {
                                File(capturePartsPngDirPath, it).absolutePath
                            }
                        if(
                            previousPngList.isEmpty()
                        ) previousPngList = curPngPathList
                        if(
                            curPngPathList.size == 1
                            || curPngPathList == previousPngList
                        ) continue
                        previousPngList = curPngPathList

                        createGifFromPngs(
                            curPngPathList,
                            currentAppDirPath,
                            firstUpdateDirName,
                        )

                    }
                }
            }
        }


    }

    private fun createGifFromPngs(
        pingPathList: List<String>,
        currentAppDirPath: String,
        urlUniqueDirName: String,
    ){
        val bitMapList = pingPathList.map {
            BitmapTool.convertFileToBitmap(it)
        }
        if(bitMapList.isEmpty()) {
            FileSystems.writeFile(
                File(UsePath.cmdclickDefaultAppDirPath, "gif_failue.txt").absolutePath,
                listOf(
                    "pingPathList: ${pingPathList}",
                    "currentAppDirPath: ${currentAppDirPath}"
                ).joinToString("\n")
            )
            return
        }
        val execCreateGifStart = LocalDateTime.now()
        val bo = BitmapTool.generateGIF(bitMapList)
        val execCreateGifEnd = LocalDateTime.now()
////            BitmapTool.saveGif(
////                File(UsePath.cmdclickDefaultAppDirPath, "gif.gif").absolutePath,
////                bo
////            )
        val gifTxtPath = getCaptureGifPath(
            currentAppDirPath,
            urlUniqueDirName
        )
        BitmapTool.saveGif(
            gifTxtPath,
            bo
        )
//        BitmapTool.saveGifTxt(
//            gifTxtPath,
//            bo,
//        )
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
            FileSystems.writeFile(
                File(UsePath.cmdclickDefaultAppDirPath, "gif_failue2.txt").absolutePath,
                listOf(
                    "currentAppDirPath: ${currentAppDirPath}",
                    "dirPath: ${pingPathList}",
                    "urlUniqueDirName: ${urlUniqueDirName}",
                    "gifTxtPath: ${gifTxtPath}"
                ).joinToString("\n")
            )

    }

    private fun getCapturePartsPngDirPath(
        currentAppDirPath: String,
        urlUniqueDirName: String
    ): String {
        return listOf(
            UrlHistoryPath.makeCaptureHistoryDirPath(currentAppDirPath),
            urlUniqueDirName,
            "partPng"
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }

    fun getCaptureGifPath(
        currentAppDirPath: String,
        urlUniqueDirName: String
    ): String {
        return listOf(
            UrlHistoryPath.makeCaptureHistoryDirPath(currentAppDirPath),
            urlUniqueDirName,
            "gif.gif"
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }

    fun getCaptureGifTextPath(
        currentAppDirPath: String,
        urlUniqueDirName: String
    ): String {
        return listOf(
            UrlHistoryPath.makeCaptureHistoryDirPath(currentAppDirPath),
            urlUniqueDirName,
            "gif.txt"
        ).joinToString("/").replace(
            Regex("[/]+"), "/"
        )
    }
}