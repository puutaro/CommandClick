package com.puutaro.commandclick.activity_lib.event.lib.terminal

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment

class ExecPageSearchResult {
    companion object {
        fun reflect(
            activity: MainActivity,
            activeMatchOrdinal: Int,
            numberOfMatches: Int
        ) {
            val cmdIndexFragment = try {
                activity.supportFragmentManager.findFragmentByTag(
                    activity.getString(R.string.command_index_fragment)
                ) as CommandIndexFragment
            } catch (e: Exception) {
                return
            }
            if (!cmdIndexFragment.isVisible) return
            val binding = cmdIndexFragment.binding
            val pageSearch = binding.pageSearch
            val cmdindexSearchTotal = pageSearch.cmdindexSearchTotal
            val displayActivePerTotal = "${activeMatchOrdinal}/${numberOfMatches}"
            cmdindexSearchTotal.setText(displayActivePerTotal)
            if (numberOfMatches == 0) {
                cmdindexSearchTotal.setTextColor(
                    activity.getColor(com.termux.shared.R.color.dark_red)
                )
            } else {
                cmdindexSearchTotal.setTextColor(
                    activity.getColor(R.color.black)
                )
            }
        }
    }
}