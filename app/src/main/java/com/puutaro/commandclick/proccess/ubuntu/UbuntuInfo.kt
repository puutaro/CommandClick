package com.puutaro.commandclick.proccess.ubuntu

import com.puutaro.commandclick.BuildConfig


object UbuntuInfo {
    val user = "cmdclick"

    private const val devFalseInRelease = false
    private val createImageSwitchOffInRelease = CreateImageSwitch.off.name
    private val buildConfigDebug = BuildConfig.DEBUG

    //  for development
    val onForDev = if(
        buildConfigDebug
    ) false
    else devFalseInRelease

    val createImageSwitch = if(
        buildConfigDebug
    ) CreateImageSwitch.off.name
    else createImageSwitchOffInRelease

    val arm64UbuntuRootfsUrl =
        decideArm64UbuntuRootfsUrl(createImageSwitch)

    private fun decideArm64UbuntuRootfsUrl(
        imageSwitch: String
    ): String {
        return when(imageSwitch){
            CreateImageSwitch.on.name
            -> "https://partner-images.canonical.com/core/jammy/" +
                    "current/ubuntu-jammy-core-cloudimg-arm64-root.tar.gz"
            else
            -> "https://github.com/puutaro/CommandClick-Linux/releases/download/v1.1.2/rootfs.tar.gz"
        }
    }

    private enum class CreateImageSwitch {
        on,
        off
    }
}