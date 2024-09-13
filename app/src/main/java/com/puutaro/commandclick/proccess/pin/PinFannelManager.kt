package com.puutaro.commandclick.proccess.pin

import android.content.Context
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import java.io.File

object PinFannelManager {

    private const val pinFannelTxtName = "pinFannel.txt"
    private val pinFannelTxtPath = "${UsePath.cmdclickFannelSystemDirPath}/${pinFannelTxtName}"
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
    enum class PinFannelKey(val key: String){
        FANNEL_NAME("fannelName"),
        ENABLE_SELECTION_TEXT("enableSelectionText")
    }

    private const val switchOn = "ON"

    private val pinInfoMapPath = "${UsePath.fannelSettingsDirPath}/pinInfoMap.txt"
    private const val pinInfoMapSeparator = ','

    private val firstPinFannelList = listOf(
        SystemFannel.textToSpeech,
        SystemFannel.cmdBookmaker,
    )

    fun extractPinFannelMapList(
        cmdindexSelectionSearchButton: CardView?,
    ): List<Map<String, String>> {
       return ReadText(pinFannelTxtPath).textToList().map {
           mapCon ->
           CmdClickMap.createMap(
               mapCon,
               pinInfoMapSeparator
           ).toMap()
       }.filter {
           map ->
           when(cmdindexSelectionSearchButton?.isVisible == true) {
               true -> map.get(PinFannelKey.ENABLE_SELECTION_TEXT.key) == switchOn
               else -> true
           }
       }
    }

    fun saveForPreInstall(
        context: Context?
    ){
        if(
            File(pinFannelTxtPath).isFile
        ) return
        val pinFannelInfoMapList = firstPinFannelList.map {
                fannelName ->
            makePinFannelInfoMap(
                context,
                fannelName,
            )
        }
        save(pinFannelInfoMapList)
    }
    fun save(
        pinFannelInfoMapList: List<Map<String, String>>
    ){
        val pinFannelInfoMapListCon = pinFannelInfoMapList.map {
                pinFannelInfoMap ->
            if(
                pinFannelInfoMap.get(PinFannelKey.FANNEL_NAME.key).isNullOrEmpty()
            ) return@map String()
            convertMapToCon(pinFannelInfoMap)
        }.filter { it.isNotEmpty() }.joinToString("\n")
        FileSystems.writeFile(
            pinFannelTxtPath,
            pinFannelInfoMapListCon
        )
    }

    fun add(
        context: Context?,
        fannelNameList: List<String>
    ){
        val addEntryPinInfoMapList = fannelNameList.map {
            fannelName ->
            makePinFannelInfoMap(
                context,
                fannelName,
            )
        }
        val pinFannelMapInfoList =
            ReadText(pinFannelTxtPath).textToList().map {
                val mapCon = it.trim()
                CmdClickMap.createMap(
                    mapCon,
                    pinInfoMapSeparator
                ).toMap()
            }
        val pinFannelKeyName = PinFannelKey.FANNEL_NAME.key
        val pinFannelMapInfoListByFilter = pinFannelMapInfoList.filter {
                pinFannelMapInfo ->
            val curPinFannelName =
                pinFannelMapInfo.get(pinFannelKeyName)
                    ?: return@filter false
            val isNotContain = !addEntryPinInfoMapList.any {
                addEntryPinInfoMap ->
                addEntryPinInfoMap.get(pinFannelKeyName) ==
                        curPinFannelName
            }
            isNotContain
        }
        val updatePinInfoMapList =
            pinFannelMapInfoListByFilter + addEntryPinInfoMapList
        val updatePinInfoMapConList = updatePinInfoMapList.map {
            convertMapToCon(it)
        }.joinToString("\n")
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "pin.txt").absolutePath,
            listOf(
                "addEntryPinInfoMapList: ${addEntryPinInfoMapList}",
                "pinFannelMapInfoList: ${pinFannelMapInfoList}",
                "pinFannelMapInfoListByFilter: ${pinFannelMapInfoListByFilter}",
                "updatePinInfoMapList: ${updatePinInfoMapList}",
                "updatePinInfoMapConList: ${updatePinInfoMapConList}",
            ).joinToString("\n")
        )
        FileSystems.writeFile(
            pinFannelTxtPath,
            updatePinInfoMapConList,
        )
    }

    fun remove(
        fannelName: String
    ){
        val pinFannelMapConList =
            ReadText(pinFannelTxtPath).textToList()
        val removedPinFannelMapList =
            pinFannelMapConList.filter {
                mapCon ->
                val pinInfoMap = CmdClickMap.createMap(
                    mapCon,
                    pinInfoMapSeparator
                ).toMap()
                pinInfoMap.get(PinFannelKey.FANNEL_NAME.key) != fannelName
            }.joinToString("\n")
        FileSystems.writeFile(
            pinFannelTxtPath,
            removedPinFannelMapList
        )
    }

    fun updateBroadcast(context: Context?){
        BroadcastSender.normalSend(
            context,
            BroadCastIntentSchemeTerm.FANNEL_PIN_BAR_UPDATE.action
        )
    }

    private fun convertMapToCon(
        pinInfoMap: Map<String, String>,
    ): String {
        return pinInfoMap.map {
            "${it.key}=${it.value}"
        }.joinToString(pinInfoMapSeparator.toString())
    }

    private fun makePinFannelInfoMap(
        context: Context?,
        fannelName: String
    ): Map<String, String> {
        val fannelPath =
            File(cmdclickDefaultAppDirPath, fannelName).absolutePath
        val fannelConList = ReadText(
            fannelPath
        ).textToList()
        val settingVariableListSrc =
            CommandClickVariables.extractValListFromHolder(
                fannelConList,
                CommandClickScriptVariable.SETTING_SEC_START,
                CommandClickScriptVariable.SETTING_SEC_END,
            ) ?: emptyList()

        val setReplaceVariables = SetReplaceVariabler.makeSetReplaceVariableMap(
            context,
            settingVariableListSrc,
            fannelPath
        )
        return ScriptPreWordReplacer.replace(
                pinInfoMapPath,
                fannelName,
            ).let {
                pinInfoMapPath ->
            val pinInfoMapCon = SettingFile.read(
                pinInfoMapPath,
                fannelPath,
                setReplaceVariables,
                false
            )
            val pinInfoMapSrc = CmdClickMap.createMap(
                pinInfoMapCon,
                pinInfoMapSeparator
            ).toMap()
            val pinFannelNameKey = PinFannelKey.FANNEL_NAME.key
            pinInfoMapSrc + mapOf(pinFannelNameKey to fannelName)
        }
    }
}