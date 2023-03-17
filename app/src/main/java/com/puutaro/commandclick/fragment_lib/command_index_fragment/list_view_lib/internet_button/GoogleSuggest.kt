package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button

import android.os.Build
import android.text.Editable
import android.widget.Toast
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.*
import java.util.zip.GZIPInputStream


class GoogleSuggest(
    private val cmdIndexFragment: CommandIndexFragment,
){
    private val context = cmdIndexFragment.context
    private val suggestEditTexter = SuggestEditTexter(cmdIndexFragment)
    private var mDispText = String()

    fun set (
        searchEditable: Editable?
    ) {
        mDispText = String()
        if(searchEditable.isNullOrEmpty()) return
        val localLanguage = Locale.getDefault().toString()
        val searchWord = searchEditable.toString()
            .replace(Regex("　　*"), " ")
            .replace("　", " ")
        if(
            searchWord.startsWith(WebUrlVariables.httpsPrefix)
            || searchWord.startsWith(WebUrlVariables.httpPrefix)
            || searchWord.startsWith(WebUrlVariables.filePrefix)
            || searchWord.startsWith(WebUrlVariables.slashPrefix)
        ) return
        val searchWordEncoded = URLEncoder.encode(searchWord, "UTF-8")
        val urlString = "https://api.bing.com/qsonhs.aspx?mkt=${localLanguage}&q=${searchWordEncoded}"
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 100000
        //レスポンスデータ読み取りタイムアウトを設定する。
        connection.readTimeout = 100000
        connection.setRequestProperty("User-Agent", "Android " + Build.MODEL)
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate")
        connection.requestMethod = "GET"
//        connection.useCaches = false;// キャッシュ利用
//        connection.doOutput = false;// リクエストのボディの送信を許可(GETのときはfalse,POSTのときはtrueにする)
        connection.doInput = true;// レスポンスのボディの受信を許可

        connection.addRequestProperty(
            "Accept-Language",
            Locale.getDefault().toString()
        )

        cmdIndexFragment.suggestJob?.cancel()
        cmdIndexFragment.suggestJob = CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    try {
                        connection.connect()
                    } catch (e: Exception) {
                        connection.disconnect()
                    }
                }
                withContext(Dispatchers.IO){
                    val statusCode = connection.responseCode
                    if (statusCode != HttpURLConnection.HTTP_OK) {
                        connection.disconnect()
                    }
                    val contentEncoding: String = connection.contentEncoding
                    val inputStream = if (contentEncoding.contains("gzip")) {
                        GZIPInputStream(connection.getInputStream())
                    } else {
                        connection.inputStream
                    }
                    val bufferedReader =
                        BufferedReader(InputStreamReader(inputStream))
                    val responseBody = bufferedReader.use { it.readText() }
                    bufferedReader.close()
                    connection.disconnect()
                    val rootObject = JSONObject(responseBody)
                    var results: JSONArray? = null
                    val `as`: JSONObject? = rootObject.optJSONObject("AS")
                    if (`as` != null) {
                        results = `as`.optJSONArray("Results")
                    }
                    if (results != null) {
                        expandJSONArray(
                            results
                                .optJSONObject(0)
                                .optJSONArray("Suggests"))
                    } else String()
                }
                withContext(Dispatchers.Main){
                    suggestEditTexter.setAdapter(
                        context,
                        cmdIndexFragment.binding.cmdSearchEditText,
                        mDispText.split("\n").filter {
                            it.isNotEmpty()
                        }
                    )
                }
            }
    }

    fun expandJSONArray(array: JSONArray?) {
        if (array == null) {
            return
        }
        var i: Int
        val n = array.length()
        i = 0
        while (i < n) {
            val `object` = array.optJSONObject(i) ?: break
            mDispText += """
                ${`object`.optString("Txt")}
                
                """.trimIndent()
            i++
        }
    }
}

