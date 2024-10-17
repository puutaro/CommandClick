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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object IndexInitHandler {

    private var initJob: Job? = null
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

    fun exit(){
        initJob?.cancel()
    }
    fun handle(
        cmdIndexFragment: CommandIndexFragment
    ) {
        val context = cmdIndexFragment.context
            ?: return
        val startUpPref = FannelInfoTool.getSharePref(context)

        TerminalShower.show(
            cmdIndexFragment
        )
        val ubuntuFiles = UbuntuFiles(context)
        ubuntuFiles.setupLinksForBusyBox()
//            val listener = this.context as? CommandIndexFragment.OnBackstackDeleteListner
//            listener?.onBackstackDelete()
//        val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
//        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
//        val onUrlLaunchIntent = UrlLaunchIntentAction.judge(
//            activity
//        )

//        if (onUrlLaunchIntent) {
//            FileSystems.updateLastModified(
//                File(
//                    cmdclickAppDirAdminPath,
//                    UsePath.cmdclickDefaultAppDirName +
//                            UsePath.JS_FILE_SUFFIX
//                ).absolutePath
//            )
//        } else {
//            UpdateLastModifyFromSharePrefDir.update()
//        }

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
        exit()
        initJob = CoroutineScope(Dispatchers.IO).launch {
            ConfigFromPreferenceFileSetter.set(
                cmdIndexFragment,
            )

            AssetsFileManager.copyFileToDirFromAssets(
                cmdIndexFragment.context,
                "${cmdclickDefaultAppDirPath}/${UsePath.cmdclickJsSystemDirRelativePath}",
                AssetsFileManager.assetsClipToHistoryForBookmark
            )
        }

    }
}
