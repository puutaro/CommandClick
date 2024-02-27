package com.puutaro.commandclick.util

import android.content.Context
import android.graphics.text.LineBreaker
import android.os.Build
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.puutaro.commandclick.R


class LinearLayoutAdderForDialog {
    companion object {

        fun add(
            context: Context?,
            descriptionContents: String,
        ): LinearLayout? {
                val textView = TextView(context)
            textView.text = descriptionContents
                textView.setTextIsSelectable(true)
                if(Build.VERSION.SDK_INT > 29) textView.breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE
                if(context == null) return null
                textView.setTextColor(
                    context.getColor(R.color.black)
                )
                textView.textSize = 17F
                textView.setPadding(10, 0, 10, 0)

                val scrollView = ScrollView(context)
                val linearLayoutForScrollViewParam = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                scrollView.layoutParams = linearLayoutForScrollViewParam
                scrollView.addView(textView)

                val linearLayout = LinearLayout(context)
                linearLayout.orientation =  LinearLayout.VERTICAL
                linearLayout.weightSum = 1F
                val linearLayoutParam = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                linearLayout.layoutParams = linearLayoutParam
                linearLayout.addView(scrollView)
                return linearLayout
            }
        }
    }