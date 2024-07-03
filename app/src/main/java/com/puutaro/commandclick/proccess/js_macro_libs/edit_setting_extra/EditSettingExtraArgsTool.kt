package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePrefTool

object EditSettingExtraArgsTool {

    enum class ExtraKey(
        val key: String
    ) {
        PARENT_DIR_PATH("parentDirPath"),
        COMP_PREFIX("compPrefix"),
        COMP_SUFFIX("compSuffix"),
        REMOVE_PREFIX("removePrefix"),
        REMOVE_SUFFIX("removeSuffix"),
        FILTER_PREFIX("prefix"),
        FILTER_SUFFIX("suffix"),
        INITIAL_PATH("initialPath"),
        SHELL_PATH("shellPath"),
        SHELL_CON("shellCon"),
        MACRO("macro"),
        TAG("tag"),
    }

    fun getParentDirPath(
        extraMap: Map<String, String>?,
        currentAppDirPath: String,
    ): String {
        return extraMap?.get(ExtraKey.PARENT_DIR_PATH.key).let {
            if(
                it.isNullOrEmpty()
            ) return@let currentAppDirPath
            it
        }
    }

    fun makeCompFileName(
        editFragment: EditFragment,
        srcFileName: String,
        extraMap: Map<String, String>?,
    ): String {
        if(
            extraMap.isNullOrEmpty()
        ) return srcFileName
        return FileName.comp(
            editFragment,
            srcFileName,
            extraMap,
            '&'
        )
    }

    fun makeShellCon(
        extraMap: Map<String, String>?,
    ): String {
        if(
            extraMap.isNullOrEmpty()
        ) return String()
        val shellPath = extraMap.get(
            ExtraKey.SHELL_PATH.key
        ) ?: return String()
        return ReadText(shellPath).readText()

    }


    private object FileName {
        fun comp(
            editFragment: EditFragment,
            srcFileName: String,
            compTitleMap: Map<String, String>?,
            nextNextSeparator: Char
        ): String {
            if(
                compTitleMap.isNullOrEmpty()
            ) return srcFileName
            val removePrefixFileName = makeRemovePrefixFileName(
                compTitleMap,
                srcFileName,
                nextNextSeparator,
            )
            val removeSuffixFileName = makeRemoveSuffixFileName(
                compTitleMap,
                removePrefixFileName,
                nextNextSeparator,
            )
            val compPrefixFileName = compTitleMap.get(
                ExtraKey.COMP_PREFIX.key
            ).let {
                if(
                    it.isNullOrEmpty()
                ) return@let removeSuffixFileName
                UsePath.compPrefix(
                    removeSuffixFileName,
                    it
                )
            }

            val compSuffixFileName = compTitleMap.get(
                ExtraKey.COMP_SUFFIX.key
            ).let {
                if(
                    it.isNullOrEmpty()
                ) return@let compPrefixFileName
                UsePath.compExtend(
                    compPrefixFileName,
                    it
                )
            }
            val compFileNameByShell = compByShell(
                editFragment,
                compTitleMap,
                compSuffixFileName,
            )
            val macroStr = compTitleMap.get(ExtraKey.MACRO.key)
            val fileNameByMacro = makeFileNameByMacro(
                compFileNameByShell,
                macroStr,
            )
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "add.txt").absolutePath,
//                listOf(
//                    "compTitleMap: ${compTitleMap}",
//                    "srcFileName: ${srcFileName}",
//                    "removePrefixFileName: ${removePrefixFileName}",
//                    "removeSuffixFileName: ${removeSuffixFileName}",
//                    "compPrefixFileName: ${compPrefixFileName}",
//                    "compSuffixFileName: ${compSuffixFileName}",
//                    "compFileNameByShell: ${compFileNameByShell}",
//                    "macroStr: ${macroStr}",
//                    "fileNameByMacro: ${fileNameByMacro}",
//                ).joinToString("\n\n\n")
//            )
            return fileNameByMacro
        }

        private fun makeRemovePrefixFileName(
            compTitleMap: Map<String, String>,
            fileName: String,
            nextSeparator: Char,
        ): String {
            return compTitleMap.get(
                ExtraKey.REMOVE_PREFIX.key
            )?.split(nextSeparator).let {
                if(
                    it.isNullOrEmpty()
                ) return@let fileName
                var removePrefixFileName = fileName
                it.forEach {
                    removePrefixFileName = removePrefixFileName.removePrefix(it)
                }
                removePrefixFileName
            }
        }

        private fun makeRemoveSuffixFileName(
            compTitleMap: Map<String, String>,
            fileName: String,
            nextSeparator: Char,
        ): String {
            return compTitleMap.get(
                ExtraKey.REMOVE_SUFFIX.key
            )?.split(nextSeparator).let {
                if(
                    it.isNullOrEmpty()
                ) return@let fileName
                var removeSuffixFileName = fileName
                it.forEach {
                    removeSuffixFileName = removeSuffixFileName.removeSuffix(it)
                }
                removeSuffixFileName
            }
        }

        private fun compByShell(
            editFragment: EditFragment,
            compTitleMap: Map<String, String>,
            srcFileName: String,
        ): String {
            val readSharePreferenceMap =
                editFragment.readSharePreferenceMap

            val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
                readSharePreferenceMap
            )
            val currentFannelName = SharePrefTool.getCurrentFannelName(
                readSharePreferenceMap
            )
            val busyboxExecutor = editFragment.busyboxExecutor
            val setReplaceVariablesMap = editFragment.setReplaceVariableMap
            val shellCon = makeShellCon(compTitleMap).let {
                SetReplaceVariabler.execReplaceByReplaceVariables(
                    it,
                    setReplaceVariablesMap,
                    currentAppDirPath,
                    currentFannelName
                )
            }
            return shellCon.let {
                if(
                    it.isEmpty()
                ) return@let srcFileName
                busyboxExecutor?.getCmdOutput(
                    shellCon.replace(
                        "\${FILE_NAME}",
                        srcFileName
                    ),
                    compTitleMap,
                ) ?:srcFileName
            }
        }

        private fun makeShellCon(
            extraMap: Map<String, String>?,
        ): String {
            if(
                extraMap.isNullOrEmpty()
            ) return String()
            val shellCon = extraMap.get(
                ExtraKey.SHELL_CON.key
            )
            if(
                !shellCon.isNullOrEmpty()
            ) return shellCon
            val shellPath = extraMap.get(
                ExtraKey.SHELL_PATH.key
            ) ?: return String()
            return ReadText(shellPath).readText()
        }

        private enum class AddTitleMacro {
            CAMEL_TO_BLANK_SNAKE
        }

            private fun makeFileNameByMacro(
            srcFileName: String,
            macroStr: String?,
        ): String {
            val macro = AddTitleMacro.values().firstOrNull {
                it.name == macroStr
            } ?: return srcFileName
            return when(macro){
                AddTitleMacro.CAMEL_TO_BLANK_SNAKE -> {
                    srcFileName.camelToBlankSnakeCase()
                }
            }
        }

        private fun String.camelToBlankSnakeCase(): String {
            val pattern = "(?<=.)[A-Z]".toRegex()
            return this.replace(pattern, " $0").lowercase()
        }
    }
}