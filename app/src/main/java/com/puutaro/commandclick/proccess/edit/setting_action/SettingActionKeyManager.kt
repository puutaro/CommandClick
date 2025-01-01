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
    }

    object ActionImportManager {

        enum class ActionImportKey(
            val key: String,
        ) {
            IMPORT_PATH(CommonPathKey.IMPORT_PATH.key),
            REPLACE("replace"),
            S_IF("sIf"),
            ARGS(SettingSubKey.ARGS.key),
        }
    }

    val imageVarPrefix = "#{}"
    enum class CommandMacro {
        EXIT_SIGNAL,
    }
}