package com.puutaro.commandclick.component.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryPath
import com.puutaro.commandclick.proccess.pin.PinFannelManager
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FannelSettingMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class FannelManageAdapter(
    private val context: Context?,
    private val fannelInfoMap: Map<String, String>,
    var fannelNameList: MutableList<String>
    ): RecyclerView.Adapter<FannelManageAdapter.FannelManageViewHolder>(){

    private val fannelSettingInfoMap = FannelSettingMap.create()
    private val pinFannelList = PinFannelManager.get()
    private val homeFannel = SystemFannel.home
    private val switchOn = FannelSettingMap.switchOn


    companion object {

//        const val pinLimit = 6
        val pinExistColor = R.color.checked_item_color
        val buttonOrdinalyColor = R.color.file_dark_green_color
        val buttonGrayOutColor = R.color.gray_out
//        val editExecuteColor = R.color.fannel_icon_color
        val ordialyBkColor = R.color.setting_menu_footer
    }
    class FannelManageViewHolder(val view: View): RecyclerView.ViewHolder(view) {


        val fannelHistoryAdapterConstraintLayout = view.findViewById<ConstraintLayout>(R.id.fannel_history_adapter_constraint_layout)
//        val fannelHistoryAdapterRelativeLayout = view.findViewById<RelativeLayout>(R.id.fannel_history_adapter_relative_layout)
        val fannelCaptureView = view.findViewById<AppCompatImageView>(R.id.fannel_history_adapter_capture)
        val fannelNameTextView = view.findViewById<OutlineTextView>(R.id.fannel_history_name)
        val titleTextView = view.findViewById<OutlineTextView>(R.id.fannel_history_title)
//        val fannelHistoryAdapterBottomLinearInner = view.findViewById<LinearLayoutCompat>(R.id.fannel_history_adapter_bottom_linear_inner)
        val shareImageRelativeLayoutView = view.findViewById<RelativeLayout>(R.id.fannel_history_adapter_logo_relative_layout)
        val shareImageView = view.findViewById<AppCompatImageView>(R.id.fannel_history_adapter_icon)
        val pinImageButtonView = view.findViewById<AppCompatImageButton>(R.id.fannel_history_adapter_pin)
        val editImageButtonView = view.findViewById<AppCompatImageButton>(R.id.fannel_history_adapter_edit)
        val longPressImageButtonView = view.findViewById<AppCompatImageButton>(R.id.fannel_history_adapter_long_press)
    }

    private val intrudeGifByteArray = AssetsFileManager.assetsByteArray(
        context,
        AssetsFileManager.intrudeGifPath
    )

    private val isIndex = FannelInfoTool.isEmptyFannelName(
        FannelInfoTool.getCurrentFannelName(fannelInfoMap)
    )


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FannelManageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(
            R.layout.fannel_history_adapter_layout,
            parent,
            false
        )
        val fannelManageViewHolder = FannelManageViewHolder(itemView)
        itemView.setOnClickListener {
            itemClickListener?.onItemClick(fannelManageViewHolder)
        }
//        itemView.setOnLongClickListener {
//            itemLongClickListener?.onItemLongClick(itemView, fannelHistoryViewHolder)
//            true
//        }

        return fannelManageViewHolder
    }

    override fun getItemCount(): Int = fannelNameList.size

    override fun getItemId(position: Int): Long {
        //return super.getItemId(position)
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)
        return position
    }

    override fun onBindViewHolder(
        holder: FannelManageViewHolder,
        position: Int
    ) {
        val fannelName = fannelNameList[position]
        CoroutineScope(Dispatchers.IO).launch {
            holder.fannelNameTextView.revOutline(true)
            val settingMap = withContext(Dispatchers.IO) {
                fannelSettingInfoMap.get(
                   fannelName
                )
            }
            withContext(Dispatchers.Main) {
                val displayFannelName = when(
                    fannelName == homeFannel
                ){
                    true -> "HOME"
                    else -> CcPathTool.trimAllExtend(fannelName)
                }
                holder.fannelNameTextView.text = displayFannelName
//                when(
//                    settingMap?.get(
//                        FannelHistorySettingKey.ENABLE_EDIT_EXECUTE.key
//                    ) == switchOn
//                            || fannelName == homeFannel
//                ) {
//                    true -> holder.fannelNameTextView.setText(
//                        underlineSpannableFannelName(displayFannelName),
//                        TextView.BufferType.SPANNABLE
//                    )
//                    else -> holder.fannelNameTextView.text = displayFannelName
//                }

//                holder.appDirNameTextView.isVisible = false
            }
            withContext(Dispatchers.Main) {
                when(fannelName == homeFannel){
                    true -> {
                        holder.titleTextView.text = "\uD83C\uDFE0"
                        holder.titleTextView.textSize = 70f
                    }
                    else -> {
                        holder.titleTextView.text =
                            settingMap?.get(FannelSettingMap.FannelHistorySettingKey.TITLE.key)
                        holder.titleTextView.textSize = 22f
                    }
                }
            }
            withContext(Dispatchers.Main){
                if(
                    fannelName == homeFannel
                    || !isIndex
                ) {
                    holder.pinImageButtonView.isEnabled = false
                    holder.pinImageButtonView.imageTintList =
                        context?.getColorStateList(buttonGrayOutColor)
                    return@withContext
                }
                holder.pinImageButtonView.isEnabled = true
                if(
                    !pinFannelList.contains(fannelName)
                ) {
                    holder.pinImageButtonView.imageTintList =
                        context?.getColorStateList(buttonOrdinalyColor)
                    return@withContext
                }
                holder.pinImageButtonView.imageTintList =
                    context?.getColorStateList(pinExistColor)
            }
            withContext(Dispatchers.Main){
                if(
                    settingMap?.get(
                        FannelSettingMap.FannelHistorySettingKey.ENABLE_LONG_PRESS_BUTTON.key
                    ) != switchOn
                    || !isIndex
                ) {
                    holder.longPressImageButtonView.imageTintList =
                        context?.getColorStateList(buttonGrayOutColor)
                    holder.longPressImageButtonView.isEnabled = false
                    return@withContext
                }
                holder.longPressImageButtonView.imageTintList =
                    context?.getColorStateList(buttonOrdinalyColor)
                holder.longPressImageButtonView.isEnabled = true
            }
            withContext(Dispatchers.Main){
                val isNotHomeFannel = fannelName != homeFannel
                val disableEditSettingVals =
                    settingMap?.get(
                        FannelSettingMap.FannelHistorySettingKey.ENABLE_EDIT_SETTING_VALS.key
                ) != switchOn && isNotHomeFannel
                if(
                    disableEditSettingVals
                    || !isIndex
                ) {
                    holder.editImageButtonView.isEnabled = false
                    holder.editImageButtonView.imageTintList =
                        context?.getColorStateList(buttonGrayOutColor)
                    return@withContext
                }
                holder.editImageButtonView.isEnabled = true
                holder.editImageButtonView.imageTintList =
                    context?.getColorStateList(buttonOrdinalyColor)
            }
            withContext(Dispatchers.IO){
                val logoPngPath = listOf(
                    UsePath.fannelLogoPngPath,
                ).joinToString("/").let {
                    ScriptPreWordReplacer.replace(
                        it,
                        fannelName
                    )
                }
                withContext(Dispatchers.Main) setImage@{
                    val isEditExecute = settingMap?.get(
                        FannelSettingMap.FannelHistorySettingKey.ENABLE_EDIT_EXECUTE.key
                    ) == switchOn
                            || fannelName == homeFannel
                    setLogoBackground(
                        holder.shareImageRelativeLayoutView,
                        holder.shareImageView,
                        isEditExecute,
                    )
                    if(
                        fannelName == homeFannel
                    ){
                        setFannelLogo(holder.shareImageView)
                        return@setImage
                    }
                    if (
                        !File(logoPngPath).isFile
                    ) {
                        setFannelShareLogo(holder)
                        return@setImage
                    }
                    setFannelLogo(
                        holder.shareImageView,
                        logoPngPath,
                    )
                }
            }
            setCaptureImage(
                holder,
                FannelHistoryPath.getCaptureGifPath(
//                    File(UsePath.cmdclickAppDirPath, appDirName).absolutePath,
                    fannelName,
                ),
            )
            withContext(Dispatchers.IO){
//                val currentAppDirPath = File(
//                    FannelInfoTool.getCurrentAppDirPath(fannelInfoMap)
//                ).name
//                if(
//                    appDirName != currentAppDirPath
//                ) return@withContext
                val currentFannelName = FannelInfoTool.getCurrentFannelName(fannelInfoMap)
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "gFannelLit.txt").absolutePath,
//                    listOf(
//                        "currentFannelName: ${currentFannelName}",
//                        "fannelName: ${fannelName}",
//                    ).joinToString("\n\n") + "\n------\n"
//                )
                val isAppDir =
                    fannelName == homeFannel
//                    FannelInfoTool.isEmptyFannelName(fannelName)
                val isCurentAppDir =
                    FannelInfoTool.isEmptyFannelName(currentFannelName)
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
                holder.shareImageRelativeLayoutView.setOnClickListener {
                    shareItemClickListener?.onItemClick(holder)
                }
                holder.pinImageButtonView.setOnClickListener {
                    pinItemClickListener?.onItemClick(holder)
                }
                holder.longPressImageButtonView.setOnClickListener {
                    longPressItemClickListener?.onItemClick(holder)
                }
                holder.editImageButtonView.setOnClickListener {
                    editItemClickListener?.onItemClick(holder)
                }
            }
        }
    }

    var itemClickListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(holder: FannelManageViewHolder)
    }

