package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.list_index_for_edit.EditFrameMaker
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.LayoutSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class EditComponentListAdapter(
    val context: Context?,
    val fannelInfoMap: Map<String, String>,
    val setReplaceVariableMap: Map<String, String>?,
    val listIndexConfigMap: Map<String, String>?,
    val busyboxExecutor: BusyboxExecutor?,
    val indexListMap: Map<String, String>,
    var lineMapList: MutableList<Map<String, String>>,
): RecyclerView.Adapter<EditComponentListAdapter.ListIndexListViewHolder>()
{
//    private val fannelInfoMap = editFragmentRef.get()?.fannelInfoMap ?: emptyMap()
//    private val context = editFragmentRef.get()?.context
//    private val activity = editFragment.activity
    private val maxTakeSize = 150
    private val listLimitSize = 300
    private val editExecuteAlways = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
//    private val busyboxExecutor = editFragmentRef.get()?.busyboxExecutor

//    private val listIndexConfigMap =
//        editFragmentRef.get()?.listIndexConfigMap
//            ?: emptyMap()
    private val layoutConfigMap = LayoutSettingsForListIndex.getLayoutConfigMap(
        listIndexConfigMap
    )
    private val layoutMargin = layoutConfigMap.get(
        LayoutSettingsForListIndex.LayoutSettingKey.MARGIN.key
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
        LayoutSettingsForListIndex.LayoutSettingKey.ELEVATION.key
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
//    private val layoutType = LayoutSettingsForListIndex.decideLayoutType(
//        layoutConfigMap
//    )
//    private var performMap: Map<String, String> = mapOf()

//    val checkItemConfigMap = CmdClickMap.createMap(
//        listIndexConfigMap.get(
//            ListIndexEditConfig.ListIndexConfigKey.CHECK_ITEM.key
//        ),
//        '|'
//    ).toMap()
    private val frameMapListToLinearMapList = ListSettingsForListIndex.ViewLayoutPathManager.parse(
        fannelInfoMap,
        setReplaceVariableMap,
        indexListMap
    )
    private val frameMap = frameMapListToLinearMapList?.first ?: emptyMap()
    private val frameTagToLinearKeysListMap = frameMapListToLinearMapList?.second ?: emptyMap()
    var deleteConfigMap: Map<String, String> = mapOf()
//    private val qrDialogConfigMap =
//        editFragmentRef.get()?.qrDialogConfig ?: mapOf()
//    private val textImagePngBitMap = ImageAdapterTool.makeFileMarkBitMap(
//        editFragmentRef.get()?.context,
//        AssetsFileManager.textImagePingPath
//    )
//    private val qrLogoConfigMap = QrLogoSettingsForQrDialog.makeLogoConfigMap(
//        qrDialogConfigMap
//    )
//    private val iconConfigMap =
//        QrLogoSettingsForQrDialog.QrIconSettingKeysForQrDialog.makeIconConfigMap(
//            qrLogoConfigMap
//        )
//    private val itemNameToNameColorConfigMap =
//        QrLogoSettingsForQrDialog.QrIconSettingKeysForQrDialog.makeIconNameConfigMap(
//            editFragmentRef.get()?.setReplaceVariableMap,
//            fannelInfoMap,
//            iconConfigMap,
//        )
    private var recentAppDirPath = String()
//    private var filterDir = String()
    private var filterPrefix = String()
    private var filterSuffix = String()

    init {
        if(lineMapList.size == 0) {
            setListProperty()
        }
    }

    class ListIndexListViewHolder(
//        val activity: FragmentActivity?,
        val view: View
    ): RecyclerView.ViewHolder(view) {
        val materialCardView =
            view.findViewById<MaterialCardView>(
                com.puutaro.commandclick.R.id.edit_component_adapter_mterial_card_view
            )
        var keyPairListConMap: MutableMap<String, String?> = mutableMapOf()
//        val editComponentLinearLayout =
//            view.findViewById<LinearLayoutCompat>(
//                com.puutaro.commandclick.R.id.edit_component_adapter_linearlayout
//            )
//        val fileContentsQrLogoLinearLayout =
//            view.findViewById<RelativeLayout>(
//                com.puutaro.commandclick.R.id.list_index_edit_adapter_logo_linearlayout
//            )
//        val fileContentsQrLogoView =
//            view.findViewById<AppCompatImageView>(
//                com.puutaro.commandclick.R.id.list_index_edit_adapter_contents
//            )
//        val qrLogoImageCaptionTextView =
//            view.findViewById<MagicTextView>(
//                com.puutaro.commandclick.R.id.list_index_edit_adapter_image_caption
//            )
//
//        val rightLinearlayout =
//            view.findViewById<LinearLayoutCompat>(
//                com.puutaro.commandclick.R.id.list_index_edit_adapter_vertical_linearlayout
//            )
//        val fileNameTextView =
//            view.findViewById<AppCompatTextView>(
//                com.puutaro.commandclick.R.id.list_index_edit_adapter_file_name
//            )
//        val fileDescTextView =
//            view.findViewById<AppCompatTextView>(
//                com.puutaro.commandclick.R.id.list_index_edit_adapter_file_desc
//            )
        var fileName = String()
    }

    override fun getItemId(position: Int): Long {
        setHasStableIds(true)
        return super.getItemId(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListIndexListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            com.puutaro.commandclick.R.layout.list_index_edit_adapter_layout,
            parent,
            false
        )
        return ListIndexListViewHolder(
//            activity,
            itemView
        )
    }

    override fun getItemCount(): Int = lineMapList.size
    private val typeSeparator = EditComponent.Template.typeSeparator

    private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
    private val isConsecKey = EditComponent.Template.EditComponentKey.IS_CONSEC.key
    override fun onBindViewHolder(
        holder: ListIndexListViewHolder,
        listIndexPosition: Int
    ) {
        if(
            context == null
        ) return
        if(
            listIndexPosition > listLimitSize
        ) return
        initListProperty(listIndexPosition)
        val lineMap = lineMapList[listIndexPosition]
        val srcLabel = lineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.SRC_LABEL.key
        ) ?: String()
        val srcCon = lineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
        ) ?: String()
        val frameTag = lineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.VIEW_LAYOUT_TAG.key
        ).let {
            if(
                !it.isNullOrEmpty()
            ) return@let it
            indexListMap.get(
                ListSettingsForListIndex.ListSettingKey.DEFAULT_FRAME_TAG.key
            )
        }
        if(
            frameTag.isNullOrEmpty()
        ) return
        CoroutineScope(Dispatchers.IO).launch {
            val frameKeyPairsCon = withContext(Dispatchers.IO) {
                EditComponent.Template.ReplaceHolder.replaceHolder(
                    frameMap.get(frameTag),
                    srcLabel,
                    srcCon
                )
            }
            val frameKeyPairsList = withContext(Dispatchers.IO) {
                CmdClickMap.createMap(
                    frameKeyPairsCon,
                    typeSeparator
                )
            }
            FileSystems.writeFile(
                File(UsePath.cmdclickDefaultAppDirPath, "lviewLayout.txt").absolutePath,
                listOf(
                    "indexListMap: ${indexListMap}",
                    "frameMap: ${frameMap}",
                    "frameKeyMap: ${frameKeyPairsList}",
                    "linearMapList: ${frameTagToLinearKeysListMap}",
                ).joinToString("\n\n")
            )
            val materialCardView = holder.materialCardView.apply {
                layoutElevation?.let {
                    elevation = it
                }
            }
//            GridLayoutManager
            val cardLinearParams = materialCardView.layoutParams as GridLayoutManager.LayoutParams
            layoutMargin?.let {
                cardLinearParams.setMargins(it)
            }
            withContext(Dispatchers.Main) {
                val frameFrameLayout = EditFrameMaker.make(
                    context,
                    frameKeyPairsList,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    null,
                    frameTag,
                    false
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
                                                listIndexPosition
                                            )
                                        }
                                        android.view.MotionEvent.ACTION_UP,
                                        android.view.MotionEvent.ACTION_CANCEL,
                                        -> {
                                            editAdapterTouchUpListener?.onEditAdapterTouchUp(
                                                frameFrameLayout,
                                                holder,
                                                listIndexPosition
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
                                    listIndexPosition,
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
                                srcLabel,
                                srcCon
                            )
                        }
                        val linearFrameKeyPairsList = withContext(Dispatchers.IO) {
                            CmdClickMap.createMap(
                                linearFrameKeyPairsListCon,
                                typeSeparator
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
                            linearFrameKeyPairsList,
                            0,
                            layoutWeight,
                            linearFrameTag,
                            false
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
                                                                listIndexPosition
                                                            )
                                                        }
                                                        android.view.MotionEvent.ACTION_UP,
                                                        android.view.MotionEvent.ACTION_CANCEL,
                                                        -> {
                                                            editAdapterTouchUpListener?.onEditAdapterTouchUp(
                                                                linearFrameLayout,
                                                                holder,
                                                                listIndexPosition
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
                                                listIndexPosition,
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

//            withContext(Dispatchers.Main) {
//                ListIndexEditConfig.setCheckToMaterialCardView(
//                    holder.materialCardView,
//                    checkItemConfigMap,
//                    lineMapList,
//                    listIndexPosition,
//                )
//            }
//
//            val fileNameOrInstallFannelLine =
//                lineMapList.getOrNull(listIndexPosition)
//                    ?: return@launch
//            holder.fileName = FannelListVariable.getFannelName(
//                fileNameOrInstallFannelLine
//            )
//            val fileNameText = withContext(Dispatchers.Main) {
//                ListIndexEditConfig.makeFileNameText(
////                    listIndexTypeKey,
//                    holder.fileNameTextView,
//                    holder.fileName,
//                    listIndexConfigMap,
//                    busyboxExecutor,
//                )
//            }
//            val fileConList = makeFileConList(holder.fileName)
//            withContext(Dispatchers.Main){
//                holder.rightLinearlayout.isVisible = false
//                if(
//                    fileNameText.isNullOrEmpty()
//                ) return@withContext
//                holder.qrLogoImageCaptionTextView.isVisible = true
//                holder.qrLogoImageCaptionTextView.text = fileNameText
//            }
//            when(layoutType){
//                LayoutSettingsForListIndex.LayoutTypeValueStr.LINEAR -> {
//                    withContext(Dispatchers.Main){
//                        if(
//                            fileNameText.isNullOrEmpty()
//                        ) return@withContext
//                        holder.fileNameTextView?.text = fileNameText
//                    }
//                    setDescView(
//                        holder.fileDescTextView,
//                        fileNameOrInstallFannelLine,
//                        fileConList.joinToString("\n"),
//                    )
//                }
//                LayoutSettingsForListIndex.LayoutTypeValueStr.GRID -> {
//                    withContext(Dispatchers.Main){
//                        holder.rightLinearlayout.isVisible = false
//                        if(
//                            fileNameText.isNullOrEmpty()
//                        ) return@withContext
//                        holder.qrLogoImageCaptionTextView.isVisible = true
//                        holder.qrLogoImageCaptionTextView.text = fileNameText
//                    }
//                }
//            }
            val fileConBackGroundColorInt = withContext(Dispatchers.IO) {
                val editExecuteValueForInstallFannel = String()
//                    when(
//                    listIndexTypeKey
//                ){
//                    TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
//                    -> FannelListVariable.getEditExecute(
//                        fileNameOrInstallFannelLine
//                    )
//                    else -> String()
//                }
//                setFileContentsBackColor(
//                    fileConList,
////                    fileNameOrInstallFannelLine,
//                    editExecuteValueForInstallFannel,
//                )
            }

//            withContext(Dispatchers.Main) {
//                context?.let {
//                    holder.fileContentsQrLogoLinearLayout.backgroundTintList =
//                        it.getColorStateList(fileConBackGroundColorInt)
//                }
//            }
//            withContext(Dispatchers.Main) {
//                QrLogoSettingsForQrDialog.OneSideLength.setLayout(
//                    editFragmentRef.get(),
//                    holder.baseLinearLayout,
//                    holder.materialCardView,
//                    holder.fileContentsQrLogoLinearLayout,
//                    qrLogoConfigMap,
////                    layoutType
//                )
//            }
//            withContext(Dispatchers.Main) {
//                val disableQrLogo =
//                    QrLogoSettingsForQrDialog.Disable.how(qrLogoConfigMap)
//                if(
//                    disableQrLogo
//                ) return@withContext
//                qrLogoSetHandler(
//                    holder,
//                    recentAppDirPath,
//                )
//            }
            withContext(Dispatchers.Main) {
                val itemView = holder.itemView
//                itemView.setOnLongClickListener {
//                    itemLongClickListener?.onItemLongClick(
//                        itemView,
//                        holder,
//                        listIndexPosition
//                    )
//                    true
//                }
                itemView.setOnClickListener {
                    editAdapterClickListener?.onEditAdapterClick(
                        itemView,
                        holder,
                        listIndexPosition,
                    )
                }
//                val fileContentsQrLogoView = holder.fileContentsQrLogoView
//                fileContentsQrLogoView.setOnClickListener {
//                    fileQrLogoClickListener?.onFileQrLogoClick(
//                        itemView,
//                        holder,
//                        listIndexPosition
//                    )
//                }
//                fileContentsQrLogoView.setOnLongClickListener {
//                    qrLongClickListener?.onQrLongClick(
//                        fileContentsQrLogoView,
//                        holder,
//                        listIndexPosition
//                    )
//                    true
//                }
            }
        }
    }

    var editAdapterClickListener: OnEditAdapterClickListener? = null
//    var qrLongClickListener: OnQrLogoLongClickListener? = null
    interface OnEditAdapterClickListener {
        fun onEditAdapterClick(
            itemView: View,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int,
        )
    }

    var editAdapterTouchDownListener: OnEditAdapterTouchDownListener? = null
    interface OnEditAdapterTouchDownListener {
        fun onEditAdapterTouchDown(
            itemView: View,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int,
        )
    }

    var editAdapterTouchUpListener: OnEditAdapterTouchUpListener? = null
    interface OnEditAdapterTouchUpListener {
        fun onEditAdapterTouchUp(
            itemView: View,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int,
        )
    }

//    interface OnQrLogoLongClickListener {
//        fun onQrLongClick(
//            imageView: AppCompatImageView,
//            holder: ListIndexListViewHolder,
//            listIndexPosition: Int
//        )
//    }

//    var fileQrLogoClickListener: OnFileQrLogoItemClickListener? = null
//    interface OnFileQrLogoItemClickListener {
//        fun onFileQrLogoClick(
//            itemView: View,
//            holder: ListIndexListViewHolder,
//            listIndexPosition: Int,
//        )
//    }

//    var itemLongClickListener: OnItemLongClickListener? = null
//    interface OnItemLongClickListener {
//        fun onItemLongClick(
//            itemView: View,
//            holder: ListIndexListViewHolder,
//            listIndexPosition: Int
//        )
//    }

    private fun makeFileConList(
        fileName: String,
    ): List<String> {
        if (
            listIndexConfigMap.isNullOrEmpty()
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
        val makeFileDescArgsMaker = ListIndexEditConfig.MakeFileDescArgsMaker(
//            filterDir,
            fileNameOrInstallFannelLine,
            fileCon,
            listIndexConfigMap,
            busyboxExecutor,
        )
        val descCon = ListIndexEditConfig.makeFileDesc(
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
//        val languageType =
//            CommandClickVariables.judgeJsOrShellFromSuffix(fileName)

//        val languageTypeToSectionHolderMap =
//            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
//        val settingSectionStart = languageTypeToSectionHolderMap?.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
//        ) as String
//        val settingSectionEnd = languageTypeToSectionHolderMap.get(
//            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
//        ) as String
        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            fileConList,
            CommandClickScriptVariable.SETTING_SEC_START,
            CommandClickScriptVariable.SETTING_SEC_END,
//            settingSectionStart,
//            settingSectionEnd
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

//    private suspend fun qrLogoSetHandler(
//        holder: ListIndexListViewHolder,
//        recentAppDirPath: String,
//    ){
//        val itemName = holder.fileName.split("\t").lastOrNull() ?: String()
//        val okSetIcon = QrLogoSettingsForQrDialog.QrIconSettingKeysForQrDialog.set(
//            context,
////            filterDir,
////            listIndexTypeKey,
//            itemName,
//            holder.fileContentsQrLogoView,
//            holder.fileContentsQrLogoLinearLayout,
//            qrLogoConfigMap,
//            iconConfigMap,
//            itemNameToNameColorConfigMap,
//            textImagePngBitMap
//        )
//        if(okSetIcon) return
//        val qrMode = QrModeSettingKeysForQrDialog.getQrMode(qrDialogConfigMap)
//        when(qrMode){
//            QrModeSettingKeysForQrDialog.QrMode.TSV_EDIT -> {}
//            QrModeSettingKeysForQrDialog.QrMode.NORMAL,
//            QrModeSettingKeysForQrDialog.QrMode.FANNEL_REPO -> {
//                val qrLogoHandlerArgsMaker = withContext(Dispatchers.IO) {
//                    QrDialogConfig.QrLogoHandlerArgsMaker(
////                        editFragmentRef.get(),
//                        recentAppDirPath,
//                        qrLogoConfigMap,
////                        filterDir,
//                        holder.fileName,
//                        holder.fileContentsQrLogoView,
//                    )
//                }
//                withContext(Dispatchers.Main) {
//                    QrLogoSettingsForQrDialog.setQrLogoHandler(
//                        context,
//                        qrLogoHandlerArgsMaker
//                    )
//                }
//            }
//        }
//    }

    private fun initListProperty(
        listIndexPosition: Int,
    ){
        if(
            listIndexPosition != 0
        ) return
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "layout.txt").absolutePath,
            listOf(
                "frameMapListToLinearMapList: ${frameMapListToLinearMapList}",
                "frameMap: ${frameMap}",
                "linearMapList: ${frameTagToLinearKeysListMap}",
            ).joinToString("\n\n")
        )
        setListProperty()
    }

    private fun setListProperty(){
        recentAppDirPath = UsePath.cmdclickDefaultAppDirPath
//        performMap = PerformSettingForListIndex.makePerformMap(
//            listIndexConfigMap
//        )
//        indexListMap = ListIndexEditConfig.getConfigKeyMap(
//            listIndexConfigMap,
//            ListIndexEditConfig.ListIndexConfigKey.LIST.key
//        )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "getfile_index_list.txt").absolutePath,
//            listOf(
//                "listIndexConfigMap: ${listIndexConfigMap}",
//                "indexListMap: ${indexListMap}",
//            ).joinToString("\n\n\n")
//        )
        deleteConfigMap = ListIndexEditConfig.getConfigKeyMap(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.DELETE.key
        )
//        listIndexTypeKey = editFragmentRef.get()?.let {
//            ListIndexEditConfig.getListIndexType(
//                it
//            )
//        } ?: TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
//        filterDir = editFragmentRef.get()?.let {
//            ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
//                it,
//                indexListMap,
////                listIndexTypeKey
//            )
//        } ?: String()
        filterPrefix =
            FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                ListSettingsForListIndex.ListSettingKey.PREFIX.key
            ) ?: String()
        filterSuffix =
            FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariableMap,
                indexListMap,
                ListSettingsForListIndex.ListSettingKey.SUFFIX.key
            ) ?: String()
    }
    fun getLayoutConfigMap(): Map<String, String> {
        return layoutConfigMap
    }
}