package com.puutaro.commandclick.proccess.setting_button

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.icon.CmdClickIcons
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.custom_view.NoScrollListView
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.ScriptFileEdit
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.AddScriptHandler
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.InstallFannelHandler
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.InstallFromFannelRepo
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.proccess.AppProcessManager
import com.puutaro.commandclick.proccess.EnableNavForWebView
import com.puutaro.commandclick.proccess.ExecSetTermSizeForCmdIndexFragment
import com.puutaro.commandclick.proccess.ExistTerminalFragment
import com.puutaro.commandclick.proccess.NoScrollUrlSaver
import com.puutaro.commandclick.proccess.SelectTermDialog
import com.puutaro.commandclick.proccess.TermRefresh
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithIndexListView
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.edit.lib.SettingFile
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.lib.VariationErrDialog
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CcScript
import com.puutaro.commandclick.util.CmdClickMap
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.RecordNumToMapNameValueInHolder
import com.puutaro.commandclick.util.ScriptPreWordReplacer
import com.puutaro.commandclick.util.SharePreffrenceMethod
import com.puutaro.commandclick.util.dialog.UsageDialog
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.io.File

class SettingButtonHandler(
    private val fragment: Fragment,
    readSharePreffernceMap: Map<String, String>,
) {

    var languageType = LanguageTypeSelects.JAVA_SCRIPT
    var languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
            languageType
        )
    var settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String

    var settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String
    private var menuPopupWindow: PopupWindow? = null

    private val context = fragment.context
    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )

    private val currentScriptFileName: String by lazy {
        val currentScriptFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )
        if(
            currentScriptFileName.isEmpty()
            || currentScriptFileName == "-"
            || currentScriptFileName == CommandClickScriptVariable.EMPTY_STRING
        ) return@lazy UsePath.cmdclickStartupJsName
        currentScriptFileName
    }

    val currentScriptContentsList = ReadText(
        currentAppDirPath,
        currentScriptFileName
    ).textToList()

    private val fannelDirName = CcPathTool.makeFannelDirName(currentScriptFileName)

    private val recordNumToMapNameValueInSettingHolder =
        RecordNumToMapNameValueInHolder.parse(
            currentScriptContentsList,
            settingSectionStart,
            settingSectionEnd,
            true,
            currentScriptFileName
        )
    private val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMap(
        recordNumToMapNameValueInSettingHolder,
        currentAppDirPath,
        fannelDirName,
        currentScriptFileName,
    )

    private val settingMenuMapList = createSettingMenuMapList()
    private val settingButtonConfigMap = createSettingButtonConfigMap()
    private val isSettingButtonSwitch: Boolean by lazy {
        if(settingButtonConfigMap.isNullOrEmpty())
            return@lazy false
        val switchKey = SettingButtonMapKey.SWITCH.str
        settingButtonConfigMap.filterKeys {
            it == switchKey
        }.isNotEmpty()
    }



    fun handle(
        isLongClickSrc: Boolean,
        settingButtonView: ImageButton?,
    ){
        val isLongClick = when(
            isSettingButtonSwitch
        ){
            true -> !isLongClickSrc
            else -> isLongClickSrc
        }
        when(isLongClick){
            true -> launchSettingMenu(
                settingButtonView,
            )
            else -> monitorSizeChange()
        }
    }

    private fun launchSettingMenu(
//        buttonInnerView: View,
        settingButtonView: ImageButton?,
    ){
        if(
            context == null
        ) return
        val existEditExecuteTerminalFragment = when(fragment) {
            is CommandIndexFragment ->
                ExistTerminalFragment
                    .how(
                        fragment,
                        context.getString(
                            R.string.index_terminal_fragment
                        )
                    )
            is EditFragment ->
                ExistTerminalFragment
                    .how(
                        fragment,
                        context.getString(
                            R.string.edit_execute_terminal_fragment
                        )
                    )
            else -> null
        }
        if(
            existEditExecuteTerminalFragment == null
        ){
            Toast.makeText(
                context,
                "no working",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        createPopUpForSetting(
//            buttonInnerView,
            context,
            settingButtonView
        )
    }

    private fun monitorSizeChange(){
        when(fragment){
            is EditFragment ->
                monitorSizeChangeForEdit(
                    fragment
                )
            is CommandIndexFragment -> {
                ExecSetTermSizeForCmdIndexFragment.execSetTermSizeForCmdIndexFragment(
                    fragment,
                )
            }
        }
    }

    private fun monitorSizeChangeForEdit(
        editFragment: EditFragment
    ){
        if(
            editFragment.terminalOn
            == SettingVariableSelects.TerminalDoSelects.OFF.name
        ) return
        val existEditExecuteTerminalFragment = ExistTerminalFragment
            .how(
                editFragment,
                editFragment.context?.getString(
                    R.string.edit_execute_terminal_fragment
                )
            )
        if(
            existEditExecuteTerminalFragment?.isVisible != true
        ) {
            Toast.makeText(
                context,
                "no terminal",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val linearLayoutParam =
            editFragment.binding.editFragment.layoutParams as LinearLayout.LayoutParams
        val isShow = linearLayoutParam.weight != ReadLines.LONGTH
        EditLayoutViewHideShow.exec(
            editFragment,
            isShow
        )
        val listener =
            context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
        listener?.onToolbarMenuCategoriesForEdit(
            ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX
        )
    }

    private fun createPopUpForSetting(
        settingButtonViewContext: Context,
        settingButtonView: ImageButton?
    ){
        if(settingButtonView == null) return
        val context = settingButtonView.context
        menuPopupWindow = PopupWindow(
            context,
        ).apply {
            elevation = 5f
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(null)
            animationStyle = R.style.popup_window_animation_phone
            val inflater = LayoutInflater.from(context)
            contentView = inflater.inflate(
                R.layout.setting_popup_for_index,
                LinearLayoutCompat(settingButtonViewContext),
                false
            ).apply {
                val menuListView =
                    this.findViewById<NoScrollListView>(
                        R.id.setting_menu_list_view
                    )
                val menuListMap = createPopupMenuListMap()
                val menuListAdapter = SubMenuAdapter(
                    settingButtonViewContext,
                    menuListMap.toMutableList()
                )
                menuListView.adapter = menuListAdapter
                menuListViewSetOnItemClickListener(menuListView)
                setNaviBarForEdit(this)
                measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
            }
        }.also { popupWindow ->
            // Absolute location of the anchor view
            val location = IntArray(2).apply {
                settingButtonView.getLocationOnScreen(this)
            }
            val size = Size(
                popupWindow.contentView.measuredWidth,
                popupWindow.contentView.measuredHeight
            )
            popupWindow.showAtLocation(
                settingButtonView,
                Gravity.TOP or Gravity.START,
                location[0] - (size.width - settingButtonView.width) / 2,
                location[1] - size.height
            )
        }
    }

    private fun createPopupMenuListMap(): List<Pair<String, Int>>{
        val parentMenuKey = SettingMenuMapKey.PARENT_MEMU.str
        return settingMenuMapList.filter {
            it?.get(parentMenuKey).isNullOrEmpty()
        }.let {
            execCreateMenuListMap(
                it
            )
        }
    }


    private fun menuListViewSetOnItemClickListener(
        menuListView: NoScrollListView
    ){
        val menuNameKey = SettingMenuMapKey.NAME.str
        val parentMenuNameKey = SettingMenuMapKey.PARENT_MEMU.str
        menuListView.setOnItemClickListener {
                parent, View, pos, id ->
            menuPopupWindow?.dismiss()
            val menuListAdapter = menuListView.adapter as SubMenuAdapter
            val clickedMenuName = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            val currentSettingMenuMap = settingMenuMapList.filter {
                it?.get(menuNameKey) == clickedMenuName
            }.firstOrNull()
            val onSubMenuLabel = !settingMenuMapList.filter {
                it?.get(parentMenuNameKey) == clickedMenuName
            }.firstOrNull().isNullOrEmpty()
//                currentSettingMenuMap?.get(parentMenuNameKey).isNullOrEmpty()
            FileSystems.writeFile(
                UsePath.cmdclickDefaultAppDirPath,
                "clickedMenuName.txt",
                "clickedMenuName: ${clickedMenuName}\n\n" +
                        "settingMenuMapList: ${settingMenuMapList.map { it.toString() }.joinToString("\n")}\n\n" +
                        "currentSettingMenuMap: ${currentSettingMenuMap}\n\n" +
                        "onSubMenuLabel: ${onSubMenuLabel}\n\n"
            )
            when(onSubMenuLabel) {
                true ->
                    SettingButtonSubMenuDialog.launch (
                        fragment,
                        currentAppDirPath,
                        currentScriptFileName,
                        settingMenuMapList,
                        clickedMenuName,
                    )
                else ->
                    jsPathHandler(
                        fragment,
                        currentAppDirPath,
                        currentScriptFileName,
                        clickedMenuName,
                        settingMenuMapList,
                    )

            }
        }
    }

    private fun createSettingMenuMapList(): List<Map<String, String>?> {
        val fannelDirName = CcPathTool.makeFannelDirName(currentScriptFileName)
        val settingMenuSettingFilePath = ScriptPreWordReplacer.replace(
            UsePath.settingMenuSettingFilePath,
            currentAppDirPath,
            fannelDirName,
            currentScriptFileName,
        )
        val settingMenuSettingFilePathObj = File(settingMenuSettingFilePath)
        val settingMenuMapCon = when(settingMenuSettingFilePathObj.isFile){
            true -> {
                val parentDirPath = settingMenuSettingFilePathObj.parent
                    ?: return emptyList()
                SettingFile.read(
                    parentDirPath,
                    settingMenuSettingFilePathObj.name
                )
            }
            else -> {
                SettingFile.formSettingContents(
                    makeSettingMenuConHandler(fragment).split("\n")
                )
            }
        }
        return makeSettingMenuMapList(
            settingMenuMapCon,
            fannelDirName,
        )
    }


    private fun setNaviBarForEdit(
        settingButtonInnerView: View
    ){
        execSetNavImageButtonForEdit(
            settingButtonInnerView,
            R.id.setting_menu_nav_back_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.BACK,
            EnableNavForWebView.checkForGoBack(fragment)
        )
        execSetNavImageButtonForEdit(
            settingButtonInnerView,
            R.id.setting_menu_nav_reload_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.RELOAD,
            EnableNavForWebView.checkForReload(fragment),
        )
        execSetNavImageButtonForEdit(
            settingButtonInnerView,
            R.id.setting_menu_nav_forward_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.FORWARD,
            EnableNavForWebView.checkForGoForward(fragment)
        )
    }

    private fun execSetNavImageButtonForEdit (
        settingButtonInnerView: View,
        buttonId: Int,
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
        buttonEnable: Boolean
    ){
        val navImageButton =
            settingButtonInnerView.findViewById<AppCompatImageButton>(
                buttonId
            )
        navImageButton.setOnClickListener {
            menuPopupWindow?.dismiss()
            when(fragment){
                is CommandIndexFragment -> {
                    val listener = context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
                    listener?.onToolbarMenuCategories(
                        toolbarMenuCategoriesVariantForCmdIndex
                    )
                }
                is EditFragment -> {
                    val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                    listener?.onToolbarMenuCategoriesForEdit(
                        toolbarMenuCategoriesVariantForCmdIndex
                    )
                }
            }
        }
        navImageButton.isEnabled = buttonEnable
        val colorId = if(buttonEnable) R.color.cmdclick_text_black else R.color.gray_out
        navImageButton.imageTintList = context?.getColorStateList(colorId)
    }


    private fun createSettingButtonConfigMap(): Map<String, String>? {
        val propertySeparator = ","
        val fannelDirName = CcPathTool.makeFannelDirName(currentScriptFileName)
        val settingMenuSettingFilePath = ScriptPreWordReplacer.replace(
            UsePath.settingButtonConfigPath,
            currentAppDirPath,
            fannelDirName,
            currentScriptFileName,
        )
        val settingMenuSettingFilePathObj = File(settingMenuSettingFilePath)
        return when (settingMenuSettingFilePathObj.isFile) {
            true -> {
                val parentDirPath = settingMenuSettingFilePathObj.parent
                    ?: return null
                SettingFile.read(
                    parentDirPath,
                    settingMenuSettingFilePathObj.name
                )
            }

            else -> {
                SettingFile.formSettingContents(
                    makeSettingButtonConfigConForEdit().split("\n")
                )
            }
        }.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                fannelDirName,
                currentScriptFileName
            )
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                fannelDirName,
                currentScriptFileName
            )
        }.split(propertySeparator).map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }.toMap()
    }

    private fun makeSettingMenuMapList(
        settingMenuMapCon: String,
        fannelDirName: String,
    ): List<Map<String, String>?> {
        val menuSeparator = ","
        val keySeparator = "|"
        return settingMenuMapCon.let {
            ScriptPreWordReplacer.replace(
                it,
                currentAppDirPath,
                fannelDirName,
                currentScriptFileName
            )
        }.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
                currentAppDirPath,
                fannelDirName,
                currentScriptFileName
            )
        }.split(menuSeparator).map {
            if(
                it.isEmpty()
            ) return@map mapOf()
            CmdClickMap.createMap(
                it,
                keySeparator
            ).toMap()
        }.filter {
            it.isNotEmpty()
        }
    }
}

