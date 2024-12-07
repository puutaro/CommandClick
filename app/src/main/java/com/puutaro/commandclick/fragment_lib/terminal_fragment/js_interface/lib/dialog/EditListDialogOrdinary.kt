package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.EditSettings
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.system.JsFannelInfo
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithEditComponentListView
import com.puutaro.commandclick.proccess.edit.lib.ListSettingVariableListMaker
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class EditListDialogOrdinary(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    private val context = terminalFragmentRef.get()?.context
    private var isAlreadyShow = false

    private val density = ScreenSizeCalculator.getDensity(context)
    private val requestBuilderSrc: RequestBuilder<Drawable>? =
        context?.let {
            Glide.with(it)
                .asDrawable()
                .sizeMultiplier(0.1f)
        }

    val editListDialogOrdinary = context?.let {
        Dialog(
            it,
            R.style.FullScreenRoundCornerDialogTheme
        ).apply {
            setContentView(
                R.layout.edit_list_dialog_layout
            )
            window?.apply {
                setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    }
    private val constraintLayoutSrc =
        editListDialogOrdinary?.findViewById<ConstraintLayout>(
            R.id.edit_list_dialog_constraint_layout
        )
    private val editBackstackCountFrameSrc =
        editListDialogOrdinary?.findViewById<FrameLayout>(
            R.id.edit_list_dialog_backstack_count_frame
        )
    private val editBackstackCountViewSrc =
        editListDialogOrdinary?.findViewById<ShapeableImageView>(
            R.id.edit_list_dialog_backstack_count
        )
    private val editListTitleViewSrc =
        editListDialogOrdinary?.findViewById<OutlineTextView>(
            R.id.edit_list_dialog_title_view
        )
    private val editListTitleImageSrc =
        editListDialogOrdinary?.findViewById<AppCompatImageView>(
            R.id.edit_list_dialog_title_image
        )
    private val editListRecyclerViewSrc =
        editListDialogOrdinary?.findViewById<RecyclerView>(
            R.id.edit_list_dialog_recycler_view
        )
    private val editListBkFrameSrc =
        editListDialogOrdinary?.findViewById<FrameLayout>(
            R.id.edit_list_bk_frame
        )

    private val editListSearchEditTextSrc =
        editListDialogOrdinary?.findViewById<AppCompatEditText>(
            R.id.edit_list_dialog_search_edit_text
        )
    private val editFooterHorizonLayoutSrc =
        editListDialogOrdinary?.findViewById<LinearLayoutCompat>(
            R.id.edit_list_dialog_footer_horizon_layout
        )
    private val verticalLinearListForFooter =
        listOf(
            editListDialogOrdinary?.findViewById<LinearLayoutCompat>(
                R.id.vertical_linear1
            ),
            editListDialogOrdinary?.findViewById<LinearLayoutCompat>(
                R.id.vertical_linear2
            )
        )
    private val horizonIdListSrc = listOf(
        R.id.edit_component_adapter_horizon1,
        R.id.edit_component_adapter_horizon2,
    )
    private val horizonLinearListForFooter = verticalLinearListForFooter.map {
            vertical ->
        horizonIdListSrc.map {
            vertical?.findViewById<LinearLayoutCompat>(it)
        }
    }
    private val contentsLayoutIdListList = listOf(
        listOf(
            R.id.button_frame_layout11,
            R.id.button_frame_layout12,
            R.id.button_frame_layout13,
        ),
        listOf(
            R.id.button_frame_layout21,
            R.id.button_frame_layout22,
            R.id.button_frame_layout23,
        ),
    )
    private val verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter =
        horizonLinearListForFooter.mapIndexed {
                _, readyHorizonLayoutList ->
            readyHorizonLayoutList.mapIndexed {
                    horizonIndex, horizon ->
                val curLayoutIdListForHorizon =
                    contentsLayoutIdListList.get(horizonIndex)
                curLayoutIdListForHorizon.map {
                        layoutId ->
                    horizon?.findViewById<FrameLayout>(layoutId)
                }
            }
        }
    init {
        editListDialogOrdinary?.setOnCancelListener {
            dismissForInner(
                terminalFragmentRef.get(),
                editListRecyclerViewSrc,
                editFooterHorizonLayoutSrc,
                editListBkFrameSrc,
                constraintLayoutSrc,
                true,
            )
        }
    }



    fun create(
        fannelInfoCon: String,
        editListConfigPath: String,
    ){
        CoroutineScope(Dispatchers.Main).launch {
            execCreate(
                fannelInfoCon,
                editListConfigPath,
            )
        }
    }

    fun isAlreadyShow(): Boolean {
        return isAlreadyShow
    }


    fun execCreate(
        fannelInfoCon: String,
        editListConfigPath: String,
    ){
        isAlreadyShow = true
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val editBackstackCountFrame =
            editBackstackCountFrameSrc
                ?: return
        val editBackstackCountView =
            editBackstackCountViewSrc
                ?: return
        val editListTitleView =
            editListTitleViewSrc
                ?: return
        val editListTitleImage =
            editListTitleImageSrc
                ?: return
        val editListRecyclerView =
            editListRecyclerViewSrc
                ?: return
        val editListBkFrame = editListBkFrameSrc
            ?: return
        val editListSearchEditText = editListSearchEditTextSrc
            ?:return
        val editFooterHorizonLayout =
            editFooterHorizonLayoutSrc
                ?:return
//        val constraintLayout =
//            constraintLayoutSrc
//                ?:return
        CoroutineScope(Dispatchers.IO).launch {
            val fannelInfoMap = withContext(Dispatchers.IO) {
                CmdClickMap.createMap(
                    fannelInfoCon,
                    JsFannelInfo.fannelInfoMapSeparator
                ).toMap()
            }
            val mainFannelFile = withContext(Dispatchers.IO) {
                FannelInfoTool.getCurrentFannelName(
                    fannelInfoMap,
                ).let {
                    File(UsePath.cmdclickDefaultAppDirPath, it)
                }
            }
            val mainFannelConList = withContext(Dispatchers.IO) {
                ReadText(
                    mainFannelFile.absolutePath,
                ).textToList()
            }
            val settingVariableList = withContext(Dispatchers.IO) {
                CommandClickVariables.extractValListFromHolder(
                    mainFannelConList,
                    CommandClickScriptVariable.SETTING_SEC_START,
                    CommandClickScriptVariable.SETTING_SEC_END,
                )
            }
            val setReplaceVariableMap = withContext(Dispatchers.IO) {
                SetReplaceVariabler.makeSetReplaceVariableMap(
                    context,
                    settingVariableList,
                    mainFannelFile.name
                )
            }
            val virtualSettingValsListForEditList = withContext(Dispatchers.IO) {
                listOf(
                    CommandClickScriptVariable.SETTING_SEC_START,
                    "${CommandClickScriptVariable.EDIT_LIST_CONFIG}=${EditSettings.filePrefix}${editListConfigPath}",
                    CommandClickScriptVariable.SETTING_SEC_END,
                )
            }
            val editListConfigMap = withContext(Dispatchers.IO) {
                ListSettingVariableListMaker.makeConfigMapFromSettingValList(
                    context,
                    CommandClickScriptVariable.EDIT_LIST_CONFIG,
                    virtualSettingValsListForEditList,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    String()
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                WithEditComponentListView.create(
                    terminalFragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    terminalFragment.busyboxExecutor,
                    editListConfigMap,
                    editBackstackCountFrame,
                    editBackstackCountView,
                    editListTitleView,
                    editListTitleImage,
                    editListRecyclerView,
                    editListBkFrame,
                    editListSearchEditText,
                    editFooterHorizonLayout,
                    verticalLinearListForFooter,
                    horizonLinearListForFooter,
                    verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter,
                    null,
                    null,
                    mainFannelConList,
                    density,
                    requestBuilderSrc,
                )
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main){
                editListDialogOrdinary?.show()
            }
//            withContext(Dispatchers.IO){
//                delay(1000)
//            }
//            withContext(Dispatchers.Main){
//                val firstEditListDialogForSetting =
//                    terminalFragment.editListDialogOrdinaly?.last()
//                terminalFragment.editListDialogOrdinaly = listOf(
//                    EditListDialogOrdinary(
//                        terminalFragmentRef
//                    ),
//                    firstEditListDialogForSetting,
//                )
//            }
        }
    }

    private fun dismissForInner(
        terminalFragment: TerminalFragment?,
        editListRecyclerView: RecyclerView?,
        editFooterLinearlayout: LinearLayoutCompat?,
        editListBkFrame: FrameLayout?,
        constraintLayout: ConstraintLayout?,
        isRecreate: Boolean,
    ){
        editListRecyclerView?.layoutManager = null
        editListRecyclerView?.adapter = null
        editListRecyclerView?.recycledViewPool?.clear()
        editListRecyclerView?.removeAllViews()
        editFooterLinearlayout?.removeAllViews()
        editListBkFrame?.removeAllViews()
        constraintLayout?.removeAllViews()
//        terminalFragment?.editListDialogOrdinaly?.first()?.dismiss()
//        when(isRecreate) {
//            true -> {
////                val firstEditListDialogForSetting =
////                    terminalFragment?.editListDialogOrdinalyList?.last()
////                terminalFragment?.editListDialogOrdinalyList = listOf(
////                    firstEditListDialogForSetting,
////                    EditListDialogOrdinary(
////                        terminalFragmentRef
////                    )
////                )
//            }
//            else -> terminalFragment?.editListDialogOrdinaly = null
//        }
    }

    fun dismiss(){
        val constraintLayout =
            constraintLayoutSrc
                ?: return
        val editListRecyclerView =
            editListRecyclerViewSrc
                ?: return
        val editListBkFrame =
            editListBkFrameSrc
                ?: return
        val editFooterLinearlayout =
            editFooterHorizonLayoutSrc
                ?: return
        val terminalFragment =
            terminalFragmentRef.get()
        dismissForInner(
            terminalFragment,
            editListRecyclerView,
            editFooterLinearlayout,
            editListBkFrame,
            constraintLayout,
            false
        )
    }
}