package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryManager
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryPath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File

object FannelHistoryGifCreator {

    private val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
    private var gifCreateJob: Job? = null
    private val urlFileSystems = UrlFileSystems()

    fun exit(){
        gifCreateJob?.cancel()
    }

    fun watch(
        terminalFragment: TerminalFragment
    ){
        val context = terminalFragment.context
            ?: return
        UrlCaptureWatcher.exit()
        val cmdclickAppHistoryDirAdminPath = UsePath.cmdclickAppHistoryDirAdminPath
        val concurrentLimit = 5
        val semaphore = Semaphore(concurrentLimit)
        gifCreateJob = terminalFragment.lifecycleScope.launch {
            terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val fannelList = withContext(Dispatchers.IO) {
                    urlFileSystems.execGetFannelList(
                        context
                    ).split("\n").filter {
                        it.isNotEmpty()
                    }
                }
                val historyList = withContext(Dispatchers.IO) {
                    FileSystems.filterSuffixShellOrJsFiles(
                        cmdclickAppHistoryDirAdminPath
                    )
                }
                val urlPrtPngMap = withContext(Dispatchers.IO) {
                    makeUrlPrtPngMapList(
                        historyList,
                        fannelList,
                    )
                }
//                val partPngMap = withContext(Dispatchers.IO) {
//                    makePartPngMap(
//                        historyList,
//                    )
//                }
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "gifList.txt").absolutePath,
//                    listOf(
//                        "fannelList: ${fannelList}",
//                        "historyList: ${historyList}",
//                        "urlPrtPngMap: ${urlPrtPngMap}",
////                        "partPngMap: ${partPngMap}",
//                    ).joinToString("\n############\n") + "\n----------------\n"
//                )
                withContext(Dispatchers.IO) {
                    val jobList = urlPrtPngMap.map {
                            map ->
                        async {
                            semaphore.withPermit {
                                val historyName = map.key
                                val urlPngPathList = map.value
                            val appDirName = withContext(Dispatchers.IO) {
                                FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
                                    historyName
                                )
                            }
                            val appDirPath = File(cmdclickAppDirPath, appDirName).absolutePath
                            val fannelName = withContext(Dispatchers.IO){
                                FannelHistoryManager.getFannelNameFromAppHistoryFileName(
                                    historyName
                                )
                            }
                            val designList = makeDesignConList(
                                historyName,
                                appDirName,
                                urlPngPathList,
                            )
                            val captureGifDesignConList = FannelHistoryPath.getCaptureGifDesignPath(
                                appDirPath,
                                fannelName,
                            ).let {
                                ReadText(it).textToList()
                            }
                            val gifPath = FannelHistoryPath.getCaptureGifPath(
                                appDirPath,
                                fannelName,
                            )
                            val isRecreateGif =
                                !File(gifPath).isFile
                                        || captureGifDesignConList != designList
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultAppDirPath,
//                                    "gif_makeUrlPrtPngMapList_watch2.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "historyName: ${historyName}",
//                                    "urlPngPathList: ${urlPngPathList}",
//                                    "captureGifDesignConList: ${captureGifDesignConList}",
//                                    "designList: ${designList}",
//                                    "isRecreateGif: ${isRecreateGif}",
//                                ).joinToString("\n") + "\n-----------\n"
//                            )
                            if (
                                !isRecreateGif
                            ) return@async
                            GifCreator.create(
                                context,
                                historyName,
                                urlPngPathList,
                            )
                        }
                    }
                    }
                    jobList.forEach { it.await() }
                }
            }
        }
    }

    private suspend fun makeDesignConList(
        historyName: String,
        appDirName: String,
        urlPngPathList: List<String>,
    ): List<String> {
        val appDirPath = File(cmdclickAppDirPath, appDirName).absolutePath
        val fannelName = withContext(Dispatchers.IO){
            FannelHistoryManager.getFannelNameFromAppHistoryFileName(
                historyName
            )
        }
        val capturePartsPngDirPath = FannelHistoryPath.getCapturePartsPngDirPath(
            appDirPath,
            fannelName,
        )
        val capturePartsPngList = when(
            fannelName.isEmpty()
                    || fannelName == FannelInfoSetting.current_fannel_name.defalutStr
        ) {
            true -> FileSystems.sortedFiles(capturePartsPngDirPath)
            else ->
                urlPngPathList.map { File(it).name }.sortedBy { it }
        }.sortedBy { it }.map {
            File(capturePartsPngDirPath, it).absolutePath
        }
        val facePngPath = FannelHistoryPath.getCaptureFacePngDirPath(
            appDirPath,
            fannelName,
        ).let {
            faceDirPath ->
            FileSystems.sortedFiles(faceDirPath).map {
                File(faceDirPath, it).absolutePath
            }.filter {
                File(it).isFile
            }.firstOrNull()
        }
        return designCreateHandler(
            capturePartsPngList,
            facePngPath
        )
    }

    private object GifCreator {

        suspend fun create(
            context: Context,
            historyName: String,
            urlPngPathList: List<String>,
        ) {
            val appDirName = withContext(Dispatchers.IO) {
                FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
                    historyName
                )
            }
            val appDirPath = File(cmdclickAppDirPath, appDirName).absolutePath
            val fannelName = withContext(Dispatchers.IO) {
                FannelHistoryManager.getFannelNameFromAppHistoryFileName(
                    historyName
                )
            }
            val partPngDirPath = FannelHistoryPath.getCapturePartsPngDirPath(
                appDirPath,
                fannelName
            )
            FileSystems.removeAndCreateDir(
                partPngDirPath
            )
            val pngPathToBitmapList = withContext(Dispatchers.IO) {
                downloadPngBitmapList(
                    context,
                    appDirPath,
                    urlPngPathList
                )
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "gifDown.txt").absolutePath,
//                listOf(
//                    "pngPathToBitmapList: ${pngPathToBitmapList.map { it.first }}"
//                ).joinToString("\n") + "\n----------\n"
//            )
            pngPathToBitmapList.forEach {
                val pngPath = it.first
                val bitmap = it.second
                FileSystems.writeFromByteArray(
                    pngPath,
                    BitmapTool.convertBitmapToByteArray(bitmap)
                )
            }
            val faceDirPath = FannelHistoryPath.getCaptureFacePngDirPath(
                appDirPath,
                fannelName,
            )
            val facePngPath = FileSystems.sortedFiles(faceDirPath).map {
                File(faceDirPath, it).absolutePath
            }.filter {
                File(it).isFile
            }.firstOrNull()
            saveGifDesign(
                appDirPath,
                fannelName,
                facePngPath,
                pngPathToBitmapList
            )
            val bitmapList = when (facePngPath.isNullOrEmpty()) {
                true
                -> pngPathToBitmapList.take(3).map {
                    it.second
                }

                else
                -> listOf(
                    BitmapTool.convertFileToBitmap(facePngPath)
                ) + pngPathToBitmapList.takeLast(2).map {
                    it.second
                }
            }
            createGifFromPngs(
                bitmapList,
                appDirPath,
                fannelName,
            )
        }

        private fun saveGifDesign(
            appDirPath: String,
            fannelName: String,
            facePngPath: String?,
            pngPathToBitmapList: List<Pair<String, Bitmap>>

        ) {
            val designList = designCreateHandler(
                pngPathToBitmapList.map {
                    it.first
                },
                facePngPath
            )
            FannelHistoryPath.getCaptureGifDesignPath(
                appDirPath,
                fannelName,
            ).let {
                FileSystems.writeFile(
                    it,
                    designList.joinToString("\n")
                )
            }
        }

        private fun createGifFromPngs(
            bitMapList: List<Bitmap?>,
            currentAppDirPath: String,
            fannelName: String,
        ) {
            if (
                bitMapList.isEmpty()
            ) {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "gif_failue.txt").absolutePath,
//                listOf(
//                    "pingPathList: ${pngPathTobitMapList}",
//                    "currentAppDirPath: ${currentAppDirPath}"
//                ).joinToString("\n")
//            )
                return
            }
            val bo = BitmapTool.generateGIF(bitMapList)
            val gifPath = FannelHistoryPath.getCaptureGifPath(
                currentAppDirPath,
                fannelName
            )
            BitmapTool.saveGif(
                gifPath,
                bo
            )

        }
    }

    private fun designCreateHandler(
        capturePartsPngList: List<String>,
        facePngPath: String?
    ): List <String> {
        return when(facePngPath.isNullOrEmpty()){
            true
            -> capturePartsPngList.take(3)
            else
            -> listOf(
                facePngPath
            ) + capturePartsPngList.takeLast(2)
        }
    }

    private fun downloadPngBitmapList(
        context: Context,
        appDirPath: String,
        urlPngPathList: List<String>
    ): List<Pair<String, Bitmap>> {
        val pngBitMapList = urlPngPathList.map {
                pngPath ->
            val relativePath = pngPath.replace(
                appDirPath,
                String()
            ).removePrefix("/")
            val pngUrl = listOf(
                urlFileSystems.gitUserContentFannelPrefix,
                relativePath
            ).joinToString("/")
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(pngUrl)
                .submit()
                .get().let {
                    BitmapTool.resizeByMaxHeight(
                        it,
                        800.0
                    )
                }
            pngPath to bitmap
        }
        return pngBitMapList.sortedBy {
                File(it.first).name
            }
    }

    private suspend fun makeUrlPrtPngMapList(
        historyList: List<String>,
        fannelList: List<String>,
    ): Map<String, List<String>>{
        return historyList.map {
                historyLine ->
            val appDirName = withContext(Dispatchers.IO){
                FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
                    historyLine
                )
            }
            val appDirPath = File(cmdclickAppDirPath, appDirName).absolutePath
            val fannelName = withContext(Dispatchers.IO){
                FannelHistoryManager.getFannelNameFromAppHistoryFileName(
                    historyLine
                )
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "gif_makeUrlPrtPngMapList.txt").absolutePath,
//                listOf(
//                    "appDirPath: ${appDirPath}",
//                    "fannelName: ${fannelName}"
//                ).joinToString("\n")
//            )
            val partPngList = fannelList.filter {
                    relativePath ->
                val pngPath = File(appDirPath, relativePath).absolutePath
                val partPngDirPath = FannelHistoryPath.getCapturePartsPngDirPath(
                    appDirPath,
                    fannelName
                )
                val isPartDirFile = pngPath.startsWith(
                    partPngDirPath
                )
                val isPng = isImage(pngPath)
//                if(
//                    fannelName.contains("clipFormatMaker")
//                ) {
//                    FileSystems.updateFile(
//                        File(
//                            UsePath.cmdclickDefaultAppDirPath,
//                            "gif_makeUrlPrtPngMapList2.txt"
//                        ).absolutePath,
//                        listOf(
//                            "appDirPath: ${appDirPath}",
//                            "fannelName: ${fannelName}",
//                            "relativePath: ${relativePath}",
//                            "pngPath: ${pngPath}",
//                            "partPngDirPath: ${partPngDirPath}",
//                            "isPartDirFile: ${isPartDirFile}",
//                            "isPng: ${isPng}",
//                        ).joinToString("\n")
//                    )
//                }
                isPartDirFile && isPng
            }.map {
                    relativePath ->
                File(appDirPath, relativePath).absolutePath
            }
            historyLine to partPngList
        }.toMap()
    }

//    private suspend fun makePartPngMap(
//        historyList: List<String>
//    ): Map<String, List<String>> {
//        return historyList.map {
//                historyLine ->
//            val appDirName = withContext(Dispatchers.IO) {
//                FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
//                    historyLine
//                )
//            }
//            val appDirPath = File(cmdclickAppDirPath, appDirName).absolutePath
//            val fannelName = withContext(Dispatchers.IO) {
//                FannelHistoryManager.getFannelNameFromAppHistoryFileName(
//                    historyLine
//                )
//            }
//            val partPngDirPath = FannelHistoryPath.getCapturePartsPngDirPath(
//                appDirPath,
//                fannelName
//            )
//            val partPngList = FileSystems.sortedFiles(
//                partPngDirPath
//            ).filter {
//                    fileName ->
//                val isPng = fileName.endsWith(".png")
//                isPng
//            }.map {
//                    pngName ->
//                File(partPngDirPath, pngName).absolutePath
//            }
//            historyLine to partPngList
//        }.toMap()
//    }

}

private fun isImage(path: String): Boolean {
    return path.endsWith(".png")
            || path.endsWith(".jpg")
}