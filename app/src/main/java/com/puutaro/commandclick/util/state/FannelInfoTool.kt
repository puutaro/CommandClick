package com.puutaro.commandclick.util.state

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.settings.FannelInfoSetting
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.SharePrefTool
import java.io.File

object FannelInfoTool {

    private enum class SdCardKey(
        val key: String,
    ) {
        FannelInfo("fannelInfo")
    }

    class FannelInfoSharePref(
        val sharePref: SharedPreferences?
    )
    fun getSharePref(
        context: Context?
    ): FannelInfoSharePref {
        val sharePref = context?.getSharedPreferences(
            SdCardKey.FannelInfo.key,
            Context.MODE_PRIVATE
        )
        return FannelInfoSharePref(sharePref)
    }

    fun getFannelInfoMap(
        fragment: Fragment,
        mainOrSubFannelPath: String?,
    ): Map<String, String> {
        if(
            mainOrSubFannelPath.isNullOrEmpty()
        ){
            return when(fragment){
                is CommandIndexFragment -> fragment.fannelInfoMap
                is EditFragment -> fragment.fannelInfoMap
                is TerminalFragment -> fragment.fannelInfoMap
                else -> mapOf()
            }
        }
        val currentAppDirPath = CcPathTool.getMainAppDirPath(
            mainOrSubFannelPath
        )
        val currentFannelName = File(
            CcPathTool.getMainFannelFilePath(mainOrSubFannelPath)
        ).name
        return mapOf(
//            FannelInfoSetting.current_app_dir.name
//                    to currentAppDirPath,
            FannelInfoSetting.current_fannel_name.name
                    to currentFannelName,
        )

    }

//    fun getCurrentAppDirPath(
//        fannelInfoMap: Map<String, String>,
//    ): String {
//        return getValFromFannelInfoMap(
//            fannelInfoMap,
//            FannelInfoSetting.current_app_dir
//        )
//    }

    fun getCurrentFannelName(
        fannelInfoMap: Map<String, String>?,
    ): String {
        if(
            fannelInfoMap.isNullOrEmpty()
        ) return String()
        return getValFromFannelInfoMap(
            fannelInfoMap,
            FannelInfoSetting.current_fannel_name
        )
    }

    fun isEmptyFannelName(
        currentFannelName: String
    ): Boolean {
        return currentFannelName.isEmpty()
                || currentFannelName == FannelInfoSetting.current_fannel_name.defalutStr
    }

    fun getCurrentStateName(
        fannelInfoMap: Map<String, String>?,
    ): String {
        if(
            fannelInfoMap.isNullOrEmpty()
        ) return String()
        return getValFromFannelInfoMap(
            fannelInfoMap,
            FannelInfoSetting.current_fannel_state
        )
    }

    fun getOnShortcut(
        fannelInfoMap: Map<String, String>?,
    ): String {
        if(
            fannelInfoMap.isNullOrEmpty()
        ) return String()
        return getValFromFannelInfoMap(
            fannelInfoMap,
            FannelInfoSetting.on_shortcut
        )
    }

    fun getReplaceVariableMap(
        fragment: Fragment,
        subFannelPath: String?
    ): Map<String, String>? {
        val context = fragment.context
        if(
            subFannelPath.isNullOrEmpty()
        ) return when(fragment){
            is EditFragment -> fragment.setReplaceVariableMap
            is TerminalFragment -> fragment.setReplaceVariableMap
            else -> mapOf()
        }
        return SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            context,
            subFannelPath
        )
    }

    fun getStringFromFannelInfo(
        fannelInfoSharePref: FannelInfoSharePref?,
        fannelInfoSetting: FannelInfoSetting
    ): String {
        val defaultStrValue = fannelInfoSetting.defalutStr
        if(fannelInfoSharePref == null) return defaultStrValue
        return fannelInfoSharePref.sharePref?.getString(
            fannelInfoSetting.name,
            defaultStrValue
        ) ?: defaultStrValue
    }

    fun putAllFannelInfo(
        fannelInfoSharePref: FannelInfoSharePref?,
//        currentAppDirPath: String?,
        currentFannelName: String?,
        onShortcutValue: String?,
        currentFannelState: String?,
    ){
        val nullStr = "NULL_STR"
        val fannelInfoMap = listOf(
//            FannelInfoSetting.current_app_dir.name
//                    to (currentAppDirPath ?: nullStr),
            FannelInfoSetting.current_fannel_name.name
                    to (currentFannelName ?: nullStr),
            FannelInfoSetting.on_shortcut.name
                    to (onShortcutValue ?: nullStr),
            FannelInfoSetting.current_fannel_state.name
                    to (currentFannelState ?: nullStr),
        ).filter {
           it.second != nullStr
        }.map {
            it.first to it.second
        }.toMap()
        SharePrefTool.putSharePref (
            fannelInfoSharePref?.sharePref,
            fannelInfoMap
        )
    }

    fun makeFannelInfoMapByShare(
        fannelInfoSharePref: FannelInfoSharePref?
    ): Map<String, String> {
//        val sharedCurrentAppPath = UsePath.cmdclickDefaultAppDirPath

        val sharedCurrentShellFileName = getStringFromFannelInfo(
            fannelInfoSharePref,
            FannelInfoSetting.current_fannel_name
        )

        val sharedOnShortcut = getStringFromFannelInfo(
            fannelInfoSharePref,
            FannelInfoSetting.on_shortcut
        )
        val currentFannelState = getStringFromFannelInfo(
            fannelInfoSharePref,
            FannelInfoSetting.current_fannel_state
        )

        return mapOf(
//            FannelInfoSetting.current_app_dir.name
//                    to sharedCurrentAppPath,
            FannelInfoSetting.current_fannel_name.name
                    to sharedCurrentShellFileName,
            FannelInfoSetting.on_shortcut.name
                    to sharedOnShortcut,
            FannelInfoSetting.current_fannel_state.name
                    to currentFannelState
        )
    }

    fun makeFannelInfoMapByString(
//        currentAppDirPath: String = String(),
        currentFannelName: String = String(),
        currentFannelState: String = String()
    ): Map<String, String> {
        return mapOf(
//            FannelInfoSetting.current_app_dir.name
//                    to currentAppDirPath,
            FannelInfoSetting.current_fannel_name.name
                    to currentFannelName,
            FannelInfoSetting.current_fannel_state.name
                    to currentFannelState,
        )
    }

    private fun getValFromFannelInfoMap(
        fannelInfoMap: Map<String, String>,
        fannelInfoSetting: FannelInfoSetting
    ): String {
        return try {
            fannelInfoMap.get(
                fannelInfoSetting.name
            ) ?: fannelInfoSetting.defalutStr
        } catch (e: Exception){
            fannelInfoSetting.defalutStr
        }
    }
}