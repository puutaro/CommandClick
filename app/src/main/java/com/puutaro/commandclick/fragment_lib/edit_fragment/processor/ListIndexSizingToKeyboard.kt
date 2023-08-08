package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.widget.RelativeLayout
import com.puutaro.commandclick.fragment.EditFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ListIndexSizingToKeyboard {
    fun handle(
        editFragment: EditFragment,
        isOpen: Boolean
    ){
        if(
            !editFragment.existIndexList
        ) return
        val binding = editFragment.binding
        val editListRecyclerView = binding.editListRecyclerView
        val listIndexForEditAdapter = editListRecyclerView.adapter
            ?: return
        val editListLinearLayout = binding.editListLinearLayout
        val layoutParams = editListLinearLayout.layoutParams as RelativeLayout.LayoutParams
        if(
            isOpen
        ) layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        else layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        CoroutineScope(Dispatchers.Main).launch {
            delay(100)
            editListRecyclerView.scrollToPosition(
                listIndexForEditAdapter.itemCount - 1
            )
        }
    }
}
