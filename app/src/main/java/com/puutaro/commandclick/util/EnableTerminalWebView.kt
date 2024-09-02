package com.puutaro.commandclick.util

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

object EnableTerminalWebView {
    suspend fun check(
        currentFragment: Fragment,
        fragmentTag: String?
    ): Boolean {
        return withContext(Dispatchers.Main) {
            var hitTimes = 0
            for (i in 1..10) {
                val targetTerminalFragment = TargetFragmentInstance.getFromFragment<TerminalFragment>(
                    currentFragment.activity,
                    fragmentTag,
                )
                if(
                    targetTerminalFragment?.isResumed == true
                    && targetTerminalFragment.binding.terminalWebView.isVisible
                ) {
                    hitTimes++
                }
                if(
                    hitTimes > 2
                ) return@withContext true
                delay(100)
            }
            return@withContext false
        }
    }
}