package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.custom_view.NoScrollListView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.EnableNavForWebView
import com.puutaro.commandclick.proccess.ExistTerminalFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.util.state.EditFragmentArgs

object PopupSettingMenu {

    private var menuPopupWindow: PopupWindow? = null
    fun launchSettingMenu(
        editFragment: EditFragment,
        settingButtonView: View?,
        jsActionMap: Map<String, String>
    ) {
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
            editFragment,
            settingButtonView,
            jsActionMap,
        )
    }

    private fun createPopUpForSetting(
        editFragment: EditFragment,
        settingButtonView: View?,
        jsActionMap: Map<String, String>,
    ) {
//        val settingButtonView = toolbarButtonArgsMaker.settingButtonView
        if (settingButtonView == null) return
        val context = settingButtonView.context
        val menuPairList =
            ToolbarButtonArgsMaker.makeSettingButtonMenuPairList(
                editFragment,
                jsActionMap
            )
        val menuListMap = MenuSettingTool.createListMenuListMap(
            menuPairList
        )
        if (menuListMap.size == 1) {
            val updateJsActionMap = JsActionTool.makeJsActionMap(
                editFragment,
                editFragment.readSharePreferenceMap,
                MenuSettingTool.convertMenuPairListToJsKeyToSubCon(
                    menuPairList.first()
                ),
                editFragment.setReplaceVariableMap,
            )
            JsPathHandlerForToolbarButton.handle(
                editFragment,
                String(),
                settingButtonView,
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
                    editFragment,
                    settingButtonView,
                    menuListView,
                    jsActionMap,
                )
                footerSettingHandler(
                    editFragment,
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
        editFragment: EditFragment,
        settingButtonView: View?,
        menuListView: NoScrollListView,
        jsActionMap: Map<String, String>,
    ) {
        menuListView.setOnItemClickListener { parent, View, pos, id ->
            menuPopupWindow?.dismiss()
            val menuListAdapter = menuListView.adapter as SubMenuAdapter
            val clickedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            jsPathOrSubMenuHandler(
                editFragment,
                settingButtonView,
                jsActionMap,
                clickedMenuName,
            )
        }
    }

    private fun jsPathOrSubMenuHandler(
        editFragment: EditFragment,
        settingButtonView: View?,
        jsActionMap: Map<String, String>?,
        clickedMenuName: String,

        ) {
        val settingButtonMenuPairList =
            ToolbarButtonArgsMaker.makeSettingButtonMenuPairList(
                editFragment,
                jsActionMap
            )
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
                ToolbarButtonSubMenuDialog.launch(
                    editFragment,
                    settingButtonView,
                    jsActionMap,
                    clickedMenuName,
                )

            else -> {
                val updateJsActionMap = JsActionTool.makeJsActionMap(
                    editFragment,
                    editFragment.readSharePreferenceMap,
                    MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
                        settingButtonMenuPairList,
                        clickedMenuName
                    ),
                    editFragment.setReplaceVariableMap,
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
                JsPathHandlerForToolbarButton.handle(
                    editFragment,
                    String(),
                    settingButtonView,
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