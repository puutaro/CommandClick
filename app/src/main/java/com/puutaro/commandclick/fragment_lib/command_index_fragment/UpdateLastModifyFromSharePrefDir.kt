package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.NoUpdatelastModifyForCmdIndex
import com.puutaro.commandclick.util.FileSystems
import java.io.File

object UpdateLastModifyFromSharePrefDir {

    fun update(
        sharePref: SharedPreferences?,
    ) {
        val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
        val checkCurrentAppDirPathSource = sharePref?.getString(
            SharePrefferenceSetting.current_app_dir.name,
            SharePrefferenceSetting.current_app_dir.defalutStr
        ) ?: SharePrefferenceSetting.current_app_dir.defalutStr
        val checkCurrentAppDirPath = if(
            NoUpdatelastModifyForCmdIndex().judge(
                checkCurrentAppDirPathSource
            )
        ) {
            val cmdclickAppDirPath = UsePath.cmdclickAppDirPath
            val currentDirName = FileSystems.filterSuffixJsFiles(
                cmdclickAppDirAdminPath,
                "on"
            ).first().removeSuffix(
                CommandClickScriptVariable.JS_FILE_SUFFIX
            )
            "${cmdclickAppDirPath}/${currentDirName}"
        } else {
            checkCurrentAppDirPathSource
        }
        val currenDirObj = File(checkCurrentAppDirPath)
        if(
            !currenDirObj.isDirectory
        ) return
        val currentDirName = currenDirObj.name
        FileSystems.updateLastModified(
            cmdclickAppDirAdminPath,
            currentDirName + CommandClickScriptVariable.JS_FILE_SUFFIX
        )
    }
}