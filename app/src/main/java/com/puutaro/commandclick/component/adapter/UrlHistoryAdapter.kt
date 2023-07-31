package com.puutaro.commandclick.component.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.WebUrlVariables
import java.io.File


class UrlHistoryAdapter(
    context: Context,
    private val mResource: Int,
    private val urlHistoryList: MutableList<String>
) : ArrayAdapter<String>(
        context,
        mResource,
        urlHistoryList
    ) {
    private val mInflater =
        context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater



    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param resource リソースID
     * @param items リストビューの要素
     */
    private var displayTimes = 0
    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = convertView ?: mInflater.inflate(
            mResource,
            parent,
            false
        )
        val titleUrlLine = urlHistoryList[position].split("\t")
        val urlStr = titleUrlLine.getOrNull(1) ?: String()
        val title = titleUrlLine.firstOrNull().let {
            if(
                it.isNullOrEmpty()
            ) return@let urlStr
            it
        }
        val thumbnailView = view.findViewById<ImageView>(
            R.id.url_hisotry_thumbnail
        )
        setThumbnail(
            thumbnailView,
            title,
            urlStr
        )
        val titleTextView = view.findViewById<TextView>(
            R.id.url_history_title
        )
        titleTextView.text = title
        displayTimes++
        return view
    }

    override fun getCount(): Int {
        return urlHistoryList.size
    }

    override fun clear() {
        urlHistoryList.clear()
    }

    override fun add(`object`: String?) {
        if(
            `object`.isNullOrEmpty()
        ) return
        urlHistoryList.add(`object`)
    }


    fun addAll(items: List<String>) {
        if(
            items.isEmpty()
        ) return
        urlHistoryList.addAll(items)
    }

    override fun getItem(position: Int): String? {
        return urlHistoryList.getOrNull(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    private fun setThumbnail(
        thumbnailView: ImageView,
        title: String,
        url: String
    ) {
        if(
            url.startsWith(WebUrlVariables.httpsPrefix)
            || url.startsWith(WebUrlVariables.httpPrefix)
        ) {
            thumbnailView.setImageResource(R.drawable.internet)
            thumbnailView.imageTintList =
                context.getColorStateList(R.color.web_icon_color)
            return
        }
        val isUrlFile = url.startsWith(WebUrlVariables.filePrefix)
                || url.startsWith(WebUrlVariables.slashPrefix)
        val parentDirPath =  File(url).parent ?: String()
        val isFannel = File("${parentDirPath}/${title}").isFile
        if(
            isFannel && isUrlFile
        ) {
            thumbnailView.setImageResource(R.drawable.icons8_file)
            thumbnailView.imageTintList =
                context.getColorStateList(R.color.fannel_icon_color)
            return
        }
        if(isUrlFile) {
            thumbnailView.setImageResource(R.drawable.icons8_file)
            thumbnailView.imageTintList =
                context.getColorStateList(R.color.file_icon_color)
            return
        }

        thumbnailView.setImageResource(R.drawable.ic_terminal)
        thumbnailView.imageTintList =
            context.getColorStateList(R.color.terminal_color)
    }



}