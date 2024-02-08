package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FDialogTempFile
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateManager
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod

class JsFDialog(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val readSharedPreferences = terminalFragment.readSharedPreferences

    @JavascriptInterface
    fun launch(
        settingValConSrc: String,
        cmdValConSrc: String,
    ){
        execLaunch(
            settingValConSrc,
            cmdValConSrc,
        )
    }

    @JavascriptInterface
    fun launchByFree(
        settingValConSrc: String,
        cmdValConSrc: String,
    ){
        execLaunch(
            settingValConSrc,
            cmdValConSrc,
            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key,
        )
    }

    private fun execLaunch(
        settingValConSrc: String,
        cmdValConSrc: String,
        destiOnShortcut: String = EditFragmentArgs.Companion.OnShortcutSettingKey.OFF.key,
    ){
        val srcAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharedPreferences,
            SharePrefferenceSetting.current_app_dir
        )
        val srcFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharedPreferences,
            SharePrefferenceSetting.current_fannel_name
        )
        val onShortcut = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharedPreferences,
            SharePrefferenceSetting.on_shortcut
        )
        val fannelState = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharedPreferences,
            SharePrefferenceSetting.current_fannel_state
        )
        val destiFDialogFannelName =
            "${CommandClickScriptVariable.makeCopyPrefix()}_${UsePath.fDialogTempFannelName}"
        val fannelCon = JsScript(terminalFragment).makeFannelCon(
            settingValConSrc,
            cmdValConSrc,
        )
        val isSuccess = FDialogTempFile.create(
            terminalFragment,
            readSharedPreferences,
            destiFDialogFannelName,
            fannelCon,
            destiOnShortcut
        )
        if(!isSuccess) return

        val srcReadSharePreferenceMap = mapOf(
            SharePrefferenceSetting.current_app_dir.name
                    to srcAppDirPath,
            SharePrefferenceSetting.current_fannel_name.name
                    to srcFannelName,
            SharePrefferenceSetting.on_shortcut.name
                    to onShortcut,
            SharePrefferenceSetting.current_fannel_state.name
                    to fannelState,
        )
        val destiFannelState = FannelStateManager.getSate(
            srcAppDirPath,
            destiFDialogFannelName
        )
        val desiReadSharePreferenceMap = mapOf(
            SharePrefferenceSetting.current_app_dir.name
                    to srcAppDirPath,
            SharePrefferenceSetting.current_fannel_name.name
                    to destiFDialogFannelName,
            SharePrefferenceSetting.on_shortcut.name
                    to destiOnShortcut,
            SharePrefferenceSetting.current_fannel_state.name
                    to destiFannelState
        )
        val cmdEditFragTag = FragmentTagManager.makeCmdValEditTag(
            srcAppDirPath,
            destiFDialogFannelName,
            destiFannelState,
        )
        val editFragArg = EditFragmentArgs(
            desiReadSharePreferenceMap,
            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT,
            srcReadSharePreferenceMap,
        )

        val destiTerminalFragTag =
            context?.getString(R.string.edit_terminal_fragment)
                ?: return

        val listener =
            context as? TerminalFragment.OnChangeEditFragmentListenerForTerm
                ?: return
        listener.onChangeEditFragment(
            editFragArg,
            cmdEditFragTag,
            destiTerminalFragTag
        )
    }
}