package com.puutaro.commandclick.proccess.edit_list

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.util.image_tools.ColorTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TextViewTool {

    private val onUpdateKey = EditComponent.Template.TextManager.TextKey.ON_UPDATE.key
    private val textSizeKey = EditComponent.Template.TextManager.PropertyKey.SIZE.key
    private val textBkColorKey = EditComponent.Template.TextManager.PropertyKey.BK_COLOR.key
    private val textStyleKey = EditComponent.Template.TextManager.PropertyKey.STYLE.key
    private val textFontKey = EditComponent.Template.TextManager.PropertyKey.FONT.key
    //    private val textTagKey = EditComponent.Template.TextManager.Property.TAG.key
    private val textLayoutGravityKey =
        EditComponent.Template.TextManager.PropertyKey.LAYOUT_GRAVITY.key
    private val textGravityKey = EditComponent.Template.TextManager.PropertyKey.GRAVITI.key
    private val textWidthKey = EditComponent.Template.TextManager.PropertyKey.WIDTH.key
    private val textHeightKey = EditComponent.Template.TextManager.PropertyKey.HEIGHT.key
    private val textColorKey = EditComponent.Template.TextManager.PropertyKey.COLOR.key
    private val textVisibleKey = EditComponent.Template.TextManager.PropertyKey.VISIBLE.key
    private val strokeColorKey = EditComponent.Template.TextManager.PropertyKey.STROKE_COLOR.key
    private val strokeWidthKey = EditComponent.Template.TextManager.PropertyKey.STROKE_WIDTH.key
    private val textAlphaKey = EditComponent.Template.TextManager.PropertyKey.ALPHA.key
    private val textMaxLinesKey = EditComponent.Template.TextManager.PropertyKey.MAX_LINES.key
    private val paddingKey = EditComponent.Template.EditComponentKey.PADDING.key
    private val textPaddingTopKey = EditComponent.Template.TextManager.PropertyKey.PADDING_TOP.key
    private val textPaddingStartKey = EditComponent.Template.TextManager.PropertyKey.PADDING_START.key
    private val textPaddingEndKey = EditComponent.Template.TextManager.PropertyKey.PADDING_END.key
    private val textPaddingBottomKey = EditComponent.Template.TextManager.PropertyKey.PADDING_BOTTOM.key
    private val textShadowRadiusKey = EditComponent.Template.TextManager.PropertyKey.SHADOW_RADIUS.key
    private val textShadowColorKey = EditComponent.Template.TextManager.PropertyKey.SHADOW_COLOR.key
    private val textShadowXKey = EditComponent.Template.TextManager.PropertyKey.SHADOW_X.key
    private val textShadowYKey = EditComponent.Template.TextManager.PropertyKey.SHADOW_Y.key
    private val letterSpacingKey = EditComponent.Template.TextManager.PropertyKey.LETTER_SPACING.key
    private val elevationKey = EditComponent.Template.EditComponentKey.ELEVATION.key

    suspend fun setVisibility(
        textView: OutlineTextView,
        textMap: Map<String, String>?,
    ){
        textView.apply {
            textMap?.get(
                textVisibleKey,
            )?.let { visibleStr ->
                withContext(Dispatchers.Main) {
                    visibility = EditComponent.Template.VisibleManager.getVisible(
                        visibleStr
                    )
                }
            }
        }
    }
    suspend fun set(
        textView: OutlineTextView,
        textMap: Map<String, String>?,
        settingValue: String?,
        overrideText: String?,
        defaultGravity: Int?,
        defaultMaxLen: Int?,
        defaultTextColorResId: Int?,
        defaultStrokeColorResId: Int?,
        defaultStrokeWidth: Int?,
        defaultTextSize: Float?,
        defaultLetterSpacing: Float?,
        defaultTextStyle: EditComponent.Template.TextManager.TextStyle?,
        defaultFont: EditComponent.Font?,
        enableTextViewClick: Boolean?,
        outValue: TypedValue?,
        where: String,
        density: Float,
    ){
        if(
            textMap.isNullOrEmpty()
        ) return
        textView.apply {
            withContext(Dispatchers.IO) {
                textMap.get(
                    textGravityKey,
                )?.let { gravityStr ->
                    EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                        it.key == gravityStr
                    }?.gravity ?: defaultGravity
                }
            }?.let {
                withContext(Dispatchers.Main) {
                    gravity = it
                }
            }
            withContext(Dispatchers.IO) {
                textMap.get(
                    elevationKey,
                )?.let {
                    try {
                        it.toFloat()
                    }catch (e: Exception){
                        null
                    }
                }
            }?.let {
                withContext(Dispatchers.Main) {
                    elevation = it
                }
            }
            val paddingData = withContext(Dispatchers.IO) {
                val padding = textMap.get(
                    paddingKey
                )
                EditComponent.Template.PaddingData(
                    textMap.get(
                        textPaddingTopKey,
                    ) ?: padding,
                    textMap.get(
                        textPaddingBottomKey,
                    ) ?: padding,
                    textMap.get(
                        textPaddingStartKey,
                    ) ?: padding,
                    textMap.get(
                        textPaddingEndKey,
                    ) ?: padding,
                    density,
                )
            }
            withContext(Dispatchers.Main) {
                setPadding(
                    paddingData.paddingStart ?: paddingStart,
                    paddingData.paddingTop ?: paddingTop,
                    paddingData.paddingEnd ?: paddingEnd,
                    paddingData.paddingBottom ?: paddingBottom,
                )
            }
//                val settingValue = withContext(Dispatchers.IO) {
//                    textMap.get(
//                        EditComponent.Template.TextManager.TextKey.SETTING_VALUE.key
//                    )
//                }
            settingValue?.let {
                withContext(Dispatchers.Main) {
                    setAutofillHints(it)
                }
            }
//                withContext(Dispatchers.IO) {
//                    EditComponent.Template.TextManager.makeText(
//                        fannelInfoMap,
//                        setReplaceVariableMap,
//                        busyboxExecutor,
//                        textMap,
//                        settingValue
//                    )
//                }
            overrideText?.let {
                withContext(Dispatchers.Main) {
                    text = it
                }
            }
            withContext(Dispatchers.IO) {
                textMap.get(
                    textMaxLinesKey
                )?.let {
                    try {
                        it.toInt()
                    } catch (e: Exception) {
                        null
                    }
                } ?: defaultMaxLen
            }?.let {
                withContext(Dispatchers.Main) {
                    maxLines = it
                }
            }
            withContext(Dispatchers.IO) {
                textMap.get(
                    textColorKey,
                )?.let { colorStr ->
                    val parsedColorStr = ColorTool.parseColorStr(
                        context,
                        colorStr,
                        textColorKey,
                        where,
                    )
                    Color.parseColor(parsedColorStr)
                } ?: defaultTextColorResId
            }?.let {
                withContext(Dispatchers.Main) {
                    setFillColor(it)
                }
            }
            val textBkColor = withContext(Dispatchers.IO) {
                textMap.get(
                    textBkColorKey,
                )?.let { colorStr ->
                    val parsedColorStr = ColorTool.parseColorStr(
                        context,
                        colorStr,
                        textBkColorKey,
                        where,
                    )
                    Color.parseColor(parsedColorStr)
                }
            }
            enableTextViewClick?.let {
                withContext(Dispatchers.Main) {
                    isClickable = it
                }
            }
            withContext(Dispatchers.Main) {
                when (enableTextViewClick) {
                    true -> outValue?.let {
                        setBackgroundResource(it.resourceId)
                    }

                    else -> {
                        background =
                            textBkColor?.let {
                                setBackgroundResource(0)
                                ColorDrawable(it)
                            }
                    }
                }
            }
            val strokeColorStr = withContext(Dispatchers.IO) {
                textMap.get(
                    strokeColorKey,
                )
            }
            let {
                CmdClickColor.entries.firstOrNull {
                    it.str == strokeColorStr
                }?.id ?: defaultStrokeColorResId
            }?.let {
                withContext(Dispatchers.Main) {
                    setStrokeColor(it)
                }
            }
            withContext(Dispatchers.IO) {
                textMap.get(
                    strokeWidthKey,
                )?.let {
                    try {
                        it.toInt()
                    } catch (e: Exception) {
                        null
                    }
                } ?: defaultStrokeWidth
            }?.let {
                withContext(Dispatchers.Main) {
                    strokeWidthSrc = it
                }
            }
            val overrideTextSize = withContext(Dispatchers.IO) {
                textMap.get(
                    textSizeKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            withContext(Dispatchers.Main) {
                overrideTextSize?.let {
                    textSize = it
                } ?: let {
                    if (defaultTextSize == null) return@let
                    setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        defaultTextSize,
                    )
                }
            }
            withContext(Dispatchers.IO) {
                textMap.get(
                    letterSpacingKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch (e: Exception) {
                        null
                    }
                } ?: defaultLetterSpacing
            }?.let {
                withContext(Dispatchers.Main) {
                    letterSpacing = it
                }
            }
            withContext(Dispatchers.IO) {
                textMap.get(
                    textAlphaKey,
                )?.let {
                    try {
                        it.toFloat()
                    } catch (e: Exception) {
                        null
                    }
                }
            }?.let {
                withContext(Dispatchers.Main) {
                    alpha = it
                }
            }
            val textShadowXFloat = withContext(Dispatchers.IO) {
                textMap.get(
                    textShadowXKey,
                )?.let {
                    try {
                        ScreenSizeCalculator.toDpByDensity(
                            it.toFloat(),
                            density
                        ).toFloat()
                    } catch (e: Exception) {
                        null
                    }
                } ?: shadowDx
            }
            val textShadowYFloat = withContext(Dispatchers.IO) {
                textMap.get(
                    textShadowYKey,
                )?.let {
                    try {
                        ScreenSizeCalculator.toDpByDensity(
                            it.toFloat(),
                            density
                        ).toFloat()
                    } catch (e: Exception) {
                        null
                    }
                } ?: shadowDy
            }
            val textShadowRadiusFloat = withContext(Dispatchers.IO) {
                textMap.get(
                    textShadowRadiusKey,
                )?.let {
                    try {
                        ScreenSizeCalculator.toDpByDensity(
                            it.toFloat(),
                            density
                        ).toFloat()
                    } catch (e: Exception) {
                        null
                    }
                } ?: shadowRadius
            }
            val textShadowColor = withContext(Dispatchers.IO) {
                textMap.get(
                    textShadowColorKey,
                )?.let {
                    val colorStr = ColorTool.parseColorStr(
                        context,
                        it,
                        textShadowColorKey,
                        where
                    )
                    Color.parseColor(colorStr)
                } ?: shadowColor
//                        ?: Color.TRANSPARENT
            }
            withContext(Dispatchers.Main) {
                setShadowLayer(
                    textShadowRadiusFloat,
                    textShadowXFloat,
                    textShadowYFloat,
                    textShadowColor
                )
            }
            val overrideTextStyle = withContext(Dispatchers.IO) {
                textMap.get(
                    textStyleKey,
                )?.let { textStyleStr ->
                    EditComponent.Template.TextManager.TextStyle.entries.firstOrNull {
                        it.key == textStyleStr
                    }
                } ?: defaultTextStyle
            }
            val overrideFont = withContext(Dispatchers.IO) {
                textMap.get(
                    textFontKey,
                )?.let { textFontStr ->
                    EditComponent.Font.entries.firstOrNull {
                        it.key == textFontStr
                    }
                } ?: defaultFont
            }
            let {
                if (
                    overrideTextStyle == null
                    || overrideFont == null
                ) return@let
                withContext(Dispatchers.Main) {
                    setTypeface(overrideFont.typeface, overrideTextStyle.style)
                }
            }
        }
    }
}