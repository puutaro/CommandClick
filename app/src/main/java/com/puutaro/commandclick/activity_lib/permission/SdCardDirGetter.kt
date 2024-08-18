package com.puutaro.commandclick.activity_lib.permission

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Environment
import android.provider.DocumentsContract
import android.view.Gravity
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.getAbsolutePath
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.sd.SdCardTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class SdCardDirGetter(
    val activity: MainActivity
) {


    private val getSdcardDirLauncher = setSdcardDirGetter(activity)

    fun handle(
        activity: MainActivity,
        isCreate: Boolean,
    ){
        val sharePref =  SdCardTool.getSharePref(activity)
        when(isCreate) {
            false -> {
                SdCardTool.removeTreeUri(sharePref)
            }
            else -> {
                SdCardTool.getTreeUriStr(sharePref) ?: let {
                    CoroutineScope(Dispatchers.Main).launch {
                        withContext(Dispatchers.Main) {
                            SdcardTreeUriGetDialog.get(
                                activity,
                                "Sd card dir",
                                getSdcardDirLauncher,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setSdcardDirGetter(
        activity: MainActivity
    ): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            if (
                result?.resultCode != AppCompatActivity.RESULT_OK
            ) return@registerForActivityResult
            // ファイルアクセスのためのオブジェクトを取得する
            val treeUri = result.data?.data
                ?: return@registerForActivityResult
            activity.contentResolver.takePersistableUriPermission(
                treeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            val sdCardRootDirPath = getSdCardRootDirPath(activity)
            sdCardRootDirPath?.let {
                val isSdcardDirPath = DocumentFile.fromTreeUri(activity, treeUri)?.getAbsolutePath(activity)
                    ?.startsWith(it) == true
                if(isSdcardDirPath) return@let

                SdcardTreeUriGetDialog.get(
                    activity,
                    "[Re] Sd card dir",
                    getSdcardDirLauncher,
                )
                return@registerForActivityResult
            }
            val sharePref = SdCardTool.getSharePref(activity)
            SdCardTool.putTreeUri(
                activity,
                sharePref,
                treeUri
            )
            UbuntuServiceManager.launch(
                activity
            )
        }
    }


    private object SdcardTreeUriGetDialog{
        private var getSdcardTreeUriConfirmDialog: Dialog? = null

        fun get(
            activity: MainActivity,
            title: String,
            getSdcardDirLauncher: ActivityResultLauncher<Intent>,
        ){
            getSdcardTreeUriConfirmDialog = Dialog(
                activity
            )
            getSdcardTreeUriConfirmDialog?.setContentView(
                R.layout.confirm_text_gif_dialog
            )
//            val confirmTitleTextView =
//                getSdcardTreeUriConfirmDialog?.findViewById<AppCompatTextView>(
//                    com.puutaro.commandclick.R.id.confirm_text_dialog_title
//                )
//            confirmTitleTextView?.text = title
            getSdcardTreeUriConfirmDialog?.findViewById<AppCompatImageView>(
                R.id.confirm_text_gif_dialog_bk_image
            )?.let {
                val foldaGifByteArray = AssetsFileManager.assetsByteArray(
                    activity,
                    AssetsFileManager.foldaGifPath
                )
                val requestBuilder: RequestBuilder<Drawable> =
                    Glide.with(activity)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                Glide
                    .with(it.context)
                    .load(foldaGifByteArray)
                    .placeholder(R.mipmap.ic_cmdclick_launcher)
                    .thumbnail(requestBuilder)
                    .centerCrop()
                    .into(it)
            }
            val confirmContentTextView =
                getSdcardTreeUriConfirmDialog?.findViewById<OutlineTextView>(
                    R.id.confirm_text_gif_dialog_text_view
                )
            confirmContentTextView?.outlineWidthSrc = 5
            confirmContentTextView?.text =
                "\n".repeat(4) + "Specify use dir in sd card, ok?"
            val confirmCancelButton =
                getSdcardTreeUriConfirmDialog?.findViewById<AppCompatImageButton>(
                    R.id.confirm_text_gif_dialog_cancel
                )
            confirmCancelButton?.setOnClickListener {
                getSdcardTreeUriConfirmDialog?.dismiss()
                getSdcardTreeUriConfirmDialog = null
            }
            val confirmOkButton =
                getSdcardTreeUriConfirmDialog?.findViewById<AppCompatImageButton>(
                    R.id.confirm_text_gif_dialog_ok
                )
            confirmOkButton?.setOnClickListener {
                getSdcardTreeUriConfirmDialog?.dismiss()
                getSdcardTreeUriConfirmDialog = null
                execGet(
                    activity,
                    getSdcardDirLauncher,
                )
            }
            getSdcardTreeUriConfirmDialog?.setOnCancelListener {
                getSdcardTreeUriConfirmDialog?.dismiss()
                getSdcardTreeUriConfirmDialog = null
            }
            getSdcardTreeUriConfirmDialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            getSdcardTreeUriConfirmDialog?.window?.setGravity(
                Gravity.CENTER
            )
            getSdcardTreeUriConfirmDialog?.show()
        }

        private fun execGet(
            activity: MainActivity,
            getSdcardDirLauncher: ActivityResultLauncher<Intent>,
        ){
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    val sdPermitIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    val initialPath = getSdCardRootDirPath(activity)
                        ?: return@withContext
                    val initialFolderUri =
                        DocumentFile.fromFile(File(initialPath)).uri
                    sdPermitIntent.putExtra(
                        DocumentsContract.EXTRA_INITIAL_URI,
                        initialFolderUri
                    )
                    getSdcardDirLauncher.launch(
                        sdPermitIntent
                    )
                }
            }
        }
    }
}

private fun getSdCardRootDirPath(context: Context): String? {
    // getExternalFilesDirsはAndroid4.4から利用できるAPI。
    // filesディレクトリのリストを取得できる。
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
    }?.absolutePath?.split("/")?.filterIndexed {
            index, _ ->
        index < 3
    }?.joinToString("/")
}