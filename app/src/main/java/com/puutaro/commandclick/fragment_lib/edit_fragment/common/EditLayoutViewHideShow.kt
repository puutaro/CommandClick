package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import androidx.core.view.isVisible
import com.puutaro.commandclick.fragment.EditFragment

object EditLayoutViewHideShow {
    fun exec(
        editFragment: EditFragment,
        onShow: Boolean
    ){
        editFragment.binding.editTextScroll.isVisible = onShow
        editFragment.binding.editListLinearLayout.isVisible = onShow
    }
}