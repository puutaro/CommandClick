package com.puutaro.commandclick.proccess.history

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.ExistTerminalFragment
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryButtonEvent
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryButtonEvent
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File


object HistoryButtonSwitcher {

    fun switch(
        fragment: Fragment,
        terminalFragmentTag: String?,
        historySwitch: String,
        urlHistoryButtonEvent: UrlHistoryButtonEvent,
        clickType: CLICLTYPE
    ) {
        launchCapture(
            fragment,
        )
        val fannelInfoMap = when(
            fragment
        ){
            is CommandIndexFragment -> fragment.fannelInfoMap
            is EditFragment -> fragment.fannelInfoMap
            else -> return
        }
        val sharedPref = FannelInfoTool.getSharePref(fragment.context)
        val switchOnSource = (
                historySwitch ==
                        SettingVariableSelects.HistorySwitchSelects.ON.name
                )
        val switchOn = if(
            clickType == CLICLTYPE.SHORT
        ) switchOnSource
        else !switchOnSource

        when(switchOn) {
            true -> FannelHistoryButtonEvent(
                fragment,
                sharedPref,
            ).invoke()
           else -> urlHistoryButtonHandler(
               fragment,
               fannelInfoMap,
               urlHistoryButtonEvent,
               terminalFragmentTag,
           ).let {
                   onUrlHistory ->
               if(
                   onUrlHistory
               ) return@let
               FannelHistoryButtonEvent(
                   fragment,
                   sharedPref,
               ).invoke()
           }
        }
    }

    private fun launchCapture(
        fragment: Fragment,
    ){
       when(fragment){
            is CommandIndexFragment -> {
                val listener = fragment.context as? CommandIndexFragment.OnCaptureActivityListenerForIndex
                    ?: return
                listener.onCaptureActivityForIndex()
            }
            is EditFragment -> {
                val listener = fragment.context as? EditFragment.OnCaptureActivityListenerForEdit
                    ?: return
                listener.onCaptureActivityForEdit()
            }
            else -> return
        }
    }
}

private fun urlHistoryButtonHandler(
    fragment: Fragment,
    fannelInfoMap: Map<String, String>,
    urlHistoryButtonEvent: UrlHistoryButtonEvent,
    terminalFragmentTag: String?,
): Boolean {
    ExistTerminalFragment.how(
        fragment,
        terminalFragmentTag,
    ) ?: return false
    val urlHistoryOrButtonExecUrlHistory =
        SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name
    val urlHistoryOrButtonExec = when(
        fragment
    ){
        is CommandIndexFragment
            -> fragment.urlHistoryOrButtonExec
        is EditFragment
            -> fragment.urlHistoryOrButtonExec
        else
            -> SettingVariableSelects.UrlHistoryOrButtonExecSelects.URL_HISTORY.name
    }
    val onTerminal = when(
        fragment
    ){
        is CommandIndexFragment
        -> true
        is EditFragment
        -> {
            fragment.terminalOn != SettingVariableSelects.TerminalDoSelects.OFF.name
        }
        else
        -> true
    }
    val onUrlHistory = urlHistoryOrButtonExec ==
            urlHistoryOrButtonExecUrlHistory
    if(
        !onTerminal
        && onUrlHistory
    ) return false
    if(
        onUrlHistory
    ) {
        urlHistoryButtonEvent.invoke()
        return true
    }

    val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
        fannelInfoMap
    )
    val currentShellFileName = UsePath.cmdclickButtonExecShellFileName
    if(
        !File(
            currentAppDirPath,
            currentShellFileName
        ).isFile
    ) return false
    ExecJsOrSellHandler.handle(
        fragment,
        currentAppDirPath,
        currentShellFileName,
    )
    return true
}


enum class CLICLTYPE {
    LONG,
    SHORT
}