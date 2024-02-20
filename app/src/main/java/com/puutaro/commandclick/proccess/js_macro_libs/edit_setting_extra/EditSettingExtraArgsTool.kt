package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

import android.content.Intent
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap

object EditSettingExtraArgsTool {

    const val extraSettingKeyName = "extra"
    enum class ExtraKey(
        val key: String
    ) {
        PARENT_DIR_PATH("parentDirPath"),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
        SHELL_PATH("shellPath"),
        BROADCAST_ACTION("broadcastAction"),
        BROADCAST_SCHEMAS("broadcastSchemas"),
    }

    fun createExtraMapFromMenuMapList(
        menuMapList: List<Map<String, String>?>,
        filterValue: String,
        targetKey: String,
        separator: Char,
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
        srcMap: Map<String, String>?,
        separator: Char,
    ): Map<String, String> {
        if(
            srcMap.isNullOrEmpty()
        ) return emptyMap()
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
        busyboxExecutor: BusyboxExecutor?,
        srcFileName: String,
        extraMap: Map<String, String>?,
    ): String {
        if(
            extraMap.isNullOrEmpty()
        ) return srcFileName
        val compPrefix = extraMap.get(ExtraKey.COMP_PREFIX.key)
        val compSuffix = extraMap.get(ExtraKey.COMP_SUFFIX.key)
        val shellCon = makeShellCon(extraMap)
        val compPrefixFileName = compPrefix.let {
            if(
                it.isNullOrEmpty()
            ) return@let srcFileName
            UsePath.compPrefix(
                srcFileName,
                it
            )
        }
        val compSuffixFileName = compSuffix.let {
            if(
                it.isNullOrEmpty()
            ) return@let compPrefixFileName
            UsePath.compExtend(
                compPrefixFileName,
                it
            )
        }
        return shellCon.let {
            if(
                it.isEmpty()
            ) return@let compSuffixFileName
            busyboxExecutor?.getCmdOutput(
                shellCon,
                HashMap(extraMap),
            ) ?:compSuffixFileName
        }
    }

    fun makeBroadcastIntent(
        extraMap: Map<String, String>?,
        separator: Char,
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

    fun makeShellCon(
        extraMap: Map<String, String>?,
    ): String {
        if(
            extraMap.isNullOrEmpty()
        ) return String()
        val shellPath = extraMap.get(
            ExtraKey.SHELL_PATH.key
        ) ?: return String()
        return ReadText(shellPath).readText()

    }
}