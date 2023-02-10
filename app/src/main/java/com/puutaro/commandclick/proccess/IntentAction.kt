package com.puutaro.commandclick.proccess

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment

class IntentAction {
    companion object {
        fun judge(
            activity: Activity?
        ): Boolean{
            val intent = activity?.intent
            if(
                intent?.dataString.isNullOrEmpty()
            ) return false
            val intentAction =
                activity?.intent?.action
            return when(
                intentAction
            ) {
                Intent.ACTION_VIEW,
                Intent.ACTION_MAIN -> {
                    true
                }
                else -> false
            }
        }
    }
}