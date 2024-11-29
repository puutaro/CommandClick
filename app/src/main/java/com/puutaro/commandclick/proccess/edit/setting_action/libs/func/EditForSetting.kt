package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.proccess.edit.func.EditComponentFunc
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EditForSetting {
    suspend fun handle(
        fragment: Fragment,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
        editComponentListAdapterArg: EditComponentListAdapter?,
    ): Pair<String?, FuncCheckerForSetting.FuncCheckErr?> {
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
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        FuncCheckerForSetting.checkArgs(
            funcName,
            methodNameStr,
            methodNameClass.argsNameList,
            argsPairList
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
        } ?: return null to FuncCheckerForSetting.FuncCheckErr("Cannot get terminal fragment")
        val settingValueStr = withContext(Dispatchers.Main) {
            when (methodNameClass) {
                MethodNameClass.GET_SETTING_VALUE -> {
                    val firstArg = argsList.get(0)
                    val secondArg = argsList.get(0)
                    EditComponentFunc.getSettingValue(
                        terminalFragment,
                        firstArg,
                        secondArg,
                        editComponentListAdapterArg,
                    )

                }
            }
        }
        return settingValueStr to null
    }

    enum class MethodNameClass(
        val str: String,
        val argsNameList: List<String>,
    ) {
        GET_SETTING_VALUE("getSettingValue", getSettingValueArgsNameList),
    }

    private val getSettingValueArgsNameList = listOf(
        "targetVariableName",
        "srcFragment",
    )
}
