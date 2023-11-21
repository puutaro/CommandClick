package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.graphics.BitmapFactory
import android.webkit.JavascriptInterface
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.puutaro.commandclick.fragment.TerminalFragment


class JsQr(
    terminalFragment: TerminalFragment
) {
    @JavascriptInterface
    fun scanFromImage(
        qrImagePath: String
    ): String {
        val bMap = BitmapFactory.decodeFile(qrImagePath)
        var contents: String? = null

        val intArray = IntArray(bMap.width * bMap.height)
//copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.width, 0, 0, bMap.width, bMap.height)

        val source: LuminanceSource =
            RGBLuminanceSource(bMap.width, bMap.height, intArray)
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        val reader = MultiFormatReader()
        val result = reader.decode(bitmap)
        return result.getText()
    }

}