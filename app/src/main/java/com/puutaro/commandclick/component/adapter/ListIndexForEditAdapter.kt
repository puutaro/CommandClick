package com.puutaro.commandclick.component.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.qr.QrLogo
import com.puutaro.commandclick.proccess.qr.QrMapper
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SettingVariableReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ListIndexForEditAdapter(
    val editFragment: EditFragment,
    val currentAppDirPath: String,
    var listIndexList: MutableList<String>,
    val isConQr: Boolean,
): RecyclerView.Adapter<ListIndexForEditAdapter.ListIndexListViewHolder>()
{

    val context = editFragment.context
    val activity = editFragment.activity
    private val maxTakeSize = 150
    private val qrPngNameRelativePath = UsePath.qrPngRelativePath


    class ListIndexListViewHolder(
        val activity: FragmentActivity?,
        val view: View
    ): RecyclerView.ViewHolder(view) {

        val fannelContentsQrLogoView =
            view.findViewById<AppCompatImageView>(
                com.puutaro.commandclick.R.id.fannel_index_list_qr_log
            )
        val fannelNameTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.fannel_index_list_adapter_name
            )
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
            com.puutaro.commandclick.R.layout.fannel_index_list_adapter_layout,
            parent,
            false
        )
        val listIndexListViewHolder = ListIndexListViewHolder(
            activity,
            itemView
        )
        return listIndexListViewHolder
    }

    override fun getItemCount(): Int = listIndexList.size


    override fun onBindViewHolder(
        holder: ListIndexListViewHolder,
        position: Int
    ) {
        val qrLogo =  QrLogo(editFragment)
        CoroutineScope(Dispatchers.IO).launch {
            val fannelName = listIndexList[position]
            withContext(Dispatchers.Main) {
                holder.fannelNameTextView.text = fannelName
            }
            val fannelConList = withContext(Dispatchers.IO) {
                ReadText(
                    currentAppDirPath,
                    fannelName
                ).textToList().take(maxTakeSize)
            }
            val fannelDirName = CcPathTool.makeFannelDirName(fannelName)
            val fannelDirPath = "${currentAppDirPath}/${fannelDirName}"
            val qrPngPath = "${fannelDirPath}/${qrPngNameRelativePath}"
            val qrPngPathObj = File(qrPngPath)

            withContext(Dispatchers.Main) {
                if(
                    fannelName.isEmpty()
                    || fannelName == "-"
                ) return@withContext
                if(qrPngPathObj.isFile){
                    holder.fannelContentsQrLogoView.load(qrPngPath)
                    return@withContext
                }
                qrLogoCreateHandler(
                    qrLogo,
                    fannelName,
                    fannelDirPath,
                )?.let {
                    holder.fannelContentsQrLogoView.setImageDrawable(it)
                }
            }
            val fannelConBackGroundColorInt = withContext(Dispatchers.IO) {
                setFannelContentsBackColor(
                    fannelConList,
                    fannelName,
                )
            }
            withContext(Dispatchers.Main){
                holder.fannelContentsQrLogoView.setBackgroundColor(
                    context?.getColor(fannelConBackGroundColorInt) as Int
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
                        fannelNameClickListener?.onFannelNameClick(
                            itemView,
                            holder
                        )
                    }
                    val fannelContentsQrLogoView = holder.fannelContentsQrLogoView
                    fannelContentsQrLogoView.setOnClickListener {
                        fannelQrLogoClickListener?.onFannelQrLogoClick(
                            itemView,
                            holder
                        )
                    }
                    fannelContentsQrLogoView.setOnLongClickListener {
                        qrLongClickListener?.onQrLongClick(
                            fannelContentsQrLogoView,
                            holder,
                            isConQr,
                            position
                        )
                        true
                    }
                }
            }
        }
    }

    var fannelNameClickListener: OnFannelNameItemClickListener? = null
    var qrLongClickListener: OnQrLogoLongClickListener? = null
    interface OnFannelNameItemClickListener {
        fun onFannelNameClick(
            itemView: View,
            holder: ListIndexListViewHolder
        )
    }

    interface OnQrLogoLongClickListener {
        fun onQrLongClick(
            imageView: AppCompatImageView,
            holder: ListIndexListViewHolder,
            isConQr: Boolean,
            position: Int
        )
    }

    var fannelQrLogoClickListener: OnFannelQrLogoItemClickListener? = null
    interface OnFannelQrLogoItemClickListener {
        fun onFannelQrLogoClick(
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

    private fun setFannelContentsBackColor(
        fannelConList: List<String>,
        fannelName: String,
    ): Int {
        if(
            context == null
        ) return com.puutaro.commandclick.R.color.fannel_icon_color
        val languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(fannelName)

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        val settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        val settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            fannelConList,
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

    private fun qrLogoCreateHandler(
        qrLogo: QrLogo,
        fannelName: String,
        fannelDirPath: String,
    ): Drawable? {
        return when(isConQr){
            true -> {
                val qrDesignFilePath = "${fannelDirPath}/${UsePath.qrDesignRelativePath}"
                val qrDesignMap = qrLogo.readQrDesignMapWithCreate(
                    qrDesignFilePath,
                    currentAppDirPath,
                    fannelName,
                )
                qrLogo.createAndSaveFromDesignMap(
                    qrDesignMap,
                    currentAppDirPath,
                    fannelName,
                )
            }
            else -> {
                val fannelRawName = CcPathTool.makeFannelRawName(fannelName)
                qrLogo.createAndSaveRnd(
                    QrMapper.onGitTemplate.format(fannelRawName),
                    currentAppDirPath,
                    fannelName,
                )
            }
        }
    }

}
