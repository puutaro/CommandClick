package com.puutaro.commandclick.proccess.history

import android.view.View
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.ExistTerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File


object HistoryButtonSwitcher {

    fun switch(
        fragment: androidx.fragment.app.Fragment,
        innerView: View,
        terminalFragmentTag: String?,
        historySwitch: String,
        urlHistoryButtonEvent: UrlHistoryButtonEvent,
        clickType: CLICLTYPE
    ) {
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
            clickType == CLICLTYPE.LONG
        ) switchOnSource
        else !switchOnSource

        if(switchOn) {
            CmdClickHistoryButtonEvent(
//                innerView,
                fragment,
                sharedPref,
            ).invoke()
            return
        }

        ExistTerminalFragment.how(
            fragment,
            terminalFragmentTag,
        )
            ?: return
        urlHistoryButtonHandler(
            fragment,
            innerView,
            fannelInfoMap,
            urlHistoryButtonEvent,
        )
    }
}


private fun urlHistoryButtonHandler(
    fragment: androidx.fragment.app.Fragment,
    innerView: View,
    fannelInfoMap: Map<String, String>,
    urlHistoryButtonEvent: UrlHistoryButtonEvent,
) {

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
    ) return
    if(
        onUrlHistory
    ) {
        urlHistoryButtonEvent.invoke()
        return
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
    ) return
    ExecJsOrSellHandler.handle(
        fragment,
        currentAppDirPath,
        currentShellFileName,
    )
}


enum class CLICLTYPE {
    LONG,
    SHORT
}