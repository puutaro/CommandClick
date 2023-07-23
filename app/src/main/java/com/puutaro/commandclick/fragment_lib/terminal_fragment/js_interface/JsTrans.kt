package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.bush.translator.Language
import me.bush.translator.Translation
import me.bush.translator.Translator

class JsTrans(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val languageMap = mapOf<String,Language>(
        "ja" to Language.JAPANESE,
        "en" to Language.ENGLISH,
        "ch" to Language.CHINESE_TRADITIONAL,
        "sp" to Language.SPANISH
    )

    @JavascriptInterface
    fun get(
        text: String,
        langStr: String
    ): String {
        val language = languageMap.get(langStr) ?: Language.ENGLISH
        val translator = Translator()
        val chunkedList = text.chunked(1000)
        val chunkedListLength = chunkedList.size
        return chunkedList.indices.map {
            val currentOrder = it + 1
            val chunkedText = chunkedList[it]
            execGet(
                chunkedText,
                translator,
                language,
                currentOrder,
                chunkedListLength
            )
        }.joinToString("")
    }

    private fun execGet(
        chunkedText: String,
        translator: Translator,
        language: Language,
        currentOrder: Int,
        chunkedListLength: Int,
    ): String {
        var chunkTranslation = String()
        var transFinish = false
        var transResult: Result<Translation>? = null
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main){
                if(
                    currentOrder % 3 != 1
                ) return@withContext
                Toast.makeText(
                    context,
                    "[$currentOrder/$chunkedListLength] trans..",
                    Toast.LENGTH_SHORT
                ).show()
            }
            transResult = withContext(Dispatchers.IO){
                translator.translateCatching(
                    chunkedText,
                    language,
                    Language.AUTO
                )
            }
            chunkTranslation =
                transResult
                    ?.getOrNull()
                    ?.translatedText
                    ?.replace("\\t", "\t")
                    ?: String()
            transFinish = true

        }
        runBlocking {
            withContext(Dispatchers.Main){
                for(i in 1..50){
                    if(
                        transFinish
                    ) break
                    delay(100)
                }
            }
        }
        if(
            transResult?.isFailure == true
        ) transFinish = true
        return chunkTranslation
    }
}