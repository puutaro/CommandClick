package com.puutaro.commandclick.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting


class ShortCutManager(
    private val activity: MainActivity,
) {
    val context = activity.applicationContext
    var shortcutNamePromptDialog: Dialog? = null

        fun createShortCut() {

            val execIntent = createExecIntent()
            createShortcutDialog(
                execIntent
            )
        }

    private fun execCreateShortcut(
        execIntent: Intent,
        shortCutLabel: String
    ){
        val shortCutId = getRandomString()
        val shortcut = ShortcutInfo.Builder(context, shortCutId)
            .setShortLabel(shortCutLabel)
            .setLongLabel(shortCutLabel)
            .setIcon(
                Icon.createWithResource(
                    context,
                    com.puutaro.commandclick.R.mipmap.ic_cmdclick_launcher
                )
            )
            .setIntent(
                execIntent
            )
            .build()

        val manager = activity.getSystemService(Context.SHORTCUT_SERVICE) as ShortcutManager
        manager.requestPinShortcut(shortcut, null)
    }


    private fun createExecIntent(
    ): Intent {
        val startUpPref =  activity.getPreferences(Context.MODE_PRIVATE)
        val readSharePreffernceMap = SharePreffrenceMethod.makeReadSharePreffernceMap(
            startUpPref
        )
        val curentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentShellFileName = SharePreffrenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_script_file_name
        )

        val execIntent = Intent(activity, activity::class.java)
        execIntent
            .setAction(Intent.ACTION_MAIN)
            .flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        execIntent.putExtra(
            SharePrefferenceSetting.current_app_dir.name,
            curentAppDirPath
        )
        execIntent.putExtra(
            SharePrefferenceSetting.current_script_file_name.name,
            currentShellFileName
        )
        return execIntent

    }

    private fun createShortcutDialog(
        execIntent: Intent
    ){
        shortcutNamePromptDialog = Dialog(
            activity
        )
        shortcutNamePromptDialog?.setContentView(
            com.puutaro.commandclick.R.layout.prompt_dialog_layout
        )
        val promptTitleTextView =
            shortcutNamePromptDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.prompt_dialog_title
            )
        promptTitleTextView?.text = "Input shortcut label"
        val promptMessageTextView =
            shortcutNamePromptDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.prompt_dialog_message
            )
        promptMessageTextView?.isVisible = false
        val promptEditText =
            shortcutNamePromptDialog?.findViewById<AppCompatEditText>(
                com.puutaro.commandclick.R.id.prompt_dialog_input
            )
        val promptCancelButton =
            shortcutNamePromptDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.prompt_dialog_cancel
            )
        promptCancelButton?.setOnClickListener {
            shortcutNamePromptDialog?.dismiss()
        }
        val promptOkButtonView =
            shortcutNamePromptDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.prompt_dialog_ok
            )
        promptOkButtonView?.setOnClickListener {
            shortcutNamePromptDialog?.dismiss()
            val shortcutNameEditable = promptEditText?.text
            if(
                shortcutNameEditable.isNullOrEmpty()
            ) return@setOnClickListener
            val inputLabelName = shortcutNameEditable.toString()
            execCreateShortcut(
                execIntent,
                inputLabelName
            )
        }
        shortcutNamePromptDialog?.setOnCancelListener {
            shortcutNamePromptDialog?.dismiss()
        }
        shortcutNamePromptDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        shortcutNamePromptDialog?.window?.setGravity(
            Gravity.BOTTOM
        )
        shortcutNamePromptDialog?.show()
    }

}


internal fun getRandomString() : String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
    return (1..10)
        .map { charset.random() }
        .joinToString("")
}
