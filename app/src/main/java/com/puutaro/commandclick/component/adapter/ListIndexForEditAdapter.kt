package com.puutaro.commandclick.component.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.component.adapter.lib.ImageAdapterTool
import com.puutaro.commandclick.custom_view.MagicTextView
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.LayoutSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.PerformSettingForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrLogoSettingsForQrDialog
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrModeSettingKeysForQrDialog
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ListIndexForEditAdapter(
    private val editFragment: EditFragment,
    var listIndexList: MutableList<String>,
): RecyclerView.Adapter<ListIndexForEditAdapter.ListIndexListViewHolder>()
{
    private val fannelInfoMap = editFragment.fannelInfoMap
    private val context = editFragment.context
    private val activity = editFragment.activity
    private val maxTakeSize = 150
    private val listLimitSize = 300
    private val editExecuteAlways = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
    private val busyboxExecutor = editFragment.busyboxExecutor

    private val listIndexConfigMap = editFragment.listIndexConfigMap
    private val layoutConfigMap = LayoutSettingsForListIndex.getLayoutConfigMap(
        listIndexConfigMap
    )
    private val layoutType = LayoutSettingsForListIndex.decideLayoutType(
        layoutConfigMap
    )
    private var performMap: Map<String, String> = mapOf()

    val checkItemConfigMap = CmdClickMap.createMap(
        listIndexConfigMap?.get(
            ListIndexEditConfig.ListIndexConfigKey.CHECK_ITEM.key
        ),
        '|'
    ).toMap()
    private val qrDialogConfigMap =
        editFragment.qrDialogConfig ?: mapOf()
    private val textImagePngBitMap = ImageAdapterTool.makeFileMarkBitMap(
        context,
        AssetsFileManager.textImagePingPath
    )
    private val qrLogoConfigMap = QrLogoSettingsForQrDialog.makeLogoConfigMap(
        qrDialogConfigMap
    )
    private val iconConfigMap =
        QrLogoSettingsForQrDialog.QrIconSettingKeysForQrDialog.makeIconConfigMap(
            qrLogoConfigMap
        )
    private val itemNameToNameColorConfigMap =
        QrLogoSettingsForQrDialog.QrIconSettingKeysForQrDialog.makeIconNameConfigMap(
            editFragment,
            iconConfigMap,
        )
    private var recentAppDirPath = String()
    private var filterDir = String()
    private var filterPrefix = String()
    private var filterSuffix = String()

    init {
        if(listIndexList.size == 0) {
            setListProperty()
        }
    }

    companion object {
        var indexListMap: Map<String, String> = mapOf()
        var deleteConfigMap: Map<String, String> = mapOf()
        var listIndexTypeKey = TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
    }
    class ListIndexListViewHolder(
        val activity: FragmentActivity?,
        val view: View
    ): RecyclerView.ViewHolder(view) {
        val baseLinearLayout =
            view.findViewById<LinearLayoutCompat>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_horizontal_linearlayout
            )
        val materialCardView =
            view.findViewById<MaterialCardView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_mterial_card_view
            )
        val fileContentsQrLogoLinearLayout =
            view.findViewById<RelativeLayout>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_logo_linearlayout
            )
        val fileContentsQrLogoView =
            view.findViewById<AppCompatImageView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_contents
            )
        val qrLogoImageCaptionTextView =
            view.findViewById<MagicTextView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_image_caption
            )

        val rightLinearlayout =
            view.findViewById<LinearLayoutCompat>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_vertical_linearlayout
            )
        val fileNameTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_file_name
            )
        val fileDescTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_file_desc
            )
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
            activity,
            itemView
        )
    }

    override fun getItemCount(): Int = listIndexList.size

    override fun onBindViewHolder(
        holder: ListIndexListViewHolder,
        listIndexPosition: Int
    ) {
        if(
            listIndexPosition > listLimitSize
        ) return
        initListProperty(listIndexPosition)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                ListIndexEditConfig.setCheckToMaterialCardView(
                    holder.materialCardView,
                    checkItemConfigMap,
                    listIndexList,
                    listIndexPosition,
                )
            }

            val fileNameOrInstallFannelLine =
                listIndexList.getOrNull(listIndexPosition)
                    ?: return@launch
            holder.fileName = FannelListVariable.getFannelName(
                fileNameOrInstallFannelLine
            )
            val fileNameText = withContext(Dispatchers.Main) {
                ListIndexEditConfig.makeFileNameText(
                    listIndexTypeKey,
                    holder.fileNameTextView,
                    holder.fileName,
                    listIndexConfigMap,
                    busyboxExecutor,
                )
            }
            val fileConList = makeFileConList(holder.fileName)
            when(layoutType){
                LayoutSettingsForListIndex.LayoutTypeValueStr.LINEAR -> {
                    withContext(Dispatchers.Main){
                        if(
                            fileNameText.isNullOrEmpty()
                        ) return@withContext
                        holder.fileNameTextView?.text = fileNameText
                    }
                    setDescView(
                        holder.fileDescTextView,
                        fileNameOrInstallFannelLine,
                        fileConList.joinToString("\n"),
                    )
                }
                LayoutSettingsForListIndex.LayoutTypeValueStr.GRID -> {
                    withContext(Dispatchers.Main){
                        holder.rightLinearlayout.isVisible = false
                        if(
                            fileNameText.isNullOrEmpty()
                        ) return@withContext
                        holder.qrLogoImageCaptionTextView.isVisible = true
                        holder.qrLogoImageCaptionTextView.text = fileNameText
                    }
                }
            }
            val fileConBackGroundColorInt = withContext(Dispatchers.IO) {
                val editExecuteValueForInstallFannel = when(
                    listIndexTypeKey
                ){
                    TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL
                    -> FannelListVariable.getEditExecute(
                        fileNameOrInstallFannelLine
                    )
                    else -> String()
                }
                setFileContentsBackColor(
                    fileConList,
                    fileNameOrInstallFannelLine,
                    editExecuteValueForInstallFannel,
                )
            }

            withContext(Dispatchers.Main) {
                context?.let {
                    holder.fileContentsQrLogoLinearLayout.backgroundTintList =
                        it.getColorStateList(fileConBackGroundColorInt)
                }
            }
            withContext(Dispatchers.Main) {
                QrLogoSettingsForQrDialog.OneSideLength.setLayout(
                    editFragment,
                    holder.baseLinearLayout,
                    holder.materialCardView,
                    holder.fileContentsQrLogoLinearLayout,
                    qrLogoConfigMap,
                    layoutType
                )
            }
            withContext(Dispatchers.Main) {
                val disableQrLogo =
                    QrLogoSettingsForQrDialog.Disable.how(qrLogoConfigMap)
                if(
                    disableQrLogo
                ) return@withContext
                qrLogoSetHandler(
                    holder,
                    recentAppDirPath,
                )
            }
            withContext(Dispatchers.Main) {
                val itemView = holder.itemView
                itemView.setOnLongClickListener {
                    itemLongClickListener?.onItemLongClick(
                        itemView,
                        holder,
                        listIndexPosition
                    )
                    true
                }
                itemView.setOnClickListener {
                    fileNameClickListener?.onFileNameClick(
                        itemView,
                        holder,
                        listIndexPosition,
                    )
                }
                val fileContentsQrLogoView = holder.fileContentsQrLogoView
                fileContentsQrLogoView.setOnClickListener {
                    fileQrLogoClickListener?.onFileQrLogoClick(
                        itemView,
                        holder,
                        listIndexPosition
                    )
                }
                fileContentsQrLogoView.setOnLongClickListener {
                    qrLongClickListener?.onQrLongClick(
                        fileContentsQrLogoView,
                        holder,
                        listIndexPosition
                    )
                    true
                }
            }
        }
    }

    var fileNameClickListener: OnFileNameItemClickListener? = null
    var qrLongClickListener: OnQrLogoLongClickListener? = null
    interface OnFileNameItemClickListener {
        fun onFileNameClick(
            itemView: View,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int,
        )
    }

    interface OnQrLogoLongClickListener {
        fun onQrLongClick(
            imageView: AppCompatImageView,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int
        )
    }

    var fileQrLogoClickListener: OnFileQrLogoItemClickListener? = null
    interface OnFileQrLogoItemClickListener {
        fun onFileQrLogoClick(
            itemView: View,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int,
        )
    }

    var itemLongClickListener: OnItemLongClickListener? = null
    interface OnItemLongClickListener {
        fun onItemLongClick(
            itemView: View,
            holder: ListIndexListViewHolder,
            listIndexPosition: Int
        )
    }

    private fun makeFileConList(
        fileName: String,
    ): List<String> {
        if (
            listIndexConfigMap.isNullOrEmpty()
        ) return emptyList()
        if(
            PerformSettingForListIndex.howFastMode(performMap)
            || listIndexTypeKey == TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
        ) return emptyList()
        return ReadText(
            File(filterDir, fileName).absolutePath,
        ).textToList().take(maxTakeSize)
    }

    private suspend fun setDescView(
        fileDescTextView: AppCompatTextView,
        fileNameOrInstallFannelLine: String,
        fileCon: String,
    ){
        val makeFileDescArgsMaker = ListIndexEditConfig.MakeFileDescArgsMaker(
            filterDir,
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
        fileName: String,
        editExecuteValueForInstallFannel: String,
    ): Int {
        if(
            context == null
        ) return com.puutaro.commandclick.R.color.fannel_icon_color
        if(
            editExecuteValueForInstallFannel == editExecuteAlways
        ) return com.puutaro.commandclick.R.color.terminal_color
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(fileName)

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String
        val settingVariableList = CommandClickVariables.extractValListFromHolder(
            fileConList,
            settingSectionStart,
            settingSectionEnd
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

    private suspend fun qrLogoSetHandler(
        holder: ListIndexListViewHolder,
        recentAppDirPath: String,
    ){
        val itemName = holder.fileName.split("\t").lastOrNull() ?: String()
        val okSetIcon = QrLogoSettingsForQrDialog.QrIconSettingKeysForQrDialog.set(
            context,
            filterDir,
            listIndexTypeKey,
            itemName,
            holder.fileContentsQrLogoView,
            holder.fileContentsQrLogoLinearLayout,
            qrLogoConfigMap,
            iconConfigMap,
            itemNameToNameColorConfigMap,
            textImagePngBitMap
        )
        if(okSetIcon) return
        val qrMode = QrModeSettingKeysForQrDialog.getQrMode(qrDialogConfigMap)
        when(qrMode){
            QrModeSettingKeysForQrDialog.QrMode.TSV_EDIT -> {}
            QrModeSettingKeysForQrDialog.QrMode.NORMAL,
            QrModeSettingKeysForQrDialog.QrMode.FANNEL_REPO -> {
                val qrLogoHandlerArgsMaker = withContext(Dispatchers.IO) {
                    QrDialogConfig.QrLogoHandlerArgsMaker(
                        editFragment,
                        recentAppDirPath,
                        qrLogoConfigMap,
                        filterDir,
                        holder.fileName,
                        holder.fileContentsQrLogoView,
                    )
                }
                withContext(Dispatchers.Main) {
                    QrLogoSettingsForQrDialog.setQrLogoHandler(
                        qrLogoHandlerArgsMaker
                    )
                }
            }
        }
    }

    private fun initListProperty(
        listIndexPosition: Int,
    ){
        if(
            listIndexPosition != 0
        ) return
        setListProperty()
    }

    private fun setListProperty(){
        recentAppDirPath = FileSystems.getRecentAppDirPath()
        performMap = PerformSettingForListIndex.makePerformMap(
            listIndexConfigMap
        )
        indexListMap = ListIndexEditConfig.getConfigKeyMap(
            listIndexConfigMap,
            ListIndexEditConfig.ListIndexConfigKey.LIST.key
        )
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
        listIndexTypeKey = ListIndexEditConfig.getListIndexType(
            editFragment
        )
        filterDir = ListSettingsForListIndex.ListIndexListMaker.getFilterDir(
            editFragment,
            indexListMap,
            listIndexTypeKey
        )
        filterPrefix = FilePrefixGetter.get(
            editFragment,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.PREFIX.key
        )  ?: String()
        filterSuffix = FilePrefixGetter.get(
            editFragment,
            indexListMap,
            ListSettingsForListIndex.ListSettingKey.SUFFIX.key
        )  ?: String()
    }
    fun getLayoutConfigMap(): Map<String, String> {
        return layoutConfigMap
    }
}
