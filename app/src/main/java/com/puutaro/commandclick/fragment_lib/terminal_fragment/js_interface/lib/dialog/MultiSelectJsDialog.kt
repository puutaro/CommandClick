package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import androidx.fragment.app.activityViewModels
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference

class MultiSelectJsDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    fun create(
        title: String,
        currentItemListStr: String,
        preSelectedItemListStr: String,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context ?: return String()
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        terminalViewModel.multiSelectTabString = String()
        val currentItemList = currentItemListStr.split("\n")
        val preSelectedItemList = preSelectedItemListStr.split("\n")
        val updatedMultiModelArray = ArrayList(
            (currentItemList.indices).map {
                MultiSelectModel(it, currentItemList[it])
            }
        )
        val preSelectedMultiModelIdArray = ArrayList(
            (currentItemList.indices).filter {
                val currentItem = currentItemList[it]
                preSelectedItemList.contains(currentItem)
            }
        )
        val listener =
            context as? TerminalFragment.OnMultiSelectListenerForTerm
        listener?.onMultiSelectForTerm(
            title,
            updatedMultiModelArray,
            preSelectedMultiModelIdArray
        )
        runBlocking {
            for (i in 1..1200){
                if(
                    terminalViewModel.multiSelectTabString.isNotEmpty()
                ) break
                delay(100)
            }
        }
        if(
            terminalViewModel.multiSelectTabString
            == CommandClickScriptVariable.EMPTY_STRING
        ) return String()
        return terminalViewModel.multiSelectTabString
    }
}