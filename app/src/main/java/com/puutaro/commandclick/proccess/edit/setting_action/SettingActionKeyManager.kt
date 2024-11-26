package com.puutaro.commandclick.proccess.edit.setting_action

import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager.CommonPathKey
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager.JsSubKey

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
        RETURN("onReturn"),
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

            INVALID_AFTER_IN_AC_IMPORT("INVALID_AFTER_IN_AC_IMPORT"),
            MISS_LAST_USE_VAR_KEY("MISS_LAST_USE_VAR_KEY"),
            MISS_LAST_VAR_KEY("MISS_LAST_VAR_KEY"),
            MISS_LAST_RETURN_KEY("MISS_LAST_RETURN_KEY"),
            MISS_IMPORT_PATH("MISS_IMPORT_PATH"),
        }
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