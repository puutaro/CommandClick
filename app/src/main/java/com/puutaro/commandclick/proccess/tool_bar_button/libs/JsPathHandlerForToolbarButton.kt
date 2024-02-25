package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.content.Intent
import android.view.View
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.qr.JsQrGetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsSettingValFrag
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsFileOrDirGetter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar.JsQrScanner
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.ExecJsInterfaceAdder
import com.puutaro.commandclick.proccess.AppProcessManager
import com.puutaro.commandclick.proccess.NoScrollUrlSaver
import com.puutaro.commandclick.proccess.SelectTermDialog
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.proccess.edit.lib.ListContentsSelectBoxTool
import com.puutaro.commandclick.proccess.edit.lib.SaveTagForListContents
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.proccess.intent.lib.JavascriptExecuter
import com.puutaro.commandclick.proccess.monitor.MonitorSizeManager
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddFileForEdit
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddUrl
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddUrlCon
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AppDirAdder
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ConfigEdit
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ListSyncer
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.PopupSettingMenu
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.QrConGetterDialog
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ToolbarMenuDialog
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.UrlHistoryAddToTsv
import com.puutaro.commandclick.service.GitCloneService
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.dialog.UsageDialog
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File

object JsPathHandlerForToolbarButton {

    fun handle(
        editFragment: EditFragment,
        settingButtonView: View?,
        jsActionMap: Map<String, String>?
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
        when (jsActionType) {
            JsActionDataMapKeyObj.JsActionDataTypeKey.MACRO
            -> jsPathMacroHandler(
                editFragment,
                settingButtonView,
                jsActionMap
            )

            JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON
            -> execJs(
                    editFragment,
                    jsActionMap
                )
        }
    }

