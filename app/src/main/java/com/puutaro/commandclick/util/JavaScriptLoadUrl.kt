package com.puutaro.commandclick.util

import TsvImportManager
import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.import.CmdVariableReplacer
import com.puutaro.commandclick.proccess.import.JsImportManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object JavaScriptLoadUrl {

    val commentOutMark = "//"
    private val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(LanguageTypeSelects.JAVA_SCRIPT)
    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String
    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String

    private val commandSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
    ) as String
    private val commandSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
    ) as String
    fun make (
        context: Context?,
        execJsPath: String,
        jsListSource: List<String>,
        setReplaceVariableMapSrc: Map<String, String>? = null,
        extraRepValMap: Map<String, String>? = null,
    ):String? {
//        val jsFileObj = File(execJsPath)
        if(
            !File(execJsPath).isFile
        ) return null
        val fannelName = File(
            CcPathTool.getMainFannelFilePath(execJsPath)
        ).name
//        val recentAppDirPath = jsFileObj.parent
//        if(
//            recentAppDirPath.isNullOrEmpty()
//        ) return null

//        val scriptFileName = jsFileObj.name
        val jsListBeforeRemoveTsv = jsListSource.ifEmpty {
            ReadText(execJsPath).textToList()
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "js_repbals.txt").absolutePath,
//            listOf(
//                "execJsPath: ${execJsPath}",
//                "jsListBeforeRemoveTsv: ${jsListBeforeRemoveTsv}",
//            ).joinToString("\n\n")
//        )
        if(
            jsListBeforeRemoveTsv.isEmpty()
        ) return null
        if(
            jsListBeforeRemoveTsv.joinToString().replace("\n", "").trim().isEmpty()
        ) return null
        val setReplaceVariableMapBeforeConcatTsvMap = createMakeReplaceVariableMapHandler(
            context,
            jsListBeforeRemoveTsv,
//            recentAppDirPath,
            fannelName,
            setReplaceVariableMapSrc,
        )
        val setReplaceVariableMapBeforeConcatExtraMap = TsvImportManager.concatRepValWithTsvImport(
            execJsPath,
            jsListBeforeRemoveTsv,
            setReplaceVariableMapBeforeConcatTsvMap
        )
        val setReplaceVariableMapBeforeConcatCmdVal = concatWithExtraMap(
            setReplaceVariableMapBeforeConcatExtraMap,
            extraRepValMap
        )
        val setReplaceVariableMap = CmdVariableReplacer.replace(
            execJsPath,
            setReplaceVariableMapBeforeConcatCmdVal
        )

        val jsList = TsvImportManager.removeTsvImport(jsListBeforeRemoveTsv)

        CoroutineScope(Dispatchers.IO).launch {
//            val currentJsPath = "${UsePath.cmdclickDefaultAppDirPath}/$fannelName"
////            val mainCurrentAppDirPath = CcPathTool.getMainAppDirPath(
////                currentJsPath
////            )
//            val mainFannelName = File(
//                    CcPathTool.getMainFannelFilePath(
//                        currentJsPath
//                    )
//                ).name
            makeReplaceVariableTableTsv(
                setReplaceVariableMap,
//                mainCurrentAppDirPath,
                fannelName,
            )
        }

        val loadJsUrl = excludeSettingVariable(
            context,
            jsList,
            execJsPath,
            setReplaceVariableMap,
        ).joinToString(" ")
            .let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableMap,
//                    recentAppDirPath,
                    fannelName
                )
            }
        if(
            loadJsUrl.isEmpty()
            || loadJsUrl.isBlank()
        ) return null
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "js_repbals.txt").absolutePath,
//            listOf(
//                "execJsPath: ${execJsPath}",
//                "setReplaceVariableMap: ${setReplaceVariableMap}",
//                "jsList: ${jsList}",
//                "loadJsUrl: ${loadJsUrl}",
//                "makeLastJsCon(loadJsUrl): ${makeLastJsCon(loadJsUrl)}"
//            ).joinToString("\n\n")
//        )
        return makeLastJsCon(loadJsUrl)
    }

    private fun excludeSettingVariable(
        context: Context?,
        jsList: List<String>,
        execJsPath: String,
        setReplaceVariableMap: Map<String, String>?,
    ): List<String> {
        var countSettingSectionStart = 0
        var countSettingSectionEnd = 0
        var countCmdSectionStart = 0
        var countCmdSectionEnd = 0
        return jsList.map {
            val afterJsImport = JsImportManager.import(
                context,
                it,
                execJsPath,
                setReplaceVariableMap
            )
            if(
                afterJsImport.startsWith(settingSectionStart)
                && afterJsImport.endsWith(settingSectionStart)
            ) countSettingSectionStart++
            if(
                afterJsImport.startsWith(settingSectionEnd)
                && afterJsImport.endsWith(settingSectionEnd)
            ) countSettingSectionEnd++
            if(
                afterJsImport.startsWith(commandSectionStart)
                && afterJsImport.endsWith(commandSectionStart)
            ) countCmdSectionStart++
            if(
                afterJsImport.startsWith(commandSectionEnd)
                && afterJsImport.endsWith(commandSectionEnd)
            ) countCmdSectionEnd++
            if(
                countSettingSectionStart > 0
                && countSettingSectionEnd == 0
            ) "$afterJsImport;"
            else if(
                countCmdSectionStart > 0
                && countCmdSectionEnd == 0
            ) "$afterJsImport;"
            else afterJsImport
        }.joinToString("\n").split("\n").map {
            val trimJsRow = it.trim()
            if(
                trimJsRow.startsWith(commentOutMark)
            ) return@map String()
            if(
                !trimJsRow.contains(commentOutMark)
            ) return@map trimJsRow
            if(
                !trimJsRow.endsWith(";")
            ) return@map trimJsRow
            val trimJsRowList = trimJsRow.split(";")
            val includeCommentOut =  trimJsRowList
                .lastOrNull()
                ?.contains(commentOutMark)
            if(
                includeCommentOut != true
            ) return@map trimJsRow
            val trimJsRowListSize = trimJsRowList.size
            val sliceTrimJsRowList = trimJsRowList.slice(
                0..trimJsRowListSize - 2
            ).joinToString(";") + ";"
            sliceTrimJsRowList
        }
    }



    fun makeRawJsConFromContents(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        jsConBeforeJsImport: String,
        setReplaceVariableMap: Map<String, String>?,
    ): String {
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val currentFannelPath = File(UsePath.cmdclickDefaultAppDirPath, currentFannelName).absolutePath
        val jsConBeforeJsImportCompNewLine = "\n${jsConBeforeJsImport}"
        val setReplaceVariableMapByConcat =
            TsvImportManager.concatRepValMapWithTsvImportFromContents(
                jsConBeforeJsImportCompNewLine,
                setReplaceVariableMap
            )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsJavaLoadUrl.txt").absolutePath,
//            listOf(
//                "jsConBeforeJsImport: ${jsConBeforeJsImport}",
//                "setReplaceVariableMapByConcat: ${setReplaceVariableMapByConcat}"
//            ).joinToString("\n")
//        )
        val jsConBeforeReplace = TsvImportManager.removeTsvImport(
            jsConBeforeJsImportCompNewLine.split("\n")
        ).map {
            JsImportManager.import(
                fragment.context,
                it,
                currentFannelPath,
                setReplaceVariableMap
            )
        }.joinToString("\n")
        return SetReplaceVariabler.execReplaceByReplaceVariables(
            jsConBeforeReplace,
            setReplaceVariableMapByConcat,
//            currentAppDirPath,
            currentFannelName
        )
    }

    fun makeFromContents (
        context: Context?,
        jsList: List<String>
    ):String? {
        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(LanguageTypeSelects.JAVA_SCRIPT)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String

        val commandSectionStart = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
        ) as String
        val commandSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
        ) as String
        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            jsList,
            settingSectionStart,
            settingSectionEnd
        )
        val setReplaceVariableMap =
            SetReplaceVariabler.makeSetReplaceVariableMap(
                context,
                settingVariableList,
//                String(),
                String()
            )
        var countSettingSectionStart = 0
        var countSettingSectionEnd = 0
        var countCmdSectionStart = 0
        var countCmdSectionEnd = 0
        val loadJsUrl = jsList.map {
            if(
                it.startsWith(settingSectionStart)
                && it.endsWith(settingSectionStart)
            ) countSettingSectionStart++
            if(
                it.startsWith(settingSectionEnd)
                && it.endsWith(settingSectionEnd)
            ) countSettingSectionEnd++
            if(
                it.startsWith(commandSectionStart)
                && it.endsWith(commandSectionStart)
            ) countCmdSectionStart++
            if(
                it.startsWith(commandSectionEnd)
                && it.endsWith(commandSectionEnd)
            ) countCmdSectionEnd++
            if(
                countSettingSectionStart > 0
                && countSettingSectionEnd == 0
            ) "$it;"
            else if(
                countCmdSectionStart > 0
                && countCmdSectionEnd == 0
            ) "$it;"
            else it
        }.joinToString("\n").split("\n").map {
            val trimJsRow = it
                .trim()
            if(
                trimJsRow.startsWith(commentOutMark)
            ) return@map String()
            if(
                !trimJsRow.contains(commentOutMark)
            ) return@map trimJsRow
            if(
                !trimJsRow.endsWith(";")
            ) return@map trimJsRow
            val trimJsRowList = trimJsRow.split(";")
            val includeCommentOut =  trimJsRowList
                .lastOrNull()
                ?.contains(commentOutMark)
            if(
                includeCommentOut != true
            ) return@map trimJsRow
            val trimJsRowListSize = trimJsRowList.size
            val sliceTrimJsRowList = trimJsRowList.slice(
                0..trimJsRowListSize - 2
            ).joinToString(";") + ";"
            sliceTrimJsRowList
        }.joinToString(" ")
            .let {
                ScriptPreWordReplacer.replace(
                    it,
//                    String(),
                    String()
                )
            }.let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariableMap,
