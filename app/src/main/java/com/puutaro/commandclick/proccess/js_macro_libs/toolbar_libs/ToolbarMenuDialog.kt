package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionDataMapKeyObj
import com.puutaro.commandclick.proccess.js_macro_libs.macros.MacroForToolbarButton
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton


object ToolbarMenuDialog {

    private var listIndexMenuDialog: Dialog? = null
    
    fun launch(
        editFragment: EditFragment,
        anchorView: View?,
        jsActionMap: Map<String, String>?,
    ) {
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        val context = editFragment.context
            ?: return
        listIndexMenuDialog = Dialog(
            context
        )
        listIndexMenuDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        val argsMap = JsActionDataMapKeyObj.getJsMacroArgs(
            jsActionMap,
        )
        val title =
            argsMap?.get(
                MacroForToolbarButton.MenuMacroArgsKey.TITLE.key
            )

        setListView(
            editFragment,
            anchorView,
            jsActionMap,
            title,
        )
        when(
            title.isNullOrEmpty()
        ) {
            true ->
                listIndexMenuDialog?.findViewById<LinearLayoutCompat>(
                    R.id.list_dialog_title_image
                )?.isVisible = false
            else -> listIndexMenuDialog?.findViewById<AppCompatTextView>(
                R.id.list_dialog_title
            )?.text = title
        }
        listIndexMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_message
        )?.isVisible = false
        listIndexMenuDialog?.findViewById<AppCompatEditText>(
            R.id.list_dialog_search_edit_text
        )?.isVisible = false
        setCancelListener()
        listIndexMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        listIndexMenuDialog
            ?.window
            ?.setGravity(Gravity.BOTTOM)
        listIndexMenuDialog?.show()
    }

    private fun setListView(
        editFragment: EditFragment,
        anchorView: View?,
        jsActionMap: Map<String, String>,
        title: String?,
    ) {
        val context = editFragment.context
            ?: return
        val subMenuListView =
            listIndexMenuDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            )
        val menuPairList = MenuSettingTool.createListMenuListMap(
            ListIndexArgsMaker.makeListIndexClickMenuPairList(
                editFragment,
                jsActionMap
            ),
        )
        val subMenuAdapter = SubMenuAdapter(
            context,
            menuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            editFragment,
            anchorView,
            jsActionMap,
            subMenuListView,
            title,
        )
    }

    private fun setCancelListener(
    ){
        val cancelImageButton =
            listIndexMenuDialog?.findViewById<ImageButton>(
                R.id.list_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            listIndexMenuDialog?.dismiss()
        }
        listIndexMenuDialog?.setOnCancelListener {
            listIndexMenuDialog?.dismiss()
        }
    }

    private fun subMenuItemClickListener(
        editFragment: EditFragment,
        anchorView: View?,
        jsActionMap: Map<String, String>,
        subMenuListView: ListView?,
        title: String?,
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            listIndexMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val clickedMenuName = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            jsPathOrSubMenuHandlerForToolbar(
                editFragment,
                anchorView,
                clickedMenuName,
                jsActionMap,
                title,
            )
        }
    }


    private fun jsPathOrSubMenuHandlerForToolbar(
        editFragment: EditFragment,
        anchorView: View?,
        clickedMenuName: String,
        jsActionMap: Map<String, String>,
        selectedItem: String?,
    ) {
        val settingButtonMenuPairList =
            ListIndexArgsMaker.makeListIndexClickMenuPairList(
                editFragment,
                jsActionMap,
            )
        val hitMenuItemPairList = !MenuSettingTool.firstOrNullByParentMenuName(
            settingButtonMenuPairList,
            clickedMenuName
        ).isNullOrEmpty()
        when (hitMenuItemPairList) {
            true ->
                ToolbarSubMenuDialog.launch(
                    editFragment,
                    anchorView,
                    jsActionMap,
                    selectedItem,
                    clickedMenuName,
                )

            else -> {
                val updateJsActionMap = JsActionTool.makeJsActionMap(
                    editFragment,
                    MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
                        settingButtonMenuPairList,
                        clickedMenuName
                    )
                )
                JsPathHandlerForToolbarButton.handle(
                    editFragment,
                    anchorView,
                    updateJsActionMap,
                )
            }
        }
    }
}