package com.puutaro.commandclick.activity_lib.event.lib.edit

import androidx.lifecycle.ViewModelProvider
import com.abdeveloper.library.MultiSelectDialog
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.CommandClickScriptVariable
import com.puutaro.commandclick.view_model.activity.TerminalViewModel

object MultiSelectListContentsDialogForEdit {
    fun show(
        activity: MainActivity,
        title: String,
        updatedMultiModelArray: ArrayList<MultiSelectModel>,
        preSelectedMultiModelArray: ArrayList<Int>
    ){
        val terminalViewModel: TerminalViewModel =
            ViewModelProvider(activity).get(TerminalViewModel::class.java)
        val multiSelectDialog = MultiSelectDialog()
            .title(title)
            .titleSize(25f)
            .positiveText("OK")
            .negativeText("CANCEL")
            .setMinSelectionLimit(0)
            .preSelectIDsList(preSelectedMultiModelArray)
            .setMaxSelectionLimit(updatedMultiModelArray.size)
            .multiSelectList(updatedMultiModelArray)
            .onSubmit(object : MultiSelectDialog.SubmitCallbackListener {
                override fun onSelected(
                    selectedIds: ArrayList<Int>,
                    selectedNames: ArrayList<String>,
                    dataString: String
                ) {

                     val multiSelectTabStringSource = selectedIds.map {
                        updatedMultiModelArray[it].name
                    }.joinToString("\t")
                    terminalViewModel.multiSelectTabString = if(
                        multiSelectTabStringSource.isEmpty()
                    ) CommandClickScriptVariable.EMPTY_STRING
                    else multiSelectTabStringSource
                }
                override fun onCancel() {
                    terminalViewModel.multiSelectTabString =
                        CommandClickScriptVariable.EMPTY_STRING
                }
            })
        multiSelectDialog.show(
            activity.supportFragmentManager,
            "multiSelectDialog"
        )
    }
}
