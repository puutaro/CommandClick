package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.state.EditFragmentArgs
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object TerminalOnHandlerForEdit {
    fun handle(
        terminalFragment: TerminalFragment
    ){

        val context = terminalFragment.context
            ?: return

        val indexTerminalTag = context.getString(R.string.index_terminal_fragment)
        if(
            terminalFragment.tag == indexTerminalTag
        ) {
            val listener = context as? TerminalFragment.OnTermShortSizeListenerForTerminalFragment
            listener?.onTermNormalSizeForTerminalFragment(terminalFragment)
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                for (i in 1..10) {
                    if (terminalFragment.isVisible) break
                    delay(100)
                }
            }
            val isSettingValEdit =
                withContext(Dispatchers.IO) {
                    terminalFragment.editType ==
                            EditFragmentArgs.Companion.EditTypeSettingsKey.SETTING_VAL_EDIT
                }
            if(isSettingValEdit) return@launch
            val disableShortcut = withContext(Dispatchers.IO) {
                SharePreferenceMethod.getReadSharePreffernceMap(
                    terminalFragment.readSharedPreferences,
                    SharePrefferenceSetting.on_shortcut
                ) != EditFragmentArgs.Companion.OnShortcutSettingKey.ON.key
            }
            if(disableShortcut) return@launch
            val disableTerminal = terminalFragment.terminalOn ==
                    SettingVariableSelects.TerminalDoSelects.OFF.name
            if (disableTerminal) return@launch

            withContext(Dispatchers.Main) {
                val listener = context as? TerminalFragment.OnTermShortSizeListenerForTerminalFragment
                listener?.onTermNormalSizeForTerminalFragment(terminalFragment)
            }
        }
    }
}