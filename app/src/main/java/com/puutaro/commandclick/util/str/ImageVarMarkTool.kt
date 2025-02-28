package com.puutaro.commandclick.util.str

import android.graphics.Bitmap
import com.puutaro.commandclick.proccess.edit.image_action.ImageActionKeyManager.VarPrefix

object ImageVarMarkTool {

    fun matchBitmapVarName(
        bitmapVarName: String,
    ): Boolean {
        val bitmapVarRegex = Regex("^#[{][a-zA-Z0-9_]+[}]$")
        return bitmapVarRegex.matches(bitmapVarName)
                && !bitmapVarName.startsWith(VarPrefix.RUN.prefix)
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
}