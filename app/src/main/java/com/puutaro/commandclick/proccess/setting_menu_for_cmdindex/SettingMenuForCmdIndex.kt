package com.puutaro.commandclick.proccess.setting_menu_for_cmdindex

import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.custom_view.NoScrollListView
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.CmdClickSystemFannelManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.EnableNavForWebView
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher
import com.puutaro.commandclick.util.state.EditFragmentArgs
import kotlinx.coroutines.runBlocking

object SettingMenuForCmdIndex {


    private var menuPopupWindow: PopupWindow? = null
    private val menuMapList = MenuEnumsForCmdIndex.values().toList().map{
        it.itemName to it.imageId
    }


    fun launch(
        fragment: Fragment
    ){
        val buttonLinearView = when(fragment){
             is CommandIndexFragment  -> fragment.binding.cmdindexSettingButton
             is TerminalFragment -> fragment.binding.termSettingButton
             else -> return
        }
        ToastUtils.showShort("aaa")
        toolbarSettingButtonOnClick(
            fragment,
            buttonLinearView
        )
    }


    private fun toolbarSettingButtonOnClick(
        fragment: Fragment,
        settingButtonView: LinearLayoutCompat
    ){
        settingButtonView.setOnClickListener {
                settingButtonInnerView ->
            val settingButtonViewContext = settingButtonInnerView.context
            menuPopupWindow = PopupWindow(
                settingButtonView.context,
            ).apply {
                elevation = 5f
                isFocusable = true
                isOutsideTouchable = true
                setBackgroundDrawable(null)
                animationStyle = R.style.popup_window_animation_phone
                val inflater = LayoutInflater.from(settingButtonView.context)
                contentView = inflater.inflate(
                    R.layout.setting_popup_for_index,
                    LinearLayoutCompat(settingButtonViewContext),
                    false
                ).apply {
                    val menuListView =
                        this.findViewById<NoScrollListView>(
                            R.id.setting_menu_list_view
                        )
                    val menuListAdapter = SubMenuAdapter(
                        settingButtonViewContext,
                        menuMapList.toMutableList()
                    )
                    menuListView.adapter = menuListAdapter
                    menuListViewSetOnItemClickListener(
                        fragment,
                        menuListView
                    )
                    navButtonsSeter(
                        fragment,
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
//            true
        }
    }

    private fun navButtonsSeter(
        fragment: Fragment,
        settingButtonInnerView: View
    ){
        execSetNavImageButton(
            fragment,
            settingButtonInnerView,
            R.id.setting_menu_nav_back_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.BACK,
            EnableNavForWebView.checkForGoBack(fragment)
        )
        execSetNavImageButton(
            fragment,
            settingButtonInnerView,
            R.id.setting_menu_nav_reload_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.RELOAD,
            EnableNavForWebView.checkForReload(fragment),
        )
        execSetNavImageButton(
            fragment,
            settingButtonInnerView,
            R.id.setting_menu_nav_forward_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.FORWARD,
            EnableNavForWebView.checkForGoForward(fragment)
        )
    }

    private fun execSetNavImageButton (
        fragment: Fragment,
        settingButtonInnerView: View,
        buttonId: Int,
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
        buttonEnable: Boolean
    ){
        val context = fragment.context
        val navImageButton =
            settingButtonInnerView.findViewById<AppCompatImageButton>(
                buttonId
            )
        navImageButton.setOnClickListener {
            menuPopupWindow?.dismiss()
            menuPopupWindow = null

            val fannelInfoMap = when(fragment){
                is CommandIndexFragment -> fragment.fannelInfoMap
                is TerminalFragment -> fragment.fannelInfoMap
                else -> return@setOnClickListener
            }
            val listener = fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
            listener?.onToolbarMenuCategories(
                toolbarMenuCategoriesVariantForCmdIndex,
                EditFragmentArgs(
                    fannelInfoMap,
                    EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                )
            )
        }
        navImageButton.isEnabled = buttonEnable
        val colorId = if(buttonEnable) R.color.cmdclick_text_black else R.color.gray_out
        navImageButton.imageTintList = context?.getColorStateList(colorId)
    }

    private fun menuListViewSetOnItemClickListener(
        fragment: Fragment,
        menuListView: NoScrollListView
    ){
        menuListView.setOnItemClickListener {
                parent, View, pos, id ->
            menuPopupWindow?.dismiss()
            menuPopupWindow = null
            val menuListAdapter =
                menuListView.adapter as SubMenuAdapter
            when(menuListAdapter.getItem(pos)){
//                MenuEnums.INSTALL_FANNEL.itemName ->
//                    SystemFannelLauncher.launch(
//                        cmdIndexFragment,
////                        UsePath.cmdclickDefaultAppDirPath,
//                        SystemFannel.fannelRepoFannelName
//                    )
                MenuEnumsForCmdIndex.QR_SCAN.itemName ->
                    QrScanner.scanFromCamera(
                        fragment,
//                        currentAppDirPath,
                    )
//                MenuEnumsForCmdIndex.NO_SCROLL_SAVE_URL.itemName ->
//                    NoScrollUrlSaver.save(
//                        cmdIndexFragment,
////                        currentAppDirPath,
//                        String()
//                    )
//                MenuEnumsForCmdIndex.USAGE.itemName ->
//                    UsageDialog.launch(
//                        cmdIndexFragment,
//                    )
//                MenuEnumsForCmdIndex.EDIT_PREFERENCE.itemName ->
//                    preferenceEdit(fragment)
                MenuEnumsForCmdIndex.MANAGE.itemName ->
                    ManageSubMenuDialog.launch(
                        fragment,
//                        currentAppDirPath
                    )
                MenuEnumsForCmdIndex.SETTING.itemName ->
                    SettingSubMenuDialog.launch(fragment)
            }
        }
    }

    private fun preferenceEdit(
        cmdIndexFragment: CommandIndexFragment
    ){
        val context = cmdIndexFragment.context
        runBlocking {
            CmdClickSystemFannelManager.createPreferenceFannel(
                context,
            )
        }
        SystemFannelLauncher.launch(
            cmdIndexFragment,
//            currentAppDirPath,
            SystemFannel.preference,
        )
    }
}

private enum class MenuEnumsForCmdIndex(
    val itemName: String,
    val imageId: Int,
) {
    //    INSTALL_FANNEL("Install fannel", R.drawable.icons8_puzzle),
//    USAGE("Usage", R.drawable.icons8_info),
//    EDIT_PREFERENCE("Preference", R.drawable.icons8_setup),
//    NO_SCROLL_SAVE_URL("No scroll save url", R.drawable.icons8_check_ok),
    QR_SCAN("Scan QR", R.drawable.icons_qr_code),
    MANAGE("Manage", R.drawable.icons8_setup),
    SETTING("Setting", R.drawable.icons8_setting),
}