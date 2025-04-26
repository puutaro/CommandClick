package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import androidx.core.graphics.createBitmap

object ImageOverlay {

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

}