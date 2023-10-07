package com.puutaro.commandclick.component.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SettingVariableReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ListIndexForEditAdapter(
    val editFragment: EditFragment,
    val currentAppDirPath: String,
    var listIndexList: MutableList<String>
): RecyclerView.Adapter<ListIndexForEditAdapter.ListIndexListViewHolder>()
{

    val context = editFragment.context
    val activity = editFragment.activity
    private val shortSizeThreshold = 50
    private val middleSizeThreshold = 100
    private val maxTakeSize = 150


    class ListIndexListViewHolder(
        val activity: FragmentActivity?,
        val view: View
    ): RecyclerView.ViewHolder(view) {

        val fannelContentsTextView =
            view.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.fannel_index_list_adapter_contents
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

    var fannelNameClickListener: OnFannelNameItemClickListener? = null
    interface OnFannelNameItemClickListener {
        fun onFannelNameClick(
            itemView: View,
            holder: ListIndexListViewHolder
        )
    }

    var fannelContentsClickListener: OnFannelContentsItemClickListener? = null
    interface OnFannelContentsItemClickListener {
        fun onFannelContentsClick(
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
