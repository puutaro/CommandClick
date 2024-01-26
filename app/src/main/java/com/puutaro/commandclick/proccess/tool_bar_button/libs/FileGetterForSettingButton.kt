package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.content.Intent
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.extra_args.ExtraArgsTool
import com.puutaro.commandclick.proccess.menu_tool.MenuSettingTool
import com.puutaro.commandclick.proccess.tool_bar_button.common_settings.JsPathMacroForSettingButton
import com.puutaro.commandclick.util.FileSystems
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
        val getFileName = pathSource.name
        val targetScriptFilePathSource = "${parentDirPath}/${getFileName}"
        FileSystems.execCopyFileWithDir(
            File(sourceFilePath),
            File(targetScriptFilePathSource),
            true
        )
        updateBroadcastIntent?.let {
            context?.sendBroadcast(
                it
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
}