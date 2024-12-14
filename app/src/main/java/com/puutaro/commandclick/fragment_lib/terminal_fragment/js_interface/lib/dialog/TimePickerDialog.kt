package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.TimePickerDialog
import android.content.DialogInterface
import android.view.Gravity
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.LogSystems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.Locale

class TimePickerDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var returnValue = String()
    private var onDialog = false
    val keySeparator = '|'

    private enum class ColorPickerConfig(
        val key: String
    ){
        TITLE("title"),
    }

    fun create(
        configMapCon: String,
    ): String {
        returnValue = String()
        onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        configMapCon,
                    )
                } catch(e: Exception){
                    LogSystems.stdErr(
                        terminalFragmentRef.get()?.context,
                        e.toString()
                    )
                    dismiss(String())
                }
            }
            withContext(Dispatchers.IO) {
                for (i in 1..1000) {
                    delay(100)
                    if (
                        !onDialog
                    ) break
                }
            }
        }
        return returnValue
    }

    private fun execCreate(
        configMapCon: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
            ?: return Unit.also {
                dismiss(String())
            }
        val calender = Calendar.getInstance()
        val hour = calender.get(Calendar.HOUR)
        val minutes = calender.get(Calendar.MINUTE)

        val dateDialog = TimePickerDialog(
            context, {
                    _, getHour, getMinutes ->
                val timeStr = String.format(
                    Locale.US,
                    "%02d:%02d",
                    getHour,
                    getMinutes
                )
                dismiss(
                    timeStr
                )
            },
            hour,
            minutes,
            true
        )
        dateDialog.show()
        dateDialog.apply {
            setOnCancelListener {
                dismiss(String())
            }
            getButton(
                DialogInterface.BUTTON_POSITIVE
            ).setTextColor(
                context.getColor(R.color.fannel_icon_color) as Int
            )
            getButton(
                DialogInterface.BUTTON_NEGATIVE
            ).setTextColor(
                context.getColor(R.color.fannel_icon_color) as Int
            )

            window?.apply {
//                setLayout(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
                setGravity(Gravity.BOTTOM)
            }
        }
    }

    private fun dismiss(
        returnValueStr: String,
    ){
        onDialog = false
        returnValue = returnValueStr
    }

}