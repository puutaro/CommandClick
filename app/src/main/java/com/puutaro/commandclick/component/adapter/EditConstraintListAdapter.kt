package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.setMargins
import androidx.core.view.setPadding
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
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent.Template
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionManager
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.edit_list.EditConstraintFrameMaker
import com.puutaro.commandclick.proccess.edit_list.EditImageViewSetter
import com.puutaro.commandclick.proccess.edit_list.EditListConfig
import com.puutaro.commandclick.proccess.edit_list.EditTextViewSetter
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


class EditConstraintListAdapter(
    private val fragmentRef: WeakReference<Fragment>,
    private val layoutInflater: LayoutInflater,
    val fannelInfoMap: Map<String, String>,
    val setReplaceVariableMap: Map<String, String>?,
    private val settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
    private val imageActionAsyncCoroutine: ImageActionAsyncCoroutine,
    private val globalVarNameToValueMap: Map<String, String>?,
    private val globalVarNameToBitmapMap: Map<String, Bitmap?>,
    frameMapAndFrameTagToContentsMapListToTagIdList: Triple<
            Map<String, String>,
            Map<String, List<List<String>>>,
            Map<String, Int>
            >?,
    val editListConfigMap: Map<String, String>?,
    val busyboxExecutor: BusyboxExecutor?,
    val indexListMap: Map<String, String>,
    var lineMapList: MutableList<Map<String, String>>,
    var fannelContentsList: List<String>?,
    private val density: Float,
): RecyclerView.Adapter<EditConstraintListAdapter.EditListViewHolder>()
{
    private val context = fragmentRef.get()?.context
    private val listLimitSize = 300
    private var initSettingValMap = RecordNumToMapNameValueInHolder.parse(
        fannelContentsList,
        CommandClickScriptVariable.SETTING_SEC_START,
        CommandClickScriptVariable.SETTING_SEC_END,
    )
    private var initCmdValMap = RecordNumToMapNameValueInHolder.parse(
        fannelContentsList,
        CommandClickScriptVariable.CMD_SEC_START,
        CommandClickScriptVariable.CMD_SEC_END,
    )
    private var totalSettingValMap = makeTotalSettingValMap(
        initSettingValMap,
        initCmdValMap,
        )
    private fun makeTotalSettingValMap(
        initSettingValMap: Map<String, String>?,
        initCmdValMap: Map<String, String>?,
    ): Map<String, String> {
        return (initSettingValMap ?: emptyMap()) + (initCmdValMap ?: emptyMap())
    }
    fun getTotalSettingValMap(): Map<String, String> {
        return totalSettingValMap
    }
    val footerKeyPairListConMap: MutableMap<String, String> = mutableMapOf()
    private val layoutConfigMap = LayoutSettingsForEditList.getLayoutConfigMap(
        editListConfigMap,
        setReplaceVariableMap,
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
    private val innerPadding = layoutConfigMap.get(
        LayoutSettingsForEditList.LayoutSettingKey.INNER_PADDING.key
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
    private val innerMargin = layoutConfigMap.get(
        LayoutSettingsForEditList.LayoutSettingKey.INNER_MARGIN.key
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
    private val frameMap =
        frameMapAndFrameTagToContentsMapListToTagIdList?.first ?: emptyMap()
    private val frameTagToContentsMapList =
        frameMapAndFrameTagToContentsMapListToTagIdList?.second
    private val tagToIdListSrc =
        frameMapAndFrameTagToContentsMapListToTagIdList?.third
//            .let {
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "leframeMapListToLinearMapList.txt").absolutePath,
//                listOf(
//                    "lineMapList: ${lineMapList}",
//                    "frameMap: ${frameMap}",
//                    "frameTagToContentsMapList: ${frameTagToContentsMapList}",
//                    "tagToIdListSrc: ${it}",
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
    private val tagKey = EditComponent.Template.EditComponentKey.TAG.key

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
                R.id.edit_constraint_adapter_mterial_card_view
            )
        val bkFrameLayout = materialCardView.findViewById<FrameLayout>(
            R.id.edit_constraint_adapter_bk_frame_layout
        )
        val totalConstraintLayout = materialCardView.findViewById<ConstraintLayout>(
            R.id.edit_constraint_adapter_constraint,
        )
//        val contentsLayoutList = listOf(
//            R.id.button_frame_layout1,
//            R.id.button_frame_layout2,
//            R.id.button_frame_layout3,
//            R.id.button_frame_layout4,
//        ).map {
//            totalConstraintLayout.findViewById<FrameLayout>(it)
//        }
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
            R.layout.edit_list_constraint_adapter_layout,
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
//        private val onClickViewsKey =
//            EditComponent.Template.EditComponentKey.CLICK_VIEWS.key
    }

    private val switchOn = EditComponent.Template.switchOn
    private val switchOff = EditComponent.Template.switchOff
    private val requestBuilderSrc: RequestBuilder<Drawable>? =
        context?.let {
            Glide.with(it)
                .asDrawable()
                .sizeMultiplier(0.1f)
        }
    private val delayTime = 1000L
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
                    indexListMap.get(
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
                    val totalConstraintLayout = holder.totalConstraintLayout
//                    val contentsLayoutList = totalConstraintLayout.children.filter {
//                        it is FrameLayout
//                    }.toList() as List<FrameLayout>
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
//                    val hideContentsLayoutListJob = async {
//                        contentsLayoutList.forEach {
//                            it.visibility = View.GONE
//                        }
//                    }
                    val removeAndSet2TotalConstraint = async {
                        totalConstraintLayout.removeAllViews()
                        totalConstraintLayout.apply {
                            innerPadding?.let {
                                setPadding(it)
                            }
                        }
                    }

                    val removeAllViewFromBkFrameJob = async {
                        holder.bkFrameLayout.removeAllViews()
                    }
                    listOf(
                        cardViewSettingJob,
                        removeAndSet2TotalConstraint,
                        removeAllViewFromBkFrameJob
//                        hideContentsLayoutListJob,
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
                    val isDuplicateTagErr = correctFrameTag.isNullOrEmpty()
                    if (
                        isDuplicateTagErr
                    ) return@frameTagCheck true
                    false
                }
            }
            val totalMapListElInfo =
                listOf(
                    "frameTag: ${frameTag}",
                    "mapListElInfo: ${mapListElInfo}",
                    plusKeyToSubKeyConWhere
                ).joinToString(", ")
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
                        settingActionAsyncCoroutine,
                        globalVarNameToValueMap?.map{
                            it.key
                        },
                        globalVarNameToValueMap,
                        innerFrameKeyPairsConSrc,
                        totalMapListElInfo,
                        editConstraintListAdapterArg = this@EditConstraintListAdapter
                    ) + (globalVarNameToValueMap ?: emptyMap())
                    CmdClickMap.replace(
                        innerFrameKeyPairsConSrc,
                        varNameToValueMap
                    ) to varNameToValueMap
                }
            }
            val frameKeyPairsCon = frameKeyPairsConToVarNameValueMap.first
            val frameVarNameValueMap = frameKeyPairsConToVarNameValueMap.second
            val tagIdMap = withContext(Dispatchers.IO){
                tagToIdListSrc?.map {
                    val key = CmdClickMap.replace(
                        it.key,
                        frameVarNameValueMap
                    )
                    key to it.value
                }?.toMap() ?: emptyMap()
            }
            val imageView = withContext(Dispatchers.IO){
                holder.bkFrameLayout.children.firstOrNull {
                        view ->
                    view is AppCompatImageView
                } as? AppCompatImageView
            }
            val varNameToBitmapMapInFrame = withContext(Dispatchers.IO){
                ImageActionManager().exec(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    imageView,
                    requestBuilderSrc,
                    imageActionAsyncCoroutine,
                    globalVarNameToBitmapMap.map {
                        it.key
                    },
                    globalVarNameToBitmapMap,
                    frameKeyPairsCon,
                    totalMapListElInfo,
                )
            }
