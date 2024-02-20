package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File

class JsSelectTerm(
    private val terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
    val subMenuPairListScr = SettingSubMenuEnums.values().map {
        "${it.itemName}|${it.imageId}"
    }.reversed().joinToString("\t")

    @JavascriptInterface
    fun launch(){
        val selectedMonitorFile = JsDialog(terminalFragment).listDialog(
            String(),
            String(),
            subMenuPairListScr
        )
        if(
            selectedMonitorFile.isEmpty()
        ) return
        SettingSubMenuEnums.values().firstOrNull {
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
        Toast.makeText(
            context,
            "set ${selectedMonitorFile}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private enum class SettingSubMenuEnums(
        val itemName: String,
        val imageId: Int
    ){
        MONITOR_1(UsePath.cmdClickMonitorFileName_1, R.drawable.icons8_file),
        MONITOR_2(UsePath.cmdClickMonitorFileName_2, R.drawable.icons8_file),
        MONITOR_3(UsePath.cmdClickMonitorFileName_3, R.drawable.icons8_file),
        MONITOR_4(UsePath.cmdClickMonitorFileName_4, R.drawable.icons8_file)
    }
}