package com.puutaro.commandclick.util.Intent

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object CurlManager {
    fun get(
        mainUrl: String,
        queryParameter: String = String(),
        header: String = String(),
        timeout: Int
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
        connection.connectTimeout = timeout
        //レスポンスデータ読み取りタイムアウトを設定する。
        connection.readTimeout = timeout
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
            errPrefix + "invalid url\n" + e.toString()
        }
    }
}