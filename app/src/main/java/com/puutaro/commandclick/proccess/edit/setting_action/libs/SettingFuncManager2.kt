package com.puutaro.commandclick.proccess.edit.setting_action.libs

//object SettingFuncManager {
//
//    private const val funcTypeAndMethodSeparatorDot = "."
//
//    suspend fun handle(
//        fragment: Fragment,
//        funcTypeDotMethod: String,
//        baseArgsPairList: List<Pair<String, String>>,
//        busyboxExecutor: BusyboxExecutor?,
//        editConstraintListAdapter: EditConstraintListAdapter?,
////        varNameToValueStrMap: Map<String, String?>,
//    ): Pair<
//            Pair<
//                    String?,
//                    SettingActionKeyManager.BreakSignal?
//                    >?,
//            FuncCheckerForSetting.FuncCheckErr?
//            >? {
//        val funcTypeAndMethodList =
//            funcTypeDotMethod.split(funcTypeAndMethodSeparatorDot)
//        val funcTypeStr = funcTypeAndMethodList.first()
//        val funcType = FuncType.entries.firstOrNull {
//            it.key == funcTypeStr
//        } ?: let {
//            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                CheckTool.errRedCode,
//                funcTypeStr
//            )
//            return null to FuncCheckerForSetting.FuncCheckErr("Irregular func name: ${spanFuncTypeStr}")
//        }
//        val methodName = funcTypeAndMethodList.getOrNull(1)
//            ?: let {
//                val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//                    CheckTool.errRedCode,
//                    funcTypeStr
//                )
//                return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: ${spanFuncTypeStr}")
//            }
//        return when(funcType){
//            FuncType.FILE_SYSTEMS ->
//                FileSystemsForSettingHandler.handle(
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap,
//                )
//            FuncType.TOAST -> {
//                ToastForSetting.handle(
//                    fragment.context,
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap
//                )
//            }
//            FuncType.DEBUG -> {
//                DebugForSetting.handle(
//                    fragment.context,
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap
//                )
//            }
//            FuncType.EXIT ->
//                ExitForSetting.handle(
//                    funcTypeStr,
//                    methodName
//                )
//            FuncType.PATH ->
//                PathForSettingHandler.handle(
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap,
//                )
//            FuncType.LOCAL_DATETIME ->
//                LocalDatetimeForSetting.handle(
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap,
//                )
//            FuncType.TSV_TOOL ->
//                TsvToolForSetting.handle(
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap,
//                )
//            FuncType.SHELL ->
//                ShellToolManagerForSetting.handle(
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
//                    busyboxExecutor,
////                    varNameToValueStrMap,
//                )
//            FuncType.EDIT ->
//                EditForSetting.handle(
//                    fragment,
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
//                    editConstraintListAdapter,
////                    varNameToValueStrMap,
//                )
//            FuncType.CULC ->
//                MathCulcForSetting.handle(
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap,
//                )
//            FuncType.COLOR -> {
//                ColorForSetting.handle(
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap
//                )
//            }
//            FuncType.LIST ->
//                ListForSetting.handle(
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap
//                )
//            FuncType.RND ->
//                RndForSetting.handle(
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap,
//                )
//            FuncType.SYSTEM_INFO ->
//                SystemInfoForSetting.handle(
//                    fragment,
//                    funcTypeStr,
//                    methodName,
//                    baseArgsPairList,
////                    varNameToValueStrMap,
//                )
//        }
//
//    }
//
//    private enum class FuncType(
//        val key: String,
//    ) {
//        FILE_SYSTEMS("fileSystems"),
//        TOAST("toast"),
//        DEBUG("debug"),
//        EXIT("exit"),
//        PATH("path"),
//        LOCAL_DATETIME("localDatetime"),
//        TSV_TOOL("tsvTool"),
//        SHELL("shell"),
//        EDIT("edit"),
//        CULC("culc"),
//        COLOR("color"),
//        LIST("list"),
//        RND("rnd"),
//        SYSTEM_INFO("systemInfo"),
//    }
//
//}