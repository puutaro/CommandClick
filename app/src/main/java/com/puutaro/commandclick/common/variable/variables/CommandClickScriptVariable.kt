package com.puutaro.commandclick.common.variable.variables

import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.AltRegexTool
import java.io.File


object CommandClickScriptVariable {

    val EMPTY_STRING = "COMMAND_CLICK_EMPTY_STRING"
    const val LABELING_SEC_START = "/// LABELING_SECTION_START"
    const val LABELING_SEC_END = "/// LABELING_SECTION_END"
    const val SETTING_SEC_START = "/// SETTING_SECTION_START"
    const val SETTING_SEC_END = "/// SETTING_SECTION_END"
    const val CMD_SEC_START = "/// CMD_VARIABLE_SECTION_START"
    const val CMD_SEC_END = "/// CMD_VARIABLE_SECTION_END"
    const val SCRIPT_START = "/// Please write bellow with javascript"

    val UBUNTU_EXEC_MODE = "ubuntuExecMode"
    val UBUNTU_OUTPUT_FILE = "ubuntuOutputFile"
    val TERMINAL_DO = "terminalDo"
    val EDIT_EXECUTE = "editExecute"
    val TERMINAL_OUTPUT_MODE = "terminalOutputMode"
    val SET_VARIABLE_TYPE = "setVariableTypes"
    val SET_REPLACE_VARIABLE = "setReplaceVariables"
    val ON_UPDATE_LAST_MODIFY = "onUpdateLastModify"
    val ON_URL_LAUNCH_MACRO = "onUrlLaunchMacro"
    val EXEC_JS_OR_HTML_PATH = "execJsOrHtmlPath"
    val BEFORE_COMMAND = "beforeCommand"
    val AFTER_COMMAND = "afterCommand"
    val CMDCLICK_ON_AUTO_EXEC = "onAutoExec"
    val AUTO_EXEC_PATH = "autoExecPath"
    val NO_ARG_JS_PATH = "noArgJsPath"
    val FANNEL_STATE_CONFIG = "fannelStateConfig"
    val ON_TERM_BACKEND_WHEN_START = "onTermBackendWhenStart"
    val ON_TERM_VISIBLE_WHEN_KEYBOARD = "onTermVisibleWhenKeyboard"
    val ON_TERM_SHORT_WHEN_LOAD = "onTermShortWhenLoad"
//    val CMDCLICK_HISTORY_SWITCH = "historySwitch"
//    val CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC = "urlHistoryOrButtonExec"
    val ON_URL_HISTORY_REGISTER = "onUrlHistoryRegister"
    val IGNORE_HISTORY_PATHS = "ignoreHistoryPaths"
    val ON_ADBLOCK = "onAdBlock"
    val ON_LAUNCH_URL_HISTORY_BY_BACKSTACK = "onLaunchUrlHistoryByBackstack"
    val DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT="disableShowToolbarWhenHighlight"
    val CMDCLICK_TERMINAL_FONT_ZOOM = "terminalFontZoom"
    val EDIT_BOX_TITLE_CONFIG = "editBoxTitleConfig"
    val TERMINAL_COLOR = "terminalColor"
    val TERMINAL_FONT_COLOR = "terminalFontColor"
//    val HOME_SCRIPT_URLS_PATH = "homeScriptUrlsPath"
    val CMDCLICK_SHIBAN_DEFAULT_VALUE = "#!/bin/bash"
//    val CMDCLICK_RUN_SHELL_DEFAULT_VALUE = "bash"
//    val CMDCLICK_HOME_FANNELS_PATH = "homeFannelsPath"
//    val PASS_CMDVARIABLE_EDIT = "passCmdVariableEdit"
    val SETTING_BUTTON_CONFIG = "settingButtonConfig"
    val EDIT_BUTTON_CONFIG = "editButtonConfig"
    val PLAY_BUTTON_CONFIG = "playButtonConfig"
    val EXTRA_BUTTON_CONFIG = "extraButtonConfig"
    val EDIT_LIST_CONFIG = "editListConfig"
//    val QR_DIALOG_CONFIG = "qrDialogConfig"
//    val HIDE_SETTING_VARIABLES = "hideSettingVariables"
//    val NO_SCROLL_SAVE_URLS = "noScrollSaveUrls"
    val DISABLE_SETTING_VALS_EDIT = "disableSettingValsEdit"
    val SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH = "srcImageAnchorLongPressMenuFilePath"
    val SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH = "srcAnchorLongPressMenuFilePath"
    val IMAGE_LONG_PRESS_MENU_FILE_PATH = "imageLongPressMenuFilePath"
    val SRC_IMAGE_ANCHOR_LONG_PRESS_JS_PATH = "srcImageAnchorLongPressJsPath"
    val SRC_ANCHOR_LONG_PRESS_JS_PATH = "srcAnchorLongPressJsPath"
    val IMAGE_LONG_PRESS_JS_PATH = "imageLongPressJsPath"
    val UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF = "ubuntuSleepDelayMinInScreenOff"
    val MUST_UBUNTU_SETUP = "mustUbuntuSetup"
    val DEFAULT_MONITOR_FILE = "defaultMonitorFile"
    val SETTING_IMPORT = "settingImport"
    val IMPORT_DISABLE_VAL_LIST = "importDisableValList"
    val ON_ROOTFS_SDCARD_SAVE = "onRootfsSdCardSave"
    val UBUNTU_AUTO_SETUP = "ubuntuAutoSetup"
    val jsActionShiban = "js/action"
    val CMDCLICK_LONG_PRESS_LINK_URL = "LONG_PRESS_LINK_URL"
    val CMDCLICK_LONG_PRESS_IMAGE_URL = "LONG_PRESS_IMAGE_URL"
    val CMDCLICK_CURRENT_PAGE_URL = "CMDCLICK_CURRENT_PAGE_URL"
    val CMDDLICK_EXTERNAL_EXEC_REPLACE_TXT = "EXTERNAL_EXEC_REPLACE_TXT"

