package com.puutaro.commandclick.util.file

object ConfirmDialogAssets {

    const val confirmDialogDirPath = "${AssetsFileManager.resPngDirPath}/confirm_dialog"

    val xOPairPngToMakeTypeList = listOf(
        Pair(
            "${confirmDialogDirPath}/x3.png",
            "${confirmDialogDirPath}/o32.png"
        ) to ImageMakingType.PLANE,  //35
        Pair(
            "${confirmDialogDirPath}/x3.png",
            "${confirmDialogDirPath}/o32.png"
        ) to ImageMakingType.OPACITY,
    )

    enum class ImageMakingType {
        PLANE,
        OPACITY,
    }
}