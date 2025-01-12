package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.CcPathTool
import java.io.File
import kotlin.enums.EnumEntries

object PathForSettingHandler {
    fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            > {
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        } ?: let {
            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                funcName
            )
            val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                methodNameStr
            )
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
//        FuncCheckerForSetting.checkArgs(
//            funcName,
//            methodNameStr,
//            methodNameClass.argsNameToTypeList,
//            argsPairList,
////            varNameToValueStrMap,
//        )?.let {
//                argsCheckErr ->
//            return null to argsCheckErr
//        }
//        val argsList = argsPairList.map {
//            it.second
//        }
        val args =
            methodNameClass.args
        return when(args){
            is PathMethodArgClass.MakeFannelDirNameArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val fannelName = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.fannelNameKeyToIndex,
                    where
                ).let { fannelNameToErr ->
                    val funcErr = fannelNameToErr.second
                        ?: return@let fannelNameToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    CcPathTool.makeFannelDirName(fannelName),
                    null,
                ) to null
            }
            is PathMethodArgClass.TrimAllExtendNameArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val fileName = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.fileNameKeyToIndex,
                    where
                ).let { fannelNameToErr ->
                    val funcErr = fannelNameToErr.second
                        ?: return@let fannelNameToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    CcPathTool.trimAllExtend(fileName),
                    null
                ) to null
            }
            is PathMethodArgClass.MakeFannelRawNameArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val fannelName = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.fannelNameKeyToIndex,
                    where
                ).let { fannelNameToErr ->
                    val funcErr = fannelNameToErr.second
                        ?: return@let fannelNameToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    CcPathTool.makeFannelRawName(fannelName),
                    null
                ) to null
            }
            is PathMethodArgClass.GetMainAppDirPathArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val pathStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.currentSubFannelPathToIndex,
                    where
                ).let { pathStrToErr ->
                    val funcErr = pathStrToErr.second
                        ?: return@let pathStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    CcPathTool.getMainAppDirPath(pathStr),
                    null
                ) to null
            }
            is PathMethodArgClass.GetMainFannelFilePathArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val pathStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.currentSubFannelPathToIndex,
                    where
                ).let { pathStrToErr ->
                    val funcErr = pathStrToErr.second
                        ?: return@let pathStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    CcPathTool.getMainFannelFilePath(pathStr),
                    null,
                ) to null
            }
            is PathMethodArgClass.GetMainFannelDirPathArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val pathStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.currentSubFannelPathToIndex,
                    where
                ).let { pathStrToErr ->
                    val funcErr = pathStrToErr.second
                        ?: return@let pathStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    CcPathTool.getMainFannelDirPath(pathStr),
                    null,
                ) to null
            }
            is PathMethodArgClass.GetParentDirPathArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val pathStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.fileNameKeyToIndex,
                    where
                ).let { pathStrToErr ->
                    val funcErr = pathStrToErr.second
                        ?: return@let pathStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    File(pathStr).parent,
                    null,
                ) to null
            }
            is PathMethodArgClass.GetFileNameArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val pathStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.filePathKeyToIndex,
                    where
                ).let { pathStrToErr ->
                    val funcErr = pathStrToErr.second
                        ?: return@let pathStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    File(pathStr).name,
                    null,
                ) to null
            }
            is PathMethodArgClass.IsFileArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val pathStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.filePathKeyToIndex,
                    where
                ).let { pathStrToErr ->
                    val funcErr = pathStrToErr.second
                        ?: return@let pathStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    File(pathStr).isFile.toString(),
                    null,
                    ) to null
            }
            is PathMethodArgClass.IsDirArgs -> {
                val formalArgIndexToNameToTypeList =
                    args.entries.mapIndexed { index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val pathStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.dirPathKeyToIndex,
                    where
                ).let { pathStrToErr ->
                    val funcErr = pathStrToErr.second
                        ?: return@let pathStrToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                Pair(
                    File(pathStr).isDirectory.toString(),
                    null,
                    ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: PathMethodArgClass,
    ){
        MAKE_FANNEL_DIR_NAME(
            "makeFannelDirName",
            PathMethodArgClass.MakeFannelDirNameArgs
        ),
        TRIM_ALL_EXTEND(
            "trimAllExtend",
            PathMethodArgClass.TrimAllExtendNameArgs
        ),
        MAKE_FANNEL_RAW_NAME(
            "makeFannelRawName",
            PathMethodArgClass.MakeFannelRawNameArgs
        ),
        GET_MAIN_APP_DIR_PATH(
            "getMainAppDirPath",
            PathMethodArgClass.GetMainAppDirPathArgs
        ),
        GET_MAIN_FANNEL_FILE_PATH(
            "getMainFannelFilePath",
            PathMethodArgClass.GetMainFannelFilePathArgs
        ),
        GET_MAIN_FANNEL_DIR_PATH(
            "getMainFannelDirPath",
            PathMethodArgClass.GetMainFannelDirPathArgs
        ),
        GET_PARENT_DIR_PATH(
            "getParentDirPath",
            PathMethodArgClass.GetParentDirPathArgs
        ),
        GET_FILE_NAME(
            "getFileName",
            PathMethodArgClass.GetFileNameArgs
        ),
        IS_FILE(
            "isFile",
            PathMethodArgClass.IsFileArgs
        ),
        IS_DIR(
            "isDir",
            PathMethodArgClass.IsDirArgs
        ),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class PathMethodArgClass {
        data object MakeFannelDirNameArgs : PathMethodArgClass(), ArgType {
            override val entries = MakeFannelDirNameEnumArgs.entries
            val fannelNameKeyToIndex = Pair(
                MakeFannelDirNameEnumArgs.FANNEL_NAME.key,
                MakeFannelDirNameEnumArgs.FANNEL_NAME.index
            )

            enum class MakeFannelDirNameEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FANNEL_NAME("fannelName", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object TrimAllExtendNameArgs : PathMethodArgClass(), ArgType {
            override val entries = TrimAllExtendEnumArgs.entries
            val fileNameKeyToIndex = Pair(
                TrimAllExtendEnumArgs.FILE_NAME.key,
                TrimAllExtendEnumArgs.FILE_NAME.index
            )

            enum class TrimAllExtendEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FILE_NAME("fileName", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object MakeFannelRawNameArgs : PathMethodArgClass(), ArgType {
            override val entries = MakeFannelRawNameEnumArgs.entries
            val fannelNameKeyToIndex = Pair(
                MakeFannelRawNameEnumArgs.FANNEL_NAME.key,
                MakeFannelRawNameEnumArgs.FANNEL_NAME.index
            )

            enum class MakeFannelRawNameEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FANNEL_NAME("fannelName", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object GetMainAppDirPathArgs : PathMethodArgClass(), ArgType {
            override val entries = GetMainAppDirPathEnumArgs.entries
            val currentSubFannelPathToIndex = Pair(
                GetMainAppDirPathEnumArgs.CURRENT_SUB_FANNEL_PATH.key,
                GetMainAppDirPathEnumArgs.CURRENT_SUB_FANNEL_PATH.index
            )

            enum class GetMainAppDirPathEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                CURRENT_SUB_FANNEL_PATH("currentSubFannelPath", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object GetMainFannelFilePathArgs : PathMethodArgClass(), ArgType {
            override val entries = GetMainFannelFilePathEnumArgs.entries
            val currentSubFannelPathToIndex = Pair(
                GetMainFannelFilePathEnumArgs.CURRENT_SUB_FANNEL_PATH.key,
                GetMainFannelFilePathEnumArgs.CURRENT_SUB_FANNEL_PATH.index
            )

            enum class GetMainFannelFilePathEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                CURRENT_SUB_FANNEL_PATH("currentSubFannelPath", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object GetMainFannelDirPathArgs : PathMethodArgClass(), ArgType {
            override val entries = GetMainFannelDirPathEnumArgs.entries
            val currentSubFannelPathToIndex = Pair(
                GetMainFannelDirPathEnumArgs.CURRENT_SUB_FANNEL_PATH.key,
                GetMainFannelDirPathEnumArgs.CURRENT_SUB_FANNEL_PATH.index
            )

            enum class GetMainFannelDirPathEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                CURRENT_SUB_FANNEL_PATH("currentSubFannelPath", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object GetParentDirPathArgs : PathMethodArgClass(), ArgType {
            override val entries = GetParentDirPathEnumArgs.entries
            val fileNameKeyToIndex = Pair(
                GetParentDirPathEnumArgs.FILE_NAME.key,
                GetParentDirPathEnumArgs.FILE_NAME.index
            )

            enum class GetParentDirPathEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FILE_NAME("fileName", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object GetFileNameArgs : PathMethodArgClass(), ArgType {
            override val entries = GetFileNameEnumArgs.entries
            val filePathKeyToIndex = Pair(
                GetFileNameEnumArgs.FILE_PATH.key,
                GetFileNameEnumArgs.FILE_PATH.index
            )

            enum class GetFileNameEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FILE_PATH("filePath", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object IsFileArgs : PathMethodArgClass(), ArgType {
            override val entries = IsFileArgs.entries
            val filePathKeyToIndex = Pair(
                IsFileArgs.FILE_PATH.key,
                IsFileArgs.FILE_PATH.index
            )

            enum class IsFileArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FILE_PATH("filePath", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }

        data object IsDirArgs : PathMethodArgClass(), ArgType {
            override val entries = IsDirArgs.entries
            val dirPathKeyToIndex = Pair(
                IsDirArgs.DIR_PATH.key,
                IsDirArgs.DIR_PATH.index
            )

            enum class IsDirArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                DIR_PATH("dirPath", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }
}
