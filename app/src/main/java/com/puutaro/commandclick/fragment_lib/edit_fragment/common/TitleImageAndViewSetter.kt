package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.QrLogo
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool

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
        val fannelInfoMap =
            editFragment.fannelInfoMap

        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
            fannelInfoMap
        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
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
        return execMakeBackstackCount(
            fragment
        )
    }

    private fun setTitleImage(
        editFragment: EditFragment,
    ){
        val fannelInfoMap =
            editFragment.fannelInfoMap

        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
            fannelInfoMap
        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
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
        val busyboxExecutor =
            editFragment.busyboxExecutor
                ?: return null
        val setReplaceVariableMap =
            editFragment.setReplaceVariableMap

        val fannelInfoMap =
            editFragment.fannelInfoMap

        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
            fannelInfoMap
        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val shellConSrc = editTextPropertyMap.get(
            TitleTextSettingKey.SHELL_CON.key
        )
        val repValMap = editTextPropertyMap.get(
            TitleTextSettingKey.ARGS.key
        ).let {
            CmdClickMap.createMap(
                it,
                '?'
            )
        }.toMap()
        if(
            !shellConSrc.isNullOrEmpty()
        ) return getOutputByShellCon(
            editFragment,
            repValMap,
            shellConSrc,
            currentAppDirPath,
            currentFannelName,
            currentVariableValue
        )
        val backstackCountKey =
            SearchBoxSettingsForListIndex.backstackCountMarkForInsertEditText
        val backstackCountMap = mapOf(
            backstackCountKey to execMakeBackstackCount(
                editFragment
            ).toString()
        )
        val updateRepValMap = repValMap + backstackCountMap
        return ShellMacroHandler.handle(
            editFragment.context,
            busyboxExecutor,
            editTextPropertyMap.get(
                TitleTextSettingKey.SHELL_PATH.key
            ) ?: String(),
            setReplaceVariableMap,
            updateRepValMap
        )
//                EditSettingExtraArgsTool.makeShellCon(editTextPropertyMap)
    }

    private fun getOutputByShellCon(
        editFragment: EditFragment,
        repValMap: Map<String, String>?,
        shellConSrc: String,
        currentAppDirPath: String,
        currentFannelName: String,
        currentVariableValue: String?
    ): String? {
        val shellCon = SetReplaceVariabler.execReplaceByReplaceVariables(
            shellConSrc,
            editFragment.setReplaceVariableMap,
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
        if(
            shellCon.isEmpty()
        ) return null
        return editFragment.busyboxExecutor?.getCmdOutput(
            shellCon,
            repValMap
        )
    }
}

private fun execMakeBackstackCount(
    fragment: Fragment
): Int {
    return fragment
        .activity
        ?.supportFragmentManager
        ?.backStackEntryCount
        ?: 0
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