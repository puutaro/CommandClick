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
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.str.PairListTool

object EditTextAndFannelUpdaterForTerm {
    fun update(
        activity: MainActivity,
        indexOrParentTagName: String,
        srcFragmentStr: String,
        tagName: String,
        updateText: String,
        isSave: Boolean,
    ) {
        if(
           indexOrParentTagName.isEmpty()
           || tagName.isEmpty()
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
            val frameLayout = materialCardView.findViewWithTag<FrameLayout>(
                tagName
            )
            val textView = frameLayout.children.firstOrNull {
                view ->
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

            UpdateAndSaveMainFannel.updateAndSave(
                editComponentListAdapter,
                holder.keyPairListConMap,
                tagName,
                holder.srcTitle,
                holder.srcCon,
                holder.srcImage,
                editListIndex,
                updateText,
                textView,
                isSave,
            )
            return
        }

//        val editToolbarTag = activity.getString(
//            R.string.edit_toolbar_tag
//        )
        val srcFragmentEnum = EditListRecyclerViewGetter.RecyclerViewFragment.values().firstOrNull {
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
        val frameLayout = linearLayout?.findViewWithTag<FrameLayout>(
            tagName
        )
        val textView = frameLayout?.children?.firstOrNull {
                view ->
            view is OutlineTextView
        } as? OutlineTextView
        UpdateAndSaveMainFannel.updateAndSave(
            editComponentListAdapter,
            editComponentListAdapter.footerKeyPairListConMap,
            tagName,
            tagName,
            tagName,
            tagName,
            noSignIndex,
            updateText,
            textView,
            isSave,
        )
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
            textView?.text = text
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