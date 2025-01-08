package com.puutaro.commandclick.proccess.edit.setting_action

object SettingActionKeyManager {

    const val landSeparator = ','
    const val mainKeySeparator = '|'
    const val subKeySepartor = '?'
    const val valueSeparator = '&'

    enum class SettingActionsKey(
        val key: String
    ) {
        SETTING_VAR("sVar"),
        SETTING_ACTION_VAR("sAcVar"),
        SETTING_RETURN("sReturn")
    }

    enum class VarPrefix(
        val prefix: String
    ) {
        RUN("run"),
        RUN_ASYNC("${RUN.prefix}Async"),
        ASYNC("async"),
    }

    object ValueStrVar {

        const val itPronoun = "it"

        fun matchStringVarName(
            bitmapVarName: String,
        ): Boolean {
            val bitmapVarRegex = Regex("^[$][{][a-zA-Z0-9_]+[}]$")
            return bitmapVarRegex.matches(bitmapVarName)
                    && !bitmapVarName.startsWith(VarPrefix.RUN.prefix)
        }

        fun convertStrKey(bitmapVar: String): String {
            return bitmapVar
                .removePrefix("${'$'}{")
                .removeSuffix("}")
        }
    }


    object AwaitManager {
        private const val awaitSeparator = ','

        fun getAwaitVarNameList(awaitVarNameListCon: String): List<String> {
            return awaitVarNameListCon.split(awaitSeparator).map {
                it.trim()
            }.filter {
                it.isNotEmpty()
            }
        }
    }

    private enum class CommonPathKey(
        val key: String
    ) {
        IMPORT_PATH("importPath"),
    }

    enum class SettingSubKey(
        val key: String
    ) {
        SETTING_VAR(SettingActionsKey.SETTING_VAR.key),
        SETTING_RETURN(SettingActionsKey.SETTING_RETURN.key),
        FUNC("func"),
        ARGS("args"),
        ON_RETURN("onReturn"),
        S_IF("sIf"),
        VALUE("value"),
        AWAIT("await"),
    }

    object ActionImportManager {

        enum class ActionImportKey(
            val key: String,
        ) {
            IMPORT_PATH(CommonPathKey.IMPORT_PATH.key),
            REPLACE("replace"),
            S_IF(SettingSubKey.S_IF.key),
            ARGS(SettingSubKey.ARGS.key),
            AWAIT(SettingSubKey.AWAIT.key),
        }
    }


    object SettingReturnManager {

        enum class OutputReturn {
            OUTPUT_RETURN
        }

        enum class SettingReturnKey(
            val key: String,
        ) {
            S_IF(SettingSubKey.S_IF.key),
            ARGS(SettingSubKey.ARGS.key),
        }
    }

    enum class BreakSignal {
        EXIT_SIGNAL,
        RETURN_SIGNAL
    }

//    enum class CommandMacro {
//        EXIT_SIGNAL,
//    }
}