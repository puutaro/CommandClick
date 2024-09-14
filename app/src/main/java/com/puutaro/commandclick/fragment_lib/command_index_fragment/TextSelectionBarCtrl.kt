package com.puutaro.commandclick.fragment_lib.command_index_fragment

import androidx.core.view.isVisible
import com.puutaro.commandclick.R
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
import java.net.URLEncoder

object TextSelectionBarCtrl {
    fun setOnClickListener(
        cmdIndexFragment: CommandIndexFragment,
    ){
        val binding = cmdIndexFragment.binding
        ExecSetToolbarButtonImage.SelectionBarButton.updatePocketSearchImage(
            binding
        )
        binding.cmdindexSelectionSearchCaption.apply {
//            setFillColor(R.color.file_dark_green_color)
            outlineWidthSrc = 5
//            text = "\uD83D\uDD0D Search"
        }
        binding.cmdindexSelectionSearchCurText.apply {
            isVisible = false
            setFillColor(R.color.white)
            setStrokeColor(R.color.fannel_icon_color)
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
                    if(
                        selectionText.isEmpty()
                    ) return@withContext
                    val queryUrl = "${WebUrlVariables.queryUrl}${URLEncoder.encode(selectionText, "utf-8")}"
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