package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface

import android.webkit.JavascriptInterface
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.proccess.broadcast.BroadCastSenderSchemaForCommon
import com.puutaro.commandclick.proccess.broadcast.BroadcastSender

class JsBroadcast(
    terminalFragment: TerminalFragment
) {

    private val context = terminalFragment.context

    @JavascriptInterface
    fun send(
        action: String,
        broadCastMapStr: String
    ){
        /*
        Send broad cast

        ### action arg

        Broad cast action

        -> [Action Detail](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/common/variable/broadcast/scheme)

        ### broadcastMapStr arg
        Broad cast extra key-value map contents by separated by `keySeparator`

        -> [Extra Detail](https://github.com/puutaro/CommandClick/blob/master/app/src/main/java/com/puutaro/commandclick/common/variable/broadcast/extra)

        ### Example js version

        ```js.js
        jsBroadcast.send(
           "com.puutaro.commandclick.music_player.play",
           `playMode=shuffle|onLoop=on|onTrack=on
            `,
        )
        ```

        ### Example js action version

        ```js.js
        var=runMusicPlay
            ?func=jsBroadcast.send
            ?args=
                &action="com.puutaro.commandclick.music_player.play"
                &broadCastMapStr=`
                    |playMode=shuffle
                    |onLoop=on
                    |onTrack=on
                `
        ```
        */

        val keySeparator = '|'
        val broadcastMap = mapOf(
            BroadCastSenderSchemaForCommon.action.name to action,
            BroadCastSenderSchemaForCommon.extras.name to broadCastMapStr
        )
        BroadcastSender.send(
            context,
            broadcastMap,
            keySeparator
        )
    }
}