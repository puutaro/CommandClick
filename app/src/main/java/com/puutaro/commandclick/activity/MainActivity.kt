package com.puutaro.commandclick.activity

import android.app.Activity
import android.content.*
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.abdeveloper.library.MultiSelectDialog
import com.abdeveloper.library.MultiSelectModel
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.getAbsolutePath
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.InitManager
import com.puutaro.commandclick.activity_lib.NotifierSetter
import com.puutaro.commandclick.activity_lib.RunCommandSetter
import com.puutaro.commandclick.activity_lib.event.*
import com.puutaro.commandclick.activity_lib.event.lib.ExecInitForEditFragment
import com.puutaro.commandclick.activity_lib.event.lib.cmdIndex.*
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecBackstackHandle.Companion.execBackstackHandle
import com.puutaro.commandclick.activity_lib.event.lib.common.RestartWhenPreferenceCheckErr
import com.puutaro.commandclick.activity_lib.event.lib.edit.ExecOnToolBarVisibleChangeForEdit
import com.puutaro.commandclick.activity_lib.event.lib.edit.MultiSelectDialogForEdit
import com.puutaro.commandclick.activity_lib.event.lib.edit.MultiSelectListContentsDialogForEdit
import com.puutaro.commandclick.activity_lib.event.lib.terminal.EditTextUpdaterForTerminalFragment
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecFilterWebView
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecPageSearchResult
import com.puutaro.commandclick.activity_lib.event.lib.terminal.SpinnerUpdaterForTerminalFragment
import com.puutaro.commandclick.activity_lib.init.MonitorFiles
import com.puutaro.commandclick.activity_lib.manager.WrapFragmentManager
import com.puutaro.commandclick.activity_lib.manager.curdForFragment.FragmentManagerForActivity
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.databinding.ActivityMainBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.PageSearchToolbarButtonVariant
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditInitType
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.fragment_lib.terminal_fragment.variable.ChangeTargetFragment
import com.puutaro.commandclick.service.GitCloneService
import com.puutaro.commandclick.util.TargetFragmentInstance


