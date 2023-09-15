package com.puutaro.commandclick.common.variable

import android.os.Environment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.long_click.lib.UbuntuSetUp

object UbuntuSetPath {

    val genRootDir = "/data/user/0/com.puutaro.commandclick/files/ubuntu-gen"
    val ubuntuTarGzPath = "${genRootDir}/ubuntu.tar.gz"
    val rootfsTarGzName = "rootfs.tar.gz"
    val downlaodDirPath =
        Environment.getExternalStorageDirectory().absolutePath +
                "/${Environment.DIRECTORY_DOWNLOADS}"
    val downloadRootfsTarGzPath = "${downlaodDirPath}/${rootfsTarGzName}"
}