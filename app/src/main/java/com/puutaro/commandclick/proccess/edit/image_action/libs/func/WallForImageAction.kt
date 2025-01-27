package com.puutaro.commandclick.proccess.edit.image_action.libs.func

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.fragment_lib.command_index_fragment.UrlImageDownloader
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager
import com.puutaro.commandclick.proccess.edit.setting_action.libs.FuncCheckerForSetting
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import java.io.File
import kotlin.enums.EnumEntries

object WallForImageAction {
    suspend fun handle(
        fragment: Fragment,
        funcName: String,
        methodNameStr: String,
        argsPairList: List<Pair<String, String>>,
    ): Pair<
            Pair<
                    Bitmap?,
                    ImageActionKeyManager.BreakSignal?
                    >?,
            FuncCheckerForSetting.FuncCheckErr?
            >? {
        val context =
            fragment.context
                ?: return Pair(Pair(null, null), null)
        val methodNameClass = MethodNameClass.entries.firstOrNull {
            it.str == methodNameStr
        } ?: let {
            val spanFuncTypeStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                funcName
            )
            val spanMethodNameStr = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errRedCode,
                methodNameStr
            )
            return null to FuncCheckerForSetting.FuncCheckErr("Method name not found: func.method: ${spanFuncTypeStr}.${spanMethodNameStr}")
        }
        val args =
            methodNameClass.args
        return when(args){
            is WallMethodArgClass.MakeArgs -> {
                val formalArgIndexToNameToTypeList = args.entries.mapIndexed {
                        index, formalArgsNameToType ->
                    Triple(
                        index,
                        formalArgsNameToType.key,
                        formalArgsNameToType.type,
                    )
                }
                val mapArgMapList = FuncCheckerForSetting.MapArg.makeMapArgMapListByIndex(
                    formalArgIndexToNameToTypeList,
                    argsPairList
                )
                val where = FuncCheckerForSetting.WhereManager.makeWhereFromList(
                    funcName,
                    methodNameStr,
                    argsPairList,
                    formalArgIndexToNameToTypeList
                )
                val macroStr = FuncCheckerForSetting.Getter.getStringFromArgMapByIndex(
                    mapArgMapList,
                    args.macroKeyToIndex,
                    where
                ).let { macroStrToErr ->
                    val funcErr = macroStrToErr.second
                        ?: return@let macroStrToErr.first
                    return Pair(
                        null,
                        ImageActionKeyManager.BreakSignal.EXIT_SIGNAL
                    ) to funcErr
                }
                val cmdClickBkImageFilePath = BkWallPath.get(
                    macroStr
                ) ?: return null
                val returnBitmap = BitmapTool.convertFileToBitmap(cmdClickBkImageFilePath)?.let {
                        BitmapTool.ImageTransformer.cutCenter(
                            it,
                            400,
                            800
                        )
                    }
                Pair(
                    returnBitmap,
                    null,
                ) to null
            }
        }
    }

    private object BkWallPath {

        fun get(
            wallRelativePath: String,
        ): String? {
            val fannelWallDirPath = UrlImageDownloader.fannelWallDirPath
            val fannelWallDirName = File(fannelWallDirPath).name
            if(
                !wallRelativePath
                    .startsWith(fannelWallDirName)
            ) return null
            val wallPathObj = File(
                UrlImageDownloader.imageDirObj.absolutePath,
                wallRelativePath
            )
            val wallPathOrDirPath = wallPathObj.absolutePath
            return when(true){
                wallPathObj.isFile -> {
                    if(
                        !isImageFile(wallPathOrDirPath)
                    ) return null
                    wallPathOrDirPath
                }
                else -> {
                    File(fannelWallDirPath).walk().filter {
                            wallImageFileEntry ->
                        if(
                            !wallImageFileEntry.isFile
                        ) return@filter false
                        val wallImageFilePath =
                            wallImageFileEntry.absolutePath
                        wallImageFilePath.startsWith(
                            wallPathOrDirPath
                        ) && isImageFile(
                            wallImageFilePath
                        )
                    }.shuffled().firstOrNull()?.absolutePath
                        ?: return null
                }
            }
        }

        private fun isImageFile(
            wallImageFilePath: String
        ): Boolean {
            val imageFileExtendList = listOf(".jpeg", ".jpg", ".png")
            return imageFileExtendList.any {
                    imageFileExtend ->
                wallImageFilePath.endsWith(imageFileExtend)
            }
        }

        private fun getBkImageFilePathFromDirPath(
            bkImageDirPath: String,
        ): String {
            return FileSystems.sortedFiles(
                bkImageDirPath
            ).random().let {
                File(bkImageDirPath, it).absolutePath
            }
        }
    }

    private enum class MethodNameClass(
        val str: String,
        val args: WallMethodArgClass,
    ){
        MAKE("make", WallMethodArgClass.MakeArgs),
    }

    private sealed interface ArgType {
        val entries: EnumEntries<*>
    }

    private sealed class WallMethodArgClass {
        data object MakeArgs : WallMethodArgClass(), ArgType {
            override val entries = MakeEnumArgs.entries
            val macroKeyToIndex = Pair(
                MakeEnumArgs.MACRO.key,
                MakeEnumArgs.MACRO.defaultValueStr
            )
            enum class MakeEnumArgs(
                val key: String,
                val defaultValueStr: Int,
                val type: FuncCheckerForSetting.ArgType,
            ){
                MACRO("macro", 0, FuncCheckerForSetting.ArgType.STRING),
            }
        }
    }
}