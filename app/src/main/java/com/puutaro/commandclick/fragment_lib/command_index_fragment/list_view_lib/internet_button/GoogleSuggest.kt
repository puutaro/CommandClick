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
        connection.doInput = true;// レスポンスのボディの受信を許可

        connection.addRequestProperty(
            "Accept-Language",
            Locale.getDefault().toString()
        )

        cmdIndexFragment.suggestJob?.cancel()
        cmdIndexFragment.suggestJob = CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    delay(200)
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
                    cmdIndexFragment.binding.cmdSearchEditText.threshold = 0
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

    private fun expandJSONArray(array: JSONArray?) {
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
