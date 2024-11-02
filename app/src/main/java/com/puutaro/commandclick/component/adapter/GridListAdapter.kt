package com.puutaro.commandclick.component.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.puutaro.commandclick.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GridListAdapter(
    val imagePathList: List<String>,
): RecyclerView.Adapter<GridListAdapter.GridListViewHolder>() {

    class GridListViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val itemImageView = view.findViewById<AppCompatImageView>(R.id.grid_list_v2_adapter_image)
    }

    override fun getItemCount(): Int = imagePathList.size

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
    ): GridListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            R.layout.grid_list_v2_adapter_layout,
            parent,
            false
        )
        val promptListViewHolder =
            GridListViewHolder(itemView)
        itemView.setOnClickListener {
            itemClickListener?.onItemClick(promptListViewHolder)
        }
        return promptListViewHolder
    }

    override fun onBindViewHolder(
        holder: GridListViewHolder,
        position: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val imagePath = withContext(Dispatchers.IO){
                imagePathList.get(position)
            } ?: return@launch
            withContext(Dispatchers.Main){
                val itemImageView = holder.itemImageView
                val itemImageContext = itemImageView.context
                val requestBuilder: RequestBuilder<Drawable> =
                    Glide.with(itemImageContext)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                Glide
                    .with(itemImageContext)
                    .load(imagePath)
                    .skipMemoryCache( true )
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail( requestBuilder )
                    .into(itemImageView)
                YoYo.with(Techniques.RotateInUpRight)
                    .duration(100)
                    .repeat(0)
                    .playOn(itemImageView)
            }
            withContext(Dispatchers.Main){
                holder.itemView.setOnClickListener {
                    itemClickListener?.onItemClick(
                        holder
                    )
                }
            }
        }
    }

    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(holder: GridListViewHolder)
    }
}