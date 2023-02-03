package com.puutaro.commandclick.activity_lib

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.manager.InitFragmentManager


class InitManager {
    companion object {
        fun startFragment (
            activity: MainActivity,
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
            activity: MainActivity,
            savedInstanceStateVal: Bundle?,
            requestPermissionLauncher: ActivityResultLauncher<String>
        ){
            when (
                ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                PackageManager.PERMISSION_GRANTED -> {
                    startFragment (
                        activity,
                        savedInstanceStateVal,

                    )
                }
                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            }
        }
    }
}