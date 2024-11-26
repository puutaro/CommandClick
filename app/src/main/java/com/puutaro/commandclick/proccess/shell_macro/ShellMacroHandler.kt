package com.puutaro.commandclick.proccess.shell_macro

import android.content.Context
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.JsAcAlterIfTool
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.SearchBoxSettingsForEditList
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.tsv.TsvTool
import java.io.File

object ShellMacroHandler {

    fun handle(
        context: Context?,
        busyboxExecutor: BusyboxExecutor?,
        shellPathOrMacro: String,
        setReplaceVariableMap: Map<String, String>?,
        extraRepValMap: Map<String, String>?,
    ): String {
        val macro = ShellMacro.entries.firstOrNull {
            it.name == shellPathOrMacro
        } ?: return getOutputByShell(
            busyboxExecutor,
            shellPathOrMacro,
            setReplaceVariableMap,
            extraRepValMap,
        ) ?: String()
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "jsAc_extraRepValMap00.txt").absolutePath,
//            listOf(
//                "shellPathOrMacro: ${shellPathOrMacro}",
//                "extraRepValMap: ${extraRepValMap}"
//            ).joinToString("\n\n")
//        )
        val concatRepValMap =
            (setReplaceVariableMap ?: mapOf()) +
                    (extraRepValMap ?: mapOf())
        return when(macro){
            ShellMacro.MAKE_HEADER_TITLE ->
                HeaderTitleMacro.get(concatRepValMap)
            ShellMacro.SAVE_PLAY_LIST -> {
                PlayListSaver.save(
                    concatRepValMap,
                )
                String()
            }
            ShellMacro.JUDGE_TSV_VALUE ->
                JudgeTsv.getStrByJudgeTsvValue(
                    context,
                    concatRepValMap
                )
            ShellMacro.JUDGE_LIST_DIR -> {
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "jsAc_extraRepValMap11.txt").absolutePath,
//                    listOf(
//                        "shellPathOrMacro: ${shellPathOrMacro}",
//                        "extraRepValMap: ${extraRepValMap}",
//                        "alterCon: ${extraRepValMap?.get("alterCon")}",
//                    ).joinToString("\n\n") + "\n----------\n"
//                )
                JudgeTsv.getStrByListDirValue(
                    context,
                    concatRepValMap
                )
            }

        }
    }

    private fun makeShellCon(
        shellPath: String,
        setReplaceVariableMap: Map<String, String>?,
        extraRepValMap: Map<String, String>?,
    ): String {
        val concatRepValMap =
            (setReplaceVariableMap ?: mapOf()) +
                    (extraRepValMap ?: mapOf())
        return execMakeShellCon(
            shellPath,
            concatRepValMap,
        )
    }

    object HeaderTitleMacro{
        private enum class ArgsKey(
            val key: String
        ){
            FANNEL_PATH("fannelPath"),
            CORE_TITLE("coreTitle"),
            EXTRA_TITLE("extraTitle"),
            BACKSTACK_COUNT(SearchBoxSettingsForEditList.backstackCountMarkForInsertEditText),
        }

        fun get(
            concatRepValMap: Map<String, String>
        ): String {
            val coreTitleSrc = ArgsManager.get(
                concatRepValMap,
                ArgsKey.CORE_TITLE.key
            )
            val coreTitle =
                when(coreTitleSrc.isNullOrEmpty()) {
                    false -> coreTitleSrc
                    else -> ArgsManager.get(
                        concatRepValMap,
                        ArgsKey.FANNEL_PATH.key
                    )?.let {
                        CcPathTool.trimAllExtend(
                            File(it).name
                        )
                    } ?: String()
            }
            val extraTitle = ArgsManager.get(
                concatRepValMap,
                ArgsKey.EXTRA_TITLE.key
            ) ?: String()
            val backstackCount =
                concatRepValMap.get(ArgsKey.BACKSTACK_COUNT.key)
                    ?: String()
            return listOf(
                backstackCount,
                "${coreTitle}:",
                extraTitle,
            ).joinToString(" ")
        }
    }

    object PlayListSaver {

        private enum class Args(
            val arg: String
        ){
            PLAY_TITLE("playTitle"),
            PLAY_PATH("playPath"),
            SAVE_PATH("savePath"),
        }
        fun save(
            extraMap: Map<String, String>,
        ){
            val playPath =
                ArgsManager.get(
                    extraMap,
                    Args.PLAY_PATH.arg,
                )
            if(
                playPath.isNullOrEmpty()
            ) return
            val savePath =
                ArgsManager.get(
                    extraMap,
                    Args.SAVE_PATH.arg,
                )
            if(
                savePath.isNullOrEmpty()
            ) return
            val title = makePlayTitle(
                extraMap,
                playPath
            )
            val insertLine = listOf(
                title,
                playPath
            ).joinToString("\t")
            val previousList = ReadText(savePath).textToList().sorted().distinct()
            if(
                previousList.contains(insertLine)
            ) return
            TsvTool.insertTsvInFirst(
                savePath,
                insertLine,
                previousList
            )
        }

        private fun makePlayTitle(
            extraMap: Map<String, String>,
            playPath: String,
        ): String {
            val playTitle =
                ArgsManager.get(
                    extraMap,
                    Args.PLAY_TITLE.arg
                )
            return when(
                playTitle.isNullOrEmpty()
            ){
                true -> File(playPath).name
                else -> playTitle
            }
        }
    }

    private enum class CommonKeyArgs (
        val args: String,
        val defaultVal: String,
    ){
        ALTER_CON("alterCon", "no display"),
    }

    private object JudgeTsv {

        private enum class JudgeArg(
            val arg: String,
        ){
            TSV_PATH("tsvPath"),
            TSV_KEY("tsvKey"),
            TSV_VALUE("tsvValue"),
            ALTER_CON(CommonKeyArgs.ALTER_CON.args),
        }

        private val alterConDefaultValue = CommonKeyArgs.ALTER_CON.defaultVal

        private enum class JudgeTsvKey(
            val str: String
        ){
            LIST_DIR(ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key)
        }

        fun getStrByListDirValue(
            context: Context?,
            extraRepValMap: Map<String, String>?
        ): String {
            val updateExtraMap = (extraRepValMap ?: mapOf()) + mapOf(
                JudgeArg.TSV_KEY.arg
                        to JudgeTsvKey.LIST_DIR.str
            )
            return getStrByJudgeTsvValue(
                context,
                updateExtraMap,
            )
        }
        fun getStrByJudgeTsvValue(
            context: Context?,
            extraRepValMap: Map<String, String>,
        ): String {
            val tsvPath = ArgsManager.get(
                extraRepValMap,
                JudgeArg.TSV_PATH.arg
            ) ?: return String()
            val tsvKey = ArgsManager.get(
                extraRepValMap,
                JudgeArg.TSV_KEY.arg
            ) ?: return String()
            val valueSeparator = '&'
            val tsvValueList = ArgsManager.get(
                extraRepValMap,
                JudgeArg.TSV_VALUE.arg
            )?.split(valueSeparator) ?: emptyList()
            val alterCon = ArgsManager.get(
                extraRepValMap,
                JudgeArg.ALTER_CON.arg
            ) ?: let {
                LogSystems.stdErr(
                    context,
                        "'${JudgeArg.ALTER_CON.arg}' not specify " +
                                "in ${JsAcAlterIfTool.IfShellKey.IF_ARGS.key} " +
                                "in ${ShellMacro.JUDGE_LIST_DIR.name} or ${ShellMacro.JUDGE_TSV_VALUE.name}"
                )
                String()
            }
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsAcAlter_alterCon.txt").absolutePath,
//                listOf(
//                    "extraRepValMap: ${extraRepValMap}",
//                    "alterCon: ${alterCon}",
//                ).joinToString("\n\n") + "\n--------\n"
//            )
            val currentValue = QuoteTool.trimBothEdgeQuote(
                TsvTool.getKeyValueFromFile(
                    tsvPath,
                    tsvKey
                )
            )
            val isMatch =
                tsvValueList.contains(currentValue)
            return when(isMatch){
                true -> alterCon
                else -> String()
            }
        }
    }

    private fun getOutputByShell(
        busyboxExecutor: BusyboxExecutor?,
        shellPath: String,
        setReplaceVariableMap: Map<String, String>?,
        extraRepValMap: Map<String, String>?,
    ): String? {
        if(
            busyboxExecutor == null
        ) return null
        val shellCon = makeShellCon(
            shellPath,
            setReplaceVariableMap,
            extraRepValMap,
        )
        return busyboxExecutor.getCmdOutput(
            shellCon,
            extraRepValMap
        )
    }

    private fun execMakeShellCon(
        shellPath: String,
        setReplaceVariableMap: Map<String, String>?,
    ): String {
        val currentAppDirPath = CcPathTool.getMainAppDirPath(
            shellPath
        )
        val currentFannelName = CcPathTool.getMainFannelFilePath(
            currentAppDirPath
        )
        return ReadText(
            shellPath
        ).readText().let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName
            )
        }
    }

    private object ArgsManager {

        private enum class ArgsPrefix(
            val prefix: String
        ){
            FILE_PREFIX(EditSettings.filePrefix)
        }
        fun get(
            argsMap: Map<String, String>?,
            argKey: String,
        ): String? {
            if (
                argsMap.isNullOrEmpty()
            ) return null
            val argStrValue = argsMap.get(argKey)
                ?.trim()
                ?: return null
            val filePrefix = ArgsPrefix.FILE_PREFIX.prefix
            return when(true){
                argStrValue.startsWith(filePrefix) -> {
                    val filePath = argStrValue.removePrefix(filePrefix)
                    ReadText(filePath).readText().trim()
                }
                else -> argStrValue
            }
        }
    }

    fun makeSetReplaceVariableMapFromSubFannel(
        context: Context,
        shellPath: String,
    ): Map<String, String>? {
        val isNotMacro = ShellMacro.values().firstOrNull {
            it.name == shellPath
        } == null
        return when(isNotMacro) {
            true -> SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                context,
                shellPath
            )
            else -> null
        }
    }

    private enum class ShellMacro {
        SAVE_PLAY_LIST,
        JUDGE_TSV_VALUE,
        JUDGE_LIST_DIR,
        MAKE_HEADER_TITLE,
    }
}
