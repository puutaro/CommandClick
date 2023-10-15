package com.puutaro.commandclick.common.variable.variant

object SettingVariableSelects {
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
        OFF,
        RECENT,
        FREQUENCY,
    }

    enum class ButtonEditExecVariantSelects {
        BackStack,
        TermOut,
        NoJsTermOut,
        TermLong,
    }

    enum class OnAdblockSelects {
        INHERIT,
        ON,
        OFF,
    }

    enum class OnUrlHistoryRegisterSelects {
        OFF,
        ON,
    }

    enum class OnTermVisibleWhenKeyboardSelects {
        INHERIT,
        OFF,
        ON,
    }

    enum class overrideItemClickExecSelects {
        ON,
        OFF
    }

    enum class disableSettingButtonSelects {
        ON,
        OFF
    }

    enum class disablePlayButtonSelects {
        ON,
        OFF
    }

    enum class disableEditButtonSelects {
        ON,
        OFF
    }

    enum class OnTermBackendWhenStartSelects {
        INHERIT,
        ON,
        OFF
    }
    enum class OnTermShortWhenLoadSelects {
        INHERIT,
        ON,
        OFF
    }
    enum class DisableShowToolbarWhenHighlightSelects {
        ON,
        OFF
    }
    enum class ShellExecEnvSelects {
        UBUNTU,
        TERMUX
    }
    enum class UbuntuExecModeSelects {
        foreground,
        background,
    }
}
