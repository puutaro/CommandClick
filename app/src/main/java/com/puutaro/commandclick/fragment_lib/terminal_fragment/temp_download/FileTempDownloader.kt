package com.puutaro.commandclick.fragment_lib.terminal_fragment.temp_download

import android.util.Log
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL

object FileTempDownloader {

    private val cmdclickTempDownloadDirPath = UsePath.cmdclickTempDownloadDirPath

    fun downloadFile(urlStr: String) {
        val url =  URL(urlStr)
        val path =
            cmdclickTempDownloadDirPath +
                    urlStr.substring(
                        urlStr.lastIndexOf("/")
                    )
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                FileSystems.removeAndCreateDir(
                    cmdclickTempDownloadDirPath
                )
            }
            withContext(Dispatchers.IO) {
                try {
                    url
                        .openStream()
                        .copyTo(
                            FileOutputStream(
                                File(path)
                            )
                        )
                } catch (e: Exception){
                    Log.w(
                        this@FileTempDownloader::class.java.name,
                        "downlaod err $path"
                    )
                    return@withContext
                }
            }
        }

    }
}