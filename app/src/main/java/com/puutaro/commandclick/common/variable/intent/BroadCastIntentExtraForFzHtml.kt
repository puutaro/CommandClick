package com.puutaro.commandclick.common.variable.intent

enum class BroadCastIntentExtraForFzHtml(
    val scheme: String
) {
    TEMPLATE_PROMPT_MESSAGE(
        "prompt"
    ),
    ITEM_CLICK_JAVASCRIPT(
        "item_click_js"
    ),
    ITEM_LONG_CLICK_JAVASCRIPT(
        "item_lclick_js"
    ),
    REAL_TIME_LIST_SET_JAVASCRIPT(
        "ls_set_js"
    ),
}