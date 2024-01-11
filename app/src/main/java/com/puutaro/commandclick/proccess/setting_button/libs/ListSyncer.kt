package com.puutaro.commandclick.proccess.setting_button.libs

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.setting_button.JsPathMacroForSettingButton

object ListSyncer {
    fun sync(
        fragment: Fragment,
        settingMenuMapList: List<Map<String, String>?>,
    ){
        val context = fragment.context
            ?: return
        val extraMap = ExtraMapTool.createExtraMap(
            JsPathMacroForSettingButton.SYNC.name,
            settingMenuMapList
        )
        val broadcastIntent = ExtraMapTool.makeBroadcastIntent(extraMap)
        Toast.makeText(
            context,
            "sync ok",
            Toast.LENGTH_SHORT
        ).show()
        context.sendBroadcast(broadcastIntent)
    }
}