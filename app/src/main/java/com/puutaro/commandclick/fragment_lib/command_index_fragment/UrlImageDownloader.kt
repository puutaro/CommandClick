package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.gz.GzTool
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File

object UrlImageDownloader {

    private val downloadImageUrlPrefix =
        "${UrlFileSystems.cmdClickAssetsRepoPrefix}/master"
    private val imageListMapLineTextUrl =
        "${downloadImageUrlPrefix}/image_compress/tar/image_tar_list.txt"
    private val mapListSeparator = ','
//    private val downloadSuffixList = listOf(
//        ubuntuAlertGifSuffix,
//    )

    val imageDirObj = File(UsePath.cmdclickFannelSystemDirPath, "images")
    val fannelWallDirPath = File(
        imageDirObj.absolutePath,
        "WALL_DIR"
    ).absolutePath
    val ubuntuAlertGifPath = File(
        imageDirObj.absolutePath,
        "ubuntu/setup_alert/ubuntuSetupAlert.gif"
    ).absolutePath
    private val imageTempDirPath = File(imageDirObj.absolutePath, "temp").absolutePath
    private var downloadJob: Job? = null
//    private val ubuntuSetupAlertGifPath = File(imageDirObj.absolutePath, ubuntuAlertGifSuffix)

    fun exit(){
        downloadJob?.cancel()
    }

