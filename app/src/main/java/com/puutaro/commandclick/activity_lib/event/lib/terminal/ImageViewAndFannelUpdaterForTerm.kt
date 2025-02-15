package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionManager
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit_list.EditConstraintFrameMaker
import com.puutaro.commandclick.proccess.edit_list.ImageViewTool
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object ImageViewAndFannelUpdaterForTerm {

    private val objectName = this::class.java.name

    fun update(
        activity: MainActivity,
        fannelPath: String,
        fannelState: String,
        indexOrParentTagName: String,
        srcFragmentStr: String,
        tagNameList: List<String>,
        imageMap: Map<String, String>,
//        imagePropertyMap: Map<String, String>,
        imageAcCon: String,
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
            val imageView = getImageView(
                tagNameList,
                materialCardView
            )?.second ?: return
//            val frameLayoutList = tagNameList.map {
//                    tagName ->
//                materialCardView.findViewWithTag<FrameLayout>(
//                    tagName
//                )
//            }
//            frameLayoutList.forEach {
//                    frameLayout ->
//                if(frameLayout == null) return@forEach
//                val imageView = frameLayout.children.firstOrNull { view ->
//                    view is AppCompatImageView
//                } as? AppCompatImageView
//                    ?: return@forEach
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "editListIndex.txt").absolutePath,
//                listOf(
//                    "textView: ${textView?.text}",
//                    "updateText: ${updateText}",
//                    "holder.keyPairListConMap: ${holder.keyPairListConMap}",
//                    "",
//                ).joinToString("\n")
//            )
            execUpdate(
                activity,
                fannelPath,
                fannelState,
                terminalFragment,
                imageView,
                imageMap,
                editListRecyclerView.adapter as EditConstraintListAdapter,
                imageAcCon,
                density
            )
//            CoroutineScope(Dispatchers.Main).launch {
//
//                EditConstraintFrameMaker.setImageViewForDynamic(
//                    imageView,
//                    imageMap,
////                        imagePropertyMap,
//                    density,
//                )
//            }
//            CoroutineScope(Dispatchers.IO).launch {
//                execImageAction(
//                    activity,
//                    fannelPath,
//                    fannelState,
//                    terminalFragment,
//                    imageView,
//                    editListRecyclerView.adapter as EditConstraintListAdapter,
//                    imageAcCon,
//                )
//            }
//            }
            return
        }

        val editToolbarTag = activity.getString(
            R.string.edit_toolbar_tag
        )
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
//        val frameLayoutList = tagNameList.map {
//                tagName ->
//            constraintLayout?.findViewWithTag<FrameLayout>(
//                tagName
//            )
//        }
        val imageView = getImageView(
            tagNameList,
            constraintLayout
        )?.second ?: return
//        frameLayoutList.forEach {
//                frameLayout ->
//            val imageView = frameLayout?.children?.firstOrNull { view ->
//                view is AppCompatImageView
//            } as? AppCompatImageView
//                ?: return@forEach
        execUpdate(
            activity,
            fannelPath,
            fannelState,
            terminalFragment,
            imageView,
            imageMap,
            null,
            imageAcCon,
            density
        )
//            CoroutineScope(Dispatchers.Main).launch {
//                EditConstraintFrameMaker.setImageViewForDynamic(
//                    imageView,
//                    imageMap,
////                    imagePropertyMap,
//                    density,
//                )
//            }
//            CoroutineScope(Dispatchers.IO).launch {
//                execImageAction(
//                    activity,
//                    fannelPath,
//                    fannelState,
//                    terminalFragment,
//                    imageView,
//                    editListRecyclerView.adapter as EditConstraintListAdapter,
//                    imageAcCon,
//                )
//            }
//        }
    }

    private fun execUpdate(
        activity: MainActivity,
        fannelPath: String,
        fannelState: String,
        terminalFragment: TerminalFragment?,
        imageView: AppCompatImageView,
        imageMap: Map<String, String>?,
        editConstraintListAdapter: EditConstraintListAdapter?,
        imageAcCon: String,
        density: Float
    ){
        CoroutineScope(Dispatchers.Main).launch {
            val outValue = withContext(Dispatchers.IO) {
                val outValueSrc = TypedValue()
                activity.theme?.resolveAttribute(
                    android.R.attr.selectableItemBackground,
                    outValueSrc,
                    true
                )
                outValueSrc
            }
            val requestBuilderSrc = withContext(Dispatchers.IO) {
                Glide.with(activity)
                    .asDrawable()
                    .sizeMultiplier(0.1f)
            }
            ImageViewTool.setVisibility(
                imageView,
                imageMap
            )
            ImageViewTool.set(
                imageView,
                imageMap,
                null,
                ImageView.ScaleType.FIT_CENTER,
                null,
                outValue,
                requestBuilderSrc,
                density,
                "ImageViewAndFannelUpdaterForTerm.update"
            )
//            EditConstraintFrameMaker.setImageViewForDynamic(
//                imageView,
//                imageMap,
////                        imagePropertyMap,
//                density,
//            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            execImageAction(
                activity,
                fannelPath,
                fannelState,
                terminalFragment,
                imageView,
                editConstraintListAdapter,
                imageAcCon,
            )
        }
//            }
    }

    private fun getImageView(
        tagNameList: List<String>,
        viewGroup: ViewGroup?,
    ): Pair<String, AppCompatImageView?>? {
        if (
            viewGroup == null
        ) return null
        tagNameList.forEach { tagName ->
            val imageViewEntry = try {
                viewGroup.findViewWithTag<AppCompatImageView>(
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
            return tagName to imageViewEntry
        }
        tagNameList.forEach { tagName ->
            val frameLayoutEntry = try {
                viewGroup.findViewWithTag<FrameLayout>(
                    tagName
                )
            } catch (e: Exception) {
                null
            } ?: return@forEach
            val imageView = frameLayoutEntry.children.firstOrNull { view ->
                view is AppCompatImageView
            } as? AppCompatImageView
                ?: return@forEach
            return tagName to imageView
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

    private suspend fun execImageAction(
        activity: MainActivity,
        fannelPath: String,
        fannelState: String,
        terminalFragment: TerminalFragment?,
        imageView: AppCompatImageView,
        editConstraintListAdapter: EditConstraintListAdapter?,
        imageAcCon: String,
    ){
        if(
            terminalFragment == null
        ) return
        val setReplaceVariableMapSrc = withContext(Dispatchers.IO) {
            SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
                terminalFragment.context,
                fannelPath
            )
        }
        val fannelInfoMap = withContext(Dispatchers.IO) {
            FannelInfoTool.makeFannelInfoMapByString(
                File(fannelPath).name,
                fannelState,
            )
        }
        val requestBuilder =  withContext(Dispatchers.IO) {
            Glide.with(imageView.context)
                .asDrawable()
                .sizeMultiplier(0.1f)
        }
        val errWhere = withContext(Dispatchers.IO) {
            listOf(
                "objectName: ${objectName}",
                fannelInfoMap.entries.joinToString(",")
            ).joinToString(", ")
        }
        withContext(Dispatchers.IO) {
            ImageActionManager().exec(
                terminalFragment.context,
                fannelInfoMap,
                setReplaceVariableMapSrc,
                terminalFragment.busyboxExecutor,
                imageView,
                requestBuilder,
                ImageActionAsyncCoroutine(),
                null,
                null,
                imageAcCon,
                errWhere,
                editConstraintListAdapter
            )
        }
    }
}