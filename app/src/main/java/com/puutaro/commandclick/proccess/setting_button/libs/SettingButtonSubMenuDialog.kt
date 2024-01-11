package com.puutaro.commandclick.proccess.setting_button.libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.proccess.setting_button.SettingButtonMenuMapKey

object SettingButtonSubMenuDialog {

    private var settingButtonSubMenuDialog: Dialog? = null

    fun launch(
        settingButtonArgsMaker: SettingButtonArgsMaker,
        parentMenuName: String,
    ){
        val fragment = settingButtonArgsMaker.fragment
        val context = fragment.context
            ?: return
        settingButtonSubMenuDialog = Dialog(
            context
        )
        settingButtonSubMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            settingButtonArgsMaker,
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
        settingButtonArgsMaker: SettingButtonArgsMaker,
        parentMenuName: String,
    ) {
        val fragment = settingButtonArgsMaker.fragment
        val context = fragment.context
            ?: return
        val subMenuListView =
            settingButtonSubMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = createPopupSubMenuListMap(
            settingButtonArgsMaker,
            parentMenuName,
        )
        val subMenuAdapter = SubMenuAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            settingButtonArgsMaker,
            subMenuListView,
        )
    }

    private fun subMenuItemClickListener(
        settingButtonArgsMaker: SettingButtonArgsMaker,
        subMenuListView: ListView?,
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            settingButtonSubMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val clickedSubMenu = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            JsPathHandler.handle(
                settingButtonArgsMaker,
                clickedSubMenu,
            )
        }
    }

    private fun createPopupSubMenuListMap(
        settingButtonArgsMaker: SettingButtonArgsMaker,
        parentMenuName: String,
    ): List<Pair<String, Int>>{
        val settingButtonMenuMapList = settingButtonArgsMaker.makeSettingButtonMenuMapList()
        val parentMenuKey = SettingButtonMenuMapKey.PARENT_NAME.str
        return settingButtonMenuMapList.filter {
            it?.get(parentMenuKey) == parentMenuName
        }.let {
            settingButtonArgsMaker.execCreateMenuListMap(
                it
            )
        }
    }
}