    private fun execJs(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>
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
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "js_execJs.txt").absolutePath,
            listOf(
                "jsConSrc: ${jsConSrc}",
                "jsCon: ${jsCon}",
            ).joinToString("\n\n\n")
        )
        JavascriptExecuter.jsUrlLaunchHandler(
            editFragment,
            JavaScriptLoadUrl.makeLastJsCon(jsCon)
        )
    }

    private fun jsPathMacroHandler(
        editFragment: EditFragment,
        settingButtonView: View?,
        jsActionMap: Map<String, String>
    ) {
        val readSharePreffernceMap = editFragment.readSharePreferenceMap
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val currentAppDirPath =
            SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            )
        val currentFannelName =
            SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_fannel_name
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
                editFragment,
                currentAppDirPath,
                currentFannelName
            )
            MacroForToolbarButton.Macro.USAGE
            -> UsageDialog.launch(
                editFragment,
            )
            MacroForToolbarButton.Macro.NO_SCROLL_SAVE_URL
            -> NoScrollUrlSaver.save(
                editFragment,
                currentAppDirPath,
                String()
            )
            MacroForToolbarButton.Macro.QR_SCAN
            -> ExecJsLoad.jsConLaunchHandler(
                editFragment,
                "${
                    ExecJsInterfaceAdder.convertUseJsInterfaceName(
                        JsQrScanner::class.java.simpleName
                    )}.scan_S();",
            )

            MacroForToolbarButton.Macro.SHORTCUT
            -> {
                val listener =
                    editFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                listener?.onToolbarMenuCategories(
                    ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT,
                    EditFragmentArgs(
                        readSharePreffernceMap,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                    )
                )
            }
            MacroForToolbarButton.Macro.TERMUX_SETUP
            -> {
                val listener =
                    editFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                listener?.onToolbarMenuCategories(
                    ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP,
                    EditFragmentArgs(
                        readSharePreffernceMap,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                    )
                )
            }
            MacroForToolbarButton.Macro.CONFIG
            -> ConfigEdit.edit(editFragment)

            MacroForToolbarButton.Macro.REFRESH_MONITOR
            -> TermRefresh.refresh(
                    terminalViewModel.currentMonitorFileName
                )

            MacroForToolbarButton.Macro.SELECT_MONITOR
            -> SelectTermDialog.launch(editFragment)

            MacroForToolbarButton.Macro.RESTART_UBUNTU
            -> UbuntuServiceManager.launch(
                editFragment.activity
            )

            MacroForToolbarButton.Macro.INSTALL_FANNEL
            -> SystemFannelLauncher.launch(
                editFragment,
                UsePath.cmdclickSystemAppDirPath,
                UsePath.fannelRepoFannelName
            )

            MacroForToolbarButton.Macro.ADD
                -> AddFileForEdit.add(
                editFragment,
                jsActionMap,
            )

            MacroForToolbarButton.Macro.ADD_APP_DIR
            -> AppDirAdder.add(
                editFragment
            )

            MacroForToolbarButton.Macro.JS_IMPORT
            -> SystemFannelLauncher.launch(
                editFragment,
                UsePath.cmdclickSystemAppDirPath,
                UsePath.jsImportManagerFannelName
            )

            MacroForToolbarButton.Macro.APP_DIR_MANAGER
            -> SystemFannelLauncher.launch(
                editFragment,
                UsePath.cmdclickSystemAppDirPath,
                UsePath.appDirManagerFannelName
            )

            MacroForToolbarButton.Macro.SIZING
            -> MonitorSizeManager.changeForEdit(
                editFragment
            )
            MacroForToolbarButton.Macro.MENU
            -> PopupSettingMenu.launchSettingMenu(
                editFragment,
                settingButtonView,
                jsActionMap
            )
            MacroForToolbarButton.Macro.D_MENU
            -> ToolbarMenuDialog.launch(
                editFragment,
                settingButtonView,
                jsActionMap,
            )

            MacroForToolbarButton.Macro.SYNC
            -> ListSyncer.sync(editFragment)

            MacroForToolbarButton.Macro.GET_FILE
            -> getFileOrDirHandler(
                editFragment,
            )
            MacroForToolbarButton.Macro.GET_DIR
            -> getFileOrDirHandler(
                editFragment,
                true
            )
            MacroForToolbarButton.Macro.GET_QR_CON
            -> QrConGetterDialog.launch(
                editFragment,
                jsActionMap
            )

            MacroForToolbarButton.Macro.FANNEL_REPO_SYNC
            -> syncFannelRepo(editFragment)

            MacroForToolbarButton.Macro.EDIT
            -> changeSettingFragment(
                editFragment,
                jsActionMap
            )

            MacroForToolbarButton.Macro.WEB_SEARCH,
            MacroForToolbarButton.Macro.PAGE_SEARCH,
            MacroForToolbarButton.Macro.NORMAL
            -> {
                val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
                    JsQrGetter::class.java.simpleName
                )
                ExecJsLoad.jsConLaunchHandler(
                    editFragment,
                    """
                        ${useClassName}.get_S(
                            "${macro.name}"
                        );
                    """.trimIndent()
                )
            }
            MacroForToolbarButton.Macro.OK
            -> OkHandler(
                    editFragment,
                ).execForOk()
            MacroForToolbarButton.Macro.ADD_URL_HISTORY
            -> UrlHistoryAddToTsv(
                editFragment,
                jsActionMap,
            ).invoke()
            MacroForToolbarButton.Macro.ADD_URL_CON
            -> AddUrlCon.add(
                editFragment,
                jsActionMap,
            )
            MacroForToolbarButton.Macro.ADD_URL
            -> AddUrl.add(
                editFragment,
                jsActionMap,
            )
        }
    }

    private fun getFileOrDirHandler(
        editFragment: EditFragment,
        onDirectoryPick: Boolean = false,
    ) {
        val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
            JsFileOrDirGetter::class.java.simpleName
        )
        ExecJsLoad.jsConLaunchHandler(
            editFragment,
            "${useClassName}.get_S(${onDirectoryPick});",
        )
    }

    private fun syncFannelRepo(
        editFragment: EditFragment
    ) {
        val context = editFragment.context
            ?: return
        val intent = Intent(
            context,
            GitCloneService::class.java
        )
        context.startForegroundService(intent)
    }


    private fun changeSettingFragment(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>?
    ) {
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap,
        ) ?: emptyMap()
        val currentState = argsMap.values.firstOrNull() ?: String()
        val useClassName = ExecJsInterfaceAdder.convertUseJsInterfaceName(
            JsSettingValFrag::class.java.simpleName
        )
        ExecJsLoad.jsConLaunchHandler(
            editFragment,
            "${useClassName}.change_S(\"${currentState}\");",
        )
    }


    private class OkHandler(
        private val editFragment: EditFragment,
    ) {
        private val context = editFragment.context
        private val readSharePreffernceMap = editFragment.readSharePreferenceMap
        private val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        private val currentScriptFileName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        private val enableCmdEdit = editFragment.enableCmdEdit
        private val onPassCmdVariableEdit =
            editFragment.passCmdVariableEdit ==
                    CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE
        private val scriptFileSaver = ScriptFileSaver(
            editFragment,
        )

        fun execForOk() {
            val buttonTag = SaveTagForListContents.OK.tag
            scriptFileSaver.save()
            val isCmdEditExecute = enableCmdEdit
                    && editFragment.enableEditExecute
                    && !onPassCmdVariableEdit
            val isSettingEditByPass = enableCmdEdit
                    && editFragment.enableEditExecute
                    && onPassCmdVariableEdit
            val isSettingEdit = !enableCmdEdit

//            val isFdialogFannel = FDialogTempFile.howFDialogFile(currentScriptFileName)
            val isOnlyCmdEditNoFdialog = enableCmdEdit
                    && !editFragment.enableEditExecute
//                    && !isFdialogFannel
//            val isOnlyCmdEditWithFdialog = enableCmdEdit
//                    && !editFragment.enableEditExecute
//                    && isFdialogFannel
            when (true) {
                isCmdEditExecute -> {
                    Keyboard.hiddenKeyboardForFragment(
                        editFragment
                    )
                    ListContentsSelectBoxTool.saveListContents(
                        editFragment,
                        buttonTag
                    )
                    TerminalShowByTerminalDo.show(
                        editFragment,
                    )
                    ExecJsOrSellHandler.handle(
                        editFragment,
                        currentAppDirPath,
                        currentScriptFileName,
                    )
                }
//                isOnlyCmdEditWithFdialog ->
//                    fDialogOkButtonProcess()

                isSettingEditByPass,
                isOnlyCmdEditNoFdialog,
                isSettingEdit,
                -> {
                    val listener =
                        context as? EditFragment.onToolBarButtonClickListenerForEditFragment
                    listener?.onToolBarButtonClickForEditFragment(
                        String(),
                        ToolbarButtonBariantForEdit.CANCEL,
                        mapOf(),
                        false
                    )
                }
                else -> {}
            }
        }

//        private fun fDialogOkButtonProcess() {
//            FreeDialogReflector.reflect(
//                editFragment.srcReadSharePreffernceMap,
//                editFragment.readSharePreferenceMap,
//            )
//            val listener =
//                this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
//            listener?.onToolBarButtonClickForEditFragment(
//                String(),
//                ToolbarButtonBariantForEdit.CANCEL,
//                mapOf(),
//                false
//            )
//        }
    }
}