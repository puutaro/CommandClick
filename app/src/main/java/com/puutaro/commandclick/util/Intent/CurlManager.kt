package com.puutaro.commandclick.util.Intent

import android.content.Context
import com.puutaro.commandclick.util.LogSystems
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.Locale


object CurlManager {

    val invalidResponse = "cmdclickConnectionError"

    fun isConnOk(
        resByteArray: ByteArray
    ): Boolean {
        return !resByteArray.contentEquals(invalidResponse.toByteArray())
    }

    fun convertResToStrByConn(
        resByteArray: ByteArray
    ): String {
        if (
            !isConnOk(resByteArray)
        ) return String()
        return String(resByteArray)
    }
    fun get(
        context: Context?,
        mainUrl: String,
        queryParameter: String = String(),
        header: String = String(),
        timeout: Int
    ): ByteArray {
        if(
            mainUrl.isEmpty()
        ) return byteArrayOf()
        val urlString = if(queryParameter.isEmpty()) {
            mainUrl
        } else {
            "${mainUrl}?${queryParameter}"
        }

        val connection =
            setConnectionForGet(
                context,
                urlString,
                header,
                timeout
            ) ?: return invalidResponse.toByteArray()


        try {
            connection.connect()
            return responseHandler(
                connection,
            )
        } catch (e: Exception) {
            stdErrByLowLevelSysNoti(
                context,
                e
            )
        } finally {
            connection.disconnect()
        }
        return invalidResponse.toByteArray()
    }



    fun post(
        context: Context?,
        mainUrl: String,
        header: String = String(),
        bodyStr: String,
        timeout: Int
    ): ByteArray {
        val bodyData = bodyStr.toByteArray(Charsets.UTF_8)

        // HttpURLConnectionの作成
        val connection = setConnectionForPost(
            context,
            mainUrl,
            header,
            bodyData,
            timeout
        ) ?: return invalidResponse.toByteArray()
        try {
            connection.connect()
            // Bodyの書き込み
            val outputStream = connection.outputStream
            outputStream.write(bodyData)
            outputStream.flush()
            outputStream.close()

            return responseHandler(
                connection,
            )
        } catch (e: Exception) {
            if(e !is SocketTimeoutException) {
                LogSystems.stdErrByLowLevelSysNoti(
                    context,
                    e.toString()
                )
            }
        } finally {
            connection.disconnect()
        }
        return invalidResponse.toByteArray()
    }

    private fun setConnectionForGet(
        context: Context?,
        urlString: String,
        header: String = String(),
        timeout: Int
    ): HttpURLConnection? {
        try {
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
            connection.setRequestProperty("Connection", "close")
            connection.addRequestProperty(
                "Accept-Language",
                Locale.getDefault().toString()
            )
            return connection
        } catch (e: Exception){
            stdErrByLowLevelSysNoti(
                context,
                e
            )
            return null
        }
    }

    private fun setConnectionForPost(
        context: Context?,
        mainUrl: String,
        header: String = String(),
        bodyData: ByteArray,
        timeout: Int
    ): HttpURLConnection? {
        try {
            val url = URL(mainUrl)
            val connection = url.openConnection() as HttpURLConnection
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
            connection.addRequestProperty("User-Agent", "Android")
            connection.setRequestProperty("Connection", "close")
            connection.addRequestProperty(
                "Accept-Language",
                Locale.getDefault().toString()
            )
            val headerList = header.split(',')
            headerList.forEach {
                val headerRow = it.trim().split("\t")
                if (headerRow.size < 2) return@forEach
                connection.addRequestProperty(headerRow.first(), headerRow[1])
            }
            return connection
        } catch (e: Exception){
            LogSystems.stdErrByLowLevelSysNoti(
                context,
                e.toString()
            )
            return null
        }
    }

    private fun responseHandler(
        connection: HttpURLConnection,
    ): ByteArray {
        val statusCode = connection.responseCode
        return when {
            statusCode == HttpURLConnection.HTTP_OK
            -> inputStreamToByteArray(
                connection,
            )
            else -> {
                connection.disconnect()
                LogSystems.stdErrByNoBroad(statusCode.toString())
                invalidResponse.toByteArray()
            }
        }
    }

    private fun inputStreamToByteArray(
        connection: HttpURLConnection,
    ): ByteArray {
        val resOutputStream = ByteArrayOutputStream()
        connection.inputStream.use { input ->
            resOutputStream.use { output ->
                input.copyTo(output)
            }
        }
        connection.disconnect()
        return resOutputStream.toByteArray()
    }

    private fun stdErrByLowLevelSysNoti(
        context: Context?,
        e: Exception
    ){
        if(
            e is SocketTimeoutException
            || e is EOFException
        ) return
        LogSystems.stdErrByLowLevelSysNoti(
            context,
            e.toString()
        )
    }
}