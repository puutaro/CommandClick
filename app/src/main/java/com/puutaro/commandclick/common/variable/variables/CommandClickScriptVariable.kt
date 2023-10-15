package com.puutaro.commandclick.common.variable.variables

import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingCmdArgs
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ButtonViewProducer
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.FileSystems
import java.io.File


object CommandClickScriptVariable {
    enum class HolderTypeName {
        LABELING_SEC_START,
        LABELING_SEC_END,
        SETTING_SEC_START,
        SETTING_SEC_END,
        CMD_SEC_START,
        CMD_SEC_END,
        SCRIPT_START
    }
    val EMPTY_STRING = "COMMAND_CLICK_EMPTY_STRING"
    private const val labelingSectionStartSentence = "LABELING_SECTION_START"
    private const val labelingSectionEndSentence = "LABELING_SECTION_END"
    private const val settingSectionStartSentence = "SETTING_SECTION_START"
    private const val settingSectionEndSentence = "SETTING_SECTION_END"
    private const val commandSectionStartSentence = "CMD_VARIABLE_SECTION_START"
    private const val commandSectionEndSentence = "CMD_VARIABLE_SECTION_END"
    val LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP = mapOf(
        LanguageTypeSelects.SHELL_SCRIPT to mapOf(
            HolderTypeName.LABELING_SEC_START to "### $labelingSectionStartSentence",
            HolderTypeName.LABELING_SEC_END to "### $labelingSectionEndSentence",
            HolderTypeName.SETTING_SEC_START to "### $settingSectionStartSentence",
            HolderTypeName.SETTING_SEC_END to "### $settingSectionEndSentence",
            HolderTypeName.CMD_SEC_START to "### $commandSectionStartSentence",
            HolderTypeName.CMD_SEC_END to "### $commandSectionEndSentence",
            HolderTypeName.SCRIPT_START to "### Please write bellow with shell script",

        ),
        LanguageTypeSelects.JAVA_SCRIPT to mapOf(
            HolderTypeName.LABELING_SEC_START to "/// $labelingSectionStartSentence",
            HolderTypeName.LABELING_SEC_END to "/// $labelingSectionEndSentence",
            HolderTypeName.SETTING_SEC_START to "/// $settingSectionStartSentence",
            HolderTypeName.SETTING_SEC_END to "/// $settingSectionEndSentence",
            HolderTypeName.CMD_SEC_START to "/// $commandSectionStartSentence",
            HolderTypeName.CMD_SEC_END to "/// $commandSectionEndSentence",
            HolderTypeName.SCRIPT_START to "/// Please write bellow with javascript",
        )
    )
    val SHELL_EXEC_ENV = "shellExecEnv"
    val UBUNTU_EXEC_MODE = "ubuntuExecMode"
    val UBUNTU_OUTPUT_FILE = "ubuntuOutputFile"
    val TERMINAL_DO = "terminalDo"
    val EDIT_EXECUTE = "editExecute"
    val TERMINAL_SIZE_TYPE = "terminalSizeType"
    val TERMINAL_OUTPUT_MODE = "terminalOutputMode"
    val SET_VARIABLE_TYPE = "setVariableTypes"
    val SET_REPLACE_VARIABLE = "setReplaceVariables"
    val ON_UPDATE_LAST_MODIFY = "onUpdateLastModify"
    val ON_URL_LAUNCH_MACRO = "onUrlLaunchMacro"
    val EXEC_JS_OR_HTML_PATH = "execJsOrHtmlPath"
    val EXEC_PLAY_BTN_LONG_PRESS = "execPlayBtnLongPress"
    val EXEC_EDIT_BTN_LONG_PRESS = "execEditBtnLongPress"
    val BEFORE_COMMAND = "beforeCommand"
    val AFTER_COMMAND = "afterCommand"
    val SCRIPT_FILE_NAME = "scriptFileName"
    val CMDCLICK_SHIBAN = "cmdclickShiban"
    val CMDCLICK_RUN_SHELL = "cmdclickRunShell"
    val CMDCLICK_ON_AUTO_EXEC = "onAutoExec"
    val ON_TERM_BACKEND_WHEN_START = "onTermBackendWhenStart"
    val ON_TERM_VISIBLE_WHEN_KEYBOARD = "onTermVisibleWhenKeyboard"
    val ON_TERM_SHORT_WHEN_LOAD = "onTermShortWhenLoad"
    val CMDCLICK_HISTORY_SWITCH = "historySwitch"
    val CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC = "urlHistoryOrButtonExec"
    val ON_URL_HISTORY_REGISTER = "onUrlHistoryRegister"
    val IGNORE_HISTORY_PATHS = "ignoreHistoryPaths"
    val ON_ADBLOCK = "onAdBlock"
    val DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT="disableShowToolbarWhenHighlight"
    val CMDCLICK_TERMINAL_FONT_ZOOM = "terminalFontZoom"
    val CMDCLICK_ON_HISTORY_URL_TITLE = "onHistoryUrlTitle"
    val TERMINAL_COLOR = "terminalColor"
    val TERMINAL_FONT_COLOR = "terminalFontColor"
    val STATUS_BAR_ICON_COLOR_MODE = "statusBarIconColorMode"
    val HOME_SCRIPT_URLS_PATH = "homeScriptUrlsPath"
    val CMDCLICK_SHIBAN_DEFAULT_VALUE = "#!/bin/bash"
    val CMDCLICK_RUN_SHELL_DEFAULT_VALUE = "bash"
    val CMDCLICK_LONG_PRESS_LINK_URL = "CMDCLICK_LONG_PRESS_LINK_URL"
    val CMDCLICK_LONG_PRESS_IMAGE_URL = "CMDCLICK_LONG_PRESS_IMAGE_URL"
    val CMDCLICK_CURRENT_PAGE_URL = "CMDCLICK_CURRENT_PAGE_URL"
    val CMDCLICK_HOME_FANNELS_PATH = "homeFannelsPath"
    val OVERRIDE_ITEM_CLICK_EXEC = "overrideItemClickExec"
    val PASS_CMDVARIABLE_EDIT = "passCmdVariableEdit"
    val DISABLE_SETTING_BUTTON = "disableSettingButton"
    val DISABLE_PLAY_BUTTON = "disablePlayButton"
    val DISABLE_EDIT_BUTTON = "disableEditButton"
    val HIDE_SETTING_VARIABLES = "hideSettingVariables"
    val NO_SCROLL_SAVE_URLS = "noScrollSaveUrls"
    val SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH = "srcImageAnchorLongPressMenuFilePath"
    val SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH = "srcAnchorLongPressMenuFilePath"
    val IMAGE_LONG_PRESS_MENU_FILE_PATH = "imageLongPressMenuFilePath"
    val UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF = "ubuntuSleepDelayMinInScreenOff"
    val DEFAULT_MONITOR_FILE = "defaultMonitorFile"

