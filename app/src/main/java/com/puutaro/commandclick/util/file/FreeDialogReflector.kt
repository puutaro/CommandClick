package com.puutaro.commandclick.util.file

//
//object FreeDialogReflector {
//
//    private val languageType = LanguageTypeSelects.JAVA_SCRIPT
//    private val languageTypeToSectionHolderMap =
//        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
//            languageType
//        )
//    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//    ) as String
//
//    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//    ) as String
//    private val commandSectionStart = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
//    ) as String
//    private val commandSectionEnd = languageTypeToSectionHolderMap?.get(
//        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
//    ) as String
//
//    fun reflect(
//        mainFannelReadSharePreffernceMap: Map<String, String>?,
//        freeDialogReadSharePreffernceMap: Map<String, String>?,
//    ) {
//        if(
//            mainFannelReadSharePreffernceMap.isNullOrEmpty()
//        ) return
//        val mainFannelAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
//            mainFannelReadSharePreffernceMap,
//            SharePrefferenceSetting.current_app_dir
//        )
//        val mainFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
//            mainFannelReadSharePreffernceMap,
//            SharePrefferenceSetting.current_fannel_name
//        )
//        val mainFannelCon = ReadText(
//            File(
//                mainFannelAppDirPath,
//                mainFannelName
//            ).absolutePath
//        ).readText()
//        if(
//            freeDialogReadSharePreffernceMap.isNullOrEmpty()
//        ) return
//        val freeDialogAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
//            freeDialogReadSharePreffernceMap,
//            SharePrefferenceSetting.current_app_dir
//        )
//        val freeDialogFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
//            freeDialogReadSharePreffernceMap,
//            SharePrefferenceSetting.current_fannel_name
//        )
//        val fDialogConList = ReadText(
//            File(
//                freeDialogAppDirPath,
//                freeDialogFannelName
//            ).absolutePath
//        ).textToList()
//        val fDialogCommandValCon = CommandClickVariables.substituteVariableListFromHolder(
//            fDialogConList,
//            commandSectionStart,
//            commandSectionEnd
//        )?.filter {
//            !it.startsWith(FDialogTempFile.jsDescPrefix)
//                    && it.isNotEmpty()
//        }?.joinToString("\t") ?: String()
//        val replaceSrcFannelConInSettingVal = CommandClickVariables.replaceVariableInHolder(
//            mainFannelCon,
//            fDialogCommandValCon,
//            settingSectionStart,
//            settingSectionEnd,
//        )
//        val replaceSrcFanneCon = CommandClickVariables.replaceVariableInHolder(
//            replaceSrcFannelConInSettingVal,
//            fDialogCommandValCon,
//            commandSectionStart,
//            commandSectionEnd,
//        )
////        FileSystems.writeFile(
////            UsePath.cmdclickSystemAppDirPath,
////            "reflect02.txt",
////            listOf(
////                "mainFannelAppDirPath: ${mainFannelAppDirPath}",
////                "mainFannelName: ${mainFannelName}",
////                "fDialogCommandValCon: ${fDialogCommandValCon}",
////                "mainFannelCon: ${mainFannelCon}",
////                "replaceSrcFannelConInSettingVal: ${replaceSrcFannelConInSettingVal}",
////                "replaceSrcFanneCon: ${replaceSrcFanneCon}",
////                "isDiff: ${replaceSrcFanneCon != mainFannelCon}",
////            ).joinToString("\n")
////        )
//        if (
//            replaceSrcFanneCon != mainFannelCon
//        ) {
//            FileSystems.writeFile(
//                File(
//                    mainFannelAppDirPath,
//                    mainFannelName
//                ).absolutePath,
//                replaceSrcFanneCon
//            )
//        }
//        copyDirectoryWithDeleteWithBackup(
//            mainFannelReadSharePreffernceMap,
//            freeDialogReadSharePreffernceMap,
//        )
//    }
//
//    private fun copyDirectoryWithDeleteWithBackup(
//        mainFannelReadSharePreffernceMap: Map<String, String>,
//        freeDialogReadSharePreffernceMap: Map<String, String>,
//    ){
//        val mainAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
//            mainFannelReadSharePreffernceMap,
//            SharePrefferenceSetting.current_app_dir
//        )
//        val mainFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
//            mainFannelReadSharePreffernceMap,
//            SharePrefferenceSetting.current_fannel_name
//        )
//        val freeDialogAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
//            freeDialogReadSharePreffernceMap,
//            SharePrefferenceSetting.current_app_dir
//        )
//        val freeDialogFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
//            freeDialogReadSharePreffernceMap,
//            SharePrefferenceSetting.current_fannel_name
//        )
//        val freeDialogFannelDirName = CcPathTool.makeFannelDirName(freeDialogFannelName)
//        val freeDialogFannelDirPath = File(freeDialogAppDirPath, freeDialogFannelDirName).absolutePath
//        val mainFannelDirName = CcPathTool.makeFannelDirName(mainFannelName)
//        val mainFannelDirPath = File(mainAppDirPath, mainFannelDirName).absolutePath
//        CoroutineScope(Dispatchers.IO).launch {
//            val buckupDirPath =
//                File(
//                    freeDialogAppDirPath,
//                    UsePath.clickBackupDirNameInAppDir
//                ).absolutePath
//            FileSystems.createDirs(buckupDirPath)
//            val buckupTargetDirPath =
//                File(buckupDirPath, mainFannelDirName).absolutePath
//            FileSystems.copyDirectory(
//                freeDialogFannelDirPath,
//                buckupTargetDirPath
//            )
//        }
//        val isMainFannelDir = mainAppDirPath
//            .startsWith("${UsePath.cmdclickAppDirPath}/")
//        if(isMainFannelDir) {
//            FileSystems.removeDir(
//                mainFannelDirPath
//            )
//        }
//        FileSystems.copyDirectory(
//            freeDialogFannelDirPath,
//            mainFannelDirPath
//        )
//    }
//}