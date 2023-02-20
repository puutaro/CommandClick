package com.puutaro.commandclick.proccess

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class CmdIndexToolbarSwitcher {
    companion object {
        fun switch(
            cmdIndexFragment: CommandIndexFragment?,
            onPageSearch: Boolean
        ) {
            if(cmdIndexFragment == null) return
            if(!cmdIndexFragment.isVisible) return
            val context = cmdIndexFragment.context
            if(cmdIndexFragment.tag != context?.getString(
                    R.string.command_index_fragment
                )
            ) return
            val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
            if(
                cmdIndexFragment.WebSearchSwitch
                && terminalViewModel.readlinesNum == ReadLines.SHORTH
            ) return
            val binding = cmdIndexFragment.binding
            val cmdclickPageSearchToolBar = binding.cmdclickPageSearchToolBar
            if(cmdclickPageSearchToolBar.isVisible == onPageSearch) return
            val cmdclickToolBar = binding.cmdclickToolBar
            val cmdPageSearchEditText = binding.cmdPageSearchEditText
            val cmdSearchEditText = binding.cmdSearchEditText

            val linearLayoutParamPageSearchToolBar = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
            )
            linearLayoutParamPageSearchToolBar.weight = weightSwicher(onPageSearch)

            val linearLayoutParamToolbar = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
            )
            linearLayoutParamToolbar.weight = weightSwicher(!onPageSearch)

            cmdclickPageSearchToolBar.layoutParams = linearLayoutParamPageSearchToolBar
            cmdclickPageSearchToolBar.isVisible = onPageSearch
            cmdPageSearchEditText.setText(String())
            cmdSearchEditText.setText(String())
            cmdclickToolBar.layoutParams = linearLayoutParamToolbar
            if(onPageSearch) {
                cmdSearchEditText.clearFocus()
                cmdPageSearchEditText.requestFocus()
            } else {
                cmdPageSearchEditText.clearFocus()
            }
        }
    }
}

internal fun weightSwicher(
    onPageSearch: Boolean
): Float {
    return if(onPageSearch) 1F else 0F

}