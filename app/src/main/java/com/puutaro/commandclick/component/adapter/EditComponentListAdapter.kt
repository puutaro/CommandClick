package com.puutaro.commandclick.component.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
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
    private val maxTakeSize = 150
    private val listLimitSize = 300
    private val editExecuteAlways = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
    val initSettingValMap = RecordNumToMapNameValueInHolder.parse(
        fannelContentsList,
        CommandClickScriptVariable.SETTING_SEC_START,
        CommandClickScriptVariable.SETTING_SEC_END,
    )
    val initCmdValMap = RecordNumToMapNameValueInHolder.parse(
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

    fun getCurrentSettingVals(
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
    private val frameMapListToLinearMapList = ListSettingsForEditList.ViewLayoutPathManager.parse(
        fannelInfoMap,
        setReplaceVariableMap,
        viewLayoutPath
    )
    private val frameMap = frameMapListToLinearMapList?.first ?: emptyMap()
    private val frameTagToLinearKeysListMap = frameMapListToLinearMapList?.second ?: emptyMap()
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
                com.puutaro.commandclick.R.id.edit_component_adapter_mterial_card_view
            )
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
            com.puutaro.commandclick.R.layout.list_index_edit_adapter_layout,
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
        private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
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
        if(
            frameTag.isNullOrEmpty()
        ) return
        CoroutineScope(Dispatchers.IO).launch {
            val frameKeyPairsConSrc = withContext(Dispatchers.IO) {
                frameMap.get(frameTag)

            }
            val frameKeyPairsCon = withContext(Dispatchers.IO) {
                EditComponent.Template.ReplaceHolder.replaceHolder(
                    frameKeyPairsConSrc,
                    holder.srcTitle,
                    holder.srcCon,
                    holder.srcImage,
                    holder.bindingAdapterPosition,
                )
            }
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
                    false,
                    totalSettingValMap,

                )
                val frameKeyList = JsActionKeyManager.JsActionsKey.values().map{
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
                            frameKeyPairsConSrc
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
            withContext(Dispatchers.Main) {
                val baseLinearParam = LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(ScreenSizeCalculator.toDp(context,10))
                }
                val baseLinearLayout = LinearLayoutCompat(context).apply {
                    layoutParams = baseLinearParam
                    orientation = LinearLayoutCompat.VERTICAL
                }
                frameTagToLinearKeysListMap.get(frameTag)?.forEach {
                        linearKeys ->
                    val linearParam = LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
                    )
                    val weightSumFloat = 1f
                    val linearLayout = LinearLayoutCompat(context).apply {
                        layoutParams = linearParam
                        weightSum = weightSumFloat
                        orientation = LinearLayoutCompat.HORIZONTAL
                    }
                    val layoutWeight = weightSumFloat / linearKeys.size
                    linearKeys.forEach setFrame@ { linearFrameKeyPairsListConSrc ->
                        val linearFrameKeyPairsListCon = withContext(Dispatchers.IO) {
                            EditComponent.Template.ReplaceHolder.replaceHolder(
                                linearFrameKeyPairsListConSrc,
                                holder.srcTitle,
                                holder.srcCon,
                                holder.srcImage,
                                holder.bindingAdapterPosition,
                            )
                        }
                        val linearFrameKeyPairsList = withContext(Dispatchers.IO) {
                            makeLinearFrameKeyPairsList(
                                linearFrameKeyPairsListCon,
                            )
                        }
                        val linearFrameTag = withContext(Dispatchers.IO) {
                            PairListTool.getValue(
                                linearFrameKeyPairsList,
                                tagKey,
                            ) ?: String()
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
                            false,
                            totalSettingValMap
                        ) ?: return@setFrame
                        val linearKeyList = JsActionKeyManager.JsActionsKey.values().map{
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
//                                FileSystems.updateFile(
//                                    File(UsePath.cmdclickDefaultAppDirPath, "lSetClick.txt").absolutePath,
//                                    listOf(
//                                        "linearFrameKeyPairsList: ${linearFrameKeyPairsList}",
//                                        "isJsAc: ${isJsAc}",
//                                        "linearFrameLayout.tag: ${linearFrameLayout.tag}",
//
//                                        ).joinToString("\n")
//                                )
                                if(
                                    !isJsAc
                                    && linearFrameLayout.tag != null
                                ) {
                                    linearFrameLayout.setBackgroundResource(0)
                                    linearFrameLayout.isClickable = false
                                    return@let
                                }
                                holder.keyPairListConMap.put(
                                    linearFrameTag,
                                    linearFrameKeyPairsListCon
                                )
                                val outValue = TypedValue()
                                context.theme.resolveAttribute(
                                    android.R.attr.selectableItemBackground,
                                    outValue,
                                    true
                                )
                                linearFrameLayout.setBackgroundResource(outValue.resourceId)
                                linearFrameLayout.isClickable = true
                                when(isConsec) {
                                    true ->
                                            with (linearFrameLayout) {
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
                                    else -> linearFrameLayout.setOnClickListener {
                                            editAdapterClickListener?.onEditAdapterClick(
                                                linearFrameLayout,
                                                holder,
                                                editListPosition,
                                            )
                                        }
                                }

                            }
                        }
                        linearLayout.addView(linearFrameLayout)
                    }
                    baseLinearLayout.addView(linearLayout)
                }
                materialCardView.addView(baseLinearLayout)

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
//    var qrLongClickListener: OnQrLogoLongClickListener? = null
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
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lclickUpdateEnable.txt").absolutePath,
//                listOf(
//                    "layoutConfigMap: ${layoutConfigMap}",
//                    "indexListMap: ${indexListMap}",
//                    "enableClickUpdate: ${enableClickUpdate}",
//                ).joinToString("\n")
//            )
            if (
                !enableClickUpdate
            ) return
            execUpdateLineMapList(
                fragment,
                fannelInfoMap,
                setReplaceVariableMap,
                editListRecyclerView,
                indexListMap,
//                lineMapList,
                indexListPosition
            )
        }

        private fun execUpdateLineMapList(
            fragment: Fragment?,
            fannelInfoMap: Map<String, String>,
            setReplaceVariableMap: Map<String, String>?,
            editRecyclerView: RecyclerView,
            indexListMap: Map<String, String>,
//            lineMapList: List<Map<String, String>>,
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
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lSort.txt").absolutePath,
//                listOf(
//                    "before: ${lineMapList}\n\n",
//                    "lineMapList: ${editComponentListAdapter.lineMapList}"
//                ).joinToString("\n")
//            )
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
//            initSettingValMap.put(tag, settingValue)
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
//            initCmdValMap.put(tag, settingValue)
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
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lclickUpdate.txt").absolutePath,
//                    listOf(
//                        "tag: ${tag}",
//                        "settingValue: ${settingValue}\n",
//                        "frameOrLinearCon: ${frameOrLinearCon}\n",
//                        "updateFannelList: ${updateFannelList}\n",
//                        "fannelContentsList: ${fannelContentsList}",
//                    ).joinToString("\n")
//                )
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
//                CmdClickMap.createMap(
//                    frameOrLinearCon,
//                    EditComponent.Template.typeSeparator
//                ),
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
//        if(
//            PerformSettingForListIndex.howFastMode(performMap)
////            || listIndexTypeKey == TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
//        ) return emptyList()
//        return ReadText(
//            File(filterDir, fileName).absolutePath,
//        ).textToList().take(maxTakeSize)
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
        ) return com.puutaro.commandclick.R.color.fannel_icon_color
        if(
            editExecuteValueForInstallFannel == editExecuteAlways
        ) return com.puutaro.commandclick.R.color.terminal_color
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
        ) return com.puutaro.commandclick.R.color.terminal_color
        return com.puutaro.commandclick.R.color.fannel_icon_color
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
