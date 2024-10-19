package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.ListJsDialogV2
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.PromptWithListDialog
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File
import java.lang.ref.WeakReference

class JsSelectMonitor(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private val subMenuPairListStr = MonitorFileEnums.entries.map {
        "${it.itemName}\t${it.imageStr}"
    }.reversed().joinToString(PromptWithListDialog.valueSeparator.toString())
    private val switchOn = PromptWithListDialog.switchOn
    private val switchOff = PromptWithListDialog.switchOff

    @JavascriptInterface
    fun launch(){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        val listDialogMapCon = listOf(
            "${ListJsDialogV2.ListJsDialogKey.SEARCH_VISIBLE.key}=${PromptWithListDialog.switchOff}",
            "${ListJsDialogV2.ListJsDialogKey.SAVE_TAG.key}=selectMonitor",
            "${ListJsDialogV2.ListJsDialogKey.FOCUS_ITEM_TITLES.key}=${terminalViewModel.currentMonitorFileName}",
//            "${ListJsDialogV2.ListJsDialogKey.BACKGROUND_TYPE.key}=${PromptWithListDialog.Companion.PromptBackground.Type.transparent.name}",
//            "${ListJsDialogV2.ListJsDialogKey.MAX_LINES.key}=null",
            "${ListJsDialogV2.ListJsDialogKey.ON_KEY_OPEN_MODE.key}=${switchOff}",
        ).joinToString(ListJsDialogV2.listJsDialogMapSeparator.toString())
        val selectedMonitorFile = JsDialog(terminalFragmentRef).listDialog(
            File(UsePath.cmdclickDefaultAppDirPath, SystemFannel.preference).absolutePath,
            "Select monitor",
            subMenuPairListStr,
            listDialogMapCon
        )
        if(
            selectedMonitorFile.isEmpty()
        ) return
        MonitorFileEnums.entries.firstOrNull {
            it.itemName == selectedMonitorFile
        } ?: return
        FileSystems.createDirs(
            UsePath.cmdclickMonitorDirPath,
        )
        FileSystems.createFiles(
            File(
                UsePath.cmdclickMonitorDirPath,
                selectedMonitorFile
            ).absolutePath
        )
        terminalViewModel.currentMonitorFileName = selectedMonitorFile
        ToastUtils.showShort("set ${selectedMonitorFile}")
    }

    private enum class MonitorFileEnums(
        val itemName: String,
        val imageStr: String
    ){
        MONITOR_1(UsePath.cmdClickMonitorFileName_1, CmdClickIcons.FILE.str),
        MONITOR_2(UsePath.cmdClickMonitorFileName_2, CmdClickIcons.FILE.str),
        MONITOR_3(UsePath.cmdClickMonitorFileName_3, CmdClickIcons.FILE.str),
        MONITOR_4(UsePath.cmdClickMonitorFileName_4, CmdClickIcons.FILE.str)
    }
}