//                    String(),
                    String()
                )
            }
        if(
            loadJsUrl.isEmpty()
            || loadJsUrl.isBlank()
        ) return null
        return makeLastJsCon(loadJsUrl)
    }

    private fun concatWithExtraMap(
        setReplaceVariableMap: Map<String, String>?,
        extraRepValMap: Map<String, String>?
    ): Map<String, String>? {
        return when(true){
            (extraRepValMap.isNullOrEmpty()
                    && setReplaceVariableMap.isNullOrEmpty())
            -> null
            (!extraRepValMap.isNullOrEmpty()
                    && setReplaceVariableMap.isNullOrEmpty())
            -> extraRepValMap
            (extraRepValMap.isNullOrEmpty()
                    && !setReplaceVariableMap.isNullOrEmpty())
            -> setReplaceVariableMap
            (!extraRepValMap.isNullOrEmpty()
                    && !setReplaceVariableMap.isNullOrEmpty())
            -> extraRepValMap + setReplaceVariableMap
            else -> null
        }
    }

    fun makeLastJsCon(
        loadJsUrl: String
    ): String {
        return "javascript:(function() { " +
                    "try{${loadJsUrl}} catch(error){" +
                        "const errMessage = error.message;" +
                        "if(errMessage.includes(\"exitZero\")){return;};" +
                        "jsToast.errLog(`ERROR ${'$'}{errMessage}`);" +
                        "jsFileSystem.revUpdateFile(errMessage);" +
                        "jsFileSystem.errJsLog(errMessage);" +
                    "};" +
                "})();"
    }

    fun makeReplaceVariableTableTsv(
        setReplaceVariableMap:  Map<String, String>?,
//        recentAppDirPath: String,
        scriptFileName: String,
    ){
        if(
            setReplaceVariableMap.isNullOrEmpty()
        ) return
        val preWordTsvTable = ScriptPreWordReplacer.makeTsvTable(
//            recentAppDirPath,
            scriptFileName,
        )
        val replaceVariableTable = setReplaceVariableMap.entries.map {
            val replacedVal = ScriptPreWordReplacer.replace(
                it.value,
//                recentAppDirPath,
                scriptFileName,
            )
            "${it.key}\t${replacedVal}"
        }.joinToString("\n")
        val fannelSettingsDirPath = ScriptPreWordReplacer.replace(
            UsePath.fannelSettingVariablsDirPath,
//            recentAppDirPath,
            scriptFileName,
        )
        FileSystems.writeFile(
            File(
                fannelSettingsDirPath,
                UsePath.replaceVariablesTsv
            ).absolutePath,
            "${preWordTsvTable}\n${replaceVariableTable}"
        )
    }

    fun createMakeReplaceVariableMapHandler(
        context: Context?,
        jsList: List<String>,
//        recentAppDirPath: String,
        scriptFileName:  String,
        setReplaceVariableMapSrc: Map<String, String>? = null,
    ): Map<String, String>? {
        if(
            !setReplaceVariableMapSrc.isNullOrEmpty()
        ) return setReplaceVariableMapSrc
        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            jsList,
            settingSectionStart,
            settingSectionEnd
        )
        val setReplaceVariableMap =
            SetReplaceVariabler.makeSetReplaceVariableMap(
                context,
                settingVariableList,
//                recentAppDirPath,
                scriptFileName
            )
        if(
            !setReplaceVariableMap.isNullOrEmpty()
        ) return setReplaceVariableMap
        return SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            "${UsePath.cmdclickDefaultAppDirPath}/${scriptFileName}",
        )
    }
}
