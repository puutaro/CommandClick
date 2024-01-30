package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.util.state.SharePreferenceMethod

object SearchBoxSettingsForListIndex {

    enum class SearchBoxSettingKey(
        val key: String,
    ) {
        HINT("hint"),
        VISIBLE("visible"),
    }

    enum class SearchBoxVisibleKey {
        OFF
    }

    private const val backstackCountMarkForInsertEditText = "\${BACKSTACK_COUNT}"
    private const val fannelTitleMarkForInsertEditText = "\${FANNEL_TITLE}"

    fun makeCurrentVariableValueInEditText(
        fragment: Fragment,
        currentVariableValue: String?
    ): String {
        if(
            currentVariableValue.isNullOrEmpty()
        ) return String()
        if(
            fragment !is EditFragment
        ) return currentVariableValue
        val readSharePreffernceMap = fragment.readSharePreferenceMap

        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val replaceBackstackCurrentVariableValue =
            when(
                currentVariableValue.contains(backstackCountMarkForInsertEditText)
            ){
                true -> {
                    val backstackNum =
                        TitleImageAndViewSetter.makeBackstackCount(fragment)
                    currentVariableValue.replace(
                        backstackCountMarkForInsertEditText,
                        backstackNum.toString(),
                    )
                }
                else -> currentVariableValue
            }
        return when(
            replaceBackstackCurrentVariableValue.contains(fannelTitleMarkForInsertEditText)
        ){
            true -> {
                val fannelTitleStr = TitleImageAndViewSetter.makeCompressFannelPath(
                    currentAppDirPath,
                    currentFannelName,
                )
                replaceBackstackCurrentVariableValue.replace(
                    fannelTitleMarkForInsertEditText,
                    fannelTitleStr,
                )
            }
            else -> replaceBackstackCurrentVariableValue
        }
    }
}