package com.puutaro.commandclick.proccess.setting_button.libs

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.AddScriptHandler
import com.puutaro.commandclick.proccess.setting_button.JsPathMacroForSettingButton
import com.puutaro.commandclick.proccess.setting_button.SettingButtonMenuMapKey
import com.puutaro.commandclick.util.FileSystems

object AddFileForEdit {

    private var promptDialog: Dialog? = null

    fun add(
        fragment: Fragment,
        currentAppDirPath: String,
        settingMenuMapList: List<Map<String, String>?>,
    ){
        when(fragment) {
            is CommandIndexFragment -> {
                AddScriptHandler(
                    fragment,
                    currentAppDirPath,
                ).handle()
            }
            is EditFragment -> {
                execAddItemForEdit(
                    fragment,
                    currentAppDirPath,
                    settingMenuMapList,
                )
            }
        }
    }

    private fun execAddItemForEdit(
        fragment: Fragment,
        currentAppDirPath: String,
        settingMenuMapList: List<Map<String, String>?>,
    ){
        val context = fragment.context
            ?: return
        val jsPathKey = SettingButtonMenuMapKey.JS_PATH.str
        val addJSMacroStr = JsPathMacroForSettingButton.ADD.name
        val currentSettingMenuMap = settingMenuMapList.filter {
            it?.get(jsPathKey) == addJSMacroStr
        }.firstOrNull()
        if(
            currentSettingMenuMap.isNullOrEmpty()
        ) return
        val extraMap = ExtraMapTool.createExtraMap(
            JsPathMacroForSettingButton.ADD.name,
            settingMenuMapList
        )
        val parentDirPath =
            ExtraMapTool.getParentDirPath(
                extraMap,
                currentAppDirPath
            )
        val broadcastIntent = ExtraMapTool.makeBroadcastIntent(extraMap)

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
        promptTitleTextView?.text = "Type item name"
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
            val inputEditable = promptEditText?.text
            if(
                inputEditable.isNullOrEmpty()
            ) {
                Toast.makeText(
                    context,
                    "No type item name",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val compFileName = ExtraMapTool.makeCompFileName(
                inputEditable.toString(),
                extraMap,
            )
            FileSystems.writeFile(
                parentDirPath,
                compFileName,
                String()
            )
            broadcastIntent?.let {
                context.sendBroadcast(it)
            }
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