package com.puutaro.commandclick.activity_lib.permission

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


object CameraSetter {

    fun getPermissionAndSet(
        activity: MainActivity,
    ){
        val terminalViewModel: TerminalViewModel =
            ViewModelProvider(activity).get(TerminalViewModel::class.java)

        val cameraPermission = Manifest.permission.CAMERA
        val checkingCameraPermission =
            ContextCompat.checkSelfPermission(
                activity,
                cameraPermission
            )
        if(
            checkingCameraPermission ==
            PackageManager.PERMISSION_GRANTED
        ) {
            terminalViewModel.onPermDialog = false
            return
        }
        activity.getCameraSetterLaunch.launch(
            cameraPermission
        )
    }

    fun set(
        activity: MainActivity
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            val terminalViewModel: TerminalViewModel =
                ViewModelProvider(activity).get(TerminalViewModel::class.java)
            terminalViewModel.onPermDialog = false
//            if(isGranted){
//                // TODO load implement?
//                return@registerForActivityResult
//            }
        }
    }
}