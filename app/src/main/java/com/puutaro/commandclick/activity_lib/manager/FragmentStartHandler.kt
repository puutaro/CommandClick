package com.puutaro.commandclick.activity_lib.manager

import androidx.databinding.DataBindingUtil
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity

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
        initFragmentManager.startFragment(
            activity.savedInstanceStateVal
        )
    }



}