package com.puutaro.commandclick.util.state

object FragmentTagPrefix {
    enum class Prefix(
        val str: String
    ) {
        CMD_VAL_EDIT_PREFIX("cmdValEdit"),
        SETTING_VAL_EDIT_PREFIX("settingValEdit"),
    }
}