package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class JsFileSystem(
    private val terminalFragment: TerminalFragment
) {
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

    @JavascriptInterface
    fun readLocalFile(path: String): String {
        val fileObj = File(path)
        if(!fileObj.isFile) return String()
        val parentDir = fileObj.parent ?: return String()
        return ReadText(
            parentDir,
            fileObj.name
        ).readText()
    }

    @JavascriptInterface
    fun writeLocalFile(path: String, contents: String) {
        val fileObj = File(path)
        val parentDir = fileObj.parent ?: return
        FileSystems.writeFile(
            parentDir,
            fileObj.name,
            contents
        )
    }

    @JavascriptInterface
    fun fileEcho(
        fileName: String,
        outPutOption: String,
    ) {
        if(
            terminalViewModel.onNoJsTermOut
        ) return
        if(
            SettingVariableSelects.Companion.TerminalOutPutModeSelects.NO.name
            == outPutOption
        ) return
        val currentMonitorPath = "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
        val fileObj = File(currentMonitorPath)
        val parentDir = fileObj.parent
            ?: return
        val currentTime = ZonedDateTime.now(
            ZoneId.of("Asia/Tokyo")
        ).format(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        ).replace(Regex("[.].*$"), "")
        val addContents = "\n### $currentTime ${fileName}\n"
        if(
            SettingVariableSelects.Companion.TerminalOutPutModeSelects.REFLASH.name
            == outPutOption
        ) {
            FileSystems.writeFile(
                parentDir,
                fileObj.name,
                addContents
            )
            return
        }
        val echoContents = ReadText(
            parentDir,
            fileObj.name
        ).readText() + addContents
        FileSystems.writeFile(
            parentDir,
            fileObj.name,
            echoContents
        )
    }

    @JavascriptInterface
    fun jsEcho(
        outPutOption: String,
        contents: String,
    ) {
        if(
            terminalViewModel.onNoJsTermOut
        ) return
        if (
            SettingVariableSelects.Companion.TerminalOutPutModeSelects.NO.name
            == outPutOption
        ) return
        val currentMonitorPath =
            "${UsePath.cmdclickMonitorDirPath}/${terminalViewModel.currentMonitorFileName}"
        val fileObj = File(currentMonitorPath)
        val parentDir = fileObj.parent
            ?: return
        if (
            SettingVariableSelects.Companion.TerminalOutPutModeSelects.REFLASH.name
            == outPutOption
        ) {
            FileSystems.writeFile(
                parentDir,
                fileObj.name,
                contents
            )
            return
        }
        val echoContents = ReadText(
            parentDir,
            fileObj.name
        ).readText() + contents
        FileSystems.writeFile(
            parentDir,
            fileObj.name,
            "${echoContents}\n"
        )
    }


    @JavascriptInterface
    fun copyToClipboard(text: String?) {
        val clipboard: ClipboardManager? =
            terminalFragment.activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("demo", text)
        clipboard?.setPrimaryClip(clip)
    }
}