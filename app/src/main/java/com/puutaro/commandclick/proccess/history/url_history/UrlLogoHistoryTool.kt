package com.puutaro.commandclick.proccess.history.url_history

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import java.io.File

object UrlLogoHistoryTool {

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
        val base64Str = BitmapTool.Base64Tool.encode(
            favicon,
            50
        ) ?: return
        val base64TxtName = makeBase64TxtName(url)
        val logoHisDirPath =
            makeLogoHistoryDirPath(recentAppDirPath)
        FileSystems.writeFile(
            File(logoHisDirPath, base64TxtName).absolutePath,
            base64Str,
        )
    }

    fun getCaptureBase64TxtPathByUrl(
        currentAppDirPath: String,
        url: String,
    ): File? {
        val base64TxtFile = File(
            makeLogoHistoryDirPath(currentAppDirPath),
            makeBase64TxtName(url),
        )
        if(
            !base64TxtFile.isFile
        ) return null
        return base64TxtFile
    }

    private fun makeBase64TxtName(url: String): String {
        return UrlHistoryPath.makePathNameFromUrl(url) + ".txt"
    }

    private fun makeIconBase64TxtPath(
        currentAppDirPath: String
    ): String {
        return File(
            "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}",
            ""
        ).absolutePath
    }

    fun makeLogoHistoryDirPath(
        currentAppDirPath: String
    ): String {
        return File(
            File(currentAppDirPath, UsePath.cmdclickUrlSystemDirRelativePath).absolutePath,
            "logo"
        ).absolutePath
    }
}