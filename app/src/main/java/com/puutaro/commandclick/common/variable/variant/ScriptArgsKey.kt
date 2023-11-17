package com.puutaro.commandclick.common.variable.variant

import com.puutaro.commandclick.common.variable.path.UsePath

enum class ScriptArgs(
    val str: String,
    val dirName: String,
    val jsName: String,
) {
    ON_AUTO_EXEC(
        "onAutoExec",
        UsePath.systemExecJsDirName,
        "onAutoExec.js",
    ),
    URL_HISTORY_CLICK(
        "urlHistoryClick",
        UsePath.systemExecJsDirName,
        "urlHistoryClick.js",
    ),
    NO_ARG(
        String(),
        UsePath.systemExecJsDirName,
    "noArg.js",
    ),
    LONG_PRESS(
        "longPress",
        String(),
        String(),
    )
}