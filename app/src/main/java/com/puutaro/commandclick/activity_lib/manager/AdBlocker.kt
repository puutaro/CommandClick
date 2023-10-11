package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.SettingVariableSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*


object AdBlocker {
    private const val AD_HOSTS_FILE = "hosts.txt"

    fun init(terminalFragment: MainActivity) {
        val terminalViewModel: TerminalViewModel =
            ViewModelProvider(terminalFragment).get(TerminalViewModel::class.java)


//        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
//        val terminalViewModel: TerminalViewModel by terminalFragment.activityViewModels()
//        terminalFragment.loadAssetCoroutineJob?.cancel()
//        if(
//            terminalFragment.onAdBlock != SettingVariableSelects.OnAdblockSelects.ON.name
//        ) return
//        if(
//            terminalFragment.loadAssetCoroutineJob != null
//        ) return
        if(
            terminalViewModel.blocklist.size > 0
        ) return
//        terminalFragment.loadAssetCoroutineJob =
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                loadFromAssets(
                    terminalFragment,
                    terminalViewModel
                )
            }
        }
    }


    private fun loadFromAssets(
        terminalFragment: MainActivity,
        terminalViewModel: TerminalViewModel,
    ) {
        try {
            terminalViewModel.blocklist =
                AssetsFileManager.readFromAssets(
                    terminalFragment,
                    AD_HOSTS_FILE
                ).split("\n").toHashSet()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
