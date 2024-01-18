package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file_tool.FDialogTempFile
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod

class JsFDialog(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val readSharedPreferences = terminalFragment.readSharedPreferences

    @JavascriptInterface
    fun launch(
        fannelCon: String,
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
        val destiFDialogFannelName =
            "${CommandClickScriptVariable.makeCopyPrefix()}_${UsePath.fDialogTempFannelName}"

        val isSuccess = FDialogTempFile.create(
            terminalFragment,
            readSharedPreferences,
            destiFDialogFannelName,
            fannelCon,
        )
        if(!isSuccess) return

        val srcReadSharePreferenceMap = mapOf(
            SharePrefferenceSetting.current_app_dir.name
                    to srcAppDirPath,
            SharePrefferenceSetting.current_fannel_name.name
                    to srcFannelName,
            SharePrefferenceSetting.on_shortcut.name
                    to onShortcut
        )
        val desiReadSharePreferenceMap = mapOf(
            SharePrefferenceSetting.current_app_dir.name
                    to srcAppDirPath,
            SharePrefferenceSetting.current_fannel_name.name
                    to destiFDialogFannelName,
            SharePrefferenceSetting.on_shortcut.name
                    to EditFragmentArgs.Companion.OnShortcutSettingKey.OFF.key,
        )
        val cmdEditFragTag = FragmentTagManager.makeCmdValEditTag(
            srcAppDirPath,
            destiFDialogFannelName,
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