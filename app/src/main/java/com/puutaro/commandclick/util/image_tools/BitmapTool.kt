package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.ScreenSizeCalculator
import com.puutaro.commandclick.util.file.FileSystems
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.Arrays


object BitmapTool {

    fun hash(
        bitmap: Bitmap
    ): String {
        val buffer = ByteBuffer.allocate(bitmap.getByteCount())
        bitmap.copyPixelsToBuffer(buffer)
        return Arrays.hashCode(buffer.array()).toString()
    }

    fun resizeByScreenWidth(
        fragment: Fragment,
        imagePath: String,
    ): Bitmap {
        val beforeResizeBitMap = BitmapFactory.decodeFile(imagePath)
        val baseWidth = ScreenSizeCalculator.dpWidth(fragment)
//                                    resizeScale = 180.0 / beforeResizeBitMap.width
        val resizeScale: Double =
            (baseWidth / beforeResizeBitMap.width).toDouble()
        return Bitmap.createScaledBitmap(
            beforeResizeBitMap,
            (beforeResizeBitMap.width * resizeScale).toInt(),
            (beforeResizeBitMap.height * resizeScale).toInt(),
            true
        )
    }

    fun convertFileToBitmap(path: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(path)
        } catch (e: Exception){
            null
        }
    }

    fun convertFileToByteArray(
        path: String,
        quality: Int = 100,
    ): ByteArray? {
        if(
            !File(path).isFile
        ) return null
        return try {
            val stream = ByteArrayOutputStream()
            val bitmap = BitmapFactory.decodeFile(path)
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
            val byteArray = stream.toByteArray()
            stream.close()
            byteArray
        } catch (e: Exception){
            null
        }
    }

    fun convertBitmapToByteArrayForGif(
        path: String,
    ): ByteArray? {
        if(
            !File(path).isFile
        ) return null
        return try {
            val inputStream = FileInputStream(path)
            val buffer = ByteArray(1024)
            var bytesRead: Int
            val output = ByteArrayOutputStream()
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            val byteArray = output.toByteArray()
            output.close()
            inputStream.close()
            byteArray
        } catch (e: Exception){
            null
        }
    }

    fun resizeByMaxHeight(
        beforeResizeBitMap: Bitmap,
        maxHeight: Double,
    ): Bitmap {
        val resizeScale: Double =
            (maxHeight / beforeResizeBitMap.height)
        return Bitmap.createScaledBitmap(
            beforeResizeBitMap,
            (beforeResizeBitMap.width * resizeScale).toInt(),
            (beforeResizeBitMap.height * resizeScale).toInt(),
            true
        )
    }

    fun getScreenShotFromView(
        v: View?
    ): Bitmap? {
        if(
            v == null
        ) return null
        // create a bitmap object
        val screenshot = Bitmap.createBitmap(
            v.measuredWidth,
            v.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        // Now draw this bitmap on a canvas
        val canvas = Canvas(screenshot)
        v.draw(canvas)
        return screenshot
    }

    fun convertBitmapToByteArray(
        myBitmap: Bitmap,
        quality: Int = 100,
    ): ByteArray {
        val stream = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
        val byteArray = stream.toByteArray()
        stream.close()
        return byteArray
    }


    fun getLowScreenShotFromView(
        v: View?
    ): Bitmap? {
        if(
            v == null
        ) return null
        // create a bitmap object
        val screenshot = Bitmap.createBitmap(
            v.measuredWidth,
            v.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        // Now draw this bitmap on a canvas
        val canvas = Canvas(screenshot)
        v.draw(canvas)
        return screenshot
    }

    object Base64Tool {

        fun decode(base64Str: String?): Bitmap? {
            if(
                base64Str.isNullOrEmpty()
            ) return null
            return try {
                val decodedBytes: ByteArray = Base64.decode(
                    base64Str,
                    Base64.NO_WRAP
                )
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception){
                null
            }
        }

        fun decodeAsByteArray(base64Str: String?): ByteArray? {
            if(
                base64Str.isNullOrEmpty()
            ) return null
            return try {
                Base64.decode(
                    base64Str,
                    Base64.NO_WRAP
                )
            } catch (e: Exception){
                null
            }
        }

        fun encode(
            bitmap: Bitmap?,
            quality: Int = 100
        ): String? {
            if(
                bitmap == null
            ) return null
            return try {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
                encodeFromByteArray(outputStream.toByteArray())
            } catch (e: Exception){
                null
            }
        }

        fun encodeFromByteArray(
            byteArray: ByteArray?,
        ): String? {
            if(
                byteArray == null
            ) return null
            return try {
                Base64.encodeToString(byteArray, Base64.NO_WRAP)
            } catch (e: Exception){
                FileSystems.writeFile(
                    File(UsePath.cmdclickDefaultAppDirPath, "gitErr_encodeFromByteArray.txt").absolutePath,
                    e.toString()
                )
                null
            }
        }
    }

    fun generateGIF(bitMapList: List<Bitmap?>): ByteArray? {
        val bos = ByteArrayOutputStream()
        val encoder = AnimatedGifEncoder()
        encoder.setDelay(800)
//        encoder.setQuality(10)

        encoder.start(bos)
        for (bitmap in bitMapList) {
            if(bitmap == null) continue
            encoder.addFrame(bitmap)
        }
        encoder.finish()
        return bos.toByteArray()
    }

    fun saveGif(
        path: String,
        byteArray: ByteArray?
    ) {
        try {
            val outStream = FileOutputStream(path)
            outStream.write(byteArray)
            outStream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun saveGifTxt(
        path: String,
        byteArray: ByteArray?
    ) {
        try {
            val base64Str = Base64Tool.encodeFromByteArray(byteArray)
                ?: return
            FileSystems.writeFile(
                path,
                base64Str
            )
        } catch (e: java.lang.Exception) {
           FileSystems.writeFile(
               File(UsePath.cmdclickDefaultAppDirPath, "gitErr.txt").absolutePath,
               e.toString()
           )
        }
    }
}