//            FileSystems.updateFile(
//                File(
//                    UsePath.cmdclickDefaultAppDirPath,
//                    "ltagIdMap.txt"
//                ).absolutePath,
//                listOf(
//                    "tagIdMap: ${tagIdMap}",
//                ).joinToString("\n") + "\n\n============\n\n\n"
//            )
            val frameKeyPairsList = withContext(Dispatchers.IO) {
                EditComponent.AdapterSetter.makeLinearFrameKeyPairsList(
                    frameKeyPairsCon,
                )
            }

//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lviewLayout_inadapter${editListPosition}.txt").absolutePath,
//                listOf(
//                    "frameTag: ${frameTag}",
//                    "indexListMap: ${indexListMap}",
//                    "frameMap: ${frameMap}",
//                    "frameKeyPairsList: ${frameKeyPairsList}",
//                ).joinToString("\n\n") + "\n\n============\n\n"
//            )
            val textTagToMapForFrame = withContext(Dispatchers.IO) {
                EditComponent.AdapterSetter.makeTextTagToMap(
                    frameKeyPairsList,
                    frameTag,
                    totalSettingValMap,
                )
            }
            val imageTagToMapForFrame = withContext(Dispatchers.IO) {
                EditComponent.AdapterSetter.makeImageTagToMap(
                    frameKeyPairsList,
                )
            }
            val textOrImageTagListForFrame =
                textTagToMapForFrame.keys.toList() +
                        imageTagToMapForFrame.keys.toList()
            val tagGenreForFrame =
                EditComponent.Template.TagManager.TagGenre.TEXT_OR_IMAGE_TAG
            val isDuplicateTextOrImageTagErrJobForFrame = withContext(Dispatchers.IO) {
                val alreadyUseTagListSrc =
                    EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
                        alreadyUseTagList,
                        alreadyUseTagListMutex
                    )
                run dupCheckTextOrImageTag@{
                    textOrImageTagListForFrame.forEach {
                        textOrImageTag ->
                        val correctContentsTag =
                            EditComponent.AdapterSetter.tagDuplicateErrHandler(
                                context,
                                tagGenreForFrame,
                                textOrImageTag,
                                alreadyUseTagListSrc,
                                totalMapListElInfo,
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
                        ) return@dupCheckTextOrImageTag true
                    }
                    false
                }
            }
            if(
                isDuplicateTextOrImageTagErrJobForFrame
            ) return@launch
            val returnFrameLayout = withContext(Dispatchers.Main) {
                EditComponent.AdapterSetter.makeContentsFrameLayout(
                    context,
                    null,
                    frameTag,
                    null,
                    textTagToMapForFrame,
                    imageTagToMapForFrame,
                    holder.bkFrameLayout
                )
            }
            val bkFrameLayout = returnFrameLayout.frameLayoutRef.get()
                ?: return@launch
            val tagToTextViewListForFrame = returnFrameLayout.tagToTextViewListRef.get()
                ?: return@launch
            val tagToImageViewListForFrame = returnFrameLayout.tagToImageViewListRef.get()
                ?: return@launch
