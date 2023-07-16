package com.puutaro.commandclick.util.Intent

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import java.io.File

object IntentVarient {
    fun sharePngImage(
        file: File,
        activity: FragmentActivity?
    ){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/png"
        val photoFile: File = file
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile))
        activity?.startActivity(Intent.createChooser(shareIntent, "Share image using"))
    }
}