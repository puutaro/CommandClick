package com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings

object ClickSettingsForQrDialog {
    fun makeClickConfigListStr(
        qrDialogConfigMap: Map<String, String>,
        clickKeyName: String,
    ): String? {
        return qrDialogConfigMap.get(clickKeyName)
    }

    fun howEnableClick(
        clickKey: String,
        qrDialogConfigMap: Map<String, String>,
    ): Boolean {
        return qrDialogConfigMap.get(clickKey).let {
            if(it == null) return@let true
            if(
                it.isEmpty()
            ) return@let false
            true
        }
    }
}