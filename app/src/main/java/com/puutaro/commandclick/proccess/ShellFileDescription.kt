package com.puutaro.commandclick.proccess

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.text.LineBreaker
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.util.CommandClickVariables


class ShellFileDescription {
    companion object {
        fun show(
            context: Context?,
            currentShellContentsList: List<String>
        ){
            if(context == null) return
            val descripitionContents = mekeDescriptionContents(
                currentShellContentsList
            )

            val linearLayout = addLinearLayout(
                context,
                descripitionContents
            ) ?: return

            val alertDialog = AlertDialog.Builder(context)
                .setTitle(
                    "Description"
                )
                .setView(linearLayout)
                .setPositiveButton("OK", null)
                .show()

            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
                context.getColor(android.R.color.black) as Int
            )
            alertDialog.getWindow()?.setGravity(Gravity.BOTTOM)
        }

        private fun addLinearLayout (
            context: Context?,
            descripitionContents: String,
        ): LinearLayout? {
            val textView = TextView(context)
            textView.setText(descripitionContents)
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


        private fun mekeDescriptionContents(
            currentShellContentsList: List<String>
        ): String {
            val labelingSectionStart = CommandClickShellScript.LABELING_SECTION_START
            val labelingSectionEnd = CommandClickShellScript.LABELING_SECTION_END

            val descripitionContentsList =
                CommandClickVariables.substituteVariableListFromHolder(
                    currentShellContentsList,
                    labelingSectionStart,
                    labelingSectionEnd,
                )?.filter {
                    (
                            !it.startsWith(labelingSectionStart)
                                    && !it.endsWith(labelingSectionStart)
                            )
                            && (
                            !it.startsWith(labelingSectionEnd)
                                    && !it.endsWith(labelingSectionEnd)
                            )
                }?.map {
                    it
                        .trim(' ')
                        .removePrefix("#")
                } ?: return String()
            return descripitionContentsList.joinToString("\n")
        }
    }
}