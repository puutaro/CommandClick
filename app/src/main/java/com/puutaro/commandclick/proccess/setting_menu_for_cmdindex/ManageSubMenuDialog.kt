package com.puutaro.commandclick.proccess.setting_menu_for_cmdindex

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.broadcast.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.JsDebugger
import com.puutaro.commandclick.proccess.AppProcessManager
import com.puutaro.commandclick.proccess.SelectTermDialog
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object ManageSubMenuDialog {

    private var manageSubMenuDialog: Dialog? = null

    fun launch(
        fragment: Fragment,
//        currentAppDirPath: String
    ){
        val context = fragment.context
            ?: return

        manageSubMenuDialog = Dialog(
            context
        )
        manageSubMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            fragment,
//            currentAppDirPath,
        )
        setCancelListener()
        manageSubMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        manageSubMenuDialog?.window?.setGravity(Gravity.BOTTOM)
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
            manageSubMenuDialog = null
        }
        manageSubMenuDialog?.setOnCancelListener {
            manageSubMenuDialog?.dismiss()
            manageSubMenuDialog = null
        }
    }

    private fun setListView(
        fragment: Fragment,
//        currentAppDirPath: String,
    ) {
        val context = fragment.context
            ?: return
        val subMenuListView =
            manageSubMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = ManageSubMenuEnums.values().map {
            it.itemName to it.imageId
        }
        val subMenuAdapter = SubMenuAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            fragment,
//            currentAppDirPath,
            subMenuListView
        )
    }

    private fun subMenuItemClickListener(
        fragment: Fragment,
//        currentAppDirPath: String,
        subMenuListView: ListView?
    ){
        val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            manageSubMenuDialog?.dismiss()
            manageSubMenuDialog = null
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val selectedSubMenu = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            val manageSubMenuEnums = ManageSubMenuEnums.values().firstOrNull {
                it.itemName == selectedSubMenu
            } ?: return@setOnItemClickListener
            when(manageSubMenuEnums){
                ManageSubMenuEnums.KILL -> {
                    AppProcessManager.killDialogForCmdIndex(
                        fragment,
//                        currentAppDirPath,
                        String(),
                    )
                }
                ManageSubMenuEnums.RESTART_UBUNTU -> {
                    UbuntuServiceManager.launch(
                        fragment.activity
                    )
                }
                ManageSubMenuEnums.SELECT_MONITOR -> {
                    SelectTermDialog.launch(fragment)
                }
//                ManageSubMenuEnums.ADD -> {
//                    AddScriptHandler(
//                        cmdIndexFragment,
////                        currentAppDirPath,
//                    ).handle()
//                }
                ManageSubMenuEnums.LAUNCH_DEBUGGER -> {
                    JsDebugger.sendDebugNoti(
                        fragment.context,
                        BroadCastIntentExtraForJsDebug.DebugGenre.JS_DEBUG.type,
                        BroadCastIntentExtraForJsDebug.NotiLevelType.HIGH.level,
                    )
                }
//                ManageSubMenuEnums.JS_IMPORT -> {
//                    SystemFannelLauncher.launch(
//                        cmdIndexFragment,
////                        UsePath.cmdclickDefaultAppDirPath,
//                        SystemFannel.jsImportManagerFannelName
//                    )
//                }
                ManageSubMenuEnums.REFRESH_MONITOR -> {
                    TermRefresh.refresh(
                        terminalViewModel.currentMonitorFileName
                    )
                }
            }
        }
    }

    private enum class ManageSubMenuEnums(
        val itemName: String,
        val imageId: Int
    ){
        KILL("Kill", R.drawable.icons8_cancel),
        REFRESH_MONITOR("Refresh monitor", R.drawable.icons8_refresh),
        SELECT_MONITOR("Select monitor", R.drawable.icons8_file),
        RESTART_UBUNTU("Restart ubuntu", R.drawable.icons8_launch),
        LAUNCH_DEBUGGER("Launch debugger", R.drawable.icon_debug),
//        JS_IMPORT("Js import manager", R.drawable.icons8_folda),
//        ADD("Add", R.drawable.icons8_plus),
    }
}