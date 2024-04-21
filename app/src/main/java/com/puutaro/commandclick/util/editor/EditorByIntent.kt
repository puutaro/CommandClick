package com.puutaro.commandclick.util.editor

import android.content.Context
import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.BuildConfig
import java.io.File

class EditorByIntent(
    private val parentDirPath: String,
    private val fileName: String,
    private val context: Context?
) {
    fun byIntent(){
        val openFile = File(
            parentDirPath,
            fileName
        )
        val uri = FileProvider.getUriForFile(
            context as Context,
            BuildConfig.APPLICATION_ID + ".provider",
            openFile
        )
        val intent = Intent(Intent.ACTION_EDIT)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension("sh")
        intent.setDataAndType(uri, mimetype)
        try {
            context.startActivity(intent)
        } catch(e: Exception){
            ToastUtils.showLong("no editor app, why not intall?")
        }
    }
}