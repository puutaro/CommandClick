package com.puutaro.commandclick.proccess.setting_button

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object SystemFannelLauncher {
    fun launch(
        fragment: Fragment,
        parentDirPath: String,
        fannelScriptName: String,
    ) {

        val context = fragment.context
        val sharedPref =
            fragment.activity?.getPreferences(
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
        SharePreferenceMethod.putSharePreference(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_app_dir.name
                        to parentDirPath,
            )
        )
        OnEditExecuteEvent.invoke(
            fragment,
            editFragmentTag,
            sharedPref,
            fannelScriptName,
        )
    }
}