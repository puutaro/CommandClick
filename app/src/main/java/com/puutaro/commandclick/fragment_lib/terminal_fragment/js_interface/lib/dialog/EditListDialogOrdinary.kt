package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
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
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithEditConstraintListView
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
    private val contentsLayoutForFooter =
        constraintLayoutSrc?.findViewById<ConstraintLayout>(
            R.id.edit_list_dialog_footer_constraint_layout
        )
    init {
        editListDialogOrdinary?.setOnCancelListener {
            dismissForInner(
                editListRecyclerViewSrc,
                editListBkFrameSrc,
                constraintLayoutSrc,
                contentsLayoutForFooter
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
//        val editFooterHorizonLayout =
//            editFooterHorizonLayoutSrc
//                ?:return
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
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "leditDistDialog.txt").absolutePath,
//                listOf(
//                    "fannelInfoCon: ${fannelInfoCon}",
//                    "editListConfigPath: ${editListConfigPath}",
//                    "editListConfigPath.isFole: ${File(editListConfigPath).isFile}",
//                    "editListConfigMap: ${editListConfigMap}",
//                    "mainFannelFile: ${mainFannelFile.absolutePath}",
//                    "editListSearchEditText.id: ${editListSearchEditText.id}"
//                ).joinToString("\n")
//            )
            CoroutineScope(Dispatchers.IO).launch {
                WithEditConstraintListView.create(
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
                    contentsLayoutForFooter,
//                    editFooterHorizonLayout,
//                    verticalLinearListForFooter,
//                    horizonLinearListForFooter,
//                    verticalIndexAndHorizonIndexAndReadyContentsLayoutListForFooter,
                    null,
//                    null,
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
        editListRecyclerView: RecyclerView?,
        editListBkFrame: FrameLayout?,
        constraintLayout: ConstraintLayout?,
        contentsLayoutForFooter: ConstraintLayout?,
    ){
        contentsLayoutForFooter?.removeAllViews()
        editListRecyclerView?.layoutManager = null
        editListRecyclerView?.adapter = null
        editListRecyclerView?.recycledViewPool?.clear()
        editListRecyclerView?.removeAllViews()
        editListBkFrame?.removeAllViews()
        constraintLayout?.removeAllViews()
        editListDialogOrdinary?.dismiss()
    }

    fun destroy(){
        dismissForInner(
            editListRecyclerViewSrc,
            editListBkFrameSrc,
            constraintLayoutSrc,
            contentsLayoutForFooter,
        )
    }
}