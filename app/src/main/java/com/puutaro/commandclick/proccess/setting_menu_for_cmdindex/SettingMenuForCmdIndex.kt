package com.puutaro.commandclick.proccess.setting_menu_for_cmdindex

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.broadcast.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.custom_view.NoScrollListView
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.ExtraMenuGifCreator
import com.puutaro.commandclick.fragment_lib.command_index_fragment.init.CmdClickSystemFannelManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver.JsDebugger
import com.puutaro.commandclick.proccess.EnableNavForWebView
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.proccess.tool_bar_button.SystemFannelLauncher
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.state.EditFragmentArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


object SettingMenuForCmdIndex {


    private var menuPopupWindow: PopupWindow? = null
    private val menuMapList = MenuEnumsForCmdIndex.values().toList().map{
        it.itemName to it.imageId
    }
    private var imageDialogObj: Dialog? = null
    private val shuujiColorList = listOf(
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
        settingButtonView: LinearLayoutCompat
    ){
        settingButtonView.setOnClickListener {
                settingButtonInnerView ->
            val context = fragment.context
                ?: return@setOnClickListener
            val settingButtonViewContext = settingButtonInnerView.context
            imageDialogObj = Dialog(
                context,
                R.style.extraMenuDialogStyle,
            )
            imageDialogObj?.requestWindowFeature(Window.FEATURE_NO_TITLE);
            imageDialogObj?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            imageDialogObj?.setContentView(
                R.layout.extra_popup_for_cmdindex
            )
            val constraintLayout = imageDialogObj?.findViewById<ConstraintLayout>(
                R.id.extra_popup_cmdindex_layout
            )
//            val extraMapDrawableList = when(fragment) {
//                is CommandIndexFragment -> fragment.extraMapBitmapList
//                is TerminalFragment -> fragment.extraMapBitmapList
//                else -> return@setOnClickListener
//            }
            val extraBitmapList = ExtraMenuGifCreator.extraMapBitmapList
            CoroutineScope(Dispatchers.Main).launch {
//                imageDialogObj?.findViewById<AppCompatImageView>(
//                    R.id.extra_popup_cmdindex_bk_image
//                ).let {
//                    setImageView(
//                        context,
//                        it,
//                        extraBitmapList
//                    )
//                }
                val imageNum = 3
                val sameColor =
                    when(
                        (1..5).random() % 5 <= 1
                    ){
                        true -> darkShujiColorList.random()
                        else -> null
                    }
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
//                val textBackground = withContext(Dispatchers.IO){
//                    AssetsFileManager.makeDrawable(
//                        context,
//                        AssetsFileManager.underLinePngPath
//                    )
//                }
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
                    R.id.extra_popup_cmdindex_scan_qr_image
                )?.let {
                    ImageButtonSetterForCmdIndexPopup.set(
                        it,
                        CmdClickIcons.QR,
                    )
                    withContext(Dispatchers.Main){
                        it.setOnClickListener {
                            exitDialog(constraintLayout)
                            QrScanner.scanFromCamera(fragment)
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
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            imageDialogObj?.window?.setGravity(Gravity.BOTTOM)
            imageDialogObj?.show()

//            menuPopupWindow = PopupWindow(
//                settingButtonView.context,
//            ).apply {
//                elevation = 5f
//                isFocusable = true
//                isOutsideTouchable = true
//                setBackgroundDrawable(null)
//                animationStyle = R.style.popup_window_animation_phone
//                val inflater = LayoutInflater.from(settingButtonView.context)
//                contentView = inflater.inflate(
//                    R.layout.extra_popup_for_cmdindex,
//                    ConstraintLayout(settingButtonViewContext),
//                    false
//                ).apply {
////                    val menuListView =
////                        this.findViewById<NoScrollListView>(
////                            R.id.setting_menu_list_view
////                        )
////                    val menuListAdapter = SubMenuAdapter(
////                        settingButtonViewContext,
////                        menuMapList.toMutableList()
////                    )
////                    menuListView.adapter = menuListAdapter
////                    menuListViewSetOnItemClickListener(
////                        fragment,
////                        menuListView
////                    )
////                    navButtonsSeter(
////                        fragment,
////                        this
////                    )
//                    val fgGifDirPath =  ExtraMenuGifCreator.fgGifDirPath
//                    val bitmapList = FileSystems.sortedFiles(
//                        fgGifDirPath
//                    ).map {
//                        BitmapTool.convertFileToBitmap(
//                            File(fgGifDirPath, it).absolutePath,
//                        )
//                    }
//                    val imageView = this.findViewById<AppCompatImageView>(R.id.extra_popup_cmdindex_image)
//                    val animation = AnimationDrawable()
//                    bitmapList.forEach {
//                        animation.addFrame(BitmapDrawable(context.resources, it), 80)
//                    }
//
////                    animation.addFrame(BitmapDrawable(context.resources, bitmap2), 50)
////                    animation.addFrame(BitmapDrawable(context.resources, bitmap3), 30)
//                    animation.isOneShot = false
//                    imageView.setImageDrawable(animation)
//                    animation.start()
//                    measure(
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//                    )
//                }
//            }.also { popupWindow ->
//                // Absolute location of the anchor view
//                val location = IntArray(2).apply {
//                    settingButtonView.getLocationOnScreen(this)
//                }
//                val size = Size(
//                    popupWindow.contentView.measuredWidth,
//                    popupWindow.contentView.measuredHeight
//                )
//                popupWindow.showAtLocation(
//                    settingButtonView,
//                    Gravity.TOP or Gravity.START,
//                    location[0] - (size.width - settingButtonView.width) / 2,
//                    location[1] - size.height
//                )
//            }
//            true
        }
    }
    private fun setImageView(
        context: Context,
        imageView: AppCompatImageView?,
        bitmapList: List<Bitmap?>,
        colorId: Int,

    ){
//        val bitmapList = FileSystems.sortedFiles(
//            inputDirPath
//        ).map {
//            BitmapTool.convertFileToBitmap(
//                File(inputDirPath, it).absolutePath,
//            )
//        }
        val animation = AnimationDrawable()

        val rndList = (14..20)
        bitmapList.forEach {
            animation.addFrame(
                BitmapDrawable(context.resources, it),
                rndList.random() * 100
            )
        }
//            animation.alpha = 0

//                    animation.addFrame(BitmapDrawable(context.resources, bitmap2), 50)
//                    animation.addFrame(BitmapDrawable(context.resources, bitmap3), 30)
        animation.isOneShot = false
//        imageView?.backgroundTintList = null
//        imageView?.imageTintList = null
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

//    private fun navButtonsSeter(
//        fragment: Fragment,
//        settingButtonInnerView: View
//    ){
//        execSetNavImageButton(
//            fragment,
//            settingButtonInnerView,
//            R.id.setting_menu_nav_back_iamge_view,
//            ToolbarMenuCategoriesVariantForCmdIndex.BACK,
//            EnableNavForWebView.checkForGoBack(fragment)
//        )
//        execSetNavImageButton(
//            fragment,
//            settingButtonInnerView,
//            R.id.setting_menu_nav_reload_iamge_view,
//            ToolbarMenuCategoriesVariantForCmdIndex.RELOAD,
//            EnableNavForWebView.checkForReload(fragment),
//        )
//        execSetNavImageButton(
//            fragment,
//            settingButtonInnerView,
//            R.id.setting_menu_nav_forward_iamge_view,
//            ToolbarMenuCategoriesVariantForCmdIndex.FORWARD,
//            EnableNavForWebView.checkForGoForward(fragment)
//        )
//    }

    private fun execSetNavImageButton (
        fragment: Fragment,
        imageButton: AppCompatImageView,
//        settingButtonInnerView: View,
//        buttonId: Int,
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
        buttonEnable: Boolean,
        constraintLayout: ConstraintLayout?
    ){
        val context = fragment.context
//        val navImageButton =
//            settingButtonInnerView.findViewById<AppCompatImageButton>(
//                buttonId
//            )
        imageButton.setOnClickListener {
            exitDialog(constraintLayout)
//            menuPopupWindow?.dismiss()
//            menuPopupWindow = null

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
//        navImageButton.isEnabled = buttonEnable
        val alpha = if(buttonEnable) 1f else 0.2f
        imageButton.alpha = alpha
//        val colorId = if(buttonEnable) R.color.cmdclick_text_black else R.color.gray_out
//        navImageButton.imageTintList = context?.getColorStateList(colorId)
    }

    private fun menuListViewSetOnItemClickListener(
        fragment: Fragment,
        menuListView: NoScrollListView
    ){
        menuListView.setOnItemClickListener {
                parent, View, pos, id ->
            menuPopupWindow?.dismiss()
            menuPopupWindow = null
            val menuListAdapter =
                menuListView.adapter as SubMenuAdapter
            when(menuListAdapter.getItem(pos)){
//                MenuEnums.INSTALL_FANNEL.itemName ->
//                    SystemFannelLauncher.launch(
//                        cmdIndexFragment,
////                        UsePath.cmdclickDefaultAppDirPath,
//                        SystemFannel.fannelRepoFannelName
//                    )
                MenuEnumsForCmdIndex.QR_SCAN.itemName ->
                    QrScanner.scanFromCamera(
                        fragment,
//                        currentAppDirPath,
                    )
//                MenuEnumsForCmdIndex.NO_SCROLL_SAVE_URL.itemName ->
//                    NoScrollUrlSaver.save(
//                        cmdIndexFragment,
////                        currentAppDirPath,
//                        String()
//                    )
//                MenuEnumsForCmdIndex.USAGE.itemName ->
//                    UsageDialog.launch(
//                        cmdIndexFragment,
//                    )
//                MenuEnumsForCmdIndex.EDIT_PREFERENCE.itemName ->
//                    preferenceEdit(fragment)
                MenuEnumsForCmdIndex.MANAGE.itemName ->
                    ManageSubMenuDialog.launch(
                        fragment,
//                        currentAppDirPath
                    )
                MenuEnumsForCmdIndex.SETTING.itemName ->
                    SettingSubMenuDialog.launch(fragment)
            }
        }
    }

    private fun preferenceEdit(
        cmdIndexFragment: CommandIndexFragment
    ){
        val context = cmdIndexFragment.context
        runBlocking {
            CmdClickSystemFannelManager.createPreferenceFannel(
                context,
            )
        }
        SystemFannelLauncher.launch(
            cmdIndexFragment,
//            currentAppDirPath,
            SystemFannel.preference,
        )
    }
}

private enum class MenuEnumsForCmdIndex(
    val itemName: String,
    val imageId: Int,
) {
    //    INSTALL_FANNEL("Install fannel", R.drawable.icons8_puzzle),
//    USAGE("Usage", R.drawable.icons8_info),
//    EDIT_PREFERENCE("Preference", R.drawable.icons8_setup),
//    NO_SCROLL_SAVE_URL("No scroll save url", R.drawable.icons8_check_ok),
    QR_SCAN("Scan QR", R.drawable.icons_qr_code),
    MANAGE("Manage", R.drawable.icons8_setup),
    SETTING("Setting", R.drawable.icons8_setting),
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