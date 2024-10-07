package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ClickSettingsForListIndex
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

object EditComponent {
        object Template {

                const val sectionSeparator = ','
                const val typeSeparator = '|'
                const val keySeparator = '?'
                const val valueSeparator = '&'

                val switchOn = "ON"

                object ReplaceHolder {
                        fun replaceHolder(
                                con: String?,
                                srcLabel: String,
                                srcCon: String,
                        ): String? {
                                if(con == null) return null
                                return con.replace(
                                        SrcReplaceHolders.SRC_LABEL.key,
                                        srcLabel
                                ).replace(
                                        SrcReplaceHolders.SRC_CON.key,
                                        srcCon
                                )

                        }

                        enum class SrcReplaceHolders(val key: String){
                                SRC_LABEL("\${SRC_LABEL}"),
                                SRC_CON("\${SRC_CON}"),
                                SETTING_VALUE("\${SETTING_VALUE}"),
                        }
                }

                enum class LayoutKey(val key: String){
                        FRAME("frame"),
                        FOOTER("footer"),
                        LINEAR("linear"),
                }

//                val blockLayoutKeyList = listOf(
//                        LayoutKey.FRAME.key,
//                        LayoutKey.FOOTER.key,
//                )

                enum class EditComponentKey(val key: String){
                        TAG("tag"),
                        LABEL_TAG("labelTag"),
                        IMAGE_TAG("imageTag"),
                        LABEL("label"),
                        SRC_CON("srcCon"),
                        SRC_LABEL("srcLabel"),
                        IMAGE_PATH("imagePath"),
                        SET_IMAGE_TO_TAGS("setImageToTags"),
                        SET_TEXT_TO_TAGS("setTextToTags"),
                        SET_SETTING_VALS_TO_TAGS("setSettingValsToTags"),
                        SET_CMD_VALS_TO_TAGS("setCmdValsToTags"),
                        SET_IMAGE_TO_BTN_TAGS("setImageToBtnTags"),
                        SET_TEXT_TO_BTN_TAGS("setTextToBtnTags"),
                        SET_SUGGEST_TO_BTN_TAGS("setSuggestToBtnTags"),
                        DISABLE_TEXT_SELECT("disableTextSelect"),
                        TEXT_PREFIXS("textPrefixs"),
                        TEXT_SUFFIXS("textSuffixs"),
                        ON_SAVE("onSave"),
                        IS_CONSEC("isConsec"),
                        TEXT_SIZE("textSize"),
                        DISABLE_KEYBOARD_HIDDEN("disableKeyboardHidden"),
                        TEXT_COLOR("textColor"),
                        STROKE_COLOR("strokeColor"),
                        STROKE_WIDTH("strokeWidth"),
                        HEIGHT("height"),
                        TEXT_ALPHA("textAlpha"),
                        IMAGE_ALPHA("imageAlpha"),
                        IMAGE_SCALE("imageScale")
                }

                object OnSaveManager {
//                        fun makeClickConfigMap(
//                                listIndexConfigMap: Map<String, String>?
//                        ): Map<String, String> {
//                                return CmdClickMap.createMap(
//                                        listIndexConfigMap?.get(ListIndexEditConfig.ListIndexConfigKey.LIST.key),
//                                        '|'
//                                ).toMap()
//                        }


//                        fun howEnableClickUpdate(
//                                clickConfigMap: Map<String, String>?
//                        ): Boolean {
//                                return clickConfigMap?.get(ClickSettingsForListIndex.ClickSettingKey.ENABLE_UPDATE.key)?.let {
//                                        QuoteTool.trimBothEdgeQuote(it)
//                                } == ClickSettingsForListIndex.OnDisableUpdateValue.ON.name
//                        }
//
//                        fun howEnableClickSave(
//                                clickConfigMap: Map<String, String>?
//                        ): Boolean {
//                                return clickConfigMap?.get(ClickSettingsForListIndex.ClickSettingKey.ON_SCRIPT_SAVE.key)
//                                        ?.let {
//                                                QuoteTool.trimBothEdgeQuote(it)
//                                        } == ClickSettingsForListIndex.OnScriptSave.ON.name
//                        }
                }


                enum class ImageScale(
                        val str: String,
                        val scale:  ImageView.ScaleType
                ){
                        FIT_CENTER("fitCenter", ImageView.ScaleType.FIT_CENTER),
                        FIT_XY("fitXy", ImageView.ScaleType.FIT_XY),
                        CENTER_CROP("centerCrop", ImageView.ScaleType.CENTER_CROP),
                }

                object LabelManager {
                        enum class LabelKey(val key: String) {
//                                PREFIX("prefix"),
//                                SUFFIX("suffix"),
//                                FILTER_SHELL_PATH("filterShellPath"),
                                SRC("src"),
                                SETTING_VALUE("settingValue"),
                        }


                        fun createLabelMap(
                                labelMapCon: String?,
                                settingValue: String?,
                        ): Map<String,String> {
                                val labelMapSrc = CmdClickMap.createMap(
                                        labelMapCon,
                                        keySeparator
                                ).toMap()
                                return when(settingValue.isNullOrEmpty()){
                                        true -> labelMapSrc
                                        else -> labelMapSrc + mapOf(
                                                LabelKey.SETTING_VALUE.key to settingValue
                                        )
                                }
                        }

                        fun makeLabel(
                                labelMap: Map<String, String>?,
                                settingValue: String?,
//                                busyboxExecutor: BusyboxExecutor?,
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
                                        labelMap.isNullOrEmpty()
                                ) return String()
                                return labelMap.get(
                                        LabelKey.SRC.key
                                )?.replace(
                                        ReplaceHolder.SrcReplaceHolders.SETTING_VALUE.key,
                                        settingValue ?: String()
                                )
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

//    const val idDuration = 1000


}