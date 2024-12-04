package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter.Companion.makeLinearFrameKeyPairsList
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.libs.FilterAndMapModule
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList.LogErrLabel
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
import org.jsoup.Jsoup

object EditComponent {
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
//                        FOOTER("footer"),
                        VERTICAL("vertical"),
                        HORIZON("horizon"),
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
                        SET_IMAGE_TO_TAGS("setImageToTags"),
                        SET_TEXT_TO_TAGS("setTextToTags"),
                        SET_SETTING_VALS_TO_TAGS("setSettingValsToTags"),
                        SET_CMD_VALS_TO_TAGS("setCmdValsToTags"),
                        SET_IMAGE_TO_BTN_TAGS("setImageToBtnTags"),
                        SET_TEXT_TO_BTN_TAGS("setTextToBtnTags"),
                        SET_SUGGEST_TO_BTN_TAGS("setSuggestToBtnTags"),
                        ON_CLICK("onClick"),
                        ON_SAVE("onSave"),
                        ON_CONSEC("onConsec"),
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
                        LAYOUT_GRAVITY("layoutGravity"),
                        BK_COLOR("bkColor"),
                        VISIBLE("visible"),
                        ENABLE("enable"),
                }

                object LinearManager {
                        val linearNoSettingKey = listOf(
                                EditComponentKey.TEXT.key,
                                EditComponentKey.TEXT_PROPERTY.key,
                                EditComponentKey.IMAGE.key,
                                EditComponentKey.IMAGE_PROPERTY.key,
                                EditComponentKey.ON_SAVE.key,
                                EditComponentKey.ON_CONSEC.key,
                        )
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
                        numStr: String?,
                ): Int? {
                        return numStr?.let {
                                try {
                                        ScreenSizeCalculator.toDp(
                                                context,
                                                it.toInt()
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

                        fun extractVerticalTag(
                                curVerticalTag: String,
                        ): String {
                                return curVerticalTag.split(
                                        curFrameTagAndCurVerticalTagSeparator
                                ).getOrNull(1) ?: String()
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

                        fun extractHorizonTag(
                                curHorizonTag: String,
                        ): String {
                                return curHorizonTag.split(
                                        curFrameTagAndCurVerticalTagSeparator
                                ).getOrNull(2) ?: String()
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

                fun culcVerticalLinerWeight(
                        verticalTagToKeyPairsListToVarNameToValueMapList: List<Pair<String, Pair<List<Pair<String, String>>, Map<String, String>>>>
                ): Float {
                        val enableKey = Template.EditComponentKey.ENABLE.key
                        val switchOff = Template.switchOff
                        val enableListSize = verticalTagToKeyPairsListToVarNameToValueMapList.filter { verticalTagToKeyPairsListToVarNameToValueMap ->
                                val keyPairsListToVarNameToValueMap =
                                        verticalTagToKeyPairsListToVarNameToValueMap.second
                                val verticalKeyPairs = keyPairsListToVarNameToValueMap.first
                                PairListTool.getValue(
                                        verticalKeyPairs,
                                        enableKey,
                                ).let {
                                        enableStr ->
                                        enableStr != switchOff
                                }
                        }.size
                        return 1f / enableListSize
                //verticalTagToKeyPairsListToVarNameToValueMapListSize.toFloat()
                }

                fun culcLinearKeyValueSize(
                        contentsTagToKeyPairsList: List<Pair<String, String?>>
                ): Int {
                        val typeSeparator = Template.typeSeparator
                        val enableKey = Template.EditComponentKey.ENABLE.key
                        val switchOff = Template.switchOff
                       return contentsTagToKeyPairsList.filterIndexed {
                                                index, tagTokeyValuesCon ->
                               val keyValuesCon = tagTokeyValuesCon.second
                               val keyValuePairList = CmdClickMap.createMap(
                                       keyValuesCon,
                                       typeSeparator
                               )
                                        val isEnable = PairListTool.getValue(
                                                keyValuePairList,
                                                enableKey,
                                        ).let { enableStr ->
                                                enableStr != switchOff
                                        }
                                        isEnable
                                }.size
                        }

                fun isNotLinearKeyErr(
                        context: Context?,
                        horizonOrVerticalSettingKey: String,
                        linearFrameKeyPairsList: List<Pair<String, String>>,
                        mapListElInfo: String,
                        plusKeyToSubKeyConWhere: String,
                ): Boolean {
                        val horizonSettingKeyEntryList = linearFrameKeyPairsList.map {
                                it.first
                        }
                        val irregularSettingKey = Template.LinearManager.linearNoSettingKey.firstOrNull {
                                horizonSettingKeyEntryList.contains(it)
                        }
                        val isErr = !irregularSettingKey.isNullOrEmpty()
                        if(
                                !isErr
                        ) return false
                        val spanIrregularSettingKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errRedCode,
                                irregularSettingKey ?: String()
                        )
                        val spanWhereForLog = CheckTool.LogVisualManager.execMakeSpanTagHolder(
                                CheckTool.errBrown,
                                "${mapListElInfo} in ${plusKeyToSubKeyConWhere}"
                        )
                        val errSrcMessage =
                                "Irregular ${horizonOrVerticalSettingKey} setting key: ${spanIrregularSettingKey}"
                        val errMessage =
                                "[${LogErrLabel.VIEW_LAYOUT.label}] ${errSrcMessage} about ${spanWhereForLog}"
                        LogSystems.broadErrLog(
                                context,
                                Jsoup.parse(errSrcMessage).text(),
                                errMessage
                        )
                        return true

                }

                suspend fun makeFrameVarNameToValueMap(
                        fragment: Fragment?,
                        fannelInfoMap: Map<String, String>,
                        setReplaceVariableMap: Map<String, String>?,
                        busyboxExecutor: BusyboxExecutor?,
                        editComponentListAdapter: EditComponentListAdapter?,
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
                                        editComponentListAdapterArg = editComponentListAdapter
                                ).let updateVarNameToValueMap@ {
                                        if(
                                                it.isEmpty()
                                        ) return@updateVarNameToValueMap emptyMap()
                                        it
                                }
                        }
                }

                suspend fun makeLinearTagAndKeyPairsListToVarNameToValueMap(
                        fragment: Fragment?,
                        fannelInfoMap: Map<String, String>,
                        setReplaceVariableMap: Map<String, String>?,
                        busyboxExecutor: BusyboxExecutor?,
                        frameTag: String,
                        frameTagToVerticalKeysConList: List<Pair<String, String>>,
                        frameVarNameValueMap: Map<String, String>,
                        srcTitle: String?,
                        srcCon: String?,
                        srcImage: String?,
                        bindingAdapterPosition: Int?,
                        mapListElInfo: String,
                ): List<
                        Pair<
                                String,
                                Pair<
                                        List<Pair<String, String>>,
                                        Map<String, String>
                                        >
                                >
                        > {
                        return frameTagToVerticalKeysConList.filter {
                                val curFrameTag = it.first
                                curFrameTag == frameTag
                        }.map {
                                frameTagToVerticalKeysCon ->
                                val firstVerticalTag = frameTagToVerticalKeysCon.first
                                val verticalKeysConToVarNameToValueMap = frameTagToVerticalKeysCon.second.let {
                                                verticalKeysConSrcBeforeReplaceSrc ->
                                        if(
                                                verticalKeysConSrcBeforeReplaceSrc.isEmpty()
                                        ) return@let String() to emptyMap()
                                        val verticalKeysConSrcBeforeReplace = CmdClickMap.replace(
                                                verticalKeysConSrcBeforeReplaceSrc,
                                                frameVarNameValueMap,
                                        )
                                        val verticalKeysConSrc = Template.ReplaceHolder.replaceHolder(
                                                verticalKeysConSrcBeforeReplace,
                                                srcTitle ?: String(),
                                                srcCon?: String(),
                                                srcImage?: String(),
                                                bindingAdapterPosition ?: -1,
                                        ) ?: String()
                                        val settingActionManager = SettingActionManager()
                                        val varNameToValueMap = settingActionManager.exec(
                                                fragment,
                                                fannelInfoMap,
                                                setReplaceVariableMap,
                                                busyboxExecutor,
                                                verticalKeysConSrc,
                                                "verticalTag: ${firstVerticalTag}, frameTag: ${frameTag}, mapListInfo: ${mapListElInfo}",
                                        )
                                        CmdClickMap.replace(
                                                verticalKeysConSrc,
                                                varNameToValueMap
                                        ) to varNameToValueMap
                                }
                                val verticalKeysCon = verticalKeysConToVarNameToValueMap.first
                                if(
                                        verticalKeysCon.isEmpty()
                                ) return@map String() to Pair(
                                        emptyList(),
                                        emptyMap()
                                )
                                val varNameToValueMap = verticalKeysConToVarNameToValueMap.second
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
                                verticalTag to Pair(verticalKeyPairs, varNameToValueMap)
                        }.filter {
                                it.first.isNotEmpty()
                        }
                }

                fun makeLinearFrameTagToKeyPairsList(
                        linearKeyValues: List<String>,
                        horizonVarNameToValueMap: Map<String, String>?,
                        srcTitle: String,
                        srcCon: String,
                        srcImage: String,
                        bindingAdapterPosition: Int,
                ):  List<
                        Pair<
                                String,
                                String?
                        >
                        > {
                        return linearKeyValues.map { linearFrameKeyPairsListConSrc ->
                                if(
                                        linearFrameKeyPairsListConSrc.isEmpty()
                                ) return@map Pair(String(), null)
                                val linearFrameKeyPairsListCon =
                                        Template.ReplaceHolder.replaceHolder(
                                                linearFrameKeyPairsListConSrc,
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
                                        makeLinearFrameKeyPairsList(
                                                linearFrameKeyPairsListCon,
                                        )
                                val linearFrameTag =
                                        PairListTool.getValue(
                                                linearFrameKeyPairsList,
                                                Template.EditComponentKey.TAG.key,
                                        ) ?: String()
                                Pair(
                                        linearFrameTag,
//                                        linearFrameKeyPairsList,
                                        linearFrameKeyPairsListCon
                                )
                        }.filter {
                                it.first.isNotEmpty()
                                        && !it.second.isNullOrEmpty()
                        }
                }
                suspend fun makeVerticalLinear(
                        context: Context,
                        xmlVerticalLinearLayout: LinearLayoutCompat?,
                        curExtraVerticalLinearId: Int?,
                        verticalKeyPairs: List<Pair<String, String>>,
                        verticalLinerWeight: Float,
                        verticalTag: String,
                ): LinearLayoutCompat {
                        return when(xmlVerticalLinearLayout == null) {
                                false -> xmlVerticalLinearLayout
                                else -> LinearLayoutCompat(context).apply {
                                        curExtraVerticalLinearId?.let {
                                                id = it
                                        }
                                }
                        }.apply {
                                val verticalLayout = this
                                visibility = PairListTool.getValue(
                                        verticalKeyPairs,
                                        Template.EditComponentKey.VISIBLE.key,
                                ).let {
                                                visibleStr ->
                                        Template.VisibleManager.getVisible(visibleStr)
                                }
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
//                                LinearLayoutCompat.LayoutParams(
//                                        verticalWidth,
//                                        verticalHeight
//                                )
                                val verticalLinearParam = when(xmlVerticalLinearLayout == null) {
                                        false -> xmlVerticalLinearLayout.layoutParams
                                        else -> LinearLayoutCompat.LayoutParams(
                                                verticalWidth,
                                                verticalHeight
                                        )
                                }
                                verticalLinearParam.apply {
                                        if(xmlVerticalLinearLayout != null) {
                                                width = verticalWidth
                                                height = verticalHeight
                                        }
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
                                        val overrideLayoutGravity = PairListTool.getValue(
                                                verticalKeyPairs,
                                                Template.EditComponentKey.LAYOUT_GRAVITY.key,
                                        )?.let {
                                                        gravityStr ->
                                                EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                                                        it.key == gravityStr
                                                }?.gravity
                                        } ?: Gravity.CENTER
                                        gravity = overrideLayoutGravity
                                        val bkColor = withContext(Dispatchers.IO) {
                                                PairListTool.getValue(
                                                        verticalKeyPairs,
                                                        Template.EditComponentKey.BK_COLOR.key,
                                                )?.let { colorStr ->
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
                                        val verticalLinear = this as LinearLayoutCompat.LayoutParams
                                        verticalLinear.apply setLinear@ {
                                                weight = overrideVerticalLinearWeight
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
                                                topMargin = marginData.marginTop ?: 0
                                                bottomMargin = marginData.marginBottom ?: 0
                                                marginStart = marginData.marginStart ?: 0
                                                marginEnd = marginData.marginBottom ?: 0
                                        }
//                            setMargins(ScreenSizeCalculator.toDp(context,10))
                                }
                                layoutParams = verticalLinearParam
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
                                        paddingData.paddingStart ?: 0,
                                        paddingData.paddingTop ?: 0,
                                        paddingData.paddingEnd ?: 0,
                                        paddingData.paddingBottom ?: 0,
                                )
                                orientation = LinearLayoutCompat.VERTICAL

                        }
                }


                suspend fun makeHorizonLinear(
                        context: Context,
                        xmlHorizonLinearLayout: LinearLayoutCompat?,
                        curExtraHorizonLinearId: Int?,
                        horizonKeyPairs: List<Pair<String, String>>,
                        horizonTag: String,
                ): LinearLayoutCompat {
                        return when(xmlHorizonLinearLayout == null) {
                                false -> xmlHorizonLinearLayout
                                else -> LinearLayoutCompat(context).apply {
                                        curExtraHorizonLinearId?.let {
                                                id = it
                                        }
                                }
                        }.apply {
                                visibility = PairListTool.getValue(
                                        horizonKeyPairs,
                                        Template.EditComponentKey.VISIBLE.key,
                                ).let {
                                                visibleStr ->
                                        Template.VisibleManager.getVisible(visibleStr)
                                }
                                orientation = LinearLayoutCompat.HORIZONTAL
                                val verticalWidth =
                                        PairListTool.getValue(
                                                horizonKeyPairs,
                                                Template.EditComponentKey.WIDTH.key,
                                        ).let {
                                                Template.LinearLayoutUpdater.convertWidth(
                                                        context,
                                                        it,
                                                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                                                )
                                        }
                                val verticalHeight =
                                        PairListTool.getValue(
                                                horizonKeyPairs,
                                                Template.EditComponentKey.HEIGHT.key,
                                        ).let {
                                                Template.LinearLayoutUpdater.convertWidth(
                                                        context,
                                                        it,
                                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                                )
                                        }
//                                LinearLayoutCompat.LayoutParams(
//                                        verticalWidth,
//                                        verticalHeight
//                                )
                                val verticalLinearParam = when(xmlHorizonLinearLayout == null) {
                                        false -> xmlHorizonLinearLayout.layoutParams
                                        else -> LinearLayoutCompat.LayoutParams(
                                                verticalWidth,
                                                verticalHeight
                                        )
                                }
                                verticalLinearParam.apply {
                                        if(xmlHorizonLinearLayout != null) {
                                                width = verticalWidth
                                                height = verticalHeight
                                        }
                                        tag = horizonTag
                                        val overrideLayoutGravity = PairListTool.getValue(
                                                horizonKeyPairs,
                                                Template.EditComponentKey.LAYOUT_GRAVITY.key,
                                        )?.let {
                                                        gravityStr ->
                                                EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                                                        it.key == gravityStr
                                                }?.gravity
                                        } ?: Gravity.CENTER
                                        gravity = overrideLayoutGravity
                                        val bkColor = withContext(Dispatchers.IO) {
                                                PairListTool.getValue(
                                                        horizonKeyPairs,
                                                        Template.EditComponentKey.BK_COLOR.key,
                                                )?.let { colorStr ->
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
                                        val verticalLinear = this as LinearLayoutCompat.LayoutParams
                                        verticalLinear.apply setLinear@ {
                                                val marginData = Template.MarginData(
                                                        context,
                                                        PairListTool.getValue(
                                                                horizonKeyPairs,
                                                                Template.EditComponentKey.MARGIN_TOP.key,
                                                        ),
                                                        PairListTool.getValue(
                                                                horizonKeyPairs,
                                                                Template.EditComponentKey.MARGIN_BOTTOM.key,
                                                        ),
                                                        PairListTool.getValue(
                                                                horizonKeyPairs,
                                                                Template.EditComponentKey.MARGIN_START.key,
                                                        ),
                                                        PairListTool.getValue(
                                                                horizonKeyPairs,
                                                                Template.EditComponentKey.MARGIN_END.key,
                                                        ),
                                                )
                                                topMargin = marginData.marginTop ?: 0
                                                bottomMargin = marginData.marginBottom ?: 0
                                                marginStart = marginData.marginStart ?: 0
                                                marginEnd = marginData.marginBottom ?: 0
                                        }
//                            setMargins(ScreenSizeCalculator.toDp(context,10))
                                }
                                layoutParams = verticalLinearParam
                                val overrideGravity = PairListTool.getValue(
                                        horizonKeyPairs,
                                        Template.EditComponentKey.GRAVITI.key,
                                )?.let {
                                                gravityStr ->
                                        EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                                                it.key == gravityStr
                                        }?.gravity
                                } ?: Gravity.CENTER
                                gravity = overrideGravity
                                val paddingData = Template.PaddingData(
                                        context,
                                        PairListTool.getValue(
                                                horizonKeyPairs,
                                                Template.EditComponentKey.PADDING_TOP.key,
                                        ),
                                        PairListTool.getValue(
                                                horizonKeyPairs,
                                                Template.EditComponentKey.PADDING_BOTTOM.key,
                                        ),
                                        PairListTool.getValue(
                                                horizonKeyPairs,
                                                Template.EditComponentKey.PADDING_START.key,
                                        ),
                                        PairListTool.getValue(
                                                horizonKeyPairs,
                                                Template.EditComponentKey.PADDING_END.key,
                                        ),
                                )
                                setPadding(
                                        paddingData.paddingStart ?: 0,
                                        paddingData.paddingTop ?: 0,
                                        paddingData.paddingEnd ?: 0,
                                        paddingData.paddingBottom ?: 0,
                                )

                        }
                }

                suspend  fun setHorizonLinear(
                        horizonLinearLayout: LinearLayoutCompat?,
                        linearFrameKeyPairsList: List<Pair<String, String>>
                ) {
                        withContext(Dispatchers.Main) {
                                horizonLinearLayout?.apply {
                                        visibility = PairListTool.getValue(
                                                linearFrameKeyPairsList,
                                                Template.EditComponentKey.VISIBLE.key,
                                        ).let {
                                                        visibleStr ->
                                                Template.VisibleManager.getVisible(visibleStr)
                                        }
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
                                                val overrideLayoutGravity = PairListTool.getValue(
                                                        linearFrameKeyPairsList,
                                                        Template.EditComponentKey.LAYOUT_GRAVITY.key,
                                                )?.let {
                                                                gravityStr ->
                                                        EditComponent.Template.GravityManager.Graviti.entries.firstOrNull {
                                                                it.key == gravityStr
                                                        }?.gravity
                                                } ?: Gravity.CENTER
                                                gravity = overrideLayoutGravity
                                                val marginData = Template.MarginData(
                                                        context,
                                                        PairListTool.getValue(
                                                                linearFrameKeyPairsList,
                                                                Template.EditComponentKey.MARGIN_TOP.key,
                                                        ),
                                                        PairListTool.getValue(
                                                                linearFrameKeyPairsList,
                                                                Template.EditComponentKey.MARGIN_BOTTOM.key,
                                                        ),
                                                        PairListTool.getValue(
                                                                linearFrameKeyPairsList,
                                                                Template.EditComponentKey.MARGIN_START.key,
                                                        ),
                                                        PairListTool.getValue(
                                                                linearFrameKeyPairsList,
                                                                Template.EditComponentKey.MARGIN_END.key,
                                                        ),
                                                )
                                                topMargin = marginData.marginTop  ?: 0
                                                bottomMargin = marginData.marginBottom ?: 0
                                                marginStart = marginData.marginStart ?: 0
                                                marginEnd = marginData.marginBottom ?: 0
                                                val bkColor = withContext(Dispatchers.IO) {
                                                        PairListTool.getValue(
                                                                linearFrameKeyPairsList,
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
                                                        linearFrameKeyPairsList,
                                                        Template.EditComponentKey.PADDING_TOP.key,
                                                ),
                                                PairListTool.getValue(
                                                        linearFrameKeyPairsList,
                                                        Template.EditComponentKey.PADDING_BOTTOM.key,
                                                ),
                                                PairListTool.getValue(
                                                        linearFrameKeyPairsList,
                                                        Template.EditComponentKey.PADDING_START.key,
                                                ),
                                                PairListTool.getValue(
                                                        linearFrameKeyPairsList,
                                                        Template.EditComponentKey.PADDING_END.key,
                                                ),
                                        )
                                        setPadding(
                                                paddingData.paddingStart ?: 0,
                                                paddingData.paddingTop ?: 0,
                                                paddingData.paddingEnd ?: 0,
                                                paddingData.paddingBottom ?: 0,
                                        )
                                        val overrideGravity = PairListTool.getValue(
                                                linearFrameKeyPairsList,
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