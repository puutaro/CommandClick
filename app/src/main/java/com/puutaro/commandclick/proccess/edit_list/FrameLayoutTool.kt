package com.puutaro.commandclick.proccess.edit_list

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.FrameLayout
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.ColorTool
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object FrameLayoutTool {
    
    private val layoutGravity = EditComponent.Template.EditComponentKey.LAYOUT_GRAVITY.key
    private val widthKey = EditComponent.Template.EditComponentKey.WIDTH.key
    private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key
    private val paddingTopKey = EditComponent.Template.EditComponentKey.PADDING_TOP.key
    private val paddingBottomKey = EditComponent.Template.EditComponentKey.PADDING_BOTTOM.key
    private val paddingStartKey = EditComponent.Template.EditComponentKey.PADDING_START.key
    private val paddingEndKey = EditComponent.Template.EditComponentKey.PADDING_END.key
    private val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
    private val visibleKey = EditComponent.Template.EditComponentKey.VISIBLE.key
    private val bkColorKey = EditComponent.Template.EditComponentKey.BK_COLOR.key
    private val layoutGravityKey = EditComponent.Template.EditComponentKey.LAYOUT_GRAVITY.key
    private val gravityKey = EditComponent.Template.EditComponentKey.GRAVITI.key
    private val elevationKey = EditComponent.Template.EditComponentKey.ELEVATION.key
    private val alphaKey = EditComponent.Template.EditComponentKey.ALPHA.key
//
//    suspend fun setLayout(
//        frameLayout: FrameLayout,
//        frameMap: Map<String, String>,
//        density: Float,
//        whereForErr: String,
//    ){
//        frameLayout.apply {
//            val overrideGravity = withContext(Dispatchers.IO) {
//                frameMap.get(gravityKey)?.let { gravityStr ->
//                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
//                        it.key == gravityStr
//                    }?.gravity
//                }
//            } ?: Gravity.CENTER
//            withContext(Dispatchers.Main) {
//                foregroundGravity = overrideGravity
//            }
//            val paddingData = withContext(Dispatchers.IO) {
//                EditComponent.Template.PaddingData(
//                    frameMap.get(paddingTopKey),
//                    frameMap.get(paddingBottomKey),
//                    frameMap.get(paddingStartKey),
//                    frameMap.get(paddingEndKey),
//                    density,
//                )
//            }
//            withContext(Dispatchers.Main) {
//                setPadding(
//                    paddingData.paddingStart ?: 0,
//                    paddingData.paddingTop ?: 0,
//                    paddingData.paddingEnd ?: 0,
//                    paddingData.paddingBottom ?: 0,
//                )
//            }
//            val bkColorStr = withContext(Dispatchers.IO) {
//                frameMap.get(bkColorKey)
//            }
//            val bkColorDrawable = withContext(Dispatchers.IO) {
//                if (
//                    bkColorStr.isNullOrEmpty()
//                ) return@withContext null
//                ColorTool.parseColorStr(
//                    context,
//                    bkColorStr,
//                    bkColorKey,
//                    whereForErr,
//                ).let {
//                    ColorDrawable(Color.parseColor(it))
//                }
//            }
//
////            bkColorStr?.let {
////                    ColorStateList.valueOf(Color.parseColor(it))
////                }
//            withContext(Dispatchers.Main) {
//                background = bkColorDrawable
//            }
//            val elevationFloat = withContext(Dispatchers.IO) {
//                frameMap.get(elevationKey)?.let {
//                    try {
//                        it.toFloat()
//                    } catch (e: Exception) {
//                        null
//                    }
//                }
//            } ?: elevation
//            withContext(Dispatchers.Main) {
//                elevation = elevationFloat
//            }
//            val alphaFloat = withContext(Dispatchers.IO) {
//                frameMap.get(alphaKey)?.let {
//                    try {
//                        it.toFloat()
//                    } catch (e: Exception) {
//                        null
//                    }
//                }
//            } ?: alpha
//            withContext(Dispatchers.Main) {
//                alpha = alphaFloat
//            }
//        }
//    }
    
    suspend fun setParam(
        layoutParam: FrameLayout.LayoutParams,
        paramMap: Map<String, String>?,
        density: Float,
        defaultWidth: Int,
        defaultHeight: Int,
    ): FrameLayout.LayoutParams {
        val overrideWidth = withContext(Dispatchers.IO) {
            paramMap?.get(
                widthKey,
            ).let {
                EditComponent.Template.LinearLayoutUpdater.convertWidth(
                    it,
                    defaultWidth,
                    density,
                )
            }
        }
        val overrideHeight = withContext(Dispatchers.IO) {
            paramMap?.get(
                heightKey,
            ).let {
                EditComponent.Template.LinearLayoutUpdater.convertHeight(
                    it,
                    defaultHeight,
                    density,
                )
            }
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath,"lparm.txt").absolutePath,
//            listOf(
//                "paramMap: ${paramMap}",
//                "defaultWidth: ${defaultWidth}",
//                "defaultHeight: ${defaultHeight}",
//                "width: ${overrideWidth}",
//                "height: ${overrideHeight}",
//            ).joinToString("\n\n") + "\n===========\n\n"
//        )
        layoutParam.apply setParam@ {
            width = overrideWidth
            height = overrideHeight
            val overrideLayoutGravity = withContext(Dispatchers.IO) {
                paramMap?.get(
                    layoutGravity,
                )?.let { gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity
                }
            } ?: Gravity.CENTER
            gravity = overrideLayoutGravity
        }
        return layoutParam
    }
}