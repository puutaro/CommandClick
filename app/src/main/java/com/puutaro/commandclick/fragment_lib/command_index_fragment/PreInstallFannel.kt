package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.component.adapter.FannelManageAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press.LongPressMenuTool
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryPath
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File

object PreInstallFannel {

    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
    private val cmdclickUpdateFannelInfoSystemDirPath = UsePath.cmdclickUpdateFannelInfoSystemDirPath
    private val languageType = LanguageTypeSelects.JAVA_SCRIPT
    private val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
            languageType
        )
    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String

    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String
    private const val concurrencyLimit = 5

    fun install(
        cmdIndexFragment: CommandIndexFragment,
    ){
        val context = cmdIndexFragment.context
        cmdIndexFragment.lifecycleScope.launch {
            cmdIndexFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(500)
                val fannelList = withContext(Dispatchers.IO) {
                    UrlFileSystems.getFannelList(context)
                        .split("\n")
                }
                val fannelNameList = withContext(Dispatchers.IO) {
                    UrlFileSystems.extractFannelNameList(fannelList)
                }
                val fannelNameToDownloadList = withContext(Dispatchers.IO) {
                    makeDownloadListByFannelName(
                        context,
                        fannelNameList,
                        fannelList,
                    )
                }
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "downloadList.txt").absolutePath,
//                listOf(
//                    "fannelRawNameToDownloadList: ${fannelRawNameToDownloadList}").joinToString("\n")
//            )
//            StartFileMaker.makeForConfig(
////                cmdIndexFragment
//            )
                val semaphore = Semaphore(concurrencyLimit)
                withContext(Dispatchers.IO) {
                    val jobList = fannelNameToDownloadList.map {
                        async {
                            semaphore.withPermit {
                                val fannelName = it.first
                                val downloadList = it.second
                                UrlFileSystems.createFileByOverride(
                                    context,
                                    fannelName,
                                    downloadList
                                )
                            }
                        }
                    }
                    jobList.forEach { it.await() }
                }
                withContext(Dispatchers.IO) {
                    BroadcastSender.normalSend(
                        context,
                        BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
                    )
                }
                withContext(Dispatchers.IO) {
                    val isNotUpdateFannelInfo = FileSystems.sortedFiles(
                        cmdclickUpdateFannelInfoSystemDirPath
                    ).isEmpty()
                    if (
                        isNotUpdateFannelInfo
                    ) return@withContext
                    val fannelSettingMapList = FannelSettingMapMaker.make(
                        context,
                        fannelNameToDownloadList.map { it.first }
                    )
                    FileSystems.writeFile(
                        UsePath.fannelSettingMapTsvPath,
                        fannelSettingMapList.joinToString("\n")
                    )
//                    FileSystems.removeAndCreateDir(cmdclickUpdateFannelInfoSystemDirPath)
                }
                withContext(Dispatchers.IO){
                    val pinFannelTsvPath = UsePath.pinFannelTsvPath
                    if(
                        File(pinFannelTsvPath).isFile
                    ) return@withContext
                    FileSystems.writeFile(
                        pinFannelTsvPath,
                        SystemFannel.firstPinFannelList.joinToString("\n")
                    )
                }
                withContext(Dispatchers.IO) {
                    val preInstallFannelListTsvPath = UsePath.preInstallFannelListTsvPath
                    val preInstallFannelList = listOf(SystemFannel.home) + fannelNameList
                    FileSystems.writeFile(
                        preInstallFannelListTsvPath,
                        preInstallFannelList.joinToString("\n")
                    )
                }
            }
        }
    }

    fun isPreinstallFannel(
        fannelName: String
    ): Boolean {
        return ReadText(
            UsePath.preInstallFannelListTsvPath
        ).textToList().contains(fannelName)
    }

    private suspend fun makeDownloadListByFannelName(
        context: Context?,
        fannelNameList: List<String>,
        fannelList: List<String>,
    ): List<Pair<String, List<String>>> {
        val semaphore = Semaphore(concurrencyLimit)
        val channel = Channel<Pair<String, List<String>>>(fannelNameList.size)
        withContext(Dispatchers.IO) {
            val jobList = fannelNameList.mapIndexed { index, fannelName ->
                semaphore.withPermit {
                    async {
                        val downloadEntryList = fannelList.filter { relativePath ->
                            val isNotPartPng = !"/${relativePath}".contains(
                                "/${FannelHistoryPath.makePartPngDirCut()}/"
                            )
                            UrlFileSystems.isFannelListByName(
                                relativePath,
                                fannelName
                            ) && isNotPartPng
                        }
                        val downloadListByVersion = makeDownloadListByVersion(
                            context,
                            fannelName,
                            downloadEntryList,
                        )
                        val downloadList = when (
                            downloadListByVersion.isNullOrEmpty()
                        ) {
                            false -> downloadListByVersion
                            else -> downloadEntryList.filter {
                                    relativePath ->
                                val isNotExist =
                                    !File(cmdclickDefaultAppDirPath, relativePath).isFile
                                isNotExist
                            }
                        }
                        if(
                            downloadList.isNotEmpty()
                        ){
                            FileSystems.writeFile(
                                File(
                                    cmdclickUpdateFannelInfoSystemDirPath,
                                    CcPathTool.trimAllExtend(fannelName)
                                ).absolutePath,
                                String(),
                            )
                        }
                        channel.send(fannelName to downloadList)
                    }
                }
            }
            jobList.forEach { it.await() }
            channel.close()
        }
        val fanneNameToDownLoadListList: MutableList<Pair<String, List<String>>> = mutableListOf()
        for (fanneNameToDownLoadList in channel){
            fanneNameToDownLoadListList.add(fanneNameToDownLoadList)
        }
        return fanneNameToDownLoadListList
    }


    private fun makeDownloadListByVersion(
        context: Context?,
        fannelName: String,
        downloadEntryList: List<String>,
    ): List<String>? {
        val infoDirName = "info"
        val infoVersionDirName = "${infoDirName}/version"
        val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
        val versionRelativeDirPath = "${fannelDirName}/${infoVersionDirName}"
        val urlVersionFileRelativePath = downloadEntryList.firstOrNull {
            it.startsWith(versionRelativeDirPath)
        } ?: return null
        if(
            File(cmdclickDefaultAppDirPath, urlVersionFileRelativePath).isFile
        ) return null
        val escapeTsvRelativePath = "${fannelDirName}/${infoDirName}/escape.tsv"
        val escapeRelativeUrlPathList = makeEscapeRelativeUrlPathList(
            context,
            escapeTsvRelativePath,
            File(cmdclickDefaultAppDirPath, escapeTsvRelativePath).absolutePath,
        )
        return downloadEntryList.filter {
                downloadRelativePath ->
            !escapeRelativeUrlPathList.contains(downloadRelativePath)
        }
    }

    private object FannelSettingMapMaker {

        private val switchOn = FannelManageAdapter.switchOn
        suspend fun make(
            context: Context?,
            fannelNameList: List<String>
        ): List<String> {
            val semaphore = Semaphore(concurrencyLimit)
            val channel = Channel<String>(fannelNameList.size)
            val fannelInfoMapList = mutableListOf<String>()
            withContext(Dispatchers.IO) {
                val jobList = fannelNameList.map { fannelName ->
                    async {
                        semaphore.withPermit {
                            val repValsMap =
                                SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                                    context,
                                    File(cmdclickDefaultAppDirPath, fannelName).absolutePath
                                )
                            val settingVariableList =
                                CommandClickVariables.extractValListFromHolder(
                                    CommandClickVariables.makeMainFannelConList(
                                        fannelName,
                                        repValsMap
                                    ),
                                    settingSectionStart,
                                    settingSectionEnd
                                )
                            val enableLongPressButtonKeyValue = makeLongPressEnableKeyCon(
                                fannelName,
                                settingVariableList,
                                repValsMap
                            )
                            val enableEditExecuteButtonKeyValue = makeEditExecuteEnableKeyCon(
                                settingVariableList
                            )
                            val enableEditSettingVals = makeEditSettingValsEnableKeyCon(
                                settingVariableList
                            )
                            val fannelTitleKeyValue = makeTitleKeyValue(
                                fannelName
                            )
                            val fannelInfoValueCon = listOf(
                                enableEditExecuteButtonKeyValue,
                                enableEditSettingVals,
                                enableLongPressButtonKeyValue,
                                fannelTitleKeyValue,
                            ).joinToString(FannelManageAdapter.keySeparator.toString())
                            channel.send("${fannelName}\t${fannelInfoValueCon}")

                        }
                    }
                }
                jobList.forEach { it.await() }
                channel.close()
                for (fannelInfoMapLine in channel) {
                    fannelInfoMapList.add(fannelInfoMapLine)
                }
            }
            return fannelInfoMapList
        }

        private fun makeEditExecuteEnableKeyCon(
            settingVariableList: List<String>?
        ): String {
            val isEditExecute = SettingVariableReader.getCbValue(
                settingVariableList,
                CommandClickScriptVariable.EDIT_EXECUTE,
                CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE,
                String(),
                CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE,
                SettingVariableSelects.EditExecuteSelects.values().map { it.name },
            ) == SettingVariableSelects.EditExecuteSelects.ALWAYS.name
            val enableEditExecuteButtonValue = when (isEditExecute) {
                false -> String()
                else -> switchOn
            }
            return listOf(
                FannelManageAdapter.Companion.FannelHistorySettingKey.ENABLE_EDIT_EXECUTE.key,
                enableEditExecuteButtonValue,
            ).joinToString("=")
        }

        private fun makeEditSettingValsEnableKeyCon(
            settingVariableList: List<String>?
        ): String {
            val enableEditSettingVals = SettingVariableReader.getCbValue(
                settingVariableList,
                CommandClickScriptVariable.DISABLE_SETTING_VALS_EDIT,
                SettingVariableSelects.DisableSettingValsEdit.OFF.name,
                String(),
                SettingVariableSelects.DisableSettingValsEdit.OFF.name,
                SettingVariableSelects.DisableSettingValsEdit.values().map { it.name },
            ) != SettingVariableSelects.DisableSettingValsEdit.ON.name
            val enableEditSettingValsValue = when (enableEditSettingVals) {
                false -> String()
                else -> switchOn
            }
            return listOf(
                FannelManageAdapter.Companion.FannelHistorySettingKey.ENABLE_EDIT_SETTING_VALS.key,
                enableEditSettingValsValue,
            ).joinToString("=")
        }

        private fun makeLongPressEnableKeyCon(
            fannelName: String,
            settingVariableList: List<String>?,
            repValsMap: Map<String, String>?
        ): String {
            val longPressInfoMapPath = UsePath.longPressInfoMapPath
            val longPressInfoMapCon = SetReplaceVariabler.execReplaceByReplaceVariables(
                ReadText(longPressInfoMapPath).readText(),
                repValsMap,
                fannelName
            )
            val longPressInfoMap = CmdClickMap.createMap(
                longPressInfoMapCon,
                '\n'
            ).toMap()
            val isDisable = longPressInfoMap.get(
                LongPressMenuTool.LongPressKey.DISABLE.key
            ) == LongPressMenuTool.longPressDisableOn
            val longPressJsPathSettingValsList = listOf(
                CommandClickScriptVariable.IMAGE_LONG_PRESS_JS_PATH,
                CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_JS_PATH,
                CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_JS_PATH,
            )
            val isNotJsPath = longPressJsPathSettingValsList.firstOrNull { valName ->
                val jsPath = SettingVariableReader.getStrValue(
                    settingVariableList,
                    valName,
                    String(),
                )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "down_longpress.txt").absolutePath,
//                    listOf(
//                        "jsPath: ${jsPath}",
//                        "settingVariableList: ${settingVariableList}",
//                        "fannelName: ${fannelName}",
//                    ).joinToString("\n") + "\n----------\n"
//                )
                File(jsPath).isFile
                        || jsPath == switchOn
            }.isNullOrEmpty()
            val enableLongPressButtonValue = when (isDisable || isNotJsPath) {
                true -> String()
                else -> switchOn
            }
            return listOf(
                FannelManageAdapter.Companion.FannelHistorySettingKey.ENABLE_LONG_PRESS_BUTTON.key,
                enableLongPressButtonValue,
            ).joinToString("=")
        }


        private fun makeTitleKeyValue(
            fannelName: String
        ): String {
            val descCon = extractDescription(
                fannelName
            )
            return listOf(
                FannelManageAdapter.Companion.FannelHistorySettingKey.TITLE.key,
                descCon
            ).joinToString("=")

        }

        private fun extractDescription(
            fannelName: String
        ): String {
            val fannelConList = ReadText(
                File(cmdclickDefaultAppDirPath, fannelName).absolutePath,
            ).textToList()
            val descConSrc = ScriptFileDescription.makeDescriptionContents(
                fannelConList,
                fannelName
            )
            val readmeUrl = ScriptFileDescription.getReadmeUrl(descConSrc)
            val descCon = when(readmeUrl.isNullOrEmpty()){
                true
                -> descConSrc
                else
                -> {
                    val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
                    val readmePath = listOf(
                        cmdclickDefaultAppDirPath,
                        fannelDirName,
                        UsePath.fannelReadmeName
                    ).joinToString("/")
                    ReadText(readmePath).readText()
//                    CurlManager.get(
//                        context,
//                        FannelListVariable.makeReadmeRawUrl(readmeUrl),
//                        String(),
//                        String(),
//                        2000
//                    ).let {
//                        val isConnOk = CurlManager.isConnOk(it)
//                        if (!isConnOk) return@let String()
//                        String(it)
//                    }
                }
            }
            val firstDescriptionLineRange = 50
            val descFirstLineSource =
                descCon.split('\n').take(firstDescriptionLineRange).firstOrNull {
                    val trimLine = it.trim()
                    val isLetter =
                        trimLine.firstOrNull()?.isLetter()
                            ?: false
                    isLetter && trimLine.isNotEmpty()
                }
            val descFirstLine = when(
                !descFirstLineSource.isNullOrEmpty()
                        && descFirstLineSource.length > FannelListVariable.descriptionFirstLineLimit
            ) {
                true -> descFirstLineSource.substring(0, FannelListVariable.descriptionFirstLineLimit)
                else -> descFirstLineSource
            }
            return descFirstLine?.replace(
                FannelManageAdapter.keySeparator.toString(),
                " "
            )?.trim() ?: String()
        }
    }


    private fun makeEscapeRelativeUrlPathList(
        context: Context?,
        urlEscapeTsvPath: String,
        curEscapeTsvPath: String,
    ): List<String> {
//        val debug = File(UsePath.cmdclickDefaultAppDirPath, "down_escape.txt").absolutePath
//        FileSystems.writeFile(
//            debug,
//            listOf(
//                "escape download",
//                "urlEscapeTsvPath: ${urlEscapeTsvPath}"
//            ).joinToString("\n")
//        )
        return CurlManager.get(
            context,
            "${UrlFileSystems.gitUserContentFannelPrefix}/${urlEscapeTsvPath}",
            String(),
            String(),
            2_000,
        ).let { conByteArray ->
            if (
                !CurlManager.isConnOk(conByteArray)
            ) return@let String()
//            FileSystems.updateFile(
//                debug,
//                listOf(
//                    "escape ${String(conByteArray)}"
//                ).joinToString("\n")
//            )
            String(conByteArray)
        }.split("\n").map { line ->
            val trimLine = line.trim()
            if (
                trimLine.isEmpty()
            ) return@map String()
            val relativePathAndVersion =
                trimLine.split("\t")
            val relativePath = relativePathAndVersion
                .firstOrNull()
                ?.trim()
                ?: String()
            val urlsVersion = relativePathAndVersion.getOrNull(1)
            val curVersion = TsvTool.getKeyValue(
                curEscapeTsvPath,
                relativePath
            ).let { QuoteTool.trimBothEdgeQuote(it) }
//            FileSystems.updateFile(
//                debug,
//                listOf(
//                    "trimLine ${trimLine}",
//                    "relativePathAndVersion ${relativePathAndVersion}",
//                    "urlsVersion ${urlsVersion}",
//                    "curVersion ${curVersion}",
//                ).joinToString("\n")
//            )
            val isEscape = urlsVersion.isNullOrEmpty()
                    || urlsVersion == curVersion
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "escape_inner.txt").absolutePath,
//                listOf(
//                    "relativePathAndVersion: ${relativePathAndVersion}",
//                    "curEscapeTsvPath: ${curEscapeTsvPath}",
//                    "assetsVersion: ${assetsVersion}",
//                    "curVersion: ${curVersion}",
//                    "isEscape: ${isEscape}",
//                ).joinToString("\n\n") + "\n----\n"
//            )
            when (isEscape) {
                false -> String()
                else -> relativePath
            }
        }.filter {
            it.isNotEmpty()
        }
    }
}