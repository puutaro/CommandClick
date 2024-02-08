package com.puutaro.commandclick.util.state

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting

class EditFragmentArgs(
    private val readSharePreferenceMap: Map<String, String>,
    private val editType: EditTypeSettingsKey,
    private val srcReadSharePreferenceMap: Map<String, String>? = null,
) {

    companion object {

        enum class FragmentArgsKey(
            val key: String,
        ){
            CURRENT_APP_DIR_PATH(SharePrefferenceSetting.current_app_dir.name),
            CURRENT_FANNEL_NAME(SharePrefferenceSetting.current_fannel_name.name),
            ON_SHORTCUT(SharePrefferenceSetting.on_shortcut.name),
            CURRENT_FANNEL_STATE(SharePrefferenceSetting.current_fannel_state.name),
            EDIT_TYPE("editType"),
            SRC_CURRENT_APP_DIR_PATH("src_${SharePrefferenceSetting.current_app_dir.name}"),
            SRC_CURRENT_FANNEL_NAME("src_${SharePrefferenceSetting.current_fannel_name.name}"),
            SRC_ON_SHORTCUT("src_${SharePrefferenceSetting.on_shortcut.name}"),
            SRC_CURRENT_FANNEL_STATE("src_${SharePrefferenceSetting.current_fannel_state.name}"),
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
            fannelState: String,
        ): Map<String, String> {
            return mapOf(
                SharePrefferenceSetting.current_app_dir.name to currentAppDirPath,
                SharePrefferenceSetting.current_fannel_name.name to currentFannelName,
                SharePrefferenceSetting.on_shortcut.name to onShortcut,
                SharePrefferenceSetting.current_fannel_state.name to fannelState,
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
            val currentFannelState =
                fragArgsBundle?.getString(
                    FragmentArgsKey.CURRENT_FANNEL_STATE.key
                ) ?: SharePrefferenceSetting.current_fannel_state.defalutStr
            return mapOf(
                FragmentArgsKey.CURRENT_APP_DIR_PATH.key to currentAppDirPath,
                FragmentArgsKey.CURRENT_FANNEL_NAME.key to currentFannelName,
                FragmentArgsKey.ON_SHORTCUT.key to onShortcut,
                FragmentArgsKey.CURRENT_FANNEL_STATE.key to currentFannelState,
            )
        }

        fun getSrcReadSharePreference(
            fragArgsBundle: Bundle?,
        ): Map<String, String>? {
            val currentAppDirPath =
                fragArgsBundle?.getString(
                    FragmentArgsKey.SRC_CURRENT_APP_DIR_PATH.key
                ) ?: return null
            val currentFannelName =
                fragArgsBundle.getString(
                    FragmentArgsKey.SRC_CURRENT_FANNEL_NAME.key
                ) ?: return null
            val onShortcut =
                fragArgsBundle.getString(
                    FragmentArgsKey.SRC_ON_SHORTCUT.key
                ) ?: SharePrefferenceSetting.on_shortcut.defalutStr
            val currentFannelState =
                fragArgsBundle.getString(
                    FragmentArgsKey.SRC_CURRENT_FANNEL_STATE.key
                ) ?: SharePrefferenceSetting.current_fannel_state.defalutStr
            return mapOf(
                FragmentArgsKey.CURRENT_APP_DIR_PATH.key to currentAppDirPath,
                FragmentArgsKey.CURRENT_FANNEL_NAME.key to currentFannelName,
                FragmentArgsKey.ON_SHORTCUT.key to onShortcut,
                FragmentArgsKey.CURRENT_FANNEL_STATE.key to currentFannelState,
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

        srcReadSharePreferenceMap?.let {
            val srcCurrentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
                it,
                SharePrefferenceSetting.current_app_dir
            )
            val srcCurrentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
                it,
                SharePrefferenceSetting.current_fannel_name
            )
            val srcOnShortcut = SharePreferenceMethod.getReadSharePreffernceMap(
                it,
                SharePrefferenceSetting.on_shortcut
            )
            val srcCurrentFannelState = SharePreferenceMethod.getReadSharePreffernceMap(
                it,
                SharePrefferenceSetting.current_fannel_state
            )
            fragArgsBundle.putString(
                FragmentArgsKey.SRC_CURRENT_APP_DIR_PATH.key,
                srcCurrentAppDirPath,
            )
            fragArgsBundle.putString(
                FragmentArgsKey.SRC_CURRENT_FANNEL_NAME.key,
                srcCurrentFannelName,
            )
            fragArgsBundle.putString(
                FragmentArgsKey.SRC_ON_SHORTCUT.key,
                srcOnShortcut,
            )
            fragArgsBundle.putString(
                FragmentArgsKey.SRC_CURRENT_FANNEL_STATE.key,
                srcCurrentFannelState,
            )
        }

        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val onShortcut = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.on_shortcut
        )
        val currentFannelState = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreferenceMap,
            SharePrefferenceSetting.current_fannel_state
        )
        fragArgsBundle.putString(
            FragmentArgsKey.CURRENT_APP_DIR_PATH.key,
            currentAppDirPath,
        )
        fragArgsBundle.putString(
            FragmentArgsKey.CURRENT_FANNEL_NAME.key,
            currentFannelName,
        )
        fragArgsBundle.putString(
            FragmentArgsKey.ON_SHORTCUT.key,
            onShortcut,
        )
        fragArgsBundle.putString(
            FragmentArgsKey.CURRENT_FANNEL_STATE.key,
            currentFannelState,
        )
        fragArgsBundle.putString(
            FragmentArgsKey.EDIT_TYPE.key,
            editType.key,
        )
        fragment.arguments = fragArgsBundle
        return fragment
    }
}