package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.util.str.ImageVarMarkTool
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.runBlocking

object ImageActionReturnErrManager {

    private val mainKeySeparator = ImageActionKeyManager.mainKeySeparator
    private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
    private val runAsyncPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
    private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
    private val imageReturnKey =
        ImageActionKeyManager.ImageActionsKey.IMAGE_RETURN.key

    fun isReturnBitmapNullResultErr(
        context: Context?,
        varName: String?,
        topAcVarName: String?,
        returnBitmap: Bitmap?,
        settingSubKey: ImageActionKeyManager.ImageSubKey,
        keyToSubKeyConWhere: String,
    ): Boolean {
        if(
            returnBitmap != null
        ) return false
        if(
            topAcVarName.isNullOrEmpty()
            || topAcVarName.startsWith(escapeRunPrefix)
        ){
            return false
        }
        val spanVarName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                varName.toString()
            )
        val spanSettingReturnKey =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                imageReturnKey
            )
        val spanSubKeyName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                settingSubKey.key
            )
        val spanTopAcVarName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                topAcVarName
            )
        val spanEscapeRunPrefix =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                escapeRunPrefix
            )
        runBlocking {
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_RETURN,
                "When topAcVar(${spanTopAcVarName}) don't have ${spanEscapeRunPrefix} prefix, ${spanSettingReturnKey} value must be exist: bitmapVarName: ${spanVarName}, setting sub key: ${spanSubKeyName}",
                keyToSubKeyConWhere,
            )
        }
        return true

    }

    fun isBlankReturnErrWithoutRunPrefix(
        context: Context?,
        returnKeyToVarNameList: List<Pair<String, String>>,
        keyToSubKeyConList: List<Pair<String, String>>,
        keyToSubKeyConWhere: String,
        topAcVarName: String?,
    ): Boolean {
        if(
            topAcVarName.isNullOrEmpty()
            || topAcVarName.startsWith(escapeRunPrefix)
        ) return false
        if(
            returnKeyToVarNameList.isEmpty()
        ) return false
        val errReturnKeyToVarName = returnKeyToVarNameList.firstOrNull {
                returnKeyToVarName ->
            returnKeyToVarName.second.isEmpty()
        } ?: return false
        val spanBlankSettingKeyName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                errReturnKeyToVarName.first
            )
        val spanEscapeRunPrefix =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                escapeRunPrefix
            )
        runBlocking {
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_VAR,
                "${spanBlankSettingKeyName} must not blank in ${spanEscapeRunPrefix}",
                keyToSubKeyConWhere,
            )
        }
        return true
    }

//            fun isAsyncVarOrRunPrefixVarSpecifyErr(
//                context: Context?,
//                settingKeyToVarNameList: List<Pair<String, String>>,
//                keyToSubKeyConWhere: String,
//            ): Boolean {
//                val runPrefixVarRegex =
//                    Regex("[$][{][${escapeRunPrefix}][a-zA-Z0-9_]*[}]")
//                val asyncVarRegex = Regex("[$][{][${asyncPrefix}][a-zA-Z0-9_]*[}]")
//                settingKeyToVarNameList.forEach {
//                        settingKeyToValueStr ->
//                    val settingKey = settingKeyToValueStr.first
//                    if(
//                        settingKey != settingReturnKey
//                    ) return@forEach
//                    val valueStr = settingKeyToValueStr.second
//                    if(
//                        !runPrefixVarRegex.matches(valueStr)
//                        && !asyncVarRegex.matches(valueStr)
//                    ) return@forEach
//                    val spanSettingKey =
//                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.errRedCode,
//                            settingKey
//                        )
//                    val spanValueStr =
//                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.errRedCode,
//                            valueStr
//                        )
//                    val spanEscapeRunPrefix =
//                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.ligthBlue,
//                            escapeRunPrefix
//                        )
//                    val spanAsyncPrefix =
//                        CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.ligthBlue,
//                            asyncPrefix
//                        )
//                    runBlocking {
//                        ImageActionErrLogger.sendErrLog(
//                            context,
//                            ImageActionErrLogger.SettingActionErrType.S_VAR,
//                            "${spanSettingKey} key must not use ${spanEscapeRunPrefix} and ${spanAsyncPrefix} prefix: ${spanValueStr}",
//                            keyToSubKeyConWhere,
//                        )
//                    }
//                    return true
//                }
//                return false
//            }

