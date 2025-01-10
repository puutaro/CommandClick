package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.runBlocking
import java.io.File

object ImageActionImportErrManager {
    private val escapeRunPrefix = ImageActionKeyManager.VarPrefix.RUN.prefix
    private val runAsyncPrefix = ImageActionKeyManager.VarPrefix.RUN_ASYNC.prefix
    private val asyncPrefix = ImageActionKeyManager.VarPrefix.ASYNC.prefix
    private val iAcVarKeyName =
        ImageActionKeyManager.ImageActionsKey.IMAGE_ACTION_VAR.key
    private val imageReturnKey =
        ImageActionKeyManager.ImageActionsKey.IMAGE_RETURN.key

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
                CheckTool.ligthBlue,
                iAcVarKeyName
            )
        val spanOriginImportPathListCon =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                originImportPathList.joinToString(",")
            )
        val spanImportPath =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                importPath
            )
        runBlocking {
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_AC_VAR,
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
                CheckTool.ligthBlue,
                iAcVarKeyName
            )
        val spanImportPath =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                importPath
            )
        runBlocking {
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_AC_VAR,
                "import path not exist in ${spanSAdVarKeyName}: ${spanImportPath}",
                keyToSubKeyConWhere,
            )
        }
        return true
    }

    fun isImportShadowVarMarkErr(
        context: Context?,
        importPath: String,
        importSrcConBeforeReplace: String,
        importRepMap: Map<String, String>,
        keyToSubKeyConWhere: String,
    ): Boolean {
        val importVarSharpMarkList = importRepMap.filter {
            ImageActionKeyManager.BitmapVar.matchBitmapVarName(it.value)
        }.map {
            it.value
        }
        val importShadowVarSharpMark = importVarSharpMarkList.firstOrNull {
            importSrcConBeforeReplace.contains(it)
        }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultIDebugAppDirPath, "idebug.txt").absolutePath,
//                    listOf(
//                        "importPath: ${importPath}",
//                        "importVarSharpMarkList: ${importVarSharpMarkList}",
//                        "importShadowVarSharpMark: ${importShadowVarSharpMark}",
//                        "importSrcConBeforeReplace: ${importSrcConBeforeReplace}",
//                    ).joinToString("\n")
//                )
        if(
            importShadowVarSharpMark.isNullOrEmpty()
        ) return false
        val spanSAcVarKeyName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                iAcVarKeyName
            )
        val spanImportShadowVar =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                importShadowVarSharpMark
            )
        val spanImportPath =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                importPath
            )
        runBlocking {
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_AC_VAR,
                "must not shadow variable in ${spanSAcVarKeyName}: importPath: ${spanImportPath}, shadowVar: ${spanImportShadowVar}",
                keyToSubKeyConWhere,
            )
        }
        return true
    }

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
//                        iAcVarKeyName
//                    )
//                val spanGlobalVarNameListCon =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        settingKeyToGlobalVarNameList.map {
//                            it.second
//                        }.joinToString(". ")
//                    )
//                runBlocking {
//                    ImageActionErrLogger.sendErrLog(
//                        context,
//                        ImageActionErrLogger.ImageActionErrType.I_AC_VAR,
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
//                        iAcVarKeyName
//                    )
//                val spanGlobalVarNameListCon =
//                    CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        settingKeyToGlobalVarNameList.map {
//                            it.second
//                        }.joinToString(", ")
//                    )
//                runBlocking {
//                    ImageActionErrLogger.sendErrLog(
//                        context,
//                        ImageActionErrLogger.ImageActionErrType.I_AC_VAR,
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
        val iIfKey =
            ImageActionKeyManager.ImageSubKey.I_IF.key
        val iIfKeyRegex = Regex(
            "[?]${iIfKey}="
        )
        val lastSubKeyCon = keyToSubKeyConList.lastOrNull {
            it.first.isNotEmpty()
        }?.second
            ?: return false
        val lastSettingKeyToVarName = settingKeyToVarNameList.lastOrNull()
            ?: return false
        val isImageReturnKey =
            lastSettingKeyToVarName.first == imageReturnKey
        val varName = lastSettingKeyToVarName.second
        val isIrregularVarName = varName.startsWith(
            escapeRunPrefix
        ) || varName.startsWith(
            runAsyncPrefix
        ) || varName.startsWith(
            asyncPrefix
        )
        val containIIfKey = iIfKeyRegex.containsMatchIn(lastSubKeyCon)
        FileSystems.updateFile(
            File(UsePath.cmdclickDefaultIDebugAppDirPath, "isGlobalVarNameNotLastErrWithoutRunPrefix.txt").absolutePath,
            listOf(
                "lastSettingKeyToVarName: ${lastSettingKeyToVarName}",
                "isImageReturnKey: ${isImageReturnKey}",
                "varName: ${varName}",
                "isIrregularVarName: ${isIrregularVarName}",
                "iIfKeyRegex: ${iIfKeyRegex}",
                "keyToSubKeyConList: ${keyToSubKeyConList}",
                "lastSubKeyCon: ${lastSubKeyCon}",
                "containIIfKey: ${containIIfKey}",
            ).joinToString("\n\n")
        )
        if (
            isImageReturnKey
            && !isIrregularVarName
            && !containIIfKey
        ) return false
        val spanEscapeRunPrefix =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                escapeRunPrefix
            )
        val spanRunAsyncPrefix =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                runAsyncPrefix
            )
        val spanAsyncPrefix =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                asyncPrefix
            )
        val spanIAdVarKeyName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                iAcVarKeyName
            )
        val spanImageReturnKey =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                imageReturnKey
            )
        val spanGlobalVarName =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                varName
            )
        val spanIIfKey =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                iIfKey
            )
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "sisGlobalVarNameNotLastErrWithoutRunPrefix.txt").absolutePath,
//                    listOf(
//                        "settingKeyToVarNameList: ${settingKeyToVarNameList}",
//                        "lastSettingKeyToSubKeyCon: ${lastSettingKeyToSubKeyCon}",
//                    ).joinToString("\n")
//                )
        runBlocking {
            ImageActionErrLogger.sendErrLog(
                context,
                ImageActionErrLogger.ImageActionErrType.I_AC_VAR,
                "Without ${spanEscapeRunPrefix} prefix in ${spanIAdVarKeyName}, " +
                        "imported last setting key must be ${spanImageReturnKey} without ${spanIIfKey}: ${spanGlobalVarName}",
                keyToSubKeyConWhere
            )
        }
        return true
    }
}