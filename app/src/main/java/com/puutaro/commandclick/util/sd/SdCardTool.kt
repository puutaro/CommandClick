package com.puutaro.commandclick.util.sd

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.getAbsolutePath
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.SharePrefTool
import com.puutaro.commandclick.util.file.FileSystems

object SdCardTool {
    
    private val sdRootDirTxtPath = UsePath.sdRootDirTxtPath

    private enum class SdCardKey(
        val key: String,
    ) {
        SdCardTree("sdcardTree")
    }

    class UbuntuBackupSharePref(
        val backupSharePref: SharedPreferences?
    )

    fun getSharePref(
        context: Context?
    ): UbuntuBackupSharePref {
        val ubuntuBackup = "ubuntuBackup"
        val sharePref = context?.getSharedPreferences(
            ubuntuBackup,
            Context.MODE_PRIVATE
        )
        return UbuntuBackupSharePref(sharePref)
    }

    fun getTreeUri(
        context: Context?,
        ubuntuBackupSharePref: UbuntuBackupSharePref?,
    ): DocumentFile? {
        if(
            context == null
        ) return null
        val treeUri = getTreeUriStr(
            ubuntuBackupSharePref
        )?.let {
            Uri.parse(it)
        } ?: return null
        return DocumentFile.fromTreeUri(context, treeUri)
    }

    fun putTreeUri(
        context: Context?,
        ubuntuBackupSharePref: UbuntuBackupSharePref?,
        sdCardDir: Uri,
    ){
        if(
            context == null
            || ubuntuBackupSharePref == null
        ) return
        SharePrefTool.putSharePref(
            ubuntuBackupSharePref.backupSharePref,
            mapOf(
                SdCardKey.SdCardTree.key to sdCardDir.toString()
            )
        )
        getTreeUri(
            context,
            ubuntuBackupSharePref,
        )?.getAbsolutePath(context)?.let {
            FileSystems.writeFile(
                sdRootDirTxtPath,
                it
            )
        }
    }

    fun removeTreeUri(
        ubuntuBackupSharePref: UbuntuBackupSharePref?,
    ){
        SharePrefTool.removeSharePref(
            ubuntuBackupSharePref?.backupSharePref,
            SdCardKey.SdCardTree.key
        )
        FileSystems.removeFiles(
            sdRootDirTxtPath
        )
    }

    fun getTreeUriStr(
        ubuntuBackupSharePref: UbuntuBackupSharePref?,
    ): String? {
        if(
            ubuntuBackupSharePref == null
        ) return null
        val sdCardTreeUriStr = ubuntuBackupSharePref.backupSharePref?.getString(
            SdCardKey.SdCardTree.key,
            null
        )
        if(
            sdCardTreeUriStr.isNullOrEmpty()
        ){
            FileSystems.removeFiles(
                sdRootDirTxtPath
            )
        }
        return sdCardTreeUriStr
    }



    fun isAvailable(context: Context?): Boolean {
        if(context == null) return false
        val dirArr = context.getExternalFilesDirs(null)
        return dirArr.firstOrNull {
            if (it == null) return@firstOrNull false
            // isExternalStorageRemovableはAndroid5.0から利用できるAPI。
            // 取り外し可能かどうか（SDカードかどうか）を判定している。
            if (
                !Environment.isExternalStorageRemovable(it)
            ) return@firstOrNull false
            // 取り外し可能であればSDカード。
            true
        } != null
    }

//    fun getSdCardUseDirPath(context: Context): String? {
//        // getExternalFilesDirsはAndroid4.4から利用できるAPI。
//        // filesディレクトリのリストを取得できる。
//        val dirArr = context.getExternalFilesDirs(null)
//        return dirArr.firstOrNull {
//            if (it == null) return@firstOrNull false
//            // isExternalStorageRemovableはAndroid5.0から利用できるAPI。
//            // 取り外し可能かどうか（SDカードかどうか）を判定している。
//            if (
//                !Environment.isExternalStorageRemovable(it)
//            ) return@firstOrNull false
//            // 取り外し可能であればSDカード。
//            true
//        }?.absolutePath?.split("/")?.filterIndexed {
//                index, _ ->
//            index < 3
//        }?.joinToString("/")
//    }
//
//    fun getSdCardFilesDirPath(context: Context?): String? {
//        if(
//            context == null
//        ) return null
//        // getExternalFilesDirsはAndroid4.4から利用できるAPI。
//        // filesディレクトリのリストを取得できる。
//        val dirArr = context.getExternalFilesDirs(null)
//        return dirArr.firstOrNull {
//            if (it == null) return@firstOrNull false
//            val path = it.absolutePath
//            // isExternalStorageRemovableはAndroid5.0から利用できるAPI。
//            // 取り外し可能かどうか（SDカードかどうか）を判定している。
//            if (
//                !Environment.isExternalStorageRemovable(it)
//            ) return@firstOrNull false
//            // 取り外し可能であればSDカード。
//            true
//        }?.absolutePath
//    }
}