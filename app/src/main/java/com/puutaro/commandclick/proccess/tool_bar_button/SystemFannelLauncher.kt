package com.puutaro.commandclick.proccess.tool_bar_button

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateManager
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object SystemFannelLauncher {
    fun launch(
        fragment: Fragment,
//        parentDirPath: String,
        fannelName: String,
    ) {
        val context = fragment.context
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val fannelConList = ReadText(
            File(
                cmdclickDefaultAppDirPath,
                fannelName
            ).absolutePath
        ).textToList()

        val mainFannelConList = ReadText(
            File(cmdclickDefaultAppDirPath, fannelName).absolutePath
        ).textToList()
        val setReplaceVariableMap =
            JavaScriptLoadUrl.createMakeReplaceVariableMapHandler(
                context,
                mainFannelConList,
//                parentDirPath,
                fannelName,
            )

        val mainFannelSettingConList = CommandClickVariables.extractSettingValListByFannelName(
            mainFannelConList,
//            fannelName
        ).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it?.joinToString("\n") ?: String(),
                setReplaceVariableMap,
//                parentDirPath,
                fannelName,
            ).split("\n")
        }

        val fannelState = FannelStateManager.getState(
            context,
            fannelName,
            mainFannelSettingConList,
            setReplaceVariableMap,
        )
        val editFragmentTag = DecideEditTag(
            fannelConList,
//            parentDirPath,
            fannelName,
            fannelState
        ).decide()
            ?: return
        OnEditExecuteEvent.invoke(
            fragment,
            editFragmentTag,
//            parentDirPath,
            fannelName,
            fannelState,
        )
    }

    fun launchFromActivity(
        activity: MainActivity,
        editFragmentArgs: EditFragmentArgs,
//        appDirPath: String,
        fannelName: String,
    ){
        val sharedPref = FannelInfoTool.getSharePref(activity)

        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val mainFannelConList = ReadText(
            File(cmdclickDefaultAppDirPath, fannelName).absolutePath
        ).textToList()
        val setReplaceVariableMap =
            JavaScriptLoadUrl.createMakeReplaceVariableMapHandler(
                activity,
                mainFannelConList,
//                appDirPath,
                fannelName,
            )

        val mainFannelSettingConList = CommandClickVariables.extractSettingValListByFannelName(
            mainFannelConList,
//            fannelName
        ).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it?.joinToString("\n") ?: String(),
                setReplaceVariableMap,
//                appDirPath,
                fannelName,
            ).split("\n")
        }

        val fannelState = FannelStateManager.getState(
            activity,
            fannelName,
            mainFannelSettingConList,
            setReplaceVariableMap
        )
        FannelInfoTool.putAllFannelInfo(
            sharedPref,
//            appDirPath,
            fannelName,
            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key,
            fannelState,
        )
        val cmdEditFragmentTag =
            FragmentTagManager.makeCmdValEditTag(
//                appDirPath,
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