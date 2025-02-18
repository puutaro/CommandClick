package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object ScreenForSetting {
    suspend fun handle(
        fragmentActivity: FragmentActivity?,
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
        return withContext(Dispatchers.Main) {
            when (args) {
                is ScreenMethodArgClass.HeightArgs -> {
                    val height = withContext(Dispatchers.Main){
                        ScreenSizeCalculator.pxHeight(
                            fragmentActivity
                        )
                    }
                    Pair(
                        height.toString(),
                        null
                    ) to null
                }
                is ScreenMethodArgClass.WidthArgs -> {
                    val width = withContext(Dispatchers.Main){
                        ScreenSizeCalculator.pxWidth(
                            fragmentActivity
                        )
                    }
                    Pair(
                        width.toString(),
                        null
                    ) to null
                }
            }
        }
    }


    private enum class MethodNameClass(
        val str: String,
        val args: ScreenMethodArgClass,
    ){
        HEIGHT(
            "height",
            ScreenMethodArgClass.HeightArgs,
        ),
        WIDTH(
            "width",
            ScreenMethodArgClass.WidthArgs,
        ),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class ScreenMethodArgClass {
        data object HeightArgs : ScreenMethodArgClass()
        data object WidthArgs : ScreenMethodArgClass()
    }
}
