package com.puutaro.commandclick.fragment_lib.terminal_fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryPath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object GifCreateMonitor {

    fun watch(
        terminalFragment: TerminalFragment,
    ){
        val lastModifyExtend = UrlHistoryPath.lastModifyExtend
//        val currentAppDirPath = terminalFragment.currentAppDirPath
        val captureHistoryDirPath =
            UrlHistoryPath.makeCaptureHistoryDirPath(
//                currentAppDirPath
            )
        //: 0 create 1: no_create
//        var gitCreteTimes = 0
        terminalFragment.lifecycleScope.launch {
            terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.IO) {
                    var previousPngList: List<String> = emptyList()
                    while (true) {
                        delay(3_000)
//                        if(gitCreteTimes == 0) {
//                            gitCreteTimes = 1
//                            saveGifFromBitmapList(
//                                currentAppDirPath,
//                            )
//                        }
                        val firstUpdateDirName = FileSystems.sortedFiles(
                            captureHistoryDirPath,
                            "on"
                        ).firstOrNull()
                            ?.removeSuffix(lastModifyExtend)
                            ?: continue
//                        val captureUniqueDirPath = getCaptureUniqueDirPath(
//                            currentAppDirPath,
//                            firstUpdateDirName
//                        )
//                        FileSystems.writeFile(
//                            File(
//                                UsePath.cmdclickDefaultAppDirPath,
//                                "gfirstUpdateDirName.txt"
//                            ).absolutePath,
//                            listOf(
//                                "firstUpdateDirName: ${firstUpdateDirName}",
//                                "captureUniqueDirPath: ${captureUniqueDirPath}",
//                            ).joinToString("\n")
//                        )
                        val capturePartsPngDirPath = getCapturePartsPngDirPath(
//                            currentAppDirPath,
                            firstUpdateDirName
                        )
                        val curPngPathList =
                            FileSystems.sortedFiles(capturePartsPngDirPath).map {
                                File(capturePartsPngDirPath, it).absolutePath
                            }
                        if (
                            previousPngList.isEmpty()
                        ) previousPngList = curPngPathList
                        if (
                            curPngPathList.size == 1
                            || curPngPathList == previousPngList
                        ) continue
                        previousPngList = curPngPathList

                        createGifFromPngs(
                            curPngPathList,
//                            currentAppDirPath,
                            firstUpdateDirName,
                        )

                    }
                }
            }
        }
    }

    private fun createGifFromPngs(
        pingPathList: List<String>,
//        currentAppDirPath: String,
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
//                    "currentAppDirPath: ${currentAppDirPath}"
                ).joinToString("\n")
            )
            return
        }
        val bo = BitmapTool.generateGIF(bitMapList)
////            BitmapTool.saveGif(
////                File(UsePath.cmdclickDefaultAppDirPath, "gif.gif").absolutePath,
////                bo
////            )
//        val gifTxtPath = getCaptureGifTextPath(
//            currentAppDirPath,
//            urlUniqueDirName
//        )
        val gifPath = getCaptureGifPath(
//            currentAppDirPath,
            urlUniqueDirName
        )
        BitmapTool.saveGif(
            gifPath,
            bo
        )
//        val execCreateGifTxtStart = LocalDateTime.now()
//        BitmapTool.saveGifTxt(
//            gifTxtPath,
//            bo,
//        )
//        val execCreateGifTxtEnd = LocalDateTime.now()
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "gif_create_log2.txt").absolutePath,
//            listOf(
//                "execCreateNormalGifStart: ${execCreateNormalGifStart}",
//                "execCreateGifTxtStart: ${execCreateGifTxtStart}",
//                "execCreateGifTxtEnd: ${execCreateGifTxtEnd}",
//            ).joinToString("\n")
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
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "gif_failue2.txt").absolutePath,
//                listOf(
//                    "currentAppDirPath: ${currentAppDirPath}",
//                    "dirPath: ${pingPathList}",
//                    "urlUniqueDirName: ${urlUniqueDirName}",
////                    "gifTxtPath: ${gifTxtPath}"
//                ).joinToString("\n")
//            )

    }


    private fun saveGifFromBitmapList(
        currentAppDirPath: String,
    ){
        val bitMapList = listOf(
            "Screenshot_20240806_112412_CommandClick.jpg",
            "Screenshot_20240806_115318_Brave.jpg",
            "Screenshot_20240806_111748_CommandClick.jpg"
        ).map {
            File(UsePath.cmdclickDefaultAppDirPath, it).absolutePath
        }.map {
            val bitmap = BitmapTool.convertFileToBitmap(
                it
            ) ?: return@map null
            BitmapTool.resizeByMaxHeight(bitmap, 800.0)
        }
        if(bitMapList.isEmpty()) {
            return
        }
        val bo = BitmapTool.generateGIF(bitMapList)
        BitmapTool.saveGif(
            File(UsePath.cmdclickDefaultAppDirPath, "gif.gif").absolutePath,
            bo
        )
//        val gifTxtPath = getCaptureGifPath(
//            currentAppDirPath,
//            urlUniqueDirName
//        )
//        BitmapTool.saveGif(
//            gifTxtPath,
//            bo
//        )
        BitmapTool.saveGifTxt(
            File(currentAppDirPath, "gif.txt").absolutePath,
            bo,
        )
        FileSystems.writeFile(
            File(
                UsePath.cmdclickDefaultAppDirPath,
                "gif_created.txt"
            ).absolutePath,
            listOf(
                ""
            ).joinToString("\n")
        )

    }

    private fun getCaptureUniqueDirPath(
//        currentAppDirPath: String,
        urlUniqueDirName: String
    ): String {
        return File(
            UrlHistoryPath.makeCaptureHistoryDirPath(),
            urlUniqueDirName,
        ).absolutePath
    }

    private fun getCapturePartsPngDirPath(
//        currentAppDirPath: String,
        urlUniqueDirName: String
    ): String {
        return File(
            getCaptureUniqueDirPath(
//                currentAppDirPath,
                urlUniqueDirName
            ),
            "partPng"
        ).absolutePath
    }

    fun getCaptureGifPath(
//        currentAppDirPath: String,
        urlUniqueDirName: String
    ): String {
        return File(
            getCaptureUniqueDirPath(
//                currentAppDirPath,
                urlUniqueDirName
            ),
            "gif.gif"
        ).absolutePath
    }

    private fun getCaptureGifTextPath(
//        currentAppDirPath: String,
        urlUniqueDirName: String
    ): String {
        return File(
            getCaptureUniqueDirPath(
//                currentAppDirPath,
                urlUniqueDirName
            ),
        "gif.txt"
        ).absolutePath

    }
}