package com.puutaro.commandclick.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.activity.MainActivity


class TargetFragmentInstance {
    fun <T: Fragment> getFromFragment(
        activity: FragmentActivity?,
        targetFragmentTag: String?
    ): T? {
        return try {
            activity?.supportFragmentManager?.findFragmentByTag(
                targetFragmentTag
            )  as T
        } catch (e: Exception){
            null
        }
    }

    fun <T: Fragment> getFromActivity(
        activity: MainActivity?,
        targetFragmentTag: String?
    ): T? {
        return try {
            activity?.supportFragmentManager?.findFragmentByTag(
                targetFragmentTag
            )  as T
        } catch (e: Exception){
            null
        }
    }
}