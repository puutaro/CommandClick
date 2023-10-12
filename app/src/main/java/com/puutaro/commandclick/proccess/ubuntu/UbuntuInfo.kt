package com.puutaro.commandclick.proccess.ubuntu

object UbuntuInfo {
    val user = "cmdclick"
//  for development
    val onForDev = false

    val createImageSwitch = CreateImageSwitch.off.name
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
            -> "https://github.com/puutaro/CommandClick-Linux/releases/download/v1.1.1/rootfs.tar.gz"
        }
    }

    private enum class CreateImageSwitch {
        on,
        off
    }
}