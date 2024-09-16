package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.UrlFileSystems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File

object UrlImageDownloader {

    private val downloadImageUrlPrefix = "${UrlFileSystems.cmdClickAssetsRepoPrefix}/master"
    const val ubuntuAlertGifSuffix = "ubuntu/setup_alert/ubuntuSetupAlert.gif"
    private val downloadSuffixList = listOf(
        ubuntuAlertGifSuffix,
    )
    val imageDirPrefix = File(UsePath.cmdclickFannelSystemDirPath, "images")
    private var downloadJob: Job? = null
//    private val ubuntuSetupAlertGifPath = File(imageDirPrefix, ubuntuAlertGifSuffix)

    fun exit(){
        downloadJob?.cancel()
    }

    fun save(
        cmdIndexFragment: CommandIndexFragment,
    ) {
        exit()
        val context = cmdIndexFragment.context
            ?: return
        val concurrentLimit = 5
        val semaphore = Semaphore(concurrentLimit)
        downloadJob = cmdIndexFragment.lifecycleScope.launch {
            cmdIndexFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.IO) {
                    val jobList = downloadSuffixList.map { imagePathSuffix ->
                        val imageUrl = "${downloadImageUrlPrefix}/${imagePathSuffix}"
                        async {
                            semaphore.withPermit {
                                execSave(
                                    context,
                                    imageUrl,
                                )
                            }
                        }
                    }
                    jobList.forEach { it.await() }
                }
            }
        }
    }

    private suspend fun execSave(
        context: Context?,
        imageUrl: String,
    ) {

        val urlImageLength = withContext(Dispatchers.IO){
            CurlManager.getLength(
                context,
                imageUrl
            )
        }
        val imageFile = File(
            imageDirPrefix,
            imageUrl.replace(downloadImageUrlPrefix, String()).removePrefix("/")
        )
        if(
            imageFile.isFile
            && imageFile.length().toInt() == urlImageLength
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
            imageFile.absolutePath,
            byteArray
        )
        return
    }
}