object SettingButtonSubMenuDialog {

    private var settingButtonSubMenuDialog: Dialog? = null

    fun launch(
        fragment: Fragment,
        currentAppDirPath: String,
        currentScriptFileName: String,
        settingMenuMapList: List<Map<String, String>?>,
        parentMenuName: String,
    ){
        val context = fragment.context
            ?: return

        settingButtonSubMenuDialog = Dialog(
            context
        )
        settingButtonSubMenuDialog?.setContentView(
            R.layout.submenu_dialog
        )
        setListView(
            fragment,
            currentAppDirPath,
            currentScriptFileName,
            settingMenuMapList,
            parentMenuName,
        )
        setCancelListener()
        settingButtonSubMenuDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        settingButtonSubMenuDialog
            ?.window
            ?.setGravity(Gravity.BOTTOM)
        settingButtonSubMenuDialog?.show()

    }

    private fun setCancelListener(
    ){
        val cancelImageButton =
            settingButtonSubMenuDialog?.findViewById<ImageButton>(
                R.id.submenu_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            settingButtonSubMenuDialog?.dismiss()
        }
        settingButtonSubMenuDialog?.setOnCancelListener {
            settingButtonSubMenuDialog?.dismiss()
        }
    }

    private fun setListView(
        fragment: Fragment,
        currentAppDirPath: String,
        currentScriptFileName: String,
        settingMenuMapList: List<Map<String, String>?>,
        parentMenuName: String,
    ) {
        val context = fragment.context
            ?: return
        val subMenuListView =
            settingButtonSubMenuDialog?.findViewById<ListView>(
                R.id.sub_menu_list_view
            )
        val subMenuPairList = createPopupSubMenuListMap(
            settingMenuMapList,
            parentMenuName,
        )
        val subMenuAdapter = SubMenuAdapter(
            context,
            subMenuPairList.toMutableList()
        )
        subMenuListView?.adapter = subMenuAdapter
        subMenuItemClickListener(
            fragment,
            currentAppDirPath,
            currentScriptFileName,
            settingMenuMapList,
            subMenuListView
        )
    }