    val SETTING_VARIABLE_NAMES_LIST = arrayOf(
        UBUNTU_EXEC_MODE,
        UBUNTU_OUTPUT_FILE,
        TERMINAL_DO,
        EDIT_EXECUTE,
        TERMINAL_OUTPUT_MODE,
        SET_VARIABLE_TYPE,
        SET_REPLACE_VARIABLE,
//        CMDCLICK_HISTORY_SWITCH,
        CMDCLICK_ON_AUTO_EXEC,
        AUTO_EXEC_PATH,
        NO_ARG_JS_PATH,
        ON_UPDATE_LAST_MODIFY,
        TERMINAL_COLOR,
        TERMINAL_FONT_COLOR,
//        CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
        ON_ADBLOCK,
        ON_TERM_BACKEND_WHEN_START,
        ON_TERM_VISIBLE_WHEN_KEYBOARD,
        ON_TERM_SHORT_WHEN_LOAD,
        ON_ROOTFS_SDCARD_SAVE,
        ON_LAUNCH_URL_HISTORY_BY_BACKSTACK,
        DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT,
        UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF,
        UBUNTU_AUTO_SETUP,
        CMDCLICK_TERMINAL_FONT_ZOOM,
        EDIT_BOX_TITLE_CONFIG,
        ON_URL_LAUNCH_MACRO,
        EXEC_JS_OR_HTML_PATH,
        ON_URL_HISTORY_REGISTER,
//        CMDCLICK_HOME_FANNELS_PATH,
        BEFORE_COMMAND,
        AFTER_COMMAND,
        IGNORE_HISTORY_PATHS,
//        HOME_SCRIPT_URLS_PATH,
        PLAY_BUTTON_CONFIG,
        EDIT_BUTTON_CONFIG,
        EXTRA_BUTTON_CONFIG,
        SETTING_BUTTON_CONFIG,
        EDIT_LIST_CONFIG,
//        QR_DIALOG_CONFIG,
//        NO_SCROLL_SAVE_URLS,
        SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
        SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
        IMAGE_LONG_PRESS_MENU_FILE_PATH,
        DEFAULT_MONITOR_FILE,
        FANNEL_STATE_CONFIG,
    )
    private val terminalOn = SettingVariableSelects.TerminalDoSelects.ON.name
//    private val terminalTermux = SettingVariableSelects.TerminalDoSelects.TERMUX.name
    private val terminalOff = SettingVariableSelects.TerminalDoSelects.OFF.name
    private val editExecuteNo = SettingVariableSelects.EditExecuteSelects.NO.name
//    private val editExecuteOnce = SettingVariableSelects.EditExecuteSelects.ONCE.name
    private val editExecuteAlways = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
    private val autoExecSelectsOn = SettingVariableSelects.AutoExecSelects.ON.name
    private val autoExecSelectsOff = SettingVariableSelects.AutoExecSelects.OFF.name
    private val disableSettingValsEditOn = SettingVariableSelects.DisableSettingValsEdit.ON.name
    private val disableSettingValsEditOff = SettingVariableSelects.DisableSettingValsEdit.OFF.name
    private val terminalOutPutModeNormal = SettingVariableSelects.TerminalOutPutModeSelects.NORMAL.name
    private val terminalOutPutModeReflash = SettingVariableSelects.TerminalOutPutModeSelects.REFLASH.name
    private val terminalOutPutModeReflashAndFirstRow = SettingVariableSelects.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
    private val terminalOutPutModeDebug = SettingVariableSelects.TerminalOutPutModeSelects.DEBUG.name
    private val terminalOutPutModeNo = SettingVariableSelects.TerminalOutPutModeSelects.NO.name
//    private val historySwitchOff = SettingVariableSelects.HistorySwitchSelects.OFF.name
//    private val historySwitchOn = SettingVariableSelects.HistorySwitchSelects.ON.name
//    private val historySwitchInherit = SettingVariableSelects.HistorySwitchSelects.INHERIT.name
//    private val urlHistoryOrButtonExecUrlHistory = SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name
//    private val urlHistoryOrButtonExecUrlButtonExec = SettingVariableSelects.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name
//    private val urlHistoryOrButtonExecUrlInherit = SettingVariableSelects.UrlHistoryOrButtonExecSelects.INHERIT.name
    private val onUpdateLastModifyOn = SettingVariableSelects.OnUpdateLastModifySelects.ON.name
    private val onUpdateLastModifyOff = SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
    private val onUrlLaunchMacroRecent = SettingVariableSelects.OnUrlLaunchMacroSelects.RECENT.name
    private val onUrlLaunchMacroFrequency = SettingVariableSelects.OnUrlLaunchMacroSelects.FREQUENCY.name
    private val onUrlLaunchMacroOff = SettingVariableSelects.OnUrlLaunchMacroSelects.OFF.name
    private val onAdBlockInherit = SettingVariableSelects.OnAdblockSelects.INHERIT.name
    private val onAdBlockOn = SettingVariableSelects.OnAdblockSelects.ON.name
    private val onAdBlockOff = SettingVariableSelects.OnAdblockSelects.OFF.name
    private val onLaunchUrlHistoryByBackstackOn = SettingVariableSelects.OnLaunchUrlHistoryByBackstack.ON
    private val onLaunchUrlHistoryByBackstackOff = SettingVariableSelects.OnLaunchUrlHistoryByBackstack.OFF
    private val ubuntuExecModeSelectsBackground = SettingVariableSelects.UbuntuExecModeSelects.background.name
    private val ubuntuExecModeSelectsForeground = SettingVariableSelects.UbuntuExecModeSelects.foreground.name
    private val onTermVisibleWhenKeyboardSelectsInherit = SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.INHERIT.name
    private val onTermVisibleWhenKeyboardSelectsOn = SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.ON.name
    private val onTermVisibleWhenKeyboardSelectsOff = SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.OFF.name
    private val onTermBackendWhenStartSelectsOn = SettingVariableSelects.OnTermBackendWhenStartSelects.ON.name
    private val onTermBackendWhenStartSelectsOff = SettingVariableSelects.OnTermBackendWhenStartSelects.OFF.name
    private val onTermBackendWhenStartSelectsInherit = SettingVariableSelects.OnTermBackendWhenStartSelects.INHERIT.name
    private val onTermShortWhenLoadSelectsOn = SettingVariableSelects.OnTermShortWhenLoadSelects.ON.name
    private val onTermShortWhenLoadSelectsOff = SettingVariableSelects.OnTermShortWhenLoadSelects.OFF.name
    private val onTermShortWhenLoadSelectsInherit = SettingVariableSelects.OnTermShortWhenLoadSelects.INHERIT.name
    private val disableShowToolbarWhenHighlightOn = SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.ON.name
    private val disableShowToolbarWhenHighlightOff = SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.OFF.name
    private val onUrlHistoryRegisterOff = SettingVariableSelects.OnUrlHistoryRegisterSelects.OFF.name
    private val onUrlHistoryRegisterOn = SettingVariableSelects.OnUrlHistoryRegisterSelects.ON.name
    private val onRootfsSdcardSaveOn = SettingVariableSelects.OnRootfsSdCardSaveSelects.ON.name
    private val onRootfsSdcardSaveOff = SettingVariableSelects.OnRootfsSdCardSaveSelects.OFF.name
    private val ubuntuAutoSetupOff = SettingVariableSelects.UbuntuAutoSetup.OFF.name
    private val ubuntuAutoSetupSetup = SettingVariableSelects.UbuntuAutoSetup.SETUP.name
    private val ubuntuAutoSetupRestore = SettingVariableSelects.UbuntuAutoSetup.RESTORE.name
    val UBUNTU_EXEC_MODE_DEFAULT_VALUE = ubuntuExecModeSelectsBackground
    val UBUNTU_OUTPUT_FILE_DEFAULT_VALUE = UsePath.cmdClickMonitorFileName_1
    val TERMINAL_DO_DEFAULT_VALUE = terminalOn
    val EDIT_EXECUTE_DEFAULT_VALUE = editExecuteNo

