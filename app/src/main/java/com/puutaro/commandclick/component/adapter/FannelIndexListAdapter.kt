package com.puutaro.commandclick.component.adapter

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
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.proccess.qr.QrLogo
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.SettingVariableReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class FannelIndexListAdapter(
    val cmdIndexFragment: CommandIndexFragment,
    val currentAppDirPath: String,
    var fannelIndexList: MutableList<String>
): RecyclerView.Adapter<FannelIndexListAdapter.FannelIndexListViewHolder>()
{
    val context = cmdIndexFragment.context
    val activity = cmdIndexFragment.activity
    private val maxTakeSize = 150
    private val qrPngNameRelativePath = UsePath.qrPngRelativePath
    private val qrLogo = QrLogo(cmdIndexFragment)

    class FannelIndexListViewHolder(
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
    ): FannelIndexListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            com.puutaro.commandclick.R.layout.fannel_index_list_adapter_layout,
            parent,
            false
        )
        val fannelIndexListViewHolder = FannelIndexListViewHolder(
            activity,
            itemView
        )
        return fannelIndexListViewHolder
    }

    override fun getItemCount(): Int = fannelIndexList.size

    override fun onBindViewHolder(
        holder: FannelIndexListViewHolder,
        position: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val fannelName = fannelIndexList[position]
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
            val qrPngPath = "${currentAppDirPath}/${fannelDirName}/${qrPngNameRelativePath}"
            val qrPngPathObj = File(qrPngPath)

            withContext(Dispatchers.Main) {
                if(qrPngPathObj.isFile){
                    holder.fannelContentsQrLogoView.load(qrPngPath)
                    return@withContext
                }
                qrLogo.createAndSaveWithGitCloneOrFileCon(
                    currentAppDirPath,
                    fannelName,
                    false,
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
                    val fannelContentsQrLogo = holder.fannelContentsQrLogoView
                    fannelContentsQrLogo.setOnClickListener {
                        fannelQrLogoClickListener?.onFannelContentsClick(
                            itemView,
                            holder
                        )
                    }
                    fannelContentsQrLogo.setOnLongClickListener {
                        qrLongClickListener?.onQrLongClick(
                            fannelContentsQrLogo,
                            holder,
                            position
                        )
                        true
                    }
                }
            }
        }
    }

    var fannelNameClickListener: OnFannelNameItemClickListener? = null
    interface OnFannelNameItemClickListener {
        fun onFannelNameClick(
            itemView: View,
            holder: FannelIndexListViewHolder
        )
    }

    var fannelQrLogoClickListener: OnFannelQrLogoItemClickListener? = null
    interface OnFannelQrLogoItemClickListener {
        fun onFannelContentsClick(
            itemView: View,
            holder: FannelIndexListViewHolder
        )
    }

    var itemLongClickListener: OnItemLongClickListener? = null
    var qrLongClickListener: OnQrLongClickListener? = null
    interface OnItemLongClickListener {
        fun onItemLongClick(
            itemView: View,
            holder: FannelIndexListViewHolder,
            position: Int
        )
    }

    interface OnQrLongClickListener {
        fun onQrLongClick(
            imageView: AppCompatImageView,
            holder: FannelIndexListViewHolder,
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
}
