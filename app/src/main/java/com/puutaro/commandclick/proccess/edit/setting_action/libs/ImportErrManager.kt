package com.puutaro.commandclick.proccess.edit.setting_action.libs

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import kotlinx.coroutines.runBlocking
import java.io.File

object ImportErrManager {

    private val escapeRunPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
    private val runAsyncPrefix = SettingActionKeyManager.VarPrefix.RUN_ASYNC.prefix
    private val asyncPrefix = SettingActionKeyManager.VarPrefix.ASYNC.prefix
    private val sAcVarKeyName =
        SettingActionKeyManager.SettingActionsKey.SETTING_ACTION_VAR.key
    private val settingReturnKey =
        SettingActionKeyManager.SettingActionsKey.SETTING_RETURN.key

    fun isCircleImportErr(
        context: Context?,
        originImportPathList: List<String>?,
        importPath: String,
        keyToSubKeyConWhere: String,
    ): Boolean {
        if(
            originImportPathList.isNullOrEmpty()
        ) {
            return false
        }
        if(
            !originImportPathList.contains(importPath)
        ) return false
        val spanSAcVarKeyName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                sAcVarKeyName
            )
        val spanOriginImportPathListCon =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                originImportPathList.joinToString(",")
            )
        val spanImportPath =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                importPath
            )
        runBlocking {
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_AC_VAR,
                "must not circle in ${spanSAcVarKeyName}: importPath: ${spanImportPath}, originImportPathListCon: ${spanOriginImportPathListCon}",
                keyToSubKeyConWhere,
            )
        }
        return true
    }

    fun isNotExist(
        context: Context?,
        importPath: String,
        keyToSubKeyConWhere: String,
    ): Boolean {
        if(
            File(importPath).isFile
        ) {
            return false
        }
        val spanSAdVarKeyName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                sAcVarKeyName
            )
        val spanImportPath =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                importPath
            )
        runBlocking {
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_AC_VAR,
                "import path not exist in ${spanSAdVarKeyName}: ${spanImportPath}",
                keyToSubKeyConWhere,
            )
        }
        return true
    }

//    fun isImportShadowVarMarkErr(
//        context: Context?,
//        importPath: String,
//        importSrcConBeforeReplace: String,
//        importRepMap: Map<String, String>,
//        keyToSubKeyConWhere: String,
//    ): Boolean {
//        val importVarDollMarkList = importRepMap.filter {
//            SettingActionKeyManager.ValueStrVar.matchStringVarName(it.value)
//        }.map {
//            it.value
//        }
//        val importShadowVarDollMark = importVarDollMarkList.firstOrNull {
//            importSrcConBeforeReplace.contains(it)
//        }
////                FileSystems.updateFile(
////                    File(UsePath.cmdclickDefaultSDebugAppDirPath, "lisImportShadowVarMarkErr.txt").absolutePath,
////                    listOf(
////                        "importVarDollMarkList: ${importVarDollMarkList}",
////                        "importSrcConBeforeReplace: ${importSrcConBeforeReplace}",
////                    ).joinToString("\n\n") + "\n\n====\n\n"
////                )
//        if(
//            importShadowVarDollMark.isNullOrEmpty()
//        ) return false
//        val spanSAcVarKeyName =
//            CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                CheckTool.ligthBlue,
//                sAcVarKeyName
//            )
//        val spanImportShadowVar =
//            CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                CheckTool.ligthBlue,
//                importShadowVarDollMark
//            )
//        val spanImportPath =
//            CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                CheckTool.errRedCode,
//                importPath
//            )
//        runBlocking {
//            SettingActionErrLogger.sendErrLog(
//                context,
//                SettingActionErrLogger.SettingActionErrType.S_AC_VAR,
//                "must not shadow variable in ${spanSAcVarKeyName}: importPath: ${spanImportPath}, shadowVar: ${spanImportShadowVar}",
//                keyToSubKeyConWhere,
//            )
//        }
//        return true
//    }

//            fun isGlobalVarNameExistErrWithRunPrefix(
//                context: Context?,
//                settingKeyToVarNameList: List<Pair<String, String>>,
//                renewalVarName: String,
//                keyToSubKeyConWhere: String
//            ): Boolean {
//                if(
//                    !renewalVarName.startsWith(escapeRunPrefix)
//                ) return false
//                val settingKeyToGlobalVarNameList = settingKeyToVarNameList.filter {
//                        settingKeyToVarName ->
//                    settingKeyToVarName.second.matches(globalVarNameRegex)
//                }
//                val isGlobalVarNameExistErr = settingKeyToGlobalVarNameList.isNotEmpty()
////                FileSystems.updateFile(
////                    File(UsePath.cmdclickDefaultAppDirPath, "sisGlobalVarNameExistErrWithRunPrefix.txt").absolutePath,
////                    listOf(
////                        "renewalVarName: ${renewalVarName}",
////                        "settingKeyToGlobalVarNameList: ${settingKeyToGlobalVarNameList}"
////                    ).joinToString("\n")
////                )
//                if(
//                    !isGlobalVarNameExistErr
//                ) return false
//                val spanIAcVarKeyName =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.ligthBlue,
//                        sAcVarKeyName
//                    )
//                val spanGlobalVarNameListCon =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        settingKeyToGlobalVarNameList.map {
//                            it.second
//                        }.joinToString(". ")
//                    )
//                runBlocking {
//                    ErrLogger.sendErrLog(
//                        context,
//                        ErrLogger.SettingActionErrType.S_AC_VAR,
//                        "With ${escapeRunPrefix} prefix in ${spanIAcVarKeyName}, " +
//                                "global (uppercase) var name must not exist: ${spanGlobalVarNameListCon}",
//                        keyToSubKeyConWhere
//                    )
//                }
//                return true
//            }

