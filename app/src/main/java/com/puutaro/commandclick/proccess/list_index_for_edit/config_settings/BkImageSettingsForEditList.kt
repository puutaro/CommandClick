package com.puutaro.commandclick.proccess.list_index_for_edit.config_settings

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.list_index_for_edit.EditFrameMaker
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object BkImageSettingsForEditList {
    private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
//    private val labelTagKey = EditComponent.Template.EditComponentKey.LABEL_TAG.key
    private val imageTagKey = EditComponent.Template.EditComponentKey.IMAGE_TAG.key
//    private val labelKey = EditComponent.Template.EditComponentKey.LABEL.key
    private val imagePathKey = EditComponent.Template.EditComponentKey.IMAGE_PATH.key
//    private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key
//    private val textSizeKey = EditComponent.Template.EditComponentKey.TEXT_SIZE.key
//    private val textColorKey = EditComponent.Template.EditComponentKey.TEXT_COLOR.key
//    private val strokeColorKey = EditComponent.Template.EditComponentKey.STROKE_COLOR.key
//    private val strokeWidthKey = EditComponent.Template.EditComponentKey.STROKE_WIDTH.key
//    private val textAlphaKey = EditComponent.Template.EditComponentKey.TEXT_ALPHA.key
    private val imageAlphaKey = EditComponent.Template.EditComponentKey.IMAGE_ALPHA.key
    private val imageScaleKey = EditComponent.Template.EditComponentKey.IMAGE_SCALE.key

    suspend fun makeBkFrame(
        context: Context?,
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
            editListBkPairs,
            ViewGroup.LayoutParams.MATCH_PARENT,
            null,
            tag,
            true
        )
    }

    suspend fun makeBkImage(
        imageView: AppCompatImageView,
        editListBkPairs: List<Pair<String, String>>,
    ) {
        val imageTag =  withContext(Dispatchers.IO) {
            PairListTool.getValue(
                editListBkPairs,
                imageTagKey
            )
        }
        val imagePath =  withContext(Dispatchers.IO) {
            PairListTool.getValue(
                editListBkPairs,
                imagePathKey
            )
        }
        val imageAlpha =  withContext(Dispatchers.IO) {
            PairListTool.getValue(
                editListBkPairs,
                imageAlphaKey
            )?.let {
                try {
                    it.toFloat()
                } catch(e: Exception){
                    null
                }
            }
        }
        val imageScale =  withContext(Dispatchers.IO) {
            PairListTool.getValue(
                editListBkPairs,
                imageScaleKey
            ).let {
                    scale ->
                EditComponent.Template.ImageScale.values().firstOrNull {
                    it.str == scale
                } ?: EditComponent.Template.ImageScale.FIT_CENTER
            }
        }
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "lImage.txt").absolutePath,
            listOf(
                "imageTag: ${imageTag}",
                "imagePath: ${imagePath}",
                "imageAlpha: ${imageAlpha}",
                "imageScale: ${imageScale}",
            ).joinToString("\n")
        )
        EditFrameMaker.setImageView(
            imageView,
            imageTag,
            imagePath,
            imageAlpha,
            imageScale
        )
    }
}