    val TERMINAL_OUTPUT_MODE_DEFAULT_VALUE = terminalOutPutModeNormal
//    val HISTORY_SWITCH_DEFAULT_VALUE = historySwitchOff
    val ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE = onUpdateLastModifyOn
    val TERMINAL_COLOR_DEFAULT_VALUE = "#043d02"
    val TERMINAL_FONT_COLOR_DEFAULT_VALUE = "#f2f2f2"
    val ON_URL_LAUNCH_MACRO_DEFAULT_VALUE = onUrlLaunchMacroOff
    val ON_URL_HISTORY_REGISTER_DEFAULT_VALUE = onUrlHistoryRegisterOn
//    val PASS_CMDVARIABLE_EDIT_ON_VALUE = "ON"
    val ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE = onTermBackendWhenStartSelectsOff
    val ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE = onTermShortWhenLoadSelectsOff
    val CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE = 100
//    val CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE = SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name
    val ON_ADBLOCK_DEFAULT_VALUE = SettingVariableSelects.OnAdblockSelects.OFF.name
    val ON_LAUNCH_URL_HISTORY_BY_BACKSTACK_DEFAULT_VALUE = SettingVariableSelects.OnLaunchUrlHistoryByBackstack.OFF.name
    val CMDCLICK_ON_AUTO_EXEC_DEFAULT_VALUE =  SettingVariableSelects.AutoExecSelects.ON.name
    val ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE = SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.OFF.name
    val DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT_DEFAULT_VALUE = SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.ON.name
    val DEFAULT_MONITOR_FILE_DEFAULT_VALUE = UsePath.cmdClickMonitorFileName_1
    val fannelDirName = CcPathTool.makeFannelDirName(
        SystemFannel.preference
    )
//    private val homeScriptUrlsFilePath = UsePath.homeScriptUrlsFilePath
//    private val homeFannelsFilePath =
//        UsePath.homeFannelsFilePath
//    private val macroStrForDragSortGetListPathFromVar =
//        ProducerMacro.macroStrForDragSortGetListPathFromVar
//    private val buttonCmd = ButtonViewProducer.ButtonEditKey.cmd.name
//    private val buttonLabel = ButtonViewProducer.ButtonEditKey.label.name
//    private val buttonSetfType =
//        ButtonViewProducer.SET_F_OPTION_MAP_KEY.type
//    private val buttonSetfListAddSuffix =
//        ButtonViewProducer.SET_F_OPTION_MAP_KEY.ListAdd.suffix.name
//    private val buttonSetfListAddSourceDirPath =
//        ButtonViewProducer.SET_F_OPTION_MAP_KEY.ListAdd.dirPath.name
//    private val buttonSetfListAddHowFull =
//        ButtonViewProducer.SET_F_OPTION_MAP_KEY.ListAdd.howFull.name
//    private val listPathForListConSlSpi =
//        ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
//    private val limitNumForListConSlSpi =
//        ListContentsSelectSpinnerViewProducer.ListContentsEditKey.limitNum.name
//    private val setVaraibleValueForHomeScriptUrlsPath =
//        "$listPathForListConSlSpi=" +
//                "$macroStrForDragSortGetListPathFromVar:${homeScriptUrlsFilePath}" +
//                    "?$limitNumForListConSlSpi=20" +
//                "|$buttonCmd=" +
//                    listOf(
//                        "setf $buttonSetfType=${SettingCmdArgs.ListAdd.name}",
//                        "$buttonSetfListAddSuffix=.js&.sh&.html",
//                        "$buttonSetfListAddSourceDirPath=\${01}",
//                        "$buttonSetfListAddHowFull=on?$buttonLabel=ADD"
//                    ).joinToString(" ")
//    private val setVariableValueForHomeFannelsPath =
//        "$listPathForListConSlSpi=" +
//                "$macroStrForDragSortGetListPathFromVar:$homeFannelsFilePath" +
//                    "?$limitNumForListConSlSpi=20" +
//                "|$buttonCmd=" +
//                listOf(
//                    "setf $buttonSetfType=${SettingCmdArgs.ListAdd.name}",
//                    "$buttonSetfListAddSuffix=${UsePath.JS_FILE_SUFFIX}",
//                    "$buttonSetfListAddSourceDirPath=${UsePath.cmdclickAppHistoryDirAdminPath}" +
//                            "?$buttonLabel=ADD"
//                ).joinToString(" ")
//    private val srcImageAnchorLongPressMenuFilePath =
//        UsePath.srcImageAnchorLongPressMenuFilePath
//    private val srcAnchorLongPressMenuFilePath =
//        UsePath.srcAnchorLongPressMenuFilePath
//    private val imageLongPressMenuFilePath =
//        UsePath.imageLongPressMenuFilePath
//    private val noScrollSaveUrlsFilePath =
//        UsePath.noScrollSaveUrlsFilePath
//    private val setVariableValueForSrcImageAnchorLongPressMenuFilePath =
//        "$listPathForListConSlSpi=" +
//                "$macroStrForDragSortGetListPathFromVar:$srcImageAnchorLongPressMenuFilePath" +
//                    "?$limitNumForListConSlSpi=20" +
//                "|$buttonCmd=" +
//                listOf(
//                    "setf $buttonSetfType=${SettingCmdArgs.ListAdd.name}",
//                    "$buttonSetfListAddSuffix=${UsePath.JS_FILE_SUFFIX}",
//                    "$buttonSetfListAddSourceDirPath=\${01}" +
//                            "?$buttonLabel=ADD"
//                ).joinToString(" ")
//    private val setVariableValueForSrcAnchorLongPressMenuFilePath =
//        "$listPathForListConSlSpi=" +
//                "$macroStrForDragSortGetListPathFromVar:$srcAnchorLongPressMenuFilePath" +
//                    "?$limitNumForListConSlSpi=20" +
//                "|$buttonCmd=" +
//                listOf(
//                    "setf $buttonSetfType=${SettingCmdArgs.ListAdd.name}",
//                    "$buttonSetfListAddSuffix=${UsePath.JS_FILE_SUFFIX}",
//                    "$buttonSetfListAddSourceDirPath=\${01}" +
//                            "?$buttonLabel=ADD"
//                ).joinToString(" ")
//    private val setVariableValueForImageLongPressMenuFilePath =
//        "$listPathForListConSlSpi=" +
//                "$macroStrForDragSortGetListPathFromVar:$imageLongPressMenuFilePath" +
//                    "?$limitNumForListConSlSpi=20" +
//                "|$buttonCmd=" +
//                listOf(
//                    "setf $buttonSetfType=${SettingCmdArgs.ListAdd.name}",
//                    "$buttonSetfListAddSuffix=${UsePath.JS_FILE_SUFFIX}",
//                    "$buttonSetfListAddSourceDirPath=\${01}" +
//                            "?$buttonLabel=ADD"
//                ).joinToString(" ")
//    val buttonIconSeparator = JsPathForEditButton.buttonIconSeparator
//    val buttonIconNameIdSeparator = JsPathForEditButton.buttonIconNameIdSeparator
//    val buttonIconNameIdPairListStr = CmdClickIcons.values().reversed().map {
//        "${it.str}${buttonIconNameIdSeparator}${it.str}"
//    }.joinToString(buttonIconSeparator)

//    private val setVariableValueForNoScrollSaveUrlsFilePath =
//        "$listPathForListConSlSpi=$noScrollSaveUrlsFilePath"
//    val setVariableForSettingHolder: List<String> = listOf(
//        "$SHELL_EXEC_ENV:CB=$shellExecEnvUbuntu?$shellExecEnvTermux",
//        "$UBUNTU_EXEC_MODE:CB=$ubuntuExecModeSelectsBackground?$ubuntuExecModeSelectsForeground",
//        "$UBUNTU_OUTPUT_FILE:CB=${UsePath.cmdClickMonitorFileName_1}?${UsePath.cmdClickMonitorFileName_2}?${UsePath.cmdClickMonitorFileName_3}?${UsePath.cmdClickMonitorFileName_4}",
//        "$TERMINAL_DO:CB=$terminalOn?$terminalTermux?$terminalOff",
//        "$EDIT_EXECUTE:CB=$editExecuteNo?$editExecuteAlways",
////        "$TERMINAL_SIZE_TYPE:CB=$terminalSizeTypeOff?$terminalSizeTypeShort?$terminalSizeTypeLong",
//        "$TERMINAL_OUTPUT_MODE:CB=$terminalOutPutModeNormal?$terminalOutPutModeReflash?$terminalOutPutModeReflashAndFirstRow?$terminalOutPutModeDebug?$terminalOutPutModeNo",
//        "$ON_UPDATE_LAST_MODIFY:CB=$onUpdateLastModifyOn?$onUpdateLastModifyOff",
//        "$CMDCLICK_ON_AUTO_EXEC:CB=$autoExecSelectsOn?$autoExecSelectsOff",
//        "$CMDCLICK_HISTORY_SWITCH:CB=$historySwitchOff?$historySwitchOn?$historySwitchInherit",
//        "$CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC:CB=$urlHistoryOrButtonExecUrlHistory?$urlHistoryOrButtonExecUrlButtonExec?$urlHistoryOrButtonExecUrlInherit",
//        "$ON_URL_LAUNCH_MACRO:TXT:ECB=$onUrlLaunchMacroOff?$onUrlLaunchMacroRecent?$onUrlLaunchMacroFrequency",
//        "$ON_ADBLOCK:CB=$onAdBlockInherit?$onAdBlockOff?$onAdBlockOn",
//        "${ON_LAUNCH_URL_HISTORY_BY_BACKSTACK}:CB=${onLaunchUrlHistoryByBackstackOff}?${onLaunchUrlHistoryByBackstackOn}",
//        "$ON_TERM_BACKEND_WHEN_START:CB=$onTermBackendWhenStartSelectsInherit?$onTermBackendWhenStartSelectsOff?$onTermBackendWhenStartSelectsOn",
//        "$ON_TERM_VISIBLE_WHEN_KEYBOARD:CB=$onTermVisibleWhenKeyboardSelectsInherit?$onTermVisibleWhenKeyboardSelectsOn?$onTermVisibleWhenKeyboardSelectsOff",
//        "$ON_TERM_SHORT_WHEN_LOAD:CB=$onTermShortWhenLoadSelectsInherit?$onTermShortWhenLoadSelectsOn?$onTermShortWhenLoadSelectsOff",
//        "$DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT:CB=$disableShowToolbarWhenHighlightOn?$disableShowToolbarWhenHighlightOff",
//        "$ON_URL_HISTORY_REGISTER:CB=$onUrlHistoryRegisterOn?$onUrlHistoryRegisterOff",
//        "$DEFAULT_MONITOR_FILE:CB=${UsePath.cmdClickMonitorFileName_1}?${UsePath.cmdClickMonitorFileName_2}?${UsePath.cmdClickMonitorFileName_3}?${UsePath.cmdClickMonitorFileName_4}",
//        "${ON_ROOTFS_SDCARD_SAVE}:CB=${onRootfsSdcardSaveOn}?${onRootfsSdcardSaveOff}",
//        "${UBUNTU_AUTO_SETUP}:CB=${ubuntuAutoSetupOff}?${ubuntuAutoSetupSetup}?${ubuntuAutoSetupRestore}",
//        "$CMDCLICK_TERMINAL_FONT_ZOOM:TXT:NUM=0..1000?1",
//        "$TERMINAL_COLOR:TXT:CLR=",
//        "$TERMINAL_FONT_COLOR:TXT:CLR=",
//        "$EXEC_JS_OR_HTML_PATH:TXT:FL=",
////        "$HOME_SCRIPT_URLS_PATH:DSL:BTN=$setVaraibleValueForHomeScriptUrlsPath",
////        "$CMDCLICK_HOME_FANNELS_PATH:DSL:BTN=$setVariableValueForHomeFannelsPath",
//        "$SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH:DSL:BTN=$setVariableValueForSrcImageAnchorLongPressMenuFilePath",
//        "$SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH:DSL:BTN=$setVariableValueForSrcAnchorLongPressMenuFilePath",
//        "$IMAGE_LONG_PRESS_MENU_FILE_PATH:DSL:BTN=$setVariableValueForImageLongPressMenuFilePath",
//        "$NO_SCROLL_SAVE_URLS:DSL=$setVariableValueForNoScrollSaveUrlsFilePath",
//        "$UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF:TXT:NUM=0..60?10"
//    )

