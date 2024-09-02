package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.os.Handler


object RemoveRunner {
    fun execute(
        runner: Runnable?,
        handler: Handler
    ){
        if(runner == null) return
        handler.removeCallbacksAndMessages(runner);
    }
}