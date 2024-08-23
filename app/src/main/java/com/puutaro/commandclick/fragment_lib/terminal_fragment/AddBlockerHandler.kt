package com.puutaro.commandclick.fragment_lib.terminal_fragment

import androidx.fragment.app.activityViewModels
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object AddBlockerHandler {
    fun handle(
        terminalFragment: TerminalFragment
    ){
        val onAdBlock =
            terminalFragment.onAdBlock ==
                    SettingVariableSelects.OnAdblockSelects.ON.name
        ToastUtils.showLong(onAdBlock.toString())
        if(!onAdBlock) return
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        if(
            terminalViewModel.blockListCon.isNotEmpty()
        ) return
        val context = terminalFragment.context ?: return
        val listener =
            context as? TerminalFragment.OnAdBlockListener
        listener?.exeOnAdblock()
    }
}