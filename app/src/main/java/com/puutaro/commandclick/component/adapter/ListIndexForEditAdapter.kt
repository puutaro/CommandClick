package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.PerformSettingForListIndex
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.proccess.qr.qr_dialog_config.config_settings.QrModeSettingKeysForQrDialog
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.SettingVariableReader
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
    private val readSharePreffernceMap = editFragment.readSharePreferenceMap
    private val context = editFragment.context
    private val activity = editFragment.activity
    private val maxTakeSize = 150
    private val listLimitSize = 300
    private val editExecuteAlways = SettingVariableSelects.EditExecuteSelects.ALWAYS.name
    private val busyboxExecutor = BusyboxExecutor(
        context,
        UbuntuFiles(context as Context)
    )

    val qrDialogConfigMap =
        editFragment.qrDialogConfig ?: mapOf()

    val qrLogoConfigMap = QrDialogConfig.makeLogoConfigMap(
        qrDialogConfigMap
    )
    private val listIndexConfigMap = editFragment.listIndexConfigMap
    private var performMap: Map<String, String> = mapOf()

    val checkItemConfigMap = CmdClickMap.createMap(
        listIndexConfigMap?.get(
            ListIndexEditConfig.ListIndexConfigKey.CHECK_ITEM.key
        ),
        '|'
    ).toMap()
    private var recentAppDirPath = String()
    private var filterDir = String()
    private var filterPrefix = String()
    private var filterSuffix = String()


    companion object {
        var indexListMap: Map<String, String> = mapOf()
        var deleteConfigMap: Map<String, String> = mapOf()
        var listIndexTypeKey = TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
    }
    class ListIndexListViewHolder(
        val activity: FragmentActivity?,
        val view: View
    ): RecyclerView.ViewHolder(view) {
        val materialCardView =
            view.findViewById<MaterialCardView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_mterial_card_view
            )
        val fileContentsQrLogoLinearLayout =
            view.findViewById<LinearLayoutCompat>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_logo_linearlayout
            )
        val fileContentsQrLogoView =
            view.findViewById<AppCompatImageView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_contents
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
            withContext(Dispatchers.Main) {
                ListIndexEditConfig.setFileNameTextView(
                    listIndexTypeKey,
                    holder.fileNameTextView,
                    holder.fileName,
                    listIndexConfigMap,
                    busyboxExecutor,
                )
            }

            val fileConList = withContext(Dispatchers.IO) {
                if (
                    listIndexConfigMap.isNullOrEmpty()
                ) return@withContext emptyList()
                if(
                    PerformSettingForListIndex.howFastMode(performMap)
                    || listIndexTypeKey == TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT
                ) return@withContext emptyList()
                ReadText(
                    File(filterDir, holder.fileName).absolutePath,
                ).textToList().take(maxTakeSize)
            }
            val descCon = withContext(Dispatchers.IO){
                val makeFileDescArgsMaker = ListIndexEditConfig.MakeFileDescArgsMaker(
                    filterDir,
                    fileNameOrInstallFannelLine,
                    fileConList.joinToString("\n"),
                    listIndexConfigMap,
                    busyboxExecutor,
                )
                ListIndexEditConfig.makeFileDesc(
                    makeFileDescArgsMaker,
                )
            }
            withContext(Dispatchers.Main) {
                when(descCon.isNullOrEmpty()){
                    true -> holder.fileDescTextView.isVisible = false
                    else -> holder.fileDescTextView.text = descCon
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
                QrDialogConfig.setOneSideLength(
                    holder.fileContentsQrLogoLinearLayout,
                    qrLogoConfigMap
                )
            }
            withContext(Dispatchers.Main) {
                val disableQrLogo =
                    QrDialogConfig.howDisableQrLogo(qrLogoConfigMap)
                if(disableQrLogo) return@withContext
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
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
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
        val qrMode = QrModeSettingKeysForQrDialog.getQrMode(qrDialogConfigMap)
        when(qrMode){
            QrModeSettingKeysForQrDialog.QrMode.TSV_EDIT -> {}
            QrModeSettingKeysForQrDialog.QrMode.NORMAL,
            QrModeSettingKeysForQrDialog.QrMode.FANNEL_REPO -> {
                val qrLogoHandlerArgsMaker = withContext(Dispatchers.IO) {
                    QrDialogConfig.QrLogoHandlerArgsMaker(
                        editFragment,
                        recentAppDirPath,
                        readSharePreffernceMap,
                        qrLogoConfigMap,
                        filterDir,
                        holder.fileName,
                        holder.fileContentsQrLogoView,
                    )
                }
                withContext(Dispatchers.Main) {
                    QrDialogConfig.setQrLogoHandler(
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
}
