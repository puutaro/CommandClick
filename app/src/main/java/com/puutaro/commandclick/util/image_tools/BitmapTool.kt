package com.puutaro.commandclick.util.image_tools

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.util.Base64
import android.util.TypedValue
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.puutaro.commandclick.util.file.FileSystems
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.Arrays
import kotlin.random.Random
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import androidx.core.graphics.scale
import androidx.core.graphics.get
import androidx.core.graphics.set
import androidx.core.graphics.drawable.toDrawable


object BitmapTool {

    fun hash(
        bitmap: Bitmap
    ): String {
        val buffer = ByteBuffer.allocate(bitmap.getByteCount())
        bitmap.copyPixelsToBuffer(buffer)
        return Arrays.hashCode(buffer.array()).toString()
    }

    fun resizeByScreenWidth(
        activity: Activity?,
        imagePath: String,
    ): Bitmap {
        val beforeResizeBitMap = BitmapFactory.decodeFile(imagePath)
        val baseWidth = ScreenSizeCalculator.dpWidth(activity)
//                                    resizeScale = 180.0 / beforeResizeBitMap.width
        val resizeScale: Double =
            (baseWidth / beforeResizeBitMap.width).toDouble()
        return beforeResizeBitMap.scale(
            (beforeResizeBitMap.width * resizeScale).toInt(),
            (beforeResizeBitMap.height * resizeScale).toInt()
        )
    }

