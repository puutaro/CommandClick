package com.puutaro.commandclick.util.str

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.CcPathTool

object ScriptPreWordReplacer {

    private val currentScriptPathMark = "\${0}"
    private val cmdclickDirPathMark = "\${00}"
    private val currentAppDirPathMark = "\${01}"
    private val fannelDirNameMark = "\${001}"
    private val currentScriptNameMark = "\${02}"
    private val storagePathMark = "\${STORAGE}"
    private val storagePath = UsePath.emulatedPath

    fun makeTsvTable(
//        currentAppDirPath: String,
        scriptName: String,
    ): String {
        return listOf(
            currentScriptPathMark,
            cmdclickDirPathMark,
            currentAppDirPathMark,
            fannelDirNameMark,
            currentScriptNameMark,
        ).map {
            "$it\t" + replace(
                it,
//                currentAppDirPath,
                scriptName,
            )
        }.joinToString("\n")
    }
    fun pathReplace(
        targetFilePath: String,
//        currentAppDirPath: String,
        scriptName: String,
    ): String {
        return targetFilePath.let {
            replace(
                it,
//                currentAppDirPath,
                scriptName,
            )
        }
    }

    fun replace(
        tergetContents: String,
//        currentAppDirPath: String,
        currentScriptName: String
    ): String {
        val fannelDirName = CcPathTool.makeFannelDirName(currentScriptName)
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        return tergetContents
            .replace(currentScriptPathMark, "$cmdclickDefaultAppDirPath/$currentScriptName")
            .replace(cmdclickDirPathMark, UsePath.cmdclickDirPath)
            .replace(currentAppDirPathMark, cmdclickDefaultAppDirPath)
            .replace(fannelDirNameMark, fannelDirName)
            .replace(currentScriptNameMark, currentScriptName)
            .replace(storagePathMark, storagePath)
    }

    fun replaceForQr(
        tergetString: String,
//        currentAppDirPath: String,
    ): String {
        return tergetString
            .replace(cmdclickDirPathMark, UsePath.cmdclickDirPath)
            .replace(currentAppDirPathMark, UsePath.cmdclickDefaultAppDirPath)
    }

    fun settingValReplace(
        tergetString: String,
        currentAppDirPath: String,
    ): String {
        return tergetString
            .replace(cmdclickDirPathMark, UsePath.cmdclickDirPath)
            .replace(currentAppDirPathMark, currentAppDirPath)
    }
}