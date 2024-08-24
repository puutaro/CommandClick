package com.puutaro.commandclick.proccess.pin

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText

object PinFannelManager {

    private val pinFannelTsvPath = UsePath.pinFannelTsvPath

    fun add(
        fannelName: String
    ){
        val pinFannelList =
            ReadText(pinFannelTsvPath).textToList()
        val updatePinList =
            listOf(fannelName) + pinFannelList
        FileSystems.writeFile(
            pinFannelTsvPath,
            updatePinList.joinToString("\n")
        )
    }

    fun remove(
        fannelName: String
    ){
        val pinFannelList =
            ReadText(pinFannelTsvPath).textToList()
        val removePinFannelList =
            pinFannelList.filter {
                it != fannelName
            }.joinToString("\n")
        FileSystems.writeFile(
            pinFannelTsvPath,
            removePinFannelList
        )
    }
}