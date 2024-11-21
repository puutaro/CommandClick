package com.puutaro.commandclick.activity

import android.app.Activity
import android.content.*
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.abdeveloper.library.MultiSelectModel
import com.anggrayudi.storage.SimpleStorageHelper
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.InitManager
import com.puutaro.commandclick.activity_lib.event.*
import com.puutaro.commandclick.activity_lib.event.lib.ExecInitForEditFragment
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.*
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecBackstackHandle
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecSimpleRestartActivity
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecUpdateNoSaveUrlPaths
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecWifiSet
import com.puutaro.commandclick.activity_lib.event.lib.common.RestartWhenPreferenceCheckErr
import com.puutaro.commandclick.activity_lib.event.lib.edit.ExecFileChooser
import com.puutaro.commandclick.activity_lib.event.lib.edit.ExecOnLongTermKeyBoardOpenAdjustForEdit
import com.puutaro.commandclick.activity_lib.event.lib.edit.ExecOnToolBarVisibleChangeForEdit
import com.puutaro.commandclick.activity_lib.event.lib.edit.ExecTermMinimumForEdit
import com.puutaro.commandclick.activity_lib.event.lib.edit.GetFileForEdit
import com.puutaro.commandclick.activity_lib.event.lib.edit.GetFileListForEdit
import com.puutaro.commandclick.activity_lib.event.lib.edit.MultiSelectDialogForEdit
import com.puutaro.commandclick.activity_lib.event.lib.edit.MultiSelectListContentsDialogForEdit
import com.puutaro.commandclick.activity_lib.event.lib.terminal.*
import com.puutaro.commandclick.activity_lib.manager.AdBlocker
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.activity_lib.manager.curdForFragment.FragmentManagerForActivity
import com.puutaro.commandclick.activity_lib.permission.CameraSetter
import com.puutaro.commandclick.activity_lib.permission.LocationSetter
import com.puutaro.commandclick.activity_lib.permission.NotifierSetter
import com.puutaro.commandclick.activity_lib.permission.SdCardDirGetter
import com.puutaro.commandclick.activity_lib.permission.StorageAccessSetter
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeUbuntu
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.variant.PageSearchToolbarButtonVariant
import com.puutaro.commandclick.databinding.ActivityMainBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.ToolbarCtrlForCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter.SearchButtonClickListener
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditInitType
import com.puutaro.commandclick.proccess.broadcast.BroadcastRegister
import com.puutaro.commandclick.proccess.edit.lib.FilePickerTool
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryCaptureTool
import com.puutaro.commandclick.proccess.pin.PinFannelHideShow
import com.puutaro.commandclick.proccess.setting_menu_for_cmdindex.page_search.PageSearchManager
import com.puutaro.commandclick.service.FannelRepoDownloadService
import com.puutaro.commandclick.util.Intent.UbuntuServiceManager
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.FragmentTagManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.*


class MainActivity:
    AppCompatActivity(),
//    TerminalFragment.OnSearchTextChangeListener,
    TerminalFragment.onBackstackWhenTermLongInRestartListener,
    TerminalFragment.OnToolBarVisibleChangeListener,
    TerminalFragment.OnTermLongChangeListenerForTerminalFragment,
    TerminalFragment.OnTermShortSizeListenerForTerminalFragment,
    TerminalFragment.OnPageLoadPageSearchDisableListener,
    TerminalFragment.OnFindPageSearchResultListener,
    TerminalFragment.OnFileChooseListener,
    TerminalFragment.OnTextViewAndFannelUpdateListenerForTerm,
    TerminalFragment.OnTextViewAndMapListUpdateListenerForTerm,
