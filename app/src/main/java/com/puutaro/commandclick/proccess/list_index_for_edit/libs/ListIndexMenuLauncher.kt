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
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.menu_tool.MenuSettingTool

object ListIndexMenuLauncher {
    
    private var listIndexMenuDialog: Dialog? = null

    fun launch(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context ?: return
        createMenuDialogForListIndex(
            listIndexArgsMaker,
            selectedItem,
        )
    }

    private fun createMenuDialogForListIndex(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
    ) {
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
            ?: return
        listIndexMenuDialog = Dialog(
            context
        )
        listIndexMenuDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        setListView(
            listIndexArgsMaker,
            selectedItem,
        )
        listIndexMenuDialog?.findViewById<AppCompatTextView>(
            R.id.list_dialog_title
        )?.text = selectedItem
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
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
    ) {
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
            ?: return
        val subMenuListView =
            listIndexMenuDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            )
        val menuPairList = MenuSettingTool.createListMenuListMap(
            listIndexArgsMaker.listIndexClickMenuMapList,
        )
        val subMenuAdapter = SubMenuAdapter(
            context,
            menuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            listIndexArgsMaker,
            subMenuListView,
            selectedItem,
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
        listIndexArgsMaker: ListIndexArgsMaker,
        subMenuListView: ListView?,
        selectedItem: String,
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            listIndexMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val clickedMenuName = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            jsPathOrSubMenuHandlerForListIndex(
                clickedMenuName,
                listIndexArgsMaker,
                selectedItem,

            )
        }
    }


    private fun jsPathOrSubMenuHandlerForListIndex(
        clickedMenuName: String,
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
    ) {
        val settingButtonMenuMapList = listIndexArgsMaker.listIndexClickMenuMapList
        val parentMenuNameKey = MenuSettingTool.MenuSettingKey.PARENT_NAME.key
        val onSubMenuLabel = !settingButtonMenuMapList.filter {
            it?.get(parentMenuNameKey) == clickedMenuName
        }.firstOrNull().isNullOrEmpty()
        when (onSubMenuLabel) {
            true ->
                ListIndexSubMenuDialog.launch(
                    listIndexArgsMaker,
                    selectedItem,
                    clickedMenuName,
                )

            else -> {
                val extraMapForJsPath = ExtraArgsTool.createExtraMapFromMenuMapList(
                    listIndexArgsMaker.listIndexClickMenuMapList,
                    clickedMenuName,
                    MenuSettingTool.MenuSettingKey.NAME.key,
                    "!"
                )
                val jsPathMacroStr =
                    listIndexArgsMaker.extractJsPathMacroFromSettingMenu(
                        clickedMenuName
                    )
                JsPathHandlerForListIndex.handle(
                    listIndexArgsMaker,
                    extraMapForJsPath,
                    jsPathMacroStr,
                    selectedItem,
                )
            }
        }
    }
}