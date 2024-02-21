package com.puutaro.commandclick.proccess.tool_bar_button

import android.content.Context
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateManager
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File

object SystemFannelLauncher {
    fun launch(
        fragment: Fragment,
        parentDirPath: String,
        fannelScriptName: String,
    ) {
        val shellContentsList = ReadText(
            File(
                parentDirPath,
                fannelScriptName
            ).absolutePath
        ).textToList()
        val fannelState = FannelStateManager.getState(
            parentDirPath,
            fannelScriptName,
            ReadText(
                File(parentDirPath, fannelScriptName).absolutePath
            ).textToList()
        )
        val editFragmentTag = DecideEditTag(
            shellContentsList,
            parentDirPath,
            fannelScriptName,
            fannelState
        ).decide()
            ?: return
        OnEditExecuteEvent.invoke(
            fragment,
            editFragmentTag,
            parentDirPath,
            fannelScriptName,
            fannelState,
        )
    }

    fun launchFromActivity(
        activity: MainActivity,
        editFragmentArgs: EditFragmentArgs,
        appDirPath: String,
        fannelName: String,
    ){
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val fannelState = FannelStateManager.getState(
            appDirPath,
            fannelName,
            ReadText(
                File(appDirPath, fannelName).absolutePath
            ).textToList()
        )
        SharePreferenceMethod.putAllSharePreference(
            sharedPref,
            appDirPath,
            fannelName,
            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key,
            fannelState,
        )
        val cmdEditFragmentTag =
            FragmentTagManager.makeCmdValEditTag(
                appDirPath,
                fannelName,
                fannelState
            )
        WrapFragmentManager.changeFragmentEdit(
            activity.supportFragmentManager,
            cmdEditFragmentTag,
            String(),
            editFragmentArgs,
        )
    }
}