    val SETTING_VARIABLE_NAMES_LIST = listOf(
        SHELL_EXEC_ENV,
        UBUNTU_EXEC_MODE,
        UBUNTU_OUTPUT_FILE,
        TERMINAL_DO,
        EDIT_EXECUTE,
        TERMINAL_SIZE_TYPE,
        TERMINAL_OUTPUT_MODE,
        SET_VARIABLE_TYPE,
        SET_REPLACE_VARIABLE,
        CMDCLICK_HISTORY_SWITCH,
        CMDCLICK_ON_AUTO_EXEC,
        ON_UPDATE_LAST_MODIFY,
        TERMINAL_COLOR,
        TERMINAL_FONT_COLOR,
        STATUS_BAR_ICON_COLOR_MODE,
        CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
        ON_ADBLOCK,
        ON_TERM_BACKEND_WHEN_START,
        ON_TERM_VISIBLE_WHEN_KEYBOARD,
        ON_TERM_SHORT_WHEN_LOAD,
        DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT,
        UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF,
        CMDCLICK_TERMINAL_FONT_ZOOM,
        CMDCLICK_ON_HISTORY_URL_TITLE,
        CMDCLICK_RUN_SHELL,
        CMDCLICK_SHIBAN,
        ON_URL_LAUNCH_MACRO,
        EXEC_JS_OR_HTML_PATH,
        ON_URL_HISTORY_REGISTER,
        CMDCLICK_HOME_FANNELS_PATH,
        BEFORE_COMMAND,
        AFTER_COMMAND,
        SCRIPT_FILE_NAME,
        EXEC_PLAY_BTN_LONG_PRESS,
        EXEC_EDIT_BTN_LONG_PRESS,
        IGNORE_HISTORY_PATHS,
        HOME_SCRIPT_URLS_PATH,
        OVERRIDE_ITEM_CLICK_EXEC,
        DISABLE_SETTING_BUTTON,
        DISABLE_EDIT_BUTTON,
        DISABLE_PLAY_BUTTON,
        NO_SCROLL_SAVE_URLS,
        SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
        SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH,
        IMAGE_LONG_PRESS_MENU_FILE_PATH,
        DEFAULT_MONITOR_FILE,
    )
    private val terminalOn = SettingVariableSelects.TerminalDoSelects.ON.name
    private val terminalTermux = SettingVariableSelects.TerminalDoSelects.TERMUX.name
    private val terminalOff = SettingVariableSelects.TerminalDoSelects.OFF.name
    private val editExecuteNo = SettingVariableSelects.EditExecuteSelects.NO.name
    private val editExecuteOnce = SettingVariableSelects.EditExecuteSelects.ONCE.name
    private val editExecuteAlways = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
    private val terminalSizeTypeOff = SettingVariableSelects.TerminalSizeTypeSelects.OFF.name
    private val terminalSizeTypeShort = SettingVariableSelects.TerminalSizeTypeSelects.SHORT.name
    private val terminalSizeTypeLong = SettingVariableSelects.TerminalSizeTypeSelects.LONG.name
    private val autoExecSelectsOn = SettingVariableSelects.AutoExecSelects.ON.name
    private val autoExecSelectsOff = SettingVariableSelects.AutoExecSelects.OFF.name
    private val terminalOutPutModeNormal = SettingVariableSelects.TerminalOutPutModeSelects.NORMAL.name
    private val terminalOutPutModeReflash = SettingVariableSelects.TerminalOutPutModeSelects.REFLASH.name
    private val terminalOutPutModeReflashAndFirstRow = SettingVariableSelects.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
    private val terminalOutPutModeDebug = SettingVariableSelects.TerminalOutPutModeSelects.DEBUG.name
    private val terminalOutPutModeNo = SettingVariableSelects.TerminalOutPutModeSelects.NO.name
    private val historySwitchOff = SettingVariableSelects.HistorySwitchSelects.OFF.name
    private val historySwitchOn = SettingVariableSelects.HistorySwitchSelects.ON.name
    private val historySwitchInherit = SettingVariableSelects.HistorySwitchSelects.INHERIT.name
    private val urlHistoryOrButtonExecUrlHistory = SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name
    private val urlHistoryOrButtonExecUrlButtonExec = SettingVariableSelects.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name
    private val urlHistoryOrButtonExecUrlInherit = SettingVariableSelects.UrlHistoryOrButtonExecSelects.INHERIT.name
    private val onUpdateLastModifyOn = SettingVariableSelects.OnUpdateLastModifySelects.ON.name
    private val onUpdateLastModifyOff = SettingVariableSelects.OnUpdateLastModifySelects.OFF.name
    private val onHistoryUrlTitleON = SettingVariableSelects.OnHistoryUrlTitle.ON.name
    private val onHistoryUrlTitleOff = SettingVariableSelects.OnHistoryUrlTitle.OFF.name
    private val statusBarIconColorModeWhite = SettingVariableSelects.StatusBarIconColorModeSelects.WHITE.name
    private val statusBarIconColorModeBlack = SettingVariableSelects.StatusBarIconColorModeSelects.BLACK.name
    private val statusBarIconColorInherit = SettingVariableSelects.StatusBarIconColorModeSelects.INHERIT.name
    private val onUrlLaunchMacroRecent = SettingVariableSelects.OnUrlLaunchMacroSelects.RECENT.name
    private val onUrlLaunchMacroFrequency = SettingVariableSelects.OnUrlLaunchMacroSelects.FREQUENCY.name
    private val onUrlLaunchMacroOff = SettingVariableSelects.OnUrlLaunchMacroSelects.OFF.name
    private val onAdBlockInherit = SettingVariableSelects.OnAdblockSelects.INHERIT.name
    private val onAdBlockOn = SettingVariableSelects.OnAdblockSelects.ON.name
    private val onAdBlockOff = SettingVariableSelects.OnAdblockSelects.OFF.name
    private val shellExecEnvUbuntu = SettingVariableSelects.ShellExecEnvSelects.UBUNTU.name
    private val shellExecEnvTermux = SettingVariableSelects.ShellExecEnvSelects.TERMUX.name
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
    private val overrideItemClickExecSelectsOn = SettingVariableSelects.OnUrlHistoryRegisterSelects.ON.name
    private val overrideItemClickExecSelectsOff = SettingVariableSelects.OnUrlHistoryRegisterSelects.OFF.name
    private val disableSettingButtonOn = SettingVariableSelects.disableSettingButtonSelects.ON.name
    private val disableSettingButtonOff = SettingVariableSelects.disableSettingButtonSelects.OFF.name
    private val disableEditButtonOn = SettingVariableSelects.disableEditButtonSelects.ON.name
    private val disableEditButtonOff = SettingVariableSelects.disableEditButtonSelects.OFF.name
    private val disablePlayButtonOn = SettingVariableSelects.disablePlayButtonSelects.ON.name
    private val disablePlayButtonOff = SettingVariableSelects.disablePlayButtonSelects.OFF.name
    val SHELL_EXEC_ENV_DEFAULT_VALUE = shellExecEnvUbuntu
    val UBUNTU_EXEC_MODE_DEFAULT_VALUE = ubuntuExecModeSelectsBackground
    val UBUNTU_OUTPUT_FILE_DEFAULT_VALUE = UsePath.cmdClickMonitorFileName_1
    val TERMINAL_DO_DEFAULT_VALUE = terminalOn
    val EDIT_EXECUTE_DEFAULT_VALUE = editExecuteNo
    val TERMINAL_SIZE_TYPE_DEFAULT_VALUE = terminalSizeTypeOff
    val TERMINAL_OUTPUT_MODE_DEFAULT_VALUE = terminalOutPutModeNormal
    val HISTORY_SWITCH_DEFAULT_VALUE = historySwitchOff
    val ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE = onUpdateLastModifyOn
    val TERMINAL_COLOR_DEFAULT_VALUE = "#043d02"
    val TERMINAL_FONT_COLOR_DEFAULT_VALUE = "#f2f2f2"
    val STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE = statusBarIconColorModeWhite
    val ON_URL_LAUNCH_MACRO_DEFAULT_VALUE = onUrlLaunchMacroOff
    val ON_URL_HISTORY_REGISTER_DEFAULT_VALUE = onUrlHistoryRegisterOn
    val PASS_CMDVARIABLE_EDIT_ON_VALUE =  "ON"
    val ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE = onTermBackendWhenStartSelectsOff
    val ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE = onTermShortWhenLoadSelectsOff
    val CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE = 100
    val CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE = SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name
    val ON_ADBLOCK_DEFAULT_VALUE = SettingVariableSelects.OnAdblockSelects.OFF.name
    val CMDCLICK_ON_AUTO_EXEC_DEFAULT_VALUE =  SettingVariableSelects.AutoExecSelects.ON.name
    val ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE = SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.OFF.name
    val DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT_DEFAULT_VALUE = SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.OFF.name
    val DISABLE_WIDE_VIEW_PORT_DEFAULT_VALUE = SettingVariableSelects.DisableShowToolbarWhenHighlightSelects.OFF.name
    val CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE =  onHistoryUrlTitleOff
    val OVERRIDE_ITEM_CLICK_EXEC_DEFAULT_VALUE = overrideItemClickExecSelectsOn
    val DISABLE_SETTING_BUTTON_DEFAULT_VALUE = disableSettingButtonOff
    val DISABLE_EDIT_BUTTON_DEFAULT_VALUE = disableEditButtonOff
    val DISABLE_PLAY_BUTTON_DEFAULT_VALUE = disablePlayButtonOff
    val DEFAULT_MONITOR_FILE_DEFAULT_VALUE = UsePath.cmdClickMonitorFileName_1
    val cmdclickStartupJsName = UsePath.cmdclickStartupJsName
    val fannelDirName = CcPathTool.makeFannelDirName(
        cmdclickStartupJsName
    )
    private val homeScriptUrlsFilePath =
        UsePath.homeScriptUrlsFilePath
    private val homeFannelsFilePath =
        UsePath.homeFannelsFilePath