class MainActivity:
    AppCompatActivity(),
    TerminalFragment.OnSearchTextChangeListener,
    TerminalFragment.onBackstackWhenTermLongInRestartListener,
    TerminalFragment.OnToolBarVisibleChangeListener,
    TerminalFragment.OnTermLongChangeListenerForTerminalFragment,
    TerminalFragment.OnPageLoadPageSearchDisableListener,
    TerminalFragment.OnFindPageSearchResultListener,
    TerminalFragment.OnFileChooseListener,
    TerminalFragment.OnEditTextUpdateListenerForTermFragment,
    TerminalFragment.OnSpinnerUpdateListenerForTermFragment,
    TerminalFragment.OnMultiSelectListenerForTerm,
    CommandIndexFragment.OnListItemClickListener,
    CommandIndexFragment.OnKeyboardVisibleListener,
    CommandIndexFragment.OnToolbarMenuCategoriesListener,
    CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener,
    CommandIndexFragment.OnBackstackDeleteListener,
    CommandIndexFragment.OnLaunchUrlByWebViewListener,
    CommandIndexFragment.OnFilterWebViewListener,
    CommandIndexFragment.OnPageSearchToolbarClickListener,
    EditFragment.onToolBarButtonClickListenerForEditFragment,
    EditFragment.OnKeyboardVisibleListenerForEditFragment,
    EditFragment.OnToolbarMenuCategoriesListenerForEdit,
    EditFragment.OnInitEditFragmentListener,
    EditFragment.OnTerminalWebViewInitListenerForEdit,
    EditFragment.OnLaunchUrlByWebViewForEditListener,
    EditFragment.OnFileChooserListenerForEdit,
    EditFragment.OnTermSizeLongListenerForEdit,
    EditFragment.OnMultiSelectListenerForEdit {

    lateinit var activityMainBinding: ActivityMainBinding
    var filePath: ValueCallback<Array<Uri>>? = null
    var savedInstanceStateVal: Bundle? = null
    val getFile = registerForActivityResult(
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
    private val storageHelper = SimpleStorageHelper(this)

    val getRunCommandPermissionAndStartFragmentLauncher =
        RunCommandSetter.set(this)

    val getNotifierSetterLaunch =
        NotifierSetter.set(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceStateVal = savedInstanceState
        val actionBar = supportActionBar
        actionBar?.hide();
        volumeControlStream = AudioManager.STREAM_MUSIC


        InitManager(this).invoke()
    }


    override fun onStart() {
        super.onStart()
        MonitorFiles.trim(
            UsePath.cmdClickMonitorFileName_1
        )
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, GitCloneService::class.java)
        this.stopService(intent)
    }


    override fun onLongClickMenuItemsforCmdIndex(
        longClickMenuItemsforCmdIndex: LongClickMenuItemsforCmdIndex,
        editFragmentTag: String?,
        onOpenTerminal: Boolean,
        terminalFragmentTag: String?
    ) {
        if(editFragmentTag == null) return
        ExecLongClickMenuItemsforCmdIndex.execLongClickMenuItemsforCmdIndex(
            this,
            longClickMenuItemsforCmdIndex,
            editFragmentTag,
            onOpenTerminal,
            terminalFragmentTag
        )
    }


    override fun onToolbarMenuCategories(
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex
    ) {
        ExecToolbarMenuCategoriesForCmdIndex.execToolbarMenuCategories<CommandIndexFragment>(
            this,
            getString(R.string.command_index_fragment),
            toolbarMenuCategoriesVariantForCmdIndex
        )
    }

    override fun onToolbarMenuCategoriesForEdit(
        toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex
    ) {
        ExecToolbarMenuCategoriesForCmdIndex.execToolbarMenuCategories<EditFragment>(
            this,
            getString(R.string.cmd_variable_edit_fragment),
            toolbarMenuCategoriesVariantForCmdIndex
        )
    }

    override fun onToolBarButtonClickForEditFragment(
        callOwnerFragmentTag : String?,
        toolbarButtonBariantForEdit: ToolbarButtonBariantForEdit,
        readSharePreffernceMap: Map<String, String>,
        enableCmdEdit: Boolean,
        isLongPress: Boolean
    ){
        ExecToolBarButtonClickForEdit.execToolBarButtonClickForEdit(
            this,
            callOwnerFragmentTag,
            toolbarButtonBariantForEdit,
            readSharePreffernceMap,
            enableCmdEdit,
            isLongPress
        )
    }


    override fun onKeyBoardVisibleChange(
        isKeyboardShowing: Boolean,
        isVisible: Boolean,
        SpecialSearchSwitch: Boolean
    ){
        if(!isVisible) return
        if(SpecialSearchSwitch) {
            WrapFragmentManager.changeFragmentByKeyBoardVisibleChange(
                isKeyboardShowing,
                supportFragmentManager,
                getString(R.string.index_terminal_fragment),
            )
            return
        }
        ExecCmdIndexSizingInTermShort.execCmdIndexSizingInTermShort(
            this,
            isKeyboardShowing,
        )

    }


    override fun onKeyBoardVisibleChangeForEditFragment(
        isKeyboardShowing: Boolean,
        isVisible: Boolean
    ) {
        if(!isVisible) return
        if(!isKeyboardShowing) currentFocus?.clearFocus()
        WrapFragmentManager.changeFragmentByKeyBoardVisibleChange(
            isKeyboardShowing,
            supportFragmentManager,
            getString(R.string.edit_execute_terminal_fragment),
        )
    }

    override fun onToolBarVisibleChange(
        toolBarVisible: Boolean,
        changeTargetFragmentSelects: ChangeTargetFragment?
    ) {
        when(changeTargetFragmentSelects) {
            ChangeTargetFragment.CMD_INDEX_FRAGMENT -> {
                ExecOnToolBarVisibleChange.execOnToolBarVisibleChange(
                    this,
                    toolBarVisible
                )
            }
            ChangeTargetFragment.CMD_VARIABLES_EDIT_FRAGMENT -> {
                ExecOnToolBarVisibleChangeForEdit.execOnToolBarVisibleChangeForEdit(
                    this,
                    toolBarVisible
                )
            }
            else ->{}
        }
    }


    override fun onSearchTextChange(text: String) {
        ExecOnSearchTextChange.execOnSearchTextChange(
            this,
            text
        )
    }


    override fun onListItemClicked(
        currentFragmentTag: String
    ) {
        ExecListItemClick.invoke(
            this,
            currentFragmentTag
        )
    }

    override fun onTermLongChangeForTerminalFragment(
        changeTargetFragment: ChangeTargetFragment?
    ) {
        ExecTermLongChangeHandlerForTerm.handle(
            this,
            changeTargetFragment,
        )
    }

    override fun onTermSizeLongForEdit() {
        ExecTermLongChangeHandlerForTerm.handle(
            this,
            ChangeTargetFragment.CMD_VARIABLES_EDIT_FRAGMENT,
        )
    }

    override fun onPageLoadPageSearchDisable() {
        ExecPageLoadPageSearchDisable.change(this)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        when(event?.action) {
            KeyEvent.ACTION_DOWN -> {
                execBackstackHandle(
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
        val fragmentManagerForActivity = FragmentManagerForActivity(
            supportFragmentManager
        )
        fragmentManagerForActivity.deleteAllBackStack()
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
        searchText: String
    ) {
        PageSearchToolbarHandler.handle(
            this,
            pageSearchToolbarButtonVariant,
            searchText

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
        ExecPageSearchResult.reflect(
            this,
            activeMatchOrdinal,
            numberOfMatches
        )
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
        insertEditText: EditText
    ) {
        if (onDirectoryPick) {
            storageHelper.openFolderPicker()
            storageHelper.onFolderSelected = { requestCode, folder ->
                val absolutePath = folder.getAbsolutePath(this)
                insertEditText.setText(absolutePath)
            }
            return
        }
        storageHelper.openFilePicker()
        storageHelper.onFileSelected = { requestCode, file ->
            val absolutePath = file.getOrNull(0)?.getAbsolutePath(this)
            insertEditText.setText(absolutePath)
        }
    }

    override fun onEditTextUpdateForTermFragment(
        editTextId: Int?,
        variableValue: String
    ) {
        EditTextUpdaterForTerminalFragment.update(
            this,
            editTextId,
            variableValue
        )
    }

    override fun onSpinnerUpdateForTermFragment(spinnerId: Int?, variableValue: String) {
        SpinnerUpdaterForTerminalFragment.update(
            this,
            spinnerId,
            variableValue
        )
    }

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

}
