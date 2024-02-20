package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.EditSettingJsTool
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton

object SettingButtonSubMenuDialog {

    private var settingButtonSubMenuDialog: Dialog? = null

    fun launch(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        jsActionsMap: Map<String, String>?,
        parentMenuName: String,
    ){
        val fragment = toolbarButtonArgsMaker.editFragment
        val context = fragment.context
            ?: return
        settingButtonSubMenuDialog = Dialog(
            context
        )
        settingButtonSubMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            toolbarButtonArgsMaker,
            jsActionsMap,
            parentMenuName,
        )
        setCancelListener()
        settingButtonSubMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        settingButtonSubMenuDialog
            ?.window
            ?.setGravity(Gravity.BOTTOM)
        settingButtonSubMenuDialog?.show()

    }

    private fun setCancelListener(
    ){
        val cancelImageButton =
            settingButtonSubMenuDialog?.findViewById<ImageButton>(
                R.id.submenu_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            settingButtonSubMenuDialog?.dismiss()
        }
        settingButtonSubMenuDialog?.setOnCancelListener {
            settingButtonSubMenuDialog?.dismiss()
        }
    }

    private fun setListView(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        jsActionsMap: Map<String, String>?,
        parentMenuName: String,
    ) {
        val fragment = toolbarButtonArgsMaker.editFragment
        val context = fragment.context
            ?: return
        val subMenuListView =
            settingButtonSubMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = MenuSettingTool.createSubMenuListMap(
            toolbarButtonArgsMaker.makeSettingButtonMenuPairList(jsActionsMap),
            parentMenuName,
        )
        val subMenuAdapter = SubMenuAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            toolbarButtonArgsMaker,
            subMenuListView,
            jsActionsMap,
        )
    }

    private fun subMenuItemClickListener(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        subMenuListView: ListView?,
        jsActionsMap: Map<String, String>?
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            settingButtonSubMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val clickedSubMenu = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            val updateJsActionMap = EditSettingJsTool.makeJsActionMap(
                toolbarButtonArgsMaker.editFragment,
                MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
                    toolbarButtonArgsMaker.makeSettingButtonMenuPairList(jsActionsMap),
                    clickedSubMenu
                )
            )
            JsPathHandlerForToolbarButton.handle(
                toolbarButtonArgsMaker,
                updateJsActionMap
            )
        }
    }
}
