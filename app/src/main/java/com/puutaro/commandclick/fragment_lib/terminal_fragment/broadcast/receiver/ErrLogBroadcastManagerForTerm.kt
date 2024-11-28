package com.puutaro.commandclick.fragment_lib.terminal_fragment.broadcast.receiver

import android.content.Context
import android.content.Intent
import com.puutaro.commandclick.common.variable.broadcast.extra.BroadCastIntentExtraForJsDebug
import com.puutaro.commandclick.common.variable.broadcast.extra.ErrLogExtraForTerm
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

object ErrLogBroadcastManagerForTerm {

    object LogDatetime {
        private var beforeOutputTime = LocalDateTime.parse("2020-02-15T21:30:50")
        private val mutex = Mutex()

        suspend fun update(
            datetime: LocalDateTime
        ) {
            mutex.withLock {
                beforeOutputTime = datetime
            }
        }

        suspend fun get(): LocalDateTime {
            mutex.withLock {
                return beforeOutputTime
            }
        }
    }

    fun handle(
        context: Context?,
        intent: Intent,
    ){
        val errContents = intent.getStringExtra(
            ErrLogExtraForTerm.ERR_CONTENTS.schema
        ) ?: return
        val debugNotiJanre = intent.getStringExtra(
            ErrLogExtraForTerm.DEBUG_NOTI_JANRE.schema
        )?.let {
            debugNotiJanreStr ->
            BroadCastIntentExtraForJsDebug.DebugGenre.entries.firstOrNull {
                it.type == debugNotiJanreStr
            }
        } ?: BroadCastIntentExtraForJsDebug.DebugGenre.SYS_ERR
        val notiLevel = intent.getStringExtra(
            ErrLogExtraForTerm.NOTI_LEVEL.schema
        )?.let {
                notilevel ->
            BroadCastIntentExtraForJsDebug.NotiLevelType.entries.firstOrNull {
                it.level == notilevel
            }
        } ?: BroadCastIntentExtraForJsDebug.NotiLevelType.HIGH

        val durationSec = 5
        CoroutineScope(Dispatchers.IO).launch {
            val currentDatetime = withContext(Dispatchers.IO) {
                LocalDateTime.now()
            }
            val beforeOutputTime = withContext(Dispatchers.IO) {
                LogDatetime.get()
            }
            val diffSec = withContext(Dispatchers.IO) {
                LocalDatetimeTool.getDurationSec(
                    beforeOutputTime,
                    currentDatetime
                )
            }
            if(diffSec < durationSec) return@launch
            withContext(Dispatchers.IO) {
                LogDatetime.update(
                    currentDatetime
                )
            }
            withContext(Dispatchers.IO) {
                LogSystems.stdErr(
                    context,
                    errContents,
                    debugNotiJanre.type,
                    notiLevel.level
                )
            }
        }
    }


}