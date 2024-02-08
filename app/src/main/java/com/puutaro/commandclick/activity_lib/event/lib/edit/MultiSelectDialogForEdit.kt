package com.puutaro.commandclick.activity_lib.event.lib.edit

import android.content.Context
import android.widget.EditText
import com.abdeveloper.library.MultiSelectDialog
import com.abdeveloper.library.MultiSelectModel
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.state.FragmentTagManager
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import com.puutaro.commandclick.util.state.TargetFragmentInstance

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
                    val currentAppDirPath = SharePreferenceMethod.getStringFromSharePreference(
                        sharePref,
                        SharePrefferenceSetting.current_app_dir
                    )
                    val currentFannelName = SharePreferenceMethod.getStringFromSharePreference(
                        sharePref,
                        SharePrefferenceSetting.current_fannel_name
                    )
                    val currentFannelState = SharePreferenceMethod.getStringFromSharePreference(
                        sharePref,
                        SharePrefferenceSetting.current_fannel_state
                    )
                    val cmdEditFragmentTag = FragmentTagManager.makeCmdValEditTag(
                        currentAppDirPath,
                        currentFannelName,
                        currentFannelState
                    )
                    val settingEditFragmentTag = FragmentTagManager.makeSettingValEditTag(
                        currentAppDirPath,
                        currentFannelName,
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