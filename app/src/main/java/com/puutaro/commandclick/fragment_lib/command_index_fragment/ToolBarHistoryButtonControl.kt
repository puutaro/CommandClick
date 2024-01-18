package com.puutaro.commandclick.fragment_lib.command_index_fragment

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.CLICLTYPE
import com.puutaro.commandclick.proccess.HistoryBottunSwitcher
import com.puutaro.commandclick.proccess.UrlHistoryButtonEvent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ToolBarHistoryButtonControl(
    private val cmdIndexFragment: CommandIndexFragment,
){
    private val readSharePreffernceMap = cmdIndexFragment.readSharePreffernceMap
    private val binding = cmdIndexFragment.binding
    val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
    private val historyButtonView = binding.cmdindexHistoryButton
    private val urlHistoryButtonEvent = UrlHistoryButtonEvent(
        cmdIndexFragment,
        readSharePreffernceMap,
    )


    fun historyButtonClick() {
        historyButtonView.setOnClickListener {
            historyButtonInnerView ->
            HistoryBottunSwitcher.switch(
                cmdIndexFragment,
                historyButtonInnerView,
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
            HistoryBottunSwitcher.switch(
                cmdIndexFragment,
                historyButtonInnerView,
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
