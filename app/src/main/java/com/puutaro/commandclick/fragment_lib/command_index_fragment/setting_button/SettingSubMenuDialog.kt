package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ConfigEdit
import com.puutaro.commandclick.util.state.EditFragmentArgs

object SettingSubMenuDialog {

    private var settingSubMenuDialog: Dialog? = null

    fun launch(
        cmdIndexFragment: CommandIndexFragment
    ){
        val context = cmdIndexFragment.context
            ?: return

        settingSubMenuDialog = Dialog(
            context
        )
        settingSubMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            cmdIndexFragment,
        )
        setCancelListener()
        settingSubMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        settingSubMenuDialog?.window?.setGravity(Gravity.BOTTOM)
        settingSubMenuDialog?.show()

    }

    private fun setCancelListener(
    ){
        val cancelImageButton =
            settingSubMenuDialog?.findViewById<ImageButton>(
                R.id.submenu_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            settingSubMenuDialog?.dismiss()
            settingSubMenuDialog = null
        }
        settingSubMenuDialog?.setOnCancelListener {
            settingSubMenuDialog?.dismiss()
            settingSubMenuDialog = null
        }
    }

    private fun setListView(
        cmdIndexFragment: CommandIndexFragment,
    ) {
        val context = cmdIndexFragment.context
            ?: return
        val subMenuListView =
            settingSubMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = SettingSubMenuEnums.values().map {
            it.itemName to it.imageId
        }
        val subMenuAdapter = SubMenuAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        settingSubMenuItemClickListener(
            cmdIndexFragment,
            subMenuListView
        )
    }

    private fun settingSubMenuItemClickListener(
        cmdIndexFragment: CommandIndexFragment,
        subMenuListView: ListView?
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            settingSubMenuDialog?.dismiss()
            settingSubMenuDialog = null
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val selectedSubMenu = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            val settingSubMenuEnums = SettingSubMenuEnums.values().firstOrNull {
                it.itemName == selectedSubMenu
            } ?: return@setOnItemClickListener
            when(settingSubMenuEnums){
                SettingSubMenuEnums.APP_DIR_MANAGER -> {
                    SystemFannelLauncher.launch(
                        cmdIndexFragment,
                        UsePath.cmdclickSystemAppDirPath,
                        SystemFannel.appDirManagerFannelName
                    )
                }
                SettingSubMenuEnums.SHORTCUT -> {
                    val listener =
                        cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT,
                        EditFragmentArgs(
                            cmdIndexFragment.fannelInfoMap,
                            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                        )
                    )
                }
                SettingSubMenuEnums.TERMUX_SETUP -> {
                    val listener =
                        cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP,
                        EditFragmentArgs(
                            cmdIndexFragment.fannelInfoMap,
                            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                        )
                    )
                }
                SettingSubMenuEnums.CONFIG -> {
                    ConfigEdit.edit(cmdIndexFragment)
                }
            }
        }
    }

    private enum class SettingSubMenuEnums(
        val itemName: String,
        val imageId: Int
    ){
        APP_DIR_MANAGER("app dir manager", R.drawable.icons8_support),
        SHORTCUT("create short cut", R.drawable.icons8_shortcut),
        TERMUX_SETUP("termux setup", R.drawable.icons8_setup),
        CONFIG("config", R.drawable.icons8_edit),
    }

}