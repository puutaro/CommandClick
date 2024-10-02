package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.puutaro.commandclick.databinding.EditFragmentBinding
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
//        if(
//            !editFragment.existIndexList
//        ) return
        val binding = editFragment.binding
        val editListRecyclerView = binding.editListRecyclerView
        val listIndexForEditAdapter = editListRecyclerView.adapter
            ?: return
        alignBottomLinearlayoutToBottom(
            isOpen,
            binding,
        )
//        binding.editListInnerTopLinearLayout.isVisible = !isOpen
        CoroutineScope(Dispatchers.Main).launch {
            delay(300)
            editListRecyclerView.scrollToPosition(
                listIndexForEditAdapter.itemCount - 1
            )
        }
    }

    private fun alignBottomLinearlayoutToBottom(
        isOpen: Boolean,
        binding: EditFragmentBinding,
    ){
        val editListSearchEditText =
            binding.editListSearchEditText
        if(
            !editListSearchEditText.isVisible
        ) return
//        val editListLinearLayout =
//            binding.editListLinearLayout
//        val layoutParams =
//            editListLinearLayout.layoutParams
//                    as RelativeLayout.LayoutParams
//        when(isOpen) {
//            true -> layoutParams.addRule(
//                RelativeLayout.ALIGN_PARENT_BOTTOM
//            )
//            else -> layoutParams.removeRule(
//                RelativeLayout.ALIGN_PARENT_BOTTOM
//            )
//        }
    }
}
