package com.puutaro.commandclick.common.variable.intent

enum class BroadCastIntentExtraForHtml(
    val scheme: String
) {
    SCR_PATH(
    "srcPath"
    ),
    ON_CLICK_SORT(
    "onClickSort"
    ),
    ON_SORTABLE_JS(
        "onSortableJs"
    ),
    ON_CLICK_URL(
        "onClickUrl"
    ),
    FILTER_CODE(
        "filterCode"
    ),
    ON_DIALOG(
        "onDialog"
    ),
    EXTRA_JS_PATH_LIST(
        "extraJsPathList"
    )
}