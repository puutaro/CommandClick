package com.puutaro.commandclick.util

object AsciiTool {

    fun convertTermStrToHtml(
        contents: String
    ): String{
        val htmlContents =
            contents
                .replace(
                    "[\\u001b\\u009b][\\[()#;?]*(?:[0-9]{1,4}(?:;[0-9]{0,4})*)?[0-9A-ORZcf-nqry=><]".toRegex(),
                    ""
                )
                .replace(
                    "[\\x00-\\x1F\\x7F-\\xA0]+".toRegex(),
                    ""
                )
                .replace(
                    "\n",
                    "</br>"
                )
                .replace(
                    "(cmdclick@localhost)".toRegex(),
                    "</br><span style=\"color:green;font-weight: bold;\">$1</span>"
                )
                .replace("]0;\n*".toRegex(), "")
//                .replace(
//                    "[\\x00-\\x1F\\x7F-\\xA0]+".toRegex(),
//                    "\n"
//                )
//                .replace(
//                    "(cmdclick@localhost)".toRegex(),
//                    "</br><span style=\"color:green;font-weight: bold;\">$1</span>"
//                )
//                .replace(
//                    "\n\n*".toRegex(),
//                    "\n"
//                )
//                .replace("\\[0[0-9;:]*m".toRegex(), "")
//                .replace(
//                    "\n",
//                    "</br>"
//                )
        return """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title></title>

<style>
    #terminalCon { 
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
    <div id="terminalCon">
    ${htmlContents}
    </div>
</body>
</html>        
        
    """.trimIndent()
    }
}