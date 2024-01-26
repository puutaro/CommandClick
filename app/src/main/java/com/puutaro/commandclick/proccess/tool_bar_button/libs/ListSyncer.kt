package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.tool_bar_button.common_settings.JsPathMacroForSettingButton

object ListSyncer {
    fun sync(
        fragment: Fragment,
        settingMenuMapList: List<Map<String, String>?>,
    ){
        val context = fragment.context
            ?: return
        val extraMap = ExtraArgsTool.createExtraMapFromMenuMapList(
            settingMenuMapList,
            JsPathMacroForSettingButton.SYNC.name,
            MenuSettingTool.MenuSettingKey.JS_PATH.key,
            "!",
        )
        val broadcastIntent = ExtraArgsTool.makeBroadcastIntent(
            extraMap,
            "&"
        )
        if(
            broadcastIntent?.action.isNullOrEmpty()
        ) {
            Toast.makeText(
                context,
                "broadcast intent action not found: ${extraMap}",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        Toast.makeText(
            context,
            "sync ok",
            Toast.LENGTH_SHORT
        ).show()
        context.sendBroadcast(broadcastIntent)
    }
}