package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ExecAddForListIndexAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.tool_bar_button.common_settings.JsPathMacroForToolbarButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder

class FileOrDirGetterForSettingButton(
    fragment: Fragment,
) {
    private val context = fragment.context
    private var updateBroadcastIntent: Intent? = null
    private var onDirectoryPick = false
    private var parentDirPath = String()
    private val prefixRegex = Regex("^content.*fileprovider/root/storage")
    private val getFile = fragment.registerForActivityResult(
        ActivityResultContracts.OpenDocument()) { uri ->
        if (
            uri == null
            || uri.toString() == String()
        ) return@registerForActivityResult
        if(
            fragment !is EditFragment
        ) return@registerForActivityResult
        val pathSource = runBlocking {
            File(
                withContext(Dispatchers.IO) {
                    URLDecoder.decode(
                        uri.toString(), Charsets.UTF_8.name()
                    )
                }.replace(prefixRegex, "/storage")
            )
        }
        val sourceFileOrDirPath = if(onDirectoryPick) {
            pathSource.parent
        } else {
            pathSource.absolutePath
        }  ?: String()

        val type = ListIndexEditConfig.getListIndexType(
            fragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> {}
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {
                if(onDirectoryPick) return@registerForActivityResult
                execGetForNormal(
                    fragment,
                    sourceFileOrDirPath,
                )
            }
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> execGetForTsv(
                fragment,
                sourceFileOrDirPath,
            )
        }
    }

    fun get(
        settingMenuMapList: List<Map<String, String>?>,
        parentDirPathSrc: String,
        onDirectoryPickSrc: Boolean = false
    ){
        val extraMap =
            ExtraArgsTool.createExtraMapFromMenuMapList(
                settingMenuMapList,
                JsPathMacroForToolbarButton.ADD.name,
                MenuSettingTool.MenuSettingKey.JS_PATH.key,
                "!",
            )
        onDirectoryPick = onDirectoryPickSrc
        parentDirPath =
            parentDirPathSrc
        updateBroadcastIntent =
            ExtraArgsTool.makeBroadcastIntent(
                extraMap,
                "&"
            )
        getFile.launch(
            arrayOf(Intent.CATEGORY_OPENABLE)
        )
    }

    private fun execGetForNormal(
        editFragment: EditFragment,
        sourceFilePath: String,
    ){
        ExecAddForListIndexAdapter.execAddByCopyFileHere(
            editFragment,
            sourceFilePath,
        )
    }

    private fun execGetForTsv(
        editFragment: EditFragment,
        sourceFilePath: String,
    ){
        val sourceScriptFilePathObj = File(sourceFilePath)
        val sourceScriptName = sourceScriptFilePathObj.name
        val insertLine = "${sourceScriptName}\t${sourceFilePath}"
        ExecAddForListIndexAdapter.execAddForTsv(
            editFragment,
            insertLine
        )
    }
}