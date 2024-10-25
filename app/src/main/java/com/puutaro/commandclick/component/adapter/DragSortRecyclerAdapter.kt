package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import java.io.File


class DragSortRecyclerAdapter(
    private val context: Context,
    val dragSortList: MutableList<String>,
//    private val screenWidth: Float,
) :
    RecyclerView.Adapter<DragSortRecyclerAdapter.ViewHolder>() {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        // each data item is just a string in this case
//        var textView: TextView
        val itemTitleView = v.findViewById<OutlineTextView>(
            R.id.drag_sort_adapter_item_title
        )
        init {
//            textView = v.findViewById(
//                R.id.text_view_in_recycler_view
//            )

        }
    }

    val requestBuilder: RequestBuilder<Drawable> =
        Glide.with(context)
            .asDrawable()
            .sizeMultiplier(0.1f)




    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
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
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.imageView.setImageResource(iImages[position])
        val titleText = dragSortList[position]
//        text.chunked(10).map {
//            partText ->
//            BitmapTool.DrawText.drawTextToBitmap(
//                context,
//                partText
//            )
//        }.let {
//            partTextBitmap ->
//            BitmapTool.concatByHorizon()
//        }
//        holder.textView.text = text
//        val imageWidth = screenWidth - screenWidthPadding
//        val imageHeight = (imageWidth * 10) / 25
//        FileSystems.updateFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "ldraw.txt").absolutePath,
//            listOf(
//                "text: ${titleText}",
//                "imageWidth: ${imageWidth}",
//                "imageHeight: ${imageHeight}",
//            ).joinToString("\n")
//        )
//        val bitmap = BitmapTool.DrawText.drawTextToBitmap(
//            titleText,
//            imageWidth,
//            imageHeight
//        )
//        if(position == 0){
//            bitmap?.let {
//                FileSystems.writeFromByteArray(
//                    File(UsePath.cmdclickDefaultAppDirPath, "ltext.png").absolutePath,
//                    BitmapTool.convertBitmapToByteArray(it)
//                )
//            }
//        }
        val itemTitleView = holder.itemTitleView
        itemTitleView.apply {
            text = titleText
            setFillColor(R.color.ao)
            setStrokeColor(R.color.white)
            outlineWidthSrc = 3
        }
//        itemTitleView.imageTintList = null
////        AppCompatResources.getColorStateList(
////            context,
////            R.color.terminal_color
////        )
//        Glide.with(context)
//            .load(bitmap)
//            .skipMemoryCache(true)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .thumbnail(requestBuilder)
//            .into(itemTitleView)
//        YoYo.with(Techniques.FadeInRight)
//            .duration(700)
//            .repeat(5)
//            .playOn(itemTitleView)
//        holder?.emailView.text = iEmails[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dragSortList.size
    }
}