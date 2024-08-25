package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter

import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.UrlTexter
import com.puutaro.commandclick.util.Keyboard


object KeyListenerSetter {
    fun set(
        cmdIndexFragment: CommandIndexFragment,
//        currentAppDirPath: String,
    ){
        val binding = cmdIndexFragment.binding
        val cmdSearchEditText = binding.cmdSearchEditText
//        val cmdListView = binding.cmdList
        cmdSearchEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (
                    event.action != KeyEvent.ACTION_DOWN
                    || keyCode != KeyEvent.KEYCODE_ENTER
                ) return false
                val linearLayoutParam =
                    cmdIndexFragment.binding.commandIndexFragment.layoutParams as LinearLayout.LayoutParams
                val cmdIndexFragmentWeight = linearLayoutParam.weight
                Keyboard.hiddenKeyboardForFragment(cmdIndexFragment)
                if(
                    cmdIndexFragmentWeight == ReadLines.LONGTH
                ) {
//                    CommandListManager.execListUpdateForCmdIndex(
//                        cmdListView,
//                    )
                    return false
                }
                if(
                    cmdSearchEditText.text.isNullOrEmpty()
                ) return false
                if(!cmdIndexFragment.WebSearchSwitch) {
                    return false
                }
                UrlTexter.launch(
                    cmdIndexFragment,
                    cmdSearchEditText,
                    cmdSearchEditText.text.toString()
                )
                return false
            }
        })
    }
}