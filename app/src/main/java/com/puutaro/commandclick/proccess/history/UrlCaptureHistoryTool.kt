package com.puutaro.commandclick.proccess.history

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import java.io.File

object UrlCaptureHistoryTool {

    const val takeHistoryNum = 200
    var previousBase64Prefix = String()

    fun insertToHistory(
        currentAppDirPath: String,
        currentUrl: String?,
        capture: Bitmap?,
    ){
        if(
            currentUrl.isNullOrEmpty()
            ||  currentUrl.contains("/maps/")
            || capture == null
        ) return
        val base64Str = BitmapTool.Base64UrlImageForHistory.encode(
            capture,
            80
        ) ?: return
        val curBase64Prefix = takeFirst200Str(base64Str)
        if(
            base64Str.isEmpty()
            || curBase64Prefix == previousBase64Prefix
        ) return
        previousBase64Prefix = curBase64Prefix
        val isNotHttp = !currentUrl.startsWith(WebUrlVariables.httpsPrefix)
                && !currentUrl.startsWith(WebUrlVariables.httpsPrefix)
        if(
            isNotHttp
        ) return
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "capture_url.txt").absolutePath,
//            currentUrl
//        )
//        FileSystems.savePngFromBitMap(
//            File(UsePath.cmdclickDefaultAppDirPath, "capture.png").absolutePath,
//            capture
//        )
        val base64TxtName = UrlHistoryPath.makeBase64TxtFileNameByUrl(currentUrl)
        val captureHisDirPath = makeCaptureHistoryDirPath(currentAppDirPath)
        FileSystems.writeFile(
            File(captureHisDirPath, base64TxtName).absolutePath,
            base64Str,
        )
    }

    private fun takeFirst200Str(base64Str: String): String {
        return base64Str.take(200)
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