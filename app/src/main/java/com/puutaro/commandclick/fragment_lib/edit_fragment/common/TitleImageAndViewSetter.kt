package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.QrLogo
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool

object TitleImageAndViewSetter {

    private const val backstackCountSeparator = " "
    fun set(
        editFragment: EditFragment,
    ) {
        val titleTextMap = editFragment.editBoxTitleConfig.get(
            EditBoxTitleKey.TEXT.key
        ).let {
            CmdClickMap.createMap(
                it,
               '|'
            )
        }.toMap()
        setTitleText(
            editFragment,
            titleTextMap
        )

        setTitleImage(
            editFragment,
        )
    }

    private fun setTitleText(
        editFragment: EditFragment,
        titleTextMap: Map<String, String>?
    ){
        val binding = editFragment.binding
        val editTextView = binding.editTextView

        editTextView.text = EditTextMaker.make(
            editFragment,
            titleTextMap
        )
//            editFragment.editBoxTitleConfig
    }

    fun makeDefaultTitle(
        editFragment: EditFragment,
    ): String {
        val readSharePreferenceMap =
            editFragment.readSharePreferenceMap

        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val backstackOrder = makeBackstackCount(
            editFragment
        )
        return listOf(
            "(${backstackOrder})",
            makeCompressFannelPath(
                currentAppDirPath,
                currentFannelName
            ),
        ).joinToString(backstackCountSeparator)
    }

    fun makeCompressFannelPath(
        currentAppDirPath: String,
        currentScriptFileName: String
    ): String {
        return "${UsePath.makeOmitPath(currentAppDirPath)}/${currentScriptFileName}"
    }
//    fun makeTitleForEditTitle(
//        editFragment: EditFragment,
//        title: String
//    ): String {
//        val backstackOrder = makeBackstackCount(
//            editFragment
//        )
//        return listOf(
//            "(${backstackOrder})",
//            title
//        ).joinToString(backstackCountSeparator)
//    }

    fun makeBackstackCount(
        fragment: Fragment
    ): Int {
        return fragment
            .activity
            ?.supportFragmentManager
            ?.backStackEntryCount
            ?: 0
    }

    private fun setTitleImage(
        editFragment: EditFragment,
    ){
        val readSharePreferenceMap =
            editFragment.readSharePreferenceMap

        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val binding = editFragment.binding
        val editTitleImageView = binding.editTitleImage
        QrLogo(editFragment).setTitleQrLogo(
            editTitleImageView,
            currentAppDirPath,
            currentFannelName
        )
    }
}

private object EditTextMaker {

    fun make(
        editFragment: EditFragment,
        titleTextMap: Map<String, String>?,
    ): String {
        val defaultEditBoxTitle = TitleImageAndViewSetter.makeDefaultTitle(
            editFragment,
        )
        val shellConText = makeByShellCon(
            editFragment,
            titleTextMap,
            defaultEditBoxTitle,
        )
        return when(shellConText.isNullOrEmpty()) {
            false -> shellConText
            else -> SearchBoxSettingsForListIndex.makeCurrentVariableValueInEditText(
                editFragment,
                defaultEditBoxTitle
            )
        }
    }
    private fun makeByShellCon(
        editFragment: EditFragment,
        editTextPropertyMap: Map<String, String>?,
        currentVariableValue: String?,
    ): String? {
        if(
            editTextPropertyMap.isNullOrEmpty()
        ) return null
        val context = editFragment.context
            ?: return null
        val setReplaceVariableMap =
            editFragment.setReplaceVariableMap

        val readSharePreferenceMap =
            editFragment.readSharePreferenceMap

        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreferenceMap
        )
        val currentFannelName = SharePrefTool.getCurrentFannelName(
            readSharePreferenceMap
        )
        val shellConSrc = editTextPropertyMap.get(
            TitleTextSettingKey.SHELL_CON.key
        )
        val shellCon = when (
            shellConSrc.isNullOrEmpty()
        ) {
            true ->
                EditSettingExtraArgsTool.makeShellCon(editTextPropertyMap)

            else -> shellConSrc
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                currentFannelName
            ).replace(
                "\${defaultEditBoxTitle}",
                currentVariableValue ?: String(),
            ).let {
                SearchBoxSettingsForListIndex.backStackMarkReplace(
                    editFragment,
                    it
                )
            }
        }
        val busyboxExecutor = BusyboxExecutor(
            context,
            UbuntuFiles(context),
        )
        val repValMap = editTextPropertyMap.get(
            TitleTextSettingKey.ARGS.key
        ).let {
            CmdClickMap.createMap(
                it,
                '?'
            )
        }.toMap()
        return busyboxExecutor.getCmdOutput(
            shellCon,
            repValMap
        )

    }
}

private enum class EditBoxTitleKey(
    val key: String,
){
    TEXT("text"),
    IMAGE("image"),
}

enum class TitleTextSettingKey(
    val key: String
){
//    SIZE("size"),
    HEIGHT("height"),
    SHELL_PATH("shellPath"),
    SHELL_CON("shellCon"),
    ARGS("args"),
}