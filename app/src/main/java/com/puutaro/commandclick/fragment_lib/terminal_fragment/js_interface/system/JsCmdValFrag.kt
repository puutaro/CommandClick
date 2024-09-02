package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.lang.ref.WeakReference

class JsCmdValFrag(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun stateChange_S(
        state: String,
        disableAddToBackStack: Boolean,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val listener =
            context as? TerminalFragment.OnChangeEditFragmentListenerForTerm
                ?: return
        val fannelStateKeyName =
            FannelInfoSetting.current_fannel_state.name
        val updatedFannelInfoMap =
            fannelInfoMap.map {
                val keyName = it.key
                when(keyName){
                    fannelStateKeyName -> keyName to state
                    else -> keyName to it.value
                }
            }.toMap()
        val editFragArg = EditFragmentArgs(
            updatedFannelInfoMap,
            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT,
        )
        val cmdValEditFragTag = FragmentTagManager.makeCmdValEditTag(
//            currentAppDirPath,
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