    private fun subMenuItemClickListener(
        fragment: Fragment,
        currentAppDirPath: String,
        currentScriptFileName: String,
        settingMenuMapList: List<Map<String, String>?>,
        subMenuListView: ListView?
    ){
        subMenuListView?.setOnItemClickListener {
                parent, view, position, id ->
            settingButtonSubMenuDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val clickedSubMenu = menuListAdapter.getItem(position)
                ?: return@setOnItemClickListener
            jsPathHandler(
                fragment,
                currentAppDirPath,
                currentScriptFileName,
                clickedSubMenu,
                settingMenuMapList,
            )
        }
    }

    private fun createPopupSubMenuListMap(
        settingMenuMapList: List<Map<String, String>?>,
        parentMenuName: String,
    ): List<Pair<String, Int>>{
        val parentMenuKey = SettingMenuMapKey.PARENT_MEMU.str
        return settingMenuMapList.filter {
            it?.get(parentMenuKey) == parentMenuName
        }.let {
            execCreateMenuListMap(
                it
            )
        }
    }
}

private fun makeSettingMenuConHandler(
    fragment: Fragment
): String {
    return when(fragment){
        is CommandIndexFragment ->
            makeSettingMenuConForCmdIndex()
        is EditFragment ->
            makeSettingMenuConForEdit()
        else -> String()
    }
}

