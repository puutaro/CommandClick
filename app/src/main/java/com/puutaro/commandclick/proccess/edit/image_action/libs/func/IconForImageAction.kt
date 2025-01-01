package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.graphics.Bitmap
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageFuncCheckerForImageSetting

object IconForImageAction {
    fun handle(
        fragment: Fragment,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>
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
            return null to ImageFuncCheckerForImageSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        ImageFuncCheckerForImageSetting.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.argsNameToTypeList,
            argsPairList,
            null,
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
                val iconMacroStr = argsList.get(0)
                val width = argsList.get(1).toInt()
                val height = argsList.get(2).toInt()
               val bitmap = CmdClickIcons.entries.firstOrNull {
                   it.str == iconMacroStr
               }?.let {
                   AppCompatResources.getDrawable(
                       context,
                       it.id,
                   )?.toBitmap(
                       width,
                       height
                   )
               }
                Pair(
                    bitmap,
                    null
                ) to null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, ImageFuncCheckerForImageSetting.ArgType>>,
    ){
        MAKE("make", makeArgsNameToTypeList),
    }

    private val makeArgsNameToTypeList = listOf(
        Pair("iconMacroStr", ImageFuncCheckerForImageSetting.ArgType.STRING),
        Pair("width", ImageFuncCheckerForImageSetting.ArgType.INT),
        Pair("height", ImageFuncCheckerForImageSetting.ArgType.INT),
    )


}
