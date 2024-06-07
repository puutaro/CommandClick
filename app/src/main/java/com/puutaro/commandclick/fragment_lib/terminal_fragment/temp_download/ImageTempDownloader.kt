package com.puutaro.commandclick.fragment_lib.terminal_fragment.temp_download

import android.graphics.Bitmap
import android.util.Base64
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.file.FileSystems
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
                    || url.trim().startsWith(WebUrlVariables.base64WebpPrefix)
        FileSystems.removeAndCreateDir(
            cmdclickTempDownloadDirPath
        )
        CoroutineScope(Dispatchers.IO).launch {
            when(onBase64Image){
                false -> fromImageUrl(
                    terminalFragment,
                    url
                )
                else -> fromBase64(url)

            }
        }
    }

    private suspend fun fromImageUrl(
        terminalFragment: TerminalFragment,
        url: String
    ){
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
        fileContent: String?
    ) {
        if(
            fileContent.isNullOrEmpty()
        ) return
        try {
            val attachment = withContext(Dispatchers.IO) {
                parseBase64(fileContent)
            }
            val extend = withContext(Dispatchers.IO) {
                fileContent.removePrefix(
                    WebUrlVariables.base64Prefix
                )
                    .split(";")
                    .first()
            }

            val byteArr: ByteArray = withContext(Dispatchers.IO) {
                Base64.decode(
                    attachment,
                    Base64.DEFAULT
                )
            }
            val f = withContext(Dispatchers.IO) {
                val crc32: Checksum = CRC32()
                crc32.update(byteArr, 0, byteArr.size)
                val imageName = crc32.value
                File(
                    cmdclickTempDownloadDirPath,
                    "$imageName.$extend"
                )
            }
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private suspend fun makeBitMap(
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
}