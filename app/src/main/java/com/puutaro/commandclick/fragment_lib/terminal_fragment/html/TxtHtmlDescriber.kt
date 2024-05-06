package com.puutaro.commandclick.fragment_lib.terminal_fragment.html

import android.content.Context
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.variables.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.SharePrefTool
import java.io.File

object TxtHtmlDescriber {

    private val indexDirName = "index"
    val searchQuerySuffix = "?q="
    private val qerySeparator = '&'
    fun make(
        urlStr: String,
        terminalFragment: TerminalFragment
    ): String {
        val sharePref = terminalFragment.activity?.getPreferences(Context.MODE_PRIVATE)
        FileSystems.createDirs(UsePath.cmdclickScrollPosiDirPath)
        val basePath = urlStr.removePrefix(
            WebUrlVariables.filePrefix
        )
        val pathAndQueryMapCon = basePath.split(searchQuerySuffix)
        val filePath = pathAndQueryMapCon.firstOrNull()
            ?: return String()
        val queryMapCon = pathAndQueryMapCon.lastOrNull()
        val queryMap = CmdClickMap.createMap(
            queryMapCon,
            qerySeparator
        ).toMap()
        val disableScroll = DisableScroll.how(queryMap)
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "txtHtml.txt").absolutePath,
//            listOf(
//                "basePath: ${basePath}",
//                "pathAndQueryMapCon: ${pathAndQueryMapCon}",
//                "filePath: ${filePath}",
//                "queryMapCon: ${queryMapCon}",
//                "queryMap: ${queryMap}",
//                "disableScroll: ${disableScroll}"
//            ).joinToString("\n\n")
//        )
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
        val insertContents = contents
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
            if(${disableScroll}) return;
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
            if(${disableScroll}) return;
            positionYEntry = jsFileSystem.readLocalFile(
                "${htmlPosiFilePath}"
            );
            if(positionYEntry == null) return
            if(
                isNaN(positionYEntry)
            ) return;
            positionY = Number(positionYEntry);
            scrollTo(0, positionY);
        };
        </script>
        </html>
        """.trimMargin()
    }

    enum class TxtHtmlQueryKey(
        val key: String
    ) {
        DISABLE_SCROLL("disableScroll")
    }

    object DisableScroll {

        const val disableScrollMemoryOn = "ON"
        fun how(
            queryMap: Map<String, String>
        ): Boolean{
            return QuoteTool.trimBothEdgeQuote(
                queryMap.get(
                    TxtHtmlQueryKey.DISABLE_SCROLL.key
                )
            ) == disableScrollMemoryOn
        }
    }


}