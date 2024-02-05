package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File


class FirstUrlHistoryFile {
    companion object {
        fun delete(
            terminalFragment: TerminalFragment,
            currentAppDirPath: String
        ){
            if(
                terminalFragment.onHistoryUrlTitle ==
                SettingVariableSelects.OnHistoryUrlTitle.ON.name
            ) return
            FileSystems.removeFiles(
                File(
                    currentAppDirPath,
                    UsePath.cmdclickFirstHistoryTitle
                ).absolutePath
            )
        }

        fun update(
            terminalFragment: TerminalFragment,
        ){
            if(
                terminalFragment.onHistoryUrlTitle !=
                SettingVariableSelects.OnHistoryUrlTitle.ON.name
            ) return
            val appUrlSystemDirPath = "${terminalFragment.currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
            val firstRow = ReadText(
                File(
                    appUrlSystemDirPath,
                    UsePath.cmdclickUrlHistoryFileName
                ).absolutePath
            )
                .textToList()
                .firstOrNull()
                ?: return
            if(firstRow.isEmpty()) return
            FileSystems.writeFile(
                File(
                    appUrlSystemDirPath,
                    UsePath.cmdclickFirstHistoryTitle
                ).absolutePath,
                firstRow
            )
        }
    }
}