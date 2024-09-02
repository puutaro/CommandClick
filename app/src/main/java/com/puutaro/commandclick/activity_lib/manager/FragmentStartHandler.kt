package com.puutaro.commandclick.activity_lib.manager

import android.app.ActivityManager
import androidx.databinding.DataBindingUtil
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity


object FragmentStartHandler {

    fun handle(
        activity: MainActivity
    ){
//        ToastUtils.showShort(
//            activity.intent.extras.toString()
//        )
//        val isHandle = initFragmentManager.queryIntentHandle()
//        if(
//            isHandle
//        ) return
        val isRegister = InitFragmentManager.intentHandler(activity)
        if(
            isRegister
        ) return
        activity.activityMainBinding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_main
        )
//        ExecBackstackHandle.initBeforeAfterUrlPair(activity)
        InitFragmentManager.startFragment(
            activity,
            activity.savedInstanceStateVal
        )
    }
}

private fun isTwoOverActivity(
    mngr: ActivityManager?
): Boolean {
    val isTwoOverTask =
        (mngr?.appTasks?.size ?: 0) > 1
    if(
        isTwoOverTask
    ) return true
    val isTwoOverActivity =
        (mngr?.appTasks?.getOrNull(0)?.taskInfo?.numActivities?: 0) > 1
    if(
        isTwoOverActivity
    ) return true
    return false
}