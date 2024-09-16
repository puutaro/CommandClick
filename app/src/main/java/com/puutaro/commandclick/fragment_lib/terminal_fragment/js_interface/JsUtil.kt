package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.icu.util.Calendar
import android.webkit.JavascriptInterface
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CopyToClip
import com.puutaro.commandclick.util.datetime.DateTimeConverter
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference
import java.util.Locale

class JsUtil(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun copyToClipboard(
        text: String?,
        fontSize: Int
    ) {
        val terminalFragment = terminalFragmentRef.get()
            ?: return

        CopyToClip.copy(
            terminalFragment,
            text,
            fontSize,
        )
    }

    @JavascriptInterface
    fun echoFromClipboard(): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()

        val clipboard: ClipboardManager? =
            terminalFragment.activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clipText = clipboard
            ?.primaryClip
            ?.getItemAt(0)
            ?.coerceToStyledText(terminalFragment.context)
            ?: return String()
        val clipBoardCon = clipText.toString()
        return clipBoardCon

    }

    @JavascriptInterface
    fun convertDateTimeToMiliTime(
        datetime: String
    ): String {
        return DateTimeConverter.convert(
            datetime
        )
    }

    @JavascriptInterface
    fun lang(): String {
        val settingLang = Locale.getDefault().language
        return settingLang
    }
}