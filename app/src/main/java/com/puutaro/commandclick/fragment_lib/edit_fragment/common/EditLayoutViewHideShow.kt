package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import androidx.core.view.isVisible
import com.puutaro.commandclick.fragment.EditFragment

object EditLayoutViewHideShow {
    fun exec(
        editFragment: EditFragment,
        onShow: Boolean
    ){
        val binding = editFragment.binding
        when(editFragment.existIndexList){
            true -> {
//                binding.editListInnerTopLinearLayout.isVisible = onShow
//                binding.editListInnerBottomLinearLayout.isVisible = !onShow
                binding.editListLinearLayout.isVisible =
                    onShow
            }
            else -> {
                binding.editTextScroll.isVisible =
                    onShow
                binding.editTextView.isVisible =
                    onShow
            }
        }
    }
}