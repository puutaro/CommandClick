package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib

import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.SharePreffrenceMethod

class AppDirectoryAdminEvent {
    companion object {
        fun invoke (
            sharedPref: SharedPreferences?,
            currentAppDirPath: String,
            selectedShellFileName: String
        ) {
            FileSystems.updateLastModified(
                currentAppDirPath,
                selectedShellFileName
            )
            val updateAppDirPath = UsePath.cmdclickAppDirPath + '/' +
                    selectedShellFileName.removeSuffix(
                        CommandClickShellScript.JS_FILE_SUFFIX
                    )
            FileSystems.createDirs(updateAppDirPath)
            SharePreffrenceMethod.putSharePreffrence(
                sharedPref,
                mapOf(
                    SharePrefferenceSetting.current_app_dir.name to updateAppDirPath
                )
            )
        }
    }
}