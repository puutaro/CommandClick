package com.puutaro.commandclick.activity_lib.event.lib.terminal

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.component.adapter.EditComponentListAdapter
import com.puutaro.commandclick.fragment_lib.terminal_fragment.proccess.EditListRecyclerViewGetter
import com.puutaro.commandclick.proccess.list_index_for_edit.config_settings.ListSettingsForListIndex
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
            val editComponentListAdapter =
                editListRecyclerView.adapter as EditComponentListAdapter
            val mapListSeparator = ListSettingsForListIndex.MapListPathManager.mapListSeparator
            val selectedLineMap =
                editComponentListAdapter.lineMapList.getOrNull(
                    listIndexPosition
                ) ?: return
            val mapListPath = FilePrefixGetter.get(
                editComponentListAdapter.fannelInfoMap,
                editComponentListAdapter.setReplaceVariableMap,
                editComponentListAdapter.editListMap,
                ListSettingsForListIndex.ListSettingKey.MAP_LIST_PATH.key,
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