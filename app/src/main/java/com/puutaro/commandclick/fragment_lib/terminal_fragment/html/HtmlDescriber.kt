package com.puutaro.commandclick.fragment_lib.terminal_fragment.html

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object HtmlDescriber {

    fun make(
        terminalFragment: TerminalFragment,
        textSrc:String,
        terminalViewModel: TerminalViewModel
    ): String {
        val terminalColor = terminalFragment.terminalColor
        val terminalFontColor = terminalFragment.terminalFontColor
        val onBottomScrollbyJs = terminalViewModel.onBottomScrollbyJs
        val currentAppDirPath = terminalFragment.currentAppDirPath
        val scrollPosiFilePath = "${currentAppDirPath}/${UsePath.cmdclickScrollSystemDirRelativePath}/${UsePath.cmdclickMonitorScrollPosiFileName}"
        val leavesLineForTerm = ReadText.leavesLineForTerm
        val sb = StringBuilder("％")
        val percentZenkakuChar = sb[0]
        sb.setCharAt(0, percentZenkakuChar - 0xFEE0)
        val percentHankakuChar = sb.toString()
        val bodyText = textSrc
            .split("\n").takeLast(leavesLineForTerm).joinToString("\n")
            .replace("％", "CMDCLICK_PERCENT_CHAR")
            .replace(
                "((https?|ftp|file)://[^ 　\t]*)".toRegex(),
                "<a href=\"javascript:void(0);\" target='_blank' onclick=\"saveScrollPosi(this);\">$1</a>"
            )
            .replace("CMDCLICK_PERCENT_CHAR", percentHankakuChar)
            .replace("cmdclickLeastTagspan", "<a")
            .replace("cmdclickLeastTag/span", "</a")
            .replace("cmdclickLeastTag", "<")
            .replace("cmdclickGreatTag", ">")
        return """
        <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
        <html>
        <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        <title></title>
        <style type="text/css">
        
        .ansi2html-content { display: inline; white-space: pre-wrap; word-wrap: break-word; }
        .body_background { background-color: ${terminalColor}; }
        .inv_foreground { color: ${terminalColor}; }
        .body_foreground { color: #AAAAAA; }
        .inv_background { background-color: #AAAAAA; }
        .invisible-row {
          display: none;
        }
        
        pre {
            color: ${terminalFontColor};
        }
        
        a:link,
        a:hover,
        a:focus,
        a:active {
            color: ${terminalFontColor};
        }
        a:visited {
            color: ${terminalFontColor};
        }
    
        </style>
        </head>
        <body class="body_foreground body_background" style="font-size: normal;" >
        <p>---</p>
        <pre id="onCmdClickFilter" class="ansi2html-content">${bodyText}</pre>
        <script>
            setTimeout(function(){
                    const scrollingFirstElement = (document.scrollingElement || document.body);
                    const isHistoryMove = window.performance.navigation.type == 2
                    if(isHistoryMove
                    ) {  
                        const scrollPosiStr = jsFileSystem.readLocalFile(
                            "${scrollPosiFilePath}"
                        );
                        const scrollPosi = parseFloat(scrollPosiStr);
                        if(!scrollPosi) return
                        scrollingFirstElement.scrollTop = scrollPosi;
                        return
                    }
                    if(${onBottomScrollbyJs}) {
                        scrollingFirstElement.scrollTop = document.body.offsetHeight;
                    }
                },
                200
            );
        </script>
        </body>
        <script src="file:///android_asset/js/bootstrap.bundle.min.js"></script>
        <script src="file:///android_asset/js/jquery-3.6.3.min.js"></script>
        <script>
        const scrollingElement = (document.scrollingElement || document.body);
        let toFilterSource = ${'$'}('#onCmdClickFilter');
        const percent = String.fromCharCode('％'.charCodeAt(0) - 0xFEE0);
        let exp = /[^href=\"](\b(https?|ftp|file):\/\/[^ 　\t]*)/ig;
        
        function saveScrollPosi(thisItem){
            const loadUrl = thisItem.textContent
            var scrollPosition = scrollingElement.scrollTop;            
            jsFileSystem.writeLocalFile(
                "${scrollPosiFilePath}",
                scrollPosition
            );
            location.href = loadUrl
        };
        
        function terminalFilter(thisInput){
            var thisInputValue = thisInput.toLowerCase().trim()
            var toFilter = toFilterSource;
            if (toFilter.find('span').length < 1) {
              /* Split lines using spans, but include ending new line char */
              var oldTextSource = toFilter.html();
              var oldText = oldTextSource
                                .replace(exp,"<a href='${'$'}1' target='_blank'>${'$'}1</a>")
                                .replace(/cmdclickLeastTagspan/g, "<a")
                                .replace(/cmdclickLeastTag\/span/g, "</a")
                                .replace(/cmdclickLeastTag/g, "<")
                                .replace(/cmdclickGreatTag/g, ">");
              var oldTextSplit = oldText.split('\n');
              var newText = '<span>' + oldTextSplit.join('\n</span><span>') + '\n</span>';
              toFilter.html(newText);
            };
            if (thisInputValue) {
              /* Filter (hide) rows which contain no filter */
              toFilter.find('span').each(function(i) {
                var thisRow = ${'$'}(this);
                var thisRowText = thisRow.text().toLowerCase();
                if (thisRowText.indexOf(thisInputValue) < 0) {
                  thisRow.addClass('invisible-row');
                } else {
                  thisRow.removeClass('invisible-row');
                };
              });
            } else {
              /* Nothing to filter, show all rows */
              toFilter.find('span').removeClass('invisible-row');
            };
           
            scrollingElement.scrollTop = document.body.offsetHeight;
        }
        
        </script>
        </html>
        """.trimMargin()
    }

}
