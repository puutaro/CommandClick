package com.puutaro.commandclick.proccess.edit.setting_action

import com.puutaro.commandclick.proccess.edit.setting_action.libs.SettingActionData
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.QuoteTool

object SettingActionKeyManager {

    const val landSeparator = ','
    const val mainKeySeparator = '|'
    const val subKeySepartor = '?'
    const val valueSeparator = '&'
    val globalVarNameRegex = "[A-Z0-9_]+".toRegex()

    enum class SettingActionsKey(
        val key: String
    ) {
        SETTING_VAR("sVar"),
        SETTING_ACTION_VAR("sAcVar"),
        SETTING_RETURN("sReturn")
    }

    enum class VarPrefix(
        val prefix: String
    ) {
        RUN("run"),
        RUN_ASYNC("${RUN.prefix}Async"),
        ASYNC("async"),
    }

    object ValueStrVar {

        const val itPronoun = "it"

        fun matchStringVarName(
            bitmapVarName: String,
        ): Boolean {
            val bitmapVarRegex = Regex("^[$][{][a-zA-Z0-9_]+[}]$")
            return bitmapVarRegex.matches(bitmapVarName)
                    && !bitmapVarName.startsWith(VarPrefix.RUN.prefix)
        }

        fun convertStrKey(bitmapVar: String): String {
            return bitmapVar
                .removePrefix("${'$'}{")
                .removeSuffix("}")
        }
    }


    object AwaitManager {
        private const val awaitSeparator = ','
        const val awaitWaitTimes = 5//10

        fun getAwaitVarNameList(awaitVarNameListCon: String): List<String> {
            return awaitVarNameListCon.split(awaitSeparator).map {
                it.trim()
            }.filter {
                it.isNotEmpty()
            }
        }
    }

    private enum class CommonPathKey(
        val key: String
    ) {
        IMPORT_PATH("importPath"),
    }

    enum class SettingSubKey(
        val key: String
    ) {
        SETTING_VAR(SettingActionsKey.SETTING_VAR.key),
        SETTING_RETURN(SettingActionsKey.SETTING_RETURN.key),
        FUNC("func"),
        ARGS("args"),
        ON_RETURN("onReturn"),
        S_IF("sIf"),
        VALUE("value"),
        AWAIT("await"),
    }

    object ActionImportManager {

        enum class ActionImportKey(
            val key: String,
        ) {
            IMPORT_PATH(CommonPathKey.IMPORT_PATH.key),
            REPLACE("replace"),
            S_IF(SettingSubKey.S_IF.key),
            ARGS(SettingSubKey.ARGS.key),
            AWAIT(SettingSubKey.AWAIT.key),
        }
    }


    object SettingReturnManager {

        enum class OutputReturn {
            OUTPUT_RETURN
        }

        enum class SettingReturnKey(
            val key: String,
        ) {
            S_IF(SettingSubKey.S_IF.key),
            ARGS(SettingSubKey.ARGS.key),
        }
    }

    enum class BreakSignal {
        EXIT_SIGNAL,
        RETURN_SIGNAL
    }

    suspend fun makeVarNameToValueStrMap(
        curMapLoopKey: String,
        topVarNameToValueStrMap: Map<String, String?>?,
        importedVarNameToValueStrMap: Map<String, String?>?,
        loopKeyToVarNameValueStrMapClass: SettingActionData.LoopKeyToVarNameValueStrMap,
        privateLoopKeyVarNameValueStrMapClass: SettingActionData.PrivateLoopKeyVarNameValueStrMap,
        curImportedVarNameToValueStrMap: Map<String, String?>?,
        itToBitmapMap: Map<String, String?>?,
    ):  Map<String, String?> {
        val varNameToValueStrMap =
            (topVarNameToValueStrMap ?: emptyMap()) +
                    (importedVarNameToValueStrMap ?: emptyMap()) +
                    (loopKeyToVarNameValueStrMapClass
                        .getAsyncVarNameToValueStr(curMapLoopKey)?.toMap()
                        ?: emptyMap()) +
                    (privateLoopKeyVarNameValueStrMapClass
                        .getAsyncVarNameToValueStr(curMapLoopKey)?.toMap()
                        ?: emptyMap()) +
                    (curImportedVarNameToValueStrMap ?: emptyMap()) +
                    (itToBitmapMap ?: emptyMap())
        return varNameToValueStrMap
    }

    fun makeSettingKeyToVarNameListForReturn(
        keyToSubKeyConList: List<Pair<String, String>>,
    ): List<Pair<String, String>> {
        val defaultReturnPair = String() to String()
        val subKeySeparator = SettingActionKeyManager.subKeySepartor
        val imageKeyList =
            SettingActionKeyManager.SettingActionsKey.entries.map {
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
            SettingActionsKey.SETTING_RETURN.key
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
        private val settingActionsKeyPlusList =
            SettingActionKeyManager.SettingActionsKey.entries.map {
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
                settingActionsKeyPlusList.contains(mainKeyName)
            }
        }

        private fun makeKeyToSubConPairListByValidKey(
            keyToSubKeyCon: String?,
        ): List<Pair<String, String>> {
            return CmdClickMap.createMap(
                keyToSubKeyCon,
                keySeparator
            ).filter {
                val mainKey = it.first
                settingActionsKeyPlusList.contains(mainKey)
            }.map {
                val mainKey = it.first
                val subKeyAfterStr = it.second
                mainKey to subKeyAfterStr
            }
        }
    }

    object PairListTool {

        fun getValue(
            pairList: List<Pair<String, Map<String, String>>>?,
            key: String
        ): Map<String, String>? {
            return getPair(
                pairList,
                key
            )
        }

        fun getPair(
            pairList: List<Pair<String, Map<String, String>>>?,
            key: String
        ): Map<String, String>? {
            if(
                pairList.isNullOrEmpty()
            ) return null
            return pairList.firstOrNull {
                it.first == key
            }?.second
        }
    }
}