package com.puutaro.commandclick.fragment_lib.command_index_fragment

import android.app.AlertDialog
import com.puutaro.commandclick.fragment.CommandIndexFragment

object FannelInstallDialog {
    fun create(
        cmdIndexFragment: CommandIndexFragment
    ):AlertDialog {
        val context = cmdIndexFragment.context
        val alertDialogBuilder = AlertDialog.Builder(
            context
        )
            .setTitle("Select from bellow fannels")
        return alertDialogBuilder.create()
    }
}