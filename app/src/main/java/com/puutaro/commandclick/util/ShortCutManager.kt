package com.puutaro.commandclick.util

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting


class ShortCutManager(
    private val activity: MainActivity,
) {
    val context = activity.applicationContext

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
        val editText = EditText(activity)
        editText.inputType = InputType.TYPE_CLASS_TEXT
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(
                "Input shortcut label"
            )
            .setView(editText)
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                val inputLabelName = editText.text.toString()
                execCreateShortcut(
                    execIntent,
                    inputLabelName
                )
            })
            .setNegativeButton("NO", null)
            .show()
        alertDialog.getWindow()?.setGravity(Gravity.BOTTOM)
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            activity.getColor(R.color.black)
        );
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            activity.getColor(R.color.black)
        );
    }

}


internal fun getRandomString() : String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
    return (1..10)
        .map { charset.random() }
        .joinToString("")
}
