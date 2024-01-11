package com.puutaro.commandclick.proccess.qr

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CmdClickMap
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File

object QrDialogConfig {

    enum class QrDialogConfigKey(
        val key: String
    ) {
        ON_FILE_CON("onFileCon"),
        ON_EXEC_QR_IN_LOGO_CLICK("onExecQrInLogoClick"),
    }

    fun makeDialogConfigMap(
        readSharePreffernceMap: Map<String, String>
    ): Map<String, String> {
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptName = CcPathTool.getCurrentScriptFileName(
            readSharePreffernceMap
        )
        val qrDialogConfigPath = ScriptPreWordReplacer.replace(
            UsePath.qrDialogConfigPath,
            currentAppDirPath,
            currentScriptName
        )
        val qrDialogConfigPathObj = File(qrDialogConfigPath)
        return when(qrDialogConfigPathObj.isFile){
            true -> {
                val parentDirPath = qrDialogConfigPathObj.parent
                    ?: return emptyMap()
                ReadText(
                    parentDirPath,
                    qrDialogConfigPathObj.name
                ).readText().let {
                    CmdClickMap.createMap(it, ",")
                }.toMap().filterKeys { it.isNotEmpty() }
            }
            else -> mapOf()
        }
    }

}