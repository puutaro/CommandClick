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
        SETTING_INNER_VAR("sInVar"),
//        SETTING_IF("sIf"),
//        SETTING_TSV_VARS("sTsvVars"),
        SETTING_ACTION_VAR("sAcVar"),
    }

    enum class CommonPathKey(
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
        RETURN("onReturn"),
        S_IF("sIf"),
        VALUE("value"),
    }

    enum class OnlyVarSubKey(
        val key: String
    ){
        EXIT("exit"),
        VAR_RETURN("varReturn"),
    }

    enum class VirtualSubKey(
        val key: String
    ){
        ACTION_IMPORT_CON("actionImportCon"),
        VAR_NOT_INIT("varNotInit"),
    }

    enum class CommandMacro {
        EXIT_SIGNAL,
    }
}