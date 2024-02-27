package com.puutaro.commandclick.util.url

import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

object SiteUrl {

    suspend fun getTitle(
        targetUrl: String,
    ): String {
        val htmlString =
            withContext(Dispatchers.IO) {
                CurlManager.get(
                    targetUrl,
                    "",
                    "",
                    2000
                ).let {
                    CurlManager.convertResToStrByConn(it)
                }
            }
        val doc = Jsoup.parse(htmlString)
        return doc.title()
    }
}