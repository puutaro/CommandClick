package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.CmdClickSystemFannelManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.ConfigFromPreferenceFileSetter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.PageSearchToolbarManager
import com.puutaro.commandclick.proccess.UrlLaunchIntentAction
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryManager
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object IndexInitHandler {

    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
    private val cmdclickUpdateFannelInfoSystemDirPath =
        UsePath.cmdclickUpdateFannelInfoSystemDirPath

    fun handle(
        cmdIndexFragment: CommandIndexFragment
    ) {
        val context = cmdIndexFragment.context
            ?: return
        val activity = cmdIndexFragment.activity
        val startUpPref = FannelInfoTool.getSharePref(context)

        TerminalShower.show(
            cmdIndexFragment
        )
        val ubuntuFiles = UbuntuFiles(context)
        ubuntuFiles.setupLinksForBusyBox()
//            val listener = this.context as? CommandIndexFragment.OnBackstackDeleteListner
//            listener?.onBackstackDelete()
        val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
//        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        val onUrlLaunchIntent = UrlLaunchIntentAction.judge(
            activity
        )

        if (onUrlLaunchIntent) {
            FileSystems.updateLastModified(
                File(
                    cmdclickAppDirAdminPath,
                    UsePath.cmdclickDefaultAppDirName +
                            UsePath.JS_FILE_SUFFIX
                ).absolutePath
            )
        } else {
            UpdateLastModifyFromSharePrefDir.update()
        }

//        val currentDirName = FileSystems.filterSuffixJsFiles(
//            cmdclickAppDirAdminPath,
//            "on"
//        ).firstOrNull()?.removeSuffix(
//            UsePath.JS_FILE_SUFFIX
//        ) ?: UsePath.cmdclickDefaultAppDirName
        FileSystems.createDirs(
            "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        )
        FileSystems.createDirs(
            "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickHitSystemDirRelativePath}"
        )
        FileSystems.createDirs(
            "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickScrollSystemDirRelativePath}"
        )
        FannelInfoTool.putAllFannelInfo(
            startUpPref,
//            currentAppDirPath,
            FannelInfoSetting.current_fannel_name.defalutStr,
            FannelInfoSetting.on_shortcut.defalutStr,
            FannelInfoSetting.current_fannel_state.defalutStr
        )

        val pageSearchToolbarManager =
            PageSearchToolbarManager(cmdIndexFragment)

        pageSearchToolbarManager.cancleButtonClickListner()
        pageSearchToolbarManager.pageSearchTextChangeListner()
        pageSearchToolbarManager.onKeyListner()
        pageSearchToolbarManager.searchTopClickLisnter()
        pageSearchToolbarManager.searchDownClickLisnter()

//        if(
//            currentAppDirPath == UsePath.cmdclickSystemAppDirPath
//        ) return
        FileSystems.updateLastModified(
            File(
                UsePath.cmdclickAppHistoryDirAdminPath,
                FannelHistoryManager.makeAppHistoryFileNameForInit(
//                    currentAppDirPath,
                )
            ).absolutePath
        )
        FileSystems.removeAndCreateDir(cmdclickUpdateFannelInfoSystemDirPath)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                CmdClickSystemFannelManager.createPreferenceFannel(
                    context,
                    cmdIndexFragment.fannelInfoMap,
                )
            }
            CommandClickScriptVariable.makeButtonExecJS(
//                currentAppDirPath,
                UsePath.cmdclickButtonExecShellFileName
            )
            CommandClickScriptVariable.makeButtonExecJS(
//                currentAppDirPath,
                UsePath.cmdclickInternetButtonExecJsFileName,
                UsePath.selectMenuFannelPath
            )
//            StartFileMaker.makeCmdTerminalListFiles(
//                cmdIndexFragment,
//            )
            ConfigFromPreferenceFileSetter.set(
                cmdIndexFragment,
//                currentAppDirPath,
            )

            AssetsFileManager.copyFileToDirFromAssets(
                cmdIndexFragment.context,
                "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickJsSystemDirRelativePath}",
                AssetsFileManager.assetsClipToHistoryForBookmark
            )
        }

    }
}
