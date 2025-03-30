package com.puutaro.commandclick.util.image_tools

import android.R
import android.app.Activity
import android.content.Context
import android.content.res.Resources
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
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.util.Base64
import android.util.TypedValue
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.PAINT_FLAGS
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.Arrays
import java.util.Locale
import kotlin.math.max
import kotlin.random.Random
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt
import androidx.core.graphics.scale
import androidx.core.graphics.get
import androidx.core.graphics.set
import androidx.core.graphics.drawable.toDrawable
import kotlin.math.sqrt


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
//        if (c.width > s.width) {
//            width = c.width + s.width
//            height = c.height
//        } else {
//            width = s.width + s.width
//            height = c.height
//        }
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

    object DrawText {

        fun drawTextToBitmap(
            text: String,
            imageWidth: Float,
            imageHeight: Float,
            bkColor: Int?,
            fontSizeSrc: Float?,
            fillColorInt: Int?,
            strokeColorInt: Int?,
            strokeSize: Float?,
            firstCharRate: Float?,
            letterSpacing: Float?,
            innerWidthRate: Float = 1f,
            font: Typeface = Typeface.DEFAULT,
            isAntiAlias: Boolean = false,
        ): Bitmap {
//            val imgWidth = 200f     // 画像幅
//            val imgHeight = 200f    // 画像高さ
//            val rectWidth = imageWidth //- 10f    // 矩形幅
//            val rectHeight = imageHeight - 10f   // 矩形高さ
//            val x = (imageWidth - rectWidth) / 2      // 矩形左上x座標
//            val y = (imageHeight - rectHeight) / 2    // 矩形左上y座標

            val bmp = createBitmap(imageWidth.toInt(), imageHeight.toInt())
            val canvas = Canvas(bmp)
            val paint = Paint()

            // 全体を塗りつぶし
            paint.color = bkColor ?: Color.TRANSPARENT
            paint.style = Paint.Style.FILL

            canvas.drawRect(
                0f,
                0f,
                imageWidth,
                imageHeight,
                paint
            )

//            // 矩形描画
//            paint.color = Color.TRANSPARENT
//            paint.style = Paint.Style.STROKE
//            canvas.drawRect(x, y, x + rectWidth, y + rectHeight, paint)
            val spacingMilti = 0.85f
//            val spacingMilti = (
//                    ((fontSize ?: 80f) * spacingMiltiBase) / 80f
//                    ).let {
//                        spacintMultiSrc ->
//                        val baseMulti = 0.75f
//                        if(
//                            spacintMultiSrc > baseMulti
//                            ) return@let spacintMultiSrc
//                    baseMulti
//                }
            val fontSize = fontSizeSrc ?: 30f
            val staticLayoutForStroke = makeStaticsLayout(
                text,
                imageWidth * innerWidthRate,
                fontSize,
                strokeSize ?: 8f,
                strokeColorInt ?: Color.WHITE,
                Paint.Style.STROKE,
                spacingMilti,
                firstCharRate,
                letterSpacing,
                font = font,
                isAntiAlias = isAntiAlias,
            )
            val transX = ((canvas.width / 2f) - (staticLayoutForStroke.width / 2f)).let {
                if(it <= 0) return@let 0f
                it
            }
            val transY =  ((canvas.height / 2f) - ((staticLayoutForStroke.height / 2f))).let {
                if(it <= 0) return@let 0f
                it
            }
            canvas.translate(
                transX,
                transY,
            )
            staticLayoutForStroke.draw(canvas)


            val staticLayout = makeStaticsLayout(
                text,
                imageWidth * innerWidthRate,
                fontSize,
                0f,
                fillColorInt,
                Paint.Style.FILL,
                spacingMilti,
                firstCharRate,
                letterSpacing,
                font = font,
                isAntiAlias = isAntiAlias,
            )
//                builder.build()
//            canvas.translate(x, y)
//            canvas.translate(
//                0f,
//                0f,
//            )
//            canvas.translate(
//                ((canvas.width / 2) - (staticLayout.width / 2)).toFloat(),
//                ((canvas.height / 2) - ((staticLayout.height / 2))).toFloat(),
//            )
            staticLayout.draw(canvas)

            return bmp
        }

        fun drawTextToBitmapWithMessage(
            title: String,
            message: String,
            imageWidth: Float,
            imageHeight: Float,
            bkColor: Int?,
            fontSizeSrc: Float?,
            fillColorInt: Int?,
            strokeColorInt: Int?,
            titleStrokeSize: Float?,
            messageStrokeSize: Float?,
            firstCharRate: Float = 1.5f,
            titleLetterSpacing: Float? = null,
            messageLetterSpacing: Float? = null,
            titleSpacingMulti: Float = 1f,
            messageSpacingMulti: Float = 1f,
            innerWidthRate: Float = 1f,
            titleFont: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD),
            messageFont: Typeface =  Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),
            messageMarginTop: Float = 100f,
            messageWidthRate: Float = 0.8f,
            isAntiAlias: Boolean = false,
            maxLines: Int? = null,
        ): Bitmap {
            val bmp = createBitmap(imageWidth.toInt(), imageHeight.toInt())
            val canvas = Canvas(bmp)
            val paint = Paint()

            // 全体を塗りつぶし
            paint.color = bkColor ?: Color.TRANSPARENT
            paint.style = Paint.Style.FILL

            canvas.drawRect(
                0f,
                0f,
                imageWidth,
                imageHeight,
                paint
            )
            val fontSize = fontSizeSrc ?: 30f
            val staticLayoutForStroke = makeStaticsLayout(
                title,
                imageWidth * innerWidthRate,
                fontSize,
                titleStrokeSize ?: 8f,
                strokeColorInt ?: Color.WHITE,
                Paint.Style.STROKE,
                titleSpacingMulti,
                firstCharRate,
                titleLetterSpacing,
                font = titleFont,
                isAntiAlias = isAntiAlias,
            )

            val staticLayout = makeStaticsLayout(
                title,
                imageWidth * innerWidthRate,
                fontSize,
                0f,
                fillColorInt,
                Paint.Style.FILL,
                titleSpacingMulti,
                firstCharRate,
                titleLetterSpacing,
                font = titleFont,
                isAntiAlias = isAntiAlias
            )


            val messageRate = 0.6f
            val staticLayoutForMessage = when(message.isEmpty()) {
                true -> null
                else -> {
                    makeStaticsLayout(
                        message,
                        imageWidth * innerWidthRate * messageWidthRate,
                        fontSize * messageRate,
                        messageStrokeSize ?: 8f,
                        fillColorInt ?: Color.WHITE,
                        Paint.Style.FILL,
                        messageSpacingMulti,
                        1f,
                        messageLetterSpacing ?: 0f,
                        isFirstCharUpper = false,
                        font = messageFont,
                        isAntiAlias = isAntiAlias,
                        maxLines,
                        (canvas.height - staticLayoutForStroke.height - messageMarginTop - 10).toInt()
                    )
                }
            }
            val canvasWidth = canvas.width
            val staticLayoutForStrokeHeight = staticLayoutForStroke.height

            let translateForTitle@ {
                val titleWidth = (canvasWidth - staticLayoutForStroke.width) / 2f
                val transXForTitle =
                    if (titleWidth <= 0)  0f
                    else titleWidth
                val transYForTitle = let culcTransY@ {
                    val canvasHeight = canvas.height
                    val staticLayoutForMessageHeightWithMargin = when(staticLayoutForMessage == null) {
                        true -> 0f
                        else -> staticLayoutForMessage.height + messageMarginTop
                    }
                    val titlePlusMessageHeight =
                        staticLayoutForStrokeHeight + staticLayoutForMessageHeightWithMargin
                    val drawHeight = (canvasHeight - titlePlusMessageHeight) / 2
                    if (drawHeight <= 0) return@culcTransY 0f
                    drawHeight
                }
                canvas.translate(
                    transXForTitle,
                    transYForTitle,
                )
            }
            staticLayoutForStroke.draw(canvas)
            staticLayout.draw(canvas)

            if(staticLayoutForMessage != null) {
                let translateForMessage@{
                    val messageWidth =
                        (canvasWidth - staticLayoutForMessage.width) / 2f
                    val transXForMessage =
                        if (messageWidth <= 0) 0f
                        else messageWidth
                    val messageHeight = staticLayoutForStrokeHeight + messageMarginTop
                    val transYForMessage =
                        if (messageHeight <= 0) 0f
                        else messageHeight
                    canvas.translate(
                        transXForMessage,
                        transYForMessage
                    )
//                    val bgPaint = Paint().apply {
//                        color = Color.parseColor("#0fffffff") // 半透明の緑色
//                    }
//                    canvas.drawRect(
//                        0f,
//                        0f,
//                        staticLayoutForMessage.width.toFloat(),
//                        staticLayoutForMessage.height.toFloat(),
//                        bgPaint
//                    )
                }
                staticLayoutForMessage.draw(canvas)
            }
            return bmp
        }

        private fun makeStaticsLayout(
            text: String,
            imageWidth: Float,
            fontSize: Float,
            strokeSize: Float?,
            fillColorInt: Int?,
            paintStyle: Paint.Style,
            spacingMulti: Float?,
            firstCharRate: Float?,
            letterSpacing: Float?,
            isFirstCharUpper: Boolean? = true,
            font: Typeface,
            isAntiAlias: Boolean = false,
            maxLinesSrc: Int? = null,
            rectHeight: Int? = null,
        ): StaticLayout {
            // 文字列描画
            val textPaint = TextPaint()
            textPaint.color = fillColorInt ?: Color.BLACK
            textPaint.style = paintStyle
            textPaint.strokeWidth = strokeSize ?: 2f
            textPaint.textSize = fontSize
            letterSpacing?.let {
                textPaint.letterSpacing = it
            }
//            textPaint.setLea(lineSpacingMultiplier * paint.getFontSpacing());
//            textPaint.textAlign = Paint.Align.CENTER
            textPaint.setTypeface(font)
            textPaint.isAntiAlias = isAntiAlias

            val alignment = Layout.Alignment.ALIGN_CENTER
//            val spacingAdd = 4f
//            val spacingMulti = 1.1f
            val maxLines = let {
                if(
                    maxLinesSrc == null
                    || rectHeight == null
                    ) return@let null
                val maxLinesEntry = (rectHeight / (textPaint.getFontMetrics(null) * (spacingMulti ?: 1f))).toInt()
                when(maxLinesEntry >= 2){
                    false -> 1
                    else -> maxLinesEntry
                }
            }

            //Change first character to capital letter
            val tempStr = when(isFirstCharUpper) {
                false -> text
                else -> text.first().uppercase(Locale.getDefault()) + text.substring(1)
            }


//Change font size of the first character. You can change 2f as you want
            val spannableString = SpannableString(tempStr)
            spannableString.setSpan(
                RelativeSizeSpan(firstCharRate ?: 1f),
                0,
                1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val builder = StaticLayout.Builder.obtain(
                spannableString,
                0,
                text.length,
                textPaint,
                imageWidth.toInt()
            )
                .setAlignment(alignment)
//                .setMaxLines(2)
                .setLineSpacing(0f, spacingMulti ?: 0.5f)
                .setEllipsize(TextUtils.TruncateAt.END).apply {
                    if(maxLines == null) return@apply
                    setMaxLines(maxLines)
                }
            return builder.build()
        }

        fun drawTextToBitmapByRandom(
            text: String,
            imageWidth: Float,
            imageHeight: Float,
            fontSize: Float?,
            fillColorInt: Int?,
//            strokeColorInt: Int?,
        ): Bitmap {
//            val imgWidth = 200f     // 画像幅
//            val imgHeight = 200f    // 画像高さ
//            val rectWidth = imageWidth //- 10f    // 矩形幅
//            val rectHeight = imageHeight - 10f   // 矩形高さ
//            val x = (imageWidth - rectWidth) / 2      // 矩形左上x座標
//            val y = (imageHeight - rectHeight) / 2    // 矩形左上y座標

            val bmp = createBitmap(imageWidth.toInt(), imageHeight.toInt())
            val canvas = Canvas(bmp)
            val paint = Paint()

            // 全体を塗りつぶし
            paint.color = Color.TRANSPARENT
            paint.style = Paint.Style.FILL
            val randomWidthOffset = (1..20).random().toFloat()
            val randomHeightOffset = (1..20).random().toFloat()

            canvas.drawRect(
                -200 + randomWidthOffset,
                -200 + randomHeightOffset,
                imageWidth + randomWidthOffset,
                imageHeight + randomHeightOffset, paint
            )


            // 文字列描画
            val textPaint = TextPaint()
            textPaint.color = fillColorInt ?: Color.BLACK
            textPaint.strokeWidth = 1f
            textPaint.style = Paint.Style.FILL
            textPaint.textSize = fontSize ?: 30f
//            textPaint.setLea(lineSpacingMultiplier * paint.getFontSpacing());
//            textPaint.textAlign = Paint.Align.CENTER
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
//            textPaint.isAntiAlias = true

            val alignment = Layout.Alignment.ALIGN_CENTER
//            val spacingAdd = 4f
//            val spacingMulti = 1.1f
//            val maxLines = (rectHeight / (textPaint.getFontMetrics(null) * spacingMulti + spacingAdd)).toInt()

            val staticLayout: StaticLayout
            val builder = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, imageWidth.toInt())
                .setAlignment(alignment)
//                .setMaxLines(2)
                .setLineSpacing(0f, 0.5f)
                .setEllipsize(TextUtils.TruncateAt.END)
            staticLayout = builder.build()
//            canvas.translate(x, y)
            canvas.translate(
                ((canvas.width / 2) - (staticLayout.width / 2)).toFloat(),
                ((canvas.height / 2) - ((staticLayout.height / 2))).toFloat(),
            )
            staticLayout.draw(canvas)

            return bmp
        }

        fun drawTextToBitmapOld(
            mContext: Context,
            mText: String
        ): Bitmap? {
            try {
                val resources: Resources = mContext.resources
                val scale = resources.displayMetrics.density
//                val bitmapSrc = BitmapFactory.decodeResource(resources, resourceId)
//                val bitmapConfig = bitmapSrc.config
                // set default bitmap config if none
//            if (bitmapConfig == null) {
//                bitmapConfig = Bitmap.Config.ARGB_8888
//            }
                // resource bitmaps are imutable,
                // so we need to convert it to mutable one
//                val bitmap = bitmapSrc.copy(bitmapConfig, true)

                // new antialised Paint
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                // text color - #3D3D3D
                paint.color = Color.rgb(110, 110, 110)
                // text size in pixels
                paint.textSize = 12 * scale
                // text shadow
                paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
                // draw text to the Canvas center
                val bounds: Rect = Rect()
                paint.getTextBounds(mText, 0, mText.length, bounds)

                val transparantBitmap: Bitmap =
                    createBitmap(bounds.width() + 10, bounds.height() + 10)
                val canvas = Canvas(transparantBitmap)

                val x: Int = (transparantBitmap.width - bounds.width()) / 6
                val y: Int = (transparantBitmap.height + bounds.height()) / 5

                canvas.drawText(mText, x * scale, y * scale, paint)

                return transparantBitmap
            } catch (e: java.lang.Exception) {
                // TODO: handle exception

                return null
            }
        }

        fun drawTextOnBitmap(
            mContext: Context,
            resourceId: Int,
            mText: String
        ): Bitmap? {
            try {
                val resources: Resources = mContext.resources
                val scale = resources.displayMetrics.density
                val bitmapSrc = BitmapFactory.decodeResource(resources, resourceId)
                val bitmapConfig = bitmapSrc.config
                    ?: return null
                // set default bitmap config if none
//            if (bitmapConfig == null) {
//                bitmapConfig = Bitmap.Config.ARGB_8888
//            }
                // resource bitmaps are imutable,
                // so we need to convert it to mutable one
                val bitmap = bitmapSrc.copy(bitmapConfig, true)

                val canvas = Canvas(bitmap)
                // new antialised Paint
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                // text color - #3D3D3D
                paint.color = Color.rgb(110, 110, 110)
                // text size in pixels
                paint.textSize = 12 * scale
                // text shadow
                paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY)

                // draw text to the Canvas center
                val bounds: Rect = Rect()
                paint.getTextBounds(mText, 0, mText.length, bounds)
                val x: Int = (bitmap.width - bounds.width()) / 6
                val y: Int = (bitmap.height + bounds.height()) / 5

                canvas.drawText(mText, x * scale, y * scale, paint)

                return bitmap
            } catch (e: java.lang.Exception) {
                // TODO: handle exception

                return null
            }
        }
    }

    object AlphaManager {

        fun fadeBitmapLeftToRight(
            bitmap: Bitmap,
            alphaIncline: Float,
            alphaOffset: Float,
        ): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val transColor = Color.TRANSPARENT
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val index = y * width + x
                    val color = pixels[index]
                    val curAlpha = Color.alpha(color)
//                    val alphaSrc = ((x.toFloat() / width) * 255).toInt()
                    val alpha =
                        when(color == transColor) {
                            true -> 0
                            else -> (alphaIncline * x.toFloat() + (curAlpha + alphaOffset)).let {
                                if (it < 0f) return@let 0f
                                if (it > 255f) return@let 255f
                                it
                            }.toInt()
                        }
                    val red = Color.red(color)
                    val green = Color.green(color)
                    val blue = Color.blue(color)

//                    if(height/ 2 == y) {
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "lalpha.txt").absolutePath,
//                            listOf(
////                                "alphaSrc: ${alphaSrc}",
//                                "alpha: ${alpha}",
//                                "curAlpha: ${curAlpha}",
//                            ).joinToString("\n")
//                        )
//                    }
                    pixels[index] = Color.argb(
//                        255,
                        alpha,
//                        curAlpha,
                        red, green, blue)
                }
            }
            val fadedBitmap = createBitmap(width, height)
            fadedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return fadedBitmap
        }
        fun overrideLeftToRight(
            bitmap: Bitmap,
            alphaIncline: Float,
            alphaOffset: Float,
        ): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val transColor = Color.TRANSPARENT
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val index = y * width + x
                    val color = pixels[index]
                    val alphaSrc = ((x.toFloat() / width) * 255).toInt()
                    val alpha =
                        when(color == transColor) {
                            true -> 0
                            else -> (alphaIncline * x.toFloat() + (alphaSrc + alphaOffset)).let {
                                if(it < 0f) return@let 0f
                                if(it > 255f) return@let 255f
                                it
                            }.toInt()
                        }
                    (alphaIncline * x.toFloat() + (alphaSrc + alphaOffset)).let {
                        if(it < 0f) return@let 0f
                        if(it > 255f) return@let 255f
                        it
                    }.toInt()
