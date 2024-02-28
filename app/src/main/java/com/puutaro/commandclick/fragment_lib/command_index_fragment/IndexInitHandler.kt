package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.ConfigFromStartUpFileSetter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.PageSearchToolbarManager
import com.puutaro.commandclick.proccess.IntentAction
import com.puutaro.commandclick.proccess.filer.StartFileMaker
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.proccess.history.AppHistoryManager
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.SharePrefTool
import com.puutaro.commandclick.util.file.UrlFileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object IndexInitHandler {
    fun handle(
        cmdIndexFragment: CommandIndexFragment
    ){
        val context = cmdIndexFragment.context
            ?: return
        val activity = cmdIndexFragment.activity
        val startUpPref = cmdIndexFragment.activity?.getPreferences(
            Context.MODE_PRIVATE
        )

        TerminalShower.show(
            cmdIndexFragment
        )
        val ubuntuFiles = UbuntuFiles(context)
        ubuntuFiles.setupLinksForBusyBox()
//            val listener = this.context as? CommandIndexFragment.OnBackstackDeleteListner
//            listener?.onBackstackDelete()
        val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
        val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
        val onUrlLaunchIntent = IntentAction.judge(
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
        SharePrefTool.putAllSharePref(
            startUpPref,
            currentAppDirPath,
            SharePrefferenceSetting.current_fannel_name.defalutStr,
            SharePrefferenceSetting.on_shortcut.defalutStr,
            SharePrefferenceSetting.current_fannel_state.defalutStr
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
                AppHistoryManager.makeAppHistoryFileNameForInit(
                    currentAppDirPath,
                )
            ).absolutePath
        )
        val urlFileSystems = UrlFileSystems()
        CoroutineScope(Dispatchers.IO).launch {
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
            UrlFileSystems.Companion.FirstCreateFannels.values().forEach {
                CoroutineScope(Dispatchers.IO).launch {
                    urlFileSystems.createFile(
                        context,
                        currentAppDirPath,
                        it.str
                    )
                }
            }
            StartFileMaker.makeForStartupScript(
                cmdIndexFragment,
                currentAppDirPath
            )

            StartFileMaker.makeCmdTerminalListFiles(
                cmdIndexFragment,
            )
            ConfigFromStartUpFileSetter.set(
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
