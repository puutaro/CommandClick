package com.puutaro.commandclick.fragment_lib.command_index_fragment

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.history.CLICLTYPE
import com.puutaro.commandclick.proccess.history.HistoryButtonSwitcher
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryButtonEvent
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryButtonEvent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object ToolBarHistoryButtonControl {

    fun historyButtonClick(
        cmdIndexFragment: CommandIndexFragment
    ) {
        val binding = cmdIndexFragment.binding
        val historyButtonView = binding.cmdindexHistoryButton
        val cmdindexUrlHistoryButton = binding.cmdindexUrlHistoryButton
        historyButtonView.setOnClickListener {
            historyButtonInnerView ->
            FannelHistoryButtonEvent.invoke(cmdIndexFragment)
//            HistoryButtonSwitcher.switch(
//                cmdIndexFragment,
//                cmdIndexFragment.context?.getString(
//                    R.string.index_terminal_fragment
//                ),
//                cmdIndexFragment.historySwitch,
//                urlHistoryButtonEvent,
//                CLICLTYPE.SHORT
//            )
        }

        cmdindexUrlHistoryButton.setOnClickListener {
                historyButtonInnerView ->
            UrlHistoryButtonEvent.invoke(cmdIndexFragment)
//            FannelHistoryButtonEvent(
//                cmdIndexFragment,
////                   sharedPref,
//            ).invoke()
//            true
        }
    }
}
