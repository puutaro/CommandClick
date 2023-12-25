package com.puutaro.commandclick.util

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles

object ScriptPreWordReplacer {

    private val currentScriptPathMark = "\${0}"
    private val cmdclickDirPathMark = "\${00}"
    private val currentAppDirPathMark = "\${01}"
    private val fannelDirNameMark = "\${001}"
    private val currentScriptNameMark = "\${02}"

    fun makeTsvTable(
        currentAppDirPath: String,
        scriptName: String,
    ): String {
        val fannelDirName = CcPathTool.makeFannelDirName(
            scriptName
        )
        return listOf(
            currentScriptPathMark,
            cmdclickDirPathMark,
            currentAppDirPathMark,
            fannelDirNameMark,
            currentScriptNameMark,
        ).map {
            "$it\t" + replace(
                it,
                currentAppDirPath,
                fannelDirName,
                scriptName,
            )
        }.joinToString("\n")
    }
    fun pathReplace(
        targetFilePath: String,
        currentAppDirPath: String,
        scriptName: String,
    ): String {
        val fannelDirName = CcPathTool.makeFannelDirName(
            scriptName
        )
        return targetFilePath.let {
            replace(
                it,
                currentAppDirPath,
                fannelDirName,
                scriptName,
            )
        }
    }

    fun replace(
        tergetContents: String,
        currentAppDirPath: String,
        fannelDirName: String,
        currentScriptName: String
    ): String {
        return tergetContents
            .replace(currentScriptPathMark, "$currentAppDirPath/$currentScriptName")
            .replace(cmdclickDirPathMark, UsePath.cmdclickDirPath)
            .replace(currentAppDirPathMark, currentAppDirPath)
            .replace(fannelDirNameMark, fannelDirName)
            .replace(currentScriptNameMark, currentScriptName)
    }

    fun replaceForQr(
        tergetString: String,
        currentAppDirPath: String,
    ): String {
        return tergetString
            .replace(cmdclickDirPathMark, UsePath.cmdclickDirPath)
            .replace(currentAppDirPathMark, currentAppDirPath)
    }

    fun settingValreplace(
        tergetString: String,
        currentAppDirPath: String,
    ): String {
        return tergetString
            .replace(cmdclickDirPathMark, UsePath.cmdclickDirPath)
            .replace(currentAppDirPathMark, currentAppDirPath)
    }
}