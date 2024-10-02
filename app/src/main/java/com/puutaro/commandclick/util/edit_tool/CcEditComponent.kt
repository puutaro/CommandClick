package com.puutaro.commandclick.util.edit_tool

import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.LogSystems

//object CcEditComponent {
//
////    fun makeEditLinearLayoutList(
////        editFragment: EditFragment,
////    ): List<LinearLayoutCompat> {
////        val binding = editFragment.binding
////        return when(editFragment.existIndexList){
////            true -> listOf(
////                binding.editListInnerTopLinearLayout,
////                binding.editListInnerBottomLinearLayout,
////            )
////            else -> listOf(
////                binding.editLinearLayout,
////            )
////        }
////    }
//
////    fun findEditTextView(
////        currentId: Int,
////        editLinearLayoutList: List<LinearLayoutCompat>,
////    ): EditText? {
////        editLinearLayoutList.forEach {
////            val extractedEditText = it.findViewById<EditText>(currentId)
////            if(
////                extractedEditText != null
////            ) return extractedEditText
////        }
////        LogSystems.stdWarn(
////            "no exist editText id: ${currentId}"
////        )
////        return null
////    }
////
////    fun findSpinnerView(
////        currentId: Int,
////        editLinearLayoutList: List<LinearLayoutCompat>,
////    ): Spinner? {
////        editLinearLayoutList.forEach {
////            val extractedSpinner = it.findViewById<Spinner>(currentId)
////            if(
////                extractedSpinner != null
////            ) return extractedSpinner
////        }
////        LogSystems.stdWarn(
////            "no exist editText id: ${currentId}"
////        )
////        return null
////    }
//}