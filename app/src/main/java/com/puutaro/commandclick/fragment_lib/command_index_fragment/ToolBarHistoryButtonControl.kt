package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.CLICLTYPE
import com.puutaro.commandclick.proccess.HistoryBottunSwitcher
import com.puutaro.commandclick.proccess.UrlHistoryButtonEvent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ToolBarHistoryButtonControl(
    private val cmdIndexCommandIndexFragment: CommandIndexFragment,
    private val readSharePreffernceMap: Map<String, String>,
){

    private val binding = cmdIndexCommandIndexFragment.binding
    val terminalViewModel: TerminalViewModel by cmdIndexCommandIndexFragment.activityViewModels()
    private val historyButtonView = binding.cmdindexHistoryButton
    private val sharedPref =  cmdIndexCommandIndexFragment.activity?.getPreferences(Context.MODE_PRIVATE)
    private val urlHistoryButtonEvent = UrlHistoryButtonEvent(
        cmdIndexCommandIndexFragment,
        readSharePreffernceMap,
    )


    fun historyButtonClick() {
        historyButtonView.setOnClickListener {
            historyButtonInnerView ->
            HistoryBottunSwitcher.switch(
                cmdIndexCommandIndexFragment,
                historyButtonInnerView,
                cmdIndexCommandIndexFragment.context?.getString(
                    R.string.index_terminal_fragment
                ),
                readSharePreffernceMap,
                cmdIndexCommandIndexFragment.historySwitch,
                urlHistoryButtonEvent,
                sharedPref,
                CLICLTYPE.SHORT
            )
        }

        historyButtonView.setOnLongClickListener {
                historyButtonInnerView ->
            HistoryBottunSwitcher.switch(
                cmdIndexCommandIndexFragment,
                historyButtonInnerView,
                cmdIndexCommandIndexFragment.context?.getString(
                    R.string.index_terminal_fragment
                ),
                readSharePreffernceMap,
                cmdIndexCommandIndexFragment.historySwitch,
                urlHistoryButtonEvent,
                sharedPref,
                CLICLTYPE.LONG
            )
            true
        }
    }
}
