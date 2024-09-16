package com.puutaro.commandclick.fragment_lib.command_index_fragment

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.view_model.activity.CommandIndexViewModel
import kotlinx.coroutines.*

//object EditTextWhenReuse {
//    fun focus(
//        cmdIndexFragment: CommandIndexFragment
//    ){
//        val cmdIndexViewModel: CommandIndexViewModel by cmdIndexFragment.activityViewModels()
//        val binding = cmdIndexFragment.binding
//        cmdIndexFragment.showTerminalJobWhenReuse =
//            CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.IO) {
//                delay(100)
//            }
//            withContext(Dispatchers.Main) {
//                if (!cmdIndexViewModel.onFocusSearchText) return@withContext
//                val cmdSearchEditText = binding.cmdSearchEditText
//                cmdSearchEditText.requestFocus()
//                Keyboard.showKeyboardForFragment(
//                    cmdIndexFragment,
//                    cmdSearchEditText
//                )
//                cmdSearchEditText.setText(cmdIndexFragment.savedEditTextContents)
//                cmdSearchEditText.setSelection(cmdSearchEditText.length())
//            }
//        }
//    }
//}