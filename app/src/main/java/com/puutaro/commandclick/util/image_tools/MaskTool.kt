package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.PAINT_FLAGS
import kotlin.math.max

object MaskTool {

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

}