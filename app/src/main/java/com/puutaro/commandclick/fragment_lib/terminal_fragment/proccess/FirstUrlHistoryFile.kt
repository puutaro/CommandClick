package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText


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
                currentAppDirPath,
                UsePath.cmdclickFirstHistoryTitle
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
                appUrlSystemDirPath,
                UsePath.cmdclickUrlHistoryFileName
            )
                .textToList()
                .firstOrNull()
                ?: return
            if(firstRow.isEmpty()) return
            FileSystems.writeFile(
                appUrlSystemDirPath,
                UsePath.cmdclickFirstHistoryTitle,
                firstRow
            )
        }
    }
}