package com.puutaro.commandclick.util.image_tools

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import androidx.core.graphics.createBitmap
import java.util.Locale

object TextDraw {

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
            if (it <= 0) return@let 0f
            it
        }
        val transY = ((canvas.height / 2f) - ((staticLayoutForStroke.height / 2f))).let {
            if (it <= 0) return@let 0f
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
        messageFont: Typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL),
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
        val staticLayoutForMessage = when (message.isEmpty()) {
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

        let translateForTitle@{
            val titleWidth = (canvasWidth - staticLayoutForStroke.width) / 2f
            val transXForTitle =
                if (titleWidth <= 0) 0f
                else titleWidth
            val transYForTitle = let culcTransY@{
                val canvasHeight = canvas.height
                val staticLayoutForMessageHeightWithMargin = when (staticLayoutForMessage == null) {
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

        if (staticLayoutForMessage != null) {
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
            if (
                maxLinesSrc == null
                || rectHeight == null
            ) return@let null
            val maxLinesEntry =
                (rectHeight / (textPaint.getFontMetrics(null) * (spacingMulti ?: 1f))).toInt()
            when (maxLinesEntry >= 2) {
                false -> 1
                else -> maxLinesEntry
            }
        }

        //Change first character to capital letter
        val tempStr = when (isFirstCharUpper) {
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
                if (maxLines == null) return@apply
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
        val builder =
            StaticLayout.Builder.obtain(text, 0, text.length, textPaint, imageWidth.toInt())
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