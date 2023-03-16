package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems

object LongClickJsMaker {

    fun make(
        terminalFragment: TerminalFragment
    ){
        if(
            terminalFragment.currentAppDirPath == UsePath.cmdclickAppDirAdminPath
            || terminalFragment.currentAppDirPath == UsePath.cmdclickConfigDirPath
        ) return
        execMake(
            terminalFragment,
            UsePath.longPressSrcImageAnchorJsName
        )
        execMake(
            terminalFragment,
            UsePath.longPressSrcAnchorJsName
        )
        execMake(
            terminalFragment,
            UsePath.longPressImageAnchorJsName
        )
    }

    private fun execMake(
        terminalFragment: TerminalFragment,
        fileName: String
    ){
        val context = terminalFragment.context
        val defaultHitSystemDirPath =
            "${UsePath.cmdclickDefaultAppDirPath}/${UsePath.cmdclickHitSystemDirRelativePath}"
        val defaultSrcImageAnchorLongPressJsPath =
            "${defaultHitSystemDirPath}/${UsePath.longPressSrcImageAnchorJsName}"
        val assetRelativePath = "${UsePath.cmdclickHitSystemDirRelativePath}/${fileName}"
        FileSystems.createDirs(defaultHitSystemDirPath)
        FileSystems.copyFromAssets(
            context,
            assetRelativePath,
            defaultSrcImageAnchorLongPressJsPath
        )
        val currentHitSystemDirPath =
            "${terminalFragment.currentAppDirPath}/${UsePath.cmdclickHitSystemDirRelativePath}"
        val currentSrcImageAnchorLongPressJsPathPath =
            "${currentHitSystemDirPath}/${fileName}"
        FileSystems.createDirs(currentHitSystemDirPath)
        FileSystems.copyFromAssets(
            context,
            assetRelativePath,
            currentSrcImageAnchorLongPressJsPathPath
        )
    }
}