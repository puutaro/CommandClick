package com.puutaro.commandclick.activity_lib.permission

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.puutaro.commandclick.BuildConfig
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecRestartIntent
import com.puutaro.commandclick.activity_lib.init.ActivityFinisher

object StorageAccessSetter {

    fun storageAccessProcess(
        activity: MainActivity
    ){
        when (
            checkPermissionGranted(activity)
        ) {
            PackageManager.PERMISSION_GRANTED -> {
                NotifierSetter.getPermissionAndSet(
                    activity
                )
            }
            else -> {
                if(
                    activity.supportFragmentManager.fragments.size > 0
                ){
                    ExecRestartIntent.execRestartIntent(activity)
                    return
                }
                getStoragePermissionHandler(activity)
            }
        }
    }


    private fun getStoragePermissionHandler(
        activity: MainActivity
    ){
        if(
            Build.VERSION.SDK_INT < 30
        ){
            activity.storageAccessPermissionLauncher.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            return
        }
        getManagedFullStorageGrantedHandler(activity)
    }

    fun set(
        activity: MainActivity
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if(isGranted){
                NotifierSetter.getPermissionAndSet(
                    activity
                )
                return@registerForActivityResult
            }
            ActivityFinisher.finish(activity)
        }
    }


    private fun checkPermissionGranted(
        activity: MainActivity
    ): Int {
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
    private fun getManagedFullStorageGrantedHandler(
        activity: MainActivity
    ){
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(
                "Enable manage all storage permission, ok?"
            )
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, which ->
                val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
                activity.manageFullStoragePermissionResultLauncher.launch(
                    Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        uri
                    )
                )
            })
            .setNegativeButton("NO", DialogInterface.OnClickListener {
                    dialog, which ->
                ActivityFinisher.finish(activity)
            })
            .setOnCancelListener(object : DialogInterface.OnCancelListener {
                override fun onCancel(dialog: DialogInterface?) {
                    ActivityFinisher.finish(activity)
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
    fun setForFullStorageAccess(
        activity: MainActivity
    ): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult?> {
                if (
                    Environment.isExternalStorageManager()
                ) {
                    NotifierSetter.getPermissionAndSet(
                        activity
                    )
                    return@ActivityResultCallback
                }
                ActivityFinisher.finish(activity)
        })
    }
}
