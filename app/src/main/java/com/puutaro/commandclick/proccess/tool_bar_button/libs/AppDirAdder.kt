package com.puutaro.commandclick.proccess.tool_bar_button.libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.component.adapter.ListIndexForEditAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.list_index_for_edit.ListIndexEditConfig
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.TypeSettingsForListIndex
import com.puutaro.commandclick.util.file.FileSystems
import java.io.File

object AppDirAdder {

    private var promptDialog: Dialog? = null

    fun add(
        toolbarButtonArgsMaker: ToolbarButtonArgsMaker,
    ){
        val fragment = toolbarButtonArgsMaker.fragment
        if(
            fragment !is EditFragment
        ) return
        val context = fragment.context
            ?: return
        val type = ListIndexEditConfig.getListIndexType(
            fragment
        )
        when(type){
            TypeSettingsForListIndex.ListIndexTypeKey.INSTALL_FANNEL,
            TypeSettingsForListIndex.ListIndexTypeKey.TSV_EDIT -> {
                return
            }
            TypeSettingsForListIndex.ListIndexTypeKey.NORMAL
            -> {}
        }
        promptDialog = Dialog(
            context
        )
        promptDialog?.setContentView(
            R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Input create app directory name"
        val promptMessageTextView =
            promptDialog?.findViewById<AppCompatTextView>(
                R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            promptDialog?.findViewById<AutoCompleteTextView>(
                R.id.prompt_dialog_input
            )
        val promptCancelButton =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            promptDialog?.dismiss()
        }
        val promptOkButtonView =
            promptDialog?.findViewById<AppCompatImageButton>(
                R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            promptDialog?.dismiss()
            val inputScriptFileName = promptEditText?.text.toString()
            val jsFileSuffix = UsePath.JS_FILE_SUFFIX
            val isJsSuffix = inputScriptFileName.endsWith(jsFileSuffix)
            val scriptFileName = if (
                isJsSuffix
            ) inputScriptFileName
            else inputScriptFileName + jsFileSuffix

            CommandClickScriptVariable.makeAppDirAdminFile(
                UsePath.cmdclickAppDirAdminPath,
                scriptFileName
            )
            val addAppDirNameFilePath = File(
                UsePath.cmdclickAppDirAdminPath,
                scriptFileName
            ).absolutePath
            ListIndexForEditAdapter.sortInAddFile(
                fragment,
                addAppDirNameFilePath
            )

//            val settingButtonMenuMapList = toolbarButtonArgsMaker.makeSettingButtonMenuMapList()
//            val extraMap = ExtraArgsTool.createExtraMapFromMenuMapList(
//                settingButtonMenuMapList,
//                JsPathMacroForSettingButton.ADD_APP_DIR.name,
//                MenuSettingTool.MenuSettingKey.JS_PATH.key,
//                "!",
//            )
//            val updateBroadcastIntent = ExtraArgsTool.makeBroadcastIntent(
//                extraMap,
//                "&"
//            )
//            context.sendBroadcast(updateBroadcastIntent)


            val createAppDirName = when (isJsSuffix) {
                true -> inputScriptFileName.removeSuffix(jsFileSuffix)
                else -> inputScriptFileName
            }
            val createAppDirPath = "${UsePath.cmdclickAppDirPath}/${createAppDirName}"
            FileSystems.createDirs(
                createAppDirPath
            )
            FileSystems.createDirs(
                "${createAppDirPath}/${UsePath.cmdclickUrlSystemDirRelativePath}"
            )
        }
        promptDialog?.setOnCancelListener {
            promptDialog?.dismiss()
        }
        promptDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        promptDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialog?.show()
    }
}