//            val clickTagToViewList = returnFrameLayout.clickTagToViewListRef.get()
            val isClickEnable = withContext(Dispatchers.IO) {
                EditComponent.Template.ClickManager.isClickEnable(frameKeyPairsList.toMap())
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
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lframeTag.txt").absolutePath,
//                    listOf(
//                        "frameTag: ${frameTag}",
//                        "editListPosition: ${editListPosition}",
//                        "holder.bindingAdapterPosition: ${holder.bindingAdapterPosition}",
//                        "frameKeyPairsList: ${frameKeyPairsList}",
//                    ).joinToString("\n")
//                )
                val frameId = 10000
                withContext(Dispatchers.Main) execSetFrame@{
                    EditConstraintFrameMaker.make(
                        context,
                        frameId,
                        null,
                        bkFrameLayout,
                        fannelInfoMap,
                        setReplaceVariableMap,
                        busyboxExecutor,
                        frameKeyPairsList,
                        textTagToMapForFrame,
                        tagToTextViewListForFrame,
                        imageTagToMapForFrame,
                        tagToImageViewListForFrame,
                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        frameTag,
//                        totalSettingValMap,
                        totalMapListElInfo,
                        null,
//                        false,
                        null,
//                        null,
                        requestBuilderSrc,
                        density,
                    )
                }
                frameClickHandle(
                    frameTag,
                    holder,
                    editListPosition,
                    holder.materialCardView,
//                    frameFrameLayout,
                    frameKeyPairsList,
                    isClickEnable,
                )
            }

            val contentsChannel = Channel<
                    List<Pair<String, String?>>,
                    >(100)
            if(
                frameTagToContentsMapList.isNullOrEmpty()
            ) return@launch
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    publishContents(
                        frameTag,
                        frameTagToContentsMapList,
                        frameVarNameValueMap,
                        holder.srcTitle,
                        holder.srcCon,
                        holder.srcImage,
                        holder.bindingAdapterPosition,
                        contentsChannel,
                        mapListElInfo,
                    )
                }
                withContext(Dispatchers.IO) {
                    contentsChannel.close()
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                val totalConstraintLayout =
                    holder.totalConstraintLayout
//                val contentsLayoutList =
//                    holder.contentsLayoutList
                for (contentsTagToKeyPairsList in contentsChannel) {
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lcontentsTag.txt").absolutePath,
//                        listOf(
//                            "frameTag: ${frameTag}",
//                            "editListPosition: ${editListPosition}",
//                            "holder.bindingAdapterPosition: ${holder.bindingAdapterPosition}",
//                            "mapListElInfoForContentsTag: ${mapListElInfoForContentsTag}",
//                            "layoutWeight: ${layoutWeight}",
//                            "contentsTagToKeyPairsList: ${contentsTagToKeyPairsList}",
//                        ).joinToString("\n")
//                    )
                    contentsTagToKeyPairsList.mapIndexed execSetContents@{ execSetContentsIndex, contentsTagToKeyPairs ->
                        async {
                            val contentsTagSrc = contentsTagToKeyPairs.first
                            val contentsKeyPairsListConSrc =
                                contentsTagToKeyPairs.second
                            val topVarNameToValueMapForContents =
                                ((globalVarNameToValueMap ?: emptyMap()) + frameVarNameValueMap)
                            let {
                                val contentsKeyPairsListForEnable = EditComponent.AdapterSetter.makeLinearFrameKeyPairsList(
                                    contentsKeyPairsListConSrc?.let {
                                        CmdClickMap.replace(
                                            it,
                                            topVarNameToValueMapForContents,
                                        )
                                    }
                                )
                                PairListTool.getValue(
                                    contentsKeyPairsListForEnable,
                                    enableKey,
                                ).let { enableStr ->
                                    if (
                                        enableStr == switchOff
                                    ) return@async
                                }
                            }
//                            FileSystems.updateFile(
//                                    File(
//                                        UsePath.cmdclickDefaultSDebugAppDirPath,
//                                        "ltagIdMap_idInt${editListPosition}.txt"
//                                    ).absolutePath,
//                                    listOf(
//                                        "contentsTag: ${contentsTagSrc}",
//                                    ).joinToString("\n") + "\n\n============\n\n\n"
//                                )
                            val varNameToValueMap =
                                withContext(Dispatchers.IO) updateLinearKeyParsListCon@{
                                    Template.ReplaceHolder.replaceHolder(
                                        contentsKeyPairsListConSrc,
                                        holder.srcTitle,
                                        holder.srcCon,
                                        holder.srcImage,
                                        holder.bindingAdapterPosition,
                                    ).let {
                                            contentsKeyPairsListConSrcWithReplace ->
                                        if(
                                            contentsKeyPairsListConSrcWithReplace.isNullOrEmpty()
                                        ) return@let emptyMap()
                                        SettingActionManager().exec(
                                            fragment,
                                            fannelInfoMap,
                                            setReplaceVariableMap,
                                            busyboxExecutor,
                                            settingActionAsyncCoroutine,
                                            topVarNameToValueMapForContents.map {
                                                it.key
                                            },
                                            topVarNameToValueMapForContents,
                                            contentsKeyPairsListConSrcWithReplace,
                                            "contentsTagSrc: ${contentsTagSrc}, ${totalMapListElInfo}",
                                            editConstraintListAdapterArg = this@EditConstraintListAdapter
                                        )
                                    }
                                }
                            val contentsVarNameToValueMap =
                                frameVarNameValueMap + varNameToValueMap
                            val contentsTag = CmdClickMap.replace(
                                contentsTagSrc,
                                contentsVarNameToValueMap
                            )
                            val isContentsTagErrJob =
                                async {
                                    val tagGenre =
                                        EditComponent.Template.TagManager.TagGenre.CONTENTS_TAG
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
                                                    totalMapListElInfo,
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
                                    isDuplicateTagErrJob.await()
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
                                    EditComponent.AdapterSetter.makeLinearFrameKeyPairsList(
                                        contentsKeyPairsListCon
                                    )
                                }
                            if(
                                isContentsTagErrJob.await()
                            ) return@async
                            withContext(Dispatchers.IO) setLinearFrameLayout@{
                                PairListTool.getValue(
                                    contentsKeyPairsList,
                                    enableKey,
                                ).let { enableStr ->
                                    if (
                                        enableStr == switchOff
                                    ) return@setLinearFrameLayout
                                }
                                val idInt = withContext(Dispatchers.IO){
                                        tagIdMap.get(
                                            contentsTag
                                        )
                                }
//                                FileSystems.updateFile(
//                                    File(
//                                        UsePath.cmdclickDefaultSDebugAppDirPath,
//                                        "ltagIdMap_idInt${editListPosition}.txt"
//                                    ).absolutePath,
//                                    listOf(
//                                        "tagIdMap: ${tagIdMap}",
//                                        "contentsTag: ${contentsTag}",
//                                        "idInt: ${idInt}",
//                                    ).joinToString("\n") + "\n\n============\n\n\n"
//                                )
                                val viewType = Template.ViewTypeManager.getViewType(
                                    contentsKeyPairsList
                                )
                                when(viewType) {
                                    Template.ViewTypeManager.ViewType.TEXT ->
                                        setTextView(
                                            fragment,
                                            fannelInfoMap,
                                            setReplaceVariableMap,
                                            busyboxExecutor,
                                            holder,
                                            editListPosition,
                                            idInt,
                                            contentsTag,
                                            contentsKeyPairsList,
                                            contentsKeyPairsListCon,
                                            totalConstraintLayout,
                                            tagIdMap,
                                            totalMapListElInfo,
                                        )
                                    Template.ViewTypeManager.ViewType.IMAGE ->
                                        setImageView(
                                            fragment,
                                            holder,
                                            editListPosition,
                                            idInt,
                                            contentsTag,
                                            contentsKeyPairsList,
                                            contentsKeyPairsListCon,
                                            totalConstraintLayout,
                                            tagIdMap,
                                            totalMapListElInfo,
                                            varNameToBitmapMapInFrame,
                                        )
                                    Template.ViewTypeManager.ViewType.FRAME -> setFrameLayout(
                                        fragment,
                                        holder,
                                        editListPosition,
                                        contentsTag,
                                        contentsKeyPairsList,
                                        contentsKeyPairsListCon,
                                        totalConstraintLayout,
                                        idInt,
                                        tagIdMap,
                                        alreadyUseTagList,
                                        alreadyUseTagListMutex,
                                        totalMapListElInfo,
                                        varNameToBitmapMapInFrame,
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
            frameTag: String?,
            holder: EditListViewHolder,
            editListPosition: Int,
        )
    }

    var editAdapterTouchDownListener: OnEditAdapterTouchDownListener? = null
    interface OnEditAdapterTouchDownListener {
        fun onEditAdapterTouchDown(
            itemView: View,
            frameTag: String?,
            holder: EditListViewHolder,
            editListPosition: Int,
        )
    }

    var editAdapterTouchUpListener: OnEditAdapterTouchUpListener? = null
    interface OnEditAdapterTouchUpListener {
        fun onEditAdapterTouchUp(
            itemView: View,
            frameTag: String?,
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
        updateCmdSettingTotalMap(
            fannelContentsList
        )
        MapListUpdater.updateLineMapList(
            fragment,
            fannelInfoMap,
            setReplaceVariableMap,
            editListRecyclerView,
            indexListMap,
            layoutConfigMap,
//            lineMapList,
            indexListPosition,
        )
    }

    private suspend fun setImageView(
        fragment: Fragment?,
        holder: EditListViewHolder,
        editListPosition: Int,
        idInt: Int?,
        contentsTag: String,
        contentsKeyPairsList: List<Pair<String, String>>,
        contentsKeyPairsListCon: String?,
        totalConstraintLayout: ConstraintLayout?,
        tagIdMap: Map<String, Int>?,
        totalMapListElInfo: String,
        varNameToBitmapMapInFrame: Map<String, Bitmap?>,
    ){
        val context =
            fragment?.context ?: return
        val imageView = withContext(Dispatchers.Main) {
            EditComponent.AdapterSetter.makeImageViewLayout(
                context,
                idInt,
                contentsTag,
            )
        }
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultSDebugAppDirPath, "lcontents_${editListPosition}.txt").absolutePath,
//                                    listOf(
//                                        "textTagToMapForContents: ${textTagToMapForContents}",
//                                        "imageTagToMapForContents: ${imageTagToMapForContents}",
//                                        "contentsFrameLayout: ${contentsFrameLayout}",
//                                    ).joinToString("\n")
//                                )
        CoroutineScope(Dispatchers.IO).launch {
//                                    val varNameToBitmapMapInContents =
            withContext(Dispatchers.IO){
                val topLevelVarNameToBitmapMap =
                    globalVarNameToBitmapMap + varNameToBitmapMapInFrame
                ImageActionManager().exec(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    imageView,
                    requestBuilderSrc,
                    imageActionAsyncCoroutine,
                    topLevelVarNameToBitmapMap.map {
                        it.key
                    },
                    topLevelVarNameToBitmapMap,
                    contentsKeyPairsListCon,
                    "contentsTag: ${contentsTag}, ${totalMapListElInfo}",
                )
            }
        }
//                                if (extractContentsFrameLayout == null) {
        withContext(Dispatchers.Main) {
            totalConstraintLayout?.addView(
                imageView
            )
        }
//                                }
        val enableClick =
            withContext(Dispatchers.IO) {
                EditComponent.Template.ClickManager.isClickEnable(
                    contentsKeyPairsList.toMap()
                )
            }
//        val clickViewStrList = withContext(Dispatchers.IO) {
//            EditComponent.Template.ClickViewManager.makeClickViewStrList(
//                contentsKeyPairsList
//            )
//        }
        CoroutineScope(Dispatchers.Main).launch {
//                                    withContext(Dispatchers.IO) {
//                                        if(idInt == 10100) {
//                                            FileSystems.updateFile(
//                                                File(
//                                                    UsePath.cmdclickDefaultAppDirPath,
//                                                    "lcontentsTagBerore.txt"
//                                                ).absolutePath,
//                                                listOf(
//                                                    "frameTag: ${frameTag}",
//                                                    "editListPosition: ${editListPosition}",
//                                                    "holder.bindingAdapterPosition: ${holder.bindingAdapterPosition}",
//                                                    "mapListElInfoForContentsTag: ${mapListElInfoForContentsTag}",
//                                                    "layoutWeight: ${layoutWeight}",
//                                                    "contentsTag: ${contentsTag}",
//                                                    "\n\ncontentsKeyPairsListConSrc: ${contentsKeyPairsListConSrc}\n\n",
//                                                    "\n\ncontentsKeyPairsList: ${contentsKeyPairsList}\n\n",
//                                                ).joinToString("\n") + "\n\n==============\n\n\n"
//                                            )
//                                        }
//                                    }
            EditImageViewSetter.setForConstraint(
                context,
                tagIdMap,
                imageView,
                contentsKeyPairsList,
            0,
                enableClick,
                outValue,
                requestBuilderSrc,
                density,
                "contentsTag: ${contentsTag}, ${totalMapListElInfo}",
            )
        }
        CoroutineScope(Dispatchers.Main).launch setClick@ {
            clickHandler(
                holder,
                editListPosition,
                contentsKeyPairsList,
                imageView.tag?.toString(),
                contentsKeyPairsListCon,
                listOf(imageView),
//                enableClick,
//                clickViewStrList,
            )
        }
    }

    private suspend fun setTextView(
        fragment: Fragment?,
        fannelInfoMap: Map<String, String>,
        setReplaceVariableMap: Map<String, String>?,
        busyboxExecutor: BusyboxExecutor?,
        holder: EditListViewHolder,
        editListPosition: Int,
        idInt: Int?,
        contentsTag: String,
        contentsKeyPairsList: List<Pair<String, String>>,
        contentsKeyPairsListCon: String?,
        totalConstraintLayout: ConstraintLayout?,
        tagIdMap: Map<String, Int>?,
        totalMapListElInfo: String,
    ){
        val context =
            fragment?.context ?: return
        val textView = withContext(Dispatchers.Main) {
            EditComponent.AdapterSetter.makeTextViewLayout(
                context,
                idInt,
                contentsTag,
            )
        }
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultSDebugAppDirPath, "lcontents_${editListPosition}.txt").absolutePath,
//                                    listOf(
//                                        "textTagToMapForContents: ${textTagToMapForContents}",
//                                        "imageTagToMapForContents: ${imageTagToMapForContents}",
//                                        "contentsFrameLayout: ${contentsFrameLayout}",
//                                    ).joinToString("\n")
//                                )
//                                if (extractContentsFrameLayout == null) {
        withContext(Dispatchers.Main) {
            totalConstraintLayout?.addView(
                textView
            )
        }
//                                }
        val enableClick =
            withContext(Dispatchers.IO) {
                EditComponent.Template.ClickManager.isClickEnable(
                    contentsKeyPairsList.toMap()
                )
            }
//        val clickViewStrList = withContext(Dispatchers.IO) {
//            EditComponent.Template.ClickViewManager.makeClickViewStrList(
//                contentsKeyPairsList
//            )
//        }
        CoroutineScope(Dispatchers.Main).launch {
//                                    withContext(Dispatchers.IO) {
//                                        if(idInt == 10100) {
//                                            FileSystems.updateFile(
//                                                File(
//                                                    UsePath.cmdclickDefaultAppDirPath,
//                                                    "lcontentsTagBerore.txt"
//                                                ).absolutePath,
//                                                listOf(
//                                                    "frameTag: ${frameTag}",
//                                                    "editListPosition: ${editListPosition}",
//                                                    "holder.bindingAdapterPosition: ${holder.bindingAdapterPosition}",
//                                                    "mapListElInfoForContentsTag: ${mapListElInfoForContentsTag}",
//                                                    "layoutWeight: ${layoutWeight}",
//                                                    "contentsTag: ${contentsTag}",
//                                                    "\n\ncontentsKeyPairsListConSrc: ${contentsKeyPairsListConSrc}\n\n",
//                                                    "\n\ncontentsKeyPairsList: ${contentsKeyPairsList}\n\n",
//                                                ).joinToString("\n") + "\n\n==============\n\n\n"
//                                            )
//                                        }
//                                    }

            EditTextViewSetter.setForConstraint(
                context,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                tagIdMap,
                textView,
                contentsKeyPairsList,
                contentsKeyPairsListCon,
                totalSettingValMap.get(
                    contentsTag
                ),
                0,
                enableClick,
                outValue,
                density,
                "contentsTag: ${contentsTag}, ${totalMapListElInfo}",
            )
        }
        CoroutineScope(Dispatchers.Main).launch setClick@ {
            clickHandler(
                holder,
                editListPosition,
                contentsKeyPairsList,
                contentsTag,
                contentsKeyPairsListCon,
                listOf(textView),
//                enableClick,
//                clickViewStrList,
            )
        }
    }

    private suspend fun setFrameLayout(
        fragment: Fragment?,
        holder: EditListViewHolder,
        editListPosition: Int,
        contentsTag: String,
        contentsKeyPairsList: List<Pair<String, String>>,
        contentsKeyPairsListCon: String?,
        totalConstraintLayout: ConstraintLayout?,
        idInt: Int?,
        tagIdMap: Map<String, Int>?,
        alreadyUseTagList: MutableList<String>,
        alreadyUseTagListMutex: Mutex,
        totalMapListElInfo: String,
        varNameToBitmapMapInFrame: Map<String, Bitmap?>,
    ){
        val context =
            fragment?.context ?: return
        val textTagToMapForContents = withContext(Dispatchers.IO) {
            EditComponent.AdapterSetter.makeTextTagToMap(
                contentsKeyPairsList,
                contentsTag,
                totalSettingValMap,
            )
        }
        val imageTagToMapForContents = withContext(Dispatchers.IO) {
            EditComponent.AdapterSetter.makeImageTagToMap(
                contentsKeyPairsList,
            )
        }
        val textOrImageTagListForContents =
            textTagToMapForContents.keys.toList() +
                    imageTagToMapForContents.keys.toList()
        val tagGenreForContents =
            EditComponent.Template.TagManager.TagGenre.TEXT_OR_IMAGE_TAG
        val isDuplicateTextOrImageTagErrJobForContents = withContext(Dispatchers.IO) {
            val alreadyUseTagListSrc =
                EditComponent.AdapterSetter.AlreadyUseTagListHandler.get(
                    alreadyUseTagList,
                    alreadyUseTagListMutex
                )
            run dupCheckTextOrImageTag@{
                textOrImageTagListForContents.forEach {
                        textOrImageTag ->
                    val correctContentsTag =
                        EditComponent.AdapterSetter.tagDuplicateErrHandler(
                            context,
                            tagGenreForContents,
                            textOrImageTag,
                            alreadyUseTagListSrc,
                            totalMapListElInfo,
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
                    ) return@dupCheckTextOrImageTag true
                }
                false
            }
        }
        if(
            isDuplicateTextOrImageTagErrJobForContents
        ) return
        val returnContentsFrameLayout = withContext(Dispatchers.Main) {
            EditComponent.AdapterSetter.makeContentsFrameLayout(
                context,
                idInt,
                contentsTag,
                contentsKeyPairsList,
                textTagToMapForContents,
                imageTagToMapForContents,
                null,
            )
        }
        val contentsFrameLayout =
            returnContentsFrameLayout.frameLayoutRef.get()
                ?: return
//                ?.apply {
//                id = idInt ?: id
//            } ?: return
        val tagToTextViewListForContents = returnContentsFrameLayout.tagToTextViewListRef.get()
            ?: return
        val tagToImageViewListForContents = returnContentsFrameLayout.tagToImageViewListRef.get()
            ?: return
        val clickTagToViewListForContents = returnContentsFrameLayout.clickTagToViewListRef.get()
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultSDebugAppDirPath, "lcontents_${editListPosition}.txt").absolutePath,
//                                    listOf(
//                                        "textTagToMapForContents: ${textTagToMapForContents}",
//                                        "imageTagToMapForContents: ${imageTagToMapForContents}",
//                                        "contentsFrameLayout: ${contentsFrameLayout}",
//                                    ).joinToString("\n")
//                                )
        CoroutineScope(Dispatchers.IO).launch {
//                                    val varNameToBitmapMapInContents =
            val contentsImageView = withContext(Dispatchers.IO){
                contentsFrameLayout.children.firstOrNull {
                        view ->
                    view is AppCompatImageView
                } as? AppCompatImageView
            }
            withContext(Dispatchers.IO){
                val topLevelVarNameToBitmapMap =
                    globalVarNameToBitmapMap + varNameToBitmapMapInFrame
                ImageActionManager().exec(
                    fragment,
                    fannelInfoMap,
                    setReplaceVariableMap,
                    busyboxExecutor,
                    contentsImageView,
                    requestBuilderSrc,
                    imageActionAsyncCoroutine,
                    topLevelVarNameToBitmapMap.map {
                        it.key
                    },
                    topLevelVarNameToBitmapMap,
                    contentsKeyPairsListCon,
                    "contentsTag: ${contentsTag}, ${totalMapListElInfo}",
                )
            }
        }
//                                if (extractContentsFrameLayout == null) {
        withContext(Dispatchers.Main) {
            totalConstraintLayout?.addView(
                contentsFrameLayout
            )
        }
//                                }
//        val enableClick =
//            withContext(Dispatchers.IO) {
//                EditComponent.Template.ClickManager.isClickEnable(
//                    contentsKeyPairsList
//                )
//            }
//        val clickViewStrList = withContext(Dispatchers.IO) {
//            EditComponent.Template.ClickViewManager.makeClickViewStrList(
//                contentsKeyPairsList
//            )
//        }
        CoroutineScope(Dispatchers.Main).launch {
//                                    withContext(Dispatchers.IO) {
//                                        if(idInt == 10100) {
//                                            FileSystems.updateFile(
//                                                File(
//                                                    UsePath.cmdclickDefaultAppDirPath,
//                                                    "lcontentsTagBerore.txt"
//                                                ).absolutePath,
//                                                listOf(
//                                                    "frameTag: ${frameTag}",
//                                                    "editListPosition: ${editListPosition}",
//                                                    "holder.bindingAdapterPosition: ${holder.bindingAdapterPosition}",
//                                                    "mapListElInfoForContentsTag: ${mapListElInfoForContentsTag}",
//                                                    "layoutWeight: ${layoutWeight}",
//                                                    "contentsTag: ${contentsTag}",
//                                                    "\n\ncontentsKeyPairsListConSrc: ${contentsKeyPairsListConSrc}\n\n",
//                                                    "\n\ncontentsKeyPairsList: ${contentsKeyPairsList}\n\n",
//                                                ).joinToString("\n") + "\n\n==============\n\n\n"
//                                            )
//                                        }
//                                    }
            val clickViewTagList = clickTagToViewListForContents?.map {
                (tagName, _) ->
                tagName
            }
            EditConstraintFrameMaker.make(
                context,
                idInt,
                tagIdMap,
                contentsFrameLayout,
                fannelInfoMap,
                setReplaceVariableMap,
                busyboxExecutor,
                contentsKeyPairsList,
                textTagToMapForContents,
                tagToTextViewListForContents,
                imageTagToMapForContents,
                tagToImageViewListForContents,
                0,
                "contentsTag: ${contentsTag}, ${totalMapListElInfo}",
                clickViewTagList,
                outValue,
                requestBuilderSrc,
                density,
            )
        }
        val clickViewList = clickTagToViewListForContents?.map {
            (_, view) ->
            view
        }
        if(
            clickViewList.isNullOrEmpty()
        ) return
        CoroutineScope(Dispatchers.Main).launch setClick@ {
            clickHandler(
                holder,
                editListPosition,
                contentsKeyPairsList,
                contentsTag,
                contentsKeyPairsListCon,
                clickViewList,
//                enableClick,
//                    clickViewStrList,
            )
        }
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
            val editConstraintListAdapter = editRecyclerView.adapter as EditConstraintListAdapter
            val lineMap =
                editConstraintListAdapter.lineMapList.getOrNull(
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

    private fun updateCmdSettingTotalMap(
        fannelContentsList: List<String>?
    ){
        initSettingValMap = RecordNumToMapNameValueInHolder.parse(
            fannelContentsList,
            CommandClickScriptVariable.SETTING_SEC_START,
            CommandClickScriptVariable.SETTING_SEC_END,
        )
        initCmdValMap = RecordNumToMapNameValueInHolder.parse(
            fannelContentsList,
            CommandClickScriptVariable.CMD_SEC_START,
            CommandClickScriptVariable.CMD_SEC_END,
        )
        totalSettingValMap =  makeTotalSettingValMap(
            initSettingValMap,
            initCmdValMap,
        )
    }

    fun saveFannelCon() {
        MainFannelUpdater.execSaveFannelCon(
            fannelInfoMap,
            fannelContentsList
        )
    }

    private suspend fun frameClickHandle(
        frameTag: String,
        holder: EditListViewHolder,
        editListPosition: Int,
        materialCardView: MaterialCardView?,
        frameKeyPairsList: List<Pair<String, String>>,
        enableClick: Boolean,
    ){
        if (
            materialCardView == null
        ) return
        val isConsec =
            withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairsList,
                    onConsecKey,
                ) == switchOn
            }

        withContext(Dispatchers.Main) {
            materialCardView.isClickable = enableClick
//                    rippleColor =  context.getColorStateList(R.color.trans)
//                    setBackgroundResource(0)
        }
        if(!enableClick) return
        withContext(Dispatchers.Main) {
            when (isConsec) {
                true -> with(materialCardView) {
                    setOnTouchListener(android.view.View.OnTouchListener { v, event ->
                        when (event.action) {
                            android.view.MotionEvent.ACTION_DOWN -> {
                                editAdapterTouchDownListener?.onEditAdapterTouchDown(
                                    materialCardView,
                                    frameTag,
                                    holder,
                                    editListPosition
                                )
                            }

                            android.view.MotionEvent.ACTION_UP,
                            android.view.MotionEvent.ACTION_CANCEL,
                                -> {
                                editAdapterTouchUpListener?.onEditAdapterTouchUp(
                                    materialCardView,
                                    frameTag,
                                    holder,
                                    editListPosition
                                )
                                v.performClick()
                            }
                        }
                        true
                    })
                }

                else -> materialCardView.setOnClickListener {
                    editAdapterClickListener?.onEditAdapterClick(
                        materialCardView,
                        frameTag,
                        holder,
                        editListPosition,
                    )
                }
            }
        }
    }

    private suspend fun publishContents(
        frameTag: String,
        frameTagToContentsMapList: Map<String, List<List<String>>>,
        frameVarNameToValueMap: Map<String, String>,
        srcTitle: String,
        srcCon: String,
        srcImage: String,
        bindingAdapterPosition: Int,
        contentsChannel: Channel<
                List<Pair<String, String?>>,
            >,
        mapListElInfo: String,
    ){
        val frameTagToContentsKeysListMapWithReplace =
            withContext(Dispatchers.IO) {
                frameTagToContentsMapList.map {
                    val key = CmdClickMap.replace(
                        it.key,
                        frameVarNameToValueMap,
                    )
                    key to it.value
                }.toMap()
            }
        withContext(Dispatchers.IO) {
            val contentsKeysListMapWithReplace =
                frameTagToContentsKeysListMapWithReplace.get(frameTag)
                    ?: return@withContext
            val jobList = contentsKeysListMapWithReplace.mapIndexed setContents@{ contentsIndex, contentsKeyValues ->
                    async {
                        val contentsTagToKeyPairsList = withContext(Dispatchers.IO) {
                            EditComponent.AdapterSetter.makeContentsTagToKeyPairsList(
                                context,
                                contentsKeyValues,
                                frameVarNameToValueMap,
                                srcTitle,
                                srcCon,
                                srcImage,
                                bindingAdapterPosition,
                                mapListElInfo,
                            )
                        }
                        contentsChannel.send(
                            contentsTagToKeyPairsList
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
        contentsTag: String?,
        contentsKeyPairsListCon: String?,
        clickViewList: List<View>,
//        enableClick: Boolean,
//        clickViewStrList: List<String>,
    ){
        if(
            context == null
//            || !enableClick
        ) return
//        val enableClick =
//            EditComponent.Template.ClickManager.isClickEnable(contentsKeyPairsList)

        CoroutineScope(Dispatchers.IO).launch {
            if(
                contentsTag.isNullOrEmpty()
            ) return@launch
            holder.updateKeyPairListConMap(
                contentsTag,
                contentsKeyPairsListCon
            )
        }
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lclicllll.txt").absolutePath,
//            listOf(
//                "onClick: ${onClick}",
//                "isJsAc: ${isJsAc}",
//                "disableClick: ${disableClick}",
//                "contentsKeyPairsListCon: ${contentsKeyPairsListCon}"
//            ).joinToString("\n") + "\n==========\n\n"
//        )
//        val clickViewList =
//            withContext(Dispatchers.IO) {
//                EditComponent.Template.ClickViewManager.makeClickViewList(
//                    contentsFrameLayout.children,
//                    clickViewStrList
////                    PairListTool.getValue(
////                        contentsKeyPairsList,
////                        onClickViewsKey,
////                    )
//                )
//            }
        val isConsec = withContext(Dispatchers.IO) {
            PairListTool.getValue(
                contentsKeyPairsList,
                onConsecKey,
            ) == EditComponent.Template.switchOn
        }
        clickViewList.forEach { clickView ->
//            withContext(Dispatchers.Main) {
//                clickView.isClickable = enableClick
//            }
//            CoroutineScope(Dispatchers.IO).launch {
//                withContext(Dispatchers.IO){
//                    delay(delayTime)
//                }
//                withContext(Dispatchers.Main){
//                    when(enableClick) {
//                        false -> clickView.setBackgroundResource(0)
//                        else -> clickView.setBackgroundResource(outValue.resourceId)
//                    }
//                }
//            }
            withContext(Dispatchers.Main) {
//                clickView.setBackgroundResource(outValue.resourceId)
//                clickView.isClickable = true
                when (isConsec) {
                    true ->
                        with(clickView) {
                            setOnTouchListener(android.view.View.OnTouchListener { v, event ->
                                when (event.action) {
                                    android.view.MotionEvent.ACTION_DOWN -> {
                                        editAdapterTouchDownListener?.onEditAdapterTouchDown(
                                            clickView,
                                            null,
                                            holder,
                                            editListPosition
                                        )
                                    }

                                    android.view.MotionEvent.ACTION_UP,
                                    android.view.MotionEvent.ACTION_CANCEL,
                                        -> {
                                        editAdapterTouchUpListener?.onEditAdapterTouchUp(
                                            clickView,
                                            null,
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
                            clickView,
                            null,
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
                EditComponent.AdapterSetter.makeLinearFrameKeyPairsList(
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
            EditListConfig.EditListConfigKey.DELETE.key,
            setReplaceVariableMap,
        )
        filterPrefix =
            FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                ListSettingsForEditList.ListSettingKey.PREFIX.key
            ) ?: String()
        filterSuffix =
            FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                ListSettingsForEditList.ListSettingKey.SUFFIX.key
            ) ?: String()
    }
    fun getLayoutConfigMap(): Map<String, String> {
        return layoutConfigMap
    }
}
