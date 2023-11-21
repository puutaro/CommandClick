package com.puutaro.commandclick.activity_lib.manager

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.util.AssetsFileManager
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*


object AdBlocker {
    private const val AD_HOSTS_FILE = "hosts.txt"

    fun init(activity: MainActivity): Job? {
        val terminalViewModel: TerminalViewModel =
            ViewModelProvider(activity).get(TerminalViewModel::class.java)

        if(
            terminalViewModel.blockListCon.isNotEmpty()
        ) return null
        return activity.lifecycleScope.launch(Dispatchers.IO) {
            delay(3000)
            withContext(Dispatchers.IO) {
                if(
                    terminalViewModel.blockListCon.isNotEmpty()
                ) return@withContext
                loadFromAssets(
                    activity,
                    terminalViewModel
                )
            }
        }
    }

    fun judgeBlock(
        urlHost: String?,
        blockListCon: String,
    ): Boolean {
        if(urlHost.isNullOrEmpty()) return false
        val hostPrefix = ":::::"
        return blockListCon.contains("${hostPrefix}${urlHost}")
    }


    private fun loadFromAssets(
        terminalFragment: MainActivity,
        terminalViewModel: TerminalViewModel,
    ) {
        try {
            terminalViewModel.blockListCon =
                AssetsFileManager.readFromAssets(
                    terminalFragment,
                    AD_HOSTS_FILE
                )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
