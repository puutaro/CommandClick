package com.puutaro.commandclick.proccess.ubuntu

import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap

object UbuntuExtraSystemShells {

    private val shellPathKey = ConfigKey.SHELL_PATH.key
    private val onAutoResotreKey = ConfigKey.ON_AUTO_RESTORE.key
    private val disableKey = ConfigKey.DISABLE.key
    private const val extraSeparator = ','
    private const val switchOn = "ON"

    enum class UbuntuExtraSystemShellMacro(
        val macro: String,
        val processName: String,
        val shellPath: String,
    ) {
        PULSE("PULSE.sh", "pulseaudio --start", UbuntuFiles.pulseAudioStartUpShellPath)
    }

    fun isMacro(
        ubuntuExtraSystemShellMacro: UbuntuExtraSystemShellMacro
    ): Boolean {
        val macroName =  ubuntuExtraSystemShellMacro.macro
        val isHit = !makeMapList().firstOrNull{
            it.get(shellPathKey) == macroName
        }.isNullOrEmpty()
        return isHit
    }


    enum class ConfigKey(
        val key: String
    ){
        SHELL_PATH("shellPath"),
        ON_AUTO_RESTORE("onAutoRestore"),
        DISABLE("disable"),
    }

    object OnAutoRestore {
        fun makeRestoreProcessPathList(): List<String> {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "resotre_shell.txt").absolutePath,
//                listOf(
//                   "makeMapList(): ${makeMapList()}",
//                    "autoResotreList: ${makeMapList().filter {
//                        it.get(onAutoResotreKey) == switchOn
//                                || it.get(onAutoResotreKey) == switchOn.lowercase()
//                    }.map {
//                        it.get(shellPathKey)?.let {
//                            shellPath ->
//                            UbuntuExtraSystemShellMacro.values().firstOrNull {
//                               macro ->
//                                macro.macro == shellPath
//                            }?.processName ?: shellPath
//                        } ?: String()
//                    }.filter {
//                        it.isNotEmpty()
//                    }}"
//                ).joinToString("\n")
//            )
            return makeMapList().filter {
                it.get(onAutoResotreKey) == switchOn
                        || it.get(onAutoResotreKey) == switchOn.lowercase()
            }.map {
                it.get(shellPathKey)?.let {
                        shellPath ->
                    UbuntuExtraSystemShellMacro.values().firstOrNull {
                            macro ->
                        macro.macro == shellPath
                    }?.processName ?: shellPath
                } ?: String()
            }.filter {
                it.isNotEmpty()
            }
        }
    }

    fun makeGrepCon(): String {
        return makeMapList().map {
            if(
                it.get(disableKey) == switchOn
            ) return@map String()
            val pathOrMacro = it.get(shellPathKey)
            UbuntuExtraSystemShellMacro.values().firstOrNull {
                it.macro == pathOrMacro
            }?.processName ?: pathOrMacro
        }.filter { !it.isNullOrEmpty() }.map {
            " | grep \"${it}\""
        }.joinToString("\n")
    }

    fun makeStartupShellCon(): String {
        val shellShiban = "#!/bin/bash"
        val shellBody = makeMapList().map {
            if(
                it.get(disableKey) == switchOn
            ) return@map String()
            val pathOrMacro = it.get(shellPathKey)
            UbuntuExtraSystemShellMacro.values().firstOrNull {
                it.macro == pathOrMacro
            }?.shellPath ?: pathOrMacro
        }.filter { !it.isNullOrEmpty() }.map {
            "bash \"${it}\" &"
        }.joinToString("\n")
        return listOf(
            shellShiban,
            String(),
            shellBody,
            "wait",
        ).joinToString("\n")
    }


    private fun makeMapList(): List<Map<String, String>>{
        return ReadText(
            UbuntuFiles.ubuntuExtraStartupShellsTsvPath
        ).textToList().filter {
            it.trim().isNotEmpty()
        }.map {
            val shellPathAndExtra = it.trim().split("\t")
            val shellPathMap = mapOf(
                ConfigKey.SHELL_PATH.key to
                        (shellPathAndExtra.firstOrNull() ?: String())
            )
            val extraMap = shellPathAndExtra.getOrNull(1).let {
                CmdClickMap.createMap(
                    it,
                    extraSeparator
                ).toMap()
            }
            shellPathMap + extraMap
        }.filter {
            val isExist =
                !it.get(shellPathKey).isNullOrEmpty()
            val disableValue =
                it.get(ConfigKey.DISABLE.key)
            val isNotDisable =
                disableValue != switchOn
                        && disableValue != switchOn.lowercase()
            isExist && isNotDisable
        }
    }
}