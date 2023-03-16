package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class JsCurl(
    terminalFragment: TerminalFragment
) {

    @JavascriptInterface
    fun get(
        mainUrl: String,
        queryParameter: String = String(),
        header: String = String(),
    ): String {
        val errPrefix = "err: "
        if(
            mainUrl.isEmpty()
        ) {
            return errPrefix + "invalid url"
        }
        val urlString = if(queryParameter.isEmpty()) {
            mainUrl
        } else {
            "${mainUrl}?${queryParameter}"
        }

        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 100000
        //レスポンスデータ読み取りタイムアウトを設定する。
        connection.readTimeout = 100000
        connection.requestMethod = "GET"
        connection.useCaches = false;// キャッシュ利用
        connection.doOutput = false;// リクエストのボディの送信を許可(GETのときはfalse,POSTのときはtrueにする)
        connection.doInput = true;// レスポンスのボディの受信を許可
        val headerList = header.split(',')
        headerList.forEach {
            val headerRow = it.split("\t")
            if (headerRow.size < 2) return@forEach
            connection.addRequestProperty(headerRow.first(), headerRow[1])
        }
        connection.addRequestProperty("User-Agent", "Android")
        connection.addRequestProperty(
            "Accept-Language",
            Locale.getDefault().toString()
        )

        return try {
            connection.connect()
            val statusCode = connection.responseCode
            if (statusCode != HttpURLConnection.HTTP_OK) {
                connection.disconnect()
                return errPrefix + statusCode.toString()
            }
            val bufferedReader =
                BufferedReader(InputStreamReader(connection.inputStream))
            val responseBody = bufferedReader.use { it.readText() }
            bufferedReader.close()
            connection.disconnect()
            responseBody
        } catch (e: Exception) {
            connection.disconnect()
            errPrefix + "invalid url"
        }
    }
}