package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.graphics.BitmapFactory
import android.webkit.JavascriptInterface
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.qr.CopyFannelServer
import com.puutaro.commandclick.proccess.qr.Scanner


class JsQr(
    private val terminalFragment: TerminalFragment
) {
    val scanner = Scanner(
        terminalFragment,
        terminalFragment.currentAppDirPath
    )
    @JavascriptInterface
    fun scanFromImage(
        qrImagePath: String
    ): String {
        return scanner.scanFromImage(
            qrImagePath
        )
    }

    @JavascriptInterface
    fun launchCopyFannelServer(){
        CopyFannelServer.launch(
            terminalFragment
        )
    }

    @JavascriptInterface
    fun exitCopyFannelServer(){
        CopyFannelServer.exit(
            terminalFragment
        )
    }


}