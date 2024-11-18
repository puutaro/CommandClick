package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit_list.config_settings.SearchBoxSettingsForEditList
import com.puutaro.commandclick.proccess.shell_macro.ShellMacroHandler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import java.io.File

object TitleImageAndViewSetter {

    private const val backstackCountSeparator = " "
    private const val switchOff = "OFF"

    private const val keySeparator = '|'
    fun set(
        fragment: Fragment,
        editTextView: AppCompatTextView,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editTitleImageView: AppCompatImageView,
        titleSettingMap: Map<String, String>?,
    ) {
        val titleTextMap = titleSettingMap?.get(
            EditBoxTitleKey.TEXT.key
        ).let {
            CmdClickMap.createMap(
                it,
                keySeparator
            )
        }.toMap()
        val titleImageMap = titleSettingMap?.get(
            EditBoxTitleKey.IMAGE.key
        ).let {
            CmdClickMap.createMap(
                it,
                keySeparator
            )
        }.toMap()
        titleTextMap.get(
            TitleTextSettingKey.VISIBLE.key
        ).let {
           val onTitleSwitch =
               it != switchOff
           if (onTitleSwitch) return@let
//           editFragment.binding.editTitleLinearlayout.isVisible = false
           return
       }
        setTitleText(
            fragment,
            editTextView,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            titleTextMap
        )
        setTitleImage(
            fragment,
            editTitleImageView,
            fannelInfoMap,
            titleImageMap,
        )
    }

    private fun setTitleText(
        fragment: Fragment,
        editTextView: AppCompatTextView,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        titleTextMap: Map<String, String>?
    ){
//        val binding = editFragment.binding
//        val editTextView = binding.editTextView

        editTextView.text = EditTextMaker.make(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            titleTextMap
        )
//            editFragment.editBoxTitleConfig
    }

    fun makeDefaultTitle(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
    ): String {
//        val fannelInfoMap =
//            fragment.fannelInfoMap

//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val backstackOrder = makeBackstackCount(
            fragment
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
        return CcPathTool.trimAllExtend(
            UsePath.makeOmitPath(currentScriptFileName)
        )
    }

    fun makeBackstackCount(
        fragment: Fragment
    ): Int {
        return execMakeBackstackCount(
            fragment
        )
    }

    private fun setTitleImage(
        fragment: Fragment,
        editTitleImageView: AppCompatImageView,
        fannelInfoMap: Map<String, String>,
        titleImageMap: Map<String, String>,
    ){
        val isNotSet = titleImageMap.get(
            TitleImageSettingKey.VISIBLE.key
        ) == switchOff
        if(isNotSet) return
//        val fannelInfoMap =
//            fragment.fannelInfoMap

//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
//        val binding = fragment.binding
//        val editTitleImageView = binding.editTitleImage
        FannelLogoSetter.setTitleFannelLogo(
            fragment,
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
        fragment: Fragment,
        titleImageView: AppCompatImageView?,
//        currentAppDirPath: String,
        selectedScriptName: String,
    ){
        val context = fragment.context
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
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        titleTextMap: Map<String, String>?,
    ): String {
        val defaultEditBoxTitle = TitleImageAndViewSetter.makeDefaultTitle(
            fragment,
            fannelInfoMap,
        )
        val shellConText = makeByShellCon(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            titleTextMap,
            busyboxExecutor,
            defaultEditBoxTitle,
        )
        return when(shellConText.isNullOrEmpty()) {
            false -> shellConText
            else -> SearchBoxSettingsForEditList.makeCurrentVariableValueInEditText(
                fragment,
                fannelInfoMap,
                defaultEditBoxTitle
            )
        }
    }
    private fun makeByShellCon(
        fragment: Fragment,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        editTextPropertyMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        currentVariableValue: String?,
    ): String? {
        if(
            editTextPropertyMap.isNullOrEmpty()
            || busyboxExecutor == null
        ) return null
//        val busyboxExecutor =
//            editFragment.busyboxExecutor
//                ?: return null
//        val setReplaceVariableMap =
//            editFragment.setReplaceVariableMap
//
//        val fannelInfoMap =
//            editFragment.fannelInfoMap

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
            fragment,
            setReplaceVariableMap,
            busyboxExecutor,
            repValMap,
            shellConSrc,
//            currentAppDirPath,
            currentFannelName,
            currentVariableValue
        )
        val backstackCountKey =
            SearchBoxSettingsForEditList.backstackCountMarkForInsertEditText
        val backstackCountMap = mapOf(
            backstackCountKey to execMakeBackstackCount(
                fragment
            ).toString()
        )
        val updateRepValMap = repValMap + backstackCountMap
        return ShellMacroHandler.handle(
            fragment.context,
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
        fragment: Fragment,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        repValMap: Map<String, String>?,
        shellConSrc: String,
//        currentAppDirPath: String,
        currentFannelName: String,
        currentVariableValue: String?
    ): String? {
        val shellCon = SetReplaceVariabler.execReplaceByReplaceVariables(
            shellConSrc,
            setReplaceVariableMap,
//            currentAppDirPath,
            currentFannelName
        ).replace(
            "\${defaultEditBoxTitle}",
            currentVariableValue ?: String(),
        ).let {
            SearchBoxSettingsForEditList.backStackMarkReplace(
                fragment,
                it
            )
        }
        if(
            shellCon.isEmpty()
        ) return null
        return busyboxExecutor?.getCmdOutput(
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