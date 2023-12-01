package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.app.Dialog
import android.content.SharedPreferences
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.component.adapter.SubMenuAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment

class AddScriptHandler(
    private val cmdIndexFragment: CommandIndexFragment,
    private val sharedPref: SharedPreferences?,
    private val currentAppDirPath: String,
) {
    val context = cmdIndexFragment.context
    val binding = cmdIndexFragment.binding
    private var languageSelectDialog: Dialog? = null
    private val icons8Wheel = com.puutaro.commandclick.R.drawable.icons8_wheel

    fun handle(){
        if(context == null)  return

        val languageSelectList = LanguageTypeSelects.values().map {
            it.str
        }.map {
            it to icons8Wheel
        }

        languageSelectDialog = Dialog(
            context
        )
        languageSelectDialog?.setContentView(
            com.puutaro.commandclick.R.layout.list_dialog_layout
        )
        val listDialogTitle = languageSelectDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_title
        )
        listDialogTitle?.text = "Select add script language"
        val listDialogMessage = languageSelectDialog?.findViewById<AppCompatTextView>(
            com.puutaro.commandclick.R.id.list_dialog_message
        )
        listDialogMessage?.isVisible = false
        val listDialogSearchEditText = languageSelectDialog?.findViewById<AppCompatEditText>(
            com.puutaro.commandclick.R.id.list_dialog_search_edit_text
        )
        listDialogSearchEditText?.isVisible = false
        val cancelButton = languageSelectDialog?.findViewById<AppCompatImageButton>(
            com.puutaro.commandclick.R.id.list_dialog_cancel
        )
        cancelButton?.setOnClickListener {
            languageSelectDialog?.dismiss()
        }
        setListView(
            languageSelectList
        )

        languageSelectDialog?.setOnCancelListener {
            languageSelectDialog?.dismiss()
        }
        languageSelectDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        languageSelectDialog?.window?.setGravity(Gravity.BOTTOM)
        languageSelectDialog?.show()
    }

    private fun setListView(
        languageSelectList: List<Pair<String, Int>>,
    ) {
        val context = cmdIndexFragment.context
            ?: return
        val subMenuListView =
            languageSelectDialog?.findViewById<ListView>(
                com.puutaro.commandclick.R.id.list_dialog_list_view
            ) ?: return
        val subMenuAdapter = SubMenuAdapter(
            context,
            languageSelectList.toMutableList()
        )
        subMenuListView.adapter = subMenuAdapter
        invokeItemSetClickListenerForLanguageType(
            subMenuListView,
        )
    }

    private fun invokeItemSetClickListenerForLanguageType(
        subMenuListView: ListView,
    ){
        subMenuListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            languageSelectDialog?.dismiss()
            val menuListAdapter = subMenuListView.adapter as SubMenuAdapter
            val selectedLanguage = menuListAdapter.getItem(pos)
                ?: return@setOnItemClickListener
            val languageTypeSelects = when(
                selectedLanguage
            ){
                LanguageTypeSelects.SHELL_SCRIPT.str -> {
                    LanguageTypeSelects.SHELL_SCRIPT
                }
                else -> LanguageTypeSelects.JAVA_SCRIPT
            }
            val shellScriptName =
                CommandClickScriptVariable.makeShellScriptName(
                    languageTypeSelects
                )

            AddShellScript.addShellOrJavaScript (
                cmdIndexFragment,
                sharedPref,
                currentAppDirPath,
                shellScriptName,
                languageTypeSelects
            )
            AddConfirmDialogForSettingButton.invoke(
                cmdIndexFragment,
                currentAppDirPath,
                shellScriptName,
                languageTypeSelects
            )
        }
    }
}
