package com.puutaro.commandclick.proccess

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.TargetFragmentInstance


class ExistTerminalFragment {
    companion object {
        fun how (
            fragment: Fragment,
            terminalFragmentTag: String?
        ): TerminalFragment? {
            val terminalFragmentSource =
                TargetFragmentInstance()
                    .getFromFragment<TerminalFragment>(
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