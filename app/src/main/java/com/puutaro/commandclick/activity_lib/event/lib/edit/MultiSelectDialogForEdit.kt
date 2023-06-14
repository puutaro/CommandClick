package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.content.Context
import android.widget.EditText
import com.abdeveloper.library.MultiSelectDialog
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.FragmentTagManager
import com.puutaro.commandclick.util.SharePreffrenceMethod
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
                    val sharePref = activity.getPreferences(Context.MODE_PRIVATE)
                    val cmdEditFragmentTag = FragmentTagManager.makeTag(
                        FragmentTagManager.Prefix.cmdEditPrefix.str,
                        SharePreffrenceMethod.getStringFromSharePreffrence(
                            sharePref,
                            SharePrefferenceSetting.current_app_dir
                        ),
                        SharePreffrenceMethod.getStringFromSharePreffrence(
                            sharePref,
                            SharePrefferenceSetting.current_script_file_name
                        ),
                        FragmentTagManager.Suffix.ON.str
                    )
                    val settingEditFragmentTag = FragmentTagManager.makeTag(
                        FragmentTagManager.Prefix.cmdEditPrefix.str,
                        SharePreffrenceMethod.getStringFromSharePreffrence(
                            sharePref,
                            SharePrefferenceSetting.current_app_dir
                        ),
                        SharePreffrenceMethod.getStringFromSharePreffrence(
                            sharePref,
                            SharePrefferenceSetting.current_script_file_name
                        ),
                        String()
                    )
                    val editFragmentSource = TargetFragmentInstance().getFromActivity<EditFragment>(
                        activity,
                        cmdEditFragmentTag
                    )
                    val editFragment = if(
                        editFragmentSource == null
                    ){
                        TargetFragmentInstance().getFromActivity<EditFragment>(
                            activity,
                            settingEditFragmentTag
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