package com.puutaro.commandclick.util

object MarkDownTool {
    fun convertMdToHtml(
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
}