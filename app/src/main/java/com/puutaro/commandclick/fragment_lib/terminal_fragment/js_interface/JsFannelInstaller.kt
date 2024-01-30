package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

class JsFannelInstaller(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context

    @JavascriptInterface
    fun install(
        selectedFannel: String
    ){
        val selectedFannelPath =
            "${UsePath.cmdclickFannelItselfDirPath}/${selectedFannel}"
        val selectedFannelPathObj = File(selectedFannelPath)
        if(
            !selectedFannelPathObj.isFile
        ) return
        val recentAppDirPath = FileSystems.getRecentAppDirPath()
        val installFannelPathObj =  File("${recentAppDirPath}/${selectedFannel}")
        val compMessage = when(installFannelPathObj.isFile) {
            false -> "install ok: ${selectedFannel}"
            else -> "update ok: ${selectedFannel}"
        }
        FileSystems.execCopyFileWithDir(
            selectedFannelPathObj,
            installFannelPathObj,
            true,
        )
        Toast.makeText(
            context,
            compMessage,
            Toast.LENGTH_LONG
        ).show()
    }
}