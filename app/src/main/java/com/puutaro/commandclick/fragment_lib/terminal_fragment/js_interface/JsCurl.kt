package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.temp_download.FileTempDownloader
import com.puutaro.commandclick.fragment_lib.terminal_fragment.temp_download.ImageTempDownloader
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File


class JsCurl(
    private val terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun get(
        mainUrl: String,
        queryParameter: String = String(),
        header: String = String(),
        timeout: Int
    ): String {
        val resByteArray = CurlManager.get(
            mainUrl,
            queryParameter,
            header,
            timeout
        )
        return String(resByteArray)
    }

    fun getAndSave (
        path: String,
        mainUrl: String,
        queryParameter: String = String(),
        header: String = String(),
        timeout: Int
    ) {
        val file = File(path)
        val parentDirPath = file.parent ?: return
        val fileName = file.name
        CurlManager.get(
            mainUrl,
            queryParameter,
            header,
            timeout
        ).let {
            FileSystems.writeFromByteArray(
                parentDirPath,
                fileName,
                it
            )
        }
    }

    @JavascriptInterface
    fun post(
        mainUrl: String,
        header: String = String(),
        bodyStr: String,
        timeout: Int
    ): String {
        return CurlManager.post(
            mainUrl,
            header,
            bodyStr,
            timeout
        ).toString()
    }

    @JavascriptInterface
    fun postAndSave(
        path: String,
        mainUrl: String,
        header: String = String(),
        bodyStr: String,
        timeout: Int
    ) {
        val file = File(path)
        val parentDirPath = file.parent ?: return
        val fileName = file.name
        return CurlManager.post(
            mainUrl,
            header,
            bodyStr,
            timeout
        ).let {
            FileSystems.writeFromByteArray(
                parentDirPath,
                fileName,
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
    ){
        val cmdclickTempDownloadDirPath = UsePath.cmdclickTempDownloadDirPath
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
                        tempFileList.isNotEmpty()
                    ) break
                }
            }
        }
    }

    @JavascriptInterface
    fun isConnOk(res: String): Boolean {
        return CurlManager.isConnOk(res)
    }
}