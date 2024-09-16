package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File

object LongPressMenuTool {

    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

    fun makeExecJsPath(
        terminalFragment: TerminalFragment,
        selectedScriptNameOrPath: File,
    ): String {
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val currentFannelName = makeCurrentFannelName(
            terminalFragment,
        )
        val isScriptName = selectedScriptNameOrPath.parent.isNullOrEmpty()
        return when(
            isScriptName
        ) {
            true
            -> "${cmdclickDefaultAppDirPath}/${selectedScriptNameOrPath.name}"

            else
            -> ScriptPreWordReplacer.replace(
                selectedScriptNameOrPath.absolutePath,
                currentFannelName,
            )
        }
    }



    object LongPressInfoMapList {

        fun extractTitleIconOathList(
            longPressMenuMapList: List<Map<LongPressKey, String>>
        ): List<Pair<String, String>>{
            return longPressMenuMapList.map {
                val title = it.get(LongPressKey.TITLE) ?: String()
                val iconPath = it.get(LongPressKey.ICON_PATH) ?: String()
                title to iconPath
            }
        }
        fun makeMenuMapList(
            context: Context?,
            longPressScriptList: List<String>,
            longPressType: LongPressType,
            linkUrlList: List<String>,
        ): List<Map<LongPressKey, String>> {
            val indexTolongPressInfoMapList: MutableList<Pair<Int, Map<LongPressKey, String>>> =
                mutableListOf()
            val semaphore = Semaphore(5)
            val channel = Channel<Pair<Int, Map<LongPressKey, String>>>(longPressScriptList.size)
            runBlocking {
                val jobList =
                    longPressScriptList.mapIndexed {
                            index, fannelNameOrOriginalLongPressInfoPath ->
                            async {
                                semaphore.withPermit {
                            val isFannelName =
                                File(
                                    cmdclickDefaultAppDirPath,
                                    fannelNameOrOriginalLongPressInfoPath
                                ).isFile
                            val curLongPressFannelName = when (isFannelName) {
                                true -> fannelNameOrOriginalLongPressInfoPath
                                else -> if (
                                    File(fannelNameOrOriginalLongPressInfoPath).isFile
                                ) CcPathTool.getMainFannelFilePath(
                                    fannelNameOrOriginalLongPressInfoPath
                                )
                                    .let {
                                        File(it).name
                                    } else return@withPermit
                            }
                            val curLongPressFannelPath =
                                File(cmdclickDefaultAppDirPath, curLongPressFannelName).absolutePath
                            val fannelConList = ReadText(
                                curLongPressFannelPath
                            ).textToList()
                            val settingVariableListSrc =
                                CommandClickVariables.extractValListFromHolder(
                                    fannelConList,
                                    CommandClickScriptVariable.SETTING_SEC_START,
                                    CommandClickScriptVariable.SETTING_SEC_END,
                                ) ?: emptyList()

                            val setReplaceVariables = SetReplaceVariabler.makeSetReplaceVariableMap(
                                context,
                                settingVariableListSrc,
                                curLongPressFannelName
                            )

                            val settingVariableList =
                                SetReplaceVariabler.execReplaceByReplaceVariables(
                                    settingVariableListSrc.joinToString("\n"),
                                    setReplaceVariables,
                                    fannelNameOrOriginalLongPressInfoPath
                                ).split("\n")
                            val longPressInfoMap = when (isFannelName) {
                                true -> {
                                    val longPressInfoMapSrc = ScriptPreWordReplacer.replace(
                                        longPressInfoMapPath,
                                        curLongPressFannelName,
                                    ).let {
                                        val longPressInfoMapCon =
                                            SettingFile.read(
                                                it,
                                                curLongPressFannelPath,
                                                setReplaceVariables,
                                                false,
                                            )
//                                            SetReplaceVariabler.execReplaceByReplaceVariables(
//                                                ReadText(it).readText(),
//                                                setReplaceVariables,
//                                                curLongPressFannelName
//                                            )
                                        CmdClickMap.createMap(
                                            longPressInfoMapCon,
                                            longPressInfoMapSeparator
                                        ).toMap()
                                    }
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "longpressMapInner.txt").absolutePath,
//                            listOf(
//                                "fannelNameOrOriginalLongPressInfoPath: ${fannelNameOrOriginalLongPressInfoPath}",
//                                "isFannelName: ${isFannelName}",
//                                "curLongPressFannelName: ${curLongPressFannelName}",
//                                "settingVariableList: ${settingVariableList}",
//                                "setReplaceVariables: ${setReplaceVariables}",
//                                "linkUrlList: ${linkUrlList}",
//                                "curLongPressFannelName: ${curLongPressFannelName}",
//                                "longPressInfoMapPath: ${ScriptPreWordReplacer.replace(
//                                    longPressInfoMapPath,
//                                    curLongPressFannelName,
//                                )}",
//                                "longPressInfoMapSrc: ${longPressInfoMapSrc}",
//                            ).joinToString("\n")
//                        )
                                    execMakeLongPressMap(
                                        longPressInfoMapSrc,
                                        fannelNameOrOriginalLongPressInfoPath,
                                        longPressType,
                                        linkUrlList,
                                        curLongPressFannelName,
                                        settingVariableList,
                                    )
                                }

                                else -> {
                                    val longPressInfoMapSrc = let {
                                        val longPressInfoMapCon =
                                            SettingFile.read(
                                                fannelNameOrOriginalLongPressInfoPath,
                                                curLongPressFannelPath,
                                                setReplaceVariables,
                                                false,
                                            )
//                                            SetReplaceVariabler.execReplaceByReplaceVariables(
//                                                ReadText(fannelNameOrOriginalLongPressInfoPath).readText(),
//                                                setReplaceVariables,
//                                                curLongPressFannelName
//                                            )
                                        CmdClickMap.createMap(
                                            longPressInfoMapCon,
                                            longPressInfoMapSeparator
                                        ).toMap()
                                    }
//                        FileSystems.updateFile(
//                            File(UsePath.cmdclickDefaultAppDirPath, "longpressMapInnerByInfo.txt").absolutePath,
//                            listOf(
//                                "fannelNameOrOriginalLongPressInfoPath: ${fannelNameOrOriginalLongPressInfoPath}",
//                                "isFannelName: ${isFannelName}",
//                                "curLongPressFannelName: ${curLongPressFannelName}",
//                                "settingVariableList: ${settingVariableList}",
//                                "setReplaceVariables: ${setReplaceVariables}",
//                                "linkUrlList: ${linkUrlList}",
//                                "curLongPressFannelName: ${curLongPressFannelName}",
//                                "longPressInfoMapPath: ${ScriptPreWordReplacer.replace(
//                                    longPressInfoMapPath,
//                                    curLongPressFannelName,
//                                )}",
//                                "longPressInfoMapSrc: ${longPressInfoMapSrc}",
//                            ).joinToString("\n")
//                        )
                                    if (
                                        longPressInfoMapSrc.get(LongPressKey.JS_PATH.key)
                                            .isNullOrEmpty()
                                    ) return@withPermit
                                    execMakeLongPressMap(
                                        longPressInfoMapSrc,
                                        fannelNameOrOriginalLongPressInfoPath,
                                        longPressType,
                                        linkUrlList,
                                        curLongPressFannelName,
                                        settingVariableList,
                                    )
                                }
                            }
                            channel.send(index to longPressInfoMap)
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "longpressMap.txt").absolutePath,
//                    listOf(
//                        "fannelNameOrOriginalLongPressInfoPath: ${fannelNameOrOriginalLongPressInfoPath}",
//                        "isFannelName: ${isFannelName}",
//                        "curLongPressFannelName: ${curLongPressFannelName}",
//                        "settingVariableList: ${settingVariableList}",
//                        "setReplaceVariables: ${setReplaceVariables}",
//                        "linkUrlList: ${linkUrlList}",
//                        "longPressInfoMap: ${longPressInfoMap}",
//                    ).joinToString("\n")
//                )
//                            longPressInfoMap
                        }
//                                    .filter { it.isNotEmpty() }
                    }
                }
                jobList.forEach { it.await() }
                channel.close()
                for(indexToLongPressInfoMap in channel){
                    if(
                        indexToLongPressInfoMap.second.isEmpty()
                    ) continue
                    indexTolongPressInfoMapList.add(indexToLongPressInfoMap)
                }
            }
            indexTolongPressInfoMapList.sortBy { it.first }
            return indexTolongPressInfoMapList.map {
                it.second
            }
        }

        private fun execMakeLongPressMap(
            longPressInfoMapSrc: Map<String, String>,
            fannelNameOrOriginalLongPressInfoPath: String,
            longPressType: LongPressType,
            linkUrlList: List<String>,
            fannelName: String,
            settingVariableList: List<String>,
        ): Map<LongPressKey, String> {
            val disable =
                longPressInfoMapSrc.get(LongPressKey.DISABLE.key) == longPressDisableOn
            if(
                disable
            ) return emptyMap()
            val isTrigger = longPressInfoMapSrc.get(LongPressKey.TRIGGER_WORDS.key).let {
                triggerWord ->
                   when(
                       triggerWord.isNullOrEmpty()
                   ) {
                       true -> true
                       else -> {
                           val triggerWordList = triggerWord.split("&")
                           linkUrlList.any {
                               url ->
                               triggerWordList.any {
                                   triggerWord ->
                                   url.contains(triggerWord)
                               }
                           }
                       }
                   }
            }
            if(
                !isTrigger
            ) return emptyMap()

            val title = longPressInfoMapSrc.get(LongPressKey.TITLE.key).let {
                if(
                    it.isNullOrEmpty()
                ) return@let fannelNameOrOriginalLongPressInfoPath
                it
             }
            val iconPathSrc = longPressInfoMapSrc.get(LongPressKey.ICON_PATH.key)
            val iconPath = when(
                iconPathSrc.isNullOrEmpty()
                        || !File(iconPathSrc).isFile
            ){
                false -> iconPathSrc
                else -> {
                    val fannelLogoPngFile = ScriptPreWordReplacer.replace(
                        UsePath.fannelLogoPngPath,
                        fannelName,
                    ).let { File(it) }
                    if(fannelLogoPngFile.isFile) fannelLogoPngFile.absolutePath
                    else String()
                }
            }
            val jsPathSrc = longPressInfoMapSrc.get(LongPressKey.JS_PATH.key)
            val jsPath = when (
                jsPathSrc.isNullOrEmpty()
            ) {
                false -> jsPathSrc
                else -> {
                    val settingValName = when (longPressType) {
                        LongPressType.IMAGE -> CommandClickScriptVariable.IMAGE_LONG_PRESS_JS_PATH
                        LongPressType.SRC_ANCHOR -> CommandClickScriptVariable.SRC_ANCHOR_LONG_PRESS_JS_PATH
                        LongPressType.SRC_IMAGE_ANCHOR ->  CommandClickScriptVariable.SRC_IMAGE_ANCHOR_LONG_PRESS_JS_PATH
                    }
                    SettingVariableReader.getStrValue(
                        settingVariableList,
                        settingValName,
                        File(cmdclickDefaultAppDirPath, fannelName).absolutePath,
                    )
                }
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "longPressMapEec.txt").absolutePath,
//                listOf(
//                    "longPressInfoMapSrc: ${longPressInfoMapSrc}",
//                    "isTrigger: ${isTrigger}",
//                    "title: ${title}",
//                    "iconPath: ${iconPath}",
//                    "jsPath: ${jsPath}",
//                ).joinToString("\n")
//            )
            if(
                !File(jsPath).isFile
            ) return emptyMap()
            return mapOf(
                LongPressKey.TITLE to title,
                LongPressKey.JS_PATH to jsPath,
                LongPressKey.ICON_PATH to iconPath,
            )
        }
    }

    fun extractSettingValList(
        context: Context?,
        srcFannelPath: String,
    ): List<String>? {
        val repValMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            srcFannelPath,
        )