private fun makeSettingMenuConForCmdIndex(): String {
    val menuNameKey = SettingMenuMapKey.NAME.str
    val iconKey = SettingMenuMapKey.ICON.str
    val jsPathKey = SettingMenuMapKey.JS_PATH.str
    val parentMenuKey = SettingMenuMapKey.PARENT_MEMU.str
    return """
        ${menuNameKey}=usage
            |${iconKey}=info
            |${jsPathKey}=${JsPathMacro.USAGE.name},
        ${menuNameKey}=edit startup
            |${iconKey}=edit_frame
            |${jsPathKey}=${JsPathMacro.EDIT_STARTUP.name},
        ${menuNameKey}=no scroll save url
            |${iconKey}=ok
            |${jsPathKey}=${JsPathMacro.NO_SCROLL_SAVE_URL.name},
        ${menuNameKey}=install fannel
            |${iconKey}=puzzle
            |${jsPathKey}=${JsPathMacro.INSTALL_FANNEL.name},
        ${menuNameKey}=scan QR
            |${iconKey}=qr
            |${jsPathKey}=${JsPathMacro.QR_SCAN.name},
        ${menuNameKey}=manage
            |${iconKey}=setup,
                ${menuNameKey}=refresh monitor
                    |${iconKey}=reflesh
                    |${jsPathKey}=${JsPathMacro.REFRESH_MONITOR.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=select monitor
                    |${iconKey}=file
                    |${jsPathKey}=${JsPathMacro.SELECT_MONITOR.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=restart ubuntu
                    |${iconKey}=launch
                    |${jsPathKey}=${JsPathMacro.RESTART_UBUNTU.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=js import manager
                    |${iconKey}=folda
                    |${jsPathKey}=${JsPathMacro.JS_IMPORT.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=add
                    |${iconKey}=plus
                    |${jsPathKey}=${JsPathMacro.ADD.name}
                    |${parentMenuKey}=manage,
        ${menuNameKey}=setting
            |${iconKey}=setting,
                ${menuNameKey}=app dir manager
                    |${iconKey}=setting
                    |${jsPathKey}=${JsPathMacro.APP_DIR_MANAGER.name}
                    |${parentMenuKey}=setting,
                ${menuNameKey}=create short cut
                    |${iconKey}=shortcut
                    |${jsPathKey}=${JsPathMacro.SHORTCUT.name}
                    |${parentMenuKey}=setting,
                ${menuNameKey}=termux setup
                    |${iconKey}=setup
                    |${jsPathKey}=${JsPathMacro.TERMUX_SETUP.name}
                    |${parentMenuKey}=setting,
                ${menuNameKey}=config
                    |${iconKey}=edit_frame
                    |${jsPathKey}=${JsPathMacro.CONFIG.name}
                    |${parentMenuKey}=setting,
    """.trimIndent()
}

