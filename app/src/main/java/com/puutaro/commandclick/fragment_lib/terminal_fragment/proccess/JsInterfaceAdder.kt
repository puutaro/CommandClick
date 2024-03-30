package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.webkit.WebView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCmdIntent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsArgs
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsBroadcast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCsv
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
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsCon
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsToolbar
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsListSelect
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsMap
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
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsStop
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsText
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsTextToSpeech
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsToast
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUbuntu
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUrl
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsUtil
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.JsMusic
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsFannelConSaver
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsListConSBSaver
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit.JsValEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.file.JsF
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.judge.JsBeforeInfo
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.JsCcUsage
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.JsCopyItem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.JsDeleteItem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.JsDesc
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.JsEditorItem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.JsListTsvUpdater
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.JsRenameItem
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.list_index.JsShowItemCon
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr.JsExecQr
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr.JsQrGetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr.JsQrLogoEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsAction
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsAppDirAdder
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsBackstack
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsCancel
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsCmdValFrag
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsMonitorSizing
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSelectTerm
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSettingValFrag
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSystemFannel
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsConfigEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsExit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsFannelExecer
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsMonitorShower
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSharePref
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsAddGmailCon
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsAddToUrlHistory
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
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.tsv.JsTsv
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder

object JsInterfaceAdder {
    fun add(
        terminalFragment: TerminalFragment,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsIntent(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsDialog(terminalFragment),
        )

        ExecJsInterfaceAdder.add(
            webView,
            JsArgs(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCmdIntent(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCurl(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsToast(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsStop(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsUtil(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsUrl(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCsv(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsText(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsPath(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsTextToSpeech(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsPdf(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsPulseAudioReceiver(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSendKey(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsNetTool(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsHtml(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsUbuntu(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsLinux(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsDirect(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsBroadcast(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFannelInstaller(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsIconSelectBox(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsMap(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsMusic(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCon(terminalFragment)
        )
        dialogAdder(
            terminalFragment,
            webView
        )
        editAdder(
            terminalFragment,
            webView
        )
        fileAdder(
            terminalFragment,
            webView,
        )
        qrAdder(
            terminalFragment,
            webView
        )
        qrLogoAdder(
            terminalFragment,
            webView
        )
        systemAdder(
            terminalFragment,
            webView
        )
        toolbarAdder(
            terminalFragment,
            webView,
        )
        indexListAdder(
            terminalFragment,
            webView
        )
        tsvAdder(
            terminalFragment,
            webView
        )
        judgeAdder(
            terminalFragment,
            webView,
        )
    }

    private fun dialogAdder(
        terminalFragment: TerminalFragment,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsDialog(terminalFragment),
        )
    }

    private fun fileAdder(
        terminalFragment: TerminalFragment,
        webView: WebView,
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsFileSystem(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsF(terminalFragment)
        )
    }

    private fun qrAdder(
        terminalFragment: TerminalFragment,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsQr(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsQrEdit(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
                JsQrGetter(terminalFragment)
        )
    }

    private fun editAdder(
        terminalFragment: TerminalFragment,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsDirSelect(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsEdit(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsEditor(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFileSelect(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsListSelect(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsReplaceVariables(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsScript(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsValEdit(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFannelConSaver(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsListConSBSaver(terminalFragment)
        )
    }

    private fun systemAdder(
        terminalFragment: TerminalFragment,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsPermission(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSystemFannel(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsMonitorSizing(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSettingValFrag(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSelectTerm(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsConfigEdit(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsAppDirAdder(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsMonitorShower(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFannelExecer(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCancel(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCmdValFrag(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsBackstack(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsAction(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsExit(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsSharePref(terminalFragment)
        )
    }

    private fun toolbarAdder(
        terminalFragment: TerminalFragment,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsToolbar(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFileAdder(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFileOrDirListGetter(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsProcessKiller(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCcUsage(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsQrScanner(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsMonitorRefresh(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsFileOrDirGetter(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsEditToolbarSwitcher(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsAddToUrlHistory(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsAddUrlCon(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsAddGmailCon(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsUrlAdder(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCmdValSaveAndBack(terminalFragment)
        )
    }

    private fun indexListAdder(
        terminalFragment: TerminalFragment,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsDeleteItem(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsShowItemCon(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsCopyItem(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsRenameItem(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsDesc(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsEditorItem(terminalFragment)
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsListTsvUpdater(terminalFragment)
        )
    }

    private fun qrLogoAdder(
        terminalFragment: TerminalFragment,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsExecQr(terminalFragment),
        )
        ExecJsInterfaceAdder.add(
            webView,
            JsQrLogoEdit(terminalFragment)
        )
    }

    private fun tsvAdder(
        terminalFragment: TerminalFragment,
        webView: WebView
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsTsv(terminalFragment)
        )
    }

    private fun judgeAdder(
        terminalFragment: TerminalFragment,
        webView: WebView,
    ){
        ExecJsInterfaceAdder.add(
            webView,
            JsBeforeInfo(terminalFragment)
        )
    }
}