package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.content.Context
import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.IfErrManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingActionErrLogger
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.runBlocking

class ImageReturnExecutor {

    private val valueSeparator = ImageActionKeyManager.valueSeparator
    private val outputReturnSignal =
        ImageActionKeyManager.ImageReturnManager.OutputReturn.OUTPUT_RETURN
    private val returnSignal = ImageActionKeyManager.BreakSignal.RETURN_SIGNAL
    private val iIf = ImageActionKeyManager.ImageReturnManager.ImageReturnKey.I_IF

    suspend fun exec(
        context: Context?,
        imageActionExitManager: ImageActionData.ImageActionExitManager,
        mainSubKeyPairList: List<Pair<String, Map<String, String>>>,
        returnBitmap: Bitmap?,
        keyToSubKeyConWhere: String,
    ): Pair<
            Pair<
                    ImageActionKeyManager.ImageReturnManager.OutputReturn,
                    Bitmap?
                    >?,
            ImageActionKeyManager.BreakSignal?
            >? {
//        val isIIf = mainSubKeyPairList.any {
//            val mainSubKey = it.first
//            mainSubKey == iIf.key
//        }
        val returnKeyValueStrPair = Pair(
            outputReturnSignal,
            returnBitmap,
        )
        val iIfKey = ImageActionKeyManager.ImageReturnManager.ImageReturnKey.I_IF.key
        val ifMapList = mainSubKeyPairList.filter {
                mainSubKeyPair ->
            val mainSubKey = mainSubKeyPair.first
//            val mainSubKeyMapSrc = mainSubKeyPair.second
            mainSubKey == iIfKey
        }
        IfErrManager.isMultipleSpecifyErr(
            context,
            ifMapList.size,
            iIfKey,
            keyToSubKeyConWhere,
        ).let {
                isMultipleSpecifyErr ->
            if(
                !isMultipleSpecifyErr
            ) return@let
            imageActionExitManager.setExit()
            return null to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
        }
        val ifMap =
            ifMapList
                .firstOrNull()
                ?.second
        if(ifMap.isNullOrEmpty()){
            return Pair(
                returnKeyValueStrPair,
                returnSignal
            )
        }
        val isReturnToErrType = let {
            val argsPairList = CmdClickMap.createMap(
                ifMap.get(
                    ImageActionKeyManager.ImageSubKey.ARGS.key
                ),
                valueSeparator
            ).filter {
                it.first.isNotEmpty()
            }
            SettingIfManager.handle(
                iIfKey,
//                        judgeTargetStr,
                argsPairList,
                null
            )
        }
        val errType = isReturnToErrType.second
        if(errType != null){
            runBlocking {
                ImageActionErrLogger.sendErrLog(
                    context,
                    ImageActionErrLogger.ImageActionErrType.I_IF,
                    errType.errMessage,
                    keyToSubKeyConWhere
                )
            }
//                        isNext = false
            imageActionExitManager.setExit()
            return null to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
        }
        IfErrManager.makeIfProcNameNotExistInRuntime(
            iIfKey,
            ifMap.get(iIfKey)
        ).let {
                (ifProcName, errMsg) ->
            if(
                errMsg == null
            ) return@let ifProcName
            runBlocking {
                ImageActionErrLogger.sendErrLog(
                    context,
                    ImageActionErrLogger.ImageActionErrType.I_IF,
                    errMsg,
                    keyToSubKeyConWhere
                )
            }
            imageActionExitManager.setExit()
            return Pair(
                null,
                null
            )
        }
        val isReturnBool = isReturnToErrType.first ?: false
        if(isReturnBool){
            return returnKeyValueStrPair to returnSignal
        }
        return Pair(
            null,
            null
        )




//
//        mainSubKeyPairList.forEach {
//                mainSubKeyPair ->
//
//            val mainSubKey = mainSubKeyPair.first
//            val mainSubKeyMapSrc = mainSubKeyPair.second
////                    FileSystems.updateFile(
////                        File(UsePath.cmdclickDefaultAppDirPath, "iargsPairList_${settingVarName}.txt").absolutePath,
////                        listOf(
////                            "mainSubKeyMap: ${mainSubKeyMap}",
////
////                        ).joinToString("\n")
////                    )
////                        .map {
////                        replaceItPronoun(it.key) to replaceItPronoun(it.value)
////                    }.toMap()
//            val privateSubKeyClass = ImageActionKeyManager.ImageReturnManager.ImageReturnKey.entries.firstOrNull {
//                it.key == mainSubKey
//            } ?: return@forEach
//            when(privateSubKeyClass) {
//                ImageActionKeyManager.ImageReturnManager.ImageReturnKey.ARGS -> {}
//                ImageActionKeyManager.ImageReturnManager.ImageReturnKey.I_IF -> {
//                    if(!isNext) {
//                        isNext = true
//                        return@forEach
//                    }
////                    val judgeTargetStr = mainSubKeyMapSrc.get(mainSubKey)
////                        ?: return@forEach
//                    val argsPairList = CmdClickMap.createMap(
//                        mainSubKeyMapSrc.get(
//                            ImageActionKeyManager.ImageSubKey.ARGS.key
//                        ),
//                        valueSeparator
//                    ).filter {
//                        it.first.isNotEmpty()
//                    }
//                    val isReturnToErrType = SettingIfManager.handle(
//                        iIf.key,
////                        judgeTargetStr,
//                        argsPairList,
//                        null,
//                    )
////                            FileSystems.updateFile(
////                                File(
////                                    UsePath.cmdclickDefaultSDebugAppDirPath,
////                                    "lsetting_isReturnToErrType_${valueStrBeforeReplace}.txt"
////                                ).absolutePath,
////                                listOf(
////                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
////                                    "valueStrBeforeReplace: $valueStrBeforeReplace",
////                                    "curMapLoopKey: ${curMapLoopKey}",
////                                    "judgeTargetStr: ${judgeTargetStr}",
////                                    "argsPairList: ${argsPairList}",
////                                    "isReturnToErrType: ${isReturnToErrType}"
////                                ).joinToString("\n\n\n")
////                            )
//                    val errType = isReturnToErrType.second
//                    if(errType != null){
//                        runBlocking {
//                            ImageActionErrLogger.sendErrLog(
//                                context,
//                                ImageActionErrLogger.ImageActionErrType.I_IF,
//                                errType.errMessage,
//                                keyToSubKeyConWhere
//                            )
//                        }
//                        isNext = false
//                        imageActionExitManager.setExit()
//                        return null to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
//                    }
//                    val isReturnBool = isReturnToErrType.first ?: false
//                    if(isReturnBool){
//                        return returnKeyValueStrPair to returnSignal
//                    }
//                    isNext = true
//                }
//            }
//            if(privateSubKeyClass != iIf){
//                isNext = true
//            }
//        }
//        return Pair(
//            null,
//            null
//        )
    }
}