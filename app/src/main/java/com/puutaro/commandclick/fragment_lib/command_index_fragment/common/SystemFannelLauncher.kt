package com.puutaro.commandclick.fragment_lib.command_index_fragment.common

import android.content.Context
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SharePreffrenceMethod

object SystemFannelLauncher {
    fun launch(
        cmdIndexFragment: CommandIndexFragment,
        parentDirPath: String,
        fannelScriptName: String,
    ) {

        val context = cmdIndexFragment.context
        val sharedPref =
            cmdIndexFragment.activity?.getPreferences(
                Context.MODE_PRIVATE
            )
        val shellContentsList = ReadText(
            parentDirPath,
            fannelScriptName
        ).textToList()
        val editFragmentTag = DecideEditTag(
            shellContentsList,
            parentDirPath,
            fannelScriptName
        ).decide()
            ?: return
        SharePreffrenceMethod.putSharePreffrence(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_app_dir.name
                        to parentDirPath,
            )
        )
        OnEditExecuteEvent.invoke(
            cmdIndexFragment,
            editFragmentTag,
            sharedPref,
            fannelScriptName,
        )
    }
}