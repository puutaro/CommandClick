package com.puutaro.commandclick.proccess.edit_list.config_settings

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.edit_list.EditFrameMaker
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object BkImageSettingsForEditList {

    private val tagKey = EditComponent.Template.EditComponentKey.TAG.key

//    private val imagePathsKey = EditComponent.Template.ImageManager.ImageKey.PATHS.key
//    private val imageDelayKey = EditComponent.Template.ImageManager.ImageKey.DELAY.key
//    private val imageTagKey = EditComponent.Template.ImagePropertyManager.PropertyKey.TAG.key
//    private val imageAlphaKey = EditComponent.Template.ImagePropertyManager.PropertyKey.ALPHA.key
//    private val imageScaleKey = EditComponent.Template.ImagePropertyManager.PropertyKey.SCALE.key


////    private val labelTagKey = EditComponent.Template.EditComponentKey.LABEL_TAG.key
//    private val imageTagKey = EditComponent.Template.EditComponentKey.IMAGE_TAG.key
////    private val labelKey = EditComponent.Template.EditComponentKey.LABEL.key
//    private val imagePATHPathKey = EditComponent.Template.EditComponentKey.IMAGE_PATH.key
////    private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key
////    private val textSizeKey = EditComponent.Template.EditComponentKey.TEXT_SIZE.key
////    private val textColorKey = EditComponent.Template.EditComponentKey.TEXT_COLOR.key
////    private val strokeColorKey = EditComponent.Template.EditComponentKey.STROKE_COLOR.key
////    private val strokeWidthKey = EditComponent.Template.EditComponentKey.STROKE_WIDTH.key
////    private val textAlphaKey = EditComponent.Template.EditComponentKey.TEXT_ALPHA.key
//    private val imageAlphaKey = EditComponent.Template.EditComponentKey.IMAGE_ALPHA.key
//    private val imageScaleKey = EditComponent.Template.EditComponentKey.IMAGE_SCALE.key

    suspend fun makeBkFrame(
        context: Context?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        editListBkPairs: List<Pair<String, String>>?,
    ): FrameLayout? {
        val tag =  withContext(Dispatchers.IO) {
            PairListTool.getValue(
                editListBkPairs,
                tagKey
            )
        }
        return EditFrameMaker.make(
            context,
            fannelInfoMap,
            setReplaceVariableMap,
            busyboxExecutor,
            editListBkPairs,
            ViewGroup.LayoutParams.MATCH_PARENT,
            null,
            tag,
//            true,
        null
        )
    }

//    suspend fun makeBkImage(
//        imageView: AppCompatImageView,
//        editListBkPairs: List<Pair<String, String>>,
//    ) {
//        val imageTag =  withContext(Dispatchers.IO) {
//            PairListTool.getValue(
//                editListBkPairs,
//                imageTagKey
//            )
//        }
//        val imagePath =  withContext(Dispatchers.IO) {
//            PairListTool.getValue(
//                editListBkPairs,
//                imagePATHPathKey
//            )
//        }
//        val imageAlpha =  withContext(Dispatchers.IO) {
//            PairListTool.getValue(
//                editListBkPairs,
//                imageAlphaKey
//            )?.let {
//                try {
//                    it.toFloat()
//                } catch(e: Exception){
//                    null
//                }
//            }
//        }
//        val imageScale =  withContext(Dispatchers.IO) {
//            PairListTool.getValue(
//                editListBkPairs,
//                imageScaleKey
//            ).let {
//                    scale ->
//                EditComponent.Template.ImageScale.values().firstOrNull {
//                    it.str == scale
//                } ?: EditComponent.Template.ImageScale.FIT_CENTER
//            }
//        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lImage.txt").absolutePath,
//            listOf(
//                "imageTag: ${imageTag}",
//                "imagePath: ${imagePath}",
//                "imageAlpha: ${imageAlpha}",
//                "imageScale: ${imageScale}",
//            ).joinToString("\n")
//        )
//        EditFrameMaker.setImageView(
//            imageView,
//            imageTag,
//            imagePath,
//            imageAlpha,
//            imageScale
//        )
//    }
}