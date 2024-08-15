package com.puutaro.commandclick.activity_lib.event.lib.app_history

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.permission.NotifierSetter
import com.puutaro.commandclick.activity_lib.permission.StorageAccessSetter
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryPath
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object AppHistoryCapture {

    private var beforeHash = String()
//    private var beforeTime = LocalDateTime.parse("2020-02-15T21:30:50")
    
    fun watch(
        activity: MainActivity,
        view: View?,
        startUpPref: FannelInfoTool.FannelInfoSharePref
    ){
        activity.lifecycleScope.launch(Dispatchers.IO) {
            activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.IO) {
                    while (true) {
                        val isGranted =
                            StorageAccessSetter.checkPermissionGranted(activity)
                                    && NotifierSetter.checkPermission(activity)
                        if (
                            isGranted
                        ) break
                        delay(1000)
                    }
                }
                withContext(Dispatchers.IO) {
                    delay(5_000)
                    while (true) {
                        if (
                            view == null
                        ) break
                        val temporalBitmap = withContext(Dispatchers.Main){
                            BitmapTool.getLowScreenShotFromView(view)
                        } ?: continue
//                            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
//
//                        // Above Android O, use PixelCopy due
//                        // https://stackoverflow.com/questions/58314397/
//                        val window: Window = withContext(Dispatchers.Main) {
//                            when(view.isHardwareAccelerated) {
//                                true -> (view.context as MainActivity).window
//                                else -> null
//                            }
//                        } ?: continue
//
//                        val location = IntArray(2)
//
//                        view.getLocationInWindow(location)
//
//                        val viewRectangle =
//                            Rect(
//                                location[0],
//                                location[1],
//                                location[0] + view.width,
//                                location[1] + view.height
//                            )
//
//                        val onPixelCopyListener: PixelCopy.OnPixelCopyFinishedListener =
//                            PixelCopy.OnPixelCopyFinishedListener { copyResult ->
//                                if (copyResult != PixelCopy.SUCCESS) {
//                                    return@OnPixelCopyFinishedListener
//                                }
//                                execGetCapture(
//                                    startUpPref,
//                                    temporalBitmap
//                                )
//                            }
//
//                        PixelCopy.request(
//                            window,
//                            viewRectangle,
//                            temporalBitmap,
//                            onPixelCopyListener,
//                            Handler(Looper.getMainLooper())
//                        )
                    }
                }
            }
        }
    }

    private fun execGetCapture(
        startUpPref:  FannelInfoTool.FannelInfoSharePref,
        capture: Bitmap
    ) {
//            val currentDateTime = LocalDateTime.now()
//            if (
//                LocalDatetimeTool.getDurationSec(
//                    beforeTime,
//                    currentDateTime
//                ) < 3
//            ) return@launch
//            beforeTime = currentDateTime
            val curHash = BitmapTool.hash(capture)
            if (
                curHash == beforeHash
            ) return
            beforeHash = curHash
            val byteArray =
//                withContext(Dispatchers.IO) {
//                val smallBitmap =
                    BitmapTool.resizeByMaxHeight(capture, 700.0).let {
                        smallBitmap ->
                        BitmapTool.convertBitmapToByteArray(
                            smallBitmap
                        )
                    }
//            }
            val fannelInfoMap =
//                withContext(Dispatchers.IO) {
                FannelInfoTool.makeFannelInfoMapByShare(startUpPref)
//            }
            val currentAppDirPath =
//                withContext(Dispatchers.IO) {
                FannelInfoTool.getCurrentAppDirPath(fannelInfoMap)
//            }
            val currentFannelName =
//                withContext(Dispatchers.IO) {
                FannelInfoTool.getCurrentFannelName(fannelInfoMap).let {
                    val isEmpty = it.isEmpty()
                            || it == FannelInfoSetting.current_fannel_name.defalutStr
                    when(isEmpty){
                        true -> null
                        else -> it
                    }
                }
//            }
            val capturePartsPngDirPath =
//                withContext(Dispatchers.IO) {
                FannelHistoryPath.getCapturePartsPngDirPath(
                    currentAppDirPath,
                    currentFannelName,
                )
//            }
//            withContext(Dispatchers.IO) {
                execTrimFiles(capturePartsPngDirPath)
//            }
//            withContext(Dispatchers.IO) {
                FileSystems.writeFromByteArray(
                    File(capturePartsPngDirPath, "${curHash}.png").absolutePath,
                    byteArray
                )
//            }
            val captureGifPath =
//                withContext(Dispatchers.IO) {
                FannelHistoryPath.getCaptureGifPath(
                    currentAppDirPath,
                    currentFannelName,
                )
//            }
            if (
                !File(captureGifPath).isFile
            ) {
                FileSystems.writeFromByteArray(
                    captureGifPath,
                    byteArray,
                )
            }
        }

    private fun execTrimFiles(
        captureSaveDir: String
    ){
        val lastModifiedCapturePngPathList =
            FileSystems.sortedFiles(
                captureSaveDir,
                "on"
            )
        val totalNum = lastModifiedCapturePngPathList.size
        val limitNum = 2
        val removeNum = totalNum - limitNum
        if(removeNum <= 0) return
        lastModifiedCapturePngPathList.takeLast(removeNum).forEach {
            FileSystems.removeFiles(
                File(captureSaveDir, it).absolutePath
            )
        }
    }
}