package com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings

object QrTypeSettingsForQrDialog {

    enum class QrTypeSettingKey(
        val type: String
    ){
        FILE_CON("fileCon"),
        GIT_CLONE("gitCone"),
    }
}