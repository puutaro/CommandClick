package com.puutaro.commandclick.proccess.shell_macro

import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.tsv.TsvTool
import java.io.File

object ShellMacroHandler {

    fun handle(
        busyboxExecutor: BusyboxExecutor?,
        shellPath: String,
        setReplaceVariableMap: Map<String, String>?,
        extraRepValMap: Map<String, String>?,
    ): String {
        val macro = ShellMacro.values().firstOrNull {
            it.name == shellPath
        } ?: return getOutputByShell(
            busyboxExecutor,
            shellPath,
            setReplaceVariableMap,
            extraRepValMap,
        ) ?: String()
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
                JudgeTsv.getStrByJudgeTsvValue(concatRepValMap)
            ShellMacro.JUDGE_LIST_DIR ->
                JudgeTsv.getStrByListDirValue(concatRepValMap)
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
            EXTRA_TITLE("extraTitle"),
            BACKSTACK_COUNT(SearchBoxSettingsForListIndex.backstackCountMarkForInsertEditText),
        }

        fun get(
            concatRepValMap: Map<String, String>
        ): String {
            val coreTitle = ArgsManager.get(
                concatRepValMap,
                ArgsKey.FANNEL_PATH.key
            )?.let {
                CcPathTool.trimAllExtend(
                    File(it).name
                )
            } ?: String()
            val extraTitle = ArgsManager.get(
                concatRepValMap,
                ArgsKey.EXTRA_TITLE.key
            ) ?: String()
            val backstackCount = concatRepValMap.get(ArgsKey.BACKSTACK_COUNT.key)
                ?: String()
            return listOf(
                "(${backstackCount})",
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

    private object JudgeTsv {

        private enum class JudgeArg(
            val arg: String,
        ){
            TSV_PATH("tsvPath"),
            TSV_KEY("tsvKey"),
            TSV_VALUE("tsvValue"),
            ALTER_CON("alterCon"),
        }

        private enum class JudgeTsvKey(
            val str: String
        ){
            LIST_DIR(ListSettingsForListIndex.ListSettingKey.LIST_DIR.key)
        }

        private const val alterConDefaultValue = "no display"

        fun getStrByListDirValue(
            extraRepValMap: Map<String, String>?
        ): String {
            val updateExtraMap = (extraRepValMap ?: mapOf()) + mapOf(
                JudgeArg.TSV_KEY.arg
                        to JudgeTsvKey.LIST_DIR.str
            )
            return getStrByJudgeTsvValue(
                updateExtraMap,
            )
        }
        fun getStrByJudgeTsvValue(
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
            ) ?: alterConDefaultValue
            val currentValue = QuoteTool.trimBothEdgeQuote(
                TsvTool.getKeyValue(
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
                currentAppDirPath,
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

    private enum class ShellMacro {
        SAVE_PLAY_LIST,
        JUDGE_TSV_VALUE,
        JUDGE_LIST_DIR,
        MAKE_HEADER_TITLE,
    }
}
