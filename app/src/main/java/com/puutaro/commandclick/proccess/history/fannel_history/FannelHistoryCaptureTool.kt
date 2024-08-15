package com.puutaro.commandclick.proccess.history.fannel_history

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

object FannelHistoryCaptureTool {

    private var beforeHash = String()
    private var beforeTime = LocalDateTime.parse("2020-02-15T21:30:50")


    fun getCapture(
        startUpPref:  FannelInfoTool.FannelInfoSharePref,
        view: View?
    ){
        if(
            view == null
        ) return
        val temporalBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        // Above Android O, use PixelCopy due
        // https://stackoverflow.com/questions/58314397/
        val window: Window = (view.context as MainActivity).window

        val location = IntArray(2)

        view.getLocationInWindow(location)

        val viewRectangle = Rect(location[0], location[1], location[0] + view.width, location[1] + view.height)

        val onPixelCopyListener: PixelCopy.OnPixelCopyFinishedListener = PixelCopy.OnPixelCopyFinishedListener { copyResult ->

            if (copyResult == PixelCopy.SUCCESS) {

                execGetCapture(
                    startUpPref,
                    temporalBitmap
                )
            } else {

                error("Error while copying pixels, copy result: $copyResult")
            }
        }

        PixelCopy.request(window, viewRectangle, temporalBitmap, onPixelCopyListener, Handler(Looper.getMainLooper()))
    }
    private fun execGetCapture(
        startUpPref:  FannelInfoTool.FannelInfoSharePref,
        capture: Bitmap
    ){
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                delay(2000)
            }
            val currentDateTime = LocalDateTime.now()
            if(
                LocalDatetimeTool.getDurationSec(beforeTime, currentDateTime) < 3
            ) return@launch
            beforeTime = currentDateTime
            val curHash = BitmapTool.hash(capture)
            if(
                curHash == beforeHash
            ) return@launch
            beforeHash = curHash
            val byteArray = withContext(Dispatchers.IO) {
                val smallBitmap =
                    BitmapTool.resizeByMaxHeight(capture, 700.0)
                BitmapTool.convertBitmapToByteArray(
                    smallBitmap
                )
            }
            val fannelInfoMap = withContext(Dispatchers.IO){
                FannelInfoTool.makeFannelInfoMapByShare(startUpPref)
            }
            val currentAppDirPath = withContext(Dispatchers.IO){
                FannelInfoTool.getCurrentAppDirPath(fannelInfoMap)
            }
            val currentFannelName = withContext(Dispatchers.IO){
                FannelInfoTool.getCurrentFannelName(fannelInfoMap).let {
                    val isEmpty = it.isEmpty()
                            || it == FannelInfoSetting.current_fannel_name.defalutStr
                    when(isEmpty){
                        true -> null
                        else -> it
                    }
                }
            }
            val captureFacePngDirPath = withContext(Dispatchers.IO){
                FannelHistoryPath.getCaptureFacePngDirPath(
                    currentAppDirPath,
                    currentFannelName,
                )
            }
            withContext(Dispatchers.IO){
                FileSystems.removeAndCreateDir(captureFacePngDirPath)
                FileSystems.writeFromByteArray(
                    File(captureFacePngDirPath, "${curHash}.png").absolutePath,
                    byteArray
                )
            }
            val captureGifPath = withContext(Dispatchers.IO){
                FannelHistoryPath.getCaptureGifPath(
                    currentAppDirPath,
                    currentFannelName,
                )
            }
            if(
                !File(captureGifPath).isFile
            ){
                FileSystems.writeFromByteArray(
                    captureGifPath,
                    byteArray,
                )
            }
        }
    }
}