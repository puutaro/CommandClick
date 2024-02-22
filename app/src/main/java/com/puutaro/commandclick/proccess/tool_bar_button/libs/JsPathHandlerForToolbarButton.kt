package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.content.Intent
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.custom_view.NoScrollListView
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
import com.puutaro.commandclick.proccess.EnableNavForWebView
import com.puutaro.commandclick.proccess.ExistTerminalFragment
import com.puutaro.commandclick.proccess.NoScrollUrlSaver
import com.puutaro.commandclick.proccess.SelectTermDialog
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.proccess.edit.lib.ListContentsSelectBoxTool
import com.puutaro.commandclick.proccess.edit.lib.SaveTagForListContents
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.EditSettingJsTool
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.monitor.MonitorSizeManager
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddFileForEdit
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddUrl
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AddUrlCon
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.AppDirAdder
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ConfigEdit
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ListSyncer
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.QrConGetterDialog
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.SettingButtonSubMenuDialog
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ToolbarButtonArgsMaker
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
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
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
                toolbarButtonArgsMaker,
                jsActionMap
            )

            JsActionDataMapKeyObj.JsActionDataTypeKey.JS_CON
            -> execJs(
                    toolbarButtonArgsMaker,
                    jsActionMap
                )
        }
    }

    private fun execJs(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        jsActionMap: Map<String, String>
    ){
        if(
            jsActionMap.isEmpty()
        ) return
        val editFragment = toolbarButtonArgsMaker.editFragment
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
        ExecJsLoad.jsUrlLaunchHandler(
            editFragment,
            JavaScriptLoadUrl.makeLastJsCon(jsCon)
        )
    }

    private fun jsPathMacroHandler(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        jsActionMap: Map<String, String>
    ) {
        val editFragment = toolbarButtonArgsMaker.editFragment
        val readSharePreffernceMap = toolbarButtonArgsMaker.readSharePreffernceMap
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        val currentAppDirPath =
            SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            )
        val currentScriptFileName =
            toolbarButtonArgsMaker.currentScriptFileName
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
                currentScriptFileName
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
                toolbarButtonArgsMaker,
                jsActionMap
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
                toolbarButtonArgsMaker,
                jsActionMap,
            )
            MacroForToolbarButton.Macro.ADD_URL
            -> AddUrl.add(
                toolbarButtonArgsMaker,
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

    private object PopupSettingMenu {

        private var menuPopupWindow: PopupWindow? = null
        fun launchSettingMenu(
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
            jsActionMap: Map<String, String>
        ) {
            val editFragment = toolbarButtonArgsMaker.editFragment
            val context = editFragment.context

            if (
                context == null
            ) return
            val existEditExecuteTerminalFragment =
                ExistTerminalFragment.how(
                    editFragment,
                    context.getString(
                                R.string.edit_terminal_fragment
                            )
                )
            if (
                existEditExecuteTerminalFragment == null
            ) {
                Toast.makeText(
                    context,
                    "no working",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            createPopUpForSetting(
                toolbarButtonArgsMaker,
                jsActionMap,
            )
        }

        private fun createPopUpForSetting(
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
            jsActionMap: Map<String, String>
        ) {
            val settingButtonView = toolbarButtonArgsMaker.settingButtonView
            if (settingButtonView == null) return
            val context = settingButtonView.context
            val menuPairList =
                toolbarButtonArgsMaker.makeSettingButtonMenuPairList(jsActionMap)
            val menuListMap = MenuSettingTool.createListMenuListMap(
                menuPairList
            )
            if (menuListMap.size == 1) {
                val updateJsActionMap = EditSettingJsTool.makeJsActionMap(
                    toolbarButtonArgsMaker.editFragment,
                    MenuSettingTool.convertMenuPairListToJsKeyToSubCon(
                        menuPairList.first()
                    )
                )
                handle(
                    toolbarButtonArgsMaker,
                    updateJsActionMap,
                )
                return
            }
            menuPopupWindow = PopupWindow(
                context,
            ).apply {
                elevation = 5f
                isFocusable = true
                isOutsideTouchable = true
                setBackgroundDrawable(null)
                animationStyle = R.style.popup_window_animation_phone
                val inflater = LayoutInflater.from(context)
                contentView = inflater.inflate(
                    R.layout.setting_popup_for_index,
                    LinearLayoutCompat(context),
                    false
                ).apply {
                    val menuListView =
                        this.findViewById<NoScrollListView>(
                            R.id.setting_menu_list_view
                        )
                    val menuListAdapter = SubMenuAdapter(
                        context,
                        menuListMap.toMutableList()
                    )
                    menuListView.adapter = menuListAdapter
                    menuListViewSetOnItemClickListener(
                        toolbarButtonArgsMaker,
                        menuListView,
                        jsActionMap,
                    )
                    footerSettingHandler(
                        toolbarButtonArgsMaker.editFragment,
                        this,
                        jsActionMap,
                    )
                    measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    )
                }
            }.also { popupWindow ->
                // Absolute location of the anchor view
                val location = IntArray(2).apply {
                    settingButtonView.getLocationOnScreen(this)
                }
                val size = Size(
                    popupWindow.contentView.measuredWidth,
                    popupWindow.contentView.measuredHeight
                )
                popupWindow.showAtLocation(
                    settingButtonView,
                    Gravity.TOP or Gravity.START,
                    location[0] - (size.width - settingButtonView.width) / 2,
                    location[1] - size.height
                )
            }
        }

        private fun howFooterVisible(
            jsActionMap: Map<String, String>
        ): Boolean {
            val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
                jsActionMap
            ) ?: emptyMap()
            return !argsMap.containsKey(
                MacroForToolbarButton.MenuMacroArgsKey.ON_HIDE_FOOTER.key
            )
        }

        private fun menuListViewSetOnItemClickListener(
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
            menuListView: NoScrollListView,
            jsActionMap: Map<String, String>,
        ) {
            menuListView.setOnItemClickListener { parent, View, pos, id ->
                menuPopupWindow?.dismiss()
                val menuListAdapter = menuListView.adapter as SubMenuAdapter
                val clickedMenuName = menuListAdapter.getItem(pos)
                    ?: return@setOnItemClickListener
                jsPathOrSubMenuHandler(
                    toolbarButtonArgsMaker,
                    jsActionMap,
                    clickedMenuName,
                )
            }
        }

        private fun jsPathOrSubMenuHandler(
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
            jsActionMap: Map<String, String>?,
            clickedMenuName: String,

            ) {
            val settingButtonMenuPairList =
                toolbarButtonArgsMaker.makeSettingButtonMenuPairList(jsActionMap)
            val hitMenuItemPairList = MenuSettingTool.firstOrNullByParentMenuName(
                settingButtonMenuPairList,
                clickedMenuName,
            )
            val onSubMenuLabel = !hitMenuItemPairList.isNullOrEmpty()
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "jsHowHitMenu.txt").absolutePath,
//                listOf(
//                    "clickedMenuName: ${clickedMenuName}",
//                    "settingButtonMenuPairList: ${settingButtonMenuPairList}",
//                    "hitMenuItemPairList: ${hitMenuItemPairList}",
//                    "hitMenuitemPairLsitByMenuName: ${
//                        MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
//                        settingButtonMenuPairList,
//                        clickedMenuName
//                    )}",
//                    "jsActionMapSrc: ${jsActionMap}",
//                ).joinToString("\n\n")
//            )
            when (onSubMenuLabel) {
                true ->
                    SettingButtonSubMenuDialog.launch(
                        toolbarButtonArgsMaker,
                        jsActionMap,
                        clickedMenuName,
                    )

                else -> {
                    val updateJsActionMap = EditSettingJsTool.makeJsActionMap(
                        toolbarButtonArgsMaker.editFragment,
                        MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
                            settingButtonMenuPairList,
                            clickedMenuName
                        )
                    )
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "jsNoHitMenu.txt").absolutePath,
//                        listOf(
//                            "clickedMenuName: ${clickedMenuName}",
//                            "settingButtonMenuPairList: ${settingButtonMenuPairList}",
//                            "hitMenuItemPairList: ${hitMenuItemPairList}",
//                            "hitMenuitemPairLsitByMenuName: ${
//                                MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
//                                settingButtonMenuPairList,
//                                clickedMenuName
//                            )}",
//                            "jsActionMapSrc: ${jsActionMap}",
//                            "updateJsActionMap: ${updateJsActionMap}",
//                        ).joinToString("\n\n")
//                    )
                    handle(
                        toolbarButtonArgsMaker,
                        updateJsActionMap
                    )
                }

            }
        }

        private fun footerSettingHandler(
            editFragment: EditFragment,
            settingButtonInnerView: View,
            jsActionMap: Map<String, String>,
        ) {
            val isFooterVisible = howFooterVisible(
                jsActionMap,
            )
            when (isFooterVisible) {
                false -> settingButtonInnerView.findViewById<LinearLayoutCompat>(
                    R.id.setting_menu_nav_footer
                )?.isVisible = false

                else -> setNaviBarForEdit(
                    editFragment,
                    settingButtonInnerView
                )
            }
        }

        private fun setNaviBarForEdit(
            editFragment: EditFragment,
            settingButtonInnerView: View
        ) {
            execSetNavImageButtonForEdit(
                editFragment,
                settingButtonInnerView,
                R.id.setting_menu_nav_back_iamge_view,
                ToolbarMenuCategoriesVariantForCmdIndex.BACK,
                EnableNavForWebView.checkForGoBack(editFragment)
            )
            execSetNavImageButtonForEdit(
                editFragment,
                settingButtonInnerView,
                R.id.setting_menu_nav_reload_iamge_view,
                ToolbarMenuCategoriesVariantForCmdIndex.RELOAD,
                EnableNavForWebView.checkForReload(editFragment),
            )
            execSetNavImageButtonForEdit(
                editFragment,
                settingButtonInnerView,
                R.id.setting_menu_nav_forward_iamge_view,
                ToolbarMenuCategoriesVariantForCmdIndex.FORWARD,
                EnableNavForWebView.checkForGoForward(editFragment)
            )
        }

        private fun execSetNavImageButtonForEdit(
            editFragment: EditFragment,
            settingButtonInnerView: View,
            buttonId: Int,
            toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
            buttonEnable: Boolean
        ) {
            val context = editFragment.context
                ?: return
            val navImageButton =
                settingButtonInnerView.findViewById<AppCompatImageButton>(
                    buttonId
                )
            navImageButton.setOnClickListener {
                menuPopupWindow?.dismiss()
                val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                listener?.onToolbarMenuCategoriesForEdit(
                    toolbarMenuCategoriesVariantForCmdIndex,
                    EditFragmentArgs(
                        editFragment.readSharePreferenceMap,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                    ),
                )
            }
            navImageButton.isEnabled = buttonEnable
            val colorId = if (buttonEnable) R.color.cmdclick_text_black else R.color.gray_out
            navImageButton.imageTintList = context.getColorStateList(colorId)
        }
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