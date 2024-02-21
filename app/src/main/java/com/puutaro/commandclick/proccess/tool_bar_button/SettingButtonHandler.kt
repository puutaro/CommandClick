package com.puutaro.commandclick.proccess.tool_bar_button

import android.widget.ImageButton
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.proccess.lib.ExecSetTermSizeForIntent
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ClickSettingsForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsActionCompleterForToolbar
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.ToolbarButtonArgsMaker
import com.puutaro.commandclick.util.map.CmdClickMap

class SettingButtonHandler(
    private val editFragment: EditFragment,
) {

    fun handle(
        isLongClick: Boolean,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        settingButtonView: ImageButton?,
    ){
        val toolbarButtonConfigMap =
            editFragment.toolbarButtonConfigMap?.get(
                toolbarButtonBariantForEdit
            ) ?: mapOf()
        val toolbarButtonArgsMaker = ToolbarButtonArgsMaker(
            editFragment,
            toolbarButtonBariantForEdit,
            settingButtonView,
            isLongClick,
        )

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
            toolbarButtonArgsMaker,
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
        val isScriptSave = toolbarButtonArgsMaker
            .toolbarButtonConfigMap
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
                    ClickSettingsForToolbarButton.ClickConfigMapKey.ON_SCRIPT_SAVE.key
                )
                if(
                    onScriptStr.isNullOrEmpty()
                ) return@let ClickSettingsForToolbarButton.OnScriptSave.OFF.name
                onScriptStr
            } == ClickSettingsForToolbarButton.OnScriptSave.ON.name
        if (!isScriptSave) return
        ScriptFileSaver(
            editFragment,
        ).save()
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
        val monitorSizeStr = toolbarButtonArgsMaker
            .toolbarButtonConfigMap
            ?.get(buttonClickMapKey)
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
    VISIBLE("visible"),
    ON_FOCUS("onFocus")
}
