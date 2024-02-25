package com.puutaro.commandclick.proccess.edit.lib

import android.content.Context
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources
import com.puutaro.commandclick.R
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ButtonViewProducer

object ButtonSetter {
    fun set(
        context: Context?,
        insertButtonView: Button,
        buttonMap: Map<String, String>?
    ){
        if(
            context == null
        ) return
        val defaultButtonLayout = R.drawable.edit_button_layout
        val buttonLayout = buttonMap?.get(
            ButtonViewProducer.ButtonEditKey.onBorder.name
        ).let {
            when(it){
                ButtonViewProducer.OnBoarderKeyForButton.OFF.name
                -> R.drawable.edit_button_layout_no_stroke
                else -> defaultButtonLayout
            }
        }
        insertButtonView.background = AppCompatResources.getDrawable(
            context,
            buttonLayout
        )
        buttonMap?.get(
            ButtonViewProducer.ButtonEditKey.textSize.name
        )?.let {
            if(
                it.isEmpty()
            ) return@let
            val textSize = try{
                it.toFloat()
            } catch(e: Exception){
                return@let
            }
            insertButtonView.textSize = textSize
        }
        insertButtonView.setTextColor(
            context.getColor(R.color.terminal_color)
        )
    }
}