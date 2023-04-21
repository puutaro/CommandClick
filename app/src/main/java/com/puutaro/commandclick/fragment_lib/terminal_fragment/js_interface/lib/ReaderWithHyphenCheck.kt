package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText

object ReaderWithHyphenCheck {
    fun read(
        searchListDirPath: String,
        searchListFilePath: String,
    ): List<String> {
        FileSystems.createDirs(searchListDirPath)
        return ReadText(
            searchListDirPath,
            searchListFilePath
        ).textToList()
    }
}