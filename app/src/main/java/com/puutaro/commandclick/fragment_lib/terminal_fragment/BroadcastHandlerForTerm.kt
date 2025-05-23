package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.extra.PocketWebviewLaunchExtra
import com.puutaro.commandclick.common.variable.broadcast.extra.PocketWebviewLoadUrlExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.BroadcastHtmlReceiveHandler
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.HtmlLauncher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.JsDebugger
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.MonitorBroadcastManager
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.MonitorTextLauncher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.MonitorToast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.PocketWebViewLancher
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object BroadcastHandlerForTerm {
    fun handle(
        terminalFragment: TerminalFragment,
        intent: Intent,
    ){
        val action = intent.action
        val termBroadcastType = BroadCastIntentSchemeTerm.values().firstOrNull {
            it.action == action
        } ?: return
        when(termBroadcastType){
            BroadCastIntentSchemeTerm.HTML_LAUNCH
            ->  HtmlLauncher.launch(
                intent,
                terminalFragment,
            )
            BroadCastIntentSchemeTerm.ULR_LAUNCH
            -> BroadcastHtmlReceiveHandler.handle(
                terminalFragment,
                intent,
            )
            BroadCastIntentSchemeTerm.MONITOR_TEXT_PATH
            -> MonitorTextLauncher.handle(
                terminalFragment,
                intent,
            )
            BroadCastIntentSchemeTerm.MONITOR_MANAGER
            -> MonitorBroadcastManager.handle(
                terminalFragment,
                intent
            )
            BroadCastIntentSchemeTerm.MONITOR_TOAST
            -> MonitorToast.launch(
                intent
            )
            BroadCastIntentSchemeTerm.DEBUGGER_NOTI
            -> JsDebugger.launchNoti(
                terminalFragment,
                intent,
            )
            BroadCastIntentSchemeTerm.DEBUGGER_JS_WATCH
            -> JsDebugger.jsErrWatch(
                terminalFragment,
                intent,
            )
            BroadCastIntentSchemeTerm.DEBUGGER_SYS_WATCH -> {
                JsDebugger.sysErrWatch(
                    terminalFragment,
                    intent,
                )
            }
            BroadCastIntentSchemeTerm.DEBUGGER_CLOSE
            -> JsDebugger.close(
                terminalFragment,
                intent
            )
            BroadCastIntentSchemeTerm.POCKET_WEBVIEW_LAUNCH
            -> {
                PocketWebViewLancher.launch(
                    terminalFragment,
                    intent.getStringExtra(PocketWebviewLaunchExtra.url.schema),
                )
            }
            BroadCastIntentSchemeTerm.POCKET_WEBVIEW_LOAD_URL
            -> PocketWebViewLancher.loadUrl(
                terminalFragment,
                intent.getStringExtra(PocketWebviewLoadUrlExtra.url.schema),
            )
//            BroadCastIntentSchemeTerm.POCKET_WEBVIEW_PRELOAD_URL
//            -> PocketWebViewLancher.preLoadUrl(
//                terminalFragment,
//                intent.getStringExtra(PocketWebviewPreLoadUrlExtra.url.schema),
//            )
            BroadCastIntentSchemeTerm.FANNEL_PIN_BAR_UPDATE
            -> {
                val cmdindexSelectionSearchButton = TargetFragmentInstance.getCmdIndexFragmentFromFrag(
                    terminalFragment.activity
                )?.binding?.cmdindexSelectionSearchButton
                PinFannelBarManager.update(
                    terminalFragment.context,
                    terminalFragment.tag,
                    terminalFragment.binding.fannelPinRecyclerView,
                    cmdindexSelectionSearchButton,
                )
            }
        }
    }
}