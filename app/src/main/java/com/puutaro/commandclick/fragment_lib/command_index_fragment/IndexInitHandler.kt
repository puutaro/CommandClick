package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.ConfigFromStartUpFileSetter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.PageSearchToolbarManager
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.util.AppHistoryManager
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.SharePreffrenceMethod

class IndexInitHandler {
    companion object {
        fun handle(
            cmdIndexFragment: CommandIndexFragment
        ){
            val context = cmdIndexFragment.context
            val activity = cmdIndexFragment.activity
            val startUpPref = cmdIndexFragment.activity?.getPreferences(
                Context.MODE_PRIVATE
            )
            val appDirAdminTag = context?.getString(
                R.string.app_dir_admin
            )
            if(
                cmdIndexFragment.tag == appDirAdminTag
            ){
                SharePreffrenceMethod.putSharePreffrence(
                    startUpPref,
                    mapOf(
                        SharePrefferenceSetting.current_app_dir.name
                                to UsePath.cmdclickAppDirAdminPath,
                    )
                )
                return
            }

            terminalShowInit(
                cmdIndexFragment
            )
//            val listener = this.context as? CommandIndexFragment.OnBackstackDeleteListner
//            listener?.onBackstackDelete()
            val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
            val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
            cmdIndexFragment.onUrlLaunchIntent = IntentAction.judge(
                activity
            )

            if(
                cmdIndexFragment.onUrlLaunchIntent
            ){
                FileSystems.updateLastModified(
                    cmdclickAppDirAdminPath,
                    UsePath.cmdclickDefaultAppDirName +
                            CommandClickShellScript.JS_FILE_SUFFIX
                )
            } else {
                UpdateLastModifyFromSharePrefDir.update(startUpPref)
            }

            val currentDirName = FileSystems.filterSuffixJsFiles(
                cmdclickAppDirAdminPath,
                "on"
            ).firstOrNull()?.removeSuffix(
                CommandClickShellScript.JS_FILE_SUFFIX
            ) ?: UsePath.cmdclickDefaultAppDirName
            val currentAppDirPath = "${cmdclickAppDirPath}/${currentDirName}"
            FileSystems.createDirs(
                "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
            )
            SharePreffrenceMethod.putSharePreffrence(
                startUpPref,
                mapOf(
                    SharePrefferenceSetting.current_app_dir.name
                            to currentAppDirPath,
                    SharePrefferenceSetting.current_script_file_name.name
                            to SharePrefferenceSetting.current_script_file_name.defalutStr,
                    SharePrefferenceSetting.on_shortcut.name to SharePrefferenceSetting.on_shortcut.defalutStr,
                )
            )
            FileSystems.updateLastModified(
                UsePath.cmdclickAppHistoryDirAdminPath,
                AppHistoryManager.makeAppHistoryFileNameForInit(
                    currentAppDirPath,
                )
            )
            CommandClickShellScript.makeButtonExecJS(
                currentAppDirPath,
                UsePath.cmdclickButtonExecShellFileName
            )
            CommandClickShellScript.makeButtonExecJS(
                currentAppDirPath,
                UsePath.cmdclickInternetButtonExecJsFileName
            )
            CommandClickShellScript.makeAutoJsFile(
                currentAppDirPath,
                UsePath.cmdclickStartupJsName
            )
            ConfigFromStartUpFileSetter.set(
                cmdIndexFragment,
                currentAppDirPath,
            )

            val pageSearchToolbarManager =
                PageSearchToolbarManager(cmdIndexFragment)

            pageSearchToolbarManager.cancleButtonClickListner()
            pageSearchToolbarManager.pageSearchTextChangeListner()
            pageSearchToolbarManager.onKeyListner()
            pageSearchToolbarManager.searchTopClickLisnter()
            pageSearchToolbarManager.searchDownClickLisnter()

        }
    }
}

private fun terminalShowInit(
    cmdIndexFragment: CommandIndexFragment
){
    val listener = cmdIndexFragment.context as? CommandIndexFragment.OnKeyboardVisibleListener
    listener?.onKeyBoardVisibleChange(
        false,
        true,
        cmdIndexFragment.WebSearchSwitch
    )
}