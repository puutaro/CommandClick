package com.puutaro.commandclick.util.image_tools

import android.graphics.Bitmap

object ImageCut {

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

}