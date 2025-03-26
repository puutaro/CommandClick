package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.str.ImageVarMarkTool
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object ViewForImageAction {

    private const val intDefaultNullMacroStr = 0.toString()
//    private const val defaultNullMacroStr = FuncCheckerForSetting.defaultNullMacroStr

    suspend fun handle(
        context: Context?,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        varNameToBitmapMap: Map<String, Bitmap?>?,
        imageView: AppCompatImageView?,
        requestBuilderSrc: RequestBuilder<Drawable>?
    ): Pair<
            Pair<
                    Bitmap?,
                    ImageActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >?{
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
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        val args =
            methodNameClass.args
        return when(args){
            is ViewMethodArgClass.BySingleArgs -> {
                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                        index, formalArgsNameToType ->
                    Triple(
                        index,
                        formalArgsNameToType.key,
                        formalArgsNameToType.type,
                    )
                }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByName(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
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
                val fadeInMill = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.fadeInMillKeyToDefaultValueStr,
                    where
                ).let { fadeInMillToErr ->
                    val funcErr = fadeInMillToErr.second
                        ?: return@let fadeInMillToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                withContext(Dispatchers.Main) {
                    ImageSetter.bySingle (
                        imageView,
                        bitmap,
                        requestBuilderSrc,
                        fadeInMill
                    )
                }
                val returnBitmap =
                    varNameToBitmapMap?.get(ImageActionKeyManager.BitmapVar.itPronoun)
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is ViewMethodArgClass.ByBlurSingleArgs -> {
                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                        index, formalArgsNameToType ->
                    Triple(
                        index,
                        formalArgsNameToType.key,
                        formalArgsNameToType.type,
                    )
                }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByName(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val bitmap = FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
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
                val fadeInMill = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.fadeInMillKeyToDefaultValueStr,
                    where
                ).let { fadeInMillToErr ->
                    val funcErr = fadeInMillToErr.second
                        ?: return@let fadeInMillToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val radius = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.radiusKeyToDefaultValueStr,
                    where
                ).let { radiusToErr ->
                    val funcErr = radiusToErr.second
                        ?: return@let radiusToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val sampling = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.samplingKeyToDefaultValueStr,
                    where
                ).let { samplingToErr ->
                    val funcErr = samplingToErr.second
                        ?: return@let samplingToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                withContext(Dispatchers.Main){
                    ImageSetter.byBlurSingle(
                        imageView,
                        bitmap,
                        fadeInMill,
                        radius,
                        sampling,
                    )
                }
                val returnBitmap =
                    varNameToBitmapMap?.get(ImageActionKeyManager.BitmapVar.itPronoun)
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
            is ViewMethodArgClass.ByMultiArgs -> {
                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                        index, formalArgsNameToType ->
                    Triple(
                        index,
                        formalArgsNameToType.key,
                        formalArgsNameToType.type,
                    )
                }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByName(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                FuncCheckerForSetting.Getter.getBitmapFromArgMapByName(
                    mapArgMapList,
                    args.bitmapKeyToDefaultValueStr,
                    varNameToBitmapMap,
                    where
                ).let { bitmapToErr ->
                    val funcErr = bitmapToErr.second
                        ?: return@let bitmapToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                val delay = FuncCheckerForSetting.Getter.getIntFromArgMapByName(
                    mapArgMapList,
                    args.delayKeyToDefaultValueStr,
                    where
                ).let { delayToErr ->
                    val funcErr = delayToErr.second
                        ?: return@let delayToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL
                    ) to funcErr
                }
                ImageSetter.byMulti(
                    imageView,
                    args,
                    argsPairList,
                    varNameToBitmapMap,
                    delay,
                    where,
                )?.let {
                    funcErr ->
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.ERR_EXIT_SIGNAL,
                    ) to funcErr
                }
                val returnBitmap =
                    varNameToBitmapMap?.get(ImageActionKeyManager.BitmapVar.itPronoun)
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
        }
    }

    private object ImageSetter {
        fun bySingle (
            imageView: AppCompatImageView?,
            bitmap: Bitmap,
            requestBuilderSrc: RequestBuilder<Drawable>?,
            fadeInMilli: Int
        ){
            if(
                imageView == null
            ) return
            val imageViewContext = imageView.context
            val requestBuilder =
                requestBuilderSrc
                    ?: Glide.with(imageViewContext)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
            when (fadeInMilli > intDefaultNullMacroStr.toInt()) {
                true -> Glide.with(imageViewContext)
                    .load(bitmap)
                    .transition(DrawableTransitionOptions.withCrossFade(fadeInMilli))
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .thumbnail(requestBuilder)
                    .into(imageView)

                else -> Glide.with(imageViewContext)
                    .load(bitmap)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .dontAnimate()
                    .thumbnail(requestBuilder)
                    .into(imageView)
            }
        }

        fun byBlurSingle(
            imageView: AppCompatImageView?,
            bitmap: Bitmap,
            fadeInMilli: Int,
            blurRadius: Int,
            blurSampling: Int,
        ){
            if(
                imageView == null
            ) return
            val imageViewContext = imageView.context
            when(fadeInMilli > intDefaultNullMacroStr.toInt()) {
                true -> Blurry.with(imageViewContext)
                    .radius(blurRadius)
                    .sampling(blurSampling)
                    .async()
                    .animate(fadeInMilli)
                    .from(bitmap)
                    .into(imageView)
                else -> Blurry.with(imageViewContext)
                    .radius(blurRadius)
                    .sampling(blurSampling)
                    .async()
                    .from(bitmap)
                    .into(imageView)
            }
        }

        suspend fun byMulti(
            imageView: AppCompatImageView?,
            byMultiArgs: ViewMethodArgClass.ByMultiArgs,
            argsPairList: List<Pair<String, String>>,
            varNameToBitmapMap: Map<String, Bitmap?>?,
            delay: Int,
            where: String,
        ): FuncCheckerForSetting.FuncCheckErr? {
            if(
                imageView == null
            ) return null
            val imageViewContext = imageView.context
            val animationDrawable = AnimationDrawable()
            val bitmapArgName = byMultiArgs.bitmapKeyToDefaultValueStr.first
            val bitmapList =
                argsPairList.filter {
                    (argName, _) ->
                    argName == bitmapArgName
                }.map {
                        (_, bitmapVarMark) ->
                    if(
                        !ImageVarMarkTool.matchBitmapVarName(bitmapVarMark)
                    ) {
                        val spanBitmapVarMark = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            bitmapVarMark
                        )
                        return FuncCheckerForSetting.FuncCheckErr("Irregular bitmap var mark: $spanBitmapVarMark, ${where}")
                    }
                    val bitmapKey = ImageVarMarkTool.convertBitmapKey(bitmapVarMark)
                    varNameToBitmapMap?.get(bitmapKey) ?: let {
                        val spanBitmapVarMark = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                            CheckTool.errRedCode,
                            bitmapVarMark
                        )
                        return FuncCheckerForSetting.FuncCheckErr("Not exit bitmap:  var mark $spanBitmapVarMark, ${where}")
                    }
                }
            bitmapList.forEach {
                animationDrawable.addFrame(
                    BitmapDrawable(imageViewContext.resources, it),
                    delay
                )
            }
            animationDrawable.isOneShot = false
            withContext(Dispatchers.Main) {
                imageView.setImageDrawable(animationDrawable)
                animationDrawable.start()
            }
            return null
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: ViewMethodArgClass,
    ){
        BY_SINGLE("bySingle", ViewMethodArgClass.BySingleArgs),
        BY_BLUR_SINGLE_("byBlurSingle", ViewMethodArgClass.ByBlurSingleArgs),
        BY_MUTI("byMulti", ViewMethodArgClass.ByMultiArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ViewMethodArgClass {
        data object BySingleArgs : ViewMethodArgClass(), ArgType {
            override val entries = BySingleEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                BySingleEnumArgs.BITMAP.key,
                BySingleEnumArgs.BITMAP.defaultValueStr
            )
            val fadeInMillKeyToDefaultValueStr = Pair(
                BySingleEnumArgs.FADE_IN_MILL.key,
                BySingleEnumArgs.FADE_IN_MILL.defaultValueStr
            )
            enum class BySingleEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                FADE_IN_MILL("fadeInMill", intDefaultNullMacroStr, FuncCheckerForSetting.ArgType.INT),
            }
        }
        data object ByBlurSingleArgs : ViewMethodArgClass(), ArgType {
            override val entries = ByBlurSingleEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                ByBlurSingleEnumArgs.BITMAP.key,
                ByBlurSingleEnumArgs.BITMAP.defaultValueStr
            )
            val fadeInMillKeyToDefaultValueStr = Pair(
                ByBlurSingleEnumArgs.FADE_IN_MILL.key,
                ByBlurSingleEnumArgs.FADE_IN_MILL.defaultValueStr
            )
            val radiusKeyToDefaultValueStr = Pair(
                ByBlurSingleEnumArgs.RADIUS.key,
                ByBlurSingleEnumArgs.RADIUS.defaultValueStr
            )
            val samplingKeyToDefaultValueStr = Pair(
                ByBlurSingleEnumArgs.SAMPLING.key,
                ByBlurSingleEnumArgs.SAMPLING.defaultValueStr
            )
            enum class ByBlurSingleEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                FADE_IN_MILL("fadeInMill", intDefaultNullMacroStr, FuncCheckerForSetting.ArgType.INT),
                RADIUS("radius", intDefaultNullMacroStr, FuncCheckerForSetting.ArgType.INT),
                SAMPLING("sampling", intDefaultNullMacroStr, FuncCheckerForSetting.ArgType.INT),

            }
        }
        data object ByMultiArgs : ViewMethodArgClass(), ArgType {
            override val entries = ByMultiEnumArgs.entries
            val bitmapKeyToDefaultValueStr = Pair(
                ByMultiEnumArgs.BITMAP.key,
                ByMultiEnumArgs.BITMAP.defaultValueStr
            )
            val delayKeyToDefaultValueStr = Pair(
                ByMultiEnumArgs.DELAY.key,
                ByMultiEnumArgs.DELAY.defaultValueStr
            )

            enum class ByMultiEnumArgs(
                val key: String,
                val defaultValueStr: String?,
                val type: FuncCheckerForSetting.ArgType,
            ){
                BITMAP("bitmap", null, FuncCheckerForSetting.ArgType.BITMAP),
                DELAY("delay", null, FuncCheckerForSetting.ArgType.INT),
            }
        }
    }
}