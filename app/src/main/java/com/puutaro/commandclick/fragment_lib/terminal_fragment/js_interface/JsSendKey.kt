package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.os.SystemClock
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment


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
        SpecialKeys.values().filter {
            it.str == keyName
        }.firstOrNull()?.let {
            keyDownEvent(
                it.keyCode
            )
            return
        }
        normalOrModiferHandler(keyName)
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
                0,
                metaCode
            )
        )
    }

    private fun normalOrModiferHandler(
        str: String,
    ){
        when(
            judgeModifer(str)
        ) {
            ModifierKeyName.ctrl_shift_alt
            -> execModifierPlusNormal(
                str,
                KeyEvent.META_CTRL_ON
                        or KeyEvent.META_SHIFT_ON
                        or KeyEvent.META_ALT_ON,
            )
            ModifierKeyName.ctrl_shift
            -> execModifierPlusNormal(
                str,
                KeyEvent.META_CTRL_ON
                        or KeyEvent.META_SHIFT_ON,
            )
            ModifierKeyName.ctrl_alt
            -> execModifierPlusNormal(
                str,
                KeyEvent.META_CTRL_ON
                        or KeyEvent.META_ALT_ON,
            )
            ModifierKeyName.ctrl
            -> execModifierPlusNormal(
                str,
                KeyEvent.META_CTRL_ON
            )
            ModifierKeyName.shift
            -> execModifierPlusNormal(
                str,
                KeyEvent.META_SHIFT_ON
            )
            ModifierKeyName.alt
            -> execModifierPlusNormal(
                str,
                KeyEvent.META_ALT_ON
            )
            else
            -> typeStr(
                str
            )
        }
    }

    private fun judgeModifer(
        str: String
    ): ModifierKeyName {
        val ctrlShiftAltPrefix = "${ModifierKeyName.ctrl_shift_alt.name}${makeModifierSepalator}"
        val ctrlShiftPrefix = "${ModifierKeyName.ctrl_shift.name}${makeModifierSepalator}"
        val ctrlAltPrefix = "${ModifierKeyName.ctrl_alt.name}${makeModifierSepalator}"
        val ctrlPrefix = "${ModifierKeyName.ctrl.name}${makeModifierSepalator}"
        val shiftPrefix = "${ModifierKeyName.shift.name}${makeModifierSepalator}"
        val altPrefix = "${ModifierKeyName.alt.name}${makeModifierSepalator}"

        val isCtrlShiftAltPrefix = str.startsWith(ctrlShiftAltPrefix)
        val isCtrlShiftPrefix = str.startsWith(ctrlShiftPrefix)
        val isCtrlAltPrefix = str.startsWith(ctrlAltPrefix)
        val isCtrlPrefix = str.startsWith(ctrlPrefix)
        val isShiftPrefix = str.startsWith(shiftPrefix)
        val isAltPrefix = str.startsWith(altPrefix)
        val isOneSepalator = str.split(makeModifierSepalator).size == 2

        if(isCtrlShiftAltPrefix && isOneSepalator) return ModifierKeyName.ctrl_shift_alt
        if(isCtrlShiftPrefix && isOneSepalator) return ModifierKeyName.ctrl_shift
        if(isCtrlAltPrefix && isOneSepalator) return ModifierKeyName.ctrl_alt
        if(isCtrlPrefix && isOneSepalator) return ModifierKeyName.ctrl
        if(isShiftPrefix && isOneSepalator) return ModifierKeyName.shift
        if(isAltPrefix && isOneSepalator) return ModifierKeyName.alt
        return ModifierKeyName.normalStr
    }

    private fun execModifierPlusNormal(
        modifierConbiStr: String,
        metaKeyCode: Int,
    ){
        val modifierNormalPair = modifierConbiStr.split(makeModifierSepalator)
        val normalKey = modifierNormalPair.lastOrNull()
            ?: return
        val keyCode = SpecialKeys.values().filter {
            it.str == normalKey
        }.firstOrNull()?.keyCode ?: let {
            mKeyCharacterMap.getEvents(normalKey.toCharArray()).first().keyCode
        }
        keyDownWithMeta(
            metaKeyCode,
            keyCode,
        )
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
}

private enum class ModifierKeyName {
    ctrl,
    shift,
    alt,
    ctrl_shift,
    ctrl_alt,
    ctrl_shift_alt,
    normalStr,
}

private enum class SpecialKeys(
    val str: String,
    val keyCode: Int,
){
    Enter("enter", KeyEvent.KEYCODE_ENTER),
    Down("down", KeyEvent.KEYCODE_DPAD_DOWN),
    Up("up", KeyEvent.KEYCODE_DPAD_UP),
    Left("left", KeyEvent.KEYCODE_DPAD_LEFT),
    Right("right", KeyEvent.KEYCODE_DPAD_RIGHT),
    PageDown("pageDown", KeyEvent.KEYCODE_PAGE_DOWN),
    PageUp("pageUp", KeyEvent.KEYCODE_PAGE_UP),
    Esc("esc", KeyEvent.KEYCODE_ESCAPE),
    Home("home", KeyEvent.KEYCODE_MOVE_HOME),
    End("end", KeyEvent.KEYCODE_MOVE_END),
    Backspace("backspace", KeyEvent.KEYCODE_DEL),
    Space("space", KeyEvent.KEYCODE_SPACE),
    Tab("tab", KeyEvent.KEYCODE_TAB),
}