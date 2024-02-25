package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton

object ToolbarButtonSubMenuDialog {

    private var settingButtonSubMenuDialog: Dialog? = null

    fun launch(
        editFragment: EditFragment,
        settingButtonView: View?,
        jsActionsMap: Map<String, String>?,
        parentMenuName: String,
    ){
        val context = editFragment.context
            ?: return
        settingButtonSubMenuDialog = Dialog(
            context
        )
        settingButtonSubMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            editFragment,
            settingButtonView,
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
        editFragment: EditFragment,
        settingButtonView: View?,
        jsActionsMap: Map<String, String>?,
        parentMenuName: String,
    ) {
        val context = editFragment.context
            ?: return
        val subMenuListView =
            settingButtonSubMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = MenuSettingTool.createSubMenuListMap(
            ToolbarButtonArgsMaker.makeSettingButtonMenuPairList(
                editFragment,
                jsActionsMap
            ),
            parentMenuName,
        )
        val subMenuAdapter = SubMenuAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            editFragment,
            settingButtonView,
            subMenuListView,
            jsActionsMap,
        )
    }

    private fun subMenuItemClickListener(
        editFragment: EditFragment,
        settingButtonView: View?,
        subMenuListView: ListView?,
        jsActionsMap: Map<String, String>?
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            settingButtonSubMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val clickedSubMenu = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            val updateJsActionMap = JsActionTool.makeJsActionMap(
                editFragment,
                MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
                    ToolbarButtonArgsMaker.makeSettingButtonMenuPairList(
                        editFragment,
                        jsActionsMap
                    ),
                    clickedSubMenu
                )
            )
            JsPathHandlerForToolbarButton.handle(
                editFragment,
                settingButtonView,
                updateJsActionMap
            )
        }
    }
}
