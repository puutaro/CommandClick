package com.puutaro.commandclick.proccess.history

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.BitmapTool
import java.io.File

object UrlIconTool {

    const val takeHistoryNum = 200

    fun insertToHistory(
        recentAppDirPath: String,
        url: String?,
        favicon: Bitmap?,
    ){
        if(
            url.isNullOrEmpty()
            ||  url.contains("/maps/")
        ) return
        val base64Str = BitmapTool.Base64UrlIconForHistory.encode(favicon)
            ?: return
        val cmdclickUrlIconFilePath = makeIconHistoryPath(recentAppDirPath)
        val curIconHistory = ReadText(
            cmdclickUrlIconFilePath
        ).readText()
        if(
            curIconHistory.contains(url)
        ) return
        val updatingHistoryCon =
            "${url}\t${base64Str}\n" +
                    curIconHistory.split("\n")
                        .take(takeHistoryNum)
                        .joinToString("\n")
        FileSystems.writeFile(
            cmdclickUrlIconFilePath,
            updatingHistoryCon
        )
    }

    fun makeUrlIconList(
        currentAppDirPath: String
    ): List<Pair<String, String>> {
        val blankReturnPair = String() to String()
        return ReadText(
            makeIconHistoryPath(currentAppDirPath)
        ).textToList()
            .distinct()
            .take(takeHistoryNum).map {
                val urlAndBase64Str = it.split("\t")
                val url = urlAndBase64Str.firstOrNull()
                    ?: return@map blankReturnPair
                val base64Str = urlAndBase64Str.getOrNull(1)
                    ?: return@map blankReturnPair
                url to base64Str
            }
    }

    private fun makeIconHistoryPath(
        currentAppDirPath: String
    ): String {
        return File(
            "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}",
            UsePath.cmdclickUrlIconFileName
        ).absolutePath
    }
}