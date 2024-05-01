package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.file

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.LogTool
import com.puutaro.commandclick.common.variable.intent.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
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
        bodyPath: String,
        errCon: String,
    ){
        LogTool.saveErrLogCon(
            errCon,
            bodyPath,
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
        }.joinToString("\n")
    }

    @JavascriptInterface
    fun showFullFileList(
        dirPath: String,
        extraMapCon: String,
    ): String {
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            '|'
        ).toMap()
        val separator = "?"
        val prefixList =
            extraMap.get(FullFileOrDirListKey.PREFIX.key)
                ?.split(separator)
                ?: emptyList()
        val suffixList =
            extraMap.get(FullFileOrDirListKey.SUFFIX.key)
                ?.split(separator)
                ?: emptyList()
        val excludeFileNameList =
            extraMap.get(FullFileOrDirListKey.EXCLUDE_FILES.key)
                ?.split(separator)
                ?: emptyList()
        return FileSystems.sortedFiles(
            dirPath,
        ).filter {
            fileName ->
            val isPrefix = prefixList.any {
                fileName.startsWith(it)
            } || prefixList.isEmpty()
            val isSuffix = suffixList.any {
                fileName.endsWith(it)
            } || suffixList.isEmpty()
            val isExclude =
                !excludeFileNameList.contains(fileName)
            File("$dirPath/$fileName").isFile
                    && isExclude
                    && isPrefix
                    && isSuffix
        }.map {
            File(dirPath, it).absolutePath
        }.joinToString("\n")
    }

    @JavascriptInterface
    fun showFullDirList(
        dirPath: String,
        extraMapCon: String,
    ): String {
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            '|'
        ).toMap()
        val separator = "?"
        val prefixList =
            extraMap.get(FullFileOrDirListKey.PREFIX.key)
                ?.split(separator)
                ?: emptyList()
        val suffixList =
            extraMap.get(FullFileOrDirListKey.SUFFIX.key)
                ?.split(separator)
                ?: emptyList()
        val excludeDirNameList =
            extraMap.get(FullFileOrDirListKey.EXCLUDE_FILES.key)
                ?.split(separator)
                ?: emptyList()
        return FileSystems.showDirList(
            dirPath,
        ).filter {
                dirName ->
            val isPrefix = prefixList.any {
                dirName.startsWith(it)
            } || prefixList.isEmpty()
            val isSuffix = suffixList.any {
                dirName.endsWith(it)
            } || suffixList.isEmpty()
            val isExclude =
                !excludeDirNameList.contains(dirName)
            File("$dirPath/$dirName").isDirectory
                    && isExclude
                    && isPrefix
                    && isSuffix
        }.map {
            File(dirPath, it).absolutePath
        }.joinToString("\n")
    }

    private enum class FullFileOrDirListKey(
        val key: String
    ) {
        PREFIX(EditSettingExtraArgsTool.ExtraKey.FILTER_PREFIX.key),
        SUFFIX(EditSettingExtraArgsTool.ExtraKey.FILTER_SUFFIX.key),
        EXCLUDE_FILES("excludeFiles")
    }

    @JavascriptInterface
    fun showDirList(
        dirPath: String
    ): String {
        return FileSystems.showDirList(
            dirPath
        ).joinToString("\n")
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