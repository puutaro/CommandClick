package com.puutaro.commandclick.util.map

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.ReadText
import java.io.File


object FannelSettingMap {

    private val fannelSettingMapTsvName = "settingMap.tsv"
    val fannelSettingMapTsvPath = File(
        UsePath.cmdclickFannelSystemDirPath,
        fannelSettingMapTsvName
    ).absolutePath

    enum class FannelHistorySettingKey(val key: String){
        ENABLE_LONG_PRESS_BUTTON("enableLongPressButton"),
        ENABLE_EDIT_EXECUTE("enableEditExecute"),
        ENABLE_EDIT_SETTING_VALS("enableEditSettingVals"),
        TITLE("title"),
    }
    const val keySeparator = ','
    const val switchOn = "ON"

    fun create(): Map<String, Map<String, String>> {
        return ReadText(fannelSettingMapTsvPath).textToList().map {
                fannelAdapterInfoLine ->
            val fanneNameAndMapCon = fannelAdapterInfoLine.split("\t")
            val fannelName = fanneNameAndMapCon.firstOrNull() ?: String()
            val adapterInfoMapCon = fanneNameAndMapCon.getOrNull(1)
            fannelName to CmdClickMap.createMap(
                adapterInfoMapCon,
                keySeparator
            ).toMap()
        }.toMap()
    }
}