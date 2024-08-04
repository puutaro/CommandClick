package com.puutaro.commandclick.proccess.history

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
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
            || favicon == null
        ) return
        val base64Str = BitmapTool.Base64UrlIconForHistory.encode(
            favicon,
            10
        ) ?: return
        if(
            base64Str.isEmpty()
        ) return
        val isNotHttp = !currentUrl.startsWith(WebUrlVariables.httpsPrefix)
                && !currentUrl.startsWith(WebUrlVariables.httpsPrefix)
        if(
            isNotHttp
        ) return
        val base64TxtName = UrlHistoryPath.makeBase64TxtFileNameByUrl(currentUrl)
        val captureHisDirPath = makeCaptureHistoryDirPath(currentAppDirPath)
        FileSystems.writeFile(
            File(captureHisDirPath, base64TxtName).absolutePath,
            base64Str,
        )
    }

    fun getCaptureBase64TxtPathByUrl(
        currentAppDirPath: String,
        url: String,
    ): File? {
        val base64TxtFile = File(
            makeCaptureHistoryDirPath(currentAppDirPath),
            UrlHistoryPath.makeBase64TxtFileNameByUrl(url),
        )
        if(
            !base64TxtFile.isFile
        ) return null
        return base64TxtFile

    }

    fun makeCaptureHistoryDirPath(
        currentAppDirPath: String
    ): String {
        return File(
            File(currentAppDirPath, UsePath.cmdclickUrlSystemDirRelativePath).absolutePath,
            "capture"
        ).absolutePath
    }
}