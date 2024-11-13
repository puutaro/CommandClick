package com.puutaro.commandclick.util.url

import android.content.Context
import com.itextpdf.text.pdf.PdfFileSpecification.url
import com.puutaro.commandclick.util.Intent.CurlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


object SiteUrl {

    suspend fun getTitle(
        context: Context?,
        targetUrl: String,
    ): String {
//        val htmlString =
        val doc: Document = withContext(Dispatchers.IO) {
                Jsoup.connect(targetUrl).get()
//                CurlManager.get(
//                    context,
//                    targetUrl,
//                    "",
//                    "",
//                    2000
//                ).let {
//                    CurlManager.convertResToStrByConn(it)
//                }
            }
//        val doc = Jsoup.parse(htmlString)
        return doc.title()
    }
}