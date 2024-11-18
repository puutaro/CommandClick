package com.puutaro.commandclick.proccess.edit_list.config_settings

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.util.state.FannelInfoTool

object SearchBoxSettingsForEditList {

    enum class SearchBoxSettingKey(
        val key: String,
    ) {
        HINT("hint"),
        VISIBLE("visible"),
    }

    enum class SearchBoxVisibleKey {
        OFF
    }

    const val backstackCountMarkForInsertEditText = "\${BACKSTACK_COUNT}"
    private const val fannelTitleMarkForInsertEditText = "\${FANNEL_TITLE}"

    fun makeCurrentVariableValueInEditText(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        currentVariableValue: String?
    ): String {
        if(
            currentVariableValue.isNullOrEmpty()
        ) return String()
        if(
            fragment !is EditFragment
        ) return currentVariableValue
//        val fannelInfoMap = fragment.fannelInfoMap
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
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
//                    currentAppDirPath,
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

    fun backStackMarkReplace(
        fragment: Fragment,
        currentVariableValue: String,
    ): String {
        val backstackNum =
            TitleImageAndViewSetter.makeBackstackCount(fragment)
        return currentVariableValue.replace(
            backstackCountMarkForInsertEditText,
            backstackNum.toString(),
        )
    }
}