package com.puutaro.commandclick.util

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

object EnableTerminalWebView {

    private var beforeTime = LocalDateTime.parse("2020-02-15T21:30:50")
    suspend fun check(
        currentFragment: Fragment,
        fragmentTag: String?
    ): Boolean {
        val currentDatetime = LocalDateTime.now()
        val isSaveSec = LocalDatetimeTool.getDurationSec(
            beforeTime,
            currentDatetime
        ) <= 2
        if(isSaveSec){
            beforeTime = currentDatetime
            return true
        }
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
                ) {
                    beforeTime = LocalDateTime.now()
                    return@withContext true
                }
                delay(100)
            }
            return@withContext false
        }
    }
}