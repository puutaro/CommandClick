package com.puutaro.commandclick.fragment_lib.edit_fragment

import android.widget.EditText
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.UpdateLastModifiedForAppHistory
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.EditTextProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ToolbarButtonProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import kotlinx.coroutines.*
import java.lang.ref.WeakReference


object EditModeHandler{
    val settingSectionStart = CommandClickScriptVariable.SETTING_SEC_START
    val settingSectionEnd = CommandClickScriptVariable.SETTING_SEC_END

    fun execByHowFullEdit(editFragment: EditFragment){
        when(
            IsCmdEdit.judge(editFragment)
        ) {
            false -> editSettingVariable(
                editFragment
            )
            else -> editCommandVariable(editFragment)
        }
    }

    private fun editCommandVariable(
        editFragment: EditFragment
    ) {
        val fannelInfoMap = editFragment.fannelInfoMap
        val editExecuteValue = editFragment.editExecuteValue
        val recordNumToMapNameValueInCommandHolder =
            editFragment.recordNumToMapNameValueInCommandHolder
        if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
        ) {
            backToCmdIndex(editFragment)
            return
        }
        UpdateLastModifiedForAppHistory.update(
            editExecuteValue,
            fannelInfoMap,
        )
        buttonCreate(
            editFragment,
            ToolbarButtonBariantForEdit.HISTORY,
        )

        buttonCreate(
            editFragment,
            ToolbarButtonBariantForEdit.OK,
        )

        buttonCreate(
            editFragment,
            ToolbarButtonBariantForEdit.EDIT,
        )
        buttonCreate(
            editFragment,
            ToolbarButtonBariantForEdit.SETTING,
        )
        buttonCreate(
            editFragment,
            ToolbarButtonBariantForEdit.EXTRA,
        )
    }

    private fun editSettingVariable(
        editFragment: EditFragment
    ) {
        val recordNumToMapNameValueInCommandHolder =
            editFragment.recordNumToMapNameValueInCommandHolder
        val recordNumToMapNameValueInSettingHolder =
            editFragment.recordNumToMapNameValueInSettingHolder
        if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
            && recordNumToMapNameValueInSettingHolder.isNullOrEmpty()
        ) {
            backToCmdIndex(editFragment)
            return
        }
        buttonCreate(
            editFragment,
            ToolbarButtonBariantForEdit.OK,
        )
        EditTextProducerForEdit.adds(
            editFragment,
        true
        )
//        if(editFragment.isToolbarBtnCustomInSettingSelects) {
//            buttonCreate(
//                ToolbarButtonBariantForEdit.EDIT,
//            )
//            buttonCreate(
//                ToolbarButtonBariantForEdit.SETTING,
//            )
//            buttonCreate(
//                ToolbarButtonBariantForEdit.EXTRA,
//            )
//        }
    }

    private fun buttonCreate(
        editFragment: EditFragment,
        toolbarButtonVariantForEdit: ToolbarButtonBariantForEdit,
    ){
        ToolbarButtonProducerForEdit.make(
            editFragment,
            toolbarButtonVariantForEdit,
        )
    }

    private fun backToCmdIndex(editFragment: EditFragment){
        val context = editFragment.context
        editFragment.popBackStackToIndexImmediateJob?.cancel()
        editFragment.popBackStackToIndexImmediateJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO){
                delay(200)
            }
            withContext(Dispatchers.Main){
                ToastUtils.showLong("No editable variable therefore, go back")
                val listener = context
                        as? EditFragment.OnInitEditFragmentListener
                listener?.onInitEditFragment()
            }
        }
    }
}
