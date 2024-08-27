package com.puutaro.commandclick.service.lib.ubuntu.libs

import android.content.Context
import com.puutaro.commandclick.common.variable.broadcast.extra.UbuntuServerIntentExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.service.UbuntuService
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.NetworkTool
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.sd.SdPath
import java.io.File

object UbuntuAutoSetupManager {

    fun manage(ubuntuService: UbuntuService) {
        val context = ubuntuService.applicationContext
        val ubuntuFiles = ubuntuService.ubuntuFiles
        if(
            ubuntuFiles?.ubuntuSetupCompFile?.isFile == true
        ) return
        if(
            !NetworkTool.isWifi(context)
        ) return
        val settingValList = File(
            UsePath.cmdclickDefaultAppDirPath,
            SystemFannel.preference
        ).let {
            CommandClickVariables.extractSettingValListByFannelName(
                ReadText(it.absolutePath).textToList(),
//                it.name,
            )
        }
        val ubuntuAutoSetupOff =
            SettingVariableSelects.UbuntuAutoSetup.OFF
        val autoSetup = getAutoSetupValue(
            settingValList
        )
        if(
            autoSetup == ubuntuAutoSetupOff
        ) return
        TriggerHandler.handle(
            context,
            autoSetup,
            settingValList,
        )


    }

    private fun getAutoSetupValue(
        settingValList: List<String>?
    ): SettingVariableSelects.UbuntuAutoSetup {
        val ubuntuAutoSetupOff =
            SettingVariableSelects.UbuntuAutoSetup.OFF
        return SettingVariableReader.getCbValue(
            settingValList,
            CommandClickScriptVariable.UBUNTU_AUTO_SETUP,
            ubuntuAutoSetupOff.name,
            String(),
            String(),
            listOf(
                SettingVariableSelects.UbuntuAutoSetup.SETUP.name,
                SettingVariableSelects.UbuntuAutoSetup.RESTORE.name,
                ubuntuAutoSetupOff.name,
            ),
        ).let {
                curAutoSetupValue ->
            SettingVariableSelects.UbuntuAutoSetup.values().firstOrNull {
                val autoSetupValue = it.name
                autoSetupValue == curAutoSetupValue
            } ?: ubuntuAutoSetupOff
        }
    }


    private object TriggerHandler {
        fun handle(
            context: Context,
            autoSetup: SettingVariableSelects.UbuntuAutoSetup,
            settingValList: List<String>?
        ) {
            when (autoSetup) {
                SettingVariableSelects.UbuntuAutoSetup.SETUP
                -> BroadcastSender.normalSend(
                    context,
                    BroadCastIntentSchemeUbuntu.START_UBUNTU_SERVICE.action,
                )

                SettingVariableSelects.UbuntuAutoSetup.RESTORE
                -> {
                    if (
                        isNotTriggerCheck(settingValList)
                    ) return
                    val extraList = if (
                        UbuntuFiles.isUbuntuRestore()
                    ) Pair(
                        UbuntuServerIntentExtra.ubuntuRestoreSign.schema,
                        "on"
                    ).let { listOf(it) }
                    else null
                    BroadcastSender.normalSend(
                        context,
                        BroadCastIntentSchemeUbuntu.START_UBUNTU_SERVICE.action,
                        extraList
                    )
                }
                else -> {}
            }
        }

        private fun isNotTriggerCheck(
            settingValList: List<String>?,
        ): Boolean {
            val onRootfsSdCardSaveSelectsOn =
                SettingVariableSelects.OnRootfsSdCardSaveSelects.ON.name
            val isRootfsSdCardSave = SettingVariableReader.getCbValue(
                settingValList,
                CommandClickScriptVariable.ON_ROOTFS_SDCARD_SAVE,
                String(),
                String(),
                String(),
                listOf(onRootfsSdCardSaveSelectsOn),
            ) == onRootfsSdCardSaveSelectsOn
            if (
                !isRootfsSdCardSave
            ) return false
            return SdPath.getSdUseRootPath().isEmpty()
        }
    }
}