package com.puutaro.commandclick.common.variable

class SettingVariableSelects {
    companion object {
        enum class TerminalDoSelects {
            ON,
            OFF,
            TERMUX
        }

        enum class EditExecuteSelects {
            NO,
            ONCE,
            ALWAYS
        }

        enum class TerminalSizeTypeSelects {
            OFF,
            SHORT,
            LONG,
        }

        enum class AutoExecSelects {
            OFF,
            ON,
        }

        enum class TerminalOutPutModeSelects {
            NORMAL,
            REFLASH,
            REFLASH_AND_FIRST_ROW,
            DEBUG,
            NO,
        }

        enum class HistorySwitchSelects {
            OFF,
            ON,
            INHERIT
        }

        enum class UrlHistoryOrButtonExecSelects {
            URL_HISTORY,
            BUTTON_EXEC,
            INHERIT
        }

        enum class OnUpdateLastModifySelects {
            ON,
            OFF,
        }

        enum class OnHistoryUrlTitle {
            ON,
            OFF,
        }

        enum class StatusBarIconColorModeSelects {
            BLACK,
            WHITE,
            INHERIT,
        }

        enum class OnUrlLaunchMacroSelects {
            RECENT,
            FREAQUENCY,
            OFF,
        }
    }

}