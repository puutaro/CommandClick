package com.puutaro.commandclick.proccess

import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.history_button.CmdClickHistoryButtonEvent
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File


object HistoryBottunSwitcher {

    fun switch(
        fragment: androidx.fragment.app.Fragment,
        innerView: View,
        terminalFragmentTag: String?,
        readSharePreffernceMap: Map<String, String>,
        historySwitch: String,
        urlHistoryButtonEvent:UrlHistoryButtonEvent,
        sharedPref: SharedPreferences?,
        clickType: CLICLTYPE
    ) {
        val switchOnSource = (
                historySwitch ==
                        SettingVariableSelects.Companion.HistorySwitchSelects.ON.name
                )
        val switchOn = if(
            clickType == CLICLTYPE.LONG
        ) switchOnSource
        else !switchOnSource

        if(switchOn) {
            CmdClickHistoryButtonEvent(
                innerView,
                fragment,
                sharedPref,
            ).invoke()
            return
        }

        ExistTerminalFragment
            .how(
                fragment,
                terminalFragmentTag,
            )
            ?: return
        urlHistoryButtonHandler(
            fragment,
            innerView,
            readSharePreffernceMap,
            urlHistoryButtonEvent,
        )
    }
}


private fun urlHistoryButtonHandler(
    fragment: androidx.fragment.app.Fragment,
    innerView: View,
    readSharePreffernceMap: Map<String, String>,
    urlHistoryButtonEvent:UrlHistoryButtonEvent,
) {

    val urlHistoryOrButtonExecUrlHistory =
        SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name
    val urlHistoryOrButtonExec = when(
        fragment
    ){
        is CommandIndexFragment
            -> fragment.urlHistoryOrButtonExec
        is EditFragment
            -> fragment.urlHistoryOrButtonExec
        else
            -> SettingVariableSelects.Companion.UrlHistoryOrButtonExecSelects.URL_HISTORY.name
    }
    val onTerminal = when(
        fragment
    ){
        is CommandIndexFragment
        -> true
        is EditFragment
        -> {
            fragment.terminalOn != SettingVariableSelects.Companion.TerminalDoSelects.OFF.name
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
        urlHistoryButtonEvent.invoke(
            innerView,
        )
        return
    }

    val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
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