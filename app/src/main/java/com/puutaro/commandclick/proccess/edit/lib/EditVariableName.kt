package com.puutaro.commandclick.proccess.edit.lib

import android.widget.EditText
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.view_model.activity.EditViewModel

object EditVariableName {

    fun getText(
        editFragment: EditFragment,
        targetVariableName: String,
    ): String {
        val editViewModel: EditViewModel by editFragment.activityViewModels()
        val editTextId = editViewModel.variableNameToEditTextIdMap.get(targetVariableName)
            ?: return String()
        val binding = editFragment.binding
        val editLinearLayout = binding.editLinearLayout
        val editTextInEditFragment =
            editLinearLayout.findViewById<EditText>(editTextId)
                ?: return String()
        return editTextInEditFragment.text.toString()
    }
}