private fun makeSettingMenuConForEdit(): String {
    val menuNameKey = SettingMenuMapKey.NAME.str
    val iconKey = SettingMenuMapKey.ICON.str
    val jsPathKey = SettingMenuMapKey.JS_PATH.str
    val parentMenuKey = SettingMenuMapKey.PARENT_MEMU.str
    return """
        ${menuNameKey}=kill
            |${iconKey}=cancel
            |${jsPathKey}=${JsPathMacro.KILL.name},
        ${menuNameKey}=usage
            |${iconKey}=info
            |${jsPathKey}=${JsPathMacro.USAGE.name},
        ${menuNameKey}=no scroll save url
            |${iconKey}=ok
            |${jsPathKey}=${JsPathMacro.NO_SCROLL_SAVE_URL.name},
        ${menuNameKey}=scan QR
            |${iconKey}=qr
            |${jsPathKey}=${JsPathMacro.QR_SCAN.name},
        ${menuNameKey}=manage
            |${iconKey}=setup,
                ${menuNameKey}=refresh monitor
                    |${iconKey}=reflesh
                    |${jsPathKey}=${JsPathMacro.REFRESH_MONITOR.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=select monitor
                    |${iconKey}=file
                    |${jsPathKey}=${JsPathMacro.SELECT_MONITOR.name}
                    |${parentMenuKey}=manage,
                ${menuNameKey}=restart ubuntu
                    |${iconKey}=launch
                    |${jsPathKey}=${JsPathMacro.RESTART_UBUNTU.name}
                    |${parentMenuKey}=manage,
        ${menuNameKey}=setting
            |${iconKey}=setting,
                ${menuNameKey}=create short cut
                    |${iconKey}=setting
                    |${jsPathKey}=${JsPathMacro.SHORTCUT.name}
                    |${parentMenuKey}=setting,
                ${menuNameKey}=termux setup
                    |${iconKey}=setup
                    |${jsPathKey}=${JsPathMacro.TERMUX_SETUP.name}
                    |${parentMenuKey}=setting,
                ${menuNameKey}=config
                    |${iconKey}=edit_frame
                    |${jsPathKey}=${JsPathMacro.CONFIG.name}
                    |${parentMenuKey}=setting,
    """.trimIndent()
}


