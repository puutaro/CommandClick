package com.puutaro.commandclick.custom_view

import android.content.Context
import android.widget.TextView
import com.puutaro.commandclick.R
import com.skydoves.colorpickerview.AlphaTileView
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.flag.FlagView


class ColorPickerFlag(context: Context?, layout: Int) : FlagView(context, layout) {
    private val textView: TextView = findViewById<TextView>(R.id.flag_color_code)
    private val alphaTileView: AlphaTileView = findViewById<AlphaTileView>(R.id.flag_color_layout)

    override fun onRefresh(colorEnvelope: ColorEnvelope) {
        textView.text = "#" + colorEnvelope.hexCode
        alphaTileView.setPaintColor(colorEnvelope.color)
    }
}