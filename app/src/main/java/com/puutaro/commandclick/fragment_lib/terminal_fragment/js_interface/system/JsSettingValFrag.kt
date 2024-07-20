package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.FannelInfoTool

class JsSettingValFrag(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val activity = terminalFragment.activity
    private val fannelInfoMap = terminalFragment.fannelInfoMap
    private val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    private val currentFannelName = FannelInfoTool.getCurrentFannelName(
        fannelInfoMap
    )

    @JavascriptInterface
    fun change_S(
        fannelState: String
    ){
        val listener =
            context as? TerminalFragment.OnChangeEditFragmentListenerForTerm
                ?: return
        val editFragArg = EditFragmentArgs(
            fannelInfoMap,
            EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT,
        )
        val settingFragTag = FragmentTagManager.makeSettingValEditTag(
            currentAppDirPath,
            currentFannelName,
            fannelState
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