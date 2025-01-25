package com.puutaro.commandclick.proccess.edit.setting_action.libs

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.runBlocking
import java.io.File

object IfErrManager {

    fun isIfHoldErr(
        context: Context?,
        keyToSubKeyConList: List<Pair<String, String>>?,
        ifKeyToIfEndKey: Pair<String, String>,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val ifStartKey = ifKeyToIfEndKey.first
        val ifEndKey = ifKeyToIfEndKey.second
        val ifHolderKeyRegex =
            Regex("[?](${ifStartKey}|${ifEndKey})=[^\n?|]+")
        val keyToSubKeyCon = keyToSubKeyConList?.map {
            (mainKey, subKeyCon) ->
            "|${mainKey}=${subKeyCon}"
        }?.joinToString("\n")?.let {
            QuoteTool.maskSurroundQuote(it)
        } ?: String()
        val ifHolderKeyList = ifHolderKeyRegex.findAll(keyToSubKeyCon).map {
            ifKeyEqualProcNameResult ->
            ifKeyEqualProcNameResult.value.removePrefix("?").let {
                ifKeyEqualProcName ->
                CcScript.makeKeyValuePairFromSeparatedString(
                    ifKeyEqualProcName,
                    "="
                )
            }
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "liferr.txt").absolutePath,
//            listOf(
//                "ifKeyToIfEndKey: ${ifKeyToIfEndKey}",
//                "ifHolderKeyList: ${ifHolderKeyList.joinToString(", ")}",
//            ).joinToString("\n\n")
//        )
        val ifStackList =
            mutableListOf<String>()
        ifHolderKeyList.forEach {
            (ifKey, procNameEntry) ->
            when(ifKey){
                ifStartKey -> {
                    val procName = makeIfProcNameNotExistInRuntime(
                        ifKey,
                        procNameEntry
                    ).let {
                            (procNameSrc, errMsg) ->
                        if(
                            errMsg == null
                        ) return@let procNameSrc
                        runBlocking {
                            SettingActionErrLogger.sendErrLog(
                                context,
                                SettingActionErrLogger.SettingActionErrType.S_IF,
                                errMsg,
                                keyToSubKeyConWhere,
                            )
                        }
                        return true
                    }
                    ifStackList.add(procName)
                }
                ifEndKey -> {
                    makeIfProcNameNotExistInRuntime(
                        ifKey,
                        procNameEntry
                    ).let {
                        (procName, errMsg) ->
                        if(
                            errMsg == null
                        ) return@let procName
                        runBlocking {
                            SettingActionErrLogger.sendErrLog(
                                context,
                                SettingActionErrLogger.SettingActionErrType.S_IF,
                                errMsg,
                                keyToSubKeyConWhere,
                            )
                        }
                        return true
                    }
                    val curIfProc = ifStackList.lastOrNull()
                    makeLastIfProcNotMatchErr(
                        ifKey,
                        curIfProc,
                        procNameEntry,
                    )?.let {
                       errMsg ->
                        runBlocking {
                            SettingActionErrLogger.sendErrLog(
                                context,
                                SettingActionErrLogger.SettingActionErrType.S_IF,
                                errMsg,
                                keyToSubKeyConWhere,
                            )
                        }
                        return true
                    }
                    if(
                        ifStackList.isNotEmpty()
                    ) {
                        ifStackList.removeAt(ifStackList.lastIndex)
                    }
                }
                else -> {}
            }
        }
        if(
            ifStackList.isNotEmpty()
        ) {
            val spanIfStartKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                ifStartKey,
            )
            val spanIfEndKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                ifEndKey,
            )
            val spanIfStackListCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                ifStackList.joinToString(","),
            )
            runBlocking {
                SettingActionErrLogger.sendErrLog(
                    context,
                    SettingActionErrLogger.SettingActionErrType.S_IF,
                    "${spanIfStartKey} and ${spanIfEndKey} num not match: ${spanIfStackListCon}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun makeIfProcNameNotExistInRuntime(
        curIfKeyName: String,
        ifProcName: String?
    ): Pair<String, String?> {
        if(
            !ifProcName.isNullOrEmpty()
        ) return ifProcName to null
        return runBlocking {
            val spanIfEndKeyName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                curIfKeyName,
            )
            String() to "${spanIfEndKeyName} con not exist"
        }
    }

    fun isMultipleSpecifyErr(
        context: Context?,
        ifMapListSize: Int,
        ifKey: String,
        keyToSubKeyConWhere: String,
    ): Boolean {
        if(
            ifMapListSize <= 1
        ) return false
        val spanIfProcName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            ifKey,
        )
        val spanReturnKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.lightBlue,
            SettingActionKeyManager.SettingActionsKey.SETTING_RETURN.key,
        )
        runBlocking {
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_IF,
                "In ${spanReturnKey}, ${spanIfProcName} key must not multiply specify",
                keyToSubKeyConWhere
            )
        }
        return true
    }



    private fun makeLastIfProcNotMatchErr(
        ifEndKeyName: String,
        ifProcName: String?,
        ifEndProcName: String?,
    ): String? {
        if(
            ifEndProcName.isNullOrEmpty()
            || ifEndProcName == ifProcName
            ) return null
        val spanIfEndKeyName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.lightBlue,
            ifEndKeyName,
        )
        val spanIfProcName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            ifProcName.toString(),
        )
        val spanIfEndProcName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errRedCode,
            ifEndProcName,
        )
        return "${spanIfEndKeyName} con is not match: if: ${spanIfProcName}, ifEnd: ${spanIfEndProcName}"
    }
}