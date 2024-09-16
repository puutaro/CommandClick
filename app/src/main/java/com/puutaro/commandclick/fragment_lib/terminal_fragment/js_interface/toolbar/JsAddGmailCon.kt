package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.toolbar

import android.webkit.JavascriptInterface
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import java.lang.ref.WeakReference

class JsAddGmailCon(
    private val terminalFragmentRef: WeakReference<TerminalFragment,>
) {
    private val keySeparator = '|'

    @JavascriptInterface
    fun add(
        gmailAd: String,
        extraMapCon: String,
    ){
        /*
        Add gmail contents from url

        ### Corresponding macro

        -> [ADD_GMAIL_CON](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#add_gmail_con)

        ### gmailAd arg

        Target gmail url address

        ### extraMapCon arg

        -> [Args for ADD_GMAIL_CON macro](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_action/js_action_macro_for_toolbar.md#args-for-add_gmail_con)

        ### Example

        ```js.js
        var=runAddGmailAdCon
            ?func=jsAddGmailCon.add
            ?args=
                &gmailAd="http://gmail.com/~"
                &extraMapCon=`
                    urlConSaveParentDirPath=${cmdTtsPlayerSaveUrlConDirPath}
                    |compSuffix=${TXT_SUFFIX}
                `
        ```

        */

        val terminalFragment = terminalFragmentRef.get()
            ?: return
        if(
            gmailAd.isEmpty()
        ) return
        val extraMap = CmdClickMap.createMap(
            extraMapCon,
            keySeparator
        ).toMap()
        val urlConSaveParentDirPath = extraMap.get(
            AddGmailConKey.URL_CON_SAVE_PARENT_DIR_PATH.key
        ) ?: String()
        val compSuffix = extraMap.get(
            AddGmailConKey.COMP_SUFFIX.key
        ) ?: String()
        ExecJsLoad.execExternalJs(
            terminalFragment,
//            UsePath.cmdclickDefaultAppDirPath,
            UsePath.saveGmailConDialogFannelName,
            listOf(
                gmailAd,
                urlConSaveParentDirPath,
                compSuffix,
            ),
        )
    }
}

enum class AddGmailConKey(
    val key: String,
) {
    URL_CON_SAVE_PARENT_DIR_PATH(AddUrlConKey.URL_CON_SAVE_PARENT_DIR_PATH.key),
    COMP_SUFFIX(AddUrlConKey.COMP_SUFFIX.key),
}