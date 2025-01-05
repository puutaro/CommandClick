package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SystemInfoForSetting {
    suspend fun handle(
        fragment: Fragment,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
//        varNameToValueStrMap: Map<String, String?>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.ExitSignal?
                    >?,
            FuncCheckerForSetting2.FuncCheckErr?
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
            return null to FuncCheckerForSetting2.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        FuncCheckerForSetting2.checkArgs(
            funcName,
            methodNameStr,
            null,
            argsPairList,
//            varNameToValueStrMap,
        )?.let { argsCheckErr ->
            return null to argsCheckErr
        }
        val settingValueStr = withContext(Dispatchers.Main) {
            when (methodNameClass) {
                MethodNameClass.GET_BACKSTACK_COUNT -> {
                    fragment
                        .activity
                        ?.supportFragmentManager
                        ?.backStackEntryCount
                        ?.toString()
                        ?: "0"
                }
            }
        }
        return Pair(
            settingValueStr,
            null
        ) to null
    }

    enum class MethodNameClass(
        val str: String,
    ) {
        GET_BACKSTACK_COUNT("getBackstackCount"),
    }

}
