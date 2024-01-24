package com.puutaro.commandclick.proccess.qr.qr_dialog_config

import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.util.CcScript

object ExtraMapToolForQrDialog {

    fun createExtraMapFromClickConfigMap(
        qrDialogClickConfigMap: Map<String, String>,
    ): Map<String, String>? {
        val extraKey = QrDialogConfig.ClickSettingKeyForQrDialog.EXTRA.key
        return qrDialogClickConfigMap.get(
            extraKey
        )?.split("!")?.map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }?.toMap()?.filterKeys {
            it.isNotEmpty()
        }
    }

    fun getParentDirPath(
        extraMap: Map<String, String>?,
        currentAppDirPath: String,
    ): String {
        return extraMap?.get(QrDialogConfig.QrDialogExtraKey.PARENT_DIR_PATH.str).let {
            if(
                it.isNullOrEmpty()
            ) return@let currentAppDirPath
            it
        }
    }
}