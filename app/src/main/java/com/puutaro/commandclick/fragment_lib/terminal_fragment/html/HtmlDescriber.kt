package com.puutaro.commandclick.fragment_lib.terminal_fragment.html

class HtmlDescriber {
    companion object {
        fun make(
            terminalColor: String,
            terminalFontColor: String,
            text:String,
            onBottomScrollByJs: Boolean
        ): String {
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
            <pre id="onCmdClickFilter" class="ansi2html-content">${text}</pre>
            </body>
            <script src="file:///android_asset/js/bootstrap.bundle.min.js"></script>
            <script src="file:///android_asset/js/jquery-3.6.3.min.js"></script>
            <script>
            const scrollingElement = (document.scrollingElement || document.body);
            if(%b){
                scrollingElement.scrollTop = document.body.offsetHeight;
            }
            let toFilterSource = ${'$'}('#onCmdClickFilter');
            const percent = String.fromCharCode('％'.charCodeAt(0) - 0xFEE0);
            let exp = /[^href=\"](\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/&#37;?=~_|!:,.;]*[-A-Z0-9+&@#\/&#37;=~_|])/ig;
            ${'$'}('#onCmdClickFilter')
                .html(
                    ${'$'}('#onCmdClickFilter')
                    .html().replaceAll(percent, "CMDCLICK_PERCENT_CHAR")
                    .replace(exp,"<a href='${'$'}1' target='_blank'>${'$'}1</a>")
                    .replaceAll("CMDCLICK_PERCENT_CHAR", percent)
                    .replace(/cmdclickLeastTagspan/g, "<a")
                    .replace(/cmdclickLeastTag\/span/g, "</a")
                    .replace(/cmdclickLeastTag/g, "<")
                    .replace(/cmdclickGreatTag/g, ">")
                );
               
            
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
            """.trimMargin().format(onBottomScrollByJs)
        }
    }
}