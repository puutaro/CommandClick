package com.puutaro.commandclick.view_model.activity

import androidx.lifecycle.ViewModel
import com.puutaro.commandclick.common.variable.path.UsePath
import kotlinx.coroutines.Job


class TerminalViewModel: ViewModel() {

    var currentMonitorFileName = UsePath.cmdClickMonitorFileName_1
    var onDisplayUpdate = true
    var onBottomScrollbyJs = true
    var editExecuteOnceCurrentShellFileName: String? = null
    var launchUrl: String? = null
    var onExecInternetButtonShell = false
    var onDialog = false
    var dialogReturnValue = String()
    var jsArguments = String()
    var blockListCon = String()
    var isStop = false
    var multiSelectTabString = String()
    var jsExecuteJob: Job? = null
    var onPermDialog = false
}

