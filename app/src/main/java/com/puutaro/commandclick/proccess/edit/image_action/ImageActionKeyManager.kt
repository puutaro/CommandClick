package com.puutaro.commandclick.proccess.edit.image_action

import android.graphics.Bitmap
import com.puutaro.commandclick.proccess.edit.image_action.libs.ImageActionData
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.QuoteTool

object ImageActionKeyManager {
    const val landSeparator = ','
    const val mainKeySeparator = '|'
    const val subKeySepartor = '?'
    const val valueSeparator = '&'
    val globalVarNameRegex = "[A-Z0-9_]+".toRegex()
    const val awaitWaitTimes = 5//10
    const val returnTopAcVarNameMacro = "RESULT_OUTPUT"

    enum class ImageActionsKey(
        val key: String
    ) {
        IMAGE_VAR("iVar"),
        IMAGE_ACTION_VAR("iAcVar"),
        IMAGE_RETURN("iReturn"),
    }

    enum class VarPrefix(
        val prefix: String
    ) {
        RUN("run"),
        RUN_ASYNC("${RUN.prefix}Async"),
        ASYNC("async"),
    }

    object LoopKeyManager {
        const val mapRoopKeyUnit = "loop"
        private const val mapLoopKeySeparator = "___"


        fun addLoopKey(
            curMapLoopKey: String
        ): String {
            return listOf(
                curMapLoopKey,
                mapRoopKeyUnit
            ).joinToString(mapLoopKeySeparator)
        }

        fun removeLoopKey(
            curMapLoopKey: String
        ): String {
            return curMapLoopKey.removeSuffix(
                "${mapLoopKeySeparator}${mapRoopKeyUnit}"
            )
        }

        suspend fun getResultLoopKeyToVarNameValueMap(
            loopKeyToVarNameBitmapMap: ImageActionData.LoopKeyToVarNameBitmapMap?
        ): Map<String, Bitmap?> {
            return loopKeyToVarNameBitmapMap?.convertAsyncVarNameToBitmapToMap(mapRoopKeyUnit)
                ?: emptyMap()
        }
    }

    object BitmapVar {


        const val itPronoun = "it"

        fun matchBitmapVarName(
            bitmapVarName: String,
        ): Boolean {
            val bitmapVarRegex = Regex("^#[{][a-zA-Z0-9_]+[}]$")
            return bitmapVarRegex.matches(bitmapVarName)
                    && !bitmapVarName.startsWith(VarPrefix.RUN.prefix)
        }

        fun convertBitmapKey(bitmapVar: String): String {
            return bitmapVar
                .removePrefix("#{")
                .removeSuffix("}")
        }
    }

    object AwaitManager {
        private const val awaitSeparator = ','

        fun getAwaitVarNameList(awaitVarNameListCon: String): List<String> {
            return awaitVarNameListCon.split(awaitSeparator).asSequence().map {
                it.trim()
            }.filter {
                it.isNotEmpty()
            }.toList()
        }
    }

    private enum class CommonPathKey(
        val key: String
    ) {
        IMPORT_PATH("importPath"),
    }

    enum class ImageSubKey(
        val key: String
    ) {
        IMAGE_VAR(ImageActionsKey.IMAGE_VAR.key),
        IMAGE_RETURN(ImageActionsKey.IMAGE_RETURN.key),
        FUNC("func"),
        ARGS("args"),
        ON_RETURN("onReturn"),
        I_IF("iIf"),
        I_IF_END("iIfEnd"),
//        VALUE("value"),
        AWAIT("await"),
    }

    object ActionImportManager {

        enum class ActionImportKey(
            val key: String,
        ) {
            IMPORT_PATH(CommonPathKey.IMPORT_PATH.key),
            REPLACE("replace"),
            I_IF("iIf"),
            ARGS(ImageSubKey.ARGS.key),
            AWAIT("await"),

        }
    }

    object ImageReturnManager {

        enum class OutputReturn {
            OUTPUT_RETURN
        }

        enum class ImageReturnKey(
            val key: String,
        ) {
            I_IF(ImageSubKey.I_IF.key),
            ARGS(ImageSubKey.ARGS.key),
        }
    }

