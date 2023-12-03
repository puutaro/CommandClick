package com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.SelectTermDialog
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object ManageSubMenuDialogForEdit {

    private var manageSubMenuDialog: Dialog? = null

    fun launch(
        editFragment: EditFragment
    ){
        val context = editFragment.context
            ?: return

        manageSubMenuDialog = Dialog(
            context
        )
        manageSubMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            editFragment,
        )
        setCancelListener()
        manageSubMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        manageSubMenuDialog
            ?.window
            ?.setGravity(Gravity.BOTTOM)
        manageSubMenuDialog?.show()

    }

    private fun setCancelListener(
    ){
        val cancelImageButton =
            manageSubMenuDialog?.findViewById<ImageButton>(
                R.id.submenu_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            manageSubMenuDialog?.dismiss()
        }
        manageSubMenuDialog?.setOnCancelListener {
            manageSubMenuDialog?.dismiss()
        }
    }

    private fun setListView(
        editFragment: EditFragment,
    ) {
        val context = editFragment.context
            ?: return
        val subMenuListView =
            manageSubMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = SettingSubMenuEnumsForEdit.values().map {
            it.itemName to it.imageId
        }
        val subMenuAdapter = SubMenuAdapter(
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
        val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            manageSubMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val selectedSubMenu = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            when(selectedSubMenu){
                SettingSubMenuEnumsForEdit.REFRESH_MONITOR.itemName
                -> TermRefresh.refresh(
                    terminalViewModel.currentMonitorFileName
                )
                SettingSubMenuEnumsForEdit.SELECT_MONITOR.itemName
                -> SelectTermDialog.launch(editFragment)
                SettingSubMenuEnumsForEdit.RESTART_UBUNTU.itemName
                -> UbuntuServiceManager.launch(
                    editFragment.activity
                )
            }
        }
    }

    private enum class SettingSubMenuEnumsForEdit(
        val itemName: String,
        val imageId: Int
    ){
        REFRESH_MONITOR("refresh monitor", R.drawable.icons8_refresh),
        SELECT_MONITOR("select monitor", R.drawable.icons8_file),
        RESTART_UBUNTU("restart ubuntu", R.drawable.icons8_launch),
    }
}
