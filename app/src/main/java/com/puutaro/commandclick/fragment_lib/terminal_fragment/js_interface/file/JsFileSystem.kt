package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.file

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.LogTool
import com.puutaro.commandclick.common.variable.intent.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class JsFileSystem(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

    @JavascriptInterface
    fun readLocalFile(path: String): String {
        val fileObj = File(path)
        if(!fileObj.isFile) return String()
        return ReadText(
            fileObj.absolutePath
        ).readText()
    }

    @JavascriptInterface
    fun read(path: String): String {
        return readLocalFile(
            path
        )
    }

    @JavascriptInterface
    fun writeLocalFile(
        filePath: String,
        contents: String
    ) {
        FileSystems.writeFile(
            filePath,
            contents
        )
    }

    @JavascriptInterface
    fun write(
        filePath: String,
        contents: String
    ) {
        writeLocalFile(
            filePath,
            contents,
        )
    }

    @JavascriptInterface
    fun fileEcho(
        fileName: String,
        outPutOption: String,
    ) {
        if(
            SettingVariableSelects.TerminalOutPutModeSelects.NO.name
            == outPutOption
        ) return
        val currentMonitorPath = File(
            UsePath.cmdclickMonitorDirPath,
            terminalViewModel.currentMonitorFileName
        ).absolutePath
        val currentTime = ZonedDateTime.now(
            ZoneId.of("Asia/Tokyo")
        ).format(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        ).replace(Regex("[.].*$"), "")
        val addContents = "\n### $currentTime ${fileName}\n"
        terminalViewModel.onBottomScrollbyJs = !(
                outPutOption ==
                        SettingVariableSelects.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
                )
        if(
            SettingVariableSelects.TerminalOutPutModeSelects.REFLASH.name
            == outPutOption
            || SettingVariableSelects.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
            == outPutOption
        ) {
            FileSystems.writeFile(
                currentMonitorPath,
                addContents
            )
            return
        }
        val echoContents = ReadText(
            currentMonitorPath
        ).readText() + addContents
        FileSystems.writeFile(
            currentMonitorPath,
            echoContents
        )
    }

    @JavascriptInterface
    fun errLog(
        con: String
    ){
        LogSystems.stdErr(
            context,
            con
        )
    }

    @JavascriptInterface
    fun errJsLog(
        con: String
    ){
        LogSystems.stdErr(
            context,
            con,
            debugNotiJanre = BroadCastIntentExtraForJsDebug.DebugGenre.JS_ERR.type
        )
    }

    @JavascriptInterface
    fun stdLog(
        con: String
    ){
        LogSystems.stdSys(con)
    }

    @JavascriptInterface
    fun jsEcho(
        outPutOption: String,
        contents: String,
    ) {
        execJsEcho(
            outPutOption,
            contents,
            "yes"
        )
    }

    private fun execJsEcho(
        outPutOption: String,
        contents: String,
        wrap: String
    ) {
        if (
            SettingVariableSelects.TerminalOutPutModeSelects.NO.name
            == outPutOption
        ) return
        val currentMonitorPath =
            File(
                UsePath.cmdclickMonitorDirPath,
                terminalViewModel.currentMonitorFileName
            ).absolutePath
        terminalViewModel.onBottomScrollbyJs = !(
                outPutOption ==
                        SettingVariableSelects.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
                )
        if (
            SettingVariableSelects.TerminalOutPutModeSelects.REFLASH.name
            == outPutOption
            || SettingVariableSelects.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
            == outPutOption
        ) {
            FileSystems.writeFile(
                currentMonitorPath,
                contents
            )
            return
        }
        val echoContents = ReadText(
            currentMonitorPath,
        ).readText() + contents
        val wrapChar = if(wrap == "yes"){
            "\n"
        } else String()
        FileSystems.writeFile(
            currentMonitorPath,
            "${echoContents}${wrapChar}"
        )
    }

    fun outputSwitch(
        switch: String
    ){
        if(switch == "on") {
            terminalViewModel.onDisplayUpdate = true
            return
        }
        terminalViewModel.onDisplayUpdate = false
    }

    @JavascriptInterface
    fun revUpdateFile(
        path: String,
        con: String,
    ){
        val errEvidence = LogTool.makeTopPreTagLogTagHolder(
            LogTool.errRedCode,
            con
        )
//            LogTool.preTagHolder.format(
//            LogTool.errRedCode,
//            con
//        )
//        val bodyCon = LogTool.makeSpanTagHolder(
//            LogTool.logGreenPair,
//            ReadText(path).readText()
//        )
        val saveCon = errEvidence + ReadText(
            path
        ).readText()
        FileSystems.writeFile(
            path,
            saveCon
        )
    }

    @JavascriptInterface
    fun removeFile(path: String){
        FileSystems.removeFiles(path)
    }

    @JavascriptInterface
    fun createDir(path: String){
        FileSystems.createDirs(path)
    }

    @JavascriptInterface
    fun removeDir(path: String){
        FileSystems.removeDir(path)
    }

    @JavascriptInterface
    fun copyDir(
        sourcePath: String,
        destiDirPath: String
    ){
        FileSystems.copyDirectory(
            sourcePath,
            destiDirPath
        )
    }

    @JavascriptInterface
    fun copyFile(
        sourceFilePath: String,
        destiFilePath: String
    ){
        FileSystems.copyFile(
            sourceFilePath,
            destiFilePath
        )
    }


    @JavascriptInterface
    fun showFileList(
        dirPath: String
    ): String {
        return FileSystems.sortedFiles(
            dirPath,
        ).filter {
            File("$dirPath/$it").isFile
        }.joinToString("\t")
    }

    @JavascriptInterface
    fun showDirList(
        dirPath: String
    ): String {
        return FileSystems.showDirList(
            dirPath
        ).joinToString("\t")
    }

    @JavascriptInterface
    fun isFile(
        path: String
    ): Boolean {
        return File(path).isFile
    }

    @JavascriptInterface
    fun isDir(
        path: String
    ): Boolean {
        return File(path).isDirectory
    }
}