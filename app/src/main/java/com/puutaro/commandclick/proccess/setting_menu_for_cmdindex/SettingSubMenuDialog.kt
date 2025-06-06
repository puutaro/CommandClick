package com.puutaro.commandclick.proccess.setting_menu_for_cmdindex

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
//import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ConfigEdit
import com.puutaro.commandclick.util.state.EditFragmentArgs

object SettingSubMenuDialog {

    private var settingSubMenuDialog: Dialog? = null

    fun launch(
        fragment: Fragment
    ){
        val context = fragment.context
            ?: return

        settingSubMenuDialog = Dialog(
            context
        )
        settingSubMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            fragment,
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
        fragment: Fragment,
    ) {
        val context = fragment.context
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
            fragment,
            subMenuListView
        )
    }

    private fun settingSubMenuItemClickListener(
        fragment: Fragment,
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
//                SettingSubMenuEnums.APP_DIR_MANAGER -> {
//                    SystemFannelLauncher.launch(
//                        cmdIndexFragment,
////                        UsePath.cmdclickDefaultAppDirPath,
//                        SystemFannel.appDirManagerFannelName
//                    )
//                }
                SettingSubMenuEnums.SHORTCUT -> {
                    val fannelInfoMap = when(fragment){
                        is CommandIndexFragment -> fragment.fannelInfoMap
                        is TerminalFragment -> fragment.fannelInfoMap
                        else -> return@setOnItemClickListener
                    }
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
//                SettingSubMenuEnums.TERMUX_SETUP -> {
//                    val listener =
//                        cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
//                    listener?.onToolbarMenuCategories(
//                        ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP,
//                        EditFragmentArgs(
//                            cmdIndexFragment.fannelInfoMap,
//                            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
//                        )
//                    )
//                }
//                SettingSubMenuEnums.CONFIG -> {
//                    ConfigEdit.edit(cmdIndexFragment)
//                }
            }
        }
    }

    private enum class SettingSubMenuEnums(
        val itemName: String,
        val imageId: Int
    ){
//        APP_DIR_MANAGER("App dir manager", R.drawable.icons8_support),
        SHORTCUT("Create short cut", R.drawable.icons8_shortcut),
//        TERMUX_SETUP("Termux setup", R.drawable.icons8_setup),
//        CONFIG("Config", R.drawable.icons8_edit),
    }

}