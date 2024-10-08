package com.puutaro.commandclick.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.activity.MainActivity


object Keyboard {
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

    fun hiddenKeyboardForView(
        context: Context?,
        view: View?
    ){
        val imm = context?.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.hideSoftInputFromWindow(
            view?.windowToken, 0
        )
    }

    fun showKeyboardForFragment(
        fragment: Fragment,
        editText: EditText
    ){
        val imm = fragment.activity?.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.showSoftInput(editText, 0)
    }

    fun showKeyboardForCmdIndexFromActivity(
        activity: MainActivity,
        editText: EditText
    ){
        val imm = activity.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        imm.showSoftInput(editText, 0)
    }

    fun showKeyboard(
        context: Context?,
//        activity: FragmentActivity?,
        focusView: View?,
    ){
        val imm =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as? InputMethodManager
        imm?.showSoftInput(focusView,0)
    }
}