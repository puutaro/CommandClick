package com.puutaro.commandclick.util.file

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object AssetsFileManager {
    private const val assetsFannelsDirName = "fannels"
    const val textImagePingPath = "res/png/text_image.png"
    const val pdfImagePingPath = "res/png/pdf_image.png"
    private const val assetsBookmarkDirPath = "$assetsFannelsDirName/bookmark"
    const val assetsClipToHistoryForBookmark = "$assetsBookmarkDirPath/clipToHistory.js"
    private const val assetsDialogWebViewDirPath = "$assetsFannelsDirName/dialog_webview"
    const val assetsHighlightSchForDialogWebViewPath =
        "$assetsDialogWebViewDirPath/highlightSchForDialogWebView.js"
    const val assetsHighlightCopy =
        "$assetsDialogWebViewDirPath/highlightCopy.js"
    const val ggleSchBoxFocus = "${assetsDialogWebViewDirPath}/ggleSchBoxFocus.js"
    private const val assetsEditDirPath = "$assetsFannelsDirName/edit"
    const val iconSelectBox = "$assetsEditDirPath/iconSelectBox.js"
    const val fannelShellDirPath = "$assetsFannelsDirName/shell"
    const val ubuntuSetupDirPath = "ubuntu_setup"
    const val ubunutSupportDirPath = "$ubuntuSetupDirPath/support"
    const val ubunutSupportCmdDirPath = "$ubunutSupportDirPath/cmd"
    const val cmdTerminalDirPath = "$assetsFannelsDirName/cmdTerminal"
    const val cmdListTxt = "$cmdTerminalDirPath/list/cmdList.txt"
    const val extraKeyListTxt = "$cmdTerminalDirPath/list/extraKeyList.txt"

    fun concatAssetsPath(
        pathList: List<String>
    ): String {
        return pathList.joinToString("/").replace(
            Regex("[/]+"),
            "/"
        ).removePrefix("/")
    }

    fun readFromAssets(
        context: Context?,
        assetRelativePath: String,
    ): String {
        val assetsManager = context?.assets
        val fis2: InputStream =
            try {
                assetsManager?.open(
                    assetRelativePath
                ) ?: return String()
            } catch(e: Exception){
                return String()
            }
        val contents = try {
            fis2.bufferedReader().use {
                it.readText()
            }
        } catch(e: Exception) {
            fis2.close()
            return String()
        }
        fis2.close()
        return contents
    }

    fun copyFileOrDirFromAssetsWhenNoExist(
        context: Context?,
        path: String,
        replacePrefix: String,
        targetDirPath: String,
        targetFileName: String,
        escapeRelativeAssetsPathList: List<String>,
    ){
        if(
            File(
                targetDirPath,
                targetFileName
            ).isFile
        ) return
        copyFileOrDirFromAssets(
            context,
            path,
            replacePrefix,
            targetDirPath,
            escapeRelativeAssetsPathList,
        )
    }

    fun copyFileOrDirFromAssets(
        context: Context?,
        path: String,
        replacePrefix: String,
        targetDirPath: String,
        escapeRelativeAssetsPathList: List<String>,
    ) {
        val assetManager = context?.assets
            ?: return
        var assets: Array<String>? = null
        try {
            assets = assetManager.list(path)
                ?: return
            val isFile = assets.isEmpty()
            if (isFile) {
                copyFileFromAssets(
                    context,
                    path,
                    replacePrefix,
                    targetDirPath,
                    escapeRelativeAssetsPathList,
                )
                return
            }
            val dirPath =
                "${targetDirPath}/${path.removePrefix(replacePrefix)}".replace(
                    Regex("[/]+"),
                    "/"
                )
            val dir = File(
                dirPath
            )
            if (
                !dir.exists()
            ) dir.mkdir()
            for (i in assets.indices) {
                copyFileOrDirFromAssets(
                    context,
                    path + "/" + assets[i],
                    replacePrefix,
                    targetDirPath,
                    escapeRelativeAssetsPathList,
                )
            }
        } catch (ex: IOException) {
            Log.e("tag", "I/O Exception", ex)
        }
    }

    private fun copyFileFromAssets(
        context: Context?,
        assetsFilePath: String,
        replacePrefix: String,
        targetDirPath: String,
        escapeRelativeAssetsPathList: List<String>,
    ) {
        val assetsRelativeFilePath = assetsFilePath
            .removePrefix(replacePrefix)
            .removePrefix("/")
        val newFilePathObj = File(
            targetDirPath,
            assetsRelativeFilePath
        )
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "cmdclickconfigEscape.txt").absolutePath,
//            listOf(
//                "newFilePath: ${newFilePathObj.absolutePath}",
//                "newFilePathObj.isFile: ${newFilePathObj.isFile}",
//                "assetsRelativeFilePath : ${assetsRelativeFilePath}",
//                "escapeRelativeAssetsPathList: ${escapeRelativeAssetsPathList}",
//                "contain: ${escapeRelativeAssetsPathList.contains(assetsRelativeFilePath)}",
//            ).joinToString("\n\n") + "\n------\n"
//        )
        val isContainEscapeRelativePathList = escapeRelativeAssetsPathList.any {
            escapeRelativePath ->
            assetsRelativeFilePath.startsWith(escapeRelativePath)
        }
        if(
            newFilePathObj.isFile
            && isContainEscapeRelativePathList
        ) return
        val newFilePath = newFilePathObj.absolutePath
        val assetManager = context?.getAssets()
            ?: return
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = assetManager.open(assetsFilePath)
            out = FileOutputStream(newFilePath)
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()
            `in` = null
            out.flush()
            out.close()
            out = null
        } catch (e: java.lang.Exception) {
            Log.e("tag", e.message!!)
        }
    }

    fun copyFileToDirFromAssets(
        context: Context?,
        targetDirPath: String,
        assetsFilePath: String,
    ){
        val clipToHistoryCon = readFromAssets(
            context,
            assetsClipToHistoryForBookmark
        )
        val fileName = File(assetsFilePath).name
        val targetFilePathObj = File("$targetDirPath/$fileName")
        if(targetFilePathObj.isFile) return
        FileSystems.writeFile(
            File(
                targetDirPath,
                fileName
            ).absolutePath,
            clipToHistoryCon
        )
    }
}