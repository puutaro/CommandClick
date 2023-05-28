package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.fragment.CommandIndexFragment

object TerminalShower {
    fun show(
        cmdIndexCommandIndexFragment: CommandIndexFragment
    ){
        val listener = cmdIndexCommandIndexFragment.context as? CommandIndexFragment.OnKeyboardVisibleListener
        listener?.onKeyBoardVisibleChange(
            false,
            true,
            cmdIndexCommandIndexFragment.WebSearchSwitch
        )
    }
}