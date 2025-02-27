package com.puutaro.commandclick.proccess.edit.image_action.libs

import android.graphics.Bitmap

object ImageActionMapTool {
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