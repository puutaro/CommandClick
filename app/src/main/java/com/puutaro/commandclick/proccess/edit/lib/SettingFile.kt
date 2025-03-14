package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.SettingFileVariables
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionManager
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingActionData
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingArgsTool
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.AltRegexTool
import com.puutaro.commandclick.util.str.NewLineTool
import com.puutaro.commandclick.util.str.SpeedReplacer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File

object SettingFile {

    fun read(
        context: Context?,
        fannelPath: String,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
        settingFilePath: String,
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val fannelName = fannelPathObj.name
        val firstSettingConList = ReadSettingFileBuf.read(
            settingFilePath
        ).split("\n")
//        ReadText(
//            settingFilePath
//        ).textToList()
        val settingConList = ImportManager.import(
            context,
            fannelName,
            setReplaceVariableMapSrc,
            busyboxExecutor,
            null,
            null,
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            firstSettingConList,
        ).split("\n")
//        if(settingConList.joinToString("\n").contains("settingAction=")) {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "settingFile_read.txt").absolutePath,
//                listOf(
//                    "firstSettingConList: ${firstSettingConList.joinToString("~~~")}",
//                    "-----",
//                    "settingConList: ${settingConList.joinToString("~~~")}"
//                ).joinToString("\n\n\n") + "\n\n==============\n\n"
//            )
//        }
        return settingConFormatter(
            settingConList
        ).let {
            formSettingContents(it)
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMapSrc,
                fannelName
            )
        }
    }

    fun readLayout(
        context: Context?,
        fannelPath: String,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        globalVarNameToValueMap: Map<String, String>?,
        globalVarNameToBitmapMap: Map<String, Bitmap?>?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        settingFilePath: String,
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val fannelName = fannelPathObj.name
//        val dateList = mutableListOf<Pair<String, LocalDateTime>>()
//        dateList.add("read" to LocalDateTime.now())
        val firstSettingConList = ReadSettingFileBuf.read(
            settingFilePath
        ).split("\n")
//        ReadText(
//            settingFilePath
//        ).textToList()
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "settingFile.txt").absolutePath,
//            listOf(
//                "firstSettingCon: ${firstSettingConList.joinToString("\n")}",
//                "readLayoutFromList: ${readLayoutFromList(
//                    context,
//                    firstSettingConList,
//                    fannelName,
//                    setReplaceVariableCompleteMap,
//                )}"
//            ).joinToString("\n\n\n") + "\n\n--------------\n\n"
//        )
//        dateList.add("readLayoutFromList" to LocalDateTime.now())
        return readLayoutFromList(
            context,
            fannelName,
            setReplaceVariableMapSrc,
            busyboxExecutor,
            globalVarNameToValueMap,
            globalVarNameToBitmapMap,
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            firstSettingConList,
        )
//            .let {
//            dateList.add("readLayoutFromList_end" to LocalDateTime.now())
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultAppDirPath, "lreadLayout.txt").absolutePath,
////                (listOf(settingFilePath) + dateList).joinToString("\n") + "\n========\n\n"
////            )
//            it
//        }
    }

    fun readLayoutFromList(
        context: Context?,
        fannelName: String,
        setReplaceVariableMapSrc: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        globalVarNameToValueMap: Map<String, String>?,
        globalVarNameToBitmapMap: Map<String, Bitmap?>?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        firstSettingConList: List<String>,
    ): String {
        val settingConList = ImportManager.import(
            context,
            fannelName,
            setReplaceVariableMapSrc,
            busyboxExecutor,
            globalVarNameToValueMap,
            globalVarNameToBitmapMap,
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            firstSettingConList,
        ).split("\n")
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "readLayoutFromList00.txt").absolutePath,
//            listOf(
//                "firstSettingConList: ${firstSettingConList}",
//                "-----",
//                "settingConList: ${settingConList}"
//            ).joinToString("\n\n\n") + "\n\n==============\n\n"
//        )
        return settingConFormatter(
            settingConList
        ).let {
            formSettingContents(it)
        }
