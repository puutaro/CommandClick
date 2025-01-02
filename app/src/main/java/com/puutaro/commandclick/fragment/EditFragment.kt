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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeForEdit
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.component.adapter.lib.edit_list_adapter.ListViewToolForEditListAdapter
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.UpdateLastModifyForEdit
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.*
import com.puutaro.commandclick.fragment_lib.edit_fragment.broadcast.receiver.BroadcastReceiveHandlerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDoWhenReuse
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditInitType
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonToolForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.CurrentFannelConListMaker
import com.puutaro.commandclick.proccess.broadcast.BroadcastRegister
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.proccess.js_macro_libs.edit_list_libs.DirectoryAndCopyGetter
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelStateManager
import com.puutaro.commandclick.util.state.FannelStateRooterManager
import com.puutaro.commandclick.util.state.SettingFannelConHandlerForEdit
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.File


class EditFragment: Fragment() {

    private var _binding: EditFragmentBinding? = null
    val binding get() = _binding!!
    var busyboxExecutor: BusyboxExecutor? = null
//    var historySwitch = SettingVariableSelects.HistorySwitchSelects.OFF.name
    var onTermVisibleWhenKeyboard =
        CommandClickScriptVariable.ON_TERM_VISIBLE_WHEN_KEYBOARD_DEFAULT_VALUE
//    var urlHistoryOrButtonExec =
//        CommandClickScriptVariable.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE
    var fontZoomPercent = CommandClickScriptVariable.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE
    var terminalOn = CommandClickScriptVariable.TERMINAL_DO_DEFAULT_VALUE
    var terminalColor = CommandClickScriptVariable.TERMINAL_COLOR_DEFAULT_VALUE
    var jsExecuteJob: Job? = null
//    var popBackStackToIndexImmediateJob: Job? = null
    var suggestJob: Job? = null
    var fannelInfoMap: Map<String, String> = mapOf()
    var srcFannelInfoMap: Map<String, String>? = null
    var editTypeSettingKey =
        EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
    var setReplaceVariableMap: Map<String, String>? = null
    var settingFannelPath: String = String()
//    var setVariableTypeList: List<String>? = null
//    var hideSettingVariableList: List<String> = emptyList()
//    var enableCmdEdit = false
    var editExecuteValue = CommandClickScriptVariable.EDIT_EXECUTE_DEFAULT_VALUE
    var enableEditExecute = false
    var mainFannelConList = emptyList<String>()
    var currentFannelConList = emptyList<String>()
    var settingFannelConList: List<String>? = null
//    var existIndexList: Boolean = false
//    var passCmdVariableEdit = String()
    var toolbarButtonConfigMap: Map<ToolbarButtonBariantForEdit, Map<String, String>?>? = null
    var editListConfigMap: Map<String, String>? = null
//    var qrDialogConfig: Map<String, String>? = null
    var directoryAndCopyGetter: DirectoryAndCopyGetter? = null
    val toolBarButtonVisibleMap = ToolbarButtonToolForEdit.createInitButtonDisableMap()
    val toolBarButtonIconMap: MutableMap<ToolbarButtonBariantForEdit, Pair<Int, String>> = ToolbarButtonToolForEdit.createInitButtonIconMap()
    var editBoxTitleConfig: Map<String, String> = emptyMap()
//    var filterDir = String()
    var buttonWeight = 0.25f
//    var onNoUrlSaveMenu = false
    var onUpdateLastModify = false
    var disableKeyboardFragmentChange = false
    val listConSelectBoxMapList: MutableList<Map<String, String>?> = mutableListOf()
//    var recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null
//    var recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>? = null
    var firstUpdate = false
    val alterIfShellResultMap: MutableMap<String, String> = mutableMapOf()
    val imageActionAsyncCoroutine = ImageActionAsyncCoroutine()


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
        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.pageSearch.cmdclickPageSearchToolBar.isVisible = false
//        binding.webSearch.webSearchToolbar.isVisible = false
//        binding.editToolBarLinearLayout.isVisible = false
        directoryAndCopyGetter = DirectoryAndCopyGetter(this)
        val sharePref = FannelInfoTool.getSharePref(context)
        fannelInfoMap =
            EditFragmentArgs.getFannelInfoMap(arguments)
        srcFannelInfoMap =
            EditFragmentArgs.getSrcFannelInfoMap(arguments)
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )
        val onShortcutValue = FannelInfoTool.getOnShortcut(
            fannelInfoMap
        )
        val currentFannelState = FannelInfoTool.getCurrentStateName(
            fannelInfoMap
        )

        editTypeSettingKey = EditFragmentArgs.getEditType(arguments)
