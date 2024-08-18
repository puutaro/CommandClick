package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button

import android.R
import android.content.Context
import android.os.Build
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.UrlTexter
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.NetworkTool
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import java.util.zip.GZIPInputStream


class GoogleSuggest(
    private val fragment: Fragment,
    private val cmdSearchEditText: AutoCompleteTextView
){
    private val context = fragment.context
    private val suggestEditTexter = SuggestEditTexter(
        fragment,
        cmdSearchEditText
    )
    private var mDispText = String()

    fun set (
        searchEditable: Editable?
    ) {
        if(
            context == null
        ) return
        suggestEditTexter.setItemClickListener()
        mDispText = String()
        if(searchEditable.isNullOrEmpty()) return
        if(
            !NetworkTool.isOnline(context)
        ) return
        val localLanguage = Locale.getDefault().toString()
        val searchWord = searchEditable.toString()
            .trim()
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
        connection.doInput = true // レスポンスのボディの受信を許可

        connection.addRequestProperty(
            "Accept-Language",
            Locale.getDefault().toString()
        )

        when(fragment) {
            is CommandIndexFragment -> {
                fragment.suggestJob?.cancel()
                    fragment.suggestJob = launchSuggestCoroutine(connection)
            }
            is EditFragment -> {
                fragment.suggestJob?.cancel()
                fragment.suggestJob = launchSuggestCoroutine(connection)
            }
        }

    }

    private fun launchSuggestCoroutine(
        connection: HttpURLConnection
    ): Job? {
        return try {
            execLaunchSuggestCoroutine(connection)
        } catch (e: Exception){
            null
        }
    }

    private fun execLaunchSuggestCoroutine(
        connection:  HttpURLConnection
    ):Job {
        return CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                delay(200)
                try {
                    connection.connect()
                } catch (e: Exception) {
                    connection.disconnect()
                }
            }
            withContext(Dispatchers.IO){
                try {
                    getSuggestText(connection)
                } catch (e: Exception){
                    return@withContext
                }
            }
            withContext(Dispatchers.Main){
                if(
                    mDispText.isEmpty()
                ) return@withContext
                cmdSearchEditText.threshold = 0
                suggestEditTexter.setAdapter(
                    context,
                    cmdSearchEditText,
                    mDispText.split("\n").filter {
                        it.isNotEmpty()
                    }
                )
            }
        }
    }

    private fun getSuggestText(
        connection: HttpURLConnection
    ) {
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


private class SuggestEditTexter(
    private val fragment: Fragment,
    private val cmdSearchEditText: AutoCompleteTextView
) {

    private val tabReplaceStr = "\t"
    private val queryLimitStrLength = 50


    fun setAdapter(
        context: Context?,
        cmdSearchEditText: AutoCompleteTextView,
        suggestList: List<String>
    ) {
        if(context == null) return
        cmdSearchEditText.setAdapter(
            makeUrlComAdapter(
                context,
                suggestList,
            )
        )
        cmdSearchEditText.threshold = 0
    }

    private fun makeUrlComAdapter(
        context: Context,
        suggestList: List<String>
    ): ArrayAdapter<String> {
        return ArrayAdapter(
            context,
            R.layout.simple_list_item_1,
            suggestList
        )
    }


    fun setItemClickListener(){
        cmdSearchEditText.setOnItemClickListener { parent, _, position, _ ->
            val selectedUrlSource = parent.getItemAtPosition(position) as String
            val selectedUrl = selectedUrlSource.split(tabReplaceStr).lastOrNull()
            val queryUrl = WebUrlVariables.queryUrl

            if (
                selectedUrl?.startsWith(queryUrl) != true
            ) {
                execUrlLaunch(selectedUrl)
                return@setOnItemClickListener
            }
            val decodedSelectedUrl =
                URLDecoder.decode(
                    selectedUrl.removePrefix(queryUrl),
                    "utf-8"
                )
            if(
                decodedSelectedUrl.length < queryLimitStrLength
            ) {
                cmdSearchEditText.setText(
                    decodedSelectedUrl
                )
                return@setOnItemClickListener
            }

            cmdSearchEditText.clearFocus()
            Keyboard.hiddenKeyboardForFragment(
                fragment
            )
            execUrlLaunch(selectedUrl)
        }
    }

    private fun execUrlLaunch(
        selectedUrl: String?
    ){
        cmdSearchEditText.clearFocus()
        Keyboard.hiddenKeyboardForFragment(
            fragment
        )
        UrlTexter.launch(
            fragment,
            cmdSearchEditText,
            selectedUrl
        )
    }
}
