package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import androidx.fragment.app.activityViewModels
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MultiSelectJsDialog(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

    fun create(
        title: String,
        currentItemListStr: String,
        preSelectedItemListStr: String,
    ): String {
        terminalViewModel.multiSelectTabString = String()
        val currentItemList = currentItemListStr.split("\t")
        val preSelectedItemList = preSelectedItemListStr.split("\t")
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