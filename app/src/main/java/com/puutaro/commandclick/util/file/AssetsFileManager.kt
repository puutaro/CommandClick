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
    private const val assetsEditDirPath = "$assetsFannelsDirName/edit"
    const val iconSelectBox = "$assetsEditDirPath/iconSelectBox.js"
    const val fannelShellDirPath = "$assetsFannelsDirName/shell"
    const val fannelIfShellDirPath = "${fannelShellDirPath}/if"
    const val fannelMakeShellDirPath = "${fannelShellDirPath}/make"
    const val fannelSaveShellDirPath = "${fannelShellDirPath}/save"
    const val savePreviousPlayListShellPath =
        "${fannelSaveShellDirPath}/savePreviousPlayList.sh"
    const val judgeTsvValueShellPath = "${fannelIfShellDirPath}/judge_tsv_value.sh"
    const val judgeListDirShellPath = "${fannelIfShellDirPath}/judge_list_dir.sh"
    const val makeHeaderTitlePath = "${fannelMakeShellDirPath}/makeHeaderTitle.sh"
    const val ubuntuSetupDirPath = "ubuntu_setup"
    const val ubunutSupportDirPath = "$ubuntuSetupDirPath/support"
    const val ubunutSupportCmdDirPath = "$ubunutSupportDirPath/cmd"
    const val cmdTerminalDirPath = "$assetsFannelsDirName/cmdTerminal"
    const val cmdListTxt = "$cmdTerminalDirPath/list/cmdList.txt"
    const val extraKeyListTxt = "$cmdTerminalDirPath/list/extraKeyList.txt"

    fun readFromAssets(
        context: Context?,
        assetRelativePath: String,
    ): String {
        val assetsManager = context?.assets
        val fis2: InputStream =
            assetsManager?.open(
                assetRelativePath
            ) ?: return String()
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
        targetFileName: String
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
        )
    }

    fun copyFileOrDirFromAssets(
        context: Context?,
        path: String,
        replacePrefix: String,
        targetDirPath: String,
    ) {
        val assetManager = context?.assets
            ?: return
        var assets: Array<String>? = null
        try {
            assets = assetManager.list(path)
                ?: return
            if (
                assets.size == 0
            ) {
                copyFileFromAssets(
                    context,
                    path,
                    replacePrefix,
                    targetDirPath
                )
                return
            }
            val dir = File(
                "${targetDirPath}/${path.removePrefix("${replacePrefix}/")}"
            )
            if (
                !dir.exists()
            ) dir.mkdir()
            for (i in assets.indices) {
                copyFileOrDirFromAssets(
                    context,
                    path + "/" + assets[i],
                    replacePrefix,
                    targetDirPath
                )
            }
        } catch (ex: IOException) {
            Log.e("tag", "I/O Exception", ex)
        }
    }

    private fun copyFileFromAssets(
        context: Context?,
        filename: String,
        replacePrefix: String,
        targetDirPath: String
    ) {
        val assetManager = context?.getAssets()
            ?: return
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = assetManager.open(filename)
            val newFileName = targetDirPath + "/" + filename.removePrefix("${replacePrefix}/")
            out = FileOutputStream(newFileName)
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
        val assetsFileObj = File("$targetDirPath/$fileName")
        if(assetsFileObj.isFile) return
        FileSystems.writeFile(
            File(
                targetDirPath,
                fileName
            ).absolutePath,
            clipToHistoryCon
        )
    }
}