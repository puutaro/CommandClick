package com.puutaro.commandclick.activity_lib

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.puutaro.commandclick.BuildConfig
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.InitFragmentManager
import com.puutaro.commandclick.util.LinearLayoutAdderForDialog
import com.termux.shared.termux.TermuxConstants


class InitManager(
    private val activity: MainActivity,
) {

    private val runCommandPermissionName = "com.termux.permission.RUN_COMMAND"
    private val termuxSetUpCommand = "pkg update -y && pkg upgrade -y \\\n" +
            "&& yes | termux-setup-storage \\\n" +
            "&& sed -r 's/^\\#\\s(allow-external-apps.*)/\\1/' -i \"\$HOME/.termux/termux.properties\""
    private var clipboard =
        activity.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager


    fun invoke(){
        storageAccessProcess()
    }

    private fun storageAccessProcess(){
        when (
            checkPermissionGranted()
        ) {
            PackageManager.PERMISSION_GRANTED ->
                runCommandAndStartFragmentProcess()
            else -> {
                if(
                    activity.supportFragmentManager.fragments.size > 0
                ){
                    execRestartIntent()
                    return
                }
                getStoragePermissionHandler()
            }
        }
    }


    private fun getStoragePermissionHandler(){
        if(
            Build.VERSION.SDK_INT < 30
        ){
            storageAccessPermissionLauncher.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            return
        }
        getManagedFullStorageGrantedHandler()
    }

    private val storageAccessPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                runCommandAndStartFragmentProcess()
                return@registerForActivityResult
            }
            activity.finish()
        }


    private fun runCommandAndStartFragmentProcess(){
        val checkingRunCommandPermission =
            ContextCompat.checkSelfPermission(
                activity,
                runCommandPermissionName
            )
        if(
            checkingRunCommandPermission ==
            PackageManager.PERMISSION_GRANTED
        ) {
            startFragment()
            return
        }
        if(
            activity.supportFragmentManager.fragments.size > 0
        ){
            execRestartIntent()
            return
        }
        try {
            getRunCommandPermissionAndStartFragmentLauncher.launch(
                runCommandPermissionName
            )
        } catch (e: Exception){
            launchDialogTitleMessageOnly("termux not installed")
        }
    }

    private val getRunCommandPermissionAndStartFragmentLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                termuxSetupAndStorageAccessPermissionProcessLauncher()
                return@registerForActivityResult
            }
            startFragmentWithAlert()
        }

    private fun termuxSetupAndStorageAccessPermissionProcessLauncher(){
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
                val launchIntent =
                    activity.getPackageManager().getLaunchIntentForPackage(
                        TermuxConstants.TERMUX_PACKAGE_NAME
                    ) ?: return@OnClickListener
                Toast.makeText(
                    activity,
                    "Long press and click paste popup on termux",
                    Toast.LENGTH_LONG
                ).show()
                activity.startActivity(launchIntent)
                startFragment()
            })
            .setOnCancelListener(object : DialogInterface.OnCancelListener {
                override fun onCancel(dialog: DialogInterface?) {
                    val launchIntent =
                        activity.getPackageManager().getLaunchIntentForPackage(
                            TermuxConstants.TERMUX_PACKAGE_NAME
                        ) ?: return
                    activity.startActivity(launchIntent)
                    startFragment()
                }
            })
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            activity.getColor(android.R.color.black)
        )
    }


    private fun startFragmentWithAlert(){
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle("not exist run_command permission or termux app")
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, which ->
                startFragment()

            })
            .setOnCancelListener(object : DialogInterface.OnCancelListener {
                override fun onCancel(dialog: DialogInterface?) {
                    startFragment()
                }
            })
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            activity.getColor(android.R.color.black)
        )
    }

    private fun launchDialogTitleMessageOnly(
        title: String
    ){
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, which ->

            })
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            activity.getColor(android.R.color.black) as Int
        )
    }


    private fun startFragment(){
        val initFragmentManager = InitFragmentManager(activity)
        initFragmentManager.registerSharePreferenceFromIntentExtra()
        activity.activityMainBinding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_main
        )
        initFragmentManager.startFragment(
            activity.savedInstanceStateVal
        )
    }


    private fun checkPermissionGranted(): Int {
        if(
            Build.VERSION.SDK_INT < 30
        ){
            return ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        return checkedManagedFullStorageGranted()
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkedManagedFullStorageGranted(): Int {
        return if(
            Environment.isExternalStorageManager()
        ){
            PackageManager.PERMISSION_GRANTED
        } else {
            PackageManager.PERMISSION_DENIED
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun getManagedFullStorageGrantedHandler(){
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(
                "Enable manage all storage permission, ok?"
            )
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, which ->
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                manageFullStoragePermissionResultLauncher.launch(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        uri
                    )
                )
            })
            .setNegativeButton("NO", DialogInterface.OnClickListener {
                    dialog, which ->
                activity.finish()
            })
        .setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                activity.finish()
            }
        })
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            activity.getColor(android.R.color.black) as Int
        )
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            activity.getColor(android.R.color.black)
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    val manageFullStoragePermissionResultLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult?> {
            if (
                Environment.isExternalStorageManager()
            ) {
                runCommandAndStartFragmentProcess()
                return@ActivityResultCallback
            }
            activity.finish()
        })


    private fun execRestartIntent() {
        val execIntent = Intent(
            activity, activity::class.java
        )
        execIntent.setAction(Intent.ACTION_VIEW)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(execIntent)
        activity.finish()
    }

}