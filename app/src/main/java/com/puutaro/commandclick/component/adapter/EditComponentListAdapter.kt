package com.puutaro.commandclick.component.adapter

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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ToolbarButtonProducerForEdit
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.LayoutSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.PerformSettingForListIndex
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.ScreenSizeCalculator
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference


class EditComponentListAdapter(
    private val editFragmentRef: WeakReference<EditFragment>,
    val indexListMap: Map<String, String>,
    var lineMapList: MutableList<Map<String, String>>,
): RecyclerView.Adapter<EditComponentListAdapter.ListIndexListViewHolder>()
{
    private val fannelInfoMap = editFragmentRef.get()?.fannelInfoMap ?: emptyMap()
    private val context = editFragmentRef.get()?.context
//    private val activity = editFragment.activity
    private val maxTakeSize = 150
    private val listLimitSize = 300
    private val editExecuteAlways = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
    private val busyboxExecutor = editFragmentRef.get()?.busyboxExecutor

    private val listIndexConfigMap =
        editFragmentRef.get()?.listIndexConfigMap
            ?: emptyMap()
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
        editFragmentRef.get()?.setReplaceVariableMap,
        indexListMap
    )
    private val frameMap = frameMapListToLinearMapList?.first ?: emptyMap()
    private val linearMapList = frameMapListToLinearMapList?.second ?: emptyMap()
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
                com.puutaro.commandclick.R.id.list_index_edit_adapter_mterial_card_view
            )
        val baseLinearLayout =
            view.findViewById<LinearLayoutCompat>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_horizontal_linearlayout
            )
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

    override fun onBindViewHolder(
        holder: ListIndexListViewHolder,
        listIndexPosition: Int
    ) {
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
            val frameKeyMap = withContext(Dispatchers.IO) {
                frameMap.get(frameTag)
            }
            FileSystems.writeFile(
                File(UsePath.cmdclickDefaultAppDirPath, "lviewLayout.txt").absolutePath,
                listOf(
                    "indexListMap: ${indexListMap}",
                    "frameMap: ${frameMap}",
                    "frameKeyMap: ${frameKeyMap}",
//                    "labelMap: ${labelMap}",
//                    "label: ${label}",
//                    "imagePath: ${imagePath}",
//                    "tag: ${tag}",
                ).joinToString("\n\n")
            )
            withContext(Dispatchers.Main) {
                val buttonFrame = FrameMaker.make(
                    editFragmentRef.get(),
                    frameKeyMap,
                    srcTitle,
                    srcCon,
                )
                holder.materialCardView.addView(buttonFrame)
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
                    fileNameClickListener?.onFileNameClick(
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

    var fileNameClickListener: OnFileNameItemClickListener? = null
//    var qrLongClickListener: OnQrLogoLongClickListener? = null
    interface OnFileNameItemClickListener {
        fun onFileNameClick(
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
                "linearMapList: ${linearMapList}",
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
        filterPrefix = editFragmentRef.get()?.let {
            FilePrefixGetter.get(
                it.fannelInfoMap,
                it.setReplaceVariableMap,
                indexListMap,
                ListSettingsForListIndex.ListSettingKey.PREFIX.key
            )
        } ?: String()
        filterSuffix = editFragmentRef.get()?.let {
            FilePrefixGetter.get(
                it.fannelInfoMap,
                it.setReplaceVariableMap,
                indexListMap,
                ListSettingsForListIndex.ListSettingKey.SUFFIX.key
            )
        }  ?: String()
    }
    fun getLayoutConfigMap(): Map<String, String> {
        return layoutConfigMap
    }


    private object FrameMaker {

        private val tagKey = EditComponent.Template.EditComponentKey.TAG.key
        private val labelKey = EditComponent.Template.EditComponentKey.LABEL.key
        private val imagePathKey = EditComponent.Template.EditComponentKey.IMAGE_PATH.key
        private val heightKey = EditComponent.Template.EditComponentKey.HEIGHT.key
        private val textSizeKey = EditComponent.Template.EditComponentKey.TEXT_SIZE.key
        suspend fun make(
            editFragment: EditFragment?,
            frameKeyMap: List<Pair<String, String>>?,
            srcTitle: String,
            srcCon: String,
//            tag: String?,
//            label: String?,
//            imagePath: String?,
//            height: Int,
//            textSize: Float?,
        ): FrameLayout? {
            val context = editFragment?.context
                ?: return null
            val inflater = LayoutInflater.from(context)
            val buttonLayout = inflater.inflate(
                R.layout.icon_caption_layout,
                null
            ) as FrameLayout
            val tag = withContext(Dispatchers.IO) {
                frameKeyMap?.firstOrNull {
                    it.first == tagKey
                }?.second?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                }
            }
            val height = withContext(Dispatchers.IO) {
                frameKeyMap?.firstOrNull {
                    it.first == heightKey
                }?.second?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                }?.let {
                    try {
                        ScreenSizeCalculator.toDp(context, it.toInt())
                    } catch(e: Exception){
                        null
                    }
                } ?: ScreenSizeCalculator.toDp(context, 50)
            }
            tag?.let {
                buttonLayout.tag = it
            }
            val param = LinearLayoutCompat.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height,
            )
            val marginDp = ScreenSizeCalculator.toDp(
                context,
                context?.resources?.getDimension(R.dimen.toolbar_button_horizon_margin) ?: 0
            )
            param.marginStart = marginDp
            param.marginEnd = marginDp
            param.gravity = Gravity.CENTER
            param.weight = editFragment.buttonWeight
            buttonLayout.layoutParams = param

            buttonLayout.findViewById<AppCompatImageView>(R.id.icon_caption_layout_image)?.let {
                    imageButtonView ->
                val imagePath = withContext(Dispatchers.IO) {
                    frameKeyMap?.firstOrNull {
                        it.first == imagePathKey
                    }?.second?.let {
                        QuoteTool.trimBothEdgeQuote(it)
                    }
                }
                setImageView(
                    imageButtonView,
                    imagePath,
                )
            }
            buttonLayout.findViewById<OutlineTextView>(R.id.icon_caption_layout_caption)?.let {
                    captionTextView ->
                val labelMap = withContext(Dispatchers.IO) {
                    frameKeyMap?.firstOrNull {
                        val key = it.first
                        key == labelKey
                    }?.let {
                        EditComponent.Template.LabelManager.createLabelMap(
                            it.second
                        )
                    }
                }
                val textSize = withContext(Dispatchers.IO) {
                    frameKeyMap?.firstOrNull {
                        it.first == textSizeKey
                    }?.second?.let {
                        QuoteTool.trimBothEdgeQuote(it)
                    }?.let {
                        try {
                            it.toFloat()
                        } catch(e: Exception){
                            null
                        }
                    }
                }
                setCaption(
                    captionTextView,
                    labelMap,
                    textSize,
                    srcTitle,
                    srcCon,
                )
            }
            return buttonLayout
        }

        private suspend fun setImageView(
            imageView: AppCompatImageView,
            imagePath: String?,
        ) {
            if(
                imagePath.isNullOrEmpty()
            ) return
            val imageViewContext = imageView.context

            val requestBuilder: RequestBuilder<Drawable> =
                Glide.with(imageViewContext)
                    .asDrawable()
                    .sizeMultiplier(0.1f)
            val icon = CmdClickIcons.values().firstOrNull {
                it.str == imagePath
            }
            FileSystems.writeFile(
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
                else -> Glide.with(imageViewContext)
                    .load(iconId)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(requestBuilder)
                    .into(imageView)
            }
        }

        private suspend fun setCaption(
            captionTextView: OutlineTextView,
            labelMap: Map<String, String>?,
            textSize: Float?,
            srcTitle: String,
            srcCon: String,
        ) {
            val label = withContext(Dispatchers.IO) {
                EditComponent.Template.LabelManager.makeLabel(
                    labelMap,
                    srcTitle,
                    srcCon
                )
            }
            captionTextView.text = label
            captionTextView.setStrokeColor(R.color.white)
            captionTextView.setFillColor(R.color.fill_gray)
            textSize?.let {
                captionTextView.textSize = textSize
            }
        }
    }
}
