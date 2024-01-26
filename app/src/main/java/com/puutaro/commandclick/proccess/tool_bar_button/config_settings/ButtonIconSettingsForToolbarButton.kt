package com.puutaro.commandclick.proccess.tool_bar_button.config_settings

import com.puutaro.commandclick.common.variable.icon.CmdClickIcons

object ButtonIconSettingsForToolbarButton {
    enum class ButtonIcons(
        val str: String,
    ) {
        PLAY(CmdClickIcons.PLAY.str),
        EDIT(CmdClickIcons.EDIT.str),
        SETTING(CmdClickIcons.SETTING.str),
        OK(CmdClickIcons.OK.str),
    }
}