package com.puutaro.commandclick.common.variable.edit

import androidx.fragment.app.Fragment


data class EditParameters(
    val currentFragment: Fragment,
    val currentShellContentsList: List<String>,
    val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>?,
    val recordNumToMapNameValueInSettingHolder: Map<Int, Map<String, String>?>?,
    val fannelInfoMap: Map<String, String>,
    val setReplaceVariableMap: Map<String, String>?,
    val onFixNormalSpinner: Boolean,
    val hideSettingVariableList: List<String>
) {
    val context = currentFragment.context
    var currentId: Int = 0
    var currentVariableName: String? = null
    var currentVariableValue: String? = null
    var setVariableMap:  Map<String, String>? = null
    var variableTypeList: List<String> = emptyList()
    var onDirectoryPick: Boolean = false
    var isReturnOnlyFileName: Boolean = false
}