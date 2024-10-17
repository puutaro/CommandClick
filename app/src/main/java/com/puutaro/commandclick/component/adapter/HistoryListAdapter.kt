package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.tsv.TsvTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime

class HistoryListAdapter(
    context: Context,
    private val menuMapList: MutableList<Pair<String, String>>
) : ArrayAdapter<String>(
    context,
    R.layout.menu_list_adapter_layout,
    menuMapList.map { it.first }
) {


    private val mInflater =
        context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater


    companion object {

        val listHistoryPath = File(UsePath.fannelSystemDirPath, "listHistory.tsv").absolutePath
//        fun saveItemToList(
//            context: Context?,
//            menuName: String
//        ) {
//            val sharePref = FannelInfoTool.getSharePref(context)
//            CoroutineScope(Dispatchers.IO).launch {
//                val currentFannelName = withContext(Dispatchers.IO) {
//                    FannelInfoTool.getStringFromFannelInfo(
//                        sharePref,
//                        FannelInfoSetting.current_fannel_name
//                    )
//                }
//                val listHistoryPath = withContext(Dispatchers.IO) {
//                    ScriptPreWordReplacer.replace(
//                        listHistoryPath,
//                        currentFannelName,
//                    )
//                }
//                withContext(Dispatchers.IO) {
//                    val insertLine = "${menuName}\t${LocalDateTime.now()}"
//                    TsvTool.insertByLastUpdate(
//                        listHistoryPath,
//                        insertLine
//                    )
//                }
//            }
//        }
    }

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
        val logoPathSrc = currentMap.second
        val menuThumbnailImageView = view.findViewById<AppCompatImageView>(
            R.id.menu_list_thumbnail
        )
        menuThumbnailImageView.imageTintList = null
        when(File(logoPathSrc).isFile){
            true -> {
                val requestBuilder: RequestBuilder<Drawable> =

                    Glide.with(context)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                Glide
                    .with(context)
                    .load(logoPathSrc)
                    .skipMemoryCache( true )
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail( requestBuilder )
                    .into(menuThumbnailImageView)
            }
            else -> {
                val icon = CmdClickIcons.values().firstOrNull {
                    it.str == logoPathSrc
                } ?: CmdClickIcons.WHEEL
                CoroutineScope(Dispatchers.Main).launch {
                    ExecSetToolbarButtonImage.setImageButton(
                        menuThumbnailImageView,
                        icon,
                    )
                }
            }
        }

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

    fun add(`object`: Pair<String, String>?) {
        if(
            `object` == null
        ) return
        menuMapList.add(`object`)
    }


    fun addAll(items: List<Pair<String, String>>) {
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