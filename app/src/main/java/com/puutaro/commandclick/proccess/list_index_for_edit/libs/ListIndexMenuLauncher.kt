package com.puutaro.commandclick.proccess.list_index_for_edit.libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionTool
import com.puutaro.commandclick.proccess.js_macro_libs.exec_handler.JsPathHandlerForQrAndListIndex
import com.puutaro.commandclick.proccess.js_macro_libs.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex


object ListIndexMenuLauncher {
    
    private var listIndexMenuDialog: Dialog? = null

    fun launch(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>?,
        selectedItemLineMap: Map<String, String>,
        position: Int,
    ){
        createMenuDialogForListIndex(
            editFragment,
            jsActionMap,
            selectedItemLineMap,
            position,
        )
    }

    private fun createMenuDialogForListIndex(
        editFragment: EditFragment,
        jsActionMap: Map<String, String>?,
        selectedItemLineMap: Map<String, String>,
        position: Int,
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
        setListView(
            editFragment,
            jsActionMap,
            selectedItemLineMap,
            position,
        )
        listIndexMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )?.text = selectedItemLineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
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
        editFragment: EditFragment,
        jsActionMap: Map<String, String>,
        selectedItemLineMap: Map<String, String>,
        position: Int,
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
        editFragment: EditFragment,
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
                editFragment,
                clickedMenuName,
                jsActionMap,
                selectedItemLineMap,
                listIndexPosition,
            )
        }
    }


    private fun jsPathOrSubMenuHandlerForListIndex(
        editFragment: EditFragment,
        clickedMenuName: String,
        jsActionMap: Map<String, String>,
        selectedItemLineMap: Map<String, String>,
        listIndexPosition: Int,
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
                ListIndexSubMenuDialog.launch(
                    editFragment,
                    jsActionMap,
                    selectedItemLineMap,
                    clickedMenuName,
                    listIndexPosition,
                )

            else -> {
                val fannelInfoMap =
                    editFragment.fannelInfoMap
                val updateJsActionMap = JsActionTool.makeJsActionMap(
                    editFragment,
                    fannelInfoMap,
                    MenuSettingTool.extractJsKeyToSubConByMenuNameFromMenuPairListList(
                        settingButtonMenuPairList,
                        clickedMenuName
                    ),
                    editFragment.setReplaceVariableMap,
                    String()
                )
                JsPathHandlerForQrAndListIndex.handle(
                    editFragment,
                    updateJsActionMap,
                    selectedItemLineMap,
                    listIndexPosition,
                )
            }
        }
    }
}