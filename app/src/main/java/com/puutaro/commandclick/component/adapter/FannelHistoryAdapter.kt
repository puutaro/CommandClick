package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryPath
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryManager
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class FannelHistoryAdapter(
    private val context: Context?,
    private val fannelInfoMap: Map<String, String>,
    var historyList: MutableList<String>
    ): RecyclerView.Adapter<FannelHistoryAdapter.FannelHistoryViewHolder>(){

    class FannelHistoryViewHolder(val view: View): RecyclerView.ViewHolder(view) {


        val fannelHistoryAdapterLinearLayout = view.findViewById<LinearLayoutCompat>(R.id.fannel_history_adapter_linear_layout)
        val fannelHistoryAdapterRelativeLayout = view.findViewById<RelativeLayout>(R.id.fannel_history_adapter_relative_layout)
        val fannelCaptureView = view.findViewById<AppCompatImageView>(R.id.fannel_history_adapter_capture)
        val appDirNameTextView = view.findViewById<OutlineTextView>(R.id.fannel_history_app_dir)
        val fannelNameTextView = view.findViewById<OutlineTextView>(R.id.fannel_history_name)
        val fannelHistoryAdapterBottomLinearInner = view.findViewById<LinearLayoutCompat>(R.id.fannel_history_adapter_bottom_linear_inner)
        val shareImageButtonView = view.findViewById<AppCompatImageButton>(R.id.fannel_history_adapter_share)
        val deleteImageButtonView = view.findViewById<AppCompatImageButton>(R.id.fannel_history_adapter_delete)
    }

    private val intrudeGifByteArray = AssetsFileManager.assetsByteArray(
        context,
        AssetsFileManager.intrudeGifPath
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FannelHistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            R.layout.fannel_history_adapter_layout,
            parent,
            false
        )
        val fannelHistoryViewHolder = FannelHistoryViewHolder(itemView)
        itemView.setOnClickListener {
            itemClickListener?.onItemClick(fannelHistoryViewHolder)
        }
//        itemView.setOnLongClickListener {
//            itemLongClickListener?.onItemLongClick(itemView, fannelHistoryViewHolder)
//            true
//        }

        return fannelHistoryViewHolder
    }

    override fun getItemCount(): Int = historyList.size

    override fun getItemId(position: Int): Long {
        //return super.getItemId(position)
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)
        return position
    }

    override fun onBindViewHolder(
        holder: FannelHistoryViewHolder,
        position: Int
    ) {
        val historyLine = historyList[position]
        CoroutineScope(Dispatchers.IO).launch {
            holder.appDirNameTextView.revOutline(true)
            val appDirName = withContext(Dispatchers.IO){
                FannelHistoryManager.getAppDirNameFromAppHistoryFileName(
                    historyLine
                )
            }
            val fannelName = withContext(Dispatchers.IO){
                FannelHistoryManager.getFannelNameFromAppHistoryFileName(
                    historyLine
                )
            }
            withContext(Dispatchers.Main) {
                holder.appDirNameTextView.text = appDirName
//                holder.appDirNameTextView.isVisible = false
            }
            withContext(Dispatchers.Main) {
                holder.fannelNameTextView.text = fannelName
            }
            setCaptureImage(
                holder,
                FannelHistoryPath.getCaptureGifPath(
                    File(UsePath.cmdclickAppDirPath, appDirName).absolutePath,
                    fannelName,
                ),
            )
            withContext(Dispatchers.IO){
                val currentAppDirPath = File(
                    FannelInfoTool.getCurrentAppDirPath(fannelInfoMap)
                ).name
                if(
                    appDirName != currentAppDirPath
                ) return@withContext
                val currentFannelName = FannelInfoTool.getCurrentFannelName(fannelInfoMap)
                FileSystems.updateFile(
                    File(UsePath.cmdclickDefaultAppDirPath, "gFannelLit.txt").absolutePath,
                    listOf(
                        "currentAppDirPath: ${currentAppDirPath}",
                        "appDirName: ${appDirName}",
                        "currentFannelName: ${currentFannelName}",
                        "fannelName: ${fannelName}",
                    ).joinToString("\n\n") + "\n------\n"
                )
                val isAppDir = fannelName.isEmpty()
                        || fannelName == FannelInfoSetting.current_fannel_name.defalutStr
                val isCurentAppDir = currentFannelName.isEmpty()
                        || currentFannelName == FannelInfoSetting.current_fannel_name.defalutStr
                if(
                    isAppDir && isCurentAppDir
                ){
                    setFocus(holder)
                    return@withContext
                }
                if(
                    fannelName == currentFannelName
                ) setFocus(holder)
            }
            withContext(Dispatchers.Main){
                holder.shareImageButtonView.setOnClickListener {
                    shareItemClickListener?.onItemClick(holder)
                }
                holder.deleteImageButtonView.setOnClickListener {
                    deleteItemClickListener?.onItemClick(holder)
                }
            }
        }
    }

    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(holder: FannelHistoryViewHolder)
    }

//    var itemLongClickListener: OnItemLongClickListener? = null
//    interface OnItemLongClickListener {
//        fun onItemLongClick(itemView: View, holder: FannelHistoryViewHolder)
//    }

    var deleteItemClickListener: OnDeleteItemClickListener? = null
    interface OnDeleteItemClickListener {
        fun onItemClick(holder: FannelHistoryViewHolder)
    }

    var shareItemClickListener: OnShareItemClickListener? = null
    interface OnShareItemClickListener {
        fun onItemClick(holder: FannelHistoryViewHolder)
    }

    private suspend fun setFocus(
        holder: FannelHistoryViewHolder
    ){
        withContext(Dispatchers.Main){
            val hitFannelColor = R.color.web_icon_color
            holder.fannelHistoryAdapterLinearLayout.backgroundTintList =
                context?.getColorStateList(hitFannelColor)
            holder.fannelHistoryAdapterBottomLinearInner.backgroundTintList =
                context?.getColorStateList(hitFannelColor)
            holder.shareImageButtonView.backgroundTintList =
                context?.getColorStateList(hitFannelColor)
            holder.deleteImageButtonView.backgroundTintList =
                context?.getColorStateList(hitFannelColor)

        }
    }

    private suspend fun setCaptureImage(
        holder: FannelHistoryViewHolder,
        capturePngPathOrMacro: String?,
    ){
        val urlCaptureView = holder.fannelCaptureView
        val context = urlCaptureView.context
        val isFile = !capturePngPathOrMacro.isNullOrEmpty()
                && File(capturePngPathOrMacro).isFile
        withContext(Dispatchers.Main) {
            when (isFile) {
                true -> {
                    holder.fannelCaptureView.imageTintList = null
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
                    Glide
                        .with(holder.fannelCaptureView.context)
                        .load(intrudeGifByteArray)
                        .into(holder.fannelCaptureView)
                }
            }
        }
    }
}
