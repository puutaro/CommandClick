package com.puutaro.commandclick.util.file

import android.content.Context
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryPath
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object UrlFileSystems {

//    private var fannelListCon = String()

    val gitComPrefix = "https://github.com"
    val gitUserContentPrefix =
        "https://raw.githubusercontent.com"

    val cmdclickRepoGitUserContentPrefix =
        "$gitUserContentPrefix/" +
                "puutaro/commandclick-repository/master"
    val cmdClickAssetsRepoName = "CommandClickAssets"
    val cmdClickAssetsRepoPrefix = "${gitUserContentPrefix}/puutaro/${cmdClickAssetsRepoName}"
    private val gitUserContentManagePrefix =
        "$cmdclickRepoGitUserContentPrefix/manage"

    val gitUserContentFannelPrefix =
        "$cmdclickRepoGitUserContentPrefix/fannel"

    val gitUserContentFannelTarGzPrefix =
        "$cmdclickRepoGitUserContentPrefix/fannel_tar_gz"


    val readmeSuffix = "master/${UsePath.fannelReadmeName}"
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

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

    fun getFileNameFromUrl(
        urlStr: String
    ): String {
        return urlStr
            .split("/")
            .lastOrNull()
            ?: String()
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

    fun isFannelListByName(
        listLine: String,
        fannelName: String,
    ): Boolean {
        val isFannel =
            listLine == fannelName
        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
        val isFannelDir =
            listLine.startsWith(fannelDirName)
        return isFannel || isFannelDir
    }

    suspend fun getFannel(
        context: Context?,
        fannelName: String
    ): String? {
        val fannelUrl = sequenceOf(
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
                return@withContext String(byteArray)
            }
        }
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

//    fun createFile(
//        context: Context?,
//        destiDirPath: String,
//        fannelName: String,
//        fannelList: List<String>,
//    ){
//
//        fannelList.filter {
//            isFannelListByName(
//                it,
//                fannelName
//            )
//        }.forEach {
//            val destiFileObj = File("$destiDirPath/$it")
//            if(
//                destiFileObj.isFile
//                || destiFileObj.absolutePath.contains(
//                    "/${FannelHistoryPath.makePartPngDirCut()}/"
//                )
//            ) return@forEach
//            val downloadUrl = "$gitUserContentFannelPrefix/$it"
//            val conByteArray = CurlManager.get(
//                context,
//                downloadUrl,
//                String(),
//                String(),
//                2000,
//            )
//            if(
//                !CurlManager.isConnOk(conByteArray)
//            ) return@forEach
//            FileSystems.writeFromByteArray(
//                destiFileObj.absolutePath,
//                conByteArray
//            )
//        }
//    }
}