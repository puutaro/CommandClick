package com.puutaro.commandclick.activity_lib.permission

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.puutaro.commandclick.BuildConfig
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecRestartIntent
import com.puutaro.commandclick.activity_lib.init.ActivityFinisher

object StorageAccessSetter {

    private var getPermissionConfirmDialog: Dialog? = null

    fun storageAccessProcess(
        activity: MainActivity
    ){
        when (
            checkPermissionGranted(activity)
        ) {
            true -> {
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


    fun checkPermissionGranted(
        activity: MainActivity
    ): Boolean {
        if(
            Build.VERSION.SDK_INT < 30
        ){
            return ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        return checkedManagedFullStorageGranted() ==
                PackageManager.PERMISSION_GRANTED
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
        getPermissionConfirmDialog = Dialog(
            activity
        )
        getPermissionConfirmDialog?.setContentView(
            com.puutaro.commandclick.R.layout.confirm_text_dialog
        )
        val confirmTitleTextView =
            getPermissionConfirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_title
            )
        confirmTitleTextView?.text =
            "Enable manage all storage permission, ok?"
        val confirmContentTextView =
            getPermissionConfirmDialog?.findViewById<AppCompatTextView>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_text_view
            )
        confirmContentTextView?.isVisible = false
        val confirmCancelButton =
            getPermissionConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            getPermissionConfirmDialog?.dismiss()
            getPermissionConfirmDialog = null
            ActivityFinisher.finish(activity)
        }
        val confirmOkButton =
            getPermissionConfirmDialog?.findViewById<AppCompatImageButton>(
                com.puutaro.commandclick.R.id.confirm_text_dialog_ok
            )
        confirmOkButton?.setOnClickListener {
            getPermissionConfirmDialog?.dismiss()
            getPermissionConfirmDialog = null
            val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            activity.manageFullStoragePermissionResultLauncher.launch(
                Intent(
                    Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    uri
                )
            )
        }
        getPermissionConfirmDialog?.setOnCancelListener {
            getPermissionConfirmDialog?.dismiss()
            getPermissionConfirmDialog = null
        }
        getPermissionConfirmDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        getPermissionConfirmDialog?.window?.setGravity(
            Gravity.CENTER
        )
        getPermissionConfirmDialog?.show()
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
