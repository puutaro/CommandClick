package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentExtraForHtml
import com.puutaro.commandclick.common.variable.intent.BroadCastIntentScheme
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.CmdClickMap

class EditSiteBroadCast(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val keySeparator = "|"

    fun send(
        editPath: String,
        extraMapStr: String,
        filterCode: String,
    ){
        val editSiteIntent = Intent()
        editSiteIntent.action = BroadCastIntentScheme.HTML_LAUNCH.action
        editSiteIntent.putExtra(
            BroadCastIntentScheme.HTML_LAUNCH.scheme,
            editPath
        )
        val editSiteMap = CmdClickMap.createMap(
            extraMapStr,
            keySeparator
        ).toMap()
        editSiteMap.get(extraEditSiteKey.srcPath.name).let {
            editSiteIntent.putExtra(
                BroadCastIntentExtraForHtml.SCR_PATH.scheme,
                it
            )
        }
        editSiteMap.get(extraEditSiteKey.onClickSort.name).let {
            editSiteIntent.putExtra(
                BroadCastIntentExtraForHtml.ON_CLICK_SORT.scheme,
                it
            )
        }

        editSiteMap.get(extraEditSiteKey.onSortableJs.name).let {
            editSiteIntent.putExtra(
                BroadCastIntentExtraForHtml.ON_SORTABLE_JS.scheme,
                it
            )
        }

        editSiteMap.get(extraEditSiteKey.onClickUrl.name).let {
            editSiteIntent.putExtra(
                BroadCastIntentExtraForHtml.ON_CLICK_URL.scheme,
                it
            )
        }
        editSiteMap.get(extraEditSiteKey.onDialog.name).let {
            editSiteIntent.putExtra(
                BroadCastIntentExtraForHtml.ON_DIALOG.scheme,
                it
            )
        }
        editSiteIntent.putExtra(
            BroadCastIntentExtraForHtml.FILTER_CODE.scheme,
            filterCode
        )
        context?.sendBroadcast(editSiteIntent)
    }
}

private enum class extraEditSiteKey {
    srcPath,
    onClickSort,
    onSortableJs,
    onClickUrl,
    onDialog,
}
