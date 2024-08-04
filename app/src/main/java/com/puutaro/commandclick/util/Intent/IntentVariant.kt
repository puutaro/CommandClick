package com.puutaro.commandclick.util.Intent

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.util.LogSystems
import java.io.File
import java.lang.Exception


object IntentVariant {
    fun sharePngImage(
        file: File,
        context: Context?,
    ){
        if(
            context == null
        ) return
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/png"
        val photoFile: File = file
        val photoURI = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            photoFile
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI)
        try {
            context.startActivity(
                Intent.createChooser(shareIntent, "Share image using")
            )
        } catch (e: Exception){
            LogSystems.stdErr(
                context,
                e.toString()
            )
        }
    }
}