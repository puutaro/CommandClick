package com.puutaro.commandclick.fragment_lib.command_index_fragment

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.util.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

class StartupOrEditExecuteOnceShell {

    companion object {
        fun invoke(
            cmdIndexFragment: CommandIndexFragment,
            readSharePreffernceMap: Map<String, String>
        ) {

            val context = cmdIndexFragment.context
            val appDirAdminTag = context?.getString(R.string.app_dir_admin)
            if(
                cmdIndexFragment.tag == appDirAdminTag
            ) return

            val activity = cmdIndexFragment.activity
            val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            )
            val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()

            val editExecuteOnceCurrentShellFileName =
                terminalViewModel.editExecuteOnceCurrentShellFileName
            if (
                editExecuteOnceCurrentShellFileName.isNullOrEmpty()
            ) {
                WebUrlVariables.makeUrlHistoryFile(
                    "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
                )
                AutoShellExecManager.fire(
                    cmdIndexFragment,
                    UsePath.cmdclickStartupJsName,
                )
            } else {
                TargetFragmentInstance()
                    .getFromFragment<TerminalFragment>(
                        activity,
                        activity?.getString(R.string.index_terminal_fragment)
                    ) ?: return
                ExecJsOrSellHandler.handle(
                    cmdIndexFragment,
                    currentAppDirPath,
                    editExecuteOnceCurrentShellFileName,
                )
                terminalViewModel.editExecuteOnceCurrentShellFileName = null
            }

        }
    }
}