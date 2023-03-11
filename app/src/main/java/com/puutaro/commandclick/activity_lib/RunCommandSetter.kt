package com.puutaro.commandclick.activity_lib

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.view.Gravity
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.FragmentStartHandler
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import com.termux.shared.termux.TermuxConstants

object RunCommandSetter {

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
            Toast.makeText(
                activity,
                "already setting ok",
                Toast.LENGTH_LONG
            ).show()
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
        val termuxSetUpCommand = "pkg update -y && pkg upgrade -y \\\n" +
                "&& yes | termux-setup-storage \\\n" +
                "&& sed -r 's/^\\#\\s(allow-external-apps.*)/\\1/' -i \"\$HOME/.termux/termux.properties\""

        val clipboard =
            activity.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE)
                    as ClipboardManager
        val clipData = ClipData.newPlainText("termux_setup", termuxSetUpCommand)
        clipboard.setPrimaryClip(clipData)

        val dialogLinearLayout = LinearLayoutAdderForDialog.add(
            activity,
            "\n\n( clipboard contents:\n\n${termuxSetUpCommand} )"
        )
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(
                "To setup termux"
            )
            .setMessage("\n1. Long press on termux \n2. Click paste popup on termux\n" +
                    "3. Continue pressing Enter on termux")
            .setView(dialogLinearLayout)
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, which ->
                execTermuxSetupAndStorageAccessPermissionProcessLauncher(activity)
            })
            .setOnCancelListener(object : DialogInterface.OnCancelListener {
                override fun onCancel(dialog: DialogInterface?) {
                    execTermuxSetupAndStorageAccessPermissionProcessLauncher(activity)
                }
            })
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            activity.getColor(android.R.color.black)
        )
    }


    private fun startFragmentWithAlert(
        activity: MainActivity
    ){
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle("not exist run_command permission or termux app")
            .setPositiveButton("OK", null)
            .show()
        alertDialog.window?.setGravity(Gravity.BOTTOM)
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            activity.getColor(android.R.color.black)
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