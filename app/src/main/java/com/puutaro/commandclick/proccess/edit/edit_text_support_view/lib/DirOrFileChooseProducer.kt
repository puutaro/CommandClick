package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.edit.SetVariableTypeColumn
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool

object DirOrFileChooseProducer {

//    fun make(
//        editFragment: EditFragment,
//        editParameters: EditParameters,
//        onDirectoryPick: Boolean,
//        insertEditText: EditText,
//        currentComponentIndex: Int,
//        weight: Float,
//    ): Button {
//        val context = editFragment.context
//        val chooseButtonStr = when(onDirectoryPick) {
//            true -> "dir"
//            else -> "file"
//        }
//        val insertButtonView = Button(context)
//        insertButtonView.text = chooseButtonStr
//        ButtonSetter.set(
//            context,
//            insertButtonView,
//            mapOf()
//        )
//        val chooserMap = getChooserMap(
//            editParameters,
//            currentComponentIndex
//        )
//        val fannelName = FannelInfoTool.getCurrentFannelName(
//            editFragment.fannelInfoMap
//        )
//        val currentVariableName =
//            editParameters.currentVariableName
//                ?: String()
//        insertButtonView.setOnClickListener { view ->
//            val listener = context as? EditFragment.OnFileChooserListenerForEdit
//            listener?.onFileChooserListenerForEdit(
//                onDirectoryPick,
//                insertEditText,
//                chooserMap,
//                fannelName,
//                currentVariableName
//            )
//        }
//        val insertButtonViewParam = LinearLayoutCompat.LayoutParams(
//            0,
//            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//        )
//        insertButtonViewParam.weight = weight
//        insertButtonView.layoutParams = insertButtonViewParam
//        return insertButtonView
//    }

//    private fun getChooserMap(
//        editParameters: EditParameters,
//        currentComponentIndex: Int
//    ): Map<String, String>? {
//        val currentSetVariableMap = editParameters.setVariableMap
//        val fannelInfoMap = editParameters.fannelInfoMap
////        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
////            fannelInfoMap
////        )
//        val currentScriptName = FannelInfoTool.getCurrentFannelName(
//            fannelInfoMap
//        )
//        return currentSetVariableMap?.get(
//            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
//        )?.split('|')
//            ?.getOrNull(currentComponentIndex)
//            ?.let {
//                SetReplaceVariabler.execReplaceByReplaceVariables(
//                    it,
//                    editParameters.setReplaceVariableMap,
////                    currentAppDirPath,
//                    currentScriptName,
//                )
//            }?.let {
//                CmdClickMap.createMap(
//                    it,
//                    '?'
//                )
//            }?.toMap()
//    }
}