private fun jsPathHandler(
    fragment: Fragment,
    currentAppDirPath: String,
    currentScriptFileName: String,
    menuName: String,
    settingMenuMapList: List<Map<String, String>?>,
){
    val context = fragment.context
    val menuNameKey = SettingMenuMapKey.NAME.str
    val jsPathKey = SettingMenuMapKey.JS_PATH.str
    val currentSettingMenuMap = settingMenuMapList.filter {
        it?.get(menuNameKey) == menuName
    }.firstOrNull()
    if(
        currentSettingMenuMap.isNullOrEmpty()
    ){
        LogSystems.stdWarn("${jsPathKey} key not found in settingMenuMapList: ${settingMenuMapList}")
        return
    }
    val jsPath = currentSettingMenuMap.get(jsPathKey)
    if(
        jsPath.isNullOrEmpty()
    ){
        LogSystems.stdWarn("${jsPathKey} not found in settingMenuMapList: ${settingMenuMapList}")
        return
    }
    val filterdJsPath = JsPathMacro.values().filter {
        it.name == jsPath
    }.firstOrNull()
    when(filterdJsPath != null){
        true -> jsPathMacroHandler(
            fragment,
            currentAppDirPath,
            currentScriptFileName,
            filterdJsPath
        )
        else -> ExecJsLoad.jsUrlLaunchHandler(
            fragment,
            JavaScriptLoadUrl.make(
                context,
                jsPath,
            ) ?: String()
        )
    }
}

