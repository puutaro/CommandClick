package com.puutaro.commandclick.fragment_lib.terminal_fragment.temp_download

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.BitmapTool
import com.puutaro.commandclick.util.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.CRC32
import java.util.zip.Checksum


object ImageTempDownloader {

    private val cmdclickTempDownloadDirPath =
        UsePath.cmdclickTempDownloadDirPath

    fun download(
        terminalFragment: TerminalFragment,
        url: String
    ){
        val onBase64Image =
            url.trim().startsWith(WebUrlVariables.base64JpegPrefix)
                    || url.trim().startsWith(WebUrlVariables.base64PngPrefix)
        FileSystems.removeAndCreateDir(
            cmdclickTempDownloadDirPath
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
            makeBitMap(
                terminalFragment,
                url
            )
        val imageName = BitmapTool.hash(
            bitmap
        )
        val file = File(
            cmdclickTempDownloadDirPath,
            "$imageName.png"
        )
        // ③
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
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
            val crc32: Checksum = CRC32()
            crc32.update(byteArr, 0, byteArr.size)
            val imageName = crc32.value
            val f = File(
                cmdclickTempDownloadDirPath,
                "$imageName.$extend"
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
//            withContext(Dispatchers.Main) {
//                finishToast(context)
//            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    suspend fun makeBitMap(
        terminalFragment: TerminalFragment,
        url: String
    ): Bitmap {
        return withContext(Dispatchers.IO) {
                Glide.with(terminalFragment.activity as FragmentActivity)
                    .asBitmap()
                    .load(url)
                    .submit()
                    .get()
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
                Pattern.compile(
                    "((?<=base64,).*\\s*)",
                    Pattern.DOTALL or Pattern.MULTILINE
                )
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