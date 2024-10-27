package com.puutaro.commandclick.util.file

import java.io.File


object PromptListImageSet {
    const val promptDotImageSet = "${AssetsFileManager.resPngDirPath}/prompt_dot_image_set"


    fun isImageSetOk(
        dirPath: String
    ): Boolean {
        val imageSetFileList = getFirstDotStormPathList(
            dirPath
        ) + listOf(
            getMainImagePath(dirPath)
        ) + getStrImagePathList(
            dirPath
        )
        imageSetFileList.forEach {
            if(
                !File(it).isFile
            ) return false
        }
        return true
    }

    fun getFirstDotStormPathList(
        dirPath: String
    ): List<String> {
        return firstDotStormImagePathList.map {
            File(dirPath, File(it).name).absolutePath
        }
    }

    fun getStrImagePathList(
        dirPath: String
    ): List<String> {
        return strImagePathList.map {
            File(dirPath, File(it).name).absolutePath
        }
    }

    fun getMainImagePath(
        dirPath: String
    ): String {
        return File(dirPath, File(mainImagePath).name).absolutePath

    }

    val firstDotStormImagePathList = (1..4).mapIndexed {
        index, nameSrc ->
        AssetsFileManager.concatAssetsPath(
            listOf(
                promptDotImageSet,
            "firstDotStorm${index + 1}.png"
            )
        )
    }

    val mainImagePath =
        AssetsFileManager.concatAssetsPath(
            listOf(
                promptDotImageSet,
                "mainImage.png"
            )
        )

    val strImagePathList =  (1..4).mapIndexed {
            index, nameSrc ->
        AssetsFileManager.concatAssetsPath(
            listOf(
                promptDotImageSet,
                "strImage${index + 1}.png"
            )
        )
    }
}