//            .let {
//            val layoutContents = SetReplaceVariabler.execReplaceByReplaceVariables(
//                it,
//                setReplaceVariableCompleteMap,
//                fannelName
//            )
//            layoutContents
//        }
    }

    fun readFromList(
        settingConList: List<String>,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?
    ): String {
        val fannelPathObj = File(fannelPath)
        if (!fannelPathObj.isFile) return String()
        val scriptFileName = fannelPathObj.name
        return formSettingContents(settingConList).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableCompleteMap,
                scriptFileName
            )
        }
    }

    private fun settingConFormatter(
        settingConList: List<String>,
    ): List<String> {
        return settingConList.map {
            it.trim()
        }.filter {
            it.isNotEmpty()
                && !it.startsWith("//")
        }.joinToString("").let {
            QuoteTool.replaceBySurroundedIgnore(
                it,
                ',',
                ",\n"
            ).split("\n")
        }
    }

    fun formSettingContents(
        settingCon: List<String>
    ): String {
        return settingCon.map {
            it.trim()
        }.filter {
            it.isNotEmpty()
                    && !it.startsWith("//")
        }.joinToString("")
    }

    private object ImportManager {

        private const val importPreWord = SettingFileVariables.importPreWord

        private const val importEndSeparator = ".impEND"
        private const val settingSeparators = "|?&"
        private const val replaceSeparator = '&'
        private const val defAcQuote = '`'
//        private const val importRegexStr =
//            "\n[ \t]*[${settingSeparators}]*${importPreWord}=.+?\\${importEndSeparator}"
//        private val importRegex = importRegexStr.toRegex(RegexOption.DOT_MATCHES_ALL)
        private enum class ImportKey(val key: String) {
            IMPORT_PATH("importPath"),
            REPLACE("replace"),
            RND_VAR_MAP_CON("rndVarMapCon"),
            LOOP_VAR_NAME("loopVarName"),
            TIMES("times"),
            SEPARATOR("separator"),
            PREFIX("prefix"),
            SUFFIX("suffix"),
            DEF_SETTING_ACTION("defSettingAc"),
            DEF_IMAGE_ACTION("defImageAc"),
            IF_ARGS("ifArgs"),
        }
        private const val startLoopIndex = 1

        class RndVarManager {

            companion object {
                const val rndVarMapSeparator = ','
                const val rndValueSeparator = '|'
            }
            private val alreadyUseLoopVarNameList = arrayListOf<String>()

            fun getAlreadyUseLoopVarNameList(): ArrayList<String> {
                return alreadyUseLoopVarNameList
            }

            private fun init(){
                alreadyUseLoopVarNameList.clear()
            }

            fun addToAlreadyUseLoopVarNameList(loopVarName: String?){
                if(loopVarName.isNullOrEmpty()) return
                alreadyUseLoopVarNameList.add(loopVarName)
            }
            fun isAlreadyUseLoopVarNameList(loopVarName: String?): Boolean {
                if(loopVarName.isNullOrEmpty()) return false
                return alreadyUseLoopVarNameList.contains(loopVarName)
            }
        }

        fun findAllImportStatements(input: String): Sequence<String> {
            val blackSec = sequenceOf(
                ' ', '\t',
            )
            var result = sequenceOf<String>()
            var index = 0
            while (index < input.length) {
                // 改行のチェック
                if (input[index] != '\n') {
                    index++
                    continue
                }
                index++
                // 空白とタブのチェック
                while (
                    index < input.length
                    && blackSec.contains(input[index])
                ) {
                    index++
                }

                // 設定セパレータのチェック
                while (
                    index < input.length
                    && settingSeparators.contains(input[index])
                ) {
                    index++
                }

                // インポートプレワードのチェック
                if (
                    index + importPreWord.length >= input.length
                    || input.substring(
                        index,
                        index + importPreWord.length
                    ) != importPreWord
                ) continue
                index += importPreWord.length

                // 等号のチェック
                if (
                    index >= input.length
                    || input[index] != '='
                ) continue
                index++

                // 任意の文字のチェック
                val startIndex = index
                while (index < input.length) {
                    if (
                        index + importEndSeparator.length <= input.length
                        && input.substring(
                            index,
                            index + importEndSeparator.length
                        ) == importEndSeparator
                    ) {
                        result +=
                            input.substring(
                                startIndex - importPreWord.length - 1,
                                index + importEndSeparator.length
                            )
                        index += importEndSeparator.length
                        break
                    }
                    index++
                }
            }
            return result
        }



        fun import(
            context: Context?,
            fannelName: String,
            setReplaceVariableMapSrc: Map<String, String>?,
            busyboxExecutor: BusyboxExecutor?,
            globalVarNameToValueMap: Map<String, String>?,
            globalVarNameToBitmapMap: Map<String, Bitmap?>?,
            settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
            imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
            settingSrcConList: List<String>,
        ): String {
//            init()
            val settingConBeforeImport = SetReplaceVariabler.execReplaceByReplaceVariables(
                trimImportSrcCon(settingSrcConList),
                setReplaceVariableMapSrc,
                fannelName
            )

            var settingCon = SpeedReplacer.replace(
                settingConBeforeImport,
                SettingArgsTool.makePlaneVarNameToValueStrMap(globalVarNameToValueMap)?.map {
                    "${'$'}{${it.key}}" to it.value
                }?.asSequence()
            )
//            if(settingCon.contains("LAYOUT_LOOP_INDEX")) {
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lbkLayout.txt").absolutePath,
//                    listOf(
//                        "settingCon: ${settingCon}",
//                        "settingConBeforeImport: ${settingConBeforeImport}",
//
//                        ).joinToString("\n\n")
//                )
//            }
                //settingConBeforeImport
            for(i in 1..5) {
                val result =
                    findAllImportStatements(settingCon)
//                    importRegex.findAll(settingCon)
                if(
                    result.count() == 0
                ){
                    return settingCon
                }
                settingCon = execImport(
                    context,
                    fannelName,
                    setReplaceVariableMapSrc,
                    busyboxExecutor,
                    globalVarNameToValueMap,
                    globalVarNameToBitmapMap,
                    settingActionAsyncCoroutine,
                    imageActionAsyncCoroutine,
                    settingCon,
                    result,
                ).let {
                    trimImportSrcCon(it.split("\n"))
                }
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "sInImpoetsettingCon.txt").absolutePath,
//                listOf(
//                    "settingCon: ${settingCon}",
//                ).joinToString("\n\n=====================\n\n")
//            )
            return settingCon
        }

        private fun execImport(
            context: Context?,
            fannelName: String,
            setReplaceVariableMapSrc: Map<String, String>?,
            busyboxExecutor: BusyboxExecutor?,
            globalVarNameToValueMap: Map<String, String>?,
            globalVarNameToBitmapMap: Map<String, Bitmap?>?,
            settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
            imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
            settingConBeforeImport: String,
            result: Sequence<String>,
//            result: Sequence<MatchResult>,
        ): String {
//            val dateList = mutableListOf<Pair<String, LocalDateTime>>()
//            dateList.add("init" to LocalDateTime.now())
//            var settingCon = settingConBeforeImport
            val rawToConList = runBlocking {
                withContext(Dispatchers.IO) {
                    val importRawToConJobList = result.mapIndexed { index, importRawSrcCon ->
                        async {
//                            dateList.add("loop${index}" to LocalDateTime.now())
//                            val importRawSrcCon = it.value
                            val importSrcConWithPrefix = importRawSrcCon
                                .trim('\n')
                                .trim()
                            val separatorPrefix =
                                AltRegexTool.findPrefixChars(
                                    importSrcConWithPrefix,
                                    settingSeparators,
                                ) ?: String()
//                                Regex("^[${settingSeparators}]*").find(importSrcConWithPrefix)?.value
                            val importSrcCon =
                                when (separatorPrefix.isEmpty()) {
                                    true -> importSrcConWithPrefix
                                    else -> importSrcConWithPrefix.removePrefix(separatorPrefix)
                                }.removeSuffix(importEndSeparator)
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sInImpoet.txt").absolutePath,
//                    listOf(
//                        "importRawSrcCon: ${importRawSrcCon}",
//                        "importSrcConWithPrefix: ${importSrcConWithPrefix}",
//                        "separatorPrefix: ${separatorPrefix}",
//                        "importSrcCon: ${importSrcCon}",
//                    ).joinToString("\n\n=====================\n\n")
//                )
                            if (
                                importSrcCon.isEmpty()
                            ) return@async Pair(
                                String(),
                                String(),
                            )
                            val importMap = makeImportMap(
                                importSrcCon
                            )
                            val argsPairList = getIfArgs(
                                importMap,
                                null,
                                //globalVarNameToValueMap,
                            )

                            val isImportToErr = when(argsPairList.isEmpty()) {
                                true -> true to null
                                else -> SettingIfManager.handle(
                                    ImportKey.IF_ARGS.key,
//                                judgeTargetStr,
                                    argsPairList,
                                    null
                                    //globalVarNameToValueMap,
                                )
                            }
                            val ifArgsErr = isImportToErr.second
//                            if(argsPairList.isNotEmpty()){
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "limportCeck.txt").absolutePath,
//                                    listOf(
//                                        "importMap: ${importMap}",
//                                        "argsPairList: ${argsPairList}",
//                                        "ifArgsErr: ${ifArgsErr?.errMessage}",
//                                        "isImport: ${isImportToErr.first}"
//                                    ).joinToString("\n\n")
//                                )
//                            }
                            if (ifArgsErr != null) {
                                val spanWhere =
                                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                        CheckTool.errBrown,
                                        "importMap: ${importMap}"
                                    )
                                val errMessage =
                                    "[SETTING IMPORT] ${ifArgsErr.errMessage}: ${spanWhere}"
                                LogSystems.broadErrLog(
                                    context,
                                    Jsoup.parse(errMessage).text(),
                                    errMessage,
                                )
                                return@async Pair(
                                    importRawSrcCon,
                                    String(),
                                )
                            }

                            val isImport = isImportToErr.first
                            if(isImport == false) return@async Pair(
                                importRawSrcCon,
                                String(),
                            )
                            val importPath = getImportPath(
                                importMap
                            ) ?: return@async Pair(
                                String(),
                                String(),
                            )
                            if (
                                importPath.isEmpty()
                                || !File(importPath).isFile
                            ) {
                                LogSystems.stdErr(
                                    context,
                                    "Import path not found: ${importPath}"
                                )
//                            settingCon = settingCon.replace(
//                                importRawSrcCon,
//                                String(),
//                            )
                                return@async Pair(
                                    importRawSrcCon,
                                    String(),
                                )
                            }

                            val loopTimes = getLoopTimes(
                                importMap
                            )
                            val loopVarName = getLoopVarName(
                                importMap
                            )
                            val isLoop = loopTimes > startLoopIndex
                            if (
                                isLoop
                                && loopVarName.isNullOrEmpty()
                            ) {
                                val spanTimes = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                    CheckTool.lightBlue,
                                    ImportKey.TIMES.key
                                )
                                val spanLoopVarNameKey =
                                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                        CheckTool.errRedCode,
                                        ImportKey.LOOP_VAR_NAME.key
                                    )
                                val spanImportRawSrcCon =
                                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                        CheckTool.errBrown,
                                        importRawSrcCon
                                    )
                                val errMessage =
                                    "[SETTING IMPORT] ${spanLoopVarNameKey} must specify in ${spanTimes} > ${startLoopIndex}: ${spanImportRawSrcCon}"
                                LogSystems.broadErrLog(
                                    context,
                                    Jsoup.parse(errMessage).text(),
                                    errMessage,
                                )
                                return@async Pair(
                                    String(),
                                    String(),
                                )
                            }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lloopVarName.txt").absolutePath,
//                    listOf(
//                        "loopVarName: ${loopVarName}",
//                        "alreadyUseLoopVarNameList: ${alreadyUseLoopVarNameList}",
//                        "importRawSrcCon: ${importRawSrcCon}",
//                        "settingCon: ${settingCon}",
//                    ).joinToString("\n\n\n\n") + "\n\n========\n\n"
//                )
                            val rndVarManager = RndVarManager()
                            if (
                                isLoop
                                && rndVarManager.isAlreadyUseLoopVarNameList(loopVarName)
                            ) {
                                SettingImportErrManager.raiseAlreadyUsedVarNameErrLog(
                                    context,
                                    loopVarName,
                                    importRawSrcCon,
                                    rndVarManager.getAlreadyUseLoopVarNameList()
                                )
                                return@async Pair(
                                    String(),
                                    String(),
                                )
                            }
                            rndVarManager.addToAlreadyUseLoopVarNameList(loopVarName)
                            val rndVarNameToValueSeq = getRndVarNameToValueSeq(
                                importMap,
                            )
//                            if(rndVarNameToValueSeq?.any() == true){
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "l_import.txt").absolutePath,
//                                    "importPath: ${importPath}\n" + rndVarNameToValueSeq.joinToString("\n") + "\n\n=====\n\n"
//                                )
//                            }
                            rndVarNameToValueSeq?.forEach {
                                (varName, _) ->
                                if(
                                    !rndVarManager.isAlreadyUseLoopVarNameList(varName)
                                ) {
                                    rndVarManager.addToAlreadyUseLoopVarNameList(varName)
                                    return@forEach
                                }
                                SettingImportErrManager.raiseAlreadyUsedVarNameErrLog(
                                    context,
                                    varName,
                                    importRawSrcCon,
                                    rndVarManager.getAlreadyUseLoopVarNameList(),
                                )
                                return@async Pair(
                                    String(),
                                    String(),
                                )

                            }
                            val separator = getSeparator(
                                importMap
                            )
                            val prefix = getPrefix(
                                importMap
                            )
                            val suffix = getSuffix(
                                importMap
                            )
                            val repValMap = getRepMap(
                                importMap
                            )
