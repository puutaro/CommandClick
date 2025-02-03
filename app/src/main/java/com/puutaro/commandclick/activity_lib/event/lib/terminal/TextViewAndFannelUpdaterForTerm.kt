package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.proccess.edit_list.EditConstraintFrameMaker
import com.puutaro.commandclick.proccess.edit_list.TextViewTool
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

object TextViewAndFannelUpdaterForTerm {
    fun update(
        activity: MainActivity,
        indexOrParentTagName: String,
        srcFragmentStr: String,
        tagNameList: List<String>,
        updateText: String,
        overrideTextMap: Map<String, String>?,
//        textPropertyMap: Map< String, String>?,
        isSave: Boolean,
    ) {
        if(
           indexOrParentTagName.isEmpty()
           || tagNameList.isEmpty()
        ) return
        val editListIndex = try {
            val editListSrc = indexOrParentTagName.toInt()
            when(editListSrc < 0){
                true -> null
                else -> editListSrc
            }
        } catch (e: Exception){
            null
        }
        val terminalFragment = TargetFragmentInstance.getCurrentTerminalFragment(
            activity
        ) ?: return
        val editListRecyclerView = EditListRecyclerViewGetter.get(
            terminalFragment,
            srcFragmentStr
        ) ?: return
        val editConstraintListAdapter =
            editListRecyclerView.adapter as EditConstraintListAdapter
        if(editListIndex is Int) {
            val holder = editListRecyclerView.findViewHolderForAdapterPosition(
                editListIndex
            ) as EditConstraintListAdapter.EditListViewHolder
            val materialCardView = holder.materialCardView
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lupdateText.txt").absolutePath,
//                listOf(
////                    "tagName: ${tagName}",
////                    "tag: ${textView.tag}",
////                    "textView.text: ${textView.text}",
//                    "updateText: ${updateText}",
//                    "overrideTextMap: ${overrideTextMap}",
//                ).joinToString("\n")
//            )
            val directTagToTextView = getTextView(
                tagNameList,
                materialCardView,
            )?: return

            val tagName = directTagToTextView.first
            val textView = directTagToTextView.second
                ?: return


            val lineMap = editConstraintListAdapter.lineMapList.get(editListIndex)
            holder.srcTitle = lineMap.get(
                ListSettingsForEditList.MapListPathManager.Key.SRC_TITLE.key
            ) ?: String()
            holder.srcCon = lineMap.get(
                ListSettingsForEditList.MapListPathManager.Key.SRC_CON.key
            ) ?: String()
            holder.srcImage = lineMap.get(
                ListSettingsForEditList.MapListPathManager.Key.SRC_IMAGE.key
            ) ?: String()
            val keyPairListConMap = runBlocking {
                holder.getKeyPairListConMap()
            }
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "editListIndex.txt").absolutePath,
//                listOf(
//                    "textView: ${textView?.text}",
//                    "updateText: ${updateText}",
//                    "holder.keyPairListConMap: ${keyPairListConMap}",
//                    "",
//                ).joinToString("\n")
//            )
            UpdateAndSaveMainFannel.updateAndSave(
                editConstraintListAdapter,
                keyPairListConMap,
                tagName,
                holder.srcTitle,
                holder.srcCon,
                holder.srcImage,
                editListIndex,
                updateText,
                overrideTextMap,
//                    textPropertyMap,
                textView,
                isSave,
            )
            return
        }

//        val editToolbarTag = activity.getString(
//            R.string.edit_toolbar_tag
//        )
        val srcFragmentEnum = EditListRecyclerViewGetter.RecyclerViewFragment.entries.firstOrNull {
            it.frag == srcFragmentStr
        } ?: return
        val constraintLayout = when(srcFragmentEnum){
            EditListRecyclerViewGetter.RecyclerViewFragment.EDIT
            -> {
                val cmdEditFragmentTag = TargetFragmentInstance.getCmdEditFragmentTag(
                    activity
                )
                val editFragment = TargetFragmentInstance.getCurrentBottomFragment(
                    activity,
                    cmdEditFragmentTag
                ).let {
                    fragment ->
                    if(
                        fragment !is EditFragment
                    ) return@let null
                    fragment
                }
                editFragment?.binding?.editListFooterConstraintLayout?.findViewWithTag<ConstraintLayout>(
                    indexOrParentTagName
                )
            }
            EditListRecyclerViewGetter.RecyclerViewFragment.WEB
            -> {
                terminalFragment.editListDialogForOrdinaryRevolver
                    ?.getActiveEditListOrdinaryDialog()?.findViewById<ConstraintLayout>(
                    R.id.edit_list_dialog_footer_constraint_layout
                )
            }
        }
        val noSignIndex = -1
        val tagToTextView = getTextView(
            tagNameList,
            constraintLayout,
        )?: return
        UpdateAndSaveMainFannel.updateAndSave(
            editConstraintListAdapter,
            editConstraintListAdapter.footerKeyPairListConMap,
            String(),
            String(),
            String(),
            String(),
            noSignIndex,
            updateText,
            overrideTextMap,
//                textPropertyMap,
            tagToTextView.second,
            isSave,
        )
    }

    private fun getTextView(
        tagNameList: List<String>,
        viewGroup: ViewGroup?,
    ): Pair<String, OutlineTextView?>? {
        if (
            viewGroup == null
        ) return null
        tagNameList.forEach { tagName ->
            val textViewEntry = try {
                viewGroup.findViewWithTag<OutlineTextView>(
                    tagName
                )
            } catch (e: Exception) {
                null
            } ?: return@forEach
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lupdateText_get00.txt").absolutePath,
//                listOf(
//                    "tagName: ${tagName}",
//                    "textViewEntry: ${textViewEntry}",
//                ).joinToString("\n")
//            )
            return tagName to textViewEntry
        }
        tagNameList.forEach { tagName ->
            val frameLayoutEntry = try {
                viewGroup.findViewWithTag<FrameLayout>(
                    tagName
                )
            } catch (e: Exception) {
                null
            } ?: return@forEach
            val textView = frameLayoutEntry.children.firstOrNull { view ->
                view is OutlineTextView
            } as? OutlineTextView
                ?: return@forEach
            return tagName to textView
        }
        return null

//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lupdateTextGet00.txt").absolutePath,
//                    listOf(
//                    "tagName: ${tagName}",
//                    "pair: ${pair}",
//                    ).joinToString("\n")
//                )
//                pair
    }

    object UpdateAndSaveMainFannel {
        fun updateAndSave(
            editConstraintListAdapter: EditConstraintListAdapter,
            keyPairListConMap: Map<String, String?>,
            tagName: String,
            srcTitle: String,
            srcCon: String,
            srcImage: String,
            editListIndex: Int,
            updateText: String,
            overrideTextMap: Map<String, String>?,
//            textPropertyMap: Map< String, String>?,
            textView: OutlineTextView?,
            isSave: Boolean,
        ) {
            val linearFrameKeyPairsListConSrc = keyPairListConMap.get(
                tagName
            )
            val linearFrameKeyPairsListCon = EditComponent.Template.ReplaceHolder.replaceHolder(
                linearFrameKeyPairsListConSrc,
                srcTitle,
                srcCon,
                srcImage,
                editListIndex,
            )
            val linearFrameKeyPairsList = EditComponent.AdapterSetter.makeLinearFrameKeyPairsList(
                linearFrameKeyPairsListCon,
            )
            val textMap = PairListTool.getValue(
                linearFrameKeyPairsList,
                EditComponent.Template.EditComponentKey.TEXT.key
            ).let {
                CmdClickMap.createMap(
                    it,
                    EditComponent.Template.keySeparator,
                ).toMap()
            }
//            val text = EditComponent.Template.TextManager.makeText(
//                editConstraintListAdapter.fannelInfoMap,
//                editConstraintListAdapter.setReplaceVariableMap,
//                editConstraintListAdapter.busyboxExecutor,
//                textMap,
//                updateText
//            ).let {
//                if(
//                    it.isNullOrEmpty()
//                ) return@let updateText
//                it
//            }
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "editListIndex_updateAndSave.txt").absolutePath,
//                listOf(
//                    "textView: ${textView?.text}",
//                    "updateText: ${updateText}",
//                    "tagName: ${tagName}",
//                    "keyPairListConMap: ${keyPairListConMap}",
//                    "linearFrameKeyPairsListCon: ${linearFrameKeyPairsListCon}",
//                    "linearFrameKeyPairsList: ${linearFrameKeyPairsList}",
//                    "textMap: ${textMap}",
//                    "overrideTextMap: ${overrideTextMap}"
//                ).joinToString("\n\n")
//            )

            textView?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    val context = textView.context
                    val density = withContext(Dispatchers.Main){
                        ScreenSizeCalculator.getDensity(context)
                    }
                    val outValue = withContext(Dispatchers.IO) {
                        val outValueSrc = TypedValue()
                        context.theme?.resolveAttribute(
                            android.R.attr.selectableItemBackground,
                            outValueSrc,
                            true
                        )
                        outValueSrc
                    }
                    TextViewTool.setVisibility(
                        textView,
                        textMap,
                    )
                    TextViewTool.set(
                        textView,
                        textMap,
                        updateText,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        outValue,
                        "UpdateAndSaveMainFannel.updateAndSave",
                        density,
                    )
//                    EditConstraintFrameMaker.setTextViewByDynamic(
//                        it,
//                        overrideTextMap,
//                        updateText,
////                        text,
//                    )
                }
            }
            execUpdateAndSave(
                textView,
                editConstraintListAdapter,
                tagName,
                updateText,
                isSave,
            )
        }

        private fun execUpdateAndSave(
            textView: AppCompatTextView?,
            editConstraintListAdapter: EditConstraintListAdapter,
            tagName: String,
            updateText: String,
            isSave: Boolean,
        ) {
            val isNotUpdate =
                editConstraintListAdapter.getTotalSettingValMap().get(tagName).isNullOrEmpty()
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lUpdatetextView.txt").absolutePath,
//                listOf(
//                    "totalSettingValMap: ${editComponentListAdapter.totalSettingValMap}",
//                    "tagName: ${tagName}",
//                    "isNotUpdate: ${isNotUpdate}",
//                ).joinToString("\n")
//            )
            if (
                isNotUpdate
            ) return
            editConstraintListAdapter.updateMainFannelList(
                tagName,
                updateText
            )
//            editConstraintListAdapter.getTotalSettingValMap().get(tagName).let {
//                textView?.setAutofillHints(updateText)
//            }
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lUpdatetextView_saved00.txt").absolutePath,
//                listOf(
//                    "totalSettingValMap: ${editComponentListAdapter.totalSettingValMap}",
//                    "tagName: ${tagName}",
//                    "isUpdate: ${isNotUpdate}",
//                    "fannelContentsList: ${editComponentListAdapter.fannelContentsList}",
//                    "isSave: ${isSave}",
//                ).joinToString("\n")
//            )
            if (!isSave) return
            editConstraintListAdapter.saveFannelCon()
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lUpdatetextView_saved.txt").absolutePath,
//                listOf(
//                    "totalSettingValMap: ${editComponentListAdapter.totalSettingValMap}",
//                    "tagName: ${tagName}",
//                    "isUpdate: ${isNotUpdate}",
//                    "fannelContentsList: ${editComponentListAdapter.fannelContentsList}"
//                ).joinToString("\n")
//            )
        }
    }
}