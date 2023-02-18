package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess

import android.content.Context
import android.text.TextUtils
import android.webkit.WebResourceResponse
import androidx.annotation.WorkerThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puutaro.commandclick.fragment.TerminalFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okio.BufferedSource
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream
import java.io.IOException


object AdBlocker {
    private const val AD_HOSTS_FILE = "hosts.txt"
    private val AD_HOSTS: MutableSet<String> = HashSet()

    fun init(terminalFragment: TerminalFragment) {
        val context = terminalFragment.context
        if(context == null) return
        terminalFragment.lifecycleScope.launch {
            terminalFragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.IO) {
                    loadFromAssets(context)
                }
            }
        }
    }

    @WorkerThread
    @Throws(IOException::class)
    private fun loadFromAssets(context: Context) {
        val stream = context.assets.open(AD_HOSTS_FILE)
        val buffer: BufferedSource = stream.source().buffer()
        var line: String
        val bufferRead = buffer.readUtf8Line()
        if(bufferRead == null) {
            buffer.close()
            stream.close()
            return
        }
        while (bufferRead.also { line = it } != null) {
            AD_HOSTS.add(line)
        }
        buffer.close()
        stream.close()
    }

    fun isAd(url: String?): Boolean {
        if(url.isNullOrEmpty()) return true
        val httpUrl = url.toHttpUrlOrNull()
        return isAdHost(httpUrl?.host ?: "")
    }

    private fun isAdHost(host: String): Boolean {
        if (TextUtils.isEmpty(host)) {
            return false
        }
        val index = host.indexOf(".")
        return index >= 0 && (AD_HOSTS.contains(host) ||
                index + 1 < host.length && isAdHost(host.substring(index + 1)))
    }


    fun createEmptyResource(): WebResourceResponse {
        return WebResourceResponse("text/plain", "utf-8", ByteArrayInputStream("".toByteArray()))
    }
}
