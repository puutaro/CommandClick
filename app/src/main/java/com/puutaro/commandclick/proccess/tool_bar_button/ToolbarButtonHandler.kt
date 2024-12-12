package com.puutaro.commandclick.proccess.tool_bar_button

import android.widget.FrameLayout
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.edit.lib.ListContentsSaverByTag
import com.puutaro.commandclick.proccess.lib.ExecSetTermSizeForIntent
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ClickSettingsForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsActionCompleterForToolbar
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ToolbarButtonArgsMaker
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.map.CmdClickMap

object ToolbarButtonHandler{

    fun handle(
        editFragment: EditFragment,
        isLongClick: Boolean,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        buttonLayout: FrameLayout?,
//        settingButtonView: ImageButton?,
    ){
        val toolbarButtonConfigMap =
            editFragment.toolbarButtonConfigMap?.get(
                toolbarButtonBariantForEdit
            ) ?: mapOf()
        ListContentsSaverByTag.save(
            editFragment,
            listOf(toolbarButtonBariantForEdit.str)
        )
        val toolbarButtonArgsMaker = ToolbarButtonArgsMaker(
            toolbarButtonBariantForEdit,
            isLongClick,
        )
        Keyboard.hiddenKeyboardForFragment(editFragment)
        MonitorSizing.handle(
            editFragment,
            toolbarButtonArgsMaker,
            isLongClick
        )
        ScriptSaver.handle(
            editFragment,
            toolbarButtonArgsMaker,
            isLongClick
        )
        val jsActionMap =
            JsActionCompleterForToolbar.makeJsActionMap(
                editFragment,
                toolbarButtonConfigMap,
                toolbarButtonBariantForEdit,
                isLongClick,
            )
        JsPathHandlerForToolbarButton.handle(
            editFragment,
            String(),
            buttonLayout,
//            settingButtonView,
            jsActionMap
        )
    }
}

private object ScriptSaver{
    fun handle(
        editFragment: EditFragment,
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        isLongClick: Boolean
    ) {
        when(
            isLongClick
        ){
            true -> execOnScriptSave(
                editFragment,
                toolbarButtonArgsMaker,
                SettingButtonConfigMapKey.LONG_CLICK.key,
                ToolbarButtonArgsMaker.onSaveDefaultMapInLongClick.get(
                    toolbarButtonArgsMaker.toolbarButtonBariantForEdit
                ) ?: String(),
            )
            else -> execOnScriptSave(
                editFragment,
                toolbarButtonArgsMaker,
                SettingButtonConfigMapKey.CLICK.key,
                ToolbarButtonArgsMaker.onSaveDefaultMapInClick.get(
                    toolbarButtonArgsMaker.toolbarButtonBariantForEdit
                ) ?: String(),
            )
        }
    }

    private fun execOnScriptSave(
        editFragment: EditFragment,
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        buttonClickMapKey: String,
        defaultOnSaveValue: String,
    ) {
        val isScriptSave =
            editFragment.toolbarButtonConfigMap
                ?.get(toolbarButtonArgsMaker.toolbarButtonBariantForEdit)
                ?.get(buttonClickMapKey)
                .let { clickConfigMapStr ->
                    if (
                        clickConfigMapStr == null
                    ) return@let defaultOnSaveValue
                    if (
                        clickConfigMapStr.isEmpty()
                    ) return@let String()
                    val clickJsPathMap =
                        CmdClickMap.createMap(
                            clickConfigMapStr, '|'
                        ).toMap()
                    val onScriptStr = clickJsPathMap.get(
                        ClickSettingsForToolbarButton.ClickConfigMapKey.ON_SAVE.key
                    )
                    if(
                        onScriptStr.isNullOrEmpty()
                    ) return@let ClickSettingsForToolbarButton.OnScriptSave.OFF.name
                    onScriptStr
                } == ClickSettingsForToolbarButton.OnScriptSave.ON.name
        if (!isScriptSave) return
        val editConstraintListAdapter =
            editFragment.binding.editListRecyclerView.adapter as EditConstraintListAdapter
        editConstraintListAdapter.saveFannelCon()
//        ScriptFileSaver.save(editFragment)
    }
}

private object MonitorSizing{
    fun handle(
        editFragment: EditFragment,
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        isLongClick: Boolean
    ) {
        when(
            isLongClick
        ){
            true -> execMonitorSizing(
                editFragment,
                toolbarButtonArgsMaker,
                SettingButtonConfigMapKey.LONG_CLICK.key
            )
            else -> execMonitorSizing(
                editFragment,
                toolbarButtonArgsMaker,
                SettingButtonConfigMapKey.CLICK.key
            )
        }
    }

    private fun execMonitorSizing(
        editFragment: EditFragment,
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        buttonClickMapKey: String,
    ) {
        val monitorSizeStr = editFragment.toolbarButtonConfigMap?.get(
            toolbarButtonArgsMaker.toolbarButtonBariantForEdit
        )?.get(buttonClickMapKey)
            .let { clickConfigMapStr ->
                if (
                    clickConfigMapStr == null
                ) return@let String()
                if (
                    clickConfigMapStr.isEmpty()
                ) return@let String()
                val clickJsPathMap =
                    CmdClickMap.createMap(
                        clickConfigMapStr, '|'
                    ).toMap()
                clickJsPathMap.get(
                    ClickSettingsForToolbarButton.ClickConfigMapKey.MONITOR_SIZE.key
                ) ?: String()
            }
        ExecSetTermSizeForIntent.execSetTermSizeForIntent(
            editFragment,
            monitorSizeStr
        )
    }
}


enum class SettingButtonConfigMapKey(
    val key: String,
) {
    LONG_CLICK("longClick"),
    CLICK("click"),
    ICON("icon"),
    CAPTTION("caption"),
    VISIBLE("visible"),
    DISABLE("disable"),
    COLOR("color")
}
