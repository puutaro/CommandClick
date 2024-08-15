package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.CmdClickSystemAppDir
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.ConfigFromPreferenceFileSetter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.PageSearchToolbarManager
import com.puutaro.commandclick.proccess.UrlLaunchIntentAction
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.filer.StartFileMaker
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryManager
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.file.UrlFileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File

object IndexInitHandler {
    fun handle(
        cmdIndexFragment: CommandIndexFragment
    ){
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
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        val onUrlLaunchIntent = UrlLaunchIntentAction.judge(
            activity
        )

        if(onUrlLaunchIntent){
            FileSystems.updateLastModified(
                File(
                    cmdclickAppDirAdminPath,
                    UsePath.cmdclickDefaultAppDirName +
                            UsePath.JS_FILE_SUFFIX
                ).absolutePath
            )
        } else {
            UpdateLastModifyFromSharePrefDir.update(startUpPref)
        }

        val currentDirName = FileSystems.filterSuffixJsFiles(
            cmdclickAppDirAdminPath,
            "on"
        ).firstOrNull()?.removeSuffix(
            UsePath.JS_FILE_SUFFIX
        ) ?: UsePath.cmdclickDefaultAppDirName
        val currentAppDirPath = "${cmdclickAppDirPath}/${currentDirName}"
        FileSystems.createDirs(
            "${currentAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
        )
        FileSystems.createDirs(
            "${currentAppDirPath}/${UsePath.cmdclickHitSystemDirRelativePath}"
        )
        FileSystems.createDirs(
            "${currentAppDirPath}/${UsePath.cmdclickScrollSystemDirRelativePath}"
        )
        FannelInfoTool.putAllFannelInfo(
            startUpPref,
            currentAppDirPath,
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

        if(
            currentAppDirPath == UsePath.cmdclickSystemAppDirPath
        ) return
        FileSystems.updateLastModified(
            File(
                UsePath.cmdclickAppHistoryDirAdminPath,
                FannelHistoryManager.makeAppHistoryFileNameForInit(
                    currentAppDirPath,
                )
            ).absolutePath
        )
        val urlFileSystems = UrlFileSystems()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                CmdClickSystemAppDir.createPreferenceFannel(
                    context,
                    cmdIndexFragment.fannelInfoMap,
                )
            }
            withContext(Dispatchers.IO){
                urlFileSystems.getFannelList(context)
            }
            StartFileMaker.makeForConfig(
                cmdIndexFragment
            )
            CommandClickScriptVariable.makeButtonExecJS(
                currentAppDirPath,
                UsePath.cmdclickButtonExecShellFileName
            )
            CommandClickScriptVariable.makeButtonExecJS(
                currentAppDirPath,
                UsePath.cmdclickInternetButtonExecJsFileName,
                UsePath.selectMenuFannelPath
            )
            val concurrentLimit = 10
            val semaphore = Semaphore(concurrentLimit)
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val jobList = UrlFileSystems.Companion.FirstCreateFannels.values().map {
                        async {
                            semaphore.withPermit {
                                urlFileSystems.createFile(
                                    context,
                                    currentAppDirPath,
                                    it.str
                                )
                            }
                        }
                    }
                    jobList.forEach { it.await() }
                }
                withContext(Dispatchers.IO){
                    BroadcastSender.normalSend(
                        context,
                        BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
                    )
                }
            }

            StartFileMaker.makeCmdTerminalListFiles(
                cmdIndexFragment,
            )
            ConfigFromPreferenceFileSetter.set(
                cmdIndexFragment,
                currentAppDirPath,
            )

            AssetsFileManager.copyFileToDirFromAssets(
                cmdIndexFragment.context,
                "${currentAppDirPath}/${UsePath.cmdclickJsSystemDirRelativePath}",
                AssetsFileManager.assetsClipToHistoryForBookmark
            )
        }

    }
}
