package com.puutaro.commandclick.proccess.extra_args

import android.content.Intent
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.map.CmdClickMap

object ExtraArgsTool {

    val extraSettingKeyName = "extra"
    enum class ExtraKey(
        val key: String
    ) {
        PARENT_DIR_PATH("parentDirPath"),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
        BROADCAST_ACTION("broadcastAction"),
        BROADCAST_SCHEMAS("broadcastSchemas"),
    }

    fun createExtraMapFromMenuMapList(
        menuMapList: List<Map<String, String>?>,
        filterValue: String,
        targetKey: String,
        separator: String,
    ): Map<String, String>? {
        val currentSettingMenuMap = menuMapList.firstOrNull {
            it?.get(targetKey) == filterValue
        }
        if(
            currentSettingMenuMap.isNullOrEmpty()
        ) return null
        return CmdClickMap.createMap(
                currentSettingMenuMap.get(extraSettingKeyName),
                separator,
            ).toMap().filterKeys {
            it.isNotEmpty()
        }
    }

    fun createExtraMapFromMap(
        srcMap: Map<String, String>,
        separator: String,
    ): Map<String, String> {
        return CmdClickMap.createMap(
            srcMap.get(extraSettingKeyName),
            separator,
        ).toMap().filterKeys {
            it.isNotEmpty()
        }
    }

    fun getParentDirPath(
        extraMap: Map<String, String>?,
        currentAppDirPath: String,
    ): String {
        return extraMap?.get(ExtraKey.PARENT_DIR_PATH.key).let {
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
        val compPrefix = extraMap.get(ExtraKey.COMP_PREFIX.key)
        val compSuffix = extraMap.get(ExtraKey.COMP_SUFFIX.key)
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
        extraMap: Map<String, String>?,
        separator: String,
    ): Intent? {
        if (
            extraMap.isNullOrEmpty()
        ) return null
        val action = extraMap.get(ExtraKey.BROADCAST_ACTION.key)
            ?: return null
        val schemaMapStr =
            extraMap.get(ExtraKey.BROADCAST_SCHEMAS.key)
        return BroadcastSender.createBroadcastIntent(
            action,
            schemaMapStr,
            separator,
        )
    }
}