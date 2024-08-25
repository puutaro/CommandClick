package com.puutaro.commandclick.util

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.fannel.SystemFannel
import com.puutaro.commandclick.common.variable.path.UsePath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object FactFannel {

    fun isFactFannel(
        fannelName: String
    ): Boolean {
        return File(
            UsePath.cmdclickDefaultAppDirPath,
            convertToFactFannelName(fannelName)
        ).isFile
    }
    fun convertToFactFannelName(
        fannelName: String
    ): String {
        return when (
            fannelName == SystemFannel.home
        ) {
            true -> SystemFannel.preference
            else -> fannelName
        }
    }

    fun creatingToast(){
        CoroutineScope(Dispatchers.Main).launch {
            ToastUtils.showShort("createing..")
        }
    }
}