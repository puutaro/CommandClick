package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import kotlin.enums.EnumEntries

object FileSystemsForSettingHandler {

    fun handle(
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >?
    {
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
//        )?.let {
//            argsCheckErr ->
//            return null to argsCheckErr
//        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "settingCheck.txt").absolutePath,
//            listOf(
//                "isErr: ${isErr}",
//            ).joinToString("\n")
//        )
//        val argsList = argsPairList.map {
//            it.second
//        }
        val args =
            methodNameClass.args
        return when(args){
            is FileSystemsMethodArgClass.ReadArgs -> {
                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                        index, formalArgsNameToType ->
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
                val filePath = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.filePathKeyToIndex,
                    where
                ).let { filePathToErr ->
                        val funcErr = filePathToErr.second
                            ?: return@let filePathToErr.first
                        return Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                Pair(
                    ReadText(filePath).readText(),
                    null
                ) to null
            }
            is FileSystemsMethodArgClass.WriteArgs -> {
                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                        index, formalArgsNameToType ->
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
                val filePath = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.filePathKeyToIndex,
                    where
                ).let { filePathToErr ->
                    val funcErr = filePathToErr.second
                        ?: return@let filePathToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val contents = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.contentsToIndex,
                    where
                ).let { contentsToErr ->
                    val funcErr = contentsToErr.second
                        ?: return@let contentsToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "setting2.txt").absolutePath,
//                    listOf(
//                        "firstArg: ${firstArg}",
//                        "secondArg: ${secondArg}",
//                    ).joinToString("\n")
//                )
                FileSystems.writeFile(
                    filePath,
                    contents
                )
                null
            }
            is FileSystemsMethodArgClass.UpdateWriteArgs -> {
                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                        index, formalArgsNameToType ->
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
                val filePath = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.filePathKeyToIndex,
                    where
                ).let { filePathToErr ->
                    val funcErr = filePathToErr.second
                        ?: return@let filePathToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val contents = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.contentsToIndex,
                    where
                ).let { contentsToErr ->
                    val funcErr = contentsToErr.second
                        ?: return@let contentsToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                FileSystems.updateFile(
                    filePath,
                    contents
                )
                null
            }
            is FileSystemsMethodArgClass.RemoveArgs -> {
                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                        index, formalArgsNameToType ->
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
                val filePath = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.filePathKeyToIndex,
                    where
                ).let { filePathToErr ->
                    val funcErr = filePathToErr.second
                        ?: return@let filePathToErr.first
                    return Pair(
                        null,
                        SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                FileSystems.removeFiles(
                    filePath
                )
                null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: FileSystemsMethodArgClass,
    ){
        READ("read", FileSystemsMethodArgClass.ReadArgs),
        WRITE("write", FileSystemsMethodArgClass.WriteArgs),
        UPDATE_WRITE("updateWrite", FileSystemsMethodArgClass.UpdateWriteArgs),
        REMOVE("remove", FileSystemsMethodArgClass.RemoveArgs),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class FileSystemsMethodArgClass {
        data object WriteArgs : FileSystemsMethodArgClass(), ArgType {
            override val entries = WriteEnumArgs.entries
            val filePathKeyToIndex = Pair(
                WriteEnumArgs.FILE_PATH.key,
                WriteEnumArgs.FILE_PATH.index
            )
            val contentsToIndex = Pair(
                WriteEnumArgs.CONTENTS.key,
                WriteEnumArgs.CONTENTS.index
            )

            enum class WriteEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FILE_PATH("filePath", 0, FuncCheckerForSetting.ArgType.STRING),
                CONTENTS("contents", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object UpdateWriteArgs : FileSystemsMethodArgClass(), ArgType {
            override val entries = UpdateWriteEnumArgs.entries
            val filePathKeyToIndex = Pair(
                UpdateWriteEnumArgs.FILE_PATH.key,
                UpdateWriteEnumArgs.FILE_PATH.index
            )
            val contentsToIndex = Pair(
                UpdateWriteEnumArgs.CONTENTS.key,
                UpdateWriteEnumArgs.CONTENTS.index
            )

            enum class UpdateWriteEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FILE_PATH("filePath", 0, FuncCheckerForSetting.ArgType.STRING),
                CONTENTS("contents", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
        data object ReadArgs : FileSystemsMethodArgClass(), ArgType {
            override val entries = ReadEnumArgs.entries
            val filePathKeyToIndex = Pair(
                ReadEnumArgs.FILE_PATH.key,
                ReadEnumArgs.FILE_PATH.index
            )
            enum class ReadEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FILE_PATH("filePath", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }

        data object RemoveArgs : FileSystemsMethodArgClass(), ArgType {
            override val entries = RemoveEnumArgs.entries
            val filePathKeyToIndex = Pair(
                RemoveEnumArgs.FILE_PATH.key,
                RemoveEnumArgs.FILE_PATH.index
            )
            enum class RemoveEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                FILE_PATH("filePath", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }

}