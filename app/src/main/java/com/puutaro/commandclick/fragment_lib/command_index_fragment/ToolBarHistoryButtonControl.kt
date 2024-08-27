package com.puutaro.commandclick.fragment_lib.command_index_fragment

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.history.CLICLTYPE
import com.puutaro.commandclick.proccess.history.HistoryButtonSwitcher
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryButtonEvent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ToolBarHistoryButtonControl(
    private val cmdIndexFragment: CommandIndexFragment,
){
    private val binding = cmdIndexFragment.binding
    val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
    private val historyButtonView = binding.cmdindexHistoryButton
    private val urlHistoryButtonEvent = UrlHistoryButtonEvent(
        cmdIndexFragment,
    )


    fun historyButtonClick() {
        historyButtonView.setOnClickListener {
            historyButtonInnerView ->
            HistoryButtonSwitcher.switch(
                cmdIndexFragment,
                cmdIndexFragment.context?.getString(
                    R.string.index_terminal_fragment
                ),
                cmdIndexFragment.historySwitch,
                urlHistoryButtonEvent,
                CLICLTYPE.SHORT
            )
        }

        historyButtonView.setOnLongClickListener {
                historyButtonInnerView ->
            HistoryButtonSwitcher.switch(
                cmdIndexFragment,
                cmdIndexFragment.context?.getString(
                    R.string.index_terminal_fragment
                ),
                cmdIndexFragment.historySwitch,
                urlHistoryButtonEvent,
                CLICLTYPE.LONG
            )
            true
        }
    }
}
