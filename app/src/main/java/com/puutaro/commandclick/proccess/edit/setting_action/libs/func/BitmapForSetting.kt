package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionBitmapData
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.ColorTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.enums.EnumEntries

object BitmapForSetting {

    suspend fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >? {
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        }  ?: let {
            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                funcName
            )
            val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                methodNameStr
            )
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        val args =
            methodNameClass.args
        return withContext(Dispatchers.Main) {
            when (args) {
                is BitmapMethodArgClass.ColorAvArgs -> {
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
                    val importPath = FuncCheckerForSetting.Getter.getFileFromArgMapByIndex(
                        mapArgMapList,
                        args.importPathToKey,
                        where
                    ).let { importPathToErr ->
                        val funcErr = importPathToErr.second
                            ?: return@let importPathToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to funcErr
                    }
                    val key = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.keyToIndex,
                        where
                    ).let { keyToErr ->
                        val funcErr = keyToErr.second
                            ?: return@let keyToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to funcErr
                    }
                    val waitMill = FuncCheckerForSetting.Getter.getIntFromArgMapByIndex(
                        mapArgMapList,
                        args.waitMillToIndex,
                        where
                    ).let { keyToErr ->
                        val funcErr = keyToErr.second
                            ?: return@let keyToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                        ) to funcErr
                    }.let {
                        if(it <= 0) return@let args.defaultWaitMill
                        it
                    }
                    val delayMill = 50L
                    val waitLoopNum = waitMill / delayMill
                    var recieveBitmap: Bitmap? = null
                    for(i in 0..waitLoopNum) {
                        recieveBitmap = ImageActionBitmapData.get(
                            importPath,
                            key,
                        )
                        if(recieveBitmap != null) break
                        delay(delayMill)
                    }
                    if(recieveBitmap == null) {
                        val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errBrown,
                            funcName
                        )
                        val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            methodNameStr
                        )
                        return@withContext null to
                                FuncCheckerForSetting.FuncCheckErr(
                                    "Color average cannot culc: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}"
                                )
                    }
                    val avColorStr =
                        ColorTool.calculateAverageColor(recieveBitmap).let {
                            ColorTool.colorToHexString(it)
                        }
                    val iamgeAcDatas = ImageActionBitmapData.gets()
//                    FileSystems.writeFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "ldata_get.txt").absolutePath,
//                        listOf(
//                            "importPath: ${importPath}",
//                            "key: ${key}",
//                            "recieveBitmap: ${recieveBitmap}",
//                            "ImageActionBitmapData.gets(): ${iamgeAcDatas}",
//                            "avColorStr: ${avColorStr}",
//                        ).joinToString("\n")
//                    )
                    iamgeAcDatas.forEachIndexed {
                            index, tri ->
                        val curBitmap = tri.third
                        if(curBitmap == null) return@forEachIndexed
                        FileSystems.writeFromByteArray(
                            File(UsePath.cmdclickDefaultAppDirPath, "ldata_get_${index}.png").absolutePath,
                            BitmapTool.convertBitmapToByteArray(curBitmap)
                        )
                    }
                    Pair(
                        avColorStr,
                        null
                    ) to null
                }
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: BitmapMethodArgClass,
    ){
        COLOR_AV("colorAv", BitmapMethodArgClass.ColorAvArgs),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class BitmapMethodArgClass {
            data object ColorAvArgs : BitmapMethodArgClass(), ArgType {
            override val entries = ColorAvArgsEnum.entries
            val defaultWaitMill = 1000
            val importPathToKey = Pair(
                ColorAvArgsEnum.IMPORT_PATH.key,
                ColorAvArgsEnum.IMPORT_PATH.index
            )
            val keyToIndex = Pair(
                ColorAvArgsEnum.KEY.key,
                ColorAvArgsEnum.KEY.index
            )
            val waitMillToIndex = Pair(
                ColorAvArgsEnum.WAIT_TIME.key,
                ColorAvArgsEnum.WAIT_TIME.index
            )
            enum class ColorAvArgsEnum(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                IMPORT_PATH("importPath", 0, FuncCheckerForSetting.ArgType.FILE),
                KEY("key", 1, FuncCheckerForSetting.ArgType.STRING),
                WAIT_TIME("waitMill", 2, FuncCheckerForSetting.ArgType.INT),
            }
        }
    }
}