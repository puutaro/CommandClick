package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.proccess.edit_list.EditFrameMaker
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object TextViewAndFannelUpdaterForTerm {
    fun update(
        activity: MainActivity,
        indexOrParentTagName: String,
        srcFragmentStr: String,
        tagNameList: List<String>,
        updateText: String,
        textPropertyMap: Map< String, String>?,
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
        val editComponentListAdapter =
            editListRecyclerView.adapter as EditComponentListAdapter
        if(editListIndex is Int) {
            val holder = editListRecyclerView.findViewHolderForAdapterPosition(
                editListIndex
            ) as EditComponentListAdapter.EditListViewHolder
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
                val textView = frameLayout.children.firstOrNull { view ->
                    view is OutlineTextView
                } as? OutlineTextView

                val lineMap = editComponentListAdapter.lineMapList.get(editListIndex)
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
                UpdateAndSaveMainFannel.updateAndSave(
                    editComponentListAdapter,
                    holder.keyPairListConMap,
                    tagName,
                    holder.srcTitle,
                    holder.srcCon,
                    holder.srcImage,
                    editListIndex,
                    updateText,
                    textPropertyMap,
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
        val linearLayout = when(srcFragmentEnum){
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
                editFragment?.binding?.editFooterLinearlayout?.findViewWithTag<LinearLayoutCompat>(
                    indexOrParentTagName
                )
            }
            EditListRecyclerViewGetter.RecyclerViewFragment.WEB
            -> {
                terminalFragment.editListDialog?.findViewById<LinearLayoutCompat>(
                    R.id.edit_list_dialog_footer_linearlayout
                )
            }
        }
        val noSignIndex = -1
        val frameLayoutList = tagNameList.map {
            tagName ->
            linearLayout?.findViewWithTag<FrameLayout>(
                tagName
            )
        }
        frameLayoutList.forEach {
            frameLayout ->
            val textView = frameLayout?.children?.firstOrNull { view ->
                view is OutlineTextView
            } as? OutlineTextView
            UpdateAndSaveMainFannel.updateAndSave(
                editComponentListAdapter,
                editComponentListAdapter.footerKeyPairListConMap,
                String(),
                String(),
                String(),
                String(),
                noSignIndex,
                updateText,
                textPropertyMap,
                textView,
                isSave,
            )
        }
    }

    object UpdateAndSaveMainFannel {
        fun updateAndSave(
            editComponentListAdapter: EditComponentListAdapter,
            keyPairListConMap: Map<String, String?>,
            tagName: String,
            srcTitle: String,
            srcCon: String,
            srcImage: String,
            editListIndex: Int,
            updateText: String,
            textPropertyMap: Map< String, String>?,
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
            val linearFrameKeyPairsList = EditComponentListAdapter.makeLinearFrameKeyPairsList(
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
                editComponentListAdapter.fannelInfoMap,
                editComponentListAdapter.setReplaceVariableMap,
                editComponentListAdapter.busyboxExecutor,
                textMap,
                updateText
            )
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "editListIndex_updateAndSave.txt").absolutePath,
//                listOf(
//                    "textView: ${textView?.text}",
//                    "updateText: ${updateText}",
//                    "updateText: ${text}",
//                    "tagName: ${tagName}",
//                    "keyPairListConMap: ${keyPairListConMap}",
//                    "linearFrameKeyPairsListCon: ${linearFrameKeyPairsListCon}",
//                    "linearFrameKeyPairsList: ${linearFrameKeyPairsList}",
//                    "textMap: ${textMap}",
//                ).joinToString("\n\n")
//            )
            textView?.let {
                CoroutineScope(Dispatchers.Main).launch {
                    EditFrameMaker.setCaptionByDynamic(
                        it,
                        textPropertyMap,
                        text,
                    )
                }
            }
//            textView?.text = text
            execUpdateAndSave(
                textView,
                editComponentListAdapter,
                tagName,
                updateText,
                isSave,
            )
        }

        private fun execUpdateAndSave(
            textView: AppCompatTextView?,
            editComponentListAdapter: EditComponentListAdapter,
            tagName: String,
            updateText: String,
            isSave: Boolean,
        ) {
            val isNotUpdate =
                editComponentListAdapter.totalSettingValMap.get(tagName).isNullOrEmpty()
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
            editComponentListAdapter.updateMainFannelList(
                tagName,
                updateText
            )
            editComponentListAdapter.totalSettingValMap.get(tagName).let {
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
            editComponentListAdapter.saveFannelCon()
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