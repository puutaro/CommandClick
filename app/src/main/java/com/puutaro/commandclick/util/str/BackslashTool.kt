package com.puutaro.commandclick.util.str

object BackslashTool {


    fun toNormal(
        targetStr: String
    ): String {
        val convertedSpecialCodeCon =
            SpecialStr.replace(targetStr)
        val hexConvertedStr =
            CmdClickHexCodeToInt.to(convertedSpecialCodeCon)
        val backslashReplaceCon =
            let {
                val backslash = "\\"
                hexConvertedStr
                    .replace(
                        Regex("${backslash}${backslash}([\"'`$#${backslash}${backslash}])"),
                        "$1"
                    )
            }
        return backslashReplaceCon
    }

    fun toNormalByEscape(
        targetStr: String
    ): String {
        val convertedSpecialCodeCon =
            SpecialStr.replace(targetStr)
        val hexConvertedStr =
            CmdClickHexCodeToInt.to(convertedSpecialCodeCon)
        val backslashReplaceCon =
            let {
                val backslash = "\\"
                hexConvertedStr
                    .replace(
                        Regex("${backslash}${backslash}([`$])"),
                        "$1"
                    )
            }
        return backslashReplaceCon
    }

    private object SpecialStr {
        fun replace(
            con: String,
        ): String{
            val specialStrToCodePairList = listOf(
                Pair("\\n", "\n"),
                Pair("\\t", "\t"),
            )
            var replaceCon = con
            specialStrToCodePairList.forEach {
                    (specialStr, code) ->
                replaceCon = replaceCon.replace(
                    specialStr,
                    code
                )
            }
            return replaceCon
        }
    }

    private object CmdClickHexCodeToInt {

        private val cmdClickHexRegex = Regex(
            ".?\\\\0x[0-7][0-f]"
        )

        fun to(
            backslashReplaceCon: String,
        ): String {
            val hexPrefix = "\\0"
            val hexStringToCharList = cmdClickHexRegex.findAll(
                backslashReplaceCon
            ).map { result ->
                val hexStrWithPrefix = result.value
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lreplace1.txt").absolutePath,
//                    listOf(
//                        "hexStrWithPrefix: ${hexStrWithPrefix}",
//                        "hexStrWithPrefix.startsWith(\"\\\\\\\\\"): ${hexStrWithPrefix.startsWith("\\\\")}",
//                    ).joinToString("\n")
//                )
                if (
                    hexStrWithPrefix.startsWith("\\\\")
                ) return@map String() to String()
                val charStrWithPrefix = let {
                    val prefixToHexStr = when (
                        hexStrWithPrefix.startsWith(hexPrefix)
                    ) {
                        true -> String() to hexStrWithPrefix
                        else -> hexStrWithPrefix.first().toString() to
                                hexStrWithPrefix.substring(1)
                    }
                    val prefix = prefixToHexStr.first
                    val hexStr = prefixToHexStr.second
//                    FileSystems.updateFile(
//                        File(UsePath.cmdclickDefaultAppDirPath, "lreplace1.txt").absolutePath,
//                        listOf(
//                            "hexStrWithPrefix: ${hexStrWithPrefix}",
//                            "hexStrWithPrefix.startsWith(hexPrefix): ${hexStrWithPrefix.startsWith(hexPrefix)}",
//                            "prefixToHexStr: ${prefixToHexStr}",
//                            "prefix: ${prefix}",
//                            "hexStr: ${hexStr}",
//                            " prefix + execTo(hexStr): ${ prefix + execTo(hexStr)}"
//                        ).joinToString("\n")
//                    )
                    prefix + execTo(hexStr)
                }
                hexStrWithPrefix to charStrWithPrefix
            }.filter {
                it.first.isNotEmpty()
            }
            var conWitchHexToChar = backslashReplaceCon
            hexStringToCharList.forEach { hexStringToChar ->
                val hexStr = hexStringToChar.first
                val char = hexStringToChar.second
                conWitchHexToChar = conWitchHexToChar.replace(
                    hexStr,
                    char,
                )
            }
            return conWitchHexToChar
        }

        private fun execTo(hexString: String): String {
            //            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lreplace.txt").absolutePath,
//                listOf(
//                    "targetStr: ${targetStr}",
//                    "backslashReplaceCon: ${backslashReplaceCon}",
//                    "hexStr: ${hexStr}",
//                    "poc to 0: ${ hexStr.removePrefix("\\0x")}",
//                    "poc toInt: ${ hexStr.removePrefix("\\0x").toInt(16).toChar()}",
//                    "char: ${char}"
//                ).joinToString("\n\n") + "\n\n============\n\n"
//            )
            return try {
                hexString.removePrefix("\\0x").toInt(16).toChar().toString()
            } catch (e: Exception) {
                hexString
            }
        }
    }
}