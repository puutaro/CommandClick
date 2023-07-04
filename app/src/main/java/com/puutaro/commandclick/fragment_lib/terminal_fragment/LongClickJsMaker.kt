package com.puutaro.commandclick.fragment_lib.terminal_fragment

import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.FileSystems

object LongClickJsMaker {

    fun make(
        terminalFragment: TerminalFragment
    ){
        if(
            terminalFragment.currentAppDirPath == UsePath.cmdclickAppDirAdminPath
            || terminalFragment.currentAppDirPath == UsePath.cmdclickSystemAppDirPath
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
        val assetRelativePath = "${UsePath.cmdclickHitSystemDirRelativePath}/${fileName}"

        val longPressJsContents = AssetsFileManager.readFromAssets(
            context,
            assetRelativePath,
        )

        writeLongPressJs(
            UsePath.cmdclickDefaultAppDirPath,
            fileName,
            longPressJsContents,
        )

        writeLongPressJs(
            terminalFragment.currentAppDirPath ,
            fileName,
            longPressJsContents,
        )
    }

    private fun writeLongPressJs(
        useAppDirPath : String,
        fileName: String,
        longPressContents: String,
    ){
        val currentHitSystemDirPath =
            "${useAppDirPath}/${UsePath.cmdclickHitSystemDirRelativePath}"
        FileSystems.createDirs(currentHitSystemDirPath)
        FileSystems.writeFile(
            currentHitSystemDirPath,
            fileName,
            longPressContents
        )
    }
}