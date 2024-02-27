package com.puutaro.commandclick.util.edit_tool

import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.LogSystems

object CcEditComponent {

    fun makeEditLinearLayoutList(
        editFragment: EditFragment,
    ): List<LinearLayout> {
        val binding = editFragment.binding
        return when(editFragment.existIndexList){
            true -> listOf(
                binding.editListInnerTopLinearLayout,
                binding.editListInnerBottomLinearLayout,
            )
            else -> listOf(
                binding.editLinearLayout,
            )
        }
    }

    fun findEditTextView(
        currentId: Int,
        editLinearLayoutList: List<LinearLayout>,
    ): EditText? {
        editLinearLayoutList.forEach {
            val extractedEditText = it.findViewById<EditText>(currentId)
            if(
                extractedEditText != null
            ) return extractedEditText
        }
        LogSystems.stdWarn(
            "no exist editText id: ${currentId}"
        )
        return null
    }

    fun findSpinnerView(
        currentId: Int,
        editLinearLayoutList: List<LinearLayout>,
    ): Spinner? {
        editLinearLayoutList.forEach {
            val extractedSpinner = it.findViewById<Spinner>(currentId)
            if(
                extractedSpinner != null
            ) return extractedSpinner
        }
        LogSystems.stdWarn(
            "no exist editText id: ${currentId}"
        )
        return null
    }
}