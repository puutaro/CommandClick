package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib

import android.content.Intent
import com.puutaro.commandclick.common.variable.intent.extra.BroadCastIntentExtraForHtml
import com.puutaro.commandclick.common.variable.intent.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.map.CmdClickMap

class EditSiteBroadCast(
    terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context
    private val keySeparator = '|'

    fun send(
        editPath: String,
        extraMapStr: String,
        filterCode: String,
    ){
        val editSiteIntent = Intent()
        editSiteIntent.action = BroadCastIntentSchemeTerm.HTML_LAUNCH.action
        editSiteIntent.putExtra(
            BroadCastIntentSchemeTerm.HTML_LAUNCH.scheme,
            editPath
        )
        val editSiteMap = CmdClickMap.createMap(
            extraMapStr,
            keySeparator
        ).toMap()
        val srcPathSchema = BroadCastIntentExtraForHtml.SCR_PATH.scheme
        editSiteMap.get(srcPathSchema).let {
            editSiteIntent.putExtra(
                srcPathSchema,
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
        val latestUrlTitleFilterCodeShema = BroadCastIntentExtraForHtml.LATST_URL_TITLE_FILTER_CODE.scheme
        editSiteMap.get(latestUrlTitleFilterCodeShema).let {
            editSiteIntent.putExtra(
                latestUrlTitleFilterCodeShema,
                it
            )
        }
        val extraLabelSchema = BroadCastIntentExtraForHtml.EXTRA_LABEL.scheme
        editSiteMap.get(extraLabelSchema)?.let {
            editSiteIntent.putExtra(
                extraLabelSchema,
                it
            )
        }
        context?.sendBroadcast(editSiteIntent)
    }
}
