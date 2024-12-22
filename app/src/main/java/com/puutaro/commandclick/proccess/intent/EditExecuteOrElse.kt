package com.puutaro.commandclick.proccess.intent


import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.UrlImageDownloader
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click.lib.OnEditExecuteEvent
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.common.DecideEditTag
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.LongClickMenuItemsforCmdIndex
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.ToolbarMenuCategoriesVariantForCmdIndex
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryAdminEvent
import com.puutaro.commandclick.proccess.history.fannel_history.FannelHistoryJsEvent
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.FannelStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

object EditExecuteOrElse {

    private val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath

    fun handle(
        fragment: Fragment,
        fannelName: String
    ){
        val context = fragment.context
        val mainFannelConList = ReadText(
            File(cmdclickDefaultAppDirPath, fannelName).absolutePath
        ).textToList()
        val setReplaceVariableMap = when(
            fannelName.isEmpty()
        ) {
            true -> null
            else -> JavaScriptLoadUrl.createMakeReplaceVariableMapHandler(
                context,
                mainFannelConList,
                fannelName,
            )
        }

        val mainFannelSettingConList = CommandClickVariables.extractSettingValListByFannelName(
            mainFannelConList,
//            fannelName
        ).let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it?.joinToString("\n") ?: String(),
                setReplaceVariableMap,
//                        appDirName,
                fannelName,
            ).split("\n")
        }
        val isMustUbuntuSetup =
            CommandClickVariables.substituteCmdClickVariable(
                mainFannelSettingConList,
                CommandClickScriptVariable.MUST_UBUNTU_SETUP,
            ) == SettingVariableSelects.MustUbuntuSetupSelects.ON.name
        ubuntuSetupAlertHandler(
            context,
            isMustUbuntuSetup
        ).let {
            isAlert ->
            if(isAlert) return
        }
        val isEditExecute =
            CommandClickVariables.substituteCmdClickVariable(
                mainFannelSettingConList,
                CommandClickScriptVariable.EDIT_EXECUTE,
            ) == SettingVariableSelects.EditExecuteSelects.ALWAYS.name
                    || fannelName == SystemFannel.home
        when (isEditExecute) {
            true -> {
                execEditExecute(
                    fragment,
                    fannelName,
                    mainFannelConList,
                    mainFannelSettingConList,
                    setReplaceVariableMap
                )
                return
            }
            else -> ExecJsOrSellHandler.handle(
                fragment,
                fannelName,
                mainFannelSettingConList,
            )
        }
    }

    private fun execEditExecute(
        fragment: Fragment,
        fannelName: String,
        mainFannelContentsList: List<String>,
        mainFannelSettingConList: List<String>,
        setReplaceVariableMap: Map<String, String>?
    ){
        val isNotHome = fannelName != SystemFannel.home
        val isIndex =
            fragment is CommandIndexFragment
                    || isIndexTerminal(fragment)
        val isEditTrans = isIndex && isNotHome
        val context = fragment.context
        when(isEditTrans) {
            true -> {
                val fannelState = FannelStateManager.getState(
                    context,
                    fannelName,
                    mainFannelSettingConList,
                    setReplaceVariableMap,
                )
                val editFragmentTag = DecideEditTag(
                    mainFannelContentsList,
                    fannelName,
                    fannelState
                ).decide() ?: return
                OnEditExecuteEvent.invoke(
                    fragment,
                    editFragmentTag,
                    fannelName,
                    fannelState
                )
            }
             else -> {
                 val sharePref = FannelInfoTool.getSharePref(fragment.context)
                 FannelHistoryAdminEvent.register(
                     context,
                     sharePref,
                     fannelName,
                     mainFannelSettingConList,
                     setReplaceVariableMap,
                 )
                 launchHandler(
                     fragment,
                     fannelName,
                     mainFannelSettingConList,
                     setReplaceVariableMap
                 )
             }
        }
    }

    private fun isIndexTerminal(
        fragment: Fragment
    ): Boolean {
        if(
            fragment !is TerminalFragment
        ) return false
        val tag = fragment.tag
            ?: return false
        return tag == fragment.context?.getString(R.string.index_terminal_fragment)
    }

    private fun launchHandler(
        fragment: Fragment,
        fannelName: String,
        mainFannelSettingConList: List<String>?,
        setReplaceVariableMap: Map<String, String>?
    ){
        val context = fragment.context
        val isJsExec = FannelHistoryJsEvent.run(
            fragment,
            fannelName
        )
        val fannelState = FannelStateManager.getState(
            context,
            fannelName,
            mainFannelSettingConList,
            setReplaceVariableMap
        )
        val fannelInfoMap = EditFragmentArgs.createFannelInfoMap(
            fannelName,
            EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key,
            fannelState
        )
        val cmdValEdit =
            EditFragmentArgs.Companion.EditTypeSettingsKey.CMD_VAL_EDIT
        val jsExecWaitTime =
            if(isJsExec) 200L
            else 200L
        CoroutineScope(Dispatchers.Main).launch {
            delay(jsExecWaitTime)
            when(fragment) {
                is CommandIndexFragment -> {
                    FileSystems
                    val listener = context
                            as? CommandIndexFragment.OnLongClickMenuItemsForCmdIndexListener
                    listener?.onLongClickMenuItemsforCmdIndex(
                        LongClickMenuItemsforCmdIndex.EXEC_HISTORY,
                        EditFragmentArgs(
                            fannelInfoMap,
                            cmdValEdit,
                        ),
                        String(),
                        String()

                    )
                }
                is TerminalFragment -> {
                    val listener = context as? TerminalFragment.OnRestartListenerForTerm
                    listener?.onRestartForTerm()
                }
                else -> {
                    val listener = context as? EditFragment.OnToolbarMenuCategoriesListenerForEdit
                    listener?.onToolbarMenuCategoriesForEdit(
                        ToolbarMenuCategoriesVariantForCmdIndex.HISTORY,
                        EditFragmentArgs(
                            fannelInfoMap,
                            cmdValEdit,
                        )
                    )
                }
            }
        }
    }

    private fun ubuntuSetupAlertHandler(
        context: Context?,
        isMustUbuntuSetup: Boolean,
    ): Boolean {
        if(
            !isMustUbuntuSetup
        ) return false
        if(
            context == null
        ) return false
        val okSetup = UbuntuFiles(context).ubuntuSetupCompFile.isFile
        if(
            okSetup
        ) return false
        UbuntuSetupAlert.get(context)
        return true

    }

    private object UbuntuSetupAlert{
        private var ubuntuSetupAlertDialog: Dialog? = null

        fun get(context: Context) {
            ubuntuSetupAlertDialog = Dialog(
                context
            )
            ubuntuSetupAlertDialog?.setContentView(
                R.layout.only_alert_dialog_layout
            )
            ubuntuSetupAlertDialog?.findViewById<AppCompatImageView>(
                R.id.only_alert_dialog_bk_image
            )?.let {
                val ubuntuSetupProcedureGifByteArray =
                    FileSystems.convertFileToByteArray(
                        File(
                            UrlImageDownloader.imageDirPrefix,
                            UrlImageDownloader.ubuntuAlertGifSuffix
                        ).absolutePath
                    )
//                    AssetsFileManager.assetsByteArray(
//                    context,
//                    AssetsFileManager.ccRoboGifPath
//                )
                val requestBuilder: RequestBuilder<Drawable> =
                    Glide.with(context)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                Glide
                    .with(it.context)
                    .load(ubuntuSetupProcedureGifByteArray)
                    .placeholder(R.mipmap.ic_cmdclick_launcher)
                    .thumbnail(requestBuilder)
                    .centerCrop()
                    .into(it)
            }
            val confirmContentTextView =
                ubuntuSetupAlertDialog?.findViewById<OutlineTextView>(
                    R.id.only_alert_dialog_text_view
                )
            confirmContentTextView?.strokeWidthSrc = 5
            confirmContentTextView?.text =
                "Setup or restore ubuntu on notification bar, ok?"
//            "\n".repeat(2) +
            val okButton =
                ubuntuSetupAlertDialog?.findViewById<AppCompatImageView>(
                    R.id.only_alert_dialog_ok
                )
            CoroutineScope(Dispatchers.Main).launch {
                ExecSetToolbarButtonImage.setImageButton(
                    okButton,
                    CmdClickIcons.OK,
                )
            }

            okButton?.setOnClickListener {
                ubuntuSetupAlertDialog?.dismiss()
                ubuntuSetupAlertDialog = null
            }
            ubuntuSetupAlertDialog?.setOnCancelListener {
                ubuntuSetupAlertDialog?.dismiss()
                ubuntuSetupAlertDialog = null
            }
            ubuntuSetupAlertDialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            ubuntuSetupAlertDialog?.window?.setGravity(
                Gravity.CENTER
            )
            ubuntuSetupAlertDialog?.show()
        }
    }
}