package com.puutaro.commandclick.common.variable.variant

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.ReadText
import java.io.File


object ScriptArgsMapList {

    private val nameKey = ScriptArgsKey.NAME.key
    private val dirNameKey = ScriptArgsKey.DIR_NAME.key
    private val jsNameKey = ScriptArgsKey.JS_NAME.key

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

    fun updateScriptArgsMapList(
        scriptDirPath: String,
        scriptName: String,
        settingSectionVariableList: List<String>?
    ): List<Map<String, String>> {

        val nameKey = ScriptArgsKey.NAME.key
        val onAutoExecArgName = ScriptArgsName.ON_AUTO_EXEC.str

        val autoExecArgMap = scriptArgsMapList.first {
            it.get(nameKey) == onAutoExecArgName
        }
        val autoExecPath = CommandClickVariables.substituteFilePrefixPath(
            settingSectionVariableList,
            CommandClickScriptVariable.AUTO_EXEC_PATH,
            File(scriptDirPath, scriptName).absolutePath,
            listOf(
                scriptDirPath,
                autoExecArgMap.get(ScriptArgsKey.DIR_NAME.key),
                autoExecArgMap.get(ScriptArgsKey.JS_NAME.key)
            ).joinToString("/")
        ) ?: return scriptArgsMapList
        val autoExecPathObj = File(autoExecPath)
        val autoExecJsDirPath = autoExecPathObj.parent
            ?: return scriptArgsMapList

        return scriptArgsMapList.map {
            val isAutoExecArgName =
                it.get(nameKey) == onAutoExecArgName
            when(isAutoExecArgName){
                true -> {
                    if(
                        autoExecPath.isEmpty()
                    ) return@map autoExecArgMap
                    autoExecArgMap.toMutableMap().put(
                        ScriptArgsKey.DIR_NAME.key,
                        autoExecJsDirPath
                    )
                    autoExecArgMap.toMutableMap().put(
                        ScriptArgsKey.JS_NAME.key,
                        autoExecPathObj.name
                    )
                    autoExecArgMap
                }
                else -> it
            }
        }
    }
//    ON_AUTO_EXEC(
//        "onAutoExec",
//        UsePath.systemExecJsDirName,
//        "onAutoExec.js",
//    ),
//    URL_HISTORY_CLICK(
//        "urlHistoryClick",
//        UsePath.systemExecJsDirName,
//        "urlHistoryClick.js",
//    ),
//    NO_ARG(
//        String(),
//        UsePath.systemExecJsDirName,
//    "noArg.js",
//    ),
    enum class ScriptArgsKey(val key: String){
        NAME("name"),
        DIR_NAME("dirName"),
        JS_NAME("jsPath")
    }

    enum class ScriptArgsName(val str: String){
        ON_AUTO_EXEC( "onAutoExec"),
        URL_HISTORY_CLICK( "urlHistoryClick"),
        NO_ARG(String()),
    }
}

