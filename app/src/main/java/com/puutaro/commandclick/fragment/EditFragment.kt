package com.puutaro.commandclick.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.*
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.UpdatelastModifyForEdit
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.fragment_lib.edit_fragment.*
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDo
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.TerminalShowByTerminalDoWhenReuse
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidationSharePreferenceForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditInitType
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.util.*
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Job
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import java.io.File


class EditFragment: Fragment() {

    private var _binding: EditFragmentBinding? = null
    val binding get() = _binding!!
    var languageType = LanguageTypeSelects.JAVA_SCRIPT
    var languageTypeToSectionHolderMap =
            CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
                languageType
            )
    var settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
    ) as String

    var settingSectionEnd = languageTypeToSectionHolderMap?.get(
    CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
    ) as String

    var commandSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickShellScript.Companion.HolderTypeName.CMD_SEC_START
    ) as String
    var commandSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickShellScript.Companion.HolderTypeName.CMD_SEC_END
    ) as String
    var runShell = CommandClickShellScript.CMDCLICK_RUN_SHELL_DEFAULT_VALUE
    var historySwitch =  SettingVariableSelects.Companion.HistorySwitchSelects.OFF.name
    var urlHistoryOrButtonExec = CommandClickShellScript.CMDCLICK_URL_HISTOTY_OR_BUTTON_EXEC_DEFAULT_VALUE
    var shiban = CommandClickShellScript.CMDCLICK_SHIBAN_DEFAULT_VALUE
    var fontZoomPercent = CommandClickShellScript.CMDCLICK_TERMINAL_FONT_ZOOM_DEFAULT_VALUE
    var terminalOn = CommandClickShellScript.TERMINAL_DO_DEFAULT_VALUE
    var terminalColor = CommandClickShellScript.TERMINAL_COLOR_DEFAULT_VALUE
    var statusBarIconColorMode = CommandClickShellScript.STATUS_BAR_ICON_COLOR_MODE_DEFAULT_VALUE
    var editTerminalInitType = EditInitType.TERMINAL_SHRINK
    var jsExecuteJob: Job? = null
    var popBackStackToIndexImmediateJob: Job? = null
    var readSharePreffernceMap: Map<String, String> = mapOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        val getIntent = activity?.intent
        Keyboard.hiddenKeyboardForFragment(
            this
        )
        SetConfigInfo.set(this)
        val sharePref = activity?.getPreferences(Context.MODE_PRIVATE)
        val validationSharePreferenceForEdit = ValidationSharePreferenceForEdit(
            this,
            sharePref
        )

        val apiEditFragmentTag = getString(
            R.string.api_cmd_variable_edit_api_fragment
        )
        val cmdConfigVariableEditFragment = getString(
            R.string.cmd_config_variable_edit_fragment
        )
        val howConfigEdit = (
                tag == cmdConfigVariableEditFragment
                )
        val howEditApi = (
                tag == apiEditFragmentTag
                )
        if(howEditApi){
            val currentAppDirPathSource = getIntent?.getStringExtra(
                SharePrefferenceSetting.current_app_dir.name
            )
            val currentShellFileNameSource = getIntent?.getStringExtra(
                SharePrefferenceSetting.current_script_file_name.name
            )
            val checkOkForAppDirPath = validationSharePreferenceForEdit.checkCurrentAppDirPreference(
                currentAppDirPathSource
            )
            if(!checkOkForAppDirPath) return
            val checkOkForShellName = validationSharePreferenceForEdit.checkCurrentShellNamePreference(
                currentAppDirPathSource,
                currentShellFileNameSource
            )
            if(!checkOkForShellName) return
        } else if(!howConfigEdit) {
            val checkOkForAppDirPath = validationSharePreferenceForEdit.checkCurrentAppDirPreference()
            if(!checkOkForAppDirPath) return
            val checkOkForShellName = validationSharePreferenceForEdit.checkCurrentShellNamePreference()
            if(!checkOkForShellName) return
        }

        readSharePreffernceMap = MakeReadPreffernceMapForEdit.make(
            getIntent,
            howConfigEdit,
            howEditApi,
            sharePref,
            tag,
            apiEditFragmentTag
        )

        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentShellFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )

        languageType =
            JsOrShellFromSuffix.judge(currentShellFileName)

        val languageTypeToSectionHolderMap =
            CommandClickShellScript.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(languageType)
        settingSectionStart = languageTypeToSectionHolderMap?.get(
            CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_START
        ) as String
        settingSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickShellScript.Companion.HolderTypeName.SETTING_SEC_END
        ) as String

        commandSectionStart = languageTypeToSectionHolderMap.get(
            CommandClickShellScript.Companion.HolderTypeName.CMD_SEC_START
        ) as String
        commandSectionEnd = languageTypeToSectionHolderMap.get(
            CommandClickShellScript.Companion.HolderTypeName.CMD_SEC_END
        ) as String

        if(
            UpdatelastModifyForEdit().judge(currentAppDirPath)
            && tag != apiEditFragmentTag
        ) {
            FileSystems.updateLastModified(
                UsePath.cmdclickAppDirAdminPath,
                File(currentAppDirPath).name + CommandClickShellScript.JS_FILE_SUFFIX
            )
            FileSystems.updateLastModified(
                currentAppDirPath,
                currentShellFileName
            )
            ConfigFromShellFileSetter.set(
                this,
                readSharePreffernceMap
            )
        }

        val backstackOrder =
            this.activity?.supportFragmentManager?.getBackStackEntryCount() ?: 0
        val currentShellFilePath =
            "(${backstackOrder}) ${UsePath.makeOmitPath(currentAppDirPath)}/${currentShellFileName}"
        binding.editTextView.text = currentShellFilePath

        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        context?.let {
            window?.statusBarColor = Color.parseColor(terminalColor)
        }

        val editModeHandler = EditModeHandler(
            this,
            binding,
            readSharePreffernceMap
        )
        editModeHandler.execByHowFullEdit()
        val terminalViewModel: TerminalViewModel by activityViewModels()
        val listener = context as? EditFragment.OnKeyboardVisibleListenerForEditFragment
        KeyboardVisibilityEvent.setEventListener(activity) {
                isOpen ->
            if(!this.isVisible) return@setEventListener
            if(terminalViewModel.onDialog) return@setEventListener
            if(
                terminalViewModel.readlinesNum != ReadLines.SHORTH
                && tag != cmdConfigVariableEditFragment
            ) {
                binding.editTextScroll.isVisible = !isOpen
                binding.editToolBar.isVisible = !isOpen
                if(!isOpen) activity?.currentFocus?.clearFocus()
                return@setEventListener
            }
            listener?.onKeyBoardVisibleChangeForEditFragment(
                isOpen,
                this.isVisible
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val shellScriptContentsList = ReadText(
            SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_app_dir
            ),
            SharePreffrenceMethod.getReadSharePreffernceMap(
                readSharePreffernceMap,
                SharePrefferenceSetting.current_script_file_name
            )
        ).textToList()
        TerminalShowByTerminalDoWhenReuse.show(
            this,
            shellScriptContentsList
        )
    }

    override fun onPause() {
        super.onPause()
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
            isLongPress: Boolean = false
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
            toolbarMenuCategoriesVariantForCmdIndex: ToolbarMenuCategoriesVariantForCmdIndex
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

}