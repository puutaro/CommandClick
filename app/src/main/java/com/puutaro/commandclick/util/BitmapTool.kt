package com.puutaro.commandclick.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import android.view.View
import com.puutaro.commandclick.fragment.TerminalFragment
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
        terminalFragment: TerminalFragment,
        imagePath: String,
    ): Bitmap {
        val beforeResizeBitMap = BitmapFactory.decodeFile(imagePath)
        val baseWidth = ScreenSizeCalculator.dpWidth(terminalFragment)
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

    fun getScreenShotFromView(v: View): Bitmap? {
        // create a bitmap object
        var screenshot: Bitmap? = null
        try {
            screenshot = Bitmap.createBitmap(
                v.measuredWidth,
                v.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            // Now draw this bitmap on a canvas
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        } catch (e: Exception) {
            Log.e("GFG", "Failed to capture screenshot because:" + e.message)
        }
        // return the bitmap
        return screenshot
    }



}