//                if(importPath.endsWith("notAwaitAsyncVarErr.js")) {
//                    FileSystems.updateFile(
//                        File(
//                            UsePath.cmdclickDefaultAppDirPath,
//                            "setingImpotCon00.txt"
//                        ).absolutePath,
//                        listOf(
//                            "importMap: ${importMap}",
//                            "importCon: ${importPath}",
//                            "separator: ${separator}",
//                            "suffix: ${suffix}",
//                            "prefix: ${prefix}",
//                            "settingCon: ${settingCon}",
//                        ).joinToString("\n\n") + "\n\n============\n\n"
//                    )
//                }
//                            dateList.add("read${index}" to LocalDateTime.now())
                            val importCon = when (loopTimes < startLoopIndex) {
                                true -> String()
                                else -> {
                                    ImportExecutor.exec(
                                        context,
                                        fannelName,
                                        setReplaceVariableMapSrc,
                                        busyboxExecutor,
                                        globalVarNameToValueMap,
                                        globalVarNameToBitmapMap,
                                        settingActionAsyncCoroutine,
                                        imageActionAsyncCoroutine,
                                        importPath,
                                        repValMap,
                                        rndVarNameToValueSeq,
                                        loopTimes,
                                        loopVarName,
                                        importRawSrcCon,
                                        prefix,
                                        suffix,
                                        separator,
                                    )
                                }
                            }
