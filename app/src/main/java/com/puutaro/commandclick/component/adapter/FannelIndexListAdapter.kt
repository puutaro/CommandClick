package com.puutaro.commandclick.component.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JsOrShellFromSuffix
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.SettingVariableReader


class FannelIndexListAdapter(
    val cmdIndexFragment: CommandIndexFragment,
    val currentAppDirPath: String,
    var fannelIndexList: MutableList<String>
): RecyclerView.Adapter<FannelIndexListAdapter.FannelIndexListViewHolder>()
{
    val context = cmdIndexFragment.context
    val activity = cmdIndexFragment.activity
    private val shortSizeThreshold = 50
    private val middleSizeThreshold = 100


    class FannelIndexListViewHolder(
        val activity: FragmentActivity?,
        val view: View
        ): RecyclerView.ViewHolder(view),
        OnCreateContextMenuListener {

        val fannelContentsTextView =
            view.findViewById<TextView>(
                com.puutaro.commandclick.R.id.fannel_index_list_adapter_contents
            )
        val fannelNameTextView =
            view.findViewById<TextView>(
                com.puutaro.commandclick.R.id.fannel_index_list_adapter_name
            )
        init {
            view.setOnCreateContextMenuListener(this)
        }
        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            val inflater = activity?.menuInflater
            inflater?.inflate(com.puutaro.commandclick.R.menu.cmd_index_list_menu, menu)
        }
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
        return fannelIndexListViewHolder
    }

    override fun getItemCount(): Int = fannelIndexList.size


    override fun onBindViewHolder(
        holder: FannelIndexListViewHolder,
        position: Int
    ) {
        val fannelName = fannelIndexList[position]
        val fannelConList = ReadText(
            currentAppDirPath,
            fannelName
        ).textToList()
        holder.fannelContentsTextView.text =
            fannelConList.joinToString("\n")
        val fannelConListSize = fannelConList.size
        if(
            fannelConListSize < shortSizeThreshold
        ) holder.fannelContentsTextView.textSize = 2f
        else if(
            fannelConListSize < middleSizeThreshold
        ) holder.fannelContentsTextView.textSize = 1f
        else holder.fannelContentsTextView.textSize = 0.5f
        holder.fannelNameTextView.text = fannelName
        setFannelContentsBackColor(
            holder.fannelContentsTextView,
            fannelConList,
            fannelName,
        )
        val itemView = holder.itemView
        this@FannelIndexListAdapter.itemLongClickListener = object: OnItemLongClickListener {
            override fun onItemLongClick(
                itemView: View,
                holder: FannelIndexListViewHolder,
                position: Int
            ) {
                cmdIndexFragment.recyclerViewIndex = position
            }
        }
        itemView.setOnLongClickListener {
            itemLongClickListener?.onItemLongClick(
                itemView,
                holder,
                position
            )
            false
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

    var fannelNameClickListener: OnFannelNameItemClickListener? = null
    interface OnFannelNameItemClickListener {
        fun onFannelNameClick(
            itemView: View,
            holder: FannelIndexListViewHolder
        )
    }

    var fannelContentsClickListener: OnFannelContentsItemClickListener? = null
    interface OnFannelContentsItemClickListener {
        fun onFannelContentsClick(
            itemView: View,
            holder: FannelIndexListViewHolder
        )
    }

    var itemLongClickListener: OnItemLongClickListener? = null
    interface OnItemLongClickListener {
        fun onItemLongClick(
            itemView: View,
            holder: FannelIndexListViewHolder,
            position: Int
        )
    }

//    var fannelNameLongClickListener: OnFannelNameLongClickListener? = null
//    interface OnFannelNameLongClickListener {
//        fun onFannelNameLongClick(
//            itemView: View,
//            holder: FannelIndexListViewHolder
//        )
//    }

    private fun setFannelContentsBackColor(
        fannelContentsTextView: TextView,
        fannelConList: List<String>,
        fannelName: String,
    ){
        if(
            context == null
        ) return
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
        ) {
            fannelContentsTextView.setBackgroundColor(
                context.getColor(com.puutaro.commandclick.R.color.terminal_color)
            )
            return
        }
        fannelContentsTextView.setBackgroundColor(
            context.getColor(com.puutaro.commandclick.R.color.fannel_icon_color)
        )
    }

}
