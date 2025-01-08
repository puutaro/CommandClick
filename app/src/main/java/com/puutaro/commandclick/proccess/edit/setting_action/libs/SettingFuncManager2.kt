package com.puutaro.commandclick.proccess.edit.setting_action.libs

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ColorForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.DebugForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.EditForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ExitForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.FileSystemsForSettingHandler
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ListForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.LocalDatetimeForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.MathCulcForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.PathForSettingHandler
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.RndForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ShellToolManagerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.SystemInfoForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ToastForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.TsvToolForSetting
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor

object SettingFuncManager2 {

    private const val funcTypeAndMethodSeparatorDot = "."

    suspend fun handle(
        fragment: Fragment,
        funcTypeDotMethod: String,
        baseArgsPairList: List<Pair<String, String>>,
        busyboxExecutor: BusyboxExecutor?,
        editConstraintListAdapter: EditConstraintListAdapter?,
//        varNameToValueStrMap: Map<String, String?>,
    ): Pair<
            Pair<
                    String?,
                    SettingActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting2.FuncCheckErr?
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
            return null to FuncCheckerForSetting2.FuncCheckErr("Irregular func name: ${spanFuncTypeStr}")
        }
        val methodName = funcTypeAndMethodList.getOrNull(1)
            ?: let {
                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                    CheckTool.errRedCode,
                    funcTypeStr
                )
                return null to FuncCheckerForSetting2.FuncCheckErr("Method name not found: ${spanFuncTypeStr}")
            }
        return when(funcType){
            FuncType.FILE_SYSTEMS ->
                FileSystemsForSettingHandler.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.TOAST -> {
                ToastForSetting.handle(
                    fragment.context,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap
                )
            }
            FuncType.DEBUG -> {
                DebugForSetting.handle(
                    fragment.context,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap
                )
            }
            FuncType.EXIT ->
                ExitForSetting.handle(
                    funcTypeStr,
                    methodName
                )
            FuncType.PATH ->
                PathForSettingHandler.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.LOCAL_DATETIME ->
                LocalDatetimeForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.TSV_TOOL ->
                TsvToolForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.SHELL ->
                ShellToolManagerForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    busyboxExecutor,
//                    varNameToValueStrMap,
                )
            FuncType.EDIT ->
                EditForSetting.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
                    editConstraintListAdapter,
//                    varNameToValueStrMap,
                )
            FuncType.CULC ->
                MathCulcForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.COLOR -> {
                ColorForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap
                )
            }
            FuncType.LIST ->
                ListForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap
                )
            FuncType.RND ->
                RndForSetting.handle(
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
            FuncType.SYSTEM_INFO ->
                SystemInfoForSetting.handle(
                    fragment,
                    funcTypeStr,
                    methodName,
                    baseArgsPairList,
//                    varNameToValueStrMap,
                )
        }

    }

    private enum class FuncType(
        val key: String,
    ) {
        FILE_SYSTEMS("fileSystems"),
        TOAST("toast"),
        DEBUG("debug"),
        EXIT("exit"),
        PATH("path"),
        LOCAL_DATETIME("localDatetime"),
        TSV_TOOL("tsvTool"),
        SHELL("shell"),
        EDIT("edit"),
        CULC("culc"),
        COLOR("color"),
        LIST("list"),
        RND("rnd"),
        SYSTEM_INFO("systemInfo"),
    }

}