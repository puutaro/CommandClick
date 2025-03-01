package com.puutaro.commandclick.util.state

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting

class EditFragmentArgs(
    private val fannelInfoMap: Map<String, String>,
    private val editType: EditTypeSettingsKey,
    private val srcFannelInfoMap: Map<String, String>? = null,
) {

    companion object {

        enum class FragmentArgsKey(
            val key: String,
        ){
//            CURRENT_APP_DIR_PATH(FannelInfoSetting.current_app_dir.name),
            CURRENT_FANNEL_NAME(FannelInfoSetting.current_fannel_name.name),
            ON_SHORTCUT(FannelInfoSetting.on_shortcut.name),
            CURRENT_FANNEL_STATE(FannelInfoSetting.current_fannel_state.name),
            EDIT_TYPE("editType"),
//            SRC_CURRENT_APP_DIR_PATH("src_${FannelInfoSetting.current_app_dir.name}"),
            SRC_CURRENT_FANNEL_NAME("src_${FannelInfoSetting.current_fannel_name.name}"),
            SRC_ON_SHORTCUT("src_${FannelInfoSetting.on_shortcut.name}"),
            SRC_CURRENT_FANNEL_STATE("src_${FannelInfoSetting.current_fannel_state.name}"),
        }

        enum class EditTypeSettingsKey(val key: String){
            CMD_VAL_EDIT(FragmentTagPrefix.Prefix.CMD_VAL_EDIT_PREFIX.str),
//            CMD_VAL_EDIT_EXECUTE("cmdValEditExecute"),
//            CMD_VAL_EDIT("cmdValEdit"),
//            SETTING_VAL_EDIT(FragmentTagPrefix.Prefix.SETTING_VAL_EDIT_PREFIX.str),
        }

        enum class OnShortcutSettingKey(
            val key: String
        ) {
            ON("ON"),
            OFF("OFF"),
        }

        fun createFannelInfoMap(
//            currentAppDirPath: String,
            currentFannelName: String,
            onShortcut: String,
            fannelState: String,
        ): Map<String, String> {
            return mapOf(
//                FannelInfoSetting.current_app_dir.name to currentAppDirPath,
                FannelInfoSetting.current_fannel_name.name to currentFannelName,
                FannelInfoSetting.on_shortcut.name to onShortcut,
                FannelInfoSetting.current_fannel_state.name to fannelState,
            )
        }

        fun getFannelInfoMap(
            fragArgsBundle:  Bundle?,
        ): Map<String, String> {
//            val currentAppDirPath =
//                fragArgsBundle?.getString(
//                    FragmentArgsKey.CURRENT_APP_DIR_PATH.key
//                ) ?: FannelInfoSetting.current_app_dir.defalutStr
            val currentFannelName =
                fragArgsBundle?.getString(
                    FragmentArgsKey.CURRENT_FANNEL_NAME.key
                ) ?: FannelInfoSetting.current_fannel_name.defalutStr
            val onShortcut =
                fragArgsBundle?.getString(
                    FragmentArgsKey.ON_SHORTCUT.key
                ) ?: FannelInfoSetting.on_shortcut.defalutStr
            val currentFannelState =
                fragArgsBundle?.getString(
                    FragmentArgsKey.CURRENT_FANNEL_STATE.key
                ) ?: FannelInfoSetting.current_fannel_state.defalutStr
            return mapOf(
//                FragmentArgsKey.CURRENT_APP_DIR_PATH.key to currentAppDirPath,
                FragmentArgsKey.CURRENT_FANNEL_NAME.key to currentFannelName,
                FragmentArgsKey.ON_SHORTCUT.key to onShortcut,
                FragmentArgsKey.CURRENT_FANNEL_STATE.key to currentFannelState,
            )
        }

        fun getSrcFannelInfoMap(
            fragArgsBundle: Bundle?,
        ): Map<String, String>? {
//            val currentAppDirPath =
//                fragArgsBundle?.getString(
//                    FragmentArgsKey.SRC_CURRENT_APP_DIR_PATH.key
//                ) ?: return null
            val currentFannelName =
                fragArgsBundle?.getString(
                    FragmentArgsKey.SRC_CURRENT_FANNEL_NAME.key
                ) ?: return null
            val onShortcut =
                fragArgsBundle.getString(
                    FragmentArgsKey.SRC_ON_SHORTCUT.key
                ) ?: FannelInfoSetting.on_shortcut.defalutStr
            val currentFannelState =
                fragArgsBundle.getString(
                    FragmentArgsKey.SRC_CURRENT_FANNEL_STATE.key
                ) ?: FannelInfoSetting.current_fannel_state.defalutStr
            return mapOf(
//                FragmentArgsKey.CURRENT_APP_DIR_PATH.key to currentAppDirPath,
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
                    EditTypeSettingsKey.entries.find {
                        it.key == currentEditTypeSettingKey
                    }
            } ?: EditTypeSettingsKey.CMD_VAL_EDIT
        }
    }

    fun put(
        fragment: Fragment,
    ): Fragment {
        val fragArgsBundle = Bundle()

        srcFannelInfoMap?.let {
//            val srcCurrentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//                it,
//            )
            val srcCurrentFannelName =FannelInfoTool.getCurrentFannelName(
                it,
            )
            val srcOnShortcut = FannelInfoTool.getOnShortcut(
                it,
            )
            val srcCurrentFannelState = FannelInfoTool.getCurrentStateName(
                it,
            )
//            fragArgsBundle.putString(
//                FragmentArgsKey.SRC_CURRENT_APP_DIR_PATH.key,
//                srcCurrentAppDirPath,
//            )
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
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val onShortcut = FannelInfoTool.getOnShortcut(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )
//        fragArgsBundle.putString(
//            FragmentArgsKey.CURRENT_APP_DIR_PATH.key,
//            currentAppDirPath,
//        )
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