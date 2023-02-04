package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button.AutoCompleteEditTexter

class ExecAutoCompUpdate {
    companion object {
        fun update(
            activity: MainActivity,
            currentAppDirPath: String
        ) {
            val cmdIndexFragment = try {
                activity.supportFragmentManager.findFragmentByTag(
                    activity.getString(R.string.command_index_fragment)
                ) as CommandIndexFragment
            } catch (e: Exception) {
                return
            }
            val autoCompleteText = cmdIndexFragment.binding.cmdSearchEditText
            if (!autoCompleteText.isVisible) return
            AutoCompleteEditTexter.setAdapter(
                activity,
                cmdIndexFragment.binding.cmdSearchEditText,
                currentAppDirPath,
            )
        }
    }
}