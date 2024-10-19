package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryButtonEvent
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryButtonEvent


object ToolBarHistoryButtonControl {

    fun historyButtonClick(
        cmdIndexFragment: CommandIndexFragment
    ) {
        val binding = cmdIndexFragment.binding
        val cmdindexFannelCenterButton = binding.cmdindexFannelCenterButton
        val cmdindexUrlHistoryButton = binding.cmdindexUrlHistoryButton
        cmdindexFannelCenterButton.setOnClickListener {
            historyButtonInnerView ->
            FannelHistoryButtonEvent.invoke(cmdIndexFragment)
        }

        cmdindexUrlHistoryButton.setOnClickListener {
                historyButtonInnerView ->
            UrlHistoryButtonEvent.invoke(cmdIndexFragment)
        }
    }
}
