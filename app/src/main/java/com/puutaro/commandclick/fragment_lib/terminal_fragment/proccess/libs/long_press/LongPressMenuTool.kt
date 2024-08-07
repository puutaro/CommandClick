package com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.libs.long_press

import android.content.Context
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.common.variable.variant.LanguageTypeSelects
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.CommandClickVariables
import com.puutaro.commandclick.util.str.QuoteTool
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.str.ScriptPreWordReplacer
import com.puutaro.commandclick.util.state.FannelInfoTool
import java.io.File

object LongPressMenuTool {

    private val icons8Wheel = R.drawable.icons8_wheel

    fun makeExecJsPath(
        terminalFragment: TerminalFragment,
        currentAppDirPath: String,
        selectedScriptNameOrPath: File,
    ): String {
        val currentFannelName = makeCurrentFannelName(
            terminalFragment,
        )
        val isScriptName = selectedScriptNameOrPath.parent.isNullOrEmpty()
        return when(
            isScriptName
        ) {
            true
            -> "${currentAppDirPath}/${selectedScriptNameOrPath.name}"

            else
            -> ScriptPreWordReplacer.replace(
                selectedScriptNameOrPath.absolutePath,
                currentAppDirPath,
                currentFannelName,
            )
        }
    }

    fun makeMenuList(
        longPressScriptList: List<String>,
    ): List<Pair<String, Int>> {
        return longPressScriptList.map {
            val menuNameAndPathList = it.split("\t")
            val menuName = when(menuNameAndPathList.size){
                2 -> menuNameAndPathList.first()
                1 -> File(menuNameAndPathList.first()).name
                else -> String()
            }
            menuName to icons8Wheel
        }.filter {
            it.first.isNotEmpty()
        }
    }

    fun extractJsPathFromLongPressMenuList(
        selectedMenuName: String,
        longPressScriptList: List<String>,
    ): String? {
        return longPressScriptList.map {
            val menuNameAndPathList = it.split("\t")
            return@map when(menuNameAndPathList.size){
                2 -> {
                    if(
                        menuNameAndPathList.first() == selectedMenuName
                    ) menuNameAndPathList.last()
                    else String()
                }
                1 -> {
                    val jsPathEntry = menuNameAndPathList.first()
                    if(File(jsPathEntry).name == selectedMenuName
                    ) jsPathEntry
                    else String()
                }
                else -> String()
            }
        }.filter {
            it.isNotEmpty()
        }.firstOrNull()
    }

    fun extractSettingValList(
        context: Context?,
        srcFannelPath: String,
    ): List<String>? {
        val repValMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            srcFannelPath,
        )
        val longPressFannelMainAppDirPath = CcPathTool.getMainAppDirPath(srcFannelPath)
        val longPressFannelName =  File(srcFannelPath).name
        return CommandClickVariables.returnSettingVariableList(
            ReadText(srcFannelPath).textToList(),
            LanguageTypeSelects.JAVA_SCRIPT,
        )?.joinToString("\n")?.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                repValMap,
                longPressFannelMainAppDirPath,
                longPressFannelName
            )
        }?.split("\n")
    }


    fun makeLongPressScriptList(
        terminalFragment: TerminalFragment,
        longPressMenuDirPath: String,
        longPressMenuName: String,
    ): List<String> {
        val context = terminalFragment.context
        val longPressMenuFilePath =  File(longPressMenuDirPath, longPressMenuName).absolutePath
        val mainFannelPath = CcPathTool.getMainFannelFilePath(
            longPressMenuFilePath
        )
        val mainFannelPathObj = File(mainFannelPath)
        val currentAppDirPath = mainFannelPathObj.parent ?: String()
        val currentFannelName = mainFannelPathObj.name
        val repValMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            longPressMenuFilePath
        )
        val srcLongPressListConSrc = ReadText(longPressMenuFilePath).readText()
        val srcLongPressListCon = srcLongPressListConSrc.split("\n").map {
            QuoteTool.trimBothEdgeQuote(it)
        }.joinToString("\n")
        return SetReplaceVariabler.execReplaceByReplaceVariables(
            srcLongPressListCon,
            repValMap,
            currentAppDirPath,
            currentFannelName
        ).split("\n")
    }

    private fun makeCurrentFannelName(
        terminalFragment: TerminalFragment,
    ): String {
        val context = terminalFragment.context
            ?: return String()
        val editExecuteFragmentTag = context.getString(R.string.edit_terminal_fragment)
        return when(
            terminalFragment.tag == editExecuteFragmentTag
        ){
            true -> {
                val sharePref = FannelInfoTool.getSharePref(context)
                FannelInfoTool.getStringFromFannelInfo(
                    sharePref,
                    FannelInfoSetting.current_fannel_name
                )
            }
            else -> {
                String()
            }

        }
    }
}