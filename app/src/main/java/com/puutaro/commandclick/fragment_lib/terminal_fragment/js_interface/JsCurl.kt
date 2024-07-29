package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.temp_download.FileTempDownloader
import com.puutaro.commandclick.fragment_lib.terminal_fragment.temp_download.ImageTempDownloader
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File


class JsCurl(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun get(
        mainUrl: String,
        queryParameter: String,
        header: String,
        timeout: Int
    ): String {
        val con = CurlManager.get(
            context,
            mainUrl,
            queryParameter,
            header,
            timeout
        ).let {
            CurlManager.convertResToStrByConn(it)
        }
        return con
    }

    @JavascriptInterface
    fun getAndSave (
        savePath: String,
        mainUrl: String,
        queryParameter: String,
        header: String,
        timeout: Int
    ) {
        CurlManager.get(
            context,
            mainUrl,
            queryParameter,
            header,
            timeout
        ).let {
            if(
                !CurlManager.isConnOk(it)
            ) return@let
            FileSystems.writeFromByteArray(
                savePath,
                it
            )
        }
    }

    @JavascriptInterface
    fun post(
        mainUrl: String,
        header: String,
        bodyStr: String,
        timeout: Int
    ): String {
        return CurlManager.post(
            context,
            mainUrl,
            header,
            bodyStr,
            timeout
        ).let {
            CurlManager.convertResToStrByConn(it)
        }
    }

    @JavascriptInterface
    fun postAndSave(
        path: String,
        mainUrl: String,
        header: String,
        bodyStr: String,
        timeout: Int
    ) {
        return CurlManager.post(
            context,
            mainUrl,
            header,
            bodyStr,
            timeout
        ).let {
            if(
                !CurlManager.isConnOk(it)
            ) return@let
            FileSystems.writeFromByteArray(
                path,
                it
            )
        }
    }

    @JavascriptInterface
    fun getTextOrPdf(
        url: String
    ) {
        val cmdclickTempDownloadDirPath = UsePath.cmdclickTempDownloadDirPath
        runBlocking{
            withContext(Dispatchers.IO) {
                FileTempDownloader.downloadFile(url)
            }
            withContext(Dispatchers.IO){
                for(i in 1..50){
                    delay(100)
                    val tempFileList = FileSystems.sortedFiles(
                        cmdclickTempDownloadDirPath
                    )
                    if(
                        tempFileList.isNotEmpty()
                    ) break
                }
            }
        }
    }

    @JavascriptInterface
    fun getImage(
        url: String
    ): String {
        val cmdclickTempDownloadDirPath = UsePath.cmdclickTempDownloadDirPath
        var downloadImagePath = String()
        runBlocking{
            withContext(Dispatchers.IO) {
                ImageTempDownloader.download(
                    terminalFragment,
                    url
                )
            }
            withContext(Dispatchers.IO){
                for(i in 1..50){
                    delay(100)
                    val tempFileList = FileSystems.sortedFiles(
                        cmdclickTempDownloadDirPath
                    )
                    if(
                        tempFileList.isEmpty()
                    ) continue
                    val downloadImageName =
                        tempFileList.firstOrNull()
                            ?: String()
                    downloadImagePath =
                        File(
                            cmdclickTempDownloadDirPath,
                            downloadImageName
                        ).absolutePath
                    break
                }
            }
            withContext(Dispatchers.IO) {
                val downloadImageFile = File(downloadImagePath)
                var prevImageSize = downloadImageFile.length()
                for (i in 1..10){
                    delay(200)
                    val curImageSize = downloadImageFile.length()
                    if(
                        prevImageSize != curImageSize
                    ) {
                        prevImageSize = curImageSize
                        continue
                    }
                    break
                }
            }
        }
        return downloadImagePath
    }

    @JavascriptInterface
    fun isConnOk(
        res: String
    ): Boolean {
        return CurlManager.isConnOk(res.toByteArray())
    }
}