package com.puutaro.commandclick.util.file

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryPath
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class UrlFileSystems {

//    private var fannelListCon = String()

    companion object {
        val gitComPrefix = "https://github.com"
        val gitUserContentPrefix =
            "https://raw.githubusercontent.com"

        val cmdclickRepoGitUserContentPrefix =
            "$gitUserContentPrefix/" +
                    "puutaro/commandclick-repository/master"
        private val gitUserContentManagePrefix =
            "$cmdclickRepoGitUserContentPrefix/manage"

        val gitUserContentFannelPrefix =
            "$cmdclickRepoGitUserContentPrefix/fannel"

        val readmeSuffix = "master/${UsePath.fannelReadmeName}"
//        enum class FirstCreateFannels(
//            val str: String,
//        ) {
//            SiteSummary("siteSummary"),
//            UrlIntender("urlIntender"),
//            ShareImage("shareImage"),
//            UrlTrans("urlTrans"),
//            TextToSpeech("textToSpeech"),
//            SelectMenu("selectMenu"),
//            CmdBookmaker("cmdBookmaker"),
//            WebSearcher("webSearcher"),
//            AskGpt35("askGpt35"),
//            CopyLink("copyLink"),
//            qrLReader("qrLReader"),
//            cmdYoutuberU("cmdYoutuberU"),
//            ttsPlayer("ttsPlayer"),
//            cmdMusicPlayerU("cmdMusicPlayerU"),
//            fileManager("fileManager"),
//        }
    }

    suspend fun getFannel(
        context: Context?,
        fannelName: String
    ): ByteArray? {
        val fannelUrl = listOf(
            gitUserContentFannelPrefix,
            fannelName,
        ).joinToString("/")
        return withContext(Dispatchers.IO) {
            CurlManager.get(
                context,
                fannelUrl,
                String(),
                String(),
                2_000
            ).let {
                byteArray ->
                val isConnOk = CurlManager.isConnOk(
                    byteArray
                )
                if(
                    !isConnOk
                ) return@withContext null
                return@withContext byteArray
            }
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

//    private suspend fun getFannelList(
//        context: Context?,
//    ): List<String> {
//        return withContext(Dispatchers.IO) {
//            execGetFannelList(
//                context
//            ).split("\n")
//        }
//    }

    fun getFannelList(
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

    fun extractFannelNameList(
        fannelList: List<String>
    ): List<String> {
        val jsFileSuffix = UsePath.JS_FILE_SUFFIX
        return fannelList.filter {
            val file = File(it)
            if (
                !file.parent.isNullOrEmpty()
            ) return@filter false
            it.endsWith(jsFileSuffix)
        }
    }

    fun extractFannelListByName(
        fannelList: List<String>,
        fannelName: String,
    ): List<String> {
        return fannelList.filter {
            val isFannel =
                it == fannelName
            val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
            val isFannelDir =
                it.startsWith(fannelDirName)
            isFannel && isFannelDir
        }
    }

    fun createFile(
        context: Context?,
        destiDirPath: String,
        fannelRawName: String = String(),
        fannelList: List<String>,
    ){

        fannelList.filter {
            it.startsWith(fannelRawName)
        }.forEach {
            val destiFileObj = File("$destiDirPath/$it")
            if(
                destiFileObj.isFile
                || destiFileObj.absolutePath.contains(
                    "/${FannelHistoryPath.makePartPngDirCut()}/"
                )
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

    fun createFileByOverride(
        context: Context?,
        destiDirPath: String,
        fannelRawName: String = String(),
        fannelList: List<String>,
    ){

        fannelList.filter {
            it.startsWith(fannelRawName)
        }.forEach {
            val destiFileObj = File("$destiDirPath/$it")
            if(
                destiFileObj.absolutePath.contains(
                    "/${FannelHistoryPath.makePartPngDirCut()}/"
                )
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