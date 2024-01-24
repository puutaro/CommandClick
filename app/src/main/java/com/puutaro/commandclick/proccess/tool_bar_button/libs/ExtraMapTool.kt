package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.content.Intent
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonMenuExtraKey
import com.puutaro.commandclick.proccess.tool_bar_button.SettingButtonMenuMapKey
import com.puutaro.commandclick.util.CcScript

object ExtraMapTool {
    fun createExtraMap(
        currentJsPathMacroStr: String,
        settingMenuMapList: List<Map<String, String>?>,
    ): Map<String, String>? {
        val jsPathKey = SettingButtonMenuMapKey.JS_PATH.str
        val currentSettingMenuMap = settingMenuMapList.filter {
            it?.get(jsPathKey) == currentJsPathMacroStr
        }.firstOrNull()
        if(
            currentSettingMenuMap.isNullOrEmpty()
        ) return null
       return currentSettingMenuMap.get(SettingButtonMenuMapKey.EXTRA.str)
            ?.split("!")?.map {
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
        return extraMap?.get(SettingButtonMenuExtraKey.PARENT_DIR_PATH.str).let {
                if(
                    it.isNullOrEmpty()
                ) return@let currentAppDirPath
                it
            }
    }

    fun makeCompFileName(
        srcFileName: String,
        extraMap: Map<String, String>?,
    ): String {
        if(
            extraMap.isNullOrEmpty()
        ) return srcFileName
        val compPrefix = extraMap.get(SettingButtonMenuExtraKey.COMP_PREFIX.str)
        val compSuffix = extraMap.get(SettingButtonMenuExtraKey.COMP_SUFFIX.str)
        val compPrefixFileName = compPrefix.let {
            if(
                it.isNullOrEmpty()
            ) return@let srcFileName
            UsePath.compPrefix(
                srcFileName,
                it
            )
        }
        return compSuffix.let {
            if(
                it.isNullOrEmpty()
            ) return@let compPrefixFileName
            UsePath.compExtend(
                compPrefixFileName,
                it
            )
        }
    }

    fun makeBroadcastIntent(
        extraMap: Map<String, String>?
    ): Intent? {
        if (
            extraMap.isNullOrEmpty()
        ) return null
        val action = extraMap.get(SettingButtonMenuExtraKey.BROADCAST_ACTION.str)
            ?: return null
        val schemaMapStr =
            extraMap.get(SettingButtonMenuExtraKey.BROADCAST_SCHEMAS.str)
        return BroadcastSender.createBroadcastIntent(
            action,
            schemaMapStr,
            "&",
        )
    }
}