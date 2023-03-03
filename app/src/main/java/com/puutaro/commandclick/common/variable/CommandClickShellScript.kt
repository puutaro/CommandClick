package com.puutaro.commandclick.common.variable

import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewName
import com.puutaro.commandclick.util.FileSystems
import java.io.File


class CommandClickShellScript {
    companion object {
        val SHELL_FILE_SUFFIX = ".sh"
        val JS_FILE_SUFFIX = ".js"
        val JSX_FILE_SUFFIX = ".jsx"
        val HTML_FILE_SUFFIX = ".html"
        val HTM_FILE_SUFFIX = ".htm"
        val EMPTY_STRING = "COMMAND_CLICK_EMPTY_STRING"
        val LABELING_SECTION_START = "### LABELING_SECTION_START"
        val LABELING_SECTION_END = "### LABELING_SECTION_END"
        val SETTING_SECTION_START = "### SETTING_SECTION_START"
        val SETTING_SECTION_END = "### SETTING_SECTION_END"
        val CMD_VARIABLE_SECTION_START = "### CMD_VARIABLE_SECTION_START"
        val CMD_VARIABLE_SECTION_END = "### CMD_VARIABLE_SECTION_END"
        val CMD_PROMPT_MESSAGE = "### Please write bellow with shell script"
        val TERMINAL_DO = "terminalDo"
        val EDIT_EXECUTE = "editExecute"
        val TERMINAL_SIZE_TYPE = "terminalSizeType"
        val TERMINAL_OUTPUT_MODE = "terminalOutputMode"
        val SET_VARIABLE_TYPE = "setVariableType"
        val ON_UPDATE_LAST_MODIFY = "onUpdateLastModify"
        val ON_URL_LAUNCH_MACRO = "onUrlLaunchMacro"
        val EXEC_JS_OR_HTML_PATH = "execJsOrHtmlPath"
        val BEFORE_COMMAND = "beforeCommand"
        val AFTER_COMMAND = "afterCommand"
        val SHELL_FILE_NAME = "shellFileName"
        val CMDCLICK_SHIBAN = "cmdclickShiban"
        val CMDCLICK_RUN_SHELL = "cmdclickRunShell"
        val CMDCLICK_ON_AUTO_EXEC = "onAutoExec"
        val CMDCLICK_HISTORY_SWITCH = "historySwitch"
        val CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC = "urlHistoryOrButtonExec"
        val ON_ADBLOCK = "onAdBlock"
        val CMDCLICK_TERMINAL_FONT_ZOOM = "terminalFontZoom"
        val CMDCLICK_ON_HISTORY_URL_TITLE = "onHistoryUrlTitle"
        val TERMINAL_COLOR = "terminalColor"
        val TERMINAL_FONT_COLOR = "terminalFontColor"
        val STATUS_BAR_ICON_COLOR_MODE = "statusBarIconColorMode"
        val CMDCLICK_SHIBAN_DEFAULT_VALUE = "#!/bin/bash"
        val CMDCLICK_RUN_SHELL_DEFAULT_VALUE = "bash"

        val SETTING_VARIABLE_NAMES_LIST = listOf(
            TERMINAL_DO,
            EDIT_EXECUTE,
            TERMINAL_SIZE_TYPE,
            TERMINAL_OUTPUT_MODE,
            SET_VARIABLE_TYPE,
            CMDCLICK_HISTORY_SWITCH,
            CMDCLICK_ON_AUTO_EXEC,
            ON_UPDATE_LAST_MODIFY,
            TERMINAL_COLOR,
            TERMINAL_FONT_COLOR,
            STATUS_BAR_ICON_COLOR_MODE,
            CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC,
            ON_ADBLOCK,
            CMDCLICK_TERMINAL_FONT_ZOOM,
            CMDCLICK_ON_HISTORY_URL_TITLE,
            CMDCLICK_RUN_SHELL,
            CMDCLICK_SHIBAN,
            ON_URL_LAUNCH_MACRO,
            EXEC_JS_OR_HTML_PATH,
            BEFORE_COMMAND,
            AFTER_COMMAND,
            SHELL_FILE_NAME,
        )
        private val terminalOn = SettingVariableSelects.Companion.TerminalDoSelects.ON.name
        private val terminalTermux = SettingVariableSelects.Companion.TerminalDoSelects.TERMUX.name
        private val terminalOff = SettingVariableSelects.Companion.TerminalDoSelects.OFF.name
        private val editExecuteNo = SettingVariableSelects.Companion.EditExecuteSelects.NO.name
        private val editExecuteOnce = SettingVariableSelects.Companion.EditExecuteSelects.ONCE.name
        private val editExecuteAlways = SettingVariableSelects.Companion.EditExecuteSelects.ALWAYS.name
        private val terminalSizeTypeOff = SettingVariableSelects.Companion.TerminalSizeTypeSelects.OFF.name
        private val terminalSizeTypeShort = SettingVariableSelects.Companion.TerminalSizeTypeSelects.SHORT.name
        private val terminalSizeTypeLong = SettingVariableSelects.Companion.TerminalSizeTypeSelects.LONG.name
        private val autoExecSelectsOn = SettingVariableSelects.Companion.AutoExecSelects.ON.name
        private val autoExecSelectsOff = SettingVariableSelects.Companion.AutoExecSelects.OFF.name
        private val terminalOutPutModeNormal = SettingVariableSelects.Companion.TerminalOutPutModeSelects.NORMAL.name
        private val terminalOutPutModeReflash = SettingVariableSelects.Companion.TerminalOutPutModeSelects.REFLASH.name
        private val terminalOutPutModeReflashAndFirstRow = SettingVariableSelects.Companion.TerminalOutPutModeSelects.REFLASH_AND_FIRST_ROW.name
        private val terminalOutPutModeDebug = SettingVariableSelects.Companion.TerminalOutPutModeSelects.DEBUG.name
        private val terminalOutPutModeNo = SettingVariableSelects.Companion.TerminalOutPutModeSelects.NO.name
        private val historySwitchOff = SettingVariableSelects.Companion.HistorySwitchSelects.OFF.name
        private val historySwitchOn = SettingVariableSelects.Companion.HistorySwitchSelects.ON.name
        private val historySwitchInherit = SettingVariableSelects.Companion.HistorySwitchSelects.INHERIT.name
        private val urlHistoryOrButtonExecUrlHistory = SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name
        private val urlHistoryOrButtonExecUrlButtonExec = SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.BUTTON_EXEC.name
        private val urlHistoryOrButtonExecUrlInherit = SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.INHERIT.name
        private val onUpdateLastModifyOn = SettingVariableSelects.Companion.OnUpdateLastModifySelects.ON.name
        private val onUpdateLastModifyOff = SettingVariableSelects.Companion.OnUpdateLastModifySelects.OFF.name
        private val onHistoryUrlTitleON = SettingVariableSelects.Companion.OnHistoryUrlTitle.ON.name
        private val onHistoryUrlTitleOff = SettingVariableSelects.Companion.OnHistoryUrlTitle.OFF.name
        private val statusBarIconColorModeWhite = SettingVariableSelects.Companion.StatusBarIconColorModeSelects.WHITE.name
        private val statusBarIconColorModeBlack = SettingVariableSelects.Companion.StatusBarIconColorModeSelects.BLACK.name
        private val statusBarIconColorInherit = SettingVariableSelects.Companion.StatusBarIconColorModeSelects.INHERIT.name
        private val onUrlLaunchMacroRecent = SettingVariableSelects.Companion.OnUrlLaunchMacroSelects.RECENT.name
        private val onUrlLaunchMacroFrequency = SettingVariableSelects.Companion.OnUrlLaunchMacroSelects.FREQUENCY.name
        private val onUrlLaunchMacroOff = SettingVariableSelects.Companion.OnUrlLaunchMacroSelects.OFF.name
        private val onAdBlockInherit = SettingVariableSelects.Companion.OnAdblockSelects.INHERIT.name
        private val onAdBlockOn = SettingVariableSelects.Companion.OnAdblockSelects.ON.name
        private val onAdBlockOff = SettingVariableSelects.Companion.OnAdblockSelects.OFF.name
        val TERMINAL_DO_DEFAULT_VALUE = terminalOn
        val EDIT_EXECUTE_DEFAULT_VALUE = editExecuteNo
        val TERMINAL_SIZE_TYPE_DEFAULT_VALUE = terminalSizeTypeOff
        val TERMINAL_OUTPUT_MODE_DEFAULT_VALUE = terminalOutPutModeNormal
        val HISTORY_SWITCH_DEFAULT_VALUE = historySwitchOff
        val ON_UPDATE_LAST_MODIFY_DEFAULT_VALUE = onUpdateLastModifyOn
        val TERMINAL_COLOR_DEFAULT_VALUE = "#121212"
        val TERMINAL_FONT_COLOR_DEFAULT_VALUE = "#f2f2f2"
        val STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE = statusBarIconColorModeWhite
        val ON_URL_LAUNCH_MACRO_DEFAULT_VALUE = onUrlLaunchMacroOff
        val CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE = 100
        val CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE = SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name
        val ON_ADBLOCK_DEFAULT_VALUE = SettingVariableSelects.Companion.OnAdblockSelects.OFF.name
        val CMDCLICK_ON_AUTO_EXEC_DEFAULT_VALUE =  SettingVariableSelects.Companion.AutoExecSelects.OFF.name
        val CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE =  onHistoryUrlTitleOff


        val setVariableForSettingHolder: List<String> = listOf(
            "${TERMINAL_DO}:CB=${terminalOn}!${terminalTermux}!${terminalOff}",
            "${EDIT_EXECUTE}:CB=${editExecuteNo}!${editExecuteOnce}!${editExecuteAlways}",
            "${TERMINAL_SIZE_TYPE}:CB=${terminalSizeTypeOff}!${terminalSizeTypeShort}!${terminalSizeTypeLong}",
            "${TERMINAL_OUTPUT_MODE}:CB=${terminalOutPutModeNormal}!${terminalOutPutModeReflash}!${terminalOutPutModeReflashAndFirstRow}!${terminalOutPutModeDebug}!${terminalOutPutModeNo}",
            "${ON_UPDATE_LAST_MODIFY}:CB=${onUpdateLastModifyOn}!${onUpdateLastModifyOff}",
            "${CMDCLICK_ON_AUTO_EXEC}:CB=${autoExecSelectsOn}!${autoExecSelectsOff}",
            "${CMDCLICK_ON_HISTORY_URL_TITLE}:CB=${onHistoryUrlTitleON}!${onHistoryUrlTitleOff}",
            "${CMDCLICK_HISTORY_SWITCH}:CB=${historySwitchOff}!${historySwitchOn}!${historySwitchInherit}",
            "${CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC}:CB=${urlHistoryOrButtonExecUrlHistory}!${urlHistoryOrButtonExecUrlButtonExec}!${urlHistoryOrButtonExecUrlInherit}",
            "${STATUS_BAR_ICON_COLOR_MODE}:CB=${statusBarIconColorModeWhite}!${statusBarIconColorModeBlack}!${statusBarIconColorInherit}",
            "${ON_URL_LAUNCH_MACRO}:CB=${onUrlLaunchMacroOff}!${onUrlLaunchMacroRecent}!${onUrlLaunchMacroFrequency}",
            "${ON_ADBLOCK}:CB=${onAdBlockInherit}!${onAdBlockOff}!${onAdBlockOn}",
            "${CMDCLICK_TERMINAL_FONT_ZOOM}:NUM=1..1000!1",
            "${TERMINAL_COLOR}:CLR=",
            "${TERMINAL_FONT_COLOR}:CLR=",
            "${EXEC_JS_OR_HTML_PATH}:FL=",
        )

        fun makeShellScriptName(): String {
            return (1..10000).random().toString() + SHELL_FILE_SUFFIX
        }

        fun makeCopyPrefix(): String {
            return (1..10000).random().toString()
        }

        private val colons = "::"

        fun makeDescription(): String {
            val backstackMacroSignal = colons + SettingVariableSelects.Companion.ButtonEditExecVarantSelects.BackStack.name + colons
            val termOutMacroSignal = colons + SettingVariableSelects.Companion.ButtonEditExecVarantSelects.BackStack.name + colons
            return """
                # * ${TERMINAL_DO} is terminal screen select option
                |#  - ${terminalOn}: cmdclick terminal (default)
                |#  - ${terminalTermux}: termux terminal,
                |#  - ${terminalOff}: no terminal(backend exec)
                |# * ${TERMINAL_SIZE_TYPE} is cmdclick terminal size option
                |#  - ${terminalSizeTypeOff}: no adjust (default)
                |#  - ${terminalSizeTypeLong}: LongSize
                |#  - ${terminalSizeTypeShort}: ShortSize
                |# * ${TERMINAL_OUTPUT_MODE} decide output mode in cmdclick terminal
                |#  - ${terminalOutPutModeNormal}: normal terminal output (default)
                |#  - ${terminalOutPutModeReflash}: Before terminal output, screen resflesh
                |#  - ${terminalOutPutModeReflashAndFirstRow}: Before terminal output, screen resflesh and focus first row
                |#  - ${terminalOutPutModeDebug}: stdr + stderr
                |#  - ${terminalOutPutModeNo}: no output (bacground exec)
                |# * ${CMDCLICK_ON_AUTO_EXEC} is ready for start and end script
                |#  - ${autoExecSelectsOn}: start or end exec on
                |#  - ${autoExecSelectsOff}: exec off (default)
                |# * ${ON_UPDATE_LAST_MODIFY} is how updating file last modified status when executing
                |#  - ${onUpdateLastModifyOn}: update this (default)
                |#  - ${onUpdateLastModifyOff}: no update this
                |# * ${CMDCLICK_HISTORY_SWITCH}: switch app history with url history
                |#  - ${historySwitchOn}: switch
                |#  - ${historySwitchOff}: no switch
                |#  - ${historySwitchInherit}: inherit config setting (default)
                |# * ${CMDCLICK_ON_HISTORY_URL_TITLE} is how adding url title to history
                |#  - ${onHistoryUrlTitleON}: add
                |#  - ${onHistoryUrlTitleOff}: no
                |# * ${CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC} switch url history or button script exec
                |#  - ${urlHistoryOrButtonExecUrlInherit}: inherit config setting
                |#  - ${urlHistoryOrButtonExecUrlHistory}: switch url history
                |#  - ${urlHistoryOrButtonExecUrlButtonExec}: switch url button script exec
                |# * ${ON_ADBLOCK}: adblock switch
                |#  - ${onAdBlockInherit}: inherit config setting
                |#  - ${onAdBlockOn}: on
                |#  - ${onAdBlockOff}: off
                |# * ${ON_URL_LAUNCH_MACRO}: url launch macro(when set, cmdclick web terminal don't output)
                |#  - ${onUrlLaunchMacroOff}: no launch
                |#  - ${onUrlLaunchMacroRecent}: recent use url launch
                |#  - ${onUrlLaunchMacroFrequency}: most use url launch
                |# * ${EXEC_JS_OR_HTML_PATH}: execute javascript or html file path
                |#   - disable, when ${ON_URL_LAUNCH_MACRO} is not ${onUrlLaunchMacroOff}
                |# * ${CMDCLICK_TERMINAL_FONT_ZOOM} adjust terminal font size (percentage)
                |# * ${TERMINAL_FONT_COLOR} adjust terminal font color
                |# * ${TERMINAL_COLOR} adjust terminal background color
                |# * ${SET_VARIABLE_TYPE} is cmdsection gui edit mini program, reference to github for detail (like gtk yad)
                |#  - ex) spinner: {cmdVariable}:${EditTextSupportViewName.CHECK_BOX.str}=ON!OFF  
                |#  - ex) num crementer: {cmdVariable}:${EditTextSupportViewName.NUM_INDE_CREMENTER.str}=1!1..100!1 (({init})!{min}..{max}!{step})
                |#  - ex) file selector: {cmdVariable}:${EditTextSupportViewName.FILE_PICKER.str}=
                |#  - ex) button: {cmdVariable}:${EditTextSupportViewName.BUTTON.str}=
                |#    - button execute command 
                |#      ex) echo $0  
                |#             ("$0" is current shell path
                |#      ex) ${backstackMacroSignal} ls
                |#             ("${backstackMacroSignal}" is backstack, only work when prefix
                |#      ex) ${termOutMacroSignal} ls
                |#             ("${termOutMacroSignal}" enable terminal output
                |#      ex) top -n 1 > /dev/null  
                |#             (when suffix is "> /dev/null" or "> /dev/null 2>&1", no output
                |#  - ex) dir selector: {cmdVariable}:${EditTextSupportViewName.DIRECTORY_PICKER.str}=
                |#  - ex) read only: {cmdVariable}:${EditTextSupportViewName.READ_ONLY_EDIT_TEXT.str}=
                |#  - ex) password: {cmdVariable}:${EditTextSupportViewName.PASSWORD.str}=
                |#  - enable multiple specification
                |#  - ex) 
                |#  ${SET_VARIABLE_TYPE}="{cmdVar1}:${EditTextSupportViewName.CHECK_BOX.str}=ON!OFF"
                |#  ${SET_VARIABLE_TYPE}="{cmdVar2}:${EditTextSupportViewName.FILE_PICKER.str}="
                |#  ${SET_VARIABLE_TYPE}="..."
                |# * ${BEFORE_COMMAND} is before shell script execute, run command
                |# * ${AFTER_COMMAND} is after shell script execute, run command
                |# * ${SHELL_FILE_NAME} is your shell file name
            """.trimIndent().replace("EX_INDENT_BLANK", "        ")
        }

        fun makeShellContents(
            shiban: String,
            shellScriptName: String,
            onUpdateLastModifyValue: String,
        ): String{
            return """${shiban}
            |
            |
            |${LABELING_SECTION_START}
            |${makeDescription()}
            |${LABELING_SECTION_END}
            |
            |
            |${SETTING_SECTION_START}
            |${TERMINAL_DO}=${TERMINAL_DO_DEFAULT_VALUE}
            |${EDIT_EXECUTE}=${EDIT_EXECUTE_DEFAULT_VALUE}
            |${TERMINAL_SIZE_TYPE}=${TERMINAL_SIZE_TYPE_DEFAULT_VALUE}
            |${TERMINAL_OUTPUT_MODE}=${TERMINAL_OUTPUT_MODE_DEFAULT_VALUE}
            |${ON_UPDATE_LAST_MODIFY}=${onUpdateLastModifyValue}
            |${CMDCLICK_HISTORY_SWITCH}=${historySwitchInherit}
            |${CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC}=${urlHistoryOrButtonExecUrlInherit}
            |${ON_ADBLOCK}=${onAdBlockInherit}
            |${EXEC_JS_OR_HTML_PATH}=
            |${CMDCLICK_TERMINAL_FONT_ZOOM}=
            |${TERMINAL_COLOR}=
            |${TERMINAL_FONT_COLOR}=
            |${SET_VARIABLE_TYPE}=
            |${BEFORE_COMMAND}=
            |${AFTER_COMMAND}=
            |${SHELL_FILE_NAME}=${shellScriptName}
            |${SETTING_SECTION_END}
            |
            |
            |${CMD_VARIABLE_SECTION_START}
            |${CMD_VARIABLE_SECTION_END}
            |
            |
            |${CMD_PROMPT_MESSAGE}
            |
            |
            |
        """.trimMargin()
        }


        fun makeShellFile(
            shiban: String,
            dirPath: String,
            shellScriptName: String,
            onUpdateLastModifyValue: String = onUpdateLastModifyOn
        ) {
            val shellContents =  makeShellContents(
                shiban,
                shellScriptName,
                onUpdateLastModifyValue,
            )
            FileSystems.createDirs(dirPath)
            val createFile = File(
                dirPath,
                shellScriptName
            )
            createFile.writeText(shellContents)
        }


        fun makeAppDirShellContents(
            shellScriptName: String
        ): String{
            return """${CMDCLICK_SHIBAN_DEFAULT_VALUE}
            |
            |
            |${LABELING_SECTION_START}
            |${makeDescription()}
            |${LABELING_SECTION_END}
            |
            |
            |${SETTING_SECTION_START}
            |${SHELL_FILE_NAME}=${shellScriptName}
            |${SETTING_SECTION_END}
            |
        """.trimMargin()
        }

        fun makeAppDirAdminFile(
            dirPath: String,
            shellScriptName: String
        ) {
            FileSystems.createDirs(dirPath)
            val createFile = File(
                dirPath,
                shellScriptName
            )
            if(createFile.isFile) return
            createFile.writeText(
                makeAppDirShellContents(
                    shellScriptName
                )
            )
        }

        fun makeButtonExecShell(
            shiban: String,
            dirPath: String,
            shellScriptName: String
        ){
            FileSystems.createDirs(dirPath)
            if(
                File(
                    dirPath,
                    shellScriptName
                ).isFile
            ) return
            makeShellFile(
                shiban,
                dirPath,
                shellScriptName,
                onUpdateLastModifyOff
            )
        }

        fun makeConfigShellFile(
            dirPath: String,
            shellScriptName: String
        ) {
            val shellContents = """${CMDCLICK_SHIBAN_DEFAULT_VALUE}
            |
            |
            |${LABELING_SECTION_START}
            |${makeDescription()}
            |${LABELING_SECTION_END}
            |
            |
            |${SETTING_SECTION_START}
            |${CMDCLICK_RUN_SHELL}=${CMDCLICK_RUN_SHELL_DEFAULT_VALUE}
            |${CMDCLICK_SHIBAN}=${CMDCLICK_SHIBAN_DEFAULT_VALUE}
            |${CMDCLICK_HISTORY_SWITCH}=${HISTORY_SWITCH_DEFAULT_VALUE}
            |${CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC}=${CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE}
            |${ON_ADBLOCK}=${onAdBlockOff}
            |${CMDCLICK_TERMINAL_FONT_ZOOM}=${CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE}
            |${TERMINAL_COLOR}=${TERMINAL_COLOR_DEFAULT_VALUE}
            |${TERMINAL_FONT_COLOR}=${TERMINAL_FONT_COLOR_DEFAULT_VALUE}
            |${SETTING_SECTION_END}
            |
            |
            |
            |${CMD_PROMPT_MESSAGE}
            |
            |
            |
        """.trimMargin()
            if(!File(dirPath).isDirectory){
                FileSystems.createDirs(
                    dirPath
                )
            }
            val createFile = File(
                dirPath,
                shellScriptName
            )
            if(createFile.isFile) return
            createFile.writeText(shellContents)
        }

        fun makeAutoShellContents(
            shiban: String,
            shellScriptName: String
        ): String {
            return """${shiban}
            |
            |
            |${LABELING_SECTION_START}
            |${makeDescription()}
            |${LABELING_SECTION_END}
            |
            |
            |${SETTING_SECTION_START}
            |${TERMINAL_DO}=${TERMINAL_DO_DEFAULT_VALUE}
            |${TERMINAL_SIZE_TYPE}=${TERMINAL_SIZE_TYPE_DEFAULT_VALUE}
            |${TERMINAL_OUTPUT_MODE}=${TERMINAL_OUTPUT_MODE_DEFAULT_VALUE}
            |${CMDCLICK_ON_AUTO_EXEC}=${CMDCLICK_ON_AUTO_EXEC_DEFAULT_VALUE}
            |${ON_UPDATE_LAST_MODIFY}=${onUpdateLastModifyOff}
            |${CMDCLICK_ON_HISTORY_URL_TITLE}=${CMDCLICK_ON_HISTORY_URL_TITLE_DEFAULT_VALUE}
            |${CMDCLICK_HISTORY_SWITCH}=${historySwitchInherit}
            |${CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC}=${urlHistoryOrButtonExecUrlInherit}
            |${ON_ADBLOCK}=${onAdBlockInherit}
            |${ON_URL_LAUNCH_MACRO}=${ON_URL_LAUNCH_MACRO_DEFAULT_VALUE}
            |${EXEC_JS_OR_HTML_PATH}=
            |${CMDCLICK_TERMINAL_FONT_ZOOM}=
            |${TERMINAL_FONT_COLOR}=
            |${TERMINAL_COLOR}=
            |${SET_VARIABLE_TYPE}=
            |${BEFORE_COMMAND}=
            |${AFTER_COMMAND}=
            |${SHELL_FILE_NAME}=${shellScriptName}
            |${SETTING_SECTION_END}
            |
            |
            |${CMD_VARIABLE_SECTION_START}
            |${CMD_VARIABLE_SECTION_END}
            |
            |
            |${CMD_PROMPT_MESSAGE}
            |
            |
            |
        """.trimMargin()
        }

        fun makeAutoShellFile(
            shiban: String,
            dirPath: String,
            shellScriptName: String
        ) {
            FileSystems.createDirs(dirPath)
            if(
                File(
                    dirPath,
                    shellScriptName
                ).isFile
            ) return
            val shellContents = makeAutoShellContents(
                shiban,
                shellScriptName
            )
            val createFile = File(
                dirPath,
                shellScriptName
            )
            createFile.writeText(shellContents)
        }
    }
}