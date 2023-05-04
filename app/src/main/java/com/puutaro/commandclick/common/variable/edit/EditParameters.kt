package com.puutaro.commandclick.common.variable.edit

import android.content.Context


data class EditParameters(
    val context: Context?,
    val currentShellContentsList: List<String>,
    val recordNumToMapNameValueInCommandHolder: Map<Int, Map<String, String>?>? = null,
    val readSharePreffernceMap: Map<String, String>,
    val setReplaceVariableMap: Map<String, String>?,
    val onFixNormalSpinner: Boolean
) {
    var currentId: Int = 0
    var currentVariableValue: String? = null
    var setVariableMap:  Map<String, String>? = null
    var onDirectoryPick: Boolean = false
}