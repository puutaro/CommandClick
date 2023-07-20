package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.util.AssetsFileManager
import java.io.File
import kotlin.math.roundToInt


class MultiSelectImageAdapter(
    private val mContext: Context?
) : BaseAdapter() {

    private var holder: ViewHolder = ViewHolder()

    val selectedItemList = mutableListOf<String>()

    class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
    }
    private val pdfExtend = UsePath.pdfExtend
    private val textImagePngBitMap = makeFileMarkBitMap(
        AssetsFileManager.textImagePingPath
    )
    private val pdfImagePngBitMap = makeFileMarkBitMap(
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
    fun onItemSelect(
        v: View?,
        pos: Int,
    ) {
        if(v == null) return
        val selectedItem = itemList[pos]
        when(
            selectedItemList.contains(selectedItem)
        ){
            true
            -> selectedItemList.remove(selectedItem)
            false
            -> selectedItemList.add(selectedItem)
        }
        notifyDataSetChanged()
    }


    override fun getView(position: Int, convertViewArg: View?, parent: ViewGroup): View {
        val imagePath = itemList[position]
        val imageName = File(imagePath).name

        if (convertViewArg == null) {
            val li = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val convertView = li.inflate(
                R.layout.grid_image_items,
                parent,
                false
            ) as View
            val textView = convertView.findViewById<TextView>(com.puutaro.commandclick.R.id.caption_view)
            textView.text = imageName
            holder = ViewHolder()
            holder.textView = textView
            val imageView = convertView.findViewById<ImageView>(com.puutaro.commandclick.R.id.image_view)
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
        val selectedItem = itemList[position]
        if (
            selectedItemList.contains(selectedItem)
        ) { holder.textView?.setTextColor(Color.parseColor("#95eddd"))
            holder.imageView?.alpha = 0.4F
        } else {
            holder.textView?.setTextColor(Color.parseColor("#ffffff"))
            holder.imageView?.alpha = 1F
        }
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
        if(
            path.isNullOrEmpty()
        ) return null
        if(
            !File(path).isFile
        ) return null
        var bm: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        bm = BitmapFactory.decodeFile(path, options)
        return when(bm){
            null -> judgePdfOrOther(path)
            else -> bm
        }
    }

    private fun judgePdfOrOther(
        path: String
    ): Bitmap {
        val onPdf = path.endsWith(pdfExtend)
        return when(onPdf){
            true -> pdfImagePngBitMap
            else -> textImagePngBitMap
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            inSampleSize = if (width > height) {
                (height.toFloat() / reqHeight.toFloat()).roundToInt()
            } else {
                (width.toFloat() / reqWidth.toFloat()).roundToInt()
            }
        }
        return inSampleSize
    }

    private fun setImageView(
        imageView: ImageView?,
        imagePath: String
    ): ImageView? {
        val bm = decodeSampledBitmapFromUri(
            imagePath,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        imageView?.setImageBitmap(bm)
        imageView?.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView?.setPadding(8, 8, 8, 8)
        return imageView
    }

    private fun makeFileMarkBitMap(
        assetsRelativePath: String
    ): Bitmap {
        val assetManager = mContext?.assets
        val fileMarkbitmap = BitmapFactory.decodeStream(
            assetManager?.open(
                assetsRelativePath
            )
        )
        return fileMarkbitmap
    }
}
