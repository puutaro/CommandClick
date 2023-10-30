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
        val srcPath = BroadCastIntentExtraForHtml.SCR_PATH.scheme
        editSiteMap.get(srcPath).let {
            editSiteIntent.putExtra(
                srcPath,
                it
            )
        }
        val onClickSortSchema = BroadCastIntentExtraForHtml.ON_CLICK_SORT.scheme
        editSiteMap.get(onClickSortSchema).let {
            editSiteIntent.putExtra(
                onClickSortSchema,
                it
            )
        }
        val onSortableJsSchema = BroadCastIntentExtraForHtml.ON_SORTABLE_JS.scheme
        editSiteMap.get(onSortableJsSchema).let {
            editSiteIntent.putExtra(
                onSortableJsSchema,
                it
            )
        }
        val onClickUrlSchema = BroadCastIntentExtraForHtml.ON_CLICK_URL.scheme
        editSiteMap.get(onClickUrlSchema).let {
            editSiteIntent.putExtra(
                onClickUrlSchema,
                it
            )
        }
        val onDialogSchema = BroadCastIntentExtraForHtml.ON_DIALOG.scheme
        editSiteMap.get(onDialogSchema).let {
            editSiteIntent.putExtra(
                onDialogSchema,
                it
            )
        }
        val extraJsPathListSchema = BroadCastIntentExtraForHtml.EXTRA_JS_PATH_LIST.scheme
        editSiteMap.get(extraJsPathListSchema).let {
            editSiteIntent.putExtra(
                extraJsPathListSchema,
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
