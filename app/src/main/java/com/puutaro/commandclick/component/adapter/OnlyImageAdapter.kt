package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.lib.ImageAdapterTool
import com.puutaro.commandclick.util.AssetsFileManager

class OnlyImageAdapter(
    private val mContext: Context?
) : BaseAdapter() {

    class ViewHolder {
        var imageView: ImageView? = null
    }
    private val textImagePngBitMap = ImageAdapterTool.makeFileMarkBitMap(
        mContext,
        AssetsFileManager.textImagePingPath
    )
    private val pdfImagePngBitMap = ImageAdapterTool.makeFileMarkBitMap(
        mContext,
        AssetsFileManager.pdfImagePingPath
    )
    private var itemList = mutableListOf<String>()
    fun add(path: String) {
        itemList.add(path)
    }

    fun addAll(pathList: MutableList<String>) {
        itemList.addAll(pathList)
    }

    fun clear(){
        itemList.clear()
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(posi: Int): String {
        return itemList[posi]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(
        position: Int,
        convertViewArg: View?,
        parent: ViewGroup
    ): View {
        val holder: ViewHolder
        val imagePath = itemList[position]

        if (convertViewArg == null) {
            val li = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val convertView = li.inflate(
                R.layout.grid_only_image_layout,
                parent,
                false
            ) as View
            holder = ViewHolder()
            val imageView = convertView.findViewById<ImageView>(
                R.id.grid_only_image_view
            )
            holder.imageView = setImageView(
                imageView,
                imagePath
            )
            convertView.tag = holder
            convertView.layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            return convertView
        }
        holder = convertViewArg.tag as ViewHolder
        val imageView = holder.imageView
        setImageView(
            imageView,
            imagePath
        )
        return convertViewArg
    }

    private fun setImageView(
        imageView: ImageView?,
        imagePath: String
    ): ImageView? {
        val bm = ImageAdapterTool.decodeSampledBitmapFromUri(
            imagePath,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            pdfImagePngBitMap,
            textImagePngBitMap,
        )
        imageView?.setImageBitmap(bm)
        imageView?.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView?.setPadding(8, 8, 8, 8)
        return imageView
    }
}
