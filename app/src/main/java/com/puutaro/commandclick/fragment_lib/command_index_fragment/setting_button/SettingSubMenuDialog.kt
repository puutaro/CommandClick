package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.SystemFannelLauncher
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.proccess.lib.VariationErrDialog
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object SubMenuDialog {

    private var subMenuDialog: Dialog? = null

    fun launch(
        cmdIndexFragment: CommandIndexFragment
    ){
        val context = cmdIndexFragment.context
            ?: return
        val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()

        subMenuDialog = Dialog(
            context
        )
        subMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            cmdIndexFragment,
            terminalViewModel
        )
        setCancelListener()
        subMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        subMenuDialog?.window?.setGravity(Gravity.BOTTOM)
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
        cmdIndexFragment: CommandIndexFragment,
        terminalViewModel: TerminalViewModel
    ) {
        val context = cmdIndexFragment.context
            ?: return
        val subMenuListView =
            subMenuDialog?.findViewById<ListView>(
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
        subMenuItemClickListener(
            cmdIndexFragment,
            terminalViewModel,
            subMenuListView
        )
    }

    private fun subMenuItemClickListener(
        cmdIndexFragment: CommandIndexFragment,
        terminalViewModel: TerminalViewModel,
        subMenuListView: ListView?
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            subMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val selectedSubMenu = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            when(selectedSubMenu){
                SettingSubMenuEnums.CHDIR.itemName -> {
                    SystemFannelLauncher.launch(
                        cmdIndexFragment,
                        UsePath.cmdclickSystemAppDirPath,
                        UsePath.appDirManagerFannelName
                    )
                }
                SettingSubMenuEnums.JS_IMPORT.itemName -> {
                    SystemFannelLauncher.launch(
                        cmdIndexFragment,
                        UsePath.cmdclickSystemAppDirPath,
                        UsePath.jsImportManagerFannelName
                    )
                }
                SettingSubMenuEnums.SHORTCUT.itemName -> {
                    val listener = cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
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
                    val listener = cmdIndexFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP
                    )
                }
                SettingSubMenuEnums.CONFIG.itemName -> {
                    configEdit(cmdIndexFragment)
                }
            }
        }
    }

    private enum class SettingSubMenuEnums(
        val itemName: String,
        val imageId: Int
    ){
        CHDIR("change app dir", R.drawable.icons8_support),
        JS_IMPORT("js import manager", R.drawable.icons8_folda),
        SHORTCUT("create short cut", R.drawable.icons8_shortcut),
        TERM_REFRESH("term refresh", R.drawable.icons8_refresh),
        TERMUX_SETUP("termux setup", R.drawable.icons8_setup),
        CONFIG("config", R.drawable.icons8_edit),
    }

    private fun configEdit(
        cmdIndexFragment: CommandIndexFragment
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
            cmdIndexFragment,
            shellContentsList,
            configShellName
        )
        if(validateErrMessage.isNotEmpty()){
            val shellScriptPath = "${configDirPath}/${configShellName}"
            VariationErrDialog.show(
                cmdIndexFragment,
                shellScriptPath,
                validateErrMessage
            )
            return
        }
        val cmdclickConfigFileName = UsePath.cmdclickConfigFileName
        val sharedPref = cmdIndexFragment.activity?.getPreferences(Context.MODE_PRIVATE)
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
        val listener = cmdIndexFragment.context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            cmdEditFragmentTag
        )
    }
}