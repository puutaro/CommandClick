package com.puutaro.commandclick.view_model.activity

import androidx.lifecycle.ViewModel

class EditViewModel: ViewModel()  {
    val variableNameToEditTextIdMap: MutableMap<String, Int> = mutableMapOf()
}