package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

import com.blankj.utilcode.util.ToastUtils
import java.io.File

object CcFilterTool {

    private const val noExtend = "NoExtend"

    fun isFilterByStr(
        targetFileNameOrPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        onFilterToast: Boolean,
        separator: String,
    ): Boolean {
        val okPrefix = judgeByPrefix(
            targetFileNameOrPath,
            filterPrefixListCon,
            separator
        )
        val targetFileName = File(targetFileNameOrPath).name
        prefixFilterToast(
            okPrefix,
            onFilterToast,
            filterPrefixListCon,
            targetFileName,
        )
        val okSuffix = judgeBySuffix(
            targetFileNameOrPath,
            filterSuffixListCon,
            separator
        )
        suffixFilterToast(
            okSuffix,
            onFilterToast,
            filterSuffixListCon,
            targetFileName,
        )
        return okPrefix
                && okSuffix
    }

    fun isFilterByFile(
        targetFileNameOrPath: String,
        dirPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        onFilterToast: Boolean,
        separator: String,
    ): Boolean {
        val okPrefix = judgeByPrefix(
            targetFileNameOrPath,
            filterPrefixListCon,
            separator
        )
        val targetFileName = File(targetFileNameOrPath).name
        prefixFilterToast(
            okPrefix,
            onFilterToast,
            filterPrefixListCon,
            targetFileName,
        )
        val okSuffix = judgeBySuffix(
            targetFileNameOrPath,
            filterSuffixListCon,
            separator
        )
        suffixFilterToast(
            okSuffix,
            onFilterToast,
            filterSuffixListCon,
            targetFileName,
        )
        val isFile = File("${dirPath}/$targetFileNameOrPath").isFile
                || File(targetFileNameOrPath).isFile
        val onFileNotFoundToast = !isFile && onFilterToast
        if(onFileNotFoundToast){
            ToastUtils.showShort("File not found: ${targetFileNameOrPath}")
        }
        return okPrefix
                && okSuffix
                && isFile
    }

    fun isFilterByDir(
        targetDirNameOrPath: String,
        dirPath: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        onFilterToast: Boolean,
        separator: String
    ): Boolean {
        val okPrefix = judgeByPrefix(
            targetDirNameOrPath,
            filterPrefixListCon,
            separator
        )
        val targetDirName = File(targetDirNameOrPath).name
        prefixFilterToast(
            okPrefix,
            onFilterToast,
            filterPrefixListCon,
            targetDirName,
        )
        val okSuffix =  judgeBySuffix(
            targetDirNameOrPath,
            filterSuffixListCon,
            separator
        )
        suffixFilterToast(
            okSuffix,
            onFilterToast,
            filterSuffixListCon,
            targetDirName,
        )
        val isDir = File(
            "${dirPath}/$targetDirNameOrPath"
        ).isDirectory
                || File(targetDirNameOrPath).isDirectory
        val onDirNotFoundToast = !isDir && onFilterToast
        if(onDirNotFoundToast){
            ToastUtils.showShort("Dir not found: ${targetDirNameOrPath}")
        }
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

    private fun prefixFilterToast(
        okPrefix: Boolean,
        onFilterToast: Boolean,
        filterPrefixListCon: String,
        targetDirOrFileName: String,
    ){
        val onToast = okPrefix || !onFilterToast
        if(
            onToast
        ) return
        ToastUtils.showShort("Prefix must be ${filterPrefixListCon}: ${targetDirOrFileName}")
    }

    private fun suffixFilterToast(
        okSuffix: Boolean,
        onFilterToast: Boolean,
        filterSuffixListCon: String,
        targetDirName: String,
    ){
        val onToast = okSuffix || !onFilterToast
        if(
            onToast
        ) return
        ToastUtils.showShort("Suffix must be ${filterSuffixListCon}: ${targetDirName}")
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