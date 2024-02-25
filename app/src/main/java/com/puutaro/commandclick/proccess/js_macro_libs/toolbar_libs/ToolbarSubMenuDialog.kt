package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.EditSettingJsTool
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.list_index_for_edit.libs.ListIndexArgsMaker
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton

object ToolbarSubMenuDialog {

    private var listIndexSubMenuDialog: Dialog? = null
    
    fun launch(
        editFragment: EditFragment,
        anchorView: View?,
        jsActionMap: Map<String, String>?,
        titleSrc: String?,
        parentMenuName: String,
    ) {
        if (
            jsActionMap.isNullOrEmpty()
        ) return
        val context = editFragment.context
            ?: return
        listIndexSubMenuDialog = Dialog(
            context
        )

        listIndexSubMenuDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        val title = "${titleSrc}:${parentMenuName}"
        listIndexSubMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )?.text = title
        listIndexSubMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_message
        )?.isVisible = false
        listIndexSubMenuDialog?.findViewById<AppCompatEditText>(
            R.id.list_dialog_search_edit_text
        )?.isVisible = false
        setListView(
            editFragment,
            anchorView,
            jsActionMap,
            parentMenuName,
        )
        setCancelListener()
        listIndexSubMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        listIndexSubMenuDialog
            ?.window
            ?.setGravity(Gravity.BOTTOM)
        listIndexSubMenuDialog?.show()

    }

    private fun setCancelListener(
    ) {
        val cancelImageButton =
            listIndexSubMenuDialog?.findViewById<ImageButton>(
                R.id.list_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            listIndexSubMenuDialog?.dismiss()
        }
        listIndexSubMenuDialog?.setOnCancelListener {
            listIndexSubMenuDialog?.dismiss()
        }
    }

    private fun setListView(
        editFragment: EditFragment,
        anchorView: View?,
        jsActionMap: Map<String, String>,
        parentMenuName: String,
    ) {
        val context = editFragment.context
            ?: return
        val subMenuListView =
            listIndexSubMenuDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            )
        val subMenuPairList = MenuSettingTool.createSubMenuListMap(
            ListIndexArgsMaker.makeListIndexClickMenuPairList(
                editFragment,
                jsActionMap
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
            anchorView,
            jsActionMap,
            subMenuListView,
        )
    }

    private fun subMenuItemClickListener(
        editFragment: EditFragment,
        anchorView: View?,
        jsActionMap: Map<String, String>,
        subMenuListView: ListView?,
    ) {
        subMenuListView?.setOnItemClickListener { parent, view, position, id ->
            listIndexSubMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val clickedSubMenuName = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            val updateJsActionMap = EditSettingJsTool.makeJsActionMap(
                editFragment,
                MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
                    ListIndexArgsMaker.makeListIndexClickMenuPairList(
                        editFragment,
                        jsActionMap
                    ),
                    clickedSubMenuName
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