package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import com.puutaro.commandclick.util.file.ReadText

object CurrentFannelConListMaker {
    fun make(
        currentFannelPath: String,
        mainFannelConList: List<String>,
        settingFannelPath: String,
    ): List<String> {

        val isMainFannelSettingConList =
            currentFannelPath == settingFannelPath
        return when(
            isMainFannelSettingConList
        ) {
            true -> mainFannelConList
            else -> ReadText(settingFannelPath).textToList()
        }
    }
}