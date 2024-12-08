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
import kotlinx.coroutines.Deferred
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

    private class KeyPairListConMapUpdater {
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
        private val keyPairListConMapUpdater = KeyPairListConMapUpdater()
        suspend fun updateKeyPairListConMap(
            tagKey: String,
            keyToSubKeyCon: String?
        ){
            keyPairListConMapUpdater.update(
                tagKey,
                keyToSubKeyCon
            )
        }


        suspend fun getKeyPairListConMap(
        ): Map<String, String?> {
            return keyPairListConMapUpdater.get()
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
    private val delayTime = 1000L
    private val weightSumFloat = 1f
    private val outValue = let {
        val outValueSrc = TypedValue()
        context?.theme?.resolveAttribute(
            android.R.attr.selectableItemBackground,
            outValueSrc,
            true
        )
        outValueSrc
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
            val initLineMapEl = async {
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
            }
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
            initLineMapEl.await()
            val viewInitJob = async {
                withContext(Dispatchers.Main) {
                    val materialCardView = holder.materialCardView
                    val totalLinearLayoutList = materialCardView.children.filter { layout ->
                        layout is LinearLayoutCompat
                    }.toList() as List<LinearLayoutCompat>
                    val verticalLinearLayoutList = totalLinearLayoutList.map { totalLayout ->
                        val innerVerticalLinearLayoutList = totalLayout.children.filter { layout ->
                            layout is LinearLayoutCompat
                        }.toList() as List<LinearLayoutCompat>
                        innerVerticalLinearLayoutList
                    }.flatten()
                    val horizonLinearLayoutList = verticalLinearLayoutList.map { verticalLayout ->
                        val innerHorizonLinearLayoutList =
                            verticalLayout.children.filter { layout ->
                                layout is LinearLayoutCompat
                            }.toList() as List<LinearLayoutCompat>
                        innerHorizonLinearLayoutList
                    }.flatten()
                    val frameLayoutList = horizonLinearLayoutList.map { horizonLayout ->
                        val innerFrameLayoutList = horizonLayout.children.filter { frameLayout ->
                            frameLayout is FrameLayout
                        }.toList() as List<FrameLayout>
                        innerFrameLayoutList
                    }.flatten()
                    val cardViewSettingJob = async {
                        materialCardView.apply {
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
                    }
                    val hideVerticalLinearLayoutListJob = async {
                        verticalLinearLayoutList.forEach {
                            it.visibility = View.GONE
                        }
                    }
                    val hideHorizonLinearLayoutList = async {
                        horizonLinearLayoutList.forEach {
                            it.visibility = View.GONE
                        }
                    }
                    val hideFrameLayoutList = async {
                        frameLayoutList.forEach {
                            it.visibility = View.GONE
                        }
                    }
                    listOf(
                        cardViewSettingJob,
                        hideVerticalLinearLayoutListJob,
                        hideHorizonLinearLayoutList,
                        hideFrameLayoutList
                    ).forEach { it.await() }
                }
            }
            val mapListElInfo = withContext(Dispatchers.IO) {
                listOf(
                    "srcTitle: ${holder.srcTitle}",
                    "srcCon: ${holder.srcCon}",
                    "srcImage: ${holder.srcImage}",
                    "bindingAdapterPosition: ${holder.bindingAdapterPosition}",
                ).joinToString(" ")
            }
            val isFrameTagDulidateErrJob = async {
                withContext(Dispatchers.IO) frameTagCheck@{
                    val alreadyUseTagListSrc =
                        EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
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
                    if (
                        isDuplidateTagErr
                    ) return@frameTagCheck true
                    false
                }
            }
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
            viewInitJob.await()
            if(
                isFrameTagDulidateErrJob.await()
            ) return@launch
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
//                    withContext(Dispatchers.IO){
//                        delay(delayTime)
//                    }
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
            val horizonChannel = Channel<
                    Triple<
                            String,
                            Triple<
                                    Pair<Int, String>,
                                    Map<String, String>,
                                    LinearLayoutCompat?,
                                    >,
                            Pair<
                                    String,
                                    List<List<FrameLayout>>?,
                                    >,
                            >
                    >(100)
            val contentsChannel = Channel<
                    Triple<
                            String,
                            Pair<
                                    Map<String, String>,
                                    List<Pair<String, String?>>,
                                    >,
                            Triple<
                                    LinearLayoutCompat?,
                                    List<FrameLayout>?,
                                    Float,
                                    >,
                            >
                    >(100)
            CoroutineScope(Dispatchers.IO).launch {
                publushVerticalChannel(
                    context,
                    frameVarNameValueMap,
                    totalLinearLayout,
                    verticalChannel,
                    verticalTagToKeyPairsListToVarNameToValueMapList,
                    holder.readyVerticalLayoutList,
                    verticalLinerWeight,
                    alreadyUseTagList,
                    alreadyUseTagListMutex,
                    mapListElInfoForVertical,
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                val asyncTaskList = mutableListOf<Deferred<Unit>>()
                for (indexToUse in verticalChannel) {
                    val verticalUse = indexToUse.second
                    val job = async {
                        val verticalIndexToTag = verticalUse.first
                        val verticalIndex = verticalIndexToTag.first
    //                FileSystems.updateFile(
    //                    File(UsePath.cmdclickDefaultAppDirPath, "shorizonTagToKeyPairsListToVarNameToValueMapList.txt").absolutePath,
    //                    listOf(
    //                        "verticalTag: ${verticalTag}",
    //                        "verticalTagToHorizonKeysConList: ${verticalTagToHorizonKeysConList}",
    //                    ).joinToString("\n\n\n") + "\n\n======\n\n"
    //                )
                        val readyHorizonLayoutList =
                            holder.verticalIndexAndReadyHorizonLayoutList.getOrNull(verticalIndex)
                        val curHorizonIndexAndReadyContentsLayoutList =
                            holder.verticalIndexAndHorizonIndexAndReadyContentsLayoutList.getOrNull(verticalIndex)
                            withContext(Dispatchers.IO) {
                                publishHorizonChannel(
                                    fragment,
                                    indexToUse,
                                    holder.srcTitle,
                                    holder.srcCon,
                                    holder.srcImage,
                                    holder.bindingAdapterPosition,
                                    horizonChannel,
                                    readyHorizonLayoutList,
                                    curHorizonIndexAndReadyContentsLayoutList,
                                    alreadyUseTagList,
                                    alreadyUseTagListMutex,
                                    mapListElInfo,
                                    mapListElInfoForVertical,
                                    weightSumFloat
                                )
                            }
                        }
                    asyncTaskList.add(job)
                }
                asyncTaskList.forEach {
                    it.await()
                }
                horizonChannel.close()
            }
            CoroutineScope(Dispatchers.IO).launch {
                val asyncTaskList = mutableListOf<Deferred<Unit>>()
                for (registerIndexToHorizonUseToExtraInfo in horizonChannel) {
                    val job = async {
                        publishContents(
                            registerIndexToHorizonUseToExtraInfo,
                            holder.srcTitle,
                            holder.srcCon,
                            holder.srcImage,
                            holder.bindingAdapterPosition,
                            contentsChannel,
                        )
                    }
                    asyncTaskList.add(job)
                }
                asyncTaskList.forEach {
                    it.await()
                }
                contentsChannel.close()
            }

            CoroutineScope(Dispatchers.IO).launch {
                for (contentsUseTriple in contentsChannel) {
                    val mapListElInfoForContentsTag = contentsUseTriple.first
                    val horizontalMapToContentsTagPairsKeysListMapWithReplace =
                        contentsUseTriple.second
                    val horizonVarNameToValueMap =
                        horizontalMapToContentsTagPairsKeysListMapWithReplace.first
                    val contentsTagToKeyPairsList =
                        horizontalMapToContentsTagPairsKeysListMapWithReplace.second
                    val horizonLinearLayoutToReadyContentsLayoutListToLayoutWeight =
                        contentsUseTriple.third
                    val horizonLinearLayout =
                        horizonLinearLayoutToReadyContentsLayoutListToLayoutWeight.first
                    val readyContentsLayoutList =
                        horizonLinearLayoutToReadyContentsLayoutListToLayoutWeight.second
                    val layoutWeight =
                        horizonLinearLayoutToReadyContentsLayoutListToLayoutWeight.third

                    val frameLayoutStartId = 70000
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
                            val isContentsTagErrJob =
                                async {
                                    val tagGenre =
                                        EditComponent.Template.TagManager.TagGenre.CONTENTS_TAG
                                    val isTagBlankErrJob = async {
                                        withContext(Dispatchers.IO) {
                                            val isTagBlankErrJob =
                                                ListSettingsForEditList.ViewLayoutCheck.isTagBlankErr(
                                                    context,
                                                    contentsTag,
                                                    mapListElInfo,
                                                    tagGenre
                                                )
                                            isTagBlankErrJob
                                        }
                                    }
                                    val isDuplicateTagErrJob = async {
                                        withContext(Dispatchers.IO) {
                                            val alreadyUseTagListSrc =
                                                EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
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
                                            isDuplicateTagErr
                                        }
                                    }
                                    isTagBlankErrJob.await()
                                            || isDuplicateTagErrJob.await()
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
                            if(
                                isContentsTagErrJob.await()
                            ) return@async
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
                                    clickHandler(
                                        holder,
                                        editListPosition,
                                        contentsKeyPairsList,
                                        contentsTag,
                                        contentsKeyPairsListCon,
                                        contentsFrameLayout,
                                    )
                                }
                            }
                            if (
                                isContentsTagErrJob.await()
                            ) {
                                return@async
                            }
                        }
                    }
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

    private suspend fun publushVerticalChannel(
        context: Context,
        frameVarNameValueMap: Map<String, String>,
        totalLinearLayout: LinearLayoutCompat,
        verticalChannel: Channel<
                Pair<
                        Int,
                        Triple<
                                Pair<Int, String>,
                                Map<String, String>,
                                LinearLayoutCompat,
                                >
                        >
                >,
        verticalTagToKeyPairsListToVarNameToValueMapList: List<Pair<String, Pair<List<Pair<String, String>>, Map<String, String>>>>,
        readyVerticalLayoutList: List<LinearLayoutCompat>,
        verticalLinerWeight: Float,
        alreadyUseTagList: MutableList<String>,
        alreadyUseTagListMutex: Mutex,
        mapListElInfoForVertical: String,
    )
//    : List<Triple<Pair<Int, String>, Map<String, String>, LinearLayoutCompat>>
    {
        val extraVerticalStartId = 40000
        val switchOff = EditComponent.Template.switchOff
        val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
        val weightSumFloat = 1f
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
                    val isVerticalTagErrCheckJob =
                        async {
                          val isDuplicateTagErrJob = async verticalTagCheck@ {
                              withContext(Dispatchers.IO) {
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
                                  isDuplicateTagErr
                              }
                            }
                            val isNotVerticalKeyErrJob = async {
                                withContext(Dispatchers.IO) {
                                    EditComponent.AdapterSetter.isNotLinearKeyErr(
                                        context,
                                        EditComponent.Template.LayoutKey.VERTICAL.key,
                                        verticalKeyPairs,
                                        "verticalTag: ${verticalTag}, ${mapListElInfoForVertical}",
                                        String(),
                                    )
                                }
                            }
                            isDuplicateTagErrJob.await()
                                    || isNotVerticalKeyErrJob.await()
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
                    if(
                        isVerticalTagErrCheckJob.await()
                    ) return@async
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
                    if(
                        isVerticalTagErrCheckJob.await()
                    ) return@async
                }
            }
            jobList.forEach { it.await() }
            verticalChannel.close()
        }
    }
    private suspend fun publishHorizonChannel(
        fragment: Fragment?,
        indexToUse: Pair<
                Int,
                Triple<
                        Pair<Int, String>,
                        Map<String, String>,
                        LinearLayoutCompat
                        >
                >,
        srcTitle: String,
        srcCon: String,
        srcImage: String,
        bindingAdapterPosition: Int,
        horizonChannel: Channel<
                Triple<
                        String,
                        Triple<
                                Pair<Int, String>,
                                Map<String, String>,
                                LinearLayoutCompat?,
                                >,
                        Pair<
                                String,
                                List<List<FrameLayout>>?
                                >,
                        >,
                >,
        readyHorizonLayoutList: List<LinearLayoutCompat>?,
        curHorizonIndexAndReadyContentsLayoutList: List<List<FrameLayout>>?,
        alreadyUseTagList: MutableList<String>,
        alreadyUseTagListMutex: Mutex,
        mapListElInfo: String,
        mapListElInfoForVertical: String,
        weightSumFloat: Float,
    ) {
        val context = fragment?.context
            ?: return
        val switchOff = EditComponent.Template.switchOff
        val enableKey = EditComponent.Template.EditComponentKey.ENABLE.key
        val horizonLayoutStartId = 50000
        val verticalUse = indexToUse.second
        val verticalIndexToTag = verticalUse.first
        val verticalIndex = verticalIndexToTag.first
        val verticalTag = verticalIndexToTag.second
        val verticalVarNameToValueMap = verticalUse.second
        val verticalLinearLayout = verticalUse.third
        val horizonTagToKeyPairsListToVarNameToValueMapList = withContext(Dispatchers.IO) {
            EditComponent.AdapterSetter.makeLinearTagAndKeyPairsListToVarNameToValueMap(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                verticalTag,
                verticalTagToHorizonKeysConList,
                verticalVarNameToValueMap,
                srcTitle,
                srcCon,
                srcImage,
                bindingAdapterPosition,
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
        val mapListElInfoForHorizon =
            listOf(
                "verticalTag: ${verticalTag}",
                mapListElInfoForVertical
            ).joinToString(", ")
        withContext(Dispatchers.IO) {
            val setHorizonJob = horizonTagToKeyPairsListToVarNameToValueMapList.mapIndexed setHorizon@{ horizonIndex, horizonTagToKeyPairsListToVarNameToValueMap ->
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
                    val isHorizonTagErrJob = async {
                        val isDuplicateTagErrJob = async {
                            withContext(Dispatchers.IO) {
                                val alreadyUseTagListSrc =
                                    EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
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
                                isDuplidateTagErr
                            }
                        }
                        val isNotLinearKeyErrJob = async {
                            withContext(Dispatchers.IO) {
                                EditComponent.AdapterSetter.isNotLinearKeyErr(
                                    context,
                                    EditComponent.Template.LayoutKey.HORIZON.key,
                                    horizonKeyPairs,
                                    "horizonTag: ${horizonTag}, ${mapListElInfoForHorizon}",
                                    String(),
                                )
                            }
                        }
                        isDuplicateTagErrJob.await()
                                || isNotLinearKeyErrJob.await()
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
                    if(
                        isHorizonTagErrJob.await()
                    ) return@async
                    val horizonLinearLayout = withContext(Dispatchers.Main) {
                        let {
                            extractHorizonLinear
                                ?: LinearLayoutCompat(context).apply {
                                    id = curExtraHorizonLinearId
                                    val horizonParams = withContext(Dispatchers.IO) {
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
                            Triple(
                                registerIndex,
                                Triple(
                                    Pair(
                                        horizonIndex,
                                        horizonTag
                                    ),
                                    horizonVarNameToValueMap.toMap(),
                                    horizonLinearLayout,
                                ),
                                Pair(
                                    mapListElInfoForHorizon,
                                    curHorizonIndexAndReadyContentsLayoutList,
                                ),
                            )
                        )
                    }
                }
            }
            setHorizonJob.forEach { it.await() }
        }
    }

    private suspend fun publishContents(
        registerIndexToHorizonUseToExtraInfo: Triple<
                String,
                Triple<
                        Pair<Int, String>,
                        Map<String, String>,
                        LinearLayoutCompat?
                        >,
                Pair<
                        String,
                        List<List<FrameLayout>>?
                        >
                >,
        srcTitle: String,
        srcCon: String,
        srcImage: String,
        bindingAdapterPosition: Int,
        contentsChannel: Channel<
                Triple<
                        String,
                        Pair<
                                Map<String, String>,
                                List<Pair<String, String?>>,
                                >,
                        Triple<
                                LinearLayoutCompat?,
                                List<FrameLayout>?,
                                Float,
                                >,
                        >
                >,
    ){
        val horizonUse = registerIndexToHorizonUseToExtraInfo.second
        val horizonlIndexToTag = horizonUse.first
        val horizonIndex = horizonlIndexToTag.first
        val horizonTag = horizonlIndexToTag.second
        val horizonVarNameToValueMap = horizonUse.second
        val horizonLinearLayout = horizonUse.third
        val extraInfo = registerIndexToHorizonUseToExtraInfo.third
        val mapListElInfoForHorizon = extraInfo.first
        val curHorizonIndexAndReadyContentsLayoutList = extraInfo.second
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
            withContext(Dispatchers.IO) {
                horizonTagToContentsKeysListMap.map {
                    val key = CmdClickMap.replace(
                        it.key,
                        horizonVarNameToValueMap,
                    )
                    key to it.value
                }.toMap()
            }
        val readyContentsLayoutList =
            curHorizonIndexAndReadyContentsLayoutList?.getOrNull(horizonIndex)
        withContext(Dispatchers.IO) {
            val contentsKeysListMapWithReplace =
                horizonTagToContentsKeysListMapWithReplace.get(horizonTag)
                    ?: return@withContext
            val jobList =
                contentsKeysListMapWithReplace
                    .mapIndexed setContents@{ contentsIndex, contentsKeyValues ->
                        async {
                            val contentsTagToKeyPairsList = withContext(Dispatchers.IO) {
                                EditComponent.AdapterSetter.makeLinearFrameTagToKeyPairsList(
                                    contentsKeyValues,
                                    horizonVarNameToValueMap,
                                    srcTitle,
                                    srcCon,
                                    srcImage,
                                    bindingAdapterPosition,
                                )
                            }
                            val contentsKeyValueSize = withContext(Dispatchers.IO) {
                                EditComponent.AdapterSetter.culcLinearKeyValueSize(
                                    contentsTagToKeyPairsList,
                                )
                            }
                            val layoutWeight = weightSumFloat / contentsKeyValueSize
                            val mapListElInfoForContentsTag =
                                listOf(
                                    "horizonTag: ${horizonTag}",
                                    mapListElInfoForHorizon
                                ).joinToString(", ")
                            contentsChannel.send(
                                Triple(
                                    mapListElInfoForContentsTag,
                                    Pair(
                                        horizonVarNameToValueMap,
                                        contentsTagToKeyPairsList,
                                    ),
                                    Triple(
                                        horizonLinearLayout,
                                        readyContentsLayoutList,
                                        layoutWeight
                                    )
                                )
                            )
                        }
                    }
            jobList.forEach { it.await() }
        }
    }

    private suspend fun clickHandler(
        holder: EditListViewHolder,
        editListPosition: Int,
        contentsKeyPairsList: List<Pair<String, String>>,
        contentsTag: String,
        contentsKeyPairsListCon: String?,
        contentsFrameLayout: FrameLayout,
    ){
        if(
            context == null
        ) return
//        withContext(Dispatchers.IO) {
//            delay(delayTime)
//        }
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
        val disableClick = withContext(Dispatchers.IO) {
            val isJsAcClick = !isJsAc
                    && contentsFrameLayout.tag != null
            isJsAcClick || !onClick
        }
        val clickViewList =
            withContext(Dispatchers.IO) {
                contentsFrameLayout.children.filter {
                    it is OutlineTextView
                            || it is AppCompatImageView
                }
            }
        clickViewList.forEach { clickView ->
            if (
                disableClick
            ) {
                withContext(Dispatchers.Main) {
                    contentsFrameLayout.setBackgroundResource(
                        0
                    )
                    contentsFrameLayout.isClickable =
                        false
                }
                return@forEach
            }
            withContext(Dispatchers.Main) {
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
