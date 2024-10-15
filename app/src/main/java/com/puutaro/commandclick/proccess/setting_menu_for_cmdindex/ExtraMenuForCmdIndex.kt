package com.puutaro.commandclick.proccess.setting_menu_for_cmdindex

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.broadcast.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.ExtraMenuGifCreator
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.JsDebugger
import com.puutaro.commandclick.proccess.EnableNavForWebView
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.EditFragmentArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object ExtraMenuForCmdIndex  {


    private var imageDialogObj: Dialog? = null
    private val shuujiColorList = listOf(
        R.color.teal_200,
        R.color.teal_700,
        R.color.fannel_icon_color,
        R.color.olive_green,
        R.color.terminal_color,
        R.color.file_dark_green_color,
        R.color.web_icon_color,
        R.color.icon_selected_color,
        R.color.setting_menu_footer,
        R.color.checked_item_color,
        R.color.ligthBlue,
        R.color.navy,
        R.color.light_ao,
        R.color.ao,
        R.color.white_green,
//        R.color.wine_red,
//        R.color.pink,
//        R.color.yellow,

        )
    private val shuujiColorList_bk = listOf(
        R.color.gray_out,
        R.color.file_dark_green_color,
        R.color.light_ao,
        R.color.gold_yellow,
        R.color.fannel_icon_color,
        R.color.olive_green,
        R.color.web_icon_color,
        R.color.ao,
//        R.color.yellow,
        R.color.terminal_color,
        R.color.brown,
        R.color.purple_200,
        R.color.file_icon_color,
        R.color.icon_selected_color,
        R.color.purple_700,
        R.color.wine_red,

    )

    private val darkShujiColorList = listOf(
        R.color.file_dark_green_color,
        R.color.file_dark_green_color,
        R.color.file_dark_green_color,
        R.color.gold_yellow,
        R.color.fannel_icon_color,
        R.color.olive_green,
        R.color.terminal_color,
        R.color.terminal_color,
        R.color.terminal_color,
        R.color.web_icon_color,
        R.color.ao,
        R.color.brown,
        R.color.purple_200,
        R.color.file_icon_color,
        R.color.purple_700,
        R.color.wine_red,
    )


    fun launch(
        fragment: Fragment
    ){
        val buttonLinearView = when(fragment){
             is CommandIndexFragment  -> fragment.binding.cmdindexSettingButton
             is TerminalFragment -> fragment.binding.termSettingButton
             else -> return
        }
        toolbarSettingButtonOnClick(
            fragment,
            buttonLinearView
        )
    }


    private fun toolbarSettingButtonOnClick(
        fragment: Fragment,
        settingButtonView: FrameLayout
    ){
        settingButtonView.setOnClickListener {
                settingButtonInnerView ->
            val context = fragment.context
                ?: return@setOnClickListener
            imageDialogObj = Dialog(
                context,
//                R.style.BottomSheetDialogTheme
                R.style.extraMenuDialogStyle,
            )
            imageDialogObj?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            imageDialogObj?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            imageDialogObj?.setContentView(
                R.layout.extra_popup_for_cmdindex
            )
            val constraintLayout = imageDialogObj?.findViewById<ConstraintLayout>(
                R.id.extra_popup_cmdindex_layout
            )
            val extraBitmapList = ExtraMenuGifCreator.extraMapBitmapList
            CoroutineScope(Dispatchers.Main).launch {
                val imageNum = 3
                val sameColor = null
                imageDialogObj?.findViewById<AppCompatImageView>(
                    R.id.extra_popup_cmdindex_fg3_image
                ).let {
                    setImageView(
                        context,
                        it,
                        extraBitmapList.shuffled().take(imageNum),
                        sameColor ?: shuujiColorList.random()
                    )
                }
                imageDialogObj?.findViewById<AppCompatImageView>(
                    R.id.extra_popup_cmdindex_fg2_image
                ).let {
                    setImageView(
                        context,
                        it,
                        extraBitmapList.shuffled().take(imageNum),
                        sameColor ?: shuujiColorList.random()
                    )
                }
                imageDialogObj?.findViewById<AppCompatImageView>(
                    R.id.extra_popup_cmdindex_fg1_image
                ).let {
                    setImageView(
                        context,
                        it,
                        extraBitmapList.shuffled().take(imageNum),
                        sameColor ?: shuujiColorList.random()
                    )
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                imageDialogObj?.findViewById<AppCompatImageView>(
                    R.id.extra_popup_cmdindex_nav_back_iamge_view
                )?.let {
                    ImageButtonSetterForCmdIndexPopup.set(
                        it,
                        CmdClickIcons.BACK,
                    )
                    withContext(Dispatchers.Main){
                        execSetNavImageButton (
                            fragment,
                            it,
                            ToolbarMenuCategoriesVariantForCmdIndex.BACK,
                            EnableNavForWebView.checkForGoBack(fragment),
                            constraintLayout,
                        )
                    }
                }
                imageDialogObj?.findViewById<AppCompatImageView>(
                    R.id.extra_popup_cmdindex_nav_reload_iamge_view
                )?.let {
                    ImageButtonSetterForCmdIndexPopup.set(
                        it,
                        CmdClickIcons.UPDATE,
                    )
                    withContext(Dispatchers.Main){
                        execSetNavImageButton (
                            fragment,
                            it,
                            ToolbarMenuCategoriesVariantForCmdIndex.RELOAD,
                            EnableNavForWebView.checkForReload(fragment),
                            constraintLayout,
                        )
                    }
                }
                imageDialogObj?.findViewById<AppCompatImageView>(
                    R.id.extra_popup_cmdindex_nav_forward_iamge_view
                )?.let {
                    ImageButtonSetterForCmdIndexPopup.set(
                        it,
                        CmdClickIcons.FORWARD,
                    )
                    withContext(Dispatchers.Main){
                        execSetNavImageButton (
                            fragment,
                            it,
                            ToolbarMenuCategoriesVariantForCmdIndex.FORWARD,
                            EnableNavForWebView.checkForGoForward(fragment),
                            constraintLayout,
                        )
                    }
                }
                imageDialogObj?.findViewById<AppCompatImageView>(
                    R.id.extra_popup_cmdindex_page_search_image
                )?.let {
                    ImageButtonSetterForCmdIndexPopup.set(
                        it,
                        CmdClickIcons.SEARCH,
                    )
                    withContext(Dispatchers.Main){
                        it.setOnClickListener {
                            exitDialog(constraintLayout)
                            when(fragment){
                                is CommandIndexFragment -> {
                                    val listener = fragment.context as? CommandIndexFragment.OnPageSearchSwitchListener
                                    listener?.onPageSearchSwitch()
                                }
                                is TerminalFragment -> {
                                    val listener = fragment.context as? TerminalFragment.OnPageSearchSwitchListenerForTerm
                                    listener?.onPageSearchSwitchForTerm()
                                }
                            }
                        }
                    }
                }
                imageDialogObj?.findViewById<AppCompatImageView>(
                    R.id.extra_popup_cmdindex_shortcut_image
                )?.let {
                    ImageButtonSetterForCmdIndexPopup.set(
                        it,
                        CmdClickIcons.SHORTCUT,
                    )
                    withContext(Dispatchers.Main){
                        it.setOnClickListener {
                            exitDialog(constraintLayout)
                            makeShortcut(fragment)
                        }
                    }
                }
                imageDialogObj?.findViewById<AppCompatImageView>(
                    R.id.extra_popup_cmdindex_restart_ubuntu_image
                )?.let {
                    ImageButtonSetterForCmdIndexPopup.set(
                        it,
                        CmdClickIcons.LAUNCH,
                    )
                    withContext(Dispatchers.Main){
                        it.setOnClickListener {
                            exitDialog(constraintLayout)
                            UbuntuServiceManager.launch(fragment.activity)
                        }
                    }
                }
                imageDialogObj?.findViewById<AppCompatImageView>(
                    R.id.extra_popup_cmdindex_debugger_image
                )?.let {
                    ImageButtonSetterForCmdIndexPopup.set(
                        it,
                        CmdClickIcons.DEBUG,
                    )
                    withContext(Dispatchers.Main){
                        it.setOnClickListener {
                            exitDialog(constraintLayout)
                            JsDebugger.sendDebugNoti(
                                fragment.context,
                                BroadCastIntentExtraForJsDebug.DebugGenre.JS_DEBUG.type,
                                BroadCastIntentExtraForJsDebug.NotiLevelType.HIGH.level,
                            )
                        }
                    }
                }
            }

            imageDialogObj?.setOnCancelListener {
                exitDialog(constraintLayout)
            }
            imageDialogObj?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            imageDialogObj?.window?.setGravity(Gravity.BOTTOM)
            imageDialogObj?.show()
        }
    }
    private fun setImageView(
        context: Context,
        imageView: AppCompatImageView?,
        bitmapList: List<Bitmap?>,
        colorId: Int,

    ){
        if(
            bitmapList.isEmpty()
        ) return
        val animation = AnimationDrawable()

        val rndList = (14..20)
        bitmapList.forEach {
            animation.addFrame(
                BitmapDrawable(context.resources, it),
                rndList.random() * 100
            )
        }
        animation.isOneShot = false
        imageView?.imageTintList =
            context.getColorStateList(colorId)
        imageView?.setImageDrawable(animation)
        animation.start()
    }

    private fun makeShortcut(
        fragment: Fragment
    ){
        val fannelInfoMap = when(fragment){
            is CommandIndexFragment -> fragment.fannelInfoMap
            is TerminalFragment -> fragment.fannelInfoMap
            else -> return
        }
        val listener =
            fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
        listener?.onToolbarMenuCategories(
            ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT,
            EditFragmentArgs(
                fannelInfoMap,
                EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
            )
        )
    }

    private fun exitDialog(constraintLayout: ConstraintLayout?){
        constraintLayout?.removeAllViews()
        imageDialogObj?.cancel()
        imageDialogObj = null
    }
    private fun execSetNavImageButton (
        fragment: Fragment,
        imageButton: AppCompatImageView,
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
        buttonEnable: Boolean,
        constraintLayout: ConstraintLayout?
    ){
        imageButton.setOnClickListener {
            exitDialog(constraintLayout)

            val fannelInfoMap = when(fragment){
                is CommandIndexFragment -> fragment.fannelInfoMap
                is TerminalFragment -> fragment.fannelInfoMap
                else -> return@setOnClickListener
            }
            val listener = fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
            listener?.onToolbarMenuCategories(
                toolbarMenuCategoriesVariantForCmdIndex,
                EditFragmentArgs(
                    fannelInfoMap,
                    EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
                )
            )
        }
        imageButton.isClickable = buttonEnable
        imageButton.isEnabled = buttonEnable
        val alpha = if(buttonEnable) 1f else 0.2f
        imageButton.alpha = alpha
    }
}

private object ImageButtonSetterForCmdIndexPopup {

    suspend fun set(
        imageView: AppCompatImageView?,
        icon: CmdClickIcons
    ){
        if(imageView == null) return
        val imageFile = ExecSetToolbarButtonImage.getImageFile(icon.assetsPath)
        if (
            !imageFile.isFile
        ) return
        execSetImageButton(
            imageView,
            icon
        )
    }
    private suspend fun execSetImageButton(
        imageButton: AppCompatImageView?,
        icon: CmdClickIcons,
    ) {
        if (imageButton == null) return
        val context = imageButton.context
        withContext(Dispatchers.Main) {
            imageButton.imageTintList = null
        }
        val imagePath =
            ExecSetToolbarButtonImage.getImageFile(icon.assetsPath).absolutePath
        val checksum = withContext(Dispatchers.IO) {
            FileSystems.checkSum(imagePath)
        }
        withContext(Dispatchers.Main) {
            val beforeChecksum =
                ExecSetToolbarButtonImage.TagManager.getChecksumFromTag(imageButton.tag)
            if (
                beforeChecksum == checksum
            ) return@withContext
            imageButton.tag = ExecSetToolbarButtonImage.TagManager.make(
                icon.str,
                checksum
            )
            val requestBuilder: RequestBuilder<Drawable> =
                Glide.with(context)
                    .asDrawable()
                    .sizeMultiplier(0.1f)
            Glide
                .with(context)
                .load(imagePath)
                .transition(DrawableTransitionOptions.withCrossFade())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .thumbnail(requestBuilder)
                .into(imageButton)
        }
    }
}