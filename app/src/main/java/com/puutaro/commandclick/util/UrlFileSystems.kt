package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class UrlFileSystems {

    private var fannelListCon = String()

    private val gitUserContentManagePrefix =
        "$cmdclickRepoGitUserContentPrefix/manage"

    private val gitUserContentFannelPrefix =
        "$cmdclickRepoGitUserContentPrefix/fannel"

    companion object {

        val gitComPrefix = "https://github.com"
        val gitUserContentPrefix =
            "https://raw.githubusercontent.com"

        val cmdclickRepoGitUserContentPrefix =
            "${gitUserContentPrefix}/" +
                    "puutaro/commandclick-repository/master"

        val readmeSuffix = "master/${UsePath.fannelReadmeName}"
        enum class FirstCreateFannels(
            val str: String,
        ) {
            SiteSummary("siteSummary"),
            UrlIntender("urlIntender"),
            ShareImage("shareImage"),
            UrlTrans("urlTrans"),
            TextToSpeech("textToSpeech"),
            SelectMenu("selectMenu"),
            CmdBookmaker("cmdBookmaker"),
            WebSearcher("webSearcher"),
            AskGpt35("askGpt35"),
            CopyLink("copyLink"),
            qrLReader("qrLReader"),
        }
    }

    fun getFileNameFromUrl(
        urlStr: String
    ): String {
        return urlStr
            .split("/")
            .lastOrNull()
            ?: String()
    }

    suspend fun getFannelList(){
        if(
            fannelListCon.isNotEmpty()
        ) return
        val fannelListUrl =
            "$gitUserContentManagePrefix/fannels/list/fannels.txt"
        fannelListCon = withContext(Dispatchers.IO) {
            CurlManager.get(
                fannelListUrl,
                String(),
                String(),
                2000,
            ).let {
                if(
                    !CurlManager.isConnOk(it)
                ) return@let String()
                String(it)
            }
        }
    }

    suspend fun createFile(
        destiDirPath: String,
        fannelRawName: String = String(),
    ){

        withContext(Dispatchers.IO) {
            getFannelList()
        }
        fannelListCon.split("\n").filter {
            it.startsWith(fannelRawName)
        }.forEach {
            val destiFileObj = File("$destiDirPath/$it")
            if(
                destiFileObj.isFile
            ) return@forEach
            val destiFileParentDirPath = destiFileObj.parent
                ?: return
            val destiFileName = destiFileObj.name
            val downloadUrl = "$gitUserContentFannelPrefix/$it"
            val conByteArray = CurlManager.get(
                downloadUrl,
                String(),
                String(),
                2000,
            )
            if(
                !CurlManager.isConnOk(conByteArray)
            ) return@forEach
            FileSystems.writeFromByteArray(
                destiFileParentDirPath,
                destiFileName,
                conByteArray
            )
        }
    }
}