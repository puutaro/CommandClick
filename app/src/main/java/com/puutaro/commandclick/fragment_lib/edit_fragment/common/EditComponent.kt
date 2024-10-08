package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.text.libs.FilterAndMapModule
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool

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
                                srcText: String,
                                srcCon: String,
                        ): String? {
                                if(con == null) return null
                                return con.replace(
                                        SrcReplaceHolders.SRC_TITLE.key,
                                        srcText
                                ).replace(
                                        SrcReplaceHolders.SRC_CON.key,
                                        srcCon
                                )

                        }

                        enum class SrcReplaceHolders(val key: String){
                                SHELL_SRC("\${SHELL_SRC}"),
                                SRC_TITLE("\${SRC_TITLE}"),
                                SRC_CON("\${SRC_CON}"),
                                SRC_STR("\${SRC_STR}"),
                                SETTING_VALUE("\${SETTING_VALUE}"),
                        }
                }

                enum class LayoutKey(val key: String){
                        FRAME("frame"),
                        FOOTER("footer"),
                        LINEAR("linear"),
                }

                enum class EditComponentKey(val key: String){
                        TAG("tag"),
//                        TEXT_TAG("textTag"),
                        IMAGE_TAG("imageTag"),
                        TEXT("text"),
                        TEXT_PROPERTY("textProperty"),
                        IMAGE_PATH("imagePath"),
                        SET_IMAGE_TO_TAGS("setImageToTags"),
                        SET_TEXT_TO_TAGS("setTextToTags"),
                        SET_SETTING_VALS_TO_TAGS("setSettingValsToTags"),
                        SET_CMD_VALS_TO_TAGS("setCmdValsToTags"),
                        SET_IMAGE_TO_BTN_TAGS("setImageToBtnTags"),
                        SET_TEXT_TO_BTN_TAGS("setTextToBtnTags"),
                        SET_SUGGEST_TO_BTN_TAGS("setSuggestToBtnTags"),
//                        TEXT_PREFIXS("textPrefixs"),
//                        TEXT_SUFFIXS("textSuffixs"),
//                        TEXT_MAX_LINES("textMaxLines"),
                        ON_SAVE("onSave"),
                        IS_CONSEC("isConsec"),
//                        TEXT_SIZE("textSize"),
                        DISABLE_KEYBOARD_HIDDEN("disableKeyboardHidden"),
//                        TEXT_COLOR("textColor"),
//                        STROKE_COLOR("strokeColor"),
//                        STROKE_WIDTH("strokeWidth"),
                        HEIGHT("height"),
//                        TEXT_ALPHA("textAlpha"),
                        IMAGE_ALPHA("imageAlpha"),
                        IMAGE_SCALE("imageScale")
                }

                object TextPropertyManager {

                        fun makeTextPropertyMap(
                                textPropertyCon: String?
                        ): Map<String, String>? {
                                if(
                                        textPropertyCon.isNullOrEmpty()
                                ) return null
                                return CmdClickMap.createMap(
                                        textPropertyCon,
                                        keySeparator,
                                ).toMap()
                        }
                        enum class Property(val key: String){
                                TAG("tag"),
                                MAX_LINES("maxLines"),
                                SIZE("size"),
                                COLOR("color"),
                                STROKE_COLOR("strokeColor"),
                                STROKE_WIDTH("strokeWidth"),
                                ALPHA("alpha"),
//                                DISABLE_TEXT_SELECT("disableTextSelect"),
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
//                                if(displayLabelSrc.contains("\${SRC_STR}")) {
//                                        FileSystems.updateFile(
//                                                File(
//                                                        UsePath.cmdclickDefaultAppDirPath,
//                                                        "ldispalyText.txt"
//                                                ).absolutePath,
//                                                listOf(
//                                                        "displayLabelSrc: ${displayLabelSrc}",
//                                                        "srcStrBeforeRemove: ${
//                                                                labelMap.get(
//                                                                        LabelKey.SRC_STR.key
//                                                                )?.replace(
//                                                                        ReplaceHolder.SrcReplaceHolders.SETTING_VALUE.key,
//                                                                        settingValue
//                                                                )
//                                                        }",
//
//                                                        ).joinToString("\n")
//                                        )
//                                }
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



}