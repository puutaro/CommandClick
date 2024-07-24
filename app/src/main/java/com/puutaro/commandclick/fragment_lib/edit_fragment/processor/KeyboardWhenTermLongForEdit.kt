package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object KeyboardWhenTermLongForEdit {
    fun handle(
        editFragment: EditFragment,
        isOpen: Boolean
    ){
        val context = editFragment.context
        val activity = editFragment.activity
        val editExecuteTerminal = TargetFragmentInstance().getFromFragment<TerminalFragment>(
            activity,
            context?.getString(
                R.string.edit_terminal_fragment
            )
        ) ?: return
        if(!editExecuteTerminal.isVisible) return
        val listenerForEditSizeWhenLong =
            context as? EditFragment.OnLongTermKeyBoardOpenAjustListenerForEdit
        val binding = editFragment.binding
        when(editFragment.existIndexList) {
            true -> {
                binding.editListInnerTopLinearLayout.isVisible = !isOpen
                binding.editListInnerBottomLinearLayout.isVisible = !isOpen
            }
            else -> {
                val linearLayoutParam =
                    editFragment.binding.editFragment.layoutParams as LinearLayout.LayoutParams
                val isShow = linearLayoutParam.weight == ReadLines.LONGTH
                binding.editTextScroll.isVisible = !isOpen && isShow
            }
        }
        binding.editToolBar.isVisible = !isOpen
        binding.pageSearch.cmdindexSearchCancel.isVisible = !isOpen
        val isNotVisiblePageOrWebSearch =
            !binding.pageSearch.cmdclickPageSearchToolBar.isVisible
                    && !binding.webSearch.webSearchToolbar.isVisible
        val editWeight = if(
            isOpen
            && isNotVisiblePageOrWebSearch
        ) editBodySize.HIDE.size
        else if(isOpen) editBodySize.OPEN.size
        else editBodySize.SHRINK.size
        listenerForEditSizeWhenLong?.onLongTermKeyBoardOpenAjustForEdit(
            editWeight
        )
        if(!isOpen) activity?.currentFocus?.clearFocus()
    }
}

private enum class editBodySize(
    val size: Float
){
    HIDE(0F),
    OPEN(0.07F),
    SHRINK(0.04F)
}
