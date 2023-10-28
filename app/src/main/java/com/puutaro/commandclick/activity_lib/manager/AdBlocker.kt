package com.puutaro.commandclick.activity_lib.manager

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*


object AdBlocker {
    private const val AD_HOSTS_FILE = "hosts.txt"

    fun init(terminalFragment: MainActivity): Job? {
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
        ) return null
//        terminalFragment.loadAssetCoroutineJob =
        return terminalFragment.lifecycleScope.launch(Dispatchers.IO) {
            delay(3000)
            withContext(Dispatchers.IO) {
                if(
                    terminalViewModel.blocklist.size > 0
                ) return@withContext
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
