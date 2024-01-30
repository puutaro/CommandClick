package com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings

import com.puutaro.commandclick.proccess.qr.QrDialogConfig

object QrModeSettingKeysForQrDialog {
    enum class QrMode(
        val mode: String,
    ){
        FANNEL_REPO("fannelRepo"),
        NORMAL("normal"),
        TSV_EDIT("tsvEdit"),
    }

    fun getQrMode(
        qrDialogConfigMap: Map<String, String>,
    ): QrMode {
        val defaultQrMode = QrMode.NORMAL
        val modeKey = QrDialogConfig.QrDialogConfigKey.MODE.key
        if(
            qrDialogConfigMap.isEmpty()
        ) return defaultQrMode
        val setModeStr = qrDialogConfigMap.get(modeKey).let {
            if(
                it.isNullOrEmpty()
            ) return@let defaultQrMode.mode
            it
        }
        return QrMode.values().firstOrNull {
            it.mode == setModeStr
        } ?: defaultQrMode
    }
}