    fun makeShellScriptName(
//        shellOrJs: LanguageTypeSelects = LanguageTypeSelects.JAVA_SCRIPT
    ): String {
        val scriptSuffix = UsePath.JS_FILE_SUFFIX
//        when(shellOrJs){
//            LanguageTypeSelects.SHELL_SCRIPT -> UsePath.SHELL_FILE_SUFFIX
//            else -> UsePath.JS_FILE_SUFFIX
//        }
        return (1..10000).random().toString() + scriptSuffix
    }

    fun makeRndPrefix(): String {
        return (1..10000).random().toString()
    }

    private val settingVariableStr = "Setting variables"
    private val mdDash = "-------"
    private val mdDescription = "description"

//     |#  * [$CMDCLICK_HISTORY_SWITCH](#${replaceLowerAdnBlankDash(CMDCLICK_HISTORY_SWITCH)})
//    |#  * [$CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC](#${
//            replaceLowerAdnBlankDash(CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC)})
//    |# ### $CMDCLICK_HISTORY_SWITCH
//            |# Switch app history with url history
//            |
//            |# | switch | $mdDescription |
//            |# | $mdDash | $mdDash |
//            |# | `$historySwitchOn` | switch |
//            |# | `$historySwitchOff` | no switch |
//            |# | `$historySwitchInherit` | inherit config setting (default) |
//            |
//    |# ### $CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC
//            |# Switch url history or button script exec
//            |
//            |# | switch | description |
//            |# | $mdDash | $mdDash |
//            |# | `$urlHistoryOrButtonExecUrlInherit` | inherit config setting |
//            |# | `$urlHistoryOrButtonExecUrlHistory` | switch url history |
//            |# | `$urlHistoryOrButtonExecUrlButtonExec` | switch url button script exec |
//            |

