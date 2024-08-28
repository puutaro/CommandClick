package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.SearchBoxSettingsForListIndex
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import java.io.File

object TitleImageAndViewSetter {

    private const val backstackCountSeparator = " "
    private const val switchOff = "OFF"
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
        val titleImageMap = editFragment.editBoxTitleConfig.get(
            EditBoxTitleKey.IMAGE.key
        ).let {
            CmdClickMap.createMap(
                it,
                '|'
            )
        }.toMap()
        titleTextMap.get(
            TitleTextSettingKey.VISIBLE.key
        ).let {
           val onTitleSwitch =
               it != switchOff
           if (onTitleSwitch) return@let
           editFragment.binding.editTitleLinearlayout.isVisible = false
           return
       }
        setTitleText(
            editFragment,
            titleTextMap
        )
        setTitleImage(
            editFragment,
            titleImageMap
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

//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val backstackOrder = makeBackstackCount(
            editFragment
        )
        return listOf(
            "(${backstackOrder})",
            makeCompressFannelPath(
//                currentAppDirPath,
                currentFannelName
            ),
        ).joinToString(backstackCountSeparator)
    }

    fun makeCompressFannelPath(
//        currentAppDirPath: String,
        currentScriptFileName: String
    ): String {
        return "${UsePath.makeOmitPath(UsePath.cmdclickDefaultAppDirPath)}/${currentScriptFileName}"
    }

    fun makeBackstackCount(
        fragment: Fragment
    ): Int {
        return execMakeBackstackCount(
            fragment
        )
    }

    private fun setTitleImage(
        editFragment: EditFragment,
        titleImageMap: Map<String, String>,
    ){
        val isNotSet = titleImageMap.get(
            TitleImageSettingKey.VISIBLE.key
        ) == switchOff
        if(isNotSet) return
        val fannelInfoMap =
            editFragment.fannelInfoMap

//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val binding = editFragment.binding
        val editTitleImageView = binding.editTitleImage
        FannelLogoSetter.setTitleFannelLogo(
            editFragment,
            editTitleImageView,
//        currentAppDirPath: String,
            currentFannelName,
        )
//        QrLogo(editFragment).setTitleFannelLogo(
//            editTitleImageView,
////            currentAppDirPath,
//            currentFannelName
//        )
    }
}

private object FannelLogoSetter {
    fun setTitleFannelLogo(
        editFragment: EditFragment,
        titleImageView: AppCompatImageView?,
//        currentAppDirPath: String,
        selectedScriptName: String,
    ){
        val context = editFragment.context
            ?: return
        if(
            titleImageView == null
        ) return
//        val fannelDirName = CcPathTool.makeFannelDirName(selectedScriptName)
        val logoPngPath = listOf(
            UsePath.fannelLogoPngPath,
        ).joinToString("/").let {
            ScriptPreWordReplacer.replace(
                it,
                selectedScriptName
            )
        }
//            "${UsePath.cmdclickDefaultAppDirPath}/$fannelDirName/${UsePath.qrPngRelativePath}"
        if(!File(logoPngPath).isFile) return

//        val isEditExecute = checkEditExecute(
////            currentAppDirPath,
//            selectedScriptName,
//        )
        titleImageView.setPadding(2, 2,2,2)
//        titleImageView.background = if(isEditExecute) {
//            AppCompatResources.getDrawable(context, R.color.terminal_color)
//        } else AppCompatResources.getDrawable(context, R.color.fannel_icon_color)
        val requestBuilder: RequestBuilder<Drawable> =
            Glide.with(context)
                .asDrawable()
                .sizeMultiplier(0.1f)
        Glide
            .with(context)
            .load(logoPngPath)
            .skipMemoryCache( true )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .thumbnail( requestBuilder )
            .into(titleImageView)
//        titleImageView.load(logoPngPath)
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

//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
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
//            currentAppDirPath,
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
//        currentAppDirPath: String,
        currentFannelName: String,
        currentVariableValue: String?
    ): String? {
        val shellCon = SetReplaceVariabler.execReplaceByReplaceVariables(
            shellConSrc,
            editFragment.setReplaceVariableMap,
//            currentAppDirPath,
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
    VISIBLE("visible"),
    SHELL_PATH("shellPath"),
    SHELL_CON("shellCon"),
    ARGS("args"),
}

enum class TitleImageSettingKey(
    val key: String
){
    VISIBLE("visible"),
}