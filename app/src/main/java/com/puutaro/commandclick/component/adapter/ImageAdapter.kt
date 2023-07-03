package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.puutaro.commandclick.R
import java.io.File


class ImageAdapter(
        private val mContext: Context?
    ) : BaseAdapter() {

    class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
    }
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

    override fun getView(position: Int, convertViewArg: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val imagePath = itemList[position]
        val imageName = File(imagePath).name

        if (convertViewArg == null) {
            val li = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val convertView = li.inflate(
                R.layout.grid_items,
                parent,
                false
            ) as View
            val textView = convertView.findViewById<TextView>(R.id.text_view)
            textView.text = imageName
            holder = ViewHolder()
            holder.textView = textView
            val imageView = convertView.findViewById<ImageView>(R.id.image_view)
            holder.imageView = setImageView(
                imageView,
                imagePath
            )
            convertView.tag = holder
            convertView.layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
            )
            return convertView
        }
        holder = convertViewArg.tag as ViewHolder
        holder.textView?.text = imageName
        val imageView = holder.imageView
        setImageView(
            imageView,
            imagePath
        )
        return convertViewArg
    }

    private fun decodeSampledBitmapFromUri(
        path: String?,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        var bm: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        bm = BitmapFactory.decodeFile(path, options)
        return bm
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            inSampleSize = if (width > height) {
                Math.round(height.toFloat() / reqHeight.toFloat())
            } else {
                Math.round(width.toFloat() / reqWidth.toFloat())
            }
        }
        return inSampleSize
    }

    private fun setImageView(
        imageView: ImageView?,
        imagePath: String
    ): ImageView? {
        val bm = decodeSampledBitmapFromUri(
            imagePath, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
        )
        imageView?.setImageBitmap(bm)
        imageView?.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView?.setPadding(8, 8, 8, 8)
        return imageView
    }
}