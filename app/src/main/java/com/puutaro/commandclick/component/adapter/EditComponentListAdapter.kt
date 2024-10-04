package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.proccess.js_macro_libs.common_libs.JsActionKeyManager
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.LayoutSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.PerformSettingForListIndex
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
    val setReplaceVariablesMap: Map<String, String>?,
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
//    private val layoutType = LayoutSettingsForListIndex.decideLayoutType(
//        layoutConfigMap
//    )
    private var performMap: Map<String, String> = mapOf()

//    val checkItemConfigMap = CmdClickMap.createMap(
//        listIndexConfigMap.get(
//            ListIndexEditConfig.ListIndexConfigKey.CHECK_ITEM.key
//        ),
//        '|'
//    ).toMap()
    private val frameMapListToLinearMapList = ListSettingsForListIndex.ViewLayoutPathManager.parse(
        fannelInfoMap,
        setReplaceVariablesMap,
        indexListMap
    )
    private val frameMap = frameMapListToLinearMapList?.first ?: emptyMap()
    private val frameTagToLinearKeysListMap = frameMapListToLinearMapList?.second ?: emptyMap()
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

    companion object {
//        var indexListMap: Map<String, String> = mapOf()
        var deleteConfigMap: Map<String, String> = mapOf()
//        var listIndexTypeKey = TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
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
        val srcTitle = lineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.SRC_TITLE.key
        ) ?: String()
        val srcCon = lineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
        ) ?: String()
        val frameTag = lineMap.get(
            ListSettingsForListIndex.MapListPathManager.Key.VIEW_LAYOUT_TAG.key
        ) ?: return
        CoroutineScope(Dispatchers.IO).launch {
            val frameKeyPairsCon = withContext(Dispatchers.IO) {
                frameMap.get(frameTag)
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
            val materialCardView = holder.materialCardView
            withContext(Dispatchers.Main) {
                val frameFrameLayout = FrameMaker.make(
                    context,
                    frameKeyPairsList,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    null,
                    frameTag,
                    srcTitle,
                    srcCon,
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
                        ) return@let
                        holder.keyPairListConMap.put(
                            frameTag,
                            frameKeyPairsCon
                        )
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
                    linearKeys.forEach setFrame@ { linearFrameKeyPairsListCon ->
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
                        val linearFrameLayout = FrameMaker.make(
                            context,
                            linearFrameKeyPairsList,
                            0,
                            layoutWeight,
                            linearFrameTag,
                            srcTitle,
                            srcCon,
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
                                ) return@let
                                holder.keyPairListConMap.put(
                                    linearFrameTag,
                                    linearFrameKeyPairsListCon
                                )
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
        performMap = PerformSettingForListIndex.makePerformMap(
            listIndexConfigMap
        )
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
                setReplaceVariablesMap,
                indexListMap,
                ListSettingsForListIndex.ListSettingKey.PREFIX.key
            ) ?: String()
        filterSuffix =
            FilePrefixGetter.get(
                fannelInfoMap,
                setReplaceVariablesMap,
                indexListMap,
                ListSettingsForListIndex.ListSettingKey.SUFFIX.key
            ) ?: String()
    }
    fun getLayoutConfigMap(): Map<String, String> {
        return layoutConfigMap
    }


    private object FrameMaker {

        private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
        private val labelTagKey = EditComponent.Template.EditComponentKey.LABEL_TAG.key
        private val imageTagKey = EditComponent.Template.EditComponentKey.IMAGE_TAG.key
        private val labelKey = EditComponent.Template.EditComponentKey.LABEL.key
        private val imagePathKey = EditComponent.Template.EditComponentKey.IMAGE_PATH.key
        private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key
        private val textSizeKey = EditComponent.Template.EditComponentKey.TEXT_SIZE.key
        private val textColorKey = EditComponent.Template.EditComponentKey.TEXT_COLOR.key
        private val strokeColorKey = EditComponent.Template.EditComponentKey.STROKE_COLOR.key
        private val strokeWidthKey = EditComponent.Template.EditComponentKey.STROKE_WIDTH.key
        private val textAlphaKey = EditComponent.Template.EditComponentKey.TEXT_ALPHA.key
        private val imageAlphaKey = EditComponent.Template.EditComponentKey.IMAGE_ALPHA.key

        suspend fun make(
            context: Context?,
            frameKeyPairList: List<Pair<String, String>>?,
            width: Int,
            weight: Float?,
            tag: String?,
            srcTitle: String,
            srcCon: String,
        ): FrameLayout? {
            if(
                context == null
            ) return null
            val inflater = LayoutInflater.from(context)
            val buttonLayout = inflater.inflate(
                R.layout.icon_caption_layout,
                null
            ) as FrameLayout
            val height = withContext(Dispatchers.IO) {
                PairListTool.getValue(
                    frameKeyPairList,
                    heightKey,
                )?.let {
                    try {
                        ScreenSizeCalculator.toDp(context, it.toInt())
                    } catch(e: Exception){
                        null
                    }
                } ?: ViewGroup.LayoutParams.MATCH_PARENT
//                ,ScreenSizeCalculator.toDp(context, 50)
            }
            tag?.let {
                buttonLayout.tag = it
            }
            val param = LinearLayoutCompat.LayoutParams(
                width,
                height,
            )
            weight?.let {
                param.weight = it
            }
            val marginDp = ScreenSizeCalculator.toDp(
                context,
                context?.resources?.getDimension(R.dimen.toolbar_button_horizon_margin) ?: 0
            )
            param.marginStart = marginDp
            param.marginEnd = marginDp
            param.gravity = Gravity.CENTER
            buttonLayout.layoutParams = param
            buttonLayout.foregroundGravity = Gravity.CENTER

            buttonLayout.findViewById<AppCompatImageView>(R.id.icon_caption_layout_image)?.let {
                    imageButtonView ->
                val imageTag = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        imageTagKey,
                    )
                }
                val imagePath = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        imagePathKey,
                    )
                }
                val imageAlpha = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        imageAlphaKey,
                    )?.let {
                        try {
                            it.toFloat()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                setImageView(
                    imageButtonView,
                    imageTag,
                    imagePath,
                    imageAlpha,
                )
            }
            buttonLayout.findViewById<OutlineTextView>(R.id.icon_caption_layout_caption)?.let {
                    captionTextView ->
                val labelTag = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        labelTagKey,
                    )
                }
                val labelMap = withContext(Dispatchers.IO) {
                    PairListTool.getPair(
                        frameKeyPairList,
                        labelKey,
                    )?.let {
                        EditComponent.Template.LabelManager.createLabelMap(
                            it.second
                        )
                    }
                }
                val textSize = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        textSizeKey,
                    )?.let {
                        try {
                            it.toFloat()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                val textColorStr = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        textColorKey,
                    )
                }
                val strokeColorStr = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        strokeColorKey,
                    )
                }
                val strokeWidth = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        strokeWidthKey,
                    )?.let {
                        try {
                            it.toInt()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                val textAlpha = withContext(Dispatchers.IO) {
                    PairListTool.getValue(
                        frameKeyPairList,
                        textAlphaKey,
                    )?.let {
                        try {
                            it.toFloat()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                setCaption(
                    captionTextView,
                    labelTag,
                    labelMap,
                    textSize,
                    srcTitle,
                    srcCon,
                    textColorStr,
                    strokeColorStr,
                    strokeWidth,
                    textAlpha,
                )
            }
            return buttonLayout
        }

        private suspend fun setImageView(
            imageView: AppCompatImageView,
            imageTag: String?,
            imagePath: String?,
            imageAlpha: Float?,
        ) {
            imageView.isVisible = !imagePath.isNullOrEmpty()
            if(
                imagePath.isNullOrEmpty()
            ) {
                FileSystems.updateFile(
                    File(UsePath.cmdclickDefaultAppDirPath, "licon0.txt").absolutePath,
                    listOf(
                        "imageTag: ${imageTag}",
                    ).joinToString("\n")
                )
                return
            }
            imageTag?.let {
                imageView.tag = imageTag
            }
            imageAlpha?.let {
                imageView.alpha = imageAlpha
            }
            val imageViewContext = imageView.context

            val requestBuilder: RequestBuilder<Drawable> =
                Glide.with(imageViewContext)
                    .asDrawable()
                    .sizeMultiplier(0.1f)
            val icon = CmdClickIcons.values().firstOrNull {
                it.str == imagePath
            }
            FileSystems.updateFile(
                File(UsePath.cmdclickDefaultAppDirPath, "licon.txt").absolutePath,
                listOf(
                    "imagePath: ${imagePath}",
                    "icon: ${icon?.str}",
                ).joinToString("\n\n")
            )
            if(icon == null && File(imagePath).isFile){
                Glide.with(imageViewContext)
                    .load(imagePath)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(requestBuilder)
                    .into(imageView)
                return
            }
            val iconId = icon?.id
            imageView.tag = CmdClickIcons.values().firstOrNull {
                it.id == iconId
            }?.str

            val isImageFile =
                ExecSetToolbarButtonImage.isImageFile(icon?.assetsPath)
            when(true) {
                (isImageFile && icon != null) ->
                    ExecSetToolbarButtonImage.setImageButton(
                        imageView,
                        icon
                    )
                else -> {
                    imageView.isVisible = false
                    FileSystems.updateFile(
                        File(UsePath.cmdclickDefaultAppDirPath, "licon1.txt").absolutePath,
                        listOf(
                            "imageTag: ${imageTag}",
                            "imagePath: ${imagePath}",
                        ).joinToString("\n")
                    )
                }
//                Glide.with(imageViewContext)
//                    .load(iconId)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .thumbnail(requestBuilder)
//                    .into(imageView)
            }
        }

        private suspend fun setCaption(
            captionTextView: OutlineTextView,
            labelTag: String?,
            labelMap: Map<String, String>?,
            textSize: Float?,
            srcTitle: String,
            srcCon: String,
            textColorStr: String?,
            strokeColorStr: String?,
            strokeWidth: Int?,
            textAlpha: Float?,
        ) {
            val label = withContext(Dispatchers.IO) {
                EditComponent.Template.LabelManager.makeLabel(
                    labelMap,
                    srcTitle,
                    srcCon

                )
            }
            labelTag?.let {
                captionTextView.tag = labelTag
            }
            captionTextView.text = label
            CmdClickColor.values().firstOrNull {
                it.str == textColorStr
            }?.let {
                captionTextView.setFillColor(it.id)
            } ?: let {
                captionTextView.setFillColor(R.color.fill_gray)
            }
            CmdClickColor.values().firstOrNull {
                it.str == strokeColorStr
            }?.let {
                captionTextView.setStrokeColor(it.id)
            } ?: let {
                captionTextView.setStrokeColor(R.color.white)
            }
            strokeWidth?.let {
                captionTextView.outlineWidthSrc = it
            } ?: let {
                captionTextView.outlineWidthSrc = 2
            }
            textSize?.let {
                captionTextView.textSize = textSize
            }
            textAlpha?.let {
                captionTextView.alpha = textAlpha
            }
        }
    }
}
