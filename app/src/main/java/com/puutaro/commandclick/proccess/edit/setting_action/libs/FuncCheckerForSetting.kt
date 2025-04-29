package com.puutaro.commandclick.proccess.edit.setting_action.libs

import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.util.str.ImageVarMarkTool
import java.io.File

object FuncCheckerForSetting {

    class FuncCheckErr(
        val errMessage: String
    )

    const val defaultNullMacroStr = "NULL"
    enum class ArgType {
        FILE,
        DIR,
        STRING,
        INT,
        FLOAT,
        BOOL,
        BITMAP,
    }


    object NumChecker {
        enum class MinMaxCompare(
            val compareStr: String
        ) {
            EQUAL("<="),
            NOT_EQUAL("<"),
        }
        fun <T> minMaxTwoFloatErr(
            minFloat: T,
            maxFloat: T,
            minMaxCompare: MinMaxCompare = MinMaxCompare.EQUAL,
            minKey: String,
            maxKey: String,
            where: String,
        ):
                FuncCheckErr?
                where T : Number, T : Comparable<T>
        {
            val isOk = when(minMaxCompare){
                MinMaxCompare.EQUAL -> minFloat <= maxFloat
                MinMaxCompare.NOT_EQUAL -> minFloat < maxFloat
            }
            if(
                isOk
                ) return null
            val spanMinKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                minKey
            )
            val spanMaxKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                maxKey
            )
            val spanMinFloat = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                minFloat.toString()
            )
            val spanMaxFloat = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                maxFloat.toString()
            )
            val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                where
            )
            return FuncCheckErr(
                "Must be ${spanMinKey}(${spanMinFloat}) ${minMaxCompare.compareStr} ${spanMaxKey}(${spanMaxFloat}): ${spanWhere}"
            )
        }
        enum class CompareSignal(
            val compareStr: String
        ) {
            EQUAL("=="),
            LARGER(">"),
            EQUAL_LARGER(">="),
            SMALLER("<"),
            EQUAL_SMALLER("<="),
        }
        fun <T> compare(
            baseFloat: T,
            compareSignal: CompareSignal,
            compareFloat: T,
            compareKey: String,
            where: String,
        ):
                FuncCheckErr? where T : Number, T : Comparable<T>
        {
            val isOk = when(compareSignal){
                CompareSignal.EQUAL -> compareFloat == baseFloat
                CompareSignal.LARGER -> compareFloat > baseFloat
                CompareSignal.EQUAL_LARGER -> compareFloat >= baseFloat
                CompareSignal.SMALLER -> compareFloat < baseFloat
                CompareSignal.EQUAL_SMALLER -> compareFloat <= baseFloat
            }
            if(
                isOk
            ) return null
            val spanCompareKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                compareKey
            )
            val spanComareFloat = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                compareFloat.toString()
            )
            val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                where
            )
            return FuncCheckErr(
                "Must be ${spanCompareKey}(${spanComareFloat}) ${compareSignal.compareStr} ${baseFloat}): ${spanWhere}"
            )
        }
        enum class RangeCompareSignal(
            val minCompareStr: String,
            val maxCompareStr: String,
        ) {
            EQUAL("<=", "<="),
            MIN_EQUAL("<=", "<"),
            MAX_EQUAL("<", "<="),
            NOT_EQUAL("<", "<"),
        }
        fun <T> range (
            minBaseFloat: T,
            maxBaseFloat: T,
            rangeCompareSignal: RangeCompareSignal,
            compareFloat: T,
            compareKey: String,
            where: String,
        ): FuncCheckErr?
                where T : Number, T : Comparable<T>
        {
            val isOk = when(rangeCompareSignal){
                RangeCompareSignal.EQUAL
                    -> minBaseFloat <= compareFloat
                        && compareFloat <= maxBaseFloat
                RangeCompareSignal.MIN_EQUAL
                    -> minBaseFloat <= compareFloat
                        && compareFloat < maxBaseFloat
                RangeCompareSignal.MAX_EQUAL
                    -> minBaseFloat < compareFloat
                        && compareFloat <= maxBaseFloat
                RangeCompareSignal.NOT_EQUAL
                    -> minBaseFloat < compareFloat
                        && compareFloat < maxBaseFloat
            }
            if(
                isOk
            ) return null
            val spanCompareKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                compareKey
            )
            val spanCompareFloat = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                compareFloat.toString()
            )
            val spanWhere = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                where
            )
            return FuncCheckErr(
                "${spanCompareKey}(${spanCompareFloat}) must be ${minBaseFloat} ${rangeCompareSignal.minCompareStr} ~ ${rangeCompareSignal.maxCompareStr} ${maxBaseFloat}: ${spanWhere}"
            )
        }
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
                val funcDotMethod = sequenceOf(
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
            return sequenceOf(
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

        fun getZeroELargerFloatFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            return getZeroCompareFloatFromArgMapByName(
                mapArgMapList,
                argNameToDefaultValueStr,
                NumChecker.CompareSignal.EQUAL_LARGER,
                where,
            )
        }
        fun getZeroLargerFloatFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            return getZeroCompareFloatFromArgMapByName(
                mapArgMapList,
                argNameToDefaultValueStr,
                FuncCheckerForSetting.NumChecker.CompareSignal.LARGER,
                where,
            )
        }
        fun getZeroCompareFloatFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            compareSignal: NumChecker.CompareSignal,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
           return getCompareFloatFromArgMapByName(
                mapArgMapList,
                argNameToDefaultValueStr,
                0f,
                compareSignal,
                where,
            )
        }
        fun getCompareFloatFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            compareFloat: Float,
            compareSignal: NumChecker.CompareSignal,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            val argKey = argNameToDefaultValueStr.first
            val defaultValueStr = argNameToDefaultValueStr.second
            return MapArg.getMapByArgName(
                argKey,
                mapArgMapList,
            ).let { targetArgMap ->
                val (float, err) = getFloatArg(
                    argKey,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    where,
                )
                if(
                    err != null
                ) return float to err
                val minusErr = NumChecker.compare(
                    compareFloat,
                    compareSignal,
                    float,
                    argNameToDefaultValueStr.first,
                    where,
                )
                if(minusErr != null){
                    return float to minusErr
                }
                float to null
            }
        }
        fun getRateFloatFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            return getRangeFloatFromArgMapByName(
                mapArgMapList,
                argNameToDefaultValueStr,
                0f,
                1f,
                NumChecker.RangeCompareSignal.EQUAL,
                where,
            )
        }
        fun getRangeFloatFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            minFloat: Float,
            maxFloat: Float,
            rangeCompareSignal: NumChecker.RangeCompareSignal,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            val argKey = argNameToDefaultValueStr.first
            val defaultValueStr = argNameToDefaultValueStr.second
            return MapArg.getMapByArgName(
                argKey,
                mapArgMapList,
            ).let { targetArgMap ->
                val (float, err) = getFloatArg(
                    argKey,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    where,
                )
                if(
                    err != null
                ) return float to err
                val rangeErr = NumChecker.range(
                    minFloat,
                    maxFloat,
                    rangeCompareSignal,
                    float,
                    argNameToDefaultValueStr.first,
                    where,
                )
                if(rangeErr != null) {
                    return float to rangeErr
                }
                float to null
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

        fun getZeroELargerFloatFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            return getZeroCompareFloatFromArgMapByIndex(
                mapArgMapList,
                argNameToIndex,
                NumChecker.CompareSignal.EQUAL_LARGER,
                where,
            )
        }
        fun getZeroLargerFloatFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            return getZeroCompareFloatFromArgMapByIndex(
                mapArgMapList,
                argNameToIndex,
                NumChecker.CompareSignal.LARGER,
                where,
            )
        }

        fun getZeroCompareFloatFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            compareSignal: NumChecker.CompareSignal,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            return getCompareFloatFromArgMapByIndex(
                mapArgMapList,
                argNameToIndex,
                    0f,
                compareSignal,
                where,
            )
        }
        fun getCompareFloatFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            compareFloat: Float,
            compareSignal: NumChecker.CompareSignal,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            val argName = argNameToIndex.first
            val argIndex = argNameToIndex.second
            return MapArg.getMapByIndex(
                argIndex,
                mapArgMapList,
            ).let { targetArgMap ->
                val (float, err) = getFloatArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    where,
                )
                if(
                    err != null
                ) return float to err
                val minusErr = NumChecker.compare(
                    compareFloat,
                    compareSignal,
                    float,
                    argNameToIndex.first,
                    where,
                )
                if(minusErr != null){
                    return float to minusErr
                }
                float to null
            }
        }
        fun getRateFloatFromArgMapByIndex (
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            return getRangeFloatFromArgMapByIndex (
                mapArgMapList,
                argNameToIndex,
                0f,
                1f,
                NumChecker.RangeCompareSignal.EQUAL,
                where,
            )
        }
        fun getRangeFloatFromArgMapByIndex (
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            minFloat: Float,
            maxFloat: Float,
            rangeCompareSignal: NumChecker.RangeCompareSignal,
            where: String,
        ): Pair<Float, FuncCheckErr?> {
            val argName = argNameToIndex.first
            val argIndex = argNameToIndex.second
            return MapArg.getMapByIndex(
                argIndex,
                mapArgMapList,
            ).let { targetArgMap ->
                val (float, err) = getFloatArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    where,
                )
                if(
                    err != null
                ) return float to err
                val rangeErr = NumChecker.range(
                    minFloat,
                    maxFloat,
                    rangeCompareSignal,
                    float,
                    argName,
                    where,
                )
                if(rangeErr != null) {
                    return float to rangeErr
                }
                float to null
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

        fun getBitmapFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            varNameToBitmapMap: Map<String, Bitmap?>?,
            where: String,
        ): Pair<Bitmap?, FuncCheckErr?> {
            val argKey = argNameToDefaultValueStr.first
            val defaultValueStr = argNameToDefaultValueStr.second
            return MapArg.getMapByArgName(
                argKey,
                mapArgMapList,
            ).let { targetArgMap ->
                getBitmapArg(
                    argKey,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    varNameToBitmapMap,
                    where,
                )
            }
        }

        fun getBitmapFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            varNameToBitmapMap: Map<String, Bitmap?>?,
            where: String,
        ): Pair<Bitmap?, FuncCheckErr?> {
            val argName = argNameToIndex.first
            val argIndex = argNameToIndex.second
            return MapArg.getMapByIndex(
                argIndex,
                mapArgMapList,
            ).let { targetArgMap ->
                getBitmapArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    varNameToBitmapMap,
                    where,
                )
            }
        }

        fun getBitmapArg(
            argName: String,
            valueStr: String?,
            logIndexSrc: String?,
            mapArgMap: Map<MapArg.MapArgKey, String?>?,
            varNameToBitmapMap: Map<String, Bitmap?>?,
            where: String
        ): Pair<Bitmap?, FuncCheckErr?> {
            val logIndex =
                (logIndexSrc ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString()
            val funcCheckBaseErr = SettingActionArgCheckTool.checkByExist(
                argName,
                logIndex,
                valueStr,
                where
            )
            val errReturnBitmap = null
            if (
                funcCheckBaseErr != null
            ) return Pair(errReturnBitmap, funcCheckBaseErr)
            return try {
                valueStr ?: throw Exception()
                if(
                    !ImageVarMarkTool.matchBitmapVarName(valueStr)
                ){
                    throw Exception()
                }
                val bitmapKey = ImageVarMarkTool.convertBitmapKey(valueStr)
                val bitmap = varNameToBitmapMap?.get(bitmapKey) ?: throw Exception()
                bitmap to null
            } catch (e: Exception) {
                errReturnBitmap to launchTypeCheckErr2(
                    mapArgMap?.get(MapArg.MapArgKey.NAME).toString(),
                    (logIndex ?: mapArgMap?.get(MapArg.MapArgKey.INDEX)).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.TYPE).toString(),
                    mapArgMap?.get(MapArg.MapArgKey.VALUE_STR).toString(),
                    String(),
                    where,
                )
            }
        }

        fun getZeroELargerIntFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            where: String,
        ): Pair<Int, FuncCheckErr?> {
            return getZeroCompareIntFromArgMapByName(
                mapArgMapList,
                argNameToDefaultValueStr,
                NumChecker.CompareSignal.EQUAL_LARGER,
                where,
            )
        }
        fun getZeroLargerIntFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            where: String,
        ): Pair<Int, FuncCheckErr?> {
            return getZeroCompareIntFromArgMapByName(
                mapArgMapList,
                argNameToDefaultValueStr,
                NumChecker.CompareSignal.LARGER,
                where,
            )
        }
        fun getZeroCompareIntFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            compareSignal: NumChecker.CompareSignal,
            where: String,
        ): Pair<Int, FuncCheckErr?> {
            return getCompareIntFromArgMapByName(
                mapArgMapList,
                argNameToDefaultValueStr,
                0,
                compareSignal,
                where,
            )
        }
        fun getCompareIntFromArgMapByName(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToDefaultValueStr: Pair<String, String?>,
            compareInt: Int,
            compareSignal: NumChecker.CompareSignal,
            where: String,
        ): Pair<Int, FuncCheckErr?> {
            val argKey = argNameToDefaultValueStr.first
            val defaultValueStr = argNameToDefaultValueStr.second
            return MapArg.getMapByArgName(
                argKey,
                mapArgMapList,
            ).let { targetArgMap ->
                val (int, err) = getIntArg(
                    argKey,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    where,
                )
                if(
                    err != null
                ) return int to err
                val minusErr = NumChecker.compare(
                    compareInt,
                    compareSignal,
                    int,
                    argNameToDefaultValueStr.first,
                    where,
                )
                if(minusErr != null){
                    return int to minusErr
                }
                int to null
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
        fun getZeroELargerIntFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            where: String,
        ): Pair<Int, FuncCheckErr?> {
            return getZeroCompareIntFromArgMapByIndex(
                mapArgMapList,
                argNameToIndex,
                NumChecker.CompareSignal.EQUAL_LARGER,
                where,
            )
        }

        fun getZeroLargerIntFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            where: String,
        ): Pair<Int, FuncCheckErr?> {
            return getZeroCompareIntFromArgMapByIndex(
                mapArgMapList,
                argNameToIndex,
                NumChecker.CompareSignal.LARGER,
                where,
            )
        }
        fun getZeroCompareIntFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            compareSignal: NumChecker.CompareSignal,
            where: String,
        ): Pair<Int, FuncCheckErr?> {
            return getCompareIntFromArgMapByIndex(
                mapArgMapList,
                argNameToIndex,
                0,
                compareSignal,
                where,
            )
        }
        private fun getCompareIntFromArgMapByIndex(
            mapArgMapList: List<Map<MapArg.MapArgKey, String?>>,
            argNameToIndex: Pair<String, Int>,
            compareInt: Int,
            compareSignal: NumChecker.CompareSignal,
            where: String,
        ): Pair<Int, FuncCheckErr?> {
            val argName = argNameToIndex.first
            val argIndex = argNameToIndex.second
            return MapArg.getMapByIndex(
                argIndex,
                mapArgMapList,
            ).let { targetArgMap ->
                val (int, err) = getIntArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    where,
                )
                if(
                    err != null
                ) return int to err
                val minusErr = NumChecker.compare(
                    compareInt,
                    compareSignal,
                    int,
                    argNameToIndex.first,
                    where,
                )
                if(minusErr != null){
                    return int to minusErr
                }
                int to null
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

        fun getFileFromArgMapByName(
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
                getFileArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    where,
                )
            }
        }

        fun getFileFromArgMapByIndex(
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
                getFileArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    where,
                )
            }
        }

        private fun getFileArg(
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
                    || pathEntry == defaultNullMacroStr
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

        fun getDirFromArgMapByName(
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
                getDirArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR)
                        ?: defaultValueStr,
                    null.toString(),
                    targetArgMap,
                    where,
                )
            }
        }

        fun getDirFromArgMapByIndex(
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
                getDirArg(
                    argName,
                    targetArgMap?.get(FuncCheckerForSetting.MapArg.MapArgKey.VALUE_STR),
                    targetArgMap?.get(MapArg.MapArgKey.INDEX),
                    targetArgMap,
                    where,
                )
            }
        }

        private fun getDirArg(
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
                    File(pathEntry).isDirectory
                    || pathEntry == defaultNullMacroStr
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
                CheckTool.lightBlue,
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
                ArgType.FILE ->
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