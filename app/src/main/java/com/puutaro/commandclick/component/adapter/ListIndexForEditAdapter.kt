package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variables.FannelListVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.ReplaceVariableMapReflecter
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SettingVariableReader
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ListIndexForEditAdapter(
    private val editFragment: EditFragment,
    var listIndexList: MutableList<String>,
): RecyclerView.Adapter<ListIndexForEditAdapter.ListIndexListViewHolder>()
{
    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    private val context = editFragment.context
    private val activity = editFragment.activity
    private val maxTakeSize = 150
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

    val checkItemConfigMap = CmdClickMap.createMap(
        listIndexConfigMap?.get(
            ListIndexEditConfig.ListIndexConfigKey.CHECK_ITEM.key
        ),
        "|"
    ).toMap()

//    private val isInstallFannel = editFragment.isInstallFannelForListIndex

    companion object {
        private const val throughMark = "-"
        private const val noExtend = "NoExtend"
        private const val subMenuSeparator = "&"
        const val blankListMark = "Let's press sync button at right bellow"
        var filterDir = String()
        var filterPrefix = String()
        var filterSuffix = String()
        var filterShellCon = String()
        var listIndexTypeKey = ListIndexEditConfig.ListIndexTypeKey.NORMAL

        fun clickUpdateFileList(
            editFragment: EditFragment,
            selectedItem: String,
        ){
            FileSystems.updateLastModified(
                filterDir,
                selectedItem
            )
            listIndexListUpdateFileList(
                editFragment,
                makeFileListHandler(
                    editFragment.busyboxExecutor,
                    listIndexTypeKey
                )
            )
        }

        fun getIndexListMap(
            replacedSetVariableMap: Map<String, String>?
        ): Map<String, String> {
            val listDirKeyName = ListIndexEditConfig.ListIndexConfigKey.LIST.key

            return replacedSetVariableMap?.get(
                listDirKeyName
            ).let{
                CmdClickMap.createMap(
                    it,
                    "|"
                )
            }.toMap()
        }

        fun getFilterPrefix(
            indexListMap: Map<String, String>?,
        ): String {
            return indexListMap?.get(ListIndexEditConfig.ListIndexListSettingKey.prefix.name)?.let {
                QuoteTool.trimBothEdgeQuote(it)
            } ?: String()
        }

        fun getFilterSuffix(
            indexListMap: Map<String, String>?,
        ): String {
            return indexListMap?.get(ListIndexEditConfig.ListIndexListSettingKey.suffix.name)?.let {
                QuoteTool.trimBothEdgeQuote(it)
            } ?: String()
        }

        fun getFilterShellCon(
            indexListMap: Map<String, String>?,
            editParameters: EditParameters
        ): String {
            return indexListMap?.get(
                ListIndexEditConfig.ListIndexListSettingKey.filterShellPath.name
            )?.let {
                QuoteTool.trimBothEdgeQuote(it)
            }.let {
                if(
                    it.isNullOrEmpty()
                ) return@let String()
                val filterShellPathObj = File(it)
                val shellParentDirPath = filterShellPathObj.parent
                    ?: return@let String()
                val shellConBeforeReplace = ReadText(
                    shellParentDirPath,
                    filterShellPathObj.name
                ).readText()
                ReplaceVariableMapReflecter.reflect(
                    shellConBeforeReplace,
                    editParameters
                )?: String()
            }
        }
        fun makeFileListHandler(
            busyboxExecutor: BusyboxExecutor?,
            listIndexTypeKey: ListIndexEditConfig.ListIndexTypeKey
        ): MutableList<String> {
            return when(listIndexTypeKey) {
                ListIndexEditConfig.ListIndexTypeKey.INSTALL_FANNEL -> makeFannelListForListView().toMutableList()
                else -> makeFileList(busyboxExecutor)
            }
        }

        private fun makeFileList(
            busyboxExecutor: BusyboxExecutor?
        ): MutableList<String> {
            val itemNameMark = "\${ITEM_NAME}"
            val fileListSource = FileSystems.sortedFiles(
                filterDir,
            ).filter {
                it.startsWith(filterPrefix)
                        && judgeBySuffixForIndex(
                    it,
                    filterSuffix
                )
                        && File("${filterDir}/$it").isFile
            }.map {
                if(
                    filterShellCon.isEmpty()
                    || busyboxExecutor == null
                ) return@map it
                busyboxExecutor.getCmdOutput(filterShellCon).replace(
                    itemNameMark,
                    it,
                )
            }.filter {
                it.isNotEmpty()
            }
            if(
                fileListSource.isEmpty()
            ) return mutableListOf(throughMark)
            return fileListSource.toMutableList()
        }

        private fun makeFannelListForListView(): List<String> {
            val fannelListSource = ReadText(
                UsePath.cmdclickFannelListDirPath,
                UsePath.fannelListMemoryName,
            ).readText()
                .replace(Regex("\\*\\*([a-zA-Z0-9]*)\\*\\*"), "*$1")
                .split(FannelListVariable.cmdclickFannelListSeparator)
            return if (
                fannelListSource.isNotEmpty()
                && !fannelListSource
                    .firstOrNull()
                    ?.trim()
                    .isNullOrEmpty()
            ) {
                fannelListSource
            } else mutableListOf(blankListMark)
        }

        fun listIndexListUpdateFileList(
            editFragment: EditFragment,
            updateList: List<String>,
        ){
            val editListRecyclerView = editFragment.binding.editListRecyclerView
            if(
                !editListRecyclerView.isVisible
            ) return
            val listIndexForEditAdapter =
                editListRecyclerView.adapter as? ListIndexForEditAdapter
                    ?: return
            listIndexForEditAdapter.listIndexList.clear()
            listIndexForEditAdapter.listIndexList.addAll(updateList)
            listIndexForEditAdapter.notifyDataSetChanged()
            CoroutineScope(Dispatchers.Main).launch {
                delay(200)
                editListRecyclerView.layoutManager?.scrollToPosition(
                    listIndexForEditAdapter.itemCount - 1
                )
            }
        }

        private fun judgeBySuffixForIndex(
            targetStr: String,
            filterSuffix: String,
        ): Boolean {
            if(filterSuffix != noExtend) {
                return filterSuffix.split(subMenuSeparator).any {
                    targetStr.endsWith(it)
                }
            }
            return !Regex("\\..*$").containsMatchIn(targetStr)
        }

        fun getFilterListDir(
            indexListMap: Map<String, String>?,
            listIndexType : ListIndexEditConfig.ListIndexTypeKey,
            currentAppDirPath: String,
            currentScriptName: String,
        ): String {
            if(
                listIndexType == ListIndexEditConfig.ListIndexTypeKey.INSTALL_FANNEL
            ) return UsePath.cmdclickFannelItselfDirPath
            return indexListMap?.get(ListIndexEditConfig.ListIndexListSettingKey.listDir.name)?.let{
                ScriptPreWordReplacer.replace(
                    it,
                    currentAppDirPath,
                    currentScriptName
                )
            }?.let {
                QuoteTool.trimBothEdgeQuote(it)
            } ?: String()
        }
    }
    class ListIndexListViewHolder(
        val activity: FragmentActivity?,
        val view: View
    ): RecyclerView.ViewHolder(view) {
        val materialCardView =
            view.findViewById<MaterialCardView>(
                com.puutaro.commandclick.R.id.list_index_edit_adapter_mterial_card_view
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
        position: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                ListIndexEditConfig.setCheckToMaterialCardView(
                    holder.materialCardView,
                    checkItemConfigMap,
                    listIndexList,
                    position,
                )
            }
            val fileNameOrInstallFannelLine = listIndexList[position]
            holder.fileName =
                fileNameOrInstallFannelLine
                    .split("\n")
                    .firstOrNull() ?: String()
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
                ReadText(
                    filterDir,
                    holder.fileName
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
            val recentAppDirPath = withContext(Dispatchers.IO){
                FileSystems.getRecentAppDirPath()
            }

            withContext(Dispatchers.Main) {
                val disableQrLogo =
                    QrDialogConfig.howDisableQrLogo(qrLogoConfigMap)
                if(disableQrLogo) return@withContext
                withContext(Dispatchers.Main) {
                    QrDialogConfig.setOneSideLength(
                        holder.fileContentsQrLogoView,
                        qrLogoConfigMap
                    )
                }
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
            val fileConBackGroundColorInt = withContext(Dispatchers.IO) {
                setFileContentsBackColor(
                    fileConList,
                    fileNameOrInstallFannelLine,
                )
            }
            withContext(Dispatchers.Main){
                holder.fileContentsQrLogoView.setBackgroundColor(
                    context?.getColor(fileConBackGroundColorInt) as Int
                )
                withContext(Dispatchers.Main) {
                    val itemView = holder.itemView
                    itemView.setOnLongClickListener {
                        itemLongClickListener?.onItemLongClick(
                            itemView,
                            holder,
                            position
                        )
                        true
                    }
                    itemView.setOnClickListener {
                        fileNameClickListener?.onFileNameClick(
                            itemView,
                            holder
                        )
                    }
                    val fileContentsQrLogoView = holder.fileContentsQrLogoView
                    fileContentsQrLogoView.setOnClickListener {
                        fileQrLogoClickListener?.onFileQrLogoClick(
                            itemView,
                            holder
                        )
                    }
                    fileContentsQrLogoView.setOnLongClickListener {
                        qrLongClickListener?.onQrLongClick(
                            fileContentsQrLogoView,
                            holder,
                            position
                        )
                        true
                    }
                }
            }
        }
    }

    var fileNameClickListener: OnFileNameItemClickListener? = null
    var qrLongClickListener: OnQrLogoLongClickListener? = null
    interface OnFileNameItemClickListener {
        fun onFileNameClick(
            itemView: View,
            holder: ListIndexListViewHolder
        )
    }

    interface OnQrLogoLongClickListener {
        fun onQrLongClick(
            imageView: AppCompatImageView,
            holder: ListIndexListViewHolder,
            position: Int
        )
    }

    var fileQrLogoClickListener: OnFileQrLogoItemClickListener? = null
    interface OnFileQrLogoItemClickListener {
        fun onFileQrLogoClick(
            itemView: View,
            holder: ListIndexListViewHolder
        )
    }

    var itemLongClickListener: OnItemLongClickListener? = null
    interface OnItemLongClickListener {
        fun onItemLongClick(
            itemView: View,
            holder: ListIndexListViewHolder,
            position: Int
        )
    }

    private fun setFileContentsBackColor(
        fileConList: List<String>,
        fileName: String,
    ): Int {
        if(
            context == null
        ) return com.puutaro.commandclick.R.color.fannel_icon_color
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
}
