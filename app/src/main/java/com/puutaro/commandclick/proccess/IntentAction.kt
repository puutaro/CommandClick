package com.puutaro.commandclick.proccess

import android.content.Intent
import androidx.fragment.app.Fragment

class IntentAction {
    companion object {
        fun judge(
            fragment: Fragment
        ): Boolean{
            val intent = fragment.activity?.intent
            if(
                intent?.dataString.isNullOrEmpty()
            ) return false
            val intentAction =
                fragment.activity?.intent?.action
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