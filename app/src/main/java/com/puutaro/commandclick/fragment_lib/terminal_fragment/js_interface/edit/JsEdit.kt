package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.edit

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.QuoteTool
import java.lang.ref.WeakReference

class JsEdit(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
//    private val context = terminalFragment.context
//    private val activity = terminalFragment.activity
//    private val editViewModel: EditViewModel by terminalFragment.activityViewModels()

    @JavascriptInterface
    fun updateByVariable(
        fannelScriptPath: String,
        targetVariableName: String,
        updateVariableValue: String,
    ){
        val jsScript = JsScript(terminalFragmentRef)
//        updateEditText(
//            targetVariableName,
//            updateVariableValue
//        )
        val jsContents = ReadText(
            fannelScriptPath
        ).readText()
        val updateJsContents = jsScript.replaceCommandVariable(
            jsContents,
            "${targetVariableName}=${updateVariableValue}"
        )
        FileSystems.writeFile(
            fannelScriptPath,
            updateJsContents
        )
    }

//    @JavascriptInterface
//    fun updateEditText(
//        updateVariableName: String,
//        updateVariableValue: String
//    ){
//        val terminalFragment = terminalFragmentRef.get()
//            ?: return
//        val context = terminalFragment.context
//        val listener = context as? TerminalFragment.OnEditTextUpdateListenerForTermFragment
//        val editViewModel: EditViewModel by terminalFragment.activityViewModels()
//        val editTextId = editViewModel.variableNameToEditTextIdMap.get(updateVariableName)
//        listener?.onEditTextUpdateForTermFragment(
//            editTextId,
//            updateVariableValue
//        )
//    }

    @JavascriptInterface
    fun updateTextViewAndFannel_S(
        indexOrParentTagName: String,
        srcFragment: String,
        tagNameListCon: String,
        updateText: String,
        overrideTextMapCon: String,
        textPropertyMapCon: String,
        isSave: Boolean,
    ){
        val tagNameSeparator = '&'
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context ?: return
        val keySeparator = EditComponent.Template.keySeparator
        val overrideTextMap = CmdClickMap.createMap(
            overrideTextMapCon,
            keySeparator
        ).toMap()
        val textPropertyMap = CmdClickMap.createMap(
            textPropertyMapCon,
            keySeparator
        ).toMap()
        val listener = context as TerminalFragment.OnTextViewAndFannelUpdateListenerForTerm
        listener.onTextViewAndFannelForTermFragment(
            indexOrParentTagName,
            srcFragment,
            tagNameListCon.split(tagNameSeparator.toString()),
            updateText,
            overrideTextMap,
            textPropertyMap,
            isSave,
        )
    }

    @JavascriptInterface
    fun updateImageView_S(
        fannelPath: String,
        fannelState: String,
        indexOrParentTagName: String,
        srcFragment: String,
        tagNameListCon: String,
        imagePropertyMapCon: String,
        imageAcCon: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
            ?: return
        val keySeparator = EditComponent.Template.keySeparator
//        val imageMap = CmdClickMap.createMap(
//            imageAcCon,
//            keySeparator
//        ).toMap()
        val imagePropertyMap = CmdClickMap.createMap(
            imagePropertyMapCon,
            keySeparator
        ).toMap()
        val listener = context as TerminalFragment.OnImageViewUpdateListenerForTerm
        val tagNameSeparator = '&'
        listener.onImageViewUpdateForTerm(
            fannelPath,
            fannelState,
            indexOrParentTagName,
            srcFragment,
            tagNameListCon.split(tagNameSeparator.toString()),
            imagePropertyMap,
            imageAcCon
        )
    }

//    @JavascriptInterface
//    fun updateImageView_S_bk(
//        indexOrParentTagName: String,
//        srcFragment: String,
//        tagNameListCon: String,
//        imageMapCon: String,
//        imagePropertyMapCon: String,
//    ){
//        val terminalFragment = terminalFragmentRef.get()
//            ?: return
//        val context = terminalFragment.context
//            ?: return
//        val keySeparator = EditComponent.Template.keySeparator
//        val imageMap = CmdClickMap.createMap(
//            imageMapCon,
//            keySeparator
//        ).toMap()
//        val imagePropertyMap = CmdClickMap.createMap(
//            imagePropertyMapCon,
//            keySeparator
//        ).toMap()
//        val listener = context as TerminalFragment.OnImageViewUpdateListenerForTerm
//        val tagNameSeparator = '&'
//        listener.onImageViewUpdateForTerm(
//            indexOrParentTagName,
//            srcFragment,
//            tagNameListCon.split(tagNameSeparator.toString()),
//            imageMap,
//            imagePropertyMap,
//        )
//    }

    @JavascriptInterface
    fun updateFrameLayout_S(
        indexOrParentTagName: String,
        srcFragment: String,
        tagNameListCon: String,
        frameKeyPairListCon: String,
    ){
        val terminalFragment = terminalFragmentRef.get()
            ?: return
        val context = terminalFragment.context
            ?: return
        val keySeparator = EditComponent.Template.keySeparator
        val frameKeyPairList = CmdClickMap.createMap(
            frameKeyPairListCon,
            keySeparator
        )
        val listener = context as TerminalFragment.OnFrameLayoutUpdateListenerForTerm
        val tagNameSeparator = '&'
        listener.onFrameLayoutUpdateForTerm(
            indexOrParentTagName,
            srcFragment,
            tagNameListCon.split(tagNameSeparator.toString()),
            frameKeyPairList,
        )
    }

    @JavascriptInterface
    fun getSettingValue(
        targetVariableName: String,
        srcFragment: String,
    ): String {
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        val context = terminalFragment.context
        val editListRecyclerView = EditListRecyclerViewGetter.get(
            terminalFragment,
            srcFragment
        ) ?: return String()
        val editConstraintListAdapter =
            editListRecyclerView.adapter as EditConstraintListAdapter
        return editConstraintListAdapter.getCurrentSettingVals(
            context,
            targetVariableName
        )?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } ?: String()
    }

//    @JavascriptInterface
//    fun updateSpinner(
//        updateVariableName: String,
//        updateVariableValue: String
//    ){
//        val terminalFragment = terminalFragmentRef.get()
//            ?: return
//        val context = terminalFragment.context
//        val listener = context as? TerminalFragment.OnSpinnerUpdateListenerForTermFragment
//        val editViewModel: EditViewModel by terminalFragment.activityViewModels()
//        val editTextId =
//            editViewModel.variableNameToEditTextIdMap.get(updateVariableName)
//                ?:return
//        val currentSpinnerId = editTextId + EditTextSupportViewId.SPINNER.id
//        listener?.onSpinnerUpdateForTermFragment(
//            currentSpinnerId,
//            updateVariableValue
//        )
//    }
}