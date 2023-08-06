package com.puutaro.commandclick.component.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JsOrShellFromSuffix
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SettingVariableReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InstallFannelListAdapter(
    val cmdIndexFragment: CommandIndexFragment,
    var fannelInstallerList: MutableList<String>
): RecyclerView.Adapter<InstallFannelListAdapter.FannelInstallerListViewHolder>()
{
    val context = cmdIndexFragment.context
    val activity = cmdIndexFragment.activity
    private val shortSizeThreshold = 50
    private val middleSizeThreshold = 100
    private val maxTakeSize = 200


    class FannelInstallerListViewHolder(
        val activity: FragmentActivity?,
        val view: View
    ): RecyclerView.ViewHolder(view) {

        val fannelContentsTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.install_fannel_adapter_contents
            )
        val fannelNameTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.install_fannel_adapter_fannel_name
            )
        val fannelSummaryTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.fannel_index_list_adapter_fannel_summary
            )
    }

    override fun getItemId(position: Int): Long {
        setHasStableIds(true)
        return super.getItemId(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FannelInstallerListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            com.puutaro.commandclick.R.layout.install_fannel_adapter_layout,
            parent,
            false
        )
        val fannelInstallerListViewHolder = FannelInstallerListViewHolder(
            activity,
            itemView
        )
//        fannelIndexListViewHolder.fannelNameTextView.setOnClickListener {
//            fannelNameClickListener?.onFannelNameClick(
//                itemView,
//                fannelIndexListViewHolder
//            )
//        }
//        fannelIndexListViewHolder.fannelNameTextView.setOnLongClickListener {
//            fannelNameLongClickListener?.onFannelNameLongClick(
//                itemView,
//                fannelIndexListViewHolder
//            )
//            true
//        }
//        fannelIndexListViewHolder.fannelContentsTextView.setOnClickListener {
//            fannelContentsClickListener?.onFannelContentsClick(
//                itemView,
//                fannelIndexListViewHolder
//            )
//        }
        return fannelInstallerListViewHolder
    }

    override fun getItemCount(): Int = fannelInstallerList.size


    override fun onBindViewHolder(
        holder: FannelInstallerListViewHolder,
        position: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val fannelInstallerLineList =
                fannelInstallerList.getOrNull(position)?.split("\n")
            val fannelName = fannelInstallerLineList?.firstOrNull()
                ?: String()
            withContext(Dispatchers.Main) {
                holder.fannelNameTextView.text = fannelName
            }
            val fannelSumamry = fannelInstallerLineList?.getOrNull(1)
                ?: String()
            withContext(Dispatchers.Main) {
                holder.fannelSummaryTextView.text =
                    fannelSumamry
                        .trim()
                        .removePrefix("-")
                        .trim()
            }
            val fannelConList = withContext(Dispatchers.IO) {
                ReadText(
                    UsePath.cmdclickFannelDirPath,
                    fannelName
                ).textToList().take(maxTakeSize)
            }
            withContext(Dispatchers.Main) {
                holder.fannelContentsTextView.text =
                    fannelConList.joinToString("\n")
            }
            val textSize = withContext(Dispatchers.IO) {
                culCTextSize(
                    fannelConList
                )
            }
            withContext(Dispatchers.Main) {
                holder.fannelContentsTextView.textSize = textSize
            }
            val fannelConBackGroundColorInt = withContext(Dispatchers.IO) {
                setFannelContentsBackColor(
                    fannelConList,
                    fannelName,
                )
            }
            withContext(Dispatchers.Main){
                holder.fannelContentsTextView.setBackgroundColor(
                    context?.getColor(fannelConBackGroundColorInt) as Int
                )
                withContext(Dispatchers.Main) {
                    val itemView = holder.itemView
//                    this@FannelInstalListAdapter.itemLongClickListener =
//                        object : OnItemLongClickListener {
//                            override fun onItemLongClick(
//                                itemView: View,
//                                holder: FannelInstallerListViewHolder,
//                                position: Int
//                            ) {
//                                cmdIndexFragment.recyclerViewIndex = position
//                            }
//                        }
//                    itemView.setOnLongClickListener {
//                        itemLongClickListener?.onItemLongClick(
//                            itemView,
//                            holder,
//                            position
//                        )
//                        false
//                    }
                    itemView.setOnClickListener {
                        fannelItemClickListener?.onFannelItemClick(
                            itemView,
                            holder
                        )
                    }
                    holder.fannelContentsTextView.setOnClickListener {
                        fannelContentsClickListener?.onFannelContentsClick(
                            itemView,
                            holder
                        )
                    }
                }
            }
        }
    }

    var fannelItemClickListener: OnFannelItemClickListener? = null
    interface OnFannelItemClickListener {
        fun onFannelItemClick(
            itemView: View,
            holder: FannelInstallerListViewHolder
        )
    }


    var fannelContentsClickListener: OnFannelContentsItemClickListener? = null
    interface OnFannelContentsItemClickListener {
        fun onFannelContentsClick(
            itemView: View,
            holder: FannelInstallerListViewHolder
        )
    }

//    var itemLongClickListener: OnItemLongClickListener? = null
//    interface OnItemLongClickListener {
//        fun onItemLongClick(
//            itemView: View,
//            holder: FannelInstallerListViewHolder,
//            position: Int
//        )
//    }

//    var fannelNameLongClickListener: OnFannelNameLongClickListener? = null
//    interface OnFannelNameLongClickListener {
//        fun onFannelNameLongClick(
//            itemView: View,
//            holder: FannelIndexListViewHolder
//        )
//    }

    private fun culCTextSize(
        fannelConList: List<String>
    ): Float {
        val fannelConListSize = fannelConList.size
        if (
            fannelConListSize < shortSizeThreshold
        ) return 2f
        else if (
            fannelConListSize < middleSizeThreshold
        ) return 1f
        return 0.5f
    }

    private fun setFannelContentsBackColor(
        fannelConList: List<String>,
        fannelName: String,
    ): Int {
        if(
            context == null
        ) return com.puutaro.commandclick.R.color.fannel_icon_color
        val languageType =
            JsOrShellFromSuffix.judge(fannelName)

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