//                            dateList.add("replace${index} ${importPath}" to LocalDateTime.now())
//                            dateList.add("loopEnd${index}" to LocalDateTime.now())
//                            if(fannelName.contains("textToSpeech")) {
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "sKeytoSubKeyCon.txt").absolutePath,
//                                    listOf(
//                                        "prefix: ${prefix}",
//                                        "suffix: ${suffix}",
//                                        "separatorPrefix:${separatorPrefix}",
//                                        "importRawSrcCon: ${importRawSrcCon}",
//                                        "importCon: ${importCon}",
////                                        "editListConfigMapSrc: ${editListConfigMapSrc}",
////                                        "keyToSubKeyCon: ${
////                                            SettingActionForEditList.getSettingConfigCon(
////                                            editListConfigMapSrc,
////                                        )}"
//                                    ).joinToString("\n")
//                                )
//                            }
                            Pair(
                                importRawSrcCon,
                                "${separatorPrefix}${importCon}"
                            )
//                        settingCon = settingCon.replace(
//                            importRawSrcCon,
//                            "${separatorPrefix}${importCon}"
//                        )
//                    .let {
//                    SetReplaceVariabler.execReplaceByReplaceVariables(
//                        it,
//                        setReplaceVariableCompleteMap,
//                        fannelName,
//                    )
//                }
//                if(suffix.isNotEmpty()) {
//                    FileSystems.updateFile(
//                        File(
//                            UsePath.cmdclickDefaultAppDirPath,
//                            "setingImpotCon.txt"
//                        ).absolutePath,
//                        listOf(
//                            "suffix: ${suffix}",
//                            "importCon: ${importCon}",
//                            "settingCon: ${settingCon}",
//                        ).joinToString("\n\n") + "\n\n============\n\n"
//                    )
//                }
                        }
                    }
                    val importRawToConArray =
                        ArrayList<Pair<String, String>>(importRawToConJobList.count())
                    importRawToConJobList.forEach {
                        val (importRawCon, con) = it.await()
                        if(
                            importRawCon.isEmpty()
                        ) return@forEach
                        importRawToConArray.add(Pair(importRawCon, con))
                    }
                    importRawToConArray.filter {
                        it.first.isNotEmpty()
                    }
