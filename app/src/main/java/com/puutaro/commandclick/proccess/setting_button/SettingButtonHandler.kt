package com.puutaro.commandclick.proccess.setting_button

import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.proccess.setting_button.libs.FileGetterForSettingButton
import com.puutaro.commandclick.proccess.setting_button.libs.JsPathHandler
import com.puutaro.commandclick.proccess.setting_button.libs.SettingButtonArgsMaker
import com.puutaro.commandclick.util.map.CmdClickMap

class SettingButtonHandler(
    private val fragment: Fragment,
    private val readSharePreffernceMap: Map<String, String>,
) {
    private val fileGetterForSettingButton = FileGetterForSettingButton(fragment)

    companion object {
        fun setIcon(
            fragment: Fragment,
            readSharePreffernceMap: Map<String, String>,
            settingButtonView: ImageButton?,
            isSet: Boolean,
        ) {
            if(!isSet) return
            val settingButtonArgsMaker = SettingButtonArgsMaker(
                fragment,
                readSharePreffernceMap,
                settingButtonView,
                FileGetterForSettingButton(fragment),
                false
            )
            val settingButtonConfigMap = settingButtonArgsMaker.settingButtonConfigMap
            val iconName = settingButtonConfigMap?.get(SettingButtonConfigMapKey.ICON.str)
                ?: CmdClickIcons.SETTING.str
            val iconId = CmdClickIcons.values().find {
                it.str == iconName
            }?.id ?: CmdClickIcons.SETTING.id
            settingButtonView?.setImageResource(
                iconId
            )
        }
    }
    fun handle(
        isLongClick: Boolean,
        settingButtonView: ImageButton?,
    ){
        val settingButtonArgsMaker = SettingButtonArgsMaker(
            fragment,
            readSharePreffernceMap,
            settingButtonView,
            fileGetterForSettingButton,
            isLongClick
        )
       val jsPathMacroStr = makeJsPathMacroStr(
           settingButtonArgsMaker,
           isLongClick,
       )
        if(
            jsPathMacroStr.isEmpty()
        ) return
        val settingButtonConfigMapList = settingButtonArgsMaker.makeSettingButtonConfigMapList(jsPathMacroStr)
        JsPathHandler.handle(
            settingButtonArgsMaker,
            jsPathMacroStr,
            settingButtonConfigMapList = settingButtonConfigMapList
        )
    }

    private fun makeJsPathMacroStr(
        settingButtonArgsMaker: SettingButtonArgsMaker,
        isLongClick: Boolean,
    ): String {
        return when(
            isLongClick
        ){
            true -> execMakeJsPathMacro(
                settingButtonArgsMaker,
                SettingButtonConfigMapKey.LONG_CLICK.str,
                JsPathMacroForSettingButton.MENU.name,
            )
            else -> execMakeJsPathMacro(
                settingButtonArgsMaker,
                SettingButtonConfigMapKey.CLICK.str,
                JsPathMacroForSettingButton.SIZING.name,
            )
        }
    }

    private fun execMakeJsPathMacro(
        settingButtonArgsMaker: SettingButtonArgsMaker,
        settingButtonMapKey: String,
        defaultButtonMacroStr: String,
    ): String {
        return settingButtonArgsMaker.settingButtonConfigMap?.get(settingButtonMapKey).let {
                clickConfigMapStr ->
            if(
                clickConfigMapStr == null
            ) return@let defaultButtonMacroStr
            if(
                clickConfigMapStr.isEmpty()
            ) return@let String()
            val clickJsPathMap = CmdClickMap.createMap(clickConfigMapStr, "|").toMap()
            val clickJsMacroStr = clickJsPathMap.get(SettingButtonClickConfigMapKey.JS_PATH.str)
            JsPathMacroForSettingButton.values().filter {
                it.name == clickJsMacroStr
            }.firstOrNull()?.name ?: defaultButtonMacroStr
        }
    }
}

enum class SettingButtonMenuMapKey(
    val str: String,
) {
    NAME("name"),
    ICON("icon"),
    JS_PATH("jsPath"),
    PARENT_NAME("parentName"),
    EXTRA("extra"),
}

enum class SettingButtonMenuExtraKey(
    val str: String
) {
    PARENT_DIR_PATH("parentDirPath"),
    COMP_PREFIX("compPrefix"),
    COMP_SUFFIX("compSuffix"),
    BROADCAST_ACTION("broadcastAction"),
    BROADCAST_SCHEMAS("broadcastSchemas"),
}


enum class SettingButtonConfigMapKey(
    val str: String,
) {
    LONG_CLICK("longClick"),
    CLICK("click"),
    ICON("icon"),
}

enum class SettingButtonClickConfigMapKey(
    val str: String
){
    JS_PATH("jsPath"),
    MENU_PATH("menuPath"),
    ON_HIDE_FOOTER("onHideFooter"),
}

enum class JsPathMacroForSettingButton{
    KILL,
    USAGE,
    NO_SCROLL_SAVE_URL,
    QR_SCAN,
    SHORTCUT,
    TERMUX_SETUP,
    CONFIG,
    REFRESH_MONITOR,
    SELECT_MONITOR,
    RESTART_UBUNTU,
    INSTALL_FANNEL,
    EDIT_STARTUP,
    JS_IMPORT,
    ADD,
    ADD_APP_DIR,
    APP_DIR_MANAGER,
    SIZING,
    MENU,
    SYNC,
    GET_FILE,
    GET_QR_CON,
    FANNEL_REPO_SYNC
}
