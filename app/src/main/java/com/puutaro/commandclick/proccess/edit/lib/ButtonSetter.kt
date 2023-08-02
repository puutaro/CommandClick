package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import com.puutaro.commandclick.R

object ButtonSetter {
    fun set(
        context: Context?,
        insertButtonView: Button,
    ){
        if(
            context == null
        ) return
        insertButtonView.background = AppCompatResources.getDrawable(
            context,
            R.drawable.edit_button_layout
        )
        insertButtonView.setTextColor(
            context.getColor(R.color.terminal_color)
        )
    }
}