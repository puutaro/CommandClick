package com.puutaro.commandclick.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment


object DialogObject {

    private var simpleTextDialogObj: Dialog? = null
    private var descWebDialog: Dialog? = null
    private val defaultFontPercentage = 140

    fun simpleTextShow(
        contextSrc: Context?,
        title: String,
        contents: String
    ) {
        val context = contextSrc
            ?: return
        simpleTextDialogObj = Dialog(
                context
            )
        simpleTextDialogObj?.setContentView(
                R.layout.text_simple_dialog_layout
            )
        val titleTextView =
            simpleTextDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_title
            )
        if(
            title.isNotEmpty()
        ) titleTextView?.text = title
        else titleTextView?.isVisible = false
        val descriptionTextView =
            simpleTextDialogObj?.findViewById<AppCompatTextView>(
                R.id.text_simple_dialog_text_view
            )
        if(
            contents.isNotEmpty()
        ) descriptionTextView?.text = contents
        else descriptionTextView?.isVisible = false

        val cancelImageButton =
            simpleTextDialogObj?.findViewById<AppCompatImageButton>(
                R.id.text_simple_dialog_cancel
            )
        cancelImageButton?.setOnClickListener {
            simpleTextDialogObj?.dismiss()
        }
        simpleTextDialogObj?.setOnCancelListener {
            simpleTextDialogObj?.dismiss()
            }
        simpleTextDialogObj?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        simpleTextDialogObj?.window?.setGravity(
                Gravity.BOTTOM
            )
        simpleTextDialogObj?.show()
    }

    fun descDialog(
        fragment: Fragment,
        scriptName: String,
        contents: String
    ){
        val context = fragment.context
            ?: return
        val activity = fragment.activity
            ?: return
        descWebDialog = Dialog(
            context
        )
        descWebDialog?.setContentView(
            R.layout.markdown_simple_dialog_layout
        )
        val textView = descWebDialog?.findViewById<AppCompatTextView>(
            R.id.desc_dialog_title
        ) ?: return
        textView.isVisible = false
        val webView = descWebDialog?.findViewById<WebView>(
            R.id.desc_dialog_webview
        ) ?: return
        webViewSetting(
            fragment,
            webView
        )
        webView.loadDataWithBaseURL(
            "",
            convertMdToHtml(
                scriptName,
                contents
            ),
            "text/html",
            "utf-8",
            null
        )
        FileSystems.writeFile(
            UsePath.cmdclickDefaultAppDirPath,
            "desc.html",
            convertMdToHtml(
                scriptName,
                contents
            )
        )
        val progressBar = descWebDialog?.findViewById<ProgressBar>(
            R.id.desc_dialog_webview_progressBar
        )
        val webViewCancelBtn = descWebDialog?.findViewById<ImageButton>(
            R.id.desc_webview_dialog_cancel
        ) ?: return
        webViewCancelBtn.setOnClickListener {
            descWebDialog?.dismiss()
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    progressBar?.visibility = View.GONE
                } else {
                    progressBar?.visibility = View.VISIBLE
                    progressBar?.progress = newProgress
                }
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (
                    request?.url?.scheme.equals("intent")
                    || request?.url?.scheme.equals("android-app")
                ) {
                    val intent = Intent.parseUri(request?.url.toString(), Intent.URI_INTENT_SCHEME)
                    val packageManager = activity.packageManager
                    if (
                        packageManager != null && intent?.resolveActivity(
                            packageManager
                        ) != null
                    ) {
                        activity.startActivity(intent)
                        return true
                    }

                }
                return false
            }
        }
        val webViewBackBtn = descWebDialog?.findViewById<ImageButton>(
            R.id.desc_webview_dialog_back
        ) ?: return
        webViewBackBtn.setOnClickListener {
            if(
                !webView.canGoBack()
            ) return@setOnClickListener
            webView.goBack()
        }
        webViewBackBtn.setOnLongClickListener {
            descWebDialog?.dismiss()
            true
        }
        descWebDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        descWebDialog?.show()

        descWebDialog?.setOnCancelListener {
            descWebDialog?.dismiss()
        }
    }

    private fun webViewSetting(
        fragment: Fragment,
        webView: WebView
    ){
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowContentAccess = true
        settings.allowFileAccess = true
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        val terminalFontPercentage = getFontZoomPercentage(
            fragment
        )
        settings.textZoom = (terminalFontPercentage * 95 ) / 100
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    }

    private fun getFontZoomPercentage(
        fragment: Fragment
    ): Int {
        val context = fragment.context
            ?: return defaultFontPercentage
        val cmdIndexTerminal = TargetFragmentInstance(
        ).getFromFragment<TerminalFragment>(
            fragment.activity,
            context.getString(R.string.index_terminal_fragment)
        )
        if(
            cmdIndexTerminal != null
            && cmdIndexTerminal.isVisible
        ) return cmdIndexTerminal.fontZoomPercent

        val editExecuteTerminal = TargetFragmentInstance(
        ).getFromFragment<TerminalFragment>(
            fragment.activity,
            context.getString(R.string.edit_execute_terminal_fragment)
        )
        if(
            editExecuteTerminal != null
            && editExecuteTerminal.isVisible
        ) return editExecuteTerminal.fontZoomPercent

        return defaultFontPercentage
    }
}


private fun convertMdToHtml(
    scriptName: String,
    contents: String
): String{
    return """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title></title>
<script src="file:///android_asset/js/jquery-3.6.3.min.js"></script>

<!-- ★マークダウン変換用 -->
<script src="file:///android_asset/js/marked_0.3.2_marked.min.js"></script>

<!-- ◆シンタックスハイライト用 -->
<script src="file:///android_asset/js/highlight.js_9.9.0_highlight.min.js"></script>
<!-- ◆VBをシンタックスハイライトする必要があるならこれ↓も入れる -->
<script src="file:///android_asset/js/highlight.js_9.9.0_languages_vbnet.min.js"></script>
<!-- ◆シンタックスハイライト用 css （好きなテーマを選んで指定する） -->
<link rel="stylesheet" href="file:///android_asset/js/highlight.js_9.9.0_styles_ir-black.min.css">

<script>
    ${'$'}(function() {

        // ★marked.js の設定
        marked.setOptions({
            breaks : true,

            // highlight.js でハイライトする
            highlight: function(code, lang) {
                return hljs.highlightAuto(code, [lang]).value;
            }
        });

        // highlight.js の初期処理
        hljs.initHighlightingOnLoad(); 

        // ★マークダウンを HTML に変換して再セット
        var md = marked(getHtml("#markdown"));
        ${'$'}("#markdown").html(md);

    });

    // 比較演算子が &lt; 等になるので置換
    function getHtml(selector) {
        var html = ${'$'}(selector).html();
        html = html.replace(/&lt;/g, '<');
        html = html.replace(/&gt;/g, '>');
        html = html.replace(/&amp;/g, '&');

        return html;
    }

</script>
<style>
    table  { border-collapse: collapse; }
    th, td { border:1px solid #999; padding:2px 5px; }
    th     { background:#A8F2E1; }
/*    body { 
        width: 95%; 
        margin-right: auto;
        margint-left: auto;
    }*/

    #markdown { 
        width: 95%; 
        margin-right: auto;
        margin-left: auto;
    }
</style>
</head>
<body>
    <div id="markdown">

# ${scriptName}
-------

${contents}

</div>
</body>
</html>        
        
    """.trimIndent()
}