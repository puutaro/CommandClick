package com.puutaro.commandclick.proccess.edit_list

import android.content.Context
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object EditTextViewSetter {
    suspend fun setForConstraint(
        context: Context?,
        fannelInfoMap: HashMap<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        tagIdMap: Map<String, Int>?,
        textView: OutlineTextView?,
        contentsKeyPairList: List<Pair<String, String>>?,
        contentsKeyPairsListCon: String?,
        settingValue: String?,
        width: Int?,
        enableClick: Boolean,
        outValue: TypedValue?,
        density: Float,
        whereForErr: String,
    ){
        if(
            context == null
            || textView == null
        ) return
        val textMap = withContext(Dispatchers.IO) {
            EditComponent.Template.TextManager.createTextMap(
                contentsKeyPairsListCon,
                settingValue,
                EditComponent.Template.typeSeparator,
            )
        }
        textView.layoutParams = ConstraintTool.setConstraintParam(
            textView.layoutParams as ConstraintLayout.LayoutParams,
            tagIdMap,
            contentsKeyPairList,
            width,
            0,
            density,
        ).let {
                param ->
            LayoutSetterTool.setMargin(
                param,
                contentsKeyPairList?.toMap(),
                density,
            )
            param
        }
        if(
            textMap.isNullOrEmpty()
        ) return
//        if(
//            textView.tag == "okText"
//        ) {
//            val param = textView.layoutParams as ConstraintLayout.LayoutParams
//            FileSystems.writeFile(
//                File(
//                    UsePath.cmdclickDefaultAppDirPath,
//                    "ltagIdMap_topToBottomInt.txt"
//                ).absolutePath,
//                listOf(
//                    "textView: ${textView.id}",
//                    "overrideTag: ${textView.tag}",
//                    "textMap: ${textMap}",
//                    "tagIdMap: ${tagIdMap}",
//                    "topToBottom: ${param.topToBottom}",
//                    "topToTop: ${param.topToTop}",
//                    "bottomToBottom: ${param.bottomToBottom}",
//                    "bottomToTop: ${param.bottomToTop}",
//                    "startToEnd: ${param.startToEnd}",
//                    "startToStart: ${param.startToStart}",
//                    "endToEnd: ${param.endToEnd}",
//                    "endToStart: ${param.endToStart}",
//                ).joinToString("\n") + "\n\n============\n\n\n"
//            )
//        }
        TextViewTool.setVisibility(
            textView,
            textMap
        )
        val overrideText = withContext(Dispatchers.IO) {
            EditComponent.Template.TextManager.makeText(
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                textMap,
                settingValue
            )
        }
//        if(textView.tag.toString() == "Speed") {
//            val param = textView.layoutParams as ConstraintLayout.LayoutParams
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultSDebugAppDirPath, "lcontents_text.txt").absolutePath,
//                listOf(
//                    "tag: ${textView.tag}",
//                    "tagIdMap: ${tagIdMap}",
//                    "startToStart: ${param.startToStart}",
//                    "startToEnd: ${param.startToEnd}",
//                    "endToEnd: ${param.endToEnd}",
//                    "endToStart: ${param.endToStart}",
//                    "topToBottom: ${param.topToBottom}",
//                    "topToTop: ${param.topToTop}",
//                    "bottomToTop: ${param.bottomToTop}",
//                    "bottomToBottom: ${param.bottomToBottom}",
//                    "topMargin: ${param.topMargin}",
//                    "bottomMargin: ${param.bottomMargin}",
//                    "width: ${param.width}",
//                    "height: ${param.height}",
//                    "textMap: ${textMap}",
//                    "textView: ${textView}",
//                    "overrideText: ${overrideText}",
//                    "settingValue: ${settingValue}",
//                ).joinToString("\n\n") + "\n\n========\n\n"
//            )
//        }
        TextViewTool.set(
            textView,
            textMap,
//            settingValue,
            overrideText,
            null,
            1,
            R.color.fill_gray,
            R.color.white,
            2,
            context.resources.getDimension(R.dimen.text_size_16),
            0f,
            EditComponent.Template.TextManager.TextStyle.NORMAL,
            EditComponent.Font.SANS_SERIF,
            enableClick,
            outValue,
            whereForErr,
            density,
        )
    }
}