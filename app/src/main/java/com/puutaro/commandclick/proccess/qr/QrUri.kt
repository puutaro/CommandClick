package com.puutaro.commandclick.proccess.qr

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.variables.QrLaunchType
import com.puutaro.commandclick.common.variable.variables.QrSeparator
import com.puutaro.commandclick.util.BroadCastIntent
import com.puutaro.commandclick.util.ScriptPreWordReplacer

object QrUri {

    private val jsDescSeparator = QrSeparator.sepalator.str

    fun load(
        fragment: Fragment,
        currentAppDirPath: String,
        loadConSrc: String
    ){
        val jsDesc = QrLaunchType.JsDesc.prefix
        val replaceLoadUrlSrc =
            ScriptPreWordReplacer.replaceForQr(
                loadConSrc,
                currentAppDirPath
            )
        val loadUrl =
            if(
                replaceLoadUrlSrc.trim().startsWith(jsDesc)
            ) replaceLoadUrlSrc.split(jsDescSeparator).filterIndexed {
                    index, _ -> index > 0
            }.joinToString(jsDescSeparator)
            else replaceLoadUrlSrc
        BroadCastIntent.sendUrlCon(
            fragment,
            loadUrl.trim()
        )
    }
}