package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePrefTool

object EditSettingExtraArgsTool {

    enum class ExtraKey(
        val key: String
    ) {
        PARENT_DIR_PATH("parentDirPath"),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
        PREFIX("prefix"),
        SUFFIX("suffix"),
        SHELL_PATH("shellPath"),
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
        editFragment: EditFragment,
        srcFileName: String,
        extraMap: Map<String, String>?,
    ): String {
        if(
            extraMap.isNullOrEmpty()
        ) return srcFileName
        val readSharePreferenceMap = editFragment.readSharePreferenceMap

        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val busyboxExecutor = editFragment.busyboxExecutor
        val setReplaceVariablesMap = editFragment.setReplaceVariableMap
        val compPrefix = extraMap.get(ExtraKey.COMP_PREFIX.key)
        val compSuffix = extraMap.get(ExtraKey.COMP_SUFFIX.key)
        val shellCon = makeShellCon(extraMap).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariablesMap,
                currentAppDirPath,
                currentFannelName
            )
        }
        val compFileNameByShell = shellCon.let {
            if(
                it.isEmpty()
            ) return@let srcFileName
            busyboxExecutor?.getCmdOutput(
                shellCon.replace(
                    "\${FILE_NAME}",
                    srcFileName
                ),
                extraMap,
            ) ?:srcFileName
        }
        val compPrefixFileName = compPrefix.let {
            if(
                it.isNullOrEmpty()
            ) return@let compFileNameByShell
            UsePath.compPrefix(
                compFileNameByShell,
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
        return compSuffixFileName
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