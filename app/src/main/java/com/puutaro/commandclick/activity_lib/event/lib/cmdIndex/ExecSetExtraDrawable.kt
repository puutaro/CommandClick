package com.puutaro.commandclick.activity_lib.event.lib.cmdIndex

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment_lib.command_index_fragment.ExtraMenuGifCreator
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import java.io.File

object ExecSetExtraDrawable {
    fun set(activity: MainActivity){
        val targetFragmentInstance = TargetFragmentInstance()
        val fg1GifDirPath = ExtraMenuGifCreator.fg1GifDirPath
//        val rndList = (4..9)
        val bitmapList = FileSystems.sortedFiles(fg1GifDirPath).map {
            val pngFilePath = File(fg1GifDirPath, it).absolutePath
            BitmapTool.convertFileToBitmap(
                pngFilePath
            )
        }
//        val extraMapDrawableList = (1..4).map {
////            val animation = AnimationDrawable()
//            (1..3).map {
//                bitmapList.random()
////                animation.addFrame(
////                    BitmapDrawable(activity.resources, bitmap),
////                    rndList.random() * 100
////                )
//            }
////            animation.isOneShot = false
////            animation
//        }
//        val cmdIndexFragment = targetFragmentInstance.getCmdIndexFragment(activity)
//        cmdIndexFragment?.extraMapBitmapList = bitmapList
//        val terminalFragment = targetFragmentInstance.getCurrentTerminalFragment(activity)
//        terminalFragment?.extraMapBitmapList = bitmapList


    }
}