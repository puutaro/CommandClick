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
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.component.adapter.lib.list_index_adapter.ListIndexEditConfig
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.qr.QrDialogConfig
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.Map.ConfigMapTool
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SettingVariableReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListIndexForEditAdapter(
    private val editFragment: EditFragment,
    private val filterDir: String,
    private val readSharePreffernceMap: Map<String, String>,
    setReplaceVariableMap:  Map<String, String>?,
    var listIndexList: MutableList<String>,
): RecyclerView.Adapter<ListIndexForEditAdapter.ListIndexListViewHolder>()
{

    private val context = editFragment.context
    private val activity = editFragment.activity
    private val maxTakeSize = 150
    private val busyboxExecutor = BusyboxExecutor(
        context,
        UbuntuFiles(context as Context)
    )
//    private val qrPngNameRelativePath = UsePath.qrPngRelativePath

    val qrDialogConfigMap = QrDialogConfig.makeDialogConfigMap(
        readSharePreffernceMap,
    )

    val qrLogoConfigMap = QrDialogConfig.makeLogoConfigMap(
        qrDialogConfigMap
    )
    private val listIndexConfigMap = ConfigMapTool.create(
        UsePath.listIndexForEditConfigPath,
        String(),
        readSharePreffernceMap,
        setReplaceVariableMap,
    )


    private val isInstallFannel = editFragment.isInstallFannelForListIndex

    class ListIndexListViewHolder(
        val activity: FragmentActivity?,
        val view: View
    ): RecyclerView.ViewHolder(view) {

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
            val fileNameOrInstallFannelLine = listIndexList[position]
            holder.fileName =
                fileNameOrInstallFannelLine
                    .split("\n")
                    .firstOrNull() ?: String()
            withContext(Dispatchers.Main) {
                ListIndexEditConfig.setFileNameTextView(
                    isInstallFannel,
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
                    isInstallFannel,
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

            withContext(Dispatchers.Main) {
                QrDialogConfig.setOneSideLength(
                    holder.fileContentsQrLogoView,
                    qrLogoConfigMap
                )
                val qrLogoHandlerArgsMaker = QrDialogConfig.QrLogoHandlerArgsMaker(
                    editFragment,
                    readSharePreffernceMap,
                    qrLogoConfigMap,
                    filterDir,
                    holder.fileName,
                    holder.fileContentsQrLogoView,
                )
                QrDialogConfig.setQrLogoHandler(
                    qrLogoHandlerArgsMaker
                )
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
