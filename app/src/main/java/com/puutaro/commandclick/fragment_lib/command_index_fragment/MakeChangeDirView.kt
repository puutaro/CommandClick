package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isInvisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.databinding.CommandIndexFragmentBinding
import com.puutaro.commandclick.fragment.CommandIndexFragment


class MakeChangeDirView(
    commandIndexFragment: CommandIndexFragment,
    private val binding: CommandIndexFragmentBinding,
) {

    val context = commandIndexFragment.context

    fun addHeaderPrompt(
        promptString: String
    ){
        val textView = TextView(context)
        textView.setText("\t${promptString}")
        textView.textSize = 20F
        textView.setBackgroundColor(context?.getColor(R.color.white) as Int)
        textView.setTextColor(context.getColor(R.color.black) as Int)
        textView.setPadding(0, 40, 0, 30)
        val innerLayout = LinearLayout(context)
        innerLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        innerLayout.orientation = LinearLayout.VERTICAL
        innerLayout.setBackgroundColor(context.getColor(R.color.black))
        innerLayout.addView(textView)
        binding.commandIndexFragment.addView(innerLayout)
    }

    fun hideToolbar(){
        val cmdindexAllToolbarLinearLayout = binding.cmdindexAllToolbarLinearLayout
        cmdindexAllToolbarLinearLayout.layoutParams.height = 0
        cmdindexAllToolbarLinearLayout.isInvisible = true
    }

}