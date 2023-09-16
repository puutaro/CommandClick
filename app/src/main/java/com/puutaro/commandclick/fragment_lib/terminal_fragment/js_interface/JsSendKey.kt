package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.os.SystemClock
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.DialogObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class JsSendKey(
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context
    private val mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)
    private val makeModifierSepalator = "___"


    @JavascriptInterface
    fun send(
        keyName: String,
    ){
        when(keyName){
            "copy" -> copy()
            "paste" -> paste()
            "enter" -> enter()
            "down" -> down()
            "up" -> up()
            "left" -> left()
            "right" -> right()
            "pageDown" -> pageDown()
            "pageUp" -> pageUp()
            "esc" -> esc()
            "home" -> home()
            "end" -> end()
            "backspace" -> backspace()
            "space" -> space()
            else -> normalOrMpdiferType(keyName)
        }
    }

    private fun keyDownEvent(
        keycode: Int
    ){
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                KeyEvent.ACTION_DOWN,
                keycode
            )
        )
    }

    private fun keyDownWithMeta(
        metaCode: Int,
        keycode: Int
    ){
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                KeyEvent.ACTION_DOWN,
                keycode,
                1,
                metaCode
            )
        )
    }

    private fun up(){
        keyDownEvent(
            KeyEvent.KEYCODE_DPAD_UP,
        )
    }

    private fun down(){
        keyDownEvent(
            KeyEvent.KEYCODE_DPAD_DOWN,
        )
    }

    private fun left(){
        keyDownEvent(
            KeyEvent.KEYCODE_DPAD_LEFT,
        )
    }

    private fun right(){
        keyDownEvent(
            KeyEvent.KEYCODE_DPAD_RIGHT,
        )
    }

    private fun pageDown(){
        keyDownEvent(
            KeyEvent.KEYCODE_PAGE_DOWN,
        )
    }

    private fun pageUp(){
        keyDownEvent(
            KeyEvent.KEYCODE_PAGE_UP,
        )
    }

    private fun esc(){
        keyDownEvent(
            KeyEvent.KEYCODE_ESCAPE,
        )
    }

    private fun home(){
        keyDownEvent(
            KeyEvent.KEYCODE_MOVE_HOME,
        )
    }

    private fun end(){
        keyDownEvent(
            KeyEvent.KEYCODE_MOVE_END,
        )
    }
    private fun backspace(){
        keyDownEvent(
            KeyEvent.KEYCODE_DEL,
        )
    }

    private fun space(){
        keyDownEvent(
            KeyEvent.KEYCODE_SPACE,
        )
    }

    private fun copy(){
        keyDownWithMeta(
            KeyEvent.META_CTRL_ON,
            KeyEvent.KEYCODE_INSERT,
        )
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO){
                delay(100)
            }
            Toast.makeText(
                context,
                "copy: ${JsUtil(terminalFragment).echoFromClipboard()}",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun paste(){
        keyDownWithMeta(
            KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON,
            KeyEvent.KEYCODE_V,
        )
    }

    private fun shiftCtrlA(){
        keyDownWithMeta(
            KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON,
            KeyEvent.KEYCODE_A,
        )
        keyDownWithMeta(
            KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON,
            KeyEvent.KEYCODE_C,
        )
        DialogObject.simpleTextShow(
            context,
            "term text",
            JsUtil(terminalFragment).echoFromClipboard(),
            true
        )

    }

    private fun normalOrMpdiferType(
        str: String,
    ){
        Toast.makeText(
            context,
            judgeModifer(str).toString(),
            Toast.LENGTH_SHORT
        ).show()
        when(
            judgeModifer(str)
        ) {
            true -> makeModifierKeyConbi(str)
            else -> typeStr(str)

        }
    }

    private fun typeStr(
        str: String
    ){

        val events = mKeyCharacterMap.getEvents(str.toCharArray())
        val terminalWebView = terminalFragment.binding.terminalWebView
        events.forEach {
            terminalWebView.dispatchKeyEvent(
                it
            )
        }
    }

    private fun judgeModifer(
        str: String
    ): Boolean {
        val ctrlPrefix = "${modifierKeyName.ctrl.name}${makeModifierSepalator}"
        val shiftPrefix = "${modifierKeyName.shift.name}${makeModifierSepalator}"
        val altPrefix = "${modifierKeyName.alt.name}${makeModifierSepalator}"
        val isCtrlPrefix = str.startsWith(ctrlPrefix)
        val isShiftPrefix = str.startsWith(shiftPrefix)
        val isAltPrefix = str.startsWith(altPrefix)
        val isOneSepalator = str.split(makeModifierSepalator).size == 2
        return (isCtrlPrefix && isOneSepalator)
                || (isShiftPrefix && isOneSepalator)
                || (isAltPrefix && isOneSepalator)

    }

    private fun makeModifierKeyConbi(
        modifierConbiStr: String,
    ){
        val makeModifierSeparator = "___"
        val modifierNormalPair = modifierConbiStr.split(makeModifierSeparator)
        val modifierKey = modifierNormalPair.firstOrNull()
            ?: return
        val normalKey = modifierNormalPair.lastOrNull()
            ?: return
        val keyEvent = mKeyCharacterMap.getEvents(normalKey.toCharArray()).first()
        when(modifierKey) {
            modifierKeyName.ctrl.name -> keyDownWithMeta(
                KeyEvent.META_CTRL_ON,
                keyEvent.keyCode
            )
            modifierKeyName.shift.name -> keyDownWithMeta(
                KeyEvent.META_SHIFT_ON,
                keyEvent.keyCode
            )
            modifierKeyName.alt.name -> keyDownWithMeta(
                KeyEvent.META_ALT_ON,
                keyEvent.keyCode
            )
        }
    }

    private fun enter(){
        keyDownEvent(
            KeyEvent.KEYCODE_MOVE_END,
        )
        val enterKeyDown = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
        terminalFragment.binding.terminalWebView.dispatchKeyEvent(
            KeyEvent(
                enterKeyDown
            )
        )
    }

    private fun shiftA(){
        keyDownWithMeta(
            KeyEvent.KEYCODE_A,
            KeyEvent.META_SHIFT_ON,
        )
    }
}

private enum class modifierKeyName {
    ctrl,
    shift,
    alt,
}