//        enableCmdEdit =
//            editTypeSettingKey ==
//                    EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
        Keyboard.hiddenKeyboardForFragment(
            this
        )
        context?.let {
            busyboxExecutor = BusyboxExecutor(
                it,
                UbuntuFiles(it)
            )
        }
//        SetConfigInfo.set(this)
        FannelInfoTool.putAllFannelInfo(
            sharePref,
//            currentAppDirPath,
            currentFannelName,
            onShortcutValue,
            currentFannelState
        )

        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
        val currentFannelPath =
            File(cmdclickDefaultAppDirPath, currentFannelName).absolutePath
        mainFannelConList =
            ReadText(currentFannelPath).textToList()
        setReplaceVariableMap =
            JavaScriptLoadUrl.createMakeReplaceVariableMapHandler(
                context,
                mainFannelConList,
                currentFannelName,
            )
        settingFannelPath = FannelStateRooterManager.getSettingFannelPath(
            fannelInfoMap,
            setReplaceVariableMap
        )
        currentFannelConList = CurrentFannelConListMaker.make(
            currentFannelPath,
            mainFannelConList,
            settingFannelPath,
        )
        FannelStateManager.updateState(
            context,
            currentFannelState,
            fannelInfoMap,
            setReplaceVariableMap,
        )
        ConfigFromScriptFileSetter.set(
            this,
            mainFannelConList
        )
        buttonWeight =
            ToolbarButtonToolForEdit.culcButtonWeight(this)
        if(
            UpdateLastModifyForEdit.judge(
                this,
//                currentAppDirPath,
            )
        ) {
//            FileSystems.updateLastModified(
//                File(
//                    UsePath.cmdclickAppDirAdminPath,
//                    File(cmdclickDefaultAppDirPath).name + UsePath.JS_FILE_SUFFIX
//                ).absolutePath
//            )
            FileSystems.updateLastModified(
                File(
                    cmdclickDefaultAppDirPath,
                    currentFannelName
                ).absolutePath,
            )
//            val pageSearchToolbarManagerForEdit =
////                PageSearchToolbarManagerForEdit(this)
//            PageSearchToolbarManagerForEdit.cancleButtonClickListener(this)
//            PageSearchToolbarManagerForEdit.onKeyListner(this)
//            PageSearchToolbarManagerForEdit.pageSearchTextChangeListner(this)
//            PageSearchToolbarManagerForEdit.searchTopClickLisnter(this)
//            PageSearchToolbarManagerForEdit.searchDownClickLisnter(this)
//            WebSearchToolbarManagerForEdit.setKeyListener(this)
//            WebSearchToolbarManagerForEdit.setCancelListener(this)
//            WebSearchToolbarManagerForEdit.setGoogleSuggest(this)
        }

        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        context?.let {
            window?.statusBarColor = Color.parseColor(terminalColor)
        }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "edit.txt").absolutePath,