//                    val curAlpha = Color.alpha(color)
                    val red = Color.red(color)
                    val green = Color.green(color)
                    val blue = Color.blue(color)
                    pixels[index] = Color.argb(
                        alpha,
//                        curAlpha,
                        red, green, blue)
                }
            }
            val fadedBitmap = createBitmap(width, height)
            fadedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return fadedBitmap
        }

        fun fadeBitmapFromCenter(
            bitmap: Bitmap,
            centerX: Int,
            centerY: Int,
            alphaIncline: Float,
            alphaOffset: Float,
        ): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val transColor = Color.TRANSPARENT
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val index = y * width + x
                    val color = pixels[index]
                    val curAlpha = Color.alpha(color)
                    val distance = sqrt(
                        ((x - centerX) * (x - centerX) +
                                (y - centerY) * (y - centerY)).toDouble()
                    ).toFloat()
                    val alpha =
                        when(color == transColor) {
                            true -> 0
                            else -> (alphaIncline * distance + (curAlpha + alphaOffset)).let {
                                if (it < 0f) return@let 0f
                                if (it > 255) return@let 255
                                it
                            }.toInt()
                        }
                    val red = Color.red(color)
                    val green = Color.green(color)
                    val blue = Color.blue(color)
                    pixels[index] = Color.argb(alpha, red, green, blue)
                }
            }

            val fadedBitmap = createBitmap(width, height)
            fadedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return fadedBitmap
        }

        fun overrideFromCenter(
            bitmap: Bitmap,
            centerX: Int,
            centerY: Int,
            alphaIncline: Float,
            alphaOffset: Float,
        ): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

            val maxDistance = sqrt(
                (centerX * centerX + centerY * centerY).toDouble()
            ).toFloat()
            val transColor = Color.TRANSPARENT
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val index = y * width + x
                    val color = pixels[index]
                    val distance = sqrt(
                        ((x - centerX) * (x - centerX) +
                                (y - centerY) * (y - centerY)).toDouble()
                    ).toFloat()
                    val alphaSrc = (distance / maxDistance * 255).toInt().coerceIn(0, 255)
                    val alpha =
                        when(color == transColor) {
                            true -> 0
                            else -> (alphaIncline * distance + (alphaSrc + alphaOffset)).let {
                                if (it < 0f) return@let 0f
                                if (it > 255) return@let 255
                                it

                            }.toInt()
                        }
                    val red = Color.red(color)
                    val green = Color.green(color)
                    val blue = Color.blue(color)
                    pixels[index] = Color.argb(alpha, red, green, blue)
                }
            }

            val fadedBitmap = createBitmap(width, height)
            fadedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return fadedBitmap
        }

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

        fun maskImageByTransparent(bitmap: Bitmap, maskBitmap: Bitmap): Bitmap {
            val resultBitmap = createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(resultBitmap)

            val paint = Paint()
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

            canvas.drawBitmap(bitmap, 0f, 0f, null)
            canvas.drawBitmap(maskBitmap, 0f, 0f, paint)

            paint.xfermode = null
            return resultBitmap
        }

        suspend fun maskImageByTransparentForEqualEvenRect(
            bitmap: Bitmap,
            maskBitmap: Bitmap,
            oneSide: Int,
        ): Bitmap? {
            val divideTimes = bitmap.width / oneSide
            val divideHeight = bitmap.width / divideTimes
            val divideWidth = bitmap.height / divideTimes
            val bitmapList = (0..< divideTimes).map {
                yOrder ->
                val yOffset = yOrder * divideWidth
                (0..<divideTimes).map {
                    xOrder ->
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lmaskImageByTrans.txt").absolutePath,
//                        listOf(
//                            "divideWidth: ${divideWidth}",
//                            "divideHeight: ${divideHeight}",
//                            "xOrder * divideWidth: ${xOrder * divideWidth}",
//                            "yOffset: ${yOffset}",
//                        ).joinToString("\n")
//                    )
                    cutByTarget(
                        bitmap,
                        divideWidth,
                        divideHeight,
                        xOrder * divideWidth,
                        yOffset

                    )
                }
            }.flatten()
            val maskBitmapList = (0..<divideTimes).map {
                    yOrder ->
                val yOffset = yOrder * divideWidth
                (0..<divideTimes).map {
                        xOrder ->
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lmaskImageByTransMask.txt").absolutePath,
//                        listOf(
//                            "divideWidth: ${divideWidth}",
//                            "divideHeight: ${divideHeight}",
//                            "xOrder * divideWidth: ${xOrder * divideWidth}",
//                            "yOffset: ${yOffset}",
//                        ).joinToString("\n")
//                    )
                    cutByTarget(
                        maskBitmap,
                        divideWidth,
                        divideHeight,
                        xOrder * divideWidth,
                        yOffset

                    )
                }
            }.flatten()
            val concurrencyLimitForMakeTextBitmap = 10
            val semaphore = Semaphore(concurrencyLimitForMakeTextBitmap)
            val bitmapListSize = bitmapList.size
            val channel = Channel<Pair<Int, Bitmap?>>(bitmapListSize)
            val indexToBitmapList: ArrayList<Pair<Int, Bitmap?>> = arrayListOf()
            val maskedBitmapList = withContext(Dispatchers.IO) {
                val jobList = bitmapList.mapIndexed {
                    index, bkPartBitmap ->
                    async {
                        semaphore.withPermit {
                            val maskPartBitmap = maskBitmapList[index]
                            val maskedBitmap = maskImageByTransparent(
                                bkPartBitmap,
                                maskPartBitmap,
                            )
                            channel.send(Pair(index, maskedBitmap))
                        }
                    }
                }
                jobList.forEach { it.await() }
                channel.close()
                for(indexToBitmap in channel){
                    indexToBitmapList.add(indexToBitmap)
                }
                indexToBitmapList.sortBy { it.first }
                indexToBitmapList.map {
                    it.second
                }
            }
            val maskedBitmapListLastIndex = maskedBitmapList.lastIndex
            var totalBitmap: Bitmap? = null
            var horizonBitmap: Bitmap? = null
            for(i in 0..< maskedBitmapListLastIndex){
                val maskedBitmapPart = maskedBitmapList[i]
                horizonBitmap = when(horizonBitmap == null){
                    true -> maskedBitmapPart
                    else -> concatByHorizon(
                        horizonBitmap,
                        maskedBitmapPart as Bitmap,
                        0
                        )
                }
                if(
                    (i + 1) % divideTimes != 0
                    ) continue
                val verticalBitmap = rotate(
                    horizonBitmap as Bitmap,
                    90f
                )
                totalBitmap = when(totalBitmap == null){
                    true -> verticalBitmap
                    else -> concatByHorizon(
                        horizonBitmap,
                        maskedBitmapPart as Bitmap,
                        0
                        )
                }
            }
            if(
                totalBitmap == null
            ) return null
            return rotate(
                totalBitmap,
                -90f
            )
        }

        fun mask(
            bkBitmap: Bitmap,
            mascSrcBitmap: Bitmap,
        ): Bitmap {
            val output = createBitmap(mascSrcBitmap.width, mascSrcBitmap.height)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
            val canvas = Canvas(output)
            canvas.drawBitmap(bkBitmap, 0f, 0f, null)
//        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.color = -0x1000000
            canvas.drawBitmap(mascSrcBitmap, 0f, 0f, paint)
            return output
        }
