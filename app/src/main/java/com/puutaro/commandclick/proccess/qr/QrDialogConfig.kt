package com.puutaro.commandclick.proccess.qr

import androidx.appcompat.widget.AppCompatImageView

object QrDialogConfig {

    enum class QrDialogConfigKey(
        val key: String
    ) {
        CLICK("click"),
        LONG_CLICK("longClick"),
        LOGO("logo"),
        MODE("mode"),
    }

    class QrLogoHandlerArgsMaker(
//        val fragment: Fragment,
        val recentAppDirPath: String,
        val qrLogoConfigMap: Map<String, String>,
//        val parentDirPath: String,
        val fileName: String,
        val fannelContentsQrLogoView: AppCompatImageView?,
//        val fileContentsQrLogoLinearLayout: LinearLayoutCompat?
    )
}