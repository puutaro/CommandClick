package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.func.EditComponentFunc
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting2
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EditForSetting {
    suspend fun handle(
        fragment: Fragment,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        editConstraintListAdapterArg: EditConstraintListAdapter?,
//        varNameToValueStrMap: Map<String, String?>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
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
            methodNameClass.argsNameToTypeList,
            argsPairList,
//            varNameToValueStrMap,
        )?.let { argsCheckErr ->
            return null to argsCheckErr
        }
        val argsList = argsPairList.map {
            it.second
        }
        val terminalFragment = withContext(Dispatchers.Main) {
            TargetFragmentInstance.getCurrentTerminalFragmentFromFrag(
                fragment.activity
            )
        } ?: return null to FuncCheckerForSetting2.FuncCheckErr("Cannot get terminal fragment")
        val settingValueStr = withContext(Dispatchers.Main) {
            when (methodNameClass) {
                MethodNameClass.GET_SETTING_VALUE -> {
                    val firstArg = argsList.get(0)
                    val secondArg = argsList.get(0)
                    EditComponentFunc.getSettingValue(
                        terminalFragment,
                        firstArg,
                        secondArg,
                        editConstraintListAdapterArg,
                    )

                }
            }
        }
        return Pair(
                settingValueStr,
                null,
            ) to null
    }

    enum class MethodNameClass(
        val str: String,
        val argsNameToTypeList: List<Pair<String, FuncCheckerForSetting2.ArgType>>,
    ) {
        GET_SETTING_VALUE("getSettingValue", getSettingValueArgsNameToTypeList),
    }

    private val getSettingValueArgsNameToTypeList = listOf(
        Pair("targetVariableName", FuncCheckerForSetting2.ArgType.STRING),
        Pair("srcFragment", FuncCheckerForSetting2.ArgType.STRING),
    )
}