//    var itemLongClickListener: OnItemLongClickListener? = null
//    interface OnItemLongClickListener {
//        fun onItemLongClick(itemView: View, holder: FannelHistoryViewHolder)
//    }

//    var deleteItemClickListener: OnDeleteItemClickListener? = null
//    interface OnDeleteItemClickListener {
//        fun onItemClick(holder: FannelManageViewHolder)
//    }

    var shareItemClickListener: OnShareItemClickListener? = null
    interface OnShareItemClickListener {
        fun onItemClick(holder: FannelManageViewHolder)
    }

    var pinItemClickListener: OnPinItemClickListener? = null
    interface OnPinItemClickListener {
        fun onItemClick(holder: FannelManageViewHolder)
    }


    var longPressItemClickListener: OnLongPressItemClickListener? = null
    interface OnLongPressItemClickListener {
        fun onItemClick(holder: FannelManageViewHolder)
    }

    var editItemClickListener: OnEditItemClickListener? = null
    interface OnEditItemClickListener {
        fun onItemClick(holder: FannelManageViewHolder)
    }

    private suspend fun setFocus(
        holder: FannelManageViewHolder
    ){
        withContext(Dispatchers.Main){
            val hitFannelColor = R.color.web_icon_color
            holder.fannelHistoryAdapterConstraintLayout.backgroundTintList =
                context?.getColorStateList(hitFannelColor)
//            holder.fannelHistoryAdapterBottomLinearInner.backgroundTintList =
//                context?.getColorStateList(hitFannelColor)
            holder.shareImageView.backgroundTintList = null
//                context?.getColorStateList(hitFannelColor)
            holder.pinImageButtonView.backgroundTintList =
                context?.getColorStateList(hitFannelColor)
            holder.editImageButtonView.backgroundTintList =
                context?.getColorStateList(hitFannelColor)
            holder.longPressImageButtonView.backgroundTintList =
                context?.getColorStateList(hitFannelColor)

        }
    }

    private suspend fun setCaptureImage(
        holder: FannelManageViewHolder,
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

    private fun setLogoBackground(
        shareImageRelativeLayoutView: RelativeLayout,
        shareImageView: AppCompatImageView,
        isEditExecute: Boolean,
    ){
        if(
            context == null
        ) return
        val logoBkColorId = when(isEditExecute){
            true -> buttonOrdinalyColor
            else -> ordialyBkColor
        }
        val logoImageColorId = when(isEditExecute){
            true -> ordialyBkColor
            else -> buttonOrdinalyColor
        }
        shareImageRelativeLayoutView.background =
            AppCompatResources.getDrawable(context, logoBkColorId)
        shareImageView.imageTintList =
            context.getColorStateList(logoImageColorId)
        shareImageView.backgroundTintList = null
//                                    it.getColorStateList(null)
        shareImageView.background =
            AppCompatResources.getDrawable(context, logoBkColorId)
    }

    private fun setFannelLogo(
        shareImageView: AppCompatImageView,
        logoPngPath: String,
    ){
//        holder.shareImageView.foregroundTintList = null
        shareImageView.imageTintList = null
//        holder.shareImageView.backgroundTintList = null
        val context = shareImageView.context
        val requestBuilder: RequestBuilder<Drawable> =
            Glide.with(context)
                .asDrawable()
                .sizeMultiplier(0.1f)
        Glide
            .with(context)
            .load(logoPngPath)
            .skipMemoryCache( true )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .thumbnail( requestBuilder )
            .into(shareImageView)
    }

    private fun setFannelShareLogo(
        holder: FannelManageViewHolder,
    ){
        val shareImageView = holder.shareImageView
//        holder.shareImageView.foregroundTintList = null
//        shareImageView.imageTintList = null
//        holder.shareImageView.backgroundTintList = null
        val context = shareImageView.context
        val requestBuilder: RequestBuilder<Drawable> =
            Glide.with(context)
                .asDrawable()
                .sizeMultiplier(0.1f)
        Glide
            .with(context)
            .load(R.drawable.icons_qr_code)
            .skipMemoryCache( true )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .thumbnail( requestBuilder )
            .into(shareImageView)
    }

    private fun setFannelLogo(
        shareImageView: AppCompatImageView,
    ){
//        holder.shareImageView.foregroundTintList = null
        shareImageView.imageTintList = null
//        holder.shareImageView.backgroundTintList = null
        val context = shareImageView.context
        val requestBuilder: RequestBuilder<Drawable> =
            Glide.with(context)
                .asDrawable()
                .sizeMultiplier(0.1f)
        Glide
            .with(context)
            .load(R.mipmap.ic_cmdclick_launcher_round)
            .skipMemoryCache( true )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .thumbnail( requestBuilder )
            .into(shareImageView)
    }

//    private fun underlineSpannableFannelName(
//        text: String
//    ): SpannableString {
//        val spannable = SpannableString(text)
//        context?.let {
//            spannable.setSpan(
//                DrawableSpan(AppCompatResources.getDrawable(context, R.drawable.text_underline)),
//                0,
//                text.length,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//        }
//        return spannable
//    }
}
