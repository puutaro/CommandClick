package com.puutaro.commandclick.fragment_lib.terminal_fragment

import androidx.appcompat.widget.AppCompatImageView
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.intent.ExecJsLoad
import com.puutaro.commandclick.proccess.qr.QrScanner
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.file.AssetsFileManager

object GgleSchToolbar {

    fun set(
        terminalFragment: TerminalFragment
    ){
        setGgleForcusClickListener(
            terminalFragment,
            terminalFragment.binding.termSearchButtonImage
        )
        setQrScanClickListener(
            terminalFragment,
            terminalFragment.binding.termQrScanImage
        )
    }

    private fun setGgleForcusClickListener(
        terminalFragment: TerminalFragment,
        imageView: AppCompatImageView
    ){
        imageView.setOnClickListener {
            val jsContents = AssetsFileManager.readFromAssets(
                terminalFragment.context,
                AssetsFileManager.ggleSchBoxFocus
            )
            ExecJsLoad.jsConLaunchHandler(
                terminalFragment,
                jsContents
            )
        }
    }

    private fun setQrScanClickListener(
        terminalFragment: TerminalFragment,
        imageView: AppCompatImageView
    ){
        imageView.setOnClickListener {
            Keyboard.hiddenKeyboardForFragment(terminalFragment)
            QrScanner.scanFromCamera(terminalFragment)
        }
    }

}