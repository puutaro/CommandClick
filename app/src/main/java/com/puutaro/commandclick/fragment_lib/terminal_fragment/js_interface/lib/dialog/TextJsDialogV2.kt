package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class TextJsDialogV2(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var textDialogObj: Dialog? = null
    private var onDialog = false

    companion object {
        private var titleShowJob: Job? = null
        private val titleOridnalyMaxLines = 1

        private const val sectionSeparator = ','
        private const val keySeparator = '|'
        const val valueSeparator = '?'
        const val switchOn = "ON"
        const val switchOff = "OFF"

        enum class TextDialogSection(
            val section: String
        ){
            BODY("body")
        }

        enum class BodyKey(
            val key: String
        ){
            ON_FORMAT("onFormat")
        }
    }

    fun create(
        fannelPath: String,
        title: String,
        body: String,
        configMapConSrc: String,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context
        val fannelFile = File(fannelPath)
        if(
            !fannelFile.isFile
        ) {
            LogSystems.stdSErr("invalid fannel path: ${fannelFile.absolutePath}")
            return String()
        }
        val setReplaceVariablesMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            terminalFragment.context,
            fannelPath
        )
        val configMapCon = SetReplaceVariabler.execReplaceByReplaceVariables(
            configMapConSrc,
            setReplaceVariablesMap,
            fannelFile.name
        )
        onDialog = true
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        title,
                        body,
                        configMapCon,
                    )
                } catch (e: Exception){
                    Log.e(this.javaClass.name, e.toString())
                }
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (
                        !onDialog
                    ) break
                }
            }
        }
        return body
    }

    private fun execCreate(
        title: String,
        body: String,
        configMapCon: String,
    ) {
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        if(
            context == null
        ) {
            dismissProcess()
            return
        }
        if(
            body.isEmpty()
        ) {
            dismissProcess()
            return
        }
        val configMap = CmdClickMap.createMap(
            configMapCon,
            sectionSeparator
        ).toMap()
        textDialogObj = Dialog(
            context,
            R.style.FullScreenRoundCornerDialogTheme
        )
        textDialogObj?.setContentView(
            R.layout.text_dialog_v2_layout
        )
        val titleTextView =
            textDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_dialog_v2_title
            )?.apply {
                val titleTextViewSrc = this@apply
                setOnClickListener {
                    titleShowJob?.cancel()
                    titleShowJob = CoroutineScope(Dispatchers.Main).launch {
                        titleTextViewSrc.maxLines = Integer.MAX_VALUE
                    }
                }
            }
        when(title.isEmpty()){
            true -> titleTextView?.isVisible = false
            else -> titleTextView?.text = title
        }
        val bodyMap = configMap.get(TextDialogSection.BODY.section)?.let {
            CmdClickMap.createMap(
                it,
                keySeparator
            ).toMap()
        }
        val bodyTextView = BodyTextViewMaker.make(
            textDialogObj,
            body,
            bodyMap,
            titleTextView,
        )

        textDialogObj?.setOnCancelListener {
            dismissProcess()
        }
        textDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        textDialogObj?.show()
    }

    private fun dismissProcess(){
        onDialog = false
        textDialogObj?.dismiss()
        textDialogObj = null
    }

    object BodyTextViewMaker {

        val separatorList = listOf(
            ". ",
            ".　",
            "。",
        )


        fun make(
            textDialogObj: Dialog?,
            bodySrc: String,
            titleMap: Map<String, String>?,
            titleTextView: AppCompatTextView?,
        ): AppCompatTextView? {
            val onFormat = titleMap?.get(
                BodyKey.ON_FORMAT.key
            ) != switchOff
            val bodySpannable = when(onFormat) {
                false -> SpannableString(bodySrc)
                else -> bodySrc.replace(
                    Regex("\n[ 　]*"),
                    "\n",
                ).replace(
                    Regex("[\n]+"),
                    "\n",
                ).split("\n").map { line ->
                    var repLine = line
                    separatorList.forEach {
                        repLine = repLine.replace(
                            it,
                            "${it}\n",
                        )
                    }
                    val splitSentenceNum = 3
                    val spannableList =
                        repLine.trim('\n').split("\n").chunked(splitSentenceNum)
                            .map chunk@{ sentenceList ->
                                val sentence = sentenceList.filter {
                                    it.trim().isNotEmpty()
                                }.joinToString(String()).trim('\n') + "\n\n"
                                val ss1 = SpannableString(sentence)
                                ss1.setSpan(
                                    RelativeSizeSpan(2f),
                                    0,
                                    1,
                                    0
                                )
                                ss1
                            }
                    TextUtils.concat(*spannableList.toTypedArray())
                }.let {
                    TextUtils.concat(*it.toTypedArray())
                }
            }
            return textDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_dialog_v2_text_view
            )?.apply {
                letterSpacing = 0.1f

                text = bodySpannable
                viewTreeObserver?.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        viewTreeObserver?.removeOnPreDrawListener(this)
                        return true
                    }
                })
                setOnTouchListener { v, _ ->
                    if(
                        titleTextView?.maxLines == titleOridnalyMaxLines
                        ) return@setOnTouchListener false
                    v.performClick()
                    titleShowJob?.cancel()
                    titleTextView?.maxLines = titleOridnalyMaxLines
                    true
                }
            }
        }
    }
}