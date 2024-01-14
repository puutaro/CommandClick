package com.puutaro.commandclick.util.state

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting

class EditFragmentArgs(
    private val readSharePreferenceMap: Map<String, String>,
) {

    companion object {

        enum class FragmentArgsKey(
            val key: String,
        ){
            CURRENT_APP_DIR_PATH("currentAppDirPath"),
            CURRENT_FANNEL_NAME("currentFannelName"),
            ON_SHORTCUT("onShortcut"),
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

        fun get(
            fragment: Fragment,
        ): Map<String, String> {
            val fragArgsBundle = fragment.arguments
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
                SharePrefferenceSetting.current_app_dir.name to currentAppDirPath,
                SharePrefferenceSetting.current_fannel_name.name to currentFannelName,
                SharePrefferenceSetting.on_shortcut.name to onShortcut,
            )
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
        fragment.arguments = fragArgsBundle
        return fragment
    }
}