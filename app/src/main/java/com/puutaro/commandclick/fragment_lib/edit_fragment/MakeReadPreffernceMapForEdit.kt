package com.puutaro.commandclick.fragment_lib.edit_fragment

import android.content.Intent
import android.content.SharedPreferences
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.SharePreffrenceMethod

class MakeReadPreffernceMapForEdit {
    companion object {
        fun make(
            getIntent: Intent?,
            howConfigEdit: Boolean,
            howEditApi: Boolean,
            sharePref: SharedPreferences?,
            tag: String?,
            apiEditFragmentTag: String
        ):  Map<String, String>{
            return if(
                !howConfigEdit && !howEditApi
            ) {
                SharePreffrenceMethod.makeReadSharePreffernceMap(
                    sharePref
                )
            } else if(tag == apiEditFragmentTag ){
                mapOf(
                    SharePrefferenceSetting.current_app_dir.name to (
                            getIntent?.getStringExtra(
                                SharePrefferenceSetting.current_app_dir.name
                            ) ?: String()
                            )
                    ,
                    SharePrefferenceSetting.current_script_file_name.name to  (
                            getIntent?.getStringExtra(
                                SharePrefferenceSetting.current_script_file_name.name
                            ) ?: String()
                            ),
                    SharePrefferenceSetting.on_shortcut.name to SharePrefferenceSetting.on_shortcut.defalutStr
                )
            } else {
                mapOf(
                    SharePrefferenceSetting.current_app_dir.name to UsePath.cmdclickConfigDirPath,
                    SharePrefferenceSetting.current_script_file_name.name to UsePath.cmdclickConfigFileName,
                    SharePrefferenceSetting.on_shortcut.name to SharePrefferenceSetting.on_shortcut.defalutStr
                )
            }
        }
    }
}