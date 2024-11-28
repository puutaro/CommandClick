package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.edit_list.EditFrameMaker
import com.puutaro.commandclick.proccess.edit_list.EditListConfig
import com.puutaro.commandclick.proccess.edit_list.config_settings.LayoutSettingsForEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class EditComponentListAdapter(
    private val fragment: Fragment?,
    val fannelInfoMap: Map<String, String>,
    val setReplaceVariableMap: Map<String, String>?,
    val editListConfigMap: Map<String, String>?,
    val busyboxExecutor: BusyboxExecutor?,
    val editListMap: Map<String, String>,
    var lineMapList: MutableList<Map<String, String>>,
    var fannelContentsList: List<String>?,
): RecyclerView.Adapter<EditComponentListAdapter.EditListViewHolder>()
{
    private val context = fragment?.context
    private val listLimitSize = 300
    private val editExecuteAlways = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
    private val initSettingValMap = RecordNumToMapNameValueInHolder.parse(
        fannelContentsList,
        CommandClickScriptVariable.SETTING_SEC_START,
        CommandClickScriptVariable.SETTING_SEC_END,
    )
    private val initCmdValMap = RecordNumToMapNameValueInHolder.parse(
        fannelContentsList,
        CommandClickScriptVariable.CMD_SEC_START,
        CommandClickScriptVariable.CMD_SEC_END,
    )
    val totalSettingValMap =
        (initSettingValMap ?: emptyMap()) + (initCmdValMap ?: emptyMap())
    val footerKeyPairListConMap: MutableMap<String, String> = mutableMapOf()
//    private val busyboxExecutor = editFragmentRef.get()?.busyboxExecutor

//    private val listIndexConfigMap =
//        editFragmentRef.get()?.listIndexConfigMap
//            ?: emptyMap()
//    private val clickConfigMap = ClickSettingsForListIndex.makeClickConfigMap(
//        listIndexConfigMap
//    )
    private val layoutConfigMap = LayoutSettingsForEditList.getLayoutConfigMap(
        editListConfigMap
    )
    private val layoutMargin = layoutConfigMap.get(
        LayoutSettingsForEditList.LayoutSettingKey.MARGIN.key
    ).let {
        try{
            it?.toInt()?.let {
                ScreenSizeCalculator.toDp(
                    context,
                    it,
                )
            }
        } catch (e: Exception){
            null
        }
    }
    private val layoutElevation = layoutConfigMap.get(
        LayoutSettingsForEditList.LayoutSettingKey.ELEVATION.key
    ).let {
        try{
            it?.toFloat()?.let {
                ScreenSizeCalculator.toDpForFloat(
                    context,
                    it,
                )
            }
        } catch (e: Exception){
            null
        }
    }

    private val layoutRadius = layoutConfigMap.get(
        LayoutSettingsForEditList.LayoutSettingKey.RADIUS.key
    ).let {
        try{
            it?.toFloat()?.let {
                ScreenSizeCalculator.toDpForFloat(
                    context,
                    it,
                )
            }
        } catch (e: Exception){
            null
        }
    }

    fun getCurrentSettingVals(
        context: Context?,
        settingValName: String,
    ): String? {
        val curSettingValsMap = RecordNumToMapNameValueInHolder.parse(
            fannelContentsList,
            CommandClickScriptVariable.SETTING_SEC_START,
            CommandClickScriptVariable.SETTING_SEC_END,
        ) ?: emptyMap()
        val curCmdValsMap = RecordNumToMapNameValueInHolder.parse(
            fannelContentsList,
            CommandClickScriptVariable.CMD_SEC_START,
            CommandClickScriptVariable.CMD_SEC_END,
        ) ?: emptyMap()
        val totalSettingValsMap = curSettingValsMap + curCmdValsMap
        return totalSettingValsMap.get(settingValName)
    }
    private val viewLayoutPath = ListSettingsForEditList.ViewLayoutPathManager.getViewLayoutPath(
        fannelInfoMap,
        setReplaceVariableMap,
        editListMap,
        ListSettingsForEditList.ListSettingKey.VIEW_LAYOUT_PATH.key,
    )
    private val frameMapToFrameTagAndVerticalKeysListToLinearMapList = ListSettingsForEditList.ViewLayoutPathManager.parse(
        context,
        fannelInfoMap,
        setReplaceVariableMap,
        viewLayoutPath
    )
//        .let {
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "eframeMapListToLinearMapList.txt").absolutePath,
//            listOf(
//                "lineMapList: ${lineMapList}",
//                "eframeMapListToLinearMapList: ${it}",
//            ).joinToString("\n\n")
//        )
//        it
//    }
    private val frameMap =
        frameMapToFrameTagAndVerticalKeysListToLinearMapList?.first ?: emptyMap()
    private val frameTagToVerticalKeysCon =
        frameMapToFrameTagAndVerticalKeysListToLinearMapList?.second ?: emptyList()
    private val verticalTagToLinearKeysListMap =
        frameMapToFrameTagAndVerticalKeysListToLinearMapList?.third ?: emptyMap()
    var deleteConfigMap: Map<String, String> = mapOf()
    private var recentAppDirPath = String()
    private var filterPrefix = String()
    private var filterSuffix = String()

    init {
        if(lineMapList.size == 0) {
            setListProperty()
        }
    }

    class EditListViewHolder(
        val view: View
    ): RecyclerView.ViewHolder(view) {
        val materialCardView =
            view.findViewById<MaterialCardView>(
                R.id.edit_component_adapter_mterial_card_view
            )
        val totalLinearLayout = view.findViewById<LinearLayoutCompat>(
            R.id.edit_component_adapter_total_linear,
        )
//        val firstVerticalLinerLayout = view.findViewById<LinearLayoutCompat>(
//            R.id.edit_component_first_vertical_linear
//        )
        var keyPairListConMap: MutableMap<String, String?> = mutableMapOf()
        var srcTitle = String()
        var srcCon = String()
        var srcImage = String()
    }

    override fun getItemId(position: Int): Long {
        setHasStableIds(true)
        return super.getItemId(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            R.layout.list_index_edit_adapter_layout,
            parent,
            false
        )
        return EditListViewHolder(
            itemView
        )
    }

    override fun getItemCount(): Int = lineMapList.size

    companion object {

        private val typeSeparator = EditComponent.Template.typeSeparator
        private val isConsecKey = EditComponent.Template.EditComponentKey.IS_CONSEC.key
        fun makeLinearFrameKeyPairsList(
            linearFrameKeyPairsListCon: String?
        ): List<Pair<String, String>> {
            return CmdClickMap.createMap(
                linearFrameKeyPairsListCon,
                typeSeparator
            )
        }
    }

    override fun onBindViewHolder(
        holder: EditListViewHolder,
        editListPosition: Int
    ) {
        if(
            context == null
        ) return
        if(
            editListPosition > listLimitSize
        ) return
        initListProperty(editListPosition)
        val lineMap = lineMapList[editListPosition]
        holder.srcTitle = lineMap.get(
            ListSettingsForEditList.MapListPathManager.Key.SRC_TITLE.key
        ) ?: String()
        holder.srcCon = lineMap.get(
            ListSettingsForEditList.MapListPathManager.Key.SRC_CON.key
        ) ?: String()
        holder.srcImage = lineMap.get(
            ListSettingsForEditList.MapListPathManager.Key.SRC_IMAGE.key
        ) ?: String()
        val frameTag = lineMap.get(
            ListSettingsForEditList.MapListPathManager.Key.VIEW_LAYOUT_TAG.key
        ).let {
            if(
                !it.isNullOrEmpty()
            ) return@let it
            editListMap.get(
                ListSettingsForEditList.ListSettingKey.DEFAULT_FRAME_TAG.key
            )
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "eframeMapListToLinearMapList_inBinder.txt").absolutePath,
//            listOf(
//                "frameMap: ${frameMap}",
//                "lineMap: ${lineMap}",
//                "frameTag: ${frameTag}",
//            ).joinToString("\n\n")
//        )
        if(
            frameTag.isNullOrEmpty()
        ) return
        CoroutineScope(Dispatchers.IO).launch {
            val frameKeyPairsConToVarNameValueMap = withContext(Dispatchers.IO) {
                val frameKeyPairsConSrc = frameMap.get(frameTag)
                EditComponent.Template.ReplaceHolder.replaceHolder(
                    frameKeyPairsConSrc,
                    holder.srcTitle,
                    holder.srcCon,
                    holder.srcImage,
                    holder.bindingAdapterPosition,
                ).let {
                    frameKeyPairsConSrc ->
                    if(
                        frameKeyPairsConSrc.isNullOrEmpty()
                    ) return@let String() to emptyMap()
                    val settingActionManager = SettingActionManager()
                    val mapListElInfo = listOf(
                        "srcTitle: ${holder.srcTitle}",
                        "srcCon: ${holder.srcCon}",
                        "srcImage: ${holder.srcImage}",
                        "bindingAdapterPosition: ${holder.bindingAdapterPosition}",
                    ).joinToString(" ")
                    val varNameToValueMap = settingActionManager.exec(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        frameKeyPairsConSrc,
                        "frameTag: ${frameTag}, mapListElInfo: ${mapListElInfo}",
                    )
                    CmdClickMap.replace(
                        frameKeyPairsConSrc,
                        varNameToValueMap
                    ) to varNameToValueMap
                }
            }
            val frameKeyPairsCon = frameKeyPairsConToVarNameValueMap.first
            val frameVarNameValueMap = frameKeyPairsConToVarNameValueMap.second
            val frameKeyPairsList = withContext(Dispatchers.IO) {
                makeLinearFrameKeyPairsList(
                    frameKeyPairsCon,
                )
            }
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lviewLayout_inadapter.txt").absolutePath,
//                listOf(
//                    "indexListMap: ${indexListMap}",
//                    "frameMap: ${frameMap}",
//                    "frameKeyMap: ${frameKeyPairsList}",
//                    "linearMapList: ${frameTagToLinearKeysListMap}",
//                ).joinToString("\n\n")
//            )
            val materialCardView = withContext(Dispatchers.Main) {
                holder.materialCardView.apply {
                    removeAllViews()
                    layoutElevation?.let {
                        elevation = it
                    }
                    layoutRadius?.let {
                        radius = it
                    }
                }
            }
//            GridLayoutManager
            withContext(Dispatchers.Main) {
                val cardLinearParams =
                    materialCardView.layoutParams as GridLayoutManager.LayoutParams
                layoutMargin?.let {
                    cardLinearParams.setMargins(it)
                }
            }
            withContext(Dispatchers.Main) {
                val frameFrameLayout = EditFrameMaker.make(
                    context,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    frameKeyPairsList,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    null,
                    frameTag,
                    totalSettingValMap,

                )
                val frameKeyList = JsActionKeyManager.JsActionsKey.entries.map{
                    it.key
                }
                withContext(Dispatchers.IO) setClickOrTouch@ {
                    if(
                        frameFrameLayout == null
                    ) return@setClickOrTouch
                    val isConsec =
                        PairListTool.getValue(
                            frameKeyPairsList,
                            isConsecKey,
                        ) == EditComponent.Template.switchOn
                    frameKeyList.any {
                        !PairListTool.getValue(
                            frameKeyPairsList,
                            it,
                        ).isNullOrEmpty()
                    }.let {
                            isJsAc ->
                        if(
                            !isJsAc
                            && frameFrameLayout.tag != null
                        ) {
                            frameFrameLayout.setBackgroundResource(0)
                            frameFrameLayout.isClickable = false
                            return@let
                        }
                        holder.keyPairListConMap.put(
                            frameTag,
                            frameKeyPairsCon
                        )
                        val outValue = TypedValue()
                        context.theme.resolveAttribute(
                            android.R.attr.selectableItemBackground,
                            outValue,
                            true
                        )
                        frameFrameLayout.setBackgroundResource(outValue.resourceId)
                        frameFrameLayout.isClickable = true
                        when(isConsec) {
                            true -> with (frameFrameLayout) {
                                setOnTouchListener(android.view.View.OnTouchListener { v, event ->
                                    when (event.action) {
                                        android.view.MotionEvent.ACTION_DOWN -> {
                                            editAdapterTouchDownListener?.onEditAdapterTouchDown(
                                                frameFrameLayout,
                                                holder,
                                                editListPosition
                                            )
                                        }
                                        android.view.MotionEvent.ACTION_UP,
                                        android.view.MotionEvent.ACTION_CANCEL,
                                        -> {
                                            editAdapterTouchUpListener?.onEditAdapterTouchUp(
                                                frameFrameLayout,
                                                holder,
                                                editListPosition
                                            )
                                            v.performClick()
                                        }
                                    }
                                    true
                                })
                            }
                            else -> frameFrameLayout.setOnClickListener {
                                editAdapterClickListener?.onEditAdapterClick(
                                    frameFrameLayout,
                                    holder,
                                    editListPosition,
                                )
                            }
                        }
                    }
                }
                materialCardView.addView(frameFrameLayout)
            }
            val weightSumFloat = 1f
            val verticalTagToKeyPairsListToVarNameToValueMapList = withContext(Dispatchers.IO){
                EditComponent.AdapterSetter.makeVerticalTagAndKeyPairsListToVarNameToValueMap(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    frameTag,
                    frameTagToVerticalKeysCon,
                    frameVarNameValueMap,
                    holder.srcTitle,
                    holder.srcCon,
                    holder.srcImage,
                    holder.bindingAdapterPosition,
                )
            }
            val totalLinearLayout = withContext(Dispatchers.Main) {
                holder.totalLinearLayout.apply {
                    removeAllViews()
                }
            }
//                withContext(Dispatchers.Main){
//                LinearLayoutCompat(context).apply {
//                    val totalLinearParam = LinearLayoutCompat.LayoutParams(
//                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
//                    ).apply {
//                        tag =
//                            setMargins(ScreenSizeCalculator.toDp(context,10))
//                        weight = weightSumFloat
//                    }
//                    layoutParams = totalLinearParam
//                    orientation = LinearLayoutCompat.HORIZONTAL
//                    gravity = Gravity.CENTER
//                }
//            }
            val verticalLinerWeight = weightSumFloat / verticalTagToKeyPairsListToVarNameToValueMapList.size
            withContext(Dispatchers.Main) {
                verticalTagToKeyPairsListToVarNameToValueMapList.forEachIndexed {
                        verticalIndex, verticalTagToKeyPairsListToVarNameToValueMap ->
                    val verticalTag = verticalTagToKeyPairsListToVarNameToValueMap.first
                    val keyPairsListToVarNameToValueMap = verticalTagToKeyPairsListToVarNameToValueMap.second
                    val verticalKeyPairs = keyPairsListToVarNameToValueMap.first
                    val verticalVarNameToValueMap = keyPairsListToVarNameToValueMap.second + frameVarNameValueMap
//                ,ScreenSizeCalculator.toDp(context, 50)
//                    val verticalLinearLayoutSrc = when(verticalIndex == 0) {
//                        true -> holder.firstVerticalLinerLayout
//                        else -> null
//                    }
                    val verticalLinearLayout = EditComponent.AdapterSetter.makeVerticalLinear(
                            context,
                            null,
                            verticalKeyPairs,
                            verticalLinerWeight,
                            verticalTag,
                        )
                    verticalTagToLinearKeysListMap.get(verticalTag)?.forEach {
                            linearKeyValues ->
                        val linearParam = LinearLayoutCompat.LayoutParams(
                            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                        )
                        val horizonLinearLayout = LinearLayoutCompat(context).apply {
                            layoutParams = linearParam
                            weightSum = weightSumFloat
                            orientation = LinearLayoutCompat.HORIZONTAL
                            gravity = Gravity.CENTER
//                            background = AppCompatResources.getDrawable(context, R.color.light_ao)
                        }
                        val linearFrameTagToKeyPairsList = withContext(Dispatchers.IO) {
                            EditComponent.AdapterSetter.makeLinearFrameTagToKeyPairsList(
                                linearKeyValues,
                                holder.srcTitle,
                                holder.srcCon,
                                holder.srcImage,
                                holder.bindingAdapterPosition,
                            )
                        }
                        val linearSettingTagMacroStr =
                            EditComponent.Template.TagManager.TagMacro.LINEAR_SETTING.name
                        val linearKeyValueSize = linearFrameTagToKeyPairsList.filter {
                            val tag = it.first
                            !tag.startsWith(linearSettingTagMacroStr)
                        }.size
                        val layoutWeight = weightSumFloat / linearKeyValueSize
                        linearFrameTagToKeyPairsList.forEach setFrame@ { linearFrameTagToLinearFrameKeyPairs ->
                            val linearFrameTag = linearFrameTagToLinearFrameKeyPairs.first
                            val linearFrameKeyPairsListCon = linearFrameTagToLinearFrameKeyPairs.second.let {
                                    linearFrameKeyPairsListConSrc ->
                                if(
                                    linearFrameKeyPairsListConSrc.isNullOrEmpty()
                                ) return@let String()
                                val settingActionManager = SettingActionManager()
                                val mapListElInfo = listOf(
                                    "srcTitle: ${holder.srcTitle}",
                                    "srcCon: ${holder.srcCon}",
                                    "srcImage: ${holder.srcImage}",
                                    "bindingAdapterPosition: ${holder.bindingAdapterPosition}",
                                ).joinToString(" ")
                                val varNameToValueMap = settingActionManager.exec(
                                    fragment,
                                    fannelInfoMap,
                                    setReplaceVariableMap,
                                    busyboxExecutor,
                                    CmdClickMap.replace(
                                        linearFrameKeyPairsListConSrc,
                                        verticalVarNameToValueMap
                                    ),
                                    "linearFrameTag: ${linearFrameTag}, frameTag: ${frameTag}, mapListInfo: ${mapListElInfo}",
                                )
                                CmdClickMap.replace(
                                    linearFrameKeyPairsListConSrc,
                                    varNameToValueMap
                                )
                            }
                            val linearFrameKeyPairsList = makeLinearFrameKeyPairsList(
                                linearFrameKeyPairsListCon
                            )
//                            val linearFrameKeyPairsListCon = linearFrameTagToLinearFrameKeyPairs.third
                            if(
                                linearFrameTag.startsWith(linearSettingTagMacroStr)
                            ){
                                EditComponent.AdapterSetter.setHorizonLinear(
                                    horizonLinearLayout,
                                    verticalKeyPairs,
                                    linearFrameKeyPairsList
                                )
                                return@setFrame
                            }
                            val linearFrameLayout = EditFrameMaker.make(
                                context,
                                fannelInfoMap,
                                setReplaceVariableMap,
                                busyboxExecutor,
                                linearFrameKeyPairsList,
                                0,
                                layoutWeight,
                                linearFrameTag,
//                                false,
                                totalSettingValMap
                            ) ?: return@setFrame
                            val linearKeyList = JsActionKeyManager.JsActionsKey.entries.map{
                                it.key
                            }
                            withContext(Dispatchers.IO) {
                                val isConsec =
                                    PairListTool.getValue(
                                        linearFrameKeyPairsList,
                                        isConsecKey,
                                    ) == EditComponent.Template.switchOn
                                linearKeyList.any {
                                    !PairListTool.getValue(
                                        linearFrameKeyPairsList,
                                        it,
                                    ).isNullOrEmpty()
                                }.let {
                                        isJsAc ->
                                    holder.keyPairListConMap.put(
                                        linearFrameTag,
                                        linearFrameKeyPairsListCon
                                    )
                                    if(
                                        !isJsAc
                                        && linearFrameLayout.tag != null
                                    ) {
                                        linearFrameLayout.setBackgroundResource(0)
                                        linearFrameLayout.isClickable = false
                                        return@let
                                    }
                                    val outValue = TypedValue()
                                    context.theme.resolveAttribute(
                                        android.R.attr.selectableItemBackground,
                                        outValue,
                                        true
                                    )
                                    val clickViewList = linearFrameLayout.children.filter {
                                        it is OutlineTextView
                                                || it is AppCompatImageView
                                    }
                                    clickViewList.forEach {
                                        clickView ->
                                        clickView.setBackgroundResource(outValue.resourceId)
                                        clickView.isClickable = true
                                        when(isConsec) {
                                            true ->
                                                with (clickView) {
                                                    setOnTouchListener(android.view.View.OnTouchListener { v, event ->
                                                        when (event.action) {
                                                            android.view.MotionEvent.ACTION_DOWN -> {
                                                                editAdapterTouchDownListener?.onEditAdapterTouchDown(
                                                                    linearFrameLayout,
                                                                    holder,
                                                                    editListPosition
                                                                )
                                                            }
                                                            android.view.MotionEvent.ACTION_UP,
                                                            android.view.MotionEvent.ACTION_CANCEL,
                                                                -> {
                                                                editAdapterTouchUpListener?.onEditAdapterTouchUp(
                                                                    linearFrameLayout,
                                                                    holder,
                                                                    editListPosition
                                                                )
                                                                v.performClick()
                                                            }
                                                        }
                                                        true
                                                    })
                                                }
                                            else -> clickView.setOnClickListener {
                                                editAdapterClickListener?.onEditAdapterClick(
                                                    linearFrameLayout,
                                                    holder,
                                                    editListPosition,
                                                )
                                            }
                                        }
                                    }


                                }
                            }
                            horizonLinearLayout.addView(linearFrameLayout)
                        }
                        verticalLinearLayout.addView(horizonLinearLayout)
                    }
//                    if(verticalIndex != 0) {
                        totalLinearLayout.addView(verticalLinearLayout)
//                    }
                }
                materialCardView.addView(totalLinearLayout)
            }
            withContext(Dispatchers.Main) {
                val itemView = holder.itemView
                itemView.setOnClickListener {
                    editAdapterClickListener?.onEditAdapterClick(
                        itemView,
                        holder,
                        editListPosition,
                    )
                }
            }
        }
    }

    var editAdapterClickListener: OnEditAdapterClickListener? = null
    interface OnEditAdapterClickListener {
        fun onEditAdapterClick(
            itemView: View,
            holder: EditListViewHolder,
            editListPosition: Int,
        )
    }

    var editAdapterTouchDownListener: OnEditAdapterTouchDownListener? = null
    interface OnEditAdapterTouchDownListener {
        fun onEditAdapterTouchDown(
            itemView: View,
            holder: EditListViewHolder,
            editListPosition: Int,
        )
    }

    var editAdapterTouchUpListener: OnEditAdapterTouchUpListener? = null
    interface OnEditAdapterTouchUpListener {
        fun onEditAdapterTouchUp(
            itemView: View,
            holder: EditListViewHolder,
            editListPosition: Int,
        )
    }

    fun handleClickEvent(
        editListRecyclerView: RecyclerView,
        tag: String,
        settingValue: String?,
        indexListPosition: Int,
        frameOrLinearCon: String,
    ){
        updateAndSaveMainFannel(
            tag,
            settingValue,
            frameOrLinearCon,
        )
        MapListUpdater.updateLineMapList(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            editListRecyclerView,
            editListMap,
            layoutConfigMap,
//            lineMapList,
            indexListPosition,
        )
    }

    private object MapListUpdater {
        fun updateLineMapList(
            fragment: Fragment?,
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            editListRecyclerView: RecyclerView,
            indexListMap: Map<String, String>,
            layoutConfigMap: Map<String, String>,
//            lineMapList: List<Map<String, String>>,
            indexListPosition: Int,
        ) {
            if (
                indexListPosition == 0
                || fragment == null
            ) return
            val enableClickUpdate =
                LayoutSettingsForEditList.howClickUpdate(
                    fannelInfoMap,
                    setReplaceVariableMap,
                    layoutConfigMap
                )
            if (
                !enableClickUpdate
            ) return
            execUpdateLineMapList(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                editListRecyclerView,
                indexListMap,
                indexListPosition
            )
        }

        private fun execUpdateLineMapList(
            fragment: Fragment?,
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            editRecyclerView: RecyclerView,
            indexListMap: Map<String, String>,
            bindingAdapterPosition: Int,
        ) {
            val sortType = ListSettingsForEditList.getSortType(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap
            )
            when (sortType) {
                ListSettingsForEditList.SortByKey.SORT,
                ListSettingsForEditList.SortByKey.REVERSE
                -> return

                ListSettingsForEditList.SortByKey.LAST_UPDATE,
                -> {
                }
            }
            val editComponentListAdapter = editRecyclerView.adapter as EditComponentListAdapter
            val lineMap =
                editComponentListAdapter.lineMapList.getOrNull(
                    bindingAdapterPosition
                ) ?: return
            val mapListPath = FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key,
            )
            MapListFileTool.insertMapFileInFirst(
                mapListPath,
                lineMap
            )
            when(fragment){
                is EditFragment -> BroadcastSender.normalSend(
                    fragment.context,
                    BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action
                )
                is TerminalFragment ->  BroadcastSender.normalSend(
                    fragment.context,
                    BroadCastIntentSchemeTerm.EDIT_INDEX_LIST_UPDATE.action
                )
            }

        }
    }

    private fun updateAndSaveMainFannel(
        tag: String,
        settingValue: String?,
        frameOrLinearCon: String,
    ){
        updateMainFannelList(
            tag,
            settingValue,
        )
        MainFannelUpdater.saveFannelCon(
            fannelContentsList,
            fannelInfoMap,
            frameOrLinearCon,
        )
    }


    fun updateMainFannelList(
        tag: String,
        settingValue: String?,
    ){
        val updateSettingValsCon = settingValue?.let {
            listOf(
                "${tag}=${settingValue}"
            ).joinToString("\n")
        } ?: String()
        initSettingValMap?.contains(tag)?.let{
            if(!it) return@let
            MainFannelUpdater.makeUpdateMainFannelList(
                fannelContentsList,
                updateSettingValsCon,
                CommandClickScriptVariable.SETTING_SEC_START,
                CommandClickScriptVariable.SETTING_SEC_END,
            ).let updateFannel@ {
                    updateFannelList ->
                if(
                    updateFannelList.isNullOrEmpty()
                    || fannelContentsList == updateFannelList
                ) return@updateFannel
                fannelContentsList = updateFannelList
            }
        }
        initCmdValMap?.contains(tag)?.let{
            if(!it) return@let
            MainFannelUpdater.makeUpdateMainFannelList(
                fannelContentsList,
                updateSettingValsCon,
                CommandClickScriptVariable.CMD_SEC_START,
                CommandClickScriptVariable.CMD_SEC_END,
            ).let updateFannel@ {
                    updateFannelList ->
                if(
                    updateFannelList.isNullOrEmpty()
                    || fannelContentsList == updateFannelList
                ) return@updateFannel
                fannelContentsList = updateFannelList
            }
        }
    }

    fun saveFannelCon() {
        MainFannelUpdater.execSaveFannelCon(
            fannelInfoMap,
            fannelContentsList
        )
    }


    object MainFannelUpdater {

        fun saveFannelCon(
            saveFannelConList: List<String>?,
            fannelInfoMap: Map<String, String>,
            frameOrLinearCon: String,
        ) {
            if (
                saveFannelConList.isNullOrEmpty()
            ) return
            val isSave = PairListTool.getValue(
                makeLinearFrameKeyPairsList(
                    frameOrLinearCon,
                ),
                EditComponent.Template.EditComponentKey.ON_SAVE.key,
            ) == EditComponent.Template.switchOn
            if (!isSave) return
            execSaveFannelCon(
                fannelInfoMap,
                saveFannelConList,
            )
        }

        fun execSaveFannelCon(
            fannelInfoMap: Map<String, String>,
            saveFannelConList: List<String>?,
        ){
            if(
                saveFannelConList.isNullOrEmpty()
            ) return
            val fannelFile = FannelInfoTool.getCurrentFannelName(fannelInfoMap).let {
                File(UsePath.cmdclickDefaultAppDirPath, it)
            }
            if(
                ReadText(fannelFile.absolutePath).textToList() == saveFannelConList
            ) return
            FileSystems.writeFile(
                fannelFile.absolutePath,
                saveFannelConList.joinToString("\n")
            )
        }
        fun makeUpdateMainFannelList(
            fannelContentsListSrc: List<String>?,
            updateSettingValsCon: String?,
            startHolder: String,
            endHolder: String,
        ): List<String>? {
            if (
                updateSettingValsCon.isNullOrEmpty()
                || fannelContentsListSrc.isNullOrEmpty()
            ) return null
            return CommandClickVariables.replaceVariableInHolder(
                fannelContentsListSrc,
                updateSettingValsCon,
                startHolder,
                endHolder,
            )
        }
    }

    private fun makeFileConList(
        fileName: String,
    ): List<String> {
        if (
            editListConfigMap.isNullOrEmpty()
        ) return emptyList()
        return emptyList()
    }

    private suspend fun setDescView(
        fileDescTextView: AppCompatTextView,
        fileNameOrInstallFannelLine: String,
        fileCon: String,
    ){
        val makeFileDescArgsMaker = EditListConfig.MakeFileDescArgsMaker(
//            filterDir,
            fileNameOrInstallFannelLine,
            fileCon,
            editListConfigMap,
            busyboxExecutor,
        )
        val descCon = EditListConfig.makeFileDesc(
            makeFileDescArgsMaker,
        )
        withContext(Dispatchers.Main) {
            when (descCon.isNullOrEmpty()) {
                true -> fileDescTextView.isVisible = false
                else -> fileDescTextView.text = descCon
            }
        }
    }

    private fun setFileContentsBackColor(
        fileConList: List<String>,
//        fileName: String,
        editExecuteValueForInstallFannel: String,
    ): Int {
        if(
            context == null
        ) return R.color.fannel_icon_color
        if(
            editExecuteValueForInstallFannel == editExecuteAlways
        ) return R.color.terminal_color
        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            fileConList,
            CommandClickScriptVariable.SETTING_SEC_START,
            CommandClickScriptVariable.SETTING_SEC_END,
        )
        val editExecuteValue = SettingVariableReader.getStrValue(
            settingVariableList,
            CommandClickScriptVariable.EDIT_EXECUTE,
            CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
        )
        if(
            editExecuteValue
            == SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        ) return R.color.terminal_color
        return R.color.fannel_icon_color
    }

    private fun initListProperty(
        editListPosition: Int,
    ){
        if(
            editListPosition != 0
        ) return
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "layout.txt").absolutePath,
//            listOf(
//                "frameMapListToLinearMapList: ${frameMapListToLinearMapList}",
//                "frameMap: ${frameMap}",
//                "linearMapList: ${frameTagToLinearKeysListMap}",
//            ).joinToString("\n\n")
//        )
        setListProperty()
    }

    private fun setListProperty(){
        recentAppDirPath = UsePath.cmdclickDefaultAppDirPath
        deleteConfigMap = EditListConfig.getConfigKeyMap(
            editListConfigMap,
            EditListConfig.EditListConfigKey.DELETE.key
        )
        filterPrefix =
            FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                editListMap,
                ListSettingsForEditList.ListSettingKey.PREFIX.key
            ) ?: String()
        filterSuffix =
            FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                editListMap,
                ListSettingsForEditList.ListSettingKey.SUFFIX.key
            ) ?: String()
    }
    fun getLayoutConfigMap(): Map<String, String> {
        return layoutConfigMap
    }
}
