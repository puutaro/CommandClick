package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText
import java.lang.ref.WeakReference

class JsValEdit(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {

//    private val context = terminalFragment.context
    private val okReturnCode = "0"
    private val cancelReturnCode = "1"
//    private val fannelInfoMap = terminalFragment.fannelInfoMap


    @JavascriptInterface
    fun editAndSaveCmdVar(
        title: String,
        fannelPath: String,
        setVariableTypes: String,
        targetVariables: String,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context
        val isOk = try {
            execEditAndSaveCmdVar(
                title,
                fannelPath,
                setVariableTypes,
                targetVariables,
            )
        } catch (e: Exception){
            ToastUtils.showShort(e.toString())
            LogSystems.stdErr(
                context,
                "$e"
            )
            cancelReturnCode
        }
        return isOk
    }

    @JavascriptInterface
    fun registerFannelConChange(
        changedFannelCon: String
    ){
        /*
        Register fannel con change to Edit fragment
        */
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
        val listener = context as? TerminalFragment.OnEditFannelContentsListUpdateListenerForTerm
        listener?.onEditFannelContentsListUpdateForTerm(
            terminalFragment.fannelInfoMap,
            changedFannelCon.split("\n")
        )
    }

    private fun execEditAndSaveCmdVar(
        title: String,
        fannelPath: String,
        setVariableTypes: String,
        targetVariables: String,
    ): String {
        val resultKeyValueCon = JsDialog(terminalFragmentRef).formDialog(
            title,
            setVariableTypes,
            targetVariables,
        )
        if(
            resultKeyValueCon.isEmpty()
        ) return cancelReturnCode

        val variableMap = CmdClickMap.createMap(
            resultKeyValueCon,
            '\n'
        )
        val jsEdit = JsEdit(terminalFragmentRef)
        variableMap.forEach {
            val varName = it.first
            val varValue = it.second
            jsEdit.updateEditText(
                varName,
                varValue
            )
        }
        val jsScript = JsScript(terminalFragmentRef)
        val fcon = ReadText(
            fannelPath
        ).readText()
        val replacedCon =  jsScript.replaceCommandVariable(
            fcon,
            resultKeyValueCon
        )
        registerFannelConChange(
            replacedCon
        )
        if(
            replacedCon.isEmpty()
        ) return okReturnCode
        FileSystems.writeFile(
            fannelPath,
            replacedCon
        )
        return okReturnCode
    }
}