    private val buttonCmd = ButtonViewProducer.ButtonEditKey.cmd.name
    private val buttonLabel = ButtonViewProducer.ButtonEditKey.label.name
    private val buttonSetfType =
        ButtonViewProducer.SET_F_OPTION_MAP_KEY.type
    private val buttonSetfListAddSuffix =
        ButtonViewProducer.SET_F_OPTION_MAP_KEY.ListAdd.suffix.name
    private val buttonSetfListAddSourceDirPath =
        ButtonViewProducer.SET_F_OPTION_MAP_KEY.ListAdd.dirPath.name
    private val buttonSetfListAddHowFull =
        ButtonViewProducer.SET_F_OPTION_MAP_KEY.ListAdd.howFull.name
    private val listPathForListConSlSpi =
        ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
    private val limitNumForListConSlSpi =
        ListContentsSelectSpinnerViewProducer.ListContentsEditKey.limitNum.name
    private val setVaraibleValueForHomeScriptUrlsPath =
        "$listPathForListConSlSpi=" +
                "$homeScriptUrlsFilePath!$limitNumForListConSlSpi=20" +
                "|$buttonCmd=" +
                    listOf(
                        "setf $buttonSetfType=${SettingCmdArgs.ListAdd.name}",
                        "$buttonSetfListAddSuffix=.js&.sh&.html",
                        "$buttonSetfListAddSourceDirPath=\${01}",
                        "$buttonSetfListAddHowFull=on!$buttonLabel=ADD"
                    ).joinToString(" ")
    private val setVariableValueForHomeFannelsPath =
        "$listPathForListConSlSpi=" +
                "$homeFannelsFilePath!$limitNumForListConSlSpi=20" +
                "|$buttonCmd=" +
                listOf(
                    "setf $buttonSetfType=${SettingCmdArgs.ListAdd.name}",
                    "$buttonSetfListAddSuffix=${UsePath.JS_FILE_SUFFIX}",
                    "$buttonSetfListAddSourceDirPath=${UsePath.cmdclickAppHistoryDirAdminPath}" +
                            "!$buttonLabel=ADD"
                ).joinToString(" ")
    private val srcImageAnchorLongPressMenuFilePath =
        UsePath.srcImageAnchorLongPressMenuFilePath
    private val srcAnchorLongPressMenuFilePath =
        UsePath.srcAnchorLongPressMenuFilePath
    private val imageLongPressMenuFilePath =
        UsePath.imageLongPressMenuFilePath
    private val noScrollSaveUrlsFilePath =
        UsePath.noScrollSaveUrlsFilePath
    private val setVariableValueForSrcImageAnchorLongPressMenuFilePath =
        "$listPathForListConSlSpi=" +
                "$srcImageAnchorLongPressMenuFilePath!$limitNumForListConSlSpi=20" +
                "|$buttonCmd=" +
                listOf(
                    "setf $buttonSetfType=${SettingCmdArgs.ListAdd.name}",
                    "$buttonSetfListAddSuffix=${UsePath.JS_FILE_SUFFIX}",
                    "$buttonSetfListAddSourceDirPath=\${01}" +
                            "!$buttonLabel=ADD"
                ).joinToString(" ")
    private val setVariableValueForSrcAnchorLongPressMenuFilePath =
        "$listPathForListConSlSpi=" +
                "$srcAnchorLongPressMenuFilePath!$limitNumForListConSlSpi=20" +
                "|$buttonCmd=" +
                listOf(
                    "setf $buttonSetfType=${SettingCmdArgs.ListAdd.name}",
                    "$buttonSetfListAddSuffix=${UsePath.JS_FILE_SUFFIX}",
                    "$buttonSetfListAddSourceDirPath=\${01}" +
                            "!$buttonLabel=ADD"
                ).joinToString(" ")
    private val setVariableValueForImageLongPressMenuFilePath =
        "$listPathForListConSlSpi=" +
                "$imageLongPressMenuFilePath!$limitNumForListConSlSpi=20" +
                "|$buttonCmd=" +
                listOf(
                    "setf $buttonSetfType=${SettingCmdArgs.ListAdd.name}",
                    "$buttonSetfListAddSuffix=${UsePath.JS_FILE_SUFFIX}",
                    "$buttonSetfListAddSourceDirPath=\${01}" +
                            "!$buttonLabel=ADD"
                ).joinToString(" ")
    private val setVariableValueForNoScrollSaveUrlsFilePath =
        "$listPathForListConSlSpi=$noScrollSaveUrlsFilePath"
    val setVariableForSettingHolder: List<String> = listOf(
        "$SHELL_EXEC_ENV:CB=$shellExecEnvUbuntu!$shellExecEnvTermux",
        "$UBUNTU_EXEC_MODE:CB=$ubuntuExecModeSelectsBackground!$ubuntuExecModeSelectsForeground",
        "$UBUNTU_OUTPUT_FILE:CB=${UsePath.cmdClickMonitorFileName_1}!${UsePath.cmdClickMonitorFileName_2}!${UsePath.cmdClickMonitorFileName_3}!${UsePath.cmdClickMonitorFileName_4}",
        "$TERMINAL_DO:CB=$terminalOn!$terminalTermux!$terminalOff",
        "$EDIT_EXECUTE:CB=$editExecuteNo!$editExecuteOnce!$editExecuteAlways",
        "$TERMINAL_SIZE_TYPE:CB=$terminalSizeTypeOff!$terminalSizeTypeShort!$terminalSizeTypeLong",
        "$TERMINAL_OUTPUT_MODE:CB=$terminalOutPutModeNormal!$terminalOutPutModeReflash!$terminalOutPutModeReflashAndFirstRow!$terminalOutPutModeDebug!$terminalOutPutModeNo",
        "$ON_UPDATE_LAST_MODIFY:CB=$onUpdateLastModifyOn!$onUpdateLastModifyOff",
        "$CMDCLICK_ON_AUTO_EXEC:CB=$autoExecSelectsOn!$autoExecSelectsOff",
        "$CMDCLICK_ON_HISTORY_URL_TITLE:CB=$onHistoryUrlTitleON!$onHistoryUrlTitleOff",
        "$CMDCLICK_HISTORY_SWITCH:CB=$historySwitchOff!$historySwitchOn!$historySwitchInherit",
        "$CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC:CB=$urlHistoryOrButtonExecUrlHistory!$urlHistoryOrButtonExecUrlButtonExec!$urlHistoryOrButtonExecUrlInherit",
        "$STATUS_BAR_ICON_COLOR_MODE:CB=$statusBarIconColorModeWhite!$statusBarIconColorModeBlack!$statusBarIconColorInherit",
        "$ON_URL_LAUNCH_MACRO:TXT:ECB=$onUrlLaunchMacroOff!$onUrlLaunchMacroRecent!$onUrlLaunchMacroFrequency",
        "$ON_ADBLOCK:CB=$onAdBlockInherit!$onAdBlockOff!$onAdBlockOn",
        "$ON_TERM_BACKEND_WHEN_START:CB=$onTermBackendWhenStartSelectsInherit!$onTermBackendWhenStartSelectsOff!$onTermBackendWhenStartSelectsOn",
        "$ON_TERM_VISIBLE_WHEN_KEYBOARD:CB=$onTermVisibleWhenKeyboardSelectsInherit!$onTermVisibleWhenKeyboardSelectsOn!$onTermVisibleWhenKeyboardSelectsOff",
        "$ON_TERM_SHORT_WHEN_LOAD:CB=$onTermShortWhenLoadSelectsInherit!$onTermShortWhenLoadSelectsOn!$onTermShortWhenLoadSelectsOff",
        "$DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT:CB=$disableShowToolbarWhenHighlightOn!$disableShowToolbarWhenHighlightOff",
        "$ON_URL_HISTORY_REGISTER:CB=$onUrlHistoryRegisterOn!$onUrlHistoryRegisterOff",
        "$OVERRIDE_ITEM_CLICK_EXEC:CB=$overrideItemClickExecSelectsOff!$overrideItemClickExecSelectsOn",
        "$DISABLE_SETTING_BUTTON:CB=$disableSettingButtonOff!$disableSettingButtonOn",
        "$DISABLE_EDIT_BUTTON:CB=$disableEditButtonOff!$disableEditButtonOn",
        "$DISABLE_PLAY_BUTTON:CB=$disablePlayButtonOff!$disablePlayButtonOn",
        "$DEFAULT_MONITOR_FILE:CB=${UsePath.cmdClickMonitorFileName_1}!${UsePath.cmdClickMonitorFileName_2}!${UsePath.cmdClickMonitorFileName_3}!${UsePath.cmdClickMonitorFileName_4}",
        "$CMDCLICK_TERMINAL_FONT_ZOOM:TXT:NUM=0..1000!1",
        "$TERMINAL_COLOR:TXT:CLR=",
        "$TERMINAL_FONT_COLOR:TXT:CLR=",
        "$EXEC_JS_OR_HTML_PATH:TXT:FL=",
        "$EXEC_PLAY_BTN_LONG_PRESS:TXT:FL=",
        "$EXEC_EDIT_BTN_LONG_PRESS:TXT:FL=",
        "$HOME_SCRIPT_URLS_PATH:DSL:BTN=$setVaraibleValueForHomeScriptUrlsPath",
        "$CMDCLICK_HOME_FANNELS_PATH:DSL:BTN=$setVariableValueForHomeFannelsPath",
        "$SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH:DSL:BTN=$setVariableValueForSrcImageAnchorLongPressMenuFilePath",
        "$SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH:DSL:BTN=$setVariableValueForSrcAnchorLongPressMenuFilePath",
        "$IMAGE_LONG_PRESS_MENU_FILE_PATH:DSL:BTN=$setVariableValueForImageLongPressMenuFilePath",
        "$NO_SCROLL_SAVE_URLS:DSL=$setVariableValueForNoScrollSaveUrlsFilePath",
        "$UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF:TXT:NUM=0..60!10"
    )

