package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.fragment.TerminalFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*


object AdBlocker {
    private const val AD_HOSTS_FILE = "hosts.txt"

    fun init(terminalFragment: TerminalFragment) {
        terminalFragment.loadAssetCoroutineJob = terminalFragment.lifecycleScope.launch {
            terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.IO) {
                    loadFromAssets(terminalFragment)
                }
            }
        }
    }


    fun loadFromAssets(
        terminalFragment: TerminalFragment
    ) {
        val fis2: InputStream =
            terminalFragment.context?.assets?.open(
                AD_HOSTS_FILE
            ) ?: return
        try {
            terminalFragment.blocklist =
                fis2.bufferedReader().use {
                    it.readText()
                }.split("\n").toHashSet()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
