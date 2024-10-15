package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.terminal_fragment.ButtonImageCreator
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryPath
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.ButtonAssetsImage
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class PromptListAdapter(
    val context: Context?,
    var promptList: MutableList<String>,
    isWhiteBackgrond: Boolean,
): RecyclerView.Adapter<PromptListAdapter.PromptListViewHolder>() {

    class PromptListViewHolder(val view: View): RecyclerView.ViewHolder(view) {
//        val cardView = view.findViewById<MaterialCardView>(
//            R.id.prompt_list_adapter_cardview
//        )
        val promptListAdapterThumnail = view.findViewById<AppCompatImageView>(R.id.prompt_list_adapter_thumbnail)
        val promptListAdapterTitle = view.findViewById<OutlineTextView>(R.id.prompt_list_adapter_title)
        var itemStr: String = String()
    }

    override fun getItemCount(): Int = promptList.size

    override fun getItemId(position: Int): Long {
        //return super.getItemId(position)
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)
        return position
    }

    private val defaultUrlCapBitmap = AssetsFileManager.assetsByteArray(
        context,
        AssetsFileManager.firstUrlCapPngPath
    )?.let {
        BitmapFactory.decodeByteArray(it, 0, it.size)
    }

    private val centerWhiteColor = when(isWhiteBackgrond) {
        true -> null
        else -> "#ffffff"
    }

    private val thumbnailByteArray =
        runBlocking {
            ButtonImageCreator.ButtonImageCreator.create(
                context,
                ButtonAssetsImage.cPingPath,
                makeCapturePartPngDirPathList(),
                defaultUrlCapBitmap,
                centerWhiteColor
            )
        }
//        CmdClickIcons.values().map {
//            it.assetsPath
//        }.shuffled().firstOrNull()?.let {
//            ExecSetToolbarButtonImage.getImageFile(it)
//        }?.let {
//            BitmapTool.convertFileToByteArray(it.absolutePath)
//        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PromptListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            R.layout.prompt_list_adapter_layout,
            parent,
            false
        )
        val promptListViewHolder =
            PromptListViewHolder(itemView)
        itemView.setOnClickListener {
            itemClickListener?.onItemClick(promptListViewHolder)
        }
        return promptListViewHolder
    }

    override fun onBindViewHolder(
        holder: PromptListViewHolder,
        position: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.Main){
//                holder.cardView.elevation = 0f
//                holder.cardView.radius = 5f
//            }
            val text = promptList[position]
            withContext(Dispatchers.Main){
                val promptListAdapterTitle = holder.promptListAdapterTitle
                promptListAdapterTitle.setFillColor(R.color.ao)
                promptListAdapterTitle.outlineWidthSrc = 2
                promptListAdapterTitle.text = text
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lPromptAdapter.txt").absolutePath,
//                listOf(
//                    "text: ${text}"
//                ).joinToString("\n")
//            )
            holder.itemStr = text
            withContext(Dispatchers.Main){
                val promptListAdapterThumbnail = holder.promptListAdapterThumnail
                val rotateList = listOf(45, 135, 225, 315)
//                promptListAdapterThumbnail.rotation
                val curRotation = promptListAdapterThumbnail.rotation
                val nextRotate = rotateList.random()
                when(curRotation % 45f == 0f){
                    true -> promptListAdapterThumbnail.rotation = 45f + nextRotate
                    else -> nextRotate
                }
                promptListAdapterThumbnail.rotation = promptListAdapterThumbnail.rotation + rotateList.random()
                val logoViewContext = promptListAdapterThumbnail.context
                val requestBuilder: RequestBuilder<Drawable> =
                    Glide.with(logoViewContext)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                Glide
                    .with(logoViewContext)
                    .load(thumbnailByteArray)
                    .skipMemoryCache( true )
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail( requestBuilder )
                    .into(promptListAdapterThumbnail)
            }
            withContext(Dispatchers.Main){
                holder.itemView.setOnClickListener {
                    itemClickListener?.onItemClick(
                        holder
                    )
                }
            }
        }
    }

    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(holder: PromptListViewHolder)
    }

    private fun makeCapturePartPngDirPathList(): List<String> {
        val lastModifyExtend = UrlHistoryPath.lastModifyExtend
        val partPngDirName = UrlHistoryPath.partPngDirName
        val captureDirPath = UrlHistoryPath.makeCaptureHistoryDirPath()
        return FileSystems.sortedFiles(
            captureDirPath
        ).map {
            val dirName = it.removeSuffix(lastModifyExtend)
            listOf(
                captureDirPath,
                dirName,
                partPngDirName
            ).joinToString("/")
        }
    }
}