//    TerminalFragment.OnSpinnerUpdateListenerForTermFragment,
    TerminalFragment.OnEditableSpinnerUpdateListenerForTermFragment,
    TerminalFragment.OnMultiSelectListenerForTerm,
    TerminalFragment.OnTermSizeMinimumListenerForTerm,
    TerminalFragment.OnGetPermissionListenerForTerm,
    TerminalFragment.OnAdBlockListener,
    TerminalFragment.OnChangeEditFragmentListenerForTerm,
    TerminalFragment.OnEditFannelContentsListUpdateListenerForTerm,
    TerminalFragment.OnMonitorSizeChangingForTerm,
    TerminalFragment.OnPopStackImmediateListenerForTerm,
    TerminalFragment.OnCmdValSaveAndBackListenerForTerm,
//    TerminalFragment.OnGetFileListenerForTerm,
    TerminalFragment.OnGetFileListListenerForTerm,
    TerminalFragment.GetSdcardDirListenerForTerm,
    TerminalFragment.OnRestartListenerForTerm,
    TerminalFragment.OnPinClickForTermListener,
    TerminalFragment.OnSetToolbarButtonImageListener,
    TerminalFragment.OnSearchButtonMakeListenerForTerm,
    TerminalFragment.OnKeyboardHandleListenerForTerm,
    TerminalFragment.OnPinFannelHideListener,
    TerminalFragment.OnPageSearchSwitchListenerForTerm,
    TerminalFragment.OnCaptureActivityListenerForTerm,
    TerminalFragment.OnSelectionSearchBarSwitchListenerForTerm,
    TerminalFragment.OnUpdateSelectionTextViewListenerForTerm,
    CommandIndexFragment.OnListItemClickListener,
    CommandIndexFragment.OnKeyboardVisibleListener,
    CommandIndexFragment.OnToolbarMenuCategoriesListener,
    CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener,
    CommandIndexFragment.OnBackstackDeleteListener,
    CommandIndexFragment.OnLaunchUrlByWebViewListener,
    CommandIndexFragment.OnFilterWebViewListener,
    CommandIndexFragment.OnPageSearchToolbarClickListener,
    CommandIndexFragment.OnUpdateNoSaveUrlPathsListener,
    CommandIndexFragment.OnGetPermissionListenerForCmdIndex,
    CommandIndexFragment.OnConnectWifiListenerForCmdIndex,
    CommandIndexFragment.OnCaptureActivityListenerForIndex,
    CommandIndexFragment.OnZeroSizingListener,
    CommandIndexFragment.OnSearchButtonMakeListenerForCmdIndex,
    CommandIndexFragment.OnKeyboardHandleListenerForCmdIndex,
    CommandIndexFragment.OnPinFannelShowListener,
    CommandIndexFragment.OnPageSearchSwitchListener,
    EditFragment.OnToolBarButtonClickListenerForEditFragment,
    EditFragment.OnKeyboardVisibleListenerForEditFragment,
    EditFragment.OnToolbarMenuCategoriesListenerForEdit,
    EditFragment.OnInitEditFragmentListener,
    EditFragment.OnTerminalWebViewInitListenerForEdit,
    EditFragment.OnLaunchUrlByWebViewForEditListener,
    EditFragment.OnFileChooserListenerForEdit,
    EditFragment.OnTermSizeLongListenerForEdit,
    EditFragment.OnMultiSelectListenerForEdit,
//    EditFragment.OnLongPressPlayOrEditButtonListener,
    EditFragment.OnLongTermKeyBoardOpenAjustListenerForEdit,
    EditFragment.OnUpdateNoSaveUrlPathsListenerForEdit,
    EditFragment.OnGetPermissionListenerForEdit,
    EditFragment.OnCaptureActivityListenerForEdit {

    lateinit var activityMainBinding: ActivityMainBinding
    private var filePath: ValueCallback<Array<Uri>>? = null
    private var adBlockJob: Job? = null
    var savedInstanceStateVal: Bundle? = null
    private val getFile = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
    ) {
            if (it.resultCode == Activity.RESULT_CANCELED) {
                filePath?.onReceiveValue(null)
            } else if (it.resultCode == Activity.RESULT_OK && filePath != null) {
                filePath?.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(it.resultCode, it.data))
                filePath = null
            }
        }

    private val sdcardDirGetter = SdCardDirGetter(this)
    private val storageHelper = SimpleStorageHelper(this)
    private val getFileForEdit = GetFileForEdit(
        this,
        storageHelper,
    )
    private val getFileListForEdit = GetFileListForEdit(
        WeakReference(this),
        storageHelper
    )

    val storageAccessPermissionLauncher =
        StorageAccessSetter.set(this)

    @RequiresApi(Build.VERSION_CODES.R)
    val manageFullStoragePermissionResultLauncher =
            StorageAccessSetter.setForFullStorageAccess(this)