//        val longPressFannelMainAppDirPath = CcPathTool.getMainAppDirPath(srcFannelPath)
        val longPressFannelName =  File(srcFannelPath).name
        return CommandClickVariables.returnSettingVariableList(
            ReadText(srcFannelPath).textToList(),
//            LanguageTypeSelects.JAVA_SCRIPT,
        )?.joinToString("\n")?.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                repValMap,
//                longPressFannelMainAppDirPath,
                longPressFannelName
            )
        }?.split("\n")
    }


    fun makeLongPressScriptList(
        terminalFragment: TerminalFragment,
        longPressMenuDirPath: String,
        longPressMenuName: String,
    ): List<String> {
        val context = terminalFragment.context
        val longPressMenuFilePath =  File(longPressMenuDirPath, longPressMenuName).absolutePath
        val mainFannelPath = CcPathTool.getMainFannelFilePath(
            longPressMenuFilePath
        )
        val mainFannelPathObj = File(mainFannelPath)
//        val currentAppDirPath = mainFannelPathObj.parent ?: String()
        val currentFannelName = mainFannelPathObj.name
        val repValMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            longPressMenuFilePath
        )
        val srcLongPressListConSrc = ReadText(longPressMenuFilePath).readText()
        val srcLongPressListCon = srcLongPressListConSrc.split("\n").map {
            QuoteTool.trimBothEdgeQuote(it)
        }.joinToString("\n")
        return SetReplaceVariabler.execReplaceByReplaceVariables(
            srcLongPressListCon,
            repValMap,
//            currentAppDirPath,
            currentFannelName
        ).split("\n")
    }

    private fun makeCurrentFannelName(
        terminalFragment: TerminalFragment,
    ): String {
        val context = terminalFragment.context
            ?: return String()
        val editExecuteFragmentTag = context.getString(R.string.edit_terminal_fragment)
        return when(
            terminalFragment.tag == editExecuteFragmentTag
        ){
            true -> {
                val sharePref = FannelInfoTool.getSharePref(context)
                FannelInfoTool.getStringFromFannelInfo(
                    sharePref,
                    FannelInfoSetting.current_fannel_name
                )
            }
            else -> {
                String()
            }

        }
    }

    fun removeAll(
        fannelName: String
    ){
        val preference = SystemFannel.preference
        listOf(
            UsePath.imageLongPressMenuFilePath,
            UsePath.srcAnchorLongPressMenuFilePath,
            UsePath.srcImageAnchorLongPressMenuFilePath,
        ).joinToString("\n").let {
            ScriptPreWordReplacer.replace(
                it,
                preference
            )
        }.split("\n").forEach {
            longPressMenuPath ->
            val longPressMenuFannelList =
                ReadText(longPressMenuPath).textToList()
            val removedLongPressFannelMenuList =
                longPressMenuFannelList.filter {
                    it != fannelName
                }.joinToString("\n")
            FileSystems.writeFile(
                longPressMenuPath,
                removedLongPressFannelMenuList
            )
        }
    }

    enum class LongPressType{
        SRC_ANCHOR,
        SRC_IMAGE_ANCHOR,
        IMAGE,
    }

    enum class LongPressKey(val key: String){
        TITLE("title"),
        TRIGGER_WORDS("triggerWords"),
        DISABLE("disable"),
        JS_PATH("jsPath"),
        ICON_PATH("iconPath"),
    }



    const val longPressDisableOn = "ON"

    val longPressInfoMapPath = "${UsePath.fannelSettingsDirPath}/longPressInfoMap.txt"
    val longPressInfoMapSeparator = ','
}