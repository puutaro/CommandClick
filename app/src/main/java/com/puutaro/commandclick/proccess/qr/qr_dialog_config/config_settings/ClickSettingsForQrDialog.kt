package com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings

import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool

object ClickSettingsForQrDialog {
    enum class ClickSettingKey(
        val key: String
    ){
        TYPE("type"),
        EXTRA(ExtraArgsTool.extraSettingKeyName),
    }

    enum class ClickTypeValues(
        val mode: String
    ){
        FILE_CONTENTS("con"),
        EXEC_QR("execQR"),
        DESC("desc"),
        EDIT_LOGO("editLogo")
    }
}