    fun save(
        cmdIndexFragment: CommandIndexFragment,
    ) {
//        exit()
        val context = cmdIndexFragment.context
            ?: return
        val concurrentLimit = 5
        val semaphore = Semaphore(concurrentLimit)
        downloadJob = cmdIndexFragment.lifecycleScope.launch {
            cmdIndexFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.IO) {
                    FileSystems.removeAndCreateDir(
                        imageTempDirPath
                    )
                }
                val downLoadInfoMapList = withContext(Dispatchers.IO) {
                    CurlManager.get(
                        context,
                        imageListMapLineTextUrl,
                        String(),
                        String(),
                        2000,
                    ).let {
                        val isConnOk = CurlManager.isConnOk(it)
                        if (!isConnOk) return@let emptyList()
//                        FileSystems.writeFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "ldownlaod.txt").absolutePath,
//                            listOf(
//                                "imageListMapLineTextUrl: ${imageListMapLineTextUrl}",
//                                "list: ${String(it)}"
//                            ).joinToString("\n")
//                        )
                        String(it).split("\n").map { lineMapCon ->
                            CmdClickMap.createMap(
                                lineMapCon,
                                mapListSeparator
                            ).toMap()
                        }
                    }
                }
//                FileSystems.writeFile(
//                    File(
//                        UsePath.cmdclickDefaultAppDirPath,
//                        "ldownlaod_downLoadMapList.txt"
//                    ).absolutePath,
//                    listOf(
//                        "imageListMapLineTextUrl: ${imageListMapLineTextUrl}",
//                        "downLoadMapList: ${downLoadInfoMapList}"
//                    ).joinToString("\n")
//                )
                withContext(Dispatchers.IO) {
                    val jobList = downLoadInfoMapList.map { downloadInfoMap ->
                        async {
                            val relativeTarPath = InfoMapTool.getRelativeTarPath(
                                downloadInfoMap,
                            ) ?: return@async
                            val relativeDirPath = InfoMapTool.getRelativeDirPath(
                                downloadInfoMap
                            ) ?: return@async
                            val urlTarSize = InfoMapTool.getTarSize(
                                downloadInfoMap
                            )
                            semaphore.withPermit {
                                execSave(
                                    context,
                                    relativeTarPath,
                                    urlTarSize,
                                    relativeDirPath,
                                )
                            }
                        }
                    }
                    jobList.forEach { it.await() }
                    withContext(Dispatchers.IO) {
                        FileSystems.removeDir(
                            imageTempDirPath
                        )
                    }
                }
            }
        }
    }

    private suspend fun execSave(
        context: Context?,
        relativeTarPath: String,
        urlTarSize: Int?,
        relativeDirPath: String,
    ) {
        val imageUrl =
            "${downloadImageUrlPrefix}/${relativeTarPath}"
        val infoTxtPath = InfoMapTool.makeInfoPath(
            relativeDirPath,
        )
        val infoMap = InfoMapTool.read(
            infoTxtPath
        )
        val savedTarSize = InfoMapTool.getTarSize(
            infoMap
        )
        val isSaveDirPath =
            FileSystems.sortedFiles(
                File(imageDirObj.absolutePath, relativeDirPath).absolutePath
            ).isNotEmpty()
        val tarName = File(imageUrl).name
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "ldownlaod_${tarName}.txt").absolutePath,
//            listOf(
//                "imageUrl: ${imageUrl}",
//                "imageListMapLineTextUrl: ${imageListMapLineTextUrl}",
//                "urlTarSize: ${urlTarSize}",
//                "savedTarSize: ${savedTarSize}",
//                "relativeDirPath: ${relativeDirPath}",
//                "isSaveDirPath: ${isSaveDirPath}",
//            ).joinToString("\n")
//        )
        val downloadTarPath = File(imageTempDirPath, tarName).absolutePath
        if(
            isSaveDirPath
            && urlTarSize == savedTarSize
            && savedTarSize != null
        ) {
            return
        }
        val byteArray = CurlManager.get(
            context,
            imageUrl,
            String(),
            String(),
            5_000,
        ).let {
            val isConnOk = CurlManager.isConnOk(it)
            if(!isConnOk) return@let null
            it
        } ?: return
        FileSystems.writeFromByteArray(
            downloadTarPath,
            byteArray
        )
        GzTool.extractTarWithoutOwnership(
            downloadTarPath,
            imageDirObj.absolutePath,
            false,
        )
        InfoMapTool.save(
            infoTxtPath,
            relativeTarPath,
            urlTarSize
        )
    }

    private object InfoMapTool {


        private enum class DownloadMapListKey(val key: String) {
            RELATIVE_DIR_PATH("relativeDirPath"),
            RELATIVE_TAR_PATH("relativeTarPath"),
            TAR_SIZE("tarSize"),
        }

        fun makeInfoPath(
            relativeDirPath: String,
        ): String {
            val pathSeparator = "___"
            return listOf(
                imageDirObj.absolutePath,
                relativeDirPath,
                "info.txt",
            ).joinToString("/").replace(
                Regex("[/]+"),
                "/",
            )
        }

        fun getTarSize(
            infoMap: Map<String, String>
        ): Int? {
            return try {
                infoMap.get(
                    DownloadMapListKey.TAR_SIZE.key
                )?.toInt()
            }catch(e: Exception){
                null
            }
        }

        fun getRelativeTarPath(
            infoMap: Map<String, String>
        ): String? {
            return infoMap.get(
                DownloadMapListKey.RELATIVE_TAR_PATH.key
            )
        }

        fun getRelativeDirPath(
            infoMap: Map<String, String>
        ): String? {
            return infoMap.get(
                DownloadMapListKey.RELATIVE_DIR_PATH.key
            )
        }

        fun read(
            infoTxtPath: String,
        ): Map<String, String> {
            return CmdClickMap.createMap(
                ReadText(
                    infoTxtPath,
                ).readText(),
                mapListSeparator
            ).toMap()
        }

        fun save(
            path: String,
            relativeTarPath: String,
            urlTarSize: Int?,
        ){
            val mapCon = mapOf(
                DownloadMapListKey.RELATIVE_TAR_PATH.key to relativeTarPath,
                DownloadMapListKey.TAR_SIZE.key to (urlTarSize ?: 0)
            ).map {
                "${it.key}=${it.value}"
            }.joinToString(mapListSeparator.toString())
            FileSystems.writeFile(
                path,
                mapCon
            )
        }
    }
}