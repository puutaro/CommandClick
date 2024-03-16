package com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs

import android.content.Intent
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.service.GitCloneService

object SyncFannelRepo {
    fun sync(
        editFragment: EditFragment
    ) {
        val context = editFragment.context
            ?: return
        val intent = Intent(
            context,
            GitCloneService::class.java
        )
        context.startForegroundService(intent)
    }
}