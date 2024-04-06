package com.puutaro.commandclick.proccess.qr

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import coil.load
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrLogoSettingsForQrDialog
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrModeSettingKeysForQrDialog
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrTypeSettingsForQrDialog
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.map.CmdClickMap
import java.io.File

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
        val fragment: Fragment,
        val recentAppDirPath: String,
        val readSharePreffernceMap: Map<String, String>,
        val qrLogoConfigMap: Map<String, String>,
        val parentDirPath: String,
        val fileName: String,
        val fannelContentsQrLogoView: AppCompatImageView?,
        val fileContentsQrLogoLinearLayout: LinearLayoutCompat?
    )
}