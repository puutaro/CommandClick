package com.puutaro.commandclick.common.variable.edit

import androidx.fragment.app.Fragment


data class EditParameters(
    val currentFragment: Fragment,
    val currentShellContentsList: List<String>,
    val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
    val readSharePreffernceMap: Map<String, String>,
    val setReplaceVariableMap: Map<String, String>?,
    val onFixNormalSpinner: Boolean
) {
    val context = currentFragment.context
    var currentId: Int = 0
    var currentVariableValue: String? = null
    var setVariableMap:  Map<String, String>? = null
    var onDirectoryPick: Boolean = false
}