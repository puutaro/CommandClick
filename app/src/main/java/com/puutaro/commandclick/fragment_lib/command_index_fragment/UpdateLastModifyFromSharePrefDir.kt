package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.NoUpdatelastModifyForCmdIndex
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object UpdateLastModifyFromSharePrefDir {

    fun update(
        sharePref: FannelInfoTool.FannelInfoSharePref?,
    ) {
        val cmdclickAppDirAdminPath = UsePath.cmdclickAppDirAdminPath
        val checkCurrentAppDirPathSource = sharePref?.sharePref?.getString(
            FannelInfoSetting.current_app_dir.name,
            FannelInfoSetting.current_app_dir.defalutStr
        ) ?: FannelInfoSetting.current_app_dir.defalutStr
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
                UsePath.JS_FILE_SUFFIX
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
            File(
                cmdclickAppDirAdminPath,
                currentDirName + UsePath.JS_FILE_SUFFIX
            ).absolutePath
        )
    }
}