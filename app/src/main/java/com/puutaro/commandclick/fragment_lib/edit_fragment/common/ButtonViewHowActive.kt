package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.widget.ImageButton
import com.puutaro.commandclick.R
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment

class ButtonViewHowActive(
    private val binding: EditFragmentBinding,
    private val editFragment: EditFragment,

) {
        fun buttonViewHowActive(
            buttonTag: String,
            howActive: Boolean = false,
        ){
            val buttonView =
                binding.editToolbarLinearLayout.findViewWithTag<ImageButton>(
                    buttonTag
                ) ?: return
            val colorId = if(howActive) R.color.black else R.color.gray_out
            buttonView.imageTintList = editFragment.context?.getColorStateList(colorId)
            buttonView.isEnabled = howActive
        }
}