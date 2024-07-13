package com.puutaro.commandclick.proccess.ubuntu


object UbuntuInfo {
    const val user = "cmdclick"

//    private const val devFalseInRelease = false
    private val createImageSwitchForRelease = CreateImageSwitch.OFF.name
//    private val buildConfigDebug = BuildConfig.DEBUG
//
    //  for development
//    val onForDev = devFalseInRelease
//        if(
//        buildConfigDebug
//    ) false
//    else devFalseInRelease

    val createImageSwitch = createImageSwitchForRelease
//        if(
//        buildConfigDebug
//    ) CreateImageSwitch.on.name
//    else createImageSwitchForRelease

    val rootfsTarGzUrl = decideArm64UbuntuRootfsUrl(createImageSwitch)
//        "https://raw.githubusercontent.com/puutaro/CommandClick-Linux/master/rootfs_list/list.txt"

//    val arm64UbuntuRootfsUrl =
//        decideArm64UbuntuRootfsUrl(createImageSwitch)

    private fun decideArm64UbuntuRootfsUrl(
        imageSwitch: String
    ): String {
        return when(imageSwitch){
            CreateImageSwitch.ON.name
            -> "https://partner-images.canonical.com/core/jammy/" +
                    "current/ubuntu-jammy-core-cloudimg-arm64-root.tar.gz"
            else
            -> "https://github.com/puutaro/CommandClick-Linux/releases/download/v1.1.6/rootfs.tar.gz"
        }
    }

    enum class CreateImageSwitch {
        ON,
        OFF
    }
}