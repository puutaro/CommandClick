package com.puutaro.commandclick.proccess.setting_button.libs

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
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.custom_view.NoScrollListView
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.ScriptFileEdit
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.proccess.AppProcessManager
import com.puutaro.commandclick.proccess.EnableNavForWebView
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.proccess.ExistTerminalFragment
import com.puutaro.commandclick.proccess.NoScrollUrlSaver
import com.puutaro.commandclick.proccess.SelectTermDialog
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithIndexListView
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.proccess.setting_button.JsPathMacroForSettingButton
import com.puutaro.commandclick.proccess.setting_button.SettingButtonClickConfigMapKey
import com.puutaro.commandclick.proccess.setting_button.SettingButtonMenuMapKey
import com.puutaro.commandclick.proccess.setting_button.SystemFannelLauncher
import com.puutaro.commandclick.service.GitCloneService
import com.puutaro.commandclick.util.Map.CmdClickMap
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.dialog.UsageDialog
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object JsPathHandler {

    fun handle(
        settingButtonArgsMaker: SettingButtonArgsMaker,
        menuName: String,
        settingButtonConfigMapList: List<Map<String, String>?>? = null,
    ){
        val fragment = settingButtonArgsMaker.fragment
        val context = fragment.context
        val settingButtonMenuMapList = settingButtonArgsMaker.makeSettingButtonMenuMapList()
        val menuNameKey = SettingButtonMenuMapKey.NAME.str
        val jsPathKey = SettingButtonMenuMapKey.JS_PATH.str
        val currentSettingMenuMap = when(settingButtonConfigMapList.isNullOrEmpty()) {
            false -> (settingButtonMenuMapList + settingButtonConfigMapList).filter {
                it?.get(menuNameKey) == menuName
            }.firstOrNull()
            else -> settingButtonMenuMapList.filter {
                it?.get(menuNameKey) == menuName
            }.firstOrNull()
        }
        if(
            currentSettingMenuMap.isNullOrEmpty()
        ){
            LogSystems.stdWarn("${jsPathKey} key not found in settingMenuMapList: ${settingButtonMenuMapList}")
            return
        }
        val jsPath = currentSettingMenuMap.get(jsPathKey)
        if(
            jsPath.isNullOrEmpty()
        ){
            LogSystems.stdWarn("${jsPathKey} not found in settingMenuMapList: ${settingButtonMenuMapList}")
            return
        }
        val filterdJsPath = JsPathMacroForSettingButton.values().filter {
            it.name == jsPath
        }.firstOrNull()
        when(filterdJsPath != null){
            true -> jsPathMacroHandler(
                settingButtonArgsMaker,
                filterdJsPath,
            )
            else -> ExecJsLoad.jsUrlLaunchHandler(
                    fragment,
                    JavaScriptLoadUrl.make(
                        context,
                        jsPath,
                        setReplaceVariableMapSrc = settingButtonArgsMaker.setReplaceVariableMap
                    ) ?: String()
                )
        }
    }

    private fun jsPathMacroHandler(
        settingButtonArgsMaker: SettingButtonArgsMaker,
        jsPathMacroForSettingButton: JsPathMacroForSettingButton,
    ){
        val fragment = settingButtonArgsMaker.fragment
        val readSharePreffernceMap = settingButtonArgsMaker.readSharePreffernceMap
        val settingButtonMenuMapList = settingButtonArgsMaker.makeSettingButtonMenuMapList()
        val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentScriptFileName = settingButtonArgsMaker.currentScriptFileName
        when(jsPathMacroForSettingButton){
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
                val listener = fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                listener?.onToolbarMenuCategories(
                    ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT,
                    EditFragmentArgs(
                        readSharePreffernceMap,
                        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                    )
                )
            }
            JsPathMacroForSettingButton.TERMUX_SETUP -> {
                val listener = fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
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
                AppDirAdder.add(settingButtonArgsMaker)
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
                PopupSettingMenu.launchSettingMenu(settingButtonArgsMaker)
            JsPathMacroForSettingButton.SYNC -> ListSyncer.sync(
                fragment,
                settingButtonMenuMapList,
            )
            JsPathMacroForSettingButton.GET_FILE -> settingButtonArgsMaker.fileGetterForSettingButton.get(
                settingButtonMenuMapList,
                currentAppDirPath,
            )
            JsPathMacroForSettingButton.GET_QR_CON -> QrConGetterDialog.launch(
               settingButtonArgsMaker
            )
            JsPathMacroForSettingButton.FANNEL_REPO_SYNC ->
                syncFannelRepo(fragment)
        }
    }
}

