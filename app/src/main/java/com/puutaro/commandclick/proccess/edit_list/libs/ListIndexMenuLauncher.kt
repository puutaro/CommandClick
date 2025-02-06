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
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor


object ListIndexMenuLauncher {
    
    private var listIndexMenuDialog: Dialog? = null

    fun launch(
        fragment: Fragment,
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        jsActionMap: Map<String, String>?,
        selectedItemLineMap: Map<String, String>,
        position: Int,
    ){
        createMenuDialogForListIndex(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            editListRecyclerView,
            jsActionMap,
            selectedItemLineMap,
            position,
        )
    }

    private fun createMenuDialogForListIndex(
        fragment: Fragment,
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        jsActionMap: Map<String, String>?,
        selectedItemLineMap: Map<String, String>,
        position: Int,
    ) {
        if(
            jsActionMap.isNullOrEmpty()
        ) return
        val context = fragment.context
            ?: return
        listIndexMenuDialog = Dialog(
            context
        )
        listIndexMenuDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        setListView(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            editListRecyclerView,
            jsActionMap,
            selectedItemLineMap,
            position,
        )
        listIndexMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )?.text = selectedItemLineMap.get(
            ListSettingsForEditList.MapListPathManager.Key.SRC_TITLE.key
        )
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
        fragment: Fragment,
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        jsActionMap: Map<String, String>,
        selectedItemLineMap: Map<String, String>,
        position: Int,
    ) {
        val context = fragment.context
            ?: return
        val subMenuListView =
            listIndexMenuDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            )
        val menuPairList = MenuSettingTool.createListMenuListMap(
            ListIndexArgsMaker.makeListIndexClickMenuPairList(
                fragment,
                jsActionMap
            ),
        )
        val subMenuAdapter = SubMenuAdapter(
            context,
            menuPairList.toMutableList()
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

    private fun setCancelListener(
    ){
        val cancelImageButton =
            listIndexMenuDialog?.findViewById<ImageButton>(
                R.id.list_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            listIndexMenuDialog?.dismiss()
            listIndexMenuDialog = null
        }
        listIndexMenuDialog?.setOnCancelListener {
            listIndexMenuDialog?.dismiss()
            listIndexMenuDialog = null
        }
    }

    private fun subMenuItemClickListener(
        fragment: Fragment,
        fannelInfoMap: HashMap<String, String>,
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
            listIndexMenuDialog?.dismiss()
            listIndexMenuDialog = null
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val clickedMenuName = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            jsPathOrSubMenuHandlerForListIndex(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                editListRecyclerView,
                clickedMenuName,
                jsActionMap,
                selectedItemLineMap,
                listIndexPosition,
            )
        }
    }


    private fun jsPathOrSubMenuHandlerForListIndex(
        fragment: Fragment,
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListRecyclerView: RecyclerView,
        clickedMenuName: String,
        jsActionMap: Map<String, String>,
        selectedItemLineMap: Map<String, String>,
        listIndexPosition: Int,
    ) {
        val settingButtonMenuPairList =
            ListIndexArgsMaker.makeListIndexClickMenuPairList(
                fragment,
                jsActionMap,
            )
        val hitMenuItemPairList = !MenuSettingTool.firstOrNullByParentMenuName(
            settingButtonMenuPairList,
            clickedMenuName
        ).isNullOrEmpty()
        when (hitMenuItemPairList) {
            true ->
                ListIndexSubMenuDialog.launch(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    editListRecyclerView,
                    jsActionMap,
                    selectedItemLineMap,
                    clickedMenuName,
                    listIndexPosition,
                )

            else -> {
//                val fannelInfoMap =
//                    fragment.fannelInfoMap
                val updateJsActionMap = JsActionTool.makeJsActionMap(
                    fragment,
                    fannelInfoMap,
                    MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
                        settingButtonMenuPairList,
                        clickedMenuName
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
}