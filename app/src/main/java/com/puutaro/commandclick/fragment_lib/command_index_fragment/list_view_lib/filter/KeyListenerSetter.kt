package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter

import android.view.KeyEvent
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.proccess.UrlTexter
import com.puutaro.commandclick.util.Keyboard


object KeyListenerSetter {
    fun set(
        fragment: Fragment,
        searchEditText: AutoCompleteTextView,
//        currentAppDirPath: String,
    ){
//        val binding = cmdIndexFragment.binding
//        val cmdSearchEditText = binding.cmdSearchEditText
//        val cmdListView = binding.cmdList
        searchEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (
                    event.action != KeyEvent.ACTION_DOWN
                    || keyCode != KeyEvent.KEYCODE_ENTER
                ) return false
//                val linearLayoutParam =
//                    cmdIndexFragment.binding.commandIndexFragment.layoutParams as LinearLayoutCompat.LayoutParams
//                val cmdIndexFragmentWeight = linearLayoutParam.weight
                Keyboard.hiddenKeyboardForFragment(fragment)
//                if(
//                    cmdIndexFragmentWeight == ReadLines.LONGTH
//                ) {
////                    CommandListManager.execListUpdateForCmdIndex(
////                        cmdListView,
////                    )
//                    return false
//                }
                if(
                    searchEditText.text.isNullOrEmpty()
                ) return false
//                if(!cmdIndexFragment.WebSearchSwitch) {
//                    return false
//                }
                UrlTexter.launch(
                    fragment,
                    searchEditText,
                    searchEditText.text.toString()
                )
                return false
            }
        })
    }
}