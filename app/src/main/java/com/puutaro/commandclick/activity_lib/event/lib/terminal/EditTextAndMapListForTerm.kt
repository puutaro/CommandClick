package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.util.file.MapListFileRenamer
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.map.FilePrefixGetter
import com.puutaro.commandclick.util.state.TargetFragmentInstance

object EditTextAndMapListForTerm {

    fun update(
        activity: MainActivity,
        editListIndex: Int,
        srcFragmentStr: String,
    ) {
        if(
            editListIndex < 0
        ) return
        val terminalFragment = TargetFragmentInstance.getCurrentTerminalFragment(
            activity
        ) ?: return
        val editListRecyclerView = EditListRecyclerViewGetter.get(
            terminalFragment,
            srcFragmentStr
        ) ?: return
        ExecRenameFile.rename(
            activity,
            editListRecyclerView,
            editListIndex,
        )
        return
    }


    private object ExecRenameFile {
        fun rename(
            context: Context?,
            editListRecyclerView: RecyclerView,
            listIndexPosition: Int,
        ) {
            val editConstraintListAdapter =
                editListRecyclerView.adapter as EditConstraintListAdapter
            val mapListSeparator = ListSettingsForEditList.MapListPathManager.mapListSeparator
            val selectedLineMap =
                editConstraintListAdapter.lineMapList.getOrNull(
                    listIndexPosition
                ) ?: return
            val mapListPath = FilePrefixGetter.get(
                editConstraintListAdapter.fannelInfoMap,
                editConstraintListAdapter.setReplaceVariableMap,
                editConstraintListAdapter.indexListMap,
                ListSettingsForEditList.ListSettingKey.MAP_LIST_PATH.key,
            ) ?: String()
            val isExist = ReadText(
                mapListPath
            ).textToList().map {
                CmdClickMap.createMap(
                    it,
                    mapListSeparator
                ).toMap()
            }.contains(selectedLineMap)
            if(!isExist){
                ToastUtils.showShort("No exist")
                return
            }
            MapListFileRenamer.rename(
                context,
                editListRecyclerView,
                mapListPath,
                selectedLineMap
            )
        }
    }
}