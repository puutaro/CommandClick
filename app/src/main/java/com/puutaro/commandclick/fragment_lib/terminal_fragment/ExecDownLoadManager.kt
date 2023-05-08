package com.puutaro.commandclick.fragment_lib.terminal_fragment

import android.R
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.Gravity
import android.webkit.DownloadListener
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.puutaro.commandclick.fragment.TerminalFragment
import java.net.URLDecoder


class ExecDownLoadManager {
    companion object {
        fun set(
            terminalFragment: TerminalFragment,
        ){
            val context = terminalFragment.context
            val binding = terminalFragment.binding

            binding.terminalWebView.setDownloadListener(DownloadListener {
                    url, userAgent, contentDisposition, mimetype, contentLength ->
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                terminalFragment.startActivity(i)
//                val convetedContentDispositon = makeConvetedContentDispositon(
//                    contentDisposition
//                )
//                val isDriveDownload =
//                    !(contentDisposition == convetedContentDispositon)
//                val downloadFileName = makeDownloadFile(
//                    url,
//                    convetedContentDispositon,
//                    isDriveDownload
//                )
//
//                val downlaodUrl = makeUrl(
//                    url,
//                    terminalFragment,
//                    isDriveDownload,
//                )
//
//                val request = DownloadManager.Request(
//                    Uri.parse(downlaodUrl)
//                )
//
//                val alertDialog = AlertDialog.Builder(context)
//                    .setTitle(
//                        "download ok?"
//                    )
//                    .setMessage("file: ${downloadFileName}")
//                    .setPositiveButton("OK", DialogInterface.OnClickListener {
//                            dialog, which ->
//                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//                        request.setTitle("cmdclick")
//                        request.setDescription("downloading..\n${downloadFileName}")
//                        request.setDestinationInExternalPublicDir(
//                            Environment.DIRECTORY_DOWNLOADS,
//                            downloadFileName
//                        )
//                        val dm = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
//                        dm?.enqueue(request)
//                        Toast.makeText(
//                            context,
//                            "Downloading : ${downloadFileName}\ndetail is in norificatin bar",  //To notify the Client that the file is being downloaded
//                            Toast.LENGTH_LONG
//                        ).show()
//                    })
//                    .setNegativeButton("NO", null)
//                    .show()
//                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
//                    context?.getColor(R.color.black) as Int
//                );
//                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
//                    context.getColor(R.color.black) as Int
//                )
//                alertDialog.getWindow()?.setGravity(Gravity.BOTTOM)
            })
        }
    }
}

internal fun makeConvetedContentDispositon(
    contentDisposition: String
): String {
    return URLDecoder
        .decode(
            contentDisposition
                .toString()
                .substringAfter("UTF-8''"), "utf-8"
        )
}

internal fun makeUrl(
    url: String,
    terminalFragment: TerminalFragment,
    isDriveDownload: Boolean
): String? {
    return if(
        isDriveDownload
    ) terminalFragment.currentUrl
    else url
}


internal fun makeDownloadFile(
    url: String,
    convetedContentDispositon: String,
    isDriveDownload: Boolean
): String {
    return if(isDriveDownload) {
        convetedContentDispositon
    } else url.split("/").lastOrNull()?: "DownlaodFile_${(1..1000).random()}"
}