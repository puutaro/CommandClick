package com.puutaro.commandclick.custtom_view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.puutaro.commandclick.R


//class MenuTextView(context: Context) :
//    AppCompatTextView(context) {
//
//    init {
//        inflate(
//            context,
//            R.layout.menu_text_view,
//            null
//        )
//    }
//
//}

class MenuTextView (
    context: Context
) : AppCompatTextView(context)
{


    init {
        init()
    }

    private fun init() {
        inflate(context, R.layout.menu_text_view, null)
    }

}