package com.puutaro.commandclick.activity_lib

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.permission.StorageAccessSetter

class InitManager(
    private val activity: MainActivity,
) {
    fun invoke() {
        StorageAccessSetter.storageAccessProcess(
            activity
        )
    }
}
