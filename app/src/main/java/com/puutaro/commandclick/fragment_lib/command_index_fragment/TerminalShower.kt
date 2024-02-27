package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.fragment.CommandIndexFragment

object TerminalShower {
    fun show(
        cmdIndexFragment: CommandIndexFragment
    ){
        val listener = cmdIndexFragment.context as? CommandIndexFragment.OnKeyboardVisibleListener
        listener?.onKeyBoardVisibleChange(
            false,
            true,
            cmdIndexFragment.WebSearchSwitch
        )
    }
}