//            listOf(
//                "settingFilePath: ${FannelStateRooterManager.getSettingFannelPath(
//                    readSharePreferenceMap,
//                    setReplaceVariableMap
//                )}",
//                "settingval: ${FannelStateRooterManager.makeSettingVariableList(
//                    readSharePreferenceMap,
//                    setReplaceVariableMap,
//                    settingSectionStart,
//                    settingSectionEnd
//                )}",
//                "recordNumToMapNameValueInSettingHolder: ${recordNumToMapNameValueInSettingHolder}",
//                "recordNumToMapNameValueInCommandHolder: ${recordNumToMapNameValueInCommandHolder}"
//            ).joinToString("\n\n")
//        )
        EditModeHandler.execByHowFullEdit(this)
        context?.let {
            busyboxExecutor = BusyboxExecutor(
                it,
                UbuntuFiles(it)
            )
        }
        val listener = context as? OnKeyboardVisibleListenerForEditFragment
        activity?.let {
            KeyboardVisibilityEvent.setEventListener(
                it,
                this.viewLifecycleOwner,
                KeyboardVisibilityEventListener {
                        isOpen ->
                    if(
                        !this.isVisible
                    ) return@KeyboardVisibilityEventListener
//                    if(
//                        disableKeyboardFragmentChange
//                    ) return@KeyboardVisibilityEventListener
////            if(terminalViewModel.onDialog) return@setEventListener
//                    binding.editToolBarLinearLayout.isVisible = !isOpen
//                    val linearLayoutParam =
//                        binding.editFragment.layoutParams as LinearLayoutCompat.LayoutParams
//                    val editFragmentWeight = linearLayoutParam.weight
//                    if(
//                        editFragmentWeight != ReadLines.LONGTH
//                    ) {
//                        KeyboardWhenTermLongForEdit.handle(
//                            this,
//                            isOpen
//                        )
//                        return@KeyboardVisibilityEventListener
//                    }
//                    ListIndexSizingToKeyboard.handle(
//                        this,
//                        isOpen
//                    )
//                    binding.editToolBarLinearLayout.isVisible =
//                        when(isOpen) {
//                            true -> {
//                                val isNotSearchTextWhenIndexList =
//                                    (existIndexList && !binding.editListSearchEditText.isVisible)
//                                !existIndexList
//                                        || isNotSearchTextWhenIndexList
//                            }
//                            else -> true
//                        }
//                    val isOpenKeyboard =
//                        when(isOpen) {
//                            true -> onTermVisibleWhenKeyboard !=
//                                    SettingVariableSelects.OnTermVisibleWhenKeyboardSelects.ON.name
//                            else -> isOpen
//                        }
//                    listener?.onKeyBoardVisibleChangeForEditFragment(
//                        isOpenKeyboard,
//                        this.isVisible
//                    )
                }
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
        val settingFannelConList = SettingFannelConHandlerForEdit.handle(
            this
        )
        TerminalShowByTerminalDoWhenReuse.show(
            this,
            settingFannelConList
        )
    }

    override fun onStart() {
        super.onStart()
//        if(
//            !existIndexList
//        ) return
        if(!firstUpdate){
            firstUpdate = true
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            val editConstraintListAdapter = binding.editListRecyclerView.adapter as EditConstraintListAdapter
            ListViewToolForEditListAdapter.editListUpdateFileList(
                editConstraintListAdapter,
                ListSettingsForEditList.EditListMaker.makeLineMapListHandler(
                    this@EditFragment.fannelInfoMap,
                    this@EditFragment.setReplaceVariableMap,
                    editConstraintListAdapter.indexListMap,
                    this@EditFragment.busyboxExecutor,
//                    ListIndexAdapter.listIndexTypeKey
                )
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        CoroutineScope(Dispatchers.IO).launch {
            imageActionAsyncCoroutine.clean()
        }
        jsExecuteJob?.cancel()
        alterIfShellResultMap.clear()
        destroyViews()
        _binding = null
    }

    private fun destroyViews(){
        exitDialog(binding.editListRecyclerView)
//        binding.editToolBarHorizonLayout.removeAllViews()
//        binding.editListInnerTopLinearLayout.removeAllViews()
//        binding.editListInnerBottomLinearLayout.removeAllViews()
//        binding.editListLinearLayout.removeAllViews()
//        binding.editToolBarLinearLayout.removeAllViews()
    }

    interface OnToolBarButtonClickListenerForEditFragment {
        fun onToolBarButtonClickForEditFragment(
            callOwnerFragmentTag : String?,
            toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
            fannelInfoMap: Map<String, String>,
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
            insertEditText: EditText,
            chooserMap: Map<String, String>?,
            fannelName: String,
            currentVariableName: String,
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

//    interface OnLongPressPlayOrEditButtonListener {
//        fun onLongPressPlayOrEditButton(
//            editLongPressType: EditLongPressType,
//            tag: String?,
//            searchText: String,
//            pageSearchToolbarButtonVariant: PageSearchToolbarButtonVariant? = null,
//        )
//    }

    interface OnLongTermKeyBoardOpenAjustListenerForEdit {
        fun onLongTermKeyBoardOpenAjustForEdit(
            weight: Float
        )
    }

    interface OnUpdateNoSaveUrlPathsListenerForEdit {
        fun onUpdateNoSaveUrlPathsForEdit(
//            currentAppDirPath: String,
            fannelName: String,
        )
    }

    interface OnGetPermissionListenerForEdit {
        fun onGetPermissionForEdit(
            permissionStr: String
        )
    }

    interface OnCaptureActivityListenerForEdit {
        fun onCaptureActivityForEdit()
    }

    private fun exitDialog(
        editListRecyclerView: RecyclerView
    ){
        editListRecyclerView.layoutManager = null
        editListRecyclerView.adapter = null
        editListRecyclerView.recycledViewPool.clear()
        editListRecyclerView.removeAllViews()
    }
}