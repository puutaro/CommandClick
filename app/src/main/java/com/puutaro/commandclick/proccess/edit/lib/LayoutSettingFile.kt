package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import android.graphics.Bitmap
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionAsyncCoroutine
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

object LayoutSettingFile {

    fun read(
        context: Context?,
        fannelPath: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        globalVarNameToValueMap: Map<String, String>?,
        globalVarNameToBitmapMap: Map<String, Bitmap?>?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        settingFilePath: String,
    ): List<String> {
        return SettingFile.readLayout(
            context,
            fannelPath,
            setReplaceVariableCompleteMap,
            busyboxExecutor,
            globalVarNameToValueMap,
            globalVarNameToBitmapMap,
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            settingFilePath,
        ).let {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "layoutSettingFile.txt").absolutePath,
//                listOf(
//                    "settingFile: ${it}",
//                ).joinToString("\n") + "\n------\n"
//            )
            QuoteTool.layoutSplitBySurroundedIgnore(
                it,
            )
        }
    }

    fun readFromList(
        context: Context?,
        fannelName: String,
        setReplaceVariableCompleteMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        globalVarNameToValueMap: Map<String, String>?,
        globalVarNameToBitmapMap: Map<String, Bitmap?>?,
        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
        imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
        firstSettingConList: List<String>,
    ): List<String> {
        return SettingFile.readLayoutFromList(
            context,
            fannelName,
            setReplaceVariableCompleteMap,
            busyboxExecutor,
            globalVarNameToValueMap,
            globalVarNameToBitmapMap,
            settingActionAsyncCoroutine,
            imageActionAsyncCoroutine,
            firstSettingConList,
        ).let {
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "layoutSettingFile.txt").absolutePath,
//                listOf(
//                    "settingFile: ${it}",
//                ).joinToString("\n") + "\n------\n"
//            )
            QuoteTool.layoutSplitBySurroundedIgnore(
                it,
            )
        }
    }


}