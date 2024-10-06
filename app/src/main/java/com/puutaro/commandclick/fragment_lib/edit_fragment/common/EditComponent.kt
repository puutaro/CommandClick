package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.map.CmdClickMap

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
                        }


                        fun createLabelMap(
                                labelMapCon: String?
                        ): Map<String,String> {
                                return CmdClickMap.createMap(
                                        labelMapCon,
                                        keySeparator
                                ).toMap()
                        }

                        fun makeLabel(
                                labelMap: Map<String, String>?,
//                                busyboxExecutor: BusyboxExecutor?,
                        ): String? {
                                if(
                                        labelMap.isNullOrEmpty()
                                ) return String()
                                return labelMap.get(
                                        LabelKey.SRC.key
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