//            fun isGlobalVarNameMultipleErrWithoutRunPrefix(
//                context: Context?,
//                settingKeyToVarNameList: List<Pair<String, String>>,
//                topIAcVarName: String,
//                keyToSubKeyConWhere: String
//            ): Boolean {
//                if(
//                    topIAcVarName.startsWith(escapeRunPrefix)
//                ) return false
//                val settingKeyToGlobalVarNameList = settingKeyToVarNameList.filter {
//                        settingKeyToVarName ->
//                    settingKeyToVarName.second.matches(globalVarNameRegex)
//                }
//                val isMultipleGlobalVarNameErr = settingKeyToGlobalVarNameList.size > 1
//                if(
//                    !isMultipleGlobalVarNameErr
//                ) return false
//                val spanSAdVarKeyName =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.ligthBlue,
//                        sAcVarKeyName
//                    )
//                val spanGlobalVarNameListCon =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        settingKeyToGlobalVarNameList.map {
//                            it.second
//                        }.joinToString(", ")
//                    )
//                runBlocking {
//                    ErrLogger.sendErrLog(
//                        context,
//                        ErrLogger.SettingActionErrType.S_AC_VAR,
//                        "In ${spanSAdVarKeyName}, global (uppercase) var name must be one: ${spanGlobalVarNameListCon}",
//                        keyToSubKeyConWhere
//                    )
//                }
//                return true
//
//            }

    fun isGlobalVarNameNotLastErrWithoutRunPrefix(
        context: Context?,
        settingKeyToVarNameList: List<Pair<String, String>>,
        keyToSubKeyConList: List<Pair<String, String>>,
        renewalVarName: String,
        keyToSubKeyConWhere: String
    ): Boolean {
        if(
            renewalVarName.startsWith(escapeRunPrefix)
        ) return false
        val sIfKey =
            SettingActionKeyManager.SettingSubKey.S_IF.key
//        val sIfKeyRegex = Regex(
//            "[?]${sIfKey}="
//        )
        val sIfKeyWithHatena = "?${sIfKey}="
        val lastSubKeyCon = keyToSubKeyConList.lastOrNull {
            it.first.isNotEmpty()
        }?.second
            ?: return false
        val lastSettingKeyToSubKeyCon = settingKeyToVarNameList.lastOrNull()
            ?: return false
        val isSettingReturnKey =
            lastSettingKeyToSubKeyCon.first == settingReturnKey
        val varName = lastSettingKeyToSubKeyCon.second
        val isIrregularVarName = varName.startsWith(
            escapeRunPrefix
        ) || varName.startsWith(
            runAsyncPrefix
        ) || varName.startsWith(
            asyncPrefix
        )
        val containIIfKey = lastSubKeyCon.contains(sIfKeyWithHatena)
            //sIfKeyRegex.containsMatchIn(lastSubKeyCon)
        if (
            isSettingReturnKey
            && !isIrregularVarName
            && !containIIfKey
        ) return false
        val spanEscapeRunPrefix =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                escapeRunPrefix
            )
        val spanRunAsyncPrefix =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                runAsyncPrefix
            )
        val spanAsyncPrefix =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                asyncPrefix
            )
        val spanSAdVarKeyName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                sAcVarKeyName
            )
        val spanSettingReturnKey =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                settingReturnKey
            )
        val spanGlobalVarName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                varName
            )
        val spanSIfKey =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.lightBlue,
                sIfKey
            )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sisGlobalVarNameNotLastErrWithoutRunPrefix.txt").absolutePath,
//                    listOf(
//                        "settingKeyToVarNameList: ${settingKeyToVarNameList}",
//                        "lastSettingKeyToSubKeyCon: ${lastSettingKeyToSubKeyCon}",
//                    ).joinToString("\n")
//                )
        runBlocking {
            SettingActionErrLogger.sendErrLog(
                context,
                SettingActionErrLogger.SettingActionErrType.S_AC_VAR,
                "Without ${spanEscapeRunPrefix} prefix in ${spanSAdVarKeyName}, " +
                        "imported last setting key must be ${spanSettingReturnKey} without ${spanSIfKey}: ${spanGlobalVarName}",
                keyToSubKeyConWhere
            )
        }
        return true
    }
}