    enum class BreakSignal {
        EXIT_SIGNAL,
        RETURN_SIGNAL,
    }

    fun makeSettingKeyToBitmapVarKeyListForReturn(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, String>> {
        val defaultReturnPair = String() to String()
        val subKeySeparator = subKeySepartor
        val imageKeyList =
            ImageActionKeyManager.ImageActionsKey.entries.map {
                it.key
            }
        return keyToSubKeyConList.map {
                keyToSubKeyCon ->
            val settingKey = keyToSubKeyCon.first
            if(
                !imageKeyList.contains(settingKey)
            ) return@map defaultReturnPair
            val varName = keyToSubKeyCon.second
                .split(subKeySeparator)
                .firstOrNull()?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                } ?: return@map defaultReturnPair
            settingKey to varName
        }
    }

    fun filterSettingKeyToDefinitionListByValidVarDefinition(
        settingKeyToDefinitionList: List<Pair<String, String>>
    ): List<Pair<String, String>> {
        val settingReturnKey =
            ImageActionsKey.IMAGE_RETURN.key
        val varStrRegex = Regex("[a-zA-Z0-9_]+")
        return settingKeyToDefinitionList.filter {
            val settingKey = it.first
            if(
                settingKey.isEmpty()
            ) return@filter false
            if(
                settingKey == settingReturnKey
            ) return@filter true
            val definition = it.second
            varStrRegex.matches(definition)
        }
    }

    object KeyToSubKeyMapListMaker {

        private const val keySeparator = '|'
        private val imageActionsKeyPlusList =
            ImageActionKeyManager.ImageActionsKey.entries.map {
                it.key
            }

        fun make(
            keyToSubKeyCon: String?,
        ): List<Pair<String, String>> {
            val keyToSubKeyConListSrc = makeKeyToSubConPairListByValidKey(
                keyToSubKeyCon
            )
            val keyToSubKeyConListByValidKey =
                filterByValidKey(keyToSubKeyConListSrc)
            return keyToSubKeyConListByValidKey
        }

        private fun filterByValidKey(
            keyToSubKeyConList: List<Pair<String, String>>
        ): List<Pair<String, String>> {
            return keyToSubKeyConList.filter {
                val mainKeyName = it.first
                imageActionsKeyPlusList.contains(mainKeyName)
            }
        }

        private fun makeKeyToSubConPairListByValidKey(
            keyToSubKeyCon: String?,
        ): List<Pair<String, String>> {
            return CmdClickMap.createMap(
                keyToSubKeyCon,
                keySeparator
            ).asSequence().filter {
                val mainKey = it.first
                imageActionsKeyPlusList.contains(mainKey)
            }.map {
                val mainKey = it.first
                val subKeyAfterStr = it.second
                mainKey to subKeyAfterStr
            }.toList()
        }
    }

    suspend fun makeValueToBitmapMap(
        curMapLoopKey: String,
        topVarNameToVarNameBitmapMap: Map<String, Bitmap?>?,
        importedVarNameToBitmapMap: Map<String, Bitmap?>?,
        loopKeyToVarNameBitmapMapClass: ImageActionData.LoopKeyToVarNameBitmapMap?,
        privateLoopKeyVarNameBitmapMapClass: ImageActionData.PrivateLoopKeyVarNameBitmapMap,
        curImportedVarNameToBitmapMap: Map<String, Bitmap?>?,
        itToBitmapMap: Map<String, Bitmap?>?,
    ): Map<String, Bitmap?> {
        return (topVarNameToVarNameBitmapMap ?: emptyMap()) +
                (importedVarNameToBitmapMap ?: emptyMap()) +
                (loopKeyToVarNameBitmapMapClass
                    ?.convertAsyncVarNameToBitmapToMap(
                        curMapLoopKey
                    ) ?: emptyMap()) +
                (privateLoopKeyVarNameBitmapMapClass
                    .convertAsyncVarNameToBitmapToMap(
                        curMapLoopKey
                    ) ?: emptyMap()) +
                (curImportedVarNameToBitmapMap ?: emptyMap()) +
                (itToBitmapMap ?: emptyMap())
    }
}