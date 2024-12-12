package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.webkit.WebView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCmdIntent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsArgs
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsBroadcast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCsv
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCulc
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCurl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsDirSelect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsDirect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsEditor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsFannelInstaller
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsFileSelect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.file.JsFileSystem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsHtml
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.JsIconSelectBox
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsIntent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsLinux
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsMap
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.JsCon
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsToolbar
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsListSelect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsNetTool
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsPath
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsPdf
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsPermission
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsPulseAudioReceiver
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr.JsQr
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr.JsQrEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsReplaceVariables
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsScript
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsSendKey
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.JsText
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsTextToSpeech
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsToast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUbuntu
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUrl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUtil
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsMusic
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsVar
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsEditDialog
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.JsDiff
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.JsToListFilter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.JsToListMap
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsFannelConSaver
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsListConSBSaver
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsValEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.judge.JsBeforeInfo
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.JsCcUsage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list.JsCopyItem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list.JsCopySItem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list.JsDeleteItem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list.JsDesc
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list.JsEditorItem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list.JsListTsvUpdater
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list.JsRenameItem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit_list.JsShowItemCon
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr.JsExecQr
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr.JsQrGetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr.JsQrLogoEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsAction
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsBackstack
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsCancel
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsCmdValFrag
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsMonitorSizing
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSelectMonitor
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSettingValFrag
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSystemFannel
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsExit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsFannelExecer
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsFannelInfo
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsKeyboard
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsMonitorShower
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsPinFannel
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSelectionText
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsService
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSharePref
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsStateChange
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsToolBarCtrl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsToolbarSwitcher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsUrlHistory
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsAddGmailCon
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsAddFromUrlHistory
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsAddUrlCon
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsEditToolbarSwitcher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsFileAdder
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsFileOrDirGetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsMonitorRefresh
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsProcessKiller
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsQrScanner
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsCmdValSaveAndBack
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsFileOrDirListGetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsUrlAdder
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsUrlHistoryLauncher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.tsv.JsTsv
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import java.lang.ref.WeakReference

object JsInterfaceAdder {
    fun add(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsIntent(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsDialog(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsEditDialog(terminalFragmentRef),
        )

        ExecJsInterfaceAdder.add(
            webView,
            JsArgs(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCmdIntent(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCurl(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsToast(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsUtil(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsUrl(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCsv(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsText(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsPath(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsTextToSpeech(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsPdf(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsPulseAudioReceiver(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSendKey(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsNetTool(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsHtml(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsUbuntu(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsLinux(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsDirect(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsBroadcast(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFannelInstaller(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsIconSelectBox(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsMap(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsMusic(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCon(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsVar(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCulc(terminalFragmentRef)
        )
        dialogAdder(
            terminalFragmentRef,
            webView
        )
        editAdder(
            terminalFragmentRef,
            webView
        )
        fileAdder(
            terminalFragmentRef,
            webView,
        )
        qrAdder(
            terminalFragmentRef,
            webView
        )
        qrLogoAdder(
            terminalFragmentRef,
            webView
        )
        systemAdder(
            terminalFragmentRef,
            webView
        )
        toolbarAdder(
            terminalFragmentRef,
            webView,
        )
        indexListAdder(
            terminalFragmentRef,
            webView
        )
        tsvAdder(
            terminalFragmentRef,
            webView
        )
        judgeAdder(
            terminalFragmentRef,
            webView,
        )
        collectionAdder(
            terminalFragmentRef,
            webView,
        )
    }

    private fun dialogAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsDialog(terminalFragmentRef),
        )
    }

    private fun fileAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView,
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsFileSystem(terminalFragmentRef),
        )
    }

    private fun qrAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsQr(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsQrEdit(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
                JsQrGetter(terminalFragmentRef)
        )
    }

    private fun editAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsDirSelect(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsEdit(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsEditor(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFileSelect(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsListSelect(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsReplaceVariables(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsScript(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsValEdit(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFannelConSaver(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsListConSBSaver(terminalFragmentRef)
        )
    }

    private fun systemAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsPermission(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSystemFannel(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsService(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsMonitorSizing(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSettingValFrag(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSelectMonitor(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsMonitorShower(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFannelExecer(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCancel(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCmdValFrag(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsBackstack(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsAction(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsExit(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSharePref(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsKeyboard(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsStateChange(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsUrlHistory(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsToolbarSwitcher(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsToolBarCtrl(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSelectionText(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsPinFannel(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFannelInfo(terminalFragmentRef)
        )
    }

    private fun toolbarAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsToolbar(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFileAdder(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFileOrDirListGetter(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsProcessKiller(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCcUsage(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsQrScanner(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsMonitorRefresh(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFileOrDirGetter(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsEditToolbarSwitcher(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsAddFromUrlHistory(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsAddUrlCon(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsAddGmailCon(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsUrlAdder(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCmdValSaveAndBack(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsUrlHistoryLauncher(terminalFragmentRef)
        )
    }

    private fun indexListAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsDeleteItem(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsShowItemCon(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCopyItem(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCopySItem(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsRenameItem(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsDesc(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsEditorItem(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsListTsvUpdater(terminalFragmentRef)
        )
    }

    private fun qrLogoAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsExecQr(terminalFragmentRef),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsQrLogoEdit(terminalFragmentRef)
        )
    }

    private fun tsvAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsTsv(terminalFragmentRef)
        )
    }

    private fun judgeAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView,
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsBeforeInfo(terminalFragmentRef)
        )
    }

    private fun collectionAdder(
        terminalFragmentRef: WeakReference<TerminalFragment>,
        webView: WebView,
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsDiff(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsToListFilter(terminalFragmentRef)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsToListMap(terminalFragmentRef)
        )
    }
}