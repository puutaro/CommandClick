package com.puutaro.commandclick.proccess.edit.setting_action.libs

import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ExitForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.FileSystemsForSettingHandler
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.LocalDatetimeForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.PathForSettingHandler
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ShellToolManagerForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.ToastForSetting
import com.puutaro.commandclick.proccess.edit.setting_action.libs.func.TsvToolForSetting
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor

object SettingFuncManager {

    private const val funcTypeAndMethodSeparatorDot = "."

    fun handle(
        funcTypeDotMethod: String,
        baseArgsPairList: List<Pair<String, String>>,
        busyboxExecutor: BusyboxExecutor?
    ): String? {
        val funcTypeAndMethodList =
            funcTypeDotMethod.split(funcTypeAndMethodSeparatorDot)
        val funcTypeStr = funcTypeAndMethodList.first()
        val funcType = FuncType.entries.firstOrNull {
            it.key == funcTypeStr
        } ?: return null
        val methodName = funcTypeAndMethodList.getOrNull(1)
            ?: return null
        return when(funcType){
            FuncType.FILE_SYSTEMS ->
                FileSystemsForSettingHandler.handle(
                    methodName,
                    baseArgsPairList
                )
            FuncType.TOAST -> {
                ToastForSetting.handle(
                    methodName,
                    baseArgsPairList,
                )
                null
            }
            FuncType.EXIT ->
                ExitForSetting.handle(
                    methodName
                )
            FuncType.PATH ->
                PathForSettingHandler.handle(
                    methodName,
                    baseArgsPairList,
                )
            FuncType.LOCAL_DATETIME ->
                LocalDatetimeForSetting.handle(
                    methodName,
                    baseArgsPairList
                )
            FuncType.TSV_TOOL ->
                TsvToolForSetting.handle(
                    methodName,
                    baseArgsPairList
                )
            FuncType.SHELL -> {
                ShellToolManagerForSetting.handle(
                    methodName,
                    baseArgsPairList,
                    busyboxExecutor
                )
            }
        }

    }

    private enum class FuncType(
        val key: String,
    ) {
        FILE_SYSTEMS("fileSystems"),
        TSV_TOOL("tsvTool"),
        PATH("path"),
        TOAST("toast"),
        EXIT("exit"),
        LOCAL_DATETIME("localDatetime"),
        SHELL("shell"),
    }

}