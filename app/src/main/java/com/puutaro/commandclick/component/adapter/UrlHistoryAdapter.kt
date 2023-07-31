package com.puutaro.commandclick.component.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.WebUrlVariables
import java.io.File


class UrlHistoryAdapter(
    context: Context,
    private val mResource: Int,
    val urlHistoryList: MutableList<String>
) : ArrayAdapter<String>(
        context,
        mResource,
        urlHistoryList
    ) {
    private val mInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater



    /**
     * コンストラクタ
     * @param context コンテキスト
     * @param resource リソースID
     * @param items リストビューの要素
     */

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        view = convertView ?: mInflater.inflate(
            mResource,
            parent,
            false
        )

        // リストビューに表示する要素を取得
        val titleUrlLine = urlHistoryList[position].split("\t")
        val title = titleUrlLine.firstOrNull() ?: String()
        val urlStr = titleUrlLine.getOrNull(1) ?: String()


        // サムネイル画像を設定
        val thumbnailView = view.findViewById<ImageView>(
            R.id.url_hisotry_thumbnail
        )
        setThumbnail(
            thumbnailView,
            title,
            urlStr
        )
//            .setImageBitmap(titleUrlLine.getThumbnail())

        // タイトルを設定
        val titleTextView = view.findViewById<TextView>(
            R.id.url_history_title
        )
        titleTextView.text = title
//        val domainTextView = view.findViewById<TextView>(
//            R.id.url_history_domain
//        )
//        domainTextView.text = UrlTool.extractDomain(urlStr)
        return view
    }

    override fun getCount(): Int {
        return urlHistoryList.size - 1
    }

    override fun clear() {
        urlHistoryList.clear()
    }

    override fun add(`object`: String?) {
        if(`object`.isNullOrEmpty()) return
        urlHistoryList.add(`object`)
    }


    fun addAll(items: List<String>) {
        if(items.isEmpty()) return
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