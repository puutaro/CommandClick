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
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig

object ListIndexSubMenuDialog {

    private var listIndexSubMenuDialog: Dialog? = null

    fun launch(
        listIndexArgsMaker: ListIndexArgsMaker,
        selectedItem: String,
        parentMenuName: String,
    ){
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
            ?: return
        listIndexSubMenuDialog = Dialog(
            context
        )

        listIndexSubMenuDialog?.setContentView(
            R.layout.list_dialog_layout
        )
        val title = "${selectedItem}:${parentMenuName}"
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
            listIndexArgsMaker,
            parentMenuName,
            selectedItem,
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
        }
        listIndexSubMenuDialog?.setOnCancelListener {
            listIndexSubMenuDialog?.dismiss()
        }
    }

    private fun setListView(
        listIndexArgsMaker: ListIndexArgsMaker,
        parentMenuName: String,
        selectedItem: String,
    ) {
        val editFragment = listIndexArgsMaker.editFragment
        val context = editFragment.context
            ?: return
        val subMenuListView =
            listIndexSubMenuDialog?.findViewById<ListView>(
                R.id.list_dialog_list_view
            )
        val subMenuPairList = createPopupSubMenuListMap(
            listIndexArgsMaker,
            parentMenuName,
        )
        val subMenuAdapter = SubMenuAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            listIndexArgsMaker,
            subMenuListView,
            selectedItem
        )
    }

    private fun subMenuItemClickListener(
        listIndexArgsMaker: ListIndexArgsMaker,
        subMenuListView: ListView?,
        selectedItem: String,
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            listIndexSubMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val clickedSubMenuName = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener

            val extraMapForJsPath = ExtraMapToolForListIndex.createExtraMapFromSettingMenu(
                listIndexArgsMaker,
                clickedSubMenuName
            )
            val jsPathMacroStr =
                listIndexArgsMaker.extractJsPathMacroFromSettingMenu(
                    clickedSubMenuName
                )
            JsPathHandlerForListIndex.handle(
                listIndexArgsMaker,
                extraMapForJsPath,
                jsPathMacroStr,
                selectedItem
            )
        }
    }

    private fun createPopupSubMenuListMap(
        listIndexArgsMaker: ListIndexArgsMaker,
        parentMenuName: String,
    ): List<Pair<String, Int>>{
        val settingButtonMenuMapList = listIndexArgsMaker.listIndexClickMenuMapList
        val parentMenuKey = ListIndexEditConfig.ListIndexMenuMapKey.PARENT_NAME.str
        return settingButtonMenuMapList.filter {
            it?.get(parentMenuKey) == parentMenuName
        }.let {
            listIndexArgsMaker.execCreateMenuListMap(
                it
            )
        }
    }

}