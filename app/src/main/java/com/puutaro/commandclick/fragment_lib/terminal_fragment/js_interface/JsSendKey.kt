package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.R.attr.text
import android.R.id.input
import android.app.Instrumentation
import android.os.SystemClock
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment


class JsSendKey(
    private val terminalFragment: TerminalFragment
) {

    val constext = terminalFragment.context

    @JavascriptInterface
    fun send(
        keyName: String,
    ){
        when(keyName){
            "ls" -> typeLs()
            "shift+a" -> shiftA()
            "downKey" -> down()
            "upKey" -> up()
        }
        return
    }

    private fun up(){
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_DPAD_UP,
                1,
            )
        )

    }

    private fun down(){
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_DPAD_DOWN,
                1,
            )
        )
    }

    private fun typeLs(){
        val pwdCmd = "pwd"
        val mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)
        val events = mKeyCharacterMap.getEvents(pwdCmd.toCharArray())
        val enterKeyDown = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
        val terminalWebView = terminalFragment.binding.terminalWebView
        events.forEach {
            terminalWebView.dispatchKeyEvent(
                it
            )
        }

        terminalWebView.dispatchKeyEvent(enterKeyDown)
        return

//        val inputConnection: InputConnection = terminalFragment.binding.terminalWebView.onCreateInputConnection(EditorInfo())
//        for (i in pwdCmd.indices) {
//            val c: Char = input.charAt(i)
//            sendKeyEvent(inputConnection, c)
//        }
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_V,
                1,
                KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON,
            )
        )
        return
        val ctrlKeyDown = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_CTRL_LEFT)
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(ctrlKeyDown)

        val shiftKeyDown = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT)
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(shiftKeyDown)

        val vKeyDown = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_V)
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(vKeyDown)

        val vKeyUp = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_V)
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(vKeyUp)

        val shiftKeyUp = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SHIFT_LEFT)
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(shiftKeyUp)

        val ctrlKeyUp = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_CTRL_LEFT)
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(ctrlKeyUp)
        return
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_V,
                2,
                KeyEvent.META_SHIFT_ON and KeyEvent.META_CTRL_ON,
            )
        )
        val event = KeyEvent(
            SystemClock.uptimeMillis(), "ls", 0, 0
        )
        val aa = terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            event
        )
        Toast.makeText(
            constext,
            aa.toString(),
            Toast.LENGTH_SHORT
        ).show()
        return
//        terminalFragment.binding.terminalWebView.requestFocus()
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_CTRL_LEFT,
                2,
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_V,
                0,
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_V,
                0,
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_CTRL_LEFT,
                0,
            )
        )
        return

        val inst = Instrumentation()
        inst.sendStringSync("ls")
        KeyEvent(
            KeyEvent.META_SHIFT_ON,
            KeyEvent.KEYCODE_C
        )
//        val event = KeyEvent(
//            SystemClock.uptimeMillis(), "ls", 0, 0
//        )
//        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
//            event
//        )

        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_V,
                1,
                KeyEvent.META_SHIFT_ON or KeyEvent.META_CTRL_ON
            )
        )
        return
//        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_L)
//        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_S)
        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
    }

    private fun shiftA(){

//        val mInputConnection = BaseInputConnection(
//            terminalFragment.binding.terminalWebView,
//            true
//        )
//        val kd = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MENU)
//        val ku = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MENU)
//        mInputConnection.sendKeyEvent(KeyEvent(
//            KeyEvent.ACTION_DOWN,
//            KeyEvent.KEYCODE_CTRL_LEFT
//        ))
//        mInputConnection.sendKeyEvent(KeyEvent(
//            KeyEvent.ACTION_DOWN,
//            KeyEvent.KEYCODE_A
//        ))
//        mInputConnection.sendKeyEvent(KeyEvent(
//            KeyEvent.ACTION_UP,
//            KeyEvent.KEYCODE_A
//        ))
//        mInputConnection.sendKeyEvent(KeyEvent(
//            KeyEvent.ACTION_UP,
//            KeyEvent.KEYCODE_CTRL_LEFT
//        ))
//
//        return
        terminalFragment.binding.terminalWebView.
            dispatchKeyEvent(
                KeyEvent(
                    SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(),
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_A,
                    1,
                    KeyEvent.META_SHIFT_ON
                )
            )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_A,
                1,
                KeyEvent.META_CTRL_ON
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_C,
                1,
                KeyEvent.META_CTRL_ON
            )
        )
        return
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.META_SHIFT_ON,
                KeyEvent.KEYCODE_C
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.META_SHIFT_LEFT_ON,
                KeyEvent.KEYCODE_C
            )
        )

        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.META_SHIFT_RIGHT_ON,
                KeyEvent.KEYCODE_C
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.KEYCODE_SHIFT_LEFT,
                KeyEvent.KEYCODE_C
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.KEYCODE_SHIFT_RIGHT,
                KeyEvent.KEYCODE_C
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_SHIFT_LEFT
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_C
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_C
            )
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_SHIFT_LEFT
            )
        )
        return
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A)
        )
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_A)
        )
        return
//        val keyEvent = KeyEvent(KeyEvent.META_CTRL_ON, KeyEvent.KEYCODE_C)
//        inst.sendKeySync(keyEvent)
//        return
        val inst = Instrumentation()
        val keyEvent = KeyEvent(KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_A)
        inst.sendKeySync(keyEvent)

        longPress(
            inst, KeyEvent.KEYCODE_SHIFT_LEFT
        )
        val a = inst.sendKeyDownUpSync(KeyEvent.KEYCODE_C)


        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_SHIFT_LEFT
            )
        )
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_C
            )
        )
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_C)
        )
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_SHIFT_LEFT
            )
        )
        return
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_SHIFT_LEFT)
        )
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_A
            )
        )
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_A)
        )
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_SHIFT_LEFT
            )
        )
    }
    private fun longPress(
        inst: Instrumentation,
        key: Int
    ) {
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis()
        val event1 = KeyEvent(downTime, eventTime, KeyEvent.ACTION_DOWN, key, 0)
        val event2 = KeyEvent(downTime, eventTime, KeyEvent.ACTION_DOWN, key, 1)
        inst.sendKeySync(event1)
        inst.sendKeySync(event2)
    }

    private fun shiftDown(){
        val inst = Instrumentation()
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_SHIFT_LEFT)
        )
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_A
            )
        )
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_A)
        )
        inst.sendKeySync(
            KeyEvent(
                KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_SHIFT_LEFT
            )
        )
    }
}