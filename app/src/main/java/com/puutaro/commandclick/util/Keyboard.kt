package com.puutaro.commandclick.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.app.CommandClick

class Keyboard {
    companion object {
        fun hiddenKeyboard(
            activity: FragmentActivity?,
            view: View,
        ){
            val inputMethod = try {
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            } catch (e: java.lang.Exception){
                return
            }
            inputMethod.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

        fun hiddenKeyboardForFragment(
            fragment: Fragment
        ){
            val imm = fragment.activity?.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            imm.hideSoftInputFromWindow(
                fragment.view?.windowToken, 0
            )
        }
    }
}