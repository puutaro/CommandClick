package com.puutaro.commandclick.fragment_lib.terminal_fragment.download

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Matcher
import java.util.regex.Pattern


object ImageDownloader {

    private val cmdclickTempDownloadDirPath = UsePath.cmdclickTempDownloadDirPath
    private val tempFileName = "temp"

    fun download(
        terminalFragment: TerminalFragment,
        url: String
    ){
        val context = terminalFragment.context
        val onBase64Image =
            url.trim().startsWith(WebUrlVariables.base64JpegPrefix)
                    || url.trim().startsWith(WebUrlVariables.base64PngPrefix)
//        Toast.makeText(
//            terminalFragment.context,
//            "${onBase64Image.toString()}\n${url}",
//            Toast.LENGTH_SHORT
//        ).show()
        FileSystems.removeDir(
            cmdclickTempDownloadDirPath
        )
        FileSystems.createDirs(
            cmdclickTempDownloadDirPath
        )
        FileSystems.writeFile(
            cmdclickTempDownloadDirPath,
            "txt.txt",
            url
        )
        CoroutineScope(Dispatchers.IO).launch {
            when(onBase64Image){
                false -> fromImageUrl(
                    terminalFragment,
                    url
                )
                else -> fromBase64(
                    terminalFragment,
                    url
                )

            }
        }
    }

    private suspend fun fromImageUrl(
        terminalFragment: TerminalFragment,
        url: String
    ){
        val context = terminalFragment.context
        val bitmap =
            withContext(Dispatchers.IO) {
                Glide.with(terminalFragment.activity as FragmentActivity)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get()
            }
        val file = File(
            cmdclickTempDownloadDirPath,
            "$tempFileName.png"
        )
        // ③
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
        }
        withContext(Dispatchers.Main) {
            finishToast(context)
        }
    }

    private suspend fun fromBase64(
        terminalFragment: TerminalFragment,
        fileContent: String?
    ) {
        if(
            fileContent.isNullOrEmpty()
        ) return
        val context = terminalFragment.context
        try {
            val attachment = parseBase64(fileContent)
            val extend = fileContent.removePrefix(
                WebUrlVariables.base64Prefix
            )
                .split(";")
                .first()

            val byteArr: ByteArray = Base64.decode(attachment, Base64.DEFAULT)
            val f = File(
                cmdclickTempDownloadDirPath,
                "$tempFileName.$extend"
            )
            withContext(Dispatchers.IO) {
                f.createNewFile()
            }
            val fo = withContext(Dispatchers.IO) {
                FileOutputStream(f)
            }
            withContext(Dispatchers.IO) {
                fo.write(byteArr)
            }
            withContext(Dispatchers.IO) {
                fo.close()
            }
            withContext(Dispatchers.Main) {
                finishToast(context)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun parseBase64(
        base64: String?
    ): String {
        if(
            base64.isNullOrEmpty()
        ) return String()
        try {
            val pattern: Pattern =
                Pattern.compile("((?<=base64,).*\\s*)", Pattern.DOTALL or Pattern.MULTILINE)
            val matcher: Matcher = pattern.matcher(base64)
            return if (matcher.find()) {
                matcher.group().toString()
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun finishToast(
        context: Context?,
    ){
        Toast.makeText(
            context,
            "File downloaded",
            Toast.LENGTH_SHORT
        ).show()
    }
}