//
//// サジェスト通信処理を行うクラス
//class MyAsyncTask() {     //ここに結果を表示する
//    {
//        private var mResponse: String? = ""
//        private var mDispText = ""
//
//        // バックグラウンド処理 (UI操作禁止)
//        // return code: 0:正常,1:キャンセル,2:エラー
//        protected override fun doInBackground(vararg params: String): Long {
//            Log.d("MyAsyncTask", "doInBackground " + params[0])
//
//            // finallyで後始末するものはここで宣言する
//            var connection: HttpsURLConnection? = null
//            val headers: Map<String, List<String>>? = null
//            var inputStream: InputStream? = null
//            var reader: BufferedReader? = null
//            try {
//                // 入力文字列を Bing API に渡してサジェスト候補検索
//                val uri = "https://api.bing.com/qsonhs.aspx?mkt=ja-JP&q=" +
//                        URLEncoder.encode(params[0], "UTF-8")
//                Log.d("MyAsyncTask", "URI=$uri")
//                if (isCancelled) {
//                    Log.d("MyAsyncTask", "cancel return 1")
//                    return 1L
//                }
//                val url = URL(uri)
//                connection = url.openConnection() as HttpsURLConnection
//                if (isCancelled) {
//                    Log.d("MyAsyncTask", "cancel return 2")
//                    return 1L
//                }
//                connection.setRequestProperty("User-Agent", "Android " + Build.MODEL)
//                connection.setRequestProperty("Accept-Encoding", "gzip, deflate")
//                connection.setConnectTimeout(30000) // Timeout 30秒
//                connection.setReadTimeout(30000) // Timeout 30秒
//                connection.setDoInput(true)
//                connection.setRequestMethod("GET")
//                if (isCancelled) {
//                    Log.d("MyAsyncTask", "cancel return 3")
//                    return 1L
//                }
//                connection.connect() // 接続・検索
//                if (isCancelled) {
//                    Log.d("MyAsyncTask", "cancel return 4")
//                    return 1L
//                }
//                val responseCode: Int = connection.getResponseCode() // HTTPステータス取得
//                Log.d("MyAsyncTask", "got response $responseCode")
//                if (responseCode != HttpsURLConnection.HTTP_OK) { // 200 OK以外はエラー
//                    throw IOException("HTTP responseCode: $responseCode")
//                }
//                if (isCancelled) {
//                    Log.d("MyAsyncTask", "cancel return 5")
//                    return 1L
//                }
//
//                //headers = connection.getHeaderFields();
//
//                // サジェスト候補JSONデータストリームOpen gzip圧縮に対応
//                val contentEncoding: String = connection.getContentEncoding()
//                if (contentEncoding != null && contentEncoding.contains("gzip")) {
//                    inputStream = GZIPInputStream(connection.getInputStream())
//                } else {
//                    inputStream = connection.getInputStream()
//                }
//                if (isCancelled) {
//                    Log.d("MyAsyncTask", "cancel return 6")
//                    return 1L
//                }
//
//                // データ読み込み
//                val sb = StringBuilder()
//                reader = BufferedReader(
//                    InputStreamReader(
//                        inputStream,
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) StandardCharsets.UTF_8 else Charset.forName(
//                            "UTF-8"
//                        )
//                    )
//                )
//                var line: String?
//                while (reader.readLine().also { line = it } != null) {
//                    if (isCancelled) {
//                        Log.d("MyAsyncTask", "cancel return 7")
//                        return 1L
//                    }
//                    sb.append(line)
//                }
//                mResponse = sb.toString() // 結果JSONの生データ
//                if (mResponse == null || mResponse!!.length <= 0) {
//                    return 3L
//                }
//                Log.d("MyAsyncTask", "finished stream read")
//
//                // JSON解析開始
//                val rootObject = JSONObject(mResponse)
//                var `as`: JSONObject? = null
//                var results: JSONArray? = null
//                if (isCancelled) {
//                    Log.d("MyAsyncTask", "cancel return 8")
//                    return 1L
//                }
//                if (rootObject != null) {
//                    `as` = rootObject.optJSONObject("AS")
//                    if (`as` != null) {
//                        results = `as`.optJSONArray("Results")
//                    }
//                }
//                if (isCancelled) {
//                    Log.d("MyAsyncTask", "cancel return 9")
//                    return 1L
//                }
//                if (results != null) { // Suggests要素配列を分解して出力
//                    expandJSONArray(results.optJSONObject(0).optJSONArray("Suggests"))
//                    if (isCancelled) {
//                        Log.d("MyAsyncTask", "cancel return 10")
//                        return 1L
//                    }
//                    Log.d("MyAsyncTask", "finished expand JSON")
//                }
//            } catch (e: Exception) {
//                Log.e("AsyncTask", e.toString())
//                return 2L // エラー終了
//            } finally { // 後片付け
//                if (reader != null) {
//                    try {
//                        reader.close()
//                    } catch (e: IOException) {
//                        Log.e("AsyncTask", e.toString())
//                    }
//                }
//                if (inputStream != null) {
//                    try {
//                        inputStream.close()
//                    } catch (e: IOException) {
//                        Log.e("AsyncTask", e.toString())
//                    }
//                }
//                if (connection != null) {
//                    // A connection to https://api.bing.com/ was leaked. Did you forget to close a response body?
//                    // と言われるのを防ぐクローズ処理
//                    if (connection.getErrorStream() != null) {
//                        try {
//                            connection.getErrorStream().close()
//                        } catch (e: IOException) {
//                            Log.e("AsyncTask", e.toString())
//                        }
//                    }
//                    connection.disconnect()
//                }
//            }
//            return 0L // 正常終了（検索候補無しも含む）
//        }
//
//        // doInBackgroundでpublishProgressを呼ぶと呼ばれる
//        protected override fun onProgressUpdate(vararg values: Int) {}
//
//        // doInBackground完了後に呼ばれる（UI操作可能)
//        override fun onPostExecute(result: Long) {
//            Log.d("MyAsyncTask", "onPostExecute result=$result")
//            if (result == 0L) { //正常終了なら、サジェスト結果文字列を表示する
//                mTextView.text = mDispText
//                Log.d("MyAsyncTask", "finished display text")
//            }
//        }
//
//        // doInBackground中にcancelされたら呼ばれる（UI操作可能)
//        override fun onCancelled() {
//            Log.d("MyAsyncTask", "onCancelled")
//        }
//
//        // BingサジェストAPIの結果配列を文字列に分解する
//        // https://api.bing.com/qsonhs.aspx?mkt=ja-JP&q=Amazon
//        fun expandJSONArray(array: JSONArray?) {
//            if (array == null) {
//                return
//            }
//            var i: Int
//            val n = array.length()
//            i = 0
//            while (i < n) {
//                //Txt要素が候補文字列なのでDispTextへ追加していく
//                if (isCancelled) {
//                    Log.d("MyAsyncTask", "cancel return 11")
//                    return
//                }
//                val `object` = array.optJSONObject(i) ?: break
//                mDispText += """
//                ${`object`.optString("Txt")}
//
//                """.trimIndent()
//                i++
//            }
//        }
//    }
//}