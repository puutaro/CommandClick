package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.widget.EditText
import com.abdeveloper.library.MultiSelectDialog
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.TargetFragmentInstance

object MultiSelectDialogForEdit {
    fun show(
        activity: MainActivity,
        variableName: String,
        editTextId: Int,
        updatedMultiModelArray: ArrayList<MultiSelectModel>,
        preSelectedMultiModelArray: ArrayList<Int>
    ){
        val multiSelectDialog = MultiSelectDialog()
            .title(variableName)
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
                    val editFragmentSource = TargetFragmentInstance().getFromActivity<EditFragment>(
                        activity,
                        activity.getString(R.string.cmd_variable_edit_fragment)
                    )
                    val editFragment = if(editFragmentSource == null){
                        TargetFragmentInstance().getFromActivity<EditFragment>(
                            activity,
                            activity.getString(R.string.setting_variable_edit_fragment)
                        )
                    } else editFragmentSource
                    if(editFragment == null) return
                    val editBinding = editFragment.binding
                    val editLinearLayout = editBinding.editLinearLayout
                    val editTextInEditFragment = editLinearLayout.findViewById<EditText>(editTextId)

                    val selectString = selectedIds.map {
                        updatedMultiModelArray[it].name
                    }.joinToString(",")
                    editTextInEditFragment.setText(selectString)
                }

                override fun onCancel() {}
            })
        multiSelectDialog.show(
            activity.supportFragmentManager,
            "multiSelectDialog"
        )
    }
}