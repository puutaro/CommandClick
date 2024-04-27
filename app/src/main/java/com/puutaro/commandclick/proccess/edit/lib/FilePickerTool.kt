package com.puutaro.commandclick.proccess.edit.lib

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object FilePickerTool {

    fun makeInitialDirPath(
        filterMap: Map<String, String>?,
        fannelName: String,
        pickerMacro: PickerMacro?,
        parentDirPathSuffix: String,
    ): String {
        val initialPathSrc = filterMap?.get(
            EditSettingExtraArgsTool.ExtraKey.INITIAL_PATH.key,
        ) ?: String()
        if(
            pickerMacro == null
        ) return initialPathSrc
        return when(pickerMacro){
            PickerMacro.FROM_RECENT_DIR -> {
                val cmdclickPastPickerDirMemoryPath = makePastPickerDirMemoryPath(
                    fannelName,
                    parentDirPathSuffix,
                )
                ReadText(
                    cmdclickPastPickerDirMemoryPath,
                ).readText()
            }
        }.ifEmpty {
            initialPathSrc
        }
    }

    fun registerRecentDir(
        fannelName: String,
        parentDirPathSuffix: String,
        pickerMacro: PickerMacro?,
        registerEntryDirPath: String,
    ){
        if(
            pickerMacro == null
        ) return
        val registerDirPath = when(
            File(registerEntryDirPath).isDirectory
        ){
            true -> registerEntryDirPath
            else -> File(registerEntryDirPath).parent
        } ?: return
        val cmdclickPastPickerDirMemoryPath = makePastPickerDirMemoryPath(
            fannelName,
            parentDirPathSuffix,
        )
        FileSystems.writeFile(
            cmdclickPastPickerDirMemoryPath,
            registerDirPath
        )
    }

    private fun makePastPickerDirMemoryPath(
        fannelName: String,
        parentDirPathSuffix: String,
    ): String {
        val parentDirName =
            "${CcPathTool.trimAllExtend(fannelName)}_${parentDirPathSuffix}"
        val parentDirPath = File(
            UsePath.cmdclickTempPickerDirPath,
            parentDirName
        ).absolutePath
        return File(
            parentDirPath,
            UsePath.cmdclickPastPickerDirMemoryName
        ).absolutePath
    }

    enum class PickerMacro {
        FROM_RECENT_DIR
    }
}