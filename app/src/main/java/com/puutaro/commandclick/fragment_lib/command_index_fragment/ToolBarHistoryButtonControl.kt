package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.CLICLTYPE
import com.puutaro.commandclick.proccess.HistoryBottunSwicher
import com.puutaro.commandclick.proccess.UrlHistoryButtonEvent
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ToolBarHistoryButtonControl(
    private val cmdIndexFragment: CommandIndexFragment,
    private val readSharePreffernceMap: Map<String, String>,
){

    private val binding = cmdIndexFragment.binding
    val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
    private val historyButtonView = binding.cmdindexHistoryButton
    private val sharedPref =  cmdIndexFragment.activity?.getPreferences(Context.MODE_PRIVATE)
    private val urlHistoryButtonEvent = UrlHistoryButtonEvent(
        cmdIndexFragment,
        readSharePreffernceMap,
    )


    fun historyButtonClick() {
        historyButtonView.setOnClickListener {
            historyButtonInnerView ->
            HistoryBottunSwicher.switch(
                cmdIndexFragment,
                historyButtonInnerView,
                cmdIndexFragment.context?.getString(
                    R.string.index_terminal_fragment
                ),
                readSharePreffernceMap,
                cmdIndexFragment.historySwitch,
                urlHistoryButtonEvent,
                sharedPref,
                CLICLTYPE.SHORT
            )
        }

        historyButtonView.setOnLongClickListener {
                historyButtonInnerView ->
            HistoryBottunSwicher.switch(
                cmdIndexFragment,
                historyButtonInnerView,
                cmdIndexFragment.context?.getString(
                    R.string.index_terminal_fragment
                ),
                readSharePreffernceMap,
                cmdIndexFragment.historySwitch,
                urlHistoryButtonEvent,
                sharedPref,
                CLICLTYPE.LONG
            )
            true
        }
    }
}
