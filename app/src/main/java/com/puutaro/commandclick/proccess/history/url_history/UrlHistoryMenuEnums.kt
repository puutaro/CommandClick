package com.puutaro.commandclick.proccess.history.url_history

private val mainMenuGroupId = 70000

enum class UrlHistoryMenuEnums(
    val groupId: Int,
    val itemId: Int,
    val order: Int,
    val itemName: String
) {
    DELETE(mainMenuGroupId, 70100, 1, "delete"),
    COPY_URL(mainMenuGroupId, 70200, 2, "copy_url"),
}