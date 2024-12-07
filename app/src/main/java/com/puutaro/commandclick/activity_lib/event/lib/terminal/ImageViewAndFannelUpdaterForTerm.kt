package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.proccess.edit_list.EditFrameMaker
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImageViewAndFannelUpdaterForTerm {
    fun update(
        activity: MainActivity,
        indexOrParentTagName: String,
        srcFragmentStr: String,
        tagNameList: List<String>,
        imageMap: Map<String, String>,
        imagePropertyMap: Map<String, String>
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
            ) as EditComponentListAdapter.EditListViewHolder
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
                val imageView = frameLayout.children.firstOrNull { view ->
                    view is AppCompatImageView
                } as? AppCompatImageView
                    ?: return@forEach
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
                    EditFrameMaker.setImageViewForDynamic(
                        imageView,
                        imageMap,
                        imagePropertyMap,
                        density,
                    )
                }
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
                editFragment?.binding?.editFooterHorizonLayout?.findViewWithTag<LinearLayoutCompat>(
                    indexOrParentTagName
                )
            }
            EditListRecyclerViewGetter.RecyclerViewFragment.WEB
                -> {
                terminalFragment.editListDialogForOrdinaryRevolver
                    ?.getActiveEditListOrdinaryDialog()
                    ?.findViewById<LinearLayoutCompat>(
                        R.id.edit_list_dialog_footer_horizon_layout
                    )
            }
        }
        val frameLayoutList = tagNameList.map {
                tagName ->
            linearLayout?.findViewWithTag<FrameLayout>(
                tagName
            )
        }
        frameLayoutList.forEach {
                frameLayout ->
            val imageView = frameLayout?.children?.firstOrNull { view ->
                view is AppCompatImageView
            } as? AppCompatImageView
                ?: return@forEach
            CoroutineScope(Dispatchers.Main).launch {
                EditFrameMaker.setImageViewForDynamic(
                    imageView,
                    imageMap,
                    imagePropertyMap,
                    density,
                )
            }
        }
    }
}