package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.terminal_fragment.ButtonImageCreator
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.PromptWithListDialog
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
    var prompMapList: MutableList<Map<String, String?>>,
    private val isWhiteBackgrond: Boolean,
): RecyclerView.Adapter<PromptListAdapter.PromptListViewHolder>() {

    class PromptListViewHolder(val view: View): RecyclerView.ViewHolder(view) {
//        val cardView = view.findViewById<MaterialCardView>(
//            R.id.prompt_list_adapter_cardview
//        )
        val promptListAdapterTitleBk = view.findViewById<ShapeableImageView>(R.id.prompt_list_adapter_thumbnail_bk)
        val promptListAdapterThumnail = view.findViewById<AppCompatImageView>(R.id.prompt_list_adapter_thumbnail)
        val promptListAdapterTitle = view.findViewById<OutlineTextView>(R.id.prompt_list_adapter_title)
        var itemMap: Map<String, String?> = emptyMap()
    }

    override fun getItemCount(): Int = prompMapList.size

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
    private val rotateList = listOf(45, 135, 225, 315)

    private val thumbnailByteArray =
        runBlocking {
            ButtonImageCreator.ButtonImageCreator.create(
                context,
                ButtonAssetsImage.cPingPath,
                makeCapturePartPngDirPathList(),
                defaultUrlCapBitmap,
                centerWhiteColor,
                BitmapTool.GradientBitmap.GradOrient.DIAGONAL,
            )
        }

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

    private val titleKey =  PromptWithListDialog.Companion.PromptMapList.PromptListKey.TITLE.key
    private val iconKey =  PromptWithListDialog.Companion.PromptMapList.PromptListKey.ICON.key

    override fun onBindViewHolder(
        holder: PromptListViewHolder,
        position: Int
    ) {
        if(
            context == null
        ) return
        CoroutineScope(Dispatchers.IO).launch {
            val lineMap = prompMapList[position]
            withContext(Dispatchers.Main){
                val title = withContext(Dispatchers.IO) {
                    lineMap.get(titleKey)
                }
                val promptListAdapterTitle = holder.promptListAdapterTitle
                promptListAdapterTitle.setFillColor(R.color.ao)
                promptListAdapterTitle.outlineWidthSrc = 2
                promptListAdapterTitle.text = title
            }
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lPromptAdapter.txt").absolutePath,
//                listOf(
//                    "text: ${text}"
//                ).joinToString("\n")
//            )
            holder.itemMap = lineMap
            withContext(Dispatchers.Main){
                val iconStr = lineMap.get(iconKey)
                val icon = CmdClickIcons.entries.firstOrNull {
                    it.str == iconStr
                }
                val isSetWhiteGradBk = when (true) {
                    (!iconStr.isNullOrEmpty() && File(iconStr).isFile)
                        -> true && !isWhiteBackgrond
                    else -> false
                }
                val promptListAdapterTitleBk = holder.promptListAdapterTitleBk
                if(isSetWhiteGradBk){
                    promptListAdapterTitleBk.isVisible = true
                    val titleBkContext = promptListAdapterTitleBk.context
                    val bkTitleDrawable = AppCompatResources.getDrawable(
                        titleBkContext,
                        R.drawable.white_grad
                    )
                    val requestBuilder: RequestBuilder<Drawable> =
                        Glide.with(titleBkContext)
                            .asDrawable()
                            .sizeMultiplier(0.1f)
                    Glide
                        .with(titleBkContext)
                        .load(bkTitleDrawable)
                        .skipMemoryCache( true )
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .thumbnail( requestBuilder )
                        .into(promptListAdapterTitleBk)

                    val nextRotate = rotateList.random().toFloat()
                    val curRotation = holder.promptListAdapterTitleBk.rotation
                    val nextRotaionAngle = when(curRotation % 45f == 0f){
                        true -> nextRotate
                        else -> 45f + nextRotate
                    }
                    holder.promptListAdapterTitleBk.rotation = nextRotaionAngle
                }
                val byteArray = withContext(Dispatchers.IO) {
                    when (true) {
                        (icon != null)
                            -> {
                            ButtonImageCreator.ButtonImageCreator.create(
                                context,
                                icon.assetsPath,
                                makeCapturePartPngDirPathList(),
                                defaultUrlCapBitmap,
                                centerWhiteColor,
                                BitmapTool.GradientBitmap.GradOrient.DIAGONAL,
                            )
                        }
                        (!iconStr.isNullOrEmpty() && File(iconStr).isFile)
                            -> BitmapTool.convertFileToByteArray(iconStr)
                        else -> thumbnailByteArray
                    }
                } ?: return@withContext
                val promptListAdapterThumbnail = holder.promptListAdapterThumnail
                val curRotation = promptListAdapterThumbnail.rotation
                val nextRotate = rotateList.random().toFloat()
                when(curRotation % 45f == 0f){
                    true -> promptListAdapterThumbnail.rotation = nextRotate
                    else -> promptListAdapterThumbnail.rotation = 45f + nextRotate
                }
                val logoViewContext = promptListAdapterThumbnail.context
                val requestBuilder: RequestBuilder<Drawable> =
                    Glide.with(logoViewContext)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                Glide
                    .with(logoViewContext)
                    .load(byteArray)
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