//                        .filter { (importRawCon, _) ->
//                            importRawCon.isNotEmpty()
//                        }

                }
            }

            val settingCon = SpeedReplacer.replace(
                settingConBeforeImport,
                rawToConList.asSequence(),
            )
//            var settingCon = settingConBeforeImport
//            rawToConList.forEach {
//                settingCon = settingCon.replace(
//                    it.first,
//                    it.second
//                )
//            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lexecimport.txt").absolutePath,
//                (dateList).joinToString("\n") + "\n\n===========\n\n"
//            )
            return SetReplaceVariabler.execReplaceByReplaceVariables(
                settingCon,
                setReplaceVariableMapSrc,
                fannelName,
            )
        }


        private fun trimImportSrcCon(jsList: List<String>): String {
            return "\n" + jsList.joinToString("\n").let {
                AltRegexTool.removeSpaceTagAfterNewline(it)
            }.let {
                AltRegexTool.removeCommentLines(it)
            }.let {
                NewLineTool.replaceMultipleNewlines(it)
            }
//                .replace("\n[ 　\t]*".toRegex(), "\n")
//                .replace("\n//[^\n]+".toRegex(), "\n")
        }



        private object ImportExecutor {
            suspend fun exec(
                context: Context?,
                fannelName: String,
                setReplaceVariableMapSrc: Map<String, String>?,
                busyboxExecutor: BusyboxExecutor?,
                globalVarNameToValueMap: Map<String, String>?,
                globalVarNameToBitmapMap: Map<String, Bitmap?>?,
                settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
                imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
                importPath: String,
                repValMap: Map<String, String>,
                rndVarNameToValueSeq: Sequence<Pair<String, List<String>>>?,
                loopTimes: Int,
                loopVarName: String?,
                importRawSrcCon: String,
                prefix: String,
                suffix: String,
                separator: String,
            ): String {
                if(context == null) return String()
                val atRepValMap = rndVarNameToValueSeq?.map {
                        (varName, valueList) ->
                    varName to valueList.random()
                }?.toMap()
//                val settingAcCon = getSettingActionCon(
//                    importMap,
//                    atRepValMap
//                )
//                val imageAcCon = getImageActionCon(
//                    importMap,
//                    atRepValMap
//                )
                val innerImportCon = CmdClickMap.replaceHolderForJsAction(
                    ReadSettingFileBuf.read(
                        importPath
                    ),
//                        ReadText(importPath).readText(),
                    repValMap
                ).let {
                        con ->
                    if(
                        atRepValMap == null
                    ) return@let con
                    val repCon = CmdClickMap.replaceByAtVar(
                        con,
                        atRepValMap
                    )
                    repCon
                }
                val indexToImportConList = withContext(Dispatchers.IO) {
                    val indexToImportConJobList = (startLoopIndex..loopTimes).map { loopIndex ->
                        async {
                            if (
                                loopTimes == startLoopIndex
                                || loopVarName.isNullOrEmpty()
                                ) {
                                val innerImportConSrc = removeDefAcAndLaunchDefAction(
                                    context,
                                    fannelName,
                                    setReplaceVariableMapSrc,
                                    busyboxExecutor,
                                    globalVarNameToValueMap,
                                    globalVarNameToBitmapMap,
                                    settingActionAsyncCoroutine,
                                    imageActionAsyncCoroutine,
                                    importRawSrcCon,
                                    innerImportCon,
                                    importPath,
                                    atRepValMap,
                                    loopVarName,
                                    loopIndex,
                                )
                                return@async loopIndex to innerImportConSrc
                            }
                            if (
                                !innerImportCon.contains("@{${loopVarName}}")
                            ) {
                                val spanLoopVarName =
                                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                        CheckTool.errRedCode,
                                        loopVarName
                                    )
                                val spanImportRawSrcCon =
                                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                        CheckTool.errBrown,
                                        importRawSrcCon
                                    )
                                val errMessage =
                                    "[SETTING IMPORT] ${ImportKey.LOOP_VAR_NAME.key}'s ${spanLoopVarName} var name is not used: importCon: ${spanImportRawSrcCon}"
                                LogSystems.broadErrLog(
                                    context,
                                    Jsoup.parse(errMessage).text(),
                                    errMessage,
                                )
                                return@async loopIndex to String()
                            }
                            val innerImportConBeforeDefAcRemove = CmdClickMap.replaceByAtVar(
                                innerImportCon,
                                mapOf(
                                    loopVarName to loopIndex.toString()
                                ),
                            )
                            val innerImportConSrc = removeDefAcAndLaunchDefAction(
                                context,
                                fannelName,
                                setReplaceVariableMapSrc,
                                busyboxExecutor,
                                globalVarNameToValueMap,
                                globalVarNameToBitmapMap,
                                settingActionAsyncCoroutine,
                                imageActionAsyncCoroutine,
                                importRawSrcCon,
                                innerImportConBeforeDefAcRemove,
                                importPath,
                                atRepValMap,
                                loopVarName,
                                loopIndex,
                            )
                            loopIndex to innerImportConSrc
                        }
                    }
                    indexToImportConJobList
                        .awaitAll()
                        .sortedBy { it.first }
                        .map { it.second }
                }
                return indexToImportConList.joinToString(separator).let {
                    listOf(
                        prefix,
                        it,
                        suffix
                    ).joinToString("\n")
                }
            }

            private fun removeDefAcAndLaunchDefAction(
                context: Context,
                fannelName: String,
                setReplaceVariableMapSrc: Map<String, String>?,
                busyboxExecutor: BusyboxExecutor?,
                globalVarNameToValueMap: Map<String, String>?,
                globalVarNameToBitmapMap: Map<String, Bitmap?>?,
                settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
                imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
                baseImportRawSrcCon: String,
                importCon: String,
                importPath: String,
                atRepValMap: Map<String, String>?,
                loopVarName: String?,
                loopIndex: Int,
            ): String {
                val settingAcKeyCon = getSettingActionKeyCon(
                    importCon
                )
                val imageAcKeyCon = getImageActionKeyCon(
                    importCon
                )
                val innerImportConSrc = removeDefAcKeyCon(
                    importCon,
                    settingAcKeyCon,
                    imageAcKeyCon
                )
//                if(importCon.contains("wallShakeBackBkRect")) {
//                    FileSystems.writeFile(
//                        File(
//                            UsePath.cmdclickDefaultAppDirPath,
//                            "ldefSettingAc.txt"
//                        ).absolutePath,
//                        listOf(
//                            "importCon: ${importCon}",
//                            "settingAcKeyCon: ${settingAcKeyCon}",
//                            "innerImportConSrc: ${innerImportConSrc}"
//                        ).joinToString("==\n") + "======"
//                    )
//                }
                CoroutineScope(Dispatchers.IO).launch {
                    val settingAcConSrc = getSettingActionCon(
                        settingAcKeyCon,
                    )
                    val imageAcConSrc = getImageActionCon(
                        imageAcKeyCon,
                    )
                    if(
                        settingAcConSrc.isNullOrEmpty()
                        && imageAcConSrc.isNullOrEmpty()
                    ) return@launch
                    val settingAcCon = SetReplaceVariabler.execReplaceByReplaceVariables(
                        settingAcConSrc ?: String(),
                        setReplaceVariableMapSrc,
                        fannelName,
                    )
                    val imageAcCon = SetReplaceVariabler.execReplaceByReplaceVariables(
                        imageAcConSrc ?: String(),
                        setReplaceVariableMapSrc,
                        fannelName,
                    )
                    execLaunchAction(
                        context,
                        fannelName,
                        setReplaceVariableMapSrc,
                        busyboxExecutor,
                        globalVarNameToValueMap,
                        globalVarNameToBitmapMap,
                        settingActionAsyncCoroutine,
                        imageActionAsyncCoroutine,
                        settingAcCon,
                        imageAcCon,
                        atRepValMap,
                        loopVarName,
                        loopIndex,
                        "importPath: ${importPath}, importRawSrcCon: ${baseImportRawSrcCon}"
                    )
                }
                return innerImportConSrc
            }

            private suspend fun execLaunchAction(
                context: Context,
                fannelName: String,
                setReplaceVariableMapSrc: Map<String, String>?,
                busyboxExecutor: BusyboxExecutor?,
                globalVarNameToValueMap: Map<String, String>?,
                globalVarNameToBitmapMap: Map<String, Bitmap?>?,
                settingActionAsyncCoroutine: SettingActionAsyncCoroutine?,
                imageActionAsyncCoroutine: ImageActionAsyncCoroutine?,
                settingAcCon: String?,
                imageAcConSrc: String?,
                atRepValMap: Map<String, String>?,
                loopVarName: String?,
                loopIndex: Int,
                where: String,
            ){
                val loopVarMap =
                    when(
                        loopVarName.isNullOrEmpty()
                    ) {
                        true -> emptyMap()
                        else -> mapOf(
                            loopVarName to loopIndex.toString()
                        )
                    } +  (atRepValMap ?: emptyMap())
                val fannelInfoMap = FannelInfoTool.makeFannelInfoMapByString(
                    currentFannelName = fannelName,
                )
                val varNameToValueStrMapToSignal = withContext(Dispatchers.IO) settingAc@ {
                    if (
                        settingAcCon.isNullOrEmpty()
                    ) return@settingAc null
                    SettingActionManager().exec(
                        context,
                        null,
                        fannelInfoMap,
                        setReplaceVariableMapSrc,
                        busyboxExecutor,
                        settingActionAsyncCoroutine,
                        globalVarNameToValueMap?.map{ it.key },
                        globalVarNameToValueMap,
                        CmdClickMap.replaceByAtVar(
                            settingAcCon ?: String(),
                            loopVarMap
                        ),
                        where,
                    )
                }
                val varNameToValueStrMap = varNameToValueStrMapToSignal?.first
                val settingAcSignal = varNameToValueStrMapToSignal?.second
                if(
                    imageAcConSrc.isNullOrEmpty()
                    || SettingActionData.SettingActionExitManager.isStopAfter(settingAcSignal)
                ) return
                val imageActionCon = CmdClickMap.replace(
                    imageAcConSrc,
                    (varNameToValueStrMap ?: emptyMap()) +
                            (globalVarNameToValueMap ?: emptyMap()),
                )
                ImageActionManager().exec(
                    context,
                    fannelInfoMap,
                    setReplaceVariableMapSrc,
                    busyboxExecutor,
                    null,
                    Glide.with(context)
                        .asDrawable()
                        .sizeMultiplier(0.1f),
                    imageActionAsyncCoroutine,
                    globalVarNameToBitmapMap?.map {
                        it.key
                    },
                    globalVarNameToBitmapMap,
                    imageActionCon,
                    where,
                    null,
                    null,
                    null,
                )
            }
        }

        private object SettingImportErrManager {
            fun raiseAlreadyUsedVarNameErrLog(
                context: Context?,
                varName: String?,
                importRawSrcCon: String,
                alreadyUseLoopVarNameList: List<String>,
            ){
                val spanLoopVarNameKey =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.lightBlue,
                        ImportKey.LOOP_VAR_NAME.key
                    )
                val spanLoopVarName = varName?.let {
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        it
                    )
                } ?: String()
                val spanAlreadyUseLoopVarNameListCon =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        alreadyUseLoopVarNameList.joinToString(",")
                    )
                val spanImportRawSrcCon =
                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        importRawSrcCon
                    )
                val errMessage =
                    "[SETTING IMPORT] ${spanLoopVarNameKey} duplicated: ${spanLoopVarName}, spanAlreadyUseLoopVarNameListCon: ${spanAlreadyUseLoopVarNameListCon} impotCon: ${spanImportRawSrcCon}"
                LogSystems.broadErrLog(
                    context,
                    Jsoup.parse(errMessage).text(),
                    errMessage,
                )
            }
        }

        fun makeImportMap(
            importKeyAndSubKeyCon: String,
        ): Map<String, String> {

            return ImportMapMaker.comp(
                importKeyAndSubKeyCon,
                "${importPreWord}="
            ).toMap()
        }

        fun getImportPath(
            importMap: Map<String, String>,
        ): String? {
            val importPath = importMap.get(
                ImportKey.IMPORT_PATH.key
            )
            return importPath
        }

        fun getRepMap(
            importMap: Map<String, String>,
        ): Map<String, String> {
            return makeRepValHolderMap(
                importMap.get(
                    ImportKey.REPLACE.key
                )
            )
        }

        fun getLoopTimes(
            importMap: Map<String, String>,
        ): Int {
            return try {
               importMap.get(
                        ImportKey.TIMES.key
                    )?.toInt() ?: startLoopIndex
            } catch (e: Exception){
                startLoopIndex
            }
        }

        fun getLoopVarName(
            importMap: Map<String, String>,
        ): String? {
            return importMap.get(
                ImportKey.LOOP_VAR_NAME.key
            )
        }

        fun getRndVarNameToValueSeq(
            importMap: Map<String, String>,
        ): Sequence<Pair<String, List<String>>>? {
            return importMap.get(
                ImportKey.RND_VAR_MAP_CON.key
            )?.let {
                CmdClickMap.createMap(
                    it,
                    RndVarManager.rndVarMapSeparator
                ).asSequence().map {
                    (varName, valueListCon) ->
                    varName to valueListCon.split(
                        RndVarManager.rndValueSeparator
                    ).filter { it.isNotEmpty() }
                }.filter {
                    (varName, _) ->
                    varName.isNotEmpty()
                }
            }
        }

        fun getSettingActionKeyCon(
//            importMap: Map<String, String>,
            importCon: String,
//            loopVarMap: Map<String, String>?
        ): String? {
            if(
                importCon.isEmpty()
            ) return null
            return findDefSettingAcKeyCon(
                importCon,
                ImportKey.DEF_SETTING_ACTION.key
            )
//            return importMap.get(
//                ImportKey.DEF_SETTING_ACTION.key
//            )?.let {
//                settingConFormatter(
//                    it.split("\n")
//                ).let {
//                    formSettingContents(it)
//                }
//            }?.let {
//                CmdClickMap.replaceByAtVar(
//                    it,
//                    loopVarMap
//                )
//            }
        }

        fun getSettingActionCon(
//            importMap: Map<String, String>,
            settingActionKeyCon: String?,
//            loopVarMap: Map<String, String>?
        ): String? {
            if(
                settingActionKeyCon.isNullOrEmpty()
            ) return null
            return extractDefAcCon(
                settingActionKeyCon,
                ImportKey.DEF_SETTING_ACTION.key,
            ).let {
                settingConFormatter(
                    it.split("\n")
                )
            }.let {
                formSettingContents(it)
            }
//            return importMap.get(
//                ImportKey.DEF_SETTING_ACTION.key
//            )?.let {
//                settingConFormatter(
//                    it.split("\n")
//                ).let {
//                    formSettingContents(it)
//                }
//            }?.let {
//                CmdClickMap.replaceByAtVar(
//                    it,
//                    loopVarMap
//                )
//            }
        }

        fun getImageActionKeyCon(
//            importMap: Map<String, String>,
            importCon: String,
//            loopVarMap: Map<String, String>?,
        ): String? {
            if(
                importCon.isEmpty()
                ) return null
            return findDefSettingAcKeyCon(
                importCon,
                ImportKey.DEF_IMAGE_ACTION.key
            )
//            return importMap.get(
//                ImportKey.DEF_IMAGE_ACTION.key
//            )?.let {
//                settingConFormatter(
//                    it.split("\n")
//                ).let {
//                    formSettingContents(it)
//                }
//            }?.let {
//                CmdClickMap.replaceByAtVar(
//                    it,
//                    loopVarMap
//                )
//            }
        }

        fun getImageActionCon(
//            importMap: Map<String, String>,
            imageActionKeyCon: String?,
//            loopVarMap: Map<String, String>?,
        ): String? {
            if(
                imageActionKeyCon.isNullOrEmpty()
            ) return null
            return extractDefAcCon(
                imageActionKeyCon,
                ImportKey.DEF_IMAGE_ACTION.key,
            ).let {
                settingConFormatter(
                    it.split("\n")
                )
            }.let {
                formSettingContents(it)
            }
//            return importMap.get(
//                ImportKey.DEF_IMAGE_ACTION.key
//            )?.let {
//                settingConFormatter(
//                    it.split("\n")
//                ).let {
//                    formSettingContents(it)
//                }
//            }?.let {
//                CmdClickMap.replaceByAtVar(
//                    it,
//                    loopVarMap
//                )
//            }
        }

        private fun extractDefAcCon(
            defAcKeyCon: String,
            defAcKey: String,
        ): String {
            return defAcKeyCon.removePrefix(
                makeDefAcPreWord(defAcKey)
            ).removeSuffix(defAcQuote.toString())
        }

        fun removeDefAcKeyCon(
            importCon: String,
            settingAcKeyCon: String?,
            imageAcKeyCon: String?,
        ): String {
            val innerImportConBeforeDefImageAcRep = when(
                settingAcKeyCon.isNullOrEmpty()
            ){
                true -> importCon
                else -> importCon.replace(
                    settingAcKeyCon,
                    String(),
                )
            }
            val innerImportConSrc = when(
                imageAcKeyCon.isNullOrEmpty()
            ){
                true -> innerImportConBeforeDefImageAcRep
                else -> innerImportConBeforeDefImageAcRep.replace(
                    imageAcKeyCon,
                    String(),
                )
            }
            return innerImportConSrc
        }

        fun makeDefAcPreWord(
            defAcPreWord: String,
        ): String{
            return "\n${defAcPreWord}=${defAcQuote}"
        }

        private fun findDefSettingAcKeyCon(
            importConSrc: String,
            defAcPreWord: String,
        ): String? {
            val importCon = "\n${importConSrc}"
            val findPrefix = makeDefAcPreWord(defAcPreWord)
            val startIndex = importCon.indexOf(
                findPrefix
            )
            if (startIndex == -1) {
                return null
            }

            var currentIndex =
                startIndex + "\n${defAcPreWord}=${defAcQuote}".length
            val contentStart =
                currentIndex

            while (currentIndex < importCon.length) {
                if (importCon[currentIndex] != defAcQuote) {
                    currentIndex++
                    continue
                }
                if (
                    currentIndex > contentStart
                    && importCon[currentIndex - 1] != '\\'
                ) {
                    return importCon.substring(
                        startIndex,
                        currentIndex + 1
                    )
                }
                currentIndex++
            }

            return null // 閉じ括弧 '"' が見つからない
        }

        fun getIfArgs(
            importMap: Map<String, String>,
            varNameToValueStrMap: Map<String, String?>?,
        ): List<Pair<String, String>> {
            val separator = '&'
            return SettingArgsTool.makeArgsPairList(
                importMap,
                ImportKey.IF_ARGS.key,
                varNameToValueStrMap,
                separator,
            )
//            return importMap.get(
//                ImportKey.IF.key
//            )?.let {
//                CmdClickMap.replaceByAtVar(
//                    it,
//                    loopVarMap
//                )
//            }?.let {
//                CmdClickMap.createMap(
//                   it,
//                    separator
//                ).asSequence().filter {
//                    it.first.isNotEmpty()
//                }.map {
//                        argNameToValueStr ->
//                    argNameToValueStr.first to
//                            CmdClickMap.replaceByBackslashToNormal(
//                                argNameToValueStr.second,
//                                varNameToValueStrMap,
//                            )
//                }.toList()
//            }
        }

        fun getSeparator(
            importMap: Map<String, String>,
        ): String {
            return try {
                importMap.get(
                    ImportKey.SEPARATOR.key
                ) ?: String()
            } catch (e: Exception){
                String()
            }
        }

        fun getPrefix(
            importMap: Map<String, String>,
        ): String {
            return try {
                importMap.get(
                    ImportKey.PREFIX.key
                ) ?: String()
            } catch (e: Exception){
                String()
            }
        }

        fun getSuffix(
            importMap: Map<String, String>,
        ): String {
            return try {
                importMap.get(
                    ImportKey.SUFFIX.key
                ) ?: String()
            } catch (e: Exception){
                String()
            }
        }

        private fun makeRepValHolderMap(
            replaceKeyConWithQuote: String?,
        ): Map<String, String> {
            if(
                replaceKeyConWithQuote.isNullOrEmpty()
            ) return emptyMap()
            val replaceKeyCon = QuoteTool.trimBothEdgeQuote(
                replaceKeyConWithQuote
            )
            return CmdClickMap.createMap(
                replaceKeyCon,
                replaceSeparator
            ).toMap().filterKeys { it.isNotEmpty() }
        }
    }
}