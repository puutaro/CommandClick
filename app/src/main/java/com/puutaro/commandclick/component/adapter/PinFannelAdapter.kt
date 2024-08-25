package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.util.map.FannelSettingMap
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PinFannelAdapter(
    private val context: Context?,
    var pinFannelList: MutableList<String>,
    var fannelSettingInfoMap: Map<String, Map<String, String>>
): RecyclerView.Adapter<PinFannelAdapter.PinFannelViewHolder>() {

    private val switchOn = FannelSettingMap.switchOn

    class PinFannelViewHolder(val view: View): RecyclerView.ViewHolder(view) {


        val fannelPinAdapterConstraintLayout = view.findViewById<ConstraintLayout>(R.id.fannel_pin_adapter_constraint_layout)
        //        val fannelHistoryAdapterRelativeLayout = view.findViewById<RelativeLayout>(R.id.fannel_history_adapter_relative_layout)
        val fannelPinAdapterLogoView = view.findViewById<AppCompatImageView>(R.id.fannel_pin_adapter_logo)
        val fannelPinAdapterNameView = view.findViewById<OutlineTextView>(R.id.fannel_pin_adapter_name)
    }

    override fun getItemCount(): Int = pinFannelList.size

    override fun getItemId(position: Int): Long {
        //return super.getItemId(position)
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)
        return position
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PinFannelViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            R.layout.fannel_pin_adapter_layout,
            parent,
            false
        )
        val fannelManageViewHolder =
            PinFannelViewHolder(itemView)
        itemView.setOnClickListener {
            itemClickListener?.onItemClick(fannelManageViewHolder)
        }
        return fannelManageViewHolder
    }

    override fun onBindViewHolder(
        holder: PinFannelViewHolder,
        position: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val fannelName = pinFannelList[position]
            val fannelNameSettingMap = fannelSettingInfoMap.get(
               fannelName
            )
            withContext(Dispatchers.Main){
                val isNotEditExecute = fannelNameSettingMap?.get(
                    FannelSettingMap.FannelHistorySettingKey.ENABLE_EDIT_EXECUTE.key
                ) != switchOn
                holder.fannelPinAdapterNameView.revOutline(isNotEditExecute)
                holder.fannelPinAdapterNameView.outlineWidthSrc = 1
                holder.fannelPinAdapterNameView.text = fannelName
            }
            val fannelLogoPngPath = withContext(Dispatchers.IO) {
                ScriptPreWordReplacer.replace(
                    UsePath.fannelLogoPngPath,
                    fannelName
                )
            }
            withContext(Dispatchers.Main){
                val logoViewContext = holder.fannelPinAdapterLogoView.context
                val requestBuilder: RequestBuilder<Drawable> =
                    Glide.with(logoViewContext)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                Glide
                    .with(logoViewContext)
                    .load(fannelLogoPngPath)
                    .skipMemoryCache( true )
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail( requestBuilder )
                    .into(holder.fannelPinAdapterLogoView)
            }
        }
    }


    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(holder: PinFannelViewHolder)
    }
}