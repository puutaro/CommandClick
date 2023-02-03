package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems


class FirstUrlHistoryFile {
    companion object {
        fun delete(
            terminalFragment: TerminalFragment,
            currentAppDirPath: String
        ){
            if(
                terminalFragment.onHistoryUrlTitle ==
                SettingVariableSelects.Companion.OnHistoryUrlTitle.ON.name
            ) return
            FileSystems.removeFiles(
                currentAppDirPath,
                UsePath.cmdclickFirstHistoryTitle
            )
        }
    }
}