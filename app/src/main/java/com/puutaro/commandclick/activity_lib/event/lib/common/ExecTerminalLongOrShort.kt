package com.puutaro.commandclick.activity_lib.event.lib.common

import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment


object ExecTerminalLongOrShort {
    fun <T: Fragment>  open(
        fragmentTag: String,
        supportFragmentManager: FragmentManager,
    ){
        val targetFragment = try {
            supportFragmentManager.findFragmentByTag(fragmentTag) as T
        } catch(e: java.lang.Exception){
            Log.d(this.toString(), "not exist ${fragmentTag}")
            return
        }

        val param = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            0
        )

        val linearLayoutParam = when(targetFragment) {
            is CommandIndexFragment -> {
                targetFragment.binding.commandIndexFragment.layoutParams as LinearLayoutCompat.LayoutParams
            }
            is EditFragment -> {
                targetFragment.binding.editFragment.layoutParams as LinearLayoutCompat.LayoutParams
            }
            else -> {
                return
            }
        }
        param.weight = when(
            linearLayoutParam.weight == ReadLines.LONGTH
        ) {
            true -> ReadLines.SHORTH
            else -> ReadLines.LONGTH
        }
        targetFragment.view?.layoutParams = param
    }
}