//        fun mustByBlack(baseBitmap: Bitmap, cutoutBitmap: Bitmap): Bitmap {
//            // baseBitmapと同じサイズのARGB_8888形式のmutableなBitmapを作成
//            val resultBitmap = createBitmap(baseBitmap.width, baseBitmap.height)

//            // 新しいBitmapのCanvasを作成
//            val canvas = Canvas(resultBitmap)

//            // CanvasにbaseBitmapを描画
//            canvas.drawBitmap(baseBitmap, 0f, 0f, null)

//            // cutoutBitmapを描画するためのPaintを設定
//            val paint = Paint()
//            // PorterDuffのXfermodeを設定。ここでは、DST_OUTモードを使用する。
//            // DST_OUTモードは、「描画先の画像 (この場合 resultBitmap) の、描画元の画像 (cutoutBitmap) と重ならない部分のみを残す」モード
//            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

//            // cutoutBitmapを描画。これにより、baseBitmapの黒い部分が切り抜かれる。
//            canvas.drawBitmap(cutoutBitmap, 0f, 0f, paint)

//            // Xfermodeをクリア
//            paint.xfermode = null

//            return resultBitmap
//        }

        fun crop(
            bkBitmap: Bitmap?,
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int,
            out: Boolean
        ): Bitmap {
            bkBitmap ?: return toTransform

            val SRC_OUT_PAINT = Paint(PAINT_FLAGS).apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT) }
            val SRC_IN_PAINT = Paint(PAINT_FLAGS).apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN) }
            val srcWidth = toTransform.width
            val srcHeight = toTransform.height
            val scaleX = outWidth / srcWidth.toFloat()
            val scaleY = outHeight / srcHeight.toFloat()
            val maxScale = max(scaleX, scaleY)

            val scaledWidth = maxScale * srcWidth
            val scaledHeight = maxScale * srcHeight
            val left = (outWidth - scaledWidth) / 2f
            val top = (outHeight - scaledHeight) / 2f
            val destRect = RectF(left, top, left + scaledWidth, top + scaledHeight)

            val bitmap = createBitmap(outWidth, outHeight)
