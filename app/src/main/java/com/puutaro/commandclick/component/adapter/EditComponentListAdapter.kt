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
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import java.time.LocalDateTime


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
): RecyclerView.Adapter<EditComponentListAdapter.EditListViewHolder>()
{
//    private val context = fragment?.context
    private val listLimitSize = 300
//    private val editExecuteAlways = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
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

                ScreenSizeCalculator.toDp(
                    fragmentRef.get()?.context,
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
                    fragmentRef.get()?.context,
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
                    fragmentRef.get()?.context,
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
    private val frameMapAndFrameTagAndVerticalKeysListToVerticalTagAndHorizonKeysListToContentsMapList = ListSettingsForEditList.ViewLayoutPathManager.parse(
        fragmentRef.get()?.context,
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
        private val vertical1 = totalLinearLayout.findViewById<LinearLayoutCompat>(
            R.id.vertical_linear1,
        )
        private val vertical2 = totalLinearLayout.findViewById<LinearLayoutCompat>(
            R.id.vertical_linear2,
        )
        val readyVerticalLayoutList = listOf (
            vertical1,
            vertical2,
        )
        private val horizonIdList = listOf(
            R.id.edit_component_adapter_horizon1,
            R.id.edit_component_adapter_horizon2,
        )
        val verticalIndexAndReadyHorizonLayoutList = readyVerticalLayoutList.map {
                vertical ->
                horizonIdList.map {
                    vertical.findViewById<LinearLayoutCompat>(it)
                }
            }

//        private val buttonFrameIdListList = listOf(
//            listOf(
//                R.id.icon_caption_for_edit_layout11,
//                R.id.icon_caption_for_edit_layout12,
//                R.id.icon_caption_for_edit_layout13,
//            ),
//            listOf(
//                R.id.icon_caption_for_edit_layout21,
//                R.id.icon_caption_for_edit_layout22,
//                R.id.icon_caption_for_edit_layout23,
//            ),
//        )
//        val verticalIndexAndHorizonIndexAndReadyButtonFrameLayoutList =
//            verticalIndexAndReadyHorizonLayoutList.mapIndexed {
//                index, horizonList ->
//                buttonFrameIdListList[index].map {
//                    horizonList.map {
//                        horizon ->
//                        horizon.findViewById<FrameLayout>(it)
//                    }
//                }
//            }.let {
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
//        val layoutInflater = LayoutInflater.from(parent.context)
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
        fragmentRef.get()?.context?.let {
            Glide.with(it)
                .asDrawable()
                .sizeMultiplier(0.1f)
        }
//    private val buttonFrameLayoutInflater = LayoutInflater.from(context)

    override fun onBindViewHolder(
        holder: EditListViewHolder,
        editListPosition: Int
    ) {
        val fragment = fragmentRef.get()
            ?: return
        val context = fragment.context
            ?: return
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
        val alreadyUseTagList = mutableListOf<String>()
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
        if(
            frameTag.isNullOrEmpty()
        ) return
        val mapListElInfo = listOf(
            "srcTitle: ${holder.srcTitle}",
            "srcCon: ${holder.srcCon}",
            "srcImage: ${holder.srcImage}",
            "bindingAdapterPosition: ${holder.bindingAdapterPosition}",
        ).joinToString(" ")
        CoroutineScope(Dispatchers.Main).launch {
            val isFrameTagDulidateErr = withContext(Dispatchers.IO) frameTagCheck@ {
                val correctFrameTag = EditComponent.AdapterSetter.tagDuplicateErrHandler(
                    context,
                    EditComponent.Template.TagManager.TagGenre.FRAME_TAG,
                    frameTag,
                    alreadyUseTagList,
                    mapListElInfo,
                    plusKeyToSubKeyConWhere,
                )
                correctFrameTag?.let {
                    alreadyUseTagList.add(it)
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
            val materialCardView = withContext(Dispatchers.Main) {
                val materialCardView = holder.materialCardView
                holder.materialCardView.apply {
//                    removeAllViews()
                    layoutElevation?.let {
                        elevation = it
                    }
                    layoutRadius?.let {
                        radius = it
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
                materialCardView
            }
//            withContext(Dispatchers.Main){
//                holder.readyVerticalLayoutList.forEach {
//                    it.children.forEach {
//                        it.visibility = View.GONE
//                    }
//                    it.visibility = View.GONE
//                }
//            }
//            GridLayoutManager
            withContext(Dispatchers.Main) {
                val cardLinearParams =
                    materialCardView.layoutParams as GridLayoutManager.LayoutParams
                layoutMargin?.let {
                    cardLinearParams.setMargins(it)
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
            withContext(Dispatchers.IO){
                if(
                    !isClickEnable
                ) return@withContext
                holder.updateKeyPairListConMap(
                    frameTag,
                    frameKeyPairsCon,
                )
//                holder.keyPairListConMap.put(
//                    frameTag,
//                    frameKeyPairsCon
//                )
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
            val extraVerticalStartId = 40000
            verticalTagToKeyPairsListToVarNameToValueMapList.forEachIndexed setVertical@ {
                    verticalIndex, verticalTagToKeyPairsListToVarNameToValueMap ->
                val curExtraVerticalLinearId = extraVerticalStartId + verticalIndex

                val keyPairsListToVarNameToValueMapForVertical = verticalTagToKeyPairsListToVarNameToValueMap.second
                val verticalKeyPairs = keyPairsListToVarNameToValueMapForVertical.first
                val verticalVarNameToValueMap =
                    keyPairsListToVarNameToValueMapForVertical.second + frameVarNameValueMap
                val verticalTag = CmdClickMap.replace(
                    verticalTagToKeyPairsListToVarNameToValueMap.first,
                    verticalVarNameToValueMap,
                )
                val isVerticalTagDuplicateErr = withContext(Dispatchers.IO) verticalTagCheck@ {
                    val correctVerticalTag = EditComponent.AdapterSetter.tagDuplicateErrHandler(
                        context,
                        EditComponent.Template.TagManager.TagGenre.VERTICAL_TAG,
                        verticalTag,
                        alreadyUseTagList,
                        mapListElInfoForVertical,
                        String(),
                    )
                    correctVerticalTag?.let {
                        alreadyUseTagList.add(it)
                    }
                    val isDuplicateTagErr = correctVerticalTag.isNullOrEmpty()
                    if(
                        isDuplicateTagErr
                    ) return@verticalTagCheck true
                    false
                }
                if(isVerticalTagDuplicateErr) return@setVertical
                withContext(Dispatchers.IO) {
                    EditComponent.AdapterSetter.isNotLinearKeyErr(
                        context,
                        EditComponent.Template.LayoutKey.VERTICAL.key,
                        verticalKeyPairs,
                        "verticalTag: ${verticalTag}, ${mapListElInfoForVertical}",
                        String(),
                    )
                }.let {
                        isNotVerticalKeyErr ->
                    if(isNotVerticalKeyErr) return@setVertical
                }
                val isVerticalEnable = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        verticalKeyPairs,
                        enableKey,
                    ).let { enableStr ->
                        enableStr != switchOff
                    }
                }
                if(
                    !isVerticalEnable
                ) return@setVertical
                val readyVerticalLayout =
                    holder.readyVerticalLayoutList.getOrNull(verticalIndex)
                val extractVerticalLinear =
                    readyVerticalLayout
                        ?: let {
                            totalLinearLayout.findViewById<LinearLayoutCompat>(
                                curExtraVerticalLinearId
                            )
                        }
                val curRegExtraVerticalLinearId =
                    when (extractVerticalLinear == null) {
                        false -> extractVerticalLinear.id
                        else -> curExtraVerticalLinearId
                    }

                val verticalLinearLayout = withContext(Dispatchers.Main) {
                    EditComponent.AdapterSetter.makeVerticalLinear(
                        context,
                        extractVerticalLinear,
                        curRegExtraVerticalLinearId,
                        verticalKeyPairs,
                        verticalLinerWeight,
                        verticalTag,
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
                if(extractVerticalLinear == null) {
                    totalLinearLayout.addView(verticalLinearLayout)
                }
//                val horizonIndexAndReadyButtonFrameLayoutList =
//                    holder.verticalIndexAndHorizonIndexAndReadyButtonFrameLayoutList.getOrNull(
//                        verticalIndex
//                    )



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
                val horizonLayoutStartId = 50000
                val mapListElInfoForHorizon =
                    listOf(
                        "verticalTag: ${verticalTag}",
                        mapListElInfoForVertical
                    ).joinToString(", ")
                horizonTagToKeyPairsListToVarNameToValueMapList.forEachIndexed setHorizon@ {
                    horizonIndex, horizonTagToKeyPairsListToVarNameToValueMap ->
                    val curExtraHorizonLinearId = horizonLayoutStartId + horizonIndex
                    val keyPairsListToVarNameToValueMapForHorizon = horizonTagToKeyPairsListToVarNameToValueMap.second
                    val horizonKeyPairs = keyPairsListToVarNameToValueMapForHorizon.first
                    val horizonVarNameToValueMap =
                        keyPairsListToVarNameToValueMapForHorizon.second + verticalVarNameToValueMap
                    val horizonTag = CmdClickMap.replace(
                        horizonTagToKeyPairsListToVarNameToValueMap.first,
                        horizonVarNameToValueMap
                    )
                    withContext(Dispatchers.IO){
                        EditComponent.AdapterSetter.tagDuplicateErrHandler(
                            context,
                            EditComponent.Template.TagManager.TagGenre.HORIZON_TAG,
                            horizonTag,
                            alreadyUseTagList,
                            mapListElInfoForHorizon,
                            String(),
                        )?.let {
                            alreadyUseTagList.add(it)
                        }
                    }
                    withContext(Dispatchers.IO) {
                        EditComponent.AdapterSetter.isNotLinearKeyErr(
                            context,
                            EditComponent.Template.LayoutKey.HORIZON.key,
                            horizonKeyPairs,
                            "horizonTag: ${horizonTag}, ${mapListElInfoForHorizon}",
                            String(),
                        )
                    }.let {
                            isNotHorizonKeyErr ->
                        if(isNotHorizonKeyErr) return@setHorizon
                    }
                    val isHorizonEnable = withContext(Dispatchers.IO) {
                        PairListTool.getValue(
                            horizonKeyPairs,
                            enableKey,
                        ).let { enableStr ->
                            enableStr != switchOff
                        }
                    }
                    if(
                        !isHorizonEnable
                    ) return@setHorizon
                    val extractHorizonLinear =
                        readyHorizonLayoutList?.getOrNull(horizonIndex)
                            ?: let {
                                totalLinearLayout.findViewById<LinearLayoutCompat>(
                                    curExtraHorizonLinearId
                                )
                            }
                    val curRegExtraHorizonLinearId =
                        when (extractHorizonLinear == null) {
                            false -> extractHorizonLinear.id
                            else -> curExtraHorizonLinearId
                        }
                    val horizonLinearLayout = withContext(Dispatchers.Main) {
                        EditComponent.AdapterSetter.makeHorizonLinear(
                            context,
                            extractHorizonLinear,
                            curRegExtraHorizonLinearId,
                            horizonKeyPairs,
                            horizonTag,
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
                    if(extractHorizonLinear == null) {
                        verticalLinearLayout.addView(horizonLinearLayout)
                    }

                    val horizonTagToContentsKeysListMapWithReplace = horizonTagToContentsKeysListMap.map {
                        val key = CmdClickMap.replace(
                            it.key,
                            horizonVarNameToValueMap,
                        )
                        key to it.value
                    }.toMap()
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "smakeHorizonLinear.txt").absolutePath,
//                        listOf(
//                            "verticalTag: ${verticalTag}",
//                            "isNotExtract: ${extractHorizonLinear == null}",
//                            "verticalTagToHorizonKeysConList: ${verticalTagToHorizonKeysConList}",
//                            "horizonKeyPairs: ${horizonKeyPairs}",
//                            "horizonTag: ${horizonTag}",
//                            "horizonTagToContentsKeysListMapWithReplace: ${horizonTagToContentsKeysListMapWithReplace}",
//                            "horizonTagToContentsKeysListMapWithReplace.get(horizonTag): ${horizonTagToContentsKeysListMapWithReplace.get(horizonTag)}",
//                        ).joinToString("\n\n\n") + "\n\n======\n\n"
//                    )

                    horizonTagToContentsKeysListMapWithReplace.get(horizonTag)?.forEachIndexed setContents@ {
                            contentsIndex, contentsKeyValues ->
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
                        var contentsVarNameToValueMap = horizonVarNameToValueMap
                        val frameLayoutStartId = 70000
                        val mapListElInfoForContentsTag =
                            listOf(
                                "horizonTag: ${horizonTag}",
                                mapListElInfoForHorizon
                            ).joinToString(", ")
                        contentsTagToKeyPairsList.forEachIndexed execSetContents@ { execSetContentsIndex, contentsTagToKeyPairs ->
                            val curContentsLayoutId = frameLayoutStartId + execSetContentsIndex
                            val contentsTagSrc = contentsTagToKeyPairs.first
                            val contentsKeyPairsListConSrc = contentsTagToKeyPairs.second
                            val varNameToValueMap = withContext(Dispatchers.IO) updateLinearKeyParsListCon@ {
                                EditComponent.AdapterSetter.makeFrameVarNameToValueMap(
                                    fragment,
                                    fannelInfoMap,
                                    setReplaceVariableMap,
                                    busyboxExecutor,
                                    this@EditComponentListAdapter,
                                    contentsVarNameToValueMap,
                                    "contentsTagSrc: ${contentsTagSrc}, ${mapListElInfoForContentsTag}",
                                    contentsKeyPairsListConSrc,
                                    holder.srcTitle,
                                    holder.srcCon,
                                    holder.srcImage,
                                    holder.bindingAdapterPosition,
                                )
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "sGet_in.txt").absolutePath,
//                                    listOf(
//                                        "linearFrameKeyPairsListConSrc: ${linearFrameKeyPairsListConSrc}",
//                                        "horizonVarNameToValueMap: ${horizonVarNameToValueMap}",
//                                        "linearFrameKeyPairsListCon: ${CmdClickMap.replace(
//                                            linearFrameKeyPairsListConSrc,
//                                            horizonVarNameToValueMap
//                                        )}",
//                                    ).joinToString("\n")
//                                )
                            }
                            contentsVarNameToValueMap += varNameToValueMap
                            val contentsTag = CmdClickMap.replace(
                                contentsTagSrc,
                                contentsVarNameToValueMap
                            )
                            val isContentsTagErr = withContext(Dispatchers.IO) contentsTagCheck@ {
                                val tagGenre =
                                    EditComponent.Template.TagManager.TagGenre.CONTENTS_TAG

                                ListSettingsForEditList.ViewLayoutCheck.isTagBlankErr(
                                    context,
                                    contentsTag,
                                    mapListElInfo,
                                    tagGenre
                                )
                                val correctContentsTag = EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                    context,
                                    tagGenre,
                                    contentsTag,
                                    alreadyUseTagList,
                                    mapListElInfoForContentsTag,
                                    String(),
                                )
                                correctContentsTag?.let {
                                    alreadyUseTagList.add(it)
                                }
                                val isDuplicateTagErr = correctContentsTag.isNullOrEmpty()
                                if(
                                    isDuplicateTagErr
                                ) return@contentsTagCheck true
                                false
                            }
                            if(isContentsTagErr){
                                return@setVertical
                            }
                            val linearFrameKeyPairsListCon = contentsKeyPairsListConSrc?.let {
                                CmdClickMap.replace(
                                    it,
                                    contentsVarNameToValueMap
                                )
                            }
                            val linearFrameKeyPairsList = withContext(Dispatchers.IO) {
                                makeLinearFrameKeyPairsList(
                                    linearFrameKeyPairsListCon
                                )
                            }
                            val linearFrameLayout = withContext(Dispatchers.Main) {
                                withContext(Dispatchers.IO) {
                                    PairListTool.getValue(
                                        linearFrameKeyPairsList,
                                        enableKey,
                                    )
                                }.let {
                                        enableStr ->
                                    if(
                                        enableStr == switchOff
                                    ) return@withContext null
                                }
                                val extractButtonFrameLayout =
                                    horizonLinearLayout.findViewById<FrameLayout>(curContentsLayoutId)
                                val buttonFrameLayout =
                                    extractButtonFrameLayout ?: layoutInflater.inflate(
                                        R.layout.icon_caption_layout_for_edit_list,
                                        null
                                    ) as FrameLayout
//                                extractButtonFrameLayout ?:
//                                layoutInflater.inflate(
//                                    R.layout.icon_caption_layout_for_edit_list,
//                                null
//                            ) as FrameLayout
//                            FileSystems.updateFile(
//                                File(UsePath.cmdclickDefaultAppDirPath, "lreadyHorozontalLinear.txt").absolutePath,
//                                listOf(
//                                    "frameTag: ${frameTag}",
//                                    "verticalTag: ${verticalTag}",
//                                    "verticalIndex: ${verticalIndex}",
//                                    "horizonIndex: ${horizonIndex}",
//                                    "isreadyVerticalLinear: ${extractVerticalLinear is LinearLayoutCompat}",
//                                    "isHorozonExtrat: ${extractHorizonLayout is LinearLayoutCompat}",
////                                    "isextractButtonFrameLayout: ${extractButtonFrameLayout is FrameLayout}",
//                                    "isbuttonFrameLayout: ${buttonFrameLayout is FrameLayout}",
//                                    "linearFrameKeyPairsList: ${linearFrameKeyPairsList}",
//                                    "horizonLinearLayout.tag: ${horizonLinearLayout.tag}"
//                                ).joinToString("\n") + "\n\n============\n\n"
//                            )
                                if(extractButtonFrameLayout == null) {
                                    horizonLinearLayout.addView(buttonFrameLayout)
                                }
                                CoroutineScope(Dispatchers.Main).launch {
                                    EditFrameMaker.make(
                                        context,
                                        buttonFrameLayout,
                                        fannelInfoMap,
                                        setReplaceVariableMap,
                                        busyboxExecutor,
                                        linearFrameKeyPairsList,
                                        0,
                                        layoutWeight,
                                        contentsTag,
                                        totalSettingValMap,
                                        requestBuilderSrc,

                                    )
                                }
                                buttonFrameLayout
                            } ?: return@execSetContents

                            CoroutineScope(Dispatchers.Main).launch {
                                val isConsec = withContext(Dispatchers.IO) {
                                    PairListTool.getValue(
                                        linearFrameKeyPairsList,
                                        onConsecKey,
                                    ) == EditComponent.Template.switchOn
                                }
                                val onClick = withContext(Dispatchers.IO) {
                                    PairListTool.getValue(
                                        linearFrameKeyPairsList,
                                        onClickKey,
                                    ) != switchOff
                                }
                                val isJsAc = withContext(Dispatchers.IO) {
                                    jsActionKeyList.any { jsActionKey ->
                                        !PairListTool.getValue(
                                            linearFrameKeyPairsList,
                                            jsActionKey,
                                        ).isNullOrEmpty()
                                    }
                                }
                                withContext(Dispatchers.IO) {
                                    holder.updateKeyPairListConMap(
                                        contentsTag,
                                        linearFrameKeyPairsListCon
                                    )
                                }
                                withContext(Dispatchers.Main) setClick@ {
                                    val isJsAcClick = !isJsAc
                                            && linearFrameLayout.tag != null
                                    if (
                                        isJsAcClick
                                        || !onClick
                                    ) {
                                        linearFrameLayout.setBackgroundResource(0)
                                        linearFrameLayout.isClickable = false
                                        return@setClick
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
                        }
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
