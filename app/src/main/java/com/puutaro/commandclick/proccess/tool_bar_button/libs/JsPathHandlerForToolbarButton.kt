package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.content.Intent
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.custom_view.NoScrollListView
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.ScriptFileEdit
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.proccess.AppProcessManager
import com.puutaro.commandclick.proccess.EnableNavForWebView
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.proccess.ExistTerminalFragment
import com.puutaro.commandclick.proccess.NoScrollUrlSaver
import com.puutaro.commandclick.proccess.SelectTermDialog
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.proccess.edit.lib.ListContentsSelectBoxTool
import com.puutaro.commandclick.proccess.edit.lib.SaveTagForListContents
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher
import com.puutaro.commandclick.proccess.tool_bar_button.common_settings.JsPathMacroForSettingButton
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ClickSettingsForToolbarButton
import com.puutaro.commandclick.service.GitCloneService
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.dialog.UsageDialog
import com.puutaro.commandclick.util.file.FDialogTempFile
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object JsPathHandlerForToolbarButton {

    fun handle(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        menuName: String,
        settingButtonConfigMapList: List<Map<String, String>?>? = null,
    ) {
        val fragment = toolbarButtonArgsMaker.fragment
        val context = fragment.context
        val settingButtonMenuMapList = toolbarButtonArgsMaker.makeSettingButtonMenuMapList()
        val menuNameKey = MenuSettingTool.MenuSettingKey.NAME.key
        val jsPathKey = MenuSettingTool.MenuSettingKey.JS_PATH.key
        val currentSettingMenuMap = when (settingButtonConfigMapList.isNullOrEmpty()) {
            false -> (settingButtonMenuMapList + settingButtonConfigMapList).firstOrNull {
                it?.get(menuNameKey) == menuName
            }

            else -> settingButtonMenuMapList.firstOrNull {
                it?.get(menuNameKey) == menuName
            }
        }
        if (
            currentSettingMenuMap.isNullOrEmpty()
        ) {
            LogSystems.stdWarn("${jsPathKey} key not found in settingMenuMapList: ${settingButtonMenuMapList}")
            return
        }
        val jsPath = currentSettingMenuMap.get(jsPathKey)
        if (
            jsPath.isNullOrEmpty()
        ) {
            LogSystems.stdWarn("${jsPathKey} not found in settingMenuMapList: ${settingButtonMenuMapList}")
            return
        }
        val filterdJsPath = JsPathMacroForSettingButton.values().filter {
            it.name == jsPath
        }.firstOrNull()
        when (filterdJsPath != null) {
            true -> jsPathMacroHandler(
                toolbarButtonArgsMaker,
                filterdJsPath,
            )

            else -> execJs(
                toolbarButtonArgsMaker,
                jsPath,
            )
        }
    }

    private fun execJs(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        jsPath: String,
    ){
        if(
            jsPath.isEmpty()
            || !File(jsPath).isFile
        ) return
        val fragment = toolbarButtonArgsMaker.fragment
        val context = fragment.context
        ExecJsLoad.jsUrlLaunchHandler(
            fragment,
            JavaScriptLoadUrl.make(
                context,
                jsPath,
                setReplaceVariableMapSrc = toolbarButtonArgsMaker.setReplaceVariableMap
            ) ?: String()
        )
    }

    private fun jsPathMacroHandler(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        jsPathMacroForSettingButton: JsPathMacroForSettingButton,
    ) {
        val fragment = toolbarButtonArgsMaker.fragment
        val readSharePreffernceMap = toolbarButtonArgsMaker.readSharePreffernceMap
        val settingButtonMenuMapList = toolbarButtonArgsMaker.makeSettingButtonMenuMapList()
        val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptFileName = toolbarButtonArgsMaker.currentScriptFileName
        when (jsPathMacroForSettingButton) {
            JsPathMacroForSettingButton.KILL ->
                AppProcessManager.killDialog(
                    fragment,
                    currentAppDirPath,
                    currentScriptFileName
                )

            JsPathMacroForSettingButton.USAGE ->
                UsageDialog.launch(
                    fragment,
                    currentAppDirPath,
                )

            JsPathMacroForSettingButton.NO_SCROLL_SAVE_URL ->
                NoScrollUrlSaver.save(
                    fragment,
                    currentAppDirPath,
                    String()
                )

            JsPathMacroForSettingButton.QR_SCAN ->
                execQrScan(
                    fragment,
                    currentAppDirPath,
                )

            JsPathMacroForSettingButton.SHORTCUT -> {
                val listener =
                    fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                listener?.onToolbarMenuCategories(
                    ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT,
                    EditFragmentArgs(
                        readSharePreffernceMap,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                    )
                )
            }

            JsPathMacroForSettingButton.TERMUX_SETUP -> {
                val listener =
                    fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                listener?.onToolbarMenuCategories(
                    ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP,
                    EditFragmentArgs(
                        readSharePreffernceMap,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                    )
                )
            }

            JsPathMacroForSettingButton.CONFIG ->
                configEdit(fragment)

            JsPathMacroForSettingButton.REFRESH_MONITOR ->
                TermRefresh.refresh(
                    terminalViewModel.currentMonitorFileName
                )

            JsPathMacroForSettingButton.SELECT_MONITOR ->
                SelectTermDialog.launch(fragment)

            JsPathMacroForSettingButton.RESTART_UBUNTU ->
                UbuntuServiceManager.launch(
                    fragment.activity
                )

            JsPathMacroForSettingButton.INSTALL_FANNEL ->
                SystemFannelLauncher.launch(
                    fragment,
                    UsePath.cmdclickSystemAppDirPath,
                    UsePath.fannelRepoFannelName
                )

            JsPathMacroForSettingButton.EDIT_STARTUP ->
                scriptFileEditForCmdIndex(
                    fragment,
                    currentAppDirPath,
                )

            JsPathMacroForSettingButton.ADD -> AddFileForEdit.add(
                fragment,
                currentAppDirPath,
                settingButtonMenuMapList,
            )

            JsPathMacroForSettingButton.ADD_APP_DIR ->
                AppDirAdder.add(toolbarButtonArgsMaker)

            JsPathMacroForSettingButton.JS_IMPORT -> SystemFannelLauncher.launch(
                fragment,
                UsePath.cmdclickSystemAppDirPath,
                UsePath.jsImportManagerFannelName
            )

            JsPathMacroForSettingButton.APP_DIR_MANAGER -> SystemFannelLauncher.launch(
                fragment,
                UsePath.cmdclickSystemAppDirPath,
                UsePath.appDirManagerFannelName
            )

            JsPathMacroForSettingButton.SIZING -> monitorSizeChange(fragment)
            JsPathMacroForSettingButton.MENU ->
                PopupSettingMenu.launchSettingMenu(toolbarButtonArgsMaker)

            JsPathMacroForSettingButton.SYNC -> ListSyncer.sync(
                fragment,
                settingButtonMenuMapList,
            )

            JsPathMacroForSettingButton.GET_FILE -> getFileHandler(
                toolbarButtonArgsMaker,
            )
            JsPathMacroForSettingButton.GET_QR_CON -> QrConGetterDialog.launch(
                toolbarButtonArgsMaker
            )

            JsPathMacroForSettingButton.FANNEL_REPO_SYNC ->
                syncFannelRepo(fragment)

            JsPathMacroForSettingButton.EDIT ->
                changeSettingFragment(fragment)

            JsPathMacroForSettingButton.WEB_SEARCH ->
                EditToolbarSwitcher.switch(
                    fragment,
                    JsPathMacroForSettingButton.WEB_SEARCH.name
                )

            JsPathMacroForSettingButton.PAGE_SEARCH ->
                EditToolbarSwitcher.switch(
                    fragment,
                    JsPathMacroForSettingButton.PAGE_SEARCH.name
                )

            JsPathMacroForSettingButton.NORMAL ->
                EditToolbarSwitcher.switch(
                    fragment,
                    JsPathMacroForSettingButton.NORMAL.name
                )

            JsPathMacroForSettingButton.OK -> {
                if (fragment !is EditFragment) return
                OkHandler(
                    fragment,
                    toolbarButtonArgsMaker.recordNumToMapNameValueInCommandHolder,
                    toolbarButtonArgsMaker.recordNumToMapNameValueInSettingHolder,
                ).execForOk()
            }
        }
    }

    private fun getFileHandler(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
    ) {
        val fragment = toolbarButtonArgsMaker.fragment
        val settingButtonMenuMapList = toolbarButtonArgsMaker.makeSettingButtonMenuMapList()
        val parentDirPath = when (fragment) {
            is EditFragment -> {
                if (fragment.existIndexList) ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                    fragment,
                    ListIndexForEditAdapter.indexListMap,
                    ListIndexForEditAdapter.listIndexTypeKey
                )
                else toolbarButtonArgsMaker.currentAppDirPath
            }

            else -> toolbarButtonArgsMaker.currentAppDirPath
        }
        toolbarButtonArgsMaker.fileGetterForSettingButton?.get(
            settingButtonMenuMapList,
            parentDirPath
        )
    }

    private object PopupSettingMenu {

        private var menuPopupWindow: PopupWindow? = null
        fun launchSettingMenu(
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker
        ) {
            val fragment = toolbarButtonArgsMaker.fragment
            val context = fragment.context

            if (
                context == null
            ) return
            val existEditExecuteTerminalFragment = when (fragment) {
                is CommandIndexFragment ->
                    ExistTerminalFragment
                        .how(
                            fragment,
                            context.getString(
                                R.string.index_terminal_fragment
                            )
                        )

                is EditFragment ->
                    ExistTerminalFragment
                        .how(
                            fragment,
                            context.getString(
                                R.string.edit_terminal_fragment
                            )
                        )

                else -> null
            }
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
                toolbarButtonArgsMaker
            )
        }

        private fun createPopUpForSetting(
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker
        ) {
            val settingButtonView = toolbarButtonArgsMaker.settingButtonView
            if (settingButtonView == null) return
            val context = settingButtonView.context
            val menuListMap = MenuSettingTool.createListMenuListMap(
                toolbarButtonArgsMaker.makeSettingButtonMenuMapList()
            )
            if (menuListMap.size == 1) {
                jsPathOrSubMenuHandler(
                    menuListMap.first().first,
                    toolbarButtonArgsMaker
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
                        menuListView,
                        toolbarButtonArgsMaker,
                    )
                    footerSettingHandler(
                        toolbarButtonArgsMaker,
                        this
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
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        ): Boolean {
            val clickKey = toolbarButtonArgsMaker.decideClickKey()
            return toolbarButtonArgsMaker.settingButtonConfigMap?.get(clickKey)
                .let { clickJsPathMapStr ->
                    if (
                        clickJsPathMapStr.isNullOrEmpty()
                    ) return@let true
                    val clickConfigMapStr = CmdClickMap.createMap(clickJsPathMapStr, "|").toMap()
                    !clickConfigMapStr.containsKey(ClickSettingsForToolbarButton.ClickConfigMapKey.ON_HIDE_FOOTER.str)
                }
        }

        private fun menuListViewSetOnItemClickListener(
            menuListView: NoScrollListView,
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        ) {
            menuListView.setOnItemClickListener { parent, View, pos, id ->
                menuPopupWindow?.dismiss()
                val menuListAdapter = menuListView.adapter as SubMenuAdapter
                val clickedMenuName = menuListAdapter.getItem(pos)
                    ?: return@setOnItemClickListener
                jsPathOrSubMenuHandler(
                    clickedMenuName,
                    toolbarButtonArgsMaker,
                )
            }
        }

        private fun jsPathOrSubMenuHandler(
            clickedMenuName: String,
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker

        ) {
            val settingButtonMenuMapList = toolbarButtonArgsMaker.makeSettingButtonMenuMapList()
            val parentMenuNameKey = MenuSettingTool.MenuSettingKey.PARENT_NAME.key
            val onSubMenuLabel = !settingButtonMenuMapList.filter {
                it?.get(parentMenuNameKey) == clickedMenuName
            }.firstOrNull().isNullOrEmpty()
            when (onSubMenuLabel) {
                true ->
                    SettingButtonSubMenuDialog.launch(
                        toolbarButtonArgsMaker,
                        clickedMenuName,

                        )

                else ->
                    handle(
                        toolbarButtonArgsMaker,
                        clickedMenuName,
                    )

            }
        }

        private fun footerSettingHandler(
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
            settingButtonInnerView: View
        ) {
            val isFooterVisible = howFooterVisible(
                toolbarButtonArgsMaker,
            )
            when (isFooterVisible) {
                false -> settingButtonInnerView.findViewById<LinearLayoutCompat>(
                    R.id.setting_menu_nav_footer
                )?.isVisible = false

                else -> setNaviBarForEdit(
                    toolbarButtonArgsMaker,
                    settingButtonInnerView
                )
            }
        }

        private fun setNaviBarForEdit(
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
            settingButtonInnerView: View
        ) {
            val fragment = toolbarButtonArgsMaker.fragment
            execSetNavImageButtonForEdit(
                toolbarButtonArgsMaker,
                settingButtonInnerView,
                R.id.setting_menu_nav_back_iamge_view,
                ToolbarMenuCategoriesVariantForCmdIndex.BACK,
                EnableNavForWebView.checkForGoBack(fragment)
            )
            execSetNavImageButtonForEdit(
                toolbarButtonArgsMaker,
                settingButtonInnerView,
                R.id.setting_menu_nav_reload_iamge_view,
                ToolbarMenuCategoriesVariantForCmdIndex.RELOAD,
                EnableNavForWebView.checkForReload(fragment),
            )
            execSetNavImageButtonForEdit(
                toolbarButtonArgsMaker,
                settingButtonInnerView,
                R.id.setting_menu_nav_forward_iamge_view,
                ToolbarMenuCategoriesVariantForCmdIndex.FORWARD,
                EnableNavForWebView.checkForGoForward(fragment)
            )
        }

        private fun execSetNavImageButtonForEdit(
            toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
            settingButtonInnerView: View,
            buttonId: Int,
            toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
            buttonEnable: Boolean
        ) {
            val fragment = toolbarButtonArgsMaker.fragment
            val context = fragment.context
                ?: return
            val navImageButton =
                settingButtonInnerView.findViewById<AppCompatImageButton>(
                    buttonId
                )
            navImageButton.setOnClickListener {
                menuPopupWindow?.dismiss()
                when (fragment) {
                    is CommandIndexFragment -> {
                        val listener =
                            context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                        listener?.onToolbarMenuCategories(
                            toolbarMenuCategoriesVariantForCmdIndex,
                            EditFragmentArgs(
                                fragment.readSharePreffernceMap,
                                EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                            )
                        )
                    }

                    is EditFragment -> {
                        val listener =
                            context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                        listener?.onToolbarMenuCategoriesForEdit(
                            toolbarMenuCategoriesVariantForCmdIndex,
                            EditFragmentArgs(
                                fragment.readSharePreferenceMap,
                                EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                            ),
                        )
                    }
                }
            }
            navImageButton.isEnabled = buttonEnable
            val colorId = if (buttonEnable) R.color.cmdclick_text_black else R.color.gray_out
            navImageButton.imageTintList = context.getColorStateList(colorId)
        }
    }

    private fun monitorSizeChange(
        fragment: Fragment
    ) {
        when (fragment) {
            is CommandIndexFragment ->
                ExecSetTermSizeForCmdIndexFragment.execSetTermSizeForCmdIndexFragment(
                    fragment,
                )

            is EditFragment ->
                monitorSizeChangeForEdit(fragment)
        }
    }


    private fun monitorSizeChangeForEdit(
        editFragment: EditFragment
    ) {
        if (
            editFragment.terminalOn
            == SettingVariableSelects.TerminalDoSelects.OFF.name
        ) return
        val context = editFragment.context
        val existEditExecuteTerminalFragment = ExistTerminalFragment
            .how(
                editFragment,
                editFragment.context?.getString(
                    R.string.edit_terminal_fragment
                )
            )
        if (
            existEditExecuteTerminalFragment?.isVisible != true
        ) {
            Toast.makeText(
                context,
                "no terminal",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val linearLayoutParam =
            editFragment.binding.editFragment.layoutParams as LinearLayout.LayoutParams
        val isShow = linearLayoutParam.weight != ReadLines.LONGTH
        EditLayoutViewHideShow.exec(
            editFragment,
            isShow
        )
        val listener =
            context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
        listener?.onToolbarMenuCategoriesForEdit(
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX,
            EditFragmentArgs(
                editFragment.readSharePreferenceMap,
                EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
            )
        )
    }

    private fun execQrScan(
        fragment: Fragment,
        currentAppDirPath: String,
    ) {
        val activeCurrentDirPath = when (fragment) {
            is EditFragment -> {
                val filterDirInWithListIndex = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
                    fragment,
                    ListIndexForEditAdapter.indexListMap,
                    ListIndexForEditAdapter.listIndexTypeKey
                )
                if (
                    fragment.existIndexList
                    && filterDirInWithListIndex.isNotEmpty()
                ) filterDirInWithListIndex
                else currentAppDirPath
            }

            else -> currentAppDirPath
        }
        QrScanner(
            fragment,
            activeCurrentDirPath,
        ).scanFromCamera()
    }

    private fun scriptFileEditForCmdIndex(
        fragment: Fragment,
        currentAppDirPath: String,
    ) {
        when (fragment) {
            is EditFragment -> {}
            is CommandIndexFragment
            -> ScriptFileEdit.edit(
                fragment,
                currentAppDirPath,
                UsePath.cmdclickStartupJsName,
            )
        }
    }

    private fun configEdit(
        fragment: Fragment
    ) {
        val configDirPath = UsePath.cmdclickSystemAppDirPath
        val configShellName = UsePath.cmdclickConfigFileName
        CommandClickScriptVariable.makeConfigJsFile(
            configDirPath,
            configShellName
        )
        SystemFannelLauncher.launch(
            fragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.cmdclickConfigFileName,
        )
    }

    private fun syncFannelRepo(fragment: Fragment) {
        val context = fragment.context
            ?: return
        val intent = Intent(
            context,
            GitCloneService::class.java
        )
        context.startForegroundService(intent)
    }


    private fun changeSettingFragment(fragment: Fragment) {
        if (fragment !is EditFragment) return
        val listener = fragment.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
        listener?.onToolBarButtonClickForEditFragment(
            fragment.tag,
            ToolbarButtonBariantForEdit.EDIT,
            fragment.readSharePreferenceMap,
            fragment.enableCmdEdit
        )
    }


    private class OkHandler(
        private val editFragment: EditFragment,
        private val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
        private val recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
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
            scriptFileSaver.save(
                recordNumToMapNameValueInCommandHolder,
                recordNumToMapNameValueInSettingHolder,
            )
            val isCmdEditExecute = enableCmdEdit
                    && editFragment.enableEditExecute
                    && !onPassCmdVariableEdit
            val isSettingEditByPass = enableCmdEdit
                    && editFragment.enableEditExecute
                    && onPassCmdVariableEdit
            val isSettingEdit = !enableCmdEdit

            val isFdialogFannel = FDialogTempFile.howFDialogFile(currentScriptFileName)
            val isOnlyCmdEditNoFdialog = enableCmdEdit
                    && !editFragment.enableEditExecute
                    && !isFdialogFannel
            val isOnlyCmdEditWithFdialog = enableCmdEdit
                    && !editFragment.enableEditExecute
                    && isFdialogFannel
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
                isOnlyCmdEditWithFdialog ->
                    fDialalogOkButtonProcess()

                isSettingEditByPass,
                isOnlyCmdEditNoFdialog,
                isSettingEdit -> {
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

        private fun fDialalogOkButtonProcess() {
            val srcReadSharePreffernceMap = editFragment.srcReadSharePreffernceMap
                ?: return
            val srcAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
                srcReadSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            )
            val srcFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
                srcReadSharePreffernceMap,
                SharePrefferenceSetting.current_fannel_name
            )
            val srcFannelCon = ReadText(
                srcAppDirPath,
                srcFannelName
            ).readText()
            val fDialogConList = ReadText(
                currentAppDirPath,
                currentScriptFileName
            ).textToList()
            val fDialogCommandValCon = CommandClickVariables.substituteVariableListFromHolder(
                fDialogConList,
                editFragment.commandSectionStart,
                editFragment.commandSectionEnd
            )?.filter {
                !it.startsWith(FDialogTempFile.jsDescPrefix)
                        && it.isNotEmpty()
            }?.joinToString("\t") ?: String()
            val replaceSrcFannelConInSettingVal = CommandClickVariables.replaceVariableInHolder(
                srcFannelCon,
                fDialogCommandValCon,
                editFragment.settingSectionStart,
                editFragment.settingSectionEnd,
            )
            val replaceSrcFanneCon = CommandClickVariables.replaceVariableInHolder(
                replaceSrcFannelConInSettingVal,
                fDialogCommandValCon,
                editFragment.commandSectionStart,
                editFragment.commandSectionEnd,
            )
            if (
                replaceSrcFanneCon != srcFannelCon
            ) {
                FileSystems.writeFile(
                    srcAppDirPath,
                    srcFannelName,
                    replaceSrcFanneCon
                )
            }
            copyDirectoryWithDeleteWithBackup()
            val listener =
                this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                String(),
                ToolbarButtonBariantForEdit.CANCEL,
                mapOf(),
                false
            )
        }

        private fun copyDirectoryWithDeleteWithBackup(){
            val srcReadSharePreffernceMap = editFragment.srcReadSharePreffernceMap
                ?: return
            val srcAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
                srcReadSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            )
            val srcFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
                srcReadSharePreffernceMap,
                SharePrefferenceSetting.current_fannel_name
            )
            val currentFannelDirName = CcPathTool.makeFannelDirName(currentScriptFileName)
            val currentFannelDirPath = File(currentAppDirPath, currentFannelDirName).absolutePath
            val srcFannelDirName = CcPathTool.makeFannelDirName(srcFannelName)
            val srcFannelDirPath = File(srcAppDirPath, srcFannelDirName).absolutePath
            CoroutineScope(Dispatchers.IO).launch {
                val buckupDirPath =
                    File(
                        currentAppDirPath,
                        UsePath.clickBackupDirNameInAppDir
                    ).absolutePath
                FileSystems.createDirs(buckupDirPath)
                val buckupTargetDirPath =
                    File(buckupDirPath, srcFannelDirName).absolutePath
                FileSystems.copyDirectory(
                    currentFannelDirPath,
                    buckupTargetDirPath
                )
            }
            FileSystems.removeDir(
                srcFannelDirPath
            )
            FileSystems.copyDirectory(
                currentFannelDirPath,
                srcFannelDirPath
            )
        }
    }
}