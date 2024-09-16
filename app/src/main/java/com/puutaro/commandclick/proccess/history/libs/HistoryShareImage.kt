package com.puutaro.commandclick.proccess.history.libs

import android.content.Context
import android.graphics.Bitmap
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object HistoryShareImage {

    suspend fun makePngImageFromView(
        context: Context?,
        urlHistoryAdapterConstraintLayout: ConstraintLayout
    ): File? {
        withContext(Dispatchers.IO) {
            FileSystems.removeAndCreateDir(
                UsePath.cmdclickTempCreateDirPath
            )
        }
        val bitmap = withContext(Dispatchers.Main) {
            for(i in 1..10) {
                try {
                    val bm = BitmapTool.getScreenShotFromView(
                        urlHistoryAdapterConstraintLayout
                    )
                    return@withContext bm
                } catch (e: Exception) {
                    delay(100)
                    continue
                }
            }
            LogSystems.stdErr(
                context,
                "Cannot save screen shot"
            )
            null
        } ?: return null
        val imageName = withContext(Dispatchers.IO) {
            BitmapTool.hash(
                bitmap
            ) + ".png"
        }
        val file = File(
            UsePath.cmdclickTempCreateDirPath,
            imageName
        )
        withContext(Dispatchers.IO) {
            FileOutputStream(file).use { stream ->
                bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    stream
                )
            }
        }
        return file
    }

}