package com.puutaro.commandclick.proccess

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment


object CmdIndexToolbarSwitcher {
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
        val linearLayoutParam =
            cmdIndexFragment.binding.commandIndexFragment.layoutParams as LinearLayoutCompat.LayoutParams
        val cmdIndexFragmentWeight = linearLayoutParam.weight
        if(
            cmdIndexFragment.WebSearchSwitch
            && cmdIndexFragmentWeight == ReadLines.LONGTH
        ) return
        val binding = cmdIndexFragment.binding
        val pageSearch = binding.pageSearch
        val cmdclickPageSearchToolBar = pageSearch.cmdclickPageSearchToolBar
        if(cmdclickPageSearchToolBar.isVisible == onPageSearch) return

        val cmdclickToolBar = binding.cmdindexToolbarLinearLayout
//            binding.cmdclickToolBar
        val cmdPageSearchEditText = pageSearch.cmdPageSearchEditText
        val cmdSearchEditText = binding.cmdSearchEditText

        val linearLayoutParamPageSearchToolBar = LinearLayoutCompat.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
        )
        linearLayoutParamPageSearchToolBar.weight = weightSwicher(onPageSearch)

        val linearLayoutParamToolbar = LinearLayoutCompat.LayoutParams(
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

internal fun weightSwicher(
    onPageSearch: Boolean
): Float {
    return if(onPageSearch) 1F else 0F

}