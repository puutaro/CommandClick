package com.puutaro.commandclick.component.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class DragSortRecyclerAdapter(
    private val iNames: List<String>,
) :
    RecyclerView.Adapter<DragSortRecyclerAdapter.ViewHolder>() {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // each data item is just a string in this case
        var textView: TextView

        init {
            textView = v.findViewById(com.puutaro.commandclick.R.id.text_view_in_recycler_view)
        }
    }




    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val view: View = LayoutInflater.from(parent.context)
            .inflate(
                com.puutaro.commandclick.R.layout.drag_sort_recycler_view,
                parent,
                false
            )
        val viewHolder = ViewHolder(view)
        view.setOnClickListener{           // リスナーの実装
            itemClickListener?.onItemClick(viewHolder)
        }

        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(view)
    }

    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.imageView.setImageResource(iImages[position])
        holder.textView.text = iNames[position]
//        holder?.emailView.text = iEmails[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return iNames.size
    }
}