package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter.Companion.makeLinearFrameKeyPairsList
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.libs.FilterAndMapModule
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.PairListTool
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EditComponent {
        object Template {

                const val sectionSeparator = ','
                const val typeSeparator = '|'
                const val keySeparator = '?'
                const val valueSeparator = '&'

                const val switchOn = "ON"

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
//                        FOOTER("footer"),
                        VERTICAL("vertical"),
                        HORIZON("horizon"),
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
                        SET_IMAGE_TO_TAGS("setImageToTags"),
                        SET_TEXT_TO_TAGS("setTextToTags"),
                        SET_SETTING_VALS_TO_TAGS("setSettingValsToTags"),
                        SET_CMD_VALS_TO_TAGS("setCmdValsToTags"),
                        SET_IMAGE_TO_BTN_TAGS("setImageToBtnTags"),
                        SET_TEXT_TO_BTN_TAGS("setTextToBtnTags"),
                        SET_SUGGEST_TO_BTN_TAGS("setSuggestToBtnTags"),
                        ON_SAVE("onSave"),
                        IS_CONSEC("isConsec"),
                        DISABLE_KEYBOARD_HIDDEN("disableKeyboardHidden"),
                        HEIGHT("height"),
                        WIDTH("width"),
                        WEIGHT("weight"),
                        PADDING_TOP("paddingTop"),
                        PADDING_BOTTOM("paddingBottom"),
                        PADDING_START("paddingStart"),
                        PADDING_END("paddingEnd"),
                        MARGIN_TOP("marginTop"),
                        MARGIN_BOTTOM("marginBottom"),
                        MARGIN_START("marginStart"),
                        MARGIN_END("marginEnd"),
                        GRAVITI("gravity"),
                        BK_COLOR("bkColor"),
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
                                HEIGHT("height"),
                                WIDTH("width"),
                                GRAVITI("gravity"),
                                PADDING_TOP("paddingTop"),
                                PADDING_BOTTOM("paddingBottom"),
                                PADDING_START("paddingStart"),
                                PADDING_END("paddingEnd"),
                                MARGIN_TOP("marginTop"),
                                MARGIN_BOTTOM("marginBottom"),
                                MARGIN_START("marginStart"),
                                MARGIN_END("marginEnd"),
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

                        enum class Font(
                                val key: String,
                                val typeface: Typeface,
                        ){
                                DEFAULT("monospace", Typeface.DEFAULT),
                                MONOSPACE("monospace", Typeface.MONOSPACE),
                                SANS_SERIF("sansSerif", Typeface.SANS_SERIF),
                                SERIF("serif", Typeface.SERIF),
                        }
                }

                class MarginData(
                        context: Context?,
                        marginTopStr: String?,
                        marginBottomStr: String?,
                        marginStartStr: String?,
                        marginEndStr: String?,
                ) {
                        val marginTop = convertStrToInt(context, marginTopStr)
                        val marginBottom = convertStrToInt(context, marginBottomStr)
                        val marginStart = convertStrToInt(context, marginStartStr)
                        val marginEnd = convertStrToInt(context, marginEndStr)
                }
                class PaddingData(
                        context: Context?,
                        paddingTopStr: String?,
                        paddingBottomStr: String?,
                        paddingStartStr: String?,
                        paddingEndStr: String?,
                ) {
                        val paddingTop = convertStrToInt(context, paddingTopStr)
                        val paddingBottom = convertStrToInt(context, paddingBottomStr)
                        val paddingStart = convertStrToInt(context, paddingStartStr)
                        val paddingEnd = convertStrToInt(context, paddingEndStr)


                }

                private fun convertStrToInt(
                        context: Context?,
                        numStr: String?
                ): Int {
                        return numStr?.let {
                                try {
                                        ScreenSizeCalculator.toDp(
                                                context,
                                                it.toInt()
                                        )
                                } catch(e: Exception){
                                        null
                                }
                        } ?: 0
                }
                object TagManager {
                        enum class TagMacro {
                                LINEAR_SETTING
                        }

                        fun makeVerticalTag(
                                curFrameTag: String,
                                partVerticalTag: String,
                        ): String {
                                val curFrameTagAndCurVerticalTagSeparator = "__"
                                return listOf(
                                        curFrameTag,
                                        partVerticalTag
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
                        ): LinearLayoutCompat.LayoutParams {
                                val width = PairListTool.getValue(
                                        linearFrameKeyPairsList,
                                        EditComponentKey.WIDTH.key,
                                )?.let {
                                                widthStr ->
                                        EditComponent.Template.WidthManager.WidthMacro.entries.firstOrNull {
                                                it.name == widthStr
                                        }?.macroInt ?: try {
                                                ScreenSizeCalculator.toDp(context, widthStr.toInt())
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
                                                ScreenSizeCalculator.toDp(context, heightStr.toInt())
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
                                context: Context?,
                                widthSrcStr: String?,
                                defaultWidth: Int,
                        ): Int {
                                return widthSrcStr?.let {
                                                widthStr ->
                                        EditComponent.Template.WidthManager.WidthMacro.entries.firstOrNull {
                                                it.name == widthStr
                                        }?.macroInt ?: try {
                                                ScreenSizeCalculator.toDp(context, widthStr.toInt())
                                        } catch (e: Exception){
                                                null
                                        }
                                }?: defaultWidth
                        }

                        fun convertHeight(
                                context: Context?,
                                heightSrcStr: String?,
                                defaultHeight: Int,
                        ): Int {
                                return heightSrcStr?.let {
                                                heightStr ->
                                        EditComponent.Template.HeightManager.HeightMacro.entries.firstOrNull {
                                                it.name == heightStr
                                        }?.macroInt ?: try {
                                                ScreenSizeCalculator.toDp(context, heightStr.toInt())
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
//                                val filterPrefixListCon = labelMap.get(
//                                        LabelKey.PREFIX.key
//                                )?.split(valueSeparator)
//
//                                val filterSuffixListCon = labelMap.get(
//                                        LabelKey.SUFFIX.key
//                                )?.split(valueSeparator)
//
//                                val filterShellCon = labelMap.get(
//                                        LabelKey.FILTER_SHELL_PATH.key
//                                )?.split(valueSeparator)
//
//                                val fileListSource = makeFileListElement(
//                                        currentFileList,
//                                        busyboxExecutor,
//                                        filterDir,
//                                        filterPrefixListCon,
//                                        filterSuffixListCon,
//                                        filterShellCon,
//                                )
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
        }


//        fun makeEditLinearLayoutList(
//                editFragment: EditFragment
//        ): List<LinearLayoutCompat> {
//        val binding = editFragment.binding
//        return when(editFragment.existIndexList){
//            true -> listOf(
//                binding.editListInnerTopLinearLayout,
//                binding.editListInnerBottomLinearLayout,
//            )
//            else -> listOf(
//                binding.editLinearLayout,
//            )
//        }

        fun findEditTextView(
                currentId: Int,
                editLinearLayoutList: List<LinearLayoutCompat>,
        ): EditText? {
                editLinearLayoutList.forEach {
                        val extractedEditText = it.findViewById<EditText>(currentId)
                        if(
                                extractedEditText != null
                        ) return extractedEditText
                }
                LogSystems.stdWarn(
                        "no exist editText id: ${currentId}"
                )
                return null
        }

        fun findSpinnerView(
                currentId: Int,
                editLinearLayoutList: List<LinearLayoutCompat>,
        ): Spinner? {
                editLinearLayoutList.forEach {
                        val extractedSpinner = it.findViewById<Spinner>(currentId)
                        if(
                                extractedSpinner != null
                        ) return extractedSpinner
                }
                LogSystems.stdWarn(
                        "no exist editText id: ${currentId}"
                )
                return null
        }

        object AdapterSetter {

                fun makeVerticalTagToKeyPairsList(
                        frameTag: String,
                        frameTagToVerticalKeysCon: List<Pair<String, String>>
                ): List<Pair<String, List<Pair<String, String>>>> {
                        return frameTagToVerticalKeysCon.filter {
                                val curFrameTag = it.first
                                curFrameTag == frameTag
                        }.map {
                                val verticalKeysCon = it.second
                                val verticalKeyPairs = makeLinearFrameKeyPairsList(
                                        verticalKeysCon
                                )
                                val partVerticalTag = PairListTool.getValue(
                                        verticalKeyPairs,
                                        Template.EditComponentKey.TAG.key
                                ) ?: String()
                                val verticalTag = let {
                                        Template.TagManager.makeVerticalTag(
                                                frameTag,
                                                partVerticalTag,
                                        )
                                }
                                verticalTag to verticalKeyPairs
                        }
                }

                fun makeLinearFrameTagToKeyPairsList(
                        linearKeyValues: List<String>,
                        srcTitle: String,
                        srcCon: String,
                        srcImage: String,
                        bindingAdapterPosition: Int,
                ):  List<Triple<String, List<Pair<String, String>>, String?>> {
                        return linearKeyValues.map { linearFrameKeyPairsListConSrc ->
                                val linearFrameKeyPairsListCon =
                                        Template.ReplaceHolder.replaceHolder(
                                                linearFrameKeyPairsListConSrc,
                                                srcTitle,
                                                srcCon,
                                                srcImage,
                                                bindingAdapterPosition,
                                        )
                                val linearFrameKeyPairsList =
                                        makeLinearFrameKeyPairsList(
                                                linearFrameKeyPairsListCon,
                                        )
                                val linearFrameTag =
                                        PairListTool.getValue(
                                                linearFrameKeyPairsList,
                                                Template.EditComponentKey.TAG.key,
                                        ) ?: String()
                                Triple(
                                        linearFrameTag,
                                        linearFrameKeyPairsList,
                                        linearFrameKeyPairsListCon
                                )
                        }
                }
                suspend fun makeVerticalLinear(
                        context: Context,
                        verticalLinearLayoutSrc: LinearLayoutCompat?,
                        verticalKeyPairs: List<Pair<String, String>>,
                        verticalLinerWeight: Float,
                        verticalTag: String,
                ): LinearLayoutCompat {
                        return when(verticalLinearLayoutSrc == null) {
                                false -> verticalLinearLayoutSrc
                                else -> LinearLayoutCompat(context)
                        }.apply {
                                val verticalWidth =
                                        PairListTool.getValue(
                                                verticalKeyPairs,
                                                Template.EditComponentKey.WIDTH.key,
                                        ).let {
                                                Template.LinearLayoutUpdater.convertWidth(
                                                        context,
                                                        it,
                                                        0,
                                                )
                                        }
                                val verticalHeight =
                                        PairListTool.getValue(
                                                verticalKeyPairs,
                                                Template.EditComponentKey.HEIGHT.key,
                                        ).let {
                                                Template.LinearLayoutUpdater.convertWidth(
                                                        context,
                                                        it,
                                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                                )
                                        }
                                val verticalLinearParam = LinearLayoutCompat.LayoutParams(
                                        verticalWidth,
                                        verticalHeight
                                ).apply {
                                        tag = verticalTag
                                        val overrideVerticalLinearWeight = PairListTool.getValue(
                                                verticalKeyPairs,
                                                Template.EditComponentKey.WEIGHT.key,
                                        )?.let {
                                                try {
                                                        it.toFloat()
                                                } catch (e: Exception) {
                                                        null
                                                }
                                        } ?: verticalLinerWeight
                                        weight = overrideVerticalLinearWeight
                                        val overrideGravity = PairListTool.getValue(
                                                verticalKeyPairs,
                                                Template.EditComponentKey.GRAVITI.key,
                                        )?.let {
                                                        gravityStr ->
                                                EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                                                        it.key == gravityStr
                                                }?.gravity
                                        } ?: Gravity.CENTER
                                        gravity = overrideGravity
                                        val bkColor = withContext(Dispatchers.IO) {
                                                PairListTool.getValue(
                                                        verticalKeyPairs,
                                                        Template.EditComponentKey.BK_COLOR.key,
                                                )?.let {
                                                                colorStr ->
                                                        CmdClickColor.entries.firstOrNull {
                                                                it.str == colorStr
                                                        }
                                                }
                                        }
                                        background = bkColor?.let {
                                                AppCompatResources.getDrawable(
                                                        context,
                                                        it.id
                                                )
                                        }
                                        val marginData = Template.MarginData(
                                                context,
                                                PairListTool.getValue(
                                                        verticalKeyPairs,
                                                        Template.EditComponentKey.MARGIN_TOP.key,
                                                ),
                                                PairListTool.getValue(
                                                        verticalKeyPairs,
                                                        Template.EditComponentKey.MARGIN_BOTTOM.key,
                                                ),
                                                PairListTool.getValue(
                                                        verticalKeyPairs,
                                                        Template.EditComponentKey.MARGIN_START.key,
                                                ),
                                                PairListTool.getValue(
                                                        verticalKeyPairs,
                                                        Template.EditComponentKey.MARGIN_END.key,
                                                ),
                                        )
                                        topMargin = marginData.marginTop
                                        bottomMargin = marginData.marginBottom
                                        marginStart = marginData.marginStart
                                        marginEnd = marginData.marginBottom
//                            setMargins(ScreenSizeCalculator.toDp(context,10))
                                }
                                layoutParams = verticalLinearParam
                                val paddingData = Template.PaddingData(
                                        context,
                                        PairListTool.getValue(
                                                verticalKeyPairs,
                                                Template.EditComponentKey.PADDING_TOP.key,
                                        ),
                                        PairListTool.getValue(
                                                verticalKeyPairs,
                                                Template.EditComponentKey.PADDING_BOTTOM.key,
                                        ),
                                        PairListTool.getValue(
                                                verticalKeyPairs,
                                                Template.EditComponentKey.PADDING_START.key,
                                        ),
                                        PairListTool.getValue(
                                                verticalKeyPairs,
                                                Template.EditComponentKey.PADDING_END.key,
                                        ),
                                )
                                setPadding(
                                        paddingData.paddingStart,
                                        paddingData.paddingTop,
                                        paddingData.paddingEnd,
                                        paddingData.paddingBottom,
                                )
                                orientation = LinearLayoutCompat.VERTICAL
                        }
                }

                suspend fun setHorizonLinear(
                        horizonLinearLayout: LinearLayoutCompat?,
                        verticalKeyPairs: List<Pair<String, String>>,
                        linearFrameKeyPairsList: List<Pair<String, String>>
                ){
                        withContext(Dispatchers.Main) {
                                horizonLinearLayout?.apply {
                                        val horizonLinearLayoutParam =
                                                layoutParams as LinearLayoutCompat.LayoutParams
                                        Template.LinearLayoutUpdater.update(
                                                context,
                                                horizonLinearLayoutParam,
                                                linearFrameKeyPairsList,
                                                horizonLinearLayoutParam.width,
                                                horizonLinearLayoutParam.height
                                        )
                                        layoutParams = horizonLinearLayoutParam.apply {
                                                val marginData = Template.MarginData(
                                                        context,
                                                        PairListTool.getValue(
                                                                verticalKeyPairs,
                                                                Template.EditComponentKey.MARGIN_TOP.key,
                                                        ),
                                                        PairListTool.getValue(
                                                                verticalKeyPairs,
                                                                Template.EditComponentKey.MARGIN_BOTTOM.key,
                                                        ),
                                                        PairListTool.getValue(
                                                                verticalKeyPairs,
                                                                Template.EditComponentKey.MARGIN_START.key,
                                                        ),
                                                        PairListTool.getValue(
                                                                verticalKeyPairs,
                                                                Template.EditComponentKey.MARGIN_END.key,
                                                        ),
                                                )
                                                topMargin = marginData.marginTop
                                                bottomMargin = marginData.marginBottom
                                                marginStart = marginData.marginStart
                                                marginEnd = marginData.marginBottom
                                                val bkColor = withContext(Dispatchers.IO) {
                                                        PairListTool.getValue(
                                                                verticalKeyPairs,
                                                                Template.EditComponentKey.BK_COLOR.key,
                                                        )?.let {
                                                                        colorStr ->
                                                                CmdClickColor.entries.firstOrNull {
                                                                        it.str == colorStr
                                                                }
                                                        }
                                                }
                                                background = bkColor?.let {
                                                        AppCompatResources.getDrawable(
                                                                context,
                                                                it.id
                                                        )
                                                }
                                        }
                                        val paddingData = Template.PaddingData(
                                                context,
                                                PairListTool.getValue(
                                                        verticalKeyPairs,
                                                        Template.EditComponentKey.PADDING_TOP.key,
                                                ),
                                                PairListTool.getValue(
                                                        verticalKeyPairs,
                                                        Template.EditComponentKey.PADDING_BOTTOM.key,
                                                ),
                                                PairListTool.getValue(
                                                        verticalKeyPairs,
                                                        Template.EditComponentKey.PADDING_START.key,
                                                ),
                                                PairListTool.getValue(
                                                        verticalKeyPairs,
                                                        Template.EditComponentKey.PADDING_END.key,
                                                ),
                                        )
                                        setPadding(
                                                paddingData.paddingStart,
                                                paddingData.paddingTop,
                                                paddingData.paddingEnd,
                                                paddingData.paddingBottom,
                                        )
                                        val overrideGravity = PairListTool.getValue(
                                                verticalKeyPairs,
                                                Template.EditComponentKey.GRAVITI.key,
                                        )?.let {
                                                        gravityStr ->
                                                EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                                                        it.key == gravityStr
                                                }?.gravity
                                        } ?: Gravity.CENTER
                                        gravity = overrideGravity
                                }
                        }
                }
        }
}