    fun makeDescription(): String {
        return """# Table of Contents
            |# $mdDash
            |# * [$settingVariableStr](#${replaceLowerAdnBlankDash(settingVariableStr)})
       
            |#  * [$UBUNTU_EXEC_MODE](#${replaceLowerAdnBlankDash(UBUNTU_EXEC_MODE)})
            |#  * [$UBUNTU_OUTPUT_FILE](#${replaceLowerAdnBlankDash(UBUNTU_OUTPUT_FILE)})
            |#  * [$TERMINAL_OUTPUT_MODE](#${replaceLowerAdnBlankDash(TERMINAL_OUTPUT_MODE)})
            |#  * [$CMDCLICK_ON_AUTO_EXEC](#${replaceLowerAdnBlankDash(CMDCLICK_ON_AUTO_EXEC)})
            |#  * [$ON_UPDATE_LAST_MODIFY](#${replaceLowerAdnBlankDash(ON_UPDATE_LAST_MODIFY)})
            |#  * [$IGNORE_HISTORY_PATHS](#${replaceLowerAdnBlankDash(IGNORE_HISTORY_PATHS)})
            |#  * [$EDIT_BOX_TITLE_CONFIG](#${replaceLowerAdnBlankDash(EDIT_BOX_TITLE_CONFIG)})
            |#  * [$SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH](#${
            replaceLowerAdnBlankDash(
                SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH)})
            |#  * [$SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH](#${
            replaceLowerAdnBlankDash(SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH)})
            |#  * [$IMAGE_LONG_PRESS_MENU_FILE_PATH](#${
            replaceLowerAdnBlankDash(IMAGE_LONG_PRESS_MENU_FILE_PATH)})
            |#  * [$ON_ADBLOCK](#${replaceLowerAdnBlankDash(ON_ADBLOCK)})
            |#  * [$ON_TERM_BACKEND_WHEN_START](#${
            replaceLowerAdnBlankDash(ON_TERM_BACKEND_WHEN_START)})
            |#  * [$ON_TERM_VISIBLE_WHEN_KEYBOARD](#${
            replaceLowerAdnBlankDash(ON_TERM_VISIBLE_WHEN_KEYBOARD)})
            |#  * [$ON_TERM_SHORT_WHEN_LOAD](#${replaceLowerAdnBlankDash(ON_TERM_SHORT_WHEN_LOAD)})
            |#  * [$DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT](#${
            replaceLowerAdnBlankDash(DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT)})
            |#  * [$ON_URL_LAUNCH_MACRO](#${replaceLowerAdnBlankDash(ON_URL_LAUNCH_MACRO)})
            |#  * [$ON_URL_HISTORY_REGISTER](#${replaceLowerAdnBlankDash(ON_URL_HISTORY_REGISTER)})
            |#  * [$DEFAULT_MONITOR_FILE](#${replaceLowerAdnBlankDash(DEFAULT_MONITOR_FILE)})
            |#  * [$EXEC_JS_OR_HTML_PATH](#${replaceLowerAdnBlankDash(EXEC_JS_OR_HTML_PATH)})
            |#  * [$UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF](#${
            replaceLowerAdnBlankDash(UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF)})
            |#  * [$CMDCLICK_TERMINAL_FONT_ZOOM](#${replaceLowerAdnBlankDash(CMDCLICK_TERMINAL_FONT_ZOOM)})
            |#  * [$TERMINAL_FONT_COLOR](#${replaceLowerAdnBlankDash(TERMINAL_FONT_COLOR)})
            |#  * [$TERMINAL_COLOR](#${replaceLowerAdnBlankDash(TERMINAL_COLOR)})
            |
            |
            |# ## $settingVariableStr
            |# $mdDash
            |
            |# ### $UBUNTU_EXEC_MODE
            |# Set shell exec mode
            |
            |# | option | description |
            |# | $mdDash | $mdDash |
            |# | `$ubuntuExecModeSelectsBackground` | fast exec mode |
            |# | `$ubuntuExecModeSelectsForeground` | back ground exec mode |
            |
            |# ### $UBUNTU_OUTPUT_FILE
            |# Set ubuntu output file
            |
            |# | option | description |
            |# | $mdDash | $mdDash |
            |# | `${UsePath.cmdClickMonitorFileName_1}` | stdout |
            |# | `${UsePath.cmdClickMonitorFileName_2}` | strerr or syslog |
            |# | `${UsePath.cmdClickMonitorFileName_3}` | free |
            |# | `${UsePath.cmdClickMonitorFileName_4}` | /dev/null |
            |
            |# ### $TERMINAL_OUTPUT_MODE
            |# Decide output mode in cmdclick terminal (basically, only shellScript)
            |
            |# | output mode | $mdDescription |
            |# | $mdDash | $mdDash |
            |# | `$terminalOutPutModeNormal` | normal terminal output (default) |
            |# | `$terminalOutPutModeReflash` | Before terminal output, screen resflesh |
            |# | `$terminalOutPutModeReflashAndFirstRow` | Before terminal output, screen resflesh and focus first row |
            |# | `$terminalOutPutModeDebug` | stdr + stderr |
            |# | `$terminalOutPutModeNo` | no output (bacground exec) |
            |
            |# ### $CMDCLICK_ON_AUTO_EXEC
            |# Auto execute script when fannel launch
            |
            |# | switch | $mdDescription |
            |# | $mdDash | $mdDash |
            |# | `$autoExecSelectsOn` | start or end exec on |
            |# | `$autoExecSelectsOff` | exec off (default) |
            |
            |# ### $ON_UPDATE_LAST_MODIFY
            |# How updating file last modified status when executing
            |
            |# | switch | $mdDescription |
            |# | $mdDash | $mdDash |
            |# | `$onUpdateLastModifyOn` | update this (default) |
            |# | `$onUpdateLastModifyOff` | no update this |
            |
            |# ### $IGNORE_HISTORY_PATHS
            |# Ignore history path like grep -v
            |
            |# ### $EDIT_BOX_TITLE_CONFIG
            |# Edit box title
            |
            |# ### $SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH
            |# Src image ancher long press menu list
            |
            |# | Button type | usage | 
            |# | --------- | --------- |
            |# | DSL button | Drag and sort long press menu list |
            |# | ADD button | Add script to long press menu list |
            |
            |# ### $SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH
            |# Src ancher long press menu list
            |
            |# | Button type | usage | 
            |# | $mdDash | $mdDash |
            |# | DSL button | Drag and sort long press menu list |
            |# | ADD button | Add script to long press menu list |
            |
            |# ### $IMAGE_LONG_PRESS_MENU_FILE_PATH
            |# Image long press menu list
            |
            |# | Button type | usage | 
            |# | $mdDash | $mdDash |
            |# | DSL button | Drag and sort long press menu list |
            |# | ADD button | Add script to long press menu list |
            |
            |# | Button type | usage | 
            |# | $mdDash | $mdDash |
            |# | DSL button | Drag and sort home url script List |
            |# | ADD button | Add fannel to home url script list |
            |
            |# ### $ON_ADBLOCK
            |# Adblock switch
            |
            |# | switch | description | 
            |# | $mdDash | $mdDash |
            |# | `$onAdBlockInherit` | inherit config setting |
            |# | `$onAdBlockOn` | on |
            |# | `$onAdBlockOff` | off |
            |
            |# ### $ON_TERM_BACKEND_WHEN_START
            |# Display terminal backend when start 
            |
            |# | switch | description | 
            |# | $mdDash | $mdDash |
            |# | `$onTermBackendWhenStartSelectsInherit` | inherit config setting |
            |# | `$onTermBackendWhenStartSelectsOn` | on |
            |# | `$onTermBackendWhenStartSelectsOff` | off |
            |
            |# ### $ON_TERM_VISIBLE_WHEN_KEYBOARD
            |# terminal visible when keyboard open 
            |
            |# | switch | description | 
            |# | $mdDash | $mdDash |
            |# | `$onTermVisibleWhenKeyboardSelectsInherit` | inherit config setting |
            |# | `$onTermVisibleWhenKeyboardSelectsOn` | on |
            |# | `$onTermVisibleWhenKeyboardSelectsOff` | off |
            |
            |# ### $ON_TERM_SHORT_WHEN_LOAD
            |# Terminal short sizging when load 
            |
            |# | switch | description | 
            |# | $mdDash | $mdDash |
            |# | `$onTermShortWhenLoadSelectsInherit` | inherit config setting |
            |# | `$onTermShortWhenLoadSelectsOn` | on |
            |# | `$onTermShortWhenLoadSelectsOff` | off |
            |
            |# ### $DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT
            |# Disable show toolbar when highlight text in webview 
            |
            |# | switch | description | 
            |# | $mdDash | $mdDash |
            |# | `$disableShowToolbarWhenHighlightOff` | on |
            |# | `$disableShowToolbarWhenHighlightOn` | off |
            |
            |# ### $ON_URL_LAUNCH_MACRO
            |# Url launch macro(when set, cmdclick web terminal don't output)
            |
            |# | switch | description | 
            |# | $mdDash | $mdDash |
            |# | `$onUrlLaunchMacroOff` | no launch |
            |# | `$onUrlLaunchMacroRecent` | recent use url launch |
            |# | `$onUrlLaunchMacroFrequency` | most use url launch |
            |
            |# ### $ON_URL_HISTORY_REGISTER
            |# Url history update signal
            |
            |# | switch | description | 
            |# | $mdDash | $mdDash |
            |# | `$onUrlHistoryRegisterOn` | update |
            |# | `$onUrlHistoryRegisterOff` | no update |
            |
            |# ### $EXEC_JS_OR_HTML_PATH
            |# Execute javascript or html file path
            |# - disable, when $ON_URL_LAUNCH_MACRO is not $onUrlLaunchMacroOff
            |
            |# ### $DEFAULT_MONITOR_FILE
            |# Select default monitor file for screen
            |
            |# | switch | description | 
            |# | $mdDash | $mdDash |
            |# | `${UsePath.cmdClickMonitorFileName_1}` | stdout |
            |# | `${UsePath.cmdClickMonitorFileName_2}` | strerr or syslog |
            |# | `${UsePath.cmdClickMonitorFileName_3}` | free |
            |# | `${UsePath.cmdClickMonitorFileName_4}` | /dev/null |
            |
            |# ### $UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF
            |# Ubuntu Sleep delay minutes in screen off 
            |# - Config only value
            |# - 0 -> no sleep
            |
            |# ### $CMDCLICK_TERMINAL_FONT_ZOOM
            |# Adjust terminal font size (percentage)
            |
            |# ### $TERMINAL_FONT_COLOR
            |# Adjust terminal font color
            |
            |# ### $TERMINAL_COLOR
            |# Adjust terminal background color
            |
            |#
            |#
            |#
            |# Access bellow url about more detail
            |# https://github.com/puutaro/CommandClick/blob/master/DEVELOPER.md
            |#
            |#
            |#
        """.trimIndent().replace("EX_INDENT_BLANK", "        ")
    }

