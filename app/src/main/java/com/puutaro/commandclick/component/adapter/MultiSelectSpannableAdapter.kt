package com.puutaro.commandclick.component.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.component.adapter.lib.ImageAdapterTool
import com.puutaro.commandclick.component.adapter.lib.SppannableAdapterTool
import com.puutaro.commandclick.util.file.AssetsFileManager
import java.lang.ref.WeakReference


class MultiSelectSpannableAdapter(
    private val activityRef: WeakReference<Activity?>,
    private val mContext: Context?
) : BaseAdapter() {

    private var holder: ViewHolder = ViewHolder()

    val selectedItemList = mutableListOf<String>()

    class ViewHolder {
        var spannableView: TextView? = null
        var imageCheckedView: ImageView? = null
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
                com.puutaro.commandclick.R.layout.multi_grid_spannable_layout,
                parent,
                false
            ) as View
            val imageCheckedView = convertView.findViewById<ImageView>(
                com.puutaro.commandclick.R.id.multi_spannable_check_image_view
            )
            imageCheckedView.isVisible = false
            holder = ViewHolder()
            holder.imageCheckedView = imageCheckedView
            val spannableView = convertView.findViewById<TextView>(
                com.puutaro.commandclick.R.id.multi_spannable_image_view
            )
            holder.spannableView = SppannableAdapterTool.setSpannableView(
                activityRef.get(),
                spannableView,
                imagePath,
                textImagePngBitMap,
                pdfImagePngBitMap,
            )
            selectedChange(
                imagePath
            )
            convertView.tag = holder
            convertView.layoutParams = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,
            )
            return convertView
        }
        holder = convertViewArg.tag as ViewHolder
        val spannableView = holder.spannableView
        val selectedItem = itemList[position]
        selectedChange(
            selectedItem
        )
        SppannableAdapterTool.setSpannableView(
            activityRef.get(),
            spannableView,
            imagePath,
            textImagePngBitMap,
            pdfImagePngBitMap,
        )
        return convertViewArg
    }

    private fun selectedChange(
        selectedItem: String
    ){
        if (
            selectedItemList.contains(selectedItem)
        ) {
            holder.spannableView?.alpha = 0.4F
            holder.imageCheckedView?.isVisible = true
        } else {
            holder.spannableView?.alpha = 1F
            holder.imageCheckedView?.isVisible = false
        }
    }
}
