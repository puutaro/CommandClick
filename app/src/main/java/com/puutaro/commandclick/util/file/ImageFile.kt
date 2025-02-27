package com.puutaro.commandclick.util.file

import com.puutaro.commandclick.fragment_lib.command_index_fragment.UrlImageDownloader
import java.io.File

object ImageFile {

    fun isImageFile(
        wallImageFilePath: String
    ): Boolean {
        val imageFileExtendList = listOf(".jpeg", ".jpg", ".png")
        return imageFileExtendList.any {
                imageFileExtend ->
            wallImageFilePath.endsWith(imageFileExtend)
        }
    }
}