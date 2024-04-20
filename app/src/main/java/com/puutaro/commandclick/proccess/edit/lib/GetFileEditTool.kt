package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import com.blankj.utilcode.util.UriUtils
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder


object GetFileEditTool {
    suspend fun makeGetName(
        uri: Uri?
    ): File {
        val nativePickerPathObj = File(
            withContext(Dispatchers.IO) {
                val decodedUri = URLDecoder.decode(
                    uri.toString(),
                    Charsets.UTF_8.name(),
                )
                val documentDirResolver =
                    "content://com.android.externalstorage.documents/document/home"
                when(
                    decodedUri.startsWith(
                        "${documentDirResolver}:")
                ) {
                    true -> {
                        val path = decodedUri.replace(
                            Regex(
                                "^${documentDirResolver}:"
                            ),
                            "/storage/emulated/0/Documents/"
                        )
                        path

                    }
                    else -> UriUtils.uri2File(uri).absolutePath
                }
            }
        )
        if(
            nativePickerPathObj.isFile
        ) return nativePickerPathObj
        return File(
            withContext(Dispatchers.IO) {
                URLDecoder.decode(
                    uri.toString(),
                    Charsets.UTF_8.name(),
                )
            }.replace(
                Regex("^content.*fileprovider/root/storage"),
                "/storage"
            )
        )
    }

    fun getPath(context: Context?, uri: Uri): String? {

        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            println("getPath() uri: $uri")
            println("getPath() uri authority: " + uri.authority)
            println("getPath() uri path: " + uri.path)

            // ExternalStorageProvider
            if ("com.android.externalstorage.documents" == uri.authority) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                println("getPath() docId: " + docId + ", split: " + split.size + ", type: " + type)

                // This is for checking Main Memory
                return if ("primary".equals(type, ignoreCase = true)) {
                    if (split.size > 1) {
                        Environment.getExternalStorageDirectory().toString() + "/" + split[1] + "/"
                    } else {
                        Environment.getExternalStorageDirectory().toString() + "/"
                    }
                    // This is for checking SD Card
                } else {
                    "storage" + "/" + docId.replace(":", "/")
                }
            }
        }
        return null
    }
}
