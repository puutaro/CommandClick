package com.puutaro.commandclick.util.file_tool

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.variables.CommandClickScriptVariable
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.common.variable.variant.SettingVariableSelects
import com.puutaro.commandclick.fragment_lib.edit_fragment.processor.ValidateShell
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object FDialogTempFile {

    private val languageType = LanguageTypeSelects.JAVA_SCRIPT
    val languageTypeToSectionHolderMap =
        CommandClickScriptVariable.LANGUAGE_TYPE_TO_SECTION_HOLDER_MAP.get(
            languageType
        )
    val jsDescPrefix = "//"
    private val settingSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_START
    ) as String

    private val settingSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.SETTING_SEC_END
    ) as String
    val commandSectionStart = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_START
    ) as String
    val commandSectionEnd = languageTypeToSectionHolderMap?.get(
        CommandClickScriptVariable.HolderTypeName.CMD_SEC_END
    ) as String
    private val fDialogTempFileNameRegex = Regex("[0-9]{1,}_${UsePath.fDialogTempFannelName}")
    private val fDialogTempFannelDirNameSuffix =
        CcPathTool.makeFannelDirName(UsePath.fDialogTempFannelName)
    private val fDialogTemFileDirNameRegex =
        Regex("[0-9]{1,}_${fDialogTempFannelDirNameSuffix}")

    fun howFDialogFile(currentFannelName: String): Boolean {
        return currentFannelName.matches(fDialogTempFileNameRegex)
    }

    fun howFDialogDirName(currentFannelDirName: String): Boolean {
        return currentFannelDirName.matches(fDialogTemFileDirNameRegex)
    }


    fun remove(
        readSharePreferenceMap: Map<String, String>
    ){
        CoroutineScope(Dispatchers.IO).launch {
            val currentAppDirPath = withContext(Dispatchers.IO) {
                SharePreferenceMethod.getReadSharePreffernceMap(
                    readSharePreferenceMap,
                    SharePrefferenceSetting.current_app_dir
                )
            }
            val currentFannelName = withContext(Dispatchers.IO) {
                SharePreferenceMethod.getReadSharePreffernceMap(
                    readSharePreferenceMap,
                    SharePrefferenceSetting.current_fannel_name
                )
            }
            val isFDialogFannel = howFDialogFile(currentFannelName)
            if (isFDialogFannel) return@launch
            withContext(Dispatchers.IO) {
                removeFDialogFile(
                    currentAppDirPath,
                )
            }
            withContext(Dispatchers.IO) {
                removeFDialogDir(currentAppDirPath,)
            }
        }
    }

    private fun removeFDialogFile(
        currentAppDirPath: String,
    ){
        FileSystems.sortedFiles(currentAppDirPath).forEach {
            if (
                !howFDialogFile(it)
            ) return@forEach
            FileSystems.removeFileWithDir(
                File(
                    currentAppDirPath,
                    it
                )
            )
        }
    }

    private fun removeFDialogDir(
        currentAppDirPath: String,
    ){

        FileSystems.showDirList(currentAppDirPath).forEach {
            if (
                !howFDialogDirName(it)
            ) return@forEach
            FileSystems.removeDir(
                File(currentAppDirPath, it).absolutePath
            )
        }
    }

    fun create(
        fragment: Fragment,
        readSharePreffernceMap: Map<String, String>,
        destiFDialogFannelName: String,
        fannelCon: String,
    ): Boolean {
        val context = fragment.context
            ?: return false
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val currentFannelName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val fannelConList = fannelCon.split("\n")
        val validateErrMessage = ValidateShell.correct(
            fragment,
            fannelConList,
            currentFannelName
        )
        if (validateErrMessage.isNotEmpty()) {
            Toast.makeText(
                context,
                "Irregular fannel con",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            fannelCon.split("\n"),
            settingSectionStart,
            settingSectionEnd
        )
        val editExecuteCmdValName = CommandClickScriptVariable.EDIT_EXECUTE
        val editExecuteValue = CommandClickVariables.substituteCmdClickVariable(
            settingVariableList,
            editExecuteCmdValName,
        )?.trim()
        val isEditExecuteValue = !editExecuteValue.isNullOrEmpty()
        val compFannelConWithEditExecute = compRequireSetting(
            fannelCon,
            isEditExecuteValue,
            "${editExecuteCmdValName}=\"${SettingVariableSelects.EditExecuteSelects.NO.name}\"",
        )

        FileSystems.writeFile(
            currentAppDirPath,
            destiFDialogFannelName,
            compFannelConWithEditExecute
        )
        copyToFdialogDir(
            currentAppDirPath,
            currentFannelName,
            destiFDialogFannelName,
        )
        return true
    }

    private fun compRequireSetting(
        fannelCon: String,
        isSettingValOk: Boolean,
        compSettingValNameValueLine: String
    ): String {
        return when(isSettingValOk){
            true -> CommandClickVariables.replaceVariableInHolder(
                    fannelCon,
                    compSettingValNameValueLine,
                    settingSectionStart,
                    settingSectionEnd,
                )
            else -> compSettingValCon(
                fannelCon,
                compSettingValNameValueLine
            )
        }
    }

    private fun compSettingValCon(
        fannelCon: String,
        compSettingValNameValueLine: String
    ): String {
        val settingVariableList = CommandClickVariables.substituteVariableListFromHolder(
            fannelCon.split("\n"),
            settingSectionStart,
            settingSectionEnd
        )?.filter { !it.startsWith(jsDescPrefix) && it.trim().isNotEmpty() }
        val compSettingVariableList = when(
            settingVariableList.isNullOrEmpty()
        ) {
            true -> execMakeHolderCon(
                settingSectionStart,
                compSettingValNameValueLine,
                settingSectionEnd,
            )
            else -> {
                execMakeHolderCon(
                    settingSectionStart,
                    listOf(
                        compSettingValNameValueLine,
                        settingVariableList.joinToString("\n"),
                    ).joinToString("\n"),
                    settingSectionEnd,
                )
            }
        }
        val cmdValCon = CommandClickVariables.substituteVariableListFromHolder(
            fannelCon.split("\n"),
            commandSectionStart,
            commandSectionEnd
        )?.filter {
            it.trim().isNotEmpty()
        }?.joinToString("\n") ?: String()
        return listOf(
            String(),
            compSettingVariableList,
            String(),
            cmdValCon
        ).joinToString("\n\n")
    }

    private fun execMakeHolderCon(
        holderStartName: String,
        holderCon: String,
        holderEndName: String,
    ): String {
        return listOf(
            holderStartName,
            holderCon,
            holderEndName,
        ).distinct().joinToString("\n")

    }

    private fun copyToFdialogDir(
        currentAppDirPath: String,
        currentFannelName: String,
        destiFDialogFannelName: String,
    ){
        val srcFannelDirPath = CcPathTool.makeFannelDirName(currentFannelName).let {
            File(currentAppDirPath, it).absolutePath
        }
        val destiFDialogFannelDirPath = CcPathTool.makeFannelDirName(destiFDialogFannelName).let {
            File(currentAppDirPath, it).absolutePath
        }
        FileSystems.copyDirectory(
            srcFannelDirPath,
            destiFDialogFannelDirPath,
        )
    }
}