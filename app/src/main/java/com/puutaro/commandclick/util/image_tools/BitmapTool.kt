package com.puutaro.commandclick.util.image_tools

import android.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.Base64
import android.view.View
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
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

    object ImageRemaker {
        fun flipHorizontally(
            bitmap: Bitmap
        ): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val matrix = Matrix().apply { postScale(-1f, 1f, width / 2f, height / 2f) }
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        }

        // To flip vertically:
        fun flipVertically(
            bitmap: Bitmap
        ): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val matrix = Matrix().apply { postScale(1f, -1f, width / 2f, height / 2f) }
            return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        }

        fun makeRect(
            color: String,
            width: Int,
            height: Int,
        ): Bitmap {
            val bg: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bg)
            // paint background with the trick
            // paint background with the trick
            val rect_paint = Paint()
            rect_paint.style = Paint.Style.FILL
            rect_paint.color = Color.parseColor(color)
//            rect_paint.alpha = 0x80 // optional

            canvas.drawRect(0f, 0f, R.attr.width.toFloat(), R.attr.height.toFloat(), rect_paint) // that
            return bg
        }

        fun mask(
            bkBitmap: Bitmap,
            mascSrcBitmap: Bitmap,
        ): Bitmap {
            val output = Bitmap.createBitmap(
                mascSrcBitmap.width,
                mascSrcBitmap.height,
                Bitmap.Config.ARGB_8888
            )
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            val canvas = Canvas(output)
            canvas.drawBitmap(bkBitmap, 0f, 0f, null)
//        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = -0x1000000
            canvas.drawBitmap(mascSrcBitmap, 0f, 0f, paint)
            return output
        }

        fun cut(
            bitmap: Bitmap,
            limitWidthPx: Int,
            limitHeightPx: Int,
        ): Bitmap {
            // Set some constants
            val srcWidth = bitmap.width
            val srcHeight = bitmap.height
            val startX = (0..(srcWidth - limitWidthPx)).random()
            val startY = (0..(srcHeight - limitHeightPx)).random()

// Crop bitmap
            return Bitmap.createBitmap(bitmap, startX, startY, limitWidthPx, limitHeightPx, null, false)
        }

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

    fun generateGIF(
        bitMapList: List<Bitmap?>,
        delay: Int = 800,
        dispose: Int = 0,
        transparentColor: Int = 100
    ): ByteArray? {
        val bos = ByteArrayOutputStream()
        val encoder = AnimatedGifEncoder()
        encoder.setDelay(delay)
        encoder.setTransparent(-1)
        if(dispose > 0){
            encoder.setDispose(dispose)
        }
        if(transparentColor != 100){
            encoder.transparent = transparentColor
        }
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

