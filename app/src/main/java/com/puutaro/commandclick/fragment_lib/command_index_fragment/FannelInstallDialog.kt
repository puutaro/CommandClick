package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.app.AlertDialog
import com.puutaro.commandclick.fragment.CommandIndexFragment

object FannelInstallDialog {
    fun create(
        cmdIndexCommandIndexFragment: CommandIndexFragment
    ):AlertDialog {
        val context = cmdIndexCommandIndexFragment.context
        val alertDialogBuilder = AlertDialog.Builder(
            context
        )
            .setTitle("Select from bellow fannels")
        return alertDialogBuilder.create()
    }
}