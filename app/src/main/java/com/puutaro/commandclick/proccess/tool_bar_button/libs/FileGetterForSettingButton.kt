package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.tool_bar_button.common_settings.JsPathMacroForSettingButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder

class FileGetterForSettingButton(
    fragment: Fragment,
) {
    private val context = fragment.context
    private var updateBroadcastIntent: Intent? = null
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
        val sourceFilePath =
            pathSource.absolutePath ?: String()

        val type = ListIndexEditConfig.getListIndexType(
            fragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
            -> {}
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> execGetForNormal(
                fragment,
                sourceFilePath,
            )
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
            -> execGetForTsv(
                fragment,
                sourceFilePath,
            )
        }



        Toast.makeText(
            context,
            "get file ok",
            Toast.LENGTH_LONG
        ).show()
    }

    fun get(
        settingMenuMapList: List<Map<String, String>?>,
        parentDirPathSrc: String,
    ){
        val extraMap =
            ExtraArgsTool.createExtraMapFromMenuMapList(
                settingMenuMapList,
                JsPathMacroForSettingButton.ADD.name,
                MenuSettingTool.MenuSettingKey.JS_PATH.key,
                "!",
            )
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
        ListIndexForEditAdapter.execAddForFile(
            editFragment,
            sourceFilePath,
        )
//        val targetScriptFilePathSource = "${parentDirPath}/${getFileName}"
//        FileSystems.execCopyFileWithDir(
//            File(sourceFilePath),
//            File(targetScriptFilePathSource),
//            true
//        )
//        ListIndexForEditAdapter.sortInAddFile(
//            editFragment,
//            listIndexListViewHolder,
//            targetScriptFilePathSource,
//        )
//        updateBroadcastIntent?.let {
//            context?.sendBroadcast(
//                it
//            )
//        }
    }

    private fun execGetForTsv(
        editFragment: EditFragment,
        sourceFilePath: String,
    ){
        val sourceScriptFilePathObj = File(sourceFilePath)
        val sourceScriptName = sourceScriptFilePathObj.name
        val insertLine = "${sourceScriptName}\t${sourceFilePath}"
        ListIndexForEditAdapter.execAddForTsv(
            editFragment,
            insertLine
        )
    }
}