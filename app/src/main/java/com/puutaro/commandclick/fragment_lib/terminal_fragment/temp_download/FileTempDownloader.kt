package com.puutaro.commandclick.fragment_lib.terminal_fragment.download

import com.puutaro.commandclick.common.variable.UsePath
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

object FileDownloader {

    private val cmdclickTempDownloadDirPath = UsePath.cmdclickTempDownloadDirPath
    private val tempFileName = "temp"

    fun downloadFile(urlStr: String, fileName: String) {
        val url: URL =  URL(urlStr)
        url.openStream().use {
            Files.copy(it, Paths.get(fileName))
        }

    }
}