private object PopupSettingMenu{

    private var menuPopupWindow: PopupWindow? = null
    fun launchSettingMenu(
        settingButtonArgsMaker: SettingButtonArgsMaker
    ){
        val fragment = settingButtonArgsMaker.fragment
        val context = fragment.context

        if(
            context == null
        ) return
        val existEditExecuteTerminalFragment = when(fragment) {
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
        if(
            existEditExecuteTerminalFragment == null
        ){
            Toast.makeText(
                context,
                "no working",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        createPopUpForSetting(
            settingButtonArgsMaker
        )
    }
    private fun createPopUpForSetting(
        settingButtonArgsMaker: SettingButtonArgsMaker
    ) {
        val settingButtonView = settingButtonArgsMaker.settingButtonView
        if (settingButtonView == null) return
        val context = settingButtonView.context
        val menuListMap = createPopupMenuListMap(settingButtonArgsMaker)
        if(menuListMap.size == 1){
            jsPathOrSubMenuHandler(
                menuListMap.first().first,
                settingButtonArgsMaker
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
                    settingButtonArgsMaker,
                )
                footerSettingHandler(
                    settingButtonArgsMaker,
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
        settingButtonArgsMaker: SettingButtonArgsMaker,
    ): Boolean {
        val clickKey = settingButtonArgsMaker.decideClickKey()
        return settingButtonArgsMaker.settingButtonConfigMap?.get(clickKey).let {
                clickJsPathMapStr ->
            if(
                clickJsPathMapStr.isNullOrEmpty()
            ) return@let true
            val clickConfigMapStr = CmdClickMap.createMap(clickJsPathMapStr, "|").toMap()
            !clickConfigMapStr.containsKey(SettingButtonClickConfigMapKey.ON_HIDE_FOOTER.str)
        }
    }

    private fun createPopupMenuListMap(
        settingButtonArgsMaker: SettingButtonArgsMaker,
    ): List<Pair<String, Int>> {
        val settingButtonMenuMapList = settingButtonArgsMaker.makeSettingButtonMenuMapList()
        val parentMenuKey = SettingButtonMenuMapKey.PARENT_NAME.str
        return settingButtonMenuMapList.filter {
            it?.get(parentMenuKey).isNullOrEmpty()
        }.let {
            execCreateMenuListMap(
                it
            )
        }
    }


    private fun menuListViewSetOnItemClickListener(
        menuListView: NoScrollListView,
        settingButtonArgsMaker: SettingButtonArgsMaker,
    ) {
        menuListView.setOnItemClickListener { parent, View, pos, id ->
            menuPopupWindow?.dismiss()
            val menuListAdapter = menuListView.adapter as SubMenuAdapter
            val clickedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            jsPathOrSubMenuHandler(
                clickedMenuName,
                settingButtonArgsMaker,
            )
        }
    }

    private fun jsPathOrSubMenuHandler(
        clickedMenuName: String,
        settingButtonArgsMaker: SettingButtonArgsMaker

    ){
        val settingButtonMenuMapList = settingButtonArgsMaker.makeSettingButtonMenuMapList()
        val parentMenuNameKey = SettingButtonMenuMapKey.PARENT_NAME.str
        val onSubMenuLabel = !settingButtonMenuMapList.filter {
            it?.get(parentMenuNameKey) == clickedMenuName
        }.firstOrNull().isNullOrEmpty()
        when (onSubMenuLabel) {
            true ->
                SettingButtonSubMenuDialog.launch(
                    settingButtonArgsMaker,
                    clickedMenuName,

                    )

            else ->
                JsPathHandler.handle(
                    settingButtonArgsMaker,
                    clickedMenuName,
                )

        }
    }

    private fun footerSettingHandler(
        settingButtonArgsMaker: SettingButtonArgsMaker,
        settingButtonInnerView: View
    ){
        val isFooterVisible = howFooterVisible(
            settingButtonArgsMaker,
        )
        when(isFooterVisible){
            false -> settingButtonInnerView.findViewById<LinearLayoutCompat>(
                R.id.setting_menu_nav_footer
            )?.isVisible = false
            else -> setNaviBarForEdit(
                settingButtonArgsMaker,
                settingButtonInnerView
            )
        }
    }

    private fun setNaviBarForEdit(
        settingButtonArgsMaker: SettingButtonArgsMaker,
        settingButtonInnerView: View
    ){
        val fragment = settingButtonArgsMaker.fragment
        execSetNavImageButtonForEdit(
            settingButtonArgsMaker,
            settingButtonInnerView,
            R.id.setting_menu_nav_back_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.BACK,
            EnableNavForWebView.checkForGoBack(fragment)
        )
        execSetNavImageButtonForEdit(
            settingButtonArgsMaker,
            settingButtonInnerView,
            R.id.setting_menu_nav_reload_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.RELOAD,
            EnableNavForWebView.checkForReload(fragment),
        )
        execSetNavImageButtonForEdit(
            settingButtonArgsMaker,
            settingButtonInnerView,
            R.id.setting_menu_nav_forward_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.FORWARD,
            EnableNavForWebView.checkForGoForward(fragment)
        )
    }
    private fun execSetNavImageButtonForEdit (
        settingButtonArgsMaker: SettingButtonArgsMaker,
        settingButtonInnerView: View,
        buttonId: Int,
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
        buttonEnable: Boolean
    ){
        val fragment = settingButtonArgsMaker.fragment
        val context = fragment.context
            ?: return
        val navImageButton =
            settingButtonInnerView.findViewById<AppCompatImageButton>(
                buttonId
            )
        navImageButton.setOnClickListener {
            menuPopupWindow?.dismiss()
            when(fragment){
                is CommandIndexFragment -> {
                    val listener = context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        toolbarMenuCategoriesVariantForCmdIndex,
                        EditFragmentArgs(
                            fragment.readSharePreffernceMap,
                            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                        )
                    )
                }
                is EditFragment -> {
                    val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                    listener?.onToolbarMenuCategoriesForEdit(
                        toolbarMenuCategoriesVariantForCmdIndex,
                        EditFragmentArgs(
                            fragment.readSharePreffernceMap,
                            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                        ),
                    )
                }
            }
        }
        navImageButton.isEnabled = buttonEnable
        val colorId = if(buttonEnable) R.color.cmdclick_text_black else R.color.gray_out
        navImageButton.imageTintList = context.getColorStateList(colorId)
    }
}

private fun monitorSizeChange(
    fragment: Fragment
){
    when(fragment){
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
){
    if(
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
    if(
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
            editFragment.readSharePreffernceMap,
            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
        )
    )
}

private fun execCreateMenuListMap(
    srcMenuMapList: List<Map<String, String>?>
): List<Pair<String, Int>>{
    val menuNameKey = SettingButtonMenuMapKey.NAME.str
    val iconKey = SettingButtonMenuMapKey.ICON.str
    val ringIconId = CmdClickIcons.RING.id
    return srcMenuMapList.map {
        val iconMacroName = it?.get(iconKey)
        val menuName = it?.get(menuNameKey) ?: String()
        val iconId = CmdClickIcons.values().filter {
            it.str == iconMacroName
        }.firstOrNull()?.id ?: ringIconId
        menuName to iconId
    }.filter { it.first.isNotEmpty() }
}

private fun execQrScan(
    fragment: Fragment,
    currentAppDirPath: String,
){
    val activeCurrentDirPath = when(fragment){
        is EditFragment -> {
            val filterDirInWithListIndex = WithIndexListView.filterDir
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
){
    when(fragment){
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
){
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

private fun syncFannelRepo(fragment: Fragment){
    val context = fragment.context
        ?: return
    val intent = Intent(
        context,
        GitCloneService::class.java
    )
    context.startForegroundService(intent)
}
