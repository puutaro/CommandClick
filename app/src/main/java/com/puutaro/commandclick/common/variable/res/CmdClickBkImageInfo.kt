package com.puutaro.commandclick.common.variable.res

import com.puutaro.commandclick.common.variable.path.UsePath
import java.io.File

object CmdClickBkImageInfo {
    private val fannelBkDirPath = "${UsePath.cmdclickDefaultAppDirPath}/fannel_bk"

    enum class CmdClickBkImages(
        val file: File,
    ) {
        BLUE_HANABI(File(fannelBkDirPath, "blue_hanabi.jpg")),
        RECT_OERLAY(File(fannelBkDirPath, "rect_overlay1.png")),
        RECT_OERLAY3(File(fannelBkDirPath, "rect_overlay3.png")),
        HANABI(File(fannelBkDirPath, "hanabi.jpg")),
        JS_THUNAMI(File(fannelBkDirPath, "japan_tunami.jpg")),
        MOUNTAIN(File(fannelBkDirPath, "mountain.jpg"))
    }

    enum class CmdClickAutoCreateImage {
        AUTO_MATRIX_RECT,
        AUTO_RND_RECT,
    }

}