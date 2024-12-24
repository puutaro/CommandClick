package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.libs.FilterAndMapModule
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList.LogErrLabel
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.ColorTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.PairListTool
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object EditComponent {

        enum class Font(
                val key: String,
                val typeface: Typeface,
        ){
                DEFAULT("monospace", Typeface.DEFAULT),
                MONOSPACE("monospace", Typeface.MONOSPACE),
                SANS_SERIF("sansSerif", Typeface.SANS_SERIF),
                SERIF("serif", Typeface.SERIF),
        }

        object Template {

                const val sectionSeparator = ','
                const val typeSeparator = '|'
                const val keySeparator = '?'
                const val valueSeparator = '&'

                const val switchOn = "ON"
                const val switchOff = "OFF"

                object ReplaceHolder {
                        fun replaceHolder(
                                con: String?,
                                srcTitle: String,
                                srcCon: String,
                                srcImage: String,
                                srcPosition: Int,
                        ): String? {
                                if(con == null) return null
                                return con.replace(
                                        SrcReplaceHolders.SRC_TITLE.key,
                                        srcTitle
                                ).replace(
                                        SrcReplaceHolders.SRC_CON.key,
                                        srcCon
                                ).replace(
                                        SrcReplaceHolders.SRC_IMAGE.key,
                                        srcImage
                                ).replace(
                                        SrcReplaceHolders.EDIT_LIST_POSITION.key,
                                        srcPosition.toString()
                                )

                        }

                        enum class SrcReplaceHolders(val key: String){
                                SHELL_SRC("\${SHELL_SRC}"),
                                SRC_TITLE("\${SRC_TITLE}"),
                                SRC_CON("\${SRC_CON}"),
                                SRC_IMAGE("\${SRC_IMAGE}"),
                                EDIT_LIST_POSITION("\${EDIT_LIST_POSITION}"),
                                SRC_STR("\${SRC_STR}"),
                                SETTING_VALUE("\${SETTING_VALUE}"),
                        }
                }

                enum class LayoutKey(val key: String){
                        FRAME("frame"),
                        CONTENTS("contents")
                }

                fun makeKeyMap(
                        mapCon: String?
                ): Map<String, String>? {
                        if(
                                mapCon.isNullOrEmpty()
                        ) return null
                        return CmdClickMap.createMap(
                                mapCon,
                                keySeparator,
                        ).toMap().filter {
                                it.key.isNotEmpty()
                        }
                }

                enum class EditComponentKey(val key: String){
                        TAG("tag"),
                        TEXT("text"),
                        TEXT_PROPERTY("textProperty"),
                        IMAGE("image"),
                        IMAGE_PROPERTY("imageProperty"),
                        ON_CLICK("onClick"),
                        ON_SAVE("onSave"),
                        ON_CONSEC("onConsec"),
                        ALPHA("alpha"),
                        DISABLE_KEYBOARD_HIDDEN("disableKeyboardHidden"),
                        HEIGHT("height"),
                        WIDTH("width"),
                        PADDING_TOP("paddingTop"),
                        PADDING_BOTTOM("paddingBottom"),
                        PADDING_START("paddingStart"),
                        PADDING_END("paddingEnd"),
                        MARGIN_TOP("marginTop"),
                        MARGIN_BOTTOM("marginBottom"),
                        MARGIN_START("marginStart"),
                        MARGIN_END("marginEnd"),
                        GRAVITI("gravity"),
                        LAYOUT_GRAVITY("layoutGravity"),
                        BK_COLOR("bkColor"),
                        VISIBLE("visible"),
                        ENABLE("enable"),
                        ELEVATION("elevation"),
                        CLICK_VIEWS("clickViews"),

                        TOP_TO_TOP("topToTop"),
                        TOP_TO_BOTTOM("topToBottom"),
                        START_TO_START("startToStart"),
                        START_TO_END("startToEnd"),
                        END_TO_END("endToEnd"),
                        END_TO_START("endToStart"),
                        BOTTOM_TO_BOTTOM("bottomToBottom"),
                        BOTTOM_TO_TOP("bottomToTop"),
                        HORIZONTAL_BIAS("horizontalBias"),
                        HORIZONTAL_WEIGHT("horizontalWeight"),
                        VERTICAL_WEIGHT("verticalWeight"),
                        PERCENTAGE_WIDTH("percentageWidth"),
                        PERCENTAGE_HEIGHT("percentageHeight"),
                        DIMENSION_RATIO("dimensionRatio"),
                        HORIZONTAL_CHAIN_STYLE("horizontalChainStyle"),
                        VERTICAL_CHAIN_STYLE("verticalChainStyle"),
                }

                object ClickManager {
                        private val jsActionKeyList = JsActionKeyManager.JsActionsKey.entries.map{
                                it.key
                        }
                        fun isClickEnable(
                                frameKeyPairsList: List<Pair<String, String>>
                        ): Boolean {
                                val isJsAcForFrame =
                                        jsActionKeyList.any {
                                                !PairListTool.getValue(
                                                        frameKeyPairsList,
                                                        it,
                                                ).isNullOrEmpty()
                                        }
                                val onClickForFrame =
                                        PairListTool.getValue(
                                                frameKeyPairsList,
                                                EditComponentKey.ON_CLICK.key,
                                        ) != switchOff
                                return isJsAcForFrame
                                        && onClickForFrame
                        }
                }

                object ClickViewManager {
                        enum class ClickViews(
                                val str: String
                        ){
                                TEXT("text"),
                                IMAGE("image"),
                        }

                        fun makeClickViewList(
                                children: Sequence<View>,
                                clickViewsListStr: String?,
                        ): Sequence<View> {
                                if(
                                        clickViewsListStr.isNullOrEmpty()
                                ){
                                        return children
                                }
                                val clickViewsList =
                                        clickViewsListStr.split(valueSeparator).filter {
                                                it.isNotEmpty()
                                        }
                                val clickTextViewStr = ClickViews.TEXT.str
                                val clickImageViewStr = ClickViews.IMAGE.str
                                val isClickViewsListEmpty = clickViewsList.isEmpty()
                                return children.filter {
                                        childView ->
                                        when(true) {
                                                (childView is OutlineTextView) -> {
                                                        isClickViewsListEmpty
                                                                || clickViewsList.contains(clickTextViewStr)
                                                }
                                                (childView is AppCompatImageView) -> {
                                                        isClickViewsListEmpty
                                                                || clickViewsList.contains(clickImageViewStr)
                                                }
                                                else -> false
                                        }
                                }
                        }
                }

                object ConstraintManager {

                        enum class ConstraintParameter(
                                val str: String,
                                val int: Int,
                        ){
                                UNSET("UNSET", ConstraintLayout.LayoutParams.UNSET),
                                PARENT_ID("PARENT", ConstraintLayout.LayoutParams.PARENT_ID),
                        }

                        fun makeFloat(
                                biasFloatStr: String?
                        ): Float? {
                                if(
                                        biasFloatStr.isNullOrEmpty()
                                ) return null
                                return try {
                                        biasFloatStr.toFloat()
                                } catch (e: Exception){
                                        null
                                }
                        }

                        fun getChainStyleInt(
                                chainStyleStr: String,
                        ): Int? {
                                return ChainStyle.entries.firstOrNull {
                                        it.str == chainStyleStr
                                }?.chainStyle
                        }

                        enum class ChainStyle(
                                val str: String,
                                val chainStyle: Int,
                        ){
                                PACKED("packed", ConstraintLayout.LayoutParams.CHAIN_PACKED),
                                SPREAD("spread", ConstraintLayout.LayoutParams.CHAIN_SPREAD),
                        }
                }

                object LayoutIdMap {
                        enum class LayoutName {
                                TITLE,
                                LIST,
                                SEARCH,
                                FOOTER,
                                TOOLBAR,
                        }
                        fun makeMap(
                                titleId: Int?,
                                listId: Int?,
                                searchId: Int?,
                                footerId: Int?,
                                toolbarId: Int?,
                        ): Map<String, Int> {
                                val unsetInt =
                                        ConstraintLayout.LayoutParams.UNSET
                                return mapOf(
                                        LayoutName.TITLE.name to (titleId ?: unsetInt),
                                        LayoutName.LIST.name to (listId ?: unsetInt),
                                        LayoutName.SEARCH.name to (searchId ?: unsetInt),
                                        LayoutName.FOOTER.name to (footerId ?: unsetInt),
                                        LayoutName.TOOLBAR.name to (toolbarId ?: unsetInt),
                                )

                        }
                }

                object GravityManager {
                        enum class Graviti(
                                val key: String,
                                val gravity: Int,
                        ){
                                CENTER("center", Gravity.CENTER),
                                START("start", Gravity.START),
                                END("end", Gravity.END),
                                BOTTOM("bottom", Gravity.BOTTOM),
                                CENTER_VERTICAL("centerVertical", Gravity.CENTER_VERTICAL),
                                CENTER_HORIZONTAL("centerHorizontal", Gravity.CENTER_HORIZONTAL),
                        }
                }


                object ImageManager {
                       enum class ImageKey(val key: String) {
                               PATHS("paths"),
                               DELAY("delay"),
                               FADE_IN_MILLI("fadeInMilli"),
                               MATRIX_STORM_CONFIG_MAP_CON("matrixStormConfigMapCon"),
                               AUTO_RND_BITMAPS_CONFIG_MAP_CON("autoRndBitmapsConfigMapCon"),
                               LEFT_STRINGS_CONFIG_MAP_CON("leftStringsConfigMapCon"),
                       }

                        object MatrixStormManager {

                                private const val keySeparator = '|'
                                enum class MatrixStormKey(
                                        val key: String
                                ){
                                        WIDTH("width"),
                                        HEIGHT("height"),
                                        X_MULTI("xMulti"),
                                        Y_MULTI("yMulti"),

                                }

                                fun getWidth(
                                        matrixStormConfigMap: Map<String, String>
                                ): Int? {
                                        return matrixStormConfigMap.get(
                                                MatrixStormKey.WIDTH.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }


                                fun getHeight(
                                        matrixStormConfigMap: Map<String, String>
                                ): Int? {
                                        return matrixStormConfigMap.get(
                                                MatrixStormKey.HEIGHT.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }

                                fun getXMulti(
                                        matrixStormConfigMap: Map<String, String>
                                ): Int? {
                                        return matrixStormConfigMap.get(
                                                MatrixStormKey.X_MULTI.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }

                                fun getYMulti(
                                        matrixStormConfigMap: Map<String, String>
                                ): Int? {
                                        return matrixStormConfigMap.get(
                                                MatrixStormKey.Y_MULTI.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }

                                private fun toInt(
                                        numString: String?
                                ): Int? {
                                        return try {
                                                numString?.toInt()
                                        } catch (e: Exception){
                                                null
                                        }
                                }

                                fun makeConfigMap(
                                        imageMap: Map<String, String>?,
                                ): Map<String, String> {
                                        return imageMap?.get(
                                                ImageKey.MATRIX_STORM_CONFIG_MAP_CON.key
                                        ).let {
                                                CmdClickMap.createMap(
                                                        it,
                                                        keySeparator
                                                ).toMap()
                                        }
                                }
                        }

                        object AutoRndBitmapsManager {

                                private const val keySeparator = '|'
                                enum class AutoRndBitmapsKey(
                                        val key: String
                                ){
                                        WIDTH("width"),
                                        HEIGHT("height"),
                                        PIECE_WIDTH("pieceWidth"),
                                        PIECE_HEIGHT("pieceHeight"),
                                        TIMES("times"),
                                        SHAPE("shape"),
                                        SHAPE_COLOR("shapeColor"),
                                        SHAPE_TYPE("shapeType"),
                                        BK_COLOR("bkColor"),
                                        LAYOUT("layout")
                                }

                                enum class Layout {
                                        LEFT,
                                        RND,
                                }

                                enum class IconType {
                                        IMAGE,
                                        SVG
                                }

                                fun getLayout (
                                        autoRndBitmapsConfigMap: Map<String, String>,
                                ): Layout {
                                        return autoRndBitmapsConfigMap.get(
                                                AutoRndBitmapsKey.LAYOUT.key
                                        )?.let {
                                                layoutStr ->
                                                Layout.entries.firstOrNull {
                                                        it.name == layoutStr
                                                }
                                        } ?: Layout.LEFT
                                }

                                fun getBkColor(
                                        context: Context?,
                                        autoRndBitmapsConfigMap: Map<String, String>,
                                        where: String,
                                ): String? {
                                        val bkColorKey =  AutoRndBitmapsKey.BK_COLOR.key
                                        return autoRndBitmapsConfigMap.get(
                                                bkColorKey
                                        )?.let {
                                                        shapeTypeStr ->
                                                ColorTool.parseColorStr(
                                                        context,
                                                        shapeTypeStr,
                                                        bkColorKey,
                                                        where,
                                                )
                                        }
                                }

                                fun getShapeColor(
                                        context: Context?,
                                        autoRndBitmapsConfigMap: Map<String, String>,
                                        where: String,
                                ): String? {
                                        val shapeColorKey =  AutoRndBitmapsKey.SHAPE_COLOR.key
                                        return autoRndBitmapsConfigMap.get(
                                                shapeColorKey
                                        )?.let {
                                                        shapeTypeStr ->
                                                ColorTool.parseColorStr(
                                                        context,
                                                        shapeTypeStr,
                                                        shapeColorKey,
                                                        where,
                                                )
                                        }
                                }

                                fun getShapeType(
                                        autoRndBitmapsConfigMap: Map<String, String>,
                                ): IconType {
                                        return autoRndBitmapsConfigMap.get(
                                                AutoRndBitmapsKey.SHAPE_TYPE.key
                                        )?.let {
                                                shapeTypeStr ->
                                                IconType.entries.firstOrNull {
                                                        it.name == shapeTypeStr
                                                }
                                        } ?: IconType.SVG
                                }

                                fun getWidth(
                                        autoRndBitmapsConfigMap: Map<String, String>
                                ): Int? {
                                        return autoRndBitmapsConfigMap.get(
                                                AutoRndBitmapsKey.WIDTH.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }


                                fun getHeight(
                                        autoRndBitmapsConfigMap: Map<String, String>
                                ): Int? {
                                        return autoRndBitmapsConfigMap.get(
                                                AutoRndBitmapsKey.HEIGHT.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }

                                fun getPieceWidth(
                                        autoRndBitmapsConfigMap: Map<String, String>
                                ): Int? {
                                        return autoRndBitmapsConfigMap.get(
                                                AutoRndBitmapsKey.PIECE_WIDTH.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }

                                fun getPieceHeight(
                                        autoRndBitmapsConfigMap: Map<String, String>
                                ): Int? {
                                        return autoRndBitmapsConfigMap.get(
                                                AutoRndBitmapsKey.PIECE_HEIGHT.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }

                                fun getTimes(
                                        autoRndBitmapsConfigMap: Map<String, String>
                                ): Int? {
                                        return autoRndBitmapsConfigMap.get(
                                                AutoRndBitmapsKey.TIMES.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }

                                fun getShape(
                                        autoRndBitmapsConfigMap: Map<String, String>
                                ): String {
                                        return autoRndBitmapsConfigMap.get(
                                                AutoRndBitmapsKey.SHAPE.key
                                        ) ?: CmdClickIcons.RECT.str
                                }

                                private fun toInt(
                                        numString: String?
                                ): Int? {
                                        return try {
                                                numString?.toInt()
                                        } catch (e: Exception){
                                                null
                                        }
                                }

                                fun makeConfigMap(
                                        imageMap: Map<String, String>?,
                                ): Map<String, String> {
                                        return imageMap?.get(
                                                ImageKey.AUTO_RND_BITMAPS_CONFIG_MAP_CON.key
                                        ).let {
                                                CmdClickMap.createMap(
                                                        it,
                                                        keySeparator
                                                ).toMap()
                                        }
                                }
                        }

                        object LeftStringsManager {

                                private const val keySeparator = '|'
                                enum class LeftStringsKey(
                                        val key: String
                                ){
                                        WIDTH("width"),
                                        HEIGHT("height"),
                                        PIECE_WIDTH("pieceWidth"),
                                        PIECE_HEIGHT("pieceHeight"),
                                        TIMES("times"),
                                        STRING("string"),
                                        FONT_SIZE("fontSize"),
                                        FONT_TYPE("fontType"),
                                        FONT_STYLE("fontStyle")

                                }

                                fun getWidth(
                                        leftStringsConfigMap: Map<String, String>
                                ): Int? {
                                        return leftStringsConfigMap.get(
                                                LeftStringsKey.WIDTH.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }


                                fun getHeight(
                                        leftStringsConfigMap: Map<String, String>
                                ): Int? {
                                        return leftStringsConfigMap.get(
                                                LeftStringsKey.HEIGHT.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }

                                fun getPieceWidth(
                                        leftStringsConfigMap: Map<String, String>
                                ): Float? {
                                        return leftStringsConfigMap.get(
                                                LeftStringsKey.PIECE_WIDTH.key
                                        )?.let {
                                                toFloat(
                                                        it,
                                                )
                                        }
                                }

                                fun getPieceHeight(
                                        leftStringsConfigMap: Map<String, String>
                                ): Float? {
                                        return leftStringsConfigMap.get(
                                                LeftStringsKey.PIECE_HEIGHT.key
                                        )?.let {
                                                toFloat(
                                                        it,
                                                )
                                        }
                                }

                                fun getTimes(
                                        leftStringsConfigMap: Map<String, String>
                                ): Int? {
                                        return leftStringsConfigMap.get(
                                                LeftStringsKey.TIMES.key
                                        )?.let {
                                                toInt(
                                                        it,
                                                )
                                        }
                                }

                                fun getString(
                                        leftStringsConfigMap: Map<String, String>
                                ): String {
                                        return leftStringsConfigMap.get(
                                                LeftStringsKey.STRING.key
                                        ) ?: String()
                                }

                                fun getFontSize(
                                        leftStringsConfigMap: Map<String, String>
                                ): Float? {
                                        return leftStringsConfigMap.get(
                                                LeftStringsKey.FONT_SIZE.key
                                        )?.let {
                                                toFloat(it)
                                        }
                                }

                                fun getFontType(
                                        leftStringsConfigMap: Map<String, String>
                                ): Typeface {
                                        return leftStringsConfigMap.get(
                                                LeftStringsKey.FONT_TYPE.key,
                                        )?.let {
                                                fontTypeStr ->
                                                EditComponent.Font.entries.firstOrNull {
                                                        it.key == fontTypeStr
                                                }
                                        }?.typeface ?: Font.SANS_SERIF.typeface
                                }

                                fun getFontStyle(
                                        leftStringsConfigMap: Map<String, String>
                                ): Int {
                                        return leftStringsConfigMap.get(
                                                        LeftStringsKey.FONT_STYLE.key,
                                                )?.let {
                                                                fontStyleStr ->
                                                        EditComponent.Template.TextPropertyManager.TextStyle.entries.firstOrNull {
                                                                it.key == fontStyleStr
                                                        }
                                                }?.style ?: EditComponent.Template.TextPropertyManager.TextStyle.NORMAL.style
                                }

                                private fun toInt(
                                        numString: String?
                                ): Int? {
                                        return try {
                                                numString?.toInt()
                                        } catch (e: Exception){
                                                null
                                        }
                                }

                                private fun toFloat(
                                        numString: String?
                                ): Float? {
                                        return try {
                                                numString?.toFloat()
                                        } catch (e: Exception){
                                                null
                                        }
                                }

                                fun makeConfigMap(
                                        imageMap: Map<String, String>?,
                                ): Map<String, String> {
                                        return imageMap?.get(
                                                ImageKey.LEFT_STRINGS_CONFIG_MAP_CON.key
                                        ).let {
                                                CmdClickMap.createMap(
                                                        it,
                                                        keySeparator
                                                ).toMap()
                                        }
                                }
                        }

                        private const val iconMacroSeprator = ":"

                        fun makeIconAndTypePair(macroStr: String): Pair<String, String>{
                                val macroStrList = macroStr.split(iconMacroSeprator)
                                return Pair(
                                        macroStrList.firstOrNull() ?: String(),
                                        macroStrList.getOrNull(1) ?: String(),
                                )
                        }

                        enum class IconType(val type: String){
                                IMAGE("image"),
                                ICON("icon"),
                        }
                }

                object ImagePropertyManager {

                        enum class PropertyKey(val key: String) {
                                TAG("tag"),
                                ALPHA("alpha"),
                                SCALE("scale"),
                                COLOR("color"),
                                BK_COLOR("bkColor"),
                                BK_TINT_COLOR("bkTintColor"),
                                HEIGHT("height"),
                                WIDTH("width"),
                                LAYOUT_GRAVITY("layoutGravity"),
                                GRAVITI("gravity"),
                                PADDING_TOP("paddingTop"),
                                PADDING_BOTTOM("paddingBottom"),
                                PADDING_START("paddingStart"),
                                PADDING_END("paddingEnd"),
                                MARGIN_TOP("marginTop"),
                                MARGIN_BOTTOM("marginBottom"),
                                MARGIN_START("marginStart"),
                                MARGIN_END("marginEnd"),
                                VISIBLE("visible"),
                                BLUR_MAP_CON("blurMapCon")

                        }

                        object BlurManager {

                                private val keySeparator = '|'

                                enum class BlurKey(
                                        val key: String,
                                ) {
                                        RADIUS("radius"),
                                        SAMPLING("sampling"),
                                }
                                fun getBlueRadiusToSampling(
                                        imagePropertyMap: Map<String, String>?
                                ): Pair<Int, Int>? {
                                        if(
                                                imagePropertyMap.isNullOrEmpty()
                                        ) return null
                                        val blurMap = imagePropertyMap.get(
                                                PropertyKey.BLUR_MAP_CON.key
                                        ).let {
                                                CmdClickMap.createMap(
                                                        it,
                                                        keySeparator,
                                                ).toMap()
                                        }
                                        val radius =
                                                blurMap.get(
                                                        BlurKey.RADIUS.key
                                                )?.let {
                                                        toInt(it)
                                                } ?: return null
                                        val sampling = blurMap.get(
                                                BlurKey.SAMPLING.key
                                        )?.let {
                                                toInt(it)
                                        } ?: return null
                                        return Pair(
                                                radius,
                                                sampling,
                                                )

                                }

                                fun toInt(str: String): Int? {
                                        return try {
                                                str.toInt()
                                        } catch (e:Exception){
                                                null
                                        }
                                }
                        }

                        enum class ImageScale(
                                val str: String,
                                val scale:  ImageView.ScaleType
                        ){
                                FIT_CENTER("fitCenter", ImageView.ScaleType.FIT_CENTER),
                                FIT_XY("fitXy", ImageView.ScaleType.FIT_XY),
                                CENTER_CROP("centerCrop", ImageView.ScaleType.CENTER_CROP),
                        }
                }

                object TextPropertyManager {
                        enum class Property(val key: String){
                                TAG("tag"),
                                MAX_LINES("maxLines"),
                                SIZE("size"),
                                STYLE("style"),
                                FONT("font"),
                                COLOR("color"),
                                STROKE_COLOR("strokeColor"),
                                STROKE_WIDTH("strokeWidth"),
                                ALPHA("alpha"),
                                HEIGHT("height"),
                                WIDTH("width"),
                                LAYOUT_GRAVITY("layoutGravity"),
                                GRAVITI("gravity"),
                                PADDING_TOP("paddingTop"),
                                PADDING_BOTTOM("paddingBottom"),
                                PADDING_START("paddingStart"),
                                PADDING_END("paddingEnd"),
                                MARGIN_TOP("marginTop"),
                                MARGIN_BOTTOM("marginBottom"),
                                MARGIN_START("marginStart"),
                                MARGIN_END("marginEnd"),
                                BK_COLOR("bkColor"),
                                VISIBLE("visible"),
                                LETTER_SPACING("letterSpacing"),
                                SHADOW_RADIUS("shadowRadius"),
                                SHADOW_COLOR("shadowColor"),
                                SHADOW_X("shadowX"),
                                SHADOW_Y("shadowY"),
//                                DISABLE_TEXT_SELECT("disableTextSelect"),
                        }

                        enum class TextStyle(
                                val key: String,
                                val style: Int,
                        ){
                                NORMAL("normal", Typeface.NORMAL),
                                BOLD("bold", Typeface.BOLD),
                                BOLD_ITALIC("boldItalic", Typeface.BOLD_ITALIC),
                                ITALIC("bold", Typeface.ITALIC),
                        }
                }

                class MarginData(
                        marginTopStr: String?,
                        marginBottomStr: String?,
                        marginStartStr: String?,
                        marginEndStr: String?,
                        density: Float,
                ) {
                        val marginTop = convertStrToInt(marginTopStr, density)
                        val marginBottom = convertStrToInt(marginBottomStr, density)
                        val marginStart = convertStrToInt(marginStartStr, density)
                        val marginEnd = convertStrToInt(marginEndStr, density)
                }
                class PaddingData(
                        paddingTopStr: String?,
                        paddingBottomStr: String?,
                        paddingStartStr: String?,
                        paddingEndStr: String?,
                        density: Float,
                ) {
                        val paddingTop = convertStrToInt(paddingTopStr, density)
                        val paddingBottom = convertStrToInt(paddingBottomStr, density)
                        val paddingStart = convertStrToInt(paddingStartStr, density)
                        val paddingEnd = convertStrToInt(paddingEndStr, density)


                }

                private fun convertStrToInt(
                        numStr: String?,
                        density: Float,
                ): Int? {
                        return numStr?.let {
                                try {
                                        ScreenSizeCalculator.toDpByDensity(
                                                it.toInt(),
                                                density,
                                        )
                                } catch(e: Exception){
                                        null
                                }
                        }
                }
                object TagManager {

                        private const val curFrameTagAndCurVerticalTagSeparator = "___"

                        enum class TagGenre(val str: String){
                                FRAME_TAG("frameTag"),
                                VERTICAL_TAG("verticalTag"),
                                HORIZON_TAG("horizonTag"),
                                CONTENTS_TAG("contentsTag"),
                        }

                        fun makeVerticalTag(
                                curFrameTag: String,
                                partVerticalTag: String,
                        ): String {
                                return listOf(
                                        curFrameTag,
                                        partVerticalTag
                                ).joinToString(curFrameTagAndCurVerticalTagSeparator)
                        }

                        fun makeHorizonTag(
                                curVerticalTag: String,
                                partHorizonTag: String,
                        ): String {
                                return listOf(
                                        curVerticalTag,
                                        partHorizonTag
                                ).joinToString(curFrameTagAndCurVerticalTagSeparator)
                        }
                }

                object HeightManager {
                        enum class HeightMacro(
                                val macroInt: Int,
                        ){
                                WRAP(LinearLayoutCompat.LayoutParams.WRAP_CONTENT),
                                MATCH(LinearLayoutCompat.LayoutParams.MATCH_PARENT),
                        }
                }

                object WidthManager {
                        enum class WidthMacro(
                                val macroInt: Int,
                        ){
                                WRAP(LinearLayoutCompat.LayoutParams.WRAP_CONTENT),
                                MATCH(LinearLayoutCompat.LayoutParams.MATCH_PARENT),
                        }
                }

                object LinearLayoutUpdater {
                        fun update(
                                context: Context?,
                                linearLayoutParam: LinearLayoutCompat.LayoutParams,
                                linearFrameKeyPairsList: List<Pair<String, String>>,
                                defaultWidth: Int,
                                defaultHeight: Int,
                                density: Float,
                        ): LinearLayoutCompat.LayoutParams {
                                val width = PairListTool.getValue(
                                        linearFrameKeyPairsList,
                                        EditComponentKey.WIDTH.key,
                                )?.let {
                                                widthStr ->
                                        EditComponent.Template.WidthManager.WidthMacro.entries.firstOrNull {
                                                it.name == widthStr
                                        }?.macroInt ?: try {
                                                ScreenSizeCalculator.toDpByDensity(
                                                        widthStr.toInt(),
                                                        density
                                                )
                                        } catch (e: Exception){
                                                null
                                        }
                                }?: defaultWidth
                                val height = PairListTool.getValue(
                                        linearFrameKeyPairsList,
                                        EditComponentKey.HEIGHT.key,
                                )?.let {
                                                heightStr ->
                                        EditComponent.Template.HeightManager.HeightMacro.entries.firstOrNull {
                                                it.name == heightStr
                                        }?.macroInt ?: try {
                                                ScreenSizeCalculator.toDpByDensity(
                                                        heightStr.toInt(),
                                                        density,
                                                )
                                        } catch (e: Exception){
                                                null
                                        }
                                }?: defaultHeight
                                linearLayoutParam.width = width
                                linearLayoutParam.height = height
//                                FileSystems.updateFile(
//                                        File(UsePath.cmdclickDefaultAppDirPath, "layout_update.txt").absolutePath,
//                                        listOf(
//                                                "linearFrameKeyPairsList: ${linearFrameKeyPairsList}",
//                                                "width: ${width}",
//                                                "height: ${height}",
//                                                "defaultHeight: ${defaultHeight}",
//                                                "defaultWidth: ${defaultWidth}",
//                                        ).joinToString("\n")
//                                )
                                return linearLayoutParam
                        }

                        fun convertWidth(
                                widthSrcStr: String?,
                                defaultWidth: Int,
                                density: Float,
                        ): Int {
                                return widthSrcStr?.let {
                                                widthStr ->
                                        EditComponent.Template.WidthManager.WidthMacro.entries.firstOrNull {
                                                it.name == widthStr
                                        }?.macroInt ?: try {
                                                ScreenSizeCalculator.toDpByDensity(
                                                        widthStr.toInt(),
                                                        density
                                                )
                                        } catch (e: Exception){
                                                null
                                        }
                                }?: defaultWidth
                        }

                        fun convertHeight(
                                heightSrcStr: String?,
                                defaultHeight: Int,
                                density: Float,
                        ): Int {
                                return heightSrcStr?.let {
                                                heightStr ->
                                        EditComponent.Template.HeightManager.HeightMacro.entries.firstOrNull {
                                                it.name == heightStr
                                        }?.macroInt ?: try {
                                                ScreenSizeCalculator.toDpByDensity(
                                                        heightStr.toInt(),
                                                        density,
                                                )
                                        } catch (e: Exception){
                                                null
                                        }
                                }?: defaultHeight
                        }
                }


                object TextManager {
                        enum class TextKey(val key: String) {
                                REMOVE_REGEX("removeRegex"),
                                REPLACE_STR("replaceStr"),
                                FILTER_SHELL_PATH("filterShellPath"),
                                DISPLAY_TEXT("displayText"),
                                SRC_STR("srcStr"),
                                SETTING_VALUE("settingValue"),
                                LENGTH("length"),
                                ON_UPDATE("onUpdate"),
                        }

                        fun createTextMap(
                                textMapCon: String?,
                                settingValue: String?,
                        ): Map<String,String> {
                                val textMapSrc = CmdClickMap.createMap(
                                        textMapCon,
                                        keySeparator
                                ).toMap()
                                return when(settingValue.isNullOrEmpty()){
                                        true -> textMapSrc
                                        else -> textMapSrc + mapOf(
                                                TextKey.SETTING_VALUE.key to settingValue
                                        )
                                }
                        }

                        fun makeText(
                                fannelInfoMap: Map<String, String>,
                                setReplaceVariableMap: Map<String, String>?,
                                busyboxExecutor: BusyboxExecutor?,
                                textMap: Map<String, String>?,
                                settingValueSrc: String?,
                        ): String? {
//                                FileSystems.updateFile(
//                                        File(UsePath.cmdclickDefaultAppDirPath, "label.txt").absolutePath,
//                                        listOf(
//                                               "labelMap: ${labelMap}",
//                                                "---settingValue: ${settingValue}\n",
//                                        ).joinToString("\n")
//
//                                )
                                if(
                                        textMap.isNullOrEmpty()
                                ) return String()
                                val settingValue = settingValueSrc?.let {
                                        QuoteTool.trimBothEdgeQuote(it)
                                }?: String()
                                val displayTextSrc = makeDisplayTextByRemoveRegex(
                                        textMap,
                                        settingValue,
                                )?: return null
                                val filterShellCon = textMap.get(
                                        TextKey.FILTER_SHELL_PATH.key
                                )?.let {
                                        ReadText(it).readText().replace(
                                                ReplaceHolder.SrcReplaceHolders.SHELL_SRC.key,
                                                displayTextSrc,
                                        )
                                }
                                val length = textMap.get(
                                        TextKey.LENGTH.key
                                )?.let {
                                        try{
                                                it.toInt()
                                        }catch (e: Exception){
                                                null
                                        }
                                }
                                if(
                                        filterShellCon.isNullOrEmpty()
                                ) return displayTextSrc.let {
                                        if(
                                                length == null
                                        ) return it
                                        it.take(length)
                                }
                                val fannelName = FannelInfoTool.getCurrentFannelName(
                                        fannelInfoMap
                                )
                                return getOutputByShellCon(
                                        setReplaceVariableMap,
                                        busyboxExecutor,
                                        filterShellCon,
                                        fannelName
                                )?.let {
                                        if(
                                                length == null
                                        ) return it
                                        it.take(length)
                                }
                        }

                        private fun makeDisplayTextByRemoveRegex(
                                textMap: Map<String, String>,
                                settingValue: String,
                        ): String? {
                                val displayTextSrc = textMap.get(
                                        TextKey.DISPLAY_TEXT.key
                                )?.replace(
                                        ReplaceHolder.SrcReplaceHolders.SETTING_VALUE.key,
                                        settingValue
                                ) ?: return null
                                val srcStrBeforeRemove = textMap.get(
                                        TextKey.SRC_STR.key
                                )?.replace(
                                        ReplaceHolder.SrcReplaceHolders.SETTING_VALUE.key,
                                        settingValue
                                ) ?: return displayTextSrc
                                val removeRegexToReplaceKeyList =
                                        FilterAndMapModule.makeRemoveRegexToReplaceKeyPairList(
                                                textMap,
                                                FilterAndMapModule.ExtraMapBaseKey.REMOVE_REGEX.key
                                        )
                                val srcStr = FilterAndMapModule.applyRemoveRegex(
                                        srcStrBeforeRemove,
                                        removeRegexToReplaceKeyList,
                                        textMap,
                                )
//                                FileSystems.updateFile(
//                                        File(UsePath.cmdclickDefaultAppDirPath, "ldispalyText2.txt").absolutePath,
//                                        listOf(
//                                                "displayLabelSrc: ${displayLabelSrc}",
//                                                "srcStrBeforeRemove: ${srcStrBeforeRemove}",
//                                                "srcStr: ${srcStr}"
//                                                ).joinToString("\n")
//                                )
                                return displayTextSrc.replace(
                                        ReplaceHolder.SrcReplaceHolders.SRC_STR.key,
                                        srcStr
                                )
                        }

                        private fun getOutputByShellCon(
                                setReplaceVariableMap: Map<String, String>?,
                                busyboxExecutor: BusyboxExecutor?,
                                shellConSrc: String,
                                currentFannelName: String,
                        ): String? {
                                val shellCon = SetReplaceVariabler.execReplaceByReplaceVariables(
                                        shellConSrc,
                                        setReplaceVariableMap,
                                        currentFannelName
                                )
                                if(
                                        shellCon.isEmpty()
                                ) return null
                                return busyboxExecutor?.getCmdOutput(
                                        shellCon,
                                        null
                                )
                        }
                }

                object VisibleManager {
                        private enum class VisibleType(
                                val str: String,
                                val visible: Int,
                        ){
                                GONE("gone", View.GONE),
                                INVISIBLE("invisible", View.INVISIBLE),
                                VISIBLE("visible", View.VISIBLE),
                        }

                        fun getVisible(visibleStr: String?): Int {
                                return VisibleType.entries.firstOrNull {
                                        it.str == visibleStr
                                }?.visible ?: VisibleType.VISIBLE.visible

                        }

                }
        }


        object AdapterSetter {

                object AlreadyUseTagListHandler {

                        suspend fun get(
                                alreadyUseTagList: MutableList<String>,
                                alreadyUseTagListMutex: Mutex,
                        ): List<String> {
                                return alreadyUseTagListMutex.withLock {
                                        alreadyUseTagList
                                }.toList()

                        }
                }


                fun tagDuplicateErrHandler(
                        context: Context?,
                        tagJanre: Template.TagManager.TagGenre,
                        tagName: String,
                        alreadyUseTagList: List<String>,
                        mapListElInfo: String,
                        plusKeyToSubKeyConWhere: String,
                ): String? {
//                        FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "stagDup.txt").absolutePath,
//                                listOf(
//                                        "alreadyUseTagList: ${alreadyUseTagList}",
//                                        "tagName: ${tagName}",
//                                ).joinToString("\n")
//                        )
                        if(
                                !alreadyUseTagList.contains(tagName)
                        ) return tagName
                        val tagKeyName =
                                Template.EditComponentKey.TAG.key
                        val spanTagGenre = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.ligthBlue,
                                tagJanre.str
                        )
                        val spanTagNameKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errRedCode,
                                tagName
                        )
                        val spanWhereForLog = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errBrown,
                                "${mapListElInfo} in ${plusKeyToSubKeyConWhere}"
                        )
                        val errSrcMessage =
                                "Forbidden to duplicate ${tagKeyName}: ${spanTagGenre}: ${spanTagNameKey}"
                        val errMessage =
                                "[${LogErrLabel.VIEW_LAYOUT.label}] ${errSrcMessage} about ${spanWhereForLog}"
                        LogSystems.broadErrLog(
                                context,
                                Jsoup.parse(errSrcMessage).text(),
                                errMessage
                        )
                        return null
                }

                suspend fun makeFrameVarNameToValueMap(
                        fragment: Fragment?,
                        fannelInfoMap: Map<String, String>,
                        setReplaceVariableMap: Map<String, String>?,
                        busyboxExecutor: BusyboxExecutor?,
                        editConstraintListAdapter: EditConstraintListAdapter?,
                        verticalVarNameValueMap: Map<String, String>,
                        keyToSubKeyConWhere: String,
                        linearFrameKeyPairsListConSrc: String?,
                        srcTitle: String,
                        srcCon: String,
                        srcImage: String,
                        srcPosition: Int,
                ):  Map<String, String> {
                        if (
                                linearFrameKeyPairsListConSrc.isNullOrEmpty()
                        ) return emptyMap()
                        return Template.ReplaceHolder.replaceHolder(
                                linearFrameKeyPairsListConSrc,
                                srcTitle,
                                srcCon,
                                srcImage,
                                srcPosition,
                        ).let {
                                        linearFrameKeyPairsListConSrcWithReplace ->
                                if(
                                        linearFrameKeyPairsListConSrcWithReplace.isNullOrEmpty()
                                ) return@let emptyMap()
                                val settingActionManager = SettingActionManager()
                                settingActionManager.exec(
                                        fragment,
                                        fannelInfoMap,
                                        setReplaceVariableMap,
                                        busyboxExecutor,
                                        CmdClickMap.replace(
                                                linearFrameKeyPairsListConSrcWithReplace,
                                                verticalVarNameValueMap,
                                        ),
                                        keyToSubKeyConWhere,
                                        editConstraintListAdapterArg = editConstraintListAdapter
                                ).let updateVarNameToValueMap@ {
                                        if(
                                                it.isEmpty()
                                        ) return@updateVarNameToValueMap emptyMap()
                                        it
                                }
                        }
                }

                suspend fun makeContentsTagToKeyPairsList(
                        context: Context?,
                        contentsKeyValues: List<String>,
                        horizonVarNameToValueMap: Map<String, String>?,
                        srcTitle: String,
                        srcCon: String,
                        srcImage: String,
                        bindingAdapterPosition: Int,
                        mapListElInfo: String,
                ):  List<
                        Pair<
                                String,
                                String?
                        >
                        > {
                        return withContext(Dispatchers.IO) {
                                val jobList = contentsKeyValues.mapIndexed { index, contentsKeyPairsListConSrc ->
                                        async {
                                                if (
                                                        contentsKeyPairsListConSrc.isEmpty()
                                                ) return@async index to Pair(String(), null)
                                                val contentsKeyPairsListCon =
                                                        Template.ReplaceHolder.replaceHolder(
                                                                contentsKeyPairsListConSrc,
                                                                srcTitle,
                                                                srcCon,
                                                                srcImage,
                                                                bindingAdapterPosition,
                                                        )?.let {
                                                                CmdClickMap.replace(
                                                                        it,
                                                                        horizonVarNameToValueMap,
                                                                )
                                                        }
                                                val linearFrameKeyPairsList =
                                                        EditConstraintListAdapter.makeLinearFrameKeyPairsList(
                                                                contentsKeyPairsListCon,
                                                        )
                                                val contentsTag =
                                                        PairListTool.getValue(
                                                                linearFrameKeyPairsList,
                                                                Template.EditComponentKey.TAG.key,
                                                        ) ?: String()
                                                when(
                                                        contentsTag.isEmpty()
                                                        && !contentsKeyPairsListCon.isNullOrEmpty()
                                                ) {
                                                        true -> {
                                                                ListSettingsForEditList.ViewLayoutCheck.isTagBlankErr(
                                                                        context,
                                                                        contentsTag,
                                                                        mapListElInfo,
                                                                        EditComponent.Template.TagManager.TagGenre.CONTENTS_TAG
                                                                ).let {
                                                                        isTagBlankErrJob ->
                                                                        if (!isTagBlankErrJob) return@let
                                                                        return@async index to Pair(
                                                                                String(),
                                                                                String()
                                                                        )
                                                                }
                                                        }
                                                        else -> {}
                                                }
                                                index to Pair(
                                                        contentsTag,
                                                        contentsKeyPairsListCon
                                                )
                                        }
                                }
                                jobList.awaitAll().sortedBy {
                                        val index = it.first
                                        index
                                }.map {
                                        val linearFrameTagToKeyPairsList = it.second
                                        linearFrameTagToKeyPairsList
                                }.filter {
                                        it.first.isNotEmpty()
                                                && !it.second.isNullOrEmpty()
                                }
                        }
                }

                fun makeContentsFrameLayout(
                        context: Context,
                ): FrameLayout {
                        val dp50 =
                                context.resources.getDimension(R.dimen.toolbar_layout_height)
                        val contentsLayout =
                                FrameLayout(context).apply {
                                        layoutParams =
                                                ConstraintLayout.LayoutParams(
                                                        0,
                                                        dp50.toInt()
                                                )
                                }
                        val imageView =
                                AppCompatImageView(context).apply {
                                        layoutParams =
                                                FrameLayout.LayoutParams(
                                                        FrameLayout.LayoutParams.MATCH_PARENT,
                                                        FrameLayout.LayoutParams.MATCH_PARENT
                                                )
                                }
                        val textView =
                                OutlineTextView(context).apply {
                                        layoutParams =
                                                FrameLayout.LayoutParams(
                                                        FrameLayout.LayoutParams.WRAP_CONTENT,
                                                        FrameLayout.LayoutParams.WRAP_CONTENT
                                                )
                                }
                        return contentsLayout.apply {
                                addView(imageView)
                                addView(textView)
                        }
                }
        }
}