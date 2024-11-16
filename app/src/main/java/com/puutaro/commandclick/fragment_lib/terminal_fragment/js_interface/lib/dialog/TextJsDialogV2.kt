package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.html.TxtHtmlDescriber
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference
import java.time.LocalDateTime

class TextJsDialogV2(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var textDialogObj: Dialog? = null
    private var onDialog = false

    companion object {
        private var titleShowJob: Job? = null
        private const val titleOridnalyMaxLines = 1

        private const val sectionSeparator = ','
        private const val keySeparator = '|'
        const val valueSeparator = '?'
        const val switchOn = "ON"
        const val switchOff = "OFF"

        enum class TextDialogSection(
            val section: String
        ){
            BODY("body"),
            Y_SCROLL("yScroll"),
        }

        enum class YScroll(
            val key: String
        ){
            SAVE_TAG("saveTag")
        }

        enum class BodyKey(
            val key: String
        ){
            ON_FORMAT("onFormat"),
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
                        fannelPath,
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
        fannelPath: String,
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
        val scrollView = textDialogObj?.findViewById<ScrollView>(
            R.id.text_dialog_v2_scroll
        ) ?: return
        val titleTextView =
            textDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_dialog_v2_title
            )?.apply {
                val titleTextViewSrc = this@apply
//                setOnClickListener {
//                    titleShowJob?.cancel()
//                    titleShowJob = CoroutineScope(Dispatchers.Main).launch {
//                        titleTextViewSrc.maxLines = Integer.MAX_VALUE
//                    }
//                }
            } ?: return
        val titleTextViewShadow =
            textDialogObj?.findViewById<View>(
                R.id.text_dialog_v2_title_shadow
            ) ?: return
        when(title.isEmpty()){
            true -> {
                titleTextView.isVisible = false
                titleTextViewShadow.isVisible = false
            }
            else -> titleTextView.text = title
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
        ) ?: return
        scrollView?.apply {

            val scrollViewSrc = this@apply
            if(!titleTextView.isVisible) {
                val constraintLayoutParams =
                    scrollViewSrc.layoutParams as ConstraintLayout.LayoutParams
                constraintLayoutParams.apply setConstraint@{
                    topMargin = context.resources.getDimension(R.dimen.twenty_dp).let {
                        it * (3f / 4f)
                    }.toInt()
                }
            }
            val yScrollMap = configMap.get(TextDialogSection.Y_SCROLL.section)?.let {
                CmdClickMap.createMap(
                    it,
                    keySeparator
                ).toMap()
            }
            val saveTag = yScrollMap?.get(
                YScroll.SAVE_TAG.key
            )
            val scrollPosiSaveFile = when(saveTag.isNullOrEmpty()) {
                true -> null
                else -> TxtHtmlDescriber.makeCurrentFannelHtmlPosiDirPath(
                    terminalFragment.activity,
                    FannelInfoTool.makeFannelInfoMapByString(
                        File(fannelPath).name
                    )
                ).let {
                    File(
                        it,
                        UsePath.compExtend(saveTag, ".txt")
                    )
                }
            }
//            viewTreeObserver?.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
//                override fun onDraw() {
//                    ToastUtils.showShort("aa")
//                }
//            })

            viewTreeObserver?.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Remove listener because we don't want this called before _every_ frame
                    viewTreeObserver?.removeOnPreDrawListener(this)
                    if(
                        scrollPosiSaveFile == null
                        ) return false
                    CoroutineScope(Dispatchers.IO).launch {
                        val yPosi = withContext(Dispatchers.IO) {
                            ReadText(
                                scrollPosiSaveFile.absolutePath,
                            ).readText().let {
                                try {
                                    it.toInt()
                                } catch (e: Exception) {
                                    null
                                }
                            }
                        }?: return@launch
                        withContext(Dispatchers.Main) {
                            scrollViewSrc.scrollY = yPosi
                        }
                    }
                    return true // true because we don't want to skip this frame
                }
            })
            viewTreeObserver?.addOnScrollChangedListener(object: ViewTreeObserver.OnScrollChangedListener {
                var oldPositionY = 0
                private var beforeTime = LocalDateTime.parse("2020-02-15T21:30:50")
                override fun onScrollChanged() {
                    CoroutineScope(Dispatchers.Main).launch {
                        val curScrollPosiY = withContext(Dispatchers.Main) {
                            scrollViewSrc.scrollY
                        }
                        val currentDateTime = LocalDateTime.now()
                        if(
                            LocalDatetimeTool.getDurationMiliSec(beforeTime, currentDateTime) < 200
                        ) return@launch
                        beforeTime = currentDateTime
                        withContext(Dispatchers.IO){
                            if(
                                scrollPosiSaveFile == null
                            ) return@withContext
                            FileSystems.writeFile(
                                scrollPosiSaveFile.absolutePath,
                                curScrollPosiY.toString()
                            )
                        }
                        val diffYPosi = withContext(Dispatchers.IO) {
                            oldPositionY - curScrollPosiY
                        }
                        oldPositionY = curScrollPosiY
                        if(diffYPosi < -10 && !titleTextView.isVisible) {
                            withContext(Dispatchers.Main) {
                                titleTextView.isVisible = true
//                                CoroutineScope(Dispatchers.Main).launch {
//                                    withContext(Dispatchers.IO){
//                                        delay(100)
//                                    }
//                                    withContext(Dispatchers.Main){
//                                        scrollViewSrc.parent.requestDisallowInterceptTouchEvent(false)
//                                    }
//                                }
//                                val constraintLayoutParams = scrollView.layoutParams as ConstraintLayout.LayoutParams
//                                constraintLayoutParams.apply setConstraint@ {
//                                    topToBottom = ConstraintLayout.LayoutParams.UNSET
//                                    topToTop = ConstraintLayout.LayoutParams.PARENT_ID
//                                }
//                                scrollView.layoutParams = constraintLayoutParams
                            }

                            return@launch
                        }

                        if(diffYPosi > 100) {
                            withContext(Dispatchers.Main) {
                                if (titleTextView.isVisible) {
                                    titleTextView.isVisible = false
//                                    CoroutineScope(Dispatchers.Main).launch {
//                                        withContext(Dispatchers.IO){
//                                            delay(100)
//                                        }
//                                        withContext(Dispatchers.Main){
//                                            scrollViewSrc.parent.requestDisallowInterceptTouchEvent(false)
//                                        }
//                                    }
//                                    val constraintLayoutParams = scrollView.layoutParams as ConstraintLayout.LayoutParams
//                                    constraintLayoutParams.apply setConstraint@ {
//                                        topToBottom = R.id.text_dialog_v2_title
//                                        topToTop = ConstraintLayout.LayoutParams.UNSET
//                                    }
//                                    scrollView.layoutParams = constraintLayoutParams
                                }
                                if (titleTextView.maxLines != titleOridnalyMaxLines) {
                                    titleTextView.maxLines = titleOridnalyMaxLines
                                }
                            }
                            return@launch
                        }

                    }
                }
            })
        }

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
            }
        }
    }
}