    fun makeShellScriptName(
        shellOrJs: LanguageTypeSelects = LanguageTypeSelects.JAVA_SCRIPT
    ): String {
        val scriptSuffix = when(shellOrJs){
            LanguageTypeSelects.SHELL_SCRIPT -> UsePath.SHELL_FILE_SUFFIX
            else -> UsePath.JS_FILE_SUFFIX
        }
        return (1..10000).random().toString() + scriptSuffix
    }

    fun makeCopyPrefix(): String {
        return (1..10000).random().toString()
    }

    private val colons = "::"
    private val settingVariableStr = "Setting variables"
    private val mdDash = "-------"
    private val mdDescription = "description"

    fun makeDescription(): String {
        return """# Table of Contents
            |# $mdDash
            |# * [$settingVariableStr](#${replaceLowerAdnBlankDash(settingVariableStr)})
            |#  * [$SCRIPT_FILE_NAME](#${replaceLowerAdnBlankDash(SCRIPT_FILE_NAME)})
            |#  * [$SHELL_EXEC_ENV](#${replaceLowerAdnBlankDash(SHELL_EXEC_ENV)})
            |#  * [$UBUNTU_EXEC_MODE](#${replaceLowerAdnBlankDash(UBUNTU_EXEC_MODE)})
            |#  * [$UBUNTU_OUTPUT_FILE](#${replaceLowerAdnBlankDash(UBUNTU_OUTPUT_FILE)})
	        |#  * [$TERMINAL_SIZE_TYPE](#${replaceLowerAdnBlankDash(TERMINAL_SIZE_TYPE)})
            |#  * [$TERMINAL_OUTPUT_MODE](#${replaceLowerAdnBlankDash(TERMINAL_OUTPUT_MODE)})
            |#  * [$CMDCLICK_ON_AUTO_EXEC](#${replaceLowerAdnBlankDash(CMDCLICK_ON_AUTO_EXEC)})
            |#  * [$ON_UPDATE_LAST_MODIFY](#${replaceLowerAdnBlankDash(ON_UPDATE_LAST_MODIFY)})
            |#  * [$IGNORE_HISTORY_PATHS](#${replaceLowerAdnBlankDash(IGNORE_HISTORY_PATHS)})
            |#  * [$CMDCLICK_HISTORY_SWITCH](#${replaceLowerAdnBlankDash(CMDCLICK_HISTORY_SWITCH)})
            |#  * [$CMDCLICK_ON_HISTORY_URL_TITLE](#${
            replaceLowerAdnBlankDash(
                CMDCLICK_ON_HISTORY_URL_TITLE
            )
        })
            |#  * [$SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH](#${
            replaceLowerAdnBlankDash(
                SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH
            )
        })
            |#  * [$SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH](#${
            replaceLowerAdnBlankDash(
                SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH
            )
        })
            |#  * [$IMAGE_LONG_PRESS_MENU_FILE_PATH](#${
            replaceLowerAdnBlankDash(
                IMAGE_LONG_PRESS_MENU_FILE_PATH
            )
        })
            |#  * [$HOME_SCRIPT_URLS_PATH](#${replaceLowerAdnBlankDash(HOME_SCRIPT_URLS_PATH)})
            |#  * [$CMDCLICK_HOME_FANNELS_PATH](#${
            replaceLowerAdnBlankDash(
                CMDCLICK_HOME_FANNELS_PATH
            )
        })
            |#  * [$CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC](#${
            replaceLowerAdnBlankDash(
                CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC
            )
        })
            |#  * [$ON_ADBLOCK](#${replaceLowerAdnBlankDash(ON_ADBLOCK)})
            |#  * [$ON_TERM_BACKEND_WHEN_START](#${
            replaceLowerAdnBlankDash(
                ON_TERM_BACKEND_WHEN_START
            )
        })
            |#  * [$ON_TERM_VISIBLE_WHEN_KEYBOARD](#${
            replaceLowerAdnBlankDash(
                ON_TERM_VISIBLE_WHEN_KEYBOARD
            )
        })
            |#  * [$ON_TERM_SHORT_WHEN_LOAD](#${replaceLowerAdnBlankDash(ON_TERM_SHORT_WHEN_LOAD)})
            |#  * [$DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT](#${
            replaceLowerAdnBlankDash(
                DISABLE_SHOW_TOOLBAR_WHEN_HIGHLIGHT
            )
        })
            |#  * [$ON_URL_LAUNCH_MACRO](#${replaceLowerAdnBlankDash(ON_URL_LAUNCH_MACRO)})
            |#  * [$ON_URL_HISTORY_REGISTER](#${replaceLowerAdnBlankDash(ON_URL_HISTORY_REGISTER)})
            |#  * [$DEFAULT_MONITOR_FILE](#${replaceLowerAdnBlankDash(DEFAULT_MONITOR_FILE)})
            |#  * [$EXEC_JS_OR_HTML_PATH](#${replaceLowerAdnBlankDash(EXEC_JS_OR_HTML_PATH)})
            |#  * [$UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF](#${
            replaceLowerAdnBlankDash(
                UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF
            )
        })
            |#  * [$CMDCLICK_TERMINAL_FONT_ZOOM](#${
            replaceLowerAdnBlankDash(
                CMDCLICK_TERMINAL_FONT_ZOOM
            )
        })
            |#  * [$TERMINAL_FONT_COLOR](#${replaceLowerAdnBlankDash(TERMINAL_FONT_COLOR)})
            |#  * [$TERMINAL_COLOR](#${replaceLowerAdnBlankDash(TERMINAL_COLOR)})
            |#  * [$EXEC_PLAY_BTN_LONG_PRESS](#${replaceLowerAdnBlankDash(EXEC_PLAY_BTN_LONG_PRESS)})
            |#  * [$EXEC_EDIT_BTN_LONG_PRESS](#${replaceLowerAdnBlankDash(EXEC_EDIT_BTN_LONG_PRESS)})
            |
            |
            |# ## $settingVariableStr
            |# $mdDash
            |# 
            |# ### $SCRIPT_FILE_NAME
            |# Rename script name
            |
            |# ### $SHELL_EXEC_ENV
            |# Set shell exec enviroment
            |
            |# | option | descripton |
            |# | $mdDash | $mdDash |
            |# | `$shellExecEnvUbuntu` | ubuntu |
            |# | `$shellExecEnvTermux` | termux |
            |
            |# ### $UBUNTU_EXEC_MODE
            |# Set shell exec mode
            |
            |# | option | descripton |
            |# | $mdDash | $mdDash |
            |# | `$ubuntuExecModeSelectsBackground` | fast exec mode |
            |# | `$ubuntuExecModeSelectsForeground` | back ground exec mode |
            |
            |# ### $UBUNTU_OUTPUT_FILE
            |# Set ubuntu output file
            |
            |# | option | descripton |
            |# | $mdDash | $mdDash |
            |# | `${UsePath.cmdClickMonitorFileName_1}` | stdout |
            |# | `${UsePath.cmdClickMonitorFileName_2}` | strerr or syslog |
            |# | `${UsePath.cmdClickMonitorFileName_3}` | free |
            |# | `${UsePath.cmdClickMonitorFileName_4}` | /dev/null |
            |
            |# ### $TERMINAL_SIZE_TYPE
            |# `cmdclick terminal` size option
            |
            |# | option | descripton |
            |# | $mdDash | $mdDash |
            |# | `$terminalSizeTypeOff` | no adjust (default) |
            |# | `$terminalSizeTypeLong` | LongSize |
            |# | `$terminalSizeTypeShort` | ShortSize |
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
            |# ### $CMDCLICK_HISTORY_SWITCH
            |# Switch app history with url history
            |
            |# | switch | $mdDescription |
            |# | $mdDash | $mdDash |
            |# | `$historySwitchOn` | switch |
            |# | `$historySwitchOff` | no switch |
            |# | `$historySwitchInherit` | inherit config setting (default) |
            |
            |# ### $CMDCLICK_ON_HISTORY_URL_TITLE
            |# How adding url title to history
            |
            |# | switch | $mdDescription |
            |# | $mdDash | $mdDash |
            |# | `$onHistoryUrlTitleON` | add |
            |# | `$onHistoryUrlTitleOff` | no |
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
            |# ### $HOME_SCRIPT_URLS_PATH
            |# Specified script, url and html put always bottom in url history
            |# ex) $HOME_SCRIPT_URLS_PATH=$\{file_path}
            |
            |# | Button type | usage | 
            |# | $mdDash | $mdDash |
            |# | DSL button | Drag and sort home url script List |
            |# | ADD button | Add fannel to home url script list |
            |
            |# ### $CMDCLICK_HOME_FANNELS_PATH
            |# Specified fannels put always bottom in app history 
            |# ex) $CMDCLICK_HOME_FANNELS_PATH=$\{file_path}
            |
            |# | Button type | usage | 
            |# | $mdDash | $mdDash |
            |# | DSL button | Drag and sort home fannels list |
            |# | ADD button | Add fannel to home fannel list |
            |
            |# ### $CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC
            |# Switch url history or button script exec
            |
            |# | switch | description | 
            |# | $mdDash | $mdDash |
            |# | `$urlHistoryOrButtonExecUrlInherit` | inherit config setting |
            |# | `$urlHistoryOrButtonExecUrlHistory` | switch url history |
            |# | `$urlHistoryOrButtonExecUrlButtonExec` | switch url button script exec |
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
            |# ### $EXEC_PLAY_BTN_LONG_PRESS
            |# Execute when play button long press in editExecute=EditExecute
            |
            |# | type or name | description |
            |# | ------- | ------- |
            |# | `WEB_SEARCH` | apear web search bar |
            |# | `PAGE_SEARCH` | apear page search bar |
            |# | js file path | execute js file |
            |
            |# ### $EXEC_EDIT_BTN_LONG_PRESS
            |# Execute when edit button long press
            |
            |# | type or name | description |
            |# | ------- | ------- |
            |# | `WEB_SEARCH` | apear web search bar |
            |# | `PAGE_SEARCH` | apear page search bar |
            |# | js file path | execute js file |
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

    fun makeScriptContents(
        shiban: String,
        shellScriptName: String,
        onUpdateLastModifyValue: String,
        shellOrJs: LanguageTypeSelects = LanguageTypeSelects.JAVA_SCRIPT,
        execJsOrHtmlPathValue: String = String(),
    ): String{
        val languageTypeHolderMap = LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(shellOrJs)
        return """${shiban}
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.LABELING_SEC_START)}
        |${makeDescription()}
        |${languageTypeHolderMap?.get(HolderTypeName.LABELING_SEC_END)}
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.SETTING_SEC_START)}
        |$SHELL_EXEC_ENV="$SHELL_EXEC_ENV_DEFAULT_VALUE"
        |$UBUNTU_EXEC_MODE="$UBUNTU_EXEC_MODE_DEFAULT_VALUE"
        |$UBUNTU_OUTPUT_FILE="$UBUNTU_OUTPUT_FILE_DEFAULT_VALUE"
        |$TERMINAL_DO="$TERMINAL_DO_DEFAULT_VALUE"
        |$EDIT_EXECUTE="$EDIT_EXECUTE_DEFAULT_VALUE"
        |$TERMINAL_SIZE_TYPE="$TERMINAL_SIZE_TYPE_DEFAULT_VALUE"
        |$TERMINAL_OUTPUT_MODE="$TERMINAL_OUTPUT_MODE_DEFAULT_VALUE"
        |$ON_UPDATE_LAST_MODIFY="${onUpdateLastModifyValue}"
        |$CMDCLICK_HISTORY_SWITCH="$historySwitchInherit"
        |$CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC="$urlHistoryOrButtonExecUrlInherit"
        |$ON_ADBLOCK="$onAdBlockInherit"
        |$ON_TERM_BACKEND_WHEN_START="$ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE"
        |$ON_TERM_VISIBLE_WHEN_KEYBOARD="$ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE"
        |$ON_TERM_SHORT_WHEN_LOAD="$ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE"
        |$SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH="$srcImageAnchorLongPressMenuFilePath"
        |$SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH="$srcAnchorLongPressMenuFilePath
        |$IMAGE_LONG_PRESS_MENU_FILE_PATH="$imageLongPressMenuFilePath
        |$NO_SCROLL_SAVE_URLS="$noScrollSaveUrlsFilePath"
        |$OVERRIDE_ITEM_CLICK_EXEC="$OVERRIDE_ITEM_CLICK_EXEC_DEFAULT_VALUE
        |$ON_URL_HISTORY_REGISTER="$ON_URL_HISTORY_REGISTER_DEFAULT_VALUE"
        |$DEFAULT_MONITOR_FILE="$DEFAULT_MONITOR_FILE_DEFAULT_VALUE"
        |$IGNORE_HISTORY_PATHS=""
        |$EXEC_JS_OR_HTML_PATH="${execJsOrHtmlPathValue}"
        |$CMDCLICK_TERMINAL_FONT_ZOOM=""            
        |$TERMINAL_COLOR=""
        |$TERMINAL_FONT_COLOR=""
        |$EXEC_PLAY_BTN_LONG_PRESS=""
        |$EXEC_EDIT_BTN_LONG_PRESS=""
        |$SET_REPLACE_VARIABLE=""
        |$SET_VARIABLE_TYPE=""
        |$BEFORE_COMMAND=""
        |$AFTER_COMMAND=""
        |$SCRIPT_FILE_NAME="${shellScriptName}"
        |${languageTypeHolderMap?.get(HolderTypeName.SETTING_SEC_END)}
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.CMD_SEC_START)}
        |${languageTypeHolderMap?.get(HolderTypeName.CMD_SEC_END)}
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.SCRIPT_START)}
        |
        |
        |
    """.trimMargin()
    }


    fun makeShellOrJsFile(
        shiban: String,
        dirPath: String,
        shellScriptName: String,
        onUpdateLastModifyValue: String = onUpdateLastModifyOn,
        shellOrJs: LanguageTypeSelects = LanguageTypeSelects.JAVA_SCRIPT,
        execJsOrHtmlPathValue: String = String(),
    ) {
        val shellContents =  makeScriptContents(
            shiban,
            shellScriptName,
            onUpdateLastModifyValue,
            shellOrJs,
            execJsOrHtmlPathValue
        ).let {
            if(shellOrJs != LanguageTypeSelects.JAVA_SCRIPT) return@let it
            it
                .substring(
                    it.indexOf('\n')+1
                )
                .split("\n")
                .map {
                    it.replace(
                        Regex("^#"), "//"
                    )
            }.joinToString("\n")
        }
        FileSystems.createDirs(dirPath)
        val createFile = File(
            dirPath,
            shellScriptName
        )
        createFile.writeText(shellContents)
    }


    private fun makeAppDirScriptContents(
        shellScriptName: String,
        shellOrJs: LanguageTypeSelects = LanguageTypeSelects.JAVA_SCRIPT
    ): String{
        val languageTypeHolderMap = LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(shellOrJs)
        return """
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.LABELING_SEC_START)}
        |${makeDescription()}
        |${languageTypeHolderMap?.get(HolderTypeName.LABELING_SEC_END)}
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.SETTING_SEC_START)}
        |$SCRIPT_FILE_NAME="${shellScriptName}"
        |${languageTypeHolderMap?.get(HolderTypeName.SETTING_SEC_END)}
        |
    """.trimMargin()
            .split("\n")
            .map {
                it.replace(
                    Regex("^#"), "//"
                )
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
            makeAppDirScriptContents(
                scriptScriptName,
            )
        )
    }

    fun makeButtonExecJS(
        dirPath: String,
        shellScriptName: String,
        execJsOrHtmlPathValue: String = String()
    ){
        val shellOrJs = LanguageTypeSelects.JAVA_SCRIPT
        FileSystems.createDirs(dirPath)
        if(
            File(
                dirPath,
                shellScriptName
            ).isFile
        ) return
        makeShellOrJsFile(
            String(),
            dirPath,
            shellScriptName,
            onUpdateLastModifyOff,
            shellOrJs,
            execJsOrHtmlPathValue
        )
    }

    fun makeConfigJsFile(
        dirPath: String,
        jsScriptName: String,
    ) {
        val shellOrJs = LanguageTypeSelects.JAVA_SCRIPT
        val languageTypeHolderMap = LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(shellOrJs)
        val jsContents = """
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.LABELING_SEC_START)}
        |${makeDescription()}
        |${languageTypeHolderMap?.get(HolderTypeName.LABELING_SEC_END)}
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.SETTING_SEC_START)}
        |$EDIT_EXECUTE="$editExecuteAlways"
        |$CMDCLICK_RUN_SHELL="$CMDCLICK_RUN_SHELL_DEFAULT_VALUE"
        |$CMDCLICK_SHIBAN="$CMDCLICK_SHIBAN_DEFAULT_VALUE"
        |$CMDCLICK_HISTORY_SWITCH="$HISTORY_SWITCH_DEFAULT_VALUE"
        |$CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC="$CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE"
        |$ON_ADBLOCK="$onAdBlockOn"
        |$ON_TERM_BACKEND_WHEN_START="$ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE"
        |$ON_TERM_VISIBLE_WHEN_KEYBOARD="$ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE"
        |$ON_TERM_SHORT_WHEN_LOAD="$ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE"
        |$CMDCLICK_HOME_FANNELS_PATH="$homeFannelsFilePath"
        |$CMDCLICK_TERMINAL_FONT_ZOOM="$CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE"
        |$UBUNTU_SLEEP_DELAY_MIN_IN_SCREEN_OFF="20"
        |$TERMINAL_COLOR="$TERMINAL_COLOR_DEFAULT_VALUE"
        |$TERMINAL_FONT_COLOR="$TERMINAL_FONT_COLOR_DEFAULT_VALUE"
        |$PASS_CMDVARIABLE_EDIT="$PASS_CMDVARIABLE_EDIT_ON_VALUE"
        |$HIDE_SETTING_VARIABLES="$EDIT_EXECUTE"
        |${languageTypeHolderMap?.get(HolderTypeName.SETTING_SEC_END)}
        |
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.SCRIPT_START)}
        |
        |
        |
    """.trimMargin().split("\n")
            .map {
                it.replace(
                    Regex("^#"), "//"
                )
            }.joinToString("\n")
        if(!File(dirPath).isDirectory){
            FileSystems.createDirs(
                dirPath
            )
        }
        val createFile = File(
            dirPath,
            jsScriptName
        )
        if(createFile.isFile) return
        createFile.writeText(jsContents)
    }

    private fun makeAutoJsContents(
        jsScriptName: String,
    ): String {
        val shellOrJs = LanguageTypeSelects.JAVA_SCRIPT
        val languageTypeHolderMap = LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(shellOrJs)
        return """
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.LABELING_SEC_START)}
        |${makeDescription()}
        |${languageTypeHolderMap?.get(HolderTypeName.LABELING_SEC_END)}
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.SETTING_SEC_START)}
        |$TERMINAL_SIZE_TYPE="$TERMINAL_SIZE_TYPE_DEFAULT_VALUE"
        |$TERMINAL_OUTPUT_MODE="$TERMINAL_OUTPUT_MODE_DEFAULT_VALUE"
        |$CMDCLICK_ON_AUTO_EXEC="$CMDCLICK_ON_AUTO_EXEC_DEFAULT_VALUE"
        |$ON_UPDATE_LAST_MODIFY="$onUpdateLastModifyOff"
        |$CMDCLICK_ON_HISTORY_URL_TITLE="$CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE"
        |$CMDCLICK_HISTORY_SWITCH="$historySwitchInherit"
        |$CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC="$urlHistoryOrButtonExecUrlInherit"
        |$ON_ADBLOCK="$onAdBlockInherit"
        |$ON_TERM_BACKEND_WHEN_START="$ON_TERM_BACKEND_WHEN_START_DEFAULT_VALUE"
        |$ON_TERM_VISIBLE_WHEN_KEYBOARD="$ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE"
        |$ON_TERM_SHORT_WHEN_LOAD="$ON_TERM_SHORT_WHEN_LOAD_DEFAULT_VALUE"
        |$ON_URL_LAUNCH_MACRO="$onUrlLaunchMacroRecent"
        |$DEFAULT_MONITOR_FILE="$DEFAULT_MONITOR_FILE_DEFAULT_VALUE"
        |$SRC_IMAGE_ANCHOR_LONG_PRESS_MENU_FILE_PATH="${UsePath.srcImageAnchorLongPressMenuFilePath}"
        |$SRC_ANCHOR_LONG_PRESS_MENU_FILE_PATH="${UsePath.srcAnchorLongPressMenuFilePath}"
        |$IMAGE_LONG_PRESS_MENU_FILE_PATH="${UsePath.imageLongPressMenuFilePath}"
        |$IGNORE_HISTORY_PATHS=""
        |$EXEC_JS_OR_HTML_PATH=""
        |$HOME_SCRIPT_URLS_PATH="$homeScriptUrlsFilePath"
        |$NO_SCROLL_SAVE_URLS="$noScrollSaveUrlsFilePath"
        |$CMDCLICK_TERMINAL_FONT_ZOOM=""
        |$TERMINAL_FONT_COLOR=""
        |$TERMINAL_COLOR=""
        |$HIDE_SETTING_VARIABLES="$SCRIPT_FILE_NAME"
        |$HIDE_SETTING_VARIABLES="$ON_UPDATE_LAST_MODIFY"
        |$SCRIPT_FILE_NAME="${jsScriptName}"
        |${languageTypeHolderMap?.get(HolderTypeName.SETTING_SEC_END)}
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.CMD_SEC_START)}
        |${languageTypeHolderMap?.get(HolderTypeName.CMD_SEC_END)}
        |
        |
        |${languageTypeHolderMap?.get(HolderTypeName.SCRIPT_START)}
        |
        |
        |
    """.trimMargin()
            .split("\n")
            .map {
                it.replace(
                    Regex("^#"), "//"
                )
            }.joinToString("\n")
    }

    fun makeAutoJsFile(
        dirPath: String,
        jsScriptName: String,
    ) {
        FileSystems.createDirs(dirPath)
        if(
            File(
                dirPath,
                jsScriptName
            ).isFile
        ) return
        val shellContents = makeAutoJsContents(
            jsScriptName
        )
        val createFile = File(
            dirPath,
            jsScriptName
        )
        createFile.writeText(shellContents)
    }
}