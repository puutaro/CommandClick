package com.puutaro.commandclick.fragment_lib.edit_fragment

import android.widget.Toast
import com.puutaro.commandclick.databinding.EditFragmentBinding
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.IsCmdEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.UpdateLastModifiedForAppHistory
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.EditTextProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ToolbarButtonProducerForEdit
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.ToolbarButtonBariantForEdit
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import kotlinx.coroutines.*


class EditModeHandler(
    private val editFragment: EditFragment,
) {
    private val context = editFragment.context
    private val readSharePreferenceMap = editFragment.readSharePreferenceMap
    private val editExecuteValue = editFragment.editExecuteValue
    private val binding = editFragment.binding
    private val toolbarButtonProducerForEdit = ToolbarButtonProducerForEdit(
        binding,
        editFragment,
    )
    val settingSectionStart = editFragment.settingSectionStart
    val settingSectionEnd = editFragment.settingSectionEnd

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
            readSharePreferenceMap,
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
                Toast.makeText(
                    context,
                    "No editable variable therefore, go back",
                    Toast.LENGTH_LONG
                ).show()
                val listener = context
                        as? EditFragment.OnInitEditFragmentListener
                listener?.onInitEditFragment()
//
//                val listener = context as? EditFragment.onToolBarButtonClickListenerForEditFragment
//                listener?.onToolBarButtonClickForEditFragment(
//                    editFragment.tag,
//                    ToolbarButtonBariantForEdit.CANCEL,
//                    readSharePreferenceMap,
//                    enableCmdEdit,
//                )
            }
        }
    }
}
