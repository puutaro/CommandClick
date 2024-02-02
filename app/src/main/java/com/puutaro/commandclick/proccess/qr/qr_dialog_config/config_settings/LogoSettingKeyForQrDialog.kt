package com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings

object QrLogoSettingsForQrDialog {
    enum class QrLogoSettingKey(
        val key: String
    ){
        ONE_SIDE_LENGTH("oneSideLength"),
        DISABLE("disable"),
        TYPE("type"),
    }

    enum class QrDisableSettingKey(
        val key: String
    ) {
        ON("ON"),
        OFF("OFF"),
    }
}