package com.puutaro.commandclick.component.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.util.AppHistoryManager


class FannelHistoryAdapter(
    var historyList: MutableList<String>
    ): RecyclerView.Adapter<FannelHistoryAdapter.HistoryViewHolder>(){

    class HistoryViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val appDirNameTextView = view.findViewById<TextView>(R.id.fannel_history_app_dir)
        val fannelNameTextView = view.findViewById<TextView>(R.id.fannel_history_name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            R.layout.fannel_history,
            parent,
            false
        )
        val historyViewHolder = HistoryViewHolder(itemView)
        itemView.setOnClickListener {
            itemClickListener?.onItemClick(historyViewHolder)
        }
        itemView.setOnLongClickListener {
            itemLongClickListener?.onItemLongClick(itemView, historyViewHolder)
            true
        }
        return historyViewHolder
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(
        holder: HistoryViewHolder,
        position: Int
    ) {
        val historyLine = historyList[position]
        holder.appDirNameTextView.text =
            AppHistoryManager.getAppDirNameFromAppHistoryFileName(
                historyLine
            )
        holder.fannelNameTextView.text = AppHistoryManager.getScriptFileNameFromAppHistoryFileName(
            historyLine
        )
    }

    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(holder: FannelHistoryAdapter.HistoryViewHolder)
    }

    var itemLongClickListener: OnItemLongClickListener? = null
    interface OnItemLongClickListener {
        fun onItemLongClick(itemView: View, holder: FannelHistoryAdapter.HistoryViewHolder)
    }
}
