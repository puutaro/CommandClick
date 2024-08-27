package com.puutaro.commandclick.fragment_lib.edit_fragment

import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.UpdateLastModifiedForAppHistory
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.EditTextProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ToolbarButtonProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import kotlinx.coroutines.*


class EditModeHandler(
    private val editFragment: EditFragment,
) {
    private val context = editFragment.context
    private val fannelInfoMap = editFragment.fannelInfoMap
    private val editExecuteValue = editFragment.editExecuteValue
    private val binding = editFragment.binding
    private val toolbarButtonProducerForEdit = ToolbarButtonProducerForEdit(
        binding,
        editFragment,
    )
    val settingSectionStart = CommandClickScriptVariable.SETTING_SEC_START
    val settingSectionEnd = CommandClickScriptVariable.SETTING_SEC_END

    fun execByHowFullEdit(){
        when(
            IsCmdEdit.judge(editFragment)
        ) {
            false -> editSettingVariable()
            else -> editCommandVariable()
        }
    }

    private fun editCommandVariable(
    ) {
        val recordNumToMapNameValueInCommandHolder =
            editFragment.recordNumToMapNameValueInCommandHolder
        if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
        ) {
            backToCmdIndex()
            return
        }
        UpdateLastModifiedForAppHistory.update(
            editExecuteValue,
            fannelInfoMap,
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.HISTORY,
        )

        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
        )
        editTextProducerForEdit.adds()

        buttonCreate(
            ToolbarButtonBariantForEdit.EDIT,
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.SETTING,
        )
        buttonCreate(
            ToolbarButtonBariantForEdit.EXTRA,
        )
    }

    private fun editSettingVariable(
    ) {
        val recordNumToMapNameValueInCommandHolder =
            editFragment.recordNumToMapNameValueInCommandHolder
        val recordNumToMapNameValueInSettingHolder =
            editFragment.recordNumToMapNameValueInSettingHolder
        if(
            recordNumToMapNameValueInCommandHolder.isNullOrEmpty()
            && recordNumToMapNameValueInSettingHolder.isNullOrEmpty()
        ) {
            backToCmdIndex()
            return
        }
        buttonCreate(
            ToolbarButtonBariantForEdit.OK,
        )
        val editTextProducerForEdit = EditTextProducerForEdit(
            editFragment,
        )
        editTextProducerForEdit.adds(
        true
        )
        if(editFragment.isToolbarBtnCustomInSettingSelects) {
            buttonCreate(
                ToolbarButtonBariantForEdit.EDIT,
            )
            buttonCreate(
                ToolbarButtonBariantForEdit.SETTING,
            )
            buttonCreate(
                ToolbarButtonBariantForEdit.EXTRA,
            )
        }
    }

    private fun buttonCreate(
        toolbarButtonVariantForEdit: ToolbarButtonBariantForEdit,
    ){
        toolbarButtonProducerForEdit.make(
            toolbarButtonVariantForEdit,
        )
    }

    private fun backToCmdIndex(){
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