    private fun replaceLowerAdnBlankDash(
        targetStr: String,
    ): String {
        return targetStr
            .lowercase()
            .replace(" ", "-")
    }

//    //        |$NO_SCROLL_SAVE_URLS=""
//    |$CMDCLICK_HISTORY_SWITCH="$historySwitchInherit"
//    |$CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC="$urlHistoryOrButtonExecUrlInherit"

    fun makeScriptContents(
        onUpdateLastModifyValue: String,
//        shellOrJs: LanguageTypeSelects = LanguageTypeSelects.JAVA_SCRIPT,
        execJsOrHtmlPathValue: String = String(),
    ): String{
//        val languageTypeHolderMap = LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(shellOrJs)
        return """${CMDCLICK_SHIBAN_DEFAULT_VALUE}
        |
        |
        |${LABELING_SEC_START}
        |${makeDescription()}
        |${LABELING_SEC_END}
        |
        |
        |${SETTING_SEC_START}
        |$UBUNTU_EXEC_MODE="$UBUNTU_EXEC_MODE_DEFAULT_VALUE"
        |$UBUNTU_OUTPUT_FILE="$UBUNTU_OUTPUT_FILE_DEFAULT_VALUE"
        |$TERMINAL_DO="$TERMINAL_DO_DEFAULT_VALUE"
        |$EDIT_EXECUTE="$EDIT_EXECUTE_DEFAULT_VALUE"
        |$TERMINAL_OUTPUT_MODE="$TERMINAL_OUTPUT_MODE_DEFAULT_VALUE"
        |$ON_UPDATE_LAST_MODIFY="${onUpdateLastModifyValue}"
        |$ON_ADBLOCK="$onAdBlockInherit"
        |$ON_TERM_BACKEND_WHEN_START="$ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE"
        |$ON_TERM_VISIBLE_WHEN_KEYBOARD="$ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE"
        |$ON_TERM_SHORT_WHEN_LOAD="$ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE"
        |$SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH=""
        |$SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH=""
        |$IMAGE_LONG_PRESS_MENU_FILE_PATH=""
        |$ON_URL_HISTORY_REGISTER="$ON_URL_HISTORY_REGISTER_DEFAULT_VALUE"
        |$DEFAULT_MONITOR_FILE="$DEFAULT_MONITOR_FILE_DEFAULT_VALUE"
        |$IGNORE_HISTORY_PATHS=""
        |$EXEC_JS_OR_HTML_PATH="${execJsOrHtmlPathValue}"
        |$CMDCLICK_TERMINAL_FONT_ZOOM=""            
        |$TERMINAL_COLOR=""
        |$TERMINAL_FONT_COLOR=""
        |$SET_REPLACE_VARIABLE=""
        |$SET_VARIABLE_TYPE=""
        |$BEFORE_COMMAND=""
        |$AFTER_COMMAND=""
        |${SETTING_SEC_END}
        |
        |
        |${CMD_SEC_START}
        |${CMD_SEC_END}
        |
        |
        |${SCRIPT_START}
        |
        |
        |
    """.trimMargin()
    }


