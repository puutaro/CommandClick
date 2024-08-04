package com.puutaro.commandclick.proccess.history

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.UrlHistoryAdapter
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.tsv.TsvTool
import java.io.File

object UrlHistoryIconTool {

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
        val base64Str = BitmapTool.Base64UrlIconForHistory.encode(
            favicon,
            50
        )
            ?: return
        val cmdclickUrlIconFilePath = makeIconHistoryPath(recentAppDirPath)
        TsvTool.updateByKeyDistinct(
            cmdclickUrlIconFilePath,
            url,
            base64Str,
        )
    }

    fun makeUrlIconList(
        currentAppDirPath: String
    ): List<Map<String, String>> {
        val urlKey = UrlHistoryAdapter.Companion.UrlHistoryMapKey.URL.key
        val iconBase64Key = UrlHistoryAdapter.Companion.UrlHistoryMapKey.ICON_BASE64_STR.key
        return ReadText(
            makeIconHistoryPath(currentAppDirPath)
        ).textToList()
            .distinct()
            .take(takeHistoryNum).map {
                val urlAndBase64Str = it.split("\t")
                val url = urlAndBase64Str.firstOrNull()
                    ?: return@map emptyMap()
                val base64Str = urlAndBase64Str.getOrNull(1)
                    ?: return@map emptyMap()
                mapOf(
                    urlKey to url,
                    iconBase64Key to base64Str
                )
            }.filter { it.isNotEmpty() }
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