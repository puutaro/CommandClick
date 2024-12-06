package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
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
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.MapListFileTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.PairListTool
import com.puutaro.commandclick.util.str.SnakeCamelTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference


class EditComponentListAdapter(
    private val fragmentRef: WeakReference<Fragment>,
    private val layoutInflater: LayoutInflater,
    val fannelInfoMap: Map<String, String>,
    val setReplaceVariableMap: Map<String, String>?,
    private val globalVarNameToValueMap: Map<String, String>?,
    val editListConfigMap: Map<String, String>?,
    val busyboxExecutor: BusyboxExecutor?,
    val editListMap: Map<String, String>,
    var lineMapList: MutableList<Map<String, String>>,
    var fannelContentsList: List<String>?,
    private val density: Float,
): RecyclerView.Adapter<EditComponentListAdapter.EditListViewHolder>()
{
    private val context = fragmentRef.get()?.context
    private val listLimitSize = 300
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
    private val layoutConfigMap = LayoutSettingsForEditList.getLayoutConfigMap(
        editListConfigMap
    )
    private val layoutMargin = layoutConfigMap.get(
        LayoutSettingsForEditList.LayoutSettingKey.MARGIN.key
    ).let {
        try{
            it?.toInt()?.let {
                ScreenSizeCalculator.toDpByDensity(
                    it,
                    density,
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
                ScreenSizeCalculator.toDpForFloatByDensity(
                    it,
                    density,
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
                ScreenSizeCalculator.toDpForFloatByDensity(
                    it,
                    density,
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
    private val frameMapAndFrameTagAndVerticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList =
        ListSettingsForEditList.ViewLayoutPathManager.parse(
            context,
            fannelInfoMap,
            setReplaceVariableMap,
            viewLayoutPath
        )
    private val frameMap =
        frameMapAndFrameTagAndVerticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList?.first ?: emptyMap()
    private val verticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList =
        frameMapAndFrameTagAndVerticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList?.second
            ?: Triple(emptyList(), emptyList(), emptyMap())
    private val frameTagToVerticalKeysConList =
        verticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList.first
    private val verticalTagToHorizonKeysConList =
        verticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList.second
    private val horizonTagToContentsKeysListMap =
        verticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList.third
//            .let {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "seframeMapListToLinearMapList.txt").absolutePath,
//                listOf(
//                    "lineMapList: ${lineMapList}",
//                    "frameMap: ${frameMap}",
//                    "frameTagToVerticalKeysConList: ${frameTagToVerticalKeysConList}",
//                    "verticalTagToHorizonKeysConList: ${verticalTagToHorizonKeysConList}",
//                    "horizonTagToLinearKeysListMap: ${it}",
//                ).joinToString("\n\n\n")
//            )
//            it
//        }
    private val plusKeyToSubKeyConWhere =
        fannelInfoMap.map {
            val key = SnakeCamelTool.snakeToCamel(it.key)
            "${key}: ${it.value}"
        }.joinToString(", ")
    var deleteConfigMap: Map<String, String> = mapOf()
    private var recentAppDirPath = String()
    private var filterPrefix = String()
    private var filterSuffix = String()

    init {
        if(lineMapList.size == 0) {
            setListProperty()
        }
    }

    private val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
    class EditListViewHolder(
        val view: View
    ): RecyclerView.ViewHolder(view) {
        val materialCardView =
            view.findViewById<MaterialCardView>(
                R.id.edit_component_adapter_mterial_card_view
            )
        val bkFrameLayout = materialCardView.findViewById<FrameLayout>(
            R.id.edit_component_adapter_bk_frame_layout
        )
        val totalLinearLayout = materialCardView.findViewById<LinearLayoutCompat>(
            R.id.edit_component_adapter_total_linear,
        )
        private val verticalIdList = listOf(
            R.id.vertical_linear1,
            R.id.vertical_linear2,
        )
        val readyVerticalLayoutList = verticalIdList.map {
            totalLinearLayout.findViewById<LinearLayoutCompat>(it)
        }
        private val horizonIdList = listOf(
            R.id.edit_component_adapter_horizon1,
            R.id.edit_component_adapter_horizon2,
        )
        val verticalIndexAndReadyHorizonLayoutList =
            readyVerticalLayoutList.map {
                    vertical ->
                horizonIdList.map {
                    vertical.findViewById<LinearLayoutCompat>(it)
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
        val verticalIndexAndHorizonIndexAndReadyContentsLayoutList =
            verticalIndexAndReadyHorizonLayoutList.mapIndexed {
                    _, readyHorizonLayoutList ->
                readyHorizonLayoutList.mapIndexed {
                        horizonIndex, horizon ->
                    val curLayoutIdListForHorizon =
                        contentsLayoutIdListList.get(horizonIndex)
                    curLayoutIdListForHorizon.map {
                        layoutId ->
                        horizon.findViewById<FrameLayout>(layoutId)
                    }
                }
            }
//            .let {
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lFrame.txt").absolutePath,
//                    it.mapIndexed { index, linearLayoutCompats ->
//                        "${index}=>${linearLayoutCompats.mapIndexed {
//                            horozionIndex, it ->
//                            "${horozionIndex}->${it.mapIndexed { index, frameLayout ->
//                                "${index}--->${frameLayout is FrameLayout}"
//                            }}"
//                        }.joinToString("\n\n\n")}"
//                    }.joinToString("\n")
//                )
//                it
//            }

        private object KeyPairListConMapUpdater {
            private var keyPairListConMap: MutableMap<String, String?> = mutableMapOf()
            private val mutex = Mutex()

            suspend fun update(
                tagKey: String,
                keyToSubKeyCon: String?
            ) {
                if(
                    keyToSubKeyCon.isNullOrEmpty()
                ) return
                mutex.withLock {
                    keyPairListConMap.put(tagKey, keyToSubKeyCon)
                }
            }

            suspend fun get(): Map<String, String?> {
                mutex.withLock {
                    return keyPairListConMap
                }
            }
        }
        suspend fun updateKeyPairListConMap(
            tagKey: String,
            keyToSubKeyCon: String?
        ){
            KeyPairListConMapUpdater.update(
                tagKey,
                keyToSubKeyCon
            )
        }


        suspend fun getKeyPairListConMap(
        ): Map<String, String?> {
            return KeyPairListConMapUpdater.get()
        }
//        var keyPairListConMap: MutableMap<String, String?> = mutableMapOf()
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
        val itemView = layoutInflater.inflate(
            R.layout.edit_list_adapter_layout,
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
        private val onConsecKey = EditComponent.Template.EditComponentKey.ON_CONSEC.key
        private val onClickKey = EditComponent.Template.EditComponentKey.ON_CLICK.key
        fun makeLinearFrameKeyPairsList(
            linearFrameKeyPairsListCon: String?
        ): List<Pair<String, String>> {
            return CmdClickMap.createMap(
                linearFrameKeyPairsListCon,
                typeSeparator
            )
        }
    }

    private val switchOn = EditComponent.Template.switchOn
    private val switchOff = EditComponent.Template.switchOff
    private val jsActionKeyList = JsActionKeyManager.JsActionsKey.entries.map{
        it.key
    }
    private val requestBuilderSrc: RequestBuilder<Drawable>? =
        context?.let {
            Glide.with(it)
                .asDrawable()
                .sizeMultiplier(0.1f)
        }

    override fun onBindViewHolder(
        holder: EditListViewHolder,
        editListPosition: Int
    ) {
        if(
            editListPosition > listLimitSize
        ) return
        CoroutineScope(Dispatchers.IO).launch {
            initListProperty(editListPosition)
            if(
                context == null
            ) return@launch
            val fragment = withContext(Dispatchers.IO) {
                fragmentRef.get()
            } ?: return@launch
            val lineMap = lineMapList[editListPosition]
            holder.srcTitle = withContext(Dispatchers.IO) {
                lineMap.get(
                    ListSettingsForEditList.MapListPathManager.Key.SRC_TITLE.key
                )
            } ?: String()
            holder.srcCon = withContext(Dispatchers.IO) {
                lineMap.get(
                    ListSettingsForEditList.MapListPathManager.Key.SRC_CON.key
                )
            } ?: String()
            holder.srcImage = withContext(Dispatchers.IO) {
                lineMap.get(
                    ListSettingsForEditList.MapListPathManager.Key.SRC_IMAGE.key
                )
            } ?: String()
            val alreadyUseTagListMutex = Mutex()
            val alreadyUseTagList = mutableListOf<String>()
            val frameTag = withContext(Dispatchers.IO) {
                lineMap.get(
                    ListSettingsForEditList.MapListPathManager.Key.VIEW_LAYOUT_TAG.key
                ).let {
                    if (
                        !it.isNullOrEmpty()
                    ) return@let it
                    editListMap.get(
                        ListSettingsForEditList.ListSettingKey.DEFAULT_FRAME_TAG.key
                    )
                }
            }
            if(
                frameTag.isNullOrEmpty()
            ) return@launch
            val mapListElInfo = withContext(Dispatchers.IO) {
                listOf(
                    "srcTitle: ${holder.srcTitle}",
                    "srcCon: ${holder.srcCon}",
                    "srcImage: ${holder.srcImage}",
                    "bindingAdapterPosition: ${holder.bindingAdapterPosition}",
                ).joinToString(" ")
            }
            val isFrameTagDulidateErr = withContext(Dispatchers.IO) frameTagCheck@ {
                val alreadyUseTagListSrc = EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
                    alreadyUseTagList,
                    alreadyUseTagListMutex
                )
                val correctFrameTag = EditComponent.AdapterSetter.tagDuplicateErrHandler(
                    context,
                    EditComponent.Template.TagManager.TagGenre.FRAME_TAG,
                    frameTag,
                    alreadyUseTagListSrc,
                    mapListElInfo,
                    plusKeyToSubKeyConWhere,
                )
                correctFrameTag?.let {
                    alreadyUseTagListMutex.withLock {
                        alreadyUseTagList.add(it)
                    }
                }
                val isDuplidateTagErr = correctFrameTag.isNullOrEmpty()
                if(
                    isDuplidateTagErr
                ) return@frameTagCheck true
                false
            }
            if(isFrameTagDulidateErr) return@launch
            val frameKeyPairsConToVarNameValueMap = withContext(Dispatchers.IO) {
                val frameKeyPairsConSrc = frameMap.get(frameTag)
                EditComponent.Template.ReplaceHolder.replaceHolder(
                    frameKeyPairsConSrc,
                    holder.srcTitle,
                    holder.srcCon,
                    holder.srcImage,
                    holder.bindingAdapterPosition,
                ).let {
                    innerFrameKeyPairsConSrc ->
                    if(
                        innerFrameKeyPairsConSrc.isNullOrEmpty()
                    ) return@let String() to emptyMap()
                    val settingActionManager = SettingActionManager()
                    val varNameToValueMap = settingActionManager.exec(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        innerFrameKeyPairsConSrc,
                        "frameTag: ${frameTag}, mapListElInfo: ${mapListElInfo}, ${plusKeyToSubKeyConWhere}",
                        editComponentListAdapterArg = this@EditComponentListAdapter
                    ) + (globalVarNameToValueMap ?: emptyMap())
                    CmdClickMap.replace(
                        innerFrameKeyPairsConSrc,
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
            withContext(Dispatchers.Main) {
                val materialCardView = holder.materialCardView.apply {
                    layoutElevation?.let {
                        elevation = it
                    }
                    layoutRadius?.let {
                        radius = it
                    }
                    val cardLinearParams =
                        layoutParams as GridLayoutManager.LayoutParams
                    layoutMargin?.let {
                        cardLinearParams.setMargins(it)
                    }
                }
                val totalLinearLayoutList = materialCardView.children.filter {
                    layout ->
                    layout is LinearLayoutCompat
                }.toList() as List<LinearLayoutCompat>
                val verticalLinearLayoutList = totalLinearLayoutList.map {
                        totalLayout ->
                    val innerVerticalLinearLayoutList = totalLayout.children.filter {
                            layout ->
                        layout is LinearLayoutCompat
                    }.toList() as List<LinearLayoutCompat>
                    innerVerticalLinearLayoutList
                }.flatten()
                verticalLinearLayoutList.forEach {
                    it.visibility = View.GONE
                }
                val horizonLinearLayoutList = verticalLinearLayoutList.map {
                    verticalLayout ->
                    val innerHorizonLinearLayoutList = verticalLayout.children.filter {
                        layout ->
                        layout is LinearLayoutCompat
                    }.toList() as List<LinearLayoutCompat>
                    innerHorizonLinearLayoutList
                }.flatten()
                horizonLinearLayoutList.forEach {
                    it.visibility = View.GONE
                }
                val frameLayoutList = horizonLinearLayoutList.map {
                    horizonLayout ->
                    val innerFrameLayoutList = horizonLayout.children.filter {
                            frameLayout ->
                        frameLayout is FrameLayout
                    }.toList() as List<FrameLayout>
                    innerFrameLayoutList
                }.flatten()
                frameLayoutList.forEach {
                    it.visibility = View.GONE
                }
            }
            val isClickEnable = withContext(Dispatchers.IO) {
                val isJsAcForFrame =
                    jsActionKeyList.any {
                        !PairListTool.getValue(
                            frameKeyPairsList,
                            it,
                        ).isNullOrEmpty()
                    }
                val onClickForFrame =
                    PairListTool.getValue(
                        frameKeyPairsList,
                        onClickKey,
                    ) != switchOff
                isJsAcForFrame
                        && onClickForFrame
            }
            CoroutineScope(Dispatchers.IO).launch{
                withContext(Dispatchers.IO) {
                    if (
                        !isClickEnable
                    ) return@withContext
                    holder.updateKeyPairListConMap(
                        frameTag,
                        frameKeyPairsCon,
                    )
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                val frameFrameLayout = withContext(Dispatchers.Main) execSetFrame@{
                    EditFrameMaker.make(
                        context,
                        holder.bkFrameLayout,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        frameKeyPairsList,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        null,
                        frameTag,
                        totalSettingValMap,
                        requestBuilderSrc,
                        density,
                        true,
                    )
                }
                let setClickOrTouch@{
                    if (
                        frameFrameLayout == null
                    ) return@setClickOrTouch
                    val isConsec =
                        withContext(Dispatchers.IO) {
                            PairListTool.getValue(
                                frameKeyPairsList,
                                onConsecKey,
                            ) == switchOn
                        }
                    if (
                        !isClickEnable
                    ) {
                        withContext(Dispatchers.Main) {
                            frameFrameLayout.setBackgroundResource(0)
                            frameFrameLayout.isClickable = false
                        }
                        return@setClickOrTouch
                    }
                    withContext(Dispatchers.Main) {
                        val outValue = TypedValue()
                        context.theme.resolveAttribute(
                            android.R.attr.selectableItemBackground,
                            outValue,
                            true
                        )
                        frameFrameLayout.setBackgroundResource(outValue.resourceId)
                        frameFrameLayout.isClickable = true
                        when (isConsec) {
                            true -> with(frameFrameLayout) {
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
            }
            val weightSumFloat = 1f

            val verticalTagToKeyPairsListToVarNameToValueMapList = withContext(Dispatchers.IO){
                EditComponent.AdapterSetter.makeLinearTagAndKeyPairsListToVarNameToValueMap(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    frameTag,
                    frameTagToVerticalKeysConList,
                    frameVarNameValueMap,
                    holder.srcTitle,
                    holder.srcCon,
                    holder.srcImage,
                    holder.bindingAdapterPosition,
                    "frameTag: ${frameTag}, ${mapListElInfo}",
                )
            }
            val totalLinearLayout = holder.totalLinearLayout
            val verticalLinerWeight = withContext(Dispatchers.IO) {
                EditComponent.AdapterSetter.culcVerticalLinerWeight(
                    verticalTagToKeyPairsListToVarNameToValueMapList
                )
            }
            val mapListElInfoForVertical =
                listOf(
                    "frameTag: ${frameTag}",
                    mapListElInfo,
                ).joinToString(", ")
            val verticalUseList = withContext(Dispatchers.IO) {
                makeVerticalUseList(
                    context,
                    frameVarNameValueMap,
                    totalLinearLayout,
                    verticalTagToKeyPairsListToVarNameToValueMapList,
                    holder.readyVerticalLayoutList,
                    verticalLinerWeight,
                    alreadyUseTagList,
                    alreadyUseTagListMutex,
                    mapListElInfoForVertical,
                )
            }
            verticalUseList.forEachIndexed verticalUseList@  {
                    verticalUseIndex, verticalUse, ->
                val verticalIndexToTag = verticalUse.first
                val verticalIndex = verticalIndexToTag.first
                val verticalTag = verticalIndexToTag.second
                val verticalVarNameToValueMap = verticalUse.second
                val verticalLinearLayout = verticalUse.third
                val horizonTagToKeyPairsListToVarNameToValueMapList = withContext(Dispatchers.IO){
                    EditComponent.AdapterSetter.makeLinearTagAndKeyPairsListToVarNameToValueMap(
                        fragment,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        verticalTag,
                        verticalTagToHorizonKeysConList,
                        verticalVarNameToValueMap,
                        holder.srcTitle,
                        holder.srcCon,
                        holder.srcImage,
                        holder.bindingAdapterPosition,
                        "verticalTag: ${verticalTag}, ${mapListElInfo}",
                    )
                }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "shorizonTagToKeyPairsListToVarNameToValueMapList.txt").absolutePath,
//                    listOf(
//                        "verticalTag: ${verticalTag}",
//                        "verticalTagToHorizonKeysConList: ${verticalTagToHorizonKeysConList}",
//                    ).joinToString("\n\n\n") + "\n\n======\n\n"
//                )
                val readyHorizonLayoutList =
                    holder.verticalIndexAndReadyHorizonLayoutList.getOrNull(verticalIndex)
                val mapListElInfoForHorizon =
                    listOf(
                        "verticalTag: ${verticalTag}",
                        mapListElInfoForVertical
                    ).joinToString(", ")
                val curHorizonIndexAndReadyContentsLayoutList =
                    holder.verticalIndexAndHorizonIndexAndReadyContentsLayoutList.getOrNull(verticalIndex)
                val horizonUseList = withContext(Dispatchers.IO) {
                    makeHorizonUseList(
                        context,
                        verticalIndex,
                        verticalLinearLayout,
                        verticalVarNameToValueMap,
                        readyHorizonLayoutList,
                        horizonTagToKeyPairsListToVarNameToValueMapList,
                        alreadyUseTagList,
                        alreadyUseTagListMutex,
                        mapListElInfoForHorizon,
                        weightSumFloat,
                    )
                }
                horizonUseList.forEachIndexed horizonUseList@{ horizonUseListIndex, registerIndexToHorizonUse ->
//                    val registerIndex = registerIndexToHorizonUse.first
                    val horizonUse = registerIndexToHorizonUse.second
                    val horizonlIndexToTag = horizonUse.first
                    val horizonIndex = horizonlIndexToTag.first
                    val horizonTag = horizonlIndexToTag.second
                    val horizonVarNameToValueMap = horizonUse.second
                    val horizonLinearLayout = horizonUse.third
                    CoroutineScope(Dispatchers.IO).launch {
//                        FileSystems.updateFile(
//                            File(
//                                UsePath.cmdclickDefaultAppDirPath,
//                                "lregisterIndex${verticalUseIndex}${horizonUseListIndex}${registerIndex}.txt"
//                            ).absolutePath,
//                            listOf(
//                                "registerIndex: ${registerIndex}",
//                                "horizonTag: ${horizonTag}",
//                                "horizonVarNameToValueMap: ${horizonVarNameToValueMap}",
//                            ).joinToString("\n")
//                        )
                        val horizonTagToContentsKeysListMapWithReplace =
                            horizonTagToContentsKeysListMap.map {
                                val key = CmdClickMap.replace(
                                    it.key,
                                    horizonVarNameToValueMap,
                                )
                                key to it.value
                            }.toMap()
                        val readyContentsLayoutList =
                            curHorizonIndexAndReadyContentsLayoutList?.getOrNull(horizonIndex)
                        horizonTagToContentsKeysListMapWithReplace.get(horizonTag)
                            ?.forEachIndexed setContents@{ contentsIndex, contentsKeyValues ->
                            val contentsTagToKeyPairsList = withContext(Dispatchers.IO) {
                                EditComponent.AdapterSetter.makeLinearFrameTagToKeyPairsList(
                                    contentsKeyValues,
                                    horizonVarNameToValueMap,
                                    holder.srcTitle,
                                    holder.srcCon,
                                    holder.srcImage,
                                    holder.bindingAdapterPosition,
                                )
                            }
                            val contentsKeyValueSize = withContext(Dispatchers.IO) {
                                EditComponent.AdapterSetter.culcLinearKeyValueSize(
                                    contentsTagToKeyPairsList,
                                )
                            }
                            val layoutWeight = weightSumFloat / contentsKeyValueSize
                            val frameLayoutStartId = 70000
                            val mapListElInfoForContentsTag =
                                listOf(
                                    "horizonTag: ${horizonTag}",
                                    mapListElInfoForHorizon
                                ).joinToString(", ")
                            contentsTagToKeyPairsList.mapIndexed execSetContents@{ execSetContentsIndex, contentsTagToKeyPairs ->
                                async {
                                    val curContentsLayoutId =
                                        frameLayoutStartId + execSetContentsIndex
                                    val contentsTagSrc = contentsTagToKeyPairs.first
                                    val contentsKeyPairsListConSrc =
                                        contentsTagToKeyPairs.second
                                    val varNameToValueMap =
                                        withContext(Dispatchers.IO) updateLinearKeyParsListCon@{
                                            EditComponent.AdapterSetter.makeFrameVarNameToValueMap(
                                                fragment,
                                                fannelInfoMap,
                                                setReplaceVariableMap,
                                                busyboxExecutor,
                                                this@EditComponentListAdapter,
                                                horizonVarNameToValueMap,
                                                "contentsTagSrc: ${contentsTagSrc}, ${mapListElInfoForContentsTag}",
                                                contentsKeyPairsListConSrc,
                                                holder.srcTitle,
                                                holder.srcCon,
                                                holder.srcImage,
                                                holder.bindingAdapterPosition,
                                            )
                                        }
                                    val contentsVarNameToValueMap =
                                        horizonVarNameToValueMap + varNameToValueMap
                                    val contentsTag = CmdClickMap.replace(
                                        contentsTagSrc,
                                        contentsVarNameToValueMap
                                    )
                                    val isContentsTagErr =
                                        withContext(Dispatchers.IO) contentsTagCheck@{
                                            val tagGenre =
                                                EditComponent.Template.TagManager.TagGenre.CONTENTS_TAG

                                            ListSettingsForEditList.ViewLayoutCheck.isTagBlankErr(
                                                context,
                                                contentsTag,
                                                mapListElInfo,
                                                tagGenre
                                            )
                                            val alreadyUseTagListSrc =   EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
                                                alreadyUseTagList,
                                                alreadyUseTagListMutex
                                            )
                                            val correctContentsTag =
                                                EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                                    context,
                                                    tagGenre,
                                                    contentsTag,
                                                    alreadyUseTagListSrc,
                                                    mapListElInfoForContentsTag,
                                                    String(),
                                                )
                                            correctContentsTag?.let {
                                                alreadyUseTagListMutex.withLock {
                                                    alreadyUseTagList.add(it)
                                                }
                                            }
                                            val isDuplicateTagErr =
                                                correctContentsTag.isNullOrEmpty()
                                            if (
                                                isDuplicateTagErr
                                            ) return@contentsTagCheck true
                                            false
                                        }
                                    if (isContentsTagErr) {
                                        return@async
                                    }
                                    val contentsKeyPairsListCon =
                                        contentsKeyPairsListConSrc?.let {
                                            CmdClickMap.replace(
                                                it,
                                                contentsVarNameToValueMap
                                            )
                                        }
                                    val contentsKeyPairsList =
                                        withContext(Dispatchers.IO) {
                                            makeLinearFrameKeyPairsList(
                                                contentsKeyPairsListCon
                                            )
                                        }
                                    withContext(Dispatchers.Main) setLinearFrameLayout@{
                                        withContext(Dispatchers.IO) {
                                            PairListTool.getValue(
                                                contentsKeyPairsList,
                                                enableKey,
                                            )
                                        }.let { enableStr ->
                                            if (
                                                enableStr == switchOff
                                            ) return@setLinearFrameLayout null
                                        }
                                        val extractContentsFrameLayout =
                                            readyContentsLayoutList?.getOrNull(
                                                execSetContentsIndex
                                            ) ?: horizonLinearLayout?.findViewById<FrameLayout>(
                                                curContentsLayoutId
                                            )
                                        val contentsFrameLayout = extractContentsFrameLayout
                                            ?: let {
                                                withContext(Dispatchers.Main) {
                                                    EditComponent.AdapterSetter.makeContentsFrameLayout(
                                                        context
                                                    )
                                                }
                                            }
                                        if (extractContentsFrameLayout == null) {
                                            withContext(Dispatchers.Main) {
                                                horizonLinearLayout?.addView(
                                                    contentsFrameLayout
                                                )
                                            }
                                        }
                                        CoroutineScope(Dispatchers.Main).launch {
                                            EditFrameMaker.make(
                                                context,
                                                contentsFrameLayout,
                                                fannelInfoMap,
                                                setReplaceVariableMap,
                                                busyboxExecutor,
                                                contentsKeyPairsList,
                                                0,
                                                layoutWeight,
                                                contentsTag,
                                                totalSettingValMap,
                                                requestBuilderSrc,
                                                density,
                                            )
                                        }
                                        CoroutineScope(Dispatchers.Main).launch {
                                            val isConsec = withContext(Dispatchers.IO) {
                                                PairListTool.getValue(
                                                    contentsKeyPairsList,
                                                    onConsecKey,
                                                ) == EditComponent.Template.switchOn
                                            }
                                            val onClick = withContext(Dispatchers.IO) {
                                                PairListTool.getValue(
                                                    contentsKeyPairsList,
                                                    onClickKey,
                                                ) != switchOff
                                            }
                                            val isJsAc = withContext(Dispatchers.IO) {
                                                jsActionKeyList.any { jsActionKey ->
                                                    !PairListTool.getValue(
                                                        contentsKeyPairsList,
                                                        jsActionKey,
                                                    ).isNullOrEmpty()
                                                }
                                            }
                                            CoroutineScope(Dispatchers.IO).launch {
                                                holder.updateKeyPairListConMap(
                                                    contentsTag,
                                                    contentsKeyPairsListCon
                                                )
                                            }
                                            withContext(Dispatchers.IO) setClick@{
                                                val isJsAcClick = !isJsAc
                                                        && contentsFrameLayout.tag != null
                                                if (
                                                    isJsAcClick
                                                    || !onClick
                                                ) {
                                                    withContext(Dispatchers.Main) {
                                                        contentsFrameLayout.setBackgroundResource(0)
                                                        contentsFrameLayout.isClickable = false
                                                    }
                                                    return@setClick
                                                }
                                                val outValue = withContext(Dispatchers.IO) {
                                                    TypedValue()
                                                }
                                                val clickViewList =withContext(Dispatchers.IO) {
                                                    context.theme.resolveAttribute(
                                                        android.R.attr.selectableItemBackground,
                                                        outValue,
                                                        true
                                                    )
                                                    contentsFrameLayout.children.filter {
                                                        it is OutlineTextView
                                                                || it is AppCompatImageView
                                                    }
                                                }

                                                withContext(Dispatchers.Main) {
                                                    clickViewList.forEach { clickView ->
                                                        clickView.setBackgroundResource(outValue.resourceId)
                                                        clickView.isClickable = true
                                                        when (isConsec) {
                                                            true ->
                                                                with(clickView) {
                                                                    setOnTouchListener(android.view.View.OnTouchListener { v, event ->
                                                                        when (event.action) {
                                                                            android.view.MotionEvent.ACTION_DOWN -> {
                                                                                editAdapterTouchDownListener?.onEditAdapterTouchDown(
                                                                                    contentsFrameLayout,
                                                                                    holder,
                                                                                    editListPosition
                                                                                )
                                                                            }

                                                                            android.view.MotionEvent.ACTION_UP,
                                                                            android.view.MotionEvent.ACTION_CANCEL,
                                                                                -> {
                                                                                editAdapterTouchUpListener?.onEditAdapterTouchUp(
                                                                                    contentsFrameLayout,
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
                                                                    contentsFrameLayout,
                                                                    holder,
                                                                    editListPosition,
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lchannel.txt").absolutePath,
//                    listOf(
//                        "contentsUseList: ${contentsUseList}",
//                    ).joinToString("\n\n\n") + "\n\n==========\n\n"
//                )
                    }
                }
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
        fragment: Fragment,
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

    private suspend fun makeVerticalUseList(
        context: Context,
        frameVarNameValueMap: Map<String, String>,
        totalLinearLayout: LinearLayoutCompat,
        verticalTagToKeyPairsListToVarNameToValueMapList: List<Pair<String, Pair<List<Pair<String, String>>, Map<String, String>>>>,
        readyVerticalLayoutList: List<LinearLayoutCompat>,
        verticalLinerWeight: Float,
        alreadyUseTagList: MutableList<String>,
        alreadyUseTagListMutex: Mutex,
        mapListElInfoForVertical: String,
    ): List<Triple<Pair<Int, String>, Map<String, String>, LinearLayoutCompat>> {
        val extraVerticalStartId = 40000
        val switchOff = EditComponent.Template.switchOff
        val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
        val weightSumFloat = 1f
        val verticalChannel = Channel<
                Pair<
                        Int,
                        Triple<
                                Pair<Int, String>,
                                Map<String, String>,
                                LinearLayoutCompat,
                                >
                        >
                >(verticalTagToKeyPairsListToVarNameToValueMapList.size)
        withContext(Dispatchers.IO) {
            val jobList = verticalTagToKeyPairsListToVarNameToValueMapList.mapIndexed setVertical@{ verticalIndex, verticalTagToKeyPairsListToVarNameToValueMap ->
                async {
                    val curExtraVerticalLinearId = extraVerticalStartId + verticalIndex

                    val keyPairsListToVarNameToValueMapForVertical =
                        verticalTagToKeyPairsListToVarNameToValueMap.second
                    val verticalKeyPairs = keyPairsListToVarNameToValueMapForVertical.first
                    val verticalVarNameToValueMap =
                        keyPairsListToVarNameToValueMapForVertical.second + frameVarNameValueMap
                    val verticalTag = CmdClickMap.replace(
                        verticalTagToKeyPairsListToVarNameToValueMap.first,
                        verticalVarNameToValueMap,
                    )
                    val isVerticalTagDuplicateErr =
                        withContext(Dispatchers.IO) verticalTagCheck@{
                            val alreadyUseTagListSrc =
                                EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
                                    alreadyUseTagList,
                                    alreadyUseTagListMutex
                                )
                            val correctVerticalTag =
                                EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                    context,
                                    EditComponent.Template.TagManager.TagGenre.VERTICAL_TAG,
                                    verticalTag,
                                    alreadyUseTagListSrc,
                                    mapListElInfoForVertical,
                                    String(),
                                )
                            correctVerticalTag?.let {
                                alreadyUseTagListMutex.withLock {
                                    alreadyUseTagList.add(it)
                                }
                            }
                            val isDuplicateTagErr = correctVerticalTag.isNullOrEmpty()
                            if (
                                isDuplicateTagErr
                            ) return@verticalTagCheck true
                            false
                        }
                    if (isVerticalTagDuplicateErr) return@async
                    withContext(Dispatchers.IO) {
                        EditComponent.AdapterSetter.isNotLinearKeyErr(
                            context,
                            EditComponent.Template.LayoutKey.VERTICAL.key,
                            verticalKeyPairs,
                            "verticalTag: ${verticalTag}, ${mapListElInfoForVertical}",
                            String(),
                        )
                    }.let { isNotVerticalKeyErr ->
                        if (isNotVerticalKeyErr) return@async
                    }
                    val isVerticalEnable = withContext(Dispatchers.IO) {
                        PairListTool.getValue(
                            verticalKeyPairs,
                            enableKey,
                        ).let { enableStr ->
                            enableStr != switchOff
                        }
                    }
                    if (
                        !isVerticalEnable
                    ) return@async
                    val extractVerticalLinear =
                        readyVerticalLayoutList.getOrNull(verticalIndex)
                            ?: totalLinearLayout.findViewById<LinearLayoutCompat>(
                                curExtraVerticalLinearId
                            )
                    val verticalLinearLayout =
                        withContext(Dispatchers.Main) {
                            extractVerticalLinear
                                ?: let {
                                    LinearLayoutCompat(context).apply {
                                        id = curExtraVerticalLinearId
                                        val verticalParams = withContext(Dispatchers.IO){
                                            LinearLayoutCompat.LayoutParams(
                                                0,
                                                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                                            ).apply {
                                                weight = verticalLinerWeight
                                            }
                                        }
                                        layoutParams = verticalParams
                                    }
                                }.apply {
                                    weightSum = weightSumFloat
                                }
                        }
                    if (extractVerticalLinear == null) {
                        withContext(Dispatchers.Main) {
                            totalLinearLayout.addView(verticalLinearLayout)
                        }

                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        EditComponent.AdapterSetter.setVerticalLinear(
                            context,
                            extractVerticalLinear,
                            verticalKeyPairs,
                            verticalLinerWeight,
                            verticalTag,
                            density,
                        )
                    }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lreadyVerticalLinear.txt").absolutePath,
//                    listOf(
//                        "frameTag: ${frameTag}",
//                        "verticalTag: ${verticalTag}",
//                        "verticalIndex: ${verticalIndex}",
//                        "isreadyVerticalLinear: ${extractVerticalLinear is LinearLayoutCompat}",
//                    ).joinToString("\n")
//                )
                    withContext(Dispatchers.IO) {
                        verticalChannel.send(
                            Pair(
                                verticalIndex,
                                Triple(
                                    Pair(
                                        verticalIndex,
                                        verticalTag
                                    ),
                                    verticalVarNameToValueMap.toMap(),
                                    verticalLinearLayout,
                                )
                            )
                        )
                    }
                }
            }
            jobList.forEach { it.await() }
            verticalChannel.close()
        }
        val verticalUseList = withContext(Dispatchers.IO) {
            val verticalUseListSrc = mutableListOf<
                    Pair<
                            Int,
                            Triple<
                                    Pair<Int, String>,
                                    Map<String, String>,
                                    LinearLayoutCompat,
                                    >
                            >
                    >()
            for (v in verticalChannel) {
                verticalUseListSrc.add(v)
            }
            verticalUseListSrc.sortBy { it.first }
            verticalUseListSrc.map {
                it.second
            }
        }
        return verticalUseList
    }

    private suspend fun makeHorizonUseList(
        context: Context,
        verticalIndex: Int,
        verticalLinearLayout: LinearLayoutCompat,
        verticalVarNameToValueMap: Map<String, String>,
        readyHorizonLayoutList: List<LinearLayoutCompat>?,
        horizonTagToKeyPairsListToVarNameToValueMapList: List<Pair<String, Pair<List<Pair<String, String>>, Map<String, String>>>>,
        alreadyUseTagList: MutableList<String>,
        alreadyUseTagListMutex: Mutex,
        mapListElInfoForHorizon: String,
        weightSumFloat: Float,
    ):  MutableList<
            Pair<
                    String,
                    Triple<
                            Pair<Int, String>,
                            Map<String, String>,
                            LinearLayoutCompat?,
                            >
                    >
            > {
        val switchOff = EditComponent.Template.switchOff
        val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
        val horizonLayoutStartId = 50000
        val horizonChannel = Channel<
                Pair<
                        String,
                        Triple<
                                Pair<Int, String>,
                                Map<String, String>,
                                LinearLayoutCompat?,
                                >
                        >
                >(horizonTagToKeyPairsListToVarNameToValueMapList.size)
        withContext(Dispatchers.IO) {
            val jobList =
                horizonTagToKeyPairsListToVarNameToValueMapList.mapIndexed setHorizon@{ horizonIndex, horizonTagToKeyPairsListToVarNameToValueMap ->
                    async {
                        val curExtraHorizonLinearId = horizonLayoutStartId + horizonIndex
                        val keyPairsListToVarNameToValueMapForHorizon =
                            horizonTagToKeyPairsListToVarNameToValueMap.second
                        val horizonKeyPairs =
                            keyPairsListToVarNameToValueMapForHorizon.first
                        val horizonVarNameToValueMap =
                            keyPairsListToVarNameToValueMapForHorizon.second + verticalVarNameToValueMap
                        val horizonTag = CmdClickMap.replace(
                            horizonTagToKeyPairsListToVarNameToValueMap.first,
                            horizonVarNameToValueMap
                        )
                        val alreadyUseTagListSrc =  EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
                            alreadyUseTagList,
                            alreadyUseTagListMutex
                        )
                        val correctHorizonTag =
                            EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                context,
                                EditComponent.Template.TagManager.TagGenre.HORIZON_TAG,
                                horizonTag,
                                alreadyUseTagListSrc,
                                mapListElInfoForHorizon,
                                String(),
                            )
                        correctHorizonTag?.let {
                            alreadyUseTagListMutex.withLock {
                                alreadyUseTagList.add(it)
                            }
                        }
                        val isDuplidateTagErr = correctHorizonTag.isNullOrEmpty()
                        if (
                            isDuplidateTagErr
                        ) return@async

                        withContext(Dispatchers.IO) {
                            EditComponent.AdapterSetter.isNotLinearKeyErr(
                                context,
                                EditComponent.Template.LayoutKey.HORIZON.key,
                                horizonKeyPairs,
                                "horizonTag: ${horizonTag}, ${mapListElInfoForHorizon}",
                                String(),
                            )
                        }.let { isNotHorizonKeyErr ->
                            if (isNotHorizonKeyErr) return@async
                        }
                        val isHorizonEnable = withContext(Dispatchers.IO) {
                            PairListTool.getValue(
                                horizonKeyPairs,
                                enableKey,
                            ).let { enableStr ->
                                enableStr != switchOff
                            }
                        }
                        if (
                            !isHorizonEnable
                        ) return@async
                        val extractHorizonLinear =
                            let {
                                readyHorizonLayoutList?.getOrNull(horizonIndex)
                                    ?: verticalLinearLayout.findViewById<LinearLayoutCompat>(
                                        curExtraHorizonLinearId
                                    )
                            }
                        val horizonLinearLayout = withContext(Dispatchers.Main) {
                            let {
                                extractHorizonLinear
                                    ?: LinearLayoutCompat(context).apply {
                                        id = curExtraHorizonLinearId
                                        val horizonParams = withContext(Dispatchers.IO){
                                            LinearLayoutCompat.LayoutParams(
                                                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                                                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                                            )
                                        }
                                        layoutParams = horizonParams
                                    }
                            }.apply {
                                weightSum = weightSumFloat
                            }
                        }
                        if (extractHorizonLinear == null) {
                            withContext(Dispatchers.Main) {
                                verticalLinearLayout.addView(horizonLinearLayout)
                            }
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            EditComponent.AdapterSetter.setHorizonLinear(
                                context,
                                extractHorizonLinear,
                                horizonKeyPairs,
                                horizonTag,
                                density,
                            )
                        }
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "smakeHorizonLinear.txt").absolutePath,
//                        listOf(
//                            "verticalTag: ${verticalTag}",
//                            "isNotExtract: ${extractHorizonLinear == null}",
//                            "verticalTagToHorizonKeysConList: ${verticalTagToHorizonKeysConList}",
//                            "horizonKeyPairs: ${horizonKeyPairs}",
//                            "horizonTag: ${horizonTag}",
//                            "isVisiblity: ${horizonLinearLayout.visibility == View.VISIBLE}"
//                        ).joinToString("\n\n\n") + "\n\n======\n\n"
//                    )
                        withContext(Dispatchers.IO) {
                            val registerIndex = "${verticalIndex}${horizonIndex}"
                            horizonChannel.send(
                                Pair(
                                    registerIndex,
                                    Triple(
                                        Pair(
                                            horizonIndex,
                                            horizonTag
                                        ),
                                        horizonVarNameToValueMap.toMap(),
                                        horizonLinearLayout,
                                    )
                                )
                            )
                        }
                    }
                }
            jobList.forEach { it.await() }
            horizonChannel.close()
        }
        val horizonUseList = withContext(Dispatchers.IO) {
            val horizonUseListSrc = mutableListOf<
                    Pair<
                            String,
                            Triple<
                                    Pair<Int, String>,
                                    Map<String, String>,
                                    LinearLayoutCompat?,
                                    >
                            >
                    >()
            for (v in horizonChannel) {
                horizonUseListSrc.add(v)
            }
            horizonUseListSrc.sortBy { it.first }
            horizonUseListSrc
        }
        return horizonUseList
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

//    private fun makeFileConList(
//        fileName: String,
//    ): List<String> {
//        if (
//            editListConfigMap.isNullOrEmpty()
//        ) return emptyList()
//        return emptyList()
//    }

//    private suspend fun setDescView(
//        fileDescTextView: AppCompatTextView,
//        fileNameOrInstallFannelLine: String,
//        fileCon: String,
//    ){
//        val makeFileDescArgsMaker = EditListConfig.MakeFileDescArgsMaker(
////            filterDir,
//            fileNameOrInstallFannelLine,
//            fileCon,
//            editListConfigMap,
//            busyboxExecutor,
//        )
//        val descCon = EditListConfig.makeFileDesc(
//            makeFileDescArgsMaker,
//        )
//        withContext(Dispatchers.Main) {
//            when (descCon.isNullOrEmpty()) {
//                true -> fileDescTextView.isVisible = false
//                else -> fileDescTextView.text = descCon
//            }
//        }
//    }

//    private fun setFileContentsBackColor(
//        fileConList: List<String>,
////        fileName: String,
//        editExecuteValueForInstallFannel: String,
//    ): Int {
//        if(
//            context == null
//        ) return R.color.fannel_icon_color
//        if(
//            editExecuteValueForInstallFannel == editExecuteAlways
//        ) return R.color.terminal_color
//        val settingVariableList = CommandClickVariables.extractValListFromHolder(
//            fileConList,
//            CommandClickScriptVariable.SETTING_SEC_START,
//            CommandClickScriptVariable.SETTING_SEC_END,
//        )
//        val editExecuteValue = SettingVariableReader.getStrValue(
//            settingVariableList,
//            CommandClickScriptVariable.EDIT_EXECUTE,
//            CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
//        )
//        if(
//            editExecuteValue
//            == SettingVariableSelects.EditExecuteSelects.ALWAYS.name
//        ) return R.color.terminal_color
//        return R.color.fannel_icon_color
//    }

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
