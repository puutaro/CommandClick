package com.puutaro.commandclick.proccess.pin

import android.content.Context
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object PinFannelManager {

    private const val pinFannelTsvName = "pinFannel.tsv"
    private val pinFannelTsvPath = "${UsePath.cmdclickFannelSystemDirPath}/${pinFannelTsvName}"

    private val firstPinFannelList = listOf(
        SystemFannel.textToSpeech,
        SystemFannel.cmdBookmaker,
    )

    fun get(
    ): List<String> {
       return ReadText(pinFannelTsvPath).textToList()
    }

    fun saveForPreInstall(){
        if(
            File(pinFannelTsvPath).isFile
        ) return
        save(firstPinFannelList)
    }
    fun save(
        pinFannelList: List<String>
    ){
        FileSystems.writeFile(
            pinFannelTsvPath,
            pinFannelList.joinToString("\n")
        )
    }

    fun add(
        fannelName: String
    ){
        val pinFannelList =
            ReadText(pinFannelTsvPath).textToList()
        val updatePinList =
            pinFannelList + listOf(fannelName)
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

    fun updateBroadcast(context: Context?){
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeTerm.FANNEL_PIN_BAR_UPDATE.action
        )
    }
}