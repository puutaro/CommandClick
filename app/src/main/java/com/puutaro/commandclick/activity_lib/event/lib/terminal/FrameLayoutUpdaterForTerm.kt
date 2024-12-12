package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.proccess.edit_list.EditConstraintFrameMaker
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FrameLayoutUpdaterForTerm {
    fun update(
        activity: MainActivity,
        indexOrParentTagName: String,
        srcFragmentStr: String,
        tagNameList: List<String>,
        frameKeyPairList: List<Pair<String, String>>?
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
        val density = ScreenSizeCalculator.getDensity(activity)
        if(editListIndex is Int) {
            val holder = editListRecyclerView.findViewHolderForAdapterPosition(
                editListIndex
            ) as EditConstraintListAdapter.EditListViewHolder
            val materialCardView = holder.materialCardView
            val frameLayoutList = tagNameList.map {
                    tagName ->
                materialCardView.findViewWithTag<FrameLayout>(
                    tagName
                )
            }
            frameLayoutList.forEach {
                    frameLayout ->
                if(frameLayout == null) return@forEach
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "editListIndex.txt").absolutePath,
//                listOf(
//                    "textView: ${textView?.text}",
//                    "updateText: ${updateText}",
//                    "holder.keyPairListConMap: ${holder.keyPairListConMap}",
//                    "",
//                ).joinToString("\n")
//            )
                CoroutineScope(Dispatchers.Main).launch {
                    EditConstraintFrameMaker.setButtonFrameLayoutByDynamic(
                        activity,
                        frameLayout,
                        frameKeyPairList,
                        density
                    )
                }
            }
            return
        }
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
                editFragment?.binding?.editListFooterConstraintLayout
            }
            EditListRecyclerViewGetter.RecyclerViewFragment.WEB
                -> {
                terminalFragment.editListDialogForOrdinaryRevolver
                    ?.getActiveEditListOrdinaryDialog()
                    ?.findViewById<ConstraintLayout>(
                        R.id.edit_list_dialog_footer_constraint_layout
                    )
            }
        }
        val frameLayoutList = tagNameList.map {
                tagName ->
            constraintLayout?.findViewWithTag<FrameLayout>(
                tagName
            )
        }
        frameLayoutList.forEach {
                frameLayout ->
           if(frameLayout == null) return@forEach
            CoroutineScope(Dispatchers.Main).launch {
                EditConstraintFrameMaker.setButtonFrameLayoutByDynamic(
                    activity,
                    frameLayout,
                    frameKeyPairList,
                    density
                )
            }
        }
    }
}