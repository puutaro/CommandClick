package com.puutaro.commandclick.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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


}

