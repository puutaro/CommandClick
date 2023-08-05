package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.component.adapter.MenuListAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.SystemFannelLauncher
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.proccess.lib.VaridateionErrDialog
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object SubMenuDialog {

    private var subMenuDialog: Dialog? = null

    fun launch(
        commandIndexFragment: CommandIndexFragment
    ){
        val context = commandIndexFragment.context
            ?: return
        val terminalViewModel: TerminalViewModel by commandIndexFragment.activityViewModels()

        subMenuDialog = Dialog(
            context
        )
        subMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            commandIndexFragment,
            terminalViewModel
        )
        setCancelListener()
        subMenuDialog
            ?.window
            ?.setGravity(Gravity.BOTTOM)
        subMenuDialog?.show()

    }

    private fun setCancelListener(
    ){
        val cancelImageButton =
            subMenuDialog?.findViewById<ImageButton>(
                R.id.submenu_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            subMenuDialog?.dismiss()
        }
        subMenuDialog?.setOnCancelListener {
            subMenuDialog?.dismiss()
        }
    }

    private fun setListView(
        commandIndexFragment: CommandIndexFragment,
        terminalViewModel: TerminalViewModel
    ) {
        val context = commandIndexFragment.context
            ?: return
        val subMenuListView =
            subMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = SettingSubMenuEnums.values().map {
            it.itemName to it.imageId
        }
        val subMenuAdapter = MenuListAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            commandIndexFragment,
            terminalViewModel,
            subMenuListView
        )
    }

    private fun subMenuItemClickListener(
        commandIndexFragment: CommandIndexFragment,
        terminalViewModel: TerminalViewModel,
        subMenuListView: ListView?
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            subMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as MenuListAdapter
            val selectedSubMenu = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            when(selectedSubMenu){
                SettingSubMenuEnums.CHDIR.itemName -> {
                    SystemFannelLauncher.launch(
                        commandIndexFragment,
                        UsePath.cmdclickSystemAppDirPath,
                        UsePath.appDirManagerFannelName
                    )
                }
                SettingSubMenuEnums.CC_IMPORT.itemName -> {
                    SystemFannelLauncher.launch(
                        commandIndexFragment,
                        UsePath.cmdclickSystemAppDirPath,
                        UsePath.ccImportManagerFannelName
                    )
                }
                SettingSubMenuEnums.SHORTCUT.itemName -> {
                    val listener = commandIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT
                    )
                }
                SettingSubMenuEnums.TERM_REFRESH.itemName -> {
                    TermRefresh.refresh(
                        terminalViewModel.currentMonitorFileName
                    )
                }
                SettingSubMenuEnums.TERMUX_SETUP.itemName -> {
                    val listener = commandIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP
                    )
                }
                SettingSubMenuEnums.CONFIG.itemName -> {
                    configEdit(commandIndexFragment)
                }
            }
        }
    }

    private enum class SettingSubMenuEnums(
        val itemName: String,
        val imageId: Int
    ){
        CHDIR("change_app_dir", R.drawable.icons8_support),
        CC_IMPORT("cc_import_manager", R.drawable.icons8_folda),
        SHORTCUT("create_short_cut", R.drawable.icons8_shortcut),
        TERM_REFRESH("term_refresh", R.drawable.icons8_refresh),
        TERMUX_SETUP("termux_setup", R.drawable.icons8_setup),
        CONFIG("config", R.drawable.icons8_edit),
    }

    private fun configEdit(
        cmdIndexCommandIndexFragment: CommandIndexFragment
    ){
        val configDirPath = UsePath.cmdclickSystemAppDirPath
        val configShellName = UsePath.cmdclickConfigFileName
        CommandClickScriptVariable.makeConfigJsFile(
            configDirPath,
            configShellName
        )
        val shellContentsList = ReadText(
            configDirPath,
            configShellName
        ).textToList()
        val validateErrMessage = ValidateShell.correct(
            cmdIndexCommandIndexFragment,
            shellContentsList,
            configShellName
        )
        if(validateErrMessage.isNotEmpty()){
            val shellScriptPath = "${configDirPath}/${configShellName}"
            VaridateionErrDialog.show(
                cmdIndexCommandIndexFragment,
                shellScriptPath,
                validateErrMessage
            )
            return
        }
        val cmdclickConfigFileName = UsePath.cmdclickConfigFileName
        val sharedPref = cmdIndexCommandIndexFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        SharePreffrenceMethod.putSharePreffrence(
            sharedPref,
            mapOf(
                SharePrefferenceSetting.current_app_dir.name
                        to UsePath.cmdclickSystemAppDirPath,
                SharePrefferenceSetting.current_script_file_name.name
                        to cmdclickConfigFileName,
                SharePrefferenceSetting.on_shortcut.name
                        to FragmentTagManager.Suffix.ON.name
            )
        )
        val cmdEditFragmentTag = FragmentTagManager.makeTag(
            FragmentTagManager.Prefix.cmdEditPrefix.str,
            UsePath.cmdclickSystemAppDirPath,
            cmdclickConfigFileName,
            FragmentTagManager.Suffix.ON.name
        )
        val listener = cmdIndexCommandIndexFragment.context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            cmdEditFragmentTag
        )
    }
}