package com.puutaro.commandclick.activity_lib

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
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


class InitManager(
    private val activity: MainActivity,
) {

    @RequiresApi(Build.VERSION_CODES.R)
    val manageFullStoragePermissionResultLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult?> {
            if (
                Environment.isExternalStorageManager()
            ) {
                startFragment(
                    activity.savedInstanceStateVal
                )
                return@ActivityResultCallback
            }
            activity.finish()
        })

    private val storageAccessPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                startFragment(
                    activity.savedInstanceStateVal
                )
                return@registerForActivityResult
            }
            activity.finish()
        }


    fun startFragment (
        savedInstanceStateVal: Bundle?

    ){

        val initFragmentManager = InitFragmentManager(activity)
        initFragmentManager.registerSharePreferenceFromIntentExtra()
        activity.activityMainBinding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_main
        )
        initFragmentManager.startFragment(savedInstanceStateVal)
    }

    fun invoke(
        savedInstanceStateVal: Bundle?,
    ){
        val checkedSelfPermission =
            checkPermissionGranted()
        when (checkedSelfPermission) {
            PackageManager.PERMISSION_GRANTED -> {
                startFragment (
                    savedInstanceStateVal,

                )
            }
            else -> {
                getStoragePermissionHandler()
            }
        }
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
            .show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            activity.getColor(android.R.color.black) as Int
        )
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(
            activity.getColor(android.R.color.black)
        )
    }
}