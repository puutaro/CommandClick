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
                || File(targetFileName).isFile
        return okPrefix
                && okSuffix
                && isFile
    }

    fun isFilterByDir(
        targetDirName: String,
        dirPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        separator: String
    ): Boolean {
        val okPrefix = judgeByPrefix(
            targetDirName,
            filterPrefixListCon,
            separator
        )
        val okSuffix =  judgeBySuffix(
            targetDirName,
            filterSuffixListCon,
            separator
        )
        val isDir = File(
            "${dirPath}/$targetDirName"
        ).isDirectory
                || File(targetDirName).isDirectory
        return okPrefix
                && okSuffix
                && isDir
    }

    private fun judgeByPrefix(
        targetStr: String,
        filterPrefixListCon: String,
        separator: String,
    ): Boolean {
        val compareFileOrDirName = makeNameForComparePrefix(
            targetStr,
        )
        return filterPrefixListCon.split(separator).any {
            compareFileOrDirName.startsWith(it)
        }
    }

    private fun judgeBySuffix(
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

    private fun makeNameForComparePrefix(
        name: String,
    ): String {
        val fileOrDirName = File(name).name
        val isFileOrDirName =
            fileOrDirName == name
        if(
            isFileOrDirName
        ) return name
        return fileOrDirName

    }
}