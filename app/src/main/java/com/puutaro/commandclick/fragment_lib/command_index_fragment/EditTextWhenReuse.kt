package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.Keyboard
import kotlinx.coroutines.*

object EditTextWhenReuse {
    fun focus(
        cmdIndexCommandIndexFragment: CommandIndexFragment
    ){
        val binding = cmdIndexCommandIndexFragment.binding
        cmdIndexCommandIndexFragment.showTerminalJobWhenReuse =
            CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                delay(100)
            }
            withContext(Dispatchers.Main) {
                if (!cmdIndexCommandIndexFragment.onFocusSearchText) return@withContext
                val cmdSearchEditText = binding.cmdSearchEditText
                cmdSearchEditText.requestFocus()
                Keyboard.showKeyboardForFragment(
                    cmdIndexCommandIndexFragment,
                    cmdSearchEditText
                )
                cmdSearchEditText.setText(cmdIndexCommandIndexFragment.savedEditTextContents)
                cmdSearchEditText.setSelection(cmdSearchEditText.length())
            }
        }
    }
}