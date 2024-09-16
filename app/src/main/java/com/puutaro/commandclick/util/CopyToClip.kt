package com.puutaro.commandclick.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.TextView
import androidx.fragment.app.Fragment

object CopyToClip {
    fun copy(
        fragment: Fragment,
        text: String?,
        fontSize: Int,
    ){
        val clipboard: ClipboardManager? =
            fragment.activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val textView = TextView(fragment.context)
        textView.setText(text)
        textView.textSize = fontSize.toFloat()
        val clipText = textView.text
        val clip = ClipData.newPlainText("cmdclick", clipText)
        clipboard?.setPrimaryClip(clip)
    }
}