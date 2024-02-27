package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment

class ExecOnSearchTextChange {

    companion object {
        fun execOnSearchTextChange(
            activity: MainActivity,
            text: String
        ) {
            val commandIndexFragment = try {
                activity.supportFragmentManager.findFragmentByTag(
                    activity.getString(R.string.command_index_fragment)
                ) as CommandIndexFragment
            } catch (e: Exception) {
                return
            }
            if (!commandIndexFragment.isVisible) return
            try {
                commandIndexFragment.binding.cmdSearchEditText.setText(text)
            } catch (e: Exception) {
                return
            }
        }
    }
}