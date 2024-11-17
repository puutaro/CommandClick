package com.puutaro.commandclick.fragment_lib.terminal_fragment.html

import androidx.fragment.app.FragmentActivity
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.util.url.WebUrlVariables
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.TextJsDialogV2.BodyTextViewMaker.separatorList
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.WevViewDialogUriPrefix
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object TxtHtmlDescriber {

    private val indexDirName = "index"
    val searchQuerySuffix = "?q="
    val qerySeparator = '&'
    private val switchOn = "ON"
    private val switchOff = "OFF"


    fun makeTxtHtmlUrl(
        txtPath: String,
        queryConWithNewline: String? = null,
    ): String {
        val queryParameter = when(queryConWithNewline.isNullOrEmpty()){
            true -> String()
            else -> searchQuerySuffix + queryConWithNewline.replace("\n", qerySeparator.toString())
        }
        return listOf(
            WevViewDialogUriPrefix.TEXT_CON.prefix,
            txtPath,
            queryParameter,
        ).joinToString(String())
    }
    fun make(
        terminalFragment: TerminalFragment,
        savePath: String,
    ): String {
        FileSystems.createDirs(UsePath.cmdclickScrollPosiDirPath)
        val basePath = savePath.removePrefix(
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
        val contents = ReadText(
            filePath
        ).readText()
        val currentFannelHtmlPosiDirPath =
            makeCurrentFannelHtmlPosiDirPath(terminalFragment.activity)
        FileSystems.createDirs(currentFannelHtmlPosiDirPath)
        val fileObj = File(filePath)
        val fileName = fileObj.name
        val saveTag = queryMap.get(
            TxtHtmlQueryKey.SAVE_TAG.key
        )?.let {
            QuoteTool.trimBothEdgeQuote(it)
        }
        val fannelPath = queryMap.get(
            TxtHtmlQueryKey.FANNEL_PATH.key
        )?.let {
            QuoteTool.trimBothEdgeQuote(it)
        }
        val htmlPosiFilePath = when(
            saveTag.isNullOrEmpty()
                    || fannelPath.isNullOrEmpty()
        ) {
            true -> "${currentFannelHtmlPosiDirPath}/${fileName}"
            else -> makeCurrentFannelHtmlPosiDirPath(
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
        val onFormat = queryMap.get(
            TxtHtmlQueryKey.ON_FORMAT.key
        )?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } != switchOff
        val insertContents = when(onFormat) {
            false -> contents
            else -> TextFormater.format(contents)
        }
        val compHyphen = when(
            insertContents
                .replace("\n", String())
                .trim()
                .isEmpty()
        ){
            true -> "<p>---</p>"
            else -> String()
        }

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
            font-family: "sans-serif";
            font-size: 20px;
            color: #1e2e1c;
            letter-spacing: 0.1em;
            line-height: 1.7em;
            white-space: pre-wrap;
            word-wrap: break-word;
            overflow: auto;
            #border: 1px solid;
            #background-color: red;
        }
        </style>
        <body>
        <div id="monitorText">
        ${compHyphen}
        <pre>${insertContents}</pre>
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
        ON_FORMAT("onFormat"),
        DISABLE_SCROLL("disableScroll"),
        FANNEL_PATH("fannelPath"),
        SAVE_TAG("saveTag"),
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

    fun makeCurrentFannelHtmlPosiDirPath(
        activity: FragmentActivity?,
        fannelInfoMap: Map<String, String>? = null
    ): String {
        val sharePref = FannelInfoTool.getSharePref(activity)
        val fannelDirName = when(fannelInfoMap.isNullOrEmpty()) {
            true -> FannelInfoTool.getStringFromFannelInfo(
                sharePref,
                FannelInfoSetting.current_fannel_name
            )
            else -> FannelInfoTool.getCurrentFannelName(fannelInfoMap)
        }.let {
            val fannelDirNameSrc = CcPathTool.makeFannelDirName(it)
            when(
                fannelDirNameSrc.isEmpty()
            ) {
                true -> File(UsePath.fannelSystemDirPath).name
                else -> fannelDirNameSrc
            }
        }
        return listOf(
                UsePath.cmdclickDefaultAppDirPath,
                fannelDirName,
                UsePath.fannelSystemScrollPosiDirName,
            ).joinToString("/").replace(
            Regex("[/]+"),
            "/"
        )
//    "${UsePath.cmdclickScrollPosiDirPath}/${fannelRawName}"
    }

    private object TextFormater {
        fun format(
            bodySrc: String
        ): String {
            return bodySrc.replace(
                Regex("\n[ 　]*"),
                "\n",
            ).replace(
                Regex("[\n]+"),
                "\n",
            ).split("\n").map { line ->
                var repLine = line.trim()
                separatorList.forEach {
                    repLine = repLine.replace(
                        it,
                        "${it}\n",
                    )
                }
                val splitSentenceNum = 3
                val sentenceList =
                    repLine.trim('\n').split("\n").chunked(splitSentenceNum)
                        .map chunk@{ sentenceList ->
                            val sentence = sentenceList.filter {
                                it.trim().isNotEmpty()
                            }.joinToString(String()).trim('\n') + "\n\n"
                            if(sentence.isEmpty()) return String()
                            val firstString = sentence.first().uppercase()
                                .replace("<", "&lt;")
                                .replace(">", "&gt;")
                                .let {
                                    "<span style=\"font-size: 200%;\">${it}</span>"
                                }
                            val afterString = sentence.substring(1)
                            firstString + afterString
                        }
                sentenceList.joinToString(String())
//                TextUtils.concat(*sentenceList.toTypedArray())
            }.joinToString(String())
//                TextUtils.concat(*it.toTypedArray())
        }
    }


}