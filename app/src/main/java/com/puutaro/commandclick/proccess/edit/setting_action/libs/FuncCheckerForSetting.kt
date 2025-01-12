package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.common.variable.CheckTool
import java.io.File

class FuncCheckerForSetting(
    private val funcName: String,
    private val methodName: String,
) {

    class FuncCheckErr(
        val errMessage: String
    )

    companion object {
        const val defaultNullMacroStr = "NULL"
        enum class ArgType {
            PATH,
            STRING,
            INT,
            FLOAT,
            BOOL,
        }
        fun makeWhere2(
            argsPairList: List<Pair<String, String>>?,
            argsNameToTypeList: List<Pair<String, String>>?
        ): String {
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
                currentArgsCon,
                formatArgsCon
            ).joinToString(", ")
        }

        fun makeWhereFromList(
            argsPairList: List<Pair<String, String>>?,
            formalArgIndexToNameToTypeList: List<Triple<Int, String, ArgType>>
        ): String {
            val formalArgNameToTypeList = formalArgIndexToNameToTypeList.map {
                    formalArgIndexToNameToType ->
                formalArgIndexToNameToType.second to
                        formalArgIndexToNameToType.third
            }
            return makeWhere(
                argsPairList,
                formalArgNameToTypeList,
            )
        }
        fun makeWhere(
            argsPairList: List<Pair<String, String>>?,
            argsNameToTypeList: List<Pair<String, ArgType>>?
        ): String {
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
                currentArgsCon,
                formatArgsCon
            ).joinToString(", ")
        }

        object MapArg {
            enum class MapArgKey {
                INDEX,
                NAME,
                VALUE_STR,
                TYPE,
            }
            fun makeMapArgMapList (
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

    }

//    fun getIntArg(
//        argNameToValue: Pair<String, String>?,
//        formalArgNameToType: Pair<String, ArgType>,
//        index: Int,
//        where: String,
//    ): Pair<Int, FuncCheckErr?> {
//        val funcCheckBaseErr = SettingActionArgCheckTool.checkByBase(
//            funcName,
//            methodName,
//            index,
//            argNameToValue,
//            formalArgNameToType
//        )
//        if(
//            funcCheckBaseErr != null
//        ) return Pair(-1, funcCheckBaseErr)
//        return try {
//            (argNameToValue?.second?.toInt() as Int) to null
//        } catch (e: Exception){
//                -1 to launchTypeCheckErr(
//                    funcName,
//                    methodName,
//                    formalArgNameToType.first,
//                    index,
//                    formalArgNameToType.second,
//                    argNameToValue?.second.toString(),
//                    String(),
//                    where,
//                )
//        }
//    }

    private fun getFloatArg(
        argName: String,
        valueStr: String?,
        logIndexSrc: String?,
        mapArgMap: Map<MapArg. MapArgKey, String?>?,
        where: String
    ): Pair<Float, FuncCheckErr?> {
        val logIndex =
            (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
        val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
            argName,
            funcName,
            methodName,
            logIndex,
            valueStr,
            where
        )
        val errReturnFloat = -1f
        if(
            funcCheckBaseErr != null
        ) return Pair(errReturnFloat, funcCheckBaseErr)
        return try {
            (valueStr?.toFloat() as Float) to null
        } catch (e: Exception){
            errReturnFloat to launchTypeCheckErr2(
                funcName,
                methodName,
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
        funcCheckerForSetting: FuncCheckerForSetting,
        mapArgMapList: List<Map<MapArg. MapArgKey, String?>>,
        argNameToDefaultValueStr: Pair<String, String?>,
        where: String,
    ): Pair<Float, FuncCheckErr?>{
        val argKey = argNameToDefaultValueStr.first
        val defaultValueStr = argNameToDefaultValueStr.second
        return MapArg.getMapByArgName(
            argKey,
            mapArgMapList,
        ).let {
                targetArgMap ->
            funcCheckerForSetting.getFloatArg(
                argKey,
                targetArgMap?.get(FuncCheckerForSetting.Companion.MapArg.MapArgKey.VALUE_STR) ?: defaultValueStr,
                null.toString(),
                targetArgMap,
                where,
            )
        }
    }

    fun getFloatFromArgMapByIndex(
        funcCheckerForSetting: FuncCheckerForSetting,
        mapArgMapList: List<Map<MapArg. MapArgKey, String?>>,
        argNameToIndex: Pair<String, Int>,
        where: String,
    ): Pair<Float, FuncCheckErr?>{
        val argName = argNameToIndex.first
        val argIndex = argNameToIndex.second
        return MapArg.getMapByIndex(
            argIndex,
            mapArgMapList,
        ).let {
                targetArgMap ->
            funcCheckerForSetting.getFloatArg(
                argName,
                targetArgMap?.get(FuncCheckerForSetting.Companion.MapArg.MapArgKey.VALUE_STR),
                targetArgMap?.get(MapArg.MapArgKey.INDEX),
                targetArgMap,
                where,
            )
        }
    }

//    private fun getFloatArg(
//        argNameToValue: Pair<String, String>?,
//        formalArgNameToType: Pair<String, ArgType>,
//        index: Int,
//        where: String,
//    ): Pair<Float, FuncCheckErr?> {
//        val funcCheckBaseErr = SettingActionArgCheckTool.checkByBase(
//            funcName,
//            methodName,
//            index,
//            argNameToValue,
//            formalArgNameToType
//        )
//        if(
//            funcCheckBaseErr != null
//        ) return Pair(-1f, funcCheckBaseErr)
//        return try {
//            (argNameToValue?.second?.toFloat() as Float) to null
//        } catch (e: Exception){
//            -1f to launchTypeCheckErr(
//                funcName,
//                methodName,
//                formalArgNameToType.first,
//                index,
//                formalArgNameToType.second,
//                argNameToValue?.second.toString(),
//                String(),
//                where,
//            )
//        }
//    }

    fun getBoolFromArgMapByName(
        funcCheckerForSetting: FuncCheckerForSetting,
        mapArgMapList: List<Map<MapArg. MapArgKey, String?>>,
        argNameToDefaultValueStr: Pair<String, String?>,
        where: String,
    ): Pair<Boolean, FuncCheckErr?>{
        val argKey = argNameToDefaultValueStr.first
        val defaultValueStr = argNameToDefaultValueStr.second
        return MapArg.getMapByArgName(
            argKey,
            mapArgMapList,
        ).let {
                targetArgMap ->
            funcCheckerForSetting.getBoolArg(
                argKey,
                targetArgMap?.get(FuncCheckerForSetting.Companion.MapArg.MapArgKey.VALUE_STR) ?: defaultValueStr,
                null.toString(),
                targetArgMap,
                where,
            )
        }
    }

    fun getBoolFromArgMapByIndex(
        funcCheckerForSetting: FuncCheckerForSetting,
        mapArgMapList: List<Map<MapArg. MapArgKey, String?>>,
        argNameToIndex: Pair<String, Int>,
        where: String,
    ): Pair<Boolean, FuncCheckErr?>{
        val argName = argNameToIndex.first
        val argIndex = argNameToIndex.second
        return MapArg.getMapByIndex(
            argIndex,
            mapArgMapList,
        ).let {
                targetArgMap ->
            funcCheckerForSetting.getBoolArg(
                argName,
                targetArgMap?.get(FuncCheckerForSetting.Companion.MapArg.MapArgKey.VALUE_STR),
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
        mapArgMap: Map<MapArg. MapArgKey, String?>?,
        where: String
    ): Pair<Boolean, FuncCheckErr?> {
        val logIndex =
            (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
        val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
            funcName,
            methodName,
            argName,
            logIndex,
            valueStr,
            where
        )
        val errReturnBool = false
        if(
            funcCheckBaseErr != null
        ) return Pair(errReturnBool, funcCheckBaseErr)
        return try {
            (valueStr?.toBoolean() as Boolean) to null
        } catch (e: Exception){
            errReturnBool to launchTypeCheckErr2(
                funcName,
                methodName,
                mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                (logIndex ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                String(),
                where,
            )
        }
    }

//    fun getBoolArg(
//        argNameToValue: Pair<String, String>?,
//        formalArgNameToType: Pair<String, ArgType>,
//        index: Int,
//        where: String,
//    ): Pair<Boolean, FuncCheckErr?> {
//        val funcCheckBaseErr = SettingActionArgCheckTool.checkByBase(
//            funcName,
//            methodName,
//            index,
//            argNameToValue,
//            formalArgNameToType
//        )
//        if(
//            funcCheckBaseErr != null
//        ) return Pair(false, funcCheckBaseErr)
//        return try {
//            (argNameToValue?.second?.toBoolean() as Boolean) to null
//        } catch (e: Exception){
//            false to launchTypeCheckErr(
//                funcName,
//                methodName,
//                formalArgNameToType.first,
//                index,
//                formalArgNameToType.second,
//                argNameToValue?.second.toString(),
//                String(),
//                where,
//            )
//        }
//    }

    fun getIntFromArgMapByName(
        funcCheckerForSetting: FuncCheckerForSetting,
        mapArgMapList: List<Map<MapArg. MapArgKey, String?>>,
        argNameToDefaultValueStr: Pair<String, String?>,
        where: String,
    ): Pair<Int, FuncCheckErr?>{
        val argKey = argNameToDefaultValueStr.first
        val defaultValueStr = argNameToDefaultValueStr.second
        return MapArg.getMapByArgName(
            argKey,
            mapArgMapList,
        ).let {
                targetArgMap ->
            funcCheckerForSetting.getIntArg(
                argKey,
                targetArgMap?.get(FuncCheckerForSetting.Companion.MapArg.MapArgKey.VALUE_STR) ?: defaultValueStr,
                null.toString(),
                targetArgMap,
                where,
            )
        }
    }

    fun getIntFromArgMapByIndex(
        funcCheckerForSetting: FuncCheckerForSetting,
        mapArgMapList: List<Map<MapArg. MapArgKey, String?>>,
        argNameToIndex: Pair<String, Int>,
        where: String,
    ): Pair<Int, FuncCheckErr?>{
        val argName = argNameToIndex.first
        val argIndex = argNameToIndex.second
        return MapArg.getMapByIndex(
            argIndex,
            mapArgMapList,
        ).let {
                targetArgMap ->
            funcCheckerForSetting.getIntArg(
                argName,
                targetArgMap?.get(FuncCheckerForSetting.Companion.MapArg.MapArgKey.VALUE_STR),
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
        mapArgMap: Map<MapArg. MapArgKey, String?>?,
        where: String
    ): Pair<Int, FuncCheckErr?> {
        val logIndex =
            (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
        val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
            funcName,
            methodName,
            argName,
            logIndex,
            valueStr,
            where
        )
        val errReturnInt = -1
        if(
            funcCheckBaseErr != null
        ) return Pair(errReturnInt, funcCheckBaseErr)
        return try {
            (valueStr?.toInt() as Int) to null
        } catch (e: Exception){
            errReturnInt to launchTypeCheckErr2(
                funcName,
                methodName,
                mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                (logIndex ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                String(),
                where,
            )
        }
    }

//    fun getStringArg(
//        argNameToValue: Pair<String, String>?,
//        formalArgNameToType: Pair<String, ArgType>?,
//        index: Int,
//        where: String,
//    ): Pair<String, FuncCheckErr?> {
//        val funcCheckBaseErr = SettingActionArgCheckTool.checkByBase(
//            funcName,
//            methodName,
//            index,
//            argNameToValue,
//            formalArgNameToType
//        )
//        if (
//            funcCheckBaseErr != null
//        ) return Pair(null.toString(), funcCheckBaseErr)
//        return try {
//            argNameToValue?.second.toString() to null
//        } catch (e: Exception) {
//            null.toString() to launchTypeCheckErr(
//                funcName,
//                methodName,
//                formalArgNameToType?.first.toString(),
//                index,
//                formalArgNameToType?.second,
//                argNameToValue?.second.toString(),
//                String(),
//                where,
//            )
//        }
//    }

    fun getStringFromArgMapByName(
        funcCheckerForSetting: FuncCheckerForSetting,
        mapArgMapList: List<Map<MapArg. MapArgKey, String?>>,
        argNameToDefaultValueStr: Pair<String, String?>,
        where: String,
    ): Pair<String, FuncCheckErr?>{
        val argKey = argNameToDefaultValueStr.first
        val defaultValueStr = argNameToDefaultValueStr.second
        return MapArg.getMapByArgName(
            argKey,
            mapArgMapList,
        ).let {
                targetArgMap ->
            funcCheckerForSetting.getStringArg(
                argKey,
                targetArgMap?.get(FuncCheckerForSetting.Companion.MapArg.MapArgKey.VALUE_STR) ?: defaultValueStr,
                null.toString(),
                targetArgMap,
                where,
            )
        }
    }

    fun getStringFromArgMapByIndex(
        funcCheckerForSetting: FuncCheckerForSetting,
        mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
        argNameToIndex: Pair<String, Int>,
        where: String,
    ): Pair<String, FuncCheckErr?>{
        val argName= argNameToIndex.first
        val argIndex =
            argNameToIndex.second
        return MapArg.getMapByIndex(
            argIndex,
            mapArgMapList,
        ).let {
                targetArgMap ->
            funcCheckerForSetting.getStringArg(
                argName,
                targetArgMap?.get(FuncCheckerForSetting.Companion.MapArg.MapArgKey.VALUE_STR),
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
        mapArgMap: Map<MapArg. MapArgKey, String?>?,
        where: String
    ): Pair<String, FuncCheckErr?> {
        val logIndex =
            (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
        val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
            funcName,
            methodName,
            argName,
            logIndex,
            valueStr,
            where
        )
        if(
            funcCheckBaseErr != null
        ) return Pair(null.toString(), funcCheckBaseErr)
        return try {
            valueStr.toString() to null
        } catch (e: Exception){
            null.toString() to launchTypeCheckErr2(
                funcName,
                methodName,
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
        funcCheckerForSetting: FuncCheckerForSetting,
        mapArgMapList: List<Map<MapArg. MapArgKey, String?>>,
        argNameToDefaultValueStr: Pair<String, String?>,
        where: String,
    ): Pair<String, FuncCheckErr?>{
        val argName = argNameToDefaultValueStr.first
        val defaultValueStr = argNameToDefaultValueStr.second
        return MapArg.getMapByArgName(
            argName,
            mapArgMapList,
        ).let {
                targetArgMap ->
            funcCheckerForSetting.getPathArg(
                argName,
                targetArgMap?.get(FuncCheckerForSetting.Companion.MapArg.MapArgKey.VALUE_STR) ?: defaultValueStr,
                null.toString(),
                targetArgMap,
                where,
            )
        }
    }

    fun getPathFromArgMapByIndex(
        funcCheckerForSetting: FuncCheckerForSetting,
        mapArgMapList: List<Map<MapArg. MapArgKey, String?>>,
        argNameToIndex: Pair<String, Int>,
        where: String,
    ): Pair<String, FuncCheckErr?>{
        val argName = argNameToIndex.first
        val argIndex = argNameToIndex.second
        return MapArg.getMapByIndex(
            argIndex,
            mapArgMapList,
        ).let {
                targetArgMap ->
            funcCheckerForSetting.getPathArg(
                argName,
                targetArgMap?.get(FuncCheckerForSetting.Companion.MapArg.MapArgKey.VALUE_STR),
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
        mapArgMap: Map<MapArg. MapArgKey, String?>?,
        where: String
    ): Pair<String, FuncCheckErr?> {
        val logIndex =
            (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
        val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
            funcName,
            methodName,
            argName,
            logIndex,
            valueStr,
            where
        )
        if(
            funcCheckBaseErr != null
        ) return null.toString() to funcCheckBaseErr
        val pathEntry = valueStr.toString()
        return try {
            if(
                File(pathEntry).isFile
                || File(pathEntry).isDirectory
            ) pathEntry to null
            else {
                null.toString() to launchTypeCheckErr2(
                    funcName,
                    methodName,
                    mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                    (logIndex ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                    String(),
                    where,
                )
            }
        } catch (e: Exception){
            null.toString() to launchTypeCheckErr2(
                funcName,
                methodName,
                mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                (logIndex ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                String(),
                where,
            )
        }
    }
//    fun getPathArg(
//        argNameToValue: Pair<String, String>?,
//        formalArgNameToType: Pair<String, ArgType>,
//        index: Int,
//        where: String,
//    ): Pair<String, FuncCheckErr?> {
//        val funcCheckBaseErr = SettingActionArgCheckTool.checkByBase(
//            funcName,
//            methodName,
//            index,
//            argNameToValue,
//            formalArgNameToType
//        )
//        if(
//            funcCheckBaseErr != null
//        ) return null.toString() to funcCheckBaseErr
//        val pathEntry = argNameToValue?.second.toString()
//        return try {
//            if(
//                File(pathEntry).isFile
//                || File(pathEntry).isDirectory
//            ) pathEntry to null
//            else {
//                null.toString() to launchTypeCheckErr(
//                    funcName,
//                    methodName,
//                    formalArgNameToType.first,
//                    index,
//                    formalArgNameToType.second,
//                    pathEntry,
//                    String(),
//                    where,
//                )
//            }
//        } catch (e: Exception){
//            null.toString() to launchTypeCheckErr(
//                funcName,
//                methodName,
//                formalArgNameToType.first,
//                index,
//                formalArgNameToType.second,
//                pathEntry,
//                String(),
//                where
//            )
//        }
//    }

    private fun launchTypeCheckErr2(
        funcName: String,
        methodName: String,
        argName: String,
        logIndex: String,
        argTypeStr: String,
        argStr: String,
        bitmapErrBody: String,
        where: String,
    ): FuncCheckErr {
        val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errBrown,
            funcName
        )
        val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
            CheckTool.errBrown,
            methodName
        )
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
        return when(argType) {
            ArgType.PATH ->
                FuncCheckErr(
                    "Arg ${spanArgName} not found path: ${spanArgStr}, func.method: ${spanFuncName}.${spanMethodName}, index: ${spanArgIndex}, ${spanWhere}"
                )
            else -> {
                val errBodyMsg = bitmapErrBody.ifEmpty {
                    "not ${spanArgType} type"
                }
                FuncCheckErr(
                    "Arg ${spanArgName} ${errBodyMsg}: ${spanArgStr}, func.method: ${spanFuncName}.${spanMethodName}, index: ${spanArgIndex}, ${spanWhere}"
                )
            }
        }
    }

//    private fun launchTypeCheckErr(
//        funcName: String,
//        methodName: String,
//        argName: String,
//        index: Int,
//        argType: ArgType?,
//        argStr: String,
//        bitmapErrBody: String,
//        where: String,
//    ): FuncCheckErr {
//        val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//            CheckTool.errBrown,
//            funcName
//        )
//        val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//            CheckTool.errBrown,
//            methodName
//        )
//        val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//            CheckTool.errRedCode,
//            argName
//        )
//        val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//            CheckTool.errRedCode,
//            (index + 1).toString()
//        )
//        val spanArgType = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//            CheckTool.ligthBlue,
//            argType?.name.toString()
//        )
//        val spanArgStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//            CheckTool.errRedCode,
//            argStr
//        )
//        val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//            CheckTool.errBrown,
//            where
//        )
//        return when(argType) {
//            ArgType.PATH ->
//                FuncCheckErr(
//                    "Arg ${spanArgName} not found path: ${spanArgStr}, func.method: ${spanFuncName}.${spanMethodName}, index: ${spanArgIndex}, ${spanWhere}"
//                )
//            else -> {
//                val errBodyMsg = bitmapErrBody.ifEmpty {
//                    "not ${spanArgType} type"
//                }
//                FuncCheckErr(
//                    "Arg ${spanArgName} ${errBodyMsg}: ${spanArgStr}, func.method: ${spanFuncName}.${spanMethodName}, index: ${spanArgIndex}, ${spanWhere}"
//                )
//            }
//        }
//    }


    private object SettingActionArgCheckTool {
        fun checkByExist(
            funcName: String,
            methodName: String,
            argName: String,
            argLogIndex: String,
            valueStr: String?,
            where: String
        ): FuncCheckErr? {
            if(valueStr == null) {
                val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    funcName
                )
                val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errBrown,
                    methodName
                )
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
                    "${spanArgName} args (${spanArgLogIndex}) not exist: func.method: ${spanFuncName}.${spanMethodName}: " +
                            "args list: ${spanWhere}"
                )
            }
//            val userDefiniteArgName = argPair.first
//            if (
//                userDefiniteArgName.isEmpty()
//            ) {
//                val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errBrown,
//                    funcName
//                )
//                val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errBrown,
//                    methodName
//                )
//                val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errRedCode,
//                    argName
//                )
//                val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errRedCode,
//                   logArgIndex
//                )
//                val spanArgType = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errRedCode,
//                    formalArgNameToType?.second?.name.toString()
//                )
//                return FuncCheckErr(
//                    "Method args not exist: func.method: ${spanFuncName}.${spanMethodName}: name: ${spanArgName}, index: ${spanArgIndex}, type: ${spanArgType}"
//                )
//            }
            return null
        }

//        fun checkByBase(
//            funcName: String,
//            methodName: String,
//            index: Int,
//            argNameToValue: Pair<String, String>?,
//            formalArgNameToType: Pair<String, ArgType>?
//        ): FuncCheckErr? {
//            val argPair = argNameToValue
//                ?: let {
//                    val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errBrown,
//                        funcName
//                    )
//                    val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errBrown,
//                        methodName
//                    )
//                    val spanBaseArgsNameListCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        formalArgNameToType.toString()
//                    )
//                    return FuncCheckErr(
//                        "Method all args not exist: func.method: ${spanFuncName}.${spanMethodName}: " +
//                                "args list: ${spanBaseArgsNameListCon}"
//                    )
//                }
//            val userDefiniteArgName = argPair.first
//            if (
//                userDefiniteArgName.isEmpty()
//            ) {
//                val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errBrown,
//                    funcName
//                )
//                val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errBrown,
//                    methodName
//                )
//                val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errRedCode,
//                    formalArgNameToType?.first.toString()
//                )
//                val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errRedCode,
//                    (index + 1).toString()
//                )
//                val spanArgType = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errRedCode,
//                    formalArgNameToType?.second?.name.toString()
//                )
//                return FuncCheckErr(
//                    "Method args not exist: func.method: ${spanFuncName}.${spanMethodName}: name: ${spanArgName}, index: ${spanArgIndex}, type: ${spanArgType}"
//                )
//            }
//            return null
//        }
//
//        fun checkArgs(
//            funcName: String,
//            methodName: String,
//            baseArgsNameToTypeList: List<Pair<String, ArgType>>?,
//            argsPairList: List<Pair<String, String>>,
////        varNameToValueStrMap: Map<String, String?>?,
//        ): FuncCheckErr? {
//            if(
//                baseArgsNameToTypeList.isNullOrEmpty()
//            ) return null
//            baseArgsNameToTypeList.forEachIndexed {
//                    index, argNameToType ->
//                val argName = argNameToType.first
//                val argType = argNameToType.second
//                val argPair = argsPairList.getOrNull(index)
//                    ?: let {
//                        val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.errBrown,
//                            funcName
//                        )
//                        val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.errBrown,
//                            methodName
//                        )
//                        val spanBaseArgsNameListCon = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                            CheckTool.errRedCode,
//                            baseArgsNameToTypeList.joinToString(", ")
//                        )
//                        return FuncCheckErr(
//                            "Method all args not exist: func.method: ${spanFuncName}.${spanMethodName}: " +
//                                    "args list: ${spanBaseArgsNameListCon}"
//                        )
//                    }
//                val userDefiniteArgName = argPair.first
//                if(
//                    userDefiniteArgName.isEmpty()
//                ) {
//                    val spanFuncName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errBrown,
//                        funcName
//                    )
//                    val spanMethodName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errBrown,
//                        methodName
//                    )
//                    val spanArgName = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        argName
//                    )
//                    val spanArgIndex = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        (index + 1).toString()
//                    )
//                    val spanArgType = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                        CheckTool.errRedCode,
//                        argType.name
//                    )
//                    return FuncCheckErr(
//                        "Method args not exist: func.method: ${spanFuncName}.${spanMethodName}: name: ${spanArgName}, index: ${spanArgIndex}, type: ${spanArgType}"
//                    )
//                }
//                val userDefiniteArgStr = argPair.second
//                ArgsTypeChecker.check(
//                    funcName,
//                    methodName,
//                    argName,
//                    index,
//                    userDefiniteArgStr,
//                    argType,
////                varNameToValueStrMap,
//                ).let {
//                        funcCheckErr ->
//                    if(
//                        funcCheckErr == null
//                    ) return@let
//                    return funcCheckErr
//                }
//            }
//            return null
//        }

//        private object ArgsTypeChecker{
//            fun checkByType(
//                funcName: String,
//                methodName: String,
//                argName: String,
//                index: Int,
//                argStr: String,
//                argType: ArgType,
//            ): FuncCheckErr? {
//                return try {
//                    when (argType) {
//                        ArgType.STRING ->
//                            null
//                        ArgType.PATH -> {
//                            if(
//                                File(argStr).isFile
//                            ) null
//                            else launchTypeCheckErr(
//                                funcName,
//                                methodName,
//                                argName,
//                                index,
//                                argType,
//                                argStr,
//                                String()
//                            )
//                        }
//                        ArgType.INT -> {
//                            argStr.toInt()
//                            null
//                        }
//                        ArgType.FLOAT -> {
//                            argStr.toFloat()
//                            null
//                        }
//                        ArgType.BOOL -> {
//                            argStr.toBoolean()
//                            null
//                        }
//                    }
//                } catch (e: Exception){
//                    launchTypeCheckErr(
//                        funcName,
//                        methodName,
//                        argName,
//                        index,
//                        argType,
//                        argStr,
//                        String(),
//                    )
//                }
//            }
    }

//        private fun isNotExistStringVarName(
//            funcName: String,
//            methodName: String,
//            argStr: String,
//            argName: String,
//            index: Int,
//            argType: ArgType,
////            varNameToValueStrMap: Map<String, String?>?,
//        ): Pair<String?, FuncCheckErr?>? {
//            if(
//                !SettingActionKeyManager.ValueStrVar.matchStringVarName(argStr)
//            ) return argStr to null
//            return null to launchTypeCheckErr(
//                funcName,
//                methodName,
//                argName,
//                index,
//                argType,
//                argStr,
//                "not exist string var name"
//            )
//        }
////            SettingActionKeyManager.ValueStrVar.matchStringVarName(argStr).let {
////                    isStrVarRegex ->
////                if(isStrVarRegex) return@let
////            }
//            val strKey = SettingActionKeyManager.ValueStrVar.convertStrKey(argStr)
//            val runPrefix = SettingActionKeyManager.VarPrefix.RUN.prefix
//            (strKey.startsWith(runPrefix)).let {
//                    isRunPrefix ->
//                if(!isRunPrefix) return@let
//                return null to launchTypeCheckErr(
//                    funcName,
//                    methodName,
//                    argName,
//                    index,
//                    argType,
//                    argStr,
//                    "disables ${runPrefix} prefix"
//                )
//            }
//            val valueStr =
//                varNameToValueStrMap?.get(strKey)
//            if(
//                valueStr is String
//            ) return valueStr to null
////            FileSystems.updateFile(
////                File(UsePath.cmdclickDefaultSDebugAppDirPath, "lfuncarg.txt").absolutePath,
////                listOf(
////                    "argStr: ${argStr}",
////                    "valueStr: ${valueStr}",
////                    "varNameToValueStrMap: ${varNameToValueStrMap}",
////                ).joinToString("\n") + "\n\n====\n\n"
////            )
//            return null to launchTypeCheckErr(
//                    funcName,
//                    methodName,
//                    argName,
//                    index,
//                    argType,
//                    argStr,
//                    "not exist string var name"
//                )
//            }
//        }
}