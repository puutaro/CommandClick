package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod

class JsCmdValFrag(
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
    fun stateChange_S(
        state: String,
        disableAddToBackStack: Boolean,
    ){
        val listener =
            context as? TerminalFragment.OnChangeEditFragmentListenerForTerm
                ?: return
        val fannelStateKeyName =
            SharePrefferenceSetting.current_fannel_state.name
        val updatedReadSharePreferenceMap =
            readSharePreferenceMap.map {
                val keyName = it.key
                when(keyName){
                    fannelStateKeyName -> keyName to state
                    else -> keyName to it.value
                }
            }.toMap()
        val editFragArg = EditFragmentArgs(
            updatedReadSharePreferenceMap,
            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT,
        )
        val cmdValEditFragTag = FragmentTagManager.makeCmdValEditTag(
            currentAppDirPath,
            currentFannelName,
            state
        )
        listener.onChangeEditFragment(
            editFragArg,
            cmdValEditFragTag,
            activity?.getString(
                R.string.edit_terminal_fragment
            ) ?: String(),
            disableAddToBackStack
        )
    }
}