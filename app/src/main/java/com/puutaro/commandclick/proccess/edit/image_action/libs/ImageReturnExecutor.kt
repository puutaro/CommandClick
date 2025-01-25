package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingIfManager
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.runBlocking

class ImageReturnExecutor {

    private var isNext = true
    private val valueSeparator = ImageActionKeyManager.valueSeparator
    private val outputReturnSignal =
        ImageActionKeyManager.ImageReturnManager.OutputReturn.OUTPUT_RETURN
    private val returnSignal = ImageActionKeyManager.BreakSignal.RETURN_SIGNAL
    private val iIf = ImageActionKeyManager.ImageReturnManager.ImageReturnKey.I_IF

    suspend fun exec(
        fragment: Fragment,
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
        val context = fragment.context
        val isIIf = mainSubKeyPairList.any {
            val mainSubKey = it.first
            mainSubKey == iIf.key
        }
        val returnKeyValueStrPair = Pair(
            outputReturnSignal,
            returnBitmap,
        )
        if(!isIIf){
            return Pair(
                returnKeyValueStrPair,
                returnSignal
            )
        }
        mainSubKeyPairList.forEach {
                mainSubKeyPair ->

            val mainSubKey = mainSubKeyPair.first
            val mainSubKeyMapSrc = mainSubKeyPair.second
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "iargsPairList_${settingVarName}.txt").absolutePath,
//                        listOf(
//                            "mainSubKeyMap: ${mainSubKeyMap}",
//
//                        ).joinToString("\n")
//                    )
//                        .map {
//                        replaceItPronoun(it.key) to replaceItPronoun(it.value)
//                    }.toMap()
            val privateSubKeyClass = ImageActionKeyManager.ImageReturnManager.ImageReturnKey.entries.firstOrNull {
                it.key == mainSubKey
            } ?: return@forEach
            when(privateSubKeyClass) {
                ImageActionKeyManager.ImageReturnManager.ImageReturnKey.ARGS -> {}
                ImageActionKeyManager.ImageReturnManager.ImageReturnKey.I_IF -> {
                    if(!isNext) {
                        isNext = true
                        return@forEach
                    }
//                    val judgeTargetStr = mainSubKeyMapSrc.get(mainSubKey)
//                        ?: return@forEach
                    val argsPairList = CmdClickMap.createMap(
                        mainSubKeyMapSrc.get(
                            ImageActionKeyManager.ImageSubKey.ARGS.key
                        ),
                        valueSeparator
                    ).filter {
                        it.first.isNotEmpty()
                    }
                    val isReturnToErrType = SettingIfManager.handle(
                        iIf.key,
//                        judgeTargetStr,
                        argsPairList,
                        null,
                    )
//                            FileSystems.updateFile(
//                                File(
//                                    UsePath.cmdclickDefaultSDebugAppDirPath,
//                                    "lsetting_isReturnToErrType_${valueStrBeforeReplace}.txt"
//                                ).absolutePath,
//                                listOf(
//                                    "mainSubKeyPairList: ${mainSubKeyPairList}",
//                                    "valueStrBeforeReplace: $valueStrBeforeReplace",
//                                    "curMapLoopKey: ${curMapLoopKey}",
//                                    "judgeTargetStr: ${judgeTargetStr}",
//                                    "argsPairList: ${argsPairList}",
//                                    "isReturnToErrType: ${isReturnToErrType}"
//                                ).joinToString("\n\n\n")
//                            )
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
                        isNext = false
                        imageActionExitManager.setExit()
                        return null to ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    }
                    val isReturnBool = isReturnToErrType.first ?: false
                    if(isReturnBool){
                        return returnKeyValueStrPair to returnSignal
                    }
                    isNext = true
                }
            }
            if(privateSubKeyClass != iIf){
                isNext = true
            }
        }
        return Pair(
            null,
            null
        )
    }
}