package com.puutaro.commandclick.fragment_lib.command_index_fragment.init

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.file.AssetsFileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CmdClickSystemAppDir {
    fun create(
        cmdIndexFragment: CommandIndexFragment
    ){
        CoroutineScope(Dispatchers.IO).launch {
            val context = cmdIndexFragment.context
            val systemZipName = "system"
            val assetsPrefix = "appSystemDir"
            val systemZipPath = "${assetsPrefix}/$systemZipName"
            AssetsFileManager.copyFileOrDirFromAssets(
                context,
                systemZipPath,
                assetsPrefix,
                UsePath.cmdclickAppDirPath
            )
        }

    }
}