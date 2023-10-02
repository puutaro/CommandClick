package com.puutaro.commandclick.proccess

import android.app.Dialog
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.MenuListAdapter
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object SelectTermDialog {
    private var subMenuDialog: Dialog? = null

    fun launch(
        currentFragment: Fragment
    ){
        val context = currentFragment.context
            ?: return
        val terminalViewModel: TerminalViewModel by currentFragment.activityViewModels()

        subMenuDialog = Dialog(
            context
        )
        subMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            currentFragment,
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
        currentFragment: Fragment,
        terminalViewModel: TerminalViewModel
    ) {
        val context = currentFragment.context
            ?: return
        val subMenuListView =
            subMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairListScr = SettingSubMenuEnums.values().map {
            it.itemName to it.imageId
        }.reversed()
        val currentMonitorFileName = terminalViewModel.currentMonitorFileName
        val subMenuPairListSelected = subMenuPairListScr.filter {
            it.first == currentMonitorFileName
        }
        val subMenuPairListNoSelected = subMenuPairListScr.filter {
            it.first != currentMonitorFileName
        }
        val subMenuPairList = subMenuPairListNoSelected + subMenuPairListSelected
        val subMenuAdapter = MenuListAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            currentFragment,
            terminalViewModel,
            subMenuListView
        )
    }

    private fun subMenuItemClickListener(
        currentFragment: Fragment,
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
                SettingSubMenuEnums.MONITOR_1.itemName,
                SettingSubMenuEnums.MONITOR_2.itemName,
                SettingSubMenuEnums.MONITOR_3.itemName,
                SettingSubMenuEnums.MONITOR_4.itemName -> {
                    FileSystems.createDirs(
                        UsePath.cmdclickMonitorDirPath,
                    )
                    FileSystems.createFiles(
                        UsePath.cmdclickMonitorDirPath,
                        selectedSubMenu
                    )
                    terminalViewModel.currentMonitorFileName = selectedSubMenu
                    Toast.makeText(
                        currentFragment.context,
                        "set ${selectedSubMenu}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private enum class SettingSubMenuEnums(
        val itemName: String,
        val imageId: Int
    ){
        MONITOR_1(UsePath.cmdClickMonitorFileName_1, R.drawable.icons8_file),
        MONITOR_2(UsePath.cmdClickMonitorFileName_2, R.drawable.icons8_file),
        MONITOR_3(UsePath.cmdClickMonitorFileName_3, R.drawable.icons8_file),
        MONITOR_4(UsePath.cmdClickMonitorFileName_4, R.drawable.icons8_file)
    }
}