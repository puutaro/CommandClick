package com.puutaro.commandclick.proccess.history


object UrlHistoryPath {

    fun makeBase64TxtFileNameByUrl(
        url: String,
    ): String {
        return makePngNameFromUrl(url)
    }

    private fun makePngNameFromUrl(
        url: String
    ): String {
        return url.replace(
            Regex("[^a-zA-Z0-9_-]"),
            "_"
        ) + ".txt"
    }

}