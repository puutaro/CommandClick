package com.puutaro.commandclick.activity_lib

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.BuildConfig
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecRestartIntent
import com.puutaro.commandclick.activity_lib.manager.FragmentStartHandler
import com.puutaro.commandclick.common.variable.WebUrlVariables


class InitManager(
    private val activity: MainActivity,
) {

    fun invoke(){
        storageAccessProcess()
    }

    private fun storageAccessProcess(){
        when (
            checkPermissionGranted()
        ) {
            PackageManager.PERMISSION_GRANTED ->
                FragmentStartHandler.handle(activity)
            else -> {
                if(
                    activity.supportFragmentManager.fragments.size > 0
                ){
                    ExecRestartIntent.execRestartIntent(activity)
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
                FragmentStartHandler.handle(activity)
                return@registerForActivityResult
            }
            activity.finish()
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
            activity.getColor(android.R.color.black)
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
                FragmentStartHandler.handle(activity)
                return@ActivityResultCallback
            }
            activity.finish()
        })
}