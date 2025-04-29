package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import androidx.fragment.app.Fragment
import com.hoko.blur.HokoBlur
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.enums.EnumEntries

object BlurForImageAction {
    fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        varNameToBitmapMap: Map<String, Bitmap?>?,
    ): Pair<
            Pair<
                    Bitmap?,
                    ImageActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >? {
        if(context == null) return null
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
            return null to
                    FuncCheckerForSetting.FuncCheckErr(
                        "Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}"
                    )
        }
        val args =
            methodNameClass.args
        return when(args) {
            is BlurMethodArgClass.setArgs -> {
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
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByIndex(
                    mapArgMapList,
                    args.bitmapKeyToIndex,
                    varNameToBitmapMap,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                } ?: return null
                val radiusInt = FuncCheckerForSetting.Getter.getZeroLargerIntFromArgMapByIndex(
                    mapArgMapList,
                    args.radiusKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val samplingFloat = FuncCheckerForSetting.Getter.getZeroLargerFloatFromArgMapByIndex(
                    mapArgMapList,
                    args.samplingKeyToIndex,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val returnBitmap = BlurManager.set(
                    context,
                    bitmap,
                    radiusInt,
                    samplingFloat,
                    where,
                ).let {
                        (returnBitmapSrc, err) ->
                    if(
                        err == null
                    ) return@let returnBitmapSrc
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to err
                }
                Pair(
                    returnBitmap,
                    null
                ) to null
            }
        }
    }

    private object BlurManager {
        fun set(
            context: Context,
            bitmap: Bitmap,
            radius: Int,
            samplingFloat: Float,
            where: String,
        ): Pair<Bitmap?, FuncCheckerForSetting.FuncCheckErr?> {
            return try {
//                val gpuImage = GPUImage(context)
//                gpuImage.setFilter(GPUImageGaussianBlurFilter(radius.toFloat()))
//                val overlayBitmap = gpuImage.getBitmapWithFilterApplied(bitmap)
//                gpuImage.saveToPictures("GPUImage", "ImageWithFilter.jpg", null)


                val overlayBitmap = HokoBlur.with(context)
                    .scheme(HokoBlur.SCHEME_JAVA) //different implementation, RenderScript、OpenGL、Native(default) and Java
                    .mode(HokoBlur.MODE_STACK) //blur algorithms，Gaussian、Stack(default) and Box
                    .radius(radius) //blur radius，max=25，default=5
                    .sampleFactor(samplingFloat) //scale factor，if factor=2，the width and height of a bitmap will be scale to 1/2 sizes，default=5
                    .forceCopy(true) //If scale factor=1.0f，the origin bitmap will be modified. You could set forceCopy=true to avoid it. default=false
//                    .translateX(150)//add x axis offset when blurring
//                    .translateY(150)//add y axis offset when blurring
                    .processor() //build a blur processor
                    .blur(bitmap)
                FileSystems.writeFromByteArray(
                    File(UsePath.cmdclickDefaultAppDirPath, "lblur00.png").absolutePath,
                    BitmapTool.convertBitmapToByteArray(bitmap)
                )
                Pair(
                    overlayBitmap,
                    null
                )
            } catch (e: Exception) {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    e.toString()
                )
                return null to FuncCheckerForSetting.FuncCheckErr("${e}: ${spanFuncTypeStr}, ${where}")
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: BlurMethodArgClass,
    ){
        SET("set", BlurMethodArgClass.setArgs),
    }
    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }
    private sealed class BlurMethodArgClass {
        data object setArgs : BlurMethodArgClass(), ArgType {
            override val entries = AjustArgs.entries
            val bitmapKeyToIndex = Pair(
                AjustArgs.BITMAP.key,
                AjustArgs.BITMAP.index
            )
            val radiusKeyToIndex = Pair(
                AjustArgs.RADIUS.key,
                AjustArgs.RADIUS.index
            )
            val samplingKeyToIndex = Pair(
                AjustArgs.SAMPLING.key,
                AjustArgs.SAMPLING.index
            )
            enum class AjustArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", 0, FuncCheckerForSetting.ArgType.BITMAP),
                RADIUS("radius", 1, FuncCheckerForSetting.ArgType.INT),
                SAMPLING("sampling", 2, FuncCheckerForSetting.ArgType.FLOAT),
            }
        }
    }
}