//            pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = if (out) SRC_OUT_PAINT else SRC_IN_PAINT
            canvas.drawBitmap(bkBitmap, 0f, 0f, null)
//            drawable.bounds = Rect(0, 0, outWidth, outHeight)
//            drawable.draw(canvas)
            canvas.drawBitmap(toTransform, null, destRect, paint)
            return bitmap
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

        fun cutByTarget(
            bitmap: Bitmap,
            limitWidthPx: Int,
            limitHeightPx: Int,
            offsetX: Int,
            offsetY: Int,
        ): Bitmap {
            // Set some constants

// Crop bitmap
            return Bitmap.createBitmap(
                bitmap,
                offsetX,
                offsetY,
                limitWidthPx,
                limitHeightPx,
                null,
                false,
                )
        }

        fun cutCenter(
            bitmap: Bitmap,
            limitWidthPx: Int,
            limitHeightPx: Int,
        ): Bitmap {
            // Set some constants
            val srcWidth = bitmap.width
            val srcHeight = bitmap.height
            val startX = (srcWidth - limitWidthPx) / 2
            val startY = (srcHeight - limitHeightPx) / 2

// Crop bitmap
            return Bitmap.createBitmap(bitmap, startX, startY, limitWidthPx, limitHeightPx, null, false)
        }

        fun cutRnd(
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
            return Bitmap.createBitmap(
                bitmap,
                startX,
                startY,
                limitWidthPx,
                limitHeightPx,
                null,
                false
            )
        }

        fun overlayBitmap(bitmapBackground: Bitmap, bitmapImage: Bitmap): Bitmap {
            val bitmap2Width = bitmapImage.width
            val bitmap2Height = bitmapImage.height
            val marginLeft = (bitmapBackground.width * 0.5 - bitmap2Width * 0.5).toFloat()
            val marginTop = (bitmapBackground.height * 0.5 - bitmap2Height * 0.5).toFloat()
            val bkBitmapConfig = bitmapBackground.config as Bitmap.Config
            val overlayBitmap =
                createBitmap(bitmap2Width, bitmap2Height, bkBitmapConfig)
            val canvas = Canvas(overlayBitmap)
            canvas.drawBitmap(bitmapBackground, Matrix(), null)
            canvas.drawBitmap(bitmapImage, marginLeft, marginTop, null)
            return overlayBitmap
        }

        fun overlayOnBkBitmap(bitmapBackground: Bitmap, bitmapImage: Bitmap): Bitmap {
            val bitmapWidth = bitmapBackground.width
            val bitmapHeight = bitmapBackground.height
            val bitmap2Width = bitmapImage.width
            val bitmap2Height = bitmapImage.height
            val marginLeft = (0..(bitmapBackground.width - bitmap2Width)).random().toFloat()
            val marginTop = (0..(bitmapBackground.height - bitmap2Height)).random().toFloat()
            val bkBitmapConfig = bitmapBackground.config as Bitmap.Config
            val overlayBitmap =
                createBitmap(bitmapWidth, bitmapHeight, bkBitmapConfig)
            val canvas = Canvas(overlayBitmap)
            canvas.drawBitmap(bitmapBackground, Matrix(), null)
            canvas.drawBitmap(bitmapImage, marginLeft, marginTop, null)
            return overlayBitmap
        }

        fun overlayOnBkBitmapCenter(bitmapBackground: Bitmap, bitmapImage: Bitmap): Bitmap {
            val bitmapWidth = bitmapBackground.width
            val bitmapHeight = bitmapBackground.height
            val bitmap2Width = bitmapImage.width
            val bitmap2Height = bitmapImage.height
            val marginLeft = (bitmapBackground.width - bitmap2Width) / 2f
            val marginTop =(bitmapBackground.height - bitmap2Height) / 2f
            val bkBitmapConfig = bitmapBackground.config as Bitmap.Config
            val overlayBitmap =
                createBitmap(bitmapWidth, bitmapHeight, bkBitmapConfig)
            val canvas = Canvas(overlayBitmap)
            canvas.drawBitmap(bitmapBackground, Matrix(), null)
            canvas.drawBitmap(bitmapImage, marginLeft, marginTop, null)
            return overlayBitmap
        }

        fun overlayOnBkBitmapByPivot(
            bitmapBackground: Bitmap,
            bitmapImage: Bitmap,
            pivotX: Float,
            pivotY: Float,
        ): Bitmap {
            val bitmapWidth = bitmapBackground.width
            val bitmapHeight = bitmapBackground.height
//            val bitmap2Width = bitmapImage.width
//            val bitmap2Height = bitmapImage.height
            val bkBitmapConfig = bitmapBackground.config as Bitmap.Config
            val overlayBitmap =
                createBitmap(bitmapWidth, bitmapHeight, bkBitmapConfig)
            val canvas = Canvas(overlayBitmap)
            canvas.drawBitmap(bitmapBackground, Matrix(), null)
            canvas.drawBitmap(bitmapImage, pivotX, pivotY, null)
            return overlayBitmap
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