    fun makeShellOrJsFile(
//        dirPath: String,
        shellScriptName: String,
        onUpdateLastModifyValue: String = onUpdateLastModifyOn,
//        shellOrJs: LanguageTypeSelects = LanguageTypeSelects.JAVA_SCRIPT,
        execJsOrHtmlPathValue: String = String(),
    ) {
        val shellContents =  makeScriptContents(
            onUpdateLastModifyValue,
//            shellOrJs,
            execJsOrHtmlPathValue
        ).let {
//            if(shellOrJs != LanguageTypeSelects.JAVA_SCRIPT) return@let it
            it
                .substring(
                    it.indexOf('\n')+1
                )
                .split("\n")
                .map {
                    AltRegexTool.replacePrefix(
                        it,
                        "#",
                        "//"
                    )
//                    it.replace(
//                        Regex("^#"), "//"
//                    )
            }.joinToString("\n")
        }
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        FileSystems.createDirs(cmdclickDefaultAppDirPath)
        val createFile = File(
            cmdclickDefaultAppDirPath,
            shellScriptName
        )
        createFile.writeText(shellContents)
    }


    private fun makeAppDirScriptContents(
//        shellOrJs: LanguageTypeSelects = LanguageTypeSelects.JAVA_SCRIPT
    ): String{
//        val languageTypeHolderMap = LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(shellOrJs)
        return """
        |
        |
        |${LABELING_SEC_START}
        |${makeDescription()}
        |${LABELING_SEC_END}
        |
        |
    """.trimMargin()
            .split("\n")
            .map {
                AltRegexTool.replacePrefix(
                    it,
                    "#",
                    "//"
                )
//                it.replace(
//                    Regex("^#"), "//"
//                )
            }.joinToString("\n")
    }

