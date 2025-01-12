package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.DebugForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.FileSystemsForImageAction
import com.puutaro.commandclick.proccess.edit.image_action.libs.func.IconForImageAction
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
    ): Pair<
            Pair<
                    Bitmap?,
                    ImageActionKeyManager.BreakSignal?
                    >?,
            ImageFuncCheckerForImageSetting.FuncCheckErr?
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
            return null to ImageFuncCheckerForImageSetting.FuncCheckErr("Irregular func name: ${spanFuncTypeStr}")
        }
        val methodName = funcTypeAndMethodList.getOrNull(1)
            ?: let {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    funcTypeStr
                )
                return null to ImageFuncCheckerForImageSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}")
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
            FuncType.DEBUG ->
                DebugForImageAction.handle(
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
        DEBUG("debug"),
    }

}