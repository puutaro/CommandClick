package com.puutaro.commandclick.proccess

import com.puutaro.commandclick.util.TargetFragmentInstance


class ExistTerminalFragment {
    companion object {
        fun how (
            fragment: androidx.fragment.app.Fragment,
            terminalFragmentTag: String?
        ): com.puutaro.commandclick.fragment.TerminalFragment? {
            val terminalFragmentSource =
                TargetFragmentInstance()
                    .getFromFragment<com.puutaro.commandclick.fragment.TerminalFragment>(
                        fragment.activity,
                        terminalFragmentTag
                    )
            return if(
                terminalFragmentSource?.isVisible != true
            ) null
            else {
                terminalFragmentSource
            }
        }
    }
}