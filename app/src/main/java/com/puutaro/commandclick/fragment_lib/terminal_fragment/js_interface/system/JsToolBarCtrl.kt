package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system

import android.webkit.JavascriptInterface
import androidx.core.view.isVisible
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.lang.ref.WeakReference

class JsToolBarCtrl(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun visibleAllToolBar(isShow: Boolean){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val cmdEditFragmentTag =
            FragmentTagManager.makeCmdValEditTag(
                FannelInfoTool.getCurrentFannelName(
                    fannelInfoMap,
                ),
                FannelInfoTool.getCurrentStateName(
                    fannelInfoMap,
                ),
            )
        val bottomFragment = TargetFragmentInstance.getCurrentBottomFragmentInFrag(
            activity,
            cmdEditFragmentTag,
        )
        val listener =
            context as? TerminalFragment.OnToolBarVisibleChangeListener
        listener?.onToolBarVisibleChange(
            isShow,
            bottomFragment
        )
    }

    @JavascriptInterface
    fun isVisiblePageSearchBar(): Boolean {
        val terminalFragment = terminalFragmentRef.get()
            ?: return false
        val activity = terminalFragment.activity
        val fannelInfoMap = terminalFragment.fannelInfoMap
        val cmdEditFragmentTag =
            FragmentTagManager.makeCmdValEditTag(
                FannelInfoTool.getCurrentFannelName(
                    fannelInfoMap,
                ),
                FannelInfoTool.getCurrentStateName(
                    fannelInfoMap,
                ),
            )
        val bottomFragment = TargetFragmentInstance.getCurrentBottomFragmentInFrag(
            activity,
            cmdEditFragmentTag,
        )
        if(bottomFragment !is CommandIndexFragment){
            return false
        }
        return bottomFragment.binding.pageSearch.cmdclickPageSearchToolBar.isVisible
    }

    @JavascriptInterface
    fun visibleSelectionBar(isShow: Boolean){
        val terminalFragment = terminalFragmentRef.get() ?: return
        val context = terminalFragment.context ?: return
        val listener = context as TerminalFragment.OnSelectionSearchBarSwitchListenerForTerm
        listener.onSelectionSearchBarSwitchForTerm(isShow)

    }
}