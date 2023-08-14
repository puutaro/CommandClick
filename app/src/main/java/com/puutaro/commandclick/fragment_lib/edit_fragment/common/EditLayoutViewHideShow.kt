package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import androidx.core.view.isVisible
import com.puutaro.commandclick.fragment.EditFragment

object EditLayoutViewHideShow {
    fun exec(
        editFragment: EditFragment,
        onShow: Boolean
    ){
        val showEditTextScroll =
            onShow && !editFragment.existIndexList
        editFragment.binding.editTextScroll.isVisible =
            showEditTextScroll
        editFragment.binding.editTextView.isVisible =
            showEditTextScroll
        val showEditListLinearLayout =
            onShow && editFragment.existIndexList
        editFragment.binding.editListLinearLayout.isVisible =
            showEditListLinearLayout
    }
}