package com.puutaro.commandclick.proccess.tool_bar_button

import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ScriptFileSaver
import com.puutaro.commandclick.proccess.lib.ExecSetTermSizeForIntent
import com.puutaro.commandclick.proccess.tool_bar_button.common_settings.JsPathMacroForSettingButton
import com.puutaro.commandclick.proccess.tool_bar_button.config_settings.ClickSettingsForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.libs.JsPathHandlerForToolbarButton
import com.puutaro.commandclick.proccess.tool_bar_button.libs.ToolbarButtonArgsMaker
import com.puutaro.commandclick.util.map.CmdClickMap

class SettingButtonHandler(
    private val fragment: Fragment,
) {

    fun handle(
        isLongClick: Boolean,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        settingButtonView: ImageButton?,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
    ){
        val toolbarButtonArgsMaker = ToolbarButtonArgsMaker(
            fragment,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
            toolbarButtonBariantForEdit,
            settingButtonView,
            isLongClick,
        )
        onMonitorSizingHandler(
            toolbarButtonArgsMaker,
            isLongClick
        )
        onScriptSaveHandler(
            toolbarButtonArgsMaker,
            isLongClick
        )

        val jsPathMacroStr = makeJsPathMacroStr(
           toolbarButtonArgsMaker,
           isLongClick,
        )
        if(
            jsPathMacroStr.isEmpty()
        ) return
        val settingButtonConfigMapList =
            toolbarButtonArgsMaker.makeSettingButtonConfigMapList(
                jsPathMacroStr
            )
        JsPathHandlerForToolbarButton.handle(
            toolbarButtonArgsMaker,
            jsPathMacroStr,
            settingButtonConfigMapList = settingButtonConfigMapList
        )
    }

    private fun makeJsPathMacroStr(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        isLongClick: Boolean,
    ): String {
        return when(
            isLongClick
        ){
            true -> execMakeJsPathMacro(
                toolbarButtonArgsMaker,
                SettingButtonConfigMapKey.LONG_CLICK.str,
                ToolbarButtonArgsMaker.defaultLongClickMacroMap.get(
                    toolbarButtonArgsMaker.toolbarButtonBariantForEdit
                ) ?: String(),
            )
            else -> execMakeJsPathMacro(
                toolbarButtonArgsMaker,
                SettingButtonConfigMapKey.CLICK.str,
                ToolbarButtonArgsMaker.defaultClickMacroMap.get(
                    toolbarButtonArgsMaker.toolbarButtonBariantForEdit
                ) ?: String(),
            )
        }
    }

    private fun onScriptSaveHandler(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        isLongClick: Boolean
    ) {
        if(
            fragment !is EditFragment
        ) return
        when(
            isLongClick
        ){
            true -> execOnScriptSave(
                fragment,
                toolbarButtonArgsMaker,
                SettingButtonConfigMapKey.LONG_CLICK.str,
                ToolbarButtonArgsMaker.onSaveDefaultMapInLongClick.get(
                    toolbarButtonArgsMaker.toolbarButtonBariantForEdit
                ) ?: String(),
            )
            else -> execOnScriptSave(
                fragment,
                toolbarButtonArgsMaker,
                SettingButtonConfigMapKey.CLICK.str,
                ToolbarButtonArgsMaker.onSaveDefaultMapInClick.get(
                    toolbarButtonArgsMaker.toolbarButtonBariantForEdit
                ) ?: String(),
            )
        }
    }

    private fun execMakeJsPathMacro(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        buttonClickMapKey: String,
        defaultButtonMacroStr: String,
    ): String {
        return toolbarButtonArgsMaker.settingButtonConfigMap
            ?.get(buttonClickMapKey).let {
                    clickConfigMapStr ->
                if(
                    clickConfigMapStr == null
                ) return@let defaultButtonMacroStr
                if(
                    clickConfigMapStr.isEmpty()
                ) return@let String()
                val clickJsPathMap =
                    CmdClickMap.createMap(clickConfigMapStr, "|").toMap()
                val clickJsMacroStr =
                    clickJsPathMap.get(ClickSettingsForToolbarButton.ClickConfigMapKey.JS_PATH.str)
                JsPathMacroForSettingButton.values().firstOrNull {
                    it.name == clickJsMacroStr
                }?.name.let name@ {
                    if(it != null) return@name it
                    if(
                        clickJsMacroStr.isNullOrEmpty()
                    ) return@name defaultButtonMacroStr
                    clickJsMacroStr
                }
            }
    }

    private fun execOnScriptSave(
        editFragment: EditFragment,
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        buttonClickMapKey: String,
        defaultOnSaveValue: String,
    ) {
         val isScriptSave = toolbarButtonArgsMaker
            .settingButtonConfigMap
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
                        clickConfigMapStr, "|"
                    ).toMap()
                val onScriptStr = clickJsPathMap.get(
                    ClickSettingsForToolbarButton.ClickConfigMapKey.ON_SCRIPT_SAVE.str
                )
                if(
                    onScriptStr.isNullOrEmpty()
                ) return@let ClickSettingsForToolbarButton.OnScriptSave.OFF.name
                onScriptStr
            } == ClickSettingsForToolbarButton.OnScriptSave.ON.name
        if (!isScriptSave) return
        ScriptFileSaver(
            editFragment,
        ).save(
            toolbarButtonArgsMaker.recordNumToMapNameValueInCommandHolder,
            toolbarButtonArgsMaker.recordNumToMapNameValueInSettingHolder,
        )
    }

    private fun onMonitorSizingHandler(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        isLongClick: Boolean
    ) {
        if(
            fragment !is EditFragment
        ) return
        when(
            isLongClick
        ){
            true -> execMonitorSizing(
                toolbarButtonArgsMaker,
                SettingButtonConfigMapKey.LONG_CLICK.str
            )
            else -> execMonitorSizing(
                toolbarButtonArgsMaker,
                SettingButtonConfigMapKey.CLICK.str
            )
        }
    }

    private fun execMonitorSizing(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
        buttonClickMapKey: String,
    ) {
        val monitorSizeStr = toolbarButtonArgsMaker
            .settingButtonConfigMap
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
                        clickConfigMapStr, "|"
                    ).toMap()
                clickJsPathMap.get(
                    ClickSettingsForToolbarButton.ClickConfigMapKey.MONITOR_SIZE.str
                ) ?: String()
            }
        ExecSetTermSizeForIntent.execSetTermSizeForIntent(
            fragment,
            monitorSizeStr
        )
    }
}


enum class SettingButtonConfigMapKey(
    val str: String,
) {
    LONG_CLICK("longClick"),
    CLICK("click"),
    ICON("icon"),
    VISIBLE("visible"),
}


