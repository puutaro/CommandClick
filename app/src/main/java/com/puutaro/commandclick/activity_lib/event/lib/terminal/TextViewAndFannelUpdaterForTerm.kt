package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.proccess.edit_list.EditConstraintFrameMaker
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
            val tagNameToFrameLayoutList = tagNameList.map {
                tagName ->
                tagName to materialCardView.findViewWithTag<FrameLayout>(
                    tagName
                )
            }
            tagNameToFrameLayoutList.forEach {
                    tagNameToFrameLayout ->
                val tagName = tagNameToFrameLayout.first
                val frameLayout = tagNameToFrameLayout.second
                    ?: return@forEach
                val textView = frameLayout.children.firstOrNull { view ->
                    view is OutlineTextView
                } as? OutlineTextView

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
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "editListIndex.txt").absolutePath,
//                listOf(
//                    "textView: ${textView?.text}",
//                    "updateText: ${updateText}",
//                    "holder.keyPairListConMap: ${holder.keyPairListConMap}",
//                    "",
//                ).joinToString("\n")
//            )
                val keyPairListConMap = runBlocking {
                    holder.getKeyPairListConMap()
                }
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
            }
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
        val frameLayoutList = tagNameList.map {
            tagName ->
            constraintLayout?.findViewWithTag<FrameLayout>(
                tagName
            )
        }
        frameLayoutList.forEach {
            frameLayout ->
            if(frameLayout == null) return@forEach
            val textView = frameLayout.children.firstOrNull { view ->
                view is OutlineTextView
            } as? OutlineTextView
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
                textView,
                isSave,
            )
        }
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
            val text = EditComponent.Template.TextManager.makeText(
                editConstraintListAdapter.fannelInfoMap,
                editConstraintListAdapter.setReplaceVariableMap,
                editConstraintListAdapter.busyboxExecutor,
                textMap,
                updateText
            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "editListIndex_updateAndSave.txt").absolutePath,
//                listOf(
//                    "textView: ${textView?.text}",
//                    "updateText: ${updateText}",
//                    "text: ${text}",
//                    "tagName: ${tagName}",
//                    "keyPairListConMap: ${keyPairListConMap}",
//                    "linearFrameKeyPairsListCon: ${linearFrameKeyPairsListCon}",
//                    "linearFrameKeyPairsList: ${linearFrameKeyPairsList}",
//                    "textMap: ${textMap}",
//                    "textPropertyMap: ${textPropertyMap}",
//                    "overrideTextMap: ${overrideTextMap}"
//                ).joinToString("\n\n")
//            )
            textView?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    EditConstraintFrameMaker.setTextViewByDynamic(
                        it,
                        overrideTextMap,
                        updateText,
                        text,
                    )
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
                editConstraintListAdapter.totalSettingValMap.get(tagName).isNullOrEmpty()
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
            editConstraintListAdapter.totalSettingValMap.get(tagName).let {
                textView?.setAutofillHints(updateText)
            }
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