    fun makeAppDirAdminFile(
        dirPath: String,
        scriptScriptName: String
    ) {
        FileSystems.createDirs(dirPath)
        val createFile = File(
            dirPath,
            scriptScriptName
        )
        if(createFile.isFile) return
        createFile.writeText(
            makeAppDirScriptContents()
        )
    }

    fun makeButtonExecJS(
//        dirPath: String,
        shellScriptName: String,
        execJsOrHtmlPathValue: String = String()
    ){
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
//        val shellOrJs = LanguageTypeSelects.JAVA_SCRIPT
        FileSystems.createDirs(cmdclickDefaultAppDirPath)
        if(
            File(
                cmdclickDefaultAppDirPath,
                shellScriptName
            ).isFile
        ) return
        makeShellOrJsFile(
//            dirPath,
            shellScriptName,
            onUpdateLastModifyOff,
//            shellOrJs,
            execJsOrHtmlPathValue
        )
    }

//    private fun makeAutoJsContents(
//    ): String {
//        val shellOrJs = LanguageTypeSelects.JAVA_SCRIPT
//        val languageTypeHolderMap = LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(shellOrJs)
//        return """
//        |
//        |
//        |${languageTypeHolderMap?.get(HolderTypeName.LABELING_SEC_START)}
//        |${makeDescription()}
//        |${languageTypeHolderMap?.get(HolderTypeName.LABELING_SEC_END)}
//        |
//        |
//        |${languageTypeHolderMap?.get(HolderTypeName.SETTING_SEC_START)}
//        |$TERMINAL_OUTPUT_MODE="$TERMINAL_OUTPUT_MODE_DEFAULT_VALUE"
//        |$CMDCLICK_ON_AUTO_EXEC="$CMDCLICK_ON_AUTO_EXEC_DEFAULT_VALUE"
//        |$ON_UPDATE_LAST_MODIFY="$onUpdateLastModifyOff"
//        |$CMDCLICK_HISTORY_SWITCH="$historySwitchInherit"
//        |$CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC="$urlHistoryOrButtonExecUrlInherit"
//        |$ON_ADBLOCK="$onAdBlockInherit"
//        |${DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT}="${disableShowToolbarWhenHighlightOff}"
//        |$ON_TERM_BACKEND_WHEN_START="$ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE"
//        |$ON_TERM_VISIBLE_WHEN_KEYBOARD="$ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE"
//        |$ON_TERM_SHORT_WHEN_LOAD="$ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE"
//        |$ON_URL_LAUNCH_MACRO="$onUrlLaunchMacroRecent"
//        |$DEFAULT_MONITOR_FILE="$DEFAULT_MONITOR_FILE_DEFAULT_VALUE"
//        |$SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH=""
//        |$SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH=""
//        |$IMAGE_LONG_PRESS_MENU_FILE_PATH=""
//        |$IGNORE_HISTORY_PATHS=""
//        |$EXEC_JS_OR_HTML_PATH=""
//        |$NO_SCROLL_SAVE_URLS=""
//        |$CMDCLICK_TERMINAL_FONT_ZOOM=""
//        |$TERMINAL_FONT_COLOR=""
//        |$TERMINAL_COLOR=""
//        |$HIDE_SETTING_VARIABLES="$ON_UPDATE_LAST_MODIFY"
//        |${languageTypeHolderMap?.get(HolderTypeName.SETTING_SEC_END)}
//        |
//        |
//        |${languageTypeHolderMap?.get(HolderTypeName.CMD_SEC_START)}
//        |${languageTypeHolderMap?.get(HolderTypeName.CMD_SEC_END)}
//        |
//        |
//        |${languageTypeHolderMap?.get(HolderTypeName.SCRIPT_START)}
//        |
//        |
//        |
//    """.trimMargin()
//            .split("\n")
//            .map {
//                it.replace(
//                    Regex("^#"), "//"
//                )
//            }.joinToString("\n")
//    }

//    fun makeAutoJsFile(
//        dirPath: String,
//        jsScriptName: String,
//    ) {
//        FileSystems.createDirs(dirPath)
//        if(
//            File(
//                dirPath,
//                jsScriptName
//            ).isFile
//        ) return
//        val shellContents = makeAutoJsContents()
//        val createFile = File(
//            dirPath,
//            jsScriptName
//        )
//        createFile.writeText(shellContents)
//    }
}