//    val getRunCommandPermissionAndStartFragmentLauncher =
//        RunCommandSetter.set(this)

    val getNotifierSetterLaunch =
        NotifierSetter.set(this)

    val getCameraSetterLaunch =
        CameraSetter.set(this)

    val getLocationSetterLaunch =
        LocationSetter.set(this)

    private var broadcastReceiverForRestartUbuntuService: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if(
                intent.action !=
                BroadCastIntentSchemeUbuntu.RESTART_UBUNTU_SERVICE_FROM_ACTIVITY.action
            ) return
            UbuntuServiceManager.monitoringAndLaunchUbuntuService(
                this@MainActivity,
                false,
                false
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        savedInstanceStateVal = null
//        savedInstanceState
        val actionBar = supportActionBar
        actionBar?.hide()
        volumeControlStream = AudioManager.STREAM_MUSIC
        BroadcastRegister.registerBroadcastReceiverForActivity(
            this,
            broadcastReceiverForRestartUbuntuService,
            BroadCastIntentSchemeUbuntu.RESTART_UBUNTU_SERVICE_FROM_ACTIVITY.action
        )
        UbuntuServiceManager.monitoringAndLaunchUbuntuService(
            this,
            true,
            false
        )
        InitManager.invoke(this)
//        CoroutineScope(Dispatchers.IO).launch {
//            val view = withContext(Dispatchers.IO) {
//                while (true) {
//                    delay(1000)
//                    if (
//                        ::activityMainBinding.isInitialized
//                    ) return@withContext activityMainBinding.rootContainer
//                }
//                null
//            }
//            AppHistoryCapture.watch(
//                this@MainActivity,
//                this@MainActivity.window.decorView.rootView,
//                FannelInfoTool.getSharePref(this@MainActivity),
//            )
//        }
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onDestroy() {
        super.onDestroy()
        adBlockJob?.cancel()
        val intent = Intent(this, FannelRepoDownloadService::class.java)
        this.stopService(intent)
        BroadcastRegister.unregisterBroadcastReceiverForActivity(
            this,
            broadcastReceiverForRestartUbuntuService,
        )
    }


    override fun onLongClickMenuItemsforCmdIndex(
        longClickMenuItemsforCmdIndex: LongClickMenuItemsforCmdIndex,
        editFragmentArgs: EditFragmentArgs,
        editFragmentTag: String,
        terminalFragmentTag: String
    ) {
        ExecLongClickMenuItemsforCmdIndex.execLongClickMenuItemsforCmdIndex(
            this,
            longClickMenuItemsforCmdIndex,
            editFragmentTag,
            editFragmentArgs,
            terminalFragmentTag
        )
    }

    override fun onPinClickForTerm(
        longClickMenuItemsforCmdIndex: LongClickMenuItemsforCmdIndex,
        editFragmentArgs: EditFragmentArgs,
        editFragmentTag: String,
        terminalFragmentTag: String
    ) {
        ExecLongClickMenuItemsforCmdIndex.execLongClickMenuItemsforCmdIndex(
            this,
            longClickMenuItemsforCmdIndex,
            editFragmentTag,
            editFragmentArgs,
            terminalFragmentTag
        )
    }


    override fun onToolbarMenuCategories(
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
        editFragmentArgs: EditFragmentArgs,
    ) {
        ExecToolbarMenuCategoriesForCmdIndex.execToolbarMenuCategories<CommandIndexFragment>(
            this,
            getString(R.string.command_index_fragment),
            editFragmentArgs,
            toolbarMenuCategoriesVariantForCmdIndex,
        )
    }

    override fun onToolbarMenuCategoriesForEdit(
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex,
        editFragmentArgs: EditFragmentArgs,
    ) {
        val startUpPref = FannelInfoTool.getSharePref(this)
        val cmdEditFragmentTag =
            FragmentTagManager.makeCmdValEditTag(
//                FannelInfoTool.getStringFromFannelInfo(
//                    startUpPref,
////                    FannelInfoSetting.current_app_dir
//                ),
                FannelInfoTool.getStringFromFannelInfo(
                    startUpPref,
                    FannelInfoSetting.current_fannel_name
                ),
                FannelInfoTool.getStringFromFannelInfo(
                    startUpPref,
                    FannelInfoSetting.current_fannel_state
                )
            )
        ExecToolbarMenuCategoriesForCmdIndex.execToolbarMenuCategories<EditFragment>(
            this,
            cmdEditFragmentTag,
            editFragmentArgs,
            toolbarMenuCategoriesVariantForCmdIndex
        )
    }

    override fun onToolBarButtonClickForEditFragment(
        callOwnerFragmentTag : String?,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        fannelInfoMap: Map<String, String>,
        enableCmdEdit: Boolean,
    ){
        ExecToolBarButtonClickForEdit.execToolBarButtonClickForEdit(
            this,
            callOwnerFragmentTag,
            toolbarButtonBariantForEdit,
            fannelInfoMap,
            enableCmdEdit,
        )
    }


    override fun onKeyBoardVisibleChange(
        isKeyboardShowing: Boolean,
        isVisible: Boolean,
        SpecialSearchSwitch: Boolean
    ){
        if(!isVisible) return
        CoroutineScope(Dispatchers.Main).launch {
            if (SpecialSearchSwitch) {
                WrapFragmentManager.changeFragmentByKeyBoardVisibleChange(
                    isKeyboardShowing,
                    supportFragmentManager,
                    getString(R.string.index_terminal_fragment),
                )
                return@launch
            }
//            ExecCmdIndexSizingInTermShort.execCmdIndexSizingInTermShort(
//                this@MainActivity,
//                isKeyboardShowing,
//            )
        }

    }


    override fun onKeyBoardVisibleChangeForEditFragment(
        isKeyboardShowing: Boolean,
        isVisible: Boolean
    ) {
        if(
            !isVisible
        ) return
        CoroutineScope(Dispatchers.Main).launch {
            if (
                !isKeyboardShowing
            ) currentFocus?.clearFocus()
            WrapFragmentManager.changeFragmentByKeyBoardVisibleChange(
                isKeyboardShowing,
                supportFragmentManager,
                getString(R.string.edit_terminal_fragment),
            )
        }
    }

    override fun onToolBarVisibleChange(
        toolBarVisible: Boolean,
        bottomFragment: Fragment?
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            when (bottomFragment) {
                is CommandIndexFragment -> {
                    ExecOnToolBarVisibleChange.execOnToolBarVisibleChange(
                        this@MainActivity,
                        bottomFragment,
                        toolBarVisible
                    )
                }

                is EditFragment -> {
                    ExecOnToolBarVisibleChangeForEdit.execOnToolBarVisibleChangeForEdit(
                        this@MainActivity,
                        bottomFragment,
                        toolBarVisible
                    )
                }

                else -> {}
            }
        }
    }


