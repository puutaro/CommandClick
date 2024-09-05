package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryPath
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File

object FannelHistoryGifCreator {

    private var gifCreateJob: Job? = null
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

    fun exit(){
        gifCreateJob?.cancel()
    }

    private fun isActive(): Boolean {
        return gifCreateJob?.isActive == true
    }
    fun watch(
        terminalFragment: TerminalFragment
    ){
        val context = terminalFragment.context
            ?: return
        UrlCaptureWatcher.exit()
        val concurrentLimit = 5
        val semaphore = Semaphore(concurrentLimit)
        val tag = terminalFragment.tag
        gifCreateJob = terminalFragment.lifecycleScope.launch {
            terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if(
                    !tag.isNullOrEmpty()
                    && tag != terminalFragment.context?.getString(R.string.index_terminal_fragment)
                ) return@repeatOnLifecycle
                val fannelList = withContext(Dispatchers.IO) {
                    UrlFileSystems.getFannelList(
                        context
                    ).split("\n").filter {
                        it.isNotEmpty()
                    }
                }
                val fannelNameList = withContext(Dispatchers.IO) {
                    UrlFileSystems.extractFannelNameList(fannelList)
                }
//                val historyList = withContext(Dispatchers.IO) {
//                    FileSystems.filterSuffixShellOrJsFiles(
//                        cmdclickAppHistoryDirAdminPath
//                    )
//                }
                val urlPrtPngMap = withContext(Dispatchers.IO) {
                    makeUrlPrtPngMapList(
                        fannelNameList,
//                        historyList,
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
                        if(
                            !isActive()
                        ) return@withContext
                        async {
                            if(
                                !isActive()
                            ) return@async
                            semaphore.withPermit {
                                if(
                                    !isActive()
                                ) return@withPermit
                                val fannelName = map.key
                                val urlPngPathList = map.value
//                                val appDirName = withContext(Dispatchers.IO) {
//                                    FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
//                                        historyName
//                                    )
//                                }
//                                val appDirPath = File(cmdclickAppDirPath, appDirName).absolutePath
                                val designList = makeDesignConList(
                                    fannelName,
//                                    appDirName,
                                    urlPngPathList,
                                )
                                val captureGifDesignConList = FannelHistoryPath.getCaptureGifDesignPath(
//                                    appDirPath,
                                    fannelName,
                                ).let {
                                    ReadText(it).textToList()
                                }
                                val gifPath = FannelHistoryPath.getCaptureGifPath(
//                                    appDirPath,
                                    fannelName,
                                )
                                val isRecreateGif =
                                    !File(gifPath).isFile
                                            || captureGifDesignConList != designList
//                                if(historyName.contains("newsSpeecher")) {
//                                    FileSystems.updateFile(
//                                        File(
//                                            UsePath.cmdclickDefaultAppDirPath,
//                                            "gif_makeUrlPrtPngMapList_watch2.txt"
//                                        ).absolutePath,
//                                        listOf(
//                                            "historyName: ${historyName}",
//                                            "urlPngPathList: ${urlPngPathList}",
//                                            "captureGifDesignConList: ${captureGifDesignConList}",
//                                            "designList: ${designList}",
//                                            "isRecreateGif: ${isRecreateGif}",
//                                        ).joinToString("\n") + "\n-----------\n"
//                                    )
//                                }
                                if (
                                    !isRecreateGif
                                    || !isActive()
                                ) return@withPermit
                                GifCreator.create(
                                    context,
                                    fannelName,
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

    private fun makeDesignConList(
        fannelName: String,
//        appDirName: String,
        urlPngPathList: List<String>,
    ): List<String> {
//        val appDirPath = File(cmdclickAppDirPath, appDirName).absolutePath
        val capturePartsPngDirPath = FannelHistoryPath.getCapturePartsPngDirPath(
//            appDirPath,
            fannelName,
        )
        val capturePartsPngList =
            urlPngPathList.map { File(it).name }.sortedBy { it }.sortedBy { it }.map {
                File(capturePartsPngDirPath, it).absolutePath
            }
//            when(
//            fannelName.isEmpty()
//                    || fannelName == FannelInfoSetting.current_fannel_name.defalutStr
//        ) {
//            true -> FileSystems.sortedFiles(capturePartsPngDirPath)
//            else ->
//                urlPngPathList.map { File(it).name }.sortedBy { it }
//        }
        val facePngPath = FannelHistoryPath.getCaptureFacePngDirPath(
//            appDirPath,
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
            fannelName: String,
            urlPngPathList: List<String>,
        ) {
            val partPngDirPath = FannelHistoryPath.getCapturePartsPngDirPath(
//                appDirPath,
                fannelName
            )
            FileSystems.removeAndCreateDir(
                partPngDirPath
            )
            val pngPathToByteArrayList = withContext(Dispatchers.IO) {
                downloadPngByteArrayList(
                    context,
                    urlPngPathList
                )
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "gifDown.txt").absolutePath,
//                listOf(
//                    "pngPathToBitmapList: ${pngPathToBitmapList.map { it.first }}"
//                ).joinToString("\n") + "\n----------\n"
//            )
            pngPathToByteArrayList.forEach {
                val pngPath = it.first
                val byteArray = it.second ?: return@forEach
                FileSystems.writeFromByteArray(
                    pngPath,
                    byteArray
                )
            }
            val faceDirPath = FannelHistoryPath.getCaptureFacePngDirPath(
//                appDirPath,
                fannelName,
            )
            val facePngPath = FileSystems.sortedFiles(faceDirPath).map {
                File(faceDirPath, it).absolutePath
            }.filter {
                File(it).isFile
            }.firstOrNull()
            saveGifDesign(
//                appDirPath,
                fannelName,
                facePngPath,
                pngPathToByteArrayList.map {
                    it.first
                }
            )
            val bitmapList = when (facePngPath.isNullOrEmpty()) {
                true
                -> pngPathToByteArrayList.take(3).map {
                    it.second
                }

                else
                -> listOf(
                    BitmapTool.convertFileToByteArray(facePngPath)
                ) + pngPathToByteArrayList.takeLast(2).map {
                    it.second
                }
            }
            createGifFromPngs(
                bitmapList,
//                appDirPath,
                fannelName,
            )
        }

        private fun saveGifDesign(
//            appDirPath: String,
            fannelName: String,
            facePngPath: String?,
            pngPathList: List<String>,

        ) {
            val designList = designCreateHandler(
                pngPathList,
                facePngPath
            )
            FannelHistoryPath.getCaptureGifDesignPath(
//                appDirPath,
                fannelName,
            ).let {
                FileSystems.writeFile(
                    it,
                    designList.joinToString("\n")
                )
            }
        }

        private fun createGifFromPngs(
            byteArrayMapList: List<ByteArray?>,
//            currentAppDirPath: String,
            fannelName: String,
        ) {
            if (
                byteArrayMapList.isEmpty()
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
            val bitMapList = byteArrayMapList.map {
                if(
                    it == null
                ) return@map null
                val bitmapSrc = BitmapFactory.decodeByteArray(it, 0, it.size)
                BitmapTool.resizeByMaxHeight(bitmapSrc, 800.0)
            }
            val bo = BitmapTool.generateGIF(bitMapList)
            val gifPath = FannelHistoryPath.getCaptureGifPath(
//                currentAppDirPath,
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

    private fun downloadPngByteArrayList(
        context: Context,
        urlPngPathList: List<String>
    ): List<Pair<String, ByteArray?>> {
        val pngBitMapList = urlPngPathList.map {
                pngPath ->
            val relativePath = pngPath.replace(
                cmdclickDefaultAppDirPath,
                String()
            ).removePrefix("/")
            val pngUrl = listOf(
                UrlFileSystems.gitUserContentFannelPrefix,
                relativePath
            ).joinToString("/")
            val byteArray = CurlManager.get(
                context,
                pngUrl,
                String(),
                String(),
                5_000
            ).let {
                val isConnOk = CurlManager.isConnOk(it)
                if(!isConnOk) return@let null
               it
            }
//            val bitmap = Glide.with(context)
//                .asBitmap()
//                .load(pngUrl)
//                .submit()
//                .get().let {
//                    BitmapTool.resizeByMaxHeight(
//                        it,
//                        800.0
//                    )
//                }
            pngPath to byteArray
        }
        return pngBitMapList.sortedBy {
                File(it.first).name
            }
    }

    private fun makeUrlPrtPngMapList(
//        historyList: List<String>,
        fannelNameList: List<String>,
        fannelList: List<String>,
    ): Map<String, List<String>>{
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        return fannelNameList.map {
                fannelName ->
//            val appDirName = withContext(Dispatchers.IO){
//                FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
//                    historyLine
//                )
//            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "gif_makeUrlPrtPngMapList.txt").absolutePath,
//                listOf(
//                    "appDirPath: ${appDirPath}",
//                    "fannelName: ${fannelName}"
//                ).joinToString("\n")
//            )
            val partPngList = fannelList.filter {
                    relativePath ->
                val pngPath = File(cmdclickDefaultAppDirPath, relativePath).absolutePath
                val partPngDirPath = FannelHistoryPath.getCapturePartsPngDirPath(
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
                File(cmdclickDefaultAppDirPath, relativePath).absolutePath
            }
            fannelName to partPngList
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