package com.puutaro.commandclick.activity_lib

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.permission.StorageAccessSetter

object InitManager {
    fun invoke(
        activity: MainActivity
    ) {
        StorageAccessSetter.storageAccessProcess(
            activity
        )
    }
}
