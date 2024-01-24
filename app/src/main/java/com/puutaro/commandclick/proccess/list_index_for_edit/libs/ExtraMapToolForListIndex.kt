package com.puutaro.commandclick.proccess.list_index_for_edit.libs

import android.content.Intent
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.map.CmdClickMap

object ExtraMapToolForListIndex {

    fun createExtraMapFromClickConfigMap(
        listIndexClickConfigMap: Map<String, String>,
    ): Map<String, String>? {
        val extraKey = ListIndexEditConfig.ListIndexClickConfigMapKey.EXTRA.str
        return listIndexClickConfigMap.get(
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

    fun createExtraMapFromSettingMenu(
        listIndexArgsMaker: ListIndexArgsMaker,
        name: String,
    ):  Map<String, String>? {
        val nameKey = ListIndexEditConfig.ListIndexMenuMapKey.NAME.str
        return listIndexArgsMaker.listIndexClickMenuMapList.firstOrNull {
            it?.get(nameKey) == name
        }?.get(ListIndexEditConfig.ListIndexMenuMapKey.EXTRA.str)?.let {
            CmdClickMap.createMap(
                it,
                "!"
            )
        }?.toMap()?.filterKeys { it.isNotEmpty() }
    }

    fun createExtraMap(
        currentJsPathMacroStr: String,
        settingMenuMapList: List<Map<String, String>?>,
    ): Map<String, String>? {
        val jsPathKey = ListIndexEditConfig.ListIndexMenuMapKey.JS_PATH.str
        val currentSettingMenuMap = settingMenuMapList.filter {
            it?.get(jsPathKey) == currentJsPathMacroStr
        }.firstOrNull()
        if(
            currentSettingMenuMap.isNullOrEmpty()
        ) return null
        return currentSettingMenuMap.get(ListIndexEditConfig.ListIndexMenuMapKey.EXTRA.str)
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
        return extraMap?.get(ListIndexEditConfig.ListIndexMenuExtraKey.PARENT_DIR_PATH.str).let {
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
        val compPrefix = extraMap.get(ListIndexEditConfig.ListIndexMenuExtraKey.COMP_PREFIX.str)
        val compSuffix = extraMap.get(ListIndexEditConfig.ListIndexMenuExtraKey.COMP_SUFFIX.str)
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
        val action = extraMap.get(ListIndexEditConfig.ListIndexMenuExtraKey.BROADCAST_ACTION.str)
            ?: return null
        val schemaMapStr =
            extraMap.get(ListIndexEditConfig.ListIndexMenuExtraKey.BROADCAST_SCHEMAS.str)
        return BroadcastSender.createBroadcastIntent(
            action,
            schemaMapStr,
            "&",
        )
    }
}