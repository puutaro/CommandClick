package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

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
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.lib.VaridateionErrDialog
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object SubMenuDialogForEdit {

    private var subMenuDialog: Dialog? = null

    fun launch(
        editFragment: EditFragment
    ){
        val context = editFragment.context
            ?: return
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()

        subMenuDialog = Dialog(
            context
        )
        subMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            editFragment,
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
        editFragment: EditFragment,
        terminalViewModel: TerminalViewModel
    ) {
        val context = editFragment.context
            ?: return
        val subMenuListView =
            subMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = SettingSubMenuEnumsForEdit.values().map {
            it.itemName to it.imageId
        }
        val subMenuAdapter = MenuListAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            editFragment,
            subMenuListView
        )
    }

    private fun subMenuItemClickListener(
        editFragment: EditFragment,
        subMenuListView: ListView?
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            subMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as MenuListAdapter
            val selectedSubMenu = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            when(selectedSubMenu){
                SettingSubMenuEnumsForEdit.SHORTCUT.itemName -> {
                    val listener = editFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT
                    )
                }
                SettingSubMenuEnumsForEdit.TERMUX_SETUP.itemName -> {
                    val listener = editFragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP
                    )
                }
                SettingSubMenuEnumsForEdit.CONFIG.itemName -> {
                    configEdit(editFragment)
                }
            }
        }
    }

    private enum class SettingSubMenuEnumsForEdit(
        val itemName: String,
        val imageId: Int
    ){
        SHORTCUT("create_short_cut", R.drawable.icons8_shortcut),
        TERMUX_SETUP("termux_setup", R.drawable.icons8_setup),
        CONFIG("config", R.drawable.icons8_edit),
    }

    private fun configEdit(
        editFragment: EditFragment
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
            editFragment,
            shellContentsList,
            configShellName
        )
        if(validateErrMessage.isNotEmpty()){
            val shellScriptPath = "${configDirPath}/${configShellName}"
            VaridateionErrDialog.show(
                editFragment,
                shellScriptPath,
                validateErrMessage
            )
            return
        }
        val cmdclickConfigFileName = UsePath.cmdclickConfigFileName
        val sharedPref = editFragment.activity?.getPreferences(Context.MODE_PRIVATE)
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
        val listener = editFragment.context
                as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
        listener?.onLongClickMenuItemsforCmdIndex(
            LongClickMenuItemsforCmdIndex.EDIT,
            cmdEditFragmentTag
        )
    }
}
