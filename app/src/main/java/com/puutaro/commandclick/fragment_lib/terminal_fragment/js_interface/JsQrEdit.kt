package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.content.Intent
import android.webkit.JavascriptInterface
import com.puutaro.commandclick.common.variable.edit.EditTextSupportViewName
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer
import com.puutaro.commandclick.proccess.qr.FreeTextKey
import com.puutaro.commandclick.proccess.qr.QrContents
import com.puutaro.commandclick.proccess.qr.QrSchema
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CmdClickMap
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.editor.EditorByEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class JsQrEdit(
    private val terminalFragment: TerminalFragment
) {
    private val context = terminalFragment.context

    @JavascriptInterface
    fun create(
        qrConFilePath: String,
        qrPrefixType: String,
        broadcastIntentMapStr: String
    ) {
        val broadcastIntent = createBroadcastIntentForQr(
            broadcastIntentMapStr,
        )
        val qrCon = QrContents.makeFromMap(
            qrPrefixType,
            emptyMap()
        )
        val qrMap = QrSchema.makeQrMapFromCon(
            qrCon
        )
        editAndSaveHandler(
            qrConFilePath,
            qrCon,
            qrMap,
            broadcastIntent
        )
    }

    @JavascriptInterface
    fun edit(
        qrConFilePath: String,
        broadcastIntentMapStr: String,
    ) {
        val broadcastIntent = createBroadcastIntentForQr(
            broadcastIntentMapStr,
        )
        val fannelPathObj = File(qrConFilePath)
        val parentDirPath = fannelPathObj.parent
            ?: return
        val qrConFileName = fannelPathObj.name
        val qrConWithNewLine = ReadText(
            parentDirPath,
            qrConFileName
        ).readText()
        val qrMap = QrSchema.makeQrMapFromCon(
            qrConWithNewLine
        )
        editAndSaveHandler(
            qrConFilePath,
            qrConWithNewLine,
            qrMap,
            broadcastIntent
        )
    }

    private fun editAndSaveHandler(
        qrConFilePath: String,
        qrConWithNewLine: String,
        qrMap: Map<String, String>,
        broadcastIntent: Intent?,
    ){
        val qrConFilePathObj = File(qrConFilePath)
        val parentDirPath = qrConFilePathObj.parent
            ?: return
        val qrConFileName = qrConFilePathObj.name
        val freeTextKeyName = FreeTextKey.FREE_TEXT.key
        val isFreeText = qrMap.containsKey(freeTextKeyName)
        when(isFreeText){
            true ->
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        EditorByEditText.byEditText(
                            terminalFragment,
                            parentDirPath,
                            qrConFileName,
                            qrConWithNewLine,
                            broadcastIntent
                        )
                    }
                }
            else -> {
                editAndSaveByFormDialog(
                    qrConFilePath,
                    qrConWithNewLine,
                    qrMap
                )
                context?.sendBroadcast(broadcastIntent)
            }
        }
    }


    private fun editAndSaveByFormDialog(
        qrConFilePath: String,
        qrConWithNewLine: String,
        qrMap: Map<String, String>
    ) {
        val qrConFilePathObj = File(qrConFilePath)
        val parentDirPath = qrConFilePathObj.parent
            ?: return
        val qrConFileName = qrConFilePathObj.name
        val fannelDirPath = CcPathTool.getMainFannelDirPath(qrConFilePath)
        val listSaveDirPath = "${fannelDirPath}/cmdclickQrList"
        val resultKeyValueConSrc = makeResultKeyValueConSrc(
            qrConFilePath,
            qrMap,
            listSaveDirPath,
        )
        if(
            resultKeyValueConSrc.isEmpty()
        ) return
        val resultKeyValueCon =
            resultKeyValueConSrc.replace(
                "\n",
                "\t"
            )

        val updateQrMap = CmdClickMap.createMap(
            resultKeyValueCon,
            "\t"
        ).toMap()
        val jsListSelect = JsListSelect(terminalFragment)
        updateQrMap.keys.forEach {
            val listPath = "${listSaveDirPath}/${it}List.txt"
            val value = updateQrMap.get(it) ?: return@forEach
            jsListSelect.updateListFileCon(
                listPath,
                value
            )
        }
        val updateQrCon = QrContents.makeFromMap(
            qrConWithNewLine,
            updateQrMap
        )
        if(
            updateQrCon.isEmpty()
        ) return
        FileSystems.writeFile(
            parentDirPath,
            qrConFileName,
            updateQrCon
        )
    }

    private fun makeResultKeyValueConSrc(
        qrConFilePath: String,
        qrMap: Map<String, String>,
        listSaveDirPath: String,
    ): String {
        val qrConFileName = File(qrConFilePath).name
        val valLabelMacro = EditTextSupportViewName.VARIABLE_LABEL.str
        val txtEmphasisMacro = EditTextSupportViewName.EDIT_TEXT_EMPHASIS.str
        val listConSBMacro = EditTextSupportViewName.EDITABLE_LIST_CONTENTS_SELECT_BOX.str
        val listConListPathKey = ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
        val listConLimitNumKey = ListContentsSelectSpinnerViewProducer.ListContentsEditKey.limitNum.name
        val listConInitMark = ListContentsSelectSpinnerViewProducer.ListContentsEditKey.initMark.name
        val setVariableTypes = qrMap.keys.map {
            val listPath = "${listSaveDirPath}/${it}List.txt"
            val valNameAndMacro = listOf(
                it,
                valLabelMacro,
                txtEmphasisMacro,
                listConSBMacro
            ).joinToString(":")
            val valValueListStr = listOf(
                "label=this",
                "${listConListPathKey}=${listPath}!${listConLimitNumKey}=20!${listConInitMark}=DELETE"
            ).joinToString("|")
            "${valNameAndMacro}=${valValueListStr}"
        }.joinToString("\t")
        val targetVariables = qrMap.keys.map {
            "${it}=${qrMap.get(it)}"
        }.joinToString("\t")
        return JsDialog(terminalFragment).formDialog(
            "Edit: ${qrConFileName}",
            setVariableTypes,
            targetVariables,
        )
    }

    private fun createBroadcastIntentForQr(
        broadcastIntentMapStr: String,
    ): Intent? {
        val broadcastMap = CmdClickMap.createMap(
            broadcastIntentMapStr,
            "\n"
        ).toMap()
        return BroadcastSender.createBroadcastIntent(
            broadcastMap.get(BroadcastIntentMap.action.name),
            broadcastMap.get(BroadcastIntentMap.extras.name),
            "|"
        )
    }
}

private enum class BroadcastIntentMap {
    action,
    extras,
}