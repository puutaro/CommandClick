package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageFuncCheckerForImageSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import java.io.File

object FileSystemsForImageAction {
    fun handle(
        fragment: Fragment,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        varNameToBitmapMap: Map<String, Bitmap?>,
    ): Pair<
            Pair<
                    Bitmap?,
                    ImageActionKeyManager.ExitSignal?
                    >?,
            ImageFuncCheckerForImageSetting. FuncCheckErr?
            >? {
        val context =
            fragment.context
                ?: return Pair(Pair(null, null), null)
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
            MethodNameClass.SAVE -> {
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultIDebugAppDirPath, "image_save.txt").absolutePath,
//                    listOf(
//                        varNameToBitmapMap.map {
//                            "${it.key} height: ${it.value?.height} width: ${it.value?.width}"
//                        }.joinToString("\n")
//                    ).joinToString("\n\n")
//                )
                val savePath = argsList.get(0)
                val bitmapKey =
                    ImageActionKeyManager.BitmapVar.convertBitmapKey(
                        argsList.get(1)
                    )
                val bitmap = varNameToBitmapMap.get(bitmapKey)
                    ?: return null
               FileSystems.writeFromByteArray(
                   savePath,
                   BitmapTool.convertBitmapToByteArray(bitmap)
               )
                null
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, ImageFuncCheckerForImageSetting.ArgType>>,
    ){
        SAVE("save", readArgsNameToTypeList),
    }

    private val readArgsNameToTypeList = listOf(
        Pair("savePath", ImageFuncCheckerForImageSetting.ArgType.STRING),
        Pair("bitmapVarName", ImageFuncCheckerForImageSetting.ArgType.BITMAP_VAR_NAME),
    )


}
