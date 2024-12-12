package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.content.DialogInterface
import android.view.Gravity
import android.view.ViewGroup
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.LogSystems
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

class ColorPickerJsDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var returnValue = String()
    private var onDialog = false

    fun create(
        title: String,
    ): String {
        returnValue = String()
        onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        title,
                        )
                } catch(e: Exception){
                    LogSystems.stdErr(
                        terminalFragmentRef.get()?.context,
                        e.toString()
                    )
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
        title: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val colorPickerDialogBuilder = ColorPickerDialog.Builder(
            context,
//            R.style.FullScreenRoundCornerDialogTheme
        ).apply {
            setTitle(title)
            setPreferenceName("MyColorPickerDialog")
            val okEmoji = "✅"
            setPositiveButton(okEmoji, ColorEnvelopeListener { envelope, fromUser ->
                val hexCodeSource = envelope.hexCode
                val hexCode = "#${hexCodeSource.substring(2)}"
                dismiss(hexCode)
            })
            val cancelEmoji = "❌"
            setNegativeButton(cancelEmoji, DialogInterface.OnClickListener { dialogInterface, i ->
                dismiss(String())
                dialogInterface.dismiss()
            })
            setOnCancelListener {
                dismiss(String())
            }
        }
        val colorPickerDialog = colorPickerDialogBuilder.create()
        colorPickerDialog.show()
        colorPickerDialog.getButton(
            DialogInterface.BUTTON_POSITIVE
        ).setTextColor(
            context?.getColor(R.color.black) as Int
        )
        colorPickerDialog.getButton(
            DialogInterface.BUTTON_NEGATIVE
        ).setTextColor(
            context.getColor(
                R.color.black
            )
        )
        colorPickerDialog.window
            ?.apply {
//                setLayout(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
                setGravity(Gravity.BOTTOM)
            }
    }

    private fun dismiss(
        returnValueStr: String,
    ){
        onDialog = false
        returnValue = returnValueStr
    }
}