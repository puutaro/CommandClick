package com.puutaro.commandclick.util.state

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting

class EditFragmentArgs(
    private val readSharePreferenceMap: Map<String, String>,
    private val editType: EditTypeSettingsKey,
) {

    companion object {

        enum class FragmentArgsKey(
            val key: String,
        ){
            CURRENT_APP_DIR_PATH(SharePrefferenceSetting.current_app_dir.name),
            CURRENT_FANNEL_NAME(SharePrefferenceSetting.current_fannel_name.name),
            ON_SHORTCUT(SharePrefferenceSetting.on_shortcut.name),
            EDIT_TYPE("editType"),
        }

        enum class EditTypeSettingsKey(val key: String){
            CMD_VAL_EDIT("cmdValEdit"),
//            CMD_VAL_EDIT_EXECUTE("cmdValEditExecute"),
//            CMD_VAL_EDIT("cmdValEdit"),
            SETTING_VAL_EDIT("settingValEdit"),
        }

        enum class OnShortcutSettingKey(
            val key: String
        ) {
            ON("ON"),
            OFF("OFF"),
        }

        fun createReadSharePreferenceMap(
            currentAppDirPath: String,
            currentFannelName: String,
            onShortcut: String,
        ): Map<String, String> {
            return mapOf(
                SharePrefferenceSetting.current_app_dir.name to currentAppDirPath,
                SharePrefferenceSetting.current_fannel_name.name to currentFannelName,
                SharePrefferenceSetting.on_shortcut.name to onShortcut,
            )
        }

        fun getReadSharePreference(
            fragArgsBundle:  Bundle?,
        ): Map<String, String> {
            val currentAppDirPath =
                fragArgsBundle?.getString(
                    FragmentArgsKey.CURRENT_APP_DIR_PATH.key
                ) ?: SharePrefferenceSetting.current_app_dir.defalutStr
            val currentFannelName =
                fragArgsBundle?.getString(
                    FragmentArgsKey.CURRENT_FANNEL_NAME.key
                ) ?: SharePrefferenceSetting.current_fannel_name.defalutStr
            val onShortcut =
                fragArgsBundle?.getString(
                    FragmentArgsKey.ON_SHORTCUT.key
                ) ?: SharePrefferenceSetting.on_shortcut.defalutStr
            return mapOf(
                FragmentArgsKey.CURRENT_APP_DIR_PATH.key to currentAppDirPath,
                FragmentArgsKey.CURRENT_FANNEL_NAME.key to currentFannelName,
                FragmentArgsKey.ON_SHORTCUT.key to onShortcut,
            )
        }

        fun getEditType(fragArgsBundle:  Bundle?): EditTypeSettingsKey {
            return fragArgsBundle?.getString(
                    FragmentArgsKey.EDIT_TYPE.key
                ).let {
                    currentEditTypeSettingKey ->
                    EditTypeSettingsKey.values().find {
                        it.key == currentEditTypeSettingKey
                    }
            } ?: EditTypeSettingsKey.CMD_VAL_EDIT
        }
    }

    fun put(
        fragment: Fragment,
    ): Fragment {
        val fragArgsBundle = Bundle()
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        fragArgsBundle.putString(
            FragmentArgsKey.CURRENT_APP_DIR_PATH.key,
            currentAppDirPath,
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        fragArgsBundle.putString(
            FragmentArgsKey.CURRENT_FANNEL_NAME.key,
            currentFannelName,
        )
        val onShortcut = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.on_shortcut
        )
        fragArgsBundle.putString(
            FragmentArgsKey.ON_SHORTCUT.key,
            onShortcut,
        )
        fragArgsBundle.putString(
            FragmentArgsKey.EDIT_TYPE.key,
            editType.key,
        )
        fragment.arguments = fragArgsBundle
        return fragment
    }
}