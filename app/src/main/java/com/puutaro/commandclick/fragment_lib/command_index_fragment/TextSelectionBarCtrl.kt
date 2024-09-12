package com.puutaro.commandclick.fragment_lib.command_index_fragment

import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.broadcast.extra.PocketWebviewLaunchExtra
import com.puutaro.commandclick.common.variable.broadcast.scheme.BroadCastIntentSchemeTerm
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.proccess.broadcast.BroadCastIntent
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender
import com.puutaro.commandclick.util.JavaScriptLoadUrl
import com.puutaro.commandclick.util.file.AssetsFileManager
import com.puutaro.commandclick.util.state.TargetFragmentInstance
import com.puutaro.commandclick.util.url.WebUrlVariables
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object TextSelectionBarCtrl {
    fun setOnClickListener(
        cmdIndexFragment: CommandIndexFragment,
    ){
        val binding = cmdIndexFragment.binding
//        binding.cmdindexSelectionSearchLinearLayout.isVisible = true
        ExecSetToolbarButtonImage.SelectionBarButton.updatePocketSearchImage(
            binding
        )
        binding.cmdindexSelectionSearchCaption.apply {
//            setFillColor(R.color.file_dark_green_color)
            outlineWidthSrc = 5
//            text = "\uD83D\uDD0D Search"
        }
        val cmdindexSelectionSearchButton = binding.cmdindexSelectionSearchButton
        val context = cmdIndexFragment.context ?: return
        cmdindexSelectionSearchButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val terminalFragment = TargetFragmentInstance.getCurrentTerminalFragmentFromFrag(
                    cmdIndexFragment.activity
                ) ?: return@launch
                withContext(Dispatchers.IO) {
                    val selectionText = terminalFragment.selectionText
                    val queryUrl = "${WebUrlVariables.queryUrl}${selectionText}"
                    BroadcastSender.normalSend(
                        context,
                        BroadCastIntentSchemeTerm.POCKET_WEBVIEW_LAUNCH.action,
                        listOf(
                            Pair(PocketWebviewLaunchExtra.url.schema, queryUrl)
                        )
                    )
                }
                withContext(Dispatchers.IO) {
                    delay(200)
                    val jsContents = AssetsFileManager.readFromAssets(
                        context,
                        AssetsFileManager.clearSelectionTextJs
                    ).split("\n")
                    val jsScriptUrl = JavaScriptLoadUrl.makeFromContents(
                        context,
                        jsContents
                    ) ?: return@withContext
                    BroadCastIntent.sendUrlCon(
                        context,
                        jsScriptUrl
                    )
                }
            }
        }
    }
}