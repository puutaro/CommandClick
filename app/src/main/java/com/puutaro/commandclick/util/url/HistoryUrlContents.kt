package com.puutaro.commandclick.util.url

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object HistoryUrlContents {

    fun extract(
//        currentAppDirPath: String,
        macroStr: String,
    ): String? {
        val appUrlSystemPath = File(
            UsePath.cmdclickDefaultAppDirPath,
            UsePath.cmdclickUrlSystemDirRelativePath
        ).absolutePath
        return when(macroStr) {
            SettingVariableSelects.OnUrlLaunchMacroSelects.RECENT.name -> {
                ReadText(
                    File(
                        appUrlSystemPath,
                        UsePath.cmdclickUrlHistoryFileName
                    ).absolutePath
                ).textToList()
                    .filter {
                        EnableUrlPrefix.isHttpOrFilePrefix(
                            it.split("\t").lastOrNull()
                        )
                    }
                    .firstOrNull()
                    ?.split("\t")?.lastOrNull()
            }
            SettingVariableSelects.OnUrlLaunchMacroSelects.FREQUENCY.name -> {
                ReadText(
                    File(
                        appUrlSystemPath,
                        UsePath.cmdclickUrlHistoryFileName
                    ).absolutePath
                ).textToList()
                    .filter {
                        EnableUrlPrefix.isHttpOrFilePrefix(
                            it.split("\t").lastOrNull()
                        )
                    }
                    .groupBy { it }
                    .mapValues { it.value.size }
                    .maxBy { it.value }
                    .key
                    .split("\t")
                    .lastOrNull()
            }
            else -> macroStr
        }
    }
}