package com.puutaro.commandclick.fragment_lib.terminal_fragment.html

import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import java.io.File

object TxtHtmlDescriber {
    fun make(
        urlStr: String,
    ): String {
        FileSystems.createDirs(UsePath.cmdclickTextHtmlDirPath)
        val filePath = urlStr.removePrefix(
            WebUrlVariables.filePrefix
        )
        val fileObj = File(filePath)
        val parent = fileObj.parent ?: String()
        val fileName = fileObj.name
        val contents = ReadText(
            parent,
            fileName
        ).readText()
        val insertContents =
            contents
//                .replace("<", "&lt;")
//                .replace(">", "&gt;")
//                .replace(" ", "&nbsp;")
//                .replace("%", "&#37;")
//                .replace("&", "&amp;")
        return """
        <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
        <head>
        <title>${filePath}</title>
        <meta name="color-scheme" content="light dark">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
        </head>
        <body>
        <p>---</p>
        <pre style="word-wrap: break-word; white-space: pre-wrap;">
        ${insertContents}
        </pre>
        </body>
        <script>
        var positionY;
        var STORAGE_KEY = "scrollY";
        const title = document.title;
        function checkOffset(){
            positionY = window.pageYOffset;
            jsFileSystem.writeLocalFile(
                "${UsePath.cmdclickScrollPosiFilePath}",
                title + positionY
            );
        }
        window.addEventListener("load", function(){
            execScroll();
            
            window.addEventListener("scroll", checkOffset, false);
        });
        
        
        function execScroll(){
            const positionYWithPrefix = jsFileSystem.readLocalFile(
                "${UsePath.cmdclickScrollPosiFilePath}"
            );
            if(positionYWithPrefix == null) return
            const regex = new RegExp('^' + title);
            const positionYEntry = positionYWithPrefix.replace(regex, "");
            if(
                isNaN(positionYEntry)
            ) return;
            positionY =  positionYEntry;
            scrollTo(0, positionY);
        };
        </script>
        </html>
        """.trimMargin()
    }
}