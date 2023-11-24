package com.puutaro.commandclick.util.Intent

import com.puutaro.commandclick.util.LogSystems
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
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
        connection.useCaches = false // キャッシュ利用
        connection.doOutput = false // リクエストのボディの送信を許可(GETのときはfalse,POSTのときはtrueにする)
        connection.doInput = true // レスポンスのボディの受信を許可
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
            LogSystems.stdErr(e.toString())
            String()
        } finally {
            connection.disconnect()
        }
    }



    fun post(
        mainUrl: String,
        header: String = String(),
        bodyStr: String,
        timeout: Int
    ): ByteArray {
        // Bodyのデータ（サンプル）
        val sendDataJson = bodyStr
//            "{\"id\":\"1234567890\",\"name\":\"hogehoge\"}"
        val bodyData = sendDataJson.toByteArray(Charsets.UTF_8)



        // HttpURLConnectionの作成
        LogSystems.stdSys(bodyStr)
        val url = URL(mainUrl)
        val connection = url.openConnection() as HttpURLConnection
        try {
            // ミリ秒単位でタイムアウトを設定
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
//            connection.requestMethod = "POST"
            // Bodyへ書き込むを行う
            connection.doOutput = true

            // リクエストBodyのストリーミング有効化（どちらか片方を有効化）
            connection.setFixedLengthStreamingMode(bodyData.size)
//            connection.setChunkedStreamingMode(0)
            // プロパティの設定
//            connection.setRequestProperty("Content-type", "text/plain")
//            connection.setRequestProperty("Connection", "close");
            val headerList = header.split(',')
            headerList.forEach {
                val headerRow = it.trim().split("\t")
                if (headerRow.size < 2) return@forEach
                connection.addRequestProperty(headerRow.first(), headerRow[1])
            }

            connection.connect()
            // Bodyの書き込み
            val outputStream = connection.outputStream
            outputStream.write(bodyData)
            outputStream.flush()
            outputStream.close()

            // Responseの読み出し
            val statusCode = connection.responseCode
            if (statusCode == HttpURLConnection.HTTP_OK) {
                val resOutputStream = ByteArrayOutputStream()
                connection.inputStream.use { input ->
                    resOutputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                return resOutputStream.toByteArray()
            }
        } catch (e: Exception) {
            LogSystems.stdErr(e.toString())
        } finally {
            connection.disconnect()
        }
        return byteArrayOf()
    }
}