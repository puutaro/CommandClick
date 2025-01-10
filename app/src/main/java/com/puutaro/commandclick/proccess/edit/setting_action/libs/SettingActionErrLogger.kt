package com.puutaro.commandclick.proccess.edit.setting_action.libs

import android.content.Context
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.datetime.LocalDatetimeTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.time.LocalDateTime
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

object SettingActionErrLogger {

    private val durationSec = 5

    enum class SettingActionErrType {
        S_RETURN,
        S_VAR,
        S_AC_VAR,
        FUNC,
        S_IF,
        AWAIT,
    }

    object LogDatetime {
        private var beforeOutputTime = LocalDateTime.parse("2020-02-15T21:30:50")
        private val mutex = ReentrantReadWriteLock()

        suspend fun update(
            datetime: LocalDateTime
        ) {
            mutex.writeLock().withLock {
                beforeOutputTime = datetime
            }
        }

        suspend fun get(): LocalDateTime {
            mutex.readLock().withLock {
                return beforeOutputTime
            }
        }
    }

    suspend fun sendErrLog(
        context: Context?,
        errType: SettingActionErrType,
        errMessage: String,
        keyToSubKeyConWhere: String,
    ) {
        val currentDatetime =
            withContext(Dispatchers.IO) {
                LocalDateTime.now()
            }
        val diffSec = withContext(Dispatchers.IO) {
            val beforeOutputTime = LogDatetime.get()
            LocalDatetimeTool.getDurationSec(
                beforeOutputTime,
                currentDatetime
            )
        }
        if(diffSec < durationSec) return
        withContext(Dispatchers.IO) {
            LogDatetime.update(
                currentDatetime
            )
        }
        val spanKeyToSubKeyConWhere =
            CheckTool.LogVisualManager.execMakeSpanTagHolder(
                CheckTool.errBrown,
                keyToSubKeyConWhere
            )
        val errToastMessage =
            Jsoup.parse(errMessage).text()
        val logErrMessage =
            "[SETTING ACTION] (${errType.name}) $errMessage about ${spanKeyToSubKeyConWhere}"
        LogSystems.broadErrLog(
            context,
            errToastMessage,
            logErrMessage,
        )
    }
}