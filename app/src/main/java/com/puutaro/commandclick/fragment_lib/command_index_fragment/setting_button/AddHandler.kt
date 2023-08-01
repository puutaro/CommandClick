package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.ListView
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.LanguageTypeSelects
import com.puutaro.commandclick.fragment.CommandIndexFragment

class AddScriptHandler(
    private val cmdIndexCommandIndexFragment: CommandIndexFragment,
    private val sharedPref: SharedPreferences?,
    private val currentAppDirPath: String,
) {
    val context = cmdIndexCommandIndexFragment.context
    val binding = cmdIndexCommandIndexFragment.binding

    fun handle(){
        if(context == null)  return

        val languageSelectList = LanguageTypeSelects.values().map {
            it.str
        }
        val languageSelectListView = ListView(cmdIndexCommandIndexFragment.context)
        val languageSelectListAdapter = ArrayAdapter(
            context,
            R.layout.simple_list_item_1,
            languageSelectList
        )
        languageSelectListView.adapter = languageSelectListAdapter
        languageSelectListView.setSelection(languageSelectListAdapter.count)
        val alertDialog = AlertDialog.Builder(
            context
        )
            .setTitle("Select add script language")
            .setView(languageSelectListView)
            .setNegativeButton("NO", null)
            .create()
        alertDialog.getWindow()?.setGravity(Gravity.BOTTOM)
        alertDialog.show()
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            context.getColor(android.R.color.black)
        )

        invokeItemSetClickListenerForLanguageType(
            languageSelectListView,
            languageSelectList,
            alertDialog,
        )
    }

    private fun invokeItemSetClickListenerForLanguageType(
        languageSelectListView: ListView,
        languageSelectList: List<String>,
        alertDialog: AlertDialog,
    ){
        languageSelectListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            alertDialog.dismiss()
            val selectedLanguage = languageSelectList
                .get(pos)
                .split("\n")
                .firstOrNull()
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
                cmdIndexCommandIndexFragment,
                sharedPref,
                currentAppDirPath,
                shellScriptName,
                languageTypeSelects
            )
            AddConfirmDialogForSettingButton.invoke(
                cmdIndexCommandIndexFragment,
                binding,
                currentAppDirPath,
                shellScriptName,
                languageTypeSelects
            )
        }
    }
}
