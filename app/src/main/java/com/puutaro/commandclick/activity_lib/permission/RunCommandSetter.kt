package com.puutaro.commandclick.activity_lib.permission

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.util.DialogObject
import com.termux.shared.termux.TermuxConstants

object RunCommandSetter {


    private var termuxSetupDialogObj: Dialog? = null

    fun getPermissionAndSet(
        activity: MainActivity
    ){
        val runCommandPermissionName = "com.termux.permission.RUN_COMMAND"

        val checkingRunCommandPermission =
            ContextCompat.checkSelfPermission(
                activity,
                runCommandPermissionName
            )
        if(
            checkingRunCommandPermission ==
            PackageManager.PERMISSION_GRANTED
        ) {
            termuxSetupAndStorageAccessPermissionProcessLauncher(
                activity
            )
            return
        }
        try {
            activity.getRunCommandPermissionAndStartFragmentLauncher.launch(
                runCommandPermissionName
            )
        } catch (e: Exception){
            launchDialogTitleMessageOnly(
                activity,
                "termux not installed"
            )
        }
    }

    private fun launchDialogTitleMessageOnly(
        activity: MainActivity,
        title: String
    ){
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, which ->

            })
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            activity.getColor(android.R.color.black)
        )
    }

    fun set(
        activity: MainActivity
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                termuxSetupAndStorageAccessPermissionProcessLauncher(activity)
                return@registerForActivityResult
            }
            startFragmentWithAlert(activity)
        }
    }


    private fun termuxSetupAndStorageAccessPermissionProcessLauncher(
        activity: MainActivity
    ){
        val termuxSetUpCommand = "termux-change-repo \\\n" +
                "&& pkg update -y && pkg upgrade -y \\\n" +
                "&& yes | termux-setup-storage \\\n" +
                "&& pkg install -y termux-api \\\n" +
                "&& sed -r 's/^\\#\\s(allow-external-apps.*)/\\1/' \\\n" +
                "-i \"\$HOME/.termux/termux.properties\" \n"
        val refContents =
            "\n\n( clipboard contents:\n\n${termuxSetUpCommand} )"
                .replace(
            "\n",
            "\t\n"
                )
        val clipboard =
            activity.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE)
                    as ClipboardManager
        val clipData = ClipData.newPlainText(
            "termux_setup",
            termuxSetUpCommand
        )
        clipboard.setPrimaryClip(clipData)

        termuxSetupDialogObj = Dialog(
            activity
        )
        termuxSetupDialogObj?.setContentView(
            R.layout.text_simple_dialog_layout
        )
        val titleTextView =
            termuxSetupDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_title
            )
        titleTextView?.text = "To setup termux"
        val descriptionTextView =
            termuxSetupDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_text_view
            )
        descriptionTextView?.text =
            "\n1. Long press on termux \n2. Click paste popup on termux\n" +
                "3. Continue pressing Enter on termux" + "\n\n" + refContents

        val cancelImageButton =
            termuxSetupDialogObj?.findViewById<AppCompatImageButton>(
                R.id.text_simple_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            termuxSetupDialogObj?.dismiss()
            execTermuxSetupAndStorageAccessPermissionProcessLauncher(activity)
        }
        cancelImageButton?.setImageResource(R.drawable.icons8_check_ok)
        termuxSetupDialogObj?.setOnCancelListener {
            termuxSetupDialogObj?.dismiss()
            execTermuxSetupAndStorageAccessPermissionProcessLauncher(activity)
        }
        termuxSetupDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        termuxSetupDialogObj?.window?.setGravity(
            Gravity.BOTTOM
        )
        termuxSetupDialogObj?.show()
    }


    private fun startFragmentWithAlert(
        activity: MainActivity
    ){
        DialogObject.simpleTextShow(
            activity,
            "not exist run_command permission or termux app",
            String()
        )
    }

    private fun execTermuxSetupAndStorageAccessPermissionProcessLauncher(
        activity: MainActivity
    ){
        val launchIntent =
            activity.packageManager.getLaunchIntentForPackage(
                TermuxConstants.TERMUX_PACKAGE_NAME
            ) ?: return
        Toast.makeText(
            activity,
            "Long press and click paste popup on termux",
            Toast.LENGTH_LONG
        ).show()
        activity.startActivity(launchIntent)
    }

}