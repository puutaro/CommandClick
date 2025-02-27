package com.puutaro.commandclick.proccess.edit.setting_action.libs

object SettingActionMapTool {
    suspend fun makeVarNameToValueStrMap(
        curMapLoopKey: String,
        topVarNameToValueStrMap: Map<String, String?>?,
        importedVarNameToValueStrMap: Map<String, String?>?,
        loopKeyToVarNameValueStrMapClass: SettingActionData.LoopKeyToVarNameValueStrMap?,
        privateLoopKeyVarNameValueStrMapClass: SettingActionData.PrivateLoopKeyVarNameValueStrMap,
        curImportedVarNameToValueStrMap: Map<String, String?>?,
        itToBitmapMap: Map<String, String?>?,
    ):  Map<String, String?> {
        val varNameToValueStrMap =
            (topVarNameToValueStrMap ?: emptyMap()) +
                    (importedVarNameToValueStrMap ?: emptyMap()) +
                    (loopKeyToVarNameValueStrMapClass
                        ?.convertAsyncVarNameToValueStrMapToMap(curMapLoopKey)
                        ?: emptyMap()) +
                    (privateLoopKeyVarNameValueStrMapClass
                        .convertAsyncVarNameToValueStrToMap(curMapLoopKey)
                        ?: emptyMap()) +
                    (curImportedVarNameToValueStrMap ?: emptyMap()) +
                    (itToBitmapMap ?: emptyMap())
        return varNameToValueStrMap
    }
}