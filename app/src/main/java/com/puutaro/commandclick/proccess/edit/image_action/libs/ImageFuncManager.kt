package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ArbForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.BlurForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ColorForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.CutForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.DebugForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.DelayForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.FileSystemsForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.IconForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.OpacityForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.OverlayForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.SizeForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.ViewForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.WallForImageAction
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor

object ImageFuncManager {

    private const val funcTypeAndMethodSeparatorDot = "."

    suspend fun handle(
        fragment: Fragment,
        funcTypeDotMethod: String,
        baseArgsPairList: List<Pair<String, String>>,
        busyboxExecutor: BusyboxExecutor?,
        editConstraintListAdapter: EditConstraintListAdapter?,
        varNameToBitmapMap: Map<String, Bitmap?>,
        imageView: AppCompatImageView?,
        requestBuilder: RequestBuilder<Drawable>?,
    ): Pair<
            Pair<
                    Bitmap?,
                    ImageActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >? {
        val funcTypeAndMethodList =
            funcTypeDotMethod.split(funcTypeAndMethodSeparatorDot)
        val funcTypeStr = funcTypeAndMethodList.first()
        val funcType = FuncType.entries.firstOrNull {
            it.key == funcTypeStr
        } ?: let {
            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                funcTypeStr
            )
            return null to FuncCheckerForSetting.FuncCheckErr("Irregular func name: ${spanFuncTypeStr}")
        }
        val methodName = funcTypeAndMethodList.getOrNull(1)
            ?: let {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    funcTypeStr
                )
                return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}")
            }
        return when(funcType){
            FuncType.ICON ->
                IconForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList
                )
            FuncType.FILE ->
                FileSystemsForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    varNameToBitmapMap,
                )
            FuncType.WALL ->
                WallForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                )
            FuncType.ARB ->
                ArbForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                )
            FuncType.DEBUG ->
                DebugForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    varNameToBitmapMap,
                )
            FuncType.DELAY ->
                DelayForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    varNameToBitmapMap,
                )
            FuncType.VIEW ->
                ViewForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    varNameToBitmapMap,
                    imageView,
                    requestBuilder
                )
            FuncType.CUT ->
                CutForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    varNameToBitmapMap,
                )
            FuncType.OVERLAY ->
                OverlayForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    varNameToBitmapMap,
                )
            FuncType.OPACITY ->
                OpacityForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    varNameToBitmapMap,
                )
            FuncType.BLUR ->
                BlurForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    varNameToBitmapMap,
                )
            FuncType.COLOR ->
                ColorForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    varNameToBitmapMap,
                )
            FuncType.SIZE ->
                SizeForImageAction.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    varNameToBitmapMap,
                )
        }

    }

    private enum class FuncType(
        val key: String,
    ) {
        ICON("icon"),
        FILE("file"),
        WALL("wall"),
        DEBUG("debug"),
        VIEW("view"),
        DELAY("delay"),
        ARB("arb"),
        CUT("cut"),
        OVERLAY("overlay"),
        OPACITY("opacity"),
        BLUR("blur"),
        COLOR("color"),
        SIZE("size")
    }

}