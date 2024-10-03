package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsSettingValFrag(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

//    @JavascriptInterface
//    fun change_S(
//        fannelState: String
//    ){
//        val terminalFragment = terminalFragmentRef.get()
//            ?: return
//        val context = terminalFragment.context
//        val activity = terminalFragment.activity
//        val fannelInfoMap = terminalFragment.fannelInfoMap
//        val currentFannelName = FannelInfoTool.getCurrentFannelName(
//            fannelInfoMap
//        )
//        val listener =
//            context as? TerminalFragment.OnChangeEditFragmentListenerForTerm
//                ?: return
//        val editFragArg = EditFragmentArgs(
//            fannelInfoMap,
//            EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT,
//        )
//        val settingFragTag = FragmentTagManager.makeSettingValEditTag(
////            currentAppDirPath,
//            currentFannelName,
//            fannelState
//        )
//        listener.onChangeEditFragment(
//            editFragArg,
//            settingFragTag,
//            activity?.getString(
//                R.string.edit_terminal_fragment
//            ) ?: String()
//        )
//    }
}