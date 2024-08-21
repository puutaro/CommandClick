package com.puutaro.commandclick.component.adapter
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.util.url.EnableUrlPrefix
import com.puutaro.commandclick.proccess.history.url_history.UrlHistoryPath
import com.puutaro.commandclick.proccess.history.url_history.UrlLogoHistoryTool
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class UrlHistoryAdapter(
    private val fragment: Fragment,
//    private val currentAppDirPath: String,
    var urlHistoryMapList: MutableList<Map<String, String>>,
    private val currentUrl: String?,
): RecyclerView.Adapter<UrlHistoryAdapter.UrlHistoryViewHolder>(){

    class UrlHistoryViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val urlHistoryAdapterConstraintLayout = view.findViewById<ConstraintLayout>(R.id.url_history_adapter_constraint_layout)
        val urlHistoryAdapterRelativeLayout = view.findViewById<RelativeLayout>(R.id.url_history_adapter_relative_layout)
        val urlCaptureView = view.findViewById<AppCompatImageView>(R.id.url_history_adapter_capture)
        val urlTitleTextView = view.findViewById<OutlineTextView>(R.id.url_history_adapter_title)
//        val urlHistoryAdapterBottomLinearInner = view.findViewById<LinearLayoutCompat>(R.id.url_history_adapter_bottom_linear_inner)
        val urlSiteLogoView = view.findViewById<AppCompatImageButton>(R.id.url_history_adapter_site_logo)
        val copyImageButtonView = view.findViewById<AppCompatImageButton>(R.id.url_history_adapter_copy)
        val deleteImageButtonView = view.findViewById<AppCompatImageButton>(R.id.url_history_adapter_delete)
    }

    companion object {
        enum class UrlHistoryMapKey(
            val key: String
        ){
            TITLE("title"),
            URL("url"),
            ICON_BASE64_STR("iconBase64Str"),
            CAPTURE_BASE64_STR("captureBase64Str"),
        }

        enum class FileType{
            BOTTOM_FANNEL,
            NORMAL_FANNEL,
        }
    }

    private val context = fragment.context
    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

    private val urlHistoryGifByteArray = AssetsFileManager.assetsByteArray(
        context,
        AssetsFileManager.urlHistoryGifPath
    )
    private val internetGifByteArray = AssetsFileManager.assetsByteArray(
        context,
        AssetsFileManager.internetGifPath
    )

//    private val tileHeight = ScreenSizeCalculator.dpWidth(fragment).let {
//        (it * 3) / 2
//    }
//
//    private val tileRelativeLayoutParam = LinearLayoutCompat.LayoutParams(
//        LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
//        tileHeight.toInt()
//    )
//
//    private val recyclerViewLayoutParam = RecyclerView.LayoutParams(
//        RecyclerView.LayoutParams.MATCH_PARENT,
//        tileHeight.toInt() + ScreenSizeCalculator.toDp(fragment.context, 40)
//    )


