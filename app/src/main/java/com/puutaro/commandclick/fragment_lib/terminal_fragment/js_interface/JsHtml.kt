package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import java.lang.ref.WeakReference

class JsHtml(
    terminalFragmentRef: WeakReference<TerminalFragment>
) {

    @JavascriptInterface
    fun txt2Html(
        contents: String,
        isScrollBottom: Boolean,
    ): String {
        /*
        Convert text to html
        */
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