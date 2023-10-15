package com.puutaro.commandclick.fragment_lib.command_index_fragment


import android.content.SharedPreferences
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.subMenuAdapter
import com.puutaro.commandclick.custom_view.NoScrollListView
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.ScriptFileEdit
import com.puutaro.commandclick.proccess.SelectTermDialog
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.AddScriptHandler
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.InstallFannelHandler
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.InstallFromFannelRepo
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.SubMenuDialog
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.EnableNavForWebView
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.proccess.NoScrollUrlSaver
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.SharePreffrenceMethod


class ToolBarSettingButtonControl(
    binding: CommandIndexFragmentBinding,
    private val cmdIndexFragment: CommandIndexFragment,
    private val sharedPref: SharedPreferences?,
    readSharePreffernceMap: Map<String, String>,
){
    private val context = cmdIndexFragment.context
    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )

    private val settingButtonView = binding.cmdindexSettingButton
//    private val popup = PopupMenu(context, settingButtonView)
    private val installFromFannelRepo = InstallFromFannelRepo(
        cmdIndexFragment,
        currentAppDirPath,
    )
    private val menuListMap = MenuEnums.values().toList().map{
        it.itemName to it.imageId
    }
    private var menuPopupWindow: PopupWindow? = null

    fun toolbarSettingButtonOnLongClick() {
        settingButtonView.setOnClickListener {
            ExecSetTermSizeForCmdIndexFragment.execSetTermSizeForCmdIndexFragment(
                cmdIndexFragment,
            )
        }
    }

    fun toolbarSettingButtonOnClick(){
        settingButtonView.setOnLongClickListener {
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
                    val menuListAdapter = subMenuAdapter(
                        settingButtonViewContext,
                        menuListMap.toMutableList()
                    )
                    menuListView.adapter = menuListAdapter
                    menuListViewSetOnItemClickListener(menuListView)
                    navButtonsSeter(this)
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
            true
        }
    }

    private fun navButtonsSeter(
        settingButtonInnerView: View
    ){
        execSetNavImageButton(
            settingButtonInnerView,
            R.id.setting_menu_nav_back_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.BACK,
            EnableNavForWebView.checkForGoBack(cmdIndexFragment)
        )
        execSetNavImageButton(
            settingButtonInnerView,
            R.id.setting_menu_nav_reload_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.RELOAD,
            EnableNavForWebView.checkForReload(cmdIndexFragment),
        )
        execSetNavImageButton(
            settingButtonInnerView,
            R.id.setting_menu_nav_forward_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.FORWARD,
            EnableNavForWebView.checkForGoForward(cmdIndexFragment)
        )
    }

    private fun execSetNavImageButton (
        settingButtonInnerView: View,
        buttonId: Int,
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
        buttonEnable: Boolean
    ){
        val navImageButton =
            settingButtonInnerView.findViewById<AppCompatImageButton>(
                buttonId
            )
        navImageButton.setOnClickListener {
            menuPopupWindow?.dismiss()
            val listener = cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
            listener?.onToolbarMenuCategories(
                toolbarMenuCategoriesVariantForCmdIndex
            )
        }
        navImageButton.isEnabled = buttonEnable
        val colorId = if(buttonEnable) R.color.cmdclick_text_black else R.color.gray_out
        navImageButton.imageTintList = context?.getColorStateList(colorId)
    }

    private fun menuListViewSetOnItemClickListener(
        menuListView: NoScrollListView
    ){
        menuListView.setOnItemClickListener {
                parent, View, pos, id ->
            menuPopupWindow?.dismiss()
            val menuListAdapter =
                menuListView.adapter as subMenuAdapter
            when(menuListAdapter.getItem(pos)){
                MenuEnums.ADD.itemName -> {
                    AddScriptHandler(
                        cmdIndexFragment,
                        sharedPref,
                        currentAppDirPath,
                    ).handle()
                }
                MenuEnums.SETTING.itemName -> {
                    SubMenuDialog.launch(cmdIndexFragment)
                }
                MenuEnums.SELECTTERM.itemName -> {
                    SelectTermDialog.launch(cmdIndexFragment)
                }
                MenuEnums.INSTALL_FANNEL.itemName -> {
                    InstallFannelHandler.handle(
                        cmdIndexFragment,
                        installFromFannelRepo
                    )
                }
                MenuEnums.NO_SCROLL_SAVE_URL.itemName -> {
                    NoScrollUrlSaver.save(
                        cmdIndexFragment,
                        currentAppDirPath,
                        String()
                    )
                }

                MenuEnums.EDIT_STARTUP.itemName -> {
                    ScriptFileEdit.edit(
                        cmdIndexFragment,
                        currentAppDirPath,
                        UsePath.cmdclickStartupJsName,
                    )
                }
                MenuEnums.RESTART_UBUNTU.itemName -> {
                    UbuntuServiceManager.launch(
                        cmdIndexFragment.activity
                    )
                }
            }
        }
    }
}

private enum class MenuEnums(
    val itemName: String,
    val imageId: Int,
) {
    ADD("add", R.drawable.icons8_plus),
    SELECTTERM("select term", R.drawable.icons8_file),
//    TERM1("term_1"),
//    TERM2("term_2"),
//    TERM3("term_3"),
//    TERM4("term_4"),
    EDIT_STARTUP("edit startup", R.drawable.icons8_edit_frame),
    RESTART_UBUNTU("restart ubuntu", R.drawable.icons8_launch),
    NO_SCROLL_SAVE_URL("no scroll save url", R.drawable.icons8_check_ok),
    INSTALL_FANNEL("install fannel", R.drawable.icons8_puzzle),
    SETTING("setting",R.drawable.icons8_setting),
    //    SETUP_UBUNTU("setup ubuntu", R.drawable.ic_terminal),
}

