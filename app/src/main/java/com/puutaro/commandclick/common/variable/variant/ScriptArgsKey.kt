package com.puutaro.commandclick.common.variable.variant

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File


object ScriptArgsMapList {

    private val nameKey = ScriptArgsKey.NAME.key
    private val dirNameKey = ScriptArgsKey.DIR_NAME.key
    private val jsNameKey = ScriptArgsKey.JS_NAME.key

    fun updateScriptArgsMapList(
        scriptDirPath: String,
        scriptName: String,
        settingSectionVariableList: List<String>?,
    ): List<Map<String, String>> {
        var updateScriptArgsMapList = scriptArgsMapList
        settingValAndArgNamePair.forEach {
            updateScriptArgsMapList = execUpdateScriptArgsMapList(
                updateScriptArgsMapList,
                scriptDirPath,
                scriptName,
                settingSectionVariableList,
                it
            )
        }
        return updateScriptArgsMapList
    }

    private fun execUpdateScriptArgsMapList(
        scriptArgsMapList: List<Map<String, String>>,
        scriptDirPath: String,
        scriptName: String,
        settingSectionVariableList: List<String>?,
        settingValAndArgsNamePair: Pair<String, String>
    ): List<Map<String, String>> {
        val settingValName = settingValAndArgsNamePair.first
        val argName = settingValAndArgsNamePair.second
        val settingArgMap = scriptArgsMapList.first {
            it.get(nameKey) == argName
        }
        val fannelDirName = CcPathTool.makeFannelDirName(scriptName)
        val fannelDirPath = File(scriptDirPath, fannelDirName).absolutePath
        val scriptPathObj = File(scriptDirPath, scriptName)
        val scriptPath = scriptPathObj.absolutePath
        val settingPathSrc = CommandClickVariables.substituteFilePrefixPath(
            settingSectionVariableList,
            settingValName,
            listOf(
                fannelDirPath,
                settingArgMap.get(ScriptArgsKey.DIR_NAME.key),
                settingArgMap.get(ScriptArgsKey.JS_NAME.key)
            ).joinToString("/")
        )
        if(
            settingPathSrc.isNullOrEmpty()
        ) return scriptArgsMapList
        val setReplaceVariable = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            scriptPath
        )
        val settingPath = SetReplaceVariabler.execReplaceByReplaceVariables(
            settingPathSrc,
            setReplaceVariable,
            scriptDirPath,
            scriptName
        )
        val settingPathObj = File(settingPath)
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "args.txt").absolutePath,
//            listOf(
//                "scriptPath: ${scriptPath}",
//                "autoExecArgMap: $settingArgMap",
//                "autoExecPathSrc: ${CommandClickVariables.substituteFilePrefixPath(
//                    settingSectionVariableList,
//                    settingValName,
//                    scriptPath,
//                    listOf(
//                        scriptDirPath,
//                        settingArgMap.get(ScriptArgsKey.DIR_NAME.key),
//                        settingArgMap.get(ScriptArgsKey.JS_NAME.key)
//                    ).joinToString("/")
//                ) }",
//                "setReplaceVariableMap: ${SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
//                    scriptPath
//                )}",
//                "autoExecPathObj: ${settingPathObj.absolutePath}",
//            ).joinToString("\n\n")
//        )
        return scriptArgsMapList.map {
            val isArgName =
                it.get(nameKey) == argName
            when(isArgName){
                true -> makeAutoExecArgMapForFilePrefix(
                    scriptDirPath,
                    scriptName,
                    settingArgMap,
                    settingPathObj,
                )
                else -> it
            }
        }
    }

    private fun makeAutoExecArgMapForFilePrefix(
        scriptDirPath: String,
        scriptName: String,
        autoExecArgMap: Map<String, String>,
        autoExecPathObj: File,
    ): Map<String, String> {
        val autoExecPath = autoExecPathObj.absolutePath
        val autoExecJsDirPath = autoExecPathObj.parent
            ?: return autoExecArgMap
        if(
            autoExecPath.isEmpty()
        ) return autoExecArgMap
        val fannelDirName = CcPathTool.makeFannelDirName(scriptName)
        val fannelDirPath = File(scriptDirPath, fannelDirName).absolutePath
        val dirName = autoExecJsDirPath.replace(
            fannelDirPath,
            String()
        ).removePrefix("/")
        val updateAutoExecArgMap =
            autoExecArgMap.toMutableMap()
        updateAutoExecArgMap.put(
            ScriptArgsKey.DIR_NAME.key,
            dirName
        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath,
//            "args_make.txt").absolutePath,
//            "dirName: ${dirName}"
//        )
        updateAutoExecArgMap.put(
            ScriptArgsKey.JS_NAME.key,
            autoExecPathObj.name
        )
        return updateAutoExecArgMap
    }


    private val settingValAndArgNamePair =
        listOf(
            CommandClickScriptVariable.AUTO_EXEC_PATH to
                    ScriptArgsName.ON_AUTO_EXEC.str,
            CommandClickScriptVariable.NO_ARG_JS_PATH to
                    ScriptArgsName.NO_ARG.str,

            )

    private val scriptArgsMapList = listOf(
        mapOf(
            nameKey to ScriptArgsName.ON_AUTO_EXEC.str,
            dirNameKey to UsePath.systemExecJsDirName,
            jsNameKey to "onAutoExec.js",
        ),
        mapOf(
            nameKey to ScriptArgsName.URL_HISTORY_CLICK.str,
            dirNameKey to UsePath.systemExecJsDirName,
            jsNameKey to "urlHistoryClick.js",
        ),
        mapOf(
            nameKey to ScriptArgsName.NO_ARG.str,
            dirNameKey to UsePath.systemExecJsDirName,
            jsNameKey to "noArg.js",
        )
    )

    enum class ScriptArgsKey(val key: String){
        NAME("name"),
        DIR_NAME("dirName"),
        JS_NAME("jsName")
    }

    enum class ScriptArgsName(val str: String){
        ON_AUTO_EXEC( "onAutoExec"),
        URL_HISTORY_CLICK( "urlHistoryClick"),
        NO_ARG(String()),
    }
}

