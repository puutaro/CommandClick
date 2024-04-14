package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.os.SystemClock
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment


class JsSendKey(
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context
    private val mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)
    private val makeModifierSeparator = "___"


    @JavascriptInterface
    fun sendPocket(
        keyName: String,
    ){
        val webView = terminalFragment.webViewDialogInstance?.findViewById<WebView>(
            R.id.webview_dialog_webview
        ) ?: return
        SpecialKeys.values().filter {
            it.str == keyName
        }.firstOrNull()?.let {
            keyDownEvent(
                it.keyCode,
                webView
            )
            return
        }
        normalOrModiferHandler(keyName)
    }

    @JavascriptInterface
    fun send(
        keyName: String,
    ){
        SpecialKeys.values().filter {
            it.str == keyName
        }.firstOrNull()?.let {
            keyDownEvent(
                it.keyCode,
                terminalFragment.binding.terminalWebView
            )
            return
        }
        normalOrModiferHandler(keyName)
    }

    private fun keyDownEvent(
        keycode: Int,
        webview: WebView?
    ){
        webview?.dispatchKeyEvent(
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
        val ctrlShiftAltPrefix = "${ModifierKeyName.ctrl_shift_alt.name}${makeModifierSeparator}"
        val ctrlShiftPrefix = "${ModifierKeyName.ctrl_shift.name}${makeModifierSeparator}"
        val ctrlAltPrefix = "${ModifierKeyName.ctrl_alt.name}${makeModifierSeparator}"
        val ctrlPrefix = "${ModifierKeyName.ctrl.name}${makeModifierSeparator}"
        val shiftPrefix = "${ModifierKeyName.shift.name}${makeModifierSeparator}"
        val altPrefix = "${ModifierKeyName.alt.name}${makeModifierSeparator}"

        val isCtrlShiftAltPrefix = str.startsWith(ctrlShiftAltPrefix)
        val isCtrlShiftPrefix = str.startsWith(ctrlShiftPrefix)
        val isCtrlAltPrefix = str.startsWith(ctrlAltPrefix)
        val isCtrlPrefix = str.startsWith(ctrlPrefix)
        val isShiftPrefix = str.startsWith(shiftPrefix)
        val isAltPrefix = str.startsWith(altPrefix)
        val isOneSepalator = str.split(makeModifierSeparator).size == 2

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
        val modifierNormalPair = modifierConbiStr.split(makeModifierSeparator)
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