package com.puutaro.commandclick.util.str

import android.graphics.Bitmap
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager.VarPrefix

object ImageVarMarkTool {

    fun matchBitmapVarName2(
        bitmapVarName: String,
    ): Boolean {
//        val bitmapVarRegex = Regex("^#[{][a-zA-Z0-9_]+[}]$")
        return matchBitmapVarName(bitmapVarName)
        //bitmapVarRegex.matches(bitmapVarName)
                && !bitmapVarName.startsWith(VarPrefix.RUN.prefix)
    }

    fun matchBitmapVarName(input: String): Boolean {
        if (
            input.isEmpty()
            || !input.startsWith("#{")
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

    private fun Char.isAlphanumeric(): Boolean {
        return this in 'A'..'Z' || this in 'a'..'z' || this in '0'..'9'
    }

    fun convertBitmapKey(bitmapVar: String): String {
        return bitmapVar
            .removePrefix("#{")
            .removeSuffix("}")
    }

    fun extractValNameListByBackslashEscape(
        con: String
    ): Sequence<String> {
        val varRegex = "(?<!\\\\)[#][{][a-zA-Z0-9_]+[}]".toRegex()
        val varNameSec = varRegex.findAll(con).map {
            convertBitmapKey(it.value)
        }.sorted().distinct()
        return varNameSec
    }

    fun extractValNameList(
        con: String
    ): Sequence<String> {
        val varRegex = "[#][{][a-zA-Z0-9_]+[}]".toRegex()
        val varNameSec = varRegex.findAll(con).map {
            convertBitmapKey(it.value)
        }.sorted().distinct()
        return varNameSec
    }


    fun filterByUseVarMark2(
        targetMap: Map<String, Bitmap?>?,
        valNameSeq: Sequence<String>
    ): Map<String, Bitmap?>? {
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
        targetMap: Map<String, Bitmap?>?,
        con: String,
    ): Map<String, Bitmap?>? {
        val filteredMap = targetMap?.filter {
            con.contains("#{${it.key}}")
        }
        return filteredMap
    }

    fun findAllVarMark(input: String): Sequence<String> {
        var result: Sequence<String> = emptySequence()
        var index = 0

        while (index < input.length) {
            if (input[index] == '#' && index + 1 < input.length && input[index + 1] == '{') {
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