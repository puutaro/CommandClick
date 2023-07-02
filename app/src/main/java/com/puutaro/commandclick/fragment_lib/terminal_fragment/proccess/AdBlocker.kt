package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*


object AdBlocker {
    private const val AD_HOSTS_FILE = "hosts.txt"

    fun init(terminalFragment: TerminalFragment) {
        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
        terminalFragment.loadAssetCoroutineJob?.cancel()
        if(
            terminalFragment.onAdBlock != SettingVariableSelects.Companion.OnAdblockSelects.ON.name
        ) return
        if(
            terminalFragment.loadAssetCoroutineJob != null
        ) return
        if(
            terminalViewModel.blocklist.size > 0
        ) return
        terminalFragment.loadAssetCoroutineJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                loadFromAssets(
                    terminalFragment,
                    terminalViewModel
                )
            }
        }
    }


    private fun loadFromAssets(
        terminalFragment: TerminalFragment,
        terminalViewModel: TerminalViewModel,
    ) {
        try {
            terminalViewModel.blocklist =
                FileSystems.readFromAssets(
                    terminalFragment.context,
                    AD_HOSTS_FILE
                ).split("\n").toHashSet()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
