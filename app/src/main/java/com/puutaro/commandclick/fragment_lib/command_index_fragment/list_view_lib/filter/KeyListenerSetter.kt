package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.filter

import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.ReadLines
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.UrlTexter
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.view_model.activity.TerminalViewModel


class KeyListenerSetter {
    companion object {
        fun set(
            cmdIndexFragment: CommandIndexFragment,
            currentAppDirPath: String,
            cmdListAdapter: ArrayAdapter<String>
        ){
            val context = cmdIndexFragment.context
            val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
            val binding = cmdIndexFragment.binding
            val cmdSearchEditText = binding.cmdSearchEditText
            val cmdListView = binding.cmdList
            cmdSearchEditText.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    if (event.getAction() != KeyEvent.ACTION_DOWN ||
                        keyCode != KeyEvent.KEYCODE_ENTER
                    ) return false
                    Keyboard.hiddenKeyboardForFragment(cmdIndexFragment)
                    if(
                        terminalViewModel.readlinesNum == ReadLines.SHORTH
                    ) {
                        CommandListManager.execListUpdate(
                            currentAppDirPath,
                            cmdListAdapter,
                            cmdListView
                        )
                        return false
                    }
                    if(
                        cmdSearchEditText.text.isNullOrEmpty()
                    ) return false
                    if(!cmdIndexFragment.WebSearchSwitch) {
                        return false
                    }
                    UrlTexter.launch(
                        context,
                        cmdSearchEditText,
                        cmdSearchEditText.text.toString()
                    )
                    return false
                }
            })
        }
    }
}