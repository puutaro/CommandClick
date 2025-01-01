package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageFuncCheckerForImageSetting

object DebugForImageAction {
    fun handle(
        fragment: Fragment,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        varNameToBitmapMap: Map<String, Bitmap?>?,
    ): Pair<
            Pair<
                    Bitmap?,
                    ImageActionKeyManager.ExitSignal?
                    >?,
            ImageFuncCheckerForImageSetting. FuncCheckErr?
            >? {
        val context =
            fragment.context
                ?: return null
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
                    ImageFuncCheckerForImageSetting.FuncCheckErr(
                        "Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}"
                    )
        }
        ImageFuncCheckerForImageSetting.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.argsNameToTypeList,
            argsPairList,
            varNameToBitmapMap,
        )?.let {
                argsCheckErr ->
            return null to argsCheckErr
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "settingCheck.txt").absolutePath,
//            listOf(
//                "isErr: ${isErr}",
//            ).joinToString("\n")
//        )
        val argsList = argsPairList.map {
            it.second
        }
        return when(methodNameClass){
            MethodNameClass.MAKE -> {
                val bitmapKey =
                    ImageActionKeyManager.BitmapVar.convertBitmapKey(
                        argsList.get(0)
                    )
                val bitmap = varNameToBitmapMap?.get(bitmapKey)
                    ?: return null
                Pair(
                    bitmap,
                    null
                ) to null
            }
            MethodNameClass.NULL -> {
                Pair(
                    null,
                    null
                ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, ImageFuncCheckerForImageSetting.ArgType>>?,
    ){
        MAKE("reflect", makeArgsNameToTypeList),
        NULL("null", null)
    }

    private val makeArgsNameToTypeList = listOf(
        Pair("bitmapVarName", ImageFuncCheckerForImageSetting.ArgType.BITMAP_VAR_NAME),
    )


}
