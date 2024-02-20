package com.puutaro.commandclick.proccess.js_macro_libs.macros



object MacroForToolbarButton{
    enum class Macro{
        ADD,
        ADD_APP_DIR,
        ADD_URL,
        ADD_URL_CON,
        ADD_URL_HISTORY,
        APP_DIR_MANAGER,
        CONFIG,
        EDIT,
        FANNEL_REPO_SYNC,
        GET_FILE,
        GET_DIR,
        GET_QR_CON,
        INSTALL_FANNEL,
        JS_IMPORT,
        KILL,
        MENU,
        NORMAL,
        NO_SCROLL_SAVE_URL,
        OK,
        PAGE_SEARCH,
        QR_SCAN,
        REFRESH_MONITOR,
        RESTART_UBUNTU,
        SIZING,
        SELECT_MONITOR,
        SHORTCUT,
        SYNC,
        TERMUX_SETUP,
        USAGE,
        WEB_SEARCH,
    }

    enum class MenuMacroArgsKey(
        val key: String
    ){
        MENU_PATH("menuPath"),
        ON_HIDE_FOOTER("onHideFooter"),
    }

}