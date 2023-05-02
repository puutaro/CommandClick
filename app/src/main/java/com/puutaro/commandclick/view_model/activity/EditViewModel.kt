package com.puutaro.commandclick.view_model.activity

import androidx.lifecycle.ViewModel

class EditViewModel: ViewModel()  {
    var variableNameToEditTextIdMap: MutableMap<String, Int> = mutableMapOf()
    var variableNameToSpinnerIdMap: MutableMap<String, Int> = mutableMapOf()
}