package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.view.View
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsToolbarSwitcher
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsQrScanner
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.AppProcessManager
import com.puutaro.commandclick.proccess.NoScrollUrlSaver
import com.puutaro.commandclick.proccess.SelectTermDialog
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.monitor.MonitorSizeManager
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddFileForEdit
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddGmailCon
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddUrl
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddUrlCon
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ChangeSettingFannel
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.GeneralFileGetter
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.GeneralFileListGetter
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ListSyncer
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.OkButtonHandler
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.PopupSettingMenu
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.QrConGetterDialog
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ToolbarMenuDialog
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.UrlHistoryAddToTsv
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.dialog.UsageDialog
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object JsPathHandlerForToolbarButton {

    fun handle(
        fragment: Fragment,
        mainOrSubFannelPath: String = String(),
        settingButtonView: View?,
        jsActionMap: Map<String, String>?,
        webView: WebView? = null
    ) {
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        val actionType = jsActionMap.get(
            JsActionDataMapKeyObj.JsActionDataMapKey.TYPE.key
        ) ?: return
        val jsActionType =
            JsActionDataMapKeyObj.JsActionDataTypeKey.values()
                .firstOrNull{
                    it.key == actionType
                } ?: return
        terminalShowHandle(fragment)
        when (jsActionType) {
            JsActionDataMapKeyObj.JsActionDataTypeKey.MACRO
            -> jsPathMacroHandler(
                fragment,
                mainOrSubFannelPath,
                settingButtonView,
                jsActionMap
            )

            JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON
            -> execJs(
                    fragment,
                    jsActionMap,
                    webView
                )
        }
    }

    private fun execJs(
        fragment: Fragment,
        jsActionMap: Map<String, String>,
        webView: WebView?
    ){
        if(
            jsActionMap.isEmpty()
        ) return
        val jsConSrc = jsActionMap.get(
            JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
        )
        val jsCon = jsConSrc
            ?.replace(Regex("\n[ \t]*//.*"), "")
            ?.replace(Regex("^[ \t]*//.*"), "")
        if(
            jsCon.isNullOrEmpty()
        ) return
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "js_execJs.txt").absolutePath,
//            listOf(
//                "jsConSrc: ${jsConSrc}",
//                "jsCon: ${jsCon}",
//            ).joinToString("\n\n\n")
//        )
        JavascriptExecuter.jsUrlLaunchHandler(
            fragment,
            JavaScriptLoadUrl.makeLastJsCon(jsCon),
            webView
        )
    }

    private fun jsPathMacroHandler(
        fragment: Fragment,
        mainOrSubFannelPath: String,
        settingButtonView: View?,
        jsActionMap: Map<String, String>
    ) {
        val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
        val fannelInfoMap =
            FannelInfoTool.getFannelInfoMap(
                fragment,
                mainOrSubFannelPath,
            )
//        val currentAppDirPath =
//            FannelInfoTool.getCurrentAppDirPath(
//                fannelInfoMap
//            )
        val currentFannelName =
            FannelInfoTool.getCurrentFannelName(
                fannelInfoMap,
            )
        if(
            jsActionMap.isEmpty()
        ) return
        val macroStr =
            jsActionMap.get(
                JsActionDataMapKeyObj.JsActionDataMapKey.JS_CON.key
            )
        val macro = MacroForToolbarButton.Macro.values().firstOrNull {
            it.name == macroStr
        } ?: return
        when (macro) {
            MacroForToolbarButton.Macro.KILL
            -> AppProcessManager.killDialog(
                fragment,
//                cmdclickDefaultAppDirPath,
                currentFannelName
            )
            MacroForToolbarButton.Macro.USAGE
            -> UsageDialog.launch(
                fragment,
            )
            MacroForToolbarButton.Macro.NO_SCROLL_SAVE_URL
            -> NoScrollUrlSaver.save(
                fragment,
//                currentAppDirPath,
                String()
            )
            MacroForToolbarButton.Macro.QR_SCAN
            -> ExecJsLoad.jsConLaunchHandler(
                fragment,
                "${
                    ExecJsInterfaceAdder.convertUseJsInterfaceName(
                        JsQrScanner::class.java.simpleName
                    )}.scan_S();",
            )

            MacroForToolbarButton.Macro.SHORTCUT
            -> {
                val listener =
                    fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                listener?.onToolbarMenuCategories(
                    ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT,
                    EditFragmentArgs(
                        fannelInfoMap,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                    )
                )
            }
//            MacroForToolbarButton.Macro.TERMUX_SETUP
//            -> {
//                val listener =
//                    fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
//                listener?.onToolbarMenuCategories(
//                    ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP,
//                    EditFragmentArgs(
//                        fannelInfoMap,
//                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
//                    )
//                )
//            }
//            MacroForToolbarButton.Macro.CONFIG
//            -> ConfigEdit.edit(fragment)

            MacroForToolbarButton.Macro.REFRESH_MONITOR
            -> TermRefresh.refresh(
                    terminalViewModel.currentMonitorFileName
                )

            MacroForToolbarButton.Macro.SELECT_MONITOR
            -> SelectTermDialog.launch(fragment)

            MacroForToolbarButton.Macro.RESTART_UBUNTU
            -> UbuntuServiceManager.launch(
                fragment.activity
            )

//            MacroForToolbarButton.Macro.INSTALL_FANNEL
//            -> SystemFannelLauncher.launch(
//                fragment,
////                UsePath.cmdclickDefaultAppDirPath,
//                SystemFannel.fannelRepoFannelName
//            )

            MacroForToolbarButton.Macro.ADD
                -> {
                if(
                    fragment !is EditFragment
                ) return
                AddFileForEdit.add(
                    fragment,
                    jsActionMap,
                )
            }
//            MacroForToolbarButton.Macro.ADD_APP_DIR
//            -> {
//                if(
//                    fragment !is EditFragment
//                ) return
//                AppDirAdder.add(
//                    fragment
//                )
//            }

//            MacroForToolbarButton.Macro.JS_IMPORT
//            -> SystemFannelLauncher.launch(
//                fragment,
////                UsePath.cmdclickDefaultAppDirPath,
//                SystemFannel.jsImportManagerFannelName
//            )

//            MacroForToolbarButton.Macro.APP_DIR_MANAGER
//            -> SystemFannelLauncher.launch(
//                fragment,
////                UsePath.cmdclickDefaultAppDirPath,
//                SystemFannel.appDirManagerFannelName
//            )

            MacroForToolbarButton.Macro.SIZING
            -> {
                if(
                    fragment !is EditFragment
                ) return
                MonitorSizeManager.changeForEdit(
                    fragment
                )
            }
            MacroForToolbarButton.Macro.MENU
            -> {
                if(
                    fragment !is EditFragment
                ) return
                PopupSettingMenu.launchSettingMenu(
                    fragment,
                    settingButtonView,
                    jsActionMap
                )
            }
            MacroForToolbarButton.Macro.D_MENU
            -> ToolbarMenuDialog.launch(
                fragment,
                mainOrSubFannelPath,
                settingButtonView,
                jsActionMap,
            )

            MacroForToolbarButton.Macro.SYNC
            -> {
                if(
                    fragment !is EditFragment
                ) return
                ListSyncer.sync(fragment)
            }

            MacroForToolbarButton.Macro.GET_FILE
            -> {
                if(
                    fragment !is EditFragment
                ) return
                GeneralFileGetter.get(
                    fragment,
                    jsActionMap
                )
            }
            MacroForToolbarButton.Macro.GET_DIR
            -> {
                if(
                    fragment !is EditFragment
                ) return
                GeneralFileGetter.get(
                    fragment,
                    jsActionMap,
                    true
                )
            }
            MacroForToolbarButton.Macro.GET_FILES
            -> {
                if(
                    fragment !is EditFragment
                ) return
                GeneralFileListGetter.get(
                    fragment,
                    jsActionMap,
                )
            }
            MacroForToolbarButton.Macro.GET_DIRS
            -> {
                if(
                    fragment !is EditFragment
                ) return
                GeneralFileListGetter.get(
                    fragment,
                    jsActionMap,
                    true,
                )
            }
            MacroForToolbarButton.Macro.GET_QR_CON
            -> {
                if(
                    fragment !is EditFragment
                ) return
                QrConGetterDialog.launch(
                    fragment,
                    jsActionMap
                )
            }
//
//            MacroForToolbarButton.Macro.FANNEL_REPO_SYNC
//            -> {
//                if(
//                    fragment !is EditFragment
//                ) return
//                SyncFannelRepo.sync(fragment)
//            }

            MacroForToolbarButton.Macro.EDIT
            -> ChangeSettingFannel.change(
                fragment,
                jsActionMap
            )

            MacroForToolbarButton.Macro.WEB_SEARCH,
            MacroForToolbarButton.Macro.PAGE_SEARCH,
            MacroForToolbarButton.Macro.NORMAL
            -> {
                val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
                    JsToolbarSwitcher::class.java.simpleName
                )
                ExecJsLoad.jsConLaunchHandler(
                    fragment,
                    """
                        ${useClassName}.switch_S(
                            "${macro.name}"
                        );
                    """.trimIndent()
                )
            }
            MacroForToolbarButton.Macro.OK
            -> {
                if(
                    fragment !is EditFragment
                ) return
                OkButtonHandler.handle(
                    fragment,
                )
            }
            MacroForToolbarButton.Macro.ADD_URL_HISTORY
            -> {
                if(
                    fragment !is EditFragment
                ) return
                UrlHistoryAddToTsv(
                    fragment,
                    jsActionMap,
                ).invoke()
            }
            MacroForToolbarButton.Macro.ADD_URL_CON
            -> {
                if(
                    fragment !is EditFragment
                ) return
                AddUrlCon.add(
                    fragment,
                    jsActionMap,
                )
            }
            MacroForToolbarButton.Macro.ADD_GMAIL_CON
            -> {
                if(
                    fragment !is EditFragment
                ) return
                AddGmailCon.add(
                    fragment,
                    jsActionMap,
                )
            }
            MacroForToolbarButton.Macro.ADD_URL
            -> {
                if(
                    fragment !is EditFragment
                ) return
                AddUrl.add(
                    fragment,
                    jsActionMap,
                )
            }
        }
    }

    private fun terminalShowHandle(
        fragment: Fragment
    ){
        if(
            fragment !is EditFragment
        ) return
        TerminalShowByTerminalDo.show(
            fragment,
        )
    }
}