//    override fun onSearchTextChange(text: String) {
//        ExecOnSearchTextChange.execOnSearchTextChange(
//            this,
//            text
//        )
//    }


    override fun onListItemClicked(
        currentFragmentTag: String
    ) {
        ExecListItemClick.invoke(
            this,
        )
    }

    override fun onTermLongChangeForTerminalFragment(
        bottomFragment: Fragment?
    ) {
        ExecTermLongChangeHandlerForTerm.handle(
            this,
            bottomFragment,
        )
    }

    override fun onTermNormalSizeForTerminalFragment(terminalFragment: TerminalFragment) {
        ExecTermShortForTerm.short(
            this,
            terminalFragment,
        )
    }

    override fun onTermSizeLongForEdit(
        editFragment: EditFragment
    ) {
        ExecTermLongChangeHandlerForTerm.handle(
            this,
            editFragment,
        )
    }

    override fun onTermSizeMinimumForTerm() {
        ExecTermMinimumForEdit.min(
            this
        )
    }

    override fun onPageLoadPageSearchDisable() {
        ExecPageLoadPageSearchDisable.change(this)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        when(event?.action) {
            KeyEvent.ACTION_DOWN -> {
                ExecBackstackHandle.execBackstackHandle(
                    keyCode,
                    this,
                )
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onInitEditFragment() {
        RestartWhenPreferenceCheckErr.restartWhenPreferenceCheckErr(
            this,
        )
    }


    override fun onBackstackDelete(){
        FragmentManagerForActivity.deleteAllBackStack(supportFragmentManager)
    }

    override fun onLaunchUrlByWebView(
        searchUrl: String
    ) {
        ExecLoadUrlForWebView.execLoadUrlForWebView(
            this,
            searchUrl,
        )
    }


    override fun onLaunchUrlByWebViewForEdit(
        searchUrl: String
    ) {
        ExecLoadUrlForWebView.execLoadUrlForWebView(
            this,
            searchUrl,
        )
    }

    override fun onBackstackWhenTermLongInRestart() {
        supportFragmentManager.popBackStackImmediate()
    }


    override fun onPageSearchToolbarClick(
        pageSearchToolbarButtonVariant: PageSearchToolbarButtonVariant,
        tag: String?,
        searchText: String,
    ) {
        PageSearchToolbarHandler.handle(
            this,
            pageSearchToolbarButtonVariant,
            tag,
            searchText,
        )
    }

    override fun onFilterWebView(filterText: String) {
        ExecFilterWebView.invoke(
            this,
            filterText,
        )
    }


    override fun onFindPageSearchResultListner(
        activeMatchOrdinal: Int,
        numberOfMatches: Int
    ) {
//        ExecPageSearchResult.reflect(
//            this,
//            activeMatchOrdinal,
//            numberOfMatches
//        )
    }

    override fun onFileCoose(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ) {
        try {
            filePath = filePathCallback
            val contentIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentIntent.type = "*/*"
            contentIntent.addCategory(Intent.CATEGORY_OPENABLE)
            getFile.launch(contentIntent)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Cannot Open File Chooser",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onTerminalWebViewInitForEdit(
        editInitType: EditInitType,
    ) {
        ExecInitForEditFragment.execInitForEditFragment(
            this,
            editInitType,
        )
    }

    override fun onFileChooserListenerForEdit(
        onDirectoryPick: Boolean,
        insertEditText: EditText,
        chooserMap: Map<String, String>?,
        fannelName: String,
        currentVariableName: String,
    ) {
        ExecFileChooser.exec(
            this,
            storageHelper,
            onDirectoryPick,
            insertEditText,
            chooserMap,
            fannelName,
            currentVariableName,
        )
    }

    override fun onTextViewAndFannelForTermFragment(
        indexOrParentTagName: String,
        srcFragment: String,
        tagNameList: List<String>,
        updateText: String,
        isSave: Boolean,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            EditTextAndFannelUpdaterForTerm.update(
                this@MainActivity,
                indexOrParentTagName,
                srcFragment,
                tagNameList,
                updateText,
                isSave
            )
        }
    }

    override fun onTextViewAndMapListUpdateForTerm(
        editListIndex: Int,
        srcFragmentStr: String
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            EditTextAndMapListForTerm.update(
                this@MainActivity,
                editListIndex,
                srcFragmentStr,
            )
        }
    }

    override fun onEditableSpinnerUpdateForTermFragment(spinnerId: Int?, variableValue: String) {
        EditableSpinnerUpdaterForTerminalFragment.update(
            this,
            spinnerId,
            variableValue
        )
    }

//    override fun onSpinnerUpdateForTermFragment(spinnerId: Int?, variableValue: String) {
//        SpinnerUpdaterForTerminalFragment.update(
//            this,
//            spinnerId,
//            variableValue
//        )
//    }

    override fun onMultiSelectForEdit(
        variableName: String,
        editTextId: Int,
        updatedMultiModelArray: ArrayList<MultiSelectModel>,
        preSelectedMultiModelArray: ArrayList<Int>
    ) {
        MultiSelectDialogForEdit.show(
            this@MainActivity,
            variableName,
            editTextId,
            updatedMultiModelArray,
            preSelectedMultiModelArray
        )
    }

    override fun onMultiSelectForTerm(
        title: String,
        updatedMultiModelArray: ArrayList<MultiSelectModel>,
        preSelectedMultiModelArray: ArrayList<Int>
    ) {
        MultiSelectListContentsDialogForEdit.show(
            this,
            title,
            updatedMultiModelArray,
            preSelectedMultiModelArray
        )
    }

//    override fun onLongPressPlayOrEditButton(
//        editLongPressType: EditLongPressType,
//        tag: String?,
//        searchText: String,
//        pageSearchToolbarButtonVariant: PageSearchToolbarButtonVariant?
//    ) {
//        ExecOnLongPressPlayOrEditButton.handle(
//            this,
//            editLongPressType,
//            tag,
//            searchText,
//            pageSearchToolbarButtonVariant
//        )
//    }

    override fun onLongTermKeyBoardOpenAjustForEdit(
        weight: Float
    ) {
        ExecOnLongTermKeyBoardOpenAdjustForEdit.adjust(
            this,
            weight
        )
    }

    override fun onUpdateNoSaveUrlPaths(
//        currentAppDirPath: String,
        fannelName: String,
    ) {
        ExecUpdateNoSaveUrlPaths.update(
            this,
//            currentAppDirPath,
            fannelName
        )
    }

    override fun onUpdateNoSaveUrlPathsForEdit(
//        currentAppDirPath: String,
        fannelName: String,
    ) {
        ExecUpdateNoSaveUrlPaths.update(
            this,
//            currentAppDirPath,
            fannelName
        )
    }

    override fun exeOnAdblock() {
        adBlockJob?.cancel()
        adBlockJob = AdBlocker.init(this)
    }

    override fun onGetPermission(
        permissionStr: String
    ){
        ExecGetPermission.get(
            this,
            permissionStr
        )
    }

    override fun onGetPermissionForCmdIndex(
        permissionStr: String
    ){
        ExecGetPermission.get(
            this,
            permissionStr
        )
    }

    override fun onGetPermissionForEdit(permissionStr: String) {
        ExecGetPermission.get(
            this,
            permissionStr
        )
    }

    override fun onConnectWifiForCmdIndex(ssid: String, pin: String) {
        ExecWifiSet.set(
            this,
            ssid,
            pin
        )
    }

    override fun onChangeEditFragment(
        editFragmentArgs: EditFragmentArgs,
        cmdEditFragmentTag: String,
        editTerminalFragmentTag: String,
        disableAddToBackStack: Boolean,
    ) {
        ExecCommandEdit.execCommandEdit(
            this,
            cmdEditFragmentTag,
            editFragmentArgs,
            editTerminalFragmentTag,
            disableAddToBackStack,
        )
    }

    override fun onEditFannelContentsListUpdateForTerm(
        fannelInfoMap: Map<String, String>,
        updateScriptContents: List<String>,
    ) {
        ExecFannelConListUpdate.update(
            this,
            fannelInfoMap,
            updateScriptContents,
        )
    }

    override fun onMonitorSizeChangingForTerm(
        fannelInfoMap: Map<String, String>,
    ) {
        ExecMonitorSizeChangeForTerm.change(
            this,
            fannelInfoMap,
        )
    }

    override fun onPopStackImmediateForTerm() {
        this.supportFragmentManager.popBackStackImmediate()
    }

    override fun onSettingOkButtonForTerm() {
        ExecSettingOkButton.handle(this)
    }

//    override fun onGetFileForTerm(
//        parentDirPathSrc: String,
//        onDirectoryPickSrc: Boolean,
//        filterPrefixListCon: String,
//        filterSuffixListCon: String,
//        filterShellCon: String,
//        initialPath: String,
//        pickerMacro: FilePickerTool.PickerMacro?,
//        currentFannelName: String,
//        tag: String,
//    ) {
//        getFileForEdit.get(
//            parentDirPathSrc,
//            filterPrefixListCon,
//            filterSuffixListCon,
//            filterShellCon,
//            initialPath,
//            onDirectoryPickSrc,
//            pickerMacro,
//            currentFannelName,
//            tag,
//        )
//    }

    override fun onGetFileListForTerm(
        onDirectoryPickSrc: Boolean,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        filterShellPathCon: String,
        initialPath: String,
        pickerMacro: FilePickerTool.PickerMacro?,
        currentFannelName: String,
        tag: String,
    ) {
        getFileListForEdit.get(
            filterPrefixListCon,
            filterSuffixListCon,
            filterShellPathCon,
            initialPath,
            onDirectoryPickSrc,
            pickerMacro,
            currentFannelName,
            tag,
        )
    }

    override fun getSdcardDirForTerm(
        isCreate: Boolean
    ) {
        sdcardDirGetter.handle(
            this,
            isCreate,
        )
    }

    override fun onCaptureActivityForIndex() {
        val startUpPref = FannelInfoTool.getSharePref(this)
        FannelHistoryCaptureTool.getCapture(
            startUpPref,
            activityMainBinding.rootContainer
        )
    }

    override fun onCaptureActivityForEdit() {
        val startUpPref = FannelInfoTool.getSharePref(this)
        FannelHistoryCaptureTool.getCapture(
            startUpPref,
            activityMainBinding.rootContainer
        )
    }

    override fun onCaptureActivityForTerm() {
        val startUpPref = FannelInfoTool.getSharePref(this)
        FannelHistoryCaptureTool.getCapture(
            startUpPref,
            activityMainBinding.rootContainer
        )
    }

    override fun onRestartForTerm() {
        ExecSimpleRestartActivity.execSimpleRestartActivity(this)
    }

    override fun onSetToolbarButtonImage() {
        ExecSetToolbarButtonImage.set(
            this
        )
    }

    override fun onZeroSizing() {
    }

    override fun onSearchButtonMakeForCmdIndex() {
        SearchButtonClickListener.handle(
            this,
            false,
        )
    }

    override fun onSearchButtonMakeForTerm() {
        SearchButtonClickListener.handle(
            this,
            true,
        )
    }

    override fun onKeyboardHandleForCmdIndex(
        isOpen: Boolean
    ) {
        ToolbarCtrlForCmdIndex.hideShow(
            this,
            isOpen,
            false,
        )
    }

    override fun onKeyboardHandleForTerm(isOpen: Boolean) {
        ToolbarCtrlForCmdIndex.hideShow(
            this,
            isOpen,
            true,
        )
    }

    override fun onSelectionSearchBarSwitchForTerm(
        isShow: Boolean
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            ToolbarCtrlForCmdIndex.hideShowForTextSelection(
                this@MainActivity,
                isShow
            )
        }
    }

    override fun onUpdateSelectionTextViewForTerm(updateText: String) {
        CoroutineScope(Dispatchers.Main).launch {
            ToolbarCtrlForCmdIndex.updateTextSelection(
                this@MainActivity,
                updateText,
            )
        }
    }

    override fun onPinFannelHide(
        fannelManagerPinImageView: AppCompatImageView?
    ) {
        PinFannelHideShow.execHideShow(
            this,
            true,
            fannelManagerPinImageView,
        )
    }

    override fun onPinFannelShow(
        fannelManagerPinImageView: AppCompatImageView?
    ) {
        PinFannelHideShow.execHideShow(
            this,
            false,
            fannelManagerPinImageView,
        )
    }

    override fun onPageSearchSwitch() {
        PageSearchManager.switch(
            this
        )
    }

    override fun onPageSearchSwitchForTerm() {
        PageSearchManager.switch(
            this
        )
    }

}
