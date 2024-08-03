package com.puutaro.commandclick.component.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.puutaro.commandclick.R
import com.puutaro.commandclick.component.adapter.lib.ImageAdapterTool
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EnableUrlPrefix
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.image_tools.BitmapTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UrlHistoryAdapter(
    val context: Context?,
    var urlHistoryMapList: MutableList<Map<String, String>>
): RecyclerView.Adapter<UrlHistoryAdapter.UrlHistoryViewHolder>(){

    class UrlHistoryViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val urlCaptureView = view.findViewById<AppCompatImageView>(R.id.url_history_adapter_capture)
        val urlTitleImageView = view.findViewById<AppCompatImageView>(R.id.url_history_adapter_title_image)
        val urlTitleTextView = view.findViewById<OutlineTextView>(R.id.url_history_adapter_title)
        val copyImageView = view.findViewById<AppCompatImageButton>(R.id.url_history_adapter_copy)
        val deleteImageView = view.findViewById<AppCompatImageButton>(R.id.url_history_adapter_delete)
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

    private val fileMarkbitmap = ImageAdapterTool.makeFileMarkBitMap(
        context,
        AssetsFileManager.textImagePingPath
    )

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
            val urlStr = withContext(Dispatchers.IO) {
                urlHistoryMap.get(UrlHistoryMapKey.URL.key) ?: String()
            }
            val iconBase64Str = withContext(Dispatchers.IO) {
                urlHistoryMap.get(UrlHistoryMapKey.ICON_BASE64_STR.key)
            }
            val captureBase64Str = withContext(Dispatchers.IO) {
                urlHistoryMap.get(UrlHistoryMapKey.CAPTURE_BASE64_STR.key)
            }
            setCaptureImage(
                holder,
                captureBase64Str,
            )
            setIcon(
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
                holder.copyImageView.setOnClickListener {
                    copyItemClickListener?.onItemClick(holder)
                }
                holder.deleteImageView.setOnClickListener {
                    deleteItemClickListener?.onItemClick(holder)
                }
            }
        }
    }

    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
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
        captureBase64Str: String?,
    ){
        val captureBitMap = withContext(Dispatchers.IO) {
            BitmapTool.Base64UrlIconForHistory.decode(
                captureBase64Str
            )
        }
        withContext(Dispatchers.Main) {
            when (captureBitMap == null) {
                false -> {
                    holder.urlCaptureView.imageTintList = null
                    holder.urlCaptureView.load(captureBitMap)
                }

                else -> {
                    holder.urlCaptureView.load(fileMarkbitmap)
                }
            }
        }
    }

    private suspend fun setIcon(
        holder: UrlHistoryViewHolder,
        iconBase64Str: String?,
        urlStr: String,
    ){
        val fileType = FileType.values().firstOrNull {
            it.name == iconBase64Str
        }
        if(fileType != null) {
            val color = when(fileType){
                FileType.BOTTOM_FANNEL -> R.color.fannel_icon_color
                FileType.NORMAL_FANNEL -> R.color.orange
            }
            withContext(Dispatchers.Main) {
                holder.urlTitleImageView.load(R.drawable.icons8_file)
                holder.urlTitleImageView.imageTintList =
                    context?.getColorStateList(color)
            }
            return
        }
        val iconBitMap = withContext(Dispatchers.IO) {
            BitmapTool.Base64UrlIconForHistory.decode(
                iconBase64Str
            )
        }
        withContext(Dispatchers.Main) {
            if (
                iconBitMap != null
            ) {
                holder.urlTitleImageView.imageTintList = null
                holder.urlTitleImageView.load(iconBitMap)
                return@withContext
            }
            holder.urlTitleImageView.imageTintList =
                when(
                    EnableUrlPrefix.isHttpPrefix(urlStr)
                ) {
                    false -> {
                        holder.urlTitleImageView.load(R.drawable.icons8_file)
                        context?.getColorStateList(R.color.orange)
                    }
                    else -> {
                        holder.urlTitleImageView.load(R.drawable.internet)
                        context?.getColorStateList(R.color.web_icon_color)
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
