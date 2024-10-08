package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.click

import android.view.View
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.FannelIndexListAdapter
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.util.file.ReadText
import java.io.File

object FannelQrLogoClickListener {
    fun set(
        cmdIndexFragment: CommandIndexFragment,
//        currentAppDirPath: String,
        fannelIndexListAdapter: FannelIndexListAdapter
    ){
        fannelIndexListAdapter.fannelQrLogoClickListener = object: FannelIndexListAdapter.OnFannelQrLogoItemClickListener {
            override fun onFannelContentsClick(
                itemView: View,
                holder: FannelIndexListAdapter.FannelIndexListViewHolder
            ) {
                val fannelNameTextView = holder.fannelNameTextView
//                val itemContext = itemView.context
                val fannelName = fannelNameTextView.text.toString()
                ScriptFileDescription.show(
                    cmdIndexFragment,
                    ReadText(
                        File(
                            UsePath.cmdclickDefaultAppDirPath,
                            fannelName
                        ).absolutePath
                    ).textToList(),
//                    currentAppDirPath,
                    fannelName
                )
            }
        }
    }
}