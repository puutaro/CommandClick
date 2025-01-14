package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object FuncCheckerForSetting {

    class FuncCheckErr(
        val errMessage: String
    )

    const val defaultNullMacroStr = "NULL"
    enum class ArgType {
        PATH,
        STRING,
        INT,
        FLOAT,
        BOOL,
    }

    object WhereManager {
        fun makeWhereFromList(
            funcName: String,
            methodNameStr: String,
            argsPairList: List<Pair<String, String>>?,
            formalArgIndexToNameToTypeList: List<Triple<Int, String, ArgType>>
        ): String {
            val formalArgNameToTypeList =
                formalArgIndexToNameToTypeList.map { formalArgIndexToNameToType ->
                    formalArgIndexToNameToType.second to
                            formalArgIndexToNameToType.third
                }
            return makeWhere(
                funcName,
                methodNameStr,
                argsPairList,
                formalArgNameToTypeList,
            )
        }

        private fun makeWhere(
            funcName: String,
            methodNameStr: String,
            argsPairList: List<Pair<String, String>>?,
            argsNameToTypeList: List<Pair<String, ArgType>>?
        ): String {
            val funcDotMethodCon = let {
                val funcDotMethod = listOf(
                    funcName,
                    methodNameStr,
                ).joinToString(".")
                "funcDotMethod: ${funcDotMethod}"
            }
            val currentArgsCon = argsPairList?.map {
                "${it.first}=${it.second}"
            }?.joinToString(",")?.let {
                "current args: ${it}"
            } ?: String()
            val formatArgsCon = argsNameToTypeList?.map {
                "${it.first}(${it.second})"
            }?.joinToString(",")?.let {
                "args format: ${it}"
            } ?: String()
            return listOf(
                funcDotMethodCon,
                currentArgsCon,
                formatArgsCon
            ).joinToString(", ")
        }
    }

    object MapArg {
        enum class MapArgKey {
            INDEX,
            NAME,
            VALUE_STR,
            TYPE,
        }
        fun makeMapArgMapListByName (
            formalArgIndexToNameToTypeList: List<Triple<Int, String, ArgType>>,
            argsNameToValueStrList: List<Pair<String, String>>,
        ): List<Map<MapArgKey, String?>> {
            return formalArgIndexToNameToTypeList.map { formalArgIndexToNameToType ->
                val formalArgIndex = formalArgIndexToNameToType.first
                val formalArgName = formalArgIndexToNameToType.second
                val formalArType = formalArgIndexToNameToType.third
                val valueStr = argsNameToValueStrList.firstOrNull { argNameToValueStr ->
                    val argName = argNameToValueStr.first
                    formalArgName.startsWith(argName)
                }?.second
                mapOf (
                    MapArgKey.INDEX to formalArgIndex.toString(),
                    MapArgKey.NAME to formalArgName,
                    MapArgKey.VALUE_STR to valueStr,
                    MapArgKey.TYPE to formalArType.name,
                )
            }
        }

        fun makeMapArgMapListByIndex (
            formalArgIndexToNameToTypeList: List<Triple<Int, String, ArgType>>,
            argsNameToValueStrList: List<Pair<String, String>>,
        ): List<Map<MapArgKey, String?>> {
            return formalArgIndexToNameToTypeList.mapIndexed { index, formalArgIndexToNameToType ->
                val formalArgIndex = formalArgIndexToNameToType.first
                val formalArgName = formalArgIndexToNameToType.second
                val formalArType = formalArgIndexToNameToType.third
                val valueStr = argsNameToValueStrList.getOrNull(index)?.second
                mapOf (
                    MapArgKey.INDEX to formalArgIndex.toString(),
                    MapArgKey.NAME to formalArgName,
                    MapArgKey.VALUE_STR to valueStr,
                    MapArgKey.TYPE to formalArType.name,
                )
            }
        }

        fun getMapByArgName(
            name: String,
            mapArgMapList: List<Map<MapArgKey, String?>>,
        ): Map<MapArgKey, String?>? {
            return mapArgMapList.firstOrNull {
                    mapArgMap ->
                mapArgMap.get(MapArgKey.NAME) == name
            }
        }

        fun getValueStrByName(
            name: String,
            mapArgMapList: List<Map<MapArgKey, String?>>,
        ):String? {
            return mapArgMapList.firstOrNull {
                mapArgMap ->
                mapArgMap.get(MapArgKey.NAME) == name
            }?.get(MapArgKey.VALUE_STR)
        }

        fun getMapByIndex(
            index: Int,
            mapArgMapList: List<Map<MapArgKey, String?>>,
        ): Map<MapArgKey, String?>? {
            return mapArgMapList.firstOrNull {
                    mapArgMap ->
                mapArgMap.get(MapArgKey.INDEX) == index.toString()
            }
        }

        fun getValueStrByIndex(
            index: Int,
            mapArgMapList: List<Map<MapArgKey, String?>>,
        ):String? {
            return mapArgMapList.firstOrNull {
                    mapArgMap ->
                mapArgMap.get(MapArgKey.INDEX) == index.toString()
            }?.get(MapArgKey.VALUE_STR)
        }
    }

    object Getter {
        private fun getFloatArg(
            argName: String,
            valueStr: String?,
            logIndexSrc: String?,
            mapArgMap: Map<MapArg.MapArgKey, String?>?,
            where: String
        ): Pair<Float, FuncCheckErr?> {
            val logIndex =
                (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
            val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
                argName,
                logIndex,
                valueStr,
                where
            )
            val errReturnFloat = -1f
            if (
                funcCheckBaseErr != null
            ) return Pair(errReturnFloat, funcCheckBaseErr)
            return try {
                (valueStr?.toFloat() as Float) to null
            } catch (e: Exception) {
                errReturnFloat to launchTypeCheckErr2(
                    mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                    (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                    String(),
                    where,
                )
            }
        }

        fun getFloatFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            val argKey = argNameToDefaultValueStr.first
            val defaultValueStr = argNameToDefaultValueStr.second
            return MapArg.getMapByArgName(
                argKey,
                mapArgMapList,
            ).let { targetArgMap ->
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lfloat.txt").absolutePath,
//                    listOf(
//                        "defaultValueStr: ${defaultValueStr}",
//                        "valueStr: ${targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)}",
//                        "mapArgMapList: ${mapArgMapList}",
//                        "targetArgMap: ${targetArgMap}",
//                        "argNameToDefaultValueStr: ${argNameToDefaultValueStr}"
//                    ).joinToString("\n"),
//                )
                getFloatArg(
                    argKey,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    where,
                )
            }
        }

        fun getFloatFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            val argName = argNameToIndex.first
            val argIndex = argNameToIndex.second
            return MapArg.getMapByIndex(
                argIndex,
                mapArgMapList,
            ).let { targetArgMap ->
                getFloatArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    where,
                )
            }
        }


        fun getBoolFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            where: String,
        ): Pair<Boolean, FuncCheckErr?> {
            val argKey = argNameToDefaultValueStr.first
            val defaultValueStr = argNameToDefaultValueStr.second
            return MapArg.getMapByArgName(
                argKey,
                mapArgMapList,
            ).let { targetArgMap ->
                getBoolArg(
                    argKey,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    where,
                )
            }
        }

        fun getBoolFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            where: String,
        ): Pair<Boolean, FuncCheckErr?> {
            val argName = argNameToIndex.first
            val argIndex = argNameToIndex.second
            return MapArg.getMapByIndex(
                argIndex,
                mapArgMapList,
            ).let { targetArgMap ->
                getBoolArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    where,
                )
            }
        }

        private fun getBoolArg(
            argName: String,
            valueStr: String?,
            logIndexSrc: String?,
            mapArgMap: Map<MapArg.MapArgKey, String?>?,
            where: String
        ): Pair<Boolean, FuncCheckErr?> {
            val logIndex =
                (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
            val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
                argName,
                logIndex,
                valueStr,
                where
            )
            val errReturnBool = false
            if (
                funcCheckBaseErr != null
            ) return Pair(errReturnBool, funcCheckBaseErr)
            return try {
                (valueStr?.toBoolean() as Boolean) to null
            } catch (e: Exception) {
                errReturnBool to launchTypeCheckErr2(
                    mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                    (logIndex ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                    String(),
                    where,
                )
            }
        }

        fun getIntFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            where: String,
        ): Pair<Int, FuncCheckErr?> {
            val argKey = argNameToDefaultValueStr.first
            val defaultValueStr = argNameToDefaultValueStr.second
            return MapArg.getMapByArgName(
                argKey,
                mapArgMapList,
            ).let { targetArgMap ->
                getIntArg(
                    argKey,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    where,
                )
            }
        }

        fun getIntFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            where: String,
        ): Pair<Int, FuncCheckErr?> {
            val argName = argNameToIndex.first
            val argIndex = argNameToIndex.second
            return MapArg.getMapByIndex(
                argIndex,
                mapArgMapList,
            ).let { targetArgMap ->
                getIntArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    where,
                )
            }
        }

        private fun getIntArg(
            argName: String,
            valueStr: String?,
            logIndexSrc: String?,
            mapArgMap: Map<MapArg.MapArgKey, String?>?,
            where: String
        ): Pair<Int, FuncCheckErr?> {
            val logIndex =
                (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
            val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
                argName,
                logIndex,
                valueStr,
                where
            )
            val errReturnInt = -1
            if (
                funcCheckBaseErr != null
            ) return Pair(errReturnInt, funcCheckBaseErr)
            return try {
                (valueStr?.toInt() as Int) to null
            } catch (e: Exception) {
                errReturnInt to launchTypeCheckErr2(
                    mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                    (logIndex ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                    String(),
                    where,
                )
            }
        }

        fun getStringFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            where: String,
        ): Pair<String, FuncCheckErr?> {
            val argKey = argNameToDefaultValueStr.first
            val defaultValueStr = argNameToDefaultValueStr.second
            return MapArg.getMapByArgName(
                argKey,
                mapArgMapList,
            ).let { targetArgMap ->
                getStringArg(
                    argKey,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    where,
                )
            }
        }

        fun getStringFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            where: String,
        ): Pair<String, FuncCheckErr?> {
            val argName = argNameToIndex.first
            val argIndex =
                argNameToIndex.second
            return MapArg.getMapByIndex(
                argIndex,
                mapArgMapList,
            ).let { targetArgMap ->
                getStringArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    where,
                )
            }
        }

        private fun getStringArg(
            argName: String,
            valueStr: String?,
            logIndexSrc: String?,
            mapArgMap: Map<MapArg.MapArgKey, String?>?,
            where: String
        ): Pair<String, FuncCheckErr?> {
            val logIndex =
                (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
            val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
                argName,
                logIndex,
                valueStr,
                where
            )
            if (
                funcCheckBaseErr != null
            ) return Pair(null.toString(), funcCheckBaseErr)

            return try {
                valueStr.toString() to null
            } catch (e: Exception) {
                null.toString() to launchTypeCheckErr2(
                    mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                    (logIndex ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                    String(),
                    where,
                )
            }
        }

        fun getPathFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            where: String,
        ): Pair<String, FuncCheckErr?> {
            val argName = argNameToDefaultValueStr.first
            val defaultValueStr = argNameToDefaultValueStr.second
            return MapArg.getMapByArgName(
                argName,
                mapArgMapList,
            ).let { targetArgMap ->
                getPathArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    where,
                )
            }
        }

        fun getPathFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            where: String,
        ): Pair<String, FuncCheckErr?> {
            val argName = argNameToIndex.first
            val argIndex = argNameToIndex.second
            return MapArg.getMapByIndex(
                argIndex,
                mapArgMapList,
            ).let { targetArgMap ->
                getPathArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    where,
                )
            }
        }

        private fun getPathArg(
            argName: String,
            valueStr: String?,
            logIndexSrc: String?,
            mapArgMap: Map<MapArg.MapArgKey, String?>?,
            where: String
        ): Pair<String, FuncCheckErr?> {
            val logIndex =
                (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
            val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
                argName,
                logIndex,
                valueStr,
                where
            )
            if (
                funcCheckBaseErr != null
            ) return null.toString() to funcCheckBaseErr
            val pathEntry = valueStr.toString()
            return try {
                if (
                    File(pathEntry).isFile
                    || File(pathEntry).isDirectory
                ) pathEntry to null
                else {
                    null.toString() to launchTypeCheckErr2(
                        mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                        (logIndex ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                        mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                        mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                        String(),
                        where,
                    )
                }
            } catch (e: Exception) {
                null.toString() to launchTypeCheckErr2(
                    mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                    (logIndex ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                    String(),
                    where,
                )
            }
        }

        private fun launchTypeCheckErr2(
            argName: String,
            logIndex: String,
            argTypeStr: String,
            argStr: String,
            bitmapErrBody: String,
            where: String,
        ): FuncCheckErr {
            val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                argName
            )
            val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                logIndex
            )
            val spanArgType = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.ligthBlue,
                argTypeStr
            )
            val spanArgStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                argStr
            )
            val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                where
            )
            val argType = ArgType.entries.firstOrNull {
                it.name == argTypeStr
            }
            return when (argType) {
                ArgType.PATH ->
                    FuncCheckErr(
                        "Arg ${spanArgName} not found path: ${spanArgStr}, func.method: index: ${spanArgIndex}, ${spanWhere}"
                    )

                else -> {
                    val errBodyMsg = bitmapErrBody.ifEmpty {
                        "not ${spanArgType} type"
                    }
                    FuncCheckErr(
                        "Arg ${spanArgName} ${errBodyMsg}: ${spanArgStr}, func.method: index: ${spanArgIndex}, ${spanWhere}"
                    )
                }
            }
        }

        private object SettingActionArgCheckTool {
            fun checkByExist(
                argName: String,
                argLogIndex: String,
                valueStr: String?,
                where: String
            ): FuncCheckErr? {
                if(valueStr == null) {
                    val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        argName
                    )
                    val spanArgLogIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errRedCode,
                        argLogIndex
                    )
                    val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                        CheckTool.errBrown,
                        where
                    )
                    return FuncCheckErr(
                        "${spanArgName} args (${spanArgLogIndex}) not exist: func.method: " +
                                "args list: ${spanWhere}"
                    )
                }
                return null
            }
        }
    }
}