private fun jsPathMacroHandler(
    fragment: Fragment,
    currentAppDirPath: String,
    currentScriptFileName: String,
    jsPathMacro: JsPathMacro,
){
    val sharedPref =  fragment.activity?.getPreferences(Context.MODE_PRIVATE)
    val terminalViewModel: TerminalViewModel by fragment.activityViewModels()
    when(jsPathMacro){
        JsPathMacro.KILL ->
            AppProcessManager.killDialog(
                fragment,
                currentAppDirPath,
                currentScriptFileName
            )
        JsPathMacro.USAGE ->
            UsageDialog.launch(
                fragment,
                currentAppDirPath,
            )
        JsPathMacro.NO_SCROLL_SAVE_URL ->
            NoScrollUrlSaver.save(
                fragment,
                currentAppDirPath,
                String()
            )
        JsPathMacro.QR_SCAN ->
            execQrScan(
                fragment,
                currentAppDirPath,
            )
        JsPathMacro.SHORTCUT -> {
            val listener = fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
            listener?.onToolbarMenuCategories(
                ToolbarMenuCategoriesVariantForCmdIndex.SHORTCUT
            )
        }
        JsPathMacro.TERMUX_SETUP -> {
            val listener = fragment.context as? CommandIndexFragment.OnToolbarMenuCategoriesListener
            listener?.onToolbarMenuCategories(
                ToolbarMenuCategoriesVariantForCmdIndex.TERMUX_SETUP
            )
        }
        JsPathMacro.CONFIG ->
            configEdit(fragment)
        JsPathMacro.REFRESH_MONITOR ->
            TermRefresh.refresh(
                terminalViewModel.currentMonitorFileName
            )
        JsPathMacro.SELECT_MONITOR ->
            SelectTermDialog.launch(fragment)
        JsPathMacro.RESTART_UBUNTU ->
            UbuntuServiceManager.launch(
                fragment.activity
            )
        JsPathMacro.INSTALL_FANNEL ->
            installFannelHandler(
                fragment,
                currentAppDirPath,
            )
        JsPathMacro.EDIT_STARTUP ->
            scriptFileEditForCmdIndex(
                fragment,
                currentAppDirPath,
            )
        JsPathMacro.ADD -> addScriptHandler(
            fragment,
            currentAppDirPath,
            sharedPref
        )
        JsPathMacro.JS_IMPORT -> SystemFannelLauncher.launch(
            fragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.jsImportManagerFannelName
        )
        JsPathMacro.APP_DIR_MANAGER -> SystemFannelLauncher.launch(
            fragment,
            UsePath.cmdclickSystemAppDirPath,
            UsePath.appDirManagerFannelName
        )
    }
}

private fun addScriptHandler(
    fragment: Fragment,
    currentAppDirPath: String,
    sharedPref: SharedPreferences?,
){
    when(fragment) {
        is CommandIndexFragment ->
            AddScriptHandler(
                fragment,
                sharedPref,
                currentAppDirPath,
            ).handle()
        is EditFragment -> {}
    }
}

private fun scriptFileEditForCmdIndex(
    fragment: Fragment,
    currentAppDirPath: String,
){
    when(fragment){
        is EditFragment -> {}
        is CommandIndexFragment
        -> ScriptFileEdit.edit(
            fragment,
            currentAppDirPath,
            UsePath.cmdclickStartupJsName,
        )
    }
}

