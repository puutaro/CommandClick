package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.content.DialogInterface
import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SpanableJsDialog(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context
    private val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()

    fun create(
        title: String,
        htmlSpannableStr: String

    ){
        terminalViewModel.onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                execCreate(
                    title,
                    htmlSpannableStr
                )
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (!terminalViewModel.onDialog) break
                }
            }
        }
    }

    fun execCreate(
        title: String,
        htmlSpannableStr: String
    ){
        val asciiText = TextView(context)

        asciiText.text = makeSpannable(
            htmlSpannableStr
        )
        val scrollView = makeScrollView()
        val linearLayout = makeLinearLayout()
        linearLayout.addView(asciiText)
        scrollView.addView(linearLayout)

        val alertDialog = android.app.AlertDialog.Builder(
            context
        )
            .setTitle(title)
            .setView(scrollView)
            .setPositiveButton("OK", DialogInterface.OnClickListener{ dialog, which ->
                terminalViewModel.onDialog = false
            })
            .show()
        alertDialog.window?.setGravity(Gravity.BOTTOM)
        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
            }
        })
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(
            context?.getColor(android.R.color.black) as Int
        )
    }

    private fun makeLinearLayout(
    ): LinearLayout {
        val linearLayout = LinearLayout(context)
        linearLayout.orientation =  LinearLayout.VERTICAL
        linearLayout.weightSum = 1F
        val linearLayoutParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutParam.marginStart = 20
        linearLayoutParam.marginEnd = 20
        linearLayout.layoutParams = linearLayoutParam
        return linearLayout
    }

    private fun makeScrollView(
    ): ScrollView {
        val scrollView = ScrollView(context)
        val linearLayoutForScrollViewParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        scrollView.layoutParams = linearLayoutForScrollViewParam
        return scrollView
    }

    private fun makeSpannable(
        htmlSpannableStr: String
    ): Spanned {
        val tempSpannable = terminalViewModel.tempSpannable
        if(
            tempSpannable != null
        ) {
            terminalViewModel.tempSpannable = null
            return tempSpannable
        }
        return Html.fromHtml(
                htmlSpannableStr,
                Html.FROM_HTML_MODE_LEGACY
            )

    }
}
