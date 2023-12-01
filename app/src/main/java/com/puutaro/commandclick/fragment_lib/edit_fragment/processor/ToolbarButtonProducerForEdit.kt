package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.content.Context
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.custom_view.NoScrollListView
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditLayoutViewHideShow
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.lib.SubMenuDialogForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.*
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.intent.ExecJsOrSellHandler
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class ToolbarButtonProducerForEdit(
    private val binding: EditFragmentBinding,
    private val editFragment: EditFragment,
    private val enableCmdEdit: Boolean,
) {

    private val context = editFragment.context
    private val cmdWebSearchEditText = binding.webSearch.cmdWebSearchEditText
    private val insertImageButtonParam = LinearLayout.LayoutParams(
        0,
        LinearLayout.LayoutParams.MATCH_PARENT,
    )
    private var menuPopupWindow: PopupWindow? = null
    private val readSharePreffernceMap = editFragment.readSharePreffernceMap
    private val terminalViewModel: TerminalViewModel by editFragment.activityViewModels()
    private val sharedPref =  editFragment.activity?.getPreferences(Context.MODE_PRIVATE)
    private val urlHistoryButtonEvent = UrlHistoryButtonEvent(
        editFragment,
        readSharePreffernceMap,
    )

    private val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_app_dir
    )

    private val currentScriptFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
        readSharePreffernceMap,
        SharePrefferenceSetting.current_script_file_name
    )

    private val scriptFileSaver = ScriptFileSaver(
        binding,
        editFragment,
        readSharePreffernceMap,
    )
    private val menuListMap = MenuEnumsForEdit.values().map {
        it.itemName to it.imageId
    }

    fun make(
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null,
        shellContentsList: List<String> = listOf(),
        editExecuteValue :String = SettingVariableSelects.EditExecuteSelects.NO.name,
        setDrawble: Int? = null
    ) {
        insertImageButtonParam.weight = 1F
        val makeButtonView = ImageButton(context)
        makeButtonView.imageTintList =
            context?.getColorStateList(R.color.terminal_color)
        if (setDrawble == null) {
            makeButtonView.setImageResource(
                toolbarButtonBariantForEdit.drawbleIconInt
            )
        } else {
            makeButtonView.setImageResource(
                setDrawble
            )
        }
        makeButtonView.backgroundTintList =
            context?.getColorStateList(R.color.white)
        makeButtonView.layoutParams = insertImageButtonParam
        makeButtonView.tag = toolbarButtonBariantForEdit.str

        makeButtonView.setOnLongClickListener {
                buttonInnerView ->
            onLongClickHandler(
                buttonInnerView,
                makeButtonView,
                toolbarButtonBariantForEdit,
                shellContentsList,
                recordNumToMapNameValueInCommandHolder,
                recordNumToMapNameValueInSettingHolder,
            )
            true
        }

        makeButtonView.setOnClickListener { view ->
            when (toolbarButtonBariantForEdit) {
                ToolbarButtonBariantForEdit.HISTORY -> {
                    val editExecuteTerminalFragment = TargetFragmentInstance()
                        .getFromFragment<TerminalFragment>(
                            editFragment.activity,
                            context?.getString(R.string.edit_execute_terminal_fragment)
                        )
                    if(
                        editExecuteTerminalFragment != null
                    ){
                        val listener = context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
                        listener?.onKeyBoardVisibleChangeForEditFragment(
                            false,
                            true
                        )
                    }
                    HistoryBottunSwitcher.switch(
                        editFragment,
                        view,
                        editFragment.context?.getString(
                            R.string.edit_execute_terminal_fragment
                        ),
                        readSharePreffernceMap,
                        editFragment.historySwitch,
                        urlHistoryButtonEvent,
                        sharedPref,
                        CLICLTYPE.SHORT
                    )
                    return@setOnClickListener
                }
                ToolbarButtonBariantForEdit.OK -> {
                    execForOk(
                        shellContentsList,
                        recordNumToMapNameValueInCommandHolder,
                        recordNumToMapNameValueInSettingHolder,
                        toolbarButtonBariantForEdit,
                        editExecuteValue,
                        enableCmdEdit
                    )
                    return@setOnClickListener
                }
                ToolbarButtonBariantForEdit.SETTING -> {
                    if(
                        editFragment.terminalOn
                        == SettingVariableSelects.TerminalDoSelects.OFF.name
                    ) return@setOnClickListener
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
                            view.context,
                            "no terminal",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    val onShorth = terminalViewModel.readlinesNum == ReadLines.SHORTH
                    EditLayoutViewHideShow.exec(
                        editFragment,
                        !onShorth
                    )
                    val listener =
                        context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                    listener?.onToolbarMenuCategoriesForEdit(
                        ToolbarMenuCategoriesVariantForCmdIndex.TERMMAX
                    )
                    return@setOnClickListener
                }
                ToolbarButtonBariantForEdit.EDIT -> {}
                else -> {
                    println("pass")
                }
            }
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                toolbarButtonBariantForEdit,
                readSharePreffernceMap,
                enableCmdEdit
            )
        }

        binding.editToolbarLinearLayout.addView(makeButtonView)
    }

    private fun onLongClickHandler(
        buttonInnerView: View,
        makeButtonView: ImageButton,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
    ){
        when (toolbarButtonBariantForEdit) {
            ToolbarButtonBariantForEdit.HISTORY -> {
                HistoryBottunSwitcher.switch(
                    editFragment,
                    buttonInnerView,
                    editFragment.context?.getString(
                        R.string.edit_execute_terminal_fragment
                    ),
                    readSharePreffernceMap,
                    editFragment.historySwitch,
                    urlHistoryButtonEvent,
                    sharedPref,
                    CLICLTYPE.LONG
                )
            }
            ToolbarButtonBariantForEdit.SETTING -> {
                val existEditExecuteTerminalFragment = ExistTerminalFragment
                    .how(
                        editFragment,
                        editFragment.context?.getString(
                            R.string.edit_execute_terminal_fragment
                        )
                    )
                if(
                    existEditExecuteTerminalFragment == null
                ){
                    Toast.makeText(
                        buttonInnerView.context,
                        "no working",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                createPopUpForSetting(
                    buttonInnerView,
                    makeButtonView
                )
            }
            ToolbarButtonBariantForEdit.OK -> {
                try {
                    execForOkLongClick(
                        shellContentsList,
                        recordNumToMapNameValueInCommandHolder,
                        recordNumToMapNameValueInSettingHolder,
                        editFragment.existIndexList
                    )
                } catch(e: Exception){
                    Toast.makeText(
                        context,
                        e.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            ToolbarButtonBariantForEdit.EDIT -> {
                execForEditLongClick(
                    shellContentsList,
                    recordNumToMapNameValueInCommandHolder,
                    recordNumToMapNameValueInSettingHolder,
                    editFragment.existIndexList
                )
            }
            else -> {}
        }
    }

    private fun execForOkLongClick(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
        existIndexList: Boolean
    ) {
        if(!existIndexList) {
            execScriptSave(
                shellContentsList,
                recordNumToMapNameValueInCommandHolder,
                recordNumToMapNameValueInSettingHolder,
            )
        }

        editFragment.execPlayBtnLongPress = replaceLongPressMacroInToolBar(
            shellContentsList,
            editFragment.execPlayBtnLongPress,
        )

        EditToolbarSwitcher.switch(
            editFragment,
            editFragment.execPlayBtnLongPress
        )
    }

    private fun execForEditLongClick(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
        existIndexList: Boolean
    ) {
        if(!existIndexList) {
            execScriptSave(
                shellContentsList,
                recordNumToMapNameValueInCommandHolder,
                recordNumToMapNameValueInSettingHolder,
            )
        }
        editFragment.execEditBtnLongPress = replaceLongPressMacroInToolBar(
            shellContentsList,
            editFragment.execEditBtnLongPress,
        )
        EditToolbarSwitcher.switch(
            editFragment,
            editFragment.execEditBtnLongPress
        )
    }

    private fun replaceLongPressMacroInToolBar(
        shellContentsList: List<String>,
        execLongPressMacro: String,
    ): String {
        val fannelDirName = CcPathTool.makeFannelDirName(currentScriptFileName)
        val recordNumToMapNameValueInSettingHolderForLongPress = RecordNumToMapNameValueInHolder.parse(
            shellContentsList,
            editFragment.settingSectionStart,
            editFragment.settingSectionEnd,
            true,
            currentScriptFileName
        )
        val setReplaceVariableMap = SetReplaceVariabler.makeSetReplaceVariableMap(
            recordNumToMapNameValueInSettingHolderForLongPress,
            currentAppDirPath,
            fannelDirName,
            currentScriptFileName
        )
        return SetReplaceVariabler.execReplaceByReplaceVariables(
            execLongPressMacro,
            setReplaceVariableMap,
            currentAppDirPath,
            fannelDirName,
            currentScriptFileName
        )
    }
    private fun execScriptSave(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
    ){
        if(
            editFragment.tag?.startsWith(
                FragmentTagManager.Prefix.cmdEditPrefix.str
            ) != true
            || editFragment.tag?.endsWith(
                FragmentTagManager.Suffix.ON.name
            ) != true
        ) return
        scriptFileSaver.save(
            shellContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )
    }

    private fun execForOk(
        shellContentsList: List<String>,
        recordNumToMapNameValueInCommandHolder:  Map<Int, Map<String, String>?>?,
        recordNumToMapNameValueInSettingHolder:  Map<Int, Map<String, String>?>?,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        editExecuteValue: String,
        enableCmdEdit: Boolean
    ){
        val onPassCmdVariableEdit =
            editFragment.passCmdVariableEdit ==
                    CommandClickScriptVariable.PASS_CMDVARIABLE_EDIT_ON_VALUE

        scriptFileSaver.save(
            shellContentsList,
            recordNumToMapNameValueInCommandHolder,
            recordNumToMapNameValueInSettingHolder,
        )


        val EditExecuteAlways =
            SettingVariableSelects.EditExecuteSelects.ALWAYS.name
        val EditExecuteOnce =
            SettingVariableSelects.EditExecuteSelects.ONCE.name
        val shortcutValue = FragmentTagManager.makeListFromTag(
            editFragment.tag
                ?: String()
        ).getOrNull(FragmentTagManager.modeIndex)
            ?: String()
        val onShortcut = shortcutValue != SharePrefferenceSetting.on_shortcut.defalutStr
                && shortcutValue.isNotEmpty()
        if(
            editExecuteValue == EditExecuteAlways
            && enableCmdEdit
            && onShortcut
            && !onPassCmdVariableEdit
        ) {
            Keyboard.hiddenKeyboardForFragment(
                editFragment
            )
            TerminalShowByTerminalDo.show(
                editFragment,
                shellContentsList
            )
            ExecJsOrSellHandler.handle(
                editFragment,
                currentAppDirPath,
                currentScriptFileName,
            )
            return
        }
        if(
            (editExecuteValue == EditExecuteAlways)
            == !enableCmdEdit
            && onShortcut
        ){
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                ToolbarButtonBariantForEdit.HISTORY,
                readSharePreffernceMap,
                enableCmdEdit
            )
            return
        }
        if(
            editExecuteValue == EditExecuteOnce
        ) {
            terminalViewModel.editExecuteOnceCurrentShellFileName = currentScriptFileName
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                toolbarButtonBariantForEdit,
                readSharePreffernceMap,
                enableCmdEdit,
            )
            return
        }

        if(onPassCmdVariableEdit){
            val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
            listener?.onToolBarButtonClickForEditFragment(
                editFragment.tag,
                ToolbarButtonBariantForEdit.CANCEL,
                readSharePreffernceMap,
                enableCmdEdit
            )
            return
        }


        val listener = this.context as? EditFragment.onToolBarButtonClickListenerForEditFragment
        listener?.onToolBarButtonClickForEditFragment(
            editFragment.tag,
            toolbarButtonBariantForEdit,
            readSharePreffernceMap,
            enableCmdEdit
        )
    }

    private fun createPopUpForSetting(
        buttonInnerView: View,
        settingButtonView: ImageButton
    ){
        val settingButtonViewContext = buttonInnerView.context
        menuPopupWindow = PopupWindow(
            settingButtonView.context,
        ).apply {
            elevation = 5f
            isFocusable = true
            isOutsideTouchable = true
            setBackgroundDrawable(null)
            animationStyle = R.style.popup_window_animation_phone
            val inflater = LayoutInflater.from(settingButtonView.context)
            contentView = inflater.inflate(
                R.layout.setting_popup_for_index,
                LinearLayoutCompat(settingButtonViewContext),
                false
            ).apply {
                val menuListView =
                    this.findViewById<NoScrollListView>(
                        R.id.setting_menu_list_view
                    )
                val menuListAdapter = SubMenuAdapter(
                    settingButtonViewContext,
                    menuListMap.toMutableList()
                )
                menuListView.adapter = menuListAdapter
                menuListViewSetOnItemClickListener(menuListView)
                navButtonsSeter(this)
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

    private fun navButtonsSeter(
        settingButtonInnerView: View
    ){
        execSetNavImageButton(
            settingButtonInnerView,
            R.id.setting_menu_nav_back_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.BACK,
            EnableNavForWebView.checkForGoBack(editFragment)
        )
        execSetNavImageButton(
            settingButtonInnerView,
            R.id.setting_menu_nav_reload_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.RELOAD,
            EnableNavForWebView.checkForReload(editFragment),
        )
        execSetNavImageButton(
            settingButtonInnerView,
            R.id.setting_menu_nav_forward_iamge_view,
            ToolbarMenuCategoriesVariantForCmdIndex.FORWARD,
            EnableNavForWebView.checkForGoForward(editFragment)
        )
    }

    private fun execSetNavImageButton (
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
            val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
            listener?.onToolbarMenuCategoriesForEdit(
                toolbarMenuCategoriesVariantForCmdIndex
            )
        }
        navImageButton.isEnabled = buttonEnable
        val colorId = if(buttonEnable) R.color.cmdclick_text_black else R.color.gray_out
        navImageButton.imageTintList = context?.getColorStateList(colorId)
    }

    private fun menuListViewSetOnItemClickListener(
        menuListView: NoScrollListView
    ){
        menuListView.setOnItemClickListener {
                parent, View, pos, id ->
            menuPopupWindow?.dismiss()
            val menuListAdapter = menuListView.adapter as SubMenuAdapter
            when(menuListAdapter.getItem(pos)){
                MenuEnumsForEdit.SETTING.itemName -> {
                    SubMenuDialogForEdit.launch(
                        editFragment,
                    )
                }
                MenuEnumsForEdit.NO_SCROLL_SAVE_URL.itemName -> {
                    NoScrollUrlSaver.save(
                        editFragment,
                        currentAppDirPath,
                        String()
                    )
                }
                MenuEnumsForEdit.KILL.itemName -> {
                    AppProcessManager.killDialog(
                        editFragment,
                        currentAppDirPath,
                        currentScriptFileName
                    )
                }
                MenuEnumsForEdit.TERM_REFRESH.itemName -> {
                    TermRefresh.refresh(
                        terminalViewModel.currentMonitorFileName
                    )
                }
                MenuEnumsForEdit.SELECT_TERM.itemName -> {
                    SelectTermDialog.launch(editFragment)
                }
                MenuEnumsForEdit.USAGE.itemName -> {
                    UrlTexter.launch(
                        editFragment,
                        null,
                        WebUrlVariables.commandClickUsageUrl
                    )
                }
                MenuEnumsForEdit.RESTART_UBUNTU.itemName -> {
                    UbuntuServiceManager.launch(
                        editFragment.activity
                    )
                }
            }
        }
    }
}


private enum class MenuEnumsForEdit(
    val itemName: String,
    val imageId: Int,
) {
    TERM_REFRESH("term refresh", R.drawable.icons8_refresh),
    SELECT_TERM("select term", R.drawable.icons8_file),
    RESTART_UBUNTU("restart ubuntu", R.drawable.icons8_launch),
    KILL("kill", R.drawable.cancel),
    NO_SCROLL_SAVE_URL("no scroll save url", R.drawable.icons8_check_ok),
    USAGE("usage", R.drawable.icons8_info),
    SETTING("setting", R.drawable.icons8_setting),
}
