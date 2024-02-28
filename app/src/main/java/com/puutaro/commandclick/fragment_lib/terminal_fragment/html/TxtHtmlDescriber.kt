package com.puutaro.commandclick.fragment_lib.terminal_fragment.html

import android.content.Context
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePrefTool
import java.io.File

object TxtHtmlDescriber {

    private val indexDirName = "index"
    fun make(
        urlStr: String,
        terminalFragment: TerminalFragment
    ): String {
        val sharePref = terminalFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        FileSystems.createDirs(UsePath.cmdclickScrollPosiDirPath)
        val filePath = urlStr.removePrefix(
            WebUrlVariables.filePrefix
        )
        val contents = ReadText(
            filePath
        ).readText()
        val fannelRawName = SharePrefTool.getStringFromSharePref(
                sharePref,
                SharePrefferenceSetting.current_fannel_name
            ).replace(
            Regex("\\.[a-zA-Z0-9]*$"),
            ""
        )
        val currentFannelHtmlPosiDirPath = if(
            fannelRawName.isEmpty()
        ) indexDirName
        else "${UsePath.cmdclickScrollPosiDirPath}/${fannelRawName}"
        FileSystems.createDirs(currentFannelHtmlPosiDirPath)
        val fileObj = File(filePath)
        val fileName = fileObj.name
        val htmlPosiFilePath =
            "${currentFannelHtmlPosiDirPath}/${fileName}"
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
        <style type="text/css">
        #monitorText {
            width: 99%;
            margin-right: auto;
            margin-left: auto;
            #background-color: yellow;
        }
        pre {
            white-space: pre-wrap;
            word-wrap: break-word;
            overflow: auto;
            #border: 1px solid;
            #background-color: red;
        }
        </style>
        <body>
        <div id="monitorText">
        <p>---</p>
        <pre>
        ${insertContents}
        </pre>
        </div>
        </body>
        <script>
        var positionY;
        var STORAGE_KEY = "scrollY";
        const title = document.title;
        function checkOffset(){
            positionY = window.pageYOffset;
            jsFileSystem.writeLocalFile(
                "${htmlPosiFilePath}",
                positionY
            );
        }
        window.addEventListener("load", function(){
            execScroll();
            
            window.addEventListener("scroll", checkOffset, false);
        });
        
        
        function execScroll(){
            positionYEntry = jsFileSystem.readLocalFile(
                "${htmlPosiFilePath}"
            );
            if(positionYEntry == null) return
            if(
                isNaN(positionYEntry)
            ) return;
            positionY =  Number(positionYEntry);
            scrollTo(0, positionY);
        };
        </script>
        </html>
        """.trimMargin()
    }
}