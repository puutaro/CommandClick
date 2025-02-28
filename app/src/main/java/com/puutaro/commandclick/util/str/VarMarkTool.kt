package com.puutaro.commandclick.util.str

object VarMarkTool {

    fun replaceByValue2(
        con :String,
        varName: String,
        varValue: String,
    ): String {
        return try {
            con.replace(
                "(?<!\\\\)[$][{]$varName[}]]".toRegex(),
                varValue,
            )
//            con.replace(
//                Regex("""([^\\])[$][{]${varName}[}]"""),
//                "$1${varValue}"
//            ).replace(
//                Regex("""^[$][{]${varName}[}]"""),
//                varValue
//            )
        } catch (e: Exception){
            con
        }
    }

    fun replaceByValue(
        input: String,
        varName: String,
        varValue: String,
    ): String {
        val target = "${'$'}{${varName}}"
        val result = StringBuilder()
        var index = 0

        while (true) {
            val targetIndex = input.indexOf(target, index)
            if (targetIndex == -1) {
                result.append(input.substring(index))
                break
            }

            if (targetIndex == 0 || input[targetIndex - 1] != '\\') {
                result.append(input.substring(index, targetIndex)).append(varValue)
                index = targetIndex + target.length
            } else {
                result.append(input.substring(index, targetIndex + target.length))
                index = targetIndex + target.length
            }
        }
        return result.toString()
    }


        fun convertVarName(varMark: String): String {
        return varMark
            .removePrefix("${'$'}{")
            .removeSuffix("}")
    }

    fun extractValNameListByBackslashEscape(
        con: String
    ): Sequence<String> {
        val varRegex = "(?<!\\\\)[$][{][a-zA-Z0-9_]+[}]".toRegex()
        val varNameSec = varRegex.findAll(con).map {
            convertVarName(it.value)
        }.sorted().distinct()
        return varNameSec
    }

    fun extractValNameList(
        con: String
    ): Sequence<String> {
        val varRegex = "[$][{][a-zA-Z0-9_]+[}]".toRegex()
        val varNameSec = varRegex.findAll(con).map {
            convertVarName(it.value)
        }.sorted().distinct()
        return varNameSec
    }

    fun filterByUseVarMark2(
        targetMap: Map<String, String?>?,
        valNameSeq: Sequence<String>
    ): Map<String, String?>? {
        if(
            targetMap.isNullOrEmpty()
            || !valNameSeq.any()
        ){
            return targetMap
        }
        val filteredMap = targetMap.filter {
            valNameSeq.contains(it.key)
        }
        return filteredMap
    }

    fun filterByUseVarMark(
        targetMap: Map<String, String?>?,
        con: String,
    ): Map<String, String?>? {
        val filteredMap = targetMap?.filter {
            con.contains("${'$'}{${it.key}}")
        }
        return filteredMap
    }

    fun findVarNameIndex(
        input: String,
        varName: String
    ): Pair<Int, Int>? {
        val target = "\${$varName}"
        var index = 0
        while (true) {
            val startIndex = input.indexOf(target, index)
            if (startIndex == -1) {
                return null
            }

            if (startIndex == 0 || input[startIndex - 1] != '\\') {
                return startIndex to startIndex + target.length
            } else {
                index = startIndex + target.length
            }
        }
    }

    fun findVarName(
        input: String,
        varName: String
    ): String? {
        val target = "\${$varName}"
        var index = 0

        while (true) {
            val targetIndex = input.indexOf(target, index)
            if (targetIndex == -1) {
                return null
            }

            if (targetIndex == 0 || input[targetIndex - 1] != '\\') {
                return target
            } else {
                index = targetIndex + target.length
            }
        }
    }

    fun matchesAlphaNumUnderscore(input: String): Boolean {
        if (input.isEmpty()) return false

        for (char in input) {
            if (!char.isAlphanumeric() && char != '_') {
                return false
            }
        }
        return true
    }

    private fun Char.isAlphanumeric(): Boolean {
        return this in 'A'..'Z' || this in 'a'..'z' || this in '0'..'9'
    }

    fun matchesUpperAlphNumOrUnderscore(input: String): Boolean {
        if (input.isEmpty()) return false

        for (char in input) {
            if (!char.isUpperAlphanumeric() && char != '_') {
                return false
            }
        }
        return true
    }

    private fun Char.isUpperAlphanumeric(): Boolean {
        return this in 'A'..'Z' || this in '0'..'9'
    }

    fun matchStringVarName(input: String): Boolean {
        if (
            input.isEmpty()
            || !input.startsWith("\${")
        ) return false

        var index = 2 // "${" の次の文字からチェックを開始

        // 英数字またはアンダースコアのチェック
        while (index < input.length - 1) {
            val char = input[index]
            if (!char.isAlphanumeric() && char != '_') {
                return false
            }
            index++
        }

        // 最後の文字が `}` であるかどうかをチェック
        return index > 2 && index == input.length - 1 && input[index] == '}'
    }

    fun matchStringVarBodyAlphaNum(input: String): Boolean {
        if (input.isEmpty()) return false

        for (char in input) {
            if (!char.isAlphanumeric() && char != '_') {
                return false
            }
        }
        return true
    }

    fun findAllVarMark(input: String): Sequence<String> {
        var result = sequenceOf<String>()
        var index = 0

        while (index < input.length) {
            if (input[index] == '$' && index + 1 < input.length && input[index + 1] == '{') {
                // バックスラッシュのチェック
                if (index > 0 && input[index - 1] == '\\') {
                    index += 2
                    continue
                }

                var endIndex = index + 2
                while (
                    endIndex < input.length
                    && (input[endIndex].isAlphanumeric()
                            || input[endIndex] == '_')
                ) {
                    endIndex++
                }

                if (endIndex < input.length && input[endIndex] == '}') {
                    result += sequenceOf(input.substring(index, endIndex + 1))
                    index = endIndex + 1
                    continue
                }
            }
            index++
        }
        return result
    }
}