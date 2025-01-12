package com.puutaro.commandclick.proccess.edit.setting_action.libs.func

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.func.EditComponentFunc
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.enums.EnumEntries

object EditForSetting {
    suspend fun handle(
        fragment: Fragment?,
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
//        FuncCheckerForSetting.checkArgs(
//            funcName,
//            methodNameStr,
//            methodNameClass.argsNameToTypeList,
//            argsPairList,
////            varNameToValueStrMap,
//        )?.let { argsCheckErr ->
//            return null to argsCheckErr
//        }
//        val argsList = argsPairList.map {
//            it.second
//        }
        val args =
            methodNameClass.args
        val terminalFragment = withContext(Dispatchers.Main) {
            TargetFragmentInstance.getCurrentTerminalFragmentFromFrag(
                fragment?.activity
            )
        } ?: return null to FuncCheckerForSetting.FuncCheckErr("Cannot get terminal fragment")
        return withContext(Dispatchers.Main) {
            when (args) {
                is EditMethodArgClass.GetSettingValueArgs -> {
                    val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                            index, formalArgsNameToType ->
                        Triple(
                            index,
                            formalArgsNameToType.key,
                            formalArgsNameToType.type,
                        )
                    }
                    val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                        formalArgIndexToNameToTypeList,
                        argsPairList
                    )
                    val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                        funcName,
                        methodNameStr,
                        argsPairList,
                        formalArgIndexToNameToTypeList
                    )
                    val targetVariableName = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.targetVariableNameKeyToIndex,
                        where
                    ).let { targetVariableNameToErr ->
                        val funcErr = targetVariableNameToErr.second
                            ?: return@let targetVariableNameToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    val srcFragmentStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                        mapArgMapList,
                        args.srcFragmentToIndex,
                        where
                    ).let { srcFragmentToErr ->
                        val funcErr = srcFragmentToErr.second
                            ?: return@let srcFragmentToErr.first
                        return@withContext Pair(
                            null,
                            SettingActionKeyManager.BreakSignal.EXIT_SIGNAL
                        ) to funcErr
                    }
                    Pair (
                        EditComponentFunc.getSettingValue(
                            terminalFragment,
                            targetVariableName,
                            srcFragmentStr,
                            editConstraintListAdapterArg,
                        ),
                        null
                    ) to null

                }
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: EditMethodArgClass,
    ){
        GET_SETTING_VALUE("getSettingValue", EditMethodArgClass.GetSettingValueArgs),
    }


    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class EditMethodArgClass {
        data object GetSettingValueArgs : EditMethodArgClass(), ArgType {
            override val entries = GetSettingValueEnumArgs.entries
            val targetVariableNameKeyToIndex = Pair(
                GetSettingValueEnumArgs.TARGET_VARIABLE_NAME.key,
                GetSettingValueEnumArgs.TARGET_VARIABLE_NAME.index
            )
            val srcFragmentToIndex = Pair(
                GetSettingValueEnumArgs.SRC_FRAGMENT.key,
                GetSettingValueEnumArgs.SRC_FRAGMENT.index
            )

            enum class GetSettingValueEnumArgs(
                val key: String,
                val index: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                TARGET_VARIABLE_NAME("targetVariableName", 0, FuncCheckerForSetting.ArgType.STRING),
                SRC_FRAGMENT("srcFragment", 1, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }
}
