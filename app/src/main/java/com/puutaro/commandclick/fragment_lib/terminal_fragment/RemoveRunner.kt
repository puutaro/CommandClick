package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.os.Handler


class RemoveRunner {
    companion object {
        fun execute(
            runner: Runnable?,
            handler: Handler
        ){
            if(runner == null) return
            handler.removeCallbacksAndMessages(runner);
        }
    }
}