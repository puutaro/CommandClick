package com.puutaro.commandclick.proccess.edit_list.libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.JsPathHandlerForQrAndEditList
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor

object ListIndexSubMenuDialog {

    private var listIndexSubMenuDialog: Dialog? = null

    fun launch(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        jsActionMap: Map<String,String>?,
        selectedItemLineMap: Map<String, String>,
        parentMenuName: String,
        position: Int,
    ){
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        val context = fragment.context
            ?: return
        listIndexSubMenuDialog = Dialog(
            context
        )

        listIndexSubMenuDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        val title = "${selectedItemLineMap}:${parentMenuName}"
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
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            editListRecyclerView,
            jsActionMap,
            parentMenuName,
            selectedItemLineMap,
            position,
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
    ){
        val cancelImageButton =
            listIndexSubMenuDialog?.findViewById<ImageButton>(
                R.id.list_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            listIndexSubMenuDialog?.dismiss()
            listIndexSubMenuDialog = null
        }
        listIndexSubMenuDialog?.setOnCancelListener {
            listIndexSubMenuDialog?.dismiss()
            listIndexSubMenuDialog = null
        }
    }

    private fun setListView(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        jsActionMap: Map<String, String>,
        parentMenuName: String,
        selectedItemLineMap: Map<String, String>,
        position: Int,
    ) {
        val context = fragment.context
            ?: return
        val subMenuListView =
            listIndexSubMenuDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            )
        val subMenuPairList = MenuSettingTool.createSubMenuListMap(
            ListIndexArgsMaker.makeListIndexClickMenuPairList(
                fragment,
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
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            editListRecyclerView,
            jsActionMap,
            subMenuListView,
            selectedItemLineMap,
            position,
        )
    }

    private fun subMenuItemClickListener(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        jsActionMap: Map<String, String>,
        subMenuListView: ListView?,
        selectedItemLineMap: Map<String, String>,
        listIndexPosition: Int,
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            listIndexSubMenuDialog?.dismiss()
            listIndexSubMenuDialog = null
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val clickedSubMenuName = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
//            val fannelInfoMap =
//                fragment.fannelInfoMap
            val updateJsActionMap = JsActionTool.makeJsActionMap(
                fragment,
                fannelInfoMap,
                MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
                    ListIndexArgsMaker.makeListIndexClickMenuPairList(
                        fragment,
                        jsActionMap
                    ),
                    clickedSubMenuName
                ),
                setReplaceVariableMap,
                String()
            )
            JsPathHandlerForQrAndEditList.handle(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editListRecyclerView,
                updateJsActionMap,
                selectedItemLineMap,
                listIndexPosition,
            )
        }
    }
}