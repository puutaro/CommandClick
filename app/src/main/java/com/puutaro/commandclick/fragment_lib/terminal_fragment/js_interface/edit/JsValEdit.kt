package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import android.widget.Toast
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.dialog.JsDialog
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.ReadText

class JsValEdit(
    private val terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context
    private val okReturnCode = "0"
    private val cancelReturnCode = "1"
    private val readSharePreffernceMap = terminalFragment.readSharePreferenceMap


    @JavascriptInterface
    fun editAndSaveCmdVar(
        title: String,
        fContents: String,
        setVariableTypes: String,
        targetVariables: String,
    ): String {
        return try {
            execEditAndSaveCmdVar(
                title,
                fContents,
                setVariableTypes,
                targetVariables,
            )
        } catch (e: Exception){
            Toast.makeText(
                context,
                "$e",
                Toast.LENGTH_SHORT
            ).show()
            LogSystems.stdErr(
                context,
                "$e"
            )
            cancelReturnCode
        }
    }

    @JavascriptInterface
    fun registerFannelConChange(
        changedFannelCon: String
    ){
        val listener = context as? TerminalFragment.OnEditFannelContentsListUpdateListenerForTerm
        listener?.onEditFannelContentsListUpdateForTerm(
            readSharePreffernceMap ,
            changedFannelCon.split("\n")
        )
    }

    private fun execEditAndSaveCmdVar(
        title: String,
        fannelPath: String,
        setVariableTypes: String,
        targetVariables: String,
    ): String {
        val resultKeyValueConSrc = JsDialog(terminalFragment).formDialog(
            title,
            setVariableTypes,
            targetVariables,
        )
        if(
            resultKeyValueConSrc.isEmpty()
        ) return cancelReturnCode
        val resultKeyValueCon =
            resultKeyValueConSrc.replace(
                "\n",
                "\t"
            )

        val variableMap = CmdClickMap.createMap(
            resultKeyValueCon,
            '\t'
        )
        val jsEdit = JsEdit(terminalFragment)
        variableMap.forEach {
            val varName = it.first
            val varValue = it.second
            jsEdit.updateEditText(
                varName,
                varValue
            )
        }
        val jsScript = JsScript(terminalFragment)
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