package com.puutaro.commandclick.util.file

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class UrlFileSystems {

    private var fannelListCon = String()

    private val gitUserContentManagePrefix =
        "$cmdclickRepoGitUserContentPrefix/manage"

    val gitUserContentFannelPrefix =
        "$cmdclickRepoGitUserContentPrefix/fannel"

    companion object {

        val gitComPrefix = "https://github.com"
        val gitUserContentPrefix =
            "https://raw.githubusercontent.com"

        val cmdclickRepoGitUserContentPrefix =
            "$gitUserContentPrefix/" +
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
            cmdYoutuberU("cmdYoutuberU"),
            ttsPlayer("ttsPlayer"),
            cmdMusicPlayerU("cmdMusicPlayerU"),
            fileManager("fileManager"),
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

    suspend fun getFannelList(
        context: Context?
    ){
        if(
            fannelListCon.isNotEmpty()
        ) return
        fannelListCon = withContext(Dispatchers.IO) {
            exeGetFannelList(
                context
            )
        }
    }

    fun exeGetFannelList(
        context: Context?
    ): String {
        val fannelListUrl =
            "$gitUserContentManagePrefix/fannels/list/fannels.txt"
        return CurlManager.get(
            context,
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

    suspend fun createFile(
        context: Context?,
        destiDirPath: String,
        fannelRawName: String = String(),
    ){

        withContext(Dispatchers.IO) {
            getFannelList(context)
        }
        fannelListCon.split("\n").filter {
            it.startsWith(fannelRawName)
        }.forEach {
            val destiFileObj = File("$destiDirPath/$it")
            if(
                destiFileObj.isFile
            ) return@forEach
            val downloadUrl = "$gitUserContentFannelPrefix/$it"
            val conByteArray = CurlManager.get(
                context,
                downloadUrl,
                String(),
                String(),
                2000,
            )
            if(
                !CurlManager.isConnOk(conByteArray)
            ) return@forEach
            FileSystems.writeFromByteArray(
                destiFileObj.absolutePath,
                conByteArray
            )
        }
    }
}