package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment

class JsHtml(
    terminalFragment: TerminalFragment
) {
    val context = terminalFragment.context

    @JavascriptInterface
    fun txtHtml(
        contents: String,
        isScrollBottom: Boolean,
    ): String {
        return """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title></title>

<style>
    #textCon { 
        width: 95%; 
        margin-right: auto;
        margin-left: auto;
    }
    
    a { 
        color:#0000FF; text-decoration:none; 
    }

</style>
</head>
<body>
    <div id="textCon">
    ${contents}
    </div>
    <script>
        const scrollingElement = (document.scrollingElement || document.body);
        if(${isScrollBottom}){
            scrollingElement.scrollTop = document.body.offsetHeight;
        }
    </script> 
</body>
</html>        
        
    """.trimIndent()
    }
}