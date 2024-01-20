package com.puutaro.commandclick.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.PageSearchToolbarButtonVariant
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.UpdateLastModifyForEdit
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.*
import com.puutaro.commandclick.fragment_lib.edit_fragment.broadcast.receiver.BroadcastReceiveHandlerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TitleImageAndViewSetter
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDoWhenReuse
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.KeyboardWhenTermLongForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ListIndexSizingToKeyboard
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.PageSearchToolbarManagerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidationSharePreferenceForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.WebSearchToolbarManagerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditInitType
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonToolForEdit
import com.puutaro.commandclick.proccess.setting_button.libs.EditLongPressType
import com.puutaro.commandclick.proccess.broadcast.BroadcastRegister
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.WithIndexListView
import com.puutaro.commandclick.proccess.setting_button.libs.FileGetterForSettingButton
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file_tool.FDialogTempFile
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.view_model.activity.CommandIndexViewModel
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File


class EditFragment: Fragment() {

    private var _binding: EditFragmentBinding? = null
    val binding get() = _binding!!
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

    var commandSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
    ) as String
    var commandSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
    ) as String
    var runShell = CommandClickScriptVariable.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
    var historySwitch = SettingVariableSelects.HistorySwitchSelects.OFF.name
    var onTermVisibleWhenKeyboard =
        CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE
    var urlHistoryOrButtonExec =
        CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE
    var shiban = CommandClickScriptVariable.CMDCLICK_SHIBAN_DEFAULT_VALUE
    var fontZoomPercent = CommandClickScriptVariable.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE
    var terminalOn = CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
    var terminalColor = CommandClickScriptVariable.TERMINAL_COLOR_DEFAULT_VALUE
    var statusBarIconColorMode =
        CommandClickScriptVariable.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE
    var jsExecuteJob: Job? = null
    var popBackStackToIndexImmediateJob: Job? = null
    var suggestJob: Job? = null
    var readSharePreffernceMap: Map<String, String> = mapOf()
    var srcReadSharePreffernceMap: Map<String, String>? = null
    var editTypeSettingKey =
        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
    var setReplaceVariableMap: Map<String, String>? = null
    var enableCmdEdit = false
    var editExecuteValue = CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
    var enableEditExecute = false
    var currentScriptContentsList = emptyList<String>()
    var homeFannelHistoryNameList: List<String>? = null
    var bottomScriptUrlList = emptyList<String>()
    var execPlayBtnLongPress = String()
    var execEditBtnLongPress = String()
    var overrideItemClickExec = String()
    var existIndexList: Boolean = false
    var passCmdVariableEdit = String()
    var toolbarButtonConfigMap: Map<ToolbarButtonBariantForEdit, Map<String, String>?>? = null
    var fileGetterForSettingButton: FileGetterForSettingButton? = null
    val toolBarButtonVisibleMap = ToolbarButtonToolForEdit.createInitButtonDisableMap()
    val toolBarButtonIconMap = ToolbarButtonToolForEdit.createInitButtonIconMap()
    var editBoxTitle = String()
    var buttonWeight = 0.25f
    var onNoUrlSaveMenu = false
    var onUpdateLastModify = false
    var isInstallFannelForListIndex = false

    private var broadcastReceiverForEdit: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            BroadcastReceiveHandlerForEdit.handle(
                this@EditFragment,
                intent
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.edit_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pageSearch.cmdclickPageSearchToolBar.isVisible = false
        binding.webSearch.webSearchToolbar.isVisible = false
        binding.editListLinearLayout.isVisible = false
        fileGetterForSettingButton = FileGetterForSettingButton(this)
        val sharePref = activity?.getPreferences(Context.MODE_PRIVATE)
        readSharePreffernceMap =
            EditFragmentArgs.getReadSharePreference(arguments)
        srcReadSharePreffernceMap =
            EditFragmentArgs.getSrcReadSharePreference(arguments)
        FDialogTempFile.remove(readSharePreffernceMap)
        val currentAppDirPath =
            SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            )
        val currentScriptFileName =
            SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_fannel_name
            )
        val onShortcutValue =
            SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.on_shortcut
            )

        editTypeSettingKey = EditFragmentArgs.getEditType(arguments)
        enableCmdEdit =
            editTypeSettingKey ==
                    EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
        Keyboard.hiddenKeyboardForFragment(
            this
        )
        SetConfigInfo.set(this)
        val validationSharePreferenceForEdit = ValidationSharePreferenceForEdit(
            this,
        )
        val checkOkForAppDirPath =
            validationSharePreferenceForEdit
                .checkCurrentAppDirPreference()
        if(!checkOkForAppDirPath) return
        val checkOkForShellName =
            validationSharePreferenceForEdit
                .checkCurrentShellNamePreference()
        if(!checkOkForShellName) return
        val checkOkIndexList =
            validationSharePreferenceForEdit
                .checkIndexList()
        if(!checkOkIndexList) return
        SharePreferenceMethod.putSharePreference(
            sharePref,
            mapOf(
                SharePrefferenceSetting.current_app_dir.name
                        to currentAppDirPath,
                SharePrefferenceSetting.current_fannel_name.name
                        to currentScriptFileName,
                SharePrefferenceSetting.on_shortcut.name
                        to onShortcutValue
            )
        )

        languageType =
            CommandClickVariables.judgeJsOrShellFromSuffix(currentScriptFileName)

        val languageTypeToSectionHolderMap =
            CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
        ) as String
        settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
        ) as String
        commandSectionStart = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
        ) as String
        commandSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
        ) as String

        currentScriptContentsList = ReadText(
            currentAppDirPath,
            currentScriptFileName
        ).textToList()
        setReplaceVariableMap =
            JavaScriptLoadUrl.createMakeReplaceVariableMapHandler(
                currentScriptContentsList,
                currentAppDirPath,
                currentScriptFileName,
            )
        ConfigFromScriptFileSetter.set(
            this,
            currentScriptContentsList,
        )
        buttonWeight = ToolbarButtonToolForEdit.culcButtonWeight(this)
        if(
            UpdateLastModifyForEdit().judge(
                this,
                currentAppDirPath,
            )
        ) {
            FileSystems.updateLastModified(
                UsePath.cmdclickAppDirAdminPath,
                File(currentAppDirPath).name + UsePath.JS_FILE_SUFFIX
            )
            FileSystems.updateLastModified(
                currentAppDirPath,
                currentScriptFileName,
            )
            val pageSearchToolbarManagerForEdit =
                PageSearchToolbarManagerForEdit(this)
            pageSearchToolbarManagerForEdit.cancleButtonClickListener()
            pageSearchToolbarManagerForEdit.onKeyListner()
            pageSearchToolbarManagerForEdit.pageSearchTextChangeListner()
            pageSearchToolbarManagerForEdit.searchTopClickLisnter()
            pageSearchToolbarManagerForEdit.searchDownClickLisnter()
            val webSearchToolbarManagerForEdit =
                WebSearchToolbarManagerForEdit(this)
            webSearchToolbarManagerForEdit.setKeyListener()
            webSearchToolbarManagerForEdit.setCancelListener()
            webSearchToolbarManagerForEdit.setGoogleSuggest()
        }
        TitleImageAndViewSetter.set(
            this,
            currentAppDirPath,
            currentScriptFileName
        )

        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        context?.let {
            window?.statusBarColor = Color.parseColor(terminalColor)
        }

        val editModeHandler = EditModeHandler(
            this,
            binding,
        )
        editModeHandler.execByHowFullEdit()
        val cmdIndexViewModel: CommandIndexViewModel by activityViewModels()
        cmdIndexViewModel.onFocusSearchText = false
        val terminalViewModel: TerminalViewModel by activityViewModels()
        val listener = context as? OnKeyboardVisibleListenerForEditFragment
        KeyboardVisibilityEvent.setEventListener(activity) {
                isOpen ->
            if(!this.isVisible) return@setEventListener
            if(terminalViewModel.onDialog) return@setEventListener
            binding.editTitleLinearlayout.isVisible = !isOpen
            val linearLayoutParam =
                binding.editFragment.layoutParams as LinearLayout.LayoutParams
            val editFragmentWeight = linearLayoutParam.weight
            if(
                editFragmentWeight != ReadLines.LONGTH
            ) {
                KeyboardWhenTermLongForEdit.handle(
                    this,
                    isOpen
                )
                return@setEventListener
            }
            ListIndexSizingToKeyboard.handle(
                this,
                isOpen
            )
            binding.editToolBarLinearLayout.isVisible = if(
                isOpen
            ) !existIndexList
            else true
            val isOpenKeyboard = if(
                isOpen
            ) onTermVisibleWhenKeyboard !=
                        SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.ON.name
            else isOpen
            listener?.onKeyBoardVisibleChangeForEditFragment(
                isOpenKeyboard,
                this.isVisible
            )
        }
    }

    override fun onPause() {
        super.onPause()
        BroadcastRegister.unregisterBroadcastReceiver(
            this,
            broadcastReceiverForEdit
        )
    }

    override fun onResume() {
        super.onResume()
        BroadcastRegister.registerBroadcastReceiverMultiActions(
            this,
            broadcastReceiverForEdit,
            listOf(
                BroadCastIntentSchemeForEdit.UPDATE_INDEX_LIST.action,
            )
        )
        val shellScriptContentsList = ReadText(
            SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            ),
            SharePreferenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_fannel_name
            )
        ).textToList()
        TerminalShowByTerminalDoWhenReuse.show(
            this,
            shellScriptContentsList
        )
    }

    override fun onStart() {
        super.onStart()
        if(existIndexList){
            CoroutineScope(Dispatchers.Main).launch {
                delay(100)
                WithIndexListView.listIndexListUpdateFileList(
                    this@EditFragment,
                    WithIndexListView.makeFileListHandler(isInstallFannelForListIndex)
                )
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        jsExecuteJob?.cancel()
    }

    interface onToolBarButtonClickListenerForEditFragment {
        fun onToolBarButtonClickForEditFragment(
            callOwnerFragmentTag : String?,
            toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
            readSharePreffernceMap: Map<String, String>,
            enableCmdEdit: Boolean,
        )
    }

    interface OnKeyboardVisibleListenerForEditFragment {
        fun onKeyBoardVisibleChangeForEditFragment(
            isKeyboardShowing: Boolean,
            isVisible: Boolean,
        )
    }

    interface OnToolbarMenuCategoriesListenerForEdit {
        fun onToolbarMenuCategoriesForEdit(
            toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
            editFragmentArgs: EditFragmentArgs,
        )
    }

    interface OnInitEditFragmentListener {
        fun onInitEditFragment()
    }

    interface OnTerminalWebViewInitListenerForEdit {
        fun onTerminalWebViewInitForEdit(
            editInitType: EditInitType,
        )
    }

    interface OnLaunchUrlByWebViewForEditListener {
        fun onLaunchUrlByWebViewForEdit(
            searchUrl: String
        )
    }

    interface OnFileChooserListenerForEdit {
        fun onFileChooserListenerForEdit(
            onDirectoryPick: Boolean,
            insertEditText: EditText
        )
    }

    interface OnTermSizeLongListenerForEdit {
        fun onTermSizeLongForEdit(
            editFragment: EditFragment
        )
    }

    interface OnMultiSelectListenerForEdit {
        fun onMultiSelectForEdit(
            variableName: String,
            editTextId: Int,
            updatedMultiModelArray: ArrayList<MultiSelectModel>,
            preSelectedMultiModelArray: ArrayList<Int>
        )
    }

    interface OnLongPressPlayOrEditButtonListener {
        fun onLongPressPlayOrEditButton(
            editLongPressType: EditLongPressType,
            tag: String?,
            searchText: String,
            pageSearchToolbarButtonVariant: PageSearchToolbarButtonVariant? = null,
        )
    }

    interface OnLongTermKeyBoardOpenAjustListenerForEdit {
        fun onLongTermKeyBoardOpenAjustForEdit(
            weight: Float
        )
    }

    interface OnUpdateNoSaveUrlPathsListenerForEdit {
        fun onUpdateNoSaveUrlPathsForEdit(
            currentAppDirPath: String,
            fannelName: String,
        )
    }

    interface OnGetPermissionListenerForEdit {
        fun onGetPermissionForEdit(
            permissionStr: String
        )
    }
}