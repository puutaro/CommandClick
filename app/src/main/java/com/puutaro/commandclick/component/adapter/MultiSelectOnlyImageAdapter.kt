package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.lib.ImageAdapterTool
import com.puutaro.commandclick.util.file.AssetsFileManager


class MultiSelectOnlyImageAdapter(
    private val mContext: Context?
) : BaseAdapter() {

    private var holder: ViewHolder = ViewHolder()

    val selectedItemList = mutableListOf<String>()

    class ViewHolder {
        var imageView: ImageView? = null
        var checkedImageView: ImageView? = null
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

    fun clear() {
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

    fun onItemSelect(
        v: View?,
        pos: Int,
    ) {
        if (v == null) return
        val selectedItem = itemList[pos]
        when (
            selectedItemList.contains(selectedItem)
        ) {
            true
            -> selectedItemList.remove(selectedItem)

            false
            -> selectedItemList.add(selectedItem)
        }
        notifyDataSetChanged()
    }


    override fun getView(
        position: Int,
        convertViewArg: View?,
        parent: ViewGroup
    ): View {
        val imagePath = itemList[position]

        if (convertViewArg == null) {
            val li = mContext?.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
            ) as LayoutInflater
            val convertView = li.inflate(
                R.layout.multi_grid_only_image_layout,
                parent,
                false
            ) as View
            val checkedImageView =
                convertView.findViewById<ImageView>(
                    R.id.multi_only_image_checkd_view
                )
            checkedImageView.isVisible = false
            holder = ViewHolder()
            holder.checkedImageView = checkedImageView
            val imageView =
                convertView.findViewById<ImageView>(
                    R.id.multi_only_image_image_view
                )
            holder.imageView = setImageView(
                imageView,
                imagePath
            )
            selectedChange(
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
        val selectedItem = itemList[position]
        selectedChange(
            selectedItem
        )
        setImageView(
            imageView,
            imagePath
        )
        return convertViewArg
    }

    private fun selectedChange(
        selectedItem: String
    ){
        if (
            selectedItemList.contains(selectedItem)
        ) {
            holder.checkedImageView?.isVisible = true
        } else {
            holder.checkedImageView?.isVisible = false
        }
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