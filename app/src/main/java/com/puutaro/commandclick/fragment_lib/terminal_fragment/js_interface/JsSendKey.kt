package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Context
import android.os.SystemClock
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.util.DialogObject
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.utils.UlaFiles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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
            else -> normalOrModiferHandler(keyName)
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
                0,
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
        try  {
            val ansi2HtmlShellCon = AssetsFileManager.readFromAssets(
                context,
                AssetsFileManager.ansi2htmlShellPath
            )
            val tempDirPath =
                "${UsePath.cmdclickDefaultAppDirPath}/${UsePath.cmdclickTempSystemDirRelativePath}"
            val ansi2HtmlShellName =
                AssetsFileManager.ansi2htmlShellPath.split("/").lastOrNull()
                    ?: return
//            val tempAnsi2HtmlPath =
//                "${tempDirPath}/${ansi2HtmlShellName}"
            FileSystems.writeFile(
                tempDirPath,
                ansi2HtmlShellName,
                ansi2HtmlShellCon
            )
            val ulaFiles = UlaFiles(
                terminalFragment.context as Context,
                context?.applicationInfo?.nativeLibraryDir ?: String(),
                onInit = false
            )

//            val busyboxExecutor = BusyboxExecutor(
//                ulaFiles,
//            )
//           val prootAnsi2Txt = busyboxExecutor.executeProotCommand(
//                listOf("su", "-", "cmdclick", "-c", "bash ${tempAnsi2HtmlPath}"),
//                        outputType = TerminalOutputType.last
//            )
//            bash '${tempAnsi2HtmlPath}'
            val contents = ReadText(
//                UsePath.cmdclickDefaultAppDirPath,
//                "ansi2html.txt"
                "${ulaFiles.filesOneRootfs}/home/cmdclick",
                "script.log"
//                "ansi2html.txt"
            ).readText()
            CoroutineScope(Dispatchers.Main).launch {
                DialogObject.termStrCopyDialog(
                    terminalFragment,
                    contents,
//                    "${UsePath.cmdclickDefaultAppDirPath}/ansi2html.txt"
                )
            }
            Toast.makeText(
                context,
                "copy",
                Toast.LENGTH_SHORT
            ).show()
        } catch(e: Exception){
            Toast.makeText(
                context,
                e.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
        return
    }

    private fun paste(){
        keyDownWithMeta(
            KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON,
            KeyEvent.KEYCODE_V,
        )
    }

    private fun enter(){
        keyDownEvent(
            KeyEvent.KEYCODE_ENTER
        )
    }

    private fun normalOrModiferHandler(
        str: String,
    ){
        when(
            judgeModifer(str)
        ) {
            ModifierKeyName.ctrl_shift_alt
            -> makeCtrlShiftAltNormal(str)
            ModifierKeyName.ctrl_shift
            -> makeCtrlShiftNormal(str)
            ModifierKeyName.ctrl_alt
            -> makeCtrlAltNormal(str)
            ModifierKeyName.ctrl,
            ModifierKeyName.shift,
            ModifierKeyName.alt
            -> makeOneModifierKeyConbi(str)
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

    private fun makeOneModifierKeyConbi(
        modifierConbiStr: String,
    ){
        val modifierNormalPair = modifierConbiStr.split(makeModifierSepalator)
        val modifierKey = modifierNormalPair.firstOrNull()
            ?: return
        val normalKey = modifierNormalPair.lastOrNull()
            ?: return
        val keyEvent = mKeyCharacterMap.getEvents(normalKey.toCharArray()).first()
        when(modifierKey) {
            ModifierKeyName.ctrl.name -> keyDownWithMeta(
                KeyEvent.META_CTRL_ON,
                keyEvent.keyCode
            )
            ModifierKeyName.shift.name -> keyDownWithMeta(
                KeyEvent.META_SHIFT_ON,
                keyEvent.keyCode
            )
            ModifierKeyName.alt.name -> keyDownWithMeta(
                KeyEvent.META_ALT_ON,
                keyEvent.keyCode
            )
        }
    }

    private fun makeCtrlShiftAltNormal(modifierConbiStr: String){
        val modifierNormalPair = modifierConbiStr.split(makeModifierSepalator)
        val normalKey = modifierNormalPair.lastOrNull()
            ?: return
        val keyEvent = mKeyCharacterMap.getEvents(normalKey.toCharArray()).first()
        keyDownWithMeta(
            KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON  or KeyEvent.META_ALT_ON,
            keyEvent.keyCode,
        )
    }

    private fun makeCtrlShiftNormal(modifierConbiStr: String){
        val modifierNormalPair = modifierConbiStr.split(makeModifierSepalator)
        val normalKey = modifierNormalPair.lastOrNull()
            ?: return
        val keyEvent = mKeyCharacterMap.getEvents(normalKey.toCharArray()).first()
        keyDownWithMeta(
            KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON,
            keyEvent.keyCode,
        )
    }

    private fun makeCtrlAltNormal(modifierConbiStr: String){
        val modifierNormalPair = modifierConbiStr.split(makeModifierSepalator)
        val normalKey = modifierNormalPair.lastOrNull()
            ?: return
        val keyEvent = mKeyCharacterMap.getEvents(normalKey.toCharArray()).first()
        keyDownWithMeta(
            KeyEvent.META_CTRL_ON or KeyEvent.META_ALT_ON,
            keyEvent.keyCode,
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