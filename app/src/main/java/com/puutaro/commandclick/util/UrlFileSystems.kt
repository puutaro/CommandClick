package com.puutaro.commandclick.util

import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object UrlFileSystems {

    val siteSummaryUrl =
        "https://raw.githubusercontent.com/" +
                "puutaro/commandclick-repository/master/fannel/siteSummary.js"
    val urlIntender =
        "https://raw.githubusercontent.com/" +
                "puutaro/commandclick-repository/master/fannel/urlIntender.js"
    val shareImage =
        "https://raw.githubusercontent.com/" +
                "puutaro/commandclick-repository/master/fannel/shareImage.js"
    val urlTrans =
        "https://raw.githubusercontent.com/" +
                "puutaro/commandclick-repository/master/fannel/urlTrans.js"
    val textToSpeech =
        "https://raw.githubusercontent.com/" +
                "puutaro/commandclick-repository/master/fannel/textToSpeech.js"
    val selectMenu =
        "https://raw.githubusercontent.com/puutaro/" +
                "commandclick-repository/master/fannel/selectMenu.js"
    val highlightSch =
        "https://raw.githubusercontent.com/" +
                "puutaro/commandclick-repository/master/fannel/highlightSch.js"
    val cmdBookmaker =
        "https://raw.githubusercontent.com/" +
                "puutaro/commandclick-repository/master/fannel/cmdBookmaker.js"
    val webSearcher =
            "https://raw.githubusercontent.com/" +
                    "puutaro/commandclick-repository/master/fannel/webSearcher.js"
    val askGpt35 =
            "https://raw.githubusercontent.com/" +
                    "puutaro/commandclick-repository/master/fannel/askGpt35.js"

    fun getFileNameFromUrl(
        urlStr: String
    ): String {
        return urlStr
            .split("/")
            .lastOrNull()
            ?: String()
    }

    suspend fun createFile(
        srcUrl: String,
        destiDirPath: String,
        fileNameSrc: String = String(),
    ){
        val fileName = if(fileNameSrc.isEmpty()) {
            getFileNameFromUrl(srcUrl)
        } else fileNameSrc
        if(
            File(
                "${destiDirPath}/${fileName}"
            ).isFile
        ) return
        val contents = withContext(Dispatchers.IO) {
            CurlManager.get(
                srcUrl,
                String(),
                String(),
                2000,
            )
        }
        withContext(Dispatchers.IO){
            FileSystems.writeFile(
                destiDirPath,
                fileName,
                contents
            )
        }
    }
}