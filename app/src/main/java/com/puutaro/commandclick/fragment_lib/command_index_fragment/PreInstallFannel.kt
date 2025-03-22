package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForCmdIndex
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press.LongPressMenuTool
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryPath
import com.puutaro.commandclick.proccess.pin.PinFannelManager
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.Intent.CurlManager
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.file.UrlFileSystems
import com.puutaro.commandclick.util.gz.GzTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FannelSettingMap
import com.puutaro.commandclick.util.str.AltRegexTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.File

object PreInstallFannel {

    private var preInstallFannelCreateJob: Job? = null

    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
    private val cmdclickUpdateFannelInfoSystemDirPath = UsePath.cmdclickUpdateFannelInfoSystemDirPath
    private const val concurrencyLimit = 5
    private val infoDirName = "info"
    private val infoVersionDirName = "${infoDirName}/version"

    fun exit(){
        preInstallFannelCreateJob?.cancel()
    }

    fun install(
        cmdIndexFragment: CommandIndexFragment,
    ){
        val context = cmdIndexFragment.context
//        exit()
        preInstallFannelCreateJob = cmdIndexFragment.lifecycleScope.launch {
            cmdIndexFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(500)
                withContext(Dispatchers.IO){
                    FileSystems.removeAndCreateDir(
                        cmdclickUpdateFannelInfoSystemDirPath
                    )
                }
                val fannelList = withContext(Dispatchers.IO) {
                    UrlFileSystems.getFannelList(context)
                        .split("\n")
                }
                val fannelNameList = withContext(Dispatchers.IO) {
                    UrlFileSystems.extractFannelNameList(fannelList)
                }
                val fannelNameToDownloadListByVersion = withContext(Dispatchers.IO) {
                    DownloadByVersion.download(
                        context,
                        fannelNameList,
                        fannelList
                    )
                }
//                withContext(Dispatchers.IO){
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "downloadList.txt").absolutePath,
//                        listOf(
//                            "fannelNameToDownloadListByVersion: ${fannelNameToDownloadListByVersion}",
//                            preInstallFannelCreateJob?.isActive.toString()
//                        ).joinToString("\n"),
//                    )
//                }
                val fannelNameToDownloadListByNotExist = withContext(Dispatchers.IO) {
                    makeDownloadListByNotExitPathByFannelName(
                        fannelNameList,
                        fannelList,
                    )
                }
                withContext(Dispatchers.IO) {
                    execDownload(
                        context,
                        fannelNameToDownloadListByNotExist
                    )
                }
                val fannelNameToDownloadList = withContext(Dispatchers.IO){
                    val fannelNameToDownloadListByVersionMap =
                        fannelNameToDownloadListByVersion.toMap()
                    val fannelNameToDownloadListByNotExistMap =
                        fannelNameToDownloadListByNotExist.toMap()
                    fannelNameList.map {
                        fannelName ->
                        val fileListByVersion = fannelNameToDownloadListByVersionMap.get(fannelName)
                            ?: emptyList()
                        val fileListByNotExist = fannelNameToDownloadListByNotExistMap.get(fannelName)
                            ?: emptyList()
                        val fileListSrc = fileListByVersion + fileListByNotExist
                        fannelName to fileListSrc.sorted().distinct()
                    }
                }
//                withContext(Dispatchers.IO) {
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "downloadList.txt").absolutePath,
//                        listOf(
//                            "fannelNameToDownloadListByVersion: ${fannelNameToDownloadListByVersion}",
//                            "fannelNameToDownloadListByNotExist: ${fannelNameToDownloadListByNotExist}",
//                            "fannelRawNameToDownloadList: ${fannelNameToDownloadList}",
//                            preInstallFannelCreateJob?.isActive.toString()
//                        ).joinToString("\n"),
//                    )
//                }
//                withContext(Dispatchers.Main){
//                    ToastUtils.showShort(
//                        preInstallFannelCreateJob?.isActive.toString()
//                    )
//                }
                withContext(Dispatchers.IO) {
                    BroadcastSender.normalSend(
                        context,
                        BroadCastIntentSchemeForCmdIndex.UPDATE_INDEX_FANNEL_LIST.action
                    )
                }
                val preInstallFannelListTsvPath = UsePath.preInstallFannelListTsvPath
                val fannelSettingMapTsvPath = FannelSettingMap.fannelSettingMapTsvPath
                withContext(Dispatchers.IO) {
                    val isNotUpdateFannelInfo = FileSystems.sortedFiles(
                        cmdclickUpdateFannelInfoSystemDirPath
                    ).isEmpty()
                            && !File(fannelSettingMapTsvPath).isFile
                            && !File(preInstallFannelListTsvPath).isFile
                    if (
                        isNotUpdateFannelInfo
                    ) return@withContext
                    val jsFileSuffix = UsePath.JS_FILE_SUFFIX
                    val existFannelList = FileSystems.sortedFiles(cmdclickDefaultAppDirPath).filter {
                        it.endsWith(jsFileSuffix)
                    }
                    val fannelSettingMapList = FannelSettingMapMaker.make(
                        context,
                        existFannelList
//                        fannelNameToDownloadList.map { it.first }
                    )
                    FileSystems.writeFile(
                        fannelSettingMapTsvPath,
                        fannelSettingMapList.joinToString("\n")
                    )
                    FileSystems.removeAndCreateDir(cmdclickUpdateFannelInfoSystemDirPath)
                }
                withContext(Dispatchers.IO){
                    PinFannelManager.saveForPreInstall(context)
                    PinFannelManager.updateBroadcast(context)
                }
                withContext(Dispatchers.IO) {
                    val preInstallFannelList = listOf(SystemFannel.home) + fannelNameList
                    FileSystems.writeFile(
                        preInstallFannelListTsvPath,
                        preInstallFannelList.joinToString("\n")
                    )
                }
            }
        }
    }

    private suspend fun execDownload(
        context: Context?,
        fannelNameToDownloadList: List<Pair<String, List<String>>>
    ){
        val semaphore = Semaphore(concurrencyLimit)
        withContext(Dispatchers.IO) {
            val jobList = fannelNameToDownloadList.map {
//                        if(
//                            preInstallFannelCreateJob?.isActive != true
//                        ) return@withContext
                async {
//                            if(
//                                preInstallFannelCreateJob?.isActive != true
//                            ) return@async
                    semaphore.withPermit {
//                                if(
//                                    preInstallFannelCreateJob?.isActive != true
//                                ) return@withPermit
                        val fannelName = it.first
                        val downloadList = it.second
                        createFileByOverride(
                            context,
                            fannelName,
                            downloadList
                        )
                    }
                }
            }
            jobList.forEach { it.await() }
        }
    }
    fun isPreinstallFannel(
        fannelName: String
    ): Boolean {
        return ReadText(
            UsePath.preInstallFannelListTsvPath
        ).textToList().contains(fannelName)
    }

    private suspend fun makeDownloadListByNotExitPathByFannelName(
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
                        val downloadList = downloadEntryList.filter {
                                relativePath ->
                            val isNotExist =
                                !File(cmdclickDefaultAppDirPath, relativePath).isFile
                            isNotExist
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

    object DownloadByVersion {

        private val preInstallDir = File(UsePath.cmdclickTempSystemDirPath, "preInstall")
        private val preInstallTarGzDir = File(preInstallDir.absolutePath, "tarGz")
        private val preInstallFannelDir = File(preInstallDir.absolutePath, "fannel")
        private val tarGzExtend = ".tar.gz"
        private val debugDirPath = File(UsePath.cmdclickDefaultAppDirPath, "debug").absolutePath

        suspend fun download(
            context: Context?,
            fannelNameList: List<String>,
            fannelList: List<String>,
        ): List<Pair<String, List<String>>> {
            FileSystems.removeAndCreateDir(
                preInstallDir.absolutePath
            )
            val downloadFannelNameList = makeDownLoadFannelNameList(
                fannelNameList,
                fannelList
            )
//            FileSystems.writeFile(
//                File(debugDirPath, "downloadList_in0.txt").absolutePath,
//                listOf(
//                    "downloadFannelNameList: ${downloadFannelNameList}",
//                    "${LocalDateTime.now()}",
//                    preInstallFannelCreateJob?.isActive.toString()
//                ).joinToString("\n"),
//            )
            downloadToTempDir(
                context,
                downloadFannelNameList
            )
//            FileSystems.writeFile(
//                File(debugDirPath, "downloadList_in1.txt").absolutePath,
//                listOf(
//                    "downloadToTempDir: ",
//                    "${LocalDateTime.now()}",
//                    preInstallFannelCreateJob?.isActive.toString()
//                ).joinToString("\n"),
//            )
            extractTarGz()
//            FileSystems.writeFile(
//                File(debugDirPath, "downloadList_in2.txt").absolutePath,
//                listOf(
//                    "extractTarGz: ",
//                    "${LocalDateTime.now()}",
//                    preInstallFannelCreateJob?.isActive.toString()
//                ).joinToString("\n"),
//            )
            val copyFannelNameToRelativePathPairList = downloadFannelNameList.map {
                it to makeCopyList(
                    it,
                    fannelList,
                )
            }
//            FileSystems.writeFile(
//                File(debugDirPath, "downloadList_in3.txt").absolutePath,
//                listOf(
//                    "${LocalDateTime.now()}",
//                    preInstallFannelCreateJob?.isActive.toString()
//                ).joinToString("\n"),
//            )
            copyFannelFile(
                copyFannelNameToRelativePathPairList
            )
//            FileSystems.writeFile(
//                File(debugDirPath, "downloadList_in4.txt").absolutePath,
//                listOf(
//                    "${LocalDateTime.now()}",
//                    preInstallFannelCreateJob?.isActive.toString()
//                ).joinToString("\n"),
//            )
            FileSystems.writeFile(
                File(
                    cmdclickUpdateFannelInfoSystemDirPath,
                    "version.txt"
                ).absolutePath,
                String(),
            )
            FileSystems.removeDir(
                preInstallDir.absolutePath
            )
            return copyFannelNameToRelativePathPairList
        }

        private suspend fun extractTarGz(){
            val semaphore = Semaphore(concurrencyLimit)
            withContext(Dispatchers.IO) {
                val jobList = FileSystems.sortedFiles(
                    preInstallTarGzDir.absolutePath
                ).filter {
                    it.endsWith(tarGzExtend)
                }.map {
                    async {
                        semaphore.withPermit {
                            val tarGzPath = File(preInstallTarGzDir.absolutePath, it)
                            GzTool.extractTarWithoutOwnership(
                                tarGzPath.absolutePath,
                                preInstallFannelDir.absolutePath,
                                true
                            )
                        }
                    }
                }
                jobList.forEach { it.await() }
            }
//            FileSystems.removeDir(preInstallTarGzDir.absolutePath)

        }

        private suspend fun downloadToTempDir(
            context: Context?,
            downLoadFannelNameList: List<String>
        ){
            val gitUserContentFannelTarGzPrefix =
                UrlFileSystems.gitUserContentFannelTarGzPrefix
            val semaphore = Semaphore(concurrencyLimit)
            withContext(Dispatchers.IO) {
                downLoadFannelNameList.map {
                    async {
                        semaphore.withPermit {
                            val tarGzName = CcPathTool.makeFannelRawName(it) + tarGzExtend
                            val urlTarGzPath =
                                "${gitUserContentFannelTarGzPrefix}/${tarGzName}"
                            val downloadTarGzPath =
                                File(preInstallTarGzDir.absolutePath, tarGzName).absolutePath
                            val byteArray = CurlManager.get(
                                context,
                                urlTarGzPath,
                                String(),
                                String(),
                                5_000
                            )
                            if(
                                !CurlManager.isConnOk(byteArray)
                            ) return@withPermit
                            FileSystems.writeFromByteArray(
                                downloadTarGzPath,
                                byteArray,
                            )
                        }
                    }
                }
            }
        }

        private fun makeDownLoadFannelNameList(
            fannelNameList: List<String>,
            fannelList: List<String>,
        ): List<String> {
            return fannelNameList.filter {
                isDownloadByVersion(
                    it,
                    fannelList,
                )
            }
        }
        private fun isDownloadByVersion(
            fannelName: String,
            downloadEntryList: List<String>,
        ): Boolean {
            val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
            val versionRelativeDirPath = "${fannelDirName}/${infoVersionDirName}"
            val urlVersionFileRelativePath = downloadEntryList.firstOrNull {
                it.startsWith(versionRelativeDirPath)
            } ?: return false
            if (
                File(cmdclickDefaultAppDirPath, urlVersionFileRelativePath).isFile
            ) return false
            return true

        }

        private fun makeCopyList(
            fannelName: String,
            fannelList: List<String>
        ): List<String> {
            val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
            val escapeTsvRelativePath = "${fannelDirName}/${infoDirName}/escape.tsv"
            val escapeRelativeUrlPathList = makeEscapeRelativeUrlPathList(
                File(preInstallFannelDir.absolutePath, escapeTsvRelativePath).absolutePath,
                File(cmdclickDefaultAppDirPath, escapeTsvRelativePath).absolutePath,
            )
            return fannelList.filter {
                    relativePath ->
                val isFannelListByName =
                    UrlFileSystems.isFannelListByName(
                        relativePath,
                        fannelName
                    )
                if(
                    !isFannelListByName
                ) return@filter false
                val isNotEscape = !escapeRelativeUrlPathList.contains(relativePath)
                val isNotPartPng = !"/${relativePath}".contains(
                    "/${FannelHistoryPath.makePartPngDirCut()}/"
                )
//                FileSystems.updateFile(
//                    File(debugDirPath, "debug_cpy_list.txt").absolutePath,
//                    listOf(
//                        "relativePath: ${relativePath}",
//                        "isNotEscape: ${isNotEscape}",
//                        "isNotPartPng: ${isNotPartPng}",
//                        "escapeRelativeUrlPathList: ${escapeRelativeUrlPathList}",
//                    ).joinToString("\n")
//                )
                isNotEscape && isNotPartPng
            }.toList()
        }
        private suspend fun copyFannelFile(
            copyFannelNameToRelativePathPairList: List<Pair<String, List<String>>>
        ){
            val copyFannelRelativePathList = copyFannelNameToRelativePathPairList.map {
                it.second
            }.flatten()
            val semaphore = Semaphore(concurrencyLimit)
            withContext(Dispatchers.IO){
                val jobList = copyFannelRelativePathList.map {
                        copyFannelFilePath ->
                    async {
                        semaphore.withPermit {
                            FileSystems.copyFile(
                                File(preInstallFannelDir.absolutePath, copyFannelFilePath).absolutePath,
                                File(cmdclickDefaultAppDirPath, copyFannelFilePath).absolutePath,
                            )
                        }
                    }
                }
                jobList.forEach { it.await() }
            }
        }

        private fun makeEscapeRelativeUrlPathList(
            escapeTsvPath: String,
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


            //        CurlManager.get(
//            context,
//            "${fannel}/${escapeTsvRelativePath}",
//            String(),
//            String(),
//            2_000,
//        ).let { conByteArray ->
//            if (
//                !CurlManager.isConnOk(conByteArray)
//            ) return@let String()
////            FileSystems.updateFile(
////                debug,
////                listOf(
////                    "escape ${String(conByteArray)}"
////                ).joinToString("\n")
////            )
//            String(conByteArray)
//        }.split("\n")
            return ReadText(escapeTsvPath).textToList().map { line ->
                val trimLine = AltRegexTool.trim(line)
                if (
                    trimLine.isEmpty()
                ) return@map String()
                val relativePathAndVersion =
                    trimLine.split("\t")
                val relativePath =
                    relativePathAndVersion
                        .firstOrNull()
                        ?.let { AltRegexTool.trim(it) }
                    ?: String()
                val urlsVersion = relativePathAndVersion.getOrNull(1)
                val curVersion = TsvTool.getKeyValueFromFile(
                    curEscapeTsvPath,
                    relativePath
                ).let { QuoteTool.trimBothEdgeQuote(it) }
//                FileSystems.updateFile(
//                    File(debugDirPath, "escape_txt").absolutePath,
//                    listOf(
//                        "escapeTsvPath: ${escapeTsvPath}",
//                        "curEscapeTsvPath: ${curEscapeTsvPath}",
//                        "relativePath: ${relativePath}",
//                        "trimLine ${trimLine}",
//                        "relativePathAndVersion ${relativePathAndVersion}",
//                        "urlsVersion ${urlsVersion}",
//                        "curVersion ${curVersion}",
//                    ).joinToString("\n")
//                )
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

    private object FannelSettingMapMaker {

        private val switchOn = FannelSettingMap.switchOn
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
                                    CommandClickScriptVariable.SETTING_SEC_START,
                                    CommandClickScriptVariable.SETTING_SEC_END,
//                                    settingSectionStart,
//                                    settingSectionEnd
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
                            ).joinToString(FannelSettingMap.keySeparator.toString())
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
                SettingVariableSelects.EditExecuteSelects.entries.asSequence().map { it.name },
            ) == SettingVariableSelects.EditExecuteSelects.ALWAYS.name
            val enableEditExecuteButtonValue = when (isEditExecute) {
                false -> String()
                else -> switchOn
            }
            return listOf(
                FannelSettingMap.FannelHistorySettingKey.ENABLE_EDIT_EXECUTE.key,
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
                SettingVariableSelects.DisableSettingValsEdit.entries.asSequence().map { it.name },
            ) != SettingVariableSelects.DisableSettingValsEdit.ON.name
            val enableEditSettingValsValue = when (enableEditSettingVals) {
                false -> String()
                else -> switchOn
            }
            return listOf(
                FannelSettingMap.FannelHistorySettingKey.ENABLE_EDIT_SETTING_VALS.key,
                enableEditSettingValsValue,
            ).joinToString("=")
        }

        private fun makeLongPressEnableKeyCon(
            fannelName: String,
            settingVariableList: List<String>?,
            repValsMap: Map<String, String>?
        ): String {
            val longPressInfoMapPath = LongPressMenuTool.longPressInfoMapPath
            val longPressInfoMapCon = SetReplaceVariabler.execReplaceByReplaceVariables(
                ReadText(longPressInfoMapPath).readText(),
                repValsMap,
                fannelName
            )
            val longPressInfoMap = CmdClickMap.createMap(
                longPressInfoMapCon,
                LongPressMenuTool.longPressInfoMapSeparator
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
                FannelSettingMap.FannelHistorySettingKey.ENABLE_LONG_PRESS_BUTTON.key,
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
                FannelSettingMap.FannelHistorySettingKey.TITLE.key,
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
                fannelConList.asSequence(),
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
                FannelSettingMap.keySeparator.toString(),
                " "
            )?.trim() ?: String()
        }
    }

    private fun createFileByOverride(
        context: Context?,
        fannelName: String,
        fannelList: List<String>,
    ){

        fannelList.filter {
            UrlFileSystems.isFannelListByName(
                it,
                fannelName,
            )
        }.forEach {
            val destiFileObj = File("${cmdclickDefaultAppDirPath}/$it")
            if(
                destiFileObj.absolutePath.contains(
                    "/${FannelHistoryPath.makePartPngDirCut()}/"
                )
            ) return@forEach
            val downloadUrl = "${UrlFileSystems.gitUserContentFannelPrefix}/$it"
            val conByteArray = CurlManager.get(
                context,
                downloadUrl,
                String(),
                String(),
                2000,
            )
            if(
                !CurlManager.isConnOk(conByteArray)
            ) return@forEach
            FileSystems.writeFromByteArray(
                destiFileObj.absolutePath,
                conByteArray
            )
        }
    }
}