package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.FileSystems

object CmdClickSystemAppDir {
    fun create(){
        FileSystems.createDirs(
            UsePath.cmdclickSystemAppDirPath
        )
                
    }
}