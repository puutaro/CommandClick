package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.FileSystems
import java.io.File

class JsFannelInstaller(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val currentAppDirPath = terminalFragment.currentAppDirPath

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
        val installFannelPathObj =  File("${currentAppDirPath}/${selectedFannel}")
        val compMessage = when(installFannelPathObj.isFile) {
            false -> "install ok: ${selectedFannel}"
            else -> "update ok: ${selectedFannel}"
        }
        FileSystems.execCopyFileWithDir(
            selectedFannelPathObj,
            installFannelPathObj,
            true,
        )
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeForCmdIndex.UPDATE_FANNEL_LIST.action
        )
        Toast.makeText(
            context,
            compMessage,
            Toast.LENGTH_LONG
        ).show()
    }
}