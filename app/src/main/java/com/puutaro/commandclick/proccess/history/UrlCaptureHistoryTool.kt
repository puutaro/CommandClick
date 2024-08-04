package com.puutaro.commandclick.proccess.history

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.component.adapter.UrlHistoryAdapter
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.tsv.TsvTool
import java.io.File

object UrlCaptureHistoryTool {

    const val takeHistoryNum = 200

    fun insertToHistory(
        currentAppDirPath: String,
        currentUrl: String?,
        favicon: Bitmap?,
    ){
        if(
            currentUrl.isNullOrEmpty()
            ||  currentUrl.contains("/maps/")
        ) return
        val base64Str = BitmapTool.Base64UrlIconForHistory.encode(
            favicon,
            10
        ) ?: return
        val cmdclickUrlCaptureFilePath = makeCaptureHistoryPath(currentAppDirPath)
        if(
            base64Str.isEmpty()
        ) return
        val isNotHttp = !currentUrl.startsWith(WebUrlVariables.httpsPrefix)
                && !currentUrl.startsWith(WebUrlVariables.httpsPrefix)
        if(
            isNotHttp
        ) return
        TsvTool.updateByKeyDistinct(
            cmdclickUrlCaptureFilePath,
            currentUrl,
            base64Str,
        )
    }


    fun makeUrlCaptureList(
        currentAppDirPath: String
    ): List<Map<String, String>> {
        val urlKey = UrlHistoryAdapter.Companion.UrlHistoryMapKey.URL.key
        val captureBase64Key = UrlHistoryAdapter.Companion.UrlHistoryMapKey.CAPTURE_BASE64_STR.key
        return ReadText(
            makeCaptureHistoryPath(currentAppDirPath)
        ).textToList()
            .distinct()
            .take(takeHistoryNum).map {
                val urlAndBase64Str = it.split("\t")
                val url = urlAndBase64Str.firstOrNull()
                    ?: return@map emptyMap()
                val base64Str = urlAndBase64Str.getOrNull(1)
                    ?: return@map emptyMap()
                url to base64Str
                mapOf(
                    urlKey to url,
                    captureBase64Key to base64Str,
                )
            }
    }

    private fun makeCaptureHistoryPath(
        currentAppDirPath: String
    ): String {
        return File(
            "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}",
            UsePath.cmdclickUrlCaptureFileName
        ).absolutePath
    }
}