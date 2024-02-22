package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod

class JsSettingValFrag(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val readSharePreferenceMap = terminalFragment.readSharePreferenceMap
    private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_app_dir
    )
    private val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
        readSharePreferenceMap,
        SharePrefferenceSetting.current_fannel_name
    )

    @JavascriptInterface
    fun change_S(
        state: String
    ){
        val listener =
            context as? TerminalFragment.OnChangeEditFragmentListenerForTerm
                ?: return
        val editFragArg = EditFragmentArgs(
            readSharePreferenceMap,
            EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT,
        )
        val settingFragTag = FragmentTagManager.makeSettingValEditTag(
            currentAppDirPath,
            currentFannelName
        )
        listener.onChangeEditFragment(
            editFragArg,
            settingFragTag,
            activity?.getString(
                R.string.edit_terminal_fragment
            ) ?: String()
        )
    }
}