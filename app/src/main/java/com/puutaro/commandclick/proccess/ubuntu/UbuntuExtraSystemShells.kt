package com.puutaro.commandclick.proccess.ubuntu

import com.puutaro.commandclick.util.file.ReadText

object UbuntuExtraSystemShell {
    enum class UbuntuExtraSystemShellMacro(
        val macro: String
    ) {
        PULSE("PULSE.sh")
    }

    fun readUbuntuExtraStartupShells(): List<String> {
        return ReadText(
            UbuntuFiles.ubuntuExtraStartupShellsPath
        ).textToList().filter {
            it.trim().isNotEmpty()
        }
    }

    fun isMacro(
        ubuntuExtraSystemShellMacro: UbuntuExtraSystemShellMacro
    ): Boolean {
        val macroName =  ubuntuExtraSystemShellMacro.macro
        val isNotPulseSet =readUbuntuExtraStartupShells().firstOrNull {
            it == macroName
        }.isNullOrEmpty()
    }
}