//            fun isRunOrAsyncDefinitionErrInReturn(
//                context: Context?,
//                returnKeyToVarNameList: List<Pair<String, String>>,
//                keyToSubKeyConList: List<Pair<String, String>>,
//                keyToSubKeyConWhere: String,
//                topAcVarName: String?,
//            ): Boolean {
//                if(
//                    topAcVarName.isNullOrEmpty()
//                    || topAcVarName.startsWith(escapeRunPrefix)
//                ) return false
//                if(
//                    returnKeyToVarNameList.isEmpty()
//                ) return false
//                val errKeyToVarName = returnKeyToVarNameList.firstOrNull {
//                        returnKeyToVarName ->
//                    val varName = returnKeyToVarName.second
//                    varName.startsWith(escapeRunPrefix)
//                            || varName.startsWith(asyncPrefix)
//                            || varName.startsWith(runAsyncPrefix)
//                } ?: return false
//                val spanBlankSettingKeyName =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        errKeyToVarName.first
//                    )
//                val spanVarName =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        errKeyToVarName.second
//                    )
//                val spanEscapeRunPrefix =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.ligthBlue,
//                        escapeRunPrefix
//                    )
//                val spanAsyncPrefix =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        asyncPrefix
//                    )
//                val spanRunAsyncPrefix =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        runAsyncPrefix
//                    )
//                runBlocking {
//                    ImageActionErrLogger.sendErrLog(
//                        context,
//                        ImageActionErrLogger.SettingActionErrType.S_VAR,
//                        "${spanBlankSettingKeyName} must not ${spanEscapeRunPrefix}, ${spanAsyncPrefix}, and ${spanRunAsyncPrefix}: ${spanVarName}",
//                        keyToSubKeyConWhere,
//                    )
//                }
//                return true
//            }

    fun isNotBeforeDefinitionInReturnErr(
        context: Context?,
        settingKeyToVarNameList: List<Pair<String, String>>,
        bitmapVarKeyList: List<String>?,
        keyToSubKeyConWhere: String,
    ): Boolean {
//        val varMarkRegex = Regex("#[{][a-zA-Z0-9_]+[}]")
//        val regexStrTemplate = "(#[{]%s[}])"
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultSDebugAppDirPath, "lisNotBeforeDefinitionInReturnErr.txt").absolutePath,
//                    listOf(
//                        "settingKeyToVarNameList: ${settingKeyToVarNameList}",
//                    ).joinToString("\n\n") + "\n\n==========\n\n"
//                )
        settingKeyToVarNameList.forEachIndexed { index, settingKeyToVarName ->
            val settingKey = settingKeyToVarName.first
            if(
                settingKey != imageReturnKey
            ) return@forEachIndexed
            val alreadyVarNameList = let {
                settingKeyToVarNameList.asSequence().filterIndexed { innerIndex, innerSettingKeyToVarName ->
                    innerSettingKeyToVarName.first != imageReturnKey
                            && innerIndex < index
                }.map { innerSettingKeyToVarName ->
                    innerSettingKeyToVarName.second
                } + (bitmapVarKeyList ?: emptyList())
            }.sorted().distinct().toList()
            val returnVarName = settingKeyToVarName.second
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultSDebugAppDirPath, "lisNotBeforeDefinitionInReturnErr_for.txt").absolutePath,
//                        listOf(
//                            "settingKeyToVarNameList: ${settingKeyToVarNameList}",
//                            "returnVarName:${returnVarName}",
//                            "alreadyVarNameList:${alreadyVarNameList}",
//                        ).joinToString("\n\n") + "\n\n==========\n\n"
//                    )
            if(
                !ImageVarMarkTool.matchBitmapVarName(returnVarName)
//                !varMarkRegex.containsMatchIn(returnVarName)
            ) return@forEachIndexed
//            val alreadyVarNameRegex = alreadyVarNameList.map {
//                regexStrTemplate.format(it)
//            }.joinToString("|").toRegex()
            if(
                alreadyVarNameList.isNotEmpty()
                ||  alreadyVarNameList.contains(
                    ImageVarMarkTool.convertBitmapKey(returnVarName)
                )
//                && alreadyVarNameRegex.containsMatchIn(returnVarName)
            ) return@forEachIndexed
            val spanImageReturnKey =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.lightBlue,
                    imageReturnKey
                )
            val spanReturnVarName =
                CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    returnVarName
                )
            runBlocking {
                ImageActionErrLogger.sendErrLog(
                    context,
                    ImageActionErrLogger.ImageActionErrType.I_VAR,
                    "Not before definition ${spanImageReturnKey} var: ${spanReturnVarName}",
                    keyToSubKeyConWhere,
                )
            }
            return true
        }
        return false
    }

    fun makeReturnKeyToBitmapVarMarkList(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, String>> {
        val defaultReturnPair = String() to String()
        val subKeySeparator = ImageActionKeyManager.subKeySepartor
        return keyToSubKeyConList.asSequence().map {
                keyToSubKeyCon ->
            val settingKey = keyToSubKeyCon.first
            if(
                settingKey != imageReturnKey
            ) return@map defaultReturnPair
            val bitmapVarMark = keyToSubKeyCon.second
                .split(subKeySeparator)
                .firstOrNull()?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: return@map defaultReturnPair
            settingKey to bitmapVarMark
        }.filter {
            it.first.isNotEmpty()
        }.toList()
    }
}
