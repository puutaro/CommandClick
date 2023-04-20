package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.icu.util.Calendar
import android.webkit.JavascriptInterface
import android.widget.TextView
import com.puutaro.commandclick.fragment.TerminalFragment

class JsUtil(
    private val terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun sleep(sleepMiriTime: Int){
        Thread.sleep(sleepMiriTime.toLong())
    }

    @JavascriptInterface
    fun copyToClipboard(text: String?, fontSize: Int) {
        val clipboard: ClipboardManager? =
            terminalFragment.activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val textView = TextView(terminalFragment.context)
        textView.setText(text)
        textView.textSize = fontSize.toFloat()
        val clipText = textView.text
        val clip = ClipData.newPlainText("cmdclick", clipText)
        clipboard?.setPrimaryClip(clip)
    }

    @JavascriptInterface
    fun echoFromClipboard():String {
        val clipboard: ClipboardManager? =
            terminalFragment.activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        return clipboard?.primaryClip?.getItemAt(0)?.text.toString()
    }

    @JavascriptInterface
    fun convertDateTimeToMiliTime(
        datetime: String
    ): String {
        val dateTimeList = datetime.split("T")
        val yearMonthDay = dateTimeList.firstOrNull()?.split('-')
            ?: return System.currentTimeMillis().toString()
        val hourMinutes = dateTimeList.getOrNull(1)?.split(':')
            ?: return System.currentTimeMillis().toString()
        val calender = java.util.Calendar.getInstance()
        val year = yearMonthDay.getOrNull(0)?.toInt()
            ?: calender.get(java.util.Calendar.YEAR)
        val month = yearMonthDay.getOrNull(1)?.toInt()
            ?: calender.get(java.util.Calendar.MONTH)
        val day = yearMonthDay.getOrNull(2)?.toInt()
            ?: calender.get(java.util.Calendar.DATE)
        val hour = hourMinutes.firstOrNull()?.toInt()
            ?: calender.get(java.util.Calendar.HOUR)
        val minute = hourMinutes.getOrNull(1)?.toInt()
            ?: calender.get(java.util.Calendar.MINUTE)
        val calenderDatetime: Calendar = Calendar.getInstance()
        calenderDatetime.set(year, month, day, hour, minute)
        return calenderDatetime.timeInMillis.toString()
    }
}