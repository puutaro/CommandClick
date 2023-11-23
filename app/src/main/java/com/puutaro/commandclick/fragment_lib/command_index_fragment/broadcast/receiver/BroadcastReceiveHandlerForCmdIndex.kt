package com.puutaro.commandclick.fragment_lib.command_index_fragment.broadcast.receiver

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button.InstallFannelList

object BroadcastReceiveHandlerForCmdIndex {
    fun handle(
        cmdIndexFragment: CommandIndexFragment,
        intent: Intent
    ){
        val installFannelList = cmdIndexFragment.installFannelDialog?.findViewById<RecyclerView>(
            R.id.install_fannel_recycler
        )
        InstallFannelList.updateInstallFannelList(
            installFannelList,
            InstallFannelList.makeFannelListForListView(),
        )
    }
}