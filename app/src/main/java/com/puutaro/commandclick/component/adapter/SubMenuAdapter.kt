package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.puutaro.commandclick.R


class SubMenuAdapter(
    context: Context,
    private val menuMapList: MutableList<Pair<String, Int>>
) : ArrayAdapter<String>(
    context,
    R.layout.menu_list_adapter_layout,
    menuMapList.map { it.first }
) {
    private val mInflater =
        context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = convertView ?: mInflater.inflate(
            R.layout.menu_list_adapter_layout,
            parent,
            false
        )
        val currentMap = menuMapList[position]
        val title = currentMap.first
        val thumbnailId = currentMap.second
        val menuThumbnailImageView = view.findViewById<AppCompatImageView>(
            R.id.menu_list_thumbnail
        )
        menuThumbnailImageView.setImageResource(thumbnailId)
        menuThumbnailImageView.imageTintList =
            context.getColorStateList(R.color.cmdclick_text_black)
        val menuTextView = view.findViewById<AppCompatTextView>(
            R.id.menu_list_title
        )
        menuTextView.text = title
//        menuTextView.setCompoundDrawablesWithIntrinsicBounds(
//            thumbnailId,
//            0,
//            0,
//            0
//        )
        return view
    }

    override fun getCount(): Int {
        return menuMapList.size
    }

    override fun clear() {
        menuMapList.clear()
    }

    fun add(`object`: Pair<String, Int>?) {
        if(
            `object` == null
        ) return
        menuMapList.add(`object`)
    }


    fun addAll(items: List<Pair<String, Int>>) {
        if(
            items.isEmpty()
        ) return
        menuMapList.addAll(items)
    }

    override fun getItem(position: Int): String? {
        return menuMapList.getOrNull(position)?.first
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}