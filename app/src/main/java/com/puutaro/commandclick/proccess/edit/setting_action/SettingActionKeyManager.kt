package com.puutaro.commandclick.proccess.edit.setting_action

object SettingActionKeyManager {

    val landSeparator = ','
    val mainKeySeparator = '|'
    val subKeySepartor = '?'
    val valueSeparator = '&'

    enum class SettingActionsKey(
        val key: String
    ) {
//        SETTING_ACTION("sAc"),
        SETTING_VAR("sVar"),
//        SETTING_INNER_VAR("sInVar"),
//        SETTING_IF("sIf"),
//        SETTING_TSV_VARS("sTsvVars"),
        SETTING_ACTION_VAR("sAcVar"),
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
        SETTING_VAR("sVar"),
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
            S_IF("sIf"),
            ARGS(SettingSubKey.ARGS.key),
            AWAIT("await"),
        }
    }

    enum class ExitSignal {
        EXIT_SIGNAL,
    }

//    enum class CommandMacro {
//        EXIT_SIGNAL,
//    }
}