private fun configEdit(
    fragment: Fragment
){
    val configDirPath = UsePath.cmdclickSystemAppDirPath
    val configShellName = UsePath.cmdclickConfigFileName
    CommandClickScriptVariable.makeConfigJsFile(
        configDirPath,
        configShellName
    )
    val shellContentsList = ReadText(
        configDirPath,
        configShellName
    ).textToList()
    val validateErrMessage = ValidateShell.correct(
        fragment,
        shellContentsList,
        configShellName
    )
    if(validateErrMessage.isNotEmpty()){
        val shellScriptPath = "${configDirPath}/${configShellName}"
        VariationErrDialog.show(
            fragment,
            shellScriptPath,
            validateErrMessage
        )
        return
    }
    val cmdclickConfigFileName = UsePath.cmdclickConfigFileName
    val sharedPref = fragment.activity?.getPreferences(Context.MODE_PRIVATE)
    SharePreffrenceMethod.putSharePreffrence(
        sharedPref,
        mapOf(
            SharePrefferenceSetting.current_app_dir.name
                    to UsePath.cmdclickSystemAppDirPath,
            SharePrefferenceSetting.current_script_file_name.name
                    to cmdclickConfigFileName,
            SharePrefferenceSetting.on_shortcut.name
                    to FragmentTagManager.Suffix.ON.name
        )
    )
    val cmdEditFragmentTag = FragmentTagManager.makeTag(
        FragmentTagManager.Prefix.cmdEditPrefix.str,
        UsePath.cmdclickSystemAppDirPath,
        cmdclickConfigFileName,
        FragmentTagManager.Suffix.ON.name
    )
    when(fragment){
        is CommandIndexFragment
        -> {
            val listener = fragment.context
                    as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
            listener?.onLongClickMenuItemsforCmdIndex(
                LongClickMenuItemsforCmdIndex.EDIT,
                cmdEditFragmentTag
            )
        }
        is EditFragment -> {
            val listener = fragment.context
                    as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
            listener?.onLongClickMenuItemsforCmdIndex(
                LongClickMenuItemsforCmdIndex.EDIT,
                cmdEditFragmentTag
            )
        }
    }
}


private fun makeSettingButtonConfigConForEdit(): String {
//    val switchKey = SettingButtonMapKey.SWITCH.str
//    val parentDirPathKey = SettingButtonMapKey.PARENT_DIR_PATH.str
    return String()
//    return """
//        ${switchKey}=,
//    """.trimIndent()
}

private fun installFannelHandler(
    fragment: Fragment,
    currentAppDirPath: String,
){
    when(fragment){
        is CommandIndexFragment
        -> InstallFannelHandler.handle(
            fragment,
            InstallFromFannelRepo(
                fragment,
                currentAppDirPath,
            )
        )
        is EditFragment -> {}
    }
}

private fun execQrScan(
    fragment: Fragment,
    currentAppDirPath: String,
){
    val activeCurrentDirPath = when(fragment){
        is EditFragment -> {
            val filterDirInWithListIndex = WithIndexListView.filterDir
            if (
                fragment.existIndexList
                && filterDirInWithListIndex.isNotEmpty()
            ) filterDirInWithListIndex
            else currentAppDirPath
        }
        else -> currentAppDirPath
    }
    QrScanner(
        fragment,
        activeCurrentDirPath,
    ).scanFromCamera()
}



private fun execCreateMenuListMap(
    srcMenuMapList: List<Map<String, String>?>
): List<Pair<String, Int>>{
    val menuNameKey = SettingMenuMapKey.NAME.str
    val iconKey = SettingMenuMapKey.ICON.str
    val ringIconId = CmdClickIcons.RING.id
    return srcMenuMapList.map {
        val iconMacroName = it?.get(iconKey)
        val menuName = it?.get(menuNameKey) ?: String()
        val iconId = CmdClickIcons.values().filter {
            it.str == iconMacroName
        }.firstOrNull()?.id ?: ringIconId
        menuName to iconId
    }.filter { it.first.isNotEmpty() }
}


private enum class SettingMenuMapKey(
    val str: String,
) {
    NAME("name"),
    ICON("icon"),
    JS_PATH("jsPath"),
    PARENT_MEMU("parentMenu"),
}


private enum class SettingButtonMapKey(
    val str: String,
) {
    SWITCH("switch"),
    PARENT_DIR_PATH("parentDirPath"),
}
private enum class JsPathMacro{
    KILL,
    USAGE,
    NO_SCROLL_SAVE_URL,
    QR_SCAN,
    SHORTCUT,
    TERMUX_SETUP,
    CONFIG,
    REFRESH_MONITOR,
    SELECT_MONITOR,
    RESTART_UBUNTU,
    INSTALL_FANNEL,
    EDIT_STARTUP,
    JS_IMPORT,
    ADD,
    APP_DIR_MANAGER,
}
