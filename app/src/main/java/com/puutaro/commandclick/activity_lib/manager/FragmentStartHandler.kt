package com.puutaro.commandclick.activity_lib.manager

import androidx.databinding.DataBindingUtil
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.lib.common.ExecBackstackHandle
import com.puutaro.commandclick.proccess.UrlLaunchIntentAction

object FragmentStartHandler {
    fun handle(
        activity: MainActivity
    ){
        val initFragmentManager = InitFragmentManager(activity)
        initFragmentManager.registerSharePreferenceFromIntentExtra()
        activity.activityMainBinding = DataBindingUtil.setContentView(
            activity,
            R.layout.activity_main
        )
        ExecBackstackHandle.initBeforeAfterUrlPair(activity)
        initFragmentManager.startFragment(
            activity.savedInstanceStateVal
        )
    }



}