//    private val intrudeGifByteArray = AssetsFileManager.assetsByteArray(
//        context,
//        AssetsFileManager.intrudeGifPath
//    )
//
//    private val ccRoboGifByteArray = AssetsFileManager.assetsByteArray(
//        context,
//        AssetsFileManager.ccRoboGifPath
//    )


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UrlHistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            R.layout.url_history_list_view_adapter_layout,
            parent,
            false
        )
        val historyViewHolder = UrlHistoryViewHolder(itemView)
        return historyViewHolder
    }

    override fun getItemId(position: Int): Long {
        //return super.getItemId(position)
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)
        return position
    }

    override fun getItemCount(): Int = urlHistoryMapList.size

    override fun onBindViewHolder(
        holder: UrlHistoryViewHolder,
        position: Int
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val urlHistoryMap = urlHistoryMapList[position]
//            withContext(Dispatchers.Main){
//                holder.urlHistoryAdapterLinearLayout.layoutParams = recyclerViewLayoutParam
//                holder.urlHistoryAdapterRelativeLayout.layoutParams = tileRelativeLayoutParam
//            }
            val urlStr = withContext(Dispatchers.IO) {
                urlHistoryMap.get(UrlHistoryMapKey.URL.key)
                    ?: String()
            }
            if(urlStr == currentUrl) {
                withContext(Dispatchers.Main) {
                    val hitUrlColor = R.color.gold_yellow
                    holder.urlHistoryAdapterConstraintLayout.backgroundTintList =
                        context?.getColorStateList(hitUrlColor)
//                    holder.urlHistoryAdapterBottomLinearInner.backgroundTintList =
//                        context?.getColorStateList(hitUrlColor)
                    holder.copyImageButtonView.backgroundTintList =
                        context?.getColorStateList(hitUrlColor)
                    holder.deleteImageButtonView.backgroundTintList =
                        context?.getColorStateList(hitUrlColor)
                    holder.urlSiteLogoView.backgroundTintList =
                        context?.getColorStateList(hitUrlColor)
                }
            }
            val iconBase64Str = withContext(Dispatchers.IO) {
                urlHistoryMap.get(UrlHistoryMapKey.ICON_BASE64_STR.key)
                    ?: let {
                        val logoBase64TxtPath = UrlLogoHistoryTool.getCaptureBase64TxtPathByUrl(
//                            currentAppDirPath,
                            urlStr,
                        )?.absolutePath
                            ?: return@let null
                        ReadText(logoBase64TxtPath).readText()
                    }
            }
            val capturePngPathOrMacro =
                urlHistoryMap.get(UrlHistoryMapKey.CAPTURE_BASE64_STR.key)
                    ?: UrlHistoryPath.getCaptureGifPath(
//                        currentAppDirPath,
                        urlStr,
                    )

            setCaptureImage(
                holder,
                capturePngPathOrMacro,
                urlStr
            )
            setSiteLogo(
                holder,
                iconBase64Str,
                urlStr,
            )
            setTitle(
                holder,
                urlHistoryMap.get(UrlHistoryMapKey.TITLE.key),
                urlStr,
            )
            withContext(Dispatchers.Main) {
                holder.itemView.setOnClickListener {
                    itemClickListener?.onItemClick(holder)
                }
                holder.urlSiteLogoView.setOnClickListener {
                    logoItemClickListener?.onItemClick(holder)
                }
                holder.copyImageButtonView.setOnClickListener {
                    copyItemClickListener?.onItemClick(holder)
                }
                holder.deleteImageButtonView.setOnClickListener {
                    deleteItemClickListener?.onItemClick(holder)
                }
            }
        }
    }

    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(holder: UrlHistoryViewHolder)
    }

    var logoItemClickListener: OnLogoItemClickListener? = null
    interface OnLogoItemClickListener {
        fun onItemClick(holder: UrlHistoryViewHolder)
    }

    var deleteItemClickListener: OnDeleteItemClickListener? = null
    interface OnDeleteItemClickListener {
        fun onItemClick(holder: UrlHistoryViewHolder)
    }
    var copyItemClickListener: OnCopyItemClickListener? = null
    interface OnCopyItemClickListener {
        fun onItemClick(holder: UrlHistoryViewHolder)
    }

    private suspend fun setCaptureImage(
        holder: UrlHistoryViewHolder,
        capturePngPathOrMacro: String?,
        url: String,
    ){
        if (
            context == null
        ) return

        val urlCaptureView = holder.urlCaptureView
        val context = urlCaptureView.context
        val isFile = !capturePngPathOrMacro.isNullOrEmpty()
                && File(capturePngPathOrMacro).isFile
        withContext(Dispatchers.Main) {
            when (isFile) {
                true -> {
                    holder.urlCaptureView.imageTintList = null
                    val requestBuilder: RequestBuilder<Drawable> =
                        Glide.with(context)
                            .asDrawable()
                            .sizeMultiplier(0.1f)
                    Glide
                        .with(context)
                        .load(capturePngPathOrMacro)
                        .skipMemoryCache( true )
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .thumbnail( requestBuilder )
                        .into(urlCaptureView)
                }

                else -> {
                    val byteArray =
                        if(
                            EnableUrlPrefix.isHttpPrefix(url)
                        ) internetGifByteArray
                        else urlHistoryGifByteArray
                    Glide
                        .with(holder.urlCaptureView.context)
                        .load(byteArray)
                        .into(holder.urlCaptureView)
                }
            }
        }
    }

    private suspend fun setSiteLogo(
        holder: UrlHistoryViewHolder,
        iconBase64Str: String?,
        urlStr: String,
    ){
        if(context == null) return
        val fileType = FileType.values().firstOrNull {
            it.name == iconBase64Str
        }
        if(fileType != null) {
            val color = when(fileType){
                FileType.BOTTOM_FANNEL -> R.color.fannel_icon_color
                FileType.NORMAL_FANNEL -> R.color.orange
            }
            withContext(Dispatchers.Main) {
                Glide
                    .with(holder.urlSiteLogoView.context)
                    .load(R.drawable.icons8_file)
                    .centerCrop()
                    .into(holder.urlSiteLogoView)
//                holder.urlSiteLogoView.load(R.drawable.icons8_file)
                holder.urlSiteLogoView.imageTintList =
                    context.getColorStateList(color)
            }
            return
        }
        val iconBitMap = withContext(Dispatchers.IO) {
            BitmapTool.Base64Tool.decode(
                iconBase64Str
            )
        }
        withContext(Dispatchers.Main) {
            if (
                iconBitMap != null
            ) {
                holder.urlSiteLogoView.imageTintList = null
                Glide
                    .with(holder.urlSiteLogoView.context)
                    .load(iconBitMap)
                    .centerCrop()
                    .into(holder.urlSiteLogoView)
//                holder.urlSiteLogoView.load(iconBitMap)
                return@withContext
            }
            holder.urlSiteLogoView.imageTintList =
                when(
                    EnableUrlPrefix.isHttpPrefix(urlStr)
                ) {
                    false -> {
                        Glide
                            .with(holder.urlSiteLogoView.context)
                            .load(R.drawable.icons8_file)
                            .centerCrop()
                            .into(holder.urlSiteLogoView)
//                        holder.urlSiteLogoView.load(R.drawable.icons8_file)
                        context.getColorStateList(R.color.orange)
                    }
                    else -> {
                        Glide
                            .with(holder.urlSiteLogoView.context)
                            .load(R.drawable.internet)
                            .centerCrop()
                            .into(holder.urlSiteLogoView)
//                        holder.urlSiteLogoView.load(R.drawable.internet)
                        context.getColorStateList(R.color.web_icon_color)
                    }
                }
        }
    }

    private suspend fun setTitle(
        holder: UrlHistoryViewHolder,
        titleSrc: String?,
        urlStr: String,
    ){
        val title = withContext(Dispatchers.IO) {
            if (
                titleSrc.isNullOrEmpty()
            ) return@withContext urlStr
            titleSrc
        }
        withContext(Dispatchers.Main) {
            holder.urlTitleTextView.text = title
        }
    }
}