    fun rotate(
        bitmapOrg: Bitmap,
        degrees: Float,
    ): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.width, bitmapOrg.height, matrix, true)
    }

    fun concatByHorizon(
        c: Bitmap,
        s: Bitmap,
        duplication: Int,
    ): Bitmap { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        var cs: Bitmap? = null
        val width = c.width + s.width - duplication
        val height = when(c.height > s.height) {
            false -> s.height
            else -> c.height
        }
        cs = createBitmap(width, height)
        val comboImage = Canvas(cs)
        comboImage.drawBitmap(c, 0f, 0f, null)
        val startX = c.width.toFloat() - duplication
        comboImage.drawBitmap(s, startX, 0f, null)

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
        /*String tmpImg = String.valueOf(System.currentTimeMillis()) + ".png";

    OutputStream os = null;
    try {
      os = new FileOutputStream(loc + tmpImg);
      cs.compress(CompressFormat.PNG, 100, os);
    } catch(IOException e) {
      Log.e("combineImages", "problem combining images", e);
    }*/return cs
    }

    fun convertBitmapToDrawable(
        context: Context,
        bitmap: Bitmap?): BitmapDrawable? {
        return bitmap?.toDrawable(context.getResources())
    }

    fun convertFileToBitmap(path: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(path)
        } catch (e: Exception){
            null
        }
    }

    object GradientBitmap {

        enum class GradOrient{
            HORIZON_LINER,
            VERTICAL_LINER,
            LINEAR,
            DIAGONAL,
            BOTH,
            VERTICAL_BOTTOM_TO_TOP,
            LEFT_RIGHT,
            TR_BL,
            BL_TR,
        }

        private val horizonLinearGradOrientList = arrayOf(
            GradientDrawable.Orientation.LEFT_RIGHT,
            GradientDrawable.Orientation.RIGHT_LEFT,
        )

        private val verticalLinearGradOrientList = arrayOf(
            GradientDrawable.Orientation.TOP_BOTTOM,
            GradientDrawable.Orientation.BOTTOM_TOP,
        )

        private val linearGradOrientList = arrayOf(
            GradientDrawable.Orientation.TOP_BOTTOM,
            GradientDrawable.Orientation.BOTTOM_TOP,
            GradientDrawable.Orientation.LEFT_RIGHT,
            GradientDrawable.Orientation.RIGHT_LEFT,
        )

        private val diagonalGradOrientList = arrayOf(
            GradientDrawable.Orientation.BL_TR,
            GradientDrawable.Orientation.TR_BL,
            GradientDrawable.Orientation.TL_BR,
            GradientDrawable.Orientation.BR_TL,
        )

        private val bottomTopGradOrientList = arrayOf(
            GradientDrawable.Orientation.BOTTOM_TOP,
        )
        private val leftRightGradOrientList = arrayOf(
            GradientDrawable.Orientation.LEFT_RIGHT,
        )
        fun addGradient(originalBitmap: Bitmap, startColor: Int, endColor: Int): Bitmap {
            val width = originalBitmap.width
            val height = originalBitmap.height
            val updatedBitmap = createBitmap(width, height)
            val canvas = Canvas(updatedBitmap)

            canvas.drawBitmap(originalBitmap, 0f, 0f, null)

            val paint = Paint()
            val shader: LinearGradient =
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    height.toFloat(),
                    startColor, //-0xf2dae,
                    endColor, //-0xf8cfb,
                    Shader.TileMode.CLAMP
                )
            paint.setShader(shader)
            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

            return updatedBitmap
        }
        fun makeGradientBitmap2(
            width: Int,
            height: Int,
            colorIntArray: IntArray,
            gradOrient: GradOrient
//            startColor: String,
//            endColor: String,
        ): Bitmap {
//            val color = intArrayOf(
//                Color.parseColor(startColor),
//                Color.parseColor(endColor),
//            )
            val gradientOrientationList = when(gradOrient){
                GradOrient.HORIZON_LINER -> horizonLinearGradOrientList
                GradOrient.VERTICAL_LINER -> verticalLinearGradOrientList
                GradOrient.BOTH -> linearGradOrientList + diagonalGradOrientList
                GradOrient.LINEAR -> linearGradOrientList
                GradOrient.DIAGONAL -> diagonalGradOrientList
                GradOrient.VERTICAL_BOTTOM_TO_TOP -> bottomTopGradOrientList
                GradOrient.LEFT_RIGHT -> leftRightGradOrientList
                GradOrient.TR_BL -> arrayOf(GradientDrawable.Orientation.TR_BL)
                GradOrient.BL_TR -> arrayOf(GradientDrawable.Orientation.BL_TR)
            }
            val gradient = GradientDrawable(gradientOrientationList.random(), colorIntArray)
            gradient.cornerRadius = 0f
            return gradient.toBitmap(width, height)
        }
    }

    fun convertFileToByteArray(
        path: String,
        quality: Int = 100,
    ): ByteArray? {
        val pathFile = File(path)
        if(
            !pathFile.isFile
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
        return beforeResizeBitMap.scale(
            (beforeResizeBitMap.width * resizeScale).toInt(),
            (beforeResizeBitMap.height * resizeScale).toInt()
        )
    }

    fun getScreenShotFromView(
        v: View?
    ): Bitmap? {
        if(
            v == null
        ) return null
        // create a bitmap object
        val screenshot = createBitmap(v.measuredWidth, v.measuredHeight)
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
        val screenshot = createBitmap(v.measuredWidth, v.measuredHeight)
        // Now draw this bitmap on a canvas
        val canvas = Canvas(screenshot)
        v.draw(canvas)
        return screenshot
    }

    object ImageTransformer {

        fun stretchImageWithoutBlur(
            originalBitmap: Bitmap?,
            targetWidth: Int,
            targetHeight: Int
        ): Bitmap? {
            if(originalBitmap == null) {
                return originalBitmap
            }
            // Create a new bitmap with the target dimensions
            val scaledBitmap = createBitmap(targetWidth, targetHeight)

            // Create a canvas to draw on the new bitmap
            val canvas = Canvas(scaledBitmap)

            // Set up the paint for drawing
            val paint = Paint().apply {
                isFilterBitmap = false  // Disable bitmap filtering
                isAntiAlias = false     // Disable anti-aliasing
            }

            // Calculate the scaling factors
            val scaleX = targetWidth.toFloat() / originalBitmap.width
            val scaleY = targetHeight.toFloat() / originalBitmap.height

            // Create a matrix to apply the scaling
            val matrix = Matrix().apply {
                setScale(scaleX, scaleY)
            }

            // Draw the scaled bitmap onto the canvas
            canvas.drawBitmap(originalBitmap, matrix, paint)

            return scaledBitmap
        }


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

        fun createKasureBitmap(bitmap: Bitmap): Bitmap {
            val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val randomList = (-10..10)
            for (x in 0 until resultBitmap.width) {
                for (y in 0 until resultBitmap.height) {
                    val pixel = resultBitmap[x, y]
                    if (
                        Color.alpha(pixel) == 0
                    ) continue
//                    val red = Color.red(pixel) + randomList.random()
//                    val green = Color.green(pixel) + randomList.random()
//                    val blue = Color.blue(pixel) + randomList.random()
                    if (randomList.random() < 3) {
                        resultBitmap[x, y] = Color.TRANSPARENT
                    }

                }
            }
            return resultBitmap
        }

        fun distortImage(bitmap: Bitmap): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val newBitmap = createBitmap(width, height)
            val canvas = Canvas(newBitmap)

            val paint = Paint()
            paint.isFilterBitmap = true // アンチエイリアス処理

            val randomList = (-10..10)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    // ランダムなオフセットを計算
                    val pixel = newBitmap[x, y]
                    if (
                        Color.alpha(pixel) == 0
                    ) {
                        paint.color = Color.TRANSPARENT
                        canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                        continue
                    }
                    val offsetX = randomList.random()
                    val offsetY = randomList.random()

                    // 新しい座標を計算
                    val newX = x + offsetX
                    val newY = y + offsetY

                    // 範囲外の場合は描画しない
                    if (newX in 0 until width && newY in 0 until height) {
                        canvas.drawPoint(newX.toFloat(), newY.toFloat(), paint)
                    }
                }
            }

            return newBitmap
        }

        fun applyUnevenFade(originalBitmap: Bitmap): Bitmap {
            val width = originalBitmap.width
            val height = originalBitmap.height
            val result = createBitmap(width, height)
            val canvas = Canvas(result)

            // 不均質な透明度を適用
            val paint = Paint()
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = originalBitmap[x, y]
                    if (
                        Color.alpha(pixel) == 0
                    ) {
                        paint.color = Color.TRANSPARENT
                        canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                        continue
                    }
                    val alpha = (Random.nextFloat() * 255).toInt()
                    paint.color =
                        Color.argb(alpha, Color.red(pixel), Color.green(pixel), Color.blue(pixel))
                    canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                }
            }
            return result
        }

        fun createNozeBitmap(bitmap: Bitmap): Bitmap {
            val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val randomList = (-10..10)
            for (x in 0 until resultBitmap.width) {
                for (y in 0 until resultBitmap.height) {
                    val pixel = resultBitmap[x, y]
                    val red = Color.red(pixel) + randomList.random()
                    val green = Color.green(pixel) + randomList.random()
                    val blue = Color.blue(pixel) + randomList.random()
                    resultBitmap[x, y] = Color.argb(255, red, green, blue)
                }
            }

            return resultBitmap
        }

        fun addPadding(Src: Bitmap, padding_x: Int, padding_y: Int): Bitmap {
            val outputimage = createBitmap(Src.width + padding_x, Src.height + padding_y)
            val can = Canvas(outputimage)
            can.drawARGB(0, 0, 0, 0) //This represents White color
            can.drawBitmap(Src, (padding_x / 2f), (padding_y / 2f), null)
            return outputimage
        }

        fun makeRect(
            color: String?,
            width: Int,
            height: Int,
        ): Bitmap {
            val bg: Bitmap = createBitmap(width, height)
            val canvas = Canvas(bg)
            // paint background with the trick
            // paint background with the trick
            val rect_paint = Paint()
            rect_paint.style = Paint.Style.FILL
            rect_paint.color = when(color == null) {
                true -> Color.TRANSPARENT
                else -> color.toColorInt()
            }
//            rect_paint.alpha = 0x80 // optional

            canvas.drawRect(0f, 0f, R.attr.width.toFloat(), R.attr.height.toFloat(), rect_paint) // that
            return bg
        }

        fun invertMonoBitmap(bitmap: Bitmap): Bitmap {
            val resultBitmap = createBitmap(bitmap.width, bitmap.height)
            val canvas = Canvas(resultBitmap)

            val paint = Paint()
            val colorMatrix = ColorMatrix().apply {
                set(floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                ))
            }
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            return resultBitmap
        }

        fun reduceContrast(bitmap: Bitmap): Bitmap {
            val resultBitmap = createBitmap(bitmap.width, bitmap.height)
            val canvas = Canvas(resultBitmap)

            val paint = Paint()
            val colorMatrix = ColorMatrix().apply {
                set(floatArrayOf(
                    0.8f, 0f, 0f, 0f, 32f,
                    0f, 0.8f, 0f, 0f, 32f,
                    0f, 0f, 0.8f, 0f, 32f,
                    0f, 0f, 0f, 1f, 0f
                ))
            }
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            return resultBitmap
        }

        private fun isWhite(pixel: Int): Boolean {
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)
            return red == 255 && green == 255 && blue == 255
        }

        fun bitmapToPath(bitmap: Bitmap): Path {
            val bitmapWidth = bitmap.width
            val bitmapHeight = bitmap.height
            val path = Path()
            for (x in 0 until bitmapWidth) {
                for (y in 0 until bitmapHeight) {
//                    val pixelColor = bitmap[x, y]
                    // Process pixel color to determine if it should be part of the path
//                    if (shouldIncludePixel(pixelColor)) {
                        // Add pixel coordinates to the path
                    path.lineTo(x.toFloat(), y.toFloat())
//                    }
                }
            }

            return path
        }

        private fun shouldIncludePixel(pixelColor: Int): Boolean {
            // Implement your logic to determine if a pixel should be included in the path
            // For example, you might check if the pixel is within a certain color range or threshold
            return pixelColor != Color.TRANSPARENT
        }

        fun roundCorner(
                context: Context?,
                bitmap: Bitmap,
                cornerDips: Int,
            ): Bitmap? {
                val output = createBitmap(bitmap.width, bitmap.height)
            val canvas = Canvas(output)
            val cornerSizePx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, cornerDips.toFloat(),
                context?.resources?.displayMetrics
            ).toInt()
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)

            // prepare canvas for transfer
            paint.isAntiAlias = true
            paint.color = -0x1
            paint.style = Paint.Style.FILL
            canvas.drawARGB(0, 0, 0, 0)
            canvas.drawRoundRect(rectF, cornerSizePx.toFloat(), cornerSizePx.toFloat(), paint)

            // draw bitmap
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }

        fun trimEdge2(bitmap: Bitmap, trimSize: Int): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val trimmedBitmap = bitmap.copy(bitmap.config!!, true)

            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pixel = bitmap[x, y]
                    if (pixel == Color.TRANSPARENT) continue
                        // 透明でないピクセルの場合、周囲のピクセルをチェック
                    var isEdge = false
                    for (dy in -trimSize..trimSize) {
                        for (dx in -trimSize..trimSize) {
                            if (
                                dx == 0
                                && dy == 0
                            ) continue
                            val nx = x + dx
                            val ny = y + dy
                            if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue
                            val neighborPixel = bitmap[nx, ny]
                            if (neighborPixel != Color.TRANSPARENT) continue
                            isEdge = true
                            break
                        }
                        if (isEdge) break
                    }
                    // 輪郭のピクセルの場合、透明にする
                    if (!isEdge) continue
                    trimmedBitmap[x, y] = Color.TRANSPARENT
                }
            }
            return trimmedBitmap
        }

        fun trimEdge(bitmap: Bitmap, trimSize: Int): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val trimmedBitmap = bitmap.copy(bitmap.config!!, true)

            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pixel = bitmap[x, y]
                    if (pixel == Color.TRANSPARENT) continue
                    // 透明でないピクセルの場合、周囲のピクセルをチェック
                    var isEdge = false
                    for (dy in -trimSize..trimSize) {
                        for (dx in -trimSize..trimSize) {
                            if (
                                dx == 0
                                && dy == 0
                            ) continue
                            val nx = x + dx
                            val ny = y + dy
                            if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue
                            val neighborPixel = bitmap[nx, ny]
                            if (neighborPixel != Color.TRANSPARENT) continue
                            isEdge = true
                            break
                        }
                        if (isEdge) break
                    }
                    // 輪郭のピクセルの場合、透明にする
                    if (!isEdge) continue
                    trimmedBitmap[x, y] = Color.TRANSPARENT
                }
            }
            return trimmedBitmap
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
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "gitErr_encodeFromByteArray.txt").absolutePath,
//                    e.toString()
//                )
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
//           FileSystems.writeFile(
//               File(UsePath.cmdclickDefaultAppDirPath, "gitErr.txt").absolutePath,
//               e.toString()
//           )
        }
    }
}

