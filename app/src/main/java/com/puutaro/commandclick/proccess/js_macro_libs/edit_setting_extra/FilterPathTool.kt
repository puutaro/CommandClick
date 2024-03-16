package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

import java.io.File

object FilterPathTool {

    private const val noExtend = "NoExtend"

    fun isFilterByFile(
        targetFileName: String,
        dirPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        separator: String
    ): Boolean {
        val okPrefix = judgeByPrefix(
            targetFileName,
            filterPrefixListCon,
            separator
        )
        val okSuffix =  judgeBySuffix(
            targetFileName,
            filterSuffixListCon,
            separator
        )
        val isFile = File("${dirPath}/$targetFileName").isFile
        return okPrefix
                && okSuffix
                && isFile
    }

    fun isFilterByDir(
        targetFileName: String,
        dirPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        separator: String
    ): Boolean {
        val okPrefix = judgeByPrefix(
            targetFileName,
            filterPrefixListCon,
            separator
        )
        val okSuffix =  judgeBySuffix(
            targetFileName,
            filterSuffixListCon,
            separator
        )
        val isDir = File(
            "${dirPath}/$targetFileName"
        ).isDirectory
        return okPrefix
                && okSuffix
                && isDir
    }

    fun judgeByPrefix(
        targetStr: String,
        filterPrefixListCon: String,
        separator: String,
    ): Boolean {
        return filterPrefixListCon.split(separator).any {
            targetStr.startsWith(it)
        }
    }

    fun judgeBySuffix(
        targetStr: String,
        filterSuffixListCon: String,
        separator: String,
    ): Boolean {
        if(filterSuffixListCon != noExtend) {
            return filterSuffixListCon.split(separator).any {
                targetStr.endsWith(it)
            }
        }
        return